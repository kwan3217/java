package org.kwansystems.space.gear.mass;

import org.kwansystems.tools.vector.*;

public class InertThreeDOFMass extends ThreeDOFMass {
  public double mass;
  public double getMass(double T, MathState RVEw) {
    return mass;
  }
  public InertThreeDOFMass(String LName, double LMass) {
    super(LName);
    mass=LMass;
  }
  public InertThreeDOFMass(String LName, InertThreeDOFMass LMass) {
    this(LName,LMass.mass);
  }
  public InertThreeDOFMass(String LName, InertSixDOFMass LMass) {
    this(LName,LMass.mass);
  }
  public InertThreeDOFMass(InertThreeDOFMass LMass) {
    this(LMass.Name,LMass.mass);
  }
  public InertThreeDOFMass(InertSixDOFMass LMass) {
    this(LMass.Name,LMass.mass);
  }
  public String toString() {
    return   Name+
           "\nMass: "+mass;
  }
}
