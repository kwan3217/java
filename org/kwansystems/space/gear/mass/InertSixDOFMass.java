package org.kwansystems.space.gear.mass;

import org.kwansystems.tools.rotation.MathMatrix;
import org.kwansystems.tools.vector.*;

import static java.lang.Math.*;
import java.util.*;

public class InertSixDOFMass extends SixDOFMass {
  public double mass;
  public MathVector CoM;
  public MathMatrix I;
  public double getMass(double T, SixDOFState RVEw) {
    return mass;
  }
  public MathVector getCoM(double T, SixDOFState RVEw) {
    return new MathVector(CoM);
  }
  public MathMatrix getI(double T, SixDOFState RVEw) {
    return new MathMatrix(I);
  }
  public InertSixDOFMass(String LName, double LMass, MathVector LCoM, MathMatrix LI) {
    super(LName);
    mass=LMass;
    CoM=LCoM;
    I=LI;
  }
  public InertSixDOFMass(String LName, InertSixDOFMass LMass) {
    this(LName,LMass.mass,new MathVector(LMass.CoM),new MathMatrix(LMass.I));
  }
  public static InertSixDOFMass MakeCylinderShell(String LName, double LMass, double ROuter, double RInner, double Aft, double Fore) {
    return new InertSixDOFMass(
      LName, 
      LMass,
      new MathVector((Aft+Fore)/2.0,0,0),
      SixDOFMass.HollowCylinderI(
        LMass, 
        ROuter, 
        RInner, 
        abs(Fore-Aft) 
      )
    );
  }
  public static InertSixDOFMass MakeCylinderShell2(String LName, double LMass, double LDens, double ROuter,  double Aft, double Fore) {
    return new InertSixDOFMass(
      LName, 
      LMass,
      new MathVector((Aft+Fore)/2.0,0,0),
      SixDOFMass.HollowCylinderI2(
        LMass, 
        LDens,
        ROuter, 
        abs(Fore-Aft) 
      )
    );
  }
  public static InertSixDOFMass MakeCylinderShell2(String LName, double LMass, double LDens, double ROuter,  MathVector Aft, double Fore) {
    return new InertSixDOFMass(
      LName, 
      LMass,
      new MathVector((Aft.X()+Fore)/2.0,Aft.Y(),Aft.Z()),
      SixDOFMass.HollowCylinderI2(
        LMass, 
        LDens,
        ROuter, 
        abs(Fore-Aft.X()) 
      )
    );
  }
  public static InertSixDOFMass MakeSolidCylinder(String LName, double LMass, double ROuter, double Aft, double Fore) {
    return new InertSixDOFMass(
      LName, 
      LMass,
      new MathVector((Aft+Fore)/2.0,0,0),
      SixDOFMass.SolidCylinderI(
        LMass, 
        ROuter, 
        abs(Fore-Aft) 
      )
    );
  }
  public static InertSixDOFMass CombineMass(String Name, List<SixDOFMass> Component) {
    return CombineMass(Name,Component,0,null);
  }
  public static InertSixDOFMass CombineMass(String Name, List<SixDOFMass> Component, double T, SixDOFState RVEw) {
    return new InertSixDOFMass(
      Name,
      CombineM(Component,T,RVEw),
      CombineCoM(Component,T,RVEw),
      CombineI(Component,T,RVEw)
    );      
  }
  public void CombineMass(List<SixDOFMass> Component, double T, SixDOFState RVEw) {
	InertSixDOFMass other=CombineMass("Other",Component,T,RVEw);
	CombineMass(other,T,RVEw);
  }
  public void CombineMass(SixDOFMass Other, double T, SixDOFState RVEw) {
	
  }
  public void Rotate(MathMatrix A2B) {
    CoM=A2B.transform(CoM);
    I=SixDOFMass.RotateI(I,A2B);
  }
  public void Translate(MathVector R) {
	CoM=MathVector.add(CoM,R);
	I=ParallelAxis(0,null,R.opp());
  }
  public String toString() {
    return   Name+
           "\nMass: "+mass+
           "\nCoM:  "+CoM+
           "\nI:    "+I;
  }
}
