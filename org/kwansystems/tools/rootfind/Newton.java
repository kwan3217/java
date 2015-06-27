package org.kwansystems.tools.rootfind;

/**
 * Newton's Method root finder. This root finder uses the 
 * function's value and derivative at each point. It is 
 * very fast when it works, but it is quite unstable.
 * There are some functions like cube root, that it just 
 * doesn't like. It is easy for the method to diverge 
 * instead of converging. And it requires a derivative
 * calculation at each point. So, just use it for known
 * functions with known good properties, basically where  
 * it is expressly called for in an algorithm.
 */
public class Newton extends RootFind {

  /**
   * Current independent value
   */
  private double X;

  /**
   * Current Dependent value
   */
  private double Y;

  /**
   * Current slope
   */
  private double DyDx;
  public Newton(double LEpsilon, Method LConvMethod, RootFunction LR) {
    super(LEpsilon,LConvMethod,LR);
  }
  public Newton(double LEpsilon, RootFunction LR) {
    super(LEpsilon,LR);
  }
  public Newton(RootFunction LR) {
    super(LR);
  }
  /**
   * Sets up Newton root finder.
   * @param LXlo Initial guess of root.
   * @param LXhi Ignored in Newton.
   */
  protected void init(double LXlo, double LXhi) {
    X=LXlo;
  }
  /**
   * Takes one step towards convergence
   * @throws org.kwansystems.tools.rootfind.LuckOut if the function evaluation is within tolerance of a root
   * @return Dependent value at latest guess for root
   */
  protected double Step() throws LuckOut {
    Y=eval(X);
    DyDx=evalp(X);
    X=(X-Y/DyDx);
    return Y;
  }
  /**
   * After convergence, get best estimate of root
   * @return Best estimate of independent variable which is a root.
   */
  protected double GetX() {
    return X;
  }
  /**
   * For debugging purposes.
   * @return A string representation of the root finder's internal state.
   */
  protected String PrintState() {
    return   "X:    "+X+
           "\nY:    "+Y+
           "\nDyDx: "+DyDx;
  }
  /**
   * Test out the Newton Root Finder
   * @param args Command line parameters
   */
  public static void main(String[] args) {
    RootFind R=new Newton(
      new RootDeriv() {
        public double F(double x) {
          return Math.exp(-x)-0.5;
        }
        public double dFdx(double x) {
          return -Math.exp(-x);
        }
      }
    );
    System.out.println(R.Find(0,0,2));
    System.out.println(Math.log(2));
  }
}
