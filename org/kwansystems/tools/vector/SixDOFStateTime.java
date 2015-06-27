package org.kwansystems.tools.vector;

import org.kwansystems.tools.time.Time;

/**
 * An object representing a 6DOF state vector, and time.
 */
public class SixDOFStateTime {
  public SixDOFState S;
  public Time T;
  public SixDOFStateTime(MathState LS, Time LT) {
    S=new SixDOFState(LS);
    T=LT;
  }
  public SixDOFStateTime(MathVector LR, MathVector LV, Time LT) {
    S=new SixDOFState(LR,LV);
    T=LT;
  }
  public String toString() {
    return "T: "+T.toString()+"\n"+S.toString();
  }
}
