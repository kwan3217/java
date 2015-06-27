package org.kwansystems.space.gear.guidance;

import org.kwansystems.space.gear.SixDOFVehicle;
import org.kwansystems.tools.rotation.MathMatrix;
import org.kwansystems.tools.rotation.Quaternion;
import org.kwansystems.tools.vector.MathState;
import org.kwansystems.tools.vector.MathVector;
import org.kwansystems.tools.vector.SixDOFState;

public interface Guidance {
  public abstract void Guide(double T, SixDOFState RVEw, MathVector FuelLevels, boolean IsMajor);
  /**
   * @param v the v to set
   */
  public void setVehicle(SixDOFVehicle v) {
    V = v;
  }
  /**
   * @return the v
   */
  public SixDOFVehicle getVehicle() {
    return V;
  }

}