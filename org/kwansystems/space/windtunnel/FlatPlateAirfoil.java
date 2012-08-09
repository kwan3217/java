package org.kwansystems.space.windtunnel;

import org.kwansystems.vector.*;

public class FlatPlateAirfoil extends Airfoil {
  public FlatPlateAirfoil(MathVector LNormal, MathVector LChordLine, MathVector lCoL,double c, double span, double e) {
    super(LNormal,LChordLine,lCoL,c,span,e);
  }
  public double[] F(double aoa, double M, double Re) {  
    double[] c=new double[3];
    c[CL]=1.2*Math.sin(2*aoa);
    c[CDE]=0.06+Math.pow(Math.sin(aoa),2)*2;
    c[CM]=0;
    return c;
  }
}
