package org.kwansystems.space.launch;

import org.kwansystems.space.planet.*;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.table.*;
import org.kwansystems.tools.vector.*;

public class SaturnGuidance extends Guidance {
  double TargetAlt,BurnoutTime,TargetSpd;
  boolean Cutoff=false;
  Table FirstStagePitch;
  Table PitchOver;
  double GravityTurnStart,GravityTurnStop;
  double Azimuth=72*Math.PI/180.0;
  double TanTheta0=0;
  double RotSpeed=0;
  double FrameShiftTime=20;
  boolean StartIter=false;
  Resolver Resolv;
  public SaturnGuidance(double LTargetAlt,double LGravityTurnStart, double LGravityTurnStop,double LRotSpeed, Planet E, ArrayListChartRecorder C) {
    super(E,C);
    TargetAlt=LTargetAlt+E.R;
    TargetSpd=Math.sqrt(E.GM/TargetAlt);
    BurnoutTime=784.98; //Allow up to 100 seconds additional
    RotSpeed=LRotSpeed;
    GravityTurnStart=LGravityTurnStart;
    GravityTurnStop=LGravityTurnStop;
    PitchOver=new LinearTable(new double[] {  0,
                                             13.6,
                                             27},
                              new double[][] {{  0,
                                              0,
                                             -6.84}});
    Resolv=new Resolver(E);
  }
  public boolean CheckCutoff(double T,MathState X, boolean IsMajor) {
    if(Cutoff) {
      if(IsMajor)C.Record(T,"Guidance comment",null,"Engine already shut down.");
      return true;
    }
    if(T>BurnoutTime | X.V().length()>TargetSpd) {
      if(IsMajor)C.Record(T,"Guidance comment",null,"At target speed... cutting off engine.");
      Cutoff=true;
      return true;
    }
    return false;
  }
  public MathVector PitchoverGuidance(double T, MathState X, double AvailAcc,boolean IsMajor) {
    double Angle=(90.0+PitchOver.Interp(T,0))*Math.PI/180.0;
    double RadialAcc=Math.sin(Angle)*AvailAcc;
    double NorthAcc=Math.cos(Angle)*AvailAcc*Math.cos(Azimuth);
    double EastAcc=Math.cos(Angle)*AvailAcc*Math.sin(Azimuth);
    if(IsMajor)C.Record(T,"Radial Acceleration","km/s^2",RadialAcc);
    if(IsMajor)C.Record(T,"North Acceleration","km/s^2",NorthAcc);
    if(IsMajor)C.Record(T,"East Acceleration","km/s^2",EastAcc);
    return Resolv.ComposeRadialNorthEastI(X,RadialAcc,NorthAcc,EastAcc);
  }  
  public MathVector GravityTurnGuidance(double T,MathState X, double AvailAcc,boolean IsMajor) {
    //Gravity turn sets angle of attack to zero... acceleration in the same direction as Vrel
    MathVector R=X.R();
    MathVector Vabs=X.V();
    MathVector Vrel=MathVector.sub(Vabs,P.Wind(X));
    Vrel.normalize();
    Vabs.normalize();
    if(T>(GravityTurnStop-FrameShiftTime)) {
      double fac=(T-GravityTurnStop+FrameShiftTime)/FrameShiftTime;
      if (fac>1) fac=1;
      Vrel.mul(1-fac);
      Vabs.mul(fac);
      Vrel.add(Vabs);
    }
    Vrel.mul(AvailAcc);
    return Vrel;
  }
  public MathVector IterativeGuidance(double T, MathState X, double AvailAcc, boolean IsMajor) {
    //Linear guidance to get to target orbit
    double LowT=T-GravityTurnStop;
    if(IsMajor)C.Record(T,"LowT","s",LowT);
    double Theta0=Math.atan(TanTheta0);
    double Theta=Theta0+LowT*RotSpeed;
    double Radial=Math.cos(Theta);
    double Transverse=Math.sin(Theta);
    if(IsMajor)C.Record(T,"Theta","rad",Theta);
    if(IsMajor)C.Record(T,"TanTheta",null,Transverse/Radial);
    MathVector TV=Resolv.ComposeRadialTransverseI(X,Radial,Transverse);
    TV.mul(AvailAcc);
    return TV;
  }
  public MathVector ThrustVector(double T, MathState X,double AvailAcc,boolean IsMajor) {
    MathVector TV;
    MathVector Wind=P.Wind(X);
    MathVector Vrel=MathVector.sub(X.V(),Wind);
    if(IsMajor)C.Record(T,"Relative Velocity","km/s",Vrel);
    if(IsMajor)C.Record(T,"Wind","km/s",Wind);
    if (CheckCutoff(T,X,IsMajor)) {
      if(IsMajor)C.Record(T,"Guidance Mode",null,"Cutoff");
      TV=new MathVector(0,0,0);
    } else if(T<GravityTurnStart) {
      if(IsMajor)C.Record(T,"Guidance Mode",null,"Pitchover");
      TV=PitchoverGuidance(T,X,AvailAcc,IsMajor);
    } else if (T<GravityTurnStop) {
      if(IsMajor)C.Record(T,"Guidance Mode",null,"Gravity Turn");
      TV=GravityTurnGuidance(T,X,AvailAcc,IsMajor);
    } else {
      if(IsMajor)C.Record(T,"Guidance Mode",null,"Iterative");
      TV=IterativeGuidance(T,X,AvailAcc,IsMajor);
    }
    MathVector R=Resolv.ResolveRadialTransverseI(new MathState(X.R(),TV));
    double Radial=R.get(0);
    double Transverse=R.get(1);
    TanTheta0=R.get(1)/R.get(0);
    if(IsMajor)C.Record(T,"Acceleration Vector","km/s^2",TV);
    if(IsMajor)C.Record(T,"Relative Velocity","km/s",Vrel);
    if(IsMajor)C.Record(T,"TanTheta0",null,TanTheta0);
    if(IsMajor)C.Record(T,"Guidance Angle of Attack","deg",180.0/Math.PI*MathVector.vangle(TV,Vrel));
    if(IsMajor)C.Record(T,"Pitch from vertical","deg",180.0/Math.PI*MathVector.vangle(TV,X.R()));
    if(IsMajor)C.Record(T,"Relative Flight Path Angle","deg",180.0/Math.PI*MathVector.vangle(Vrel,X.R()));
    if(IsMajor)C.Record(T,"Inertial Flight Path Angle","deg",180.0/Math.PI*MathVector.vangle(X.V(),X.R()));
    if(IsMajor)C.Record(T,"Radial Acceleration","km/s^2",Radial);
    if(IsMajor)C.Record(T,"Transverse Acceleration","km/s^2",Transverse);
    return TV;
  }
}
