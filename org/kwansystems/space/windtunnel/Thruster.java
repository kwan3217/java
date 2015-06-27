
package org.kwansystems.space.windtunnel;

import org.kwansystems.vector.*;
import org.kwansystems.planet.*;

public class Thruster extends ForceMomentGenerator {
  double Thrust;
  MathVector CoF;
  MathVector ThrustDir;
  public Thruster(double LThrust, MathVector LCoF,MathVector LThrustDir) {
    Thrust=LThrust;
    CoF=LCoF;
    ThrustDir=LThrustDir.normal();
  }
  public ForceMoment getForceMoment(Planet E, MathState X) {
    ForceMoment FM=new ForceMoment();
    FM.Force=MathVector.mul(ThrustDir,Thrust);
    FM.Moment=MathVector.cross(CoF,FM.Force);
    return FM;
  }
  public static void main(String[] args) {
    Planet E=new Earth();
    PointMass P=new PointMass(23010,new MathVector(10,0,0));
    MathState X=new MathState(0,0,6378137,100,0,0);
    P.getForceMoment(E,X);
  }
}
