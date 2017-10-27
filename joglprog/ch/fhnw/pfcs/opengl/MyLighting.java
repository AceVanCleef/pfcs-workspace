package ch.fhnw.pfcs.opengl;//  -------------   Beleuchtungs-Methoden  -------------------
//                                                            E.Gutknecht, August 2016
//

import com.jogamp.opengl.*;
import ch.fhnw.util.math.*;                                   // Vektor- und Matrix-Algebra

public class MyLighting {

    //  --------------  Globale Daten  -------------------------------------

    private int shadingLevel = 0;                             // Beleuchtung 0: ohne Beleuchtung
    private float[] lightPosition = {0, 0, 10, 1};               // Koordinaten der Lichtquelle
    private int shadingLevelId, lightPositionId;              // Uniform Shader Variables


    //  -------------  Konstruktor  ---------------------------

    public MyLighting(GL3 gl, int programId) {
        setupLightingParms(programId, gl);                    // Beleuchtung
    }


    //  -------------  Methoden  ------------------------------

    private void setupLightingParms(int pgm, GL3 gl) {
        // ----- get shader variable identifiers  -------------
        shadingLevelId = gl.glGetUniformLocation(pgm, "shadingLevel");
        lightPositionId = gl.glGetUniformLocation(pgm, "lightPosition");

        // -----  set uniform variables  -----------------------
        gl.glUniform1i(shadingLevelId, shadingLevel);
        gl.glUniformMatrix4fv(lightPositionId, 1, false, lightPosition, 0);
    }

    ;


    //  ----------  oeffentliche Methoden   -------------


    public void setShadingLevel(GL3 gl, int level) {
        shadingLevel = level;                                 // 0: ohne Beleuchtung
        gl.glUniform1i(shadingLevelId, shadingLevel);
    }

    public void setLightPosition(GL3 gl, MyTransf trf, float x, float y, float z) {
        Vec4 tmp = new Vec4(x, y, z, 1);
        tmp = trf.getModelViewMatrix().transform(tmp);        // Transformation in Kamera-System
        lightPosition = tmp.toArray();
        gl.glUniform4fv(lightPositionId, 1, lightPosition, 0);
    }


    public int getShadinglevel() {
        return shadingLevel;
    }


    public float[] getLightPosition() {
        float[] p = {lightPosition[0], lightPosition[1], lightPosition[2]};
        return p;
    }

}
