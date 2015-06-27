package org.kwansystems.space.gear.guidance;

import org.kwansystems.space.gear.SixDOFVehicle;
import org.kwansystems.tools.rotation.Quaternion;
import org.kwansystems.tools.vector.MathVector;
import org.kwansystems.tools.vector.SixDOFState;

public class NullGuidance extends PIDGuidance {
  public NullGuidance(SixDOFVehicle LV) {
    super(LV);
  }
  public Quaternion majorCycle(double T, SixDOFState RVEw,MathVector FuelLevels) {
	  return new Quaternion();
  }
}
