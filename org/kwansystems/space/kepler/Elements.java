package org.kwansystems.space.kepler;

import org.kwansystems.tools.rootfind.*;
import org.kwansystems.tools.rotation.MathMatrix;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeEpoch;
import org.kwansystems.tools.time.TimeScale;
import org.kwansystems.tools.time.TimeUnits;
import org.kwansystems.tools.vector.*;
import org.kwansystems.tools.*;

import static java.lang.Math.*;
import static org.kwansystems.space.planet.Spheroid.*;
import static org.kwansystems.tools.time.TimeEpoch.*;
import static org.kwansystems.tools.time.TimeUnits.*;


public class Elements {
  
  public static void main(String args[]) {
    /*
    Elements E=new Elements();
    E.A=6981354.000; 
    E.E=0.000631594;
    E.I=toRadians(97.806590);
    E.AP=toRadians(88.531550);
    E.TA=toRadians(76.304500);
    E.LAN=toRadians(8.500091);
    E.GM=3.986004418e14;
    E.LengthUnit="m";
    E.Epoch=new Time(2006,9,29,20,34,26.30,Seconds,UTC);
    E.FillInElements();
    Time DropTime=new Time(2006,9,29,20,25,16.5,Seconds,UTC);
    System.out.println(E);
    MathState RV=E.EleToPosVel();
    System.out.println(RV.R());
    System.out.println(Math.toDegrees(Earth.GMST(DropTime)));
    MathVector LLA=Earth.xyz2lla(RV.R());
    System.out.println(Math.toDegrees(LLA.X()));
    System.out.println(Math.toDegrees(LLA.Y()));
    System.out.println(LLA.Z());
    */
    //   MathVector ECF_r=new MathVector(- 4146686.6, -5451504.6, 1335249.5);
    //   MathVector ECF_v=new MathVector(-2096.2, -210.7, -7345.6);
    MathVector ECF_r=new MathVector(-4132810.632,   -5456212.608,1353831.2268);
    MathVector ECF_lla=WGS84.xyz2lla(ECF_r);
    MathVector ECF_v=new MathVector(-2108.2346,   -231.7602,-7341.1056);
    Elements E=new Elements();
    Time Epoch=new Time(2454216.359026,TimeUnits.Days,TimeScale.UTC,TimeEpoch.JD);
    double GMST=org.kwansystems.space.ephemeris.earth.EarthRotGMST.Theta(Epoch);
    System.out.println("GMST: "+toDegrees(GMST));
    MathMatrix M=MathMatrix.Rot3(-GMST);
    MathState ECF=new MathState(ECF_r,ECF_v);
    System.out.println("ECEF State Vector (m and m/s)");
    System.out.println(ECF);
    System.out.println("ECEF injection location (geodetic coordinates, deg and m)");
    System.out.println("lat: "+Math.toDegrees(ECF_lla.X())+"  lon: "+Math.toDegrees(ECF_lla.Y())+"  alt: "+ECF_lla.Z());
    System.out.println("Injection time");
    System.out.println(Epoch);
    Epoch.Units=Seconds;
    Epoch.Epoch=Java;
    System.out.println(Epoch);
    MathState ECI=M.transform(ECF);
    MathVector ECI_v=MathVector.add(ECI.V(),org.kwansystems.space.planet.Planet.Earth.Wind(ECI.R()));
    ECI=new MathState(ECI.R(),ECI_v);
    E=new Elements();
    E.verbose=true;
    E.PosVelToEle(new MathStateTime(ECI,Epoch), org.kwansystems.space.planet.Planet.Earth.S.GM, "m");
    System.out.println("ECI True of Date osculating orbit elements");
    E.FillInElements();
    System.out.println(E);
    System.out.println("ECI State Vector (m and m/s)");
    System.out.println(ECI);
  }
  //Primary Elements
  public double A;
  public double E;
  public double I;
  public double LAN;
  public double AP;
  public Time TP;            //Time of previous periapse (or only periapse for E>=1)
  //Secondary Elements
  public double Periapse,Apoapse;
  public double Period,N;    //Period in time units, Mean motion, radians/time unit
  public double TA,EA,FA,M;  //True, Eccentric, Hyperbolic, and Mean Anomalies
  public double U;           //Argument of latitude = AP+TA 
  public double LP;          //Longitude of Periapse= LAN+AP
  public double L;           //Mean Longitude = LP+M
  public Time TNextP;        //Time of next periapse
  public double P;           //Semiparameter
  public Time Epoch;         //JD extraced from TLE
  //Other stuff
  public static boolean verbose=false;
  public double GM;          //GM used to calculate the other elements
  public MathStateTime S;    //State and time used to calculate other elements
  public String LengthUnit,TimeUnit;
  public static final MathVector x=new MathVector(1,0,0);
  public static final MathVector y=new MathVector(0,1,0);
  public static final MathVector z=new MathVector(0,0,1);
  public static double sinh(double X) {
    return (Math.exp(X) - Math.exp(-X)) / 2;
  }
  public static double acosh(double X) {
    return Math.log(X + Math.sqrt(X * X - 1));
  }
  public Elements(MathStateTime LS, double LGM, String LLengthUnit) {
    PosVelToEle(LS,LGM,LLengthUnit);
  }
  public void PosVelToEle(MathStateTime LS,double LGM,String LLengthUnit) {
  //Given Position R and Velocity V at Time T in gravity field GM,
  //Find Semimajor axis A, Eccentricity E, Inclination I,
  //     Longitude of Ascending Node LAN, Argument of Pericenter AP,
  //     and Time of Pericenter Passage Tp
    S=LS;
    GM=LGM;
    MathVector RV=S.S.R();
    MathVector VV=S.S.V();
    double R=RV.length();
    double V=VV.length();
    if(verbose)System.out.println("Position RV:\n"+RV.toString());
    if(verbose)System.out.println("Velocity VV:\n"+VV.toString());
    Epoch=S.T;
    TimeUnit=(Epoch.getUnits()==TimeUnits.Days)?"day":"sec";
    LengthUnit=LLengthUnit;
    if(verbose)System.out.println("Epoch: "+Epoch.toString());
    MathVector HV=MathVector.cross(RV,VV);
    if(verbose)System.out.println("Angular Momentum Vector HV:\n"+HV.toString());
    double H=HV.length();
    MathVector NV=MathVector.cross(z,HV);
    if(verbose)System.out.println("Ascending Node Vector NV:\n"+NV.toString());
    double GMEVCoeff1=V*V-GM/R;
    double GMEVCoeff2=-MathVector.dot(RV,VV);
    MathVector GMEV=MathVector.add(RV.mul(GMEVCoeff1),VV.mul(GMEVCoeff2));
    if(verbose)System.out.println("GM*Eccentricity vector:\n"+GMEV.toString());
    MathVector EV=GMEV.div(GM);
    if(verbose)System.out.println("Eccentricity Vector:\n"+EV.toString());
    E=EV.length();
    P=Math.pow(HV.length(),2)/GM;
    A=P/(1-E*E);
    if(E<1) {
      Apoapse=A*(1+E);
      Period=2*Math.PI/Math.sqrt(GM)*Math.sqrt(A*A*A);
    } else {
      Apoapse=Double.POSITIVE_INFINITY;
      Period=Apoapse;
    }
    Periapse=A*(1-E);
    I=MathVector.vangle(HV,z);
    if(I==0|I==Math.PI) {
      LAN=0;
      if(E==0) {
        AP=0;
      } else {
        AP=Math.atan2(GMEV.Y(),GMEV.X());
      }
    } else {
      LAN=Math.atan2(NV.Y(),NV.X());
      if(LAN<0) LAN+=2*PI;
      if(E==0) {
        AP=0;
      } else {
        AP=MathVector.vangle(NV,GMEV);
        if(GMEV.Z()<0) {
          AP=2*Math.PI-AP;
        }
      }
    }
    if(E==0){
      TA=MathVector.vangle(NV, RV);
      if(RV.Z()<0) TA=2*PI-TA;
    } else {
      TA=MathVector.vangle(RV,GMEV);
      if(MathVector.dot(RV,VV)<0) TA=2*Math.PI-TA;
    }
    if(E<1) {
      EA=Math.acos((E+Math.cos(TA))/(1+E*Math.cos(TA)));
      if(TA>Math.PI||TA<0) {
        EA=2*Math.PI-EA;
      }
      while(EA<0) EA+=2*PI;
      if(verbose)System.out.println("Eccentric Anomaly EA: "+EA);
      M=EA-E*Math.sin(EA);
      while(M<0) M+=2*PI;
      if(verbose)System.out.println("Mean Anomaly M:  "+M);
      N=Math.sqrt(GM/(A*A*A));
      if(verbose)System.out.println("Mean Motion n:   "+N+"rad/"+TimeUnit);
      if(verbose)System.out.println("Tp: "+(-M/N));
      TP=Time.add(Epoch,-M/N,Seconds);
      TNextP=Time.add(TP,Period,Seconds);
      if(verbose)System.out.println("Tp: "+TP.toString());
    } else {
      double FA=acosh((E+Math.cos(TA))/(1+E*Math.cos(TA)));
      if(TA>Math.PI|TA<0) {
        FA=-Math.abs(FA);
      }
      double N=Math.sqrt(GM/(-A*A*A));
      double M=E*sinh(FA)-FA;
      TP=Time.add(Epoch,-M/N);
      TNextP=TP;
    }
    FillInElements();
  }
  public MathState Propagate(Time T) {
    //Propagate to time T and return state vector
    double TT=T.get()-Epoch.get();
    double OldM=M;
    M+=N*TT;
    MeanToTrue();
    MathState Result=EleToPosVel();
    M=OldM;
    MeanToTrue();
    return Result;
  }
  public MathVector EleToPos(double LTA) {
    P=A*(1-E*E);    
    MathVector R=new MathVector(
      P*Math.cos(LTA)/(1+E*Math.cos(LTA)),
      P*Math.sin(LTA)/(1+E*Math.cos(LTA)),
      0
    );
    if(verbose)System.out.println("R(PQW): \n"+R);
    R=PQWtoIJK().transform(R);
    return R;
  }
  public MathVector EleToVel(double LTA) {
    P=A*(1-E*E);    
    MathVector V=new MathVector(
      -Math.sqrt(GM/P)*Math.sin(TA),
       Math.sqrt(GM/P)*(E+Math.cos(TA)),
       0
    );
    if(verbose)System.out.println("V(PQW): \n"+V);
    V=PQWtoIJK().transform(V);
    return V;
  }
  public MathMatrix PQWtoIJK() {
    MathMatrix result=MathMatrix.mul(MathMatrix.mul(MathMatrix.Rot3(-LAN),MathMatrix.Rot1(-I)),MathMatrix.Rot3(-AP));
    if(verbose)System.out.println(result);
    return result;
  }
  public MathState EleToPosVel() {
  //Given elements already set in element members
  //Find position and velocity
    return new MathState(EleToPos(TA),EleToVel(TA));
  }
  public MathVector[] PlotOrbit(double TA1, double TA2, int Steps) {
    MathVector[] result=new MathVector[Steps];
    for(int i=0;i<Steps;i++) {
      double LTA=Scalar.linterp(0,TA1,1,TA2, ((double)i)/((double)Steps-1));
      result[i]=EleToPos(LTA);
    }
    return result;
  }
  public MathVector[] PlotOrbit(int Steps) {
    return PlotOrbit(-Asymptote()+0.00001,Asymptote()-0.00001, Steps);
  }
  public double Asymptote() {
    if(E<1) {
      return Math.PI;
    } else {
      return Math.acos(-1/E);
    }
  }
  public String toString() {
    String TpS,TnpS;
    if(TP!=null) {
       TpS="Time of Periapse            TP:  "+TP.toString()+"\n";
    } else {
      TpS="";
    } 	
    if(TNextP!=null & E<1) {
      TnpS="Time of next Periapse       TnP: "+TNextP.toString()+"\n";
    } else {
      TnpS="";
    } 
    String result=
(Epoch!=null?"Epoch                         :"+Epoch.toString()+"\n":"")+
           "Semimajor Axis              A:   "+A+LengthUnit+"\n"+
           "Eccentricity                E:   "+E+"\n"+
	   "Inclination                 I:   "+Math.toDegrees(I)+"deg\n"+
	   "Longitude of Ascending Node LAN: "+Math.toDegrees(LAN)+"deg\n"+
	   "Argument of Periapse        AP:  "+Math.toDegrees(AP)+"deg\n"+
	   "Longitude of Periapse       LP:  "+Math.toDegrees(LP)+"deg\n"+
           TpS+
	   "Apoapse                     Ra:  "+Apoapse+LengthUnit+"\n"+
	   "Periapse                    Rp:  "+Periapse+LengthUnit+"\n"+
	   "Period                      T:   "+Period+TimeUnit+"\n"+
           TnpS+
   	   "Mean Motion                 N:   "+Math.toDegrees(N)+"deg/"+TimeUnit+"\n"+
	   "True Anomaly                TA:  "+Math.toDegrees(TA)+"deg\n"+
	   "Mean Anomaly                M:   "+Math.toDegrees(M)+"deg\n"+
	   "Argument of Latitude        U:   "+Math.toDegrees(U)+"deg\n"+
	   "Mean Longitude              L:   "+Math.toDegrees(L)+"deg\n"+
   	   "Gravitational Parameter     GM:  "+GM+LengthUnit+"^3/"+TimeUnit+"^2\n";
    return result;
  }
  public static double MeanToTrue(double M, double E) {
    double EA=MeanToEcc(M,E);
    return EccToTrue(EA,E);
  }
  public void MeanToTrue() {
    MeanToEcc();
    EccToTrue();
  }
  public static double TrueToMean(double TA, double E) {
    double EA=TrueToEcc(TA,E);
    return EccToMean(EA,E);
  }
  public void TrueToMean() {
    TrueToEcc();
    EccToMean();
  }
  public static double EccToMean(double EA,double E) {
    double M=EA-E*Math.sin(EA);
    if(E<1 && EA>0) while(EA<0) EA+=2*PI;
    return M;
  }
  public void EccToMean() {
    //Assumes E and EA are already set correctly
    M=EccToMean(EA,E);
  }
  public static double EccToTrue(double EA, double E) {
    return 2*Math.atan(Math.sqrt((1+E)/(1-E))*Math.tan(EA/2));
  }
  public void EccToTrue() {
    //Assumes that E and EA are already set correctly
    TA=EccToTrue(EA,E);
  }
  public static double TrueToEcc(double TA, double E) {
    double EA=2*Math.atan(Math.sqrt((1-E)/(1+E))*Math.tan(TA/2));
    if(E<1 && TA>0) while(EA<0) EA+=2*PI;
    return EA;
  }
  public void TrueToEcc() {
    //Assumes that E and TA are already set correctly
    EA=TrueToEcc(TA,E);
  }
  public static double MeanToEcc(double M, final double ecc) {
    //This solves Kepler's Equation M=EA-ecc*sin(E) for EA
    while(M>2*Math.PI)M-=Math.PI*2;
    while(M<-2*Math.PI)M+=Math.PI*2;
    double EA;
    if(M>Math.PI|(M<0 & M>-Math.PI)) {
      EA=M-ecc;
    } else {
      EA=M+ecc;
    }
    return RootFind.Find(
      new Newton(  //Root finder
        1e-8,      //Tolerance
        new RootDeriv() {  //Equations
          public double F(double EA) {
            return EA-ecc*Math.sin(EA);
          }
          public double dFdx(double EA) {
            return 1-ecc*Math.cos(EA);
          }
        }
      ),
      M, //Target dependent value
      EA,//First independent value
      0  //Second independent value (ignored)
    );
  }
  public void MeanToEcc() {
    //Assumes E and M are already set correctly
    EA=MeanToEcc(M,E);
  }
  public void parseTwoLine(String Line1, String Line2) {
    Line2=" "+Line2; //To align to book columns
    I=Math.toRadians(Double.parseDouble(Line2.substring(9,16)));
    LAN=Math.toRadians(Double.parseDouble(Line2.substring(17,25)));
    E=Double.parseDouble("0."+Line2.substring(26,33));
    Epoch=new Time(TLEEpochToJD(Double.parseDouble(Line1.substring(16,30))),TimeUnits.Days,TimeScale.UTC,TimeEpoch.JD);
    AP=Math.toRadians(Double.parseDouble(Line2.substring(34,42)));
    M=Math.toRadians(Double.parseDouble(Line2.substring(43,51)));
    MeanToTrue();
    double RevPerDay=Double.parseDouble(Line2.substring(52,63));
    if(verbose)System.out.println("RevPerDay: "+RevPerDay);
    N=RevPerDay*2*Math.PI/86400.0; //rad/sec
    if(verbose)System.out.println("N: "+N);
    Period=2*Math.PI/N;
    GM=398600.4415; //TLE's are always Earth orbiters
    LengthUnit="km";
    TimeUnit="day";
    A=Math.pow(GM/(N*N),1.0/3.0);    
    P=A*(1-E*E);    
    Apoapse=A*(1+E);
    Periapse=A*(1-E);
    if(verbose)System.out.println("A: "+A);
  }
  public double VelocityFromRadius(double r) {
    return Math.sqrt(2*GM/r-GM/A);
  }
  //Fill in unknown elements from known elements. This method leaves a consistent
  //set of elements, with NaN remaining in the elements with insufficient data
  //to calculate
  public void FillInElements() {
    int changes=1,count=0;
    while(changes!=0 && count<2) {
      if(verbose)System.out.println("Top");
      changes=0;
      double NewValue;
      Time NewTimeValue;
      //P, A and E
      if(!Double.isNaN(Periapse) && !Double.isNaN(Apoapse)) {
        NewValue=(Periapse+Apoapse)/2;
        if(A!=NewValue) {A=NewValue;changes++;if(verbose)System.out.println("A=f(Ra,Rp)");}
        NewValue=(Apoapse-Periapse)/(Apoapse+Periapse);
        if(E!=NewValue) {E=NewValue;changes++;if(verbose)System.out.println("E=f(Ra,Rp)");}
        NewValue=A*(1-E*E);
        if(P!=NewValue) {P=NewValue;changes++;if(verbose)System.out.println("P=f(A,E)");}
      } else 
      //Semiparameter,Periapse and Apoapse
      if(!Double.isNaN(A) && !Double.isNaN(E)) {
        NewValue=A*(1-E*E);
        if(P!=NewValue) {P=NewValue;changes++;if(verbose)System.out.println("P=f(A,E)");}
        NewValue=A*(1-E);
        if(Periapse!=NewValue) {Periapse=NewValue;changes++;if(verbose)System.out.println("Rp=f(A,E)");}
        NewValue=A*(1+E);
        if(Apoapse!=NewValue) {Apoapse=NewValue;changes++;if(verbose)System.out.println("Ra=f(A,E)");}
      }
      //Period and N
      if(!Double.isNaN(A) && !Double.isNaN(GM)) {
        NewValue=2*Math.PI*Math.sqrt(A*A*A/GM);
        if(Period!=NewValue) {Period=NewValue;changes++;if(verbose)System.out.println("T=f(A,GM)");}
        NewValue=2*Math.PI/Period;
        if(N!=NewValue) {N=NewValue;changes++;if(verbose)System.out.println("N=f(A,GM)");}
      }
      //GM
      if(!Double.isNaN(N) && !Double.isNaN(A) && Double.isNaN(GM)) {
        NewValue=A*A*A*N*N;
        if(GM!=NewValue) {GM=NewValue;changes++;if(verbose)System.out.println("GM=f(N,A)");}
      }
      //Tp and TnP
      if(Epoch!=null && !Double.isNaN(M) && !Double.isNaN(N)) {
        TimeUnit=(Epoch.getUnits()==TimeUnits.Days)?"day":"sec";
        NewTimeValue=Time.add(Epoch,-M/N);
        if(TP==null || !TP.equals(NewTimeValue)) {TP=NewTimeValue;changes++;if(verbose)System.out.println("Tp=f(M,N)");}
        NewTimeValue=Time.add(TP,Period);
        if(TNextP==null || !TNextP.equals(NewTimeValue)) {TNextP=NewTimeValue;changes++;if(verbose)System.out.println("Tnp=f(M,N)");}
      }
      //True to Mean Anomalies
      if(!Double.isNaN(TA) && !Double.isNaN(E)) {
        NewValue=TrueToEcc(TA,E);
        if(EA!=NewValue) {EA=NewValue;changes++;if(verbose)System.out.println("EA=f(TA,E)");}
        NewValue=EccToMean(EA,E);
        if(M!=NewValue) {M=NewValue;changes++;if(verbose)System.out.println("M=f(EA,E)");}
      } else //Only set one or the other
      //Mean to True Anomalies
      if(!Double.isNaN(M) && !Double.isNaN(E)) {
        NewValue=MeanToEcc(M,E);
        if(EA!=NewValue) {EA=NewValue;changes++;if(verbose)System.out.println("EA=f(M,E)");}
        NewValue=EccToTrue(EA,E);
        if(TA!=NewValue) {TA=NewValue;changes++;if(verbose)System.out.println("TA=f(EA,E)");}
      }
      count++;
    }
    U=(AP+TA)%(2*PI);
    LP=(LAN+AP)%(2*PI);
    L=(LP+M)%(2*PI);
  }
  private static double TLEEpochToJD(double epoch) {
    int year;
    double day;
    year = (int)(Math.floor(epoch*1E-3));
    if(year<57) {
      year=year+2000;
    } else {
      year=year+1900;
    }
    day=(epoch*1E-3-Math.floor(epoch*1E-3))*1E3;
    return JDofJan0(year) + day;
  } 
  private static double JDofJan0(double year) {
    // Astronomical Formulae for Calculators, Jean Meeus, pages 23-25
    // Calculate JD of 0.0 Jan year
    long A,B;
    year = year - 1;
    A = (int)Math.floor(year/100);
    B = 2 - A + (int)Math.floor(A/4);
    return Math.floor(365.25 * year) + Math.floor(30.6001 * 14) + 1720994.5 + B;
  } 
  public Elements() {
    //Fill everything with marker values so that FillInElements works right
    A=Double.NaN;
    E=Double.NaN;
    I=Double.NaN;
    LAN=Double.NaN;
    AP=Double.NaN;
    TP=null;            
    Periapse=Double.NaN;
    Apoapse=Double.NaN;
    Period=Double.NaN;
    N=Double.NaN;    
    TA=Double.NaN;
    EA=Double.NaN;
    FA=Double.NaN;
    M=Double.NaN;
    L=Double.NaN;
    U=Double.NaN;
    LP=Double.NaN;
    TNextP=null;   
    P=Double.NaN;  
    Epoch=null;    
    GM=Double.NaN; 
  }
  public Elements(MathState mathState, Time epoch1, double gm2, String string) {
    this(new MathStateTime(mathState,epoch1),gm2,string);
  }
}

