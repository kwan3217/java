package org.kwansystems.algebra;

import java.util.*;

public class MultiSum extends MultiOperator {
  public MultiSum(DerivativeNode[] LnodeA) {
    super(LnodeA,'+');
  }
  @Override
  public DerivativeNode derivative(String respectTo) {
    DerivativeNode[] Adot=new DerivativeNode[A.length];
    for(int i=0;i<A.length;i++) Adot[i]=A[i].derivative(respectTo).simplify();
    return new MultiSum(Adot).simplify();
  }
  @Override
  public double evaluate(Map<String, Double> varValues) {
    double result=0;
    for(DerivativeNode a:A) result+=a.evaluate(varValues);
    return result;
  }
  public DerivativeNode simplify() {
    double constant=0;
    List<DerivativeNode> other=new LinkedList<DerivativeNode>();
    for(DerivativeNode a:A) {
      if(a instanceof Constant) {
        constant+=((Constant)a).evaluate();
      } else if(a instanceof MultiSum) {
        for(DerivativeNode b:((MultiSum)a).getA()) {
          other.add(b.simplify());
        }
      } else {
        other.add(a.simplify());
      }
    }
    if(other.size()==0) return new Constant(constant);
    if(constant!=0.0) other.add(new Constant(constant));
    if(other.size()==1) return other.get(0);
    if(other.size()==2) return new Sum(other.get(0),other.get(1));
    return new MultiSum(other.toArray(new DerivativeNode[0]));
  }
  public static void main(String args[]) {
    Variable V=new Variable("V");
    Map<String,Double> varValues=new LinkedHashMap<String,Double>();
    varValues.put("V",1.23456);
    MultiSum S=new MultiSum(new DerivativeNode[] {V,V,V});
    System.out.println(S.simplify());
    System.out.println(S.derivative("V"));
    System.out.println(S.derivative("X"));
  }

  public int precedence() {
    return 1;
  }

}
