package org.kwansystems.space.gear.mass;

import org.kwansystems.tools.vector.*;

/**
 * A finite (possibly time or sim state varying) mass element with
 * no size. Point mass only. 
 *
 */
public abstract class ThreeDOFMass {
  /** Name of mass element (useful for debugging purposes)
   */
  public String Name;
  public boolean Active;
  public ThreeDOFMass(String LName) {
    Name=LName;
    Active=true;
  }
  /**
   * Calculate the mass of this element, given the current sim time and state
   * @param T  Simulation time
   * @param RV Simulation state
   * @return Scalar mass of element, usually in kg
   */
  public abstract double getMass(double T, MathState RV);
  /**
   * Calculate the mass of this element, given the current sim time and state
   * @param T    Simulation time
   * @param RVEw Simulation state
   * @return Scalar mass of element, usually in kg
   */
  public double getMass(double T, SixDOFState RVEw) {
    return getMass(T,RVEw);
  }
}
