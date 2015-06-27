package org.kwansystems.space.ephemeris.sun;

import java.io.*;
import static org.kwansystems.space.ephemeris.sun.SunSatVSOP87.*;

/**
 * Calculates the VSOP87 heliocentric rectangluar series in the VSOP87
 * implementation of J2000Ecl 
 */
public class SunSatVSOP87A extends SunSatVSOP87Rect {
  public SunSatVSOP87A(String LSuffix, double Lprec) {
  	super('A',LSuffix,Lprec);
  }
  public SunSatVSOP87A(String LSuffix) {
	  this(LSuffix,0);
  }
  public SunSatVSOP87A(int LWorld, double Lprec) {
	  super('A',LWorld,Lprec);
  }
  public SunSatVSOP87A(int LWorld) {
	  this(LWorld,0);
  }
  public static void main(String[] args) throws IOException, ClassNotFoundException {
    for(int w=1;w<=9;w++) {
      SunSatVSOP87A V = new SunSatVSOP87A(w);
      double[][] testCase=V.LoadTestCase();
//      V.checkSKtoABC();
      for (int i = 0; i < testCase.length; i++) {
        System.out.printf("%-9s JD%9.1f\n",worldName[1][w],testCase[i][0]*365250.0+2451545.0);
        V.CheckVariables(testCase, i);
      }
    }
  }
}
