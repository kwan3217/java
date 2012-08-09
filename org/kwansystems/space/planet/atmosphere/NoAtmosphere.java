package org.kwansystems.space.planet.atmosphere;

public class NoAtmosphere extends SimpleAtmosphere {
  public NoAtmosphere() {
    Zlimit=0;
  }
  public double Temp(double Alt) {return 0;}
  public double Pressure(double Alt) {return 0;}
  public double MolWeight(double Alt) {return 0;}
}
