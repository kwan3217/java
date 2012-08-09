package org.kwansystems.space.asen5070;

import org.kwansystems.space.ephemeris.*;
import org.kwansystems.tools.integrator.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.vector.*;

import java.util.*;

public class HW1P3 {
  public static void main(String args[]) {
    Integrator I=new RungeKutta(
      new Time(2002,9,4,0,0,0),
      new MathState(
        new MathVector(-2436.45,-2436.45,6891.0397),
        new MathVector(5.088611,-5.088611,0.0)
      ), 
      2,
      new SecondDerivative(3,new DerivativeSet[] {
        new MoverGravity(new FixedEphemeris(new MathVector(),null),398600.5)
      })
    );
    for(int i=0;i<6740;i++) {
      if((i % 10)==0) {
        MathState S=(MathState)I.getX();
        System.out.print(I.getT()+","+S.R().length()+","+S.V().length());
	try {
          System.out.print(","+I.LastDxDt.subVector(3,3).length());
	} catch(Throwable E) {;}
	System.out.println("");
      }
      I.step();
    }
  }
}
