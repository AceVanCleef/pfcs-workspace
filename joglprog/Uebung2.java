//-------------   JOGL 2D-Programm  -------------------
import java.awt.*;
import java.awt.event.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.*;
import ch.fhnw.util.math.*;

public class Uebung2
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
double xleft = -10, xright = 10;  // ViewingVol
double ybottom, ytop;
double znear = -100, zfar = 100;

final double g = 9.81;  // Erdbeschl.
final double m = 1;     // Masse
double v0x = 6;   // Anfangsgeschw.
double v0y = 10;   // Anfangsgeschw.
double x0 = -8;
double y0 = 0;
double x = x0;
double y = y0;
double vx = v0x;
double vy = v0y;
double ax = 0;
double ay = -g;
double dt = 0.01;       // Zeitschritt
boolean stopped = false;


//#Zielscheibe
double targetX0 = 8, targetY0 = 5;
double targetX = targetX0, targetY = targetY0;
double targetV0X = 0, targetV0Y = 0;
double targetVX = targetV0X, targetVY = targetV0Y;
double targetAX = 0, targetAY = -g;

//#Anvisierlinie
Vec2 ursprungZuLinie = new Vec2(x0, y0);
Vec2 ursprungZuLinienSpitze = new Vec2(ursprungZuLinie.x, ursprungZuLinie.y);
Vec2 linie = new Vec2(ursprungZuLinie.x + ursprungZuLinienSpitze.x, ursprungZuLinie.y + ursprungZuLinienSpitze.y);
double alpha = Math.atan(vy / vx) * 180 / Math.PI;	//Drehwinkel in Grad


//  ---------  Methoden  ----------------------------------

public Uebung2()                                   // Konstruktor
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

public void zeichneViereck(GL3 gl, float x1, float y1, float width, float height) 
{	mygl.rewindBuffer(gl);             // Vertex-Buffer zuruecksetzen
	mygl.putVertex(x1, y1, 0);
	mygl.putVertex(x1 + width, y1, 0);
	mygl.putVertex(x1, y1 + height, 0);
	mygl.putVertex(x1 + width, y1 + height, 0);
	
	mygl.copyBuffer(gl);
	mygl.drawArrays(gl,GL3.GL_TRIANGLE_STRIP);
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

public void zeichneLinie(GL3 gl, float x1, float y1, float x2, float y2) {
	mygl.rewindBuffer(gl);             // Vertex-Buffer zuruecksetzen
	mygl.putVertex(x1,y1,0);           // Eckpunkte in VertexArray speichern
	mygl.putVertex(x2,y2,0);
	
	System.out.println("x1: " + x1 + "y1 " + y1 + "x2 " + x2 + " y2 " +y2);
	
	mygl.copyBuffer(gl);
	mygl.drawArrays(gl,GL3.GL_LINES);
}

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
   FPSAnimator anim = new FPSAnimator(canvas, 200, true);  // Animations-Thread, 200 Frames/sek
   anim.start();

}



@Override
public void display(GLAutoDrawable drawable)
{ 
	
	GL3 gl = drawable.getGL().getGL3();
  gl.glClear(GL3.GL_COLOR_BUFFER_BIT);             // Bildschirm loeschen
	M = Mat4.ID;
	mygl.setM(gl, M);
  
  mygl.setColor(1,1,0);                            // Farbe der Vertices
  zeichneKreis(gl, 0.2f, (float)x, (float)y, 20);
  if (stopped) return;
  x = x + vx*dt;
  y = y + vy*dt;
  vx = vx + ax*dt;
  vy = vy + ay*dt;
  if (y < ybottom)
  {  x=x0;
     y=y0;
     vx=v0x;
     vy=v0y;
  }
  
  //#Zielscheibe
  mygl.setColor(0,1,1);                            // Farbe der Vertices
  zeichneViereck(gl, (float) targetX, (float) targetY, 2.5f, 5.0f);

  targetX = targetX + targetVX*dt;
  targetY = targetY + targetVY*dt;
  targetVX = targetVX + targetAX*dt;
  targetVY = targetVY + targetAY*dt;
  if (targetY < ybottom - 20){
	  targetX = targetX0;
	  targetY = targetY0;
	  targetVY = targetV0Y;
	  targetVX = targetV0X;
  }
  
  //#Anvisierlinie
  mygl.setColor(0,0,0);                            		// Farbe der Vertices
  alpha = Math.atan(v0y / v0x) * 180 / Math.PI;	//Drehwinkel in Grad
  
  M = Mat4.translate((float) ursprungZuLinie.x, (float) ursprungZuLinie.y, 0.0f);	//setzte 0-Punkt des lokalen Systems
  M = M.postMultiply(Mat4.rotate((float) alpha, 0.0f, 0.0f, 1.0f));					//im lokalen System rotieren.
  mygl.setM(gl, M);
  linie = new Vec2(Math.cos(alpha), Math.sin(alpha));
  zeichneLinie(gl, (float) 0, (float) 0, 
		  (float) 5, (float) 0);
  
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
{ new Uebung2();
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
  { case KeyEvent.VK_UP : v0y += 0.1;
                          break;
    case KeyEvent.VK_DOWN : v0y -= 0.1;
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