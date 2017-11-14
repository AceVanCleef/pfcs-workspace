package uebung3_boomerang;
import ch.fhnw.pfcs.opengl.MyShaders;
import ch.fhnw.pfcs.opengl.MyVertexBuf;
import ch.fhnw.pfcs.std.GLApp;
import ch.fhnw.pfcs.std.GLKeyListener;
import ch.fhnw.pfcs.std.GLWindowListener;
import ch.fhnw.pfcs.std.viewing.PerspectiveProjection;
import ch.fhnw.pfcs.std.viewing.ViewingVolume;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import java.awt.*;

public class Uebung3_Boomerang extends GLApp {
	
	public static void main(String[] args) {
        (new Uebung3_Boomerang()).run(MyShaders.vShader2, MyShaders.fShader0, FPS, 2048);
    }
	
    private final static int FPS = 60;      // Frames per second

    private float radiusCam = 2, elevation = 15, azimuth = 45;

    private final GLKeyListener keyListener;
    private MyVertexBuf vertexBuf;

    private float phi;
    private float alpha;
    private boolean move;
    private boolean rotate;
    
    private boolean isFlying;
    
    private Boomerang boomerang;

    private Thrower throwPos0;	//Person who throws the boomerang
    
    public Uebung3_Boomerang(){
    	 this.phi = 0;
         this.alpha = -20;
         this.move = false;
         this.rotate = false;
         this.isFlying = false;
         
         boomerang = new Boomerang(0, alpha, 0);
      
         throwPos0 = new Thrower(0, alpha);
         
         this.keyListener = new GLKeyListener() {

             @Override
             public void update() {
                 if (isSpaceDownOnce() && !isFlying) {
                	 move = true;
                	 rotate = true;
                	 isFlying = true;
                 }
                 

                 if (isUpDown()) elevation++;
                 if (isDownDown()) elevation--;
                 if (isLeftDown()) azimuth--;
                 if (isRightDown()) azimuth++;
             }
         };
    }

    @Override
    protected ViewingVolume createViewing() {
        return new PerspectiveProjection(-0.2f, 0.2f, 0.5f, 1000);
    }

    @Override
    protected void initFrame(Frame frame) {
        frame.setName("Roland Schmid - Wurfparabel mit Luftwiderstand");
        frame.setSize(1600, 1200);
        frame.addWindowListener(new GLWindowListener());
    }

    @Override
    protected void initGLCanvas(GLCanvas glCanvas) {
        glCanvas.addKeyListener(keyListener);
    }

    @Override
    protected void initGL(GL3 gl, int programId) {
        vertexBuf = new MyVertexBuf(gl, programId, 2048);
    }

    @Override
    protected void initAnimator(FPSAnimator animator) {
        animator.start();
    }


    @Override
    protected void display(GL3 gl) {
        transf.loadIdentity(gl);
        transf.setCameraSystem(gl, radiusCam, azimuth, elevation);

        transf.translate(gl, 0, -0.2f, 0);

        vertexBuf.drawAxisTricolor(gl, 100, 100, 100);

        transf.translate(gl, 0, 0.2f, 0);
        transf.rotate(gl, phi, 0, 1, 0);

        boomerang.draw(gl, vertexBuf, transf, move, rotate);

        //starting point
        throwPos0.draw(gl, vertexBuf, transf, rotate);
        
        //draw plane
        transf.rotate(gl, 90, 1, 0, 0);
        transf.translate(gl, 0, 0, 0.25f);
        vertexBuf.setColor(0.33f, 0.33f, 0.33f);
        drawSquare(gl, vertexBuf, 0,0,0, 5.0f, 5.0f);
        transf.translate(gl, 0, 0, -0.25f);
        transf.rotate(gl, -90, 1, 0, 0);

        keyListener.update();
        
        if(boomerang.traveledFullCircle()) {
        	move = false;
       	 	rotate = false;
       	 	isFlying = false;
        }
    }

    
    private void drawSquare(GL3 gl, MyVertexBuf vb, 
			   float x0, float y0, float z0,
			   float width, float height)      {  
	   		vb.rewindBuffer(gl);
		   
		   	// Mittelpunkt (of square) M = (x0,y0,z0)
		   	float offsetX = width / 2, offsetY = height / 2;
		    //vb.setNormal(n.x, n.y, n.z);
		   	// Dreieck 1
			vb.putVertex(x0 - offsetX, y0 - offsetY, z0);          	//P1
			vb.putVertex(x0 - offsetX, y0 + offsetY, z0);			//P2
			vb.putVertex(x0 + offsetX, y0 + offsetY, z0);			//P3
			// Dreieck 2
			vb.putVertex(x0 + offsetX, y0 + offsetY, z0);			//P3
			vb.putVertex(x0 + offsetX, y0 - offsetY, z0);			//P4
			vb.putVertex(x0 - offsetX, y0 - offsetY, z0);			//P1
			
	   		int nVertices = 6;
	   		vb.copyBuffer(gl, nVertices);
	   		gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, nVertices);
	   }
    
	  
}
