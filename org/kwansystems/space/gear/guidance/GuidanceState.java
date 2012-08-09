package org.kwansystems.space.gear.guidance;

import org.kwansystems.tools.vector.*;
import org.kwansystems.space.gear.*;

public interface GuidanceState {
  public void step(GuidanceStateMachine parent, SixDOFVehicle vehicle, double T, SixDOFState RVEw, MathVector fuelLevels);
  public void enterState(GuidanceStateMachine parent, SixDOFVehicle vehicle, double T, SixDOFState RVEw, MathVector fuelLevels);
}
