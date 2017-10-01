//  -------------   JOGL 2D-Programm  -------------------
import java.awt.*;
import java.awt.event.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.*;
import ch.fhnw.util.math.*;

public class Lissajous
       implements WindowListener, GLEventListener
{

    //  ---------  globale Daten  ---------------------------

    String windowTitle = "JOGL-Application";
    int windowWidth = 800;
    int windowHeight = 800;		//#Animation: window quadratisch machen, damit keine Verzerrung.
    String vShader = MyShaders.vShader1;                 // Vertex-Shader
    String fShader = MyShaders.fShader0;                 // Fragment-Shader
    int maxVerts = 2048;                                 // max. Anzahl Vertices im Vertex-Array
    GLCanvas canvas;                                     // OpenGL Window
    MyGLBase1 mygl;                                      // eigene OpenGL-Basisfunktionen

    //#Animation
    Mat4 M = Mat4.ID;		//Transf.Matrix
    
    //#Lissajous
    //Formel:	x = A * sin(at + phi), y = B * sin (bt) @wikipedia
    double a = 1;		//Kreisfrequenz
    double b = 2;		//Kreisfrequenz
    double _A = 1;		//Amplitude
    double _B = _A;		//Amplitude
    double phi = 0;		//Rotationswinkel in Grad (3D - Effekt)
    					//0° => direkt von oben betrachtend dargestellt.

    //  ---------  Methoden  ----------------------------------

    public Lissajous()                                   // Konstruktor
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
    };



    
    public void zeichneLissajous(GL3 gl, double a1, double w1,
    		double a2, double w2)
	{  	mygl.rewindBuffer(gl);             // Vertex-Buffer zuruecksetzen

	//mygl.putVertex(x1,y1,0);           // Eckpunkte in VertexArray speichern

		//#Lissajous
		int n = 100;	//Punkte
		double dt = (2 * Math.PI) / (w1 * n);
		for (int i = 0; i < n + 1; ++i){
			dt = (2 * Math.PI) / (w1 * i);
			float x = (float) calcLissX(a1, w1, dt);
			float y = (float) calcLissY(a2, w2, dt, phi);
			mygl.putVertex(x, y, 0f);
			
		}
		//phi += 0.2;
		
		mygl.copyBuffer(gl);
		mygl.drawArrays(gl,GL3.GL_LINE_LOOP);
	}
    
    //#Lissajous
    private double calcLissX(double a, double w, double t){
    	return a * Math.cos(w * t);
    }

    //#Lissajous
    private double calcLissY(double a, double w, double t, double phi){
    	return a * Math.sin(w * t - phi);
    }

    //  ----------  OpenGL-Events   ---------------------------

    @Override
    public void init(GLAutoDrawable drawable)             //  Initialisierung
    {  GL3 gl = drawable.getGL().getGL3();
       System.out.println("OpenGl Version: " + gl.glGetString(gl.GL_VERSION));
       System.out.println("Shading Language: " + gl.glGetString(gl.GL_SHADING_LANGUAGE_VERSION));
       System.out.println();
       gl.glClearColor(0,0,0,1);                                 // Hintergrundfarbe
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
      mygl.setColor(1,0,0);                            // Farbe der Vertices
      float a = 0.2f;
      
      //#Animation
//      M = Mat4.rotate(drehwinkel, 0, 0, 1);	//Grad, x, y, z
//      drehwinkel += 0;							//drehwinkel verändern
//      mygl.setM(gl, M);
      
      //#Lissajous
      zeichneLissajous(gl, _A, a, _B, b);
      //(GL3 gl, float a1, float w1, float a2, float w2)
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height)
    {  GL3 gl = drawable.getGL().getGL3();
       // Set the viewport to be the entire window
       gl.glViewport(0, 0, width, height);
   }

    @Override
    public void dispose(GLAutoDrawable drawable)  { }                  // not needed

    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args)
    { new Lissajous();
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

}