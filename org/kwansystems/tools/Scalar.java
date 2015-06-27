package org.kwansystems.tools;

import static java.lang.Math.*;
import static org.kwansystems.tools.Scalar.dmsToRadians;
import static org.kwansystems.tools.Scalar.rdmsToRadians;
import static org.kwansystems.tools.Scalar.RootMode.*;

public class Scalar {
  public static enum RootMode {Minimum,Maximum,MinAbs,MaxAbs,MinGTZ,MaxLTZ};
  public static double linterp(double X1, double Y1, double X2, double Y2, double X) {
    return (Y1+(Y2-Y1)*(X-X1)/(X2-X1));
  }
  public static double ChooseRoot(double[] roots, RootMode Mode) {
    double Extreme=0;
    boolean ChoseRoot=false;
    switch(Mode) {
      case Minimum:
        Extreme=Double.POSITIVE_INFINITY;
        for(int i=0;i<roots.length;i++)Extreme=(roots[i]<Extreme)?roots[i]:Extreme;
      case Maximum:
        Extreme=Double.NEGATIVE_INFINITY;
        for(int i=0;i<roots.length;i++)Extreme=(roots[i]>Extreme)?roots[i]:Extreme;
      case MinAbs:
        Extreme=Double.POSITIVE_INFINITY;
        for(int i=0;i<roots.length;i++)Extreme=(abs(roots[i])<abs(Extreme))?roots[i]:Extreme;
      case MaxAbs:
        Extreme=0.0;
        for(int i=0;i<roots.length;i++)Extreme=(abs(roots[i])>abs(Extreme))?roots[i]:Extreme;
      case MinGTZ:
        Extreme=Double.POSITIVE_INFINITY;
        for(int i=0;i<roots.length;i++) {
          if(roots[i]>0 && roots[i]<Extreme) {
            Extreme=roots[i];
            ChoseRoot=true;
          }
        }
        if(!ChoseRoot)throw new IllegalArgumentException("No root greater than zero");
      case MaxLTZ:
        Extreme=Double.NEGATIVE_INFINITY;
        for(int i=0;i<roots.length;i++) {
          if(roots[i]<0 && roots[i]>Extreme) {
            Extreme=roots[i];
            ChoseRoot=true;
          }
        }
        if(!ChoseRoot)throw new IllegalArgumentException("No root less than zero");
    }
    return Extreme;
  }
  /** Solves the quadratic equation ax^2+bx+c=0.
   * @param a quadratic term coefficient
   * @param b linear term coefficient
   * @param c constant term coefficient
   * @return an array with the real roots. If the discriminant is exactly zero, the two roots will be exactly the same.
   * @throws IllegalArgumentException if there are no real roots
   */
  public static double[] QuadFormula(double a, double b, double c) throws IllegalArgumentException {
    double d=b*b-4.0*a*c;
    if(d<0) throw new IllegalArgumentException("No real roots, discriminant="+d);
    double q=-(b+signum(b)*sqrt(d));
    return new double[] {q/a,c/q};
  }
  /**Solve cubic equation qx^3+ax^2+bx+c=0
  * @param q cubic term coefficient
  * @param a quadratic term coefficient
  * @param b linear term coefficient
  * @param c constant term coefficient
  * @return an array with the real roots. There will be either one or three real roots, 
  *         and the result will have a length 1 or 3 as appropriate.
  */
  public static double[] CubicFormula(double q, double a, double b, double c) {
    //If q is zero, this is just a quadratic equation
    if(q==0) return QuadFormula(a,b,c);
    //Normalize coefficients, solve equation x^3+a'x^2+b'x+c'=0
    //Coefficients are just replaced with their new values.
    a=a/q;
    b=b/q;
    c=c/q;
    double Q=(a*a-3.0*b)/9.0;
    double R=(2.0*a*a*a-9.0*a*b+27.0*c)/54.0;
    double D=R*R-Q*Q*Q;
    if(D<0) {
      //Three real roots
      double theta=acos(R/sqrt(Q*Q*Q));
      double coeff=-2*sqrt(Q);
      return new double[] {
        coeff*cos(theta/3.0)-a/3.0,
        coeff*cos((theta+2*PI)/3.0)-a/3.0,
        coeff*cos((theta-2*PI)/3.0)-a/3.0
      };
    } else {
      //One real root
      double A=-signum(R)*pow(abs(R)+sqrt(D),1.0/3.0);
      double B=(A==0)?0:Q/A;
      return new double[]{(A+B)-a/3.0};
    }
  }
  
  /** Calculates the mod function as done in matlab. This will
   * always return a positive number between 0 and not quite y.
   * @param x Numerator
   * @param y Denominator
   * @return Positive remainder of x/y
   */
  public static double mlmod(double x, double y) {
    return x - Math.floor(x/y)*y;
  }
  public static long mlmod(long x, long y) {
    return x -mldiv(x,y)*y;
  }
  /** Properly calculates integer division.
   * @param x
   * @param y
   * @return nearest whole integer less than or equal to the true quotient
   */
  public static long mldiv(long x, long y) {
    return (x >= 0) ? (x / y) : ( (x - y + 1 ) / y );
  }
  public static void main(String[] args) {
    double[] roots=CubicFormula(1,0,0,-46);
    for(int i=0;i<roots.length;i++) System.out.println(roots[i]);
  }
  public static double rdmsToRadians(double r, double d, double m, double s) {
    return Math.toRadians(rdmsToDegrees(r,d,m,s));
  }
  public static double dmsToRadians(double d, double m, double s) {
    return rdmsToRadians(0,d,m,s);
  }
  public static double rdmsToDegrees(double r, double d, double m, double s) {
    return r*360.0+d+m/60.0+s/3600.0;
  }
  public static double dmsToSeconds(double d, double m, double s) {
    return rdmsToSeconds(0,d,m,s);
  }
  public static double rdmsToSeconds(double r, double d, double m, double s) {
    return r*360.0*3600.0+d*3600.0+m*60.0+s;
  }
  public static double dmsToDegrees(double d, double m, double s) {
    return rdmsToDegrees(0,d,m,s);
  }
  public static double hmsToRadians(double h, double m, double s) {
    return rdmsToRadians(0,h,m,s)*15;
  }
  public static double sToRadians(double ss) {
    return dmsToRadians(0,0,ss);
  }
  public static double tsToRadians(double ts) {
    return toRadians(ts/240.0);
  }
}
