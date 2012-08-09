package org.kwansystems.space.gear.actuator;

import org.kwansystems.space.gear.*;
import org.kwansystems.space.universe.*;
import org.kwansystems.space.planet.atmosphere.*;
import org.kwansystems.tools.vector.*;

public class WingForceTorque extends ForceTorque {
  public double FoilSpan;
  public double FoilChord;
  public double FoilArea;
  public double Aspect;
  public double Mach;
  public double Re;
  public double cDe;
  public double cDw;
  public double cL;
  public double cD;
  public double cN;
  public double cA;
  public double cM;
  public double q;
  public double rho,mu;
  public double BodyAlpha,BodyBeta,BodyAoA;
  public MathVector CoFOffset;
  /**Inertial velocity of wind at this point in space, m/s */
  public MathVector UnivWind; 
  /**Relative wind at this point in space in frame parallel to inertial, m/s */
  public MathVector RelWind;  
  /**Relative wind at this point in body frame, m/s */
  public MathVector BodyWind; 
  /**Velocity of wing CoL relative to body in body frame, m/s */
  public MathVector WingBodyVel; 
  /**Relative wind at wing CoF in body frame, m/s */
  public MathVector WingWindBody;   
  /**Relative wind at wing CoF in Wing frame, m/s */
  public MathVector WingWind;   
  public MathVector WingWindNormal; 
  public double alpha,beta,AoA;  //vertical, horizontal, and total angle of attack, rad
  public MathVector NormalForce;
  public MathVector AxialForce;
  public MathVector TorqueF;
  public MathVector TorqueL;
  public WingForceTorque() {
    NormalForce=new MathVector();
    AxialForce=new MathVector();
    Force=new MathVector();
    TorqueF=new MathVector();
    TorqueL=new MathVector();
    Torque=new MathVector();
  }
  public WingForceTorque(double T, SixDOFState RVEw, Universe U, MathVector CoF, MathVector CoM) {
    //Get the inertial wind speed at this point
    UnivWind=U.getWind(T,RVEw);  
    //Subtract the velocity of the vehicle to get the relative wind
    RelWind=MathVector.sub(UnivWind,RVEw.V());
    //Rotate the relative wind to get the body wind
    BodyWind=RVEw.E().invTransform(RelWind);
    //Calculate the speed of the airfoil relative to the center of mass of the vehicle in the body frame
    CoFOffset=MathVector.sub(CoF,CoM);
    WingBodyVel=MathVector.cross(RVEw.w(),CoFOffset);
    //Subtract the speed of the airfoil to get the wing relative wind in the body frame
    WingWindBody=MathVector.sub(BodyWind,WingBodyVel);
    AirProperties A=U.getAtm(T,RVEw);
    rho=A.Density;
    mu=A.Viscosity;
    if(rho==0) return;
    if(A.VSound>0) {
      Mach=WingWindBody.length()/A.VSound;
    } else {
      Mach=0;
    }
    MathVector BodyWindHat=BodyWind.normal();
    BodyAlpha=Math.atan2(BodyWind.Z(),-BodyWind.X()); 
    BodyBeta= Math.atan2(BodyWind.Y(),-BodyWind.X()); 
    BodyAoA=  Math.acos(-BodyWindHat.X());
  }
  public String toString() {
    return "FoilSpan:      "+FoilSpan+
           "\nFoilChord:   "+FoilChord+
           "\nFoilArea:    "+FoilArea+
           "\nAspect:      "+Aspect+
           "\nUnivWind:    "+UnivWind+ 
           "\nRelWind:     "+RelWind+ 
           "\nBodyWind:    "+BodyWind+
           "\nWingBodyVel: "+WingBodyVel+ 
           "\nWingWindBody:"+WingWindBody+   
           "\nWingWind:    "+WingWind+   
           "\nWingWindN:   "+WingWindNormal+
           "\nAlpha deg: "+Math.toDegrees(alpha)+
           "\nBeta deg:  "+Math.toDegrees(beta)+
           "\nAoA deg:   "+Math.toDegrees(AoA)+
           "\nBodyAlpha: "+Math.toDegrees(BodyAlpha)+
           "\nBodyBeta:  "+Math.toDegrees(BodyBeta)+
           "\nBodyAoA:   "+Math.toDegrees(BodyAoA)+
           "\nMach:      "+Mach+
           "\nReynolds:  "+Re+
           "\ncD Form:   "+cDe+
           "\ncD Wave:   "+cDw+
           "\ncD Total:  "+cD+
           "\ncL:        "+cL+
           "\ncN:        "+cN+
           "\ncA:        "+cA+
           "\ncM:        "+cM+
           "\nDynPres:   "+q+
           "\nDens:      "+rho+
           "\nViscosity: "+mu+
           "\nNForce:    "+NormalForce+
           "\nAForce:    "+AxialForce+
           "\nTorqueF:   "+TorqueF+
           "\nTorqueL:   "+TorqueL+
           "\nTorque:    "+Torque+
           "\nForce:     "+Force;
  }
}
