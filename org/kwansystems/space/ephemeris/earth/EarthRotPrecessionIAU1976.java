/**
 * 
 */
package org.kwansystems.space.ephemeris.earth;

import org.kwansystems.space.ephemeris.*;
import org.kwansystems.space.ephemeris.RotatorEphemeris.*;
import org.kwansystems.tools.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeEpoch;
import org.kwansystems.tools.time.TimeScale;
import org.kwansystems.tools.time.TimeUnits;
import org.kwansystems.tools.rotation.*;

import static org.kwansystems.tools.Scalar.*;
import static org.kwansystems.tools.rotation.MathMatrix.*;
import static org.kwansystems.tools.time.TimeEpoch.*;
import static org.kwansystems.tools.time.TimeScale.*;
import static org.kwansystems.tools.time.TimeUnits.*;
import static org.kwansystems.space.ephemeris.RotatorEphemeris.Frame.*;

/**
 * Calculate the precession matrix according to IAU 1976 Precession model.
 * This gives a rotation from the mean equator and equinox of date to the
 * J2000 equator and equinox of date. 
 *
 */
public class EarthRotPrecessionIAU1976 extends RotatorEphemeris {
    
  public EarthRotPrecessionIAU1976(Frame Lfrom, Frame Lto) {
    super(Lfrom,Lto);
    naturalFrom=MOD;
    naturalTo=J2000Ecl;
    setInv(Lfrom,Lto);
  }
  // Data from Explanatory Supplement p104
  // also Precession Matrix Based on IAU(1976) System of Astronomical Constants eq7
  /** Polynomial to generate the zeta_a angle as a function of time. 
   * Takes julian centuries from J2000 as input, returns zeta_a in arc seconds as output */
  private static final Polynomial zetaA1Poly=new Polynomial(new double[] {
      +2306.2181,1.39656,-0.000139
  },Polynomial.order.ConstFirst);
  private static final Polynomial zetaA2Poly=new Polynomial(new double[] {
      +0.30188,-0.000344
  },Polynomial.order.ConstFirst);
  private static final Polynomial zetaAPoly= new Polynomial(new double[] {
      0,zetaA1Poly.Coeffs[0],zetaA2Poly.Coeffs[0],+0.017998
  },Polynomial.order.ConstFirst);
  /** Polynomial to generate the theta_a angle as a function of time. 
   * Takes julian centuries from J2000 as input, returns theta_a in arc seconds as output */
  private static final Polynomial thetaA1Poly=new Polynomial(new double[] {
      +2004.3109,-0.85330,-0.000217
  },Polynomial.order.ConstFirst);
  private static final Polynomial thetaA2Poly=new Polynomial(new double[] {
      -0.42665,-0.000217
  },Polynomial.order.ConstFirst);
  private static final Polynomial thetaAPoly=new Polynomial(new double[] {
      0,thetaA1Poly.Coeffs[0],thetaA2Poly.Coeffs[0],-0.041833
  },Polynomial.order.ConstFirst);
  /** Polynomial to generate the z_a angle as a function of time. 
   * Takes julian centuries from J2000 as input, returns z_a in arc seconds as output */
  private static final Polynomial zA1Poly=zetaA1Poly;
  private static final Polynomial zA2Poly=new Polynomial(new double[] {
      +1.09468,+0.000066
  },Polynomial.order.ConstFirst);
  private static final Polynomial zAPoly=    new Polynomial(new double[] {
      0,zA1Poly.Coeffs[0],zA2Poly.Coeffs[1],+0.018203
  },Polynomial.order.ConstFirst);
  private static final Time E0=new Time(0,Days,TDT,J2000);  
  private static double zetaA(double t) {
    return zetaAPoly.eval(t);
  }
  private static double zetaA(double t,double T) {
    Polynomial thisPoly=new Polynomial(new double[] {
      0,zetaA1Poly.eval(T),zetaA2Poly.eval(T),zetaAPoly.Coeffs[3]
    });
    return thisPoly.eval(t);
  }
  public static double zetaA(Time t) {
    return zetaA(t.get(TimeUnits.Centuries,TimeScale.TDT,TimeEpoch.J2000));
  }
  public static double zetaA(Time Ef, Time Ed) {
    double T=(Ef.get(Days,TDT,J2000)-E0.get(Days,TDT,J2000))/36525.0;
    double t=(Ed.get(Days,TDT,J2000)-Ef.get(Days,TDT,J2000))/36525.0;
    return zetaA(t,T);
  }
  private static double thetaA(double t) {
    return thetaAPoly.eval(t);
  }
  private static double thetaA(double t,double T) {
    Polynomial thisPoly=new Polynomial(new double[] {
      0,thetaA1Poly.eval(T),thetaA2Poly.eval(T),thetaAPoly.Coeffs[3]
    });
    return thisPoly.eval(t);
  }
  public static double thetaA(Time t) {
    return thetaA(t.get(TimeUnits.Centuries,TimeScale.TDT,TimeEpoch.J2000));
  }
  public static double thetaA(Time Ef, Time Ed) {
    double T=(Ef.get(Days,TDT,J2000)-E0.get(Days,TDT,J2000))/36525.0;
    double t=(Ed.get(Days,TDT,J2000)-Ef.get(Days,TDT,J2000))/36525.0;
    return thetaA(t,T);
  }
  private static double zA(double t) {
    return zAPoly.eval(t);
  }
  private static double zA(double t,double T) {
    Polynomial thisPoly=new Polynomial(new double[] {
      0,zA1Poly.eval(T),zA2Poly.eval(T),zAPoly.Coeffs[3]
    });
    return thisPoly.eval(t);
  }
  public static double zA(Time t) {
    return zA(t.get(TimeUnits.Centuries,TimeScale.TDT,TimeEpoch.J2000));
  }
  public static double zA(Time Ef, Time Ed) {
    double T=(Ef.get(Days,TDT,J2000)-E0.get(Days,TDT,J2000))/36525.0;
    double t=(Ed.get(Days,TDT,J2000)-Ef.get(Days,TDT,J2000))/36525.0;
    return zA(t,T);
  }
 
