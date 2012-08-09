package org.kwansystems.coaster;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.util.List;

import org.kwansystems.tools.bezier.Bezier;
import org.kwansystems.tools.vector.*;

public class KBezierCanvas extends JPanel {
  private static final long serialVersionUID = -8176033174215077083L;
  public List<Bezier> Track;
  public int Steps;
  public double KmPerPix;
  private int currentSeg;
  private int Horz, Vert;
  public KBezierCanvas(int LHorz,int LVert) {
    Track=new ArrayList<Bezier>();
    KmPerPix=0.03;
    Horz=LHorz;
    Vert=LVert;
  }
  public void paintComponent(Graphics G) {
    super.paintComponent(G);
    G.clearRect(0,0,getWidth()-1,getHeight()-1);
    int CenterX=getWidth()/2;
    int CenterY=getHeight()/2;
    if(Track==null) return;

    int segnum=0;
    for(Bezier seg : Track) {
      if(segnum == currentSeg) {
        G.setColor(Color.RED);
      } else {
        G.setColor(Color.BLACK);
      }
      MathVector[] P=seg.Plot();
      int OldX=((int)(CenterX+P[0].get(Horz)/KmPerPix));
      int OldY=((int)(CenterY-P[0].get(Vert)/KmPerPix));
      for(int i=1;i<P.length;i++) {
        int NewX=((int)(CenterX+P[i].get(Horz)/KmPerPix));
        int NewY=((int)(CenterY-P[i].get(Vert)/KmPerPix));
        G.drawLine(OldX,OldY,NewX,NewY);
        OldX=NewX;
        OldY=NewY;
      }
      segnum++;
    }
  }
  public int getCurrentSeg() {
    return currentSeg;
  }
  public void setCurrentSeg(int LcurrentSeg) {
    currentSeg = LcurrentSeg;
    repaint();
  }
  public KBezierCanvas() {
    this(0,1);
  }
}
