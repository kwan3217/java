package org.kwansystems.space.asen5050;

import org.kwansystems.tools.vector.*;

public class HW5 {
  public static void main(String args[]) {

    //Problem 1
    MathVector PosECI=new MathVector(-5634,-2645,2834);
    MathVector PosECEF=Frames.ECItoECEF(PosECI,Math.toRadians(102.75)); 
    System.out.println("Problem 1: Compute ECEF given ECI");
    System.out.println("ECI:  "+PosECI);
    System.out.println("ECEF: "+PosECEF);
    System.out.println("Compute lat, lon, and alt relative to a sphere of radius 6378km");
    System.out.println("Lat (deg): "+Math.toDegrees(Frames.ECEFtoLat(PosECEF)));
    System.out.println("Lon (deg): "+Math.toDegrees(Frames.ECEFtoLon(PosECEF)));
    System.out.println("Alt (km):  "+Frames.ECEFtoAlt(PosECEF));

    //Problem 2
    System.out.println("Problem 2: Compute ECEF given Lat, lon, Alt");
    MathVector PosAustinECEF=Frames.LLAtoECEF(Math.toRadians(30.28),Math.toRadians(262.26),0.100);
    System.out.println("PosAustin (ECEF): "+PosAustinECEF);
    MathVector PosAustinECI=Frames.ECEFtoECI(PosAustinECEF,Math.toRadians(90));
    System.out.println("PosAustin (ECI):  "+PosAustinECI);

    //Problem 3
    System.out.println("Problem 3: Compute az, el, and range from Boulder to given ECEF");
    MathVector PosBoulderECEF=Frames.LLAtoECEF(Math.toRadians(40.01),Math.toRadians(254.83),1.615);
    System.out.println("PosBoulder (ECEF): "+PosBoulderECEF);
    MathVector PosSatECEF=new MathVector(-1681,-5173,4405);
    MathVector PosSatSEZ=Frames.ECEFtoSEZ(PosSatECEF,PosBoulderECEF);
    System.out.println("PosSat (SEZ):    "+PosSatSEZ);
    System.out.println("Azimuth (deg):   "+Math.toDegrees(Frames.SEZtoAz(PosSatSEZ)));
    System.out.println("Elevation (deg): "+Math.toDegrees(Frames.SEZtoEl(PosSatSEZ)));
    System.out.println("Range (km):      "+Frames.SEZtoR(PosSatSEZ));
  }
}