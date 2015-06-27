package org.kwansystems.space.gear.actuator;

import org.kwansystems.tools.table.*;
import org.kwansystems.tools.vector.*;

public class FlatPlatePlusAirfoil extends Airfoil {
  private static final double R=Math.PI/180.0;  
  protected static final double[] D=new double[] {-180*R,-165*R,-150*R,-135*R,-120*R,-105*R, -90*R, -75*R, -60*R, -45*R, -30*R, -15*R,   0*R,  15*R,   30*R,   45*R,  60*R,  75*R,  90*R, 105*R, 120*R, 135*R, 150*R, 165*R, 180*R};
  Table ClmTable;
  public FlatPlatePlusAirfoil(String LName, MathVector LCoF, MathVector LNormal, MathVector LChordLine, double lChord, double lSpan) {
    super(LName,LCoF, LNormal,LChordLine,lChord,lSpan);
  }
  public double[] C(double aoa, double M, double Re) {  
    double[] c=new double[3];
    c[CL]=ClmTable.Interp(aoa,0)+1.2*Math.sin(2*aoa);
    c[CD]=0.00+Math.pow(Math.sin(aoa),2)*2;
    c[CM]=ClmTable.Interp(aoa,1);
    return c;
  }
}
