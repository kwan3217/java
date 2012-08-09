package org.kwansystems.tools.rootfind;

/**
 * Bisector root finder. Bisection is cool, it Must Succeed and Cannot Fail
 */
public class Bisector extends RootFind {
  private double Xlo,Xhi,Ylo,Yhi,Xmid,Ymid;
  public Bisector(double LEpsilon, Method LConvMethod, RootFunction LR) {
    super(LEpsilon,LConvMethod,LR);
  }
  public Bisector(double LEpsilon, RootFunction LR) {
    super(LEpsilon,LR);
  }
  public Bisector(RootFunction LR) {
    super(LR);
  }
  protected void init(double LXlo, double LXhi) throws LuckOut {
    Xlo=LXlo;
    Xhi=LXhi;
    Ylo=eval(Xlo);
    Yhi=eval(Xhi);
    if(Ylo*Yhi>0) throw new IllegalArgumentException("Root not bracketed: f("+Xlo+")="+Ylo+", f("+Xhi+")="+Yhi);
  }
  protected double Step() throws LuckOut {
    Xmid=(Xlo+Xhi)/2;
    Ymid=eval(Xmid);
    //If Ymid and Ylo have the same sign...
    if(Ymid*Ylo>0) { 
      //Make the new low point the midpoint
      Ylo=Ymid;
      Xlo=Xmid;
    } else {
      //Make the new high point the midpoint
      Yhi=Ymid;
      Xhi=Xmid;
    }
    return Ymid;
  }
  protected double GetX() {
    return Xmid;
  }
  protected String PrintState() {
    return   "Xlo:  "+Xlo+
           "\nXhi:  "+Xhi+
           "\nYlo:  "+Ylo+
           "\nYhi:  "+Yhi+
           "\nXmid: "+Xmid+
           "\nYmid: "+Ymid;
  }
}
