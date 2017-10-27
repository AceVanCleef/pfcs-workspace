package ch.fhnw.pfcs.opengl;

import com.jogamp.opengl.*;
import com.jogamp.common.nio.*;

import java.nio.*;

public class MyVertexBufTx {

    private int maxVerts;                                     // max. Anzahl Vertices im Vertex-Array

    //  --------  Vertex-Array (fuer die Attribute Position, Color, Normal)  ------------
    private FloatBuffer vertexBuf;                            // Vertex-Array
    final int vPositionSize = 4 * Float.SIZE / 8;                 // Anz. Bytes der x,y,z,w (homogene Koordinaten)
    final int vColorSize = 4 * Float.SIZE / 8;                    // Anz. Bytes der rgba Werte
    final int vNormalSize = 4 * Float.SIZE / 8;                   // Anz. Bytes der Normalen
    final int vTexCoordSize = 4 * Float.SIZE / 8;                 // Anz. Bytes der Textur-Koord.


    final int vertexSize = vPositionSize + vColorSize +       // Anz. Bytes eines Vertex
            vNormalSize + vTexCoordSize;
    private int bufSize;                                      // Anzahl Bytes des VertexArrays = maxVerts * vertexSize

    private float[] currentColor = {1, 1, 1, 1};                // aktuelle Farbe fuer Vertices
    private float[] currentNormal = {1, 0, 0, 0};               // aktuelle Normale Vertices
    private float[] currentTexCoord = {0, 0, 0, 0};         // aktuelle Textur-Koord.

    // ------ Identifiers fuer OpenGL-Objekte und Shader-Variablen  ------

    private int vaoId;                                        // Identifier fuer OpenGL VertexArray Object
    private int vertexBufId;                                  // Identifier fuer OpenGL Vertex Buffer
    private int vPositionId, vColorId, vNormalId, vTexCoordId;   // Vertex Attribute Shader Variables


    //  ------------- Konstruktor  ---------------------------

    public MyVertexBufTx(GL3 gl,
                         int programId,                             // Program-Identifier
                         int maxVerts)                              // max. Anzahl Vertices im Vertex-Array
    {
        this.maxVerts = maxVerts;
        setupVertexBuffer(programId, gl, maxVerts);            // Vertex-Buffer
    }

    ;


    //  -------------  Methoden  ---------------------------

    private void setupVertexBuffer(int pgm, GL3 gl, int maxVerts) {
        bufSize = maxVerts * vertexSize;
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
        vTexCoordId = gl.glGetAttribLocation(pgm, "vertexTexCoord");

        //  ------  enable vertex attributes ---------------
        gl.glEnableVertexAttribArray(vPositionId);
        gl.glEnableVertexAttribArray(vColorId);
        gl.glEnableVertexAttribArray(vNormalId);
        gl.glEnableVertexAttribArray(vTexCoordId);
        gl.glVertexAttribPointer(vPositionId, 4, GL3.GL_FLOAT, false, vertexSize, 0);
        gl.glVertexAttribPointer(vColorId, 4, GL3.GL_FLOAT, false, vertexSize, vPositionSize);
        gl.glVertexAttribPointer(vNormalId, 4, GL3.GL_FLOAT, false, vertexSize, vPositionSize + vColorSize);
        gl.glVertexAttribPointer(vTexCoordId, 4, GL3.GL_FLOAT, false, vertexSize, vPositionSize +
                vColorSize + vNormalSize);
    }

    ;


    //  ----------  oeffentliche Methoden   -------------


    public void setColor(float r, float g, float b)             // aktuelle Vertexfarbe setzen
    {
        currentColor[0] = r;
        currentColor[1] = g;
        currentColor[2] = b;
        currentColor[3] = 1;
    }

    public void setNormal(float x, float y, float z)             // aktuelle Vertexfarbe setzen
    {
        currentNormal[0] = x;
        currentNormal[1] = y;
        currentNormal[2] = z;
        currentNormal[3] = 0;
    }

    public void setTexCoord(float s, float t)                    // aktuelle Textur-Koord. setzen
    {
        currentTexCoord[0] = s;
        currentTexCoord[1] = t;
    }

    public void putVertex(float x, float y, float z)            // Vertex-Daten in Buffer speichern
    {
        vertexBuf.put(x);
        vertexBuf.put(y);
        vertexBuf.put(z);
        vertexBuf.put(1);
        vertexBuf.put(currentColor);                              // Farbe
        vertexBuf.put(currentNormal);                             // Normale
        vertexBuf.put(currentTexCoord);                           // Textur-Koord.
    }

    public void copyBuffer(GL3 gl, int nVertices)                // Vertex-Array in OpenGL-Buffer kopieren
    {
        vertexBuf.rewind();
        if (nVertices > maxVerts)
            throw new IndexOutOfBoundsException();
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vertexBufId);
        gl.glBufferSubData(GL3.GL_ARRAY_BUFFER, 0, nVertices * vertexSize, vertexBuf);
    }

    public void rewindBuffer(GL3 gl)                            // Bufferposition zuruecksetzen
    {
        vertexBuf.rewind();
    }


    //  ---------  Abfrage-Methoden ----------

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

    public float[] getCurrentTexCoord() {
        float[] coord = {currentTexCoord[0],
                currentTexCoord[1]};
        return coord;
    }

    //  ---------  Zeichenmethoden  ------------------------------

    public void drawAxis(GL3 gl, float a, float b, float c)                   // Koordinatenachsen zeichnen
    {
        rewindBuffer(gl);
        putVertex(0, 0, 0);           // Eckpunkte in VertexArray speichern
        putVertex(a, 0, 0);
        putVertex(0, 0, 0);
        putVertex(0, b, 0);
        putVertex(0, 0, 0);
        putVertex(0, 0, c);
        int nVertices = 6;
        copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_LINES, 0, nVertices);
    }

}
