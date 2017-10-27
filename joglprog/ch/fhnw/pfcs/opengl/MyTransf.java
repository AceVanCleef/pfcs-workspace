package ch.fhnw.pfcs.opengl; /**
 * @author E. Gutknecht
 * @date August 2016
 */

import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;
import com.jogamp.opengl.GL3;

import java.util.Stack;

public class MyTransf {
    // ModelView Matrix
    private Mat4 M = Mat4.ID;
    // PerspectiveProjection Matrix
    private Mat4 P = Mat4.ID;

    private Stack<Mat4> matrixStack = new Stack<>();

    // Uniform Shader Variables
    private final int modelViewMatrixId, projMatrixId;

    public MyTransf(GL3 gl, int programId) {
        // Get shader variable identifiers  -------------
        modelViewMatrixId = gl.glGetUniformLocation(programId, "modelViewMatrix");
        projMatrixId = gl.glGetUniformLocation(programId, "projMatrix");
        // -----  set uniform variables  -----------------------
        gl.glUniformMatrix4fv(modelViewMatrixId, 1, false, M.toArray(), 0);
        gl.glUniformMatrix4fv(projMatrixId, 1, false, P.toArray(), 0);
    }

    private void setupMatrices(int programId, GL3 gl) {

    }

    public void setModelViewMatrix(GL3 gl, Mat4 M) {
        this.M = M;
        gl.glUniformMatrix4fv(modelViewMatrixId, 1, false, M.toArray(), 0);
    }

    public void pushMatrix(GL3 gl) {
        matrixStack.push(M);
        gl.glUniformMatrix4fv(modelViewMatrixId, 1, false, M.toArray(), 0);
    }

    public void popMatrix(GL3 gl) {
        M = matrixStack.pop();
        gl.glUniformMatrix4fv(modelViewMatrixId, 1, false, M.toArray(), 0);
    }

    /**
     * Resets to identity matrix
     *
     * @param gl
     */
    public void loadIdentity(GL3 gl) {
        setModelViewMatrix(gl, Mat4.ID);
    }

    /**
     * M = M * A
     *
     * @param gl
     * @param A
     */
    public void multMatrix(GL3 gl, Mat4 A) {
        M = M.postMultiply(A);
        gl.glUniformMatrix4fv(modelViewMatrixId, 1, false, M.toArray(), 0);
    }

    /**
     * Sets the camera system
     *
     * @param gl
     * @param A         camera position
     * @param B         target
     * @param rollAngle
     */
    public void setCameraSystem(GL3 gl, Vec3 A, Vec3 B, float rollAngle) {
        Vec3 up = new Vec3(0, 1, 0);
        M = Mat4.lookAt(A, B, up);
        Mat4 R = Mat4.rotate(-rollAngle, 0, 0, 1);
        M = R.postMultiply(M);
        gl.glUniformMatrix4fv(modelViewMatrixId, 1, false, M.toArray(), 0);
    }

    /**
     * Sets the camera system
     *
     * @param gl
     * @param A  camera position
     * @param B  target
     * @param up vector
     */
    public void setCameraSystem(GL3 gl, Vec3 A, Vec3 B, Vec3 up) {
        M = Mat4.lookAt(A, B, up);
        gl.glUniformMatrix4fv(modelViewMatrixId, 1, false, M.toArray(), 0);
    }

    /**
     * Sets the camera system
     *
     * @param gl
     * @param A         camera position
     * @param azimut    angle in degrees
     * @param elevation angle in degrees
     */
    public void setCameraSystem(GL3 gl, Vec3 A, float azimut, float elevation) {
        float toRad = (float) (Math.PI / 180);
        float cc = (float) Math.cos(toRad * elevation);
        float ss = (float) Math.sin(toRad * elevation);
        float c = (float) Math.cos(toRad * azimut);
        float s = (float) Math.sin(toRad * azimut);
        M = new Mat4(c, -s * ss, s * cc, 0,     // Spalte 1
                0, cc, ss, 0,                   // Spalte 2
                -s, -c * ss, c * cc, 0,         // Spalte 3
                -c * A.x + s * A.z, s * ss * A.x - cc * A.y + c * ss * A.z, -s * cc * A.x - ss * A.y - c * cc * A.z, 1); // Spalte 4
        gl.glUniformMatrix4fv(modelViewMatrixId, 1, false, M.toArray(), 0);
    }

    /**
     * Sets the camera system
     *
     * @param gl
     * @param r         distance between camera and O
     * @param elevation angle in degrees
     * @param azimuth   angle in degrees
     */
    public void setCameraSystem(GL3 gl, float r, float azimuth, float elevation) {
        float toRad = (float) (Math.PI / 180);
        float c = (float) Math.cos(toRad * elevation);
        float s = (float) Math.sin(toRad * elevation);
        float cc = (float) Math.cos(toRad * azimuth);
        float ss = (float) Math.sin(toRad * azimuth);
        M = new Mat4(cc, -s * ss, c * ss, 0, 0, c, s, 0, -ss, -s * cc, c * cc, 0, 0, 0, -r, 1);
        gl.glUniformMatrix4fv(modelViewMatrixId, 1, false, M.toArray(), 0);
    }

    /**
     * Rotates the object system
     *
     * @param gl
     * @param phi rotation angle in degrees
     * @param x   coordinate of rotation axis
     * @param y   coordinate of rotation axis
     * @param z   coordinate of rotation axis
     */
    public void rotate(GL3 gl, float phi, float x, float y, float z) {
        multMatrix(gl, Mat4.rotate(phi, x, y, z));
    }

    /**
     * Translates the object system
     *
     * @param gl
     * @param x  coordinate of translation
     * @param y  coordinate of translation
     * @param z  coordinate of translation
     */
    public void translate(GL3 gl, float x, float y, float z) {
        multMatrix(gl, Mat4.translate(x, y, z));
    }

    /**
     * Scales the object system
     * Only xyz-factor for normaltransformation
     *
     * @param gl
     * @param scale
     */
    public void scale(GL3 gl, float scale) {
        multMatrix(gl, Mat4.scale(scale, scale, scale));
    }

    public void setProjectionMatrix(GL3 gl, Mat4 P) {
        this.P = P;
        gl.glUniformMatrix4fv(projMatrixId, 1, false, P.toArray(), 0);
    }

    /**
     * Sets the orthogonal projection to image plane
     *
     * @param gl
     * @param left
     * @param right
     * @param bottom
     * @param top
     * @param near
     * @param far
     */
    public void setOrthogonalProjection(GL3 gl, float left, float right, float bottom, float top,
                                        float near, float far) {
        P = Mat4.ortho(left, right, bottom, top, near, far);
        gl.glUniformMatrix4fv(projMatrixId, 1, false, P.toArray(), 0);
    }

    /**
     * Sets the central projection to image plane
     *
     * @param gl
     * @param left
     * @param right
     * @param bottom
     * @param top
     * @param near
     * @param far
     */
    public void setPerspectiveProjection(GL3 gl, float left, float right,     // Zentralprojektion auf Bildebene
                                         float bottom, float top,
                                         float near, float far) {
        P = Mat4.perspective(left, right, bottom, top, near, far);
        gl.glUniformMatrix4fv(projMatrixId, 1, false, P.toArray(), 0);
    }

    public Mat4 getModelViewMatrix() {
        return M;
    }

    public Mat4 getProjectionMatrix() {
        return P;
    }

}
