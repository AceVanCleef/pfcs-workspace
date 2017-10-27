package ch.fhnw.pfcs.opengl;

//  -------------   Texturen ------------
//      Adaption von "HelloTexture" (GL3-Version, www.jogamp.com)
//                                                            E.Gutknecht, Dezember 2016
//

import com.jogamp.opengl.*;

import java.nio.*;
import java.io.IOException;

import com.jogamp.common.nio.*;
import com.jogamp.opengl.util.texture.*;

public class MyTextures {
    //  --------------  globale Daten  -----------------

    int textureId;                // OpenGL-Identifier TextureObject

    // -------  Array fuer 8x8 Schachbrett-Textur  -----------------
    float r1 = 1.0f, g1 = 1.0f, b1 = 0.0f;             // Farbe1
    float r2 = 1.0f, g2 = 0.0f, b2 = 0.0f;             // Farbe2
    public final float[] texColors = {                     // 8x8 Schachbrett-Muster
            r1, g1, b1, 1, r2, g2, b2, 1, r1, g1, b1, 1, r2, g2, b2, 1, r1, g1, b1, 1, r2, g2, b2, 1, r1, g1, b1, 1, r2, g2, b2, 1,
            r2, g2, b2, 1, r1, g1, b1, 1, r2, g2, b2, 1, r1, g1, b1, 1, r2, g2, b2, 1, r1, g1, b1, 1, r2, g2, b2, 1, r1, g1, b1, 1,
            r1, g1, b1, 1, r2, g2, b2, 1, r1, g1, b1, 1, r2, g2, b2, 1, r1, g1, b1, 1, r2, g2, b2, 1, r1, g1, b1, 1, r2, g2, b2, 1,
            r2, g2, b2, 1, r1, g1, b1, 1, r2, g2, b2, 1, r1, g1, b1, 1, r2, g2, b2, 1, r1, g1, b1, 1, r2, g2, b2, 1, r1, g1, b1, 1,
            r1, g1, b1, 1, r2, g2, b2, 1, r1, g1, b1, 1, r2, g2, b2, 1, r1, g1, b1, 1, r2, g2, b2, 1, r1, g1, b1, 1, r2, g2, b2, 1,
            r2, g2, b2, 1, r1, g1, b1, 1, r2, g2, b2, 1, r1, g1, b1, 1, r2, g2, b2, 1, r1, g1, b1, 1, r2, g2, b2, 1, r1, g1, b1, 1,
            r1, g1, b1, 1, r2, g2, b2, 1, r1, g1, b1, 1, r2, g2, b2, 1, r1, g1, b1, 1, r2, g2, b2, 1, r1, g1, b1, 1, r2, g2, b2, 1,
            r2, g2, b2, 1, r1, g1, b1, 1, r2, g2, b2, 1, r1, g1, b1, 1, r2, g2, b2, 1, r1, g1, b1, 1, r2, g2, b2, 1, r1, g1, b1, 1,
    };


    //  -------------------------  Methoden  ---------------------------------

    public MyTextures(GL3 gl, int pgm) {
        int textureLoc = gl.glGetUniformLocation(pgm, "myTexture");
        gl.glUniform1i(textureLoc, 0);
    }


    //  ---------  Textur von File einlesen  ---------------------

    public void loadTextureFromFile(GL3 gl, String fileName) {
        TextureData td = null;
        String fileType = extractFileType(fileName);
        int level = 0;
        int[] ids = new int[1];    // OpenGL-Identifiers
        try {
            td = TextureIO.newTextureData(gl.getGLProfile(), this.getClass().getResource("/textures/" + fileName), false, fileType);
        } catch (IOException | GLException ex) {
            ex.printStackTrace();
        }
        gl.glGenTextures(1, ids, 0);
        textureId = ids[0];
        gl.glBindTexture(GL3.GL_TEXTURE_2D, textureId);
        gl.glTexImage2D(GL3.GL_TEXTURE_2D, level, td.getInternalFormat(),
                td.getWidth(), td.getHeight(), td.getBorder(),
                td.getPixelFormat(), td.getPixelType(), td.getBuffer());
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_BASE_LEVEL, 0);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAX_LEVEL, level);
        // We set the swizzling. Since it is an RGB texture, we can
        // choose to make the missing component alpha equal to one.
        int[] swizzle = new int[]{GL3.GL_RED, GL3.GL_GREEN, GL3.GL_BLUE, GL3.GL_ONE};
        gl.glTexParameterIiv(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_SWIZZLE_RGBA, swizzle, 0);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_NEAREST);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_NEAREST);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
        gl.glActiveTexture(GL3.GL_TEXTURE0);
    }


    //  ---------  Textur von Array  ---------------------

    public void loadTextureFromArray(GL3 gl, float[] texColors, int m, int n) {
        FloatBuffer texBuf = Buffers.newDirectFloatBuffer(texColors);
        int[] ids = new int[1];    // OpenGL-Identifiers
        gl.glGenTextures(1, ids, 0);
        textureId = ids[0];
        gl.glBindTexture(GL3.GL_TEXTURE_2D, textureId);
        gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGBA, m, n, 0, GL3.GL_RGBA, GL3.GL_FLOAT, texBuf);
        gl.glPixelStorei(GL3.GL_UNPACK_ALIGNMENT, 1);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_NEAREST);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_NEAREST);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
        gl.glActiveTexture(GL3.GL_TEXTURE0);
    }


    public void activate(GL3 gl) {
        gl.glBindTexture(GL3.GL_TEXTURE_2D, textureId);
        gl.glActiveTexture(GL3.GL_TEXTURE0);
    }


    public void replaceData(GL3 gl, float[] texColors, int m, int n) {
        FloatBuffer texBuf = Buffers.newDirectFloatBuffer(texColors);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, textureId);
        gl.glTexSubImage2D(GL3.GL_TEXTURE_2D, 0, 0, 0, m, n, GL3.GL_RGBA, GL3.GL_FLOAT, texBuf);
        gl.glActiveTexture(GL3.GL_TEXTURE0);
    }


    String extractFileType(String s) {
        for (int i = s.length() - 1; i >= 0; i--)
            if (s.charAt(i) == '.')
                return s.substring(i);
        return s;
    }

}
