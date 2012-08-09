package org.kwansystems.space.ephemeris.neptune;

import static java.lang.Math.*;
import static org.kwansystems.space.Constants.*;

import org.kwansystems.space.ephemeris.*;
import org.kwansystems.space.kepler.*;
import org.kwansystems.tools.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeEpoch;
import org.kwansystems.tools.time.TimeScale;
import org.kwansystems.tools.time.TimeUnits;
import org.kwansystems.tools.vector.*;

public class NeptuneSatKepler extends KeplerEphemeris {
  private int body;
  public NeptuneSatKepler(int LBody) {
    body=LBody;
  }
  private static final double[][] NeptuneSatEle=new double[8][12];
  private static final int a=0;
  private static final int e=1;
  private static final int i=2;
  private static final int O=3;
  private static final int w=4;
  private static final int M=5;
  private static final int Od=6;
  private static final int wd=7;
  private static final int Md=8;
  private static final int epoch=9;
  private static final int alpha=10;
  private static final int delta=11;
  static {  //Triton
    double eswmO=0.218348542e-5;
    double ecwmO=-0.154364851e-4;
    double MpwmO=76.7243689;
    double ci2sO=+0.0281813825;
    double ci2cO=-0.2030103396;
    double dMdDpdwdDmdOdD=61.2572637;
    double dwdYmdOdY=0.382463540;
    double dOdY=0.523159764;
    double dwdDmdOdD=dwdYmdOdY/365.25;
    NeptuneSatEle[0][a]=354759146;
    NeptuneSatEle[0][e]=hypot(eswmO,ecwmO);
    double wmO=toDegrees(atan2(eswmO,ecwmO));
    NeptuneSatEle[0][M]=MpwmO-wmO;
    double ci2=hypot(ci2sO,ci2cO);
    NeptuneSatEle[0][O]=toDegrees(atan2(ci2sO,ci2cO));
    double i2=toDegrees(atan(1.0/ci2));
    NeptuneSatEle[0][i]=2*i2;
    NeptuneSatEle[0][w]=wmO+NeptuneSatEle[0][O];
    NeptuneSatEle[0][Od]=dOdY/365.25;
    NeptuneSatEle[0][wd]=dwdDmdOdD+NeptuneSatEle[0][Od];
    NeptuneSatEle[0][Md]=dMdDpdwdDmdOdD-dwdDmdOdD;
    NeptuneSatEle[0][epoch]=2447763.5;
    NeptuneSatEle[0][delta]=43.3189060;
    NeptuneSatEle[0][alpha]=298.947294;
  }
  static {  //Neried
    double eswpO=-0.724953400;
    double ecwpO=-0.196840797;
    double MpwpO=254.150289;
    double ti2sO=-0.0277252165;
    double ti2cO=+0.0567926008;
    double dMdDpdwdDpdOdD=0.999624080;
    double dwdCpdOdC=0.706322818;
    double dOdC=-3.91660963;
    double dwdDpdOdD=dwdCpdOdC/36525.0;
    NeptuneSatEle[1][a]=5513413256.0;
    NeptuneSatEle[1][e]=hypot(eswpO,ecwpO);
    double wpO=toDegrees(atan2(eswpO,ecwpO));
    NeptuneSatEle[1][M]=MpwpO-wpO;
    double ti2=hypot(ti2sO,ti2cO);
    NeptuneSatEle[1][O]=toDegrees(atan2(ti2sO,ti2cO));
    double i2=toDegrees(atan(ti2));
    NeptuneSatEle[1][i]=2*i2;
    NeptuneSatEle[1][w]=wpO-NeptuneSatEle[1][O];
    NeptuneSatEle[1][Od]=dOdC/36525.0;
    NeptuneSatEle[1][wd]=dwdDpdOdD-NeptuneSatEle[1][Od];
    NeptuneSatEle[1][Md]=dMdDpdwdDpdOdD-dwdDpdOdD;
    NeptuneSatEle[1][epoch]=2433680.5;
    NeptuneSatEle[1][delta]=69.1543762;
    NeptuneSatEle[1][alpha]=270.203984;
  }
  public static void setRow(int Lbody, double Lepoch, double La, double Lh, double Lk, double LL, double Lp, double Lq, double Lwd,double LLd,double LOd,double Lalpha,double Ldelta) {
    NeptuneSatEle[Lbody][a]=La;
    //h=e*sin(w+O)
    //k=e*cos(w+O)
    NeptuneSatEle[Lbody][e]=hypot(Lh,Lk);
    double wpO=toDegrees(atan2(Lh,Lk));
    //L=M+w+O;
    NeptuneSatEle[Lbody][M]=LL-wpO;
    //p=tan(i/2)*sin(O);
    //q=tan(i/2)*cos(O);
    double ti2=hypot(Lp,Lq);
    NeptuneSatEle[Lbody][O]=toDegrees(atan2(Lp,Lq));
    double i2=toDegrees(atan(ti2));
    NeptuneSatEle[Lbody][i]=2*i2;
    NeptuneSatEle[Lbody][w]=wpO-NeptuneSatEle[Lbody][O];
    //Lwd is in deg/sec
    NeptuneSatEle[Lbody][wd]=Lwd*86400.0;
    //LOd is in deg/sec
    NeptuneSatEle[Lbody][Od]=LOd*86400.0;
    //Lwd is in deg/sec
    NeptuneSatEle[Lbody][Md]=(LLd-Lwd-LOd)*86400.0;
    NeptuneSatEle[Lbody][epoch]=Lepoch;
    NeptuneSatEle[Lbody][alpha]=Lalpha;
    NeptuneSatEle[Lbody][delta]=Ldelta;
  }
  static {
    setRow(7, 
       /*Elements for Proteu at Julian Date: */ 2447757.0,
       /*Semi-major axis         */    117647108.3951678     ,//  m       
       /*h = esin(omega+node)    */    4.316469704955329E-04 ,//           
       /*k = ecos(omega+node)    */   -7.210801844660832E-05 ,//           
       /*mean longitude          */    213.6694288751446     ,//  deg      
       /*p = tan(i/2)sin(node)   */    1.711883968958595E-04 ,//           
       /*q = tan(i/2)cos(node)   */   -2.965561624132692E-04 ,//           
       /*apsidal rate            */    9.133585598101285E-07 ,//  deg/sec  
       /*mean longitude rate     */    3.712562763632274E-03 ,//  deg/sec  
       /*nodal rate              */   -8.950702955262804E-07 ,//  deg/sec  
       /*Reference plane pole RTA*/    298.7578382787378     ,//  deg
       /*Reference plane pole DEC*/    42.26923434586892      //  deg
     );

    setRow(6,
       /*Elements for Lariss at Julian Date: */2447757.0,
       /*Semi-major axis         */  73548331.24009847       ,//m       
       /*h = esin(omega+node)    */  6.945237904710838E-04   ,//         
       /*k = ecos(omega+node)    */   -1.198878746228356E-03 ,//           
       /*mean longitude          */    184.8281238454398     ,//  deg      
       /*p = tan(i/2)sin(node)   */    3.042139870660792E-04 ,//           
       /*q = tan(i/2)cos(node)   */    1.726074245672686E-03 ,//           
       /*apsidal rate            */    4.550153261802315E-06 ,//  deg/sec  
       /*mean longitude rate     */    7.512192673335218E-03 ,//  deg/sec  
       /*nodal rate              */   -4.539452511249425E-06 ,//  deg/sec  
       /*Reference plane pole RTA*/    298.8486977376166     ,//  deg
       /*Reference plane pole DEC*/    42.76431656177704      //  deg
     );

    setRow(5,
       /*Elements for Despin at Julian Date: */2447757.0, 
       /*Semi-major axis         */    52525945.22658495     ,//  m       
       /*h = esin(omega+node)    */    1.128043758916851E-04 ,//           
       /*k = ecos(omega+node)    */   -8.098417055716583E-05 ,//           
       /*mean longitude          */    85.27195918648782     ,//  deg      
       /*p = tan(i/2)sin(node)   */    2.452222625008457E-04 ,//           
       /*q = tan(i/2)cos(node)   */   -5.160964833839523E-04 ,//           
       /*apsidal rate            */    1.477732764822587E-05 ,//  deg/sec  
       /*mean longitude rate     */    1.245062680798036E-02 ,//  deg/sec  
       /*nodal rate              */   -1.475538975056945E-05 ,//  deg/sec  
       /*Reference plane pole RTA*/    298.8559427260777     ,//  deg
       /*Reference plane pole DEC*/    42.80345379429418      //  deg
     );

    setRow(4,
       /*Elements for Galate at Julian Date: */2447757.0,
       /*Semi-major axis         */    61952672.33239824     ,//  m       
       /*h = esin(omega+node)    */   -7.643954764882611E-05 ,//           
       /*k = ecos(omega+node)    */   -9.261893592328374E-05 ,//           
       /*mean longitude          */    46.64428546874267     ,//  deg      
       /*p = tan(i/2)sin(node)   */    4.399399135144454E-04 ,//           
       /*q = tan(i/2)cos(node)   */   -1.791773877212785E-04 ,//           
       /*apsidal rate            */    8.286590313969883E-06 ,//  deg/sec  
       /*mean longitude rate     */    9.718284359168671E-03 ,//  deg/sec  
       /*nodal rate              */   -8.273570255118341E-06 ,//  deg/sec  
       /*Reference plane pole RTA*/    298.8538628232021     ,//  deg
       /*Reference plane pole DEC*/    42.79222332505918      //  deg
     );

    setRow(3,
       /*Elements for Thalas at Julian Date: */2447757.0,
       /*Semi-major axis         */    50074551.97063884     ,//  m       
       /*h = esin(omega+node)    */    1.123719252646464E-04 ,//           
       /*k = ecos(omega+node)    */    1.079729369013819E-04 ,//           
       /*mean longitude          */    239.7371428952268     ,//  deg      
       /*p = tan(i/2)sin(node)   */    1.792404076448216E-03 ,//           
       /*q = tan(i/2)cos(node)   */    4.684855446979837E-06 ,//           
       /*apsidal rate            */    1.747529721870989E-05 ,//  deg/sec  
       /*mean longitude rate     */    1.337680047670611E-02 ,//  deg/sec  
       /*nodal rate              */   -1.744878934774183E-05 ,//  deg/sec  
       /*Reference plane pole RTA*/    298.8562812366566     ,//  deg
       /*Reference plane pole DEC*/    42.80528119838995      //  deg
     );

    setRow(2,
       /*Elements for Naiad  at Julian Date: */2447757.0,
       /*Semi-major axis         */    48227304.88030718     ,//  m       
       /*h = esin(omega+node)    */    3.264408052171053E-04 ,//           
       /*k = ecos(omega+node)    */    2.676346632678553E-05 ,//           
       /*mean longitude          */    60.26044459305379     ,//  deg      
       /*p = tan(i/2)sin(node)   */    3.106248110850256E-02 ,//           
       /*q = tan(i/2)cos(node)   */    2.732694604457570E-02 ,//           
       /*apsidal rate            */    1.966548948527533E-05 ,//  deg/sec  
       /*mean longitude rate     */    1.415328843692009E-02 ,//  deg/sec  
       /*nodal rate              */   -1.983882754543970E-05 ,//  deg/sec  
       /*Reference plane pole RTA*/    298.8564946497789     ,//  deg
       /*Reference plane pole DEC*/    42.80643322459633      //  deg
     );
  }
  public Elements CalcElements(Time TT) {
    double D=TT.get(TimeUnits.Days,TimeScale.TDB,TimeEpoch.JD)-NeptuneSatEle[body][epoch];
    Elements E=new Elements();
    E.Epoch=TT;
    E.Epoch.Units=TimeUnits.Seconds;
    E.A=NeptuneSatEle[body][a];
    E.E=NeptuneSatEle[body][e];
    E.I=toRadians(NeptuneSatEle[body][i]);
    E.LAN=toRadians(NeptuneSatEle[body][O]+NeptuneSatEle[body][Od]*D);
    E.AP=toRadians(NeptuneSatEle[body][w]+NeptuneSatEle[body][wd]*D);
    E.M=toRadians(NeptuneSatEle[body][M]+NeptuneSatEle[body][Md]*D);
    E.N=toRadians(NeptuneSatEle[body][Md]/86400.0);
    E.LengthUnit="m";
    E.TimeUnit="sec";
    E.FillInElements();
    return E;
  }
  public MathState RotateState(Time T, MathState S) {
    S=MathMatrix.Rot1d(-(90-NeptuneSatEle[body][delta])).transform(S);
    S=MathMatrix.Rot3d(-(NeptuneSatEle[body][alpha]+90)).transform(S);
    S=ToJ2000Ecl().transform(S);
    return S;
  }
  private static final double[][] TestR= new double[][] {{  302505012.402645880,   75419994.387407661, -169136137.125737224},
                                                         {-2478165523.58444727,  4458349933.95644603, 2136966644.24202155}};
  private static final double[][] TestV= new double[][] {{-1446.05178086895959, -2147.69591843686046, -3543.89348775308512},
                                                         {- 275.068314614426190,- 948.661370848684769,- 506.809954964708628}};
  private static final MathState[] Test= new MathState[] {new MathState(new MathVector(TestR[0]),new MathVector(TestV[0])),
                                                          new MathState(new MathVector(TestR[1]),new MathVector(TestV[1]))};
  private Rotator ToJ2000Equ() {
    return B1950toJ2000;
  }
  private Rotator ToJ2000Ecl() {
    return ToJ2000Equ().combine(MathMatrix.Rot1(epsJ2000R));
  }
  public static final Ephemeris[] satArray={null,
    new NeptuneSatKepler(0),
    new NeptuneSatKepler(1),
    new NeptuneSatKepler(2),
    new NeptuneSatKepler(3),
    new NeptuneSatKepler(4),
    new NeptuneSatKepler(5),
    new NeptuneSatKepler(6),
    new NeptuneSatKepler(7)
  };
  public static final double[] satMassRatio={6836534.87889192492e9,1427.86794914113370e9,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
  public static void main(String[] args) {
    System.out.println(new NeptuneSatKepler(0).CalcState(new Time(2447680.5,TimeUnits.Days,TimeScale.TDB,TimeEpoch.JD)));
    System.out.println(Test[0]);
    System.out.println("eswpO: "+NeptuneSatEle[1][e]*sin(toRadians(NeptuneSatEle[1][w]+NeptuneSatEle[1][O])));
    System.out.println("ecwpO: "+NeptuneSatEle[1][e]*cos(toRadians(NeptuneSatEle[1][w]+NeptuneSatEle[1][O])));
    System.out.println("MpwpO: "+(NeptuneSatEle[1][M]+NeptuneSatEle[1][w]+NeptuneSatEle[1][O]));
    System.out.println("ti2sO: "+tan(toRadians(NeptuneSatEle[1][i])/2)*sin(toRadians(NeptuneSatEle[1][O])));
    System.out.println("ti2cO: "+tan(toRadians(NeptuneSatEle[1][i])/2)*cos(toRadians(NeptuneSatEle[1][O])));
    System.out.println("MdpwdpOd: "+(NeptuneSatEle[1][Md]+NeptuneSatEle[1][wd]+NeptuneSatEle[1][Od]));
    System.out.println("wdpOd: "+((NeptuneSatEle[1][wd]+NeptuneSatEle[1][Od])*36525.0));
    System.out.println("Od: "+((NeptuneSatEle[1][Od])*36525.0));
    System.out.println(new NeptuneSatKepler(1).CalcState(new Time(2447680.5,TimeUnits.Days,TimeScale.TDB,TimeEpoch.JD)));
    System.out.println(Test[1]);
  }
}
