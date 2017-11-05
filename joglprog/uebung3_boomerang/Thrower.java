package uebung3_boomerang;

import com.jogamp.opengl.GL3;

import ch.fhnw.pfcs.opengl.MyTransf;
import ch.fhnw.pfcs.opengl.MyVertexBuf;
import ch.fhnw.util.math.Vec3;

public class Thrower {

    private float phi;
    private float alpha;
    
	public Thrower(float phi, float alpha){
		this.phi = phi;
        this.alpha = alpha;
	}
	
	public void draw(GL3 gl, MyVertexBuf vertexBuf, MyTransf transf, boolean rotate) {
        vertexBuf.setColor(1, 1, 1);
        float DX = 0.5f;
        transf.rotate(gl, alpha, 0, 0, 1);
        transf.rotate(gl, phi, 0, 1, 0);
        transf.translate(gl, DX, 0, 0);

        drawPseudoCube(gl, vertexBuf, transf);
		
        transf.translate(gl, -DX, 0, 0);
        transf.rotate(gl, -phi, 0, 1, 0);
        transf.rotate(gl, -alpha, 0, 0, 1);

//        if (rotate) {
//            rotation += DROT;
//            if (rotation >= 360) rotation = rotation % 360;
//        }

    }
	
	private void drawPseudoCube(GL3 gl, MyVertexBuf vertexBuf, MyTransf transf){
        float size = 0.1f;
		float delta = size/2;
		//oben
		transf.translate(gl, 0,0,delta);
		drawSquare(gl, vertexBuf, 0,0,0, size, size, 0);
		transf.translate(gl, 0,0,-delta);
		//unten
		transf.translate(gl, 0,0,-delta);
		drawSquare(gl, vertexBuf, 0,0,0, size, size, 0);	//oben
		transf.translate(gl, 0,0,delta);
		
		//rechts
		transf.translate(gl, delta,0,0);
		transf.rotate(gl, 90, 0, 1, 0);
		drawSquare(gl, vertexBuf, 0,0,0, size, size, 0);
		transf.rotate(gl, -90, 0, 1,0);
		transf.translate(gl, -delta,0,0);
		//unten
		transf.translate(gl, -delta,0,0);
		transf.rotate(gl, 90, 0, 1, 0);
		drawSquare(gl, vertexBuf, 0,0,0, size, size, 0);	//oben
		transf.rotate(gl, -90, 0, 1, 0);
		transf.translate(gl, delta,0,0);
	}
	
	 private void drawSquare(GL3 gl, MyVertexBuf vb, 
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
}
