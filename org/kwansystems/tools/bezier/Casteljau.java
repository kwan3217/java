package org.kwansystems.tools.bezier;

import java.awt.Graphics;

import org.kwansystems.tools.vector.*;
import org.kwansystems.tools.zoetrope.*;

public class Casteljau {
  Lerpable[] controlPoints;
  public Casteljau(Lerpable[] LcontrolPoints) {
    controlPoints=LcontrolPoints;
  }
  static Lerpable[] iterateCasteljau(Lerpable[] points, double t) {
    Lerpable[] result=new Lerpable[points.length-1];
    for(int i=0;i<result.length;i++) {
      result[i]=points[i].Lerp(points[i+1], t);
    }
    return result;    
  }
  public Lerpable evaluate(double t) {
    Lerpable[] intm=controlPoints;
    while(intm.length>1) {
      intm=iterateCasteljau(intm,t);
    }
    return intm[0];
  }
  public static void main(String args[]) {
    final Casteljau C=new Casteljau(null);
    HandleZoetrope Z=new HandleZoetrope("de Casteljau construction",100) {
      private void drawX(Graphics G, Lerpable L, int size) {
        MathVector V=(MathVector)L;
        drawX(G,V.X(),V.Y(),size);
      }
      private void drawLine(Graphics G, Lerpable L1,Lerpable L2) {
        MathVector V1=(MathVector)L1;
        MathVector V2=(MathVector)L2;
        drawLine(G,V1.X(),V1.Y(),V2.X(),V2.Y());
      }
      @Override
      protected void paintFrame(Graphics G) {
        double t=(double)CurrentFrame/(double)getNumFrames();
        Lerpable[] intm=C.controlPoints;
        while(intm.length>1) {
          for(int i=0;i<intm.length-1;i++) {
            drawLine(G,intm[i],intm[i+1]);
          }
          intm=iterateCasteljau(intm,t);
        }
        drawX(G,C.evaluate(t),3);
        Lerpable xn=C.evaluate(0);
        for(int i=1;i<=CurrentFrame;i++) {
          t=(double)i/(double)getNumFrames();
          Lerpable xnp1=C.evaluate(t);
          drawLine(G,xn,xnp1);
          xn=xnp1;
        }
        drawHandles(G);
      }
    };
    C.controlPoints=Z.handle;
    Z.setRmax(5);
    Z.setNumFrames(100);
    Z.setVisible(true);
    Z.start();
  }
}
