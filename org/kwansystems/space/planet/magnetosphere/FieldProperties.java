package org.kwansystems.space.planet.magnetosphere;

public class FieldProperties {

  static FieldProperties diff(FieldProperties r0, FieldProperties r1) {
    return new FieldProperties(r1.ti-r0.ti,r1.h-r0.h,r1.x-r0.x,r1.y-r0.y,r1.z-r0.z,r1.dec-r0.dec,r1.dip-r0.dip);
  }
  public double ti;
  public double h;
  public double x;
  public double y;
  public double z;
  public double dec;
  public double dip;

  FieldProperties(double Lti, double Lh, double Lx, double Ly, double Lz, double Ldec, double Ldip) {
    ti=Lti;
    h=Lh;
    x=Lx;
    y=Ly;
    z=Lz;
    dec=Ldec;
    dip=Ldip;
  }
}
