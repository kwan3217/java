package org.kwansystems.space.launch;

import org.kwansystems.space.planet.*;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.table.*;

//This is a Saturn-I with inert upper stage, values from SA-01 flight

public class SaturnIA extends Rocket {
  private static final double kgplbm=0.45359237; //lbm to kg factor, from
  private static final double ISPconv=9.80665;   //convert lbf-s/lbm to N-s/kg
                                          //http://ts.nist.gov/ts/htdocs/230/235/h4402/appenc.pdf, p12
  public SaturnIA(Guidance LG,Planet LP,ArrayListChartRecorder LC) {
    super("S-IA",LG,LP,LC);
    StartTime=0;
    StopTime=116.08;            //S-IA does not burn to depletion
    FuelMass=(429887+187584)*kgplbm;    //Propellant loaded, table D-II (252)
                                        //Includes unburned propellant, 19307+5264lb
					//and GOX evaporated during flight
    DryMass=(309635+697+898+60)*kgplbm;
                                //dry S-IA, including upper stage dry mass
                                //                    upper stage ballast
                                //                    GOX evaporated before flight
                                //                    GN2
                                //                    Hydraulic oil
                                //                    lower stage mass

    Flowrate=new StepTable(
      new double[] {  0,      //0
                     110.10,      //1
                     116.08      //2
                    },  //2
      new double[][] {{5244*kgplbm,    //0
                    2622*kgplbm,    //1
                        0}});  //2
  }
  //Isp has been shown to be almost independent of flow rate for a S-IC
  public double IspVac(double T) {
    double NonCanted=292*ISPconv;
    double InnerCant=Math.cos(Math.toRadians(3));
    double OuterCant=Math.cos(Math.toRadians(6));
    return NonCanted*4.0/8.0*InnerCant+NonCanted*4.0/8.0*OuterCant;
  }
  public double IspSL(double T) {
    double NonCanted=255.5*ISPconv;
    double InnerCant=Math.cos(Math.toRadians(3));
    double OuterCant=Math.cos(Math.toRadians(6));
    return NonCanted*4.0/8.0*InnerCant+NonCanted*4.0/8.0*OuterCant;
  }
  public double DragArea(double T) {
    return Math.PI*3.5*3.5;
  }
}