  /**
   * @param args
   */
  public static void main(String[] args) {
    EarthRotPrecessionIAU1976 P=new EarthRotPrecessionIAU1976(MOD,J2000Ecl);
    EarthRotNutationIAU1980 N=new EarthRotNutationIAU1980(TOD,MOD);
    System.out.println("Check precession against Explanatory Supplement p174");
    Time T1984=new Time(1984,1,1, 0,0,0,0,TimeUnits.Centuries,TimeScale.TDT,TimeEpoch.J2000);
    System.out.println(T1984.toString(TimeUnits.Days,TimeScale.TDT,TimeEpoch.JD));
    System.out.println(T1984.toString(TimeUnits.Centuries,TimeScale.TDT,TimeEpoch.J2000));
    Time T2000=new Time(0,Centuries,TDT,J2000);
    double zeta =zetaA( T1984,T2000);
    double z    =zA(    T1984,T2000);
    double theta=thetaA(T1984,T2000);
    System.out.println("zetaA (\"),  should be 368.9985: "+String.format("%9.4f",zeta));
    System.out.println("zA (\"),     should be 369.0118: "+String.format("%9.4f",z));
    System.out.println("thetaA (\"), should be 320.7279: "+String.format("%9.4f",theta));
    MathMatrix RP1984=P.CalcRotation(T1984,T2000).T();
    //Explanatory Supplement p174. Precession from J2000 to J1984. 
    //Calculated with precession angles rounded to 10^-4", so only good to 9 
    //places. Absurd accuracy is our obsession!
    MathMatrix RPTestOut1984=new MathMatrix(new double[][] { 
      {+0.999992390029,-0.003577999042,-0.001554929623},
      {+0.003577999042,+0.999993598937,-0.000002781855},
      {+0.001554929624,-0.000002781702,+0.999998791092}
    });
    System.out.println("Test matrix: ");
    System.out.println(RPTestOut1984.toString("%15.12f"));
    System.out.println("Calc matrix with rounded angles: ");
    System.out.println(P.CalcRotation174().T().toString("%15.12f"));
    System.out.println("Difference: ");
    System.out.println(MathMatrix.sub(P.CalcRotation174().T(), RPTestOut1984).toString("%15.12f"));
    System.out.println("Calc matrix: ");
    System.out.println(RP1984.toString("%15.12f"));
    System.out.println("Difference: ");
    System.out.println(MathMatrix.sub(RP1984, RPTestOut1984).toString("%15.12f"));
    System.out.println("Difference: ");
    System.out.println(new AxisAngle(MathMatrix.mul(RP1984.T(), RPTestOut1984)));

    System.out.println("Check precession IAU1976 eq13");
    double T=0;
    double t=-0.500002095577002;
    zeta =zetaA(t,T);
    z    =zA(   t,T);
    theta=thetaA(t,T);
    System.out.println("zetaA (\"),  should be 368.9985: "+String.format("%9.4f",zeta));
    System.out.println("zA (\"),     should be 369.0118: "+String.format("%9.4f",z));
    System.out.println("thetaA (\"), should be 320.7279: "+String.format("%9.4f",theta));
    MathMatrix RPB1950=P.CalcRotation(t,T);
    //PM Based on IAU1976 eq13. Precession from B1950 to J2000.
    //r_J2000=[M]r_B1950 or in other words, r_J2000=[M]r_MOD
    MathMatrix RPTestOutB1950=new MathMatrix(new double[][] { 
      {+0.9999257079523629,-0.0111789381377700,-0.0048590038153592},
      {+0.0111789381264276,+0.9999375133499888,-0.0000271625947142},
      {+0.0048590038414544,-0.0000271579262585,+0.9999881946023742}
    });
    
    System.out.println("Test matrix: ");
    System.out.println(RPTestOutB1950.toString("%19.16f"));
    System.out.println("Calc matrix: ");
    System.out.println(RPB1950.toString("%19.16f"));
    System.out.println("Difference: ");
    System.out.println(MathMatrix.sub(RPB1950, RPTestOutB1950).toString("%19.16f"));
    System.out.println("Difference: ");
    System.out.println(new AxisAngle(MathMatrix.mul(RPB1950.T(), RPTestOutB1950)));

    System.out.println("Check precession 1990 Almanac B18");
    /* Rotation matrix from J2000 to Mean of Date J1990.5 */
    MathMatrix RPTestOut19905=new MathMatrix(new double[][] {
      {+0.99999732,+0.00212430,+0.00092315},
      {-0.00212430,+0.99999774,-0.00000098},
      {-0.00092315,-0.00000098,+0.99999957}
    });
    Time T19905=new Time(1990,7,2,15,0,0,0,TimeUnits.Centuries,TimeScale.TDT,TimeEpoch.J2000);
    zeta =zetaA(T19905);
    z    =zA(T19905);
    theta=thetaA(T19905);
    MathMatrix RP19905=P.CalcRotation(T19905).T();
    System.out.println("zetaA (\"),  should be -219.09: "+String.format("%7.2f",zeta));
    System.out.println("zA (\"),     should be -219.08: "+String.format("%7.2f",z));
    System.out.println("thetaA (\"), should be -190.41: "+String.format("%7.2f",theta));
    System.out.println("Test matrix: ");
    System.out.println(RPTestOut19905.toString("%11.8f"));
    System.out.println("Calc matrix: ");
    System.out.println(RP19905.toString("%11.8f"));
    System.out.println("Difference: ");
    System.out.println(MathMatrix.sub(RP19905, RPTestOut19905).toString("%11.8f"));
    System.out.println("Difference: ");
    System.out.println(new AxisAngle(MathMatrix.mul(RP19905.T(), RPTestOut19905)));
   
  }

