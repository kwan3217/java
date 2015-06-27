package org.kwansystems.space.peg;

public class CentaurVVehicleModel implements PEGVehicleModel {
  public double ThrustMag(double t) {
    return 99200;
  }
  public double Mass(double t) {
    double mdot=-ThrustMag(t)/Isp(t);
    return (t-0.371)*mdot+25864.23654607;
  }
  public double Isp(double t) {
    return 4417.895825;
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
