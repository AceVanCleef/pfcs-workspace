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


	@Override
	protected ViewingVolume createViewing() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void initFrame(Frame frame) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initGLCanvas(GLCanvas glCanvas) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initGL(GL3 gl, int programId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initAnimator(FPSAnimator animator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void display(GL3 gl) {
		// TODO Auto-generated method stub
		
	}

}
