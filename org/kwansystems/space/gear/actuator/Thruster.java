package org.kwansystems.space.gear.actuator;

import org.kwansystems.space.gear.*;
import org.kwansystems.space.universe.Universe;
import org.kwansystems.tools.vector.*;

public abstract class Thruster extends Actuator {
  /** Index into Props array of SixDOFVehicle of propellant resources used.  */
  public int[] PropTank;
  /** Percentage of total propellant used which is sucked from each resource. This
   *  is parallel to PropTank, and should sum to 1. 
   */
  public double[] Mixture;
  /** Create a thruster.
   * @param LName Name of actuator
   * @param LCoF Center of Force
   * @param V Vehicle this thruster is mounted on
   * @param LPropTank Names of propellant tanks on V that this thruster uses
   * @param LMixture 
   * @param LDirection
   */
  public Thruster(String LName, MathVector LCoF, SixDOFVehicle V, String[] LPropTank, double[] LMixture, MathVector LDirection) {
    super(LName,LCoF);
    Direction=LDirection;
    PropTank=new int[LPropTank.length];
    for(int i=0;i<PropTank.length;i++) {
      PropTank[i]=V.FindProp(LPropTank[i]);
    }
    setMixture(LMixture);
  }
  public void setMixture(double[] LMixture) {
    double MixSum=0;
    for(int i=0;i<LMixture.length;i++) MixSum+=LMixture[i];
    Mixture=new double[LMixture.length];
    for(int i=0;i<LMixture.length;i++) Mixture[i]=LMixture[i]/MixSum;
  }
  public abstract double TotalFlowRate(double T, SixDOFState RVEw, Universe U);
  public double[] PropFlowRate(double T, SixDOFState RVEw, Universe U) {
    double TotalPropFlowRate=TotalFlowRate(T,RVEw,U);
    double[] result=new double[Mixture.length];
    for(int i=0;i<result.length;i++) result[i]=TotalPropFlowRate*Mixture[i];
    return result;
  }
  /** Ran out of propellant in tank PropTank[TankIdx]. Default action is to shut
   * down the thruster. Override to do stuff like explode if fuel ran out before oxidizer.
   * @param TankIdx index of the tank that ran out of propellant
   */
  public void OutOfFuel(int TankIdx) {
	  Active=false;
  }
  /** Direction of this thruster in the station frame. Force is applied in this direction, exhaust (if any)
   * is expelled in the opposite direction. */
  private MathVector Direction;
  /** Throttle level. Nominally between zero and one, though I suppose you can
   * do things like 104% with this, with proportionally greater propellant usage.
   */
  public double Throttle;
  /**
   * @param LName
   * @param LCoF
   * @param LDirection
   */
  public abstract double MaxThrust(double T, SixDOFState RVEw, Universe U);
  public ForceTorque getForceTorque(double T, SixDOFState RVEw, double Mass, MathVector CoM, Universe U) {
    double Thrust=MaxThrust(T,RVEw,U)*Throttle;
    MathVector Force=Direction.mul(Thrust);
    MathVector ThrustOffset=MathVector.sub(CoF,CoM);
    MathVector Torque=MathVector.cross(Force, ThrustOffset);
    return new ForceTorque(Force,Torque);
  }
  /** Set the direction of this thruster, in the station frame. Don't try to cheat, this vector gets
   * copied and normalized before being used.
   * 
   * @param direction The new thruster direction vector
   */
  public void setDirection(MathVector direction) {
    Direction = direction.normal();
  }
  /** Get the direction of this thruster, in the station frame. Don't try to cheat, this vector is a 
   * copy of the direction vector, and changing it will not affect the actual direction.
   * @return A copy of the thruster direction vector
   */
  public MathVector getDirection() {
    return new MathVector(Direction);
  }
}
