package uebung3_boomerang;

import ch.fhnw.pfcs.opengl.MyTransf;
import ch.fhnw.pfcs.opengl.MyVertexBuf;
import ch.fhnw.util.math.Vec3;

import com.jogamp.opengl.GL3;

public class Boomerang {

    private final static int N = 20 * 3;
    private final static float RADIUS = 0.01f;
    private final static float DX = 0.5f;
    private final static float DROT = 360 / 10;
    
    private float phi;
    private float alpha;
    private float rotation;
    
    private boolean move;
    private boolean rotate;
    
    private int k = 0;	//to time isFullcircle
    

	
	public Boomerang(float phi, float alpha) {
        this.phi = phi;
        this.alpha = alpha;
        this.rotation = 0;
    }
	
	public Boomerang(float phi, float alpha, float rotation) {
        this.phi = phi;
        this.alpha = alpha;
        this.rotation = rotation;
    }
	
	   public void draw(GL3 gl, MyVertexBuf vertexBuf, MyTransf transf, boolean move, boolean rotate) {
	        // Translate
	        transf.rotate(gl, alpha, 0, 0, 1);
	        transf.rotate(gl, phi, 0, 1, 0);
	        transf.translate(gl, DX, 0, 0);
	        transf.rotate(gl, 90, 0, 1, 0);
	        transf.rotate(gl, rotation, 0, 0, 1);

	        // Draw
	        drawShuriken(gl, vertexBuf, transf);

	        // Undo
	        transf.rotate(gl, -rotation, 0, 0, 1);
	        transf.rotate(gl, -90, 0, 1, 0);
	        transf.translate(gl, -DX, 0, 0);
	        transf.rotate(gl, -phi, 0, 1, 0);
	        transf.rotate(gl, -alpha, 0, 0, 1);

	        if (move) {
	            phi++;
	            if (phi >= 360) phi = 0;
	        }
	        if (rotate) {
	            rotation += DROT;
	            //System.out.println(rotation % 360);
	            if (rotation >= 360){
	            	rotation = rotation % 360;
	            	++k;
	            }
	        }

	    }
  
	   

	   
	   public void drawShuriken(GL3 gl, MyVertexBuf vertexBuf, MyTransf transf) {
		   		float width = 0.15f, height = 0.15f;
		   		vertexBuf.setColor(0,1,0);
		   		drawSquare(gl, vertexBuf, 0,0,0, width, height);
		   
		   		vertexBuf.setColor(0,0,1);
		   		drawSquare(gl, vertexBuf, 0,0,0.001f, width, height, 45);
	   }
	   
	   public void drawSquare(GL3 gl, MyVertexBuf vb, 
			   float x0, float y0, float z0,
			   float width, float height, float phi)      {  
		   
		   	vb.rewindBuffer(gl);
	   				   
		   	float offsetX = width / 2, offsetY = height / 2;
		   
			Vec3 A = rotateVectorXY(x0 - offsetX, y0 - offsetY, z0, phi);
			Vec3 B = rotateVectorXY(x0 - offsetX, y0 + offsetY, z0, phi);
			Vec3 C = rotateVectorXY(x0 + offsetX, y0 + offsetY, z0, phi);
			Vec3 D = rotateVectorXY(x0 + offsetX, y0 - offsetY, z0, phi);
			   
			vb.putVertex(A.x, A.y, A.z);          // Dreieck 1
		    vb.putVertex(B.x, B.y, B.z);
		    vb.putVertex(C.x, C.y, C.z);
		    vb.putVertex(C.x, C.y, C.z);          // Dreieck 2
		    vb.putVertex(D.x, D.y, D.z);
		    vb.putVertex(A.x, A.y, A.z);
	       
	   		int nVertices = 6;
			vb.copyBuffer(gl, nVertices);
		   	gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, nVertices);
	   }
	   
	   private static Vec3 rotateVectorXY(float x, float y, float z ,float phi) {
		   return new Vec3(x * Math.cos(phi) - y * Math.sin(phi),
				   x * Math.sin(phi) + y * Math.cos(phi), z);
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
	   

//	   public void launch(){
//		   rotation = 0;
//	   }
	
    public boolean traveledFullCircle(){
    	if (k >= 36){
    		k = 0;
    		return true;
    	}
    	return false;
    }
    
    
	
}
