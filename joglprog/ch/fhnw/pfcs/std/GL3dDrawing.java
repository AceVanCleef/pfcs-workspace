package ch.fhnw.pfcs.std;

import ch.fhnw.pfcs.opengl.MyVertexBuf;
import ch.fhnw.util.math.Vec3;
import com.jogamp.opengl.GL3;
//import com.sun.prism.impl.VertexBuffer;		//todo: is this really needed?

/**
 * Created by Claudio on 19.10.2016.
 */
public class GL3dDrawing {

    /**
     * Draws a line from A to B
     *
     * @param gl
     * @param vertexBuf
     * @param A         shoot point
     * @param B         end point
     */
    public static void drawLine(GL3 gl, MyVertexBuf vertexBuf, Vec3 A, Vec3 B) {
        vertexBuf.rewindBuffer(gl);
        vertexBuf.putVector(A);
        vertexBuf.putVector(B);
        int nVertices = 2;
        vertexBuf.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_LINES, 0, nVertices);
    }

    /**
     * Draws a trinangle between A B and C
     *
     * @param gl
     * @param vertexBuf
     * @param A         edge point
     * @param B         edge point
     * @param C         edge point
     */
    public static void drawTriangle(GL3 gl, MyVertexBuf vertexBuf, Vec3 A, Vec3 B, Vec3 C) {
        Vec3 U = A.subtract(B);
        Vec3 V = C.subtract(B);
        Vec3 normale = U.cross(V);         // Normalenvektor
        vertexBuf.setNormal(normale.x, normale.y, normale.z);
        vertexBuf.rewindBuffer(gl);
        vertexBuf.putVector(A);
        vertexBuf.putVector(B);
        vertexBuf.putVector(C);
        int nVertices = 3;
        vertexBuf.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_TRIANGLES, 0, nVertices);
    }

    /**
     * @param gl
     * @param vertexBuf
     * @param A         edge point
     * @param B         edge point
     * @param C         edge point
     * @param D         edge point
     */
    public static void drawQuadrangle(GL3 gl, MyVertexBuf vertexBuf, Vec3 A, Vec3 B, Vec3 C, Vec3 D) {
        vertexBuf.rewindBuffer(gl);
        Vec3 u = B.subtract(A);
        Vec3 v = C.subtract(A);
        Vec3 n = u.cross(v);
        vertexBuf.setNormal(n.x, n.y, n.z);
        vertexBuf.putVertex(A.x, A.y, A.z);     // Eckpunkte
        vertexBuf.putVertex(B.x, B.y, B.z);
        vertexBuf.putVertex(C.x, C.y, C.z);
        vertexBuf.putVertex(D.x, D.y, D.z);
        int nVertices = 4;
        vertexBuf.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, nVertices);
    }

    /**
     * @param gl
     * @param vertexBuf
     * @param A         vector dir 1
     * @param B         vector dir 2
     */
    public static void drawRhombus(GL3 gl, MyVertexBuf vertexBuf, Vec3 A, Vec3 B) {
        vertexBuf.rewindBuffer(gl);
        Vec3 normale = B.cross(A);
        vertexBuf.setNormal(normale.x, normale.y, normale.z);
        vertexBuf.putVertex(0, 0, 0);
        vertexBuf.putVector(A);
        vertexBuf.putVector(B);
        vertexBuf.putVector(A.add(B));
        int nVertices = 4;
        vertexBuf.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_TRIANGLE_STRIP, 0, nVertices);
    }

    /**
     * @param gl
     * @param vertexBuf
     * @param a         length in x dir
     * @param b         height in y dir
     * @param c         width in z dir
     */
    public static void drawCuboid(GL3 gl, MyVertexBuf vertexBuf, double a, double b, double c) {
        Vec3 A = new Vec3(a, 0, c);
        Vec3 B = new Vec3(a, 0, 0);
        Vec3 C = new Vec3(0, 0, 0);
        Vec3 D = new Vec3(0, 0, c);
        Vec3 E = new Vec3(a, b, c);
        Vec3 F = new Vec3(a, b, 0);
        Vec3 G = new Vec3(0, b, 0);
        Vec3 H = new Vec3(0, b, c);
        drawQuadrangle(gl, vertexBuf, D, C, B, A);
        drawQuadrangle(gl, vertexBuf, E, F, G, H);
        drawQuadrangle(gl, vertexBuf, A, B, F, E);
        drawQuadrangle(gl, vertexBuf, B, C, G, F);
        drawQuadrangle(gl, vertexBuf, C, D, H, G);
        drawQuadrangle(gl, vertexBuf, D, A, E, H);
    }

    /**
     * @param gl
     * @param a  vector dir 1
     * @param b  vector dir 2
     * @param c  vector dir 3
     */
    void drawParallelepiped(GL3 gl, MyVertexBuf vertexBuf, Vec3 a, Vec3 b, Vec3 c) {
        Vec3 A = new Vec3(0, 0, 0);
        Vec3 B = a;
        Vec3 C = a.add(b);
        Vec3 D = b;
        Vec3 E = c;
        Vec3 F = a.add(c);
        Vec3 G = a.add(b).add(c);
        Vec3 H = b.add(c);
        drawQuadrangle(gl, vertexBuf, D, C, B, A);
        drawQuadrangle(gl, vertexBuf, E, F, G, H);
        drawQuadrangle(gl, vertexBuf, A, B, F, E);
        drawQuadrangle(gl, vertexBuf, B, C, G, F);
        drawQuadrangle(gl, vertexBuf, C, D, H, G);
        drawQuadrangle(gl, vertexBuf, D, A, E, H);
    }

    /**
     * @param gl
     * @param vertexBuf
     * @param A         floor edge point
     * @param B         floor edge point
     * @param C         floor edge point
     * @param D         floor edge point
     * @param H         top point
     */
    public static void drawPyramid(GL3 gl, MyVertexBuf vertexBuf, Vec3 A, Vec3 B, Vec3 C, Vec3 D, Vec3 H) {
        drawTriangle(gl, vertexBuf, A, B, H);
        drawTriangle(gl, vertexBuf, B, C, H);
        drawTriangle(gl, vertexBuf, C, D, H);
        drawTriangle(gl, vertexBuf, D, A, H);
    }

    /**
     * Draws a integral curve according to curve
     *
     * @param gl
     * @param vertexBuf
     * @param curve     collection of 3d points
     */
    public static void drawIntegralCurve(GL3 gl, MyVertexBuf vertexBuf, double[][] curve) {
        vertexBuf.rewindBuffer(gl);
        int nVertices = curve.length;
        for (int i = 0; i < nVertices; i++) {
            double[] current = curve[i];
            vertexBuf.putVertex((float) current[0], (float) current[1], (float) current[2]);
        }
        vertexBuf.copyBuffer(gl, nVertices);
        gl.glDrawArrays(GL3.GL_LINE_STRIP, 0, nVertices);
    }
}
