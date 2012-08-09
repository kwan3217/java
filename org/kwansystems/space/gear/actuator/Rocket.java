package org.kwansystems.space.gear.actuator;

import org.kwansystems.space.universe.*;
import org.kwansystems.space.gear.*;
import org.kwansystems.space.planet.atmosphere.*;
import org.kwansystems.tools.*;
import org.kwansystems.tools.vector.*;

public class Rocket extends Thruster {  
  public double MaxFlowRate;
  public double Isp0;
  public double IspSl;
  public Rocket(String LName, MathVector LCoF, SixDOFVehicle V, String[] LPropTank, double[] LMixture, MathVector LDirection, double LMaxFlowRate, double LIsp0,double LIspSl) {
    super(LName,LCoF, V, LPropTank,LMixture,LDirection);
    MaxFlowRate=LMaxFlowRate;
    Isp0=LIsp0; 
    IspSl=LIspSl; 
  }
  public double Isp(double T, SixDOFState RVEw, Universe U) {
    return Scalar.linterp(0,Isp0,EarthAirConstants.P0,IspSl,U.getAtm(T, RVEw).Pressure);
  }
  public double MaxThrust(double T, SixDOFState RVEw, Universe U) {
    return Isp(T,RVEw,U)*MaxFlowRate;
  }
  public double TotalFlowRate(double T, SixDOFState RVEw, Universe U) {
    double Thrust=MaxThrust(T,RVEw,U)*Throttle;
    return Thrust/Isp(T,RVEw,U);
  }
}
