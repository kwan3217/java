package org.kwansystems.tools.table;

/**
 * Table which uses quadratic interpolation to get in-between values
 */
public class QuadraticTable extends Table {
  /**
   * Create a new QuadraticTable.
   * @param LX Independent variable values
   * @param LY Dependent variable values
   */
  public QuadraticTable(double[]LX,double[][]LY) {
    super(LX,LY);
  }
  /**
   * Create a new QuadraticTable.
   * @param X0 Lower end independent value
   * @param X1 Upper end independent value
   * @param LY Dependent variable values
   */
  public QuadraticTable(double X0, double X1,double[][]LY) {
    super(X0,X1,LY);
  }
  /**
   * Interpolate the table data by getting the two independent values which 
   * bracket Xpt and the one after that, and returning the quadratic
   * interpolation of those points.
   * @param Xpt Independent variable value
   * @return Interpolated dependent variable value
   */
  public double Interp(double Xpt, int column) {
    int i=TPIR(Xpt);
    if (i<0) i=0; //use the first 3 points to extrapolate before
    if (i+2>=X.length) i=X.length-3; //use last 3 points to extrapolate beyond
    double P0=(Xpt-X[i+1])*(Xpt-X[i+2])/((X[i+0]-X[i+1])*(X[i+0]-X[i+2]))*Y[column][i+0];
    double P1=(Xpt-X[i+0])*(Xpt-X[i+2])/((X[i+1]-X[i+0])*(X[i+1]-X[i+2]))*Y[column][i+1];
    double P2=(Xpt-X[i+0])*(Xpt-X[i+1])/((X[i+2]-X[i+0])*(X[i+2]-X[i+1]))*Y[column][i+2];
    return P0+P1+P2;
  }
}
