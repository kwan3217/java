package org.kwansystems.space.ephemeris.sun;

import java.io.*;
import static org.kwansystems.space.ephemeris.sun.SunSatVSOP87.*;

/**
 * Calculates the VSOP87 barycentric rectangluar series in the VSOP87
 * implementation of J2000Ecl
 */
public class SunSatVSOP87E extends SunSatVSOP87Rect {
  public SunSatVSOP87E(String LSuffix, double Lprec) {
  	super('E',LSuffix,Lprec);
  }
  public SunSatVSOP87E(String LSuffix) {
	  this(LSuffix,0);
  }
  public SunSatVSOP87E(int LWorld, double Lprec) {
	  super('E',LWorld,Lprec);
  }
  public SunSatVSOP87E(int LWorld) {
	  this(LWorld,0);
  }
  public static void main(String[] args) throws IOException, ClassNotFoundException {
    for(int w=1;w<=9;w++) {
      SunSatVSOP87E V = new SunSatVSOP87E(w);
      double[][] testCase=V.LoadTestCase();
//      V.checkSKtoABC();
      for (int i = 0; i < testCase.length; i++) {
        System.out.printf("%-9s JD%9.1f\n",worldName[5][w],testCase[i][0]*365250.0+2451545.0);
        V.CheckVariables(testCase, i);
      }
    }
  }
}