package org.kwansystems.algebra;

public abstract class MultiOperator extends DerivativeNode {
  protected final DerivativeNode[] A;
  protected final char opSymbol;
  public String toString(DerivativeNode[] LA, char LopSymbol) {
    String result="";
    String sA=LA[0].toString();
    if(LA[0].precedence()<this.precedence()) sA="("+sA+")";
    result=sA;
    for(int i=1;i<LA.length;i++) {
      sA=LA[i].toString();
      if(LA[i].precedence()<this.precedence()) sA="("+sA+")";
      result=result+LopSymbol+sA;
    }
    return result;
  }
  @Override
  public String toString() {
    return toString(A,opSymbol);
  }
  protected MultiOperator(DerivativeNode[] LnodeA, char LopSymbol) {
    A=new DerivativeNode[LnodeA.length];
    for(int i=0;i<A.length;i++) A[i]=LnodeA[i].simplify();
    opSymbol=LopSymbol;
  }
  public DerivativeNode[] getA() {
    DerivativeNode[] result=new DerivativeNode[A.length];
    for(int i=0;i<A.length;i++) result[i]=A[i].simplify();
    return result;
  }
}
