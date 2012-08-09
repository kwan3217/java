package org.kwansystems.algebra;

import java.util.*;

public class MultiProduct extends MultiOperator {
  public MultiProduct(DerivativeNode[] LnodeA) {
    super(LnodeA,'*');
  }
  public DerivativeNode toTree() {
    DerivativeNode result=A[0].simplify();
    for(int i=1;i<A.length;i++) result=new Product(result,A[i].simplify());
    return result;
  }
  @Override
  public DerivativeNode derivative(String respectTo) {
    //Turn it back into a tree of Products then do the derivative on it
    DerivativeNode tree=toTree();
    //Important! We are not simplifying the tree before differentiating it,
    //since that will turn it back into a MultiProduct
    DerivativeNode result=tree.derivative(respectTo).simplify();
    return result;
  }
  @Override
  public double evaluate(Map<String, Double> varValues) {
    double result=1.0;
    for(DerivativeNode a:A) result*=a.evaluate(varValues);
    return result;
  }
  public DerivativeNode simplify() {
    double constant=1;
    LinkedList<DerivativeNode> other=new LinkedList<DerivativeNode>();
    for(DerivativeNode a:A) {
      if(a instanceof Constant) {
        constant*=((Constant)a).evaluate();
      } else if(a instanceof MultiProduct) {
        for(DerivativeNode b:((MultiProduct)a).getA()) {
          other.add(b.simplify());
        }
      } else {
        other.add(a.simplify());
      }
    }
    if(other.size()==0) return new Constant(constant);
    if(constant!=1.0) other.addFirst(new Constant(constant));
    if(other.size()==1) return other.get(0);
    if(other.size()==2) return new Product(other.get(0),other.get(1));
    return new MultiProduct(other.toArray(new DerivativeNode[0]));
  }
  public static void main(String args[]) {
    Variable V=new Variable("V");
    Map<String,Double> varValues=new LinkedHashMap<String,Double>();
    varValues.put("V",1.23456);
    MultiProduct S=new MultiProduct(new DerivativeNode[] {V,V,V});
    System.out.println(S.simplify());
    System.out.println(S.derivative("V"));
    System.out.println(S.derivative("X"));
  }

  public int precedence() {
    return 1;
  }

}
