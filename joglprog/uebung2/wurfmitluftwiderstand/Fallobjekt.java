package uebung2.wurfmitluftwiderstand;

import com.jogamp.opengl.GL3;

import ch.fhnw.util.math.Mat4;

public class Fallobjekt extends PhysObj {

	public Fallobjekt(MyGLBase1 mygl, 
			double v0x, double v0y,
			double x0,	double y0,
			double x,	double y,
			double vx,	double vy,
			double ax,	double ay,
			double dt,
			double ybottom) {
		super(mygl, v0x, v0y, x0, y0, x, y, vx, vy, ax, ay, dt, ybottom);
	}
	
	
	/******************* Methoden ************************/
	
	@Override
	public void draw(GL3 gl, float...args) {
		float width = 0.5f, height = 2;
		
		float x1 = args[0], y1 = args[1];
		float x2 = x1 + width, y2  = y1;
        float x3 = x1, y3 = y1 + height;
        float x4 = x1 + width, y4 = y1 + height;
        
    	getMygl().putVertex(x1, y1, 0);
    	getMygl().putVertex(x2, y2, 0);
    	getMygl().putVertex(x3, y3, 0);
    	getMygl().putVertex(x4, y4, 0);
    	
    	getMygl().copyBuffer(gl);
    	getMygl().drawArrays(gl,GL3.GL_TRIANGLE_STRIP);
	}
	
	@Override
	public void updateCoords(){
		super.updateCoords();
	}
	
	@Override
	public void rotateRelative(GL3 gl, Mat4 M){
		super.rotateRelative(gl, M);
	}
	
	
	/******************* get / set ************************/
	
	public MyGLBase1 getMygl() {
		return super.getMygl();
	}


	public void setMygl(MyGLBase1 mygl) {
		super.setMygl(mygl);
	}


	public double getV0x() {
		return super.getV0x();
	}


	public void setV0x(double v0x) {
		super.setV0x(v0x);
	}


	public double getV0y() {
		return super.getV0y();
	}


	public void setV0y(double v0y) {
		super.setV0y(v0y);;
	}


	public double getX0() {
		return super.getX0();
	}


	public void setX0(double x0) {
		super.setX0(x0);;
	}


	public double getY0() {
		return super.getY0();
	}


	public void setY0(double y0) {
		super.getY0();
	}


	public double getX() {
		return super.getX();
	}


	public void setX(double x) {
		super.setX(x);
	}


	public double getY() {
		return super.getY();
	}


	public void setY(double y) {
		super.setY(y);
	}


	public double getVx() {
		return super.getVx();
	}


	public void setVx(double vx) {
		super.setVx(vx);
	}


	public double getVy() {
		return super.getVy();
	}


	public void setVy(double vy) {
		super.setVy(vy);
	}


	public double getAx() {
		return super.getAx();
	}


	public void setAx(double ax) {
		super.setAx(ax);
	}


	public double getAy() {
		return super.getAy();
	}


	public void setAy(double ay) {
		super.setAy(ay);
	}


	public double getDt() {
		return super.getDt();
	}


	public void setDt(double dt) {
		super.setDt(dt);
	}


	public double getYbottom() {
		return super.getYbottom();
	}


	public void setYbottom(double ybottom) {
		super.setYbottom(ybottom);
	}

}
