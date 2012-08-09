package org.kwansystems.tools.integrator;

import org.kwansystems.tools.vector.*;

/**
 * Allows something to be done to an {@link Integrator} state vector at every step.
 * This something can be stuff like constraining a quaternion or matrix to be orthonormal,
 * a fuel tank to be between empty and full, and stuff like that.
 */
public interface Constraint {
  /**
   * Apply the constraint to the state vector
   * @param T Integrator independent variable
   * @param X Unconstrained Integrator state vector
   * @return Constrained Integrator state vector
   */
  public MathVector Constrain(double T, MathVector X);
}
