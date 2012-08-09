package org.kwansystems.space.launch;

import org.kwansystems.space.planet.*;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.table.*;

public class SaturnIVB extends Rocket {
  public SaturnIVB(Guidance LG,Planet LP,ArrayListChartRecorder C) {
    super("S-IVB",LG,LP,C);
    StartTime=528.90;
    StopTime=784.98;            //Burn to guidance velocity cutoff Apollo 8 cutoff 684.98
    FuelMass=42984;             //LOX+LH2, mass at holddown release kg+10000kg
    DryMass=88009;              //kg mass of nonprop S-IVB including IU,SLA,LTA,and TLI fuel-10000kg
                                //Extra 10000kg of propellant allows sure guidance cutoff 
				//rather than fuel cutoff.
				//It's ok anyway, since the total stage mass is the same,
				//very little of this 10000kg is used on guidance cutoff,
				//and what is used really is fuel - just TLI fuel.
    Flowrate=new StepTable(
      new double[] {  0,      //0
                     528.90,  //2
                     784.98}, //9
      new double[][] {{0,        //0
                    215.06,   //8
                    0}});      //0
  }
  public double IspVac(double T) {return 4204;}
  public double DragArea(double T) {
    return Math.PI*Math.pow(260/2*0.03937,2);
  }
}
