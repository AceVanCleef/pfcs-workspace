package ch.fhnw.pfcs.std;

import ch.fhnw.pfcs.opengl.MyGLSetup;
import ch.fhnw.pfcs.opengl.MyLighting;
import ch.fhnw.pfcs.opengl.MyTransf;
import ch.fhnw.pfcs.opengl.MyVertexBuf;
import ch.fhnw.pfcs.std.viewing.ViewingVolume;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import java.awt.*;

/**
 * @author Claudio Seitz
 * @date 19.10.16
 */
public abstract class GLApp {

    protected ViewingVolume volume;
    protected MyVertexBuf vertexBuf;
    protected MyTransf transf;
    protected MyLighting lighting;
    protected FPSAnimator animator;

    public void run(String vertexShader, String fragShader, int fps, int maxVertices) {
        volume = GLApp.this.createViewing();
        Frame frame = new Frame();
        initFrame(frame);
        GLProfile glp = GLProfile.get(GLProfile.GL3);
        GLCapabilities glCaps = new GLCapabilities(glp);
        GLCanvas glCanvas = new GLCanvas(glCaps);
        initGLCanvas(glCanvas);
        glCanvas.addGLEventListener(new GLEventListener() {
            @Override
            public void init(GLAutoDrawable glAutoDrawable) {
                GL3 gl = glAutoDrawable.getGL().getGL3();
                System.out.println("OpenGl Version: " + gl.glGetString(gl.GL_VERSION));
                System.out.println("Shading Language: " + gl.glGetString(gl.GL_SHADING_LANGUAGE_VERSION));
                System.out.println();
                gl.glEnable(GL3.GL_DEPTH_TEST);
                int programId = MyGLSetup.setupProgram(gl, vertexShader, fragShader);
                vertexBuf = new MyVertexBuf(gl, programId, maxVertices);
                transf = new MyTransf(gl, programId);
                lighting = new MyLighting(gl, programId);
                animator = new FPSAnimator(glAutoDrawable, fps, true);
                initGL(gl, programId);
                initAnimator(animator);
            }

            @Override
            public void dispose(GLAutoDrawable glAutoDrawable) {
            }

            @Override
            public void display(GLAutoDrawable glAutoDrawable) {
                GL3 gl = glAutoDrawable.getGL().getGL3();
                gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
                GLApp.this.display(gl);
            }

            @Override
            public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
                GL3 gl = glAutoDrawable.getGL().getGL3();
                gl.glViewport(0, 0, width, height);
                volume.setAspect(width, height);
                volume.setProjection(gl, transf);
            }
        });
        frame.add(glCanvas);
        frame.setVisible(true);
        glCanvas.requestFocusInWindow();
    }

    protected abstract ViewingVolume createViewing();

    protected abstract void initFrame(Frame frame);

    protected abstract void initGLCanvas(GLCanvas glCanvas);

    protected abstract void initGL(GL3 gl, int programId);

    protected abstract void initAnimator(FPSAnimator animator);

    protected abstract void display(GL3 gl);

    protected double dt() {
        return 1. / animator.getFPS();
    }
}
