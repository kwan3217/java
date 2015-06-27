package org.kwansystems.algebra;

import java.util.*;

public class Fraction extends Operator {
  public Fraction(DerivativeNode LnodeA, DerivativeNode LnodeB) {
    super(LnodeA,LnodeB,'/');
  }
  public DerivativeNode derivative(String respectTo) {
    DerivativeNode node1=new Product(A.derivative(respectTo),B);
    DerivativeNode node2=new Product(A,B.derivative(respectTo));
    return new Fraction(new Sum(node1,new Product(new Constant(-1),node2)),new Product(B,B)).simplify();
  }
  public double evaluate(Map<String, Double> varValues) {
    return A.evaluate(varValues)/B.evaluate(varValues);
  }
  public static void main(String args[]) {
    Variable V=new Variable("V");
    Map<String,Double> varValues=new LinkedHashMap<String,Double>();
    varValues.put("V",1.23456);
    Fraction F=new Fraction(new Constant(1),V);
    DerivativeNode DFDV=F.derivative("V");
    System.out.println(F);
    System.out.println(DFDV);
    System.out.println(F.derivative("X"));
  }
  public DerivativeNode simplify() {
    DerivativeNode nA=A.simplify();
    DerivativeNode nB=B.simplify();
    if(nA instanceof Constant && nA.evaluate(null)==0) return new Constant(0);
    if(nB instanceof Constant && nB.evaluate(null)==1) return nA;
    if(nA instanceof Constant && nB instanceof Constant) return new Constant(nA.evaluate(null)/nB.evaluate(null));
    return new Fraction(nA,nB);
  }
  public int precedence() {
    return 3;
  }
}
