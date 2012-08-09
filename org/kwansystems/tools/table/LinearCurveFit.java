package org.kwansystems.tools.table;
import org.kwansystems.tools.rotation.MathMatrix;
import org.kwansystems.tools.vector.*;

/**
 * One-dimensional Linear least-squares fit. Fits data to a model 
 * y=sum(a[i]*basis(x,i),i=0..ma-1).
 */
public abstract class LinearCurveFit extends CurveFit{
  /**
   * Construct a new LinearCurveFit object.
   */
  public LinearCurveFit() {
  }
  /**
   * Performs the least-squares fit.
   * @param x independent variable values
   * @param y dependent variable values
   * @param ma Maximum order of model
   * @return A vector coefficients which describes the data
   */
  public MathVector Fit(MathVector x, MathVector y, int ma) {
    return A(x,ma).pinv().transform(y);
  }
  /**
   * Calculates the design matrix. The design matrix is a matrix containing the 
   * value of the all the basis functions evaluated at all the values of the 
   * independent variable.
   * @param x Independent variable values
   * @param ma Maximum model order
   * @return Design matrix. Columns correspond to basis functions, rows 
   * to values of the independent variable.
   */
  private MathMatrix A(MathVector x,int ma) {
	double[][] result=new double[x.dimension()][ma];
    for(int row=0;row<x.dimension();row++) {
      for(int col=0;col<ma;col++) {
        result[row][col]=Basis(x.get(row),col);
      }
    }
    return new MathMatrix(result);
  }
  /**
   * Evaluates the model.
   * @param x Point at which to evaluate the model
   * @param coeff A list of model coefficients.
   * @return Value of the model at this point
   */
  public double Evaluate(double x,MathVector coeff) {
    double accumulator=0;
    for(int i=0;i<coeff.dimension();i++) {
      accumulator+=coeff.get(i)*Basis(x,i);
    }
    return accumulator;
  }
  /**
   * Descendants override this with the particular basis functions
   * @param x Independent variable
   * @param order Basis function order
   * @return Dependent value of this basis function at x
   */
  protected abstract double Basis(double x, int order);
  /**
   * Test code.
   * @param args Command line arguments
   */
  public static void main(String[] args) {
    LinearCurveFit R=new LinearCurveFit() {
      protected double Basis(double x, int order) {
        return Math.pow(x,order);
      }
    };
    MathVector coeffs=R.Fit(
      new MathVector(new double[] {1,2,3,4,5,6,7,8}),
      new MathVector(new double[] {1,1,2,3,3,4,4,6}),
      2
    );
    System.out.println(coeffs);
  }
}
