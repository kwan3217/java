package org.kwansystems.space.ephemeris;

import java.io.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.time.*;
import org.kwansystems.tools.*;
import org.kwansystems.space.*;

import static org.kwansystems.space.ephemeris.RotatorEphemeris.Frame.*;

/**
 * Designed to match the Orbiter rotation model. Conventions are all the
 * usual right-handed stuff, but initialization values can be pulled straight
 * out of Orbiter. Convention conversion is performed internally.
 *
 */
public class OrbiterRotatorEphemeris extends RotatorEphemeris {
  /** Longitude of ascending node of equator, radians */
  private double theta;
  /** Obliquity of equator, radians */
  private double phi;
  /** Sidereal rotation period, seconds */
  private double T;
  /** Sidereal rotation angle at J2000, radians */
  private double omega_0;
  private MathMatrix R_A;
  public OrbiterRotatorEphemeris(double Ltheta, double Lphi, double LT, double Lomega_0, Frame Lfrom) {
    super(Lfrom,J2000Ecl);
    theta=Ltheta;
    phi=Lphi;
    T=LT;
    omega_0=Lomega_0;
    calcR_A();
  }
  private void calcR_A() {
    R_A=MathMatrix.mul(Constants.J2000Equ2Ecl.T(), MathMatrix.mul(MathMatrix.Rot3(-theta), MathMatrix.Rot1(phi)));
  }
  public OrbiterRotatorEphemeris(String infn, Frame Lfrom) {
    super(Lfrom,J2000Ecl);
    IniFile I;
    try {
      I=new IniFile(infn);
    } catch(IOException E) {throw new RuntimeException(E);}
    if(I.hasEntry("","LAN"))          theta=Double.parseDouble(I.getEntry("","LAN")); else theta=0;
    if(I.hasEntry("","Obliquity"))    phi=Double.parseDouble(I.getEntry("","Obliquity")); else phi=0;
    if(I.hasEntry("","SidRotPeriod")) T=Double.parseDouble(I.getEntry("","SidRotPeriod")); else T=0;
    if(I.hasEntry("","SidRotOffset")) omega_0=Double.parseDouble(I.getEntry("","SidRotOffset")); else omega_0=0;
    calcR_A();
  }
  private double omega(Time t) {
    return omega_0+2*Math.PI*t.get(TimeUnits.Seconds,TimeScale.UTC,TimeEpoch.J2000)/T;
  }
  /**
   * Calculate rotation of target body from planet-fixed coordinates to J2000Equ.
   * Orbiter uses a left-handed system, X towards prime meridian, Y towards pole, 
   * Z towards 90&deg;E longitude. This program works right handed, by switching 
   * Y and Z. So, sign of angles stays the same, but Rot2 becomes Rot3 and vice versa.
   * @param T
   * @return
   */
  @Override
  public Rotator CalcRotation(Time T) {
    return MathMatrix.mul(R_A,MathMatrix.Rot3(-omega(T)));
  }
  public static void main(String[] args) throws IOException {
    OrbiterRotatorEphemeris E=new OrbiterRotatorEphemeris("Data/OrbiterConfig/Earth.cfg",PEF);
    OrbiterRotatorEphemeris M=new OrbiterRotatorEphemeris("Data/OrbiterConfig/Mars.cfg",MarsCenteredFixed);
    System.out.println(E.R_A);
    System.out.println(new AxisAngle(E.R_A));
    System.out.println(M.R_A);
    System.out.println(new AxisAngle(M.R_A));
  }
}
