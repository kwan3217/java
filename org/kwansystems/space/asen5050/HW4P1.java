package org.kwansystems.space.asen5050;

import org.kwansystems.space.kepler.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeUnits;
import org.kwansystems.tools.vector.*;
public class HW4P1 {
  public static void main(String args[]) {
    System.out.println("Test Case: ");
    Elements E=new Elements();
    E.PosVelToEle(
      new MathStateTime(
        new MathState(
	        new MathVector(6524.834,6862.875,6448.296),
          new MathVector(4.901327,5.533756,-1.976341)
        ),
        new Time(TimeUnits.Seconds)
      ),
      398600.4415,
      "km"
    );
    System.out.println(E);
    System.out.println("Problem case: ");
    E.PosVelToEle(
      new MathStateTime(
        new MathState(
	        new MathVector(-5633.9,-2644.9,2834.4),
          new MathVector(2.39805,-7.02301,-1.79578)
        ),
        new Time(TimeUnits.Seconds)
      ),
      398600.4415,
      "km"
    );
    System.out.println(E);
  }
}
