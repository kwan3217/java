package org.kwansystems.space.gear.actuator;

import org.kwansystems.space.gear.*;
import org.kwansystems.space.universe.*;
import org.kwansystems.tools.vector.*;

public class MagicThruster extends Actuator {
  /** Direction of this thruster in the station frame. Force is applied in this direction, exhaust (if any)
   * is expelled in the opposite direction. */
  private MathVector Direction;
  /** Throttle level. Nominally between zero and one, though I suppose you can
   * do things like 104% with this, with proportionally greater propellant usage.
   */
  private double throttle;
  private double maxThrust;
  public MagicThruster(String LName, MathVector LCoF, MathVector LDirection, double LmaxThrust) {
    super(LName, LCoF);
    Direction=LDirection;
    maxThrust=LmaxThrust;
  }
  public ForceTorque getForceTorque(double T, SixDOFState RVEw, double Mass, MathVector CoM, Universe U) {
    double Thrust=maxThrust*throttle;
    MathVector Force=Direction.mul(Thrust);
    MathVector ThrustOffset=MathVector.sub(CoF,CoM);
    MathVector Torque=MathVector.cross(Force, ThrustOffset);
    return new ForceTorque(Force,Torque);
  }
  /** Set the direction of this thruster, in the station frame. Don't try to cheat, this vector gets
   * copied and normalized before being used.
   * 
   * @param direction The new thruster direction vector
   */
  public void setDirection(MathVector direction) {
    Direction = direction.normal();
  }
  /** Get the direction of this thruster, in the station frame. Don't try to cheat, this vector is a 
   * copy of the direction vector, and changing it will not affect the actual direction.
   * @return A copy of the thruster direction vector
   */
  public MathVector getDirection() {
    return new MathVector(Direction);
  }
  /**
   * @param maxThrust the maxThrust to set
   */
  public void setMaxThrust(double LmaxThrust) {
    maxThrust = LmaxThrust;
  }

  /**
   * @return the maxThrust
   */
  public double getMaxThrust() {
    return maxThrust;
  }
  /**
   * @param throttle the throttle to set
   */
  public void setThrottle(double Lthrottle) {
    throttle = Lthrottle>0?Lthrottle:0;
  }
  /**
   * @return the throttle
   */
  public double getThrottle() {
    return throttle;
  }

}
