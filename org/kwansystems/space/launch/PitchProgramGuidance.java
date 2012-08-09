package org.kwansystems.space.launch;

import org.kwansystems.space.planet.*;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.table.*;
import org.kwansystems.tools.vector.*;

public class PitchProgramGuidance extends Guidance {
  double TargetAlt,BurnoutTime,TargetSpd;
  boolean Cutoff=false;
  double TanTheta0=0;
  double RotSpeed=0;
  double FrameShiftTime=20;
  double Azimuth;
  boolean StartIter=false;
  Resolver Resolv;
  Table PitchProgram; //Time in seconds, and pitch from vertical in degrees
  public PitchProgramGuidance(Table LProgram, double LAzimuth, Planet LP, ArrayListChartRecorder LC) {
    super(LP,LC);
    BurnoutTime=784.98; //Allow up to 100 seconds additional
    PitchProgram=LProgram;
    Azimuth=LAzimuth*Math.PI/180.0;
    Resolv=new Resolver(P);
  }
  public boolean CheckCutoff(double T,MathState X) {
    return false;
  }
  public MathVector PitchoverGuidance(double T, MathState X, double AvailThr, boolean IsMajor) {
    double Angle=(90.0+PitchProgram.Interp(T,0))*Math.PI/180.0;
    if(IsMajor)C.Record(T,"Pitch Program commanded pitch","deg",Angle*180.0/Math.PI);
    double RadialThr=Math.sin(Angle)*AvailThr;
    double NorthThr=Math.cos(Angle)*AvailThr*Math.cos(Azimuth);
    double EastThr=Math.cos(Angle)*AvailThr*Math.sin(Azimuth);
    if(IsMajor)C.Record(T,"Radial thrust","N",RadialThr);
    if(IsMajor)C.Record(T,"North thrust","N",NorthThr);
    if(IsMajor)C.Record(T,"East thrust","N",EastThr);
    return Resolv.ComposeRadialNorthEastI(X,RadialThr,NorthThr,EastThr);
  }  
  public MathVector ThrustVector(double T, MathState X,double AvailAcc,boolean IsMajor) {
    MathVector TV;
    MathVector Wind=P.Wind(X);
    MathVector Vrel=MathVector.sub(X.V(),Wind);
    if(IsMajor)C.Record(T,"Relative Velocity","km/s",Vrel);
    if(IsMajor)C.Record(T,"Wind","km/s",Wind);
    if(IsMajor)C.Record(T,"Guidance Mode",null,"Pitchover");
    TV=PitchoverGuidance(T,X,AvailAcc,IsMajor);
    return TV;
  }
}
