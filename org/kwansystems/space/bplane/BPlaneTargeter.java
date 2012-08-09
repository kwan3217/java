package org.kwansystems.space.bplane;

import org.kwansystems.space.ephemeris.*;
import org.kwansystems.space.ephemeris.earth.EarthSatELP2000;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.vector.*;

import static org.kwansystems.tools.time.TimeScale.*;
import static org.kwansystems.tools.time.TimeUnits.*;

public class BPlaneTargeter {
  public static void main(String[] args) {
    RailMover EarthBary=new SunSatSeries96EMB();
    RailMover Earth=new CentralBody(EarthSatELP2000.satArray,EarthSatELP2000.satMassRatio);
    Earth.setReference(EarthBary);
    RailMover Moon=new EarthSatELP2000();
    Moon.setReference(Earth);
    
    //Input arg is decimal formatted JDUTC
    double T;
    Time TT;
    if(args.length>0) {
      T=Double.parseDouble(args[0]);
    } else {
      T=2439276.8167841;
    }
    TT=new Time(T,Days,TDT);
    TT.Scale=TDT;
    System.out.println(TT);
    //Heliocentric Planet system barycenters

    //Write it all out
    System.out.println("Sun "+Sun.getState(TT));
    System.out.println("Earth "+Earth.getState(TT));
    System.out.println("Moon "+Moon.getState(TT));
  }
}
