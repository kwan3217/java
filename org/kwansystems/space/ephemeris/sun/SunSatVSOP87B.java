package org.kwansystems.space.ephemeris.sun;

import java.io.*;
import static org.kwansystems.space.ephemeris.sun.SunSatVSOP87.*;

/**
 * Calculates the VSOP87 heliocentric spherical series in the VSOP87
 * implementation of J2000Ecl
 */
public class SunSatVSOP87B extends SunSatVSOP87Sph {
  public SunSatVSOP87B(String LSuffix, double Lprec) {
  	super('B',LSuffix,Lprec);
  }
  public SunSatVSOP87B(String LSuffix) {
	  this(LSuffix,0);
  }
  public SunSatVSOP87B(int LWorld, double Lprec) {
	  super('B',LWorld,Lprec);
  }
  public SunSatVSOP87B(int LWorld) {
	  this(LWorld,0);
  }
  public static void main(String[] args) throws IOException, ClassNotFoundException {
    for(int w=1;w<=8;w++) {
      SunSatVSOP87B V = new SunSatVSOP87B(w);
      double[][] testCase=V.LoadTestCase();
//      V.checkSKtoABC();
      for (int i = 0; i < testCase.length; i++) {
        System.out.printf("%-9s JD%9.1f\n",worldName[2][w],testCase[i][0]*365250.0+2451545.0);
        V.CheckVariables(testCase, i);
      }
    }
  }
}
