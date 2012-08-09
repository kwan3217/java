package org.kwansystems.space.ephemeris;

public enum TrigType {
  SIN() {
    public double eval(double d) {
      return Math.sin(d);
    }
    public double evald(double d) {
      return Math.cos(d);
    }
  },
  COS() {
    public double eval(double d) {
      return Math.cos(d);
    }
    public double evald(double d) {
      return -Math.sin(d);
    }
  };
  public abstract double eval(double d);
  public abstract double evald(double d);
}