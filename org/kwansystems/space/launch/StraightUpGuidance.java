package org.kwansystems.space.launch;

import org.kwansystems.space.planet.*;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.table.*;
import org.kwansystems.tools.vector.*;

public class StraightUpGuidance extends Guidance {
  double TargetAlt,BurnoutTime,TargetSpd;
  boolean Cutoff=false;
  Table FirstStagePitch;
  Table PitchOver;
  double GravityTurnStart,GravityTurnStop;
  double Azimuth=100*Math.PI/180.0;
  double TanTheta0=0;
  double RotSpeed=0;
  double FrameShiftTime=20;
  boolean StartIter=false;
  Resolver Resolv;
  public StraightUpGuidance(Planet LP, ArrayListChartRecorder LC) {
    super(LP,LC);
    BurnoutTime=784.98; //Allow up to 100 seconds additional
    PitchOver=new LinearTable(new double[] {  0,
                                             200,
                                             300},
                            new double[][] {{  0,
                                            0,
                                           -6.84}});
    Resolv=new Resolver(P);
  }
  public boolean CheckCutoff(double T,MathState X) {
    return false;
  }
  public MathVector PitchoverGuidance(double T, MathState X, double AvailAcc) {
    double Angle=(90.0+PitchOver.Interp(T,0))*Math.PI/180.0;
    double RadialAcc=Math.sin(Angle)*AvailAcc;
    double NorthAcc=Math.cos(Angle)*AvailAcc*Math.cos(Azimuth);
    double EastAcc=Math.cos(Angle)*AvailAcc*Math.sin(Azimuth);
    C.Record(T,"Radial Acceleration","km/s^2",RadialAcc);
    C.Record(T,"North Acceleration","km/s^2",NorthAcc);
    C.Record(T,"East Acceleration","km/s^2",EastAcc);
    return Resolv.ComposeRadialNorthEastI(X,RadialAcc,NorthAcc,EastAcc);
  }  
  public MathVector ThrustVector(double T, MathState X,double AvailAcc, boolean IsMajor) {
    MathVector TV;
    MathVector Wind=P.Wind(X);
    MathVector Vrel=MathVector.sub(X.V(),Wind);
    if(IsMajor)C.Record(T,"Relative Velocity","km/s",Vrel);
    if(IsMajor)C.Record(T,"Wind","km/s",Wind);
    if(IsMajor)C.Record(T,"Guidance Mode",null,"Pitchover");
    TV=PitchoverGuidance(T,X,AvailAcc);
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
