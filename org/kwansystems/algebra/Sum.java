package org.kwansystems.algebra;

import java.util.*;

public class Sum extends Operator {
  public Sum(DerivativeNode LnodeA, DerivativeNode LnodeB) {
    super(LnodeA,LnodeB,'+');
  }
  @Override
  public DerivativeNode derivative(String respectTo) {
    return new Sum(A.derivative(respectTo),B.derivative(respectTo)).simplify();
  }
  @Override
  public double evaluate(Map<String, Double> varValues) {
    return A.evaluate(varValues)+B.evaluate(varValues);
  }
  public DerivativeNode simplify() {
    if(A instanceof Constant && A.evaluate(null)==0) return B;
    if(B instanceof Constant && B.evaluate(null)==0) return A;
    if(A instanceof Constant && B instanceof Constant) return new Constant(A.evaluate(null)+B.evaluate(null));
    if(A.equals(B)) return new Product(new Constant(2),A).simplify();
    if(A instanceof Sum) {
      return new MultiSum(new DerivativeNode[] {((Sum)A).getA().simplify(),((Sum)A).getB().simplify(),B});
    }
    if(B instanceof Sum) {
      return new MultiSum(new DerivativeNode[] {((Sum)B).getA().simplify(),((Sum)B).getB().simplify(),A});
    }
    return this;
  }
  public static void main(String args[]) {
    Variable V=new Variable("V");
    Map<String,Double> varValues=new LinkedHashMap<String,Double>();
    varValues.put("V",1.23456);
    Sum S=new Sum(V,V);
    System.out.println(S.simplify());
    System.out.println(S.derivative("V"));
    System.out.println(S.derivative("X"));
  }

  public int precedence() {
    return 1;
  }
}
