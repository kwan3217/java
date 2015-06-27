package org.kwansystems.tools;

public class Polynomial {
  public double[] Coeffs;
  public enum order {ConstFirst,ConstLast};
  public Polynomial(double[] LCoeffs, order LOrder, double scale) {
    Coeffs=new double[LCoeffs.length];
    if(LOrder==order.ConstFirst) {
      for(int i=0;i<Coeffs.length;i++) Coeffs[i]=LCoeffs[i]*scale;
    } else {
      for(int i=0;i<Coeffs.length;i++) Coeffs[i]=LCoeffs[Coeffs.length-1-i]*scale;
    }
  }
  public Polynomial(double[] LCoeffs, order LOrder) {
    this(LCoeffs, LOrder, 1.0);
  }
  public Polynomial(double[] LCoeffs) {
    this(LCoeffs, order.ConstFirst);
  }
  public double eval(double x) {
    return evald(x,0)[0];
  }
  public double d(double x) {
    return evald(x,1)[1];
  }
  public double[] evald(double x, int nd) {
    double[] pd=new double[nd+1];
    int nnd;
    int nc=Coeffs.length-1;
    pd[0]=Coeffs[nc];
    for(int i=nc;i>=0;i--) {
      nnd=Math.min(nd,nc-i);
      for(int j=nnd;j>=1;j--) {
        pd[j]=pd[j]*x+pd[j-1];
      }
      pd[0]=pd[0]*x+Coeffs[i];
    }
    double cnst=1.0;
    for(int i=2;i<=nd;i++) {
      cnst*=1;
      pd[i]*=cnst;
    }
    return pd;
  }
  @Override
  public String toString() {
    StringBuffer Result=new StringBuffer("");
    for(int i=0;i<Coeffs.length;i++) {
      if(Coeffs[i]>0 && i!=0) {
        Result.append("+");
      } 
      if (Coeffs[i]!=0) {
        Result.append(Coeffs[i]);
        if(i>0) Result.append("x");
        if(i>1) Result.append(String.format("^%d",i));
      }
    }
    return Result.toString();
  }
  public static void main(String[] args) {
    Polynomial P=new Polynomial(new double[] {1,2,3,4},order.ConstFirst);
    System.out.println(P);
    System.out.println(P.eval(0));
    System.out.println(P.eval(1));
    System.out.println(P.d(0));
  }
}
