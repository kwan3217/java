package org.kwansystems.space.asen5050;

import org.kwansystems.space.kepler.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeUnits;
import org.kwansystems.tools.vector.*;
public class HW4 {
  public static void main(String args[]) {
    System.out.println("Problem 1)");
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
    System.out.println("Problem 2)");
    System.out.println("Test Case: ");
    E.A=36126.64283;
    E.E=0.83284;
    E.I=Math.toRadians(87.870);
    E.LAN=Math.toRadians(227.89);
    E.AP=Math.toRadians(53.58);
    E.TA=Math.toRadians(92.336);
    System.out.println(E.EleToPosVel());
    System.out.println("Problem Case: ");
    E.parseTwoLine("1 25544U 98067A 01260.91843750 .00059354 00000-0 74277-3 0 4795",
                   "2 25544 51.6396 342.1053 0008148 106.9025 231.8021 15.59182721161549");
    System.out.println(E.toString());
    System.out.println(E.EleToPosVel());
    System.out.println("Problem 3)");
    E.M=E.M+E.N*3600; //Advance 1 hour
    E.MeanToTrue();
    System.out.println(E.toString());
    System.out.println(E.EleToPosVel());
  }
}
