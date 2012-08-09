package org.kwansystems.space.ephemeris.earth;

import org.kwansystems.space.ephemeris.Ephemeris;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeEpoch;
import org.kwansystems.tools.time.TimeScale;
import org.kwansystems.tools.time.TimeUnits;
import org.kwansystems.tools.vector.*;

import static org.kwansystems.tools.time.Time.*;
import static org.kwansystems.tools.time.TimeUnits.*;

public class EarthSatSGP4 extends Ephemeris {
  
  public static void main(String[] args) {
    EarthSatSGP4 TestCase1=new EarthSatSGP4(
      "1 88888U          80275.98708465  .00073094  13844-3  66816-4 0     8",
      "2 88888  72.8435 115.9689 0086731  52.6988 110.5714 16.05824518   105"        
    );
    System.out.println(TestCase1);
    Time Epoch=TestCase1.getEpoch();
    System.out.println(Epoch);
    for(int i=0;i<5;i++) {
      Time TestTime=Time.add(Epoch,i*360*60,Seconds);
      System.out.println(TestCase1.CalcStateTime(TestTime));
    }
  }
  
  public static double getLatFromECI(MathVector pos) {
    double r=Math.sqrt(pos.X()*pos.X()+pos.Y()*pos.Y());
    double e2 = f*(2 - f);
    double lat = Math.atan2(pos.Z(),r);
    double phi,c;
    do {
      phi = lat;
      c = 1/Math.sqrt(1 - e2*Math.pow(Math.sin(phi),2));
      lat = Math.atan2(pos.Z() + xmper*c*e2*Math.sin(phi),r);
    } while(Math.abs(lat - phi) >= 1E-10);
    return lat;
  }
  
  public static double getLonFromECI(MathVector pos,double JulianDate) {
    double theta=Math.atan2(pos.Y(),pos.X());
    return Modulus(theta - ThetaG_JD(JulianDate),2*Math.PI);
  }
  
  public static double getLonFromECI(MathVector pos,Time T) {
    return getLonFromECI(pos,T.get(TimeUnits.Days, TimeScale.UTC));
  }
  
  //Lat in radians, +=N,-=S
  //Lon in radians, +=E,-=W
  //Alt in km above ellipsoid
  public static MathVector GetECIfromLatLonAltDate(double Lat, double Lon, double Alt,double JulianDate) {
    double e2 = f*(2 - f);
    double N=xmper/Math.sqrt(1-e2*Math.pow(Math.sin(Lat),2));
    Lon+=ThetaG_JD(JulianDate);
    return new MathVector((N+Alt)*Math.cos(Lat)*Math.cos(Lon),
                          (N*(1-e2)+Alt)*Math.sin(Lat),
                          (N+Alt)*Math.cos(Lat)*Math.sin(Lon));
  }
  
  public static double getAltFromECI(MathVector pos) {
    double r=Math.sqrt(pos.X()*pos.X()+pos.Y()*pos.Y());
    double e2 = f*(2 - f);
    double lat = Math.atan2(pos.Z(),r);
    double phi,c;
    do {
      phi = lat;
      c = 1/Math.sqrt(1 - e2*Math.pow(Math.sin(phi),2));
      lat = Math.atan2(pos.Z() + xmper*c*e2*Math.sin(phi),r);
    } while(Math.abs(lat - phi) >= 1E-10);
    double alt=r/Math.cos(lat) - xmper*c;
    return alt;
  }
  
  public static MathState toMSec(MathState s) {
    //Convert ER to m and ER/min to m/s
    return new MathState(s.R().mul(xmper),s.V().mul(xmper/60.0));
  }
  
  public String toString() {
    return getLine1()+"\n"+
    getLine2()+"\n"+
    "Catalog Number:                    "+getCatalogNumber()+"\n"+
    "Epoch:                             "+getEpoch()+"\n"+
    "Bstar drag term:                   "+bstar+"\n"+
    "Revolutions per day:               "+getRevsPerDay()+"\n"+
    "Eccentricity:                      "+getEccentricity()+"\n"+
    "Inclination:                       "+getInclination()+"\n"+
    "Right ascension of Ascending Node: "+getNode()+"\n"+
    "Argument of Perigee:               "+getArgPeri()+"\n"+
    "Mean Anomaly:                      "+getMeanAnom();
  }
  
  public String getLine1() {return Line1;}
  public String getLine2() {return Line2;}
  public Time getEpoch() {return new Time(julian_epoch,TimeUnits.Days,TimeScale.UTC,TimeEpoch.JD);}
  public String getCatalogNumber() {return catnr;}
  public double getEccentricity() {return eo;}
  public double getInclination() {return Math.toDegrees(xincl);}
  public double getNode() {return Math.toDegrees(xnodeo);}
  public double getArgPeri() {return Math.toDegrees(omegao);}
  public double getMeanAnom() {return Math.toDegrees(xmo);}
  public double getRevsPerDay() {return xno/2/Math.PI*xmnpda;}
  
  public EarthSatSGP4(String Line1,String Line2) {
    this.Line1=Line1;
    this.Line2=Line2;
    ConvertSateliteData(Line1,Line2);
  }
  
  //Geocentric inertial coordinates, input is JD time (UTC), output units are km and seconds
  public MathState propagate(double JDNow) {
    double dayssince=(JDNow-julian_epoch);
    double tsince=dayssince*xmnpda;
    MathState S=propagateFromEpoch(tsince);
    S=toMSec(S);
    return S;
  }
  
  public MathState propagateFromEpoch(double tsince) {
    if (ideep) {
      return SDP4(tsince);
    } else {
      return SGP4(tsince);
    }
  }
  
  public MathState CalcState(Time timeNow) {
    return propagate(timeNow.get(TimeUnits.Days, TimeScale.UTC, TimeEpoch.JD));
  }
  
  //Private data and methods below
  private double epoch;
  public String catnr,elset;
  private String Line1,Line2;
  
  //from SGP_intf.pas
  private static final double ae = 1;
  private static final double tothrd   = 2.0/3.0;
  private static final double xmper   = 6378135;         //Earth equatorial radius - kilometers (WGS '72)
  private static final double f        = 1.0/298.26;      //Earth flattening (WGS '72)
  private static final double ge       = 398600.8e9;      //Earth gravitational constant (WGS '72)
  private static final double J2       = 1.0826158E-3;    //J2 harmonic (WGS '72)
  private static final double J3       = -2.53881E-6;     //J3 harmonic (WGS '72)
  private static final double J4       = -1.65597E-6;     //J4 harmonic (WGS '72)
  private static final double ck2      = J2/2.0;
  private static final double ck4      = -3.0*J4/8.0;
  private static final double xj3      = J3;
  private static final double qo       = ae + 120.0/xmper;
  private static final double s        = ae + 78.0/xmper;
  private static final double e6a      = 1E-6;
  private static final int dpinit   = 1;               //Deep-space initialization code
  private static final int dpsec    = 2;               //Deep-space secular code
  private static final int dpper    = 3;               //Deep-space periodic code
  private static final double xmnpda   =  1440.0;        //Minutes per day
  private static final double secday   = 86400.0;        //Seconds per day
  private static final double omega_E  = 1.00273790934;  //Earth rotations per sidereal day (non-constant)
  
  private boolean ideep=false;
  private double xmo,xnodeo,omegao,eo,xincl,
  xno,xndt2o,xndd6o,bstar,
  julian_epoch, xke=Math.sqrt(3600*ge/Math.pow(xmper,3));
  private boolean iflag=false; //Initialization necessary?
  
  private static void Convert_Blanks(StringBuffer S) {
    for(int i=0;i<S.length();i++) {
      if (S.charAt(i)==' ') {
        S.setCharAt(i,'0');
      } else {
        break;
      }
    }
  }
  
