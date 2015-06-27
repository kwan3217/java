package org.kwansystems.space.planet.atmosphere;

import static org.kwansystems.space.planet.atmosphere.EarthAirConstants.*;

public abstract class SimpleAtmosphere extends Atmosphere {
  //Ratio of specific heats (1.40 by theory for diatomic gases)
  public double gamma=EarthAirConstants.gamma;
  //This is needed for Reynolds number calculation
  public double Viscosity(double Alt) { //Dynamic Viscosity mu
    double T=Temp(Alt);
    return (beta*Math.pow(T,1.5))/(T+S);
  }
  //This is needed for mean free path
  public double sigma=EarthAirConstants.sigma;
  //All other data is calculated from these 3 curves
  public abstract double Temp(double Alt);      //Temperature in Kelvins
  public abstract double Pressure(double Alt);  //Pressure in Pascals (=N/m^2)
  public abstract double MolWeight(double Alt); //MolWt in kg/kmol (=amu)
  //Derived data
  public double Density(double Alt) {           //Density in kg/m^3
    double T=Temp(Alt);
    if (T==0) {
      return 0;
    } else {
      return MolWeight(Alt)*Pressure(Alt)/(Rs*Temp(Alt));
    }
  }
  public double Vsound(double Alt) {              //Speed of Sound in m/s
    double M=MolWeight(Alt);
    if(M==0) {
      return 0;
    } else {
      return Math.sqrt(gamma*Rs*Temp(Alt)/MolWeight(Alt));
    }
  }
  public double MeanFreePath(double Alt) {
    return 1.0/(Math.sqrt(2.0)*Math.PI*Na*Math.pow(sigma,2))*Rs/MolWeight(0)*MolWeight(Alt)*MolTemp(Alt)/Pressure(Alt);
  }
  public double MolTemp(double Alt) {
    return Temp(Alt)*MolWeight(Alt)/MolWeight(0);
  }
  public AirProperties calcProps(double Z) {
    AirProperties A=new AirProperties();
    if(Z>Zlimit) return A;
    A.Temperature=Temp(Z);
    A.Pressure=Pressure(Z);
    A.Density=Density(Z);
    A.VSound=Vsound(Z); 
    A.MolWt=MolWeight(Z);
    A.Viscosity=Viscosity(Z);
    A.Altitude=Z;
    A.MolTemp=MolTemp(Z);
    A.SpecHeatRatio=gamma;
    A.NumberDensity=A.Density/A.MolWt*Na;
    A.MeanFreePath=MeanFreePath(Z);
    A.PScaleHeight=Rs*A.Temperature/(A.MolWt*g0);
    return A;
  }
}
