package org.kwansystems.tools.rootfind.optimize;

/**
 * Represents a multidimensional function which is to be optimized. Optimization 
 * is always minimization in this context -- to maximize a function, minimize
 * its opposite.
 */
public interface OptimizeMultiDFunction {
  /**
   * Evaluates a multidimensional function at a particular point
   * @param args Point at which to evaluate function
   * @return Value of function at this point
   */
  public double eval(double[] args);
}