  private static double Real_Value(String buffer,int start, int length) {
    StringBuffer buffer2 = new StringBuffer(buffer.substring(start-1,start+length-1));
    Convert_Blanks(buffer2);
    if (buffer2.length()==0) buffer2=new StringBuffer("0");
    return Double.parseDouble(buffer2.toString());
  } //Function Real_Value
  
  private static int Integer_Value(String buffer,int start, int length) {
    StringBuffer buffer2 = new StringBuffer(buffer.substring(start-1,start+length-1));
    Convert_Blanks(buffer2);
    if (buffer2.length()==0) buffer2=new StringBuffer("0");
    return Integer.parseInt(buffer2.toString());
  } //Function Integer_Value
  
  private static double Julian_Date_of_Epoch(double epoch) {
    int year;
    double day;
    // Modification to support Y2K
    // Valid 1957 through 2056
    year = (int)(Math.floor(epoch*1E-3));
    if (year < 57) {
      year = year + 2000;
    } else {
      year = year + 1900;
    }
    // End modification
    day  = (epoch*1E-3-Math.floor(epoch*1E-3))*1E3;
    return Julian_Date_of_Year(year) + day;
  } //Function Julian_Date_of_Epoch
  
  private static double Julian_Date_of_Year(double year) {
    // Astronomical Formulae for Calculators, Jean Meeus, pages 23-25
    // Calculate Julian Date of 0.0 Jan year
    long A,B;
    year = year - 1;
    A = (int)Math.floor(year/100);
    B = 2 - A + (int)Math.floor(A/4);
    return Math.floor(365.25 * year) + Math.floor(30.6001 * 14) + 1720994.5 + B;
  } //Function Julian_Date_of_Year
  
  private void ConvertSateliteData(String Line1,String Line2) {
    int iexp,ibexp;
    double a1,ao,del1,delo,xnodp,temp;
    //  Decode Card 1
    catnr    = Line1.substring(2,7);
    epoch    = Real_Value(Line1,19,14);
    julian_epoch = Julian_Date_of_Epoch(epoch);
    xndt2o   = Real_Value(Line1,34,10);
    xndd6o   = Real_Value(Line1,45,6)*1E-5;
    iexp     = Integer_Value(Line1,51,2);
    bstar    = Real_Value(Line1,54,6)*1E-5;
    ibexp    = Integer_Value(Line1,60,2);
    StringBuffer sbelset=new StringBuffer(Line1.substring(65,68));
    Convert_Blanks(sbelset);
    elset    = sbelset.toString();
    
    // Decode Card 2
    xincl    = Real_Value(Line2,9,8);        //Inclination
    xnodeo   = Real_Value(Line2,18,8);       //Right ascension of Ascending node
    eo       = Real_Value(Line2,27,7)*1E-7;  //Eccentricity
    omegao   = Real_Value(Line2,35,8);       //Argument of Perigee
    xmo      = Real_Value(Line2,44,8);       //Mean Anomaly
    xno      = Real_Value(Line2,53,11);      //Mean Motion revs/day
    // period   = 1/xno;
    // Convert to proper units
    xndd6o   = xndd6o*Math.pow(10.0,iexp);
    bstar    = bstar*Math.pow(10.0,ibexp)/ae;
    xnodeo   = Math.toRadians(xnodeo);
    omegao   = Math.toRadians(omegao);
    xmo      = Math.toRadians(xmo);
    xincl    = Math.toRadians(xincl);
    xno      = xno*2*Math.PI/xmnpda;
    xndt2o   = xndt2o*2*Math.PI/Math.pow(xmnpda,2);
    xndd6o   = xndd6o*2*Math.PI/Math.pow(xmnpda,3);
    // Determine whether Deep-Space Model is needed
    a1 = Math.pow(xke/xno,tothrd);
    temp = 1.5*ck2*(3*Math.pow(Math.cos(xincl),2)-1)/Math.pow(1 - eo*eo,1.5);
    del1 = temp/(a1*a1);
    ao = a1*(1 - del1*(0.5*tothrd + del1*(1 + 134/81*del1)));
    delo = temp/(ao*ao);
    xnodp = xno/(1 + delo);
    ideep=(2*Math.PI/xnodp >= 225.0);
    iflag = false;
  }  //Procedure Convert_Satellite_Data
  
  //dpinit
  private double eqsq,siniq,cosiq,rteqsq,ao,cosq2,sinomo,cosomo,
  bsq,xlldot,omgdt,xnodot,xnodp;
  //dpsec/dpper
  private double xll,omega_sm,xnodes,_em,xinc,t,
  qoms2t=Math.pow(qo-s,4);
  
  private static double Modulus(double arg1,double arg2) {
    double modu;
    modu = arg1 - Math.floor(arg1/arg2) * arg2;
    if(modu >= 0) {
      return modu;
    } else {
      return modu + arg2;
    }
  } //Function Modulus
  
  private static double Fmod2p(double arg) {
    return Modulus(arg,2*Math.PI);
  } //Function Fmod2p
  
  //SGP4 static method  variables
  private double
  a1     = 0,  a3ovk2 = 0,
  aodp   = 0,  aycof  = 0,  betao  = 0,
  betao2 = 0,  c1     = 0,  c1sq   = 0,
  c2     = 0,  c3     = 0,  c4     = 0,
  c5     = 0,  coef   = 0,  coef1  = 0,
  cosio  = 0,  d2     = 0,  d3     = 0,
  d4     = 0,  del1   = 0,  delmo  = 0,
  delo   = 0,  eeta   = 0,  eosq   = 0,
  eta    = 0,  etasq  = 0,
  omgcof = 0,  omgdot = 0,  perige = 0,
  pinvsq = 0,  psisq  = 0,  qoms24 = 0,
  s4     = 0,  sinio  = 0,  sinmo  = 0,
  t2cof  = 0,  t3cof  = 0,  t4cof  = 0,
  t5cof  = 0,  temp   = 0,  temp1  = 0,
  temp2  = 0,  temp3  = 0,  theta2 = 0,
  theta4 = 0,  tsi    = 0,  x1m5th = 0,
  x1mth2 = 0,  x3thm1 = 0,  x7thm1 = 0,
  xhdot1 = 0,  xlcof  = 0,  xmcof  = 0,
  xmdot  = 0,  xnodcf = 0;
  private boolean isimp = false;
  
