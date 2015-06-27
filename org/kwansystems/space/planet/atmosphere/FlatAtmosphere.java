package org.kwansystems.space.planet.atmosphere;



/**
 * Uses another atmosphere model to calculate the atmosphere at 
 * one altitude, then uses that value for all altitudes
 */
public class FlatAtmosphere extends Atmosphere {
  /**
   * AirProperties at sample altitude
   */
  AirProperties A;
  /**
   * Creates a new FlatAtmosphere object. Calculates the AirProperties at the 
   * given altitude
   * @param SourceAtmosphere Source atmosphere model to use
   * @param Z Altitude to use in source atmosphere, m
   * @param LZlimit Altitude at which atmosphere "turns off", m
   */
  public FlatAtmosphere(Atmosphere SourceAtmosphere, double Z, double LZlimit) {
    Zlimit=LZlimit;
    A=SourceAtmosphere.calcProps(Z);
  }
  public FlatAtmosphere(AirProperties LA, double Z, double LZlimit) {
    Zlimit=LZlimit;
    A=LA;
  }
  /**
   * Calculate the air properties at this altitude.
   * @param Z Input altitude, m
   * @return the pre-calculated A if inside the atmosphere, AirProperties.Vacuum otherwise.
   */
  public AirProperties calcProps(double Z) {
    if(Z>Zlimit) return AirProperties.Vacuum;
    return A;
  }
}
