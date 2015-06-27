package org.kwansystems.tools.table;
import org.kwansystems.tools.vector.*;

/**
 * Abstract curve-fitting object.
 */
public abstract class CurveFit {
  /**
   * Construct a new CurveFit object.
   */
  public CurveFit() {
  }
  /**
   * Performs the least-squares fit.
   * @param x independent variable values
   * @param y dependent variable values
   * @param ma Maximum order of model
   * @return A vector coefficients which describes the data
   */
  public abstract MathVector Fit(MathVector x, MathVector y, int ma);
  /**
   * Performs the least-squares fit.
   * @param x independent variable values
   * @param y dependent variable values
   * @param ma Maximum order of model
   * @return A vector of coefficients which describes the data
   */
  public MathVector Fit(double[] x, double[] y, int ma) {
    return Fit(new MathVector(x),new MathVector(y),ma);
  }
  /**
   * Evaluates the model.
   * @param x Point at which to evaluate the model
   * @param coeff A list of model coefficients.
   * @return Value of the model at this point
   */
  public abstract double Evaluate(double x,MathVector coeff);
}
