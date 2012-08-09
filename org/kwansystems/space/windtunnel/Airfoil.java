/*
 * Airfoil.java
 *
 * Created on September 26, 2004, 9:32 AM
 */

package org.kwansystems.space.windtunnel;

import org.kwansystems.tools.vector.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.space.planet.*;
import org.kwansystems.space.planet.atmosphere.*;

public abstract class Airfoil extends ForceMomentGenerator {
  public static final int CL=0;
  public static final int CDE=1;
  public static final int CM=2;
  MathVector Normal;
  MathVector CoL;
  MathVector ChordLine;
  MathVector Perp;
  MathMatrix VesselToWing;
  private double Chord; 
  private double SurfaceArea; 
  private double Aspect;
  private double Wingspan;
  private double Efficiency;
  private static final double M1=0.75;
  private static final double M2=1;
  private static final double M3=1.25;
  private static final double cmax=0.5;
  public Airfoil(MathVector LNormal, MathVector LChordLine, MathVector lCoL,double c, double span, double e) {
    Normal=LNormal.normal();
    ChordLine=LChordLine.normal();
    Perp=MathVector.cross(Normal,ChordLine);
    VesselToWing=MathMatrix.rowMatrix(ChordLine,Perp,Normal);
    CoL=lCoL;
    Chord=c;
    Wingspan=span;
    SurfaceArea=c*span; 
    Aspect=span/c;
    Efficiency=e;
  }
  public double oapiGetWaveDrag(double M) {
    if(M<M1) {
      return 0;
    } else if(M<M2) {
      return cmax*(M-M1)/(M2-M1);
    } else if(M<M3) {
      return cmax;
    } else {
      return cmax*Math.sqrt(M3*M3-1)/Math.sqrt(M*M-1);
    }
  }
  public double oapiGetInducedDrag(double cl) {
    return cl*cl/(Math.PI*Aspect*Efficiency);
  }
  public abstract double[] F(double aoa, double M, double Re); 
  
  public ForceMoment getForceMoment(Planet E, MathState X) {
    MathVector RelWind=X.V();
    double Alt=X.R().length()-6378137;
    AirProperties A=E.Atm.calcProps(Alt);
    return getForceMoment(A.Density,RelWind.length()/A.VSound,A.Viscosity,RelWind);
  }
  public ForceMoment getForceMoment(double rho, double Mach, double mu, MathVector RelWind) {
    ForceMoment FM=new ForceMoment();
    FM.Aspect=Aspect;
    FM.FoilChord=Chord;
    FM.FoilSpan=Wingspan;
    FM.FoilArea=SurfaceArea;
    FM.Efficiency=Efficiency;
    FM.Mach=Mach;
    FM.rho=rho;
    MathVector RelWindNormal=RelWind.normal();
    double RelSpd=RelWind.length();
    FM.Re=rho*RelSpd*Chord/mu;
    
    MathVector WingRelWindNormal=VesselToWing.Transform(RelWindNormal);
    double alpha=Math.atan2(WingRelWindNormal.Z(),WingRelWindNormal.X());
    
    double[] c=F(alpha,Mach,FM.Re);
    FM.cL=c[CL];
    FM.cDe=c[CDE];
    FM.cDi=oapiGetInducedDrag(FM.cL);
    FM.cDw=oapiGetWaveDrag(Mach);
    FM.cM=c[CM];
    FM.q=rho*RelSpd*RelSpd/2;
    
    double Lift=c[CL]*FM.q*SurfaceArea;
    FM.cD=FM.cDe+FM.cDi+FM.cDw;
    double Drag=FM.cD*FM.q*SurfaceArea;
    double Moment=FM.cM*FM.q*SurfaceArea*Chord;
    
    FM.Lift=MathVector.mul(Normal,Lift); 
    FM.Drag=MathVector.mul(ChordLine,-Drag); 
    FM.Force=MathVector.add(FM.Lift,FM.Drag); 
    FM.MomentF=MathVector.cross(CoL,FM.Force); //Moment due to off-axis force
    FM.MomentL=MathVector.mul(Perp,Moment);
    FM.Moment=MathVector.add(FM.MomentF,FM.MomentL);
    
    return FM;
    
  }
  public static void main(String[] args) {
    Planet E=new Earth();
    Airfoil A=new PegasusXLWing(new MathVector(0,0,1),new MathVector(1,0,0),new MathVector(0,0,0));
    MathState X=new MathState(0,0,6378137,-100,0,0);
    A.getForceMoment(E,X);
  }
}
