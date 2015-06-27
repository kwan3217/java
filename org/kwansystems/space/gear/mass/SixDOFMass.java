package org.kwansystems.space.gear.mass;

import org.kwansystems.tools.rotation.MathMatrix;
import org.kwansystems.tools.vector.*;

import static java.lang.Math.*;
import java.util.*;

/**
 * Finite mass element with rotational inertia. Represents an extended 
 *
 */
public abstract class SixDOFMass extends ThreeDOFMass {
  public SixDOFMass(String LName) {super(LName);}
  public abstract double getMass(double T, SixDOFState RVEw);
  public double getMass(double T, MathState RV) {
    return getMass(T,new SixDOFState(RV));
  }
  public abstract MathVector getCoM(double T, SixDOFState RVEw);
  public abstract MathMatrix getI(double T, SixDOFState RVEw);
  /**Calculates inertia tensor for solid cylinder. Cylinder axis is along X axis.
   * @param M Total mass of cylinder
   * @param R radius of cylinder
   * @param L length of cylinder
   * @return inertia tensor with respect to the center of mass and these axes.
   */
  public static MathMatrix SolidCylinderI(double M, double R, double L) {
    double IAxis=M*R*R/2.0;
    double IDiameter=IAxis/2.0+M*L*L/12.0;
    return new MathMatrix(IAxis,IDiameter,IDiameter);
  }
  public static MathMatrix HollowCylinderI(double M, double ROuter, double RInner, double L) {
    double OuterVolume=PI*ROuter*ROuter*L;
    double InnerVolume=PI*RInner*RInner*L;
    double TotalVolume=OuterVolume-InnerVolume;
    double Density=M/TotalVolume;
    double OuterMass=OuterVolume*Density;
    double InnerMass=InnerVolume*Density;
    return MathMatrix.sub(SolidCylinderI(OuterMass, ROuter, L),SolidCylinderI(InnerMass, RInner,L));
  }
  public static MathMatrix HollowCylinderI2(double M, double Dens, double ROuter, double L) {
    double OuterVolume=PI*ROuter*ROuter*L;
    double OuterMass=OuterVolume*Dens;
    double InnerMass=OuterMass-M;
    double InnerVolume=InnerMass/Dens;
    double RInner=sqrt(InnerVolume/(PI*L));
    return MathMatrix.sub(SolidCylinderI(OuterMass, ROuter, L),SolidCylinderI(InnerMass, RInner,L));
  }
  public static MathMatrix SolidSphereI(double M, double R) {
    double I=M*R*R*0.4;
    return new MathMatrix(I,I,I);
  }
  public static MathMatrix HollowSphereI(double M, double RInner, double ROuter) {
    double OuterVolume=4.0/3.0*PI*ROuter*ROuter*ROuter;
    double InnerVolume=4.0/3.0*PI*RInner*RInner*RInner;
    double TotalVolume=OuterVolume-InnerVolume;
    double Density=M/TotalVolume;
    double OuterMass=OuterVolume*Density;
    double InnerMass=InnerVolume*Density;
    return MathMatrix.sub(SolidSphereI(OuterMass, ROuter),SolidSphereI(InnerMass, RInner));
  }
  public static MathMatrix ShellSphereI(double M, double R) {
    double I=M*R*R*2.0/3.0;
    return new MathMatrix(I,I,I);
  }
  /**Returns inertia tensor for right triangular prism.
   * @param M Total mass of prism
   * @param a length of prism base along +x axis
   * @param b length of prism base along +y axis
   * @param c height of prism (along +z axis)
   * I is with respect to the center of mass and these axes.
   * To describe a prism out -x or -y, use a negative a or b
   */
  public static MathMatrix TriangularPrismI(double M, double a, double b, double c) {
    return new MathMatrix(new double[][] {
        {2*b*b+3*c*c,        a*b,           0},
        {a*b,        2*a*a+3*c*c,           0},
        {0,                    0, 2*a*a+2*b*b}
      }).mul(M/36);
  }
  /*Returns I for right rectangular prism:
    a and b describe the sides of the bases of the prism, and are laid out along
    the +x and +y axes respectively. c is the height of the prism, and is laid out
    along the +z axis. I is with respect to the center of mass and these axes.
   */
  public static MathMatrix RectangularPrismI(double M, double a, double b, double c) {
    return new MathMatrix(new double[][] {
        {b*b+c*c,       0,       0},
        {      0, a*a+c*c,       0},
        {      0,       0, a*a+b*b}
      }).mul(M/12);
  }    
  //General parallel axis theorem (Translate an I to a parallel frame with a different center)
  public static MathMatrix ParallelAxis(MathVector CoM, MathMatrix I, double M, MathVector Ref) {
    MathVector CoM_c = MathVector.sub(Ref,CoM);
    double a = CoM_c.get(0);
    double b = CoM_c.get(1);
    double c = CoM_c.get(2);
    return MathMatrix.add(
      I,new MathMatrix(new double[][] {
          { b*b+c*c,    -a*b, -a*c   },
          {-a*b,     c*c+a*a, -b*c   },
          {-a*c,        -b*c, a*a+b*b}}
        ).mul(M)
      );
  }    
  //Parallel Axis with original CoM at origin
  public static MathMatrix ParallelAxis(MathMatrix I,double M, MathVector Ref) {
    return ParallelAxis(new MathVector(),I,M,Ref);
  }
  public MathMatrix ParallelAxis(double T, SixDOFState RVEw, MathVector Ref) {
    return ParallelAxis(getCoM(T,RVEw), getI(T,RVEw), getMass(T,RVEw),Ref);
  }
  public static MathMatrix RotateI(MathMatrix IA, MathMatrix A2B) {
    return MathMatrix.mul(MathMatrix.mul(A2B,IA),A2B.T());
  }
  public static double CombineM(List<SixDOFMass> Component, double T, SixDOFState RVEw) {
    double TotalMass=0;
    for(SixDOFMass M:Component) {
      if (M!=null && M.Active) TotalMass+=M.getMass(T,RVEw);
    }
    return TotalMass;
  }
  public static MathVector CombineCoM(List<SixDOFMass> Component, double T, SixDOFState RVEw) {
    MathVector TotalMoment=new MathVector();
    double TotalMass=0;
    for(SixDOFMass M:Component) {
      if(M!=null && M.Active) {
        double ThisMass=M.getMass(T,RVEw);
        TotalMass+=ThisMass;
        TotalMoment=MathVector.add(TotalMoment,M.getCoM(T,RVEw).mul(ThisMass));
      }
    }
    return TotalMoment.div(TotalMass);
  }
  public static MathMatrix CombineI(List<SixDOFMass> Component, double T, SixDOFState RVEw) {
    MathVector CoM=CombineCoM(Component, T, RVEw);
    MathMatrix result=new MathMatrix(3,3);
    for(SixDOFMass M:Component) {
      if(M!=null && M.Active) result=MathMatrix.add(result,M.ParallelAxis(T,RVEw,CoM));
    }
    return result;
  }
  public static void main(String args[]) {
    System.out.println(SolidCylinderI(100, 1, 3));
    System.out.println(HollowCylinderI(100, 1, 0.5, 3));
    System.out.println(TriangularPrismI(100, 1,1,1));
    System.out.println(TriangularPrismI(100,-1,1,1));
    System.out.println(RotateI(TriangularPrismI(100,1,1,1), MathMatrix.Rot1d(180)));
  }
  public String toString() {
    return Name;
  }
}
