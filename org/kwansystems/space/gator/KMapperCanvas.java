package org.kwansystems.space.gator;

import java.awt.*;
import javax.swing.*;

import org.kwansystems.space.ephemeris.*;
import org.kwansystems.space.ephemeris.jupiter.JupiterSatE5;
import org.kwansystems.space.kepler.*;
import org.kwansystems.tools.vector.*;

public class KMapperCanvas extends JPanel {
  private static final long serialVersionUID = 7767229624884290326L;
  MathStateTime CurrentStateTime;
  MathVector[] R;
  double WidthKm;
  int Reference,Influence;
  Elements E;
  public KMapperCanvas() {
    WidthKm=2.5e6;
    Reference=0;
  }
  Color[] SatColor={new Color(255,0,0),new Color(255,128,0),new Color(255,255,255),new Color(128,64,0),new Color(128,128,128)};
  Color ProbeColor=new Color(255,255,0);
  
  public void paintComponent(Graphics G) {
    super.paintComponent(G);
    int XCenter=getWidth()/2;
    int YCenter=getHeight()/2;
    double Scale=WidthKm/(XCenter*2);
    int x1,y1,x2,y2;
    double xref,yref;
    xref=R[Reference].X(); 
    yref=R[Reference].Y();
    if(R!=null) {
      for(int i=0;i<=4;i++) {
        G.setColor(SatColor[i]);
        double rad=Math.max(5*Scale,JupiterSatE5.satRadius[i]);
        x1=XCenter+(int)((R[i].X()-rad-xref)/Scale);
        y1=YCenter-(int)((R[i].Y()+rad-yref)/Scale);
        x2=XCenter+(int)((R[i].X()+rad-xref)/Scale);
        y2=YCenter-(int)((R[i].Y()-rad-yref)/Scale);
        G.fillOval(x1,y1,x2-x1,y2-y1);
        x1=XCenter+(int)((-R[i].length()-xref)/Scale);
        y1=YCenter-(int)((+R[i].length()-yref)/Scale);
        x2=XCenter+(int)((+R[i].length()-xref)/Scale);
        y2=YCenter-(int)((-R[i].length()-yref)/Scale);
        G.drawOval(x1,y1,x2-x1,y2-y1);
      }
      G.setColor(ProbeColor);
      MathVector P=CurrentStateTime.S.R();
      double rad=2.5*Scale;
      x1=XCenter+(int)((P.X()-rad-xref)/Scale);
      y1=YCenter-(int)((P.Y()+rad-yref)/Scale);
      x2=XCenter+(int)((P.X()+rad-xref)/Scale);
      y2=YCenter-(int)((P.Y()-rad-yref)/Scale);
      G.fillOval(x1,y1,x2-x1,y2-y1);
    }
    if(E!=null) {
      int NPoints=360;
      MathVector[] points=E.PlotOrbit(NPoints);
      int oldx=XCenter+(int)((points[0].X()+R[Influence].X()-xref)/Scale);
      int oldy=YCenter-(int)((points[0].Y()+R[Influence].Y()-yref)/Scale);
      for(int i=1;i<NPoints-1;i++) {
        int x=XCenter+(int)((points[i].X()+R[Influence].X()-xref)/Scale);
        int y=YCenter-(int)((points[i].Y()+R[Influence].Y()-yref)/Scale);
        G.drawLine(oldx,oldy,x,y);
        oldx=x;
        oldy=y;
      }
    }
  }
  
  void Map(
    Ephemeris[] Sat,
    MathStateTime S,
    int LReference,
    int LInfluence,
    Elements LE,
    double LWidthKm
  ) {
    R=new MathVector[5];
    for(int i=0;i<=4;i++) {
      R[i]=Sat[i].getState(S.T).R();
    }
    CurrentStateTime=S;
    Reference=LReference;
    Influence=LInfluence;
    E=LE;
    WidthKm=LWidthKm;
    repaint();
  }
  void reMap(
    int LReference,
    double LWidthKm
  ) {
    Reference=LReference;
    WidthKm=LWidthKm;
    repaint();
  }
}
