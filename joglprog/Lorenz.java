//  -------------   JOGL 3D-Programm  -------------------

// *********** Klotoide Kurve ***************/

import java.awt.*;
import java.awt.event.*;
import com.jogamp.opengl.*;
import ch.fhnw.util.math.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.FPSAnimator;
import java.util.*;
public class Lorenz
       implements WindowListener, GLEventListener, KeyListener
{

    //  ---------  globale Daten  ---------------------------

    String windowTitle = "JOGL-Application";
    int windowWidth = 800;
    int windowHeight = 600;
    String vShader = MyShaders.vShader1;                 // Vertex-Shader mit Transformations-Matrizen
    String fShader = MyShaders.fShader0;                 // Fragment-Shader
    int maxVerts = 10048;                                 // max. Anzahl Vertices im Vertex-Array
    GLCanvas canvas;                                     // OpenGL Window
    MyGLBase1 mygl;                                      // OpenGL Basis-Funktionen

    Stack<Mat4> matrixStack = new Stack<>();
    float elevation=10;
    float azimut=30;

    Mat4 M;                                            // ModelView-Matrix
    Mat4 P;                                            // Projektions-Matrix

    LorenzDynamics lorenzdynamics;

    //  -------- Viewing-Volume  ---------------
    float left=-60, right=60;
    float bottom, top;
    float near=-10, far=1000;

    // LookAt-Parameter fuer Kamera-System
    Vec3 A = new Vec3(0,0,100);                   // Kamera-Pos. (Auge)
    Vec3 B = new Vec3(0,0,0);                   // Zielpunkt
    Vec3 up = new Vec3(0,1,0);                  // up-Richtung

    
    class LorenzDynamics extends Dynamics {
    	
    	public double[] f(double[]x) {
    		double x1 = x[0], x2 = x[1], x3 = x[2];
    		double[] y = { 10*x2 - 10* x1,
    				28*x1 - x2 -x1*x3, 
    				x1*x2 - 8*x3/3 };		//f1(), f2() hier als argumente
    		
    		return y;
    	}
    	
    	//berechnet Integralkurve
    	/**
    	 * calculates the integral curve.
    	 * 
    	 * Note: be sure to...
    	 * - choose a small dt
    	 * - don't set xStart, yStart, zStart on 0 because 0 means velocity = 0.
    	 * - int maxVerts for VertexBuffer had to be increased.
    	 * 
    	 * You can calculate:
    	 * - trajectory curve of planets
    	 * - ...
    	 * 
    	 * Not ideal for simulating particles in a force field!
    	 * 
    	 * @param mygl
    	 * @param gl
    	 * @param xStart
    	 * @param yStart
    	 * @param zStart
    	 * @param dt
    	 * @param nSchritte must be > 50.
    	 */
		public void zeichneBahn(MyGLBase1 mygl, GL3 gl, 
				double xStart, double yStart, double zStart,
				double dt, double nSchritte) {
			
			double[] x= {xStart, yStart, zStart};
			mygl.rewindBuffer(gl); 
			for (int i=0; i < 50; i++)
			{ 
				x = rungeKutta(x, dt);
			}
			for (int i=0; i < nSchritte; i++)
			{ 
				x = rungeKutta(x, dt);
				mygl.putVertex((float)x[0],(float)x[1],(float)x[2]);

			}
			mygl.copyBuffer(gl);
			mygl.drawArrays(gl,GL3.GL_LINE_STRIP);
		}
    }
    


    
    //  ---------  Methoden  ----------------------------------

    public Lorenz()                          // Konstruktor
    { createFrame();
    }



	void createFrame()                         // Fenster erzeugen
    {
       Frame f = new Frame(windowTitle);
       f.setSize(windowWidth, windowHeight);
       f.addWindowListener(this);
       GLProfile glp = GLProfile.get(GLProfile.GL3);
       GLCapabilities glCaps = new GLCapabilities(glp);
       canvas = new GLCanvas(glCaps);
       canvas.addGLEventListener(this);
       f.add(canvas);
       f.setVisible(true);
       f.addKeyListener(this);
       canvas.addKeyListener(this);
    };



    //  -------  Klothoide (Cornu'sche Spirale) ------------
    public void zeichneBahn(GL3 gl, double xStart, double yStart, double zStart,
                                    double phi, double ds, double kruemmung,
                                    double dKruemmung, int nPunkte)
   {  double x=xStart, y=yStart, z=zStart;
      mygl.rewindBuffer(gl);
      mygl.putVertex((float)x,(float)y,(float)z);
      for (int i=1; i < nPunkte; i++)
      {  x += Math.cos(phi) * ds;
         y += Math.sin(phi) * ds;
         mygl.putVertex((float)x,(float)y,(float)z);
         phi += kruemmung * ds;
         kruemmung += dKruemmung * ds;
      }
      mygl.copyBuffer(gl);
      mygl.drawArrays(gl,GL3.GL_LINE_STRIP);
  }


    //  ----------  OpenGL-Events   ---------------------------

    @Override
    public void init(GLAutoDrawable drawable)             //  Initialisierung
    {
       GL3 gl = drawable.getGL().getGL3();
       System.out.println("OpenGl Version: " + gl.glGetString(gl.GL_VERSION));
       System.out.println("Shading Language: " + gl.glGetString(gl.GL_SHADING_LANGUAGE_VERSION));
       System.out.println();
       gl.glEnable(GL3.GL_DEPTH_TEST);
       gl.glClearColor(0.2f,0.2f,1.0f,1);
       int programId = MyShaders.initShaders(gl,vShader,fShader);
       mygl = new MyGLBase1(gl, programId, maxVerts);
       FPSAnimator anim = new FPSAnimator(canvas, 200, true);  // Animations-Thread, 200 Frames/sek
       anim.start();
       lorenzdynamics = new LorenzDynamics();
   }


    @Override
    public void display(GLAutoDrawable drawable)
    { GL3 gl = drawable.getGL().getGL3();
      gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
      M = Mat4.ID;
      mygl.setM(gl, M);
      Mat4 R1 = Mat4.rotate(-elevation, 1,0,0);
      Mat4 R2 = Mat4.rotate(azimut,0,1,0);
      Mat4 R = R1.preMultiply(R2);
      M = Mat4.lookAt(R.transform(A),B,R.transform(up));
      mygl.setM(gl,M);
      mygl.setColor(1,1,1);
      mygl.drawAxis(gl,100,100,100);                            // Koordinatenachsen
      mygl.setColor(0,1,1);
      //zeichneBahn(gl,-20,-20,0,0,1,0,0.0005f,500);              // Spirale
      
      //LorenzDynamics
      lorenzdynamics.zeichneBahn(mygl, gl, 10, 10, 10, 0.01f, 5000);
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height)
    {  GL3 gl = drawable.getGL().getGL3();
       // Set the viewport to be the entire window
       gl.glViewport(0, 0, width, height);
       float aspect = (float)height / width;
       bottom = aspect * left;
       top = aspect * right;
       mygl.setP(gl,Mat4.ortho(left,right,bottom,top,near,far));
    }

    @Override
    public void dispose(GLAutoDrawable drawable)  { }      // not needed


    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args)
    { new Lorenz();
    }


    //  ---------  Window-Events  --------------------

    public void windowClosing(WindowEvent e)
    {   System.out.println("closing window");
        System.exit(0);
    }
    public void windowActivated(WindowEvent e) {  }
    public void windowClosed(WindowEvent e) {  }
    public void windowDeactivated(WindowEvent e) {  }
    public void windowDeiconified(WindowEvent e) {  }
    public void windowIconified(WindowEvent e) {  }
    public void windowOpened(WindowEvent e) {  }

    public void keyPressed(KeyEvent e)
    { int key = e.getKeyCode();
      switch (key)
      { case KeyEvent.VK_UP : elevation++;
                              break;
        case KeyEvent.VK_DOWN : elevation--;
                              break;
        case KeyEvent.VK_LEFT : azimut--;
                              break;
        case KeyEvent.VK_RIGHT: azimut++
        ;
                              break;
      }
    }

    public void keyReleased(KeyEvent e) { }

    public void keyTyped(KeyEvent e)
    {
    }



}