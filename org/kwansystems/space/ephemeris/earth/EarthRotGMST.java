package org.kwansystems.space.ephemeris.earth;

import org.kwansystems.space.ephemeris.*;
import org.kwansystems.tools.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeEpoch;
import org.kwansystems.tools.time.TimeScale;
import org.kwansystems.tools.time.TimeUnits;

import static java.lang.Math.*;
import static org.kwansystems.space.ephemeris.RotatorEphemeris.Frame.*;

public class EarthRotGMST extends RotatorEphemeris {
  public EarthRotGMST(Frame Lfrom, Frame Lto) {
    super(Lfrom,Lto);
    naturalFrom=PEF;
    naturalTo=TEME;
    setInv(Lfrom,Lto);
  }
  private static final Polynomial ThetaGMSTPoly=new Polynomial(new double[]{
    Scalar.tsToRadians(67310.54841),Scalar.tsToRadians(+3164400184.812866),Scalar.tsToRadians(+0.093104),Scalar.tsToRadians(-6.2e-6)
  },Polynomial.order.ConstFirst);
  public static void main(String[] args) {
    System.out.println(toDegrees(ThetaGMST(new Time(1992,8,20,12,14,00,00,TimeUnits.Centuries,TimeScale.UTC,TimeEpoch.J2000))));
    System.out.println(toDegrees(ThetaGMST(new Time(1992,8,20,12,14,00,1000,TimeUnits.Centuries,TimeScale.UTC,TimeEpoch.J2000))));
  }
  private static double ThetaGMST(double Tu) {
    double result=Scalar.mlmod(ThetaGMSTPoly.eval(Tu),2*PI);
    return result;
  }
  public static double ThetaGMST(Time T) {
    return ThetaGMST(T.get(TimeUnits.Centuries,TimeScale.UTC,TimeEpoch.J2000));
  }
  @Override
  public Rotator CalcRotation(Time T) {
    return MathMatrix.Rot3(-ThetaGMST(T));
  }

}
