package ch.fhnw.pfcs.opengl;


import ch.fhnw.util.math.Vec3;
import com.jogamp.opengl.GL3;

public class QuaderV2 {

    private static Vec3 e1 = new Vec3(1, 0, 0);               // Normalenvektoren
    private static Vec3 e2 = new Vec3(0, 1, 0);
    private static Vec3 e3 = new Vec3(0, 0, 1);
    private static Vec3 e1n = new Vec3(-1, 0, 0);             // negative Richtung
    private static Vec3 e2n = new Vec3(0, -1, 0);
    private static Vec3 e3n = new Vec3(0, 0, -1);

    private float m;
    private float a, b, c;

    public QuaderV2(float m, float a, float b, float c) {
        this.m = m;
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public void zeichne(GL3 gl, MyVertexBuf vertexBuf) {
        zeichne(gl, vertexBuf, a, b, c, true);
    }

    public float getM() {
        return m;
    }

    public float getA() {
        return a;
    }

    public float getB() {
        return b;
    }

    public float getC() {
        return c;
    }

    public static void viereck(MyVertexBuf vertexBuf, Vec3 A, Vec3 B, Vec3 C, Vec3 D, Vec3 n) {
        vertexBuf.setNormal(n.x, n.y, n.z);
        vertexBuf.putVertex(A.x, A.y, A.z);          // Dreieck 1
        vertexBuf.putVertex(B.x, B.y, B.z);
        vertexBuf.putVertex(C.x, C.y, C.z);
        vertexBuf.putVertex(C.x, C.y, C.z);          // Dreieck 2
        vertexBuf.putVertex(D.x, D.y, D.z);
        vertexBuf.putVertex(A.x, A.y, A.z);
    }

    public static void kante(MyVertexBuf vertexBuf, Vec3 a, Vec3 b) {
        vertexBuf.putVertex(a.x, a.y, a.z);
        vertexBuf.putVertex(b.x, b.y, b.z);
    }

    public static void zeichne(GL3 gl, MyVertexBuf vertexBuf, float a, float b, float c, boolean gefuellt) {
        a *= 0.5f;
        b *= 0.5f;
        c *= 0.5f;
        Vec3 A = new Vec3(a, -b, c);                        // Bodenpunkte
        Vec3 B = new Vec3(a, -b, -c);
        Vec3 C = new Vec3(-a, -b, -c);
        Vec3 D = new Vec3(-a, -b, c);
        Vec3 E = new Vec3(a, b, c);                         // Deckpunkte
        Vec3 F = new Vec3(a, b, -c);
        Vec3 G = new Vec3(-a, b, -c);
        Vec3 H = new Vec3(-a, b, c);
        vertexBuf.rewindBuffer(gl);
        int nVertices;
        if (gefuellt) {
            viereck(vertexBuf, D, C, B, A, e2n);            // Boden
            viereck(vertexBuf, E, F, G, H, e2);             // Deckflaeche
            viereck(vertexBuf, A, B, F, E, e1);             // Seitenflaechen
            viereck(vertexBuf, B, C, G, F, e3n);
            viereck(vertexBuf, D, H, G, C, e1n);
            viereck(vertexBuf, A, E, H, D, e3);
            nVertices = 36;
            vertexBuf.copyBuffer(gl, nVertices);
            gl.glDrawArrays(GL3.GL_TRIANGLES, 0, nVertices);
        } else {
            kante(vertexBuf, A, B);                         // Boden
            kante(vertexBuf, B, C);
            kante(vertexBuf, C, D);
            kante(vertexBuf, D, A);
            kante(vertexBuf, E, F);                         // Decke
            kante(vertexBuf, F, G);
            kante(vertexBuf, G, H);
            kante(vertexBuf, H, E);
            kante(vertexBuf, A, E);                         // Kanten
            kante(vertexBuf, B, F);
            kante(vertexBuf, C, G);
            kante(vertexBuf, D, H);
            nVertices = 24;
            vertexBuf.copyBuffer(gl, nVertices);
            gl.glDrawArrays(GL3.GL_LINES, 0, nVertices);
        }
    }

}