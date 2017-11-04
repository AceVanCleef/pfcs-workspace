package uebung3_boomerang;

import ch.fhnw.pfcs.opengl.MyTransf;
import ch.fhnw.pfcs.opengl.MyVertexBuf;
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
    
    Quader component1;
    Quader component2;
	
	public Boomerang(float phi, float alpha) {
        this.phi = phi;
        this.alpha = alpha;
        this.rotation = 0;
        
        component1 = new Quader();
        component2 = new Quader();
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
	        drawBoomerang(gl, vertexBuf);

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
	            if (rotation >= 360) rotation = rotation % 360;
	        }

	    }

	   
	   //todo: replace with own boomerang design/lookt
	   private void drawBoomerang(GL3 gl, MyVertexBuf vertexBuf) {
           vertexBuf.rewindBuffer(gl);

           drawSurface(vertexBuf);
           
		   int nVertices = 7;
		   vertexBuf.copyBuffer(gl, nVertices);
           gl.glDrawArrays(GL3.GL_LINE_LOOP, 0, nVertices);
	   }
	   
	   private void drawSurface(MyVertexBuf vertexBuf) {
		   float x = 0.25f, y = 0.3f, z = 0;
		   float deltaY = 0.05f;
		   float offsetY = y * 0.5f;
		   vertexBuf.putVertex(-x, 0, z);

		   vertexBuf.putVertex(-x, 0 + deltaY - offsetY, z);
		   vertexBuf.putVertex(0, y + deltaY - offsetY, z);
		   vertexBuf.putVertex(x, 0 + deltaY - offsetY, z);
		   
		   vertexBuf.putVertex(x, 0 - offsetY, z);
		   vertexBuf.putVertex(0, y - offsetY, z);
		   vertexBuf.putVertex(-x, 0 - offsetY, z);
		   
	   }
	   
	    private void drawThrowie(GL3 gl, MyVertexBuf vertexBuf) {
	        int n2 = N / 2;
	        int nVertices = n2 + 1;
	        int[][] colors = {{1, 0, 0}, {0, 1, 0}};
	        for (int j = 0; j < 2; j++) {
	            vertexBuf.rewindBuffer(gl);
	            vertexBuf.setColor(colors[j][0], colors[j][1], colors[j][2]);
	            double phi = 2 * Math.PI / N;
	            for (int i = j * n2; i <= (j + 1) * n2; i++) {
	                putTriangleFanVertex(vertexBuf, i, phi);
	            }
	            putTriangleFanVertex(vertexBuf, j * n2, phi);
	            vertexBuf.copyBuffer(gl, nVertices);
	            gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, nVertices);
	        }
	    }	
	private void putTriangleFanVertex(MyVertexBuf vertexBuf, int i, double phi) {
        vertexBuf.putVertex((float) (RADIUS * Math.cos(i * phi)),
                (float) (RADIUS * Math.sin(i * phi)), 0);
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
	
	
}
