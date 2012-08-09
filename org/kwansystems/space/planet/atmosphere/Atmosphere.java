package org.kwansystems.space.planet.atmosphere;

/**
 * Calculates air properties of an atmosphere as a pure function of altitude.
 */
public abstract class Atmosphere {
  /**
   * Highest altitude for which properties can be calculated, m.
   */
  protected double Zlimit;
  /** acceleration of gravity at zero altitude reference level. 
   * Needed to calculate scale height
   */
  protected static double g0=EarthAirConstants.g0;
  /**
   * Calculate the free air properties of the atmosphere at a given altitude
   * @param Z Altitude, m
   * @return An AirProperties object describing the free air properties at this point
   */
  public abstract AirProperties calcProps(double Z);
}
