package org.kwansystems.space.ephemeris.earth.sgp4Vallado;

import java.awt.Graphics;

import org.kwansystems.space.ephemeris.*;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeEpoch;
import org.kwansystems.tools.time.TimeScale;
import org.kwansystems.tools.time.TimeUnits;
import org.kwansystems.tools.vector.*;
import org.kwansystems.tools.zoetrope.*;
import org.kwansystems.space.planet.*;

import java.util.*;

/**Vallado SGP4 wrapper class - calls SGP4Core to actually do the calculations
 * This class coordinates reading the two lines and provides the interface
 * as an Ephemeris class.
 */
public class EarthSatVSGP4 extends Ephemeris implements Comparable<EarthSatVSGP4> {
  public final Time epoch;
  public final TwoLineElement TLE;
  private SGP4Core S;
  public String toString() {
    return TLE.toString();
  }
  public EarthSatVSGP4(TwoLineElement LTLE) {
    super();
    TLE=LTLE;
    S=new SGP4Core(TLE);
    epoch=TLE.epoch;
  }
  public EarthSatVSGP4(String Line1, String Line2, SGP4Core.gravconsttype whichconst) {
    this(new TwoLineElement(Line1,Line2,whichconst));
  }
  public EarthSatVSGP4(String Line1, String Line2) {
    this(new TwoLineElement(Line1,Line2));
  }
  /** If the ephemeris doesn't come with a velocity
   * calculator, just override this and the velocity
   * will be calculated by finite difference.
   */
  public MathVector CalcPos(Time TT) {
    return defaultCalcPos(TT);
  }
  /** If the ephemeris comes apart cleanly into a position
   * and velocity function, override both this and
   * CalcPos.
   */
  public MathVector CalcVel(Time TT) {
    return defaultCalcVel(TT);
  }
  public MathState CalcState(Time TT) {
    double deltatmin=(TT.get(TimeUnits.Minutes,TimeScale.UTC,TimeEpoch.Java)-TLE.epoch.get(TimeUnits.Minutes,TimeScale.UTC,TimeEpoch.Java));
    double[] ro=new double[3], vo=new double[3];
    S.sgp4(deltatmin, ro, vo);
    for(int i=0;i<3;i++) {
      ro[i]*=1000;
      vo[i]*=1000;
    }
    return new MathState(new MathVector(ro), new MathVector(vo));
  }
  public static void main(String[] args) {

    EarthSatVSGP4 E=new EarthSatVSGP4("1 00128U          09238.27320321  .00020000  00000-0  20000-3 0  9002",
                                      "2 00128  51.6390 307.1493 0005999 350.9190   9.1921 16.85008553    17");
    
    Time T=E.epoch;
    final MathStateTime[] ST=new MathStateTime[180];
    ChartRecorder C=new ArrayListChartRecorder();
    double T0=T.get(TimeUnits.Seconds);
    for(int i=0;i<180;i++) {
      ST[i]=E.getStateTime(T);
      MathState xyz=E.CalcState(T);
      MathVector lla=Planet.Earth.S.xyz2lla(xyz.R());
      C.Record(T.get(TimeUnits.Seconds)-T0, "Alt","m",lla.Z());
      T.add(1);
    }
    C.PrintTable(new DisplayPrinter());
    System.out.println(E);
    System.out.println(T);
    MathState xyz=E.CalcState(T);
    System.out.println(xyz);
    System.out.println(Planet.Earth.S.xyz2lla(xyz.R()));
    Zoetrope Z=new SGP4Zoetrope("SGP4", 100, ST);
    Z.setVisible(true);
    Z.start();
  }
  public int compareTo(EarthSatVSGP4 o) {
    return epoch.compareTo(o.epoch);
  }
}

class SGP4Zoetrope extends Zoetrope {
  private static final long serialVersionUID = 1457222903388635124L;
  protected MathStateTime[] ST;
  public SGP4Zoetrope(String LWindowTitle, int LFramePeriodMs, MathStateTime[] LST) {
    super(LWindowTitle,LFramePeriodMs);
    setElements(LST);
  }
  protected void FigureOutStuff() {
    for(MathStateTime E:ST) {
      double thisRmax=E.S.R().length();
      if(thisRmax>getRmax())setRmax(thisRmax);
    }
    setRmax(getRmax() * 1.1);
  }
  public void setElements(MathStateTime[] LST) {
    ST=LST;
    setNumFrames(ST.length);
    FigureOutStuff();
  }
  protected int X(MathStateTime thisST) {
    return X(thisST.S.R().X());
  }
  protected int Y(MathStateTime thisST) {
    return Y(thisST.S.R().Z());
  }
  protected void paintFrame(Graphics G) {
    //Draw all previous actual locations
    G.setColor(colors[0]);
    for (int i=0;i<=CurrentFrame;i++) {
      int thisX=X(ST[i]);
      int thisY=Y(ST[i]);
      G.drawLine(thisX-2,thisY-2,thisX+2,thisY+2);
      G.drawLine(thisX-2,thisY+2,thisX+2,thisY-2);
    }
  }
}
