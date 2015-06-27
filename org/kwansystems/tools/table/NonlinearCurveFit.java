package org.kwansystems.tools.table;

import org.kwansystems.tools.rootfind.optimize.Amoeba;
import org.kwansystems.tools.rootfind.optimize.OptimizeMultiDFunction;
import org.kwansystems.tools.Scalar;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.rootfind.*;
import org.kwansystems.tools.vector.*;
import static java.lang.Math.*;

/**
 * One-dimensional non-linear least-squares fit. Fits data to a model 
 * y=f(x;a[]).
 */
public abstract class NonlinearCurveFit extends CurveFit{
  /**
   * Construct a new LinearCurveFit object.
   */
  protected MathVector guess;
  public NonlinearCurveFit() {
    this(new MathVector());
  }
  public NonlinearCurveFit(MathVector Lguess) {
    guess=new MathVector(Lguess);
  }
  /**
   * Performs the least-squares fit.
   * @param x independent variable values
   * @param y dependent variable values
   * @param ma Maximum order of model
   * @return A vector coefficients which describes the data
   */
  public MathVector Fit(final MathVector x, final MathVector y, int ma) {
    OptimizeMultiDFunction Squares=new OptimizeMultiDFunction() {
      public double eval(double[] args) {
        double result=0;
        for(int i=0;i<x.dimension();i++) {
          result+=Math.pow(y.get(i)-Evaluate(x.get(i),new MathVector(args)),2);
        }
        return result;
      }
    };
    return new MathVector(Amoeba.amoeba(Amoeba.amoeba(guess.get(),Squares),Squares));
  }
  /**
   * Test code.
   * @param args Command line arguments
   */
  public static void main(String[] args) {
    ChartRecorder C=new ArrayListChartRecorder(100,2,"x");
    CurveFit CF=new NonlinearCurveFit(new MathVector(1,1,1)) {
      public double Gaussian(double x, double mu, double sigma, double amp) {
        return amp*exp(-pow((x-mu)/(sigma),2)/2.0);  
      }
      public double Gaussian(double x, double mu, double sigma) {
        return Gaussian(x,mu,sigma,1/(sigma*sqrt(2*PI)));
      }
      public double Evaluate(double x,MathVector coeff) {
        return Gaussian(x,coeff.get(0),coeff.get(1),coeff.get(2));
      }
    };
    double[] x=new double[100];
    double[] y=new double[100];
    MathVector coeff=new MathVector(1, 0.75,2);
    System.out.println(coeff);
    for(int i=0;i<x.length;i++) {
      x[i]=Scalar.linterp(0,-5,x.length, 5, i);
      y[i]=CF.Evaluate(x[i], coeff);
    }
    MathVector coeffFit=CF.Fit(x,y,coeff.dimension());
    System.out.println(coeffFit);
    for(int i=0;i<x.length;i++) {
      C.Record(x[i], "ydata",null, y[i]);
      C.Record(x[i], "ymodel", null, CF.Evaluate(x[i],coeffFit));
    }
    C.PrintSubTable(new String[] {"ydata","ymodel"},new DisplayPrinter());
  }

  public MathVector getGuess() {
    return guess;
  }

  public void setGuess(MathVector guess) {
    this.guess = guess;
  }
}
