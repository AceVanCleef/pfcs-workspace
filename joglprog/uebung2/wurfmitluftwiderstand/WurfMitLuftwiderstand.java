package uebung2.wurfmitluftwiderstand;
//-------------   JOGL 2D-Programm  -------------------
import java.awt.*;
import java.awt.event.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.*;
import ch.fhnw.util.math.*;

public class WurfMitLuftwiderstand
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
//double v0x = 6;   // Anfangsgeschw.
//double v0y = 10;   // Anfangsgeschw.
//double x0 = -8;
//double y0 = 0;
//double x = x0;
//double y = y0;
//double vx = v0x;
//double vy = v0y;
//double ax = 0;
//double ay = -g;
//double dt = 0.01;       // Zeitschritt
boolean stopped = false;

Wurfgeschoss speer;

//#Fallobjekt


//  ---------  Methoden  ----------------------------------

public WurfMitLuftwiderstand()                                   // Konstruktor
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

//#Fallobjekt
public void zeichneFallobjekt(GL3 gl, float x1, float y1,
						float x2, float y2, float x3, float y3, float x4, float y4){
	mygl.rewindBuffer(gl);             // Vertex-Buffer zuruecksetzen
	
	mygl.putVertex(x1, y1, 0);
	mygl.putVertex(x2, y2, 0);
	mygl.putVertex(x3, y3, 0);
	mygl.putVertex(x4, y4, 0);
	
	mygl.copyBuffer(gl);
	mygl.drawArrays(gl,GL3.GL_TRIANGLES);
}


//public void zeichneDreieck(GL3 gl, float x1, float y1,
//                         float x2, float y2, float x3, float y3)
//{  mygl.rewindBuffer(gl);             // Vertex-Buffer zuruecksetzen
//   mygl.putVertex(x1,y1,0);           // Eckpunkte in VertexArray speichern
//   mygl.putVertex(x2,y2,0);
//   mygl.putVertex(x3,y3,0);
//   mygl.copyBuffer(gl);
//   mygl.drawArrays(gl,GL3.GL_TRIANGLES);
//}
//
//public void zeichneSpeer(GL3 gl, float a, float b, float c) {
//	mygl.rewindBuffer(gl);             // Vertex-Buffer zuruecksetzen
//		//Viereckige Komponente d. Speers
//	   mygl.putVertex(-a,-b,0);           // Eckpunkte in VertexArray speichern
//	   mygl.putVertex(a,-b,0);           // Eckpunkte in VertexArray speichern
//	   mygl.putVertex(-a,b,0);           // Eckpunkte in VertexArray speichern
//	   mygl.putVertex(a,b,0);           // Eckpunkte in VertexArray speichern
//	   
//	   mygl.copyBuffer(gl);
//	   mygl.drawArrays(gl,GL3.GL_TRIANGLE_FAN);
//	   
//	   //Speerspitzen (Anfang und Ende)
//	   zeichneDreieck(gl, a, -b, a+c, 0, a, b);
//	   zeichneDreieck(gl, -a, b, -(a+c), 0, -a, -b);
//	   
//	   //#Speer zeichnen: statt 2x Dreiecke und 1x Viereck, ein 6-Eck zeichnen
//}


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
   speer = new Wurfgeschoss(mygl, v0x, v0y, x0, y0, x, y, vx, vy, ax, ay, dt, ybottom);
}



@Override
public void display(GLAutoDrawable drawable)
{ GL3 gl = drawable.getGL().getGL3();
  gl.glClear(GL3.GL_COLOR_BUFFER_BIT);             // Bildschirm loeschen
  mygl.setColor(1,1,0);                            // Farbe der Vertices
  
  //M für Kugel zurück setzen
  M = Mat4.ID;
  mygl.setM(gl, M);
  
  //zeichneKreis(gl, 0.2f, (float)x, (float)y, 20);
  if (stopped) return;
//  x = x + vx*dt;
//  y = y + vy*dt;
//  vx = vx + ax*dt;
//  vy = vy + ay*dt;
//  if (y < ybottom)
//  {  x=x0;
//     y=y0;
//     vx=v0x;
//     vy=v0y;
//  }
  
  speer.updateCoords();
  
  //#Speerwurf
//  M = Mat4.translate((float) speer.getX(), (float) speer.getY(), 0);	//Matrixtransformation: Vorwärtsbewegung des Speers
//  double alpha = Math.atan(speer.getVy() / speer.getVx());	//Drehwinkel in Radiant
//  alpha = alpha * 180 / Math.PI;		//Drehwinkel in Grad
//  M = M.postMultiply( Mat4.rotate( (float) alpha, 0, 0, 1) ); //M = M x R (transl. i. objektlokalen Koord.sys.)

  speer.rotateRelative(gl, M);
  
//  mygl.setM(gl, M);
  //zeichneSpeer(gl, 1.2f, 0.04f, 0.2f);
  speer.draw(gl, 1.2f, 0.04f, 0.2f);
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
{ new WurfMitLuftwiderstand();
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
  { case KeyEvent.VK_UP : speer.setV0y( speer.getV0y() + 0.5f); //v0y += 0.5;
                          break;
    case KeyEvent.VK_DOWN : speer.setV0y( speer.getV0y() - 0.5f ); //v0y -= 0.5;
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