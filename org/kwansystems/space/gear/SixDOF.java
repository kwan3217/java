package org.kwansystems.space.gear;

import org.kwansystems.space.gear.mass.InertSixDOFMass;
import org.kwansystems.space.gear.mass.InertThreeDOFMass;
import org.kwansystems.tools.integrator.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.vector.*;

import java.io.*;

/**Six degree of freedom derivative set. State variable carries thirteen elements:
<ul>
<li>Three position vector components</li>
<li>Three velocity vector components</li>
<li>Four attitude quaternion elements</li>
<li>Three angular velocities</li> 
</ul>
This is abstract, concrete implementations must provide:
Mass, Moments of Inertia, linear Force applied to center of mass, and Torque applied around reference axes
<p>
This assumes that I is constant when calculating attitude dynamics, even
though I is allowed to be a function of time and state. This is the only approximation.
Routine performs kinematics (translation of velocity to position) and
dynamics (translation of acceleration to velocity) automatically. Also translates
forces to accelerations.
<p>
Rotational renormalization is performed! (This requires directly manipulating the state,
which the current Integrator interface now allows). If rotation is less than about
5deg per time step, a 4th order integrator should never have to renormalize, but
we do anyway, because observed rotations have been higher. Renormalization
doesn't make it more accurate, just more stable.
*/
public abstract class SixDOF extends ThreeDOF implements Constraint {
  /**
   *  
   * @param T
   * @param RVEw
   * @return mass properties of object in body frame. Total mass is in kg, 
   *          CoM is in reference coordinates (will be ignored),
   *          I is full inertia matrix around center of mass in body frame
   */
  public abstract InertSixDOFMass EquivalentMass(double T, SixDOFState RVEw);
  public InertThreeDOFMass EquivalentMass(double T, MathState RV) {
    InertSixDOFMass EM=EquivalentMass(T, new SixDOFState(RV));
    return new InertThreeDOFMass(EM);
  }
  /**
 * @param T
 * @param RVEw
 * @param IsMajor
 * @return Total thrust acting on body, in N, in inertial coordinates, and total 
 *         torque acting around each of the body reference axes, in Nm
 */
  public abstract ForceTorque getForceTorque(double T, SixDOFState RVEw, boolean IsMajor);
  public MathVector getForce(double T, MathState RV, boolean IsMajor) {
    return getForceTorque(T, new SixDOFState(RV),IsMajor).Force;
  }
  public MathVector dxdt(double T, MathVector X, boolean IsMajor) {
    //Break state into its components
    SixDOFState RVEw=new SixDOFState(X);
    InertSixDOFMass EM=EquivalentMass(T, RVEw);
    MathMatrix I=EM.I;
    ForceTorque ft=getForceTorque(T,RVEw,IsMajor);
    //Linear kinematics, it's just this easy
    MathVector Rdot=new MathVector(RVEw.V());
    //Linear dynamics -- Newton's second law, it's just this easy
    MathVector Vdot=ft.Force.div(EM.mass);
    //Attitude kinematics -- Not hard, just unfamiliar
    Quaternion Edot=Quaternion.mul(Quaternion.mul(RVEw.E(),RVEw.w()),0.5);
    //Attitude dynamics -- Euler's equations for a rigid body
    MathVector w=RVEw.w();
    MathVector L=I.transform(w);
    MathVector N=ft.Torque;
    MathVector Iwdot=MathVector.sub(N, MathVector.cross(w,L));
    MathVector wdot=I.invTransform(Iwdot);

    SixDOFState RVEwdot=new SixDOFState(Rdot,Vdot,Edot,wdot);
    return RVEwdot; 
  }
  /** 
   * Integrator constraint. Constrains quaternion in state to be of unit length
   * @see org.kwansystems.tools.integrator.Constraint#Constrain(double, org.kwansystems.tools.vector.MathVector)
   */
  public MathVector Constrain(double T, MathVector X) {
    SixDOFState RVEw=new SixDOFState(X.subVector(0,13));
    RVEw=new SixDOFState(RVEw.R(),RVEw.V(),Quaternion.norm(RVEw.E()),RVEw.w()); 
    return RVEw;
  }
  public static void main(String args[]) throws IOException {
    SixDOF Book=new SixDOF() {
      MathVector Zero=new MathVector(0,0,0);
      InertSixDOFMass EM=new InertSixDOFMass(
        null, 
        1,
        new MathVector(Zero),
        new MathMatrix(3000,1000,2000)
      );
      public InertSixDOFMass EquivalentMass(double T, SixDOFState RVEw) {return EM;}
      public ForceTorque getForceTorque(double T, SixDOFState RVEw, boolean IsMajor) {return new ForceTorque(Zero, new MathVector(0,0,T));}
    };
    Integrator I=new RungeKutta(
      0, 
      new MathVector(new MathVector[] {
        new MathVector(0,0,0),
        new MathVector(0,0,0),
        new Quaternion(Quaternion.U).toVector(),
        new MathVector(0.01,0,0)
      }), 
      0.1,
      Book
    );
    PrintStream Ouf=new PrintStream(new FileOutputStream("Book.inc"));
    //PrintStream Ouf=System.out;
    Ouf.println("#declare Orient=array[500][3] {");
    for(int i=0;i<500;i++) {
      Quaternion Q=new Quaternion(I.getX().subVector(6,4));
      Ouf.println("{<"+Q.transform(new MathVector(3,0,0))+">,");
      Ouf.println(" <"+Q.transform(new MathVector(0,1,0))+">,");
      Ouf.println(" <"+Q.transform(new MathVector(0,0,2))+">},");
      System.out.println(I.getX());
      I.step();
    }
    Ouf.println("}");
  }
}
