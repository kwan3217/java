package org.kwansystems.space.universe;

import org.kwansystems.space.planet.atmosphere.*;
import org.kwansystems.tools.vector.*;


/**
 * An empty universe. Nothing as far as the eye can see in any direction. Devoid of atmosphere and gravity. And it's really, really, big.
 * As a result, with a standard integrator, Newton's laws, and the equivalent rotational laws, apply here, with no annoying distractions.
 */
public class EmptyUniverse extends Universe {
  public double Altitude(double T, MathState RV) {
    return 0;
  }
  public MathVector EnvironmentAcc(double T, MathState RV) {
    return new MathVector(3);
  }
  public MathVector LocalVertical(double T, MathState RV) {
    return new MathVector(0,0,1);
  }
  public AirProperties getAtm(double T, MathState RV) {
    return AirProperties.Vacuum;
  }
  public MathVector getWind(double T, MathState RV) {
    return new MathVector(3);
  }

}
