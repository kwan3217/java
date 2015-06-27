package org.kwansystems.emulator.postscript.path;

public class CurveSegment implements PathElement {
  double x1,y1,x2,y2,x3,y3;

  public double[] getEnd() {
    return new double[] {x3,y3};
  }
  public CurveSegment(double Lx1, double Ly1, double Lx2, double Ly2, double Lx3, double Ly3) {
    x1=Lx1;
    y1=Ly1;
    x2=Lx2;
    y2=Ly2;
    x3=Lx3;
    y3=Ly3;
  }
  public double[] getData() {
    return new double[] {x1,y1,x2,y2,x3,y3};
  }
}
