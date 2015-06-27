package org.kwansystems.tools.rootfind;

/**
 * Calculates the derivative of a function numerically. This is to provide 
 * derivatives for root-finders which need them, but functions which don't provide 
 * them.
 */
public abstract class RootAutoDeriv extends RootDeriv {
  /**
   * Evaluate function first derivative by single-point numerical differentiation
   * @param X Point at which to evaluate derivative
   * @return Approximation of derivative at this point
   */
  public double dFdx(double X) {
    return (F(X+0.001)-F(X))/0.001;
  }
}
