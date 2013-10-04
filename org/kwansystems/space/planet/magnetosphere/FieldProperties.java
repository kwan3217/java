package org.kwansystems.space.planet.magnetosphere;

import org.kwansystems.tools.vector.*;

public class FieldProperties {

  static FieldProperties diff(FieldProperties r0, FieldProperties r1) {
    return new FieldProperties(r1.ti-r0.ti,r1.h-r0.h,MathVector.sub(r1.B,r0.B),r1.dec-r0.dec,r1.dip-r0.dip);
  }
  public double ti;
  public double h;
  public MathVector B;
  public double dec;
  public double dip;

  FieldProperties(double Lti, double Lh, double Lx, double Ly, double Lz, double Ldec, double Ldip) {
    ti=Lti;
    h=Lh;
    B=new MathVector(Lx,Ly,Lz);
    dec=Ldec;
    dip=Ldip;
  }
  FieldProperties(double Lti, double Lh, MathVector LB, double Ldec, double Ldip) {
    ti=Lti;
    h=Lh;
    B=new MathVector(LB);
    dec=Ldec;
    dip=Ldip;
  }
}
