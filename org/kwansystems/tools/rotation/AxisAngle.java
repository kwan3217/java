package org.kwansystems.tools.rotation;

import static java.lang.Math.*;

import org.kwansystems.tools.vector.*;

/**
 * Object representing a rotation about an axis by an angle. 
 * Euler proved that any combination of pure rotations is
 * equivalent to a single rotation about a certain axis
 * by a certain amount.
 */
public class AxisAngle extends Rotator {
  public final MathVector Axis;
  public final double Angle;
  public AxisAngle(MathVector LAxis, double LAngle) {
    Axis=LAxis;
    Angle=LAngle;
  }
  public AxisAngle(Rotator R) {
    Quaternion Q2=Quaternion.norm(R.toQuaternion());
    Angle=2*acos(Q2.W());
    double S=sin(Angle/2);
    if(S==0.0) {
      Axis=new MathVector(1,0,0);
    } else {
      Axis=Q2.V().div(S);
    }
  }
  public String toString() {
    return "<"+Axis.toString()+">,"+toDegrees(Angle);
  }
  public static void main(String args[]) {
    AxisAngle A1=new AxisAngle(new MathVector(0,0,1),toRadians(90));
    System.out.println(A1);
    Quaternion Q1=A1.toQuaternion();
    System.out.println(Q1);
    AxisAngle A2=new AxisAngle(new MathVector(0,1,0),toRadians(180));
    System.out.println(A2);
    Quaternion Q2=A2.toQuaternion();
    System.out.println(Q2);
    Quaternion Q=Quaternion.mul(Q1, Q2);
    System.out.println(Q);
    System.out.println(new AxisAngle(Q));
  }
  @Override
  public Quaternion toQuaternion() {
    return new Quaternion(Axis.mul(sin(Angle/2)),cos(Angle/2));
  }
  @Override
  public AxisAngle inv() {
    return new AxisAngle(Axis,-Angle);
  }
}
