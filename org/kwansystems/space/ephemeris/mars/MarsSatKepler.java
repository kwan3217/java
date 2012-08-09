package org.kwansystems.space.ephemeris.mars;

import static java.lang.Math.*;
import static org.kwansystems.space.Constants.*;
import static org.kwansystems.tools.rotation.MathMatrix.*;
import static org.kwansystems.tools.time.Time.*;
import static org.kwansystems.tools.time.TimeScale.*;
import static org.kwansystems.tools.time.TimeUnits.*;

import org.kwansystems.space.ephemeris.*;
import org.kwansystems.space.kepler.*;
import org.kwansystems.tools.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeEpoch;
import org.kwansystems.tools.time.TimeScale;
import org.kwansystems.tools.time.TimeUnits;
import org.kwansystems.tools.vector.*;


public class MarsSatKepler extends KeplerEphemeris {
  //Orbital motion for Phobos and Deimos
  //Derived from orbital elements and formulas given in
  //  http://adsabs.harvard.edu/cgi-bin/nph-bib_query?bibcode=1989A%26A...220..321S&amp;db_key=AST&amp;high=407e2606cf08426
  //  Position is returned in units of m, centered on the mars barycenter, B1950 equator
  
  //THIS IS NOT VERY ACCURATE!!! It could be dozens of km off, and when considering distances of
  //only tens of thousands of km, that is significant. This only strives to be good for the long
  //term. Having said that, its applicability range is unknown.
  private int Body;
  //Body is 0 for Phobos, 1 for Deimos
  public MarsSatKepler(int LBody) {
    Body=LBody;
  }
  private static final int A=0;
  private static final int E=1;
  private static final int I=2;
  private static final int L=3;
  private static final int P=4;
  private static final int K=5;
  private static final int DL=6;  //=n
  private static final int DP=7;
  private static final int DK=8;
  private static final int C=9;
  private static final int Hamp=10;
  private static final int Hpha=11;
  private static final int N=12;
  private static final int J=13;
  private static final int DN=14;
  private static final int DJ=15;
  private static final double[][] MarsSatEle={
    //               a m      e        I deg   L deg    P deg   K deg   dL deg/day    dP deg/day   dK deg/day   c deg/yr^2  Hamp deg  Hpha deg N deg    J deg    dN deg/yr   dJ deg/yr
    /* Phobos */ {  9379400, 0.014979, 1.1029, 232.412, 278.96, 327.90, 1128.8445566,    0.435258,   -0.435330,  0.0012370,  0.000,     0.00,   47.386,   37.271, -0.00140,    0.00080 },
    /* Deimos */ { 23461130, 0.000391, 1.7901,  28.963, 111.70, 240.38,  285.1618875,    0.017985,   -0.018008, -0.0000028, -0.247,    43.83,   46.367,   36.623, -0.00138,    0.00079 }
  };
  private static double Rev(double X) {
    double Y=X-floor(X/360.0)*360.0;
    if(Y<0) {
      Y=Y+360;
    }
    return Y;
  }
  private Rotator ToJ2000Equ() {
    return B1950toJ2000;
  }
  private Rotator ToJ2000Ecl() {
    return ToJ2000Equ().combine(MathMatrix.Rot1(epsJ2000R));
  }
  
  public Elements CalcElements(Time T) {
    //Returns position in B1950Equ, km and km/s from mars COM
    double D=T.get(Days,TDB)-2441266.5;
    double Y=D/365.25;
    double ThisK=MarsSatEle[Body][K]+MarsSatEle[Body][DK]*D;
    double ThisP=MarsSatEle[Body][P]+MarsSatEle[Body][DP]*D;
    double ThisN=MarsSatEle[Body][N]+MarsSatEle[Body][DN]*Y;
    double ThisL=Rev(MarsSatEle[Body][L]+MarsSatEle[Body][DL]*D);
    ThisL=ThisL+MarsSatEle[Body][C]*Y*Y+  //Death spiral acceleration
    MarsSatEle[Body][Hamp]*sin(toRadians(ThisK+MarsSatEle[Body][Hpha]));
    double M=toRadians(ThisL-ThisP);
    double AP=toRadians(ThisP-ThisN-ThisK);
    Elements EE=new Elements();
    EE.A=MarsSatEle[Body][A];
    EE.E=MarsSatEle[Body][E];
    EE.M=M;
    EE.N=toRadians(MarsSatEle[Body][DL])/86400.0;
    EE.I=toRadians(MarsSatEle[Body][I]);
    EE.AP=AP;
    EE.LAN=toRadians(ThisK);
    EE.LengthUnit="m";
    EE.TimeUnit="sec";
    EE.FillInElements();
    return EE;
  }
  public MathState RotateState(Time T, MathState S) {
    double D=T.get(Days,TDB)-2441266.5;
    double Y=D/365.25;
    double ThisJ=MarsSatEle[Body][J]+MarsSatEle[Body][DJ]*Y;
    double ThisN=MarsSatEle[Body][N]+MarsSatEle[Body][DN]*Y;
	    
    S=Rot1d(-ThisJ).transform(S);
    S=Rot3d(-ThisN).transform(S);
    S=ToJ2000Ecl().transform(S);
    return S;
  }
  /*Horizons J2000Ecl
   Phobos:
  2450000.000000000 = A.D. 1995-Oct-09 12:00:00.0000 (CT)
 X = 7.231532944257718E+03 Y = 4.832164014106484E+03 Z =-3.453136780605129E+03
 VX=-9.824093719008740E-01 VY= 1.816790963520376E+00 VZ= 5.724669531221862E-01
   Deimos:
  2450000.000000000 = A.D. 1995-Oct-09 12:00:00.0000 (CT)
 X = 2.079702978629133E+04 Y = 2.487115288070685E+03 Z =-1.055673261169998E+04
 VX=-1.052132328544293E-01 VY= 1.343035404978747E+00 VZ= 1.086111325511347E-01
   */
  public static void main(String[] args) {
    MarsSatKepler P=new MarsSatKepler(0);
    MarsSatKepler D=new MarsSatKepler(1);
    System.out.println("P:     "+P.CalcState(new Time(2450000.0,TimeUnits.Days,TimeScale.TDB,TimeEpoch.JD)));
    System.out.println(D.CalcState(new Time(2450000.0,TimeUnits.Days,TimeScale.TDB,TimeEpoch.JD)));
  }
  public static Ephemeris[] satArray=new Ephemeris[] {
    null,
    new MarsSatKepler(0),
    new MarsSatKepler(1)
  };
  public static double[] satMassRatio=new double[] {4.282831081E+04,6.9E-04,1.2E-04};
  
}


