package org.kwansystems.space.gear;

import org.kwansystems.tools.vector.*;

public class ForceTorque {
  public MathVector Force;
  public MathVector Torque; 
  public ForceTorque(MathVector LForce, MathVector LTorque) {
    Force=LForce;
    Torque=LTorque;
  }
  public ForceTorque() {
    Force=new MathVector(0,0,0);
    Torque=new MathVector(0,0,0);
  }
  public ForceTorque add(MathVector LForce, MathVector LTorque) {
	  return new ForceTorque(MathVector.add(Force, LForce),MathVector.add(Torque,LTorque));
  }
  public ForceTorque add(ForceTorque LForceTorque) {
	  return add(LForceTorque.Force,LForceTorque.Torque);
  }
  public String toString() {
    return   "Torque:    "+Torque+
           "\nForce:     "+Force;
  }
}
