
package org.kwansystems.space.windtunnel;

import org.kwansystems.vector.*;
import org.kwansystems.planet.*;

public class PointMass extends ForceMomentGenerator {
  double Mass;
  MathVector CoM;
  public PointMass(double LMass, MathVector LCoM) {
    Mass=LMass;
    CoM=LCoM;
  }
  public ForceMoment getForceMoment(Planet E, MathState X) {
    ForceMoment FM=new ForceMoment();
    MathVector AccGrav=E.Gravity(X.R());
    FM.Force=MathVector.mul(E.Gravity(X.R()),Mass);
    FM.Moment=MathVector.cross(CoM,FM.Force);
    return FM;
  }
  public static void main(String[] args) {
    Planet E=new Earth();
    PointMass P=new PointMass(23010,new MathVector(10,0,0));
    MathState X=new MathState(0,0,6378137,100,0,0);
    P.getForceMoment(E,X);
  }
}
