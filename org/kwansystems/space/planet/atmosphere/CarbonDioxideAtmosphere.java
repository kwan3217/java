package org.kwansystems.space.planet.atmosphere;

public abstract class CarbonDioxideAtmosphere extends SimpleAtmosphere {
  //All CO2 data from From CO2 MSDS, http://www.airliquide.com/en/business/products/gases/gasdata/index.asp?gasid=26
  public CarbonDioxideAtmosphere() {
    gamma=1.293759; 
  }
  public double MolWeight(double Alt) {
    //Simplified model assumes pure CO2 atmosphere
    return 44.01;
  }
}
