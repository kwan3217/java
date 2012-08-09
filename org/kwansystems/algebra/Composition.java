/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.algebra;

import java.util.Map;

/**
 *
 * @author jeppesen
 */
public abstract class Composition extends DerivativeNode {
  protected final DerivativeNode inner;
  public Composition(DerivativeNode Linner) {
    inner=Linner;
  }
  public abstract double evaluateOuter(double arg);
  public double evaluate(Map<String, Double> varValues) {
    return evaluateOuter(inner.evaluate(varValues));
  }
  public abstract DerivativeNode derivativeOuter();
  public DerivativeNode derivative(String respectTo) {
    return new Product(derivativeOuter(),inner.derivative(respectTo)).simplify();
  }
  public DerivativeNode getInner() {
    return inner.simplify();
  }
  public int precedence() {
    return 10;
  }
}
