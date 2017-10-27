package ch.fhnw.pfcs.std;

import ch.fhnw.pfcs.opengl.MyVertexBuf;
import com.jogamp.opengl.GL3;

/**
 * Created by Claudio on 09.10.2016.
 */
public abstract class GL2dDrawing {
    public static void drawLine(GL3 gl, MyVertexBuf vertexBuf, float x1, float y1, float x2, float y2) {
        vertexBuf.rewindBuffer(gl);

        vertexBuf.putVertex(x1, y1, 0);
        vertexBuf.putVertex(x2, y2, 0);

        int nVertices = 2;
        vertexBuf.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_LINES, 0, nVertices);
    }

    public static void drawTriangle(GL3 gl, MyVertexBuf vertexBuf, float x1, float y1, float x2, float y2, float x3, float y3) {
        vertexBuf.rewindBuffer(gl);

        vertexBuf.putVertex(x1, y1, 0);
        vertexBuf.putVertex(x2, y2, 0);
        vertexBuf.putVertex(x3, y3, 0);

        int nVertices = 3;
        vertexBuf.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_TRIANGLES, 0, nVertices);
    }

    public static void drawRect(GL3 gl, MyVertexBuf vertexBuf, float x, float y, float dx, float dy) {
        vertexBuf.rewindBuffer(gl);

        vertexBuf.putVertex(x, y, 0);
        vertexBuf.putVertex(x, y + dy, 0);
        vertexBuf.putVertex(x + dx, y, 0);
        vertexBuf.putVertex(x + dx, y + dy, 0);

        int nVertices = 4;
        vertexBuf.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_TRIANGLE_STRIP, 0, nVertices);
    }

    public static void drawCircle(GL3 gl, MyVertexBuf vertexBuf, float radius, int n) {
        vertexBuf.rewindBuffer(gl);

        double phi = 2 * Math.PI / n;
        vertexBuf.putVertex(0, 0, 0);    //center
        for (int i = 0; i <= n; i++) {
            vertexBuf.putVertex((float) (radius * Math.cos(i * phi)),
                    (float) (radius * Math.sin(i * phi)), 0);
        }

        int nVertices = n + 2;
        vertexBuf.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, nVertices);
    }

    public static void drawLineLoop(GL3 gl, MyVertexBuf vertexBuf, int n) {
        //TODO
    }

    /**
     * @param gl
     * @param vertexBuf
     * @param a         semi-major
     * @param b         semi-minor
     * @param nPoints
     */
    public static void drawEllipseLine(GL3 gl, MyVertexBuf vertexBuf, float a, float b, int nPoints) {
        if (nPoints < 4) throw new IllegalArgumentException("nPoints must be >= 4");
        if (!(a > 0 && b > 0)) throw new IllegalArgumentException("a and b must be > 0");
        if (a < b) throw new IllegalArgumentException("a is semi-major, b is semi-minor");
        else if (a == b) drawCircle(gl, vertexBuf, a, nPoints);

        vertexBuf.rewindBuffer(gl);
        float dy;
        if ((nPoints & 1) == 0) {
            dy = 4 * b / nPoints;
            float[][] ay = new float[nPoints / 4][2];
            vertexBuf.putVertex(a, 0, 0);
            float y = 0;
            for (int i = 0; i < ay.length; i++) {
                y += dy;
                ay[i][0] = (float) Math.sqrt((a * a * (b * b - y * y)) / (b * b));
                ay[i][1] = y;
                vertexBuf.putVertex(ay[i][0], ay[i][1], 0);
            }
            for (int i = ay.length - 1; i >= 0; i++) {
                vertexBuf.putVertex(-ay[i][0], ay[i][1], 0);
            }
            vertexBuf.putVertex(-a, 0, 0);
            for (int i = 0; i < ay.length; i++) {
                vertexBuf.putVertex(-ay[i][0], -ay[i][1], 0);
            }
            for (int i = ay.length - 1; i >= 0; i++) {
                vertexBuf.putVertex(ay[i][0], -ay[i][1], 0);
            }
            int nVertices = ay.length * 4 + 2;
            vertexBuf.copyBuffer(gl, nVertices);
            gl.glDrawArrays(GL3.GL_LINE_LOOP, 0, nVertices);
        } else {
            //TODO
            throw new UnsupportedOperationException();
        }
    }
}
