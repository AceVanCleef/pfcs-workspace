//-------------   JOGL 2D-Programm  -------------------
import java.awt.*;
import java.awt.event.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.*;
import ch.fhnw.util.math.*;

public class Kepler
   implements WindowListener, GLEventListener, KeyListener
{

//  ---------  globale Daten  ---------------------------

String windowTitle = "JOGL-Application";
int windowWidth = 800;
int windowHeight = 600;
String vShader = MyShaders.vShader1;                 // Vertex-Shader
String fShader = MyShaders.fShader0;                 // Fragment-Shader
int maxVerts = 2048;                                 // max. Anzahl Vertices im Vertex-Array
GLCanvas canvas;                                     // OpenGL Window
MyGLBase1 mygl;                                   // eigene OpenGL-Basisfunktionen
Mat4 M = Mat4.ID;    // Transf.Matrix
Mat4 P = Mat4.ID;    // Proj. Matrix
double xleft = -60, xright = 60;  // ViewingVol	[60 * 1000 km]
double ybottom, ytop;
double znear = -100, zfar = 100;


//#Kepler
final double g = 9.81e-6;	// Erdbeschl.
double rE = 6.376;			//Erdradius
double GM = g*rE*rE;		//G*M (G: Gravitationskonstante, M: Erdmasse)

double x0 = 42;	//Radius Kreisbahn
double y0 = 0;
double v0x = 0;   // Anfangsgeschw.
double v0y = Math.sqrt(GM / x0);   // Anfangsgeschw.	[km/s]
double x = x0;
double y = y0;
double vx = v0x;
double vy = v0y;
double ax;
double ay;
double dt = 60;       // Zeitschritt in sec.
boolean stopped = false;



//LookAt-Parameter fuer Kamera-System
Vec3 A = new Vec3(0,0,50);                   // Kamera-Pos. (Auge); 50 = 50'000 km
Vec3 B = new Vec3(0,0,0);                   // Zielpunkt
Vec3 up = new Vec3(0,1,0);                  // up-Richtung

//#Kamerasystem
float elevation = 1f;
float azimut = 1f;

//  ---------  Methoden  ----------------------------------

public Kepler()                                   // Konstruktor
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
   f.addKeyListener(this);
   canvas.addKeyListener(this);
};


public void zeichneKreis(GL3 gl, float r,
        float xm, float ym, int nPkte)
{  double phi = 2*Math.PI / nPkte;
  double x,y;
  mygl.rewindBuffer(gl);
    mygl.putVertex(xm,ym,0);
    for (int i=0; i <= nPkte; i++)
    {  x = xm + r * Math.cos(i*phi);
       y = ym + r * Math.sin(i*phi);
       mygl.putVertex((float)x,(float)y,0);
    }
  mygl.copyBuffer(gl);
  mygl.drawArrays(gl,GL3.GL_TRIANGLE_FAN);
}





public void zeichneDreieck(GL3 gl, float x1, float y1,
                         float x2, float y2, float x3, float y3)
{  mygl.rewindBuffer(gl);             // Vertex-Buffer zuruecksetzen
   mygl.putVertex(x1,y1,0);           // Eckpunkte in VertexArray speichern
   mygl.putVertex(x2,y2,0);
   mygl.putVertex(x3,y3,0);
   mygl.copyBuffer(gl);
   mygl.drawArrays(gl,GL3.GL_TRIANGLES);
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
   FPSAnimator anim = new FPSAnimator(canvas, 200, true);  // Animations-Thread, 200 Frames/sek
   anim.start();

}



@Override
public void display(GLAutoDrawable drawable)
{ GL3 gl = drawable.getGL().getGL3();
	gl.glClear(GL3.GL_COLOR_BUFFER_BIT);                        // Bildschirm loeschen
	mygl.setColor(1,1,0);                               // Farbe der Vertices
	M = Mat4.ID;
	mygl.setM(gl,M);
	
	Mat4 R1 = Mat4.rotate(-elevation,1,0,0);
	Mat4 R2 = Mat4.rotate(azimut,0,1,0);
	Mat4 R = R1.preMultiply(R2);
	M = Mat4.lookAt(R.transform(A),B,R.transform(up));
	mygl.setM(gl,M);
	
	mygl.drawAxis(gl,50,50,50);                            // Koordinatenachsen
	
	M = M.postMultiply(Mat4.rotate(90,1,0,0));				//Mond drehen
	
	mygl.setM(gl,M);
	
	zeichneKreis(gl, (float) rE, 0, 0, 20);                     // Erde
	zeichneKreis(gl, (float) (0.2*rE), (float) x, (float) y, 20);       // Mond
	
	
	if (stopped) return;
	x = x + vx*dt;
	y = y + vy*dt;
	
	double r = Math.sqrt(x*x+y*y);
	double r3 = r*r*r;
	ax = -GM*x/r3;
	ay = -GM*y/r3;
	
	vx = vx + ax*dt;
	vy = vy + ay*dt;

}



@Override
public void reshape(GLAutoDrawable drawable, int x, int y,
                    int width, int height)
{  GL3 gl = drawable.getGL().getGL3();
   // Set the viewport to be the entire window
   gl.glViewport(0, 0, width, height);
   double aspect = (double)height / width;
   ybottom = aspect * xleft;
   ytop = aspect * xright;
   P = Mat4.ortho((float)xleft, (float)xright,
              (float)ybottom, (float)ytop,
              (float)znear, (float)zfar);
   mygl.setP(gl, P);
}

@Override
public void dispose(GLAutoDrawable drawable)  { }                  // not needed

//  -----------  main-Methode  ---------------------------

public static void main(String[] args)
{ new Kepler();
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
  { case 'V' : v0y += 0.5;
                          break;
    case 'v' : v0y -= 0.5;
                          break;
    case KeyEvent.VK_UP : elevation++;	//#Kamerasystem bewegen
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
    stopped = !stopped;
}

}