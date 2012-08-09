package org.kwansystems.space.planet.magnetosphere;

/**
 * Caluclates magnetic of a magnetosphere as a function
 * of latitude, longitude, altitude, and year.
 */
public interface Magnetosphere {
  /**
   * Calculate the free air properties of the atmosphere at a given altitude
   * @param Z Altitude, m
   * @return An AirProperties object describing the free air properties at this point
   */
  public FieldProperties calcProps(double alt, double rlat, double rlon, double time);
  public FieldProperties calcRates(double alt, double rlat, double rlon, double time);
  public FieldProperties[] calcPropRates(double alt, double rlat, double rlon, double time);
  public double getEpoch();
}
