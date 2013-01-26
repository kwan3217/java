package org.kwansystems.tools.rootfind;

/**
 * Find a root of a one-dimensional function. This is the point x at which
 * the function y=f(x)=0. The definition of root has been generalized in this
 * class to mean find the x which satisfies y=f(x)=yt, where yt is the "target"
 * value to find.
 */
public abstract class RootFind {
  /**
   *Convergence criterion
   */
  public double Epsilon=1e-6;
  /**
   *Convergence method
   */
  public Method convmethod;
  /**
   * Convergence criterion type
   */
  public enum Method {
    /**
     * Relative convergence. Value is considered converged once it is within a certain
     * fraction of the target value
     */
    Relative,
    /**
     * Absolute convergence. Value is considered converged once it is within a certain
     * absolute distance of the target value
     */
    Absolute,
    /**
     * Adaptive convergence. Uses relative convergence if |target| is more than 10,
     * absolute convergence otherwise.
     */
    Adaptive
  };
  private RootFunction R;
  private double Yt;
  public boolean IsConverged(double Y) {
    return(ErrorAmount(Y)<Epsilon);
  }
  public double ErrorAmount(double Y) {
    switch(convmethod) {
      case Adaptive:
        //If Y0<10, use difference
        //else use relative difference
        if(Math.abs(Yt)<10) {
          return Math.abs(Y);
        } else {
          return Math.abs(Y)/Math.abs(Yt);
        }
      case Relative:
        return Math.abs(Y)/Math.abs(Yt);
      case Absolute:
        return Math.abs(Y);
      default:
        throw new IllegalArgumentException("This can't happen! Strange enumeration value found");
    }
  }
  //Set Xlo and Xhi to bracket the root you want to find. Single
  //point methods will only use Xlo. Set Y0 to the desired value.
  //Descendant classes should override this function. They should
  //use IsConverged to check convergence, unless convergence is based
  //on x.
  protected abstract double Step() throws LuckOut;
  protected abstract void init(double LXlo, double LXhi) throws LuckOut;
  protected abstract double GetX();
  protected abstract String PrintState();
  private boolean verbose=false; 
  protected double eval(double x) throws LuckOut {
    double result=R.F(x)-Yt;
    if(IsConverged(result)) throw new LuckOut(x); 
    return result;
  }
  protected double evalp(double x){
    return ((RootDeriv)R).dFdx(x);
  }
  /**
   * Find the root of the function
   * @param LYt Target dependent value
   * @param LXlo First guess independent value
   * @param LXhi Second guess independent value
   * @return A Root of the function
   * @throws java.lang.ArithmeticException if convergence fails because iteration limit is exceeded
   */
  public double Find(double LYt, double LXlo, double LXhi) throws ArithmeticException {
    Yt=LYt;
    try {
      init(LXlo,LXhi);
      int IterLimit=200;
      int iters=0;
      if(verbose)System.out.println(PrintState());
      
      while(!IsConverged(Step())) { 
        if(verbose)System.out.println("Iter: "+iters+"\n"+PrintState());
        iters++;
        if(iters>IterLimit) throw new ArithmeticException("Convergence failed after "+IterLimit+" iterations");
      }
      return GetX();
    } catch (LuckOut E) { return E.x; }
  }
  public RootFind(double LEpsilon, Method LConvMethod, RootFunction LR) {
    Epsilon=LEpsilon;
    convmethod=LConvMethod;
    R=LR;
  }
  public RootFind(double LEpsilon, RootFunction LR) {
    this(LEpsilon,Method.Adaptive,LR);
  }
  public RootFind(RootFunction LR) {
    this(1e-6,LR);
  }
  /**
   * Your one-stop shop for root finding
   * @param R Root finder to use
   * @param LYt Target dependent value
   * @param LXlo First guess independent value
   * @param LXhi Second guess independent value
   * @return A Root of the function
   */
  public static double Find(RootFind R, double LYt, double LXlo, double LXhi) {
    return R.Find(LYt,LXlo,LXhi);
  }
}
