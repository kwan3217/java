/**
 * 
 */
package org.kwansystems.space.ephemeris;

import org.kwansystems.tools.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.time.Time;

/**
 * 
 *
 */
public class ChainRotatorEphemeris extends RotatorEphemeris {
  private RotatorEphemeris[] chain;
  public ChainRotatorEphemeris(RotatorEphemeris[] Lchain, Frame Lfrom, Frame Lto) {
    super(Lchain[0].naturalFrom,Lchain[Lchain.length-1].naturalTo);
    chain=Lchain;
    setInv(Lfrom,Lto);
  }
  
  /** Calculate the rotation of a bunch of rotators, then chain them together in the proper order and return the result.
   * @see org.kwansystems.space.ephemeris.RotatorEphemeris#CalcRotation(org.kwansystems.tools.time.Time)
   */
  public Rotator CalcRotation(Time T, int from, int length, boolean inverse) {
    Rotator result=MathMatrix.Identity(3);
    for(int i=from;i<length;i++) {
      result=chain[i].CalcRotation(T).combine(result);
    }
    return result;
  }
  @Override
  public Rotator CalcRotation(Time T) {
    return CalcRotation(T,0,chain.length,false);
  }
}
