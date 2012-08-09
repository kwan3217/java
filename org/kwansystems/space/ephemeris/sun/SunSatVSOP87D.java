package org.kwansystems.space.ephemeris.sun;

import java.io.*;
import static org.kwansystems.space.ephemeris.sun.SunSatVSOP87.*;

/**
 * Calculates the VSOP87 heliocentric rectangluar series in the VSOP87
 * implementation of J2000Ecl
 */
public class SunSatVSOP87D extends SunSatVSOP87Sph {
  public SunSatVSOP87D(String LSuffix, double Lprec) {
  	super('D',LSuffix,Lprec);
  }
  public SunSatVSOP87D(String LSuffix) {
	  this(LSuffix,0);
  }
  public SunSatVSOP87D(int LWorld, double Lprec) {
	  super('D',LWorld,Lprec);
  }
  public SunSatVSOP87D(int LWorld) {
	  this(LWorld,0);
  }
  public static void main(String[] args) throws IOException, ClassNotFoundException {
    SunSatVSOP87D V=new SunSatVSOP87D("ear");
    V.dumpIDL("Data/SunSatVSOP87.ear.pro");
    double[][] result=new double[1000][];
    long t1=System.currentTimeMillis();
    for(int i=0;i<1000;i++) {
      result[i]=V.CalcVariables(((double)i)/1e6-1e3);
    }
    long t2=System.currentTimeMillis();
    System.out.println(t2-t1);
  }
}
