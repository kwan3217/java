package org.kwansystems.space.asen5070;

import org.kwansystems.space.kepler.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.vector.*;
public class HW1P1 {
  public static void main(String args[]) {
    Elements E=new Elements();
    E.PosVelToEle(
      new MathStateTime(
        new MathState(
	        new MathVector(-2436.45,-2436.45,6891.0379),
          new MathVector(5.088611,-5.088611,0)
        ),
        new Time()
      ),
      398600.5,
      "km"
    );
    System.out.println(E);
  }
}
