package org.kwansystems.space.ephemeris.sun;

import org.kwansystems.tools.vector.*;

/**
 * Calculates a VSOP87 rectangluar series 
 */
public abstract class SunSatVSOP87Rect extends SunSatVSOP87 {
  protected SunSatVSOP87Rect(char Ltheory, String LSuffix, double Lprec) {
    super(Ltheory, LSuffix, Lprec);
  }
  protected SunSatVSOP87Rect(char Ltheory, String LSuffix) {
	  this(Ltheory,LSuffix,0);
  }
  protected SunSatVSOP87Rect(char Ltheory, int LWorld, double Lprec) {
    super(Ltheory, LWorld, Lprec);
  }
  protected SunSatVSOP87Rect(char Ltheory, int LWorld) {
	  this(Ltheory,LWorld,0);
  }
  public MathState CalcState(double[] Parameters) {
    return new MathState(Parameters[0],Parameters[2],Parameters[4],Parameters[1],Parameters[3],Parameters[5]);
  }
}
