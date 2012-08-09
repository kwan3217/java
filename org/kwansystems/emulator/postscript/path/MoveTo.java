package org.kwansystems.emulator.postscript.path;

public class MoveTo implements PathElement {
  double x,y;
  public double[] getEnd() {
    return new double[] {x,y};
  }
  public MoveTo(double Lx, double Ly) {
    x=Lx;
    y=Ly;
  }
  public double[] getData() {
    return new double[] {x,y};
  }
}
