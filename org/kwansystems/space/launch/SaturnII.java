package org.kwansystems.space.launch;

import org.kwansystems.space.planet.*;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.table.*;

public class SaturnII extends Rocket {
  public SaturnII(Guidance LG,Planet LP,ArrayListChartRecorder C) {
    super("S-II",LG,LP,C);
    StartTime=155.19;
    StopTime=524.04;            //Burn to Apollo 8 cutoff
    FuelMass=423347;            //LOX+LH2, mass at holddown release kg
    DryMass=51051;              //kg nonprop mass of S-II including upper interstage
    Flowrate=new StepTable(
      new double[] {  0,
                     155.19,
                     445.01,
                     524.05},
      new double[][] {{0,
                    1219.3,
                    917.6,
                    0}});
  }
  public double IspVac(double T) {
    //returns predicted Isp (N-s/kg) as a function of altitude (km)
    if(T<445.01)return 4162.1; else return 4223.7;
  }
  public double DragArea(double T) {
    return Math.PI*Math.pow(396/2*0.03937,2);
  }
}
