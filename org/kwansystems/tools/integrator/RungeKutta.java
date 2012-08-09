package org.kwansystems.tools.integrator;

import org.kwansystems.tools.vector.*;

/**
 * Numerical integrator using the generalized Runge-Kutta method. This uses
 * a Butcher Tableau to perform any explicit Runge-Kutta integration of any order. 
 */
public class RungeKutta extends Integrator {
  protected double[][] a,b;
  protected double[] c;
  /**
   * Creates new Runge-Kutta Integrator
   * 
   * @param StartT Initial value of independent variable
   * @param LX Initial conditions
   * @param LdT Initial step size
   * @param LD DerivativeSet describing differential equation
   * @param B Butcher Tableau
   */
  public RungeKutta(double StartT, MathVector LX, double LdT, DerivativeSet LD,ButcherTableau B) {
    super(StartT,LX,LdT,LD);
    a=B.getA();
    b=B.getB();
    c=B.getC();
  }
  public RungeKutta(double StartT, MathVector LX, double LdT, DerivativeSet LD) {
    this(StartT,LX,LdT,LD,RationalButcherTableau.RK4Tableau);
  }
  public MathVector[] calcSlopes(double h) {
    MathVector[] k=new MathVector[c.length];
    calcSlopes(h,k,0,c.length);
    return k;
  }
  public void calcSlopes(double h, MathVector[] k, int start, int length) {
    MathVector Xi=new MathVector(X.dimension());
    for(int i=start;i<start+length;i++) {
      double Ti=T+c[i]*h;
      Xi.set(X);
      for(int j=0;j<a[i].length;j++) {
        if(a[i][j]!=0) Xi.addScaleEq(k[j],h*a[i][j]);
      }
      k[i]=D.dxdt(Ti,Xi,i==0);
    }
    LastDxDt=k[0];
  }
  public MathVector combineSlopes(MathVector[] k,double[] bb) {
    MathVector K=new MathVector(k[0].dimension());
    for(int j=0;j<bb.length;j++) {
      if(bb[j]!=0) K.addScaleEq(k[j],bb[j]);
    }
    return K;
  }
  public MathVector combineSlopes(MathVector[] k,int i) {
    return combineSlopes(k,b[i]);
  }
  /**
   * Take exactly one step forward
   * @param h Step size
   */
  public void stepGuts(double h) {
    MathVector[] k=calcSlopes(h);
    MathVector K=combineSlopes(k,0);
    X.addScaleEq(K,h);
  }
  
}

