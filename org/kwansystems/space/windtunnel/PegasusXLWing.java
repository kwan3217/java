package org.kwansystems.space.windtunnel;

import org.kwansystems.table.*;
import org.kwansystems.vector.*;

public class PegasusXLWing extends Airfoil {
  private static final double R=Math.PI/180.0;  
  private static Table ClTable=new LinearTable(new double[] {-180*R,-165*R,-150*R,-135*R,-120*R,-105*R, -90*R, -75*R, -60*R, -45*R, -30*R, -15*R,   0*R,  15*R,  30*R,  45*R,  60*R,  75*R,  90*R, 105*R, 120*R, 135*R, 150*R, 165*R, 180*R},
                                                   new double[] {     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,  -0.5, -0.02,0.6355,     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,     0});
  private static Table CmTable=new LinearTable(new double[] {-180*R,-165*R,-150*R,-135*R,-120*R,-105*R, -90*R, -75*R, -60*R, -45*R, -30*R, -15*R,   0*R,  15*R,  30*R,  45*R,  60*R,  75*R,  90*R, 105*R, 120*R, 135*R, 150*R, 165*R, 180*R}, 
                                                   new double[] {     0,     0,     0,     0,     0,     0,     0,     0,     0, 0.002, 0.004,0.0025,0.0012,     0,-0.0012,-0.0007,   0,     0,     0,     0,     0,     0,     0,     0,     0});
  public PegasusXLWing(MathVector LNormal, MathVector LChordLine, MathVector lCoL) {
    super(LNormal,LChordLine,lCoL,2,6.7,0.6);
  }
  public double[] F(double aoa, double M, double Re) {  
    double[] c=new double[3];
    c[CL]=ClTable.Interp(aoa)+1.2*Math.sin(2*aoa);
    c[CDE]=0.06+Math.pow(Math.sin(aoa),2)*2;
    c[CM]=CmTable.Interp(aoa);;
    return c;
  }
}
