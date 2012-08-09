//KeplerGaussFG.java
//Solve the kepler and gauss problem using state vectors and universal variable formulation
//If you are looking for obital elements, you are in the wrong place.
 
package org.kwansystems.space.kepler;

import org.kwansystems.tools.rootfind.*;
import org.kwansystems.tools.time.*;
import org.kwansystems.tools.vector.*;
import org.kwansystems.tools.*;

import static org.kwansystems.space.Constants.*;
import static org.kwansystems.space.planet.Spheroid.WGS84;


/**
 * Algorithms for solving the Kepler and Gauss problems. the two fundamental problems in
 * two-body mechanics
 * <p>
 * The <i>Kepler Problem</i>, or prediction problem, is as follows: Given the GM of the
 * central body, and the position and velocity of a test particle of negligible mass at 
 * one time, find the position and velocity at any other time.
 * <p>
 * The <i>Gauss Problem</i>, or targeting problem, is as follows: Given the GM of the 
 * central body, the position at which a test particle is now, the target position where
 * it will be, and the time between the postions, find the velocity of the particle at 
 * both points such that it will travel from the initial position to the target position
 * in the given time.
 * <p> 
 * This basically follows the Universal Variable formation found in Bate, Muller, and White
 * chapters 4 and 5.
 * <p>
 * These algorithms make use of canonical units. Canonical units are distance and time units
 * relating to a particular central body, such that the GM of that body is 1. In canonical 
 * units, an object in a circular orbit of radius one Distance Unit (DU) has a speed of
 * one DU per Time Unit (TU), and therefore an angular velocity of one radian per TU. The 
 * length of a DU is arbitrary, but is customarily the radius of the central body for planets
 * and moons, and 1 AU for the Sun.   
 */
public class KeplerFG implements Verbose {
  protected Verbose debugStream=null;
  protected double DUinSU;
  protected TimeUnits timeUnits=TimeUnits.Seconds;
  protected double Epsilon=1e-10;
  protected double mu;
  public static final KeplerFG Sun=new KeplerFG(SunGM);
  public KeplerFG(double Lmu,double LDUinSU,TimeUnits LtimeUnits) {
    mu=Lmu;  
    DUinSU=LDUinSU;
    timeUnits=LtimeUnits;
  }
  public KeplerFG(double Lmu) {
    this(Lmu,(Lmu<4e14)?WGS84.Re:MPerAU,TimeUnits.Seconds);
  }

