package org.kwansystems.space.gear.mass;

import org.kwansystems.tools.rotation.MathMatrix;
import org.kwansystems.tools.vector.*;

import static org.kwansystems.space.gear.mass.PropRes.PropPhase.*;
import static org.kwansystems.space.planet.atmosphere.EarthAirConstants.*;
import static java.lang.Math.*;

/**This represents a propellant resource where the propellant completely fills
  its given volume, like a compressed gas tank. The resource is modeled as a cylinder
  with its axis along the vehicle X axis
  */
public class GasPropRes extends PropRes {
  public MathVector CoM;
  public MathMatrix NormI;
  public double Temperature; //Temperature in K
  public double Volume;
  public MathVector getCoM(double T, SixDOFState RVEw) {return new MathVector(CoM);}
  public MathMatrix getI(double T, SixDOFState RVEw) {return NormI.mul(getMass(T,RVEw));}
  //Construct a propellant resource given an original mass, cylinder center of mass, and cylinder dimensions
  public GasPropRes(String LName, double LFullMass, PropType Ltype, MathVector LCoM, double R, double L) {
    super(LName, LFullMass,Ltype,Gas);
    CoM=new MathVector(LCoM);
    NormI=SolidCylinderI(1.0, R, L);
    Volume=PI*R*R*L;
    Temperature=org.kwansystems.space.planet.atmosphere.EarthAirConstants.T0;
  }
  
  /**
   * Calculate the pressure in the propellant resource
   * @param T Time (used to get mass)
   * @param RVEw Vehicle State vector (used to get mass)
   * @return Pressure inside tank, in Pa
   */
  public double Pressure(double T, SixDOFState RVEw) {
	/* Ideal gas law - PV=nRT */
	double n=getMass(T,RVEw)/type.density;
	return n*Rs*Temperature/Volume;
  }
}
