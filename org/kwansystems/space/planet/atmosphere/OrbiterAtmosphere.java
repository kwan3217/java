package org.kwansystems.space.planet.atmosphere;

import org.kwansystems.tools.table.*;
import static java.lang.Math.*;

public class OrbiterAtmosphere extends SimpleAtmosphere {
  //All CO2 data from From CO2 MSDS, http://www.airliquide.com/en/business/products/gases/gasdata/index.asp?gasid=26
  public OrbiterAtmosphere() {
    Zlimit=112000;
    gamma=1.2941; 
  }
  Table T=new LinearTable(new double[] {0,2000,4000,14000,20000,30000,40000},
      new double[][] {{195,200,200,180,180,165,165}} );
  Table LnP=new LinearTable(new double[] {0,2000,4000,14000,20000,30000},
                             new double[][] {{log(610.0),log(499.5),log(410.1),log(145.1),log(75.2),log(23.9)}});
  //Simplified Atmosphere model from http://www.grc.nasa.gov/WWW/K-12/airplane/atmosmrm.html
  public double Temp(double Alt) {
    return T.Interp(Alt, 0);
  }
  public double Pressure(double Alt) {
    return exp(LnP.Interp(Alt,0));
  }
  public double MolWeight(double Alt) {
    //Simplified model assumes pure CO2 atmosphere
    return (286.91*EarthAirConstants.M0)/188.92;
  }
  public static void main(String[] args) {
    double[] alt=new double[] {
        -1500
        };
    Atmosphere OMA=new OrbiterAtmosphere();
    Atmosphere GMA=new GRCMarsAtmosphere();
    for (double A:alt) {
      AirProperties OA=OMA.calcProps(A);
      AirProperties GA=GMA.calcProps(A);
      System.out.printf("%.5e,%3f,%.5f,%.2f,%.5e,%3f,%.5f,%.2f\n",GA.Density,GA.Temperature,GA.Pressure,GA.VSound,OA.Density,OA.Temperature,OA.Pressure,OA.VSound);
    }
    System.out.println(OMA.calcProps(-1500).Density);
  }

}
