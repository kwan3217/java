package org.kwansystems.space.universe;

import org.kwansystems.space.planet.atmosphere.*;
import org.kwansystems.tools.vector.*;

/**
 * Universe with flat ground. Altitude is precisely RV.R.Z(), gravity is constant
 */
public class FlatEarth extends Universe { 
  //Uniform downward acceleration of gravity
  /**
   * Acceleration of gravity, m/s^2. Positive values are acceleration in the -Z direction (down)
   */
  double Gravity;
  /**
   * Atmosphere object used to generate AirProperties
   */
  Atmosphere Atm;
  /**
   * Universal constant wind speed
   */
  MathVector Wind;
  /**
   * Constructs a new FlatEarth object
   * @param LGravity Acceleration of gravity, m/s^2. Positive numbers are downward.
   * @param LAtm Atmosphere object
   */
  public FlatEarth(double LGravity, Atmosphere LAtm, MathVector LWind) {
    Gravity=LGravity;
    Atm=LAtm;
    Wind=LWind;
  }
  /**
   * Constructs a new FlatEarth object
   * @param LGravity Acceleration of gravity, m/s^2. Positive numbers are downward.
   * @param LAtm Atmosphere object
   */
  public FlatEarth(double LGravity, Atmosphere LAtm) {
    this(LGravity,LAtm,new MathVector());
  }
  /**
   * Calculates constant downward acceleration of gravity
   * @param T Simulation time, s
   * @param RV Simulation state in inertial frame, m, s
   * @return environment acceleration in m/s^2, in inertial frame
   */
  public MathVector EnvironmentAcc(double T, MathState RV) {
    return new MathVector(0,0,-Gravity);
  }  
  /**
   * Gets motion of the atmosphere at this point
   * @param T Simulation time, s
   * @param RV Simulation state in inertial frame, m, s
   * @return MathVector object describing free air velocity at this point, in inertial frame, in m/s
   */
  public MathVector getWind(double T, MathState RV) {
    return new MathVector(Wind);
  }
  /**
   * Gets atmospheric properties at specified point in the Universe. Calculates
   * altitude based on RV.R.Z() only.
   * @param T Simulation time, s
   * @param RV Simulation state in inertial frame, m, s
   * @return AirProperties object defining bulk free air properties at this point
   */
  public AirProperties getAtm(double T, MathState RV) {
    return Atm.calcProps(Altitude(T,RV));
  }
  public double Altitude(double T, MathState RV) {
    return RV.R().Z();
  }
  public MathVector LocalVertical(double T, MathState RV) {
    return new MathVector(0,0,1);
  }
}
