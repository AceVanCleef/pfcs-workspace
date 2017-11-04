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
import java.util.LinkedList;

public class Uebung3_Boomerang extends GLApp {
	
	public static void main(String[] args) {
        (new Uebung3_Boomerang()).run(MyShaders.vShader2, MyShaders.fShader0, FPS, 2048);
    }
	
    private final static int FPS = 60;      // Frames per second
    private final static int N = 1;

    private float radiusCam = 2, elevation = 15, azimuth = 45;

    private final GLKeyListener keyListener;
    private MyVertexBuf vertexBuf;

    private float phi;
    private float alpha;
    private boolean move;
    private boolean rotate;
    
    private Boomerang boomerang;
    private final LinkedList<Boomerang> boomerangs;

    
    public Uebung3_Boomerang(){
    	 this.phi = 0;
         this.alpha = -20;
         this.move = false;
         this.rotate = false;
         this.boomerangs = new LinkedList<>();
         for (int i = 0; i < N; i++) {
        	 boomerangs.add(new Boomerang(360 / N * i, alpha, 360 / N * i));
         }         
         this.keyListener = new GLKeyListener() {

             @Override
             public void update() {
                 if (isSpaceDownOnce()) move = !move;
                 if (isRDownOnce()) rotate = !rotate;

                 if (isWDown()) alpha++;
                 if (isSDown()) alpha--;
                 if (isADown()) phi--;
                 if (isDDown()) phi++;

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

        for (Boomerang t : boomerangs) {
            t.setAlpha(alpha);
            t.draw(gl, vertexBuf, transf, move, rotate);
        }

        keyListener.update();
    }

}
