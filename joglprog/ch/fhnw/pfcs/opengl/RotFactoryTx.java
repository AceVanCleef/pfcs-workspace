package ch.fhnw.pfcs.opengl;
//  -------------   Rotations-Koerper  (Kugel, Torus, Zylinder) mit Textur-Koord.  ------------
//                                                            E.Gutknecht, Dezember 2016

import com.jogamp.opengl.GL3;

public class RotFactoryTx {
    private MyVertexBufTx vb;

    public RotFactoryTx(MyVertexBufTx vb) {
        this.vb = vb;
    }

    /**
     * Calculates n1 * n2 points-grid of a rotation area
     * The defines curve will be rotated around the y-axis
     *
     * @param x   curve
     * @param y   curve
     * @param nx  normals
     * @param ny  normals
     * @param xa  grid
     * @param ya  grid
     * @param za  grid
     * @param nxa rotated normals
     * @param nya rotated normals
     * @param nza rotated normals
     * @param txa texture coordinate
     * @param tya texture coordinate
     */
    private void calculatePoints(float[] x, float[] y, float[] nx, float[] ny, float[][] xa, float[][] ya, float[][] za,
                                 float[][] nxa, float[][] nya, float[][] nza, float[][] txa, float[][] tya) {
        int n1 = xa.length;                            // Anzahl Zeilen
        int n2 = xa[0].length - 1;                         // Anzahl Spalten
        float todeg = (float) (180 / Math.PI);
        float dtheta = (float) (2 * Math.PI / n2);        // Drehwinkel
        float c = (float) Math.cos(dtheta);             // Koeff. der Drehmatrix
        float s = (float) Math.sin(dtheta);

        float ds = 1.0f / n2;                          // Zuwachs Textur-Koord. s (horizontal)
        float dt = 1.0f / (n1 - 1);                      // Zuwachs Textur-Koord. t (vertikal)


        for (int i = 0; i < n1; i++)                     // first north-south line
        {
            xa[i][0] = x[i];
            ya[i][0] = y[i];
            nxa[i][0] = nx[i];
            nya[i][0] = ny[i];
            txa[i][0] = 0;
            tya[i][0] = 1 - i * dt;
        }

        int j2;
        for (int j = 0; j < n2; j++)                     // n2 north-south lines
            for (int i = 0; i < n1; i++) {
                j2 = j + 1;
                xa[i][j2] = c * xa[i][j] + s * za[i][j];         // rotation y-axis
                ya[i][j2] = ya[i][j];
                za[i][j2] = -s * xa[i][j] + c * za[i][j];
                nxa[i][j2] = c * nxa[i][j] + s * nza[i][j];      // rotated normal vector
                nya[i][j2] = nya[i][j];
                nza[i][j2] = -s * nxa[i][j] + c * nza[i][j];
                txa[i][j2] = j2 * ds;                         // texture coordinates
                tya[i][j2] = tya[i][j];
            }
    }


    public void drawRotationArea(GL3 gl,            // Rotationsflaeche (Rotation um y-Achse)
                                 float[] x, float[] y,                // Kurve in xy-Ebene
                                 float[] nx, float[] ny,              // Normalenvektoren
                                 int n2)                              // Anzahl Drehungen um y-Achse
    {
        int n1 = x.length;                             // Anzahl Breitenlinien
        float[][] xa = new float[n1][n2 + 1];              // Vertex-Koordinaten
        float[][] ya = new float[n1][n2 + 1];
        float[][] za = new float[n1][n2 + 1];
        float[][] nxa = new float[n1][n2 + 1];             // Normalen
        float[][] nya = new float[n1][n2 + 1];
        float[][] nza = new float[n1][n2 + 1];
        float[][] txa = new float[n1][n2 + 1];             // Textur-Koord.
        float[][] tya = new float[n1][n2 + 1];

        calculatePoints(x, y, nx, ny, xa, ya, za, nxa, nya, nza, txa, tya);


        int j2;
        vb.rewindBuffer(gl);
        for (int j = 0; j < n2; j++)                     // n2 Streifen von Norden nach Sueden
            for (int i = 0; i < n1; i++) {
                vb.setNormal(nxa[i][j], nya[i][j], nza[i][j]);
                vb.setTexCoord(txa[i][j], tya[i][j]);
                vb.putVertex(xa[i][j], ya[i][j], za[i][j]);
                j2 = j + 1;
                vb.setNormal(nxa[i][j2], nya[i][j2], nza[i][j2]);
                vb.setTexCoord(txa[i][j2], tya[i][j2]);
                vb.putVertex(xa[i][j2], ya[i][j2], za[i][j2]);
            }
        int nVertices = 2 * n1 * n2;
        vb.copyBuffer(gl, nVertices);
        int nVerticesStreifen = 2 * n1;                  // Anzahl Vertices eines Streifens
        for (int j = 0; j < n2; j++)                     // die Streifen muessen einzeln gezeichnet werden
            gl.glDrawArrays(GL3.GL_TRIANGLE_STRIP, j * nVerticesStreifen, nVerticesStreifen);  // Streifen von Norden nach Sueden
    }

