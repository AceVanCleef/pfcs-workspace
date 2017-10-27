package ch.fhnw.pfcs.opengl;

import ch.fhnw.util.math.Vec3;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;

import java.nio.FloatBuffer;

public class MyVertexBuf {

    private int maxVertices;                                     // max. Anzahl Vertices im Vertex-Array

    //  --------  Vertex-Array (fuer die Attribute Position, Color, Normal)  ------------
    private FloatBuffer vertexBuf;                            // Vertex-Array
    final int vPositionSize = 4 * Float.SIZE / 8;                 // Anz. Bytes der x,y,z,w (homogene Koordinaten)
    final int vColorSize = 4 * Float.SIZE / 8;                    // Anz. Bytes der rgba Werte
    final int vNormalSize = 4 * Float.SIZE / 8;                   // Anz. Bytes der Normalen
    final int vertexSize = vPositionSize + vColorSize + vNormalSize; // Anz. Bytes eines Vertex
    private int bufSize;                                      // Anzahl Bytes des VertexArrays = maxVertices * vertexSize

    private float[] currentColor = {1, 1, 1, 1};                // aktuelle Farbe fuer Vertices
    private float[] currentNormal = {1, 0, 0, 0};               // aktuelle Normale Vertices

    // ------ Identifiers fuer OpenGL-Objekte und Shader-Variablen  ------
    private int vaoId;                                        // Identifier fuer OpenGL VertexArray Object
    private int vertexBufId;                                  // Identifier fuer OpenGL Vertex Buffer
    private int vPositionId, vColorId, vNormalId;             // Vertex Attribute Shader Variables

    public MyVertexBuf(GL3 gl, int programId, int maxVertices) {
        this.maxVertices = maxVertices;
        setupVertexBuffer(programId, gl, maxVertices);            // Vertex-Buffer
    }

    private void setupVertexBuffer(int pgm, GL3 gl, int maxVertices) {
        bufSize = maxVertices * vertexSize;
        vertexBuf = Buffers.newDirectFloatBuffer(bufSize);
        // ------  OpenGl-Objekte -----------
        int[] tmp = new int[1];
        gl.glGenVertexArrays(1, tmp, 0);                        // VertexArrayObject
        vaoId = tmp[0];
        gl.glBindVertexArray(vaoId);
        gl.glGenBuffers(1, tmp, 0);                             // VertexBuffer
        vertexBufId = tmp[0];
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vertexBufId);
        gl.glBufferData(GL3.GL_ARRAY_BUFFER, bufSize,           // Speicher allozieren
                null, GL3.GL_STATIC_DRAW);

        // ----- get shader variable identifiers  -------------
        vPositionId = gl.glGetAttribLocation(pgm, "vertexPosition");
        vColorId = gl.glGetAttribLocation(pgm, "vertexColor");
        vNormalId = gl.glGetAttribLocation(pgm, "vertexNormal");

