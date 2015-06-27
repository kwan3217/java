package org.kwansystems.space.gear.mass;

import org.kwansystems.tools.rotation.MathMatrix;
import org.kwansystems.tools.vector.*;

import static java.lang.Math.*;

import static org.kwansystems.space.gear.mass.PropRes.PropPhase.*;

//This represents a propellant resource where the propellant is a cylinder,
//like a cylindrical fuel tank. The resource is modeled as a solid cylinder
//with its axis parallel to the vehicle X axis, with its aft end at a constant
//vehicle station, while the front end shifts with the fuel level
public class LiquidPropRes extends PropRes {
  public MathVector AftSta;
  public double L; 
  public double R;
  public double getFuelHeight(double T, SixDOFState RVEw) {
    return L*getMass(T,RVEw)/FullMass;
  }
  public MathVector getCoM(double T, SixDOFState RVEw) {
    return MathVector.add(AftSta,new MathVector(getFuelHeight(T,RVEw)/2.0,0,0));
  }
  public MathMatrix getI(double T, SixDOFState RVEw) {
    return ParallelAxis(SolidCylinderI(getMass(T,RVEw),R,getFuelHeight(T,RVEw)),getMass(T,RVEw),getCoM(T,RVEw));
  }
  //Construct a propellant resource given an original mass, cylinder center of mass, and cylinder dimensions
  public LiquidPropRes(String LName, double LFullMass, PropType Ltype, MathVector LAftSta, double LR) {
    super(LName, LFullMass, Ltype,Liquid);
    R=LR;
    double Volume=FullMass/Ltype.density;
    double CrossSectionArea=PI*R*R;
    L=Volume/CrossSectionArea;
    AftSta=new MathVector(LAftSta);
  }
  //Construct a propellant resource given an original mass, cylinder radius, and cylinder endpoints
  public LiquidPropRes(String LName, double LFullMass, PropType Ltype, double StaAft, double y, double z,double LR) {
    this(LName,LFullMass,Ltype,new MathVector(StaAft,y,z),LR);
  }
  //Construct a propellant resource given an original mass, cylinder radius, and cylinder endpoints
  public LiquidPropRes(String LName, double LFullMass, PropType Ltype, double StaAft,double LR) {
    this(LName,LFullMass,Ltype,StaAft,0,0,LR);
  }
}
