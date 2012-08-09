package org.kwansystems.tools.rootfind;

/**
 * Represents a function to find the root of, and its first derivative. Used for root
 * finders which require the derivative.
 */
public abstract class RootDeriv extends RootFunction{
  /**
   * Evaluates derivative at this point
   * @param X Point at which to evaluate derivative
   * @return First derivative of function at this point
   */
  public abstract double dFdx(double X);
}
