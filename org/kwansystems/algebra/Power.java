package org.kwansystems.algebra;

import java.util.Map;

public class Power extends Operator {
  //implements A^B operator
  public Power(DerivativeNode Lbase, DerivativeNode Lexp) {
    super(Lbase,Lexp,'^');
  }
  public Power(DerivativeNode Lbase, double Lexp) {
    this(Lbase,new Constant(Lexp));
  }
  public double evaluate(Map<String, Double> varValues) {
    return Math.pow(A.evaluate(varValues),B.evaluate(varValues));
  }
  public DerivativeNode derivative(String respectTo) {
    if(A instanceof Variable && B instanceof Constant) {
      double expV=((Constant)B).evaluate();
      if(expV==0.0) return new Constant(0.0);
      if(expV==1.0) return A.simplify();
      return new Product(new Constant(expV),new Power(A,new Constant(expV-1))).simplify();
    }
    //Swiss army knife - just do it with natural exponents
    return new NaturalExp(new Product(A,new NaturalLog(B))).derivative(respectTo);
  }
  public DerivativeNode simplify() {
    if(B instanceof Constant) {
      double expV=((Constant)B).evaluate();
      if(expV==0.0) return new Constant(1.0);
      if(expV==1.0) return A.simplify();
      if(expV<0.0) return new Fraction(new Constant(1),new Power(A,-expV)).simplify();
    }
    if(A instanceof Power) {
      DerivativeNode newA=((Power)A).getA().simplify();
      DerivativeNode newB=new Product(((Power)A).getB(),getB().simplify());
      return new Power(newA,newB).simplify();
    }
    return this;
  }
  @Override
  public String toString() {
    if(inJava) return "Math.pow("+A.toString()+","+B.toString()+")";
    return super.toString();
  }
  public int precedence() {
    return 10;
  }
  public static void main(String args[]) {
    Variable x=new Variable("x");
    for(int i=-1;i<5;i++) {
      System.out.println(i);
      Power P=new Power(x,i);
      System.out.println(P.simplify());
      System.out.println(P.derivative("x"));
    }
  }
}
