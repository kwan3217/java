package org.kwansystems.space.universe;

import org.kwansystems.space.planet.*;
import org.kwansystems.space.planet.atmosphere.*;
import org.kwansystems.tools.vector.*;

/**
 * Universe with a single planet at the center of the Universe. Altitude and air
 * properties are determined from Planet properties.
 */
public class TwoBody extends Universe { 
  /**
   * Planet object carrying properties of planet
   */
  Planet P;
  /**
   * Constructs a new TwoBody object
   * @param LP A planet
   */
  public TwoBody(Planet LP) {
    P=LP;
  }
  /**
   * Calculates height above surface of given state
   * @param T Simulation time, s
   * @param RV Simulation state in inertial frame, m, s
   * @return height above surface, m
   */
  public double Altitude(double T, MathState RV) {
    return P.Alt(RV.R());
  }
  /**
   * Calculates centripetal acceleration of gravity
   * @param T Simulation time, s
   * @param RV Simulation state in inertial frame, m, s
   * @return environment acceleration in m/s^2, in inertial frame
   */
  public MathVector EnvironmentAcc(double T, MathState RV) {
    return P.Gravity(RV.R());
  }  
  /**
   * Gets motion of the atmosphere at this point
   * @param T Simulation time, s
   * @param RV Simulation state in inertial frame, m, s
   * @return MathVector object describing free air velocity at this point, in inertial frame, in m/s
   */
  public MathVector getWind(double T, MathState RV) {
    return P.Wind(RV.R());
  }
  /**
   * Gets atmospheric properties at specified point in the Universe. Gets altitude
   * from its Planet.
   * @param T Simulation time, s
   * @param RV Simulation state in inertial frame, m, s
   * @return AirProperties object defining bulk free air properties at this point
   */
  public AirProperties getAtm(double T, MathState RV) {
    return P.Air(RV.R());
  }
  public MathVector LocalVertical(double T, MathState RV) {
    return P.LocalVertical(RV.R());
  }
}
