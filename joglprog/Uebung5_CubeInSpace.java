//  -------------   JOGL 3D-Programm  -------------------
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;

import com.jogamp.opengl.*;

import ch.fhnw.pfcs.opengl.QuaderV3;
import ch.fhnw.util.math.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.FPSAnimator;

public class Uebung5_CubeInSpace
       implements WindowListener, GLEventListener, KeyListener
{

    //  ---------  globale Daten  ---------------------------

    String windowTitle = "JOGL-Application";
    int windowWidth = 800;
    int windowHeight = 600;
    //#Beleuchtung: benötigt vertex shader 'vShader2'
    String vShader = MyShaders.vShader2;                 // Vertex-Shader mit Transformations-Matrizen
    String fShader = MyShaders.fShader0;                 // Fragment-Shader
    int maxVerts = 2048;                                 // max. Anzahl Vertices im Vertex-Array
    GLCanvas canvas;                                     // OpenGL Window
    MyGLBase1 mygl;                                      // OpenGL Basis-Funktionen

    //#Transformationsmatrix - Management (wegen immutable M)
    Stack<Mat4> matrixStack = new Stack<>();
    
    //#Rotationsquader
    QuaderV3 quad ;	//#Rotationsquader
    double x = -4f, dx = 0.01;	//Position bzw Bewegungsrichtung #Rotationsquader
    //Rotation #Rotationsquader
    Quaternion qStart = Quaternion.fromAxis(new Vec3(1,0,0), 10);
    Quaternion qEnd = Quaternion.fromAxis(new Vec3(0,1,0), 30);
    double t=0, dt = 0.1;	//SLERP - Parameter
    
    GyroDynamics gyro;

    Mat4 M;                                            // ModelView-Matrix
    Mat4 P;                                            // Projektions-Matrix

    //#Kamerasystem
    float elevation = 1f;
    float azimut = 1f;
    

    //  -------- Viewing-Volume  ---------------
    float left=-4f, right=4f;
    float bottom, top;
    float near=-10, far=1000;

    // LookAt-Parameter fuer Kamera-System
    Vec3 A = new Vec3(2,1,4);                   // Kamera-Pos. (Auge)
    Vec3 B = new Vec3(0,0,0);                   // Zielpunkt
    Vec3 up = new Vec3(0,1,0);                  // up-Richtung

    //  ---------  Methoden  ----------------------------------

    public Uebung5_CubeInSpace()                          // Konstruktor
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
       f.setVisible(true);
    };



    
    //  ----------  OpenGL-Events   ---------------------------

    @Override
    public void init(GLAutoDrawable drawable)             //  Initialisierung
    {
       GL3 gl = drawable.getGL().getGL3();
       System.out.println("OpenGl Version: " + gl.glGetString(gl.GL_VERSION));
       System.out.println("Shading Language: " + gl.glGetString(gl.GL_SHADING_LANGUAGE_VERSION));
       System.out.println();
       gl.glEnable(GL3.GL_DEPTH_TEST);
       gl.glClearColor(0,0,0,1);
       int programId = MyShaders.initShaders(gl,vShader,fShader);
       mygl = new MyGLBase1(gl, programId, maxVerts);
       quad = new QuaderV3(mygl, 2.0f, 3.0f, 4.0f, 5.0f);
       
       gyro = new GyroDynamics(quad);
       gyro.initState(1.0f, 1.0f, 2.0f, 120, 5.0f, 1.0f, 1.0f);
       
       //#Kamerasystem
       FPSAnimator anim = new FPSAnimator(canvas, 200, true);
       anim.start();
    }


    @Override
    public void display(GLAutoDrawable drawable)
    { GL3 gl = drawable.getGL().getGL3();
      gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
      
      //#Kamerasystem
      Mat4 R1 = Mat4.rotate(-elevation,  1,0,0);
      Mat4 R2 = Mat4.rotate(-azimut,  0,1,0);
      Mat4 R = R1.preMultiply(R2);
      M = Mat4.lookAt(R.transform(A),  B,  R.transform(up));
      mygl.setM(gl, M);
      
      
      //mygl.setM(gl,Mat4.lookAt(A,B,up));                  // Blickrichtung A --> B
      mygl.setColor(1,1,1);
      mygl.setShadingLevel(gl,  0);	//#Beleuchtung ausschalten.
      mygl.drawAxis(gl,2,2,2);                            // Koordinatenachsen
      
      
      //#Beleuchtung
      Vec3 lightPos = new Vec3(0,1,4);
      mygl.setLightPosition(gl, lightPos.x, lightPos.y, lightPos.z);	//im absoluten Raumsystem -> wird autom. ins Kamerasystem transformiert.
      mygl.setShadingParam(gl, 0.5f, 0.8f);	//gl, ambient light, diffuse light (direkt beleuchtet)
      mygl.setShadingLevel(gl, 1);
      
    //GyroDynamics
    	double[] state = gyro.getState();
    	
      //#Rotationsquader
      mygl.setColor(0,0.75f,0);
      M = M.postMultiply(Mat4.translate((float) state[4], (float) state[5], (float) state[6]));
      mygl.setM(gl, M);      
      
	      
	      gyro.move(t);
	      t += dt;
	      M = M.postMultiply(Mat4.rotate((float) state[1],(float) state[4],(float) state[5],(float) state[6]));
	      mygl.setM(gl, M);
      quad.draw(gl, vertexBuf);;
     // x += dx;
      
     // gyro.setState(w1, w2, w3, phi, dx, y, z);
      
      matrixStack.push(M);	//M sich merken.
      
      //#Beleuchtung -squelle zeichnen
      mygl.setColor(1,0,1);
      M = M.postMultiply(Mat4.translate(lightPos.x, lightPos.y, lightPos.z));
      mygl.setM(gl, M);
      
      M = matrixStack.pop();	//M in "Originalzustand" wieder einsetzen.
      mygl.setM(gl, M);
      
      

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
    { new Uebung5_CubeInSpace();
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
      { case KeyEvent.VK_UP : elevation++;	//#Kamerasystem bewegen
                              break;
        case KeyEvent.VK_DOWN : elevation--;
                              break;
        case KeyEvent.VK_LEFT : azimut--;	//#Kamerasystem bewegen
        					  break;
        case KeyEvent.VK_RIGHT : azimut++;
        					  break;
      }
    }

    public void keyReleased(KeyEvent e) { }

    public void keyTyped(KeyEvent e)
    { char code = e.getKeyChar();
      if (code == 's')
        //stopped = !stopped;
    	  System.out.println("hello");
    }

}