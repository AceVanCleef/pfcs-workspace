package ch.fhnw.pfcs.std.viewing;

import ch.fhnw.pfcs.opengl.MyTransf;
import com.jogamp.opengl.GL3;

/**
 * Created by Claudio on 15.11.2016.
 */
public class PerspectiveProjection extends ViewingVolume {
    public PerspectiveProjection(float left, float right, float near, float far) {
        super(left, right, near, far);
    }

    @Override
    public void setProjection(GL3 gl, MyTransf transf) {
        float[] data = getViewingData();
        transf.setPerspectiveProjection(gl, data[0], data[1], data[2], data[3], data[4], data[5]);
    }
}
