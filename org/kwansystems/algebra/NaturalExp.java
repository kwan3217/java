package org.kwansystems.algebra;

import java.util.*;

public class NaturalExp extends Composition {
  public NaturalExp(DerivativeNode Linner) {
    super(Linner);
  }
  @Override
  public double evaluateOuter(double arg) {
    return Math.exp(arg);
  }
  @Override
  public DerivativeNode derivativeOuter() {
    return new NaturalExp(inner);
  }
  @Override
  public String toString() {
    if(inJava) return "Math.exp("+inner.toString()+")";
    return "exp("+inner.toString()+")";
  }
  public static void main(String args[]) {
    Variable V=new Variable("V");
    Map<String,Double> varValues=new LinkedHashMap<String,Double>();
    varValues.put("V",1.23456);
    Composition E=new NaturalExp(V);
    System.out.println(E);
    System.out.println(E.derivative("V"));
    System.out.println(E.derivative("X"));
    System.out.println(E.evaluate(varValues));
    System.out.println(E.derivative("V").evaluate(varValues));
    Composition F=new NaturalExp(new NaturalLog(V));
    System.out.println(F);
    System.out.println(F.simplify());
    Composition G=new NaturalLog(new NaturalExp(V));
    System.out.println(G);
    System.out.println(G.simplify());
    Composition H=new NaturalExp(new Product(new Constant(7),new NaturalLog(V)));
    System.out.println(H);
    System.out.println(H.simplify());
    System.out.println(H.derivative("V").simplify());
    System.out.println(H.simplify().derivative("V").simplify());
  }
  public DerivativeNode simplify() {
    DerivativeNode i=inner.simplify();
    if(i instanceof NaturalLog) return ((NaturalLog)i).getInner();
    if(i instanceof Product) {
      Product P=(Product)i;
      DerivativeNode A=P.getA().simplify();
      DerivativeNode B=P.getB().simplify();
      if(A instanceof NaturalLog) {
        DerivativeNode ii=((NaturalLog)A).getInner().simplify();
        if(ii instanceof Constant || ii instanceof Variable) {
          return new Power(B,ii).simplify();
        }
      }
      if(B instanceof NaturalLog) {
        DerivativeNode ii=((NaturalLog)B).getInner().simplify();
        if(ii instanceof Constant || ii instanceof Variable) {
          return new Power(A,ii).simplify();
        }
      }
    }
    return new NaturalExp(i);
  }
}