        //  ------  enable vertex attributes ---------------
        gl.glEnableVertexAttribArray(vPositionId);
        gl.glEnableVertexAttribArray(vColorId);
        gl.glEnableVertexAttribArray(vNormalId);
        gl.glVertexAttribPointer(vPositionId, 4, GL3.GL_FLOAT, false, vertexSize, 0);
        gl.glVertexAttribPointer(vColorId, 4, GL3.GL_FLOAT, false, vertexSize, vPositionSize);
        gl.glVertexAttribPointer(vNormalId, 4, GL3.GL_FLOAT, false, vertexSize, vPositionSize + vColorSize);
    }

    /**
     * Sets the current vertex color
     *
     * @param r red
     * @param g green
     * @param b blue
     */
    public void setColor(float r, float g, float b) {
        currentColor[0] = r;
        currentColor[1] = g;
        currentColor[2] = b;
        currentColor[3] = 1;
    }

    private void setColor(float[] color) {
        if (color.length >= 3) {
            setColor(color[0], color[1], color[2]);
        }
    }

    /**
     * Sets the current vertex normal
     *
     * @param x coordinate
     * @param y coordinate
     * @param z coordinate
     */

    public void setNormal(float x, float y, float z) {
        currentNormal[0] = x;
        currentNormal[1] = y;
        currentNormal[2] = z;
        currentNormal[3] = 0;
    }

    /**
     * Save vertex data in buffer
     *
     * @param x coordinate
     * @param y coordinate
     * @param z coordinate
     */
    public void putVertex(float x, float y, float z) {
        vertexBuf.put(x);
        vertexBuf.put(y);
        vertexBuf.put(z);
        vertexBuf.put(1);
        vertexBuf.put(currentColor);                              // Farbe
        vertexBuf.put(currentNormal);                             // Normale
    }

    public void putVector(Vec3 vector) {
        putVertex(vector.x, vector.y, vector.z);
    }

    /**
     * Copies the vertex array into the OpenGL-buffer
     *
     * @param gl
     * @param nVertices number of vertices
     */
    public void copyBuffer(GL3 gl, int nVertices) {
        vertexBuf.rewind();
        if (nVertices > maxVertices)
            throw new IndexOutOfBoundsException();
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vertexBufId);
        gl.glBufferSubData(GL3.GL_ARRAY_BUFFER, 0, nVertices * vertexSize, vertexBuf);
    }

    /**
     * Resets the buffer position
     *
     * @param gl
     */
    public void rewindBuffer(GL3 gl) {
        vertexBuf.rewind();
    }

    public float[] getCurrentColor() {
        float[] c = {currentColor[0],
                currentColor[1], currentColor[2]};
        return c;
    }

    public float[] getCurrentNormal() {
        float[] n = {currentNormal[0],
                currentNormal[1], currentNormal[2]};
        return n;
    }

    public void drawAxis(GL3 gl, float a, float b, float c) {
        float[] black = new float[]{1, 1, 1};
        drawAxis(gl, a, b, c, black, black, black);
    }

    public void drawAxisTricolor(GL3 gl, float a, float b, float c) {
        float[] red = new float[]{1, 0, 0};
        float[] green = new float[]{0, 1, 0};
        float[] blue = new float[]{0, 0, 1};
        drawAxis(gl, a, b, c, red, green, blue);
    }

    /**
     * Draws the coordinate axis
     *
     * @param gl
     * @param a
     * @param b
     * @param c
     */
    private void drawAxis(GL3 gl, float a, float b, float c, float[] ca, float[] cb, float[] cc) {
        rewindBuffer(gl);
        setColor(ca);
        putVertex(0, 0, 0);           // Eckpunkte in VertexArray speichern
        putVertex(a, 0, 0);

        setColor(cb);
        putVertex(0, 0, 0);
        putVertex(0, b, 0);

        setColor(cc);
        putVertex(0, 0, 0);
        putVertex(0, 0, c);
        int nVertices = 6;
        copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_LINES, 0, nVertices);
    }

    /**
     * Draws the coordinate axis
     *
     * @param gl
     * @param x
     * @param y
     * @param z
     */
    public void drawCAxis(GL3 gl, float x, float y, float z) {
        int nVertices = 2;

        float[] c = getCurrentColor();

        //z Axis
        rewindBuffer(gl);
        setColor(0, 0, 1);
        putVertex(0, 0, 0);
        putVertex(0, 0, z);
        copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_LINES, 0, nVertices);

        //y Axis
        rewindBuffer(gl);
        setColor(0, 1, 0);
        putVertex(0, 0, 0);
        putVertex(0, y, 0);
        copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_LINES, 0, nVertices);

        //x Axis
        rewindBuffer(gl);
        setColor(1, 0, 0);
        putVertex(0, 0, 0);
        putVertex(x, 0, 0);
        copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_LINES, 0, nVertices);

        setColor(c[0], c[1], c[2]);
    }

}