  //gp4Sdp4.pas
  private MathState SGP4(double tsince) {
    //Say it with me now folks, I HATE FORTRAN!
    //I can tell this is a translation from fortran, through all of the pascal.
    //  label
    //    9,10,90,100,110,130,140;
    
    int i;
    double cosuk,sinuk,rfdotk,vx,vy,vz,ux,uy,uz,xmy,xmx,
    cosnok,sinnok,cosik,sinik,rdotk,xinck,xnodek,uk,rk,
    cos2u,sin2u,u,sinu,cosu,betal,rfdot,rdot,r,pl,elsq,
    esine,ecose,epw,temp6,temp5,temp4,cosepw,sinepw,
    capu,ayn,xlt,aynl,xll,axn,xn,beta,xl,e,a,tfour,
    tcube,delm,delomg,templ,tempe,tempa,xnode,tsq,xmp,
    omega,xnoddf,omgadf,xmdf,x,y,z,xdot,ydot,zdot;
    // Recover original mean motion (xnodp) and semimajor axis (aodp)
    // from input elements. }
    if (!iflag) {
      a1 = Math.pow(xke/xno,tothrd);
      cosio = Math.cos(xincl);
      theta2 = cosio*cosio;
      x3thm1 = 3*theta2 - 1;
      eosq = eo*eo;
      betao2 = 1 - eosq;
      betao = Math.sqrt(betao2);
      del1 = 1.5*ck2*x3thm1/(a1*a1*betao*betao2);
      ao = a1*(1 - del1*(0.5*tothrd + del1*(1 + 134/81*del1)));
      delo = 1.5*ck2*x3thm1/(ao*ao*betao*betao2);
      xnodp = xno/(1 + delo);
      aodp = ao/(1 - delo);
      // Initialization
      // For perigee less than 220 kilometers, the isimp flag is set and
      //the equations are truncated to linear variation in sqrt a and
      //quadratic variation in mean anomaly.  Also, the c3 term, the
      //delta omega term, and the delta m term are dropped.
      isimp= ( (aodp*(1 - eo)/ae) < (220/xmper + ae) );
      // For perigee below 156 km, the values of s and qoms2t are altered.
      s4 = s;
      qoms24 = qoms2t;
      perige = (aodp*(1 - eo) - ae)*xmper;
      if (perige < 156) {
        s4 = perige - 78;
        if (perige <= 98) {
          s4 = 20;
        }
        qoms24 = Math.pow((120 - s4)*ae/xmper,4);
        s4 = s4/xmper + ae;
      }
      pinvsq = 1/(aodp*aodp*betao2*betao2);
      tsi = 1/(aodp - s4);
      eta = aodp*eo*tsi;
      etasq = eta*eta;
      eeta = eo*eta;
      psisq = Math.abs(1 - etasq);
      coef = qoms24*Math.pow(tsi,4);
      coef1 = coef/Math.pow(psisq,3.5);
      c2 = coef1*xnodp*(aodp*(1 + 1.5*etasq + eeta*(4 + etasq))
      + 0.75*ck2*tsi/psisq*x3thm1*(8 + 3*etasq*(8 + etasq)));
      c1 = bstar*c2;
      sinio = Math.sin(xincl);
      a3ovk2 = -xj3/ck2*Math.pow(ae,3);
      c3 = coef*tsi*a3ovk2*xnodp*ae*sinio/eo;
      x1mth2 = 1 - theta2;
      c4 = 2*xnodp*coef1*aodp*betao2*(eta*(2 + 0.5*etasq)
      + eo*(0.5 + 2*etasq) - 2*ck2*tsi/(aodp*psisq)
      *(-3*x3thm1*(1 - 2*eeta + etasq*(1.5 - 0.5*eeta))
      + 0.75*x1mth2*(2*etasq - eeta*(1 + etasq))*Math.cos(2*omegao)));
      c5 = 2*coef1*aodp*betao2*(1 + 2.75*(etasq + eeta) + eeta*etasq);
      theta4 = theta2*theta2;
      temp1 = 3*ck2*pinvsq*xnodp;
      temp2 = temp1*ck2*pinvsq;
      temp3 = 1.25*ck4*pinvsq*pinvsq*xnodp;
      xmdot = xnodp + 0.5*temp1*betao*x3thm1
      + 0.0625*temp2*betao*(13 - 78*theta2 + 137*theta4);
      x1m5th = 1 - 5*theta2;
      omgdot = -0.5*temp1*x1m5th + 0.0625*temp2*(7 - 114*theta2 +395*theta4)
      + temp3*(3 - 36*theta2 + 49*theta4);
      xhdot1 = -temp1*cosio;
      xnodot = xhdot1 + (0.5*temp2*(4 - 19*theta2)
      + 2*temp3*(3 - 7*theta2))*cosio;
      omgcof = bstar*c3*Math.cos(omegao);
      xmcof = -tothrd*coef*bstar*ae/eeta;
      xnodcf = 3.5*betao2*xhdot1*c1;
      t2cof = 1.5*c1;
      xlcof = 0.125*a3ovk2*sinio*(3 + 5*cosio)/(1 + cosio);
      aycof = 0.25*a3ovk2*sinio;
      delmo = Math.pow(1 + eta*Math.cos(xmo),3);
      sinmo = Math.sin(xmo);
      x7thm1 = 7*theta2 - 1;
      if (!isimp) {
        c1sq = c1*c1;
        d2 = 4*aodp*tsi*c1sq;
        temp = d2*tsi*c1/3;
        d3 = (17*aodp + s4)*temp;
        d4 = 0.5*temp*aodp*tsi*(221*aodp + 31*s4)*c1;
        t3cof = d2 + 2*c1sq;
        t4cof = 0.25*(3*d3 + c1*(12*d2 + 10*c1sq));
        t5cof = 0.2*(3*d4 + 12*c1*d3 + 6*d2*d2 + 15*c1sq*(2*d2 + c1sq));
      }
      iflag = true;
    }
    // Update for secular gravity and atmospheric drag.
    xmdf = xmo + xmdot*tsince;
    omgadf = omegao + omgdot*tsince;
    xnoddf = xnodeo + xnodot*tsince;
    omega = omgadf;
    xmp = xmdf;
    tsq = tsince*tsince;
    xnode = xnoddf + xnodcf*tsq;
    tempa = 1 - c1*tsince;
    tempe = bstar*c4*tsince;
    templ = t2cof*tsq;
    if (!isimp) {
      delomg = omgcof*tsince;
      delm = xmcof*(Math.pow(1 + eta*Math.cos(xmdf),3) - delmo);
      temp = delomg + delm;
      xmp = xmdf + temp;
      omega = omgadf - temp;
      tcube = tsq*tsince;
      tfour = tsince*tcube;
      tempa = tempa - d2*tsq - d3*tcube - d4*tfour;
      tempe = tempe + bstar*c5*(Math.sin(xmp) - sinmo);
      templ = templ + t3cof*tcube + tfour*(t4cof + tsince*t5cof);
    }
    a = aodp*tempa*tempa;
    e = eo - tempe;
    xl = xmp + omega + xnode + xnodp*templ;
    beta = Math.sqrt(1 - e*e);
    xn = xke/Math.pow(a,1.5);
    // Long period periodics
    axn = e*Math.cos(omega);
    temp = 1/(a*beta*beta);
    xll = temp*xlcof*axn;
    aynl = temp*aycof;
    xlt = xl + xll;
    ayn = e*Math.sin(omega) + aynl;
    // Solve Kepler's Equation
    capu = Fmod2p(xlt - xnode);
    temp2 = capu;
    i=1;
    do {
      sinepw = Math.sin(temp2);
      cosepw = Math.cos(temp2);
      temp3 = axn*sinepw;
      temp4 = ayn*cosepw;
      temp5 = axn*cosepw;
      temp6 = ayn*sinepw;
      epw = (capu - temp4 + temp3 - temp2)/(1 - temp5 - temp6) + temp2;
      if (Math.abs(epw - temp2) <= e6a) break;
      temp2 = epw;
      i++;
    } while(i<=10); //for i
    // Short period preliminary quantities
    ecose = temp5 + temp6;
    esine = temp3 - temp4;
    elsq = axn*axn + ayn*ayn;
    temp = 1 - elsq;
    pl = a*temp;
    r = a*(1 - ecose);
    temp1 = 1/r;
    rdot = xke*Math.sqrt(a)*esine*temp1;
    rfdot = xke*Math.sqrt(pl)*temp1;
    temp2 = a*temp1;
    betal = Math.sqrt(temp);
    temp3 = 1/(1 + betal);
    cosu = temp2*(cosepw - axn + ayn*esine*temp3);
    sinu = temp2*(sinepw - ayn - axn*esine*temp3);
    u = Math.atan2(sinu,cosu);
    sin2u = 2*sinu*cosu;
    cos2u = 2*cosu*cosu - 1;
    temp = 1/pl;
    temp1 = ck2*temp;
    temp2 = temp1*temp;
    // Update for short periodics }
    rk = r*(1 - 1.5*temp2*betal*x3thm1) + 0.5*temp1*x1mth2*cos2u;
    uk = u - 0.25*temp2*x7thm1*sin2u;
    xnodek = xnode + 1.5*temp2*cosio*sin2u;
    xinck = xincl + 1.5*temp2*cosio*sinio*cos2u;
    rdotk = rdot - xn*temp1*x1mth2*sin2u;
    rfdotk = rfdot + xn*temp1*(x1mth2*cos2u + 1.5*x3thm1);
    // Orientation vectors }
    sinuk = Math.sin(uk);
    cosuk = Math.cos(uk);
    sinik = Math.sin(xinck);
    cosik = Math.cos(xinck);
    sinnok = Math.sin(xnodek);
    cosnok = Math.cos(xnodek);
    xmx = -sinnok*cosik;
    xmy = cosnok*cosik;
    ux = xmx*sinuk + cosnok*cosuk;
    uy = xmy*sinuk + sinnok*cosuk;
    uz = sinik*sinuk;
    vx = xmx*cosuk - cosnok*sinuk;
    vy = xmy*cosuk - sinnok*sinuk;
    vz = sinik*cosuk;
    // Position and velocity
    x = rk*ux;
    y = rk*uy;
    z = rk*uz;
    xdot = rdotk*ux + rfdotk*vx;
    ydot = rdotk*uy + rfdotk*vy;
    zdot = rdotk*uz + rfdotk*vz;
    MathState Answer=new MathState(x,y,z,xdot,ydot,zdot);
    return Answer;
    //Procedure SGP4
  }
  
