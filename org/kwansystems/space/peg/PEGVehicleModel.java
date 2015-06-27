package org.kwansystems.space.peg;

public interface PEGVehicleModel {
  public abstract double ThrustMag(double t);
  public abstract double Mass(double t);
  public abstract double Isp(double t);
  /* Specific impulse of each stage (stage 1 doesn't need to be filled in) */
  public abstract double[] upperIsp();
  /* Starting acceleration of each stage (stage 1 doesn't need to be filled in) */
  public abstract double[] uppera();
  /* MET time of each stage end (stage N doesn't need to be filled in)*/
  public abstract double[] lowert();
}
