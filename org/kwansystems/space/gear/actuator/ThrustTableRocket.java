package org.kwansystems.space.gear.actuator;

import org.kwansystems.space.gear.SixDOFVehicle;
import org.kwansystems.space.universe.Universe;
import org.kwansystems.tools.table.*;
import org.kwansystems.tools.vector.*;

public class ThrustTableRocket extends Rocket {
  //In this Table, X is time, Y is relative thrust level
  Table ThrustTable;
  double AvgMaxFlowRate;
  public ThrustTableRocket(
    String LName, 
    MathVector LCoF,
    SixDOFVehicle V,
    String[] LPropTank, 
    double[] LMixture, 
    MathVector LDirection, 
    double LAvgMaxFlowRate, 
    double LIsp0,
    double LIspSl,
    Table LThrustTable
  ) {
    super(LName,LCoF, V, LPropTank, LMixture, LDirection, LAvgMaxFlowRate,LIsp0, LIspSl);
    ThrustTable=LThrustTable;
    ThrustTable.Normalize(0);
    AvgMaxFlowRate=LAvgMaxFlowRate;
  }
  public double TotalFlowRate(double T, SixDOFState RVEw, Universe U) {
    MaxFlowRate=AvgMaxFlowRate*ThrustTable.Interp(T,0);
    return super.TotalFlowRate(T,RVEw,U);
  }
}
