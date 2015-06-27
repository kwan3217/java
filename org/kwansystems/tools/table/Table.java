package org.kwansystems.tools.table;

import org.kwansystems.tools.Scalar;
import org.kwansystems.tools.vector.*;

/**
 * Abstract class defining a function described by a table of 
 * independent values and the dependent values which go with them.
 * <p>
 * The core data is two arrays of numbers of identical length, X
 * and Y. In order for certain features to work properly, the X table
 * must be sorted in ascending order.
 */
public abstract class Table {
  /**
   * List of independent values. It is critical for proper
   * interpolation and integration that these values be sorted
   * from least to greatest.
   */
  protected double[] X;
  /**
   * List of dependent values. This array must be the same length as
   * {@link Table#X}, and each element corresponds to the independent value
   * element of the same index.
   */
  protected double[][] Y;
  /**
   * Construct a new Table. The two arrays passed must be of identical length,
   * and 
   * @param LX Independent variable values
   * @param LY Dependent variable values
   */
  public Table(double[]LX, double[][]LY){
    if(LX.length!=LY[0].length) throw new IllegalArgumentException("Table Column lengths do not match (X="+LX.length+", Y="+LY[0].length+")");
    X=LX;Y=LY;
  }
  /**
   * Construct a new Table, with a range of independent variable. Table
   * will have the independent value vary linearly over X0 to X1, with 
   * the same number of elements as LY
   *
   * @param X0 Lower end independent value
   * @param X1 Upper end independent value
   * @param LY Dependent variable values
   */
  public Table(double X0, double X1, double[][]LY){
	X=new double[LY[0].length];
	for(int i=0;i<LY[0].length;i++) {
	  X[i]=Scalar.linterp(0,X0,LY[0].length-1,X1,i);
	}
    Y=LY;
  }
  /**
   * Finds the index into the {@link Table#X} array to the closest value without going
   * over the given value. Table must be sorted on X for this to work.
   * @param Xpt Value to find
   * @return Index of closest value that doesn't go over
   */
  public int TPIR(double Xpt) {
    for(int i=0;i<X.length;i++) if(X[i]>Xpt) return i-1;
    return X.length-1;
  }
  /**
   * Interpolate the table data and get a dependent value for any
   * arbitrary independent variable
   * @param Xpt Independent variable value
   * @return Interpolated dependent variable value
   */
  public abstract double Interp(double Xpt, int column);
  public double[] Interp(double Xpt) {
    double[] result=new double[Y.length];
    for(int i=0;i<Y.length;i++) {
      result[i]=Interp(Xpt,i);
    }
    return result;
  }
  /**
   * Returns the integral of the function func from a to b. The parameters EPS 
   * can be set to the desired fractional accuracy and JMAX so that 2^(JMAX-1)
   * is the maximum allowed number of steps. Integration is performed by the 
   * trapezoidal rule.
   * @param a Lower bound of integration
   * @param b Upper bound of integration
   * @return Integral of function
   */
  public double Integrate(double a, double b, int column) {
    final double EPS=1.0e-5;
    final int JMAX=10;
    int j;
    double s=0;
    double olds;
    olds = -1.0e30; //Any number that is unlikely to be the average of the
                    //function at its endpoints will do here.
    for (j=1;j<=JMAX;j++) {
      double x,tnm,sum,del;
      int it,j2;
      if (j == 1) {
        s=0.5*(b-a)*(Interp(a,column)+Interp(b,column));
      } else {
        for (it=1,j2=1;j2<j-1;j2++) it <<= 1;
        tnm=it;
        del=(b-a)/tnm; //This is the spacing of the points to be added.
        x=a+0.5*del;
        for (sum=0.0,j2=1;j2<=it;j2++,x+=del) sum += Interp(x,column);
        s=0.5*(s+(b-a)*sum/tnm); //This replaces s by its rened value.
      }
      if (Math.abs(s-olds) < EPS*Math.abs(olds) || (s == 0.0 && olds == 0.0)) return s;
      olds=s;
    }
    return(s);
  }
  public double[] Integrate(double a, double b) {
    double[] result=new double[Y.length];
    for(int i=0;i<Y.length;i++) {
      result[i]=Integrate(a,b,i);
    }
    return result;
  }
  /**
   *Scales Y table so that int(Interp(x) dx) from x=xmin to x=xmax is (xmax-xmin)
   *This makes the average value 1.0 . X values must be sorted for this to work
   */
  public void Normalize(int column) {
    double xmin=X[0];
    double xmax=X[X.length-1];
    double xext=xmax-xmin;
    double area=Integrate(xmin,xmax,column)/xext;
    for(int i=0;i<Y[column].length;i++) {
      Y[column][i]/=area;
    }
  }
}
