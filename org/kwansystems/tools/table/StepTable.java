package org.kwansystems.tools.table;

/**
 * Step table. The interpolated dependent value at each point is 
 * the same as the dependent value corresponding to the closest
 * independent value to the point.
 */
public class StepTable extends Table {
  /**
   * Create a new StepTable.
   * @param LX Independent variable values
   * @param LY Dependent variable values
   */
  public StepTable(double[]LX,double[][]LY) {super(LX,LY);}
  /**
   * Create a new StepTable.
   * @param X0 Lower end independent value
   * @param X1 Upper end independent value
   * @param LY Dependent variable values
   */
  public StepTable(double X0, double X1,double[][]LY) {
    super(X0,X1,LY);
  }
  /**
   * Interpolate the table data by getting the TPIR independent value for Xpt 
   * and returning the corresponding dependent value.
   * arbitrary independent variable
   * @param Xpt Independent variable value
   * @return Interpolated dependent variable value
   */
  public double Interp(double Xpt, int column) {
    int i=TPIR(Xpt);
    if (i<0) i=0; //use the first point to extrapolate before
    if (i+1>=X.length) i=X.length-1; //use last point to extrapolate beyond
    return (Y[column][i]);
  }
}
