/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.algebra;

import java.util.*;

/**
 *
 * @author jeppesen
 */
public class NaturalLog extends Composition {
  public NaturalLog(DerivativeNode Linner) {
    super(Linner);
  }
  @Override
  public double evaluateOuter(double arg) {
    return Math.log(arg);
  }
  @Override
  public DerivativeNode derivativeOuter() {
    return new Fraction(new Constant(1),inner).simplify();
  }
  @Override
  public String toString() {
    if(inJava) return "Math.log("+inner.toString()+")";
    return "ln("+inner.toString()+")";
  }
  public DerivativeNode simplify() {
    DerivativeNode i=inner.simplify();
    if(i instanceof NaturalExp) return ((NaturalExp)i).getInner();
    return new NaturalLog(i);
  }
  public static void main(String args[]) {
    Variable V=new Variable("V");
    Map<String,Double> varValues=new LinkedHashMap<String,Double>();
    varValues.put("V",1.23456);
    Composition E=new NaturalLog(V);
    System.out.println(E);
    System.out.println(E.derivative("V"));
    System.out.println(E.derivative("X"));
    System.out.println(E.evaluate(varValues));
    System.out.println(E.derivative("V").evaluate(varValues));
  }
}
