package uebung3_boomerang;
//  -------------   Quader mit Randlinien ------------
//                                                            E.Gutknecht, Juli 2015
import com.jogamp.opengl.*;

import ch.fhnw.pfcs.opengl.MyTransf;
import ch.fhnw.pfcs.opengl.MyVertexBuf;
import ch.fhnw.util.math.*;

public class Quader
{
    //  ----------------  globale Daten  -------------------------
//	MyVertexBuf vertexBuf;
//	MyTransf transf;
	
	
    Vec3 e1 = new Vec3(1,0,0);               // Normalenvektoren
    Vec3 e2 = new Vec3(0,1,0);
    Vec3 e3 = new Vec3(0,0,1);
    Vec3 e1n = new Vec3(-1,0,0);             // negative Richtung
    Vec3 e2n = new Vec3(0,-1,0);
    Vec3 e3n = new Vec3(0,0,-1);


    //  ---------------------  Methoden  --------------------------

//    public Quader(MyVertexBuf vertexBuf, MyTransf transf)
//    {  this.vertexBuf = vertexBuf;
//    	this.transf = transf;
//    }

    public Quader()
    {  
    }



    public void Viereck(GL3 gl, MyVertexBuf vertexBuf, 
    		Vec3 A, Vec3 B, Vec3 C, Vec3 D, Vec3 n)
    {  vertexBuf.setNormal(n.x, n.y, n.z);		 // Normale
    vertexBuf.putVertex(A.x, A.y, A.z);          // Dreieck 1
    vertexBuf.putVertex(B.x, B.y, B.z);
    vertexBuf.putVertex(C.x, C.y, C.z);
    vertexBuf.putVertex(C.x, C.y, C.z);          // Dreieck 2
    vertexBuf.putVertex(D.x, D.y, D.z);
    vertexBuf.putVertex(A.x, A.y, A.z);
    }


    public void kante(MyVertexBuf vertexBuf, Vec3  a, Vec3 b)
    {  vertexBuf.putVertex(a.x, a.y, a.z);
    vertexBuf.putVertex(b.x, b.y, b.z);
    }


    public void draw(GL3 gl, MyVertexBuf vertexBuf,
                        float a, float b, float c,   // Kantenlaengen
                        boolean gefuellt)
    {  a *= 0.5f;
       b *= 0.5f;
       c *= 0.5f;
       Vec3 A = new Vec3( a,-b, c);           // Bodenpunkte
       Vec3 B = new Vec3( a,-b,-c);
       Vec3 C = new Vec3(-a,-b,-c);
       Vec3 D = new Vec3(-a,-b, c);
       Vec3 E = new Vec3( a, b, c);           // Deckpunkte
       Vec3 F = new Vec3( a, b,-c);
       Vec3 G = new Vec3(-a, b,-c);
       Vec3 H = new Vec3(-a, b, c);
       vertexBuf.rewindBuffer(gl);
       int nVertices;
       if ( gefuellt )
       {  Viereck(gl, vertexBuf, D,C,B,A,e2n);            // Boden
          Viereck(gl, vertexBuf, E,F,G,H,e2);             // Deckflaeche
          Viereck(gl, vertexBuf, A,B,F,E,e1);             // Seitenflaechen
          Viereck(gl, vertexBuf, B,C,G,F,e3n);
          Viereck(gl, vertexBuf, D,H,G,C,e1n);
          Viereck(gl, vertexBuf, A,E,H,D,e3);
          nVertices = 36;
          vertexBuf.copyBuffer(gl, nVertices);
          gl.glDrawArrays(GL3.GL_TRIANGLES, 0, nVertices);

       }
       else
       {  kante(vertexBuf, A,B);                         // Boden
          kante(vertexBuf, B,C);
          kante(vertexBuf, C,D);
          kante(vertexBuf, D,A);
          kante(vertexBuf, E,F);                         // Decke
          kante(vertexBuf, F,G);
          kante(vertexBuf, G,H);
          kante(vertexBuf, H,E);
          kante(vertexBuf, A,E);                         // Kanten
          kante(vertexBuf, B,F);
          kante(vertexBuf, C,G);
          kante(vertexBuf, D,H);
          nVertices = 24;
         
          vertexBuf.copyBuffer(gl, nVertices);
          gl.glDrawArrays(GL3.GL_LINES, 0, nVertices);
        }
    }

}