package org.kwansystems.space.planet.atmosphere;

public class GRCMarsAtmosphere extends CarbonDioxideAtmosphere {
  //All CO2 data from From CO2 MSDS, http://www.airliquide.com/en/business/products/gases/gasdata/index.asp?gasid=26
  public GRCMarsAtmosphere() {
    Zlimit=112000;
    g0=3.711; //Only needed for scale height, so lack of accuracy doesn't affect anything else.
  }
  //Simplified Atmosphere model from http://www.grc.nasa.gov/WWW/K-12/airplane/atmosmrm.html
  public double Temp(double Alt) {
    //Temperature in Kelvins
    if(Alt<7000) {
      return 242.1-0.000998*Alt;
    } else if (Alt<Zlimit) {
      return 249.7-0.00222*Alt;
    } else {
      return 0;
    }
  }
  public double Pressure(double Alt) {
    //Pressure in Pa
    if (Alt<Zlimit) {
      return 699*Math.exp(-0.00009*Alt);
    } else {
      return 0;
    }
  }
  public static void main(String[] args) {
    Atmosphere A=new GRCMarsAtmosphere();
    System.out.println("Alt km |T K  |P mb    |rho g/cm3|Cs m/s|MolWt kg/kmol|N /m3   |L m     |mu kg/(m s)|H m");
    for(double Alt=-6;Alt<112;Alt++) {
      AirProperties P=A.calcProps(Alt*1000);
      System.out.printf("%7.0f|%5.1f|%8.2E| %8.2E|   %3.0f|       %6.3f|%8.2E|%8.2E|%8.2E|%8.2E\n",
        P.Altitude/1000, P.Temperature, P.Pressure/100.0,P.Density/1000.0,P.VSound,P.MolWt,P.NumberDensity,P.MeanFreePath,P.Viscosity,P.PScaleHeight
      );
    }
  }
}
