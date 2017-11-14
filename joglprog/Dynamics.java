/**
 * dynamic systems library (--> Vektorfeldgeschichten)
 * @author degonas
 *
 */
public abstract class Dynamics {

	/**
	 * Calculates time step of particle in vector field.
	 * @param x
	 * @param dt
	 * @return
	 */
	public double[] euler(double[] x, double dt) {
		double[] y = f(x);
		
		//Next x: x' = x + y * dt
		double[] xNext = new double[x.length];
		for (int i = 0; i < x.length; ++i) {
			xNext[i] = x[i] + y[i] * dt;
			
		}
		return xNext;
	}
	
	/**
	 * Runge-Kutta calculates a more accurate y-Shift vector for a particle 
	 * in a vector field than the euleric algorithm.
	 * @param x
	 * @param dt
	 * @return
	 */
	public double[] rungeKutta(double[] x, double dt) {
		double[] y = f(x);
		
		//Next x: x' = x + y * dt
		double[] xNext = new double[x.length];
		double[] y1= f(x);
		for (int i = 0; i < x.length; ++i) {
			xNext[i] = x[i] + 0.5 * y1[i] * dt;
		}
		double[] y2 = f(xNext);
		for (int i = 0; i < x.length; ++i) {
			xNext[i] = x[i] + 0.5 * y2[i] * dt;
		}
		double[] y3 = f(xNext);
		for (int i = 0; i < x.length; ++i) {
			xNext[i] = x[i] + y3[i] * dt;	
		}
		double[] y4 = f(xNext);
		
		//Mitteln
		for (int i = 0; i < x.length; ++i) {
			xNext[i] = x[i] +
					dt * (y1[i] + 2*y2[i] + 2*y3[i] + y4[i]) / 6;
		}
		
		return xNext;
	}
	
	public abstract double[] f(double[] x);
}
