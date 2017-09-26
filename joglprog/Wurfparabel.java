//  -------------   JOGL 2D-Programm  -------------------
import java.awt.*;
import java.awt.event.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.*;
import ch.fhnw.util.math.*;

public class Wurfparabel
       implements WindowListener, GLEventListener,
       KeyListener
{

    //  ---------  globale Daten  ---------------------------

    String windowTitle = "JOGL-Application";
    int windowWidth = 800;
    int windowHeight = 600;		//#Animation: window quadratisch machen, damit keine Verzerrung.
    String vShader = MyShaders.vShader1;                 // Vertex-Shader
    String fShader = MyShaders.fShader0;                 // Fragment-Shader
    int maxVerts = 2048;                                 // max. Anzahl Vertices im Vertex-Array
    GLCanvas canvas;                                     // OpenGL Window
    MyGLBase1 mygl;                                      // eigene OpenGL-Basisfunktionen

    //#Animation
    Mat4 M = Mat4.ID;		//Transf.Matrix
    
    //#Anwendung Projektionsmatrix, Vergrösserung Viewport
    Mat4 P = Mat4.ID;
    //Viewing volume
    double xleft = -10, xright = 10;
    double znear = -100, zfar = 100;	//Abstände zur Kamera
    
    
    //#Wurfparabel
    final double g = 9.81;	// Erdbeschl.
    final double m = 1;		// Masse
    final double v0y = 10;	// Anfangsgeschw. (m/s)
    final double v0x = 6;	// Anfangsgeschw. (m/s)
    double x0 = -8;
    double y0 = 0;
    double x = x0;
    double y = y0;
    double vy = v0y;
    double vx = v0x;
    double ay = -g;
    double ax = 0;
    double dt = 0.01;		// Zeitschritt (delta t)

    //#KeyEvents (awt)
    boolean stopped = false;

    //  ---------  Methoden  ----------------------------------

    public Wurfparabel()                                   // Konstruktor
    { createFrame();
    }


    void createFrame()                                    // Fenster erzeugen
    {  Frame f = new Frame(windowTitle);
       f.setSize(windowWidth, windowHeight);
       f.addWindowListener(this);
       GLProfile glp = GLProfile.get(GLProfile.GL3);
       GLCapabilities glCaps = new GLCapabilities(glp);
       canvas = new GLCanvas(glCaps);
       canvas.addGLEventListener(this);
       f.add(canvas);
       f.setVisible(true);
     //#KeyEvents (awt)
       f.addKeyListener(this);
    };

    /**
     * stellt einen Kreis als n-Eck dar (n relativ gross).
     * @param gl
     * @param r		- Radius
     * @param xm 	- x - Koordinate vom Kreismittelpunkt M
     * @param ym	- y - Koordinate vom Kreismittelpunkt M
     */
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

//    public void zeichneDreieck(GL3 gl, float x1, float y1,
//                             float x2, float y2, float x3, float y3)
//    {  mygl.rewindBuffer(gl);             // Vertex-Buffer zuruecksetzen
//       mygl.putVertex(x1,y1,0);           // Eckpunkte in VertexArray speichern
//       mygl.putVertex(x2,y2,0);
//       mygl.putVertex(x3,y3,0);
//       mygl.copyBuffer(gl);
//       mygl.drawArrays(gl,GL3.GL_TRIANGLES);
//    }


    //  ----------  OpenGL-Events   ---------------------------

    @Override
    public void init(GLAutoDrawable drawable)             //  Initialisierung
    {  GL3 gl = drawable.getGL().getGL3();
       System.out.println("OpenGl Version: " + gl.glGetString(gl.GL_VERSION));
       System.out.println("Shading Language: " + gl.glGetString(gl.GL_SHADING_LANGUAGE_VERSION));
       System.out.println();
       gl.glClearColor(0,0,1,1);                                 // Hintergrundfarbe
       int programId = MyShaders.initShaders(gl,vShader,fShader);  // Compile/Link Shader-Programme
       mygl = new MyGLBase1(gl, programId, maxVerts);            // OpenGL Basis-Funktionen
       
       //#Animation
       FPSAnimator anim = new FPSAnimator(canvas, 40, true);	//40 Frames / sec.
       anim.start();
    }

    //#Animation
    float drehwinkel = 0;

    @Override
    public void display(GLAutoDrawable drawable)
    { GL3 gl = drawable.getGL().getGL3();
      gl.glClear(GL3.GL_COLOR_BUFFER_BIT);             // Bildschirm loeschen
      mygl.setColor(1,1,0);                            // Farbe der Vertices
      
      //#Animation
//      M = Mat4.rotate(drehwinkel, 0, 0, 1);		//Grad, x, y, z
//      drehwinkel += 30;							//drehwinkel verändern
//      mygl.setM(gl, M);
      
      //#KeyEvents (awt)
      if(stopped) return;
      //#Wurfparabel
      	zeichneKreis(gl, 1, (float) x, (float) y, 20);
      	//#Wurfparabel
      	// Y-Achse
      	y = y + vy * dt;
      	vy = vy + ay * dt;
      	// X-Achse
      	x = x + vx * dt;
      	vx = vx + ax * dt;
      	
      	//reset
      	if (y < -8) {
      		y = y0;
      		x = x0;
      		vy = v0y;
      		vx = v0x;
      	}
      	
      	
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height)
    {  GL3 gl = drawable.getGL().getGL3();
       // Set the viewport to be the entire window
       gl.glViewport(0, 0, width, height);
       
       //#Anwendung Projektionsmatrix, Vergrösserung Viewport
	       //#Anzeigeverzerrung im rechteckigen Viewport beheben
	       double aspect = (double) height / width;	
	       double ybottom = aspect * xleft;
	       double ytop = aspect * xright;
	//       double ybottom = -10;			//would lead to an elipse instead a circle.
	//       double ytop = 10;
       P = Mat4.ortho((float)xleft, (float)xright, (float)ybottom, 
    		   (float)ytop, (float)znear, (float)zfar);
       mygl.setP(gl,  P);;
   }

    @Override
    public void dispose(GLAutoDrawable drawable)  { }                  // not needed

    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args)
    { new Wurfparabel();
    }

    //  ---------  Window-Events  --------------------
    
    //#KeyEvents (awt)
    public void keyPressed(KeyEvent e) {
    	//für Kontrolltasten
    }
    public void keyReleased(KeyEvent e) {
    	
    }
	public void keyTyped(KeyEvent e) {
    	//mit Shift; für charaktertasten
		char code = e.getKeyChar();
		if (code == 's' || code == 'S') {
			stopped = !stopped;
		}
    }

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

}