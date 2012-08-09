package org.kwansystems.space.gear;

import org.kwansystems.space.gear.mass.InertSixDOFMass;
import org.kwansystems.space.universe.Universe;
import org.kwansystems.tools.vector.MathVector;
import org.kwansystems.tools.vector.SixDOFState;

public class InertSixDOFVehicle extends SixDOFVehicle {
	public InertSixDOFVehicle(Universe LU, InertSixDOFMass LMass) {
	  super(LU, null);
	  this.AddMass(LMass);
	}
	public void Discrete(double T, SixDOFState RVEw, MathVector FuelLevels) {}
	public void Steer(double T, SixDOFState RVEw, boolean IsMajor, MathVector SteerVector) {}

}
