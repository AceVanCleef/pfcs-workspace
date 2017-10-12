package uebung2.wurfmitluftwiderstand;

import com.jogamp.opengl.GL3;

import ch.fhnw.util.math.Mat4;

public class Kanone {

	private double x;
	private double y;
	
	private final double initAngle = 0;
	private double angle;	//in Radiant
	
	MyGLBase1 mygl;                                   // eigene OpenGL-Basisfunktionen
	
	public Kanone(MyGLBase1 mygl, double x, double y){
		this.mygl = mygl;
		this.angle = initAngle;
		this.x = x;
		this.y = y;
	}
	
	public void draw(GL3 gl, float...args) {
		float width = 0.8f, height = 2;
		
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
	
	public void rotateRelative(GL3 gl, Mat4 M){
		
		//Todo: x und y ersetzen (mit was?)
//		double alpha = Math.atan(y / x);	//Drehwinkel in Radiant
//		  alpha = alpha * 180 / Math.PI;		//Drehwinkel in Grad
//		  M = M.postMultiply( Mat4.rotate( (float) alpha, 0, 0, 1) ); //M = M x R (transl. i. objektlokalen Koord.sys.)
//		  
		//todo: von absoluter drehung zu lokaler
		  double alpha = angle * 180 / Math.PI;		//Drehwinkel in Grad
		  //M = M.postMultiply( Mat4.rotate( (float) alpha, 0, 0, 1) ); //M = M x R (transl. i. objektlokalen Koord.sys.)
		  
		  M = Mat4.rotate( (float) alpha, 0, 0, 1);
		  M = M.postMultiply( Mat4.translate(0, 0, 0) );
		  mygl.setM(gl, M);
	}
	
	public void reset(){
		angle = initAngle;
	}
	
	/******************* get / set ************************/

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public MyGLBase1 getMygl() {
		return mygl;
	}

	public void setMygl(MyGLBase1 mygl) {
		this.mygl = mygl;
	}

	
	
	
}
