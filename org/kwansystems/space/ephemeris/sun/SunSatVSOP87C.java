package org.kwansystems.space.ephemeris.sun;

import java.io.*;
import static org.kwansystems.space.ephemeris.sun.SunSatVSOP87.*;


/**
 * Calculates the VSOP87 heliocentric rectangluar series in the VSOP87
 * implementation of the ecliptic and equinox of date
 */
public class SunSatVSOP87C extends SunSatVSOP87Rect {
  public SunSatVSOP87C(String LSuffix, double Lprec) {
  	super('C',LSuffix,Lprec);
  }
  public SunSatVSOP87C(String LSuffix) {
	  this(LSuffix,0);
  }
  public SunSatVSOP87C(int LWorld, double Lprec) {
	  super('C',LWorld,Lprec);
  }
  public SunSatVSOP87C(int LWorld) {
	  this(LWorld,0);
  }
  public static void main(String[] args) throws IOException, ClassNotFoundException {
    for(int w=1;w<=8;w++) {
      SunSatVSOP87C V = new SunSatVSOP87C(w);
      double[][] testCase=V.LoadTestCase();
//      V.checkSKtoABC();
      for (int i = 0; i < testCase.length; i++) {
        System.out.printf("%-9s JD%9.1f\n",worldName[3][w],testCase[i][0]*365250.0+2451545.0);
        V.CheckVariables(testCase, i);
      }
    }
  }
}
