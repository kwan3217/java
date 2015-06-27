package org.kwansystems.space.planet.atmosphere;

public class SimpleEarthAtmosphere extends SimpleAtmosphere {
  public SimpleEarthAtmosphere() {
    Zlimit=150000;
  }
  //Simplified Atmosphere model from http://www.grc.nasa.gov/www/K-12/airplane/atmosmet.html
  public double Temp(double Alt) {
    if(Alt<11000) {
      return 288.14-0.00649*Alt;
    } else if (Alt<25000) {
      return 216.64;
    } else if (Alt<Zlimit) {  //This is the top of the atmosphere
      return 141.89+0.00299*Alt;
    } else {
      return 0;
    }
  }
  public double MolWeight(double Alt) {
    //In the simple model, MolWt is constant
    return 28.9644;
  }
  public double Pressure(double Alt) {
    if(Alt<11000) {
      return 101290*Math.pow(Temp(Alt)/288.08,5.256);
    } else if (Alt<25000) {
      return 22650*Math.exp(1.73-0.000157*Alt);
    } else if (Alt<Zlimit) {
      return 2488*Math.pow(Temp(Alt)/216.6,-11.388);
    } else {
      return 0;
    }
  }
}
