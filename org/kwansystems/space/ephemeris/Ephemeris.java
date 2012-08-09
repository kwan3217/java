package org.kwansystems.space.ephemeris;

import java.util.*;
import java.awt.Graphics;
import java.io.*;

import org.kwansystems.tools.*;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.rotation.MathMatrix;
import org.kwansystems.tools.time.*;
import org.kwansystems.tools.zoetrope.*;
import org.kwansystems.tools.vector.*;

/**Position and velocity model as a function of time only. Take a Time object as input,
   return a MathState in m and m/s, aligned to ICRF 
   centered on central body center of mass
  <p>
  Aah, the ephemeris. A triumph of smart tables and stupid code.
   <p>
   Each implementation should set up an array of Sats,
  of which the zeroth element is null, representing the central body.
  <pre>
public static Ephemeris[] satArray;
</pre>
Each implementation should set up a sat mass ratio, representing the satellite
masses in any consistent set of units, with element zero being the central body.
public static double[] satMassRatio;
</pre>

  Either of these are optional. satMassRatio will frequently be set to one of these.
  <pre>
public static double[] satMass;
public static double[] satGM;
  </pre>
  This is optional.
<pre>
public static double[] satRadius;
</pre>
 */

public abstract class Ephemeris  {
  private Ephemeris Reference;
  private MathState CacheState;
  private long CacheTime;
  private MathState relCacheState;
  private long relCacheTime;
  private double DeltaT;
  protected RotatorEphemeris NaturalToFK5;
  public Ephemeris(Ephemeris LReference) {
    Reference=LReference;
    CacheTime=0;
    DeltaT=1.0;
  }
  public Ephemeris() {
    this(null);
  }
  public void setReference(Ephemeris LReference) {
    Reference=LReference;
  }
  public Ephemeris getReference() {
    return Reference;
  }
  private void ReferCacheState(Time T) {
    if(NaturalToFK5!=null) {
      CacheState=NaturalToFK5.CalcRotation(T).transform(CacheState);
    }
    if(Reference!=null) {
      CacheState=new MathState(MathVector.add(CacheState,Reference.getState(T)));
    }
  }
  private void UpdateCache(Time T) {
  relUpdateCache(T);
    if(T.getTime()!=CacheTime) {
      CacheState=CalcState(T);
      CacheTime=T.getTime();
      ReferCacheState(T);
    }
  }
  private void relUpdateCache(Time T) {
    if(T.getTime()!=relCacheTime) {
      relCacheState=CalcState(T);
      relCacheTime=T.getTime();
    }
  }
  public MathState getState(Time T) {
    UpdateCache(T);
    return CacheState;
  }
  public MathState relGetState(Time T) {
    relUpdateCache(T);
    return relCacheState;
  }
  public MathStateTime getStateTime(Time T) {
    return new MathStateTime(getState(T),T);
  }
  public static MathMatrix RTN(MathState RV) {
    MathVector R=RV.R().normal();
    MathVector V=RV.V().normal();
    MathVector N=MathVector.cross(V,R).normal();
    MathVector T=MathVector.cross(R,N).normal();
    return new MathMatrix(new MathVector[] {R,T,N},true);
  }
  /** If the ephemeris doesn't come with a velocity
   * calculator, just override this and the velocity
   * will be calculated by finite difference.
   */
  protected abstract MathVector CalcPos(Time TT);
  /** If the ephemeris comes apart cleanly into a position
   * and velocity function, override both this and
   * CalcPos.
   */
  protected MathVector CalcVel(Time TT) {
  	MathVector R1=CalcPos(TT);
	  return CalcVel(TT,R1);
  }
  protected MathVector CalcVel(Time TT, MathVector R1) {
    MathVector R0=CalcPos(Time.add(TT,-DeltaT,TimeUnits.Seconds));
    MathVector R2=CalcPos(Time.add(TT,+DeltaT,TimeUnits.Seconds));
    MathVector DR1=MathVector.sub(R1,R0);
    MathVector DR2=MathVector.sub(R2,R1);
    DR1=DR1.mul(0.5/DeltaT);
    DR2=DR2.mul(0.5/DeltaT);
    return MathVector.add(DR1,DR2);
  }
  protected MathVector defaultCalcPos(Time TT) {
	  return CalcState(TT).R();
  }
  protected MathVector defaultCalcVel(Time TT) {
	  return CalcState(TT).V();
  }
  /**If the ephemeris does not come apart cleanly, override
   * this, and make a CalcPos which just calls defaultCalcPos and
   * a CalcVel which just calls defaultCalcVel 
   */
  protected MathState CalcState(Time TT) {
    MathVector R1=CalcPos(TT);
    MathVector V1=CalcVel(TT,R1);
    return new MathState(R1,V1);
  }
  protected MathStateTime CalcStateTime(Time TT) {
    return new MathStateTime(CalcState(TT),TT);
  }
  public String TheoryName="Unnamed Theory";
  public String CenterName="Unnamed Center";
  public String SiteName="BODYCENTRIC";
  public String TargetName="Unnamed Target";
  public int TargetNumber=0;
  public int CenterNumber=0;
  
