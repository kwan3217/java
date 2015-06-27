package org.kwansystems.space;

import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.*;

/**
 * Physical constants useful within the entire solar system. Constants related to a particular
 * planet or other object don't belong here. Those related to the obliquity of the Earth's axis
 * are only here because Earth Equatorial and Earth Ecliptic coordinate systems are both used 
 * throughout the solar system.
 */
public class Constants {
  /** speed of light, m/s, by definition */
  public static final double c=299792458; 
  /**Astronomical unit, km, 149597870.0 according to Vallado Orange Book*/
  public static final double KmPerAUVallado=149597870.0;
  /**Astronomical unit, km, according to JPL, extracted from DE-405*/
  public static final double KmPerAUJPL=149597870.691;
  /**Astronomical unit, km, default value (JPL) */
  public static final double KmPerAU = KmPerAUJPL;
  /**Astronomical unit, m */
  public static final double MPerAU = KmPerAU*1000;
  /**Newtonian gravitational constant, 6.67259(85)e-11 m^3/(kg*s^2), according to CODATA 1986, used in Orbiter */
  public static final double GkgmsOrbiter=6.67259e-11;             
  /**Newtonian gravitational constant, 6.67428(67)e-11 m^3/(kg*s^2), according to CODATA 2006 */
  public static final double GkgmsCODATA= 6.67428e-11;             
  /**Newtonian gravitational constant, m^3/(kg*s^2), default value (Orbiter) */
  public static final double Gkgms=GkgmsOrbiter;             
  /** G*Sun Mass, in units of m and s from DE405 */
  public static final double SunGM=132712440017.987e9;
  /** Obliquity of Earth's axis to Earth's orbit plane on J2000, deg. From 
   */
  public static final double epsJ2000D=Scalar.dmsToDegrees(23,26.0,21.488);
  /** Obliquity of Earth's axis to Earth's orbit plane on J2000, rad */
  public static final double epsJ2000R=Math.toRadians(epsJ2000D);
  /** Obliquity of Earth's axis to Earth's orbit plane on B1950, deg */
  public static final double epsB1950D=(23+(26.0/60.0)+(44.84000/3600.0));
  /** Obliquity of Earth's axis to Earth's orbit plane on B1950, rad */
  public static final double epsB1950R=Math.toRadians(epsB1950D);
  /**B1950->J2000 matrix, according to Commision 20.
    Transcribed from JupiterSatE5.pdf .
    The original had a typo in element 1,3, specifically too many zeros before the first sigfig
   */
  public static final Rotator B1950toJ2000Commision20=new MathMatrix(new double[][] {
    {0.9999256794956877, -0.0111814832204662, -0.004859003815359},
    {0.0111814832391717,  0.9999374848933135, -0.0000271625947142},
    {0.0048590037723143, -0.0000271702937440,  0.9999881946023742}
  });
  /**B1950->J2000 matrix, according to Standish.
    Transcribed from JupiterSatE5.pdf .
    This one is orthonormal in the original, so presumably is correct.
   */
  public static final Rotator B1950toJ2000Standish=new MathMatrix(new double[][] {
    {0.9999256791774783, -0.0111815116768724, -0.0048590038154553},
    {0.0111815116959975,  0.9999374845751042, -0.0000271625775175},
    {0.0048590037714450, -0.0000271704492210,  0.9999881946023742}
  });
  /**B1950->J2000 matrix, according to Lieske.
    Transcribed from JupiterSatE5.pdf .
    The original had a typo in element 1,3, specifically too many zeros before the first sigfig
   */
  public static final Rotator B1950toJ2000Lieske=new MathMatrix(new double[][] {
    {0.9999256795268940, -0.0111810778339439, -0.004859930159015},
    {0.0111810775053504,  0.9999374894281627, -0.0000272382503387},
    {0.0048599309149990, -0.0000271030297995,  0.9999881900987267}
  });
  public static final Rotator B1950toJ2000=B1950toJ2000Lieske;
  /**J2000Equ->J2000Ecl matrix  */
  public static final MathMatrix J2000Equ2Ecl = MathMatrix.Rot1(epsJ2000R);
}
