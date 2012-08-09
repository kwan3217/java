package org.kwansystems.tools.vector;

import org.kwansystems.tools.time.Time;

/**
 * An object representing a position, velocity, and time.
 */
public class MathStateTime {
  public MathState S;
  public Time T;

  public MathStateTime(double[] rvo, Time time) {
    this(new MathState(rvo),time);
  }
  public MathStateTime(MathState LS, Time LT) {
    S=new MathState(LS);
    T=new Time(LT);
  }
  public MathStateTime(MathVector LR, MathVector LV, Time LT) {
    S=new MathState(LR,LV);
    T=new Time(LT);
  }
  public MathStateTime(MathVector LRV, Time LT) {
    S=new MathState(LRV.subVector(0, 3),LRV.subVector(3,3));
    T=new Time(LT);
  }
  public MathStateTime(double LX, double LY, double LZ, double LVX, double LVY, double LVZ, Time LT) {
    S=new MathState(LX,LY,LZ,LVX,LVY,LVZ);
    T=LT;
  }
  public MathStateTime(MathStateTime LST) {
    S=new MathState(LST.S);
    T=new Time(LST.T);
  }
  public String toString() {
    return "T: "+T.toString()+"\n"+S.toString();
  }
}