  //Constants for method Deep
  private static final double
  zns    =  1.19459E-5,     c1ss   =  2.9864797E-6,   zes    =  0.01675,
  znl    =  1.5835218E-4,   c1l    =  4.7968065E-7,   zel    =  0.05490,
  zcosis =  0.91744867,     zsinis =  0.39785416,     zsings = -0.98088458,
  zcosgs =  0.1945905,      
  q22    =  1.7891679E-6,   q31    =  2.1460748E-6,   q33    =  2.2123015E-7,
  g22    =  5.7686396,      g32    =  0.95240898,     g44    =  1.8014998,
  g52    =  1.0508330,      g54    =  4.4108898,      root22 =  1.7891679E-6,
  root32 =  3.7393792E-7,   root44 =  7.3636953E-9,   root52 =  1.1428639E-7,
  root54 =  2.1765803E-9,   thdt   =  4.3752691E-3;
  //Static method variables for Deep
  private int
  iresfl  = 0,  isynfl  = 0,
  iret    = 0,  iretn   = 0;
  private double
  a2      = 0,    a3      = 0,
  a4      = 0,    a5      = 0,    a6      = 0,
  a7      = 0,    a8      = 0,    a9      = 0,
  a10     = 0,    ainv2   = 0,    alfdp   = 0,
  aqnv    = 0,    atime   = 0,    betdp   = 0,
  bfact   = 0,    c       = 0,    cc      = 0,
  cosis   = 0,    cosok   = 0,    cosq    = 0,
  ctem    = 0,    d2201   = 0,    d2211   = 0,
  d3210   = 0,    d3222   = 0,    d4410   = 0,
  d4422   = 0,    d5220   = 0,    d5232   = 0,
  d5421   = 0,    d5433   = 0,    dalf    = 0,
  day     = 0,    dbet    = 0,
  del2    = 0,    del3    = 0,    delt    = 0,
  dls     = 0,    e3      = 0,    ee2     = 0,
  eoc     = 0,    eq      = 0,    f2      = 0,
  f220    = 0,    f221    = 0,    f3      = 0,
  f311    = 0,    f321    = 0,    f322    = 0,
  f330    = 0,    f441    = 0,    f442    = 0,
  f522    = 0,    f523    = 0,    f542    = 0,
  f543    = 0,    fasx2   = 0,    fasx4   = 0,
  fasx6   = 0,    ft      = 0,    g200    = 0,
  g201    = 0,    g211    = 0,    g300    = 0,
  g310    = 0,    g322    = 0,    g410    = 0,
  g422    = 0,    g520    = 0,    g521    = 0,
  g532    = 0,    g533    = 0,    gam     = 0,
  omegaq  = 0,    pe      = 0,    pgh     = 0,
  ph      = 0,    pinc    = 0,    pl      = 0,
  preep   = 0,    s1      = 0,    s2      = 0,
  s3      = 0,    s5      = 0,
  s6      = 0,    s7      = 0,    savtsn  = 0,
  se      = 0,    se2     = 0,    se3     = 0,
  sel     = 0,    ses     = 0,    sgh     = 0,
  sgh2    = 0,    sgh3    = 0,    sgh4    = 0,
  sghl    = 0,    sghs    = 0,    sh      = 0,
  sh2     = 0,    sh3     = 0,    sh1     = 0,
  shs     = 0,    si      = 0,    si2     = 0,
  si3     = 0,    sil     = 0,    sini2   = 0,
  sinis   = 0,    sinok   = 0,    sinq    = 0,
  sinzf   = 0,    sis     = 0,    sl      = 0,
  sl2     = 0,    sl3     = 0,    sl4     = 0,
  sll     = 0,    sls     = 0,    sse     = 0,
  ssg     = 0,    ssh     = 0,    ssi     = 0,
  ssl     = 0,    stem    = 0,    step2   = 0,
  stepn   = 0,    stepp   = 0,
  thgr    = 0,    x1      = 0,
  x2      = 0,    x2li    = 0,    x2omi   = 0,
  x3      = 0,    x4      = 0,    x5      = 0,
  x6      = 0,    x7      = 0,    x8      = 0,
  xfact   = 0,    xgh2    = 0,    xgh3    = 0,
  xgh4    = 0,    xh2     = 0,    xh3     = 0,
  xi2     = 0,    xi3     = 0,    xl      = 0,
  xl2     = 0,    xl3     = 0,    xl4     = 0,
  xlamo   = 0,    xldot   = 0,    xli     = 0,
  xls     = 0,    xmao    = 0,    xnddt   = 0,
  xndot   = 0,    xni     = 0,    xno2    = 0,
  xnodce  = 0,    xnoi    = 0,    xnq     = 0,
  xomi    = 0,    xpidot  = 0,    xqncl   = 0,
  z1      = 0,    z11     = 0,    z12     = 0,
  z13     = 0,    z2      = 0,    z21     = 0,
  z22     = 0,    z23     = 0,    z3      = 0,
  z31     = 0,    z32     = 0,    z33     = 0,
  zcosg   = 0,    zcosgl  = 0,    zcosh   = 0,
  zcoshl  = 0,    zcosi   = 0,    zcosil  = 0,
  ze      = 0,    zf      = 0,    zm      = 0,
  zmol    = 0,    zmos    = 0,
  zn      = 0,    zsing   = 0,    zsingl  = 0,
  zsinh   = 0,    zsinhl  = 0,    zsini   = 0,
  zsinil  = 0,    zx      = 0,    zy      = 0;
  
  private double ds50=0;
  private double xmdf,xnode,omgadf,em,e,xmam;
  
  private double Thetag(double epoch) {
    // Reference:  The 1992 Astronomical Almanac, page B6.
    double year,day,UT,jd,TU,GMST;
    // Modification to support Y2K
    // Valid 1957 through 2056
    year = Math.floor(epoch*1E-3);
    if (year < 57) {
      year = year + 2000;
    } else {
      year = year + 1900;
    }
    // End modification
    day  = (epoch*1E-3-Math.floor(epoch*1E-3))*1E3;
    UT   = day-Math.floor(day);
    day  = Math.floor(day);
    jd   = Julian_Date_of_Year(year) + day;
    TU   = (jd - 2451545.0)/36525;
    GMST = 24110.54841 + TU * (8640184.812866 + TU * (0.093104 - TU * 6.2E-6));
    GMST = Modulus(GMST + secday*omega_E*UT,secday);
    ds50 = jd - 2433281.5 + UT;
    return 2*Math.PI* GMST/secday;
    // ThetaG = Modulus(6.3003880987*ds50 + 1.72944494,twopi);
  } //Function ThetaG
  
