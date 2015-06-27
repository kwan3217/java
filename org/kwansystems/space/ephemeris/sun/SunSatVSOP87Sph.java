package org.kwansystems.space.ephemeris.sun;

import static java.lang.Math.*;

import org.kwansystems.tools.vector.*;

/**
 * Calculates a VSOP87 spherical series 
 */
public abstract class SunSatVSOP87Sph extends SunSatVSOP87 {
  protected SunSatVSOP87Sph(char Ltheory, String LSuffix, double Lprec) {
    super(Ltheory, LSuffix, Lprec);
  }
  protected SunSatVSOP87Sph(char Ltheory, String LSuffix) {
	  this(Ltheory,LSuffix,0);
  }
  protected SunSatVSOP87Sph(char Ltheory, int LWorld, double Lprec) {
    super(Ltheory, LWorld, Lprec);
  }
  protected SunSatVSOP87Sph(char Ltheory, int LWorld) {
	  this(Ltheory,LWorld,0);
  }
  public MathState CalcState(double[] Parameters) {
    double L=   Parameters[0];
    double B=   Parameters[1];
    double r=   Parameters[2];
    double Ldot=Parameters[3];
    double Bdot=Parameters[4];
    double rdot=Parameters[5];
    double X=r*cos(L)*cos(B);
    double Y=r*sin(L)*cos(B);
    double Z=r*       sin(B);
    return new MathState(X,Y,Z,Ldot,Bdot,rdot);
  }
}