    /**
     * Draws the rotation grid rotated around the y-axis
     *
     * @param gl
     * @param x  curve
     * @param y  curve
     * @param nx normals
     * @param ny normals
     * @param n2 number of rotations
     */
    public void drawRotationGrid(GL3 gl,         // Rotationsflaeche (Rotation um y-Achse)
                                 float[] x, float[] y,                // Kurve in xy-Ebene
                                 float[] nx, float[] ny,              // Normalenvektoren
                                 int n2)                              // Anzahl Drehungen um y-Achse
    {
        int n1 = x.length;                            // Anzahl Breitenlinien
        float[][] xa = new float[n1][n2 + 1];                    // Vertex-Koordinaten
        float[][] ya = new float[n1][n2 + 1];
        float[][] za = new float[n1][n2 + 1];
        float[][] nxa = new float[n1][n2 + 1];                   // Normalen
        float[][] nya = new float[n1][n2 + 1];
        float[][] nza = new float[n1][n2 + 1];
        float[][] txa = new float[n1][n2 + 1];             // Textur-Koord.
        float[][] tya = new float[n1][n2 + 1];

        calculatePoints(x, y, nx, ny,
                xa, ya, za, nxa, nya, nza, txa, tya);
        vb.rewindBuffer(gl);
        for (int i = 0; i < n1; i++)                     // n1 Breitenlinien (Kresie um y-Achse)
            for (int j = 0; j <= n2; j++) {
                vb.setNormal(nxa[i][j], nya[i][j], nza[i][j]);
                vb.setTexCoord(txa[i][j], tya[i][j]);
                vb.putVertex(xa[i][j], ya[i][j], za[i][j]);
            }
        int nVertices = n1 * (n2 + 1);
        vb.copyBuffer(gl, nVertices);
        int nVerticesOffset = n2 + 1;                  // Anzahl Vertices einer Breitenlinie
        for (int i = 0; i < n1; i++)                     // die Linien muessen einzeln gezeichnet werden
            gl.glDrawArrays(GL3.GL_LINE_STRIP, i * nVerticesOffset, n2 + 1);  // Breitenlinie
        vb.rewindBuffer(gl);
        for (int j = 0; j < n2; j++)                     // n2 Laengslinien
            for (int i = 0; i < n1; i++) {
                vb.setNormal(nxa[i][j], nya[i][j], nza[i][j]);
                vb.setTexCoord(txa[i][j], tya[i][j]);
                vb.putVertex(xa[i][j], ya[i][j], za[i][j]);
            }
        nVertices = n1 * n2;
        vb.copyBuffer(gl, nVertices);
        nVerticesOffset = n1;                  // Anzahl Vertices einer Laengslinie
        for (int j = 0; j < n2; j++)                     // die Linien muessen einzeln gezeichnet werden
            gl.glDrawArrays(GL3.GL_LINE_STRIP, j * nVerticesOffset, n1);  // Laengslinie
    }


    public void drawBall(GL3 gl, float r, int n1, int n2, boolean solid) {
        float[] x = new float[n1];                     // Halbkreis in xy-Ebene von Nord- zum Suedpol
        float[] y = new float[n1];
        float[] nx = new float[n1];                    // Normalenvektoren
        float[] ny = new float[n1];
        float dphi = (float) (Math.PI / (n1 - 1)), phi;
        for (int i = 0; i < n1; i++) {
            phi = (float) (0.5 * Math.PI) - i * dphi;
            x[i] = r * (float) Math.cos(phi);
            y[i] = r * (float) Math.sin(phi);
            nx[i] = x[i];
            ny[i] = y[i];
        }
        if (solid)
            drawRotationArea(gl, x, y, nx, ny, n2);
        else
            drawRotationGrid(gl, x, y, nx, ny, n2);
    }


    public void drawTorus(GL3 gl, float r, float R, int n1, int n2, boolean solid) {
        int nn1 = n1 + 1;
        float[] x = new float[nn1];                    // Kreis in xy-Ebene
        float[] y = new float[nn1];
        float[] nx = new float[nn1];                   // Normalenvektoren
        float[] ny = new float[nn1];
        float dphi = 2 * (float) (Math.PI / n1), phi;
        for (int i = 0; i <= n1; i++) {
            phi = i * dphi;
            x[i] = r * (float) Math.cos(phi);
            y[i] = r * (float) Math.sin(phi);
            nx[i] = x[i];
            ny[i] = y[i];
            x[i] += R;
        }
        if (solid)
            drawRotationArea(gl, x, y, nx, ny, n2);
        else
            drawRotationGrid(gl, x, y, nx, ny, n2);
    }


    public void drawCylinder(GL3 gl, float r, float s, int n1, int n2, boolean solid) {
        float[] x = new float[n1];                     // Mantellinie in xy-Ebene
        float[] y = new float[n1];
        float[] nx = new float[n1];                    // Normalenvektoren
        float[] ny = new float[n1];
        float dy = s / (n1 - 1);
        for (int i = 0; i < n1; i++) {
            x[i] = r;
            y[i] = s - i * dy;
            nx[i] = 1;
            ny[i] = 0;
        }
        if (solid)
            drawRotationArea(gl, x, y, nx, ny, n2);
        else
            drawRotationGrid(gl, x, y, nx, ny, n2);

        //  ------  Grund-Kreis (y=0) -------
        int nPkte = n2;
        float[] xx = new float[nPkte + 1];
        float[] zz = new float[nPkte + 1];
        float phi = 2 * (float) Math.PI / nPkte;
        for (int i = 0; i <= nPkte; i++) {
            xx[i] = r * (float) Math.cos(i * phi);
            zz[i] = -r * (float) Math.sin(i * phi);
        }
        vb.rewindBuffer(gl);
        vb.setNormal(0, -1, 0);
        vb.putVertex(0, 0, 0);
        for (int i = 0; i <= nPkte; i++)
            vb.putVertex(xx[i], 0, zz[i]);
        int nVertices = nPkte + 2;
        vb.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, nVertices);

        //  ------  Deck-Kreis  (y=s) -------
        vb.rewindBuffer(gl);
        vb.setNormal(0, 1, 0);
        vb.putVertex(0, s, 0);
        for (int i = 0; i <= nPkte; i++)
            vb.putVertex(xx[i], s, zz[i]);
        vb.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, nVertices);
    }

}
