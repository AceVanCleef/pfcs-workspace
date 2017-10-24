//  -------------   JOGL 3D-Programm  -------------------
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;

import com.jogamp.opengl.*;
import ch.fhnw.util.math.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.FPSAnimator;

public class MyFirst3D
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
    

    Quader quad ;
    RotKoerper rotk;

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

    public MyFirst3D()                          // Konstruktor
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



    public void zeichneDreieck(GL3 gl, float x1, float y1, float z1,
                             float x2, float y2, float z2,
                             float x3, float y3, float z3)
    { Vec3 u = new Vec3(x2-x1, y2-y1, z2-z1);
      Vec3 v = new Vec3(x3-x1, y3-y1, z3-z1);
      Vec3 normale = u.cross(v);         // Normalenvektor
      mygl.setNormal(normale.x, normale.y, normale.z);
       mygl.rewindBuffer(gl);
       mygl.putVertex(x1,y1,z1);           // Eckpunkte in VertexArray speichern
       mygl.putVertex(x2,y2,z2);
       mygl.putVertex(x3,y3,z3);
       mygl.copyBuffer(gl);
       mygl.drawArrays(gl,GL3.GL_TRIANGLES);
    }

    public void zeichneKreis(GL3 gl, float r, float xm, float ym, int nPkte)
	{  	
    	double phi = 2*Math.PI / nPkte;
    	double x,y;
    	mygl.rewindBuffer(gl);             // Vertex-Buffer zuruecksetzen
		mygl.putVertex(xm,ym,0);           // Kreismittelpunkt in VertexArray speichern
		for (int i=0; i < nPkte + 1; i++) {
			x = xm + r * Math.cos(i * phi);
			y = ym + r * Math.sin(i * phi);
			mygl.putVertex((float)x, (float)y, 0);
		}
		
		mygl.copyBuffer(gl);
		mygl.drawArrays(gl,GL3.GL_TRIANGLE_FAN);
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
       gl.glClearColor(0,0,0,1);
       int programId = MyShaders.initShaders(gl,vShader,fShader);
       mygl = new MyGLBase1(gl, programId, maxVerts);
       quad = new Quader(mygl);
       rotk = new RotKoerper(mygl);
       
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
      
      
      //#Beleuchtung testen mit Quader
      mygl.setColor(0,0.75f,0);
      //quad.zeichne(gl, 3.0f, 2.0f, 1.5f, true);
      //#Beleuchtung testen mit Kugel
      rotk.zeichneKugel(gl,  1,  20,  20,  true);
      
      matrixStack.push(M);	//M sich merken.
      
      //#Beleuchtung -squelle zeichnen
      mygl.setColor(1,0,1);
      M = M.postMultiply(Mat4.translate(lightPos.x, lightPos.y, lightPos.z));
      mygl.setM(gl, M);
      zeichneKreis(gl, 0.2f, 0,0, 10);
      
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
    { new MyFirst3D();
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