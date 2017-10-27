package ch.fhnw.pfcs.std.viewing;

import ch.fhnw.pfcs.opengl.MyTransf;
import com.jogamp.opengl.GL3;

/**
 * Created by Claudio on 15.11.2016.
 */

public abstract class ViewingVolume {
    private float aspect;
    private float left, right, bottom, top, near, far;

    public ViewingVolume(float left, float right, float near, float far) {
        this.left = left;
        this.right = right;
        this.near = near;
        this.far = far;
    }

    public void setAspect(int width, int height) {
        aspect = (float) height / width;
        update(left, right, near, far);
    }

    public void update(float left, float right, float near, float far) {
        this.left = left;
        this.right = right;
        this.near = near;
        this.far = far;
        this.bottom = aspect * left;
        this.top = aspect * right;
    }

    public float[] getViewingData() {
        return new float[]{left, right, bottom, top, near, far};
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getRight() {
        return right;
    }

    public void setRight(float right) {
        this.right = right;
    }

    public float getNear() {
        return near;
    }

    public void setNear(float near) {
        this.near = near;
    }

    public float getFar() {
        return far;
    }

    public void setFar(float far) {
        this.far = far;
    }

    public abstract void setProjection(GL3 gl, MyTransf transf);
}
