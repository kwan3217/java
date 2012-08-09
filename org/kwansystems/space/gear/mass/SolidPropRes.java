package org.kwansystems.space.gear.mass;

import org.kwansystems.tools.rotation.MathMatrix;
import org.kwansystems.tools.vector.*;

import static java.lang.Math.*;
import static org.kwansystems.space.gear.mass.PropRes.PropPhase.*;

//This represents a propellant resource where the propellant is a thick
//cylindrical shell, like a solid fuel charge. The resource is modeled
//as a hollow cylinder with its axis along the vehicle X axis
public class SolidPropRes extends PropRes {
  public MathVector CoM;
  public double L; 
  public double ROuter;
  public MathVector getCoM(double T, SixDOFState RVEw) {return new MathVector(CoM);}
  public MathMatrix getI(double T, SixDOFState RVEw) {
    return HollowCylinderI2(getMass(T,RVEw), type.density,ROuter,L);
  }
  //Construct a propellant resource given an original mass, cylinder center of mass, and cylinder dimensions
  public SolidPropRes(String LName, double LFullMass, PropType Ltype, MathVector LCoM, double LROuter, double LL) {
    super(LName, LFullMass, Ltype, Solid);
    ROuter=LROuter;
    L=LL;
    CoM=new MathVector(LCoM);
  }
  //Construct a propellant resource given an original mass, cylinder radius, and cylinder endpoints
  public SolidPropRes(String LName, double LFullMass, PropType Ltype, double LROuter, double StaAft, double StaFore) {
    this(LName,LFullMass,Ltype,new MathVector((StaAft+StaFore)/2,0,0), LROuter, abs(StaFore-StaAft));
  }
}