  @Override
  public MathMatrix CalcRotation(Time T) {
    return CalcRotation(T.get(TimeUnits.Centuries,TimeScale.TDT,TimeEpoch.J2000));
  }
  private MathMatrix CalcRotation(double T) {
    MathMatrix Mzeta= Rot3(sToRadians(zetaA(T)));
    MathMatrix Mtheta=Rot2(sToRadians(-thetaA(T)));
    MathMatrix Mz=    Rot3(sToRadians(zA(T)));
    return MathMatrix.mul(Mzeta, MathMatrix.mul(Mtheta, Mz));
  }
  private MathMatrix CalcRotation(double t, double T) {
    MathMatrix Mzeta= Rot3(sToRadians(zetaA(t,T)));
    MathMatrix Mtheta=Rot2(sToRadians(-thetaA(t,T)));
    MathMatrix Mz=    Rot3(sToRadians(zA(t,T)));
    return MathMatrix.mul(Mzeta, MathMatrix.mul(Mtheta, Mz));
  }
  private MathMatrix CalcRotation174() {
    MathMatrix Mzeta= Rot3(sToRadians( 368.9985));
    MathMatrix Mtheta=Rot2(sToRadians(-320.7279));
    MathMatrix Mz=    Rot3(sToRadians( 369.0188));
    return MathMatrix.mul(Mzeta, MathMatrix.mul(Mtheta, Mz));
  }
  public MathMatrix CalcRotation(Time Ef, Time Ed) {
    double T=(Ef.get(Days,TDT,J2000)-E0.get(Days,TDT,J2000))/36525.0;
    double t=(Ed.get(Days,TDT,J2000)-Ef.get(Days,TDT,J2000))/36525.0;
    return CalcRotation(t,T);
  }

}
