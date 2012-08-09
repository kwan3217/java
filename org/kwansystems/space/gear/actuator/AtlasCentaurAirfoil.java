package org.kwansystems.space.gear.actuator;

import org.kwansystems.space.gear.actuator.Airfoil.LiftModel;
import org.kwansystems.tools.vector.MathVector;

public class AtlasCentaurAirfoil extends Airfoil {
  /**
   * Creates a new airfoil.
   * @param LName Name of this airfoil
   * @param LCoF Center of force, station frame, m
   * @param LPerp Unit vector perpendicular to wing chord, in airfoil plane
   * @param LChordLine Unit vector parallel to wing chord
   * @param LChord Length of wing chord, m
   * @param LWingspan Span of airfoil, m
   */
  public AtlasCentaurAirfoil(String LName, MathVector LCoF, MathVector LPerp, MathVector LChordLine, double LChord, double LWingspan, LiftModel LModel) {
    super(LName,LCoF);
    ReferenceChordLine=LChordLine.normal();
    Perp=LPerp.normal();
    ReferenceNormal=MathVector.cross(ReferenceChordLine,Perp).normal();
    setAirfoilAngle(0); 
    Chord=LChord;
    Wingspan=LWingspan;
    SurfaceArea=Chord*Wingspan; 
    Aspect=Wingspan/Chord;
    Model=LModel;
    SurfaceAreaFactor=1;
  }
  @Override
  public double[] C(double aoa, double M, double Re) {
    // TODO Auto-generated method stub
    return null;
  }

}
