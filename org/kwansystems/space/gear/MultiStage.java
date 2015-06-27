/**
 * 
 */
package org.kwansystems.space.gear;

import org.kwansystems.space.gear.guidance.Guidance;
import org.kwansystems.space.universe.Universe;
import org.kwansystems.tools.rotation.Quaternion;
import org.kwansystems.tools.vector.MathVector;
import org.kwansystems.tools.vector.SixDOFState;

public abstract class MultiStage extends SixDOFVehicle {
  public MultiStage(Universe LU, Guidance LG) {
	super(LU, LG);
  }
  public abstract void Steer(double T, SixDOFState RVEw, boolean IsMajor,double P, double Y, double R); 
}