  private static double ThetaG_JD(double jd) {
    // Reference:  The 1992 Astronomical Almanac, page B6.
    double UT,TU,GMST;
    UT   = (jd+0.5)-Math.floor(jd + 0.5);
    jd   = jd - UT;
    TU   = (jd - 2451545.0)/36525;
    GMST = 24110.54841 + TU * (8640184.812866 + TU * (0.093104 - TU * 6.2E-6));
    GMST = Modulus(GMST + secday*omega_E*UT,secday);
    return 2*Math.PI*GMST/secday;
  } //Function ThetaG_JD
  
  private void Deep(int ideep) {
    switch(ideep) {
      case dpinit : // Entrance for deep space initialization
        thgr = Thetag(epoch);
        eq = eo;
        xnq = xnodp;
        aqnv = 1/ao;
        xqncl = xincl;
        xmao = xmo;
        xpidot = omgdt + xnodot;
        sinq = Math.sin(xnodeo);
        cosq = Math.cos(xnodeo);
        omegaq = omegao;
        // Initialize lunar solar terms
        day = ds50 + 18261.5;  //Days since 1900 Jan 0.5
        if (day != preep) {
          preep = day;
          xnodce = 4.5236020 - 9.2422029E-4*day;
          stem = Math.sin(xnodce);
          ctem = Math.cos(xnodce);
          zcosil = 0.91375164 - 0.03568096*ctem;
          zsinil = Math.sqrt(1 - zcosil*zcosil);
          zsinhl = 0.089683511*stem/zsinil;
          zcoshl = Math.sqrt(1 - zsinhl*zsinhl);
          c = 4.7199672 + 0.22997150*day;
          gam = 5.8351514 + 0.0019443680*day;
          zmol = Fmod2p(c - gam);
          zx = 0.39785416*stem/zsinil;
          zy = zcoshl*ctem + 0.91744867*zsinhl*stem;
          zx = Math.atan2(zx,zy);
          zx = gam + zx - xnodce;
          zcosgl = Math.cos(zx);
          zsingl = Math.sin(zx);
          zmos = 6.2565837 + 0.017201977*day;
          zmos = Fmod2p(zmos);
        }
        // Do solar terms
        savtsn = 1E20;
        zcosg = zcosgs;
        zsing = zsings;
        zcosi = zcosis;
        zsini = zsinis;
        zcosh = cosq;
        zsinh = sinq;
        cc = c1ss;
        zn = zns;
        ze = zes;
        xnoi = 1/xnq;
        for(int timesthrough=0;timesthrough<2;timesthrough++) {
          a1 = zcosg*zcosh + zsing*zcosi*zsinh;
          a3 = -zsing*zcosh + zcosg*zcosi*zsinh;
          a7 = -zcosg*zsinh + zsing*zcosi*zcosh;
          a8 = zsing*zsini;
          a9 = zsing*zsinh + zcosg*zcosi*zcosh;
          a10 = zcosg*zsini;
          a2 = cosiq*a7 +  siniq*a8;
          a4 = cosiq*a9 +  siniq*a10;
          a5 = -siniq*a7 +  cosiq*a8;
          a6 = -siniq*a9 +  cosiq*a10;
          x1 = a1*cosomo + a2*sinomo;
          x2 = a3*cosomo + a4*sinomo;
          x3 = -a1*sinomo + a2*cosomo;
          x4 = -a3*sinomo + a4*cosomo;
          x5 = a5*sinomo;
          x6 = a6*sinomo;
          x7 = a5*cosomo;
          x8 = a6*cosomo;
          z31 = 12*x1*x1 - 3*x3*x3;
          z32 = 24*x1*x2 - 6*x3*x4;
          z33 = 12*x2*x2 - 3*x4*x4;
          z1 = 3*(a1*a1 + a2*a2) + z31*eqsq;
          z2 = 6*(a1*a3 + a2*a4) + z32*eqsq;
          z3 = 3*(a3*a3 + a4*a4) + z33*eqsq;
          z11 = -6*a1*a5 + eqsq*(-24*x1*x7 - 6*x3*x5);
          z12 = -6*(a1*a6 + a3*a5)
          + eqsq*(-24*(x2*x7 + x1*x8) - 6*(x3*x6 + x4*x5));
          z13 = -6*a3*a6 + eqsq*(-24*x2*x8 - 6*x4*x6);
          z21 = 6*a2*a5 + eqsq*(24*x1*x5 - 6*x3*x7);
          z22 = 6*(a4*a5 + a2*a6)
          + eqsq*(24*(x2*x5 + x1*x6) - 6*(x4*x7 + x3*x8));
          z23 = 6*a4*a6 + eqsq*(24*x2*x6 - 6*x4*x8);
          z1 = z1 + z1 + bsq*z31;
          z2 = z2 + z2 + bsq*z32;
          z3 = z3 + z3 + bsq*z33;
          s3 = cc*xnoi;
          s2 = -0.5*s3/rteqsq;
          s4 = s3*rteqsq;
          s1 = -15*eq*s4;
          s5 = x1*x3 + x2*x4;
          s6 = x2*x3 + x1*x4;
          s7 = x2*x4 - x1*x3;
          se = s1*zn*s5;
          si = s2*zn*(z11 + z13);
          sl = -zn*s3*(z1 + z3 - 14 - 6*eqsq);
          sgh = s4*zn*(z31 + z33 - 6);
          sh = -zn*s2*(z21 + z23);
          if (xqncl < 5.2359877E-2) {
            sh = 0;
          }
          ee2 = 2*s1*s6;
          e3 = 2*s1*s7;
          xi2 = 2*s2*z12;
          xi3 = 2*s2*(z13 - z11);
          xl2 = -2*s3*z2;
          xl3 = -2*s3*(z3 - z1);
          xl4 = -2*s3*(-21 - 9*eqsq)*ze;
          xgh2 = 2*s4*z32;
          xgh3 = 2*s4*(z33 - z31);
          xgh4 = -18*s4*ze;
          xh2 = -2*s2*z22;
          xh3 = -2*s2*(z23 - z21);
          if (timesthrough==0) {
            // Do lunar terms
            sse = se;
            ssi = si;
            ssl = sl;
            ssh = sh/siniq;
            ssg = sgh - cosiq*ssh;
            se2 = ee2;
            si2 = xi2;
            sl2 = xl2;
            sgh2 = xgh2;
            sh2 = xh2;
            se3 = e3;
            si3 = xi3;
            sl3 = xl3;
            sgh3 = xgh3;
            sh3 = xh3;
            sl4 = xl4;
            sgh4 = xgh4;
            zcosg = zcosgl;
            zsing = zsingl;
            zcosi = zcosil;
            zsini = zsinil;
            zcosh = zcoshl*cosq + zsinhl*sinq;
            zsinh = sinq*zcoshl - cosq*zsinhl;
            zn = znl;
            cc = c1l;
            
            ze = zel;
          }
        } //for
        sse = sse + se;
        ssi = ssi + si;
        ssl = ssl + sl;
        ssg = ssg + sgh - cosiq/siniq*sh;
        ssh = ssh + sh/siniq;
        // Geopotential resonance initialization for 12 hour orbits
        iresfl = 0;
        isynfl = 0;
        if (!((xnq < 0.0052359877) && (xnq > 0.0034906585))) {
          if ((xnq < 8.26E-3) || (xnq > 9.24E-3)) return;
          if (eq < 0.5) return;
          iresfl = 1;
          eoc = eq*eqsq;
          g201 = -0.306 - (eq - 0.64)*0.440;
          if (eq <= 0.65) {
            g211 = 3.616 - 13.247*eq + 16.290*eqsq;
            g310 = -19.302 + 117.390*eq - 228.419*eqsq + 156.591*eoc;
            g322 = -18.9068 + 109.7927*eq - 214.6334*eqsq + 146.5816*eoc;
            g410 = -41.122 + 242.694*eq - 471.094*eqsq + 313.953*eoc;
            g422 = -146.407 + 841.880*eq - 1629.014*eqsq + 1083.435*eoc;
            g520 = -532.114 + 3017.977*eq - 5740*eqsq + 3708.276*eoc;
          } else {
            g211 = -72.099 + 331.819*eq - 508.738*eqsq + 266.724*eoc;
            g310 = -346.844 + 1582.851*eq - 2415.925*eqsq + 1246.113*eoc;
            g322 = -342.585 + 1554.908*eq - 2366.899*eqsq + 1215.972*eoc;
            g410 = -1052.797 + 4758.686*eq - 7193.992*eqsq + 3651.957*eoc;
            g422 = -3581.69 + 16178.11*eq - 24462.77*eqsq + 12422.52*eoc;
            if (eq <= 0.715) {
              g520 = 1464.74 - 4664.75*eq + 3763.64*eqsq;
            } else {
              g520 = -5149.66 + 29936.92*eq - 54087.36*eqsq + 31324.56*eoc;
            }
          }
          if (eq < (0.7)) {
            g533 = -919.2277 + 4988.61*eq - 9064.77*eqsq + 5542.21*eoc;
            g521 = -822.71072 + 4568.6173*eq - 8491.4146*eqsq + 5337.524*eoc;
            g532 = -853.666 + 4690.25*eq - 8624.77*eqsq + 5341.4*eoc;
          } else {
            g533 = -37995.78 + 161616.52*eq - 229838.2*eqsq + 109377.94*eoc;
            g521 = -51752.104 + 218913.95*eq - 309468.16*eqsq + 146349.42*eoc;
            g532 = -40023.88 + 170470.89*eq - 242699.48*eqsq + 115605.82*eoc;
          }
          sini2 = siniq*siniq;
          f220 = 0.75*(1 + 2*cosiq + cosq2);
          f221 = 1.5*sini2;
          f321 = 1.875*siniq*(1 - 2*cosiq - 3*cosq2);
          f322 = -1.875*siniq*(1 + 2*cosiq - 3*cosq2);
          f441 = 35*sini2*f220;
          f442 = 39.3750*sini2*sini2;
          f522 = 9.84375*siniq*(sini2*(1 - 2*cosiq - 5*cosq2)
          + 0.33333333*(-2 + 4*cosiq + 6*cosq2));
          f523 = siniq*(4.92187512*sini2*(-2 - 4*cosiq + 10*cosq2)
          + 6.56250012*(1 + 2*cosiq - 3*cosq2));
          f542 = 29.53125*siniq*(2 - 8*cosiq + cosq2*(-12 + 8*cosiq + 10*cosq2));
          f543 = 29.53125*siniq*(-2 - 8*cosiq + cosq2*(12 + 8*cosiq - 10*cosq2));
          xno2 = xnq*xnq;
          ainv2 = aqnv*aqnv;
          temp1 = 3*xno2*ainv2;
          temp = temp1*root22;
          d2201 = temp*f220*g201;
          d2211 = temp*f221*g211;
          temp1 = temp1*aqnv;
          temp = temp1*root32;
          d3210 = temp*f321*g310;
          d3222 = temp*f322*g322;
          temp1 = temp1*aqnv;
          temp = 2*temp1*root44;
          d4410 = temp*f441*g410;
          d4422 = temp*f442*g422;
          temp1 = temp1*aqnv;
          temp = temp1*root52;
          d5220 = temp*f522*g520;
          d5232 = temp*f523*g532;
          temp = 2*temp1*root54;
          d5421 = temp*f542*g521;
          d5433 = temp*f543*g533;
          xlamo = xmao + xnodeo + xnodeo - thgr - thgr;
          bfact = xlldot + xnodot + xnodot - thdt - thdt;
          bfact = bfact + ssl + ssh + ssh;
        } else {
          // Synchronous resonance terms initialization
          iresfl = 1;
          isynfl = 1;
          g200 = 1 + eqsq*(-2.5 + 0.8125*eqsq);
          g310 = 1 + 2*eqsq;
          g300 = 1 + eqsq*(-6 + 6.60937*eqsq);
          f220 = 0.75*(1 + cosiq)*(1 + cosiq);
          f311 = 0.9375*siniq*siniq*(1 + 3*cosiq) - 0.75*(1 + cosiq);
          f330 = 1 + cosiq;
          f330 = 1.875*f330*f330*f330;
          del1 = 3*xnq*xnq*aqnv*aqnv;
          del2 = 2*del1*f220*g200*q22;
          del3 = 3*del1*f330*g300*q33*aqnv;
          del1 = del1*f311*g310*q31*aqnv;
          fasx2 = 0.13130908;
          fasx4 = 2.8843198;
          fasx6 = 0.37448087;
          xlamo = xmao + xnodeo + omegao - thgr;
          bfact = xlldot + xpidot - thdt;
          bfact = bfact + ssl + ssg + ssh;
        }
        xfact = bfact - xnq;
        // Initialize integrator
        xli = xlamo;
        xni = xnq;
        atime = 0;
        stepp = 720;
        stepn = -720;
        step2 = 259200;
        break;//dpinit
      case dpsec  :  // Entrance for deep space secular effects
        xll = xll + ssl*t;
        omega_sm = omega_sm + ssg*t;
        xnodes = xnodes + ssh*t;
        _em = eo + sse*t;
        xinc = xincl + ssi*t;
        if (xinc < 0) {
          xinc = -xinc;
          xnodes = xnodes  +  Math.PI;
          omega_sm = omega_sm - Math.PI;
        }
        if (iresfl == 0) return;
        int LineNumber=100;
        //Wow, sphagetti code beyond recognition. This hack implements goto's.
        //Thank goodness for Java switch-case fall-through.
        //LineNumber is the last linenumber which is a goto target.
        //goto N is implemented as LineNumber=N;break;
        for(;;) {
          switch(LineNumber) {
            case 100:
              if ((atime==0) || ((t >= 0) && (atime < 0)) || ((t < 0) && (atime >= 0))) {LineNumber=170;break;}
            case 105:
              if (Math.abs(t) >= Math.abs(atime)) {LineNumber=120;break;}
              if (t >= 0) {
                delt = stepn;
              } else {
                delt = stepp;
              }
            case 110:
              iret = 100;
              LineNumber=160;break;
            case 120:
              if (t > 0) {
                delt = stepp;
              } else {
                delt = stepn;
              }
            case 125:
              if (Math.abs(t - atime) < stepp) {LineNumber=130;break;}
              iret = 125;
              LineNumber=160;break;
            case 130:
              ft = t - atime;
              iretn = 140;
              LineNumber=150;break;
            case 140:
              xl = xli + xldot*ft + xndot*ft*ft*0.5;
              temp = -xnodes + thgr + t*thdt;
              xll = xl - omega_sm + temp;
              if (isynfl == 0) xll = xl + temp + temp;
              return;
              // Dot terms calculated
            case 150:
              if (isynfl != 0) {
                xndot = del1*Math.sin(xli - fasx2) + del2*Math.sin(2*(xli - fasx4))
                + del3*Math.sin(3*(xli - fasx6));
                xnddt = del1*Math.cos(xli - fasx2)
                + 2*del2*Math.cos(2*(xli - fasx4))
                + 3*del3*Math.cos(3*(xli - fasx6));
              } else {
                xomi = omegaq + omgdt*atime;
                x2omi = xomi + xomi;
                x2li = xli + xli;
                xndot = d2201*Math.sin(x2omi + xli - g22)
                + d2211*Math.sin(xli - g22)
                + d3210*Math.sin(xomi + xli - g32)
                + d3222*Math.sin(-xomi + xli - g32)
                + d4410*Math.sin(x2omi + x2li - g44)
                + d4422*Math.sin(x2li - g44)
                + d5220*Math.sin(xomi + xli - g52)
                + d5232*Math.sin(-xomi + xli - g52)
                + d5421*Math.sin(xomi + x2li - g54)
                + d5433*Math.sin(-xomi + x2li - g54);
                xnddt = d2201*Math.cos(x2omi + xli - g22)
                + d2211*Math.cos(xli - g22)
                + d3210*Math.cos(xomi + xli - g32)
                + d3222*Math.cos(-xomi + xli - g32)
                + d5220*Math.cos(xomi + xli - g52)
                + d5232*Math.cos(-xomi + xli - g52)
                + 2*(d4410*Math.cos(x2omi + x2li - g44)
                + d4422*Math.cos(x2li - g44)
                + d5421*Math.cos(xomi + x2li - g54)
                + d5433*Math.cos(-xomi + x2li - g54));
              }
              xldot = xni + xfact;
              xnddt = xnddt*xldot;
              LineNumber=iretn;break;
              // Integrator
            case 160:
              iretn = 165;
              LineNumber=150;break;
            case 165:
              xli = xli + xldot*delt + xndot*step2;
              xni = xni + xndot*delt + xnddt*step2;
              atime = atime + delt;
              LineNumber=iret;break;
              // Epoch restart
            case 170:
              if (t < 0) {
                delt = stepn;
              } else {
                delt = stepp;
              }
              atime = 0;
              xni = xnq;
              xli = xlamo;
              LineNumber=125;break;
          }
        }
      case dpper:  // Entrance for lunar-solar periodics
        sinis = Math.sin(xinc);
        cosis = Math.cos(xinc);
        if (Math.abs(savtsn - t) >= 30) {
          savtsn = t;
          zm = zmos + zns*t;
          zf = zm + 2*zes*Math.sin(zm);
          sinzf = Math.sin(zf);
          f2 = 0.5*sinzf*sinzf - 0.25;
          f3 = -0.5*sinzf*Math.cos(zf);
          ses = se2*f2 + se3*f3;
          sis = si2*f2 + si3*f3;
          sls = sl2*f2 + sl3*f3 + sl4*sinzf;
          sghs = sgh2*f2 + sgh3*f3 + sgh4*sinzf;
          shs = sh2*f2 + sh3*f3;
          zm = zmol + znl*t;
          zf = zm + 2*zel*Math.sin(zm);
          sinzf = Math.sin(zf);
          f2 = 0.5*sinzf*sinzf - 0.25;
          f3 = -0.5*sinzf*Math.cos(zf);
          sel = ee2*f2 + e3*f3;
          sil = xi2*f2 + xi3*f3;
          sll = xl2*f2 + xl3*f3 + xl4*sinzf;
          sghl = xgh2*f2 + xgh3*f3 + xgh4*sinzf;
          sh1 = xh2*f2 + xh3*f3;
          pe = ses + sel;
          pinc = sis + sil;
          pl = sls + sll;
        }
        pgh = sghs + sghl;
        ph = shs + sh1;
        xinc = xinc + pinc;
        _em = _em + pe;
        if (xqncl >= 0.2) {
          // Apply periodics directly
          ph = ph/siniq;
          pgh = pgh - cosiq*ph;
          omega_sm = omega_sm + pgh;
          xnodes = xnodes + ph;
          xll = xll + pl;
        } else {
          // Apply periodics with Lyddane modification
          sinok = Math.sin(xnodes);
          cosok = Math.cos(xnodes);
          alfdp = sinis*sinok;
          betdp = sinis*cosok;
          dalf = ph*cosok + pinc*cosis*sinok;
          dbet = -ph*sinok + pinc*cosis*cosok;
          alfdp = alfdp + dalf;
          betdp = betdp + dbet;
          xls = xll + omega_sm + cosis*xnodes;
          dls = pl + pgh - pinc*xnodes*sinis;
          xls = xls + dls;
          xnodes = Math.atan2(alfdp,betdp);
          xll = xll + pl;
          omega_sm = xls - xll - Math.cos(xinc)*xnodes;
        }
    } //case
  } //Procedure Deep
  
