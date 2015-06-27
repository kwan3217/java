package org.kwansystems.space.gear.guidance;

import static java.lang.Math.*;

import org.kwansystems.space.gear.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.vector.*;

public abstract class PIDGuidance extends Guidance {
  //Control law constants
  //Elements end up being <Roll,Pitch,Yaw>
  //Proportional and damping coeffs should have opposite signs
  //Positive roll coeff, negative pitch, positive yaw
  //Go lots easier on coeff to adapt to lesser I around roll
  public MathVector Kp,Kd,Ki;
  private boolean GuidanceDigitalValid;
  private double TLastDigital;
  public double TDigitalLoop;
  private Quaternion Target,LastTarget;
  public PIDGuidance(SixDOFVehicle LV) {
    this(LV,new MathVector());
  }
  public PIDGuidance(SixDOFVehicle LV, MathVector LKp) {
    this(LV,LKp,new MathVector());
  }
  public PIDGuidance(SixDOFVehicle LV, MathVector LKp, MathVector LKd) {
	  this(LV,LKp,LKd,new MathVector());
  }
  public PIDGuidance(SixDOFVehicle LV, MathVector LKp, MathVector LKd, MathVector LKi) {
    super(LV);
    TDigitalLoop=1;
    GuidanceDigitalValid=false;
    Kp=LKp;
    Ki=LKi;
    Kd=LKd;
  }
  /** 
   * Calculate the target quaternion for the next major cycle.
   * @param T
   * @param RVEw
   * @param FuelLevels
   */
  public abstract Quaternion majorCycle(double T, SixDOFState RVEw, MathVector FuelLevels);
  /** Navigation, Guidance, Control. Navigation is already half done for us. We get fresh state in RVEw,
   *  and E and w are what we want. A real system would have to use its inertial platform, but a real 
   *  system doesn't calculate guidance on the same computer which is simulating it, since it is real.
   *  All Navigation has to do here is decide where to go.
   * @param T
   * @param RVEw
   * @param FuelLevels
   * @param IsMajor
   */
  public void minorCycle(double T, SixDOFState RVEw, MathVector FuelLevels, boolean IsMajor) {
	if(IsMajor) {
      V.Discrete(T,RVEw,FuelLevels);		
	  if((T-TLastDigital)>TDigitalLoop) GuidanceDigitalValid=false;
	}
    if(!GuidanceDigitalValid) {
	    LastTarget=Target;
	    Target=majorCycle(T,RVEw,FuelLevels);
	  }
    //Control
    //So, you are at an orientation qs and need to get to a quaternion qt.
    //  
    //1) What's the quaternion displacement?
    //Target quaternion is start quaternion rotated further by quaternion displacement
    //qt=(qs)(qd)  
    //Solve for quaternion displacement. Pre-multiply both sides by inverse of start
    //(qs^-1)(qt)=(qs^-1)(qs)(qd)
    //(qs^-1)(qt)=qd
    //For unit quaternions, quaternion inverse equals quaternion conjugate
    //(qs*)(qt)=qd
    Quaternion Displacement=Quaternion.mul(Quaternion.conj(RVEw.E()),Target);

    //2) How do you use this to determine torque?
  	//Quaternion displacement is the quaternion you have to rotate through to get
	  //from start to target, in start frame. Treat it as an axis and angle, then 
  	//torque around quaternion axis by amount proportional to quaternion angle.
	  //I don't like this, since it requires an acos, but I haven't figured out
  	//anything better yet. There are no singularities in it.
	  AxisAngle AnA=new AxisAngle(Displacement); 
	  MathVector OrientError=AnA.Axis.mul(AnA.Angle);
		   
    //Control    
    //3) Control law. Control=Kp*OrientError+Kd*AngularVelocity. Kp and Kd are
    //matrices in the general case, but we assume diagonal matrices represented by vectors
    //for now...
    MathVector ControlFactor=MathVector.add(MathVector.mul(Kp,OrientError),MathVector.mul(Kd,RVEw.w()));

    //4) Resolve it into actuator parameters. You can only resolve this if torque really
    //is a vector, but it seems to work well enough.
    if(IsMajor) {
      V.Record(T,"OrientError","deg",OrientError.mul(180.0/Math.PI));
      V.Record(T,"RotationRate","deg/sec",RVEw.w().mul(180.0/Math.PI));
      V.Record(T,"RotationCommand","deg", ControlFactor.mul(180.0/Math.PI));
    }
		  
    V.Steer(T,RVEw,IsMajor,ControlFactor);
  }
  public void Guide(double T, SixDOFState RVEw, MathVector FuelLevels, boolean IsMajor) {
    minorCycle(T,RVEw,FuelLevels,IsMajor);
  }
}
