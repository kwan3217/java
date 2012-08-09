package org.kwansystems.space.gear;

import org.kwansystems.space.gear.mass.InertThreeDOFMass;
import org.kwansystems.space.gear.mass.ThreeDOFMass;
import org.kwansystems.space.planet.*;
import org.kwansystems.tools.integrator.*;
import org.kwansystems.tools.vector.*;

import java.io.*;

/** Three degree of freedom derivative set. State variable carries six elements:
 *Three position vector components
 *Three velocity vector components
 *This is abstract, concrete implementations must provide:
 *Mass, linear Force applied to center of mass
 *Routine performs kinematics (translation of velocity to position) and
 *dynamics (translation of acceleration to velocity) automatically. Also translates
 *forces to accelerations.
 */
public abstract class ThreeDOF extends DerivativeSet {
  //Return mass properties of object in body frame. Total mass is in kg 
  public abstract InertThreeDOFMass EquivalentMass(double T, MathState RV);
  //Return total thrust acting on body, in N, in inertial coordinates
  //Return total torque acting around each of the body reference axes (will be ignored)
  public abstract MathVector getForce(double T, MathState RV, boolean IsMajor);
  public MathVector dxdt(double T, MathVector X, boolean IsMajor) {
    //Break state into its components
    MathState RV=new MathState(X);
    ThreeDOFMass EM=EquivalentMass(T, RV);
    MathVector f=getForce(T,RV,IsMajor);
    MathState RVdot=new MathState(
      //Linear kinematics, it's just this easy
      new MathVector(RV.V()),
      //Linear dynamics -- Newton's second law, it's just this easy
      f=f.div(EM.getMass(T,RV))
    );
    return RVdot; 
  }
  public static void main(String args[]) throws IOException {
    ThreeDOF PointMass=new ThreeDOF() {
      Planet P;
      {P=new SimpleEarth();}
      InertThreeDOFMass EM=new InertThreeDOFMass("PointMass", 1);
      public InertThreeDOFMass EquivalentMass(double T, MathState RV) {return EM;}
      public MathVector getForce(double T, MathState RV, boolean IsMajor) {
        return P.Gravity(RV.R()).mul(EM.mass);
      }
    };
    Integrator I=new RungeKutta(
      0, 
      new MathVector(new MathVector[] {
        new MathVector(7000000,0,0),
        new MathVector(0,0,7800)
      }), 
      1,
      PointMass
    );
    //PrintStream Ouf=new PrintStream(new FileOutputStream("PointMass.inc"));
    PrintStream Ouf=System.out;
    Ouf.println("#declare Orient=array[500][3] {");
    for(int i=0;i<500;i++) {
      Ouf.println(I.getX().subVector(0,3));
      I.step();
    }
    Ouf.println("}");
  }
}
