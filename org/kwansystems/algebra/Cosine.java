package org.kwansystems.algebra;

import java.util.*;

public class Cosine extends Composition {
  public Cosine(DerivativeNode Linner) {
    super(Linner);
  }
  @Override
  public double evaluateOuter(double arg) {
    return Math.cos(arg);
  }

  @Override
  public DerivativeNode derivativeOuter() {
    return new Product(new Constant(-1),new Sine(inner)).simplify();
  }
  @Override
  public String toString() {
    if(inJava) return "Math.cos("+inner.toString()+")";
    return "cos("+inner.toString()+")";
  }
  public static void main(String args[]) {
    Variable V=new Variable("V");
    Map<String,Double> varValues=new LinkedHashMap<String,Double>();
    varValues.put("V",1.23456);
    Composition C=new Cosine(V);
    System.out.println(C);
    System.out.println(C.derivative("V"));
    System.out.println(C.derivative("X"));
    System.out.println(C.evaluate(varValues));
    System.out.println(C.derivative("V").evaluate(varValues));
  }
  public DerivativeNode simplify() {
    return new Cosine(inner.simplify());
  }
}