  //Canonical unit functions
  private double TimeX() {
  	return Math.sqrt(Math.pow(DUinSU,3)/mu);
  }
  public double SUtoCU(double T) {
	  return T/TimeX();  
  }
  public MathVector SUtoCU(MathVector SU) {
    return SU.div(DUinSU);
  }
  public MathState SUtoCU(MathState SU) {
	  return new MathState(SU.R().div(DUinSU),SU.V().div(DUinSU/TimeX()));
  }
  public MathStateTime SUtoCU(MathStateTime SU) {
	  return new MathStateTime(SUtoCU(SU.S),SU.T);
  }
  public double CUtoSU(double T) {
	return T*TimeX();  
  }
  public MathVector CUtoSU(MathVector CU) {
    return CU.mul(DUinSU);
  }
  public MathState CUtoSU(MathState CU) {
	return new MathState(CU.R().mul(DUinSU),CU.V().mul(DUinSU/TimeX()));
  }
  public MathStateTime CUtoSU(MathStateTime CU) {
	return new MathStateTime(CUtoSU(CU.S),CU.T);
  }
  //Auxiliary function C(Z)
  public static double C(double Z) {
    if(Z > 0.0) {
      return ((1.0-Math.cos(Math.sqrt(Z)))/Z);
    } else {
      if(Z<0.0) {
        return ((1.0-Math.cosh(Math.sqrt(-Z)))/Z);
      } else {
        return (1.0/2.0);
      }
    }
  }
  //Auxiliary function S(Z)
  public static double S(double Z) {
    if(Z>0.0) {
      return ((Math.sqrt(Z)-Math.sin(Math.sqrt(Z)))/Math.pow(Z,3.0/2.0));
    } else {
      if(Z<0.0) {
        return ((Math.sinh(Math.sqrt(-Z))-Math.sqrt(-Z))/Math.pow(-Z,3.0/2.0));
      } else {
        return (1.0/6.0);
      }
    }
  }
  //Auxiliary variable f
  //R=f*R0+G*V0
  private static double F(double X,double Alpha, double r0l) {
    double Z=Math.pow(X,2)*Alpha;
    return (1.0-Math.pow(X,2)*C(Z)/r0l);
  }
  //Auxiliary variable g
  //R=f*R0+G*V0
  private static double G(double X, double Alpha,  double T) {
    double Z=Math.pow(X,2)*Alpha;
    return (T-Math.pow(X,3)*S(Z));
  }
  //derivative of f with respect to time df/dt
  //V=fdot*R0+gdot*V0
  private static double FDot(double X,double Alpha,double R, double R0) {
    double Z=Math.pow(X, 2)*Alpha;
    return (X*(Z*(S(Z))-1)/(R0*R));
  }
  //derivative of g with respect to time dg/dt
  //V=fdot*R0+gdot*V0
  private static double GDot(double X,double Alpha,double R) {
    double Z=Math.pow(X,2)*Alpha;
    return (1-Math.pow(X,2)*(C(Z))/R);
  }
  public MathState propagate(MathStateTime ST0, Time T) {
    return propagate(ST0.S,T.get(timeUnits)-ST0.T.get(timeUnits));
  }
  public MathState propagate(MathState S0, double T) {
    T=SUtoCU(T);
    PrintNumber("TCU: ",T);
    MathState SCU=SUtoCU(S0);
    Println("SCU: "+SCU);
    MathState result=CUtoSU(propagateCU(SCU,T));
    Println("ResCU: "+result);
    return result;
  }
  /**
   * Algorithm for solving the Kepler problem in canonical units: Given state at some time 
   * calculate state at some other time
   * @param S0 Initial state, in canonical distance and speed units.
   * @param T Elapsed time from time of first state to propagate to, in canonical time units. 
   *          To propagate into the past, pass a negative value here.
   * @return State at given elapsed time, in canonical distance and speed units
   */
  private MathState propagateCU(MathState S0, double T) {
    MathVector R0=S0.R();
    MathVector V0=S0.V();
    MathVector R;
    MathVector V;
    PrintVector("R0: ",R0);
    PrintVector("V0: ",V0);
    PrintNumber("T: ",T);
    if(Math.abs(T)< 1e-7) {
      R=(MathVector)R0.clone();
      V=(MathVector)V0.clone();
    } else {
      final double r0l=R0.length();
      PrintNumber("r0l: ",r0l);
      final double v0l=V0.length();
      PrintNumber("v0l: ",v0l);
      final double R0dotV0=MathVector.dot(R0,V0);
      final double Energy=Math.pow(v0l,2)/2.0-1.0/r0l;
      PrintNumber("Energy: ",Energy);
      final double Alpha=-(2.0*Energy);
      PrintNumber("Alpha: ",Alpha); //Alpha=1/A, never is infinite
      PrintNumber("A: ",1.0/Alpha);
      double Xp1;
      if(Alpha<0) {
        //hyperbolic
        double AA=Math.signum(T)*Math.sqrt(-1.0/Alpha);
        double BB=-2*T;
        double CC=R0dotV0;
        double DD=Math.signum(T)*Math.sqrt(-1.0/Alpha)*(1.0-r0l*Alpha);
        double EE=(CC+DD)/Alpha;
        double FF=Math.log(BB/EE);
        Xp1=AA*FF;
      } else {
        //elliptical
        Xp1=T*Alpha;
      }
      PrintNumber("X0: ",Xp1);
      //Iterate by Newton to fine tune
      double X=RootFind.Find(
        new Newton(  //Root finder
          Epsilon,      //Tolerance
          new RootDeriv() {  //Equations
            public double F(double X) {
              double Z=Math.pow(X,2)*Alpha;
              return R0dotV0*Math.pow(X,2)*(C(Z))+(1.0-r0l*Alpha)*Math.pow(X,3)*(S(Z))+r0l*X;
            }
            public double dFdx(double X) {
              double Z=Math.pow(X,2)*Alpha;
              return Math.pow(X, 2)*(C(Z))+R0dotV0*X*(1-Z*(S(Z)))+r0l*(1.0-Z*(C(Z)));
            }
          }
        ),
        T, //Target dependent value
        Xp1,//First independent value
        0  //Second independent value (ignored)
      );

      R=MathVector.add(R0.mul(F(X, Alpha, r0l)),V0.mul(G(X, Alpha, T)));
      PrintVector("R: ",R);
      double rl=R.length();
      V=MathVector.add(R0.mul(FDot(X, Alpha, rl, r0l)),V0.mul(GDot(X, Alpha, rl)));
      PrintVector("V: ",V);
    }
    return new MathState(R,V);
  }
  public void Print(String Label) {
    if(debugStream!=null)debugStream.Print(Label);
  }
  public void PrintNumber(String Label, double Value) {
    if(debugStream!=null)debugStream.PrintNumber(Label,Value);
  }
  public void PrintVector(String Label, MathVector Value) {
    if(debugStream!=null)debugStream.PrintVector(Label,Value);
  }
  public void Println(String Label) {
    if(debugStream!=null)debugStream.Println(Label);
  }
  public static void main(String args[]) {
    KeplerFG KG=new KeplerFG(WGS84.GM,WGS84.Re,TimeUnits.Seconds);
    //KG.debugStream=new SystemOutVerbose();
    /* Kepler Test */
    MathState TestX=new MathState(1131340,-2282343,+6672423,-5643.05,+4303.33,+2428.79);
    double TestT=40*60;
    MathState Result=KG.propagate(TestX,TestT);
    System.out.println("Calculated Result: "+Result);
    System.out.println("Documented Result: "+new MathState(-4219853, 4363116, -3958789, 3689.736, -1916.620, -6112.528));
  }
}

