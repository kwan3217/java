package org.kwansystems.space.peg;

public class SpaceShuttle implements PEGVehicleModel {
  public double ThrustMag(double t) {
    return 6806042.261325;
  }
  public double Mass(double t) {
    double mdot=-ThrustMag(t)/Isp(t);
    return (t-181.498)*mdot+562229.0631847;
  }
  public double Isp(double t) {
    return 4454.954088722;
  }

}
