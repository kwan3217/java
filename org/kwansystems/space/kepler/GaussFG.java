package org.kwansystems.space.kepler;

import static org.kwansystems.space.planet.Spheroid.*;

import org.kwansystems.tools.rootfind.*;
import org.kwansystems.space.*;
import org.kwansystems.tools.time.*;
import org.kwansystems.tools.vector.*;
import org.kwansystems.space.planet.*;
import static org.kwansystems.space.Constants.*;

public class GaussFG extends KeplerFG {
  public static GaussFG Sun=new GaussFG(SunGM);
  public GaussFG(double mu, double LDUinSU,TimeUnits LtimeUnits) {
    super(mu,LDUinSU,LtimeUnits);
  }
  public GaussFG(double mu) {
    super(mu);
  }
  public static double FindTTrialCore(double A, double SS, double X, double Y) {
    return (X*X*X*SS+A*Math.sqrt(Y));
  }
  public static double FindTTrial(double A, double r1l, double r2l, double Z) {
    double SS=(KeplerFG.S(Z));
    double CC=(KeplerFG.C(Z));
    if(CC==0) return Double.POSITIVE_INFINITY;
    double Y=r1l+r2l-A*(1-Z*SS)/Math.sqrt(CC);
    double X=Math.sqrt(Y/CC);
    return GaussFG.FindTTrialCore(A,SS,X,Y);
  }
  /**
   *Public interface for solving the Gauss problem. Given two points on orbit R1 and R2, time between points T,
   *and trajectory Type find velocities at the two points V1 and V2. This function actually just converts the
   *inputs to canonical units and calls {@link targetCU}
   * @param R1 Starting point
   * @param R2 Aim point
   * @param T  Travel time between the points
   * @param Type Trajectory type. For positive values of Type, an odd value of Type is for short way trajectories,
   * even for long-way trajectories, and half of (Type-1) is the number of complete laps
   * to do before reaching the target. Therefore,
   * <table>
   * <tr><td>Type</td><td>Minimum transfer angle</td><td>Maximum transfer angle</td></tr>
   * <tr><td>1</td><td align=right>0&deg;</td><td align=right>180&deg;</td></tr>
   * <tr><td>2</td><td align=right>180&deg;</td><td align=right>360&deg;</td></tr>
   * <tr><td>3</td><td align=right>360&deg;</td><td align=right>540&deg;</td></tr>
   * <tr><td>4</td><td align=right>540&deg;</td><td align=right>720&deg;</td></tr>
   * <tr><td>5</td><td align=right>720&deg;</td><td align=right>900&deg;</td></tr>
   * <tr><td>etc..</td></tr>
   * </table>
   * A negative type says find the prograde path, whether it is long way or short way. It will complete
   * less than abs(Type) full orbits.
   * <p>
   * Type=-1 is a prograde path with less than one complete lap<br>
   * Type=-2 is a prograde path with less than two complete lap<br>
   * Type=-3 is a prograde path with less than three complete lap<br>
   * etc...
   * @return
   *  an array of MathStates, {(R1,V1),(R2,V2)}
   */
  public Course target(MathStateTime S1, MathStateTime S2, int Type) {
    if(Type==0)Type=-1;
    if(Type<0) {
      MathVector Pole=MathVector.cross(S1.S.R(),S2.S.R());
      if(Pole.Z()>0) {
        Type=(-Type-1)*2+1;
        PrintNumber("Prograde is short way: Orbit Type ",Type);
      } else {
        Type=(-Type-1)*2+2;
        PrintNumber("Prograde is long way: Orbit Type ",Type);
      }
    }
    MathState[] result=target(S1.S.R(),S2.S.R(),S2.T.get(timeUnits)-S1.T.get(timeUnits),Type);
    return new Course(new MathStateTime(result[0],S1.T),new MathStateTime(result[1],S2.T),Type);
  }
  public static double ErrorAmount(double Y0, double Y) {
    if(Math.abs(Y0)<10) {
      return Math.abs(Y0-Y);
    } else {
      return Math.abs(Y0-Y)/Math.abs(Y0);
    }
  }
  private double FindZLo(final double A, final double r1l,final double r2l) {
    //Find the Z which results in a Y of exactly zero, by bisection
    double Zhi=0;
    double Y=1;
    double Zlo=-1;
    RootFunction R=new RootFunction() {
      public double F(double Z) {
        return r1l+r2l-A*(1-Z*S(Z))/Math.sqrt(C(Z));
      }
    };
    while (Y>0) {
      Zlo*=2;
      Y=R.F(Zlo);
    }
    double Z=RootFind.Find(
      new Bisector(Epsilon,R),  
      0, //Target dependent value
      Zlo,//First independent value
      Zhi //Second independent value 
    );
    return Z+1e-5;
  }
  private double FindZLo2(double A, double r1l,double r2l, double T) {
    //Find the Z which results in a TTrial of less than T
    double Z=-1;
    double TTrial=GaussFG.FindTTrial(A,r1l,r2l,Z)-T;
    while (TTrial>0) {
      Z*=2;
      TTrial=GaussFG.FindTTrial(A,r1l,r2l,Z)-T;
    }
    return Z;
  }
  private MathState[] target(MathVector R1, MathVector R2, double T,  int type) {
    T=SUtoCU(T);
    MathVector R1CU=SUtoCU(R1);
    MathVector R2CU=SUtoCU(R2);
    MathState[] result=targetCU(R1CU,R2CU,T,type);
    for(int i=0;i<result.length;i++) result[i]=CUtoSU(result[i]);
    return result;
  }
  private MathState[] targetCU(MathVector R1,MathVector R2,double T, int Type) {
    if(T<0) throw new IllegalArgumentException("Time to intercept is negative. Time travel is not allowed in this universe!");
    if(Type<0) throw new IllegalArgumentException("Make up your mind! You have to decide the type before here");
    final double r1l=R1.length();
    PrintNumber("T:   ",T);
    PrintVector("R1: ",R1);
    final double r2l=R2.length();
    PrintVector("R2: ",R2);
    PrintNumber("Type: ",Type);
    double DeltaNu=MathVector.vangle(R1,R2);
    int DM;
    int Revs=(Type-1)/2;
    PrintNumber("Revs: ",Revs);
    //short-way and long-way are reversed for odd-numbers of complete revs
    if((Revs%2==1)^(Type%2==1)) {
      Println("Short way");
      DM=1;
    } else {
      Println("Long way");
      DM=-1;
      DeltaNu=2*Math.PI-DeltaNu;
    }
    if(Revs>0) {
      double minA=r1l/2;
      double minT=Revs*Math.sqrt(Math.PI*2*minA*minA*minA);
      if(minT>T) throw new IllegalArgumentException("Can't do it! Minimum trip time for "+Revs+" revs is "+minT+"TU, more than requested "+T+"TU");
    }
    PrintNumber("DM:      ",DM);
    PrintNumber("DeltaNu: ",DeltaNu);
    final double A=DM*Math.sqrt(r1l*r2l*(1+Math.cos(DeltaNu)));
    PrintNumber("A: ",A);
    double Zlo,Zhi;
    if(Revs<1) {
      Println("less than one rev");
      if(Type==1) {
        Zlo=FindZLo(A,r1l,r2l);
      } else {
        Zlo=FindZLo2(A,r1l,r2l,T);
      }
      Zhi=Math.pow(2*Math.PI,2);
    } else {
      Println("more than one rev");
      //Use Zeno's method
      double Zbound;
      Zlo=Math.pow((2*Revs+1)*Math.PI,2); //Z that gives the lowest TIME, not necessarily lowest Z
      //Zbound is the value of Z which gives an infinite T
      if(Type%2==1) {
        Zbound=Math.pow((2*Revs)*Math.PI,2);
      } else {
        Zbound=Math.pow((2*Revs+2)*Math.PI,2);
      }
      Zhi=(Zbound+Zlo)/2; //Z that gives the highest TIME, not necessarily highest Z
      double Thi;
      do {
        Thi=FindTTrial(A,r1l,r2l,Zhi);
        Zhi=(Zbound+Zhi)/2; //Split the difference between current Zhi and bound
      } while(Thi<T);
    }
    Println("Zlo: "+Zlo);
    Println("Zhi: "+Zhi);
    //Solve it by bisection
    double Z=RootFind.Find(
      new Bisector(
        Epsilon,
        new RootFunction() {
          public double F(double Z) {
            return GaussFG.FindTTrial(A,r1l,r2l,Z);
          }
        }
      ),
      T,Zlo,Zhi
    );
    double SS;
    double CC;
    double Y;
    SS=(S(Z));
    CC=(C(Z));
    Y=r1l+r2l-A*(1-Z*SS)/Math.sqrt(CC);
    double F=1-Y/r1l;
    PrintNumber("F: ",F);
    double G=A*Math.sqrt(Y);
    PrintNumber("G: ",G);
    double GPrime=1-Y/r2l;
    PrintNumber("GPrime: ",GPrime);
    MathVector V1=MathVector.sub(R2,R1.mul(F)).div(G);
    PrintVector("V1: ",V1);
    MathVector V2=MathVector.sub(R2.mul(GPrime),R1).div(G);
    PrintVector("V2: ",V2);
    return new MathState[] {new MathState(R1,V1),new MathState(R2,V2)};
  }
  public static void main(String args[]) {
    GaussFG G=new GaussFG(WGS84.GM,WGS84.Re,TimeUnits.Seconds);
    //KG.debugStream=new SystemOutVerbose();
    /* Gauss Test */
    /*
    MathVector TestR1=new MathVector(15945340,0,0);
    MathVector TestR2=new MathVector(12214838.99,10249467.31,0);
    double TestT=76*60;
    MathState[] TestType1=G.target(TestR1,TestR2,TestT,1);
    System.out.println("Calculated V1: "+TestType1[0]);
    System.out.println("Documented V1: "+new MathVector(2058.913, 2915.965,0));
    System.out.println("Calculated V2: "+TestType1[1]);
    System.out.println("Documented V2: "+new MathVector(-3451.569,0910.301, 0));
//    MathState[] TestType2=GaussFG(TestR1,TestR2,TestT,1,2);
    System.out.println("Kepler propagation from R1 and V1 to R2: "+G.propagate(TestType1[0], TestT));
    System.out.println("Documented R2:                           "+TestR2);
    System.out.println("Gauss Result V2:                         "+TestType1[1].V());
    */
    MathStateTime TestR1=Planet.Earth.Orbit.getStateTime(new Time(2009,3,6,3,48,43,TimeUnits.Seconds,TimeScale.UTC,TimeEpoch.J2000));
    MathStateTime TestR2=new MathStateTime(new MathState(
      new MathVector(1.3451110696485829E+11,-7.1504959895958200E+10, 6.2126381988037741E+07),
      new MathVector(1.2846877934298604E+04, 2.6376820375113237E+04,-2.5048951035168587E+02)),
      new Time(2009,9,1,0,0,0,TimeUnits.Seconds,TimeScale.TDB,TimeEpoch.J2000)
    );
    Course C=Sun.target(TestR1, TestR2, -1);
    System.out.println(C.ResolveDeltaVdepart(Planet.Earth, 185));
  }

}
