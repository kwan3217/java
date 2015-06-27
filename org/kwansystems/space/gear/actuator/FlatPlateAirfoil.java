package org.kwansystems.space.gear.actuator;

import org.kwansystems.space.planet.atmosphere.FlatAtmosphere;
import org.kwansystems.space.planet.atmosphere.SimpleEarthAtmosphere;
import org.kwansystems.space.universe.FlatEarth;
import org.kwansystems.space.universe.Universe;
import org.kwansystems.tools.rotation.Quaternion;
import org.kwansystems.tools.vector.*;

public class FlatPlateAirfoil extends Airfoil {
  public double CLAmplitude;
  public double CDBase;
  public double CDAmplitude;
  public FlatPlateAirfoil(String LName, MathVector lCoF, MathVector LPerp, MathVector LChordLine, double chord, double span, Airfoil.LiftModel LModel, double LCDBase) {
    super(LName, lCoF, LPerp, LChordLine, chord, span, LModel);
    CLAmplitude=1.2;
    CDBase=LCDBase;
    CDAmplitude=2;
  }
  public FlatPlateAirfoil(String LName, MathVector lCoF, MathVector LPerp, MathVector LChordLine, double chord, double span, double LCDBase) {
  	this(LName, lCoF, LPerp, LChordLine, chord, span,Airfoil.LiftModel.Planar, LCDBase);
  }
  public double[] C(double AoA, double M, double Re) {  
    double[] c=new double[3];
    c[CL]=CLAmplitude*Math.sin(2*AoA);
    c[CD]=CDBase+Math.pow(Math.sin(AoA),2)*CDAmplitude;
    c[CM]=0;
    return c;
  }
  public static void main(String[] args) {
    Airfoil PA=new FlatPlateAirfoil("PlanarAirfoil",      new MathVector(-1,0,0), new MathVector(0,1,0), new MathVector(1,0,0), 1, 1, 0.25);
    PA.setAirfoilAngle(Math.toRadians(0));
    Universe U=new FlatEarth(9.80665, new FlatAtmosphere(new SimpleEarthAtmosphere(),12000,100000));
    SixDOFState RVEw=new SixDOFState(new MathVector(0,0,0),new MathVector(250,0,-00),new Quaternion(Quaternion.U),new MathVector(0.0,1,0));
    WingForceTorque WPA=(WingForceTorque)PA.getForceTorque(0, RVEw, 100, new MathVector(0,0,0), U);
    System.out.println("WingForceTorque from Planar      Wing: \n"+WPA);
  }
}
