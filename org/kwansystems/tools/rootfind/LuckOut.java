package org.kwansystems.tools.rootfind;

/**
 * Root-finding algorithm hit early convergence. Not really
 * an error. The RootFind framework traps this throwable and
 * uses the double value in it as the independent variable.
 */
public class LuckOut extends Throwable {
  private static final long serialVersionUID=8407079193331068325L;
  /**
   * Independent variable which happens to be sufficiently
   * close to be a root.
   */
  double x;
  /**
   * Constructs a new LuckOut with the value which
   * happens to be a root.
   * @param Lx Independent variable value
   */
  public LuckOut(double Lx) {
    super(
      "Algorithm hit early convergence at independent variable value: "+
      Double.toString(Lx)
    );
    x=Lx;
  }
}
