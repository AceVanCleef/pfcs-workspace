package uebung2.wurfmitluftwiderstand;

import java.awt.*;
import java.awt.event.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.*;
import ch.fhnw.util.math.*;

public abstract class PhysObj {
	
	//OpenGL spezifisch
	private MyGLBase1 mygl;

	//Anfangsgeschwindigkeit
	private double v0x;
	private double v0y;
	
	//Anfangsposition
	private double x0;
	private double y0;
	
	//Aktuelle Position
	private double x;
	private double y;
	
	//Aktuelle Geschwindigkeit
	private double vx;
	private double vy;
	
	//Momentane Beschleunigung
	private double ax;
	private double ay;
	
	//Zeitschritt / Zeichnungsintervall
	private double dt;
	
	//Anzeigefenster
	private double ybottom;
	
	/******************* Konstruktor ************************/
	
	public PhysObj(MyGLBase1 mygl, 
					double v0x, double v0y,
					double x0,	double y0,
					double x,	double y,
					double vx,	double vy,
					double ax,	double ay,
					double dt,
					double ybottom) {
		this.mygl = mygl;
		this.v0x = v0x;
		this.v0y = v0y;
		this.x0 = x0;
		this.y0 = y0;
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.dt = dt;
		this.ybottom = ybottom;
	}

	
	/******************* Methoden ************************/

	abstract void draw(GL3 gl, float... args);
	
	public void updateCoords(){
		  x = x + vx*dt;
		  y = y + vy*dt;
		  vx = vx + ax*dt;
		  vy = vy + ay*dt;
		  if (y < ybottom)
		  {  x=x0;
		     y=y0;
		     vx=v0x;
		     vy=v0y;
		  }
	}
	
	public void rotateRelative(GL3 gl, Mat4 M){
		  M = Mat4.translate((float) x, (float) y, 0);	//Matrixtransformation: Vorwärtsbewegung des Speers
		  double alpha = Math.atan(vy / vx);	//Drehwinkel in Radiant
		  alpha = alpha * 180 / Math.PI;		//Drehwinkel in Grad
		  M = M.postMultiply( Mat4.rotate( (float) alpha, 0, 0, 1) ); //M = M x R (transl. i. objektlokalen Koord.sys.)
		  
		  mygl.setM(gl, M);
	}

	/******************* get / set ************************/

	public MyGLBase1 getMygl() {
		return mygl;
	}


	public void setMygl(MyGLBase1 mygl) {
		this.mygl = mygl;
	}


	public double getV0x() {
		return v0x;
	}


	public void setV0x(double v0x) {
		this.v0x = v0x;
	}


	public double getV0y() {
		return v0y;
	}


	public void setV0y(double v0y) {
		this.v0y = v0y;
	}


	public double getX0() {
		return x0;
	}


	public void setX0(double x0) {
		this.x0 = x0;
	}


	public double getY0() {
		return y0;
	}


	public void setY0(double y0) {
		this.y0 = y0;
	}


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


	public double getVx() {
		return vx;
	}


	public void setVx(double vx) {
		this.vx = vx;
	}


	public double getVy() {
		return vy;
	}


	public void setVy(double vy) {
		this.vy = vy;
	}


	public double getAx() {
		return ax;
	}


	public void setAx(double ax) {
		this.ax = ax;
	}


	public double getAy() {
		return ay;
	}


	public void setAy(double ay) {
		this.ay = ay;
	}


	public double getDt() {
		return dt;
	}


	public void setDt(double dt) {
		this.dt = dt;
	}


	public double getYbottom() {
		return ybottom;
	}


	public void setYbottom(double ybottom) {
		this.ybottom = ybottom;
	}
	

	
}
