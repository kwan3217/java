package org.kwansystems.space.gear.actuator;

import org.kwansystems.tools.table.*;
import org.kwansystems.tools.vector.*;

public class PegasusXLWing extends FlatPlatePlusAirfoil {
  public PegasusXLWing(String LName, MathVector LCoF, MathVector LNormal, MathVector LChordLine, double lChord, double lSpan) {
    super(LName,LCoF, LNormal,LChordLine,lChord,lSpan); 
                                                //-180,-165,-150,-135,-120,-105, -90, -75, -60,  -45,  -30,   -15,     0,    15,     30,     45,60,75,90,105,120,135,150,165,180};
    ClmTable=new LinearTable(D,new double[][] {{     0,   0,   0,   0,   0,   0,   0,   0,   0,    0,    0,  -0.5,  0.02,0.6355,      0,      0, 0, 0, 0,  0,  0,  0,  0,  0,  0},
                                               {     0,   0,   0,   0,   0,   0,   0,   0,   0,0.002,0.004,0.0025,0.0012,     0,-0.0012,-0.0007, 0, 0, 0,  0,  0,  0,  0,  0,  0}});
  }
}
