package org.kwansystems.space.ephemeris.earth;

import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeEpoch;
import org.kwansystems.tools.time.TimeScale;
import org.kwansystems.tools.time.TimeUnits;
import org.kwansystems.tools.vector.*;
import org.kwansystems.space.ephemeris.earth.sgp4Vallado.*;

import static org.kwansystems.tools.time.Time.*;

public class AimSGP4 extends EarthSatVSGP4 {

  public AimSGP4(String Line1, String Line2) {
    super(Line1, Line2);
  }
  public AimSGP4() {
 /*   this("1 54321U AIM 0B   07172.00000000 -.00001034  00000-0 -11171-3 0 00009",
         "2 54321 097.8290 265.7955 0010597 271.6972 088.2949 14.88385621000160");*/
/*    
    this("1 54321U          07172.00000000  .00004625  00000-0  46249-3 0 00000",
         "2 54321 097.7757 265.4627 0015760 156.1017 153.8629 14.91458070005526");
         */
//  this("1 25544U 98067A   06185.66724689  .00020000  00000-0  20000-3 0  9018",
//       "2 25544  51.6312  97.4521 0010608  36.3479 323.8404 15.76078715 35925");
//    this("1 31304U 07015A   07115.89419231 -.00000057  00000-0  00000-0 0    11",
//         "2 31304 097.7911 213.9042 0011074 300.8450 059.1679 14.91828969    16");
//    this("1 31304U 07015A   07117.23590516  .00001438  00000-0  14898-3 0    58",
//         "2 31304 097.7927 215.2098 0010366 296.3621 063.6532 14.91550382   212");
    this("1 31304U 07015A   16050.92362986 +.00004206 +00000-0 +26057-3 0  9996",
         "2 31304 097.9505 202.8879 0005137 353.8577 006.2587 15.10351428481942");


      
  }
  public static void main(String[] args) {
    AimSGP4 A=new AimSGP4();
    System.out.println(A);
    Time T=new Time(2007,4,25,21,27,38.215,TimeUnits.Days,TimeScale.UTC,TimeEpoch.MJD);
    System.out.println(T);
    System.out.println("  Date MJD "+(T.get()));
    MathState S=A.getState(T);
    System.out.println(S);
  }

}
