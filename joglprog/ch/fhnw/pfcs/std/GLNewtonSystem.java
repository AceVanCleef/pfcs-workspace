package ch.fhnw.pfcs.std;

/**
 * Created by Claudio on 09.11.2016.
 */
public abstract class GLNewtonSystem extends GLDynamics {
    protected double[] state;

    public GLNewtonSystem(double xStart, double yStart, double vx, double vy) {
        state = new double[]{xStart, yStart, vx, vy};
    }

    public abstract void move(double dt);

    public double getX() {
        return state[0];
    }

    public double getY() {
        return state[1];
    }
}
