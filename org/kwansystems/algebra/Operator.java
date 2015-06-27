package org.kwansystems.algebra;

public abstract class Operator extends DerivativeNode {
  protected final DerivativeNode A;
  protected final DerivativeNode B;
  protected final char opSymbol;
  public String toString(DerivativeNode LA, char LopSymbol, DerivativeNode LB) {
    String sA=LA.toString();
    if(LA.precedence()<this.precedence()) sA="("+sA+")";
    String sB=LB.toString();
    if(LB.precedence()<this.precedence()) sB="("+sB+")";
    return sA+LopSymbol+sB;
  }
  @Override
  public String toString() {
    return toString(A,opSymbol,B);
  }
  protected Operator(DerivativeNode LnodeA, DerivativeNode LnodeB, char LopSymbol) {
    A=LnodeA.simplify();
    B=LnodeB.simplify();
    opSymbol=LopSymbol;
  }
  public DerivativeNode getA() {
    return A.simplify();
  }
  public DerivativeNode getB() {
    return B.simplify();
  }
}
