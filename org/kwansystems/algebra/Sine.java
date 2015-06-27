/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.algebra;

import java.util.*;

public class Sine extends Composition {
  public Sine(DerivativeNode Linner) {
    super(Linner);
  }
  @Override
  public double evaluateOuter(double arg) {
    return Math.sin(arg);
  }

  @Override
  public DerivativeNode derivativeOuter() {
    return new Cosine(inner.simplify());
  }
  @Override
  public String toString() {
    if(inJava) return "Math.sin("+inner.toString()+")";
    return "sin("+inner.toString()+")";
  }
  public static void main(String args[]) {
    Variable V=new Variable("V");
    Map<String,Double> varValues=new LinkedHashMap<String,Double>();
    varValues.put("V",1.23456);
    Composition S=new Sine(V);
    System.out.println(S);
    System.out.println(S.derivative("V"));
    System.out.println(S.derivative("X"));
  }
  public DerivativeNode simplify() {
    return new Sine(inner.simplify());
  }
}
