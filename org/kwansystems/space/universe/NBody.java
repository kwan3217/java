package org.kwansystems.space.universe;

import org.kwansystems.space.ephemeris.*;
import org.kwansystems.space.planet.atmosphere.*;
import org.kwansystems.tools.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeEpoch;
import org.kwansystems.tools.time.TimeScale;
import org.kwansystems.tools.time.TimeUnits;
import org.kwansystems.tools.vector.*;


public class NBody extends Universe {
  protected Ephemeris[] Mover;
  protected double[] Mu;
  public NBody(Ephemeris[] LMover,double[] LMu) {
    Mover=LMover;
    Mu=LMu;
  }
  private static int getClosest(double T, MathVector RV) {
    return 0;
  }

  public MathVector EnvironmentAcc(double T, MathState RV) {
    MathState S=new MathState(RV);
    MathVector ResultR=S.V(),ResultV=new MathVector(0,0,0);
    Time TT=new Time(T,TimeUnits.Seconds,TimeScale.UTC,TimeEpoch.Java);
    for(int i=0;i<Mover.length;i++) {
      MathState S2=Mover[i].getState(TT);
      MathVector rel=MathVector.sub(S.R(),S2.R());
      double R3=Math.pow(rel.length(),3);
      ResultV=MathVector.add(ResultV,rel.mul(-Mu[i]/R3));
    }
    return (MathVector)new MathState(ResultR,ResultV);
  }
  
  @Override
  public AirProperties getAtm(double T, MathState RV) {
    return null;
  }
  @Override
  public MathVector getWind(double T, MathState RV) {
    return null;
  }
  @Override
  public MathVector LocalVertical(double T, MathState RV) {
    return null;
  }
  @Override
  public double Altitude(double T, MathState RV) {
    return 0;
  }
}
