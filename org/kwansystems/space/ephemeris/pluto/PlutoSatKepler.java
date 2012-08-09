package org.kwansystems.space.ephemeris.pluto;

import static java.lang.Math.*;
import static org.kwansystems.space.Constants.*;

import java.io.*;
import org.kwansystems.space.ephemeris.*;
import org.kwansystems.space.kepler.*;
import org.kwansystems.tools.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeEpoch;
import org.kwansystems.tools.time.TimeScale;
import org.kwansystems.tools.time.TimeUnits;
import org.kwansystems.tools.vector.*;

public class PlutoSatKepler extends KeplerEphemeris {
  //Orbital motion for Charon
  //Derived from orbital elements and formulas given in
  //  http://adsabs.harvard.edu/cgi-bin/nph-bib_query?bibcode=1989A%26A...220..321S&amp;db_key=AST&amp;high=407e2606cf08426
  //  Position is returned in units of m, centered on the Pluto body center, J2000 equator
  
  //THIS IS NOT VERY ACCURATE!!! There is no consensus on the actual orbit elements. 
	//These elements are a best fit to 60 HST observations over the period 21 May 1992 through 18 Aug 1993
  private static final int A=0;
  private static final int E=1;
  private static final int I=2;
  private static final int LAN=3;
  private static final int LP=4;
  private static final int L=5;
  private static final int P=6;
  private static final double[] PlutoSatEle= {
    //               a m         e        I deg  LAN deg  LP deg   L deg   P day  
    /* Charon */    19636000,  0.0076,    96.163, 222.993, 219.1,  32.875, 6.387223
		};
  private MathMatrix ToJ2000Ecl() {
    return MathMatrix.Rot1(epsJ2000R);
  }
  
  public Elements CalcElements(Time T) {
    //Returns position in J2000Equ, km and km/s from Pluto COM
    double D=T.get(TimeUnits.Days,TimeScale.TDT,TimeEpoch.JD)-2449000.5;
    double N=360.0/PlutoSatEle[P];
    double M=toRadians(PlutoSatEle[L]+N*D-PlutoSatEle[LP]);
    double AP=toRadians(PlutoSatEle[LP]-PlutoSatEle[LAN]);
    Elements EE=new Elements();
    EE.verbose=false;
    EE.A=PlutoSatEle[A];
    EE.E=PlutoSatEle[E];
    EE.M=M;
    EE.N=toRadians(N)/86400.0; //Give it n in rad/s so that it can calc GM
    EE.I=toRadians(PlutoSatEle[I]);
    EE.AP=AP;
    EE.LAN=toRadians(PlutoSatEle[LAN]);
    EE.LengthUnit="m";
    EE.TimeUnit="sec";
    EE.FillInElements();
    return EE;
  }
  public MathState RotateState(Time T, MathState S) {
    return ToJ2000Ecl().transform(S);
  }
  public static void main(String[] args) throws IOException {
    PlutoSatKepler C=new PlutoSatKepler();
    C.printTheory(System.out, 1.0);
  }
  public static Ephemeris[] satArray=new Ephemeris[] {
    null,
    new PlutoSatKepler(),
  };
  public static double[] satMassRatio=new double[] {8.740511199155262E+02,7.315807873692954E+01};
  
}