  private void Call_dpinit() {
    eqsq   = eosq;    siniq  = sinio;   cosiq  = cosio;   rteqsq = betao;
    ao     = aodp;    cosq2  = theta2;  sinomo = sing;    cosomo = cosg;
    bsq    = betao2;  xlldot = xmdot;   omgdt  = omgdot;  
    Deep(dpinit);
    eosq   = eqsq;    sinio  = siniq;   cosio  = cosiq;   betao  = rteqsq;
    aodp   = ao;      theta2 = cosq2;   sing   = sinomo;  cosg   = cosomo;
    betao2 = bsq;     xmdot  = xlldot;  omgdot = omgdt;  
  
  } //Procedure Call_dpinit}
  
  private void Call_dpsec(double tsince) {
    xll    = xmdf;    omega_sm = omgadf;  xnodes = xnode;   /*_em     = emm;
        xinc   = xincc;*/        t      = tsince;
        Deep(dpsec);
        xmdf   = xll;     omgadf = omega_sm;  xnode  = xnodes;  em    = _em;
           tsince = t;
  } //Procedure Call_dpsec
  
  private void  Call_dpper() {
    _em     = e;         omega_sm = omgadf;  xnodes = xnode;
    xll    = xmam;
    Deep(dpper);
    e      = _em;        omgadf = omega_sm;  xnode  = xnodes;
    xmam   = xll;
  } //Procedure Call_dpper
  
