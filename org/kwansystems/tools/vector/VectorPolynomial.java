package org.kwansystems.tools.vector;

public class VectorPolynomial {
  public MathVector[] Coeffs;
  public enum order {ConstFirst,ConstLast};
  public order PolyOrder; 
  public VectorPolynomial(MathVector[] LCoeffs, order LOrder, MathVector scale) {
    Coeffs=new MathVector[LCoeffs.length];
    for(int i=1;i<LCoeffs.length;i++) if(LCoeffs[i].dimension()!=LCoeffs[0].dimension()) throw new IllegalArgumentException("All vector coefficients must be the same dimension.");
    if(LCoeffs[0].dimension()!=scale.dimension()) throw new IllegalArgumentException("Scale factor must be same dimension as coefficients");
    for(int i=0;i<Coeffs.length;i++) Coeffs[i]=MathVector.mul(LCoeffs[i],scale);
    PolyOrder=LOrder;
  }
  public VectorPolynomial(MathVector[] LCoeffs, MathVector scale) {
    this(LCoeffs, order.ConstLast, scale);
  }
  public VectorPolynomial(MathVector[] LCoeffs, order LOrder) {
    this(LCoeffs, LOrder, MathVector.I(LCoeffs[0].dimension()));
  }
  public VectorPolynomial(MathVector[] LCoeffs) {
    this(LCoeffs, order.ConstLast, MathVector.I(LCoeffs[0].dimension()));
  }
  public MathVector Eval(double x) {
    MathVector y=new MathVector(Coeffs[0].dimension());
    int c;
    int s;
    if(PolyOrder==order.ConstLast) {
      c=0;
      s=1;
    } else {
      c=Coeffs.length-1;
      s=-1;
    }
    for(int i=0;i<Coeffs.length-1;i++) {
      y=MathVector.add(y,Coeffs[c+s*i]);
      y=y.mul(x);
    }
    y=MathVector.add(y,Coeffs[c+s*(Coeffs.length-1)]);	
    return y;
  }
  public String toString() {
    String y="";
    int c;
    int s;
    if(PolyOrder==order.ConstLast) {
      c=0;
      s=1;
    } else {
      c=Coeffs.length-1;
      s=-1;
    }
    for(int i=0;i<Coeffs.length-1;i++) {
      y=y+Coeffs[c+s*i].toString()+"\n";
    }
    y=y+Coeffs[c+s*(Coeffs.length-1)].toString();	
    return y;
  }
}
