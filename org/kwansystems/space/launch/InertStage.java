package org.kwansystems.space.launch;

import org.kwansystems.space.planet.*;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.table.*;
import org.kwansystems.tools.vector.*;

public class InertStage extends Rocket {
  public double IspVac(double T) {return 0;} 
  public double Radius; //Radius of a circle with equivalent cross section area
  
  public InertStage(String LName,double LMass,double LRadius,Planet LP,ArrayListChartRecorder C) {
    super(LName,null,LP,C);
    DryMass=LMass;
    Radius=LRadius;
    StartTime=0;
    StopTime=0;
    FuelMass=0;
    Flowrate=new StepTable(new double[] {0},new double[][] {{0}});
  }
  public boolean StageActive(double T) {
    return false;
  }
  public double Thrust(double T, MathVector X) {
    return 0;
  }
  public MathVector dxdt(double T, MathVector X) {
    return new MathVector(0,0,0); //When we say inert, we mean it!
                                  //Overriding this saves tons of time evaluating the rocket eqn with zero coeffs.
  }
  public double DragArea(double T) {
    return Math.PI*Math.pow(Radius,2);
  }
}
