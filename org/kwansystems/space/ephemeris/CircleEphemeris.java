package org.kwansystems.space.ephemeris;

import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.vector.*;

public class CircleEphemeris extends Ephemeris {
  public double Radius,omega,phi,Speed;
  Time Epoch;
  MathVector Center;
  public CircleEphemeris(MathVector Cen, double R,double W,double F,Time E,Ephemeris LReference) {
    super(LReference);
    Center=Cen;
    Radius=R;
    omega=W;
    phi=F;
    Epoch=E;
    Speed=omega*Radius;
  }
  public double Theta(Time T) {
    double T1=Epoch.get();
    double T2=T.get(Epoch.Units,Epoch.Scale);
    return omega*(T2-T1)+phi;
  }
  protected MathState CalcState(Time T) {
    double q=Theta(T);
    MathVector R=MathVector.add(Center,new MathVector(Radius*Math.cos(q),Radius*Math.sin(q),0));
    MathVector V=new MathVector(-Speed*Math.sin(q),Speed*Math.cos(q),0);
    return new MathState(R,V);
  }
  @Override
  public MathVector CalcPos(Time TT) {
    return defaultCalcPos(TT);
  }
  @Override
  public MathVector CalcVel(Time TT) {
    return defaultCalcVel(TT);
  }
}
