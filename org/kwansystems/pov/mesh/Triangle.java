package org.kwansystems.pov.mesh;

import org.kwansystems.tools.vector.*;

public class Triangle {
  private PointKeeper PK;
  public int a,b,c;
  public int na,nb,nc;
  public Point normal;
  public boolean Checked;
  public Triangle(int La, int Lb, int Lc, PointKeeper LPK) {
    a=La;
    b=Lb;
    c=Lc;
    PK=LPK;
  }
  public Triangle(PointKeeper LPK) {
    this(0,0,0,LPK);
  }
  public boolean equals(Object o) {
    try {
      Triangle p=(Triangle)o;
      return(p.a==a & p.b==b & p.c==c);
    } catch (ClassCastException E) {return false;}
  }
  public int hashCode() {
    return a^b^c;
  }
  public String toString() {
    return toString(false);
  }
  public String toString(boolean useNormals) {
    if(useNormals) {
      return "<"+na+","+nb+","+nc+">";
    } else {
      return "<"+a+","+b+","+c+">";
    }
  }
  public void calcNormal() {
    MathVector A=PK.get(a).toMathVector();
    MathVector B=PK.get(b).toMathVector();
    MathVector C=PK.get(c).toMathVector();
    MathVector V1=MathVector.sub(A,B);
    MathVector V2=MathVector.sub(B,C);
    MathVector N=MathVector.cross(V1,V2);
    N=N.normal();
    normal=new Point(N);
    if(Float.isNaN(normal.x)) {
      throw new IllegalArgumentException("Aiee! NaN encountered. Degenerate triangle?");
    }
  }
  public boolean doesOrientationMatch(Triangle T) {
    for(int i=0;i<3;i++) {
      for(int j=0;j<3;j++) {
        if(T.a==a & T.b==b) {
          return false;
        }
        if(T.a==b & T.b==a) {
          return true;
        }
        T.Rotate();
      }
      Rotate();
    }
    throw new IllegalArgumentException("Triangle T ("+T.toString(false)+") is not adjacent to this ("+toString(false)+")");
  }
  public void Reverse() {
    int temp=b;
    b=c;
    c=temp;
  }
  public void Rotate() {
    int temp=a;
    a=b;
    b=c;
    c=temp;
  }
}
