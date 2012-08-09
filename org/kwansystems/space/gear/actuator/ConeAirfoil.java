package org.kwansystems.space.gear.actuator;

import org.kwansystems.tools.vector.*;

public class ConeAirfoil extends Airfoil {
  double ConeHalfAngle;
  public ConeAirfoil(String LName, MathVector lCoF, MathVector LNormal, MathVector LChordLine, double chord, double span) {
    super(LName, lCoF, LNormal, LChordLine, chord, span, Airfoil.LiftModel.Axial);
    SurfaceArea=Math.PI/4*span*span;
    ConeHalfAngle=Math.atan((span/2)/chord);
  }
  //Cone model equations
  public double Cn(double alpha) {
    return Math.pow(Math.cos(ConeHalfAngle),2)*Math.sin(2*alpha);
  }
  public double Ca(double alpha) {
    return 2*Math.pow(Math.sin(ConeHalfAngle),2)+Math.pow(Math.sin(alpha),2)*(1-3*Math.pow(Math.sin(ConeHalfAngle),2));
  }
  public double[] C(double AoA, double M, double Re) {  
    double[] c=new double[3];
    c[CL]=(Math.sin(AoA)*Ca(AoA)+Math.cos(AoA)*Cn(AoA));
    c[CD]=(Math.cos(AoA)*Ca(AoA)+Math.sin(AoA)*Cn(AoA));
    c[CM]=0;
    return c;
  }
}
