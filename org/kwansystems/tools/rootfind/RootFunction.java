package org.kwansystems.tools.rootfind;

/**
 * Represents a one-dimensional function to find a root of.
 */
public abstract class RootFunction {
  /**
   * Evaluate function at given independent variable value
   * @param X Independent variable
   * @return Value of function at this point
   */
  public abstract double F(double X);
}
