package org.kwansystems.space.planet.atmosphere;

/**
 * Physical constants and functions relating to the Earth atmosphere at Z=0 (sea level).
 * The fundamental constants are from the USSA1976 model, *NOT* the 2002 CODATA 
 * recommended values.
 */
public class EarthAirConstants {
  /** Boltzmann constant, J/K */
  public static final double k = 1.380622e-23;
  /** Avogadro constant, 1/kmol */
  public static final double Na =  6.022169e26;
  /**Ideal gas constant, J/(K kmol). This is k*Na in theory, 
   * but this uses the USSA1976 value.
   */
  public static final double Rs =      8314.32;
  /** Mean molecular weight of air at sea level, kg/kmol */
  public static final double M0 =      28.9644;
  /** Standard latitude, deg */
  public static final double Lat0 = 45.5425; 
  /** acceleration of gravity at standard lat, m/s^2 */
  public static final double g0 =      9.80665; 
  /** Earth radius at standard lat, m */
  public static final double r0 =    6356766.0; 
  /** Celsius scale zero point, K */
  public static final double Td =       273.15;
  /** Air pressure at g0, Pa */
  public static final double P0 =      101325.; 
  /** Temperature at g0, K */
  public static final double T0 =       288.15;
  /** Effective collision diameter, m */
  public static final double sigma = 3.65e-10;
  /** Ratio of Specific heats for calorically perfect diatomic gas 
   * http://www.engapplets.vt.edu/fluids/shockcal/shockCalHelp.html#Theory
   */
  public static final double gamma =     1.40;  
  /** Viscosity coefficient beta, kg/(s m K^1/2) */
  public static final double beta  = 1.458e-6; 
  /** Sutherland's constant, K */
  public static final double S     =    110.4;
  /**
   * Calculate geopotential at a given altitude above the standard latitude
   * @param Z Geometric altitude, m
   * @return Geopotential, m'
   */
  public static double geopotential(double Z) {
    return r0*Z/(r0+Z);
  }
  /**
   * Calculate magnitude of acceleration of gravity at a given
   * altitude above the standard latitude
   * @param Z Geometric altitude, m
   * @return Acceleration of gravity, m/s^2
   */
  public static double gravity(double Z) {
    /*Input:  Z        geometric height, m
    Output:  gravity  acceleration of gravity, m/s^2*/
    return g0*Math.pow(r0/(r0+Z),2);
  }
}

