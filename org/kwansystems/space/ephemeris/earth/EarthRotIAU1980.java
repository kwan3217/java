package org.kwansystems.space.ephemeris.earth;

import org.kwansystems.space.*;
import org.kwansystems.space.planet.*;
import org.kwansystems.space.ephemeris.*;
import org.kwansystems.tools.time.*;
import org.kwansystems.tools.vector.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.chart.*;
import static org.kwansystems.space.ephemeris.RotatorEphemeris.Frame.*;

public class EarthRotIAU1980 extends GraphRotatorEphemeris implements PlanetRotatorEphemeris {
  public static final RotatorEphemeris J2000FK52Ecl=new ConstRotatorEphemeris(Constants.J2000Equ2Ecl);
  public EarthRotIAU1980() {
    super();
    addEdge(new EarthRotGMST(PEF,TEME));
    addEdge(new EarthRotEqEquinox(TEME,TOD));
    addEdge(new EarthRotNutationIAU1980(TOD,MOD));
    addEdge(new EarthRotPrecessionIAU1976(MOD,J2000Equ));
  }
  public EarthRotIAU1980(Frame Lfrom, Frame Lto) {
      this();
      set(Lfrom,Lto);
  }
  public static void main(String args[]) {
    EarthRotPrecessionIAU1976 P=new EarthRotPrecessionIAU1976(MOD,J2000Equ);
    EarthRotNutationIAU1980 N=new EarthRotNutationIAU1980(TOD,MOD);
    Time T1990=new Time(1990,1,1, 0,0,0,0,TimeUnits.Centuries,TimeScale.TDB,TimeEpoch.J2000);
    MathMatrix RTestOut1990=new MathMatrix(new double[][] {
      {+0.99999717,+0.00218346,+0.00094891},
      {-0.00218343,+0.99999762,-0.00003207},
      {-0.00094898,+0.00003000,+0.99999955}
    });
    MathMatrix R1990_PN=MathMatrix.mul(P.CalcRotation(T1990),N.CalcRotation(T1990));
    //Not good in the last place, but close enough. I think the Almanac matrix is wrong, or 
    //perhaps uses a different model. Off by about 8+-2 mas
    System.out.println("Test matrix: ");
    System.out.println(RTestOut1990.toString("%11.8f"));
    System.out.println("Calc matrix: ");
    System.out.println(R1990_PN.T().toString("%11.8f"));
    System.out.println("Difference: ");
    System.out.println(MathMatrix.sub(R1990_PN.T(), RTestOut1990).toString("%11.8f"));
    System.out.println("Difference: ");
    System.out.println(new AxisAngle(MathMatrix.mul(R1990_PN, RTestOut1990)));
    EarthRotIAU1980 Chain=new EarthRotIAU1980(TOD,J2000Equ);
    MathMatrix R1990_Chain=new MathMatrix(Chain.getRotation(T1990));
    
    System.out.println("Test matrix: ");
    System.out.println(RTestOut1990.toString("%11.8f"));
    System.out.println("Calc matrix: ");
    System.out.println(R1990_Chain.T().toString("%11.8f"));
    System.out.println("Difference: ");
    System.out.println(MathMatrix.sub(R1990_Chain.T(), RTestOut1990).toString("%11.8f"));
    System.out.println("Difference: ");
    System.out.println(new AxisAngle(MathMatrix.mul(R1990_Chain, RTestOut1990)));
    
    ChartRecorder C=new ArrayListChartRecorder();
    EarthRotIAU1980 ChainJ2000_PEF=new EarthRotIAU1980(J2000Equ,PEF);
    for(int i=0;i<365;i++) {
      Time T=Time.add(T1990, 0.5+((double)i)/1.0,TimeUnits.Days);
      MathVector R=Planet.Planets[3].Orbit.getState(T).R().opp();
      C.Record(T.get(TimeUnits.Days)-T1990.get(TimeUnits.Days),"CelLat",Math.toDegrees(Math.atan2(R.Y(), R.X())));
      C.Record(T.get(TimeUnits.Days)-T1990.get(TimeUnits.Days),"CelLon",Math.toDegrees(Math.asin(R.Z()/R.length())));
      C.Record(T.get(TimeUnits.Days)-T1990.get(TimeUnits.Days),"R",R.normal());
      Rotator RR=ChainJ2000_PEF.CalcRotation(T);
      MathVector R2=RR.transform(R);
      C.Record(T.get(TimeUnits.Days)-T1990.get(TimeUnits.Days),"Date",T);
      C.Record(T.get(TimeUnits.Days)-T1990.get(TimeUnits.Days),"EoT",-Math.toDegrees(Math.atan2(R2.Y(), R2.X()))*4);
      C.Record(T.get(TimeUnits.Days)-T1990.get(TimeUnits.Days),"Lon",Math.toDegrees(Math.atan2(R2.Y(), R2.X())));
      C.Record(T.get(TimeUnits.Days)-T1990.get(TimeUnits.Days),"Lat",Math.toDegrees(Math.asin(R2.Z()/R2.length())));
      C.Record(T.get(TimeUnits.Days)-T1990.get(TimeUnits.Days),"RR",null,new MathMatrix(RR));
      C.Record(T.get(TimeUnits.Days)-T1990.get(TimeUnits.Days),"R2",null,R2.normal());
    }
    //Latitude should follow declination, -+23.5 on solstice
    //Longitude should follow equation of time, be close to +-4deg
    C.PrintTable(new HTMLPrinter("EarthRotIAU1980.html"));
    C.PrintSubTable(new String[] {"Lat","Lon","EoT"},new DisplayPrinter());
  }

    public double Theta(Time T) {
      return EarthRotGMST.ThetaGMST(T);
    }


}
