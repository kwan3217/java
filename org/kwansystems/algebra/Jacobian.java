package org.kwansystems.algebra;

/**
 * Given a 1D m-size array of expressions and a 1D n-size array of variables, 
 * return a 2D m x n array of expressions representing the derivative of each
 * expression relative to each variable
 * 
 */
public class Jacobian {
  public static DerivativeNode[][] J(DerivativeNode[] F, String[] X) {
    int m=F.length;
    int n=X.length;
    DerivativeNode[][] dFdX=new DerivativeNode[m][n];
    for(int i=0;i<m;i++) {
      for(int j=0;j<n;j++) {
        dFdX[i][j]=F[i].derivative(X[j]).simplify();
      }
    }
    return dFdX;
  }
  public static void main(String[] args) {
    //DerivativeNode.inJava=true;
    DerivativeNode[] F=new DerivativeNode[] {
//      Parser.parse("U"),
//      Parser.parse("V"),
      Parser.parse("(-1)*mu*X/((X^2+Y^2)^(1/2))^3")
//      Parser.parse("(-1)*mu*Y/((X^2+Y^2)^(1/2))^3"),
//      Parser.parse("0"),
//      Parser.parse("0"),
//      Parser.parse("0")
    };
    String[] X=new String[] {
//      "X",
//      "Y",
//      "U",
//      "V",
      "mu"
//      "Xs",
//      "Ys"
    };
    for(int i=0;i<F.length;i++) {
      System.out.println(F[i].simplify());
    }
    System.out.println("--");
    DerivativeNode[][] J=Jacobian.J(F, X);
    int row=0;
    for(int i=0;i<J[row].length;i++) {
      System.out.println(J[row][i]);
    }
  }
}
