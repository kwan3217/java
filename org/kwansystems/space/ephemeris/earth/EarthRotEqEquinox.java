package org.kwansystems.space.ephemeris.earth;

import org.kwansystems.space.ephemeris.*;
import org.kwansystems.tools.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.time.*;

import static java.lang.Math.*;
import static org.kwansystems.space.ephemeris.earth.EarthRotNutationIAU1980.*;
import static org.kwansystems.space.ephemeris.RotatorEphemeris.Frame.*;

public class EarthRotEqEquinox extends RotatorEphemeris{
  public EarthRotEqEquinox(Frame Lfrom, Frame Lto) {
    super(Lfrom,Lto);
    naturalFrom=TEME;
    naturalTo=TOD;
    setInv(Lfrom,Lto);
  }
  public static void main(String[] args) {

  }
  public static final Time TdeltaEqEquinox=new Time(1997,1,1,0,0,0,TimeUnits.Centuries,TimeScale.TDB,TimeEpoch.J2000);
  public static double deltaEqEquinox(Time T) {
    if (T.compareTo(TdeltaEqEquinox)<0) return 0; //No deltaEqEquinox before 1997 Jan 1
    double O=Omega(T);
    return Scalar.sToRadians(2.64e-3)*sin(O)-Scalar.sToRadians(9e-6)*sin(2*O);
  }
  public static double EqEquinox(Time T) {
    return deltaEqEquinox(T)+deltaPsi(T)*cos(epsilonA(T)+deltaEpsilon(T));
  }
  @Override
  public Rotator CalcRotation(Time T) {
    return MathMatrix.Rot3(-EqEquinox(T));
  }

}
