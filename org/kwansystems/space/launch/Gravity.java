package org.kwansystems.space.launch;

import org.kwansystems.tools.integrator.*;
import org.kwansystems.tools.vector.*;

public class Gravity extends DerivativeSet {
  double GM;
  public Gravity(double LGM) {
    GM=LGM;
  }
  public MathVector dxdt(double T, MathVector X, boolean IsMajor) {
    //Accepts a MathVector of the form (X,Y,Z,Xdot,Ydot,Zdot)
    //Returns a MathVector of the form (Xdotdot,Ydotdot,Zdotdot)
    //Exclusively 3 dimensional
    MathVector R=new MathVector(new double[] {X.X(),X.Y(),X.Z()});
    return R.mul(-GM/Math.pow(R.length(),3));
  }
}