  public void printTheoryHeader(PrintStream out, Time T0, Time T1, int steps) throws IOException {
    out.println("*******************************************************************************");
    out.println("  Ephemeris / Local "+new Date().toString()+"  Superior, CO USA   / Kwanscope  ");
    out.println("*******************************************************************************");
    out.println("Target body name: "+TargetName+" ("+TargetNumber+")  {source: "+TheoryName+"}");
    out.println("Center body name: "+CenterName+" ("+CenterNumber+")");
    out.println("Center-site name: BODYCENTRIC");
    out.println("*******************************************************************************");
    out.println("Start time      : "+T0.toStringHorizons());
    out.println("Stop  time      : "+T1.toStringHorizons());
    out.println("Step-size       : "+steps+" steps ("+(steps+1)+" data points)");
    out.println("*******************************************************************************");
    out.println("Output units    : KM-S ");
    out.println("Output format   : 02");
    out.println("Reference frame : ICRF/J2000");
    out.println("Output type     : GEOMETRIC cartesian states");
    out.println("Coordinate systm: Ecliptic and Mean Equinox of Reference Epoch");
    out.println("*******************************************************************************");
    out.println("JDTDB ,Date, X, Y, Z, VX, VY, VZ");
    out.println("*******************************************************************************");
    out.println("$$SOE");
  }
  public void printTheory(PrintStream out, Time T0, Time T1, double stepsize) throws IOException {
    int steps=((int)((T1.get(T0.Units,T0.Scale,T0.Epoch)-T0.get())/stepsize))+1;
    printTheoryHeader(out,T0,T1,steps);
    for(int i=0;i<steps;i++) {
      Time T=Time.add(T0, i*stepsize);
      MathState RV=getState(T);
      out.print(T.toStringHorizons());
      for(int j=0;j<RV.dimension();j++) out.printf(",%23.15E",RV.get(j)/1000.0);
      out.println(",");
    }
    printTheoryFooter(out);
  }
  public void printTheory(Time T0, Time T1, double stepsize) throws IOException {
    printTheory(System.out,T0,T1,stepsize); 
  }
  public void printTheory(PrintStream out, double stepsize) throws IOException {
    Time T0=new Time(1990, 1, 1,0,0,0,TimeUnits.Days,TimeScale.TDB,TimeEpoch.JD);
    Time T1=new Time(1990,12,31,0,0,0,TimeUnits.Days,TimeScale.TDB,TimeEpoch.JD);
    printTheory(System.out,T0,T1,stepsize); 
  }
  public void printTheoryFooter(PrintStream out) throws IOException {
    out.println("$$EOE");
    out.println("*******************************************************************************");
    out.println("Coordinate system description:");
    out.println("");
    out.println("  J2000");
    out.println("");
    out.println("    Reference epoch: J2000.0 (JDTDB 2451545.0)");
    out.println("    Reference plane: ICRF/J2000.0");
    out.println("    xy-plane: Plane of the Earth's orbit at the reference epoch");
    out.print("    x-axis  : ");
    out.println("out along the ascending node of instantaneous plane of the Earth's");
    out.println("              orbit and the Earth's mean equator at the reference epoch");
    out.println("    z-axis  : Perpendicular to the xy-plane in the directional (+ or -) sense");
    out.println("              of Earth's north pole at the reference epoch.");
    out.println("    y-axis  : completes a right-handed coordinate system");
    out.println("");
    out.println("Symbol meaning  ");
    out.println("");
    out.println("    JDTDB  Epoch Julian Date, Coordinate Time (TDB)");
    out.println("      X      x-component of position vector (km)                               ");
    out.println("      Y      y-component of position vector (km)                               ");
    out.println("      Z      z-component of position vector (km)                               ");
    out.println("      VX     x-component of velocity vector (km/s)                             ");
    out.println("      VY     y-component of velocity vector (km/s)                             ");
    out.println("      VZ     z-component of velocity vector (km/s)                             ");
    out.println("");
    out.println("Geometric states/elements have no aberration corrections applied.");
    out.println("");
    out.println(" Computations by ...");
    out.println("     "+this.getClass().getName()+" Java Ephemeris Calculator");
    out.println("     1995 E Coalton Rd #45-102, Kwan Astrodynamics (A division of Kwan Systems)");
    out.println("     Superior, CO 80027  USA");
    out.println("     Information: http://www.kwansystems.org/astro/");
    out.println("     Author     : chrisj@kwansystems.org");
    out.println("*******************************************************************************  ");
  }
  /**
   * @return the naturalToFK5
   */
  public RotatorEphemeris getNaturalToFK5() {
    return NaturalToFK5;
  }
  public static MathStateTime[] readHorizonsFile(String infn) throws IOException {
    List<MathStateTime> a=new ArrayList<MathStateTime>();
    LineNumberReader inf=new LineNumberReader(new FileReader(infn));

    String line=inf.readLine();
    while(!line.trim().equals("$$SOE")) {
      line=inf.readLine();
    }
    line=inf.readLine();
    while(line!=null && line.trim().length()>0 && line.charAt(0)!='$') {
      line=line.trim();
      String[] parts=line.split(",");
      Time T=new Time(Double.parseDouble(parts[0].trim()),TimeUnits.Days,TimeScale.TDB,TimeEpoch.JD);
      MathState S=new MathState(
          1000.0*Double.parseDouble(parts[2].trim()),
          1000.0*Double.parseDouble(parts[3].trim()),
          1000.0*Double.parseDouble(parts[4].trim()),
          1000.0*Double.parseDouble(parts[5].trim()),
          1000.0*Double.parseDouble(parts[6].trim()),
          1000.0*Double.parseDouble(parts[7].trim())
      );
      MathStateTime ST=new MathStateTime(S,T);
      a.add(ST);
      line=inf.readLine();
    }
    inf.close();
    return a.toArray(new MathStateTime[]{});
  }
  public MathStateTime[] duplicateHorizonsFile(MathStateTime[] Horizons, int first,int length) {
    MathStateTime[] result=new MathStateTime[length];
    
    for(int i=0;i<length;i++) {
      result[i]=getStateTime(Horizons[i+first].T);
    }
    
    return result;
  }
  public void testHorizons(String infn, int first,int length) throws IOException {
    MathStateTime[] Horizons=readHorizonsFile(infn);
    MathStateTime[] ThisTheory=duplicateHorizonsFile(Horizons,first,length);
    ChartRecorder C=new ArrayListChartRecorder();
    double t0=ThisTheory[0].T.get(TimeUnits.Seconds,TimeScale.TDB,TimeEpoch.Java);
    for(int i=0;i<ThisTheory.length;i++) {
      double t=ThisTheory[i].T.get(TimeUnits.Seconds,TimeScale.TDB,TimeEpoch.Java)-t0;;
      C.Record(t, "Horizons", "m,s",Horizons[i]);
      C.Record(t, TheoryName, "m,s",ThisTheory[i]);
      double rHorizons=Horizons[i].S.R().length();
      double rThisTheory=ThisTheory[i].S.R().length();
      double rDiff=rHorizons-rThisTheory;
      MathVector N=MathVector.cross(Horizons[i].S.R(),Horizons[i].S.V());
      N.normalEq();
      double nThisTheory=ThisTheory[i].S.R().Comp(N);
      MathVector TThisTheory=ThisTheory[i].S.R().ProjPerp(N);
      C.Record(t,"R difference","m",rDiff);
      C.Record(t,"N difference","m",nThisTheory);
      C.Record(t,"T difference","m",MathVector.sub(TThisTheory, Horizons[i].S.R()).length());
    }
    C.PrintSubTable(new String[] {"R difference","N difference","T difference"}, new DisplayPrinter());
    
  }
}
