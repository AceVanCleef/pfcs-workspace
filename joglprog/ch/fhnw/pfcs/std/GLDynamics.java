package ch.fhnw.pfcs.std;

/**
 * Created by Claudio on 26.10.2016.
 */
public abstract class GLDynamics {
    /**
     * Vectorfield
     *
     * @param x
     * @return
     */
    public abstract double[] field(double[] x);

    public double[] euler(double[] x, double dt) {
        double[] y = field(x);
        double[] newX = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            newX[i] = x[i] + y[i] * dt;
        }
        return newX;
    }

    public double[] rungeKutta(double[] x, double dt) {
        double[] y1 = field(x);
        double[] x1 = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            x1[i] = x[i] + y1[i] * dt / 2;
        }

        double[] y2 = field(x1);
        double[] x2 = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            x2[i] = x[i] + y2[i] * dt / 2;
        }

        double[] y3 = field(x2);
        double[] x3 = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            x3[i] = x[i] + y3[i] * dt;
        }

        double[] y4 = field(x3);
        double[] y = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            y[i] = (y1[i] + 2 * y2[i] + 2 * y3[i] + y4[i]) / 6;
        }

        double[] newX = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            newX[i] = x[i] + y[i] * dt;
        }
        return newX;
    }
}
