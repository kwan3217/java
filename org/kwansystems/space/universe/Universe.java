package org.kwansystems.space.universe;

import org.kwansystems.space.planet.atmosphere.*;
import org.kwansystems.tools.vector.*;

/**
 * Describes the universe around the vehicle, including such things atmosphere and 
 * gravity.
 */
public abstract class Universe {
  /**
   * Accelerations generated by the environment. This includes forces which:
   *    1) act on all particles of the vehicle equally
   *    2) are strictly proportional to mass (acceleration is independent of mass)
   *  These limitations pretty much exclude everything but gravity. Drag etc is 
   *  viewed as created by an airfoil (actuator).
   * @param T Simulation time, s
   * @param RV Simulation state in inertial frame, m, s
   * @return environment acceleration in m/s^2, in inertial frame
   */
  public abstract MathVector EnvironmentAcc(double T, MathState RV);
  /**
   * Gets atmospheric properties at specified point in the Universe.
   * @param T Simulation time, s
   * @param RV Simulation state in inertial frame, m, s
   * @return AirProperties object defining bulk free air properties at this point
   */
  public abstract AirProperties getAtm(double T, MathState RV);
  /**
   * Gets motion of the atmosphere at this point
   * @param T Simulation time, s
   * @param RV Simulation state in inertial frame, m, s
   * @return MathVector object describing free air velocity at this point, in inertial frame, in m/s
   */
  public abstract MathVector getWind(double T, MathState RV);
  /**
   * Gets local vertical at this point
   * @param T Simulation time, s
   * @param RV Simulation state in inertial frame, m, s
   * @return MathVector object of unit length perpendicular to local horizontal 
   * at this point
   */
  public abstract MathVector LocalVertical(double T, MathState RV);
  public abstract double Altitude(double T, MathState RV);
}