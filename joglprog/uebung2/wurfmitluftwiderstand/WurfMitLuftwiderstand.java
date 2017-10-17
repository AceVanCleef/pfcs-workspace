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
boolean stopped = false;

Wurfgeschoss speer;
Fallobjekt blech;
Kanone kanone;

Vec2 pos0Line;
Vec2 posLine;
Vec2 v0Norm;
float alpha;
Vec2 pos2Line;

float feuerkraft = 5f;
Vec2 newVelocity;

boolean readyToShoot = true;

public void zeichneLinie(GL3 gl, float x1, float y1, float x2, float y2) {
	mygl.rewindBuffer(gl);             // Vertex-Buffer zuruecksetzen
	mygl.putVertex(x1,y1,0);           // Eckpunkte in VertexArray speichern
	mygl.putVertex(x2,y2,0);
	mygl.copyBuffer(gl);
	mygl.drawArrays(gl,GL3.GL_LINES);
}



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
   kanone = new Kanone(mygl, x, y);
   
   
   //Von Ursprung zu Startpunkt des Richtungsvektur_Abschussrichtung
   pos0Line = new Vec2(speer.getX0(), speer.getY0());
   //Von Ursprung zu Endpunkt des Richtungsvektur_Abschussrichtung
   posLine = new Vec2(pos0Line.x, pos0Line.y);
   //x-Einheitsvektor normalisiert (Länge = 1)
   v0Norm = new Vec2(speer.getV0x(), speer.getV0y());
   v0Norm = v0Norm.normalize();
   //Winkel zwischen Richtungsvektor_Abschussrichtung und normalisierter x-Einheitsvektor
   alpha = (float) Math.acos( v0Norm.dot(Vec2.X) );	//Vec2.X = (1,0). .dot() -> Skalarprodukt
   
   //Linearkombination aus pos0Line und posLine = Richtungsvektor_Abschussrichtung
   pos2Line = new Vec2(pos0Line.x + posLine.x, pos0Line.y + posLine.y);
   
   v0x = 0;
   v0y = 0;
   x0 = 8;
   y0 = 5;
   x = x0; y = y0;
   vx = v0x; vy = v0y;
   ax = 0; ay = -g;
   blech = new Fallobjekt(mygl, v0x, v0y, x0, y0, x, y, vx, vy, ax, ay, dt, ybottom);
}



@Override
public void display(GLAutoDrawable drawable)
{ GL3 gl = drawable.getGL().getGL3();
  gl.glClear(GL3.GL_COLOR_BUFFER_BIT);             // Bildschirm loeschen
  mygl.setColor(1,1,0);                            // Farbe der Vertices
  
  //M für Kugel zurück setzen
  M = Mat4.ID;
  mygl.setM(gl, M);
  
  if (stopped) return;
  
  speer.updateCoords();
  speer.rotateRelative(gl, M);
  speer.draw(gl, 1.2f, 0.04f, 0.2f);
  
  M = Mat4.ID;
  mygl.setM(gl, M);
  kanone.rotateRelative(gl, M);
  kanone.draw(gl, (float) kanone.getX(), (float) kanone.getY());

  //M zurück setzen
  M = Mat4.ID;
  mygl.setM(gl, M);
  //Linie
//  double sin = Math.tan(alpha) * Math.cos(alpha);
//  double cos = Math.sin(alpha) / Math.tan(alpha);
  Vec2 sin = new Vec2(0, Math.sin(alpha));
  Vec2 cos = new Vec2(Math.cos(alpha), 0);
  pos2Line = new Vec2(sin.x + cos.x, sin.y + cos.y);
  zeichneLinie(gl, posLine.x, posLine.y, pos2Line.x, pos2Line.y);
  
  //M zurück setzen
  M = Mat4.ID;
  mygl.setM(gl, M);
  
  blech.updateCoords();
  blech.draw(gl, (float) blech.getX(), (float) blech.getY());
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
  { case KeyEvent.VK_UP : 
  		kanone.setAngle( kanone.getAngle() + 0.5f);
  	    alpha += 0.5f;
  		break;
    case KeyEvent.VK_DOWN :
		kanone.setAngle( kanone.getAngle() - 0.5f);
		alpha -= 0.5f;
		break;
  }
}

public void keyReleased(KeyEvent e) { }

public void keyTyped(KeyEvent e)
{ char code = e.getKeyChar();
	switch (code){
		case 's' : System.out.println("Schuss!");
					readyToShoot = false;
					break;
		case 'r' : System.out.println("Reset!");
					speer.resetPos();
					blech.resetPos();
					kanone.reset();
					readyToShoot = true;
					break;
		case 'v' : System.out.println("Abschussgeschw. kleiner");
					speer.setV0y( speer.getV0y() - 0.5f ); //v0y -= 0.5;
					
					feuerkraft -= 0.5f;
			  		
			  		newVelocity = v0Norm.scale(feuerkraft);
			  		
			  		//Speer ausrichten
			  		speer.setVx( newVelocity.x);
			  		speer.setVy( newVelocity.y);
			  		
			  		//Endpunkt des Richtugnsvektors_Abschussrichtung anpassen:
			  		pos2Line = new Vec2(pos0Line.x + newVelocity.x, pos0Line.y + newVelocity.y);
			  		
			  		
					break;
		case 'V' : System.out.println("Abschussgeschw. grösser");
					speer.setV0y( speer.getV0y() + 0.5f ); //v0y += 0.5;
					
					feuerkraft += 0.5f;
			  		
			  		newVelocity = v0Norm.scale(feuerkraft);
			  		
			  		//Speer ausrichten
			  		speer.setVx( newVelocity.x);
			  		speer.setVy( newVelocity.y);
			  		
			  		//Endpunkt des Richtugnsvektors_Abschussrichtung anpassen:
			  		pos2Line = new Vec2(pos0Line.x + newVelocity.x, pos0Line.y + newVelocity.y);
			  		
			  		
					break;
	}

}

}