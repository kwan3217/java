package org.kwansystems.algebra;

import java.util.*;

public class Constant extends DerivativeNode {
  private final double value;
  public Constant(double Lvalue) {
    value=Lvalue;
  }
  public DerivativeNode derivative(String respectTo) {
    return new Constant(0);
  }
  public double evaluate() {
    return value;
  }
  @Override
  public double evaluate(Map<String,Double> varValues) {
    return value;
  }
  @Override
  public String toString() {
    return Double.toString(value);
  }
  @Override
  public DerivativeNode simplify() {
    return this;
  }
  @Override
  public boolean equals(Object o) {
    if(!(o instanceof Constant)) return false;
    return ((Constant)o).value==value;
  }
  @Override
  public int hashCode() {
    long v = Double.doubleToLongBits(value);
    return (int)(v^(v>>>32));
  }
  @Override
  public int precedence() {
    return 10;
  }
}
