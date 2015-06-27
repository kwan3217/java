package org.kwansystems.emulator.postscript.path;

public class LineSegment implements PathElement {
  double x1,y1;

  public double[] getEnd() {
    return new double[] {x1,y1};
  }
  public LineSegment(double Lx1, double Ly1) {
    x1=Lx1;
    y1=Ly1;
  }
  public double[] getData() {
    return new double[] {x1,y1};
  }
}
