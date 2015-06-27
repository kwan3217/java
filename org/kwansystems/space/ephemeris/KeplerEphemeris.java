package org.kwansystems.space.ephemeris;

import org.kwansystems.space.kepler.Elements;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.vector.MathState;
import org.kwansystems.tools.vector.MathVector;

/**
 * Abstract class for satellite theories which construct a (possibly time-varying) 
 * set of keplerian elements in some natural coordinate system, then use those 
 * elements to get a state vector, and finally transform the state vector in a 
 * possibly time-varying manner.   
 *
 */
public abstract class KeplerEphemeris extends Ephemeris {

  public MathState CalcState(Time T) {
    return RotateState(T,CalcElements(T).Propagate(T));
  }
  public MathVector CalcPos(Time TT) {
	return defaultCalcPos(TT);
  }
  public MathVector CalcVel(Time TT) {
	return defaultCalcVel(TT);
  }

  /**
   * Calculates the orbital elements
   * @param T Time at which to calculate the elements
   * @return An element set
   */
  public abstract Elements CalcElements(Time T);
  /**
   * Transforms the state vector from the natural coordinate system to J2000Ecl
   * @param T Time at which to calculate the rotation
   * @param S State vector at that time in natural coordinates
   * @return State vector in J2000Ecl coordinates
   */
  public abstract MathState RotateState(Time T, MathState S);
}
