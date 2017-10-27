package ch.fhnw.pfcs.std;

/**
 * Created by Claudio on 09.11.2016.
 */
public class GLKepler extends GLNewtonSystem {
    private double GM;

    public GLKepler(double x0, double y0, double vx0, double vy0, double GM) {
        super(x0, y0, vx0, vy0);
        this.GM = GM;
    }

    @Override
    public double[] field(double[] x) {
        double xx = x[0], yy = x[1];
        double vx = x[2], vy = x[3];
        double r3 = Math.pow(xx * xx + yy * yy, 3. / 2);

        return new double[]{
                vx,
                vy,
                -GM / r3 * xx,
                -GM / r3 * yy
        };
    }

    @Override
    public void move(double dt) {
        state = rungeKutta(state, dt);
    }

    public double getX() {
        return state[0];
    }

    public double getY() {
        return state[1];
    }
}
