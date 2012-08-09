package org.kwansystems.tools.table;

import org.kwansystems.tools.vector.*;

/**
 * Table which uses a general least-squares fit get in-between values.
 */
public class RegressionTable extends Table {
  /**
   * LinearCurveFit model to fit the data
   */
  CurveFit[] R;
  /**
   * Results of regression fit
   */
  MathVector[] Coeffs;
  /**
   * Create a new RegressionTable.
   * @param LX Independent variable values
   * @param LY Dependent variable values
   * @param LR LinearCurveFit object describing the model
   * @param ma Model order
   */
  public RegressionTable(double[] LX, double[][] LY, CurveFit[] LR, int ma) {
    super(LX,LY);
    FitModel(LR,ma);
  }
  private void FitModel(CurveFit[] LR, int ma) {
    R=LR;
    for(int i=0;i<R.length;i++)Coeffs[i]=R[i].Fit(X,Y[i],ma);
  }
  /**
   * Create a new RegressionTable.
   * @param X0 Lower end independent value
   * @param X1 Upper end independent value
   * @param LY Dependent variable values
   */
  public RegressionTable(double X0, double X1,double[][]LY, CurveFit[] LR, int ma) {
    super(X0,X1,LY);
    FitModel(LR,ma);
  }
  /**
   * Interpolates the data by evaluating the regression model at this point.
   * @param x Value of the independent variable
   * @return Interpolated value of the dependent variable
   */
  public double Interp(double x, int column) {
    return R[column].Evaluate(x, Coeffs[column]);
  }
}