  //static method variables for SDP4
  private double
  cosg    = 0,
  sing    = 0;
  
  private MathState SDP4(double tsince) {
    int  i;
    double a,axn,ayn,aynl,beta,betal,capu,cos2u,cosepw,cosik,
    cosnok,cosu,cosuk,ecose,elsq,epw,esine,
    pl,r,rdot,rdotk,rfdot,rfdotk,rk,sin2u,sinepw,sinik,
    sinnok,sinu,sinuk,temp,temp4,temp5,temp6,tempa,
    tempe,templ,tsq,u,uk,ux,uy,uz,vx,vy,vz,xinck,
    xl,xll,xlt,xmx,xmy,xn,xnoddf,xnodek,
    x,y,z,xdot,ydot,zdot;
    if (!iflag) {
      // Recover original mean motion (xnodp) and semimajor axis (aodp)
      // from input elements.
      a1 = Math.pow(xke/xno,tothrd);
      cosio = Math.cos(xincl);
      theta2 = cosio*cosio;
      x3thm1 = 3*theta2 - 1;
      eosq = eo*eo;
      betao2 = 1 - eosq;
      betao = Math.sqrt(betao2);
      del1 = 1.5*ck2*x3thm1/(a1*a1*betao*betao2);
      ao = a1*(1 - del1*(0.5*tothrd + del1*(1 + 134/81*del1)));
      delo = 1.5*ck2*x3thm1/(ao*ao*betao*betao2);
      xnodp = xno/(1 + delo);
      aodp = ao/(1 - delo);
      // Initialization
      // For perigee below 156 km, the values of s and qoms2t are altered.
      s4 = s;
      qoms24 = qoms2t;
      perige = (aodp*(1 - eo) - ae)*xmper;
      if (perige < 156)  {
        s4 = perige - 78;
        if (perige <= 98)  {
          s4 = 20;
        }
        qoms24 = Math.pow((120 - s4)*ae/xmper,4);
        s4 = s4/xmper + ae;
      }
      pinvsq = 1/(aodp*aodp*betao2*betao2);
      sing = Math.sin(omegao);
      cosg = Math.cos(omegao);
      tsi = 1/(aodp - s4);
      eta = aodp*eo*tsi;
      etasq = eta*eta;
      eeta = eo*eta;
      psisq = Math.abs(1 - etasq);
      coef = qoms24*Math.pow(tsi,4);
      coef1 = coef/Math.pow(psisq,3.5);
      c2 = coef1*xnodp*(aodp*(1 + 1.5*etasq + eeta*(4 + etasq))
      + 0.75*ck2*tsi/psisq*x3thm1*(8 + 3*etasq*(8 + etasq)));
      c1 = bstar*c2;
      sinio = Math.sin(xincl);
      a3ovk2 = -xj3/ck2*Math.pow(ae,3);
      x1mth2 = 1 - theta2;
      c4 = 2*xnodp*coef1*aodp*betao2*(eta*(2 + 0.5*etasq)
      + eo*(0.5 + 2*etasq) - 2*ck2*tsi/(aodp*psisq)
      *(-3*x3thm1*(1 - 2*eeta + etasq*(1.5 - 0.5*eeta))
      + 0.75*x1mth2*(2*etasq - eeta*(1 + etasq))*Math.cos(2*omegao)));
      theta4 = theta2*theta2;
      temp1 = 3*ck2*pinvsq*xnodp;
      temp2 = temp1*ck2*pinvsq;
      temp3 = 1.25*ck4*pinvsq*pinvsq*xnodp;
      xmdot = xnodp + 0.5*temp1*betao*x3thm1
      + 0.0625*temp2*betao*(13 - 78*theta2 + 137*theta4);
      x1m5th = 1 - 5*theta2;
      omgdot = -0.5*temp1*x1m5th + 0.0625*temp2*(7 - 114*theta2 + 395*theta4)
      + temp3*(3 - 36*theta2 + 49*theta4);
      xhdot1 = -temp1*cosio;
      xnodot = xhdot1 + (0.5*temp2*(4 - 19*theta2)
      + 2*temp3*(3 - 7*theta2))*cosio;
      xnodcf = 3.5*betao2*xhdot1*c1;
      t2cof = 1.5*c1;
      xlcof = 0.125*a3ovk2*sinio*(3 + 5*cosio)/(1 + cosio);
      aycof = 0.25*a3ovk2*sinio;
      x7thm1 = 7*theta2 - 1;
      if (!isimp) {
        c1sq = c1*c1;
        d2 = 4*aodp*tsi*c1sq;
        temp = d2*tsi*c1/3;
        d3 = (17*aodp + s4)*temp;
        d4 = 0.5*temp*aodp*tsi*(221*aodp + 31*s4)*c1;
        t3cof = d2 + 2*c1sq;
        t4cof = 0.25*(3*d3 + c1*(12*d2 + 10*c1sq));
        t5cof = 0.2*(3*d4 + 12*c1*d3 + 6*d2*d2 + 15*c1sq*(2*d2 + c1sq));
      }
      iflag = true;
      Call_dpinit();
    }
    // Update for secular gravity and atmospheric drag
    xmdf = xmo + xmdot*tsince;
    omgadf = omegao + omgdot*tsince;
    xnoddf = xnodeo + xnodot*tsince;
    tsq = tsince*tsince;
    xnode = xnoddf + xnodcf*tsq;
    tempa = 1 - c1*tsince;
    tempe = bstar*c4*tsince;
    templ = t2cof*tsq;
    xn = xnodp;
    Call_dpsec(tsince);
    a = Math.pow(xke/xn,tothrd)*tempa*tempa;
    e = em - tempe;
    xmam = xmdf + xnodp*templ;
    Call_dpper();
    xl = xmam + omgadf + xnode;
    beta = Math.sqrt(1 - e*e);
    xn = xke/Math.pow(a,1.5);
    // Long period periodics
    axn = e*Math.cos(omgadf);
    temp = 1/(a*beta*beta);
    xll = temp*xlcof*axn;
    aynl = temp*aycof;
    xlt = xl + xll;
    ayn = e*Math.sin(omgadf) + aynl;
    // Solve Kepler's Equation
    capu = Fmod2p(xlt - xnode);
    temp2 = capu;
    sinepw=0;
    cosepw=0;
    temp3=0;
    temp4=0;
    temp5=0;
    temp6=0;
    epw=0;
    for(i = 1;i<=10;i++) {
      sinepw = Math.sin(temp2);
      cosepw = Math.cos(temp2);
      temp3 = axn*sinepw;
      temp4 = ayn*cosepw;
      temp5 = axn*cosepw;
      temp6 = ayn*sinepw;
      epw = (capu - temp4 + temp3 - temp2)/(1 - temp5 - temp6) + temp2;
      if (Math.abs(epw - temp2) <= e6a) break;
      temp2 = epw;
    } //for i
    // Short period preliminary quantities
    ecose = temp5 + temp6;
    esine = temp3 - temp4;
    elsq = axn*axn + ayn*ayn;
    temp = 1 - elsq;
    pl = a*temp;
    r = a*(1 - ecose);
    temp1 = 1/r;
    rdot = xke*Math.sqrt(a)*esine*temp1;
    rfdot = xke*Math.sqrt(pl)*temp1;
    temp2 = a*temp1;
    betal = Math.sqrt(temp);
    temp3 = 1/(1 + betal);
    cosu = temp2*(cosepw - axn + ayn*esine*temp3);
    sinu = temp2*(sinepw - ayn - axn*esine*temp3);
    u = Math.atan2(sinu,cosu);
    sin2u = 2*sinu*cosu;
    cos2u = 2*cosu*cosu - 1;
    temp = 1/pl;
    temp1 = ck2*temp;
    temp2 = temp1*temp;
    // Update for short periodics
    rk = r*(1 - 1.5*temp2*betal*x3thm1) + 0.5*temp1*x1mth2*cos2u;
    uk = u - 0.25*temp2*x7thm1*sin2u;
    xnodek = xnode + 1.5*temp2*cosio*sin2u;
    xinck = xinc + 1.5*temp2*cosio*sinio*cos2u;
    rdotk = rdot - xn*temp1*x1mth2*sin2u;
    rfdotk = rfdot + xn*temp1*(x1mth2*cos2u + 1.5*x3thm1);
    // Orientation vectors
    sinuk = Math.sin(uk);
    cosuk = Math.cos(uk);
    sinik = Math.sin(xinck);
    cosik = Math.cos(xinck);
    sinnok = Math.sin(xnodek);
    cosnok = Math.cos(xnodek);
    xmx = -sinnok*cosik;
    xmy = cosnok*cosik;
    ux = xmx*sinuk + cosnok*cosuk;
    uy = xmy*sinuk + sinnok*cosuk;
    uz = sinik*sinuk;
    vx = xmx*cosuk - cosnok*sinuk;
    vy = xmy*cosuk - sinnok*sinuk;
    vz = sinik*cosuk;
    // Position and velocity
    x = rk*ux;
    y = rk*uy;
    z = rk*uz;
    xdot = rdotk*ux + rfdotk*vx;
    ydot = rdotk*uy + rfdotk*vy;
    zdot = rdotk*uz + rfdotk*vz;
    return new MathState(x,y,z,xdot,ydot,zdot);
  } //Procedure SDP4

  public MathVector CalcPos(Time TT) {
	return defaultCalcPos(TT);
  }
  public MathVector CalcVel(Time TT) {
	return defaultCalcVel(TT);
  }
}
