package org.kwansystems.algebra;

import java.util.*;

public class Product extends Operator {
  public Product(DerivativeNode LnodeA, DerivativeNode LnodeB) {
    super(LnodeA,LnodeB,'*');
  }
  public DerivativeNode derivative(String respectTo) {
    DerivativeNode Adot=A.derivative(respectTo).simplify();
    DerivativeNode Bdot=B.derivative(respectTo).simplify();
    DerivativeNode node1=new Product(A,Bdot).simplify();
    DerivativeNode node2=new Product(Adot,B).simplify();
    DerivativeNode result=new Sum(node1,node2).simplify();
    return result;
  }
  public double evaluate(Map<String, Double> varValues) {
    return A.evaluate(varValues)*B.evaluate(varValues);
  }
  public static void main(String args[]) {
    Variable V=new Variable("V");
    Map<String,Double> varValues=new LinkedHashMap<String,Double>();
    varValues.put("V",1.23456);
    Product P=new Product(V,V);
    System.out.println(P);
    System.out.println(P.simplify());
    System.out.println(P.derivative("V"));
    System.out.println(P.derivative("X"));
  }

  public DerivativeNode simplify() {
    if(A instanceof Constant && A.evaluate(null)==0) return new Constant(0);
    if(A instanceof Constant && A.evaluate(null)==1) return B;
    if(B instanceof Constant && B.evaluate(null)==0) return new Constant(0);
    if(B instanceof Constant && B.evaluate(null)==1) return A;
    if(A instanceof Constant && B instanceof Constant) return new Constant(A.evaluate(null)*B.evaluate(null));
    if(A instanceof Fraction && B instanceof Fraction) {
      Fraction AA=(Fraction) A;
      Fraction BB=(Fraction) B;
      return new Fraction(new Product(AA.getA(),BB.getA()),new Product(AA.getB(),BB.getB())).simplify();
    }
    if(A instanceof Fraction) {
      Fraction AA=(Fraction) A;
      return new Fraction(new Product(AA.getA(),B),AA.getB()).simplify();
    }
    if(B instanceof Fraction) {
      Fraction BB=(Fraction) B;
      return new Fraction(new Product(A,BB.getA()),BB.getB()).simplify();
    }
    if(A.equals(B)) return new Power(A,2);
    if(A instanceof Product && B instanceof Product) {
      return new MultiProduct(new DerivativeNode[] {((Product)A).getA().simplify(),((Product)A).getB().simplify(),((Product)B).getA().simplify(),((Product)B).getB().simplify()}).simplify();
    }
    if(A instanceof Product) {
      return new MultiProduct(new DerivativeNode[] {((Product)A).getA().simplify(),((Product)A).getB().simplify(),B}).simplify();
    }
    if(B instanceof Product) {
      return new MultiProduct(new DerivativeNode[] {((Product)B).getA().simplify(),((Product)B).getB().simplify(),A}).simplify();
    }
    return new Product(A,B);
  }

  public int precedence() {
    return 2;
  }
}
