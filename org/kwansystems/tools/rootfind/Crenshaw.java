package org.kwansystems.tools.rootfind;

/**
 * Implementation of Jack Crenshaw's <A href="http://www.embedded.com/showArticle.jhtml?articleID=9900570">
 * "World's Best Root Finder"</a> in Java
 */
public class Crenshaw extends RootFind {

  /**
   * A value of the independent variable which is less than the root
   */
  private double x0;
  /**
   * Bisection of the current bracket
   */
  private double x1;
  /**
   * A value of the independent variable which is greater than the root
   */
  private double x2;
  /**
   * Value of function at x0
   */
  private double y0;
  /**
   * Value of function at y1
   */
  private double y1;
  /**
   * Value of function at x2
   */
  private double y2;

  /**
   * Linear term of parabola
   */
  private double b;

  /**
   * Quadratic term of parabola
   */
  private double c;

  private double temp, y10, y20, y21;
  /**
   * Current best estimate of root value
   */
  double xm;
  /**
   * Function value at current best estimate of root value
   */
  double ym;
  /**
   * Previous value of xm
   */
  double xmlast = x0;
  /**
   * Construct a new Crenshaw object
   * @param LEpsilon Convergence tolerance
   * @param LConvMethod Convergence Method
   * @param LR Function to find root for
   */
  public Crenshaw(double LEpsilon, Method LConvMethod, RootFunction LR) {
    super(LEpsilon,LConvMethod,LR);
  }
  public Crenshaw(double LEpsilon, RootFunction LR) {
    super(LEpsilon,LR);
  }
  public Crenshaw(RootFunction LR) {
    super(LR);
  }
  /**
   * Set up the root finder
   * @param Lx0 Value of independent variable which is a lower bracket of the desired root
   * @param Lx2 Value of independent variable which is an upper bracket of the desired root
   * @throws org.kwansystems.tools.rootfind.LuckOut If one of the bracket values happens to meet the convergence criterion
   */
  protected void init(double Lx0, double Lx2) throws LuckOut {
    x0=Lx0;
    x2=Lx2;
    xmlast=x0;
    y0=eval(x0);
    y2=eval(x2);
    if(y2*y0 > 0.0) throw new ArithmeticException("Root not bracketed");
  }
  /**
   * Take one step with the root finder
   * @throws org.kwansystems.tools.rootfind.LuckOut If one of its working values happens to meet the convergence criterion.
   * @return Dependent variable at current best estimate of root
   */
  protected double Step() throws LuckOut {
    x1 = 0.5 * (x2 + x0);
    y1 = eval(x1);
    if (y1 * y0 > 0.0) {
      temp = x0;
      x0 = x2;
      x2 = temp;
      temp = y0;
      y0 = y2;
      y2 = temp;
    }
    y10 = y1 - y0;
    y21 = y2 - y1;
    y20 = y2 - y0;
    if (y2 * y20 <2.0 * y1 * y10) {
      x2 = x1;
      y2 = y1;
    } else {
      b = (x1 - x0) / y10;   
      c = (y10 -y21) / (y21 * y20); 
      xm = x0 - b * y0 * (1.0-c * y1);
      ym = eval(xm);
      xmlast = xm;
      if (ym * y0 < 0.0) {
        x2 = xm;
        y2 = ym;
      } else {
        x0 = xm;
        y0 = ym;
        x2 = x1;
        y2 = y1;
      }
    }
    return ym;
  }
  /**
   * Get the system's best estimate of the root.
   * @return Best estimate of root of function
   */
  protected double GetX() {
    return xm;
  }
  protected String PrintState() {
    return   "x0: "+x0+
           "\nx1: "+x1+
           "\nx2: "+x2+
           "\ny0: "+y0+
           "\ny1: "+y1+
           "\ny2: "+y2;
  }
  /**
   * Test out the World's Best Root Finder
   * @param args Command line parameters
   */
  public static void main(String[] args) {
    double result=RootFind.Find(
      new Crenshaw(1e-10,
        new RootFunction() {
          public double F(double x) {
            return Math.exp(-x);
          }
        }
      ),
      0.5,
      0,
      2
    );
    System.out.println(result);
    System.out.println(Math.log(2));
  }
}
