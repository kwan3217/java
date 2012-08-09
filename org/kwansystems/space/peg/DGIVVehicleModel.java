package org.kwansystems.space.peg;

public class DGIVVehicleModel implements PEGVehicleModel {
  public double ThrustMag(double t) {
    return 320000;
  }
  public double Mass(double t) {
    double mdot=-ThrustMag(t)/Isp(t);
    return (t-150.14)*mdot+21429.7407733;
  }
  public double Isp(double t) {
    return 4300;
  }

  public double[] upperIsp() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public double[] uppera() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public double[] lowert() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
