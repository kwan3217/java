package org.kwansystems.tools.integrator;

import org.kwansystems.tools.vector.*;

/**
 * First-order numerical integrator using the Euler Integration method
 */
public class Euler extends RungeKutta {
  /**
   * Creates new Euler Integrator
   * @param StartT Initial value of independent variable
   * @param StartX Initial conditions
   * @param StartStepSize Initial step size
   * @param LEquations DerivativeSet describing differential equation
   */
  public Euler(double StartT, MathVector StartX, double StartStepSize, DerivativeSet LEquations) {
    super(StartT,StartX,StartStepSize,LEquations,RationalButcherTableau.EulerTableau); 
  }
}
