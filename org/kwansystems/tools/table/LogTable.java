package org.kwansystems.tools.table;

import org.kwansystems.tools.Scalar;
import org.kwansystems.tools.vector.*;
import static java.lang.Math.*;

/**
 * Table which uses linear interpolation to get in-between values.
 */
public class LogTable extends Table {
  /**
   * Create a new LinearTable.
   * @param LX Independent variable values
   * @param LY Dependent variable values
   */
  public LogTable(double[]LX,double[][]LY) {
    super(LX,LY);
  }
  /**
   * Create a new LinearTable.
   * @param X0 Lower end independent value
   * @param X1 Upper end independent value
   * @param LY Dependent variable values
   */
  public LogTable(double X0, double X1,double[][]LY) {
    super(X0,X1,LY);
  }
  /**
   * Interpolate the table data by getting the two independent values which 
   * bracket Xpt and returning the linear interpolation between them.
   * Extrapolates by using the first two points to extrapolate before,
   * and the last two points to extrapolate after
   * @param Xpt Independent variable value
   * @return Interpolated dependent variable value
   */
  public double Interp(double Xpt,int column) {
    int i=TPIR(Xpt);
    if (i<0) i=0; //use the first 2 points to extrapolate before
    if (i+1>=X.length) i=X.length-2; //use last 2 points to extrapolate beyond
    return exp(Scalar.linterp(X[i],log(Y[column][i]),X[i+1],log(Y[column][i+1]),Xpt));
  }
}
