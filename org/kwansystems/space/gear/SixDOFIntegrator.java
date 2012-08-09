package org.kwansystems.space.gear;

import org.kwansystems.tools.integrator.*;
import org.kwansystems.tools.vector.MathVector;

public class SixDOFIntegrator extends EmbeddedRungeKutta {
  public SixDOFIntegrator(double StartT, MathVector LX, double LdT, DerivativeSet LD) {
    super(StartT, LX, LdT, LD, 
          new int[] {0,1,2,3,4,5,6,7,8,9,10,11,12},
          new MathVector(new double[] {
              0.01,0.01,0.01,
              0.0001,0.0001,0.0001,
              1e-6,1e-6,1e-6,1e-6,
              1e-7,1e-7,1e-7}
          ));
  }

}
