package org.kwansystems.space.windtunnel;

import org.kwansystems.vector.*;

public class ForceMoment {
  public double FoilSpan;
  public double FoilChord;
  public double FoilArea;
  public double Aspect;
  public double Efficiency;
  public double Mach;
  public double Re;
  public double cDe;
  public double cDi;
  public double cDw;
  public double cL;
  public double cD;
  public double cM;
  public double q;
  public double rho;
  public MathVector Lift;
  public MathVector Drag;
  public MathVector Force;
  public MathVector MomentF;
  public MathVector MomentL;
  public MathVector Moment;
  public ForceMoment() {
    Lift=new MathVector();
    Drag=new MathVector();
    Force=new MathVector();
    MomentF=new MathVector();
    MomentL=new MathVector();
    Moment=new MathVector();
  }
}
