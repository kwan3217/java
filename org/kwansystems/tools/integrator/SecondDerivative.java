package org.kwansystems.tools.integrator;

import org.kwansystems.tools.vector.*;

public class SecondDerivative extends DerivativeSet {
  int Dim;
  DerivativeSet[] D;
  public SecondDerivative(int LDim, DerivativeSet[] LD) {
    Dim=LDim;
    D=LD;
  }
  public MathVector dxdt(double T, MathVector X, boolean IsMajor) {
    int i,j;
    double[] xdd=new double[Dim];
    for(i=0;i<D.length;i++) {
      MathVector Acceleration=D[i].dxdt(T,X, IsMajor);
      for(j=0;j<Dim;j++) xdd[j]+=Acceleration.get(j);
    }
    return new MathVector(new MathVector[] {X.subVector(Dim,Dim),new MathVector(xdd)});
  }
}
