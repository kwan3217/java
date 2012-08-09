package org.kwansystems.space.ephemeris.saturn;

import org.kwansystems.space.ephemeris.Ephemeris;
import org.kwansystems.tools.rotation.MathMatrix;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeScale;
import org.kwansystems.tools.time.TimeUnits;
import org.kwansystems.tools.vector.*;

import static org.kwansystems.space.Constants.*;

/** Ephemeris of the first 8 satellites of Saturn.
 * <p>
 * <hr>
 * <center>
                         IMCCE - Observatoire de Paris
<p>
                                       and
<p>
                 UNIVERSITE DES SCIENCES ET TECHNOLOGIES DE LILLE
<p>
                         <a href="ftp://ftp.imcce.fr/pub/ephem/satel/tass17/">/pub/ephem/satel/tass17/</a>
<p>
                                1996, november 14
<hr>
</center


TASS 1.7    :  New version of the TASS theory of the Saturnian satellites 
                        including HYPERION  (5 Nov 1996)
<p>
  
AUTHORS :   Alain VIENNE et Luc DURIEZ

E_MAIL:     VIENNE@GAT.UNIV-LILLE1.FR<br>
            DURIEZ@GAT.UNIV-LILLE1.FR
<p>

ADDRESS :   UNIVERSITE DES SCIENCES ET TECHNOLOGIES DE LILLE<br>
            Laboratoire d'Astronomie<br>
            1 Impasse de l'Observatoire<br>
            F-59000  LILLE<br>
            FRANCE            <br>    
<p>            
CONTENTS:<br>   
            tass17.f: 
<p>
            FORTRAN source code for ephemerides issued from 
            the new theory of high precision of 
            the motion of the main Saturnian satellites TASS 1.6.
            (1995, A&A 297, 588-605)
            and 'Theory of motion and ephemerides of Hyperion', 
            (to appear in A&A)  
<p>
<hr>
    Positions and velocities of the satellites Mimas, Enceladus, Tethys,
    Dione, Rhea, Titan, Hyperion and Iapetus referred to the center of
    Saturn and to the mean ecliptic and mean equinox for J2000.0 epoch
<hr>
   Installation of TASS:
 
   Split this mail in order to put the part 'SUBROUTINES' in a file 
   called for example 'POSIRED.FOR' and the part 'SERIES' in a file
   called for example 'redtass7.dat'
   Type a FORTRAN program to monitor the computations. An example of
   such a monitor is given just below with the corresponding results

<hr>
   The present version has been checked on a Work Station:
   IBM RISC 6000 mod. 360 ( AIX 3.2.5 & X11R5 1.2.4 )
<p>
<hr>
User feed-back is encouraged. Unless otherwise specified, send comments and bug 
reports to:                  <br>E-mail     : comments@imcce.fr<br>
                               Fax        : (33) 1 46 33 28 34 <br>
                               Postal mail: IMCCE - Observatoire de Paris<br>
                                            77 avenue Denfert Rochereau<br>
                                            F-75014 PARIS<br>
<hr>
 */
public class SaturnSatTASS17 extends Ephemeris {
  int SatIndex; 
  public SaturnSatTASS17(int LSatNumber) {
    SatIndex=LSatNumber;
  }
  public static final Ephemeris[] satArray;
  public static final double[] satMassRatio;
  private void TestTheory() {
    for(int i=0;i<3;i++){
      System.out.println(SatIndex+" "+TestPoints[SatIndex][i]);
      MathState State=CalcState(TestPoints[SatIndex][i]);
      MathState Test=TestVector[SatIndex][i];
      Test=new MathState(Test.R().mul(MPerAU),Test.V().mul(MPerAU/(365.25*86400)));
      System.out.println("Calc:  "+State);
      System.out.println("Given: "+Test);
      System.out.println("Dist:  "+MathVector.sub(State.R(),Test.R()).length());
    }
  }
  
  private static final double[][] TestPoints={
    {2421677.4,2441692.3,2445106.3},
    {2406147.5,2441699.9,2444714.0},
    {2409977.4,2432270.9,2445814.0},
    {2406477.5,2441257.8,2445820.6},
    {2405824.5,2432236.8,2445814.0},
    {2440512.6,2443569.3,2445061.3},
    {2406327.6,2443128.7,2445720.1},
    {2406216.6,2443179.7,2445815.1}
  };
  
  private static final MathState TestVector[][]={
    {
      new MathState(-0.001198576889,-0.000209825460, 0.000186837157, 0.723936877,-2.640848972, 1.326181365),
      new MathState( 0.000712578033, 0.000859971416,-0.000531517583,-2.484251043, 1.648703630,-0.530043564),
      new MathState( 0.000547084626,-0.000975427527, 0.000482000988, 2.733925772, 1.161445219,-0.795906708)
    },{
      new MathState(-0.000317458743, 0.001387123315,-0.000695569370,-2.613864221,-0.369066452, 0.446037406),
      new MathState( 0.000579459700,-0.001333229353, 0.000642772742, 2.478333563, 0.755925149,-0.635459090),
      new MathState(-0.001545509544,-0.000227991080, 0.000269442851, 0.542236131,-2.341439652, 1.174056665)
    },{
      new MathState( 0.001169171754,-0.001455040893, 0.000629464516, 1.917976546, 1.207348531,-0.772706551),
      new MathState( 0.001698565647, 0.000819192814,-0.000569994295,-1.201733676, 1.863569977,-0.902946376),
      new MathState( 0.001735637114, 0.000724948623,-0.000583496151,-1.103876639, 1.935135994,-0.879143230)
    },{
      new MathState(-0.001139424157, 0.002032365984,-0.000955456204,-1.880203521,-0.779545669, 0.590410817),
      new MathState( 0.001205300980, 0.001916811073,-0.001121879072,-1.848838100, 0.967081356,-0.327308693),
      new MathState(-0.000815929288, 0.002148543858,-0.001046990497,-1.990373129,-0.531066556, 0.471414144)
    },{
      new MathState( 0.000255016488,-0.003124357779, 0.001600422912, 1.781208363, 0.049114113,-0.186597578),
      new MathState(-0.003088985614, 0.001606315240,-0.000556845075,-0.849478705,-1.363239443, 0.786547570),
      new MathState( 0.002659297810,-0.002133842283, 0.000874732902, 1.164878536, 1.160534785,-0.711114746)
    },{
      new MathState(-0.008312968003, 0.000590507681, 0.000496831020,-0.019590068,-1.020463815, 0.529324054),
      new MathState( 0.002230925308, 0.006960382659,-0.003815277704,-1.107451823, 0.352122210,-0.073973367),
      new MathState( 0.000468338437,-0.007131027063, 0.003639972122, 1.188582242, 0.038910632,-0.135883060)
    },{
      new MathState( 0.010080312859,-0.003940249380, 0.001120175589, 0.378238813, 0.783156263,-0.425574501),
      new MathState(-0.007346861684,-0.006637199805, 0.004222806472, 0.690190140,-0.643116009, 0.255457813),
      new MathState(-0.009125543940, 0.005859877071,-0.001998155741,-0.489992916,-0.704007020, 0.411842188)
    },{
      new MathState( 0.002447343471, 0.022675031476,-0.006665246397,-0.671393700, 0.121862318, 0.098725209),
      new MathState( 0.011497900148, 0.019131194536,-0.006986500915,-0.586947998, 0.381227333, 0.025604377),
      new MathState(-0.017991225601, 0.016226098290,-0.000297129845,-0.445084401,-0.467243031, 0.201864213)
    }
  };
  
  public static void main(String[] args) {
    for(int i=1;i<=8;i++) {
//      MathState State=((SaturnSatTASS17)satArray[i]).CalcState(2452841+(49.0/72.0));
//      System.out.println(State.R());
      ((SaturnSatTASS17)satArray[i]).TestTheory();
    }
  }
  
  public static void PrintNumber(String label,double Number) {
    System.out.println(label+Number);
  }
  
  public MathState CalcState(Time TT) {
    return CalcState(TT.get(TimeUnits.Days,TimeScale.TDB));
  }
  public MathState CalcState(double JD) {
    //    saturnocentric position and velocity J2000 (JD=Julian Date)
    //    position (in km) and velocity (in km/s),
    //    referred to the center of Saturn
    //    and to the mean Equinox and Ecliptic J2000
    //    Time scale is not specified, but believed to be TDB.

    //'      IMPLICIT DOUBLE PRECISION (A-H,O-Z)
    double[] Elem=new double[NumCEles];/* As Double*/
    double[] MeanLongitudes=new double[NumSats];/* As Double*/
    //  PrintNumber("JD: ",JD)
    if (SatIndex == Hyperion) {
      ELEMHYP(JD, Elem);
    } else {
      CalcLongitudes(JD, MeanLongitudes);
      CalcElements(JD, Elem, MeanLongitudes);
    }
    MathState result=ElementsToState(Elem);
    result=result.replaceR(result.R().mul(MPerAU));
    return result.replaceV(result.V().mul(MPerAU/(365.25*86400)));
  }
  private void CalcLongitudes(double JD,double[] MeanLongitudes) {
    //'   IMPLICIT DOUBLE PRECSatIndexION (A-H,O-Z)
    double S, T;
    int OrderNumber,SatI;
    //'COMMON /TASERIES/ SERIES,NumTableRows,AL0,AN0,IKS
    //PrintNumber("JD: ",JD);
    T = (JD - 2444240.0) / 365.25;
    //PrintNumber("T: ",T);
    SatI=FirstSat;
    while(SatI<=LastSat) {
      if (SatI != Hyperion) {
        S = 0;
        OrderNumber=Base;
        //PrintNumber("NumTableRowsShort: ",(NumTableRows[SatI][EleLambdaShort] - Offset));
        while(OrderNumber<=(NumTableRows[SatI][EleLambdaShort] - Offset)) {
          double Amp=Series[SatI][EleLambda][OrderNumber][ArgAmp];
          double Pha=Series[SatI][EleLambda][OrderNumber][ArgPha];
          double Frq=Series[SatI][EleLambda][OrderNumber][ArgFrq];
          //System.out.println(OrderNumber+": S="+S+"+"+Amp+"*sin("+Pha+"+"+T+"*"+Frq+")");
          S = S + Amp * Math.sin(Pha+T*Frq);
          OrderNumber=OrderNumber+1;
          //PrintNumber("Cont: ",(OrderNumber<=(NumTableRows[SatI][EleLambdaShort] - Offset))?1:0);
        }
        MeanLongitudes[SatI] = S;
      } else {
        MeanLongitudes[SatI] = 0;
      }
      
      //PrintNumber("MeanLongitudes["+SatI+"]: ",MeanLongitudes[SatIndex]);
      SatI=SatI+1;
    }
  }
  public void CalcElements(double JD,double[] Elem,double[] MeanLongitudes) {
    //  'IMPLICIT DOUBLE PRECSatIndexION (A-H,O-Z)
    //  'Dim ELEM(EleO)
    double S, Phas;
    int OrderNumber, Coeff;
    double T;
    double CS;
    double SN;
    double S1;
    double S2;
    //  'COMMON /TASERIES/ SERIES,NumTableRows,AL0,AN0,IKS
    T = (JD - 2444240.0) / 365.25;
    //Calculate P series
    S = 0;
    OrderNumber=Base;
    while(OrderNumber<=NumTableRows[SatIndex][EleP] - Offset) {
      Phas = Series[SatIndex][EleP][OrderNumber][ArgPha];
      Coeff=FirstSat;
      while(Coeff<=LastSat) {
        Phas = Phas + Series[SatIndex][EleP][OrderNumber][ArgCof + Coeff] * MeanLongitudes[Coeff];
        Coeff=Coeff+1;
      }
      S = S + Series[SatIndex][EleP][OrderNumber][ArgAmp] * Math.cos(Phas + T * Series[SatIndex][EleP][OrderNumber][ArgFrq]);
      OrderNumber=OrderNumber+1;
    }
    Elem[CEleN] = S;
    //    PrintNumber("Elem[CEleN]: ",Elem[CEleN]);
    //Calculate Lambda series
    //This includes a clever shortcut. Since MeanLongitudes() already sums the terms up to the short cutoff, just sum the ones after it.
    S = MeanLongitudes[SatIndex] + AL0[SatIndex];
    OrderNumber=NumTableRows[SatIndex][EleLambdaShort] + 1 - Offset;
    while(OrderNumber<=NumTableRows[SatIndex][EleLambda] - Offset) {
      Phas = Series[SatIndex][EleLambda][OrderNumber][ArgPha];
      Coeff=FirstSat;
      while(Coeff<=LastSat) {
        Phas = Phas + Series[SatIndex][EleLambda][OrderNumber][ArgCof + Coeff] * MeanLongitudes[Coeff];
        Coeff=Coeff+1;
      }
      S = S + Series[SatIndex][EleLambda][OrderNumber][ArgAmp] * Math.sin(Phas + T * Series[SatIndex][EleLambda][OrderNumber][ArgFrq]);
      OrderNumber=OrderNumber+1;
    }
    S = S + AN0[SatIndex] * T;
    CS = Math.cos(S);
    SN = Math.sin(S);
    Elem[CEleQ] = Math.atan2(SN, CS);
    //    PrintNumber("Elem[CEleQ]: ",Elem[CEleQ]);
    //Calculate Z series
    S1 = 0;
    S2 = 0;
    OrderNumber=Base;
    while(OrderNumber<=NumTableRows[SatIndex][EleZ] - Offset) {
      Phas = Series[SatIndex][EleZ][OrderNumber][ArgPha];
      Coeff=FirstSat;
      while(Coeff<=LastSat) {
        Phas = Phas + Series[SatIndex][EleZ][OrderNumber][ArgCof + Coeff] * MeanLongitudes[Coeff];
        Coeff=Coeff+1;
      }
      S1 = S1 + Series[SatIndex][EleZ][OrderNumber][ArgAmp] * Math.cos(Phas + T * Series[SatIndex][EleZ][OrderNumber][ArgFrq]);
      S2 = S2 + Series[SatIndex][EleZ][OrderNumber][ArgAmp] * Math.sin(Phas + T * Series[SatIndex][EleZ][OrderNumber][ArgFrq]);
      OrderNumber=OrderNumber+1;
    }
    Elem[CEleE] = S1;
    //    PrintNumber("Elem[CEleE]: ",Elem[CEleE]);
    Elem[CEleW] = S2;
    //    PrintNumber("Elem[CEleW]: ",Elem[CEleW]);
    //Calculate Zeta series
    S1 = 0;
    S2 = 0;
    OrderNumber=Base;
    while(OrderNumber<=NumTableRows[SatIndex][EleZeta] - Offset) {
      Phas = Series[SatIndex][EleZeta][OrderNumber][ArgPha];
      Coeff=FirstSat;
      while(Coeff<=LastSat) {
        Phas = Phas + Series[SatIndex][EleZeta][OrderNumber][ArgCof + Coeff] * MeanLongitudes[Coeff];
        Coeff=Coeff+1;
      }
      S1 = S1 + Series[SatIndex][EleZeta][OrderNumber][ArgAmp] * Math.cos(Phas + T * Series[SatIndex][EleZeta][OrderNumber][ArgFrq]);
      S2 = S2 + Series[SatIndex][EleZeta][OrderNumber][ArgAmp] * Math.sin(Phas + T * Series[SatIndex][EleZeta][OrderNumber][ArgFrq]);
      OrderNumber=OrderNumber+1;
    }
    Elem[CEleI] = S1;
    //    PrintNumber("Elem[CEleI]: ",Elem[CEleI]);
    Elem[CEleO] = S2;
    //    PrintNumber("Elem[CEleO]: ",Elem[CEleO]);
  }
  public MathState ElementsToState(double[] Elem) {
    //IMPLICIT DOUBLE PRECSatIndexION (A-H,O-Z)
    double EPS = 0.0000000001;
    MathVector R2, V2;
    double AMO, RMU, DGA, RL, RK, RH, FLE;
    double CF, SF, CORF, DLF, RSAM1;
    double ASR, PHI, PSI, X1, Y1;
    double VX1, VY1, DWHO, RTP, RTQ, RDG;
    double CI, SI, CO, SO;
    //'COMMON /MYRD/  AAM
    //'COMMON /EDRE/ AIA,OMA,TMAS,GK1
    AMO = AAM[SatIndex] * (1 + Elem[CEleN]);
    //    PrintNumber("AMO: ",AMO);
    //      PrintNumber("AMO: ",AMO)
    RMU = GK1 * (1 + TMAS[SatIndex]);
    //    PrintNumber("RMU: ",RMU);
    DGA = Math.pow((RMU / (AMO * AMO)),(1.0/3.0));
    //    PrintNumber("DGA: ",DGA);
    RL = Elem[CEleQ];
    RK = Elem[CEleE];
    RH = Elem[CEleW];
    FLE = RL - RK * Math.sin(RL) + RH * Math.cos(RL);
    //    PrintNumber("FLE: ",FLE);
    CORF=1;
    while(Math.abs(CORF)>EPS) {
      CF = Math.cos(FLE);
      SF = Math.sin(FLE);
      CORF = (RL - FLE + RK * SF - RH * CF) / (1 - RK * CF - RH * SF);
      FLE = FLE + CORF;
    }
    CF = Math.cos(FLE);
    SF = Math.sin(FLE);
    DLF = -RK * SF + RH * CF;
    RSAM1 = -RK * CF - RH * SF;
    ASR = 1 / (1 + RSAM1);
    PHI = Math.sqrt(1 - RK * RK - RH * RH);
    PSI = 1 / (1 + PHI);
    X1 = DGA * (CF - RK - PSI * RH * DLF);
    Y1 = DGA * (SF - RH + PSI * RK * DLF);
    VX1 = AMO * ASR * DGA * (-SF - PSI * RH * RSAM1);
    VY1 = AMO * ASR * DGA * (CF + PSI * RK * RSAM1);
    DWHO = 2 * Math.sqrt(1 - Elem[CEleO] * Elem[CEleO] - Elem[CEleI] * Elem[CEleI]);
    RTP = 1 - 2 * Elem[CEleO] * Elem[CEleO];
    RTQ = 1 - 2 * Elem[CEleI] * Elem[CEleI];
    RDG = 2 * Elem[CEleO] * Elem[CEleI];
    //Coordinates in Saturn Equatorial plane and node
    R2 = new MathVector(X1 * RTP + Y1 * RDG,
    X1 * RDG + Y1 * RTQ,
    (-X1 * Elem[CEleO] + Y1 * Elem[CEleI]) * DWHO);
    //    System.out.println("R2: "+R2);
    V2 = new MathVector(VX1 * RTP + VY1 * RDG,
    VX1 * RDG + VY1 * RTQ,
    (-VX1 * Elem[CEleO] + VY1 * Elem[CEleI]) * DWHO);
    //    System.out.println("V2: "+V2);
    //Rotate to J2000
    CI = Math.cos(AIA);
    SI = Math.sin(AIA);
    CO = Math.cos(OMA);
    SO = Math.sin(OMA);
    
    MathMatrix M2=MathMatrix.mul(MathMatrix.Rot3(-OMA),MathMatrix.Rot1(-AIA));
    MathVector R =M2.transform(R2);
    MathVector V =M2.transform(V2);
    return new MathState(R,V);
  }
  private void ELEMHYP(double DJ,double[] Elem) {
    //'IMPLICIT REAL*8(A-H,O-Z)
    //      COMMON /SERHYP/NBTP,NBTQ,NBTZ,NBTZT,T0,CSTP,CSTQ,AMM7,
    //     &               SERP,FAP,FRP,
    //     &               SERQ,FAQ,FRQ,
    //     &               SERZ,FAZ,FRZ,
    //     &               SERZT,FAZT,FRZT
    double T, P;
    int I;
    double WT, Q;
    double ZR, ZI;
    double ZTR, ZTI, VL;
    T = DJ - T0Hyperion;
    
    P = CSTP;
    I=Base;
    while(I<=NBTP-Offset) {
      WT = T * PTable[I][FR] + PTable[I][FA];
      P = P + PTable[I][SER] * Math.cos(WT);
      I=I+1;
    }
    
    Q = CSTQ;
    I=Base;
    while(I<=NBTQ-Offset) {
      WT = T * QTable[I][FR] + QTable[I][FA];
      Q = Q + QTable[I][SER] * Math.sin(WT);
      I=I+1;
    }
    
    ZR = 0;
    ZI = 0;
    I=Base;
    while(I<=NBTZ-Offset) {
      WT = T * ZTable[I][FR] + ZTable[I][FA];
      ZR = ZR + ZTable[I][SER] * Math.cos(WT);
      ZI = ZI + ZTable[I][SER] * Math.sin(WT);
      I=I+1;
    }
    
    ZTR = 0;
    ZTI = 0;
    I=Base;
    while(I<=NBTZT-Offset) {
      WT = T * ZTTable[I][FR] + ZTTable[I][FA];
      ZTR = ZTR + ZTTable[I][SER] * Math.cos(WT);
      ZTI = ZTI + ZTTable[I][SER] * Math.sin(WT);
      I=I+1;
    }
    
    VL = mod(AMM7 * T + Q, PI2);
    if(VL < 0) {
      VL = VL + PI2;
    }
    Elem[CEleN] = P;
    Elem[CEleQ] = VL;
    Elem[CEleE] = ZR;
    Elem[CEleW] = ZI;
    Elem[CEleI] = ZTR;
    Elem[CEleO] = ZTI;
  }
  private static double mod(double X,double Y) {
    double Frac=(X/Y)-Math.floor(X/Y);
    return Frac*Y;
  }
  //General constants
  static private final int Base = 0;
  static private final int Offset = 1 - Base;
  static private final double PI2 = 2 * Math.PI;
  //Table Elements
  static private final int EleP = 0 + Base;
  static private final int EleLambda = 1 + Base;
  static private final int EleZ = 2 + Base;
  static private final int EleZeta = 3 + Base;
  static private final int EleLambdaShort = 4 + Base; //For NumTableRows only
  //Classical Elements
  static private final int CEleN = 0 + Base;
  static private final int CEleQ = 1 + Base;
  static private final int CEleE = 2 + Base;
  static private final int CEleW = 3 + Base;
  static private final int CEleI = 4 + Base;
  static private final int CEleO = 5 + Base;
  static private final int NumCEles = 6;
  //Table Arguments
  static private final int ArgAmp = 0 + Base;
  static private final int ArgPha = 1 + Base;
  static private final int ArgFrq = 2 + Base;
  static private final int ArgCof = 3; //Add to coefficient you want from old IKS table
  static private final int Mimas = 0 + Base;
  static private final int Hyperion = 6 + Base; //Odd man out satellite
  static private final int Japetus = 7 + Base;
  static private final int FirstSat = Mimas;
  static private final int LastSat = Japetus;
  static private final int NumSats = 8;
  
  /*Data for satellites 601 Mimas
                        602 Enceladus
                        063 Tethys
                        604 Dione
                        605 Rhea
                        606 Titan
                        608 Japetus */
  static final private double GK=0.01720209895E0;
  static final private double TAS=3498.790E0;
  static final private double GK1=Math.pow((GK*365.25),2)/TAS;
  static final private double AIA=Math.toRadians(28.0512E0);
  static final private double OMA=Math.toRadians(169.5291E0);
  static final private double[] TAM={0.1577287066246E+08,
  0.6666666666667E+07,
  0.9433962264151E+06,
  0.5094243504840E+06,
  0.2314814814815E+06,
  0.4225863977890E+04,
  0.3333333333333E+08,
  0.3225806451613E+06,
  0.2858130953844E-03};
  static final private double[] AM={0.6667061728782E+01,
  0.4585536751534E+01,
  0.3328306445055E+01,
  0.2295717646433E+01,
  0.1390853715957E+01,
  0.3940425676910E+00,
  0.2953088138695E+00,
  0.7920197763193E-01,
  0.5839811452566E-03};
  static final private double[] TMAS=new double[9];
  static final private double[] AAM=new double[9]; {
    int I=0;
    TMAS[0]=0;
    AAM[0]=0;
    while(I<9) {
      TMAS[I]=1/TAM[I];
      AAM[I] = AM[I]*365.25;
      I++;
    }
    SetupTable1();
    SetupTable2();
    SetupTable3();
    SetupTable4();
    SetupTable5();
    SetupTable6();
    //   SetupTable7();
    SetupTable8();
    Series=new double[][][][]{
      {Series11,Series12,Series13,Series14},
      {Series21,Series22,Series23,Series24},
      {Series31,Series32,Series33,Series34},
      {Series41,Series42,Series43,Series44},
      {Series51,Series52,Series53,Series54},
      {Series61,Series62,Series63,Series64},
      null,
      {Series81,Series82,Series83,Series84}
    };
  }
  static private double[][][][] Series;
  static final private double[] AL0= {0.1822484926062486E+00,
  0.7997716657090215E+00,
  0.5239109365414447E+01,
  0.1994592585279060E+01,
  0.6221340947932125E+01,
  0.4936792168079816E+01,
  0,//7 uses a different model. There is no 7
  0.1661250302251527E+00
  };
  static final private double[] AN0= {0.2435144296437475E+04,
  0.1674867298497696E+04,
  0.1215663929056177E+04,
  0.8385108703595477E+03,
  0.5080093197533360E+03,
  0.1439240478491399E+03,
  0,//7 uses a different model. There is no 7
  0.2892852233006167E+02
  };
  static final private int[][] NumTableRows={{  8, 35, 38, 32, 29},
  {  3, 12, 13,  5,  3},
  {  4, 40, 23, 34, 28},
  { 10, 19, 21,  9,  3},
  { 11, 27, 21,  9, 11},
  {  7, 36, 35, 22, 18},
  null,  //Man, why do you have to be so contrary, Hyperion?
  {100,241,184, 80, 22}};
  
  static private double[][] Series11;
  static private double[][] Series12;
  static private double[][] Series13;
  static private double[][] Series14;
  
  static private double[][] Series21;
  static private double[][] Series22;
  static private double[][] Series23;
  static private double[][] Series24;
  
  static private double[][] Series31;
  static private double[][] Series32;
  static private double[][] Series33;
  static private double[][] Series34;
  
  static private double[][] Series41;
  static private double[][] Series42;
  static private double[][] Series43;
  static private double[][] Series44;
  
  static private double[][] Series51;
  static private double[][] Series52;
  static private double[][] Series53;
  static private double[][] Series54;
  
  static private double[][] Series61;
  static private double[][] Series62;
  static private double[][] Series63;
  static private double[][] Series64;
  
  static private double[][] Series81;
  static private double[][] Series82;
  static private double[][] Series83;
  static private double[][] Series84;
  
  private static void SetupTable1() {
    Series11= new double[][] {
      {0.5196910356411064E-02,0.3141592653589793E+01,0.0000000000000000E+00,0,0,0,0,0,0,0,0},
      {0.2760763800054540E-04,0.6863463000141887E+00,0.8904537688864059E-01,0,0,0,0,0,0,0,0},
      {0.9347313993693880E-05,0.2209688858348459E+01,0.1019765304355295E+02,0,0,0,0,0,0,0,0},
      {0.1247132122206555E-03,0.3384553386830940E+01,0.2428763081719043E+04,2,0,-2,0,0,0,0,0},
      {0.5773002078925660E-04,0.9293070332553311E+00,0.2428852127095932E+04,2,0,-2,0,0,0,0,0},
      {0.5767710349652186E-04,0.2698207086816744E+01,0.2428674036342154E+04,2,0,-2,0,0,0,0,0},
      {0.1211438560239529E-04,0.2011860786802310E+01,0.2428584990965266E+04,2,0,-2,0,0,0,0,0},
      {0.1201719293977645E-04,0.4757245986859100E+01,0.2428941172472820E+04,2,0,-2,0,0,0,0,0}
    };
    Series12= new double[][] {
      {0.7574073228630605E+00,0.6863463000141887E+00,0.8904537688864059E-01,0,0,0,0,0,0,0,0},
      {0.1243299165183161E-01,0.2059038900042566E+01,0.2671361306659218E+00,0,0,0,0,0,0,0,0},
      {0.2266397525680950E-02,0.2209688858348459E+01,0.1019765304355295E+02,0,0,0,0,0,0,0,0},
      {0.1059900648786554E-02,0.4664935211924063E+01,0.1010860766666431E+02,0,0,0,0,0,0,0,0},
      {0.1022783782146454E-02,0.2896035158362648E+01,0.1028669842044159E+02,0,0,0,0,0,0,0,0},
      {0.7266042399548720E-03,0.1372692600028377E+01,0.1780907537772812E+00,0,0,0,0,0,0,0,0},
      {0.5060827138159436E-03,0.4533623190990083E+01,0.5765338167707570E-01,0,0,0,0,0,0,0,0},
      {0.3590283311520520E-03,0.3431731500070944E+01,0.4452268844432029E+00,0,0,0,0,0,0,0,0},
      {0.2628008938901560E-03,0.5445714149319756E-01,0.6492496159975472E-01,0,0,0,0,0,0,0,0},
      {0.2459408712169538E-03,0.3122254716217880E+01,0.1204373721002055E+00,0,0,0,0,0,0,0,0},
      {0.2237171274873740E-03,0.8369962583200818E+00,0.1001956228977567E+02,0,0,0,0,0,0,0,0},
      {0.2097481794022580E-03,0.3582381458376836E+01,0.1037574379733023E+02,0,0,0,0,0,0,0,0},
      {0.1969761970496516E-03,0.4459828112124973E+01,0.1131657921775265E+00,0,0,0,0,0,0,0,0},
      {0.1276023939118606E-03,0.5446831688805226E+01,0.5021841352532276E+01,0,0,0,0,0,0,0,0},
      {0.1163953693117700E-03,0.3677931635243811E+01,0.5199932106309558E+01,0,0,0,0,0,0,0,0},
      {0.9230818991264319E-04,0.2435908416203692E+01,0.3139199521156488E-01,0,0,0,0,0,0,0,0},
      {0.7345436389839680E-04,0.4239927673886392E+01,0.3866357513424390E-01,0,0,0,0,0,0,0,0},
      {0.4870003920011340E-04,0.3292242611895686E+01,0.9930516912887027E+01,0,0,0,0,0,0,0,0},
      {0.4473029935270700E-04,0.4268727758391025E+01,0.1046478917421887E+02,0,0,0,0,0,0,0,0},
      {0.2799611810201398E-04,0.5219969491004272E+01,0.1466987585657163E+00,0,0,0,0,0,0,0,0},
      {0.1317556115144799E-04,0.2490365557696890E+01,0.9631695681131960E-01,0,0,0,0,0,0,0,0},
      {0.1284412109690001E-04,0.2764723137428667E+01,0.2357441354543569E+00,0,0,0,0,0,0,0,0},
      {0.1096551493377600E-04,0.2023919695921281E+01,0.8177379696596157E-01,0,0,0,0,0,0,0,0},
      {0.9505947727365781E-05,0.3697269572615722E+01,0.5079494734209352E+01,0,0,0,0,0,0,0,0},
      {0.9283150504689616E-05,0.1804019257682701E+01,0.7271579922679017E-02,0,0,0,0,0,0,0,0},
      {0.9274943256038984E-05,0.6670083626422765E+00,0.2094827489888461E+00,0,0,0,0,0,0,0,0},
      {0.8715614709370008E-05,0.4494947316246257E+01,0.2985281258774867E+00,0,0,0,0,0,0,0,0},
      {0.5198697062661200E-05,0.4383615872629911E+01,0.5168540111097993E+01,0,0,0,0,0,0,0,0},
      {0.3847028724666396E-05,0.6152515926191327E+01,0.4990449357320712E+01,0,0,0,0,0,0,0,0},
      {0.1456357118609923E-03,0.2429607332411430E+00,0.2428763081719043E+04,2,0,-2,0,0,0,0,0},
      {0.6713447572711532E-04,0.4070899686845125E+01,0.2428852127095932E+04,2,0,-2,0,0,0,0,0},
      {0.6681066992467156E-04,0.5839799740406537E+01,0.2428674036342154E+04,2,0,-2,0,0,0,0,0},
      {0.1422191153382851E-04,0.5153453440392031E+01,0.2428584990965266E+04,2,0,-2,0,0,0,0,0},
      {0.1410806846233703E-04,0.1615653333269246E+01,0.2428941172472820E+04,2,0,-2,0,0,0,0,0},
      {0.8436544361747722E-05,0.5237122972947232E+01,0.1479320878997066E+03,-1,3,-2,0,0,0,0,0}
    };
    Series13= new double[][] {
      {0.1598170384010833E-01,0.6222473066544683E+01,0.6381214718431950E+01,-1,0,2,0,0,0,0,0},
      {0.7314708595483528E-02,0.2394534112940702E+01,0.6292169341543310E+01,-1,0,2,0,0,0,0,0},
      {0.7111400179101439E-02,0.6256340593792847E+00,0.6470260095320591E+01,-1,0,2,0,0,0,0,0},
      {0.1511549643267883E-02,0.4849780466516306E+01,0.6203123964654669E+01,-1,0,2,0,0,0,0,0},
      {0.1462159663847020E-02,0.1311980359393475E+01,0.6559305472209232E+01,-1,0,2,0,0,0,0,0},
      {0.3336219324045869E-03,0.1021841512912324E+01,0.6114078587766029E+01,-1,0,2,0,0,0,0,0},
      {0.3306740201503266E-03,0.1998326659407663E+01,0.6648350849097872E+01,-1,0,2,0,0,0,0,0},
      {0.1607455685492625E-03,0.4012828055947553E+01,-0.3816438325120998E+01,-1,0,2,0,0,0,0,0},
      {0.7741070178691200E-04,0.1331318296765385E+01,0.6438868100109027E+01,-1,0,2,0,0,0,0,0},
      {0.7462286917233880E-04,0.2684672959421853E+01,0.6737396225986513E+01,-1,0,2,0,0,0,0,0},
      {0.7291096595471061E-04,0.3477087866487927E+01,0.6025033210877388E+01,-1,0,2,0,0,0,0,0},
      {0.6027636670891800E-04,0.3061542475582978E+01,0.6501652090532156E+01,-1,0,2,0,0,0,0,0},
      {0.4732749947219130E-04,0.1688849875554602E+01,0.6323561336754874E+01,-1,0,2,0,0,0,0,0},
      {0.3701056581056820E-04,0.2375196175568790E+01,0.6412606713643515E+01,-1,0,2,0,0,0,0,0},
      {0.3214169596116360E-04,0.6241811003916594E+01,0.6260777346331746E+01,-1,0,2,0,0,0,0,0},
      {0.2716644193630320E-04,0.4144096229130206E+01,0.6234515959866234E+01,-1,0,2,0,0,0,0,0},
      {0.2628589083368250E-04,0.2017664596779575E+01,0.6527913476997668E+01,-1,0,2,0,0,0,0,0},
      {0.2051066737888520E-04,0.6449719967511965E+00,0.6349822723220386E+01,-1,0,2,0,0,0,0,0},
      {0.2018714468805330E-04,0.3747888775597167E+01,0.6590697467420797E+01,-1,0,2,0,0,0,0,0},
      {0.1733062364950220E-04,0.2413872050312614E+01,0.6171731969443105E+01,-1,0,2,0,0,0,0,0},
      {0.1650028552141730E-04,0.4604222971289160E+01,0.1648982238509626E+02,-1,0,2,0,0,0,0,0},
      {0.1636165431356800E-04,0.3371019259436040E+01,0.6826441602875153E+01,-1,0,2,0,0,0,0,0},
      {0.1620277455112710E-04,0.2835322917727745E+01,0.1666791313887354E+02,-1,0,2,0,0,0,0,0},
      {0.1578422930662670E-04,0.5932334220063532E+01,0.5935987833988747E+01,-1,0,2,0,0,0,0,0},
      {0.1155548887648690E-04,0.2148976617713557E+01,0.1657886776198490E+02,-1,0,2,0,0,0,0,0},
      {0.8915309088647409E-05,0.3161572755262240E+00,0.6145470582977594E+01,-1,0,2,0,0,0,0,0},
      {0.7716155921462711E-05,0.7762840176851795E+00,0.1640077700820762E+02,-1,0,2,0,0,0,0,0},
      {0.7443474433517800E-05,0.3521669217741933E+01,0.1675695851576218E+02,-1,0,2,0,0,0,0,0},
      {0.7427257818452120E-05,0.2704010896793763E+01,0.6616958853886308E+01,-1,0,2,0,0,0,0,0},
      {0.6793672394573960E-05,0.4434235075611354E+01,0.6679742844309437E+01,-1,0,2,0,0,0,0,0},
      {0.6526439278535280E-05,0.4869118403888217E+01,0.6082686592554465E+01,-1,0,2,0,0,0,0,0},
      {0.2602700419616530E-02,0.1822484926062486E+00,0.2435144296437475E+04,1,0,0,0,0,0,0,0},
      {0.6248133126576452E-04,0.4252092258474006E+00,0.4863907378156518E+04,3,0,-2,0,0,0,0,0},
      {0.2892274500639771E-04,0.4253148179451381E+01,0.4863996423533407E+04,3,0,-2,0,0,0,0,0},
      {0.2889623530439334E-04,0.6022048233012799E+01,0.4863818332779630E+04,3,0,-2,0,0,0,0,0},
      {0.6069308702586342E-05,0.5335701932998608E+01,0.4863729287402741E+04,3,0,-2,0,0,0,0,0},
      {0.6020614391923135E-05,0.1797901825875778E+01,0.4864085468910296E+04,3,0,-2,0,0,0,0,0},
      {0.5322831922763783E-05,0.51764107,32312329E+01,0.1543133026181386E+03,-2,3,0,0,0,0,0,0}
    };
    Series14= new double[][] {
      {0.1188963618162444E-01,0.4087787867376310E+01,-0.6371881689831457E+01,-1,0,2,0,0,0,0,0},
      {0.5317666807877856E-02,0.2598489137723283E+00,-0.6460927066720098E+01,-1,0,2,0,0,0,0,0},
      {0.5301694964646620E-02,0.4774134167390498E+01,-0.6282836312942816E+01,-1,0,2,0,0,0,0,0},
      {0.1092196251660480E-02,0.2715095267347932E+01,-0.6549972443608739E+01,-1,0,2,0,0,0,0,0},
      {0.1074089419341231E-02,0.5460480467404687E+01,-0.6193790936054175E+01,-1,0,2,0,0,0,0,0},
      {0.2328430197381575E-03,0.6146826767418875E+01,-0.6104745559165536E+01,-1,0,2,0,0,0,0,0},
      {0.2224213393203463E-03,0.5170341620923537E+01,-0.6639017820497378E+01,-1,0,2,0,0,0,0,0},
      {0.5463249591300440E-04,0.5479818404776600E+01,-0.6314228308154381E+01,-1,0,2,0,0,0,0,0},
      {0.5206791451508880E-04,0.5499877602534791E+00,-0.6015700182276895E+01,-1,0,2,0,0,0,0,0},
      {0.5011145194424460E-04,0.1342402667319555E+01,-0.6728063197386019E+01,-1,0,2,0,0,0,0,0},
      {0.4248311992240600E-04,0.9268572764146046E+00,-0.6251444317731250E+01,-1,0,2,0,0,0,0,0},
      {0.3275262890701040E-04,0.5837349983565814E+01,-0.6429535071508532E+01,-1,0,2,0,0,0,0,0},
      {0.2612362501347000E-04,0.2405109764004165E+00,-0.6340489694619891E+01,-1,0,2,0,0,0,0,0},
      {0.2254194030722800E-04,0.4107125804748224E+01,-0.6492319061931663E+01,-1,0,2,0,0,0,0,0},
      {0.1978445332406060E-04,0.5019691662617644E+01,-0.1656953473338440E+02,-1,0,2,0,0,0,0,0},
      {0.1876210282323130E-04,0.2009411029961832E+01,-0.6518580448397173E+01,-1,0,2,0,0,0,0,0},
      {0.1838941226078010E-04,0.6166164704790789E+01,-0.6225182931265740E+01,-1,0,2,0,0,0,0,0},
      {0.1417196313981720E-04,0.4793472104762412E+01,-0.6403273685043022E+01,-1,0,2,0,0,0,0,0},
      {0.1412387702084000E-04,0.1613203576428794E+01,-0.6162398940842610E+01,-1,0,2,0,0,0,0,0},
      {0.1200955094991380E-04,0.2791868511442401E+00,-0.6581364438820303E+01,-1,0,2,0,0,0,0,0},
      {0.1141748624638240E-04,0.7006377185593701E+00,0.3914816730610132E+01,-1,0,2,0,0,0,0,0},
      {0.1140613650769890E-04,0.2469537772120787E+01,0.3736725976832851E+01,-1,0,2,0,0,0,0,0},
      {0.1139717898986310E-04,0.1236334060267667E+01,-0.5926654805388254E+01,-1,0,2,0,0,0,0,0},
      {0.1083732897435760E-04,0.3797649020895159E+01,-0.6817108574274660E+01,-1,0,2,0,0,0,0,0},
      {0.8188197469152401E-05,0.1429141854518208E-01,0.3825771353721492E+01,-1,0,2,0,0,0,0,0},
      {0.6138904676788960E-05,0.4464657383537437E+01,-0.6607625825285813E+01,-1,0,2,0,0,0,0,0},
      {0.5339008498818640E-05,0.4924784125696391E+01,0.3647680599944211E+01,-1,0,2,0,0,0,0,0},
      {0.5211706783516070E-05,0.1386984018573560E+01,0.4003862107498773E+01,-1,0,2,0,0,0,0,0},
      {0.5186530053942100E-05,0.5693256976253909E+00,-0.6136137554377100E+01,-1,0,2,0,0,0,0,0},
      {0.1477125534949241E-04,0.2559894425015774E+01,0.4876660474564782E+04,3,0,-2,0,0,0,0,0},
      {0.6813829579894155E-05,0.1873548125001586E+01,0.4876571429187893E+04,3,0,-2,0,0,0,0,0},
      {0.6707536779238787E-05,0.1046480714401697E+00,0.4876749519941671E+04,3,0,-2,0,0,0,0,0}
    };
  }
  private static void SetupTable2() {
    Series21= new double[][] {
      {0.3147075653259473E-02,0.3141592653589793E+01,0.0000000000000000E+00,0,0,0,0,0,0,0,0},
      {0.2265054397578757E-04,0.7519405621081341E+00,0.1672712856276297E+04,0,2,0,-2,0,0,0,0},
      {0.7116593169805980E-05,0.5461033287440088E+00,0.9184067388830385E+03,0,2,-2,0,0,0,0,0}
    };
    Series22= new double[][] {
      {0.4496393702552367E-02,0.2342959364982154E+01,0.5659095156176798E+00,0,0,0,0,0,0,0,0},
      {0.3354575501528797E-02,0.4597833882505114E+01,0.1617016547228022E+01,0,0,0,0,0,0,0,0},
      {0.3106536996299520E-04,0.2912482457830643E+01,0.3234033094456044E+01,0,0,0,0,0,0,0,0},
      {0.2407327778886120E-04,0.4985440644756694E+01,0.4592033694415193E+03,0,1,-1,0,0,0,0,0},
      {0.2157848301674358E-04,0.3893533207807963E+01,0.1672712856276297E+04,0,2,0,-2,0,0,0,0},
      {0.2107499273982886E-04,0.5461033287440088E+00,0.9184067388830385E+03,0,2,-2,0,0,0,0,0},
      {0.1204571746494518E-04,0.3481858748969675E+01,0.1641006214897798E+03,0,2,-4,2,0,0,0,0},
      {0.1082902927586888E-04,0.2389951319910909E+01,0.1377610108324558E+04,0,3,-3,0,0,0,0,0},
      {0.6457229782189520E-05,0.4233799311077810E+01,0.1836813477766077E+04,0,4,-4,0,0,0,0,0},
      {0.5359800347726300E-05,0.1150736720732397E+01,0.3061886502424254E+04,0,2,0,0,0,-2,0,0},
      {0.4253124471669380E-05,0.1946766624913765E+01,0.8363564281381487E+03,0,1,0,-1,0,0,0,0},
      {0.4159628279141040E-05,0.6077647302244714E+01,0.2296016847207596E+04,0,5,-5,0,0,0,0,0}
    };
    Series23= new double[][] {
      {0.4803805197845248E-02,0.3189423405738944E+01,0.2154442221398995E+01,0,-1,0,2,0,0,0,0},
      {0.1097719996101334E-03,0.5532383110312554E+01,0.2720351737016674E+01,0,-1,0,2,0,0,0,0},
      {0.7443719437241270E-05,0.3988057033938039E+01,0.1588532705781315E+01,0,-1,0,2,0,0,0,0},
      {0.6715628349206410E-05,0.4645664974245721E+01,0.3771458768627016E+01,0,-1,0,2,0,0,0,0},
      {0.6526400221336371E-05,0.2681979744993624E+01,0.8933864296234374E-02,0,0,0,0,0,0,0,0},
      {0.5778112672027477E-05,0.1733182516415079E+01,0.5374256741709729E+00,0,-1,0,2,0,0,0,0},
      {0.1576763094428542E-02,0.7997716879783442E+00,0.1674867298497696E+04,0,1,0,0,0,0,0,0},
      {0.2328380239517312E-04,0.5990750337669914E+01,-0.1619461792683805E+03,0,-3,4,0,0,0,0,0},
      {0.1305558563149728E-04,0.4693005675247022E+01,0.2972571901731385E+03,0,-2,3,0,0,0,0,0},
      {0.1134789737634680E-04,0.4693304903676271E+01,0.3347580154773993E+04,0,3,0,-2,0,0,0,0},
      {0.5311492417496450E-05,0.2536683592343353E+00,0.7564605596146578E+03,0,-1,2,0,0,0,0,0},
      {0.5297748739729408E-05,0.4146902346503011E+01,-0.6211495487099000E+03,0,-4,5,0,0,0,0,0},
      {0.4864126392950970E-05,0.2790627620835740E+01,-0.1387019203926558E+04,0,-1,0,0,0,2,0,0}
    };
    Series24=new double[][] {
      {0.4833318619528478E-05,0.3221504590125614E+01,0.0000000000000000E+00,0,0,0,0,0,0,0,0},
      {0.1280938903343441E-03,0.1983149608096680E+01,-0.2659196588419094E+01,0,-1,0,2,0,0,0,0},
      {0.3085667117081219E-04,0.3937780549016139E+01,-0.1260994960410540E+01,-1,0,2,0,0,0,0,0},
      {0.1421727517964700E-04,0.4624126849030327E+01,-0.1171949583521899E+01,-1,0,2,0,0,0,0,0},
      {0.1420735399816070E-04,0.1098415954121576E+00,-0.1350040337299180E+01,-1,0,2,0,0,0,0,0}
    };
  }
  private static void SetupTable3() {
    Series31= new double[][] {
      {0.2047958691903563E-02,0.3141592653589793E+01,0.0000000000000000E+00,0,0,0,0,0,0,0,0},
      {0.9773647178903700E-05,0.3347429921083522E+01,0.7543061173932588E+03,0,0,2,-2,0,0,0,0},
      {0.8692361446792260E-05,0.3746226045578181E+01,0.2143479763541216E+04,0,0,2,0,0,-2,0,0},
      {0.6341490354446320E-05,0.3087559012405942E+00,0.1131459176089888E+04,0,0,3,-3,0,0,0,0}
    };
    Series32= new double[][] {
      {0.3597193003482037E-01,0.3827938953603982E+01,0.8904537688864059E-01,0,0,0,0,0,0,0,0},
      {0.5891806577851199E-03,0.5200631553632359E+01,0.2671361306659218E+00,0,0,0,0,0,0,0,0},
      {0.1049859753280688E-03,0.5351281511938252E+01,0.1019765304355295E+02,0,0,0,0,0,0,0,0},
      {0.4944669618681220E-04,0.1523342558334271E+01,0.1010860766666431E+02,0,0,0,0,0,0,0,0},
      {0.4772345678736420E-04,0.6037627811952441E+01,0.1028669842044159E+02,0,0,0,0,0,0,0,0},
      {0.3432162123697680E-04,0.4514285253618171E+01,0.1780907537772812E+00,0,0,0,0,0,0,0,0},
      {0.2555821179926381E-04,0.1392030537400290E+01,0.5765338167707570E-01,0,0,0,0,0,0,0,0},
      {0.1717434823483564E-04,0.2078376837414479E+01,0.1466987585657163E+00,0,0,0,0,0,0,0,0},
      {0.1697093421514454E-04,0.2901388464811505E+00,0.4452268844432029E+00,0,0,0,0,0,0,0,0},
      {0.1323271931613666E-04,0.1353354662656465E+01,0.2985281258774867E+00,0,0,0,0,0,0,0,0},
      {0.1267528991884128E-04,0.3196049795082991E+01,0.6492496159975472E-01,0,0,0,0,0,0,0,0},
      {0.1266151790256666E-04,0.5906315791018461E+01,0.2357441354543569E+00,0,0,0,0,0,0,0,0},
      {0.1157195347749026E-04,0.5165512349511074E+01,0.8177379696596157E-01,0,0,0,0,0,0,0,0},
      {0.1080021159829085E-04,0.5577501069793485E+01,0.3139199521156488E-01,0,0,0,0,0,0,0,0},
      {0.1052724821924284E-04,0.3978588911909874E+01,0.1001956228977567E+02,0,0,0,0,0,0,0,0},
      {0.1001557835312868E-04,0.6318891585209909E+00,0.2412041528888587E-01,0,0,0,0,0,0,0,0},
      {0.9867772453097199E-05,0.4407888047870434E+00,0.1037574379733023E+02,0,0,0,0,0,0,0,0},
      {0.9845954273310880E-05,0.5631958211286683E+01,0.9631695681131960E-01,0,0,0,0,0,0,0,0},
      {0.9815613284418477E-05,0.6263847369807674E+01,0.1204373721002055E+00,0,0,0,0,0,0,0,0},
      {0.9194949884833879E-05,0.1318235458535180E+01,0.1131657921775265E+00,0,0,0,0,0,0,0,0},
      {0.8037140287447099E-05,0.5556769190259295E+00,0.5079494734209352E+01,0,0,0,0,0,0,0,0},
      {0.7218512993756520E-05,0.3808601016232069E+01,0.2094827489888461E+00,0,0,0,0,0,0,0,0},
      {0.6001489632871980E-05,0.2305239035215433E+01,0.5021841352532276E+01,0,0,0,0,0,0,0,0},
      {0.5489567383712060E-05,0.5363389816540173E+00,0.5199932106309558E+01,0,0,0,0,0,0,0,0},
      {0.4021385382444620E-05,0.4239927673886392E+01,0.3866357513424390E-01,0,0,0,0,0,0,0,0},
      {0.3767845663771360E-05,0.3010923272601534E+01,0.4990449357320712E+01,0,0,0,0,0,0,0,0},
      {0.3600130988680400E-05,0.1242023219040118E+01,0.5168540111097993E+01,0,0,0,0,0,0,0,0},
      {0.3094782117290198E-05,0.1804019257682701E+01,0.7271579922679017E-02,0,0,0,0,0,0,0,0},
      {0.2857487995253398E-04,0.1029186337468635E+00,0.3771530586966294E+03,0,0,1,-1,0,0,0,0},
      {0.2612226809076254E-04,0.3347429921083522E+01,0.7543061173932588E+03,0,0,2,-2,0,0,0,0},
      {0.1309044788609482E-04,0.3087559012405942E+00,0.1131459176089888E+04,0,0,3,-3,0,0,0,0},
      {0.1086312362376316E-04,0.3746226045578181E+01,0.2143479763541216E+04,0,0,2,0,0,-2,0,0},
      {0.9634177151388563E-05,0.3197170586335357E+01,0.8366732729211768E+02,0,1,-2,1,0,0,0,0},
      {0.7589604761197260E-05,0.3553267188577251E+01,0.1508612234786518E+04,0,0,4,-4,0,0,0,0},
      {0.5706936513245540E-05,0.1177133183021748E+01,0.1415309217766697E+04,0,0,2,0,-2,0,0,0},
      {0.4749981493034660E-05,0.5145931687343214E+00,0.1885765293483147E+04,0,0,5,-5,0,0,0,0},
      {0.4252829449661740E-05,0.2159362918305770E+01,0.7076546088833486E+03,0,0,1,0,-1,0,0,0},
      {0.4209916715651780E-05,0.1843847991166901E+01,0.4592033694415193E+03,0,1,-1,0,0,0,0,0},
      {0.3381153874922860E-05,0.3443909349583986E+01,0.1071739881770608E+04,0,0,1,0,0,-1,0,0},
      {0.3367826063379420E-05,0.3687695982333802E+01,0.9184067388830385E+03,0,2,-2,0,0,0,0,0}
    };
    Series33= new double[][] {
      {0.1564767415994558E-03,0.4568461127222154E+01,0.1263056409088354E+01,-1,0,2,0,0,0,0,0},
      {0.8681007704489939E-04,0.7405221736181721E+00,0.1174011032199713E+01,-1,0,2,0,0,0,0,0},
      {0.8168879255357250E-04,0.5941153727250531E+01,0.1441147162865635E+01,-1,0,2,0,0,0,0,0},
      {0.8101165266469901E-04,0.3195768527193775E+01,0.1084965655311073E+01,-1,0,2,0,0,0,0,0},
      {0.7081435508752854E-04,0.5254807427236342E+01,0.1352101785976995E+01,-1,0,2,0,0,0,0,0},
      {0.2463670320328344E-04,0.2681979744993623E+01,0.8933864296234374E-02,0,0,0,0,0,0,0,0},
      {0.1795872905935810E-04,0.3443147200851330E+00,0.1530192539754276E+01,-1,0,2,0,0,0,0,0},
      {0.1765915793094640E-04,0.5651014880769380E+01,0.9959202784224322E+00,-1,0,2,0,0,0,0,0},
      {0.1379536465955919E-04,0.4874775170004872E+01,0.5374256741709729E+00,0,-1,0,2,0,0,0,0},
      {0.6448693043220393E-05,0.8713108941844011E+00,-0.3816438325120998E+01,-1,0,2,0,0,0,0,0},
      {0.4134187193316490E-05,0.3483793623207228E-01,0.1205403027411278E+01,-1,0,2,0,0,0,0,0},
      {0.4064250964938940E-05,0.5960491664622441E+01,0.1320709790765430E+01,-1,0,2,0,0,0,0,0},
      {0.3921367852619880E-05,0.1030661020099323E+01,0.1619237916642916E+01,-1,0,2,0,0,0,0,0},
      {0.3847793440490170E-05,0.1823075927165398E+01,0.9068749015337917E+00,-1,0,2,0,0,0,0,0},
      {0.1026432488829525E-02,0.5239109003991030E+01,0.1215663929056177E+04,0,0,1,0,0,0,0,0},
      {0.4858676635900159E-04,0.4930353102750437E+01,0.8420475296628865E+02,0,0,-2,3,0,0,0,0},
      {0.1316230344933185E-04,0.9203831673794880E+00,-0.1996452887105200E+03,0,0,-1,0,2,0,0,0},
      {0.1274174154891934E-04,0.4827434469003571E+01,-0.2929483057303405E+03,0,0,-3,4,0,0,0,0},
      {0.1012648120942390E-04,0.4634475612002642E+01,-0.9278158344850386E+03,0,0,-1,0,0,2,0,0},
      {0.9537957898915770E-05,0.1891679082907508E+01,0.4613578116629183E+03,0,0,-1,2,0,0,0,0},
      {0.6736048525042149E-05,0.1795199654407042E+01,0.1439240472855692E+03,0,0,0,0,0,1,0,0},
      {0.4893120486700722E-05,0.1582923181666916E+01,-0.6701013644269697E+03,0,0,-4,5,0,0,0,0},
      {0.4496122761411780E-05,0.2849157684080121E+01,-0.1619461792683805E+03,0,-3,4,0,0,0,0,0}
    };
    Series34= new double[][] {
      {0.1608815757672211E-04,0.3221506193392377E+01,0.0000000000000000E+00,0,0,0,0,0,0,0,0},
      {0.7978986672131195E-02,0.3937780549016139E+01,-0.1260994960410540E+01,-1,0,2,0,0,0,0,0},
      {0.3586811056529552E-02,0.1098415954121576E+00,-0.1350040337299180E+01,-1,0,2,0,0,0,0,0},
      {0.3578626570900766E-02,0.4624126849030327E+01,-0.1171949583521899E+01,-1,0,2,0,0,0,0,0},
      {0.7456079964789274E-03,0.2565087948987761E+01,-0.1439085714187821E+01,-1,0,2,0,0,0,0,0},
      {0.7269088792971872E-03,0.5310473149044516E+01,-0.1082904206633259E+01,-1,0,2,0,0,0,0,0},
      {0.1634061837956512E-03,0.5996819449058704E+01,-0.9938588297446179E+00,-1,0,2,0,0,0,0,0},
      {0.1629278673196500E-03,0.5020334302563366E+01,-0.1528131091076462E+01,-1,0,2,0,0,0,0,0},
      {0.3863423686243950E-04,0.5329811086416426E+01,-0.1203341578733464E+01,-1,0,2,0,0,0,0,0},
      {0.3696660017446820E-04,0.3999804418933084E+00,-0.9048134528559774E+00,-1,0,2,0,0,0,0,0},
      {0.3605229442958720E-04,0.1192395348959384E+01,-0.1617176467965102E+01,-1,0,2,0,0,0,0,0},
      {0.3003084312634590E-04,0.7768499580544321E+00,-0.1140557588310333E+01,-1,0,2,0,0,0,0,0},
      {0.2358139037174070E-04,0.5687342665205642E+01,-0.1318648342087615E+01,-1,0,2,0,0,0,0,0},
      {0.1853853482238710E-04,0.9050365804024403E-01,-0.1229602965198974E+01,-1,0,2,0,0,0,0,0},
      {0.1598764340805630E-04,0.3957118486388051E+01,-0.1381432332510745E+01,-1,0,2,0,0,0,0,0},
      {0.1403252488381090E-04,0.4869684344257474E+01,-0.1145864800396349E+02,-1,0,2,0,0,0,0,0},
      {0.1348141163501630E-04,0.1859403711601660E+01,-0.1407693718976255E+01,-1,0,2,0,0,0,0,0},
      {0.1307143245256740E-04,0.6016157386430616E+01,-0.1114296201844823E+01,-1,0,2,0,0,0,0,0},
      {0.1026902134395760E-04,0.4643464786402239E+01,-0.1292386955622105E+01,-1,0,2,0,0,0,0,0},
      {0.1001531421510340E-04,0.1463196258068622E+01,-0.1051512211421693E+01,-1,0,2,0,0,0,0,0},
      {0.8589176280796499E-05,0.1291795327840677E+00,-0.1470477709399386E+01,-1,0,2,0,0,0,0,0},
      {0.8194661069081041E-05,0.2319530453760617E+01,0.8847612706253768E+01,-1,0,2,0,0,0,0,0},
      {0.8097194747293030E-05,0.1086326741907496E+01,-0.8157680759673368E+00,-1,0,2,0,0,0,0,0},
      {0.8055438816212310E-05,0.5506304001991995E+00,0.9025703460031048E+01,-1,0,2,0,0,0,0,0},
      {0.7804425391545350E-05,0.3647641702534988E+01,-0.1706221844853743E+01,-1,0,2,0,0,0,0,0},
      {0.6111685892210941E-05,0.6135349392018898E+01,-0.8931239595284827E-02,0,0,0,0,0,0,0,0},
      {0.5770004540143910E-05,0.6147469407364597E+01,0.8936658083142408E+01,-1,0,2,0,0,0,0,0},
      {0.4417994658707790E-05,0.4314650065177265E+01,-0.1496739095864896E+01,-1,0,2,0,0,0,0,0},
      {0.4341737854415172E-05,0.2696600432337302E+01,-0.1754676234867099E+00,0,0,0,0,0,0,0,0},
      {0.3824550961047250E-05,0.4774776807336220E+01,0.8758567329365126E+01,-1,0,2,0,0,0,0,0},
      {0.3690275690503300E-05,0.1236976700213389E+01,0.9114748836919690E+01,-1,0,2,0,0,0,0,0},
      {0.3688282109438380E-05,0.4193183792652183E+00,-0.1025250824956183E+01,-1,0,2,0,0,0,0,0},
      {0.3369179308695820E-05,0.2149542558082810E+01,-0.9624668345330522E+00,-1,0,2,0,0,0,0,0},
      {0.4066369173620947E-05,0.2572521517863344E+00,0.2432588853072765E+04,1,0,0,0,0,0,0,0}
    };
  }
  private static void SetupTable4() {
    Series41= new double[][] {
      {0.1245046723085128E-02,0.3141592653589793E+01,0.0000000000000000E+00,0,0,0,0,0,0,0,0},
      {0.1975589414013096E-04,0.3540388778084452E+01,0.1389173646147957E+04,0,0,0,2,0,-2,0,0},
      {0.1231028575289072E-04,0.9712959155280194E+00,0.6610031003734383E+03,0,0,0,2,-2,0,0,0},
      {0.7467512730405980E-05,0.3027740200086925E+01,0.9915046505601572E+03,0,0,0,3,-3,0,0,0},
      {0.5098846661514180E-05,0.5981941867419887E+00,0.2083760469221935E+04,0,0,0,3,0,-3,0,0},
      {0.5044474622853420E-05,0.5198036938148699E+01,0.3305015501867192E+03,0,0,0,1,-1,0,0,0},
      {0.4809588755287700E-05,0.2058372674937290E+00,0.7543061173932588E+03,0,0,2,-2,0,0,0,0},
      {0.4727673963003260E-05,0.5084184484645832E+01,0.1322006200746877E+04,0,0,0,4,-4,0,0,0},
      {0.4044589508141894E-05,0.2614152002392929E+00,0.8379734446853766E+03,0,1,0,-1,0,0,0,0},
      {0.3991422799327240E-05,0.1029186337468635E+00,0.3771530586966294E+03,0,0,1,-1,0,0,0,0}
    };
    Series42= new double[][] {
      {0.1253214092917414E-03,0.5484552018571947E+01,0.5659095156176798E+00,0,0,0,0,0,0,0,0},
      {0.9470863297623297E-04,0.1456241228915321E+01,0.1617016547228022E+01,0,0,0,0,0,0,0,0},
      {0.2711677399803780E-05,0.6054075111420435E+01,0.3234033094456044E+01,0,0,0,0,0,0,0,0},
      {0.2717321774931640E-04,0.9712959155280194E+00,0.6610031003734383E+03,0,0,0,2,-2,0,0,0},
      {0.2682461086510494E-04,0.5198036938148699E+01,0.3305015501867192E+03,0,0,0,1,-1,0,0,0},
      {0.2565388997294820E-04,0.3540388778084452E+01,0.1389173646147957E+04,0,0,0,2,0,-2,0,0},
      {0.1499025754719972E-04,0.3244511287336658E+01,0.3771530586966294E+03,0,0,1,-1,0,0,0,0},
      {0.1270721017173096E-04,0.3027740200086925E+01,0.9915046505601572E+03,0,0,0,3,-3,0,0,0},
      {0.1201329949530382E-04,0.2058372674937290E+00,0.7543061173932588E+03,0,0,2,-2,0,0,0,0},
      {0.1025994897473446E-04,0.1993980622473295E+00,0.6945868230739784E+03,0,0,0,1,0,-1,0,0},
      {0.6847120194215620E-05,0.5084184484645832E+01,0.1322006200746877E+04,0,0,0,4,-4,0,0,0},
      {0.6090479694942740E-05,0.3450348554830387E+01,0.1131459176089888E+04,0,0,3,-3,0,0,0,0},
      {0.5552232090659460E-05,0.5981941867419887E+00,0.2083760469221935E+04,0,0,0,3,0,-3,0,0},
      {0.4770404124767720E-05,0.5557793274556413E-01,0.8366732729211768E+02,0,1,-2,1,0,0,0,0},
      {0.4723715451200404E-05,0.3403007853829086E+01,0.8379734446853766E+03,0,1,0,-1,0,0,0,0},
      {0.4269998249018742E-05,0.5396405225690409E+01,0.1439151134212730E+03,0,0,0,0,0,1,0,0},
      {0.3974354213288560E-05,0.8574434620251531E+00,0.1652507750933596E+04,0,0,0,5,-5,0,0,0},
      {0.3552297626424840E-05,0.4116745349874580E+00,0.1508612234786518E+04,0,0,4,-4,0,0,0,0},
      {0.3125192040346257E-05,0.1285576205981393E+01,0.1245258532726684E+04,0,0,0,2,0,-3,0,0}
    };
    Series43= new double[][] {
      {0.2203368656279073E-02,0.4874775170004872E+01,0.5374256741709729E+00,0,-1,0,2,0,0,0,0},
      {0.1172252747692422E-03,0.2681979734714067E+01,0.8933864296234374E-02,0,0,0,0,0,0,0,0},
      {0.1886080065709498E-04,0.3189421946098056E+01,0.2154442221398995E+01,0,-1,0,2,0,0,0,0},
      {0.5709763882914900E-05,0.9345492278074399E+00,0.1103335189788653E+01,0,-1,0,2,0,0,0,0},
      {0.5709737153001810E-05,0.5673408458612511E+01,-0.2848384144670679E-01,0,-1,0,2,0,0,0,0},
      {0.4177951671700660E-05,0.3418533941089551E+01,-0.1079590873057049E+01,0,-1,0,2,0,0,0,0},
      {0.2618379490210864E-05,0.1426868473632360E+01,0.1263056409088354E+01,-1,0,2,0,0,0,0,0},
      {0.6245540270289302E-03,0.1994597716654372E+01,0.8385108703595477E+03,0,0,0,1,0,0,0,0},
      {0.2859852996917539E-04,0.1788760449160644E+01,0.8420475296628865E+02,0,0,-2,3,0,0,0,0},
      {0.2669607656360495E-04,0.2108450170157240E+01,-0.1529937802006098E+03,0,0,0,-2,3,0,0,0},
      {0.2594271531157734E-04,0.1023301801126352E+01,0.1775077699861093E+03,0,0,0,-1,2,0,0,0},
      {0.2527837703075836E-04,0.1595801592159713E+01,-0.5506627757884092E+03,0,0,0,-1,0,2,0,0},
      {0.1288509519128533E-04,0.1795199654407042E+01,0.1439240472855692E+03,0,0,0,0,0,1,0,0},
      {0.7799304255363336E-05,0.1685841815413778E+01,-0.2929483057303405E+03,0,0,-3,4,0,0,0,0},
      {0.6968783491136024E-05,0.5200588559833291E-01,-0.4834953303873287E+03,0,0,0,-3,4,0,0,0},
      {0.4309530620212000E-05,0.4537996183502177E+01,-0.1245249598862388E+04,0,0,0,-2,0,3,0,0},
      {0.3787380702785700E-05,0.5033271736497301E+01,0.4613578116629183E+03,0,0,-1,2,0,0,0,0},
      {0.3539705714890319E-05,0.3850614164262772E+01,-0.4067476623671363E+03,0,0,0,-1,0,3,0,0},
      {0.3368317726971210E-05,0.3079746085685258E+01,0.5080093201728285E+03,0,0,0,0,1,0,0,0},
      {0.3304505798253070E-05,0.4278746908219012E+01,-0.8139968805740482E+03,0,0,0,-4,5,0,0,0},
      {0.2969852804959130E-05,0.4724515835256708E+01,-0.6701013644269697E+03,0,0,-4,5,0,0,0,0}
    };
    Series44=new double[][] {
      {0.5911557823662097E-04,0.3221505896904584E+01,0.0000000000000000E+00,0,0,0,0,0,0,0,0},
      {0.1655363859165119E-03,0.1556643393089241E+01,-0.5376315279689470E+00,0,-1,0,2,0,0,0,0},
      {0.3529922124449878E-04,0.2632866665182736E+01,-0.1754676234867099E+00,0,0,0,0,0,0,0,0},
      {0.2754688236937855E-04,0.7961878954263457E+00,-0.1260994960410540E+01,-1,0,2,0,0,0,0,0},
      {0.2282739732511724E-04,0.6135349401370048E+01,-0.8931239595284827E-02,0,0,0,0,0,0,0,0},
      {0.1269228313121738E-04,0.1482534195440534E+01,-0.1171949583521899E+01,-1,0,2,0,0,0,0,0},
      {0.1268342612853373E-04,0.3251434249001951E+01,-0.1350040337299180E+01,-1,0,2,0,0,0,0,0},
      {0.2655183882413472E-05,0.2168880495454724E+01,-0.1082904206633259E+01,-1,0,2,0,0,0,0,0},
      {0.2629764340505144E-05,0.5706680602577554E+01,-0.1439085714187821E+01,-1,0,2,0,0,0,0,0}
    };
  }
  private static void SetupTable5() {
    Series51=new double[][] {
      {0.6263338154589970E-03,0.3141592653589793E+01,0.0000000000000000E+00,0,0,0,0,0,0,0,0},
      {0.6497476875291102E-04,0.5710685516146226E+01,0.7281705457745186E+03,0,0,0,0,2,-2,0,0},
      {0.2344587498942920E-04,0.7120466402448569E+00,0.1092255818661778E+04,0,0,0,0,3,-3,0,0},
      {0.1460129617898124E-04,0.4426139084868009E+01,0.3640852728872593E+03,0,0,0,0,1,-1,0,0},
      {0.9207121596242679E-05,0.5198036938148699E+01,0.3305015501867192E+03,0,0,0,1,-1,0,0,0},
      {0.8875319141540999E-05,0.1996593071523072E+01,0.1456341091549037E+04,0,0,0,0,4,-4,0,0},
      {0.8448119776705509E-05,0.3455872944043167E+01,0.5842554323532455E+03,0,0,0,0,2,-3,0,0},
      {0.5977877370825760E-05,0.2159362918305770E+01,0.7076546088833486E+03,0,0,1,0,-1,0,0,0},
      {0.4621278522602440E-05,0.4112888569117812E+01,0.6610031003734383E+03,0,0,0,2,-2,0,0,0},
      {0.4000365153622274E-05,0.4740419375321384E+01,0.9483407052405049E+03,0,0,0,0,3,-4,0,0},
      {0.3452681160707960E-05,0.3281139502801291E+01,0.1820426364436296E+04,0,0,0,0,5,-5,0,0}
    };
    Series52=new double[][] {
      {0.4982624562626507E-03,0.4765136030335644E+01,0.1925543592528062E-02,0,0,0,0,0,0,0,0},
      {0.3255047679977468E-03,0.3303959081334276E+01,0.8931239595284827E-02,0,0,0,0,0,0,0,0},
      {0.6390530214147782E-04,0.2864957498258305E+01,0.4265982401557260E+00,0,0,0,0,0,0,0,0},
      {0.6147405445926997E-04,0.3666743633441163E+01,0.1754676234867099E+00,0,0,0,0,0,0,0,0},
      {0.4693769190005650E-04,0.4409577074506501E+01,0.2132991200778630E+00,0,0,0,0,0,0,0,0},
      {0.1830753059305353E-04,0.2440869213255669E+00,0.1665363838914251E+00,0,0,0,0,0,0,0,0},
      {0.1772508422160692E-04,0.5196471838140832E+01,0.1786772859246875E-01,0,0,0,0,0,0,0,0},
      {0.9471405209345812E-05,0.7450246191142529E+00,0.3783149659115309E-01,0,0,0,0,0,0,0,0},
      {0.8699581713814392E-05,0.4133277437711403E+01,0.6398973602335890E+00,0,0,0,0,0,0,0,0},
      {0.4462723299012653E-05,0.4222312533161678E+01,0.6934772211657580E-02,0,0,0,0,0,0,0,0},
      {0.3475126795324123E-05,0.5905021737251245E+01,0.3851087185056125E-02,0,0,0,0,0,0,0,0},
      {0.9265555925994737E-04,0.5710685516146226E+01,0.7281705457745186E+03,0,0,0,0,2,-2,0,0},
      {0.5229341727885530E-04,0.4426139084868009E+01,0.3640852728872593E+03,0,0,0,0,1,-1,0,0},
      {0.2755180293512720E-04,0.7120466402448569E+00,0.1092255818661778E+04,0,0,0,0,3,-3,0,0},
      {0.1367398797935688E-04,0.3455872944043167E+01,0.5842554323532455E+03,0,0,0,0,2,-3,0,0},
      {0.1266911496422784E-04,0.2056444284558907E+01,0.3305015501867192E+03,0,0,0,1,-1,0,0,0},
      {0.1003942988473349E-04,0.2171326512764949E+01,0.2201701594659863E+03,0,0,0,0,1,-2,0,0},
      {0.9413368380851320E-05,0.4112888569117812E+01,0.6610031003734383E+03,0,0,0,2,-2,0,0,0},
      {0.9310491515462421E-05,0.1996593071523072E+01,0.1456341091549037E+04,0,0,0,0,4,-4,0,0},
      {0.8614402961042187E-05,0.5396405225692853E+01,0.1439151134212730E+03,0,0,0,0,0,1,0,0},
      {0.5024449380692616E-05,0.4740419375321384E+01,0.9483407052405049E+03,0,0,0,0,3,-4,0,0},
      {0.4460853124280260E-05,0.6169332853676718E+01,0.9915046505601572E+03,0,0,0,3,-3,0,0,0},
      {0.3825560625917272E-05,0.6199699253192279E+01,0.7625504604471335E+02,0,0,0,0,1,-3,0,0},
      {0.3359851109236900E-05,0.3281139502801291E+01,0.1820426364436296E+04,0,0,0,0,5,-5,0,0},
      {0.3028875527833520E-05,0.5300955571895564E+01,0.7076546088833486E+03,0,0,1,0,-1,0,0,0},
      {0.2423482201792380E-05,0.1942591831056039E+01,0.1322006200746877E+04,0,0,0,4,-4,0,0,0},
      {0.2065629848458831E-05,0.6024965806599599E+01,0.1312425978127764E+04,0,0,0,0,4,-5,0,0}
    };
    Series53=new double[][] {
      {0.9712611923476502E-03,0.2687921917028628E+01,0.8933864296234374E-02,0,0,0,0,0,0,0,0},
      {0.1671852669066537E-03,0.5358703148577743E-01,0.1755492171961483E+00,0,0,0,0,0,0,0,0},
      {0.7641483476943351E-05,0.1733182516415079E+01,0.5374256741709729E+00,0,-1,0,2,0,0,0,0},
      {0.6496268041362011E-05,0.6195159743999218E+00,-0.8933864296234374E-02,0,0,0,0,0,0,0,0},
      {0.2256764766375221E-05,0.3428726874313563E+00,0.4176643758594916E+00,0,0,0,0,0,0,0,0},
      {0.3116043009304567E-03,0.6221338739275052E+01,0.5080093201728285E+03,0,0,0,0,1,0,0,0},
      {0.1108384911297719E-03,0.3652245876718619E+01,-0.2201612256016901E+03,0,0,0,0,-1,2,0,0},
      {0.3382899984196108E-04,0.5907058448821680E+01,-0.7624611218041713E+02,0,0,0,0,-1,3,0,0},
      {0.3248040932920730E-04,0.1795199654407042E+01,0.1439240472855692E+03,0,0,0,0,0,1,0,0},
      {0.2241664081352878E-04,0.2367699445440403E+01,-0.5842464984889493E+03,0,0,0,0,-2,3,0,0},
      {0.1308501254327346E-04,0.5250042823747033E+01,-0.1529937802006098E+03,0,0,0,-2,3,0,0,0},
      {0.6929884985440310E-05,0.1083153014162185E+01,-0.9483317713762087E+03,0,0,0,0,-3,4,0,0},
      {0.6766246868422730E-05,0.4164894454716146E+01,0.1775077699861093E+03,0,0,0,-1,2,0,0,0},
      {0.5779542899092500E-05,0.2507246294651899E+01,0.1236179865947347E+04,0,0,0,0,3,-2,0,0},
      {0.4424102595490495E-05,0.4622512017543462E+01,-0.4403313850676764E+03,0,0,0,0,-2,4,0,0},
      {0.3851717115480820E-05,0.1994597716654372E+01,0.8385108703595477E+03,0,0,0,1,0,0,0,0},
      {0.3467317924478190E-05,0.3193598539188126E+01,-0.4834953303873287E+03,0,0,0,-3,4,0,0,0},
      {0.2778461053446649E-05,0.5020278361984143E+01,0.6766900124085585E+02,0,0,0,0,-1,4,0,0},
      {0.2492530233817800E-05,0.3791792725930115E+01,0.1600265138834606E+04,0,0,0,0,4,-3,0,0},
      {0.2427569664020370E-05,0.6081791890063556E+01,-0.1312417044263468E+04,0,0,0,0,-4,5,0,0},
      {0.2224849328232030E-05,0.5239109003991030E+01,0.1215663929056177E+04,0,0,1,0,0,0,0,0}
    };
    Series54=new double[][] {
      {0.4206563248738731E-03,0.3221497379548206E+01,0.0000000000000000E+00,0,0,0,0,0,0,0,0},
      {0.2970532433280590E-02,0.2626878648804450E+01,-0.1754676234867099E+00,0,0,0,0,0,0,0,0},
      {0.1787509410723081E-03,0.6204551285275548E+01,-0.8931239595284827E-02,0,0,0,0,0,0,0,0},
      {0.2723114438070935E-04,0.6085981723978948E+01,0.4265982401557260E+00,0,0,0,0,0,0,0,0},
      {0.2414440346500943E-04,0.5096853638619743E+01,-0.2132991200778630E+00,0,0,0,0,0,0,0,0},
      {0.9525093659923903E-05,0.5044236237492881E+01,-0.1925543592528062E-02,0,0,0,0,0,0,0,0},
      {0.3807917651740949E-05,0.1070489829428804E+01,0.6398973602335890E+00,0,0,0,0,0,0,0,0},
      {0.3291355004303486E-05,0.4446713203128226E+01,0.2132991200778630E+00,0,0,0,0,0,0,0,0},
      {0.2944165615658908E-05,0.3500661322372421E+00,-0.4265982401557260E+00,0,0,0,0,0,0,0,0}
    };
  }
  private static void SetupTable6() {
    Series61=new double[][] {
      {0.1348089930929860E-03,0.3141592653589793E+01,0.0000000000000000E+00,0,0,0,0,0,0,0,0},
      {0.2514051816697911E-04,0.4426139084868009E+01,0.3640852728872593E+03,0,0,0,0,1,-1,0,0},
      {0.1234070873751902E-04,0.1993980622473295E+00,0.6945868230739784E+03,0,0,0,1,0,-1,0,0},
      {0.7428005944912294E-05,0.3443909349583986E+01,0.1071739881770608E+04,0,0,1,0,0,-1,0,0},
      {0.5193372353628770E-05,0.5396405225692853E+01,0.1439151134212730E+03,0,0,0,0,0,1,0,0},
      {0.4923588320704660E-05,0.5786945864275197E+00,0.2874214963438496E+03,0,0,0,0,0,2,0,0},
      {0.1514267591217036E-05,0.5287757340750888E+01,0.1530943251212127E+04,0,1,0,0,0,-1,0,0}
    };
    Series62=new double[][] {
      {0.1489184840960493E-02,0.4482905304402765E+01,0.1925543592528062E-02,0,0,0,0,0,0,0,0},
      {0.6277976937099409E-03,0.1641703069309713E+00,0.8931239595284827E-02,0,0,0,0,0,0,0,0},
      {0.2064532509871716E-03,0.2875895084979086E+01,0.4265982401557260E+00,0,0,0,0,0,0,0,0},
      {0.1839936992682087E-03,0.4432539236156053E+01,0.2132991200778630E+00,0,0,0,0,0,0,0,0},
      {0.3205222547434483E-04,0.2421050292728566E+01,0.6867993784180930E-02,0,0,0,0,0,0,0,0},
      {0.2910646188137447E-04,0.4133511734209461E+01,0.6398973602335890E+00,0,0,0,0,0,0,0,0},
      {0.2782832942434251E-04,0.2085876834041031E+01,0.1786772859246875E-01,0,0,0,0,0,0,0,0},
      {0.9861735003619475E-05,0.5820876772324451E+01,0.3851087185056125E-02,0,0,0,0,0,0,0,0},
      {0.6328208168488662E-05,0.1099595805258099E+01,0.2064679322415834E+00,0,0,0,0,0,0,0,0},
      {0.3918483314907732E-05,0.3102869921028134E+01,0.4355294797510108E+00,0,0,0,0,0,0,0,0},
      {0.3898032222540717E-05,0.2691928895904259E+01,0.1031868899480124E+00,0,0,0,0,0,0,0,0},
      {0.3675185983863103E-05,0.3132180625569890E+01,0.1102063824754091E+00,0,0,0,0,0,0,0,0},
      {0.3340295455803047E-05,0.3949193776134189E+01,0.4087305115632572E+00,0,0,0,0,0,0,0,0},
      {0.3080473554041300E-05,0.5390964128071579E+01,0.8531964803114520E+00,0,0,0,0,0,0,0,0},
      {0.2919923956899900E-05,0.2618006049618430E+01,0.4334294279920056E+00,0,0,0,0,0,0,0,0},
      {0.2671699222038788E-05,0.1780723999304769E-01,0.4197670523194464E+00,0,0,0,0,0,0,0,0},
      {0.1807246533364200E-05,0.5897892147256107E+01,0.3163918576803169E+00,0,0,0,0,0,0,0,0},
      {0.1012886232301891E-05,0.3824011892951753E+01,0.2201303079141426E+00,0,0,0,0,0,0,0,0},
      {0.1208825245395646E-04,0.1284546431278216E+01,0.3640852728872593E+03,0,0,0,0,1,-1,0,0},
      {0.6461479160208660E-05,0.3340990715837123E+01,0.6945868230739784E+03,0,0,0,1,0,-1,0,0},
      {0.6059629840959338E-05,0.2254812572103060E+01,0.1439151134212730E+03,0,0,0,0,0,1,0,0},
      {0.5747511089543420E-05,0.5786945864275197E+00,0.2874214963438496E+03,0,0,0,0,0,2,0,0},
      {0.4165211007064940E-05,0.3023166959941940E+00,0.1071739881770608E+04,0,0,1,0,0,-1,0,0},
      {0.3317890554439667E-05,0.5917531821903471E+01,0.7165405266799226E+00,0,0,0,0,0,-1,0,5},
      {0.3240885104844179E-05,0.1456814599107305E+01,0.7146149830873946E+00,0,0,0,0,0,-1,0,5},
      {0.3183322410669546E-05,0.3483641352342939E-01,0.7204899083316938E+00,0,0,0,0,0,-1,0,5},
      {0.2439949469786914E-05,0.4495498981484839E+01,0.7224154519242219E+00,0,0,0,0,0,-1,0,5},
      {0.2229788591597716E-05,0.3516065572948062E+01,0.7095813532095739E+00,0,0,0,0,0,-1,0,5},
      {0.2177885623014171E-05,0.5338524981350306E+01,0.7076558096170459E+00,0,0,0,0,0,-1,0,5},
      {0.1583596726276704E-05,0.1857323310303026E+01,0.7185643647391657E+00,0,0,0,0,0,-1,0,5},
      {0.8515242483441068E-06,0.4094907555655027E+01,0.7184660702724507E+00,0,0,0,0,0,-1,0,5},
      {0.8331051796244125E-06,0.2672845480380433E+01,0.7243409955167499E+00,0,0,0,0,0,-1,0,5},
      {0.1119995323971813E-05,0.5646772130696905E+01,0.2872081972302052E+03,0,0,0,0,0,2,0,0},
      {0.9999688627568780E-06,0.2569092862556433E+01,0.7281705457745186E+03,0,0,0,0,2,-2,0,0},
      {0.9325252754097003E-06,0.1465474667914254E+01,0.1435063829225766E+03,0,0,0,0,0,1,0,0},
      {0.8903963476562940E-06,0.2146164687161095E+01,0.1530943251212127E+04,0,1,0,0,0,-1,0,0}
    };
    Series63=new double[][] {
      {0.3059204611319566E-05,0.1991559296541939E+01,0.0000000000000000E+00,0,0,0,0,0,0,0,0},
      {0.2892653650392732E-01,0.2687601928257754E+01,0.8933864296234374E-02,0,0,0,0,0,0,0,0},
      {0.1921261556124706E-03,0.6049785198226078E+00,-0.8933864296234374E-02,0,0,0,0,0,0,0,0},
      {0.7446564554937326E-04,0.3484557927409254E+01,0.4176643758594916E+00,0,0,0,0,0,0,0,0},
      {0.2429402127836536E-04,0.4497027349870592E+01,0.7008320703706311E-02,0,0,0,0,0,0,0,0},
      {0.2391668026442592E-04,0.4004671318127588E+01,0.1085940788876244E-01,0,0,0,0,0,0,0,0},
      {0.1720731211496869E-04,0.3423177098527374E+01,0.1974690825885564E-02,0,0,0,0,0,0,0,0},
      {0.9603481577558545E-05,0.4751548744302498E+01,0.6309634959373547E+00,0,0,0,0,0,0,0,0},
      {0.6349090434417589E-05,0.5278059950506263E+01,0.2043652557816286E+00,0,0,0,0,0,0,0,0},
      {0.5704537193893060E-05,0.2441938711199521E+01,-0.1085940788876244E-01,0,0,0,0,0,0,0,0},
      {0.5578526024329263E-05,0.4763368404218852E+01,0.3900234418413626E-02,0,0,0,0,0,0,0,0},
      {0.2599144808715373E-05,0.6100137782540312E+01,-0.4176643758594916E+00,0,0,0,0,0,0,0,0},
      {0.2297673200703965E-05,0.4851969044359338E+01,-0.3900234418413626E-02,0,0,0,0,0,0,0,0},
      {0.2219325143437113E-05,0.3927500699309107E+00,-0.1786510389151920E-01,0,0,0,0,0,0,0,0},
      {0.2100942202805606E-05,0.2406466000093305E+01,0.4355321044519604E+00,0,0,0,0,0,0,0,0},
      {0.2030439241690526E-05,0.3465392760946876E+01,-0.1974690825885564E-02,0,0,0,0,0,0,0,0},
      {0.1840440310193102E-05,0.4554834665120421E+01,-0.2043652557816286E+00,0,0,0,0,0,0,0,0},
      {0.1818913752278572E-05,0.3943058897661665E+01,0.2222329843740974E+00,0,0,0,0,0,0,0,0},
      {0.1391334470388962E-05,0.3792208194559931E+01,0.1014313781444490E+00,0,0,0,0,0,0,0,0},
      {0.1359141741251292E-05,0.6222686370138073E+01,0.2132991200778630E+00,0,0,0,0,0,0,0,0},
      {0.1313668933428861E-05,0.5635555579481489E+01,-0.2222329843740974E+00,0,0,0,0,0,0,0,0},
      {0.1273579323673662E-05,0.4271696076744320E+01,-0.1278495148129050E-01,0,0,0,0,0,0,0,0},
      {0.1071090644258225E-05,0.6019617650063695E+01,0.8442626160152177E+00,0,0,0,0,0,0,0,0},
      {0.1054968278903017E-05,0.6106129826283189E+00,0.4108289309930330E+00,0,0,0,0,0,0,0,0},
      {0.1030286270548740E-05,0.3243359475417337E+01,0.4244861425113500E+00,0,0,0,0,0,0,0,0},
      {0.6687871156591785E-04,0.4936792307996836E+01,0.1439240472855692E+03,0,0,0,0,0,1,0,0},
      {0.1010084616284730E-04,0.6221338739275052E+01,0.5080093201728285E+03,0,0,0,0,1,0,0,0},
      {0.5269296403563900E-05,0.1994597716654372E+01,0.8385108703595477E+03,0,0,0,1,0,0,0,0},
      {0.4938696934118670E-05,0.3652245876718619E+01,-0.2201612256016901E+03,0,0,0,0,-1,2,0,0},
      {0.4930907396569310E-05,0.1216505067979523E+01,-0.1434974490582804E+03,0,0,0,0,0,-1,0,0},
      {0.3315192749055180E-05,0.5239109003991030E+01,0.1215663929056177E+04,0,0,1,0,0,0,0,0},
      {0.2596686176814385E-05,0.9084195729203095E+00,0.2878391607068422E+03,0,0,0,0,0,2,0,0},
      {0.2338446911029510E-05,0.1595801592159713E+01,-0.5506627757884092E+03,0,0,0,-1,0,2,0,0},
      {0.1363323740924180E-05,0.4634475612002642E+01,-0.9278158344850386E+03,0,0,-1,0,0,2,0,0},
      {0.9612769517722884E-06,0.2431612830889724E+01,-0.1432841499446360E+03,0,0,0,0,0,-1,0,0}
    };
    Series64=new double[][] {
      {0.5602364095311453E-02,0.3221498291239513E+01,0.0000000000000000E+00,0,0,0,0,0,0,0,0},
      {0.2789942947721349E-02,0.6204694901726296E+01,-0.8931239595284827E-02,0,0,0,0,0,0,0,0},
      {0.1312363309291625E-03,0.5044269214427242E+01,-0.1925543592528062E-02,0,0,0,0,0,0,0,0},
      {0.1125670790406430E-03,0.6084205141557698E+01,0.4265982401557260E+00,0,0,0,0,0,0,0,0},
      {0.1916668518784865E-04,0.5094974746907165E+01,-0.2132991200778630E+00,0,0,0,0,0,0,0,0},
      {0.1497943297409488E-04,0.1070503341744811E+01,0.6398973602335890E+00,0,0,0,0,0,0,0,0},
      {0.1144622908335464E-04,0.4298020835227772E+01,0.2132991200778630E+00,0,0,0,0,0,0,0,0},
      {0.1105889373330841E-04,0.3817225181469991E+01,-0.6831187836279569E-02,0,0,0,0,0,0,0,0},
      {0.9469793088277916E-05,0.3725645429506557E+01,-0.3851087185056125E-02,0,0,0,0,0,0,0,0},
      {0.6878606841089768E-05,0.4544829611068419E+01,0.1925543592528062E-02,0,0,0,0,0,0,0,0},
      {0.6072279735075281E-05,0.3088021134790829E+00,-0.4265982401557260E+00,0,0,0,0,0,0,0,0},
      {0.3672628251015072E-05,0.5291779482141999E+01,0.1786772859246875E-01,0,0,0,0,0,0,0,0},
      {0.3001306766151942E-05,0.2631624338898295E+00,0.8931239595284827E-02,0,0,0,0,0,0,0,0},
      {0.2508038681269874E-05,0.5838193085927095E+01,-0.1754676234867099E+00,0,0,0,0,0,0,0,0},
      {0.2453965972048404E-05,0.4844097342411929E+01,-0.1085678318781289E-01,0,0,0,0,0,0,0,0},
      {0.1777443612458054E-05,0.1373143790052677E+00,0.1102063824754091E+00,0,0,0,0,0,0,0,0},
      {0.1638627359387858E-05,0.2338023546843110E+01,0.8531964803114520E+00,0,0,0,0,0,0,0,0},
      {0.1619813827555800E-05,0.3201683403167966E+01,0.4197670523194464E+00,0,0,0,0,0,0,0,0},
      {0.1585471208377748E-05,0.5833093965190200E+01,0.4334294279920056E+00,0,0,0,0,0,0,0,0},
      {0.1518975368269692E-05,0.3329608496168708E+01,0.6831187836279569E-02,0,0,0,0,0,0,0,0},
      {0.1151714556320695E-05,0.2625783233864042E-01,0.4355294797510108E+00,0,0,0,0,0,0,0,0},
      {0.8830894877995539E-06,0.5372467623946739E+01,-0.6398973602335890E+00,0,0,0,0,0,0,0,0}
    };
  }
  private static void SetupTable8() {
    Series81=new double[][]{
      {0.4931880677088688E-03,0.3141592653589793E+01,0.0000000000000000E+00,0,0,0,0,0,0,0,0},
      {0.1422582505896866E-02,0.1629074651441286E+01,0.1149955249555075E+03,0,0,0,0,0,1,0,-1},
      {0.1282760621734726E-04,0.2948174134620363E+01,0.1149974504991001E+03,0,0,0,0,0,1,0,-1},
      {0.1173808537373678E-04,0.3099779317070623E+00,0.1149935994119150E+03,0,0,0,0,0,1,0,-1},
      {0.8613770793559659E-06,0.4998444131476766E+01,0.1150044561951028E+03,0,0,0,0,0,1,0,-1},
      {0.4431013807987148E-06,0.5376468530544894E+00,0.1150025306515103E+03,0,0,0,0,0,1,0,-1},
      {0.1039093910223160E-03,0.3603730645243327E+01,0.5743044643283454E+02,0,0,0,0,0,0,0,2},
      {0.1318221383295526E-04,0.3603627895388855E+01,0.5743044643283454E+02,0,0,0,0,0,0,0,2},
      {0.6634921396309160E-05,0.1781167344645605E+01,0.5743237197642707E+02,0,0,0,0,0,0,0,2},
      {0.2219668063709623E-05,0.2284631982621601E+01,0.5742852088924202E+02,0,0,0,0,0,0,0,2},
      {0.2219668063709623E-05,0.4922829307865055E+01,0.5743237197642707E+02,0,0,0,0,0,0,0,2},
      {0.7061098947712363E-06,0.1781063076666148E+01,0.5743237197642707E+02,0,0,0,0,0,0,0,2},
      {0.6897439873031985E-06,0.3603531212414229E+01,0.5743044643283454E+02,0,0,0,0,0,0,0,2},
      {0.4815890873838023E-04,0.4857380043863779E+01,0.8606897731627176E+02,0,0,0,0,0,1,0,-2},
      {0.2261000099791683E-05,0.4144773749632801E+01,0.8607593648974211E+02,0,0,0,0,0,1,0,-2},
      {0.1539010606612464E-05,0.1349751065508437E+01,0.8606502793461998E+02,0,0,0,0,0,1,0,-2},
      {0.1205192931267955E-05,0.2446685865249762E-01,0.8606310239102744E+02,0,0,0,0,0,1,0,-2},
      {0.1049405056557275E-05,0.6205868207753926E+01,0.8607090285986429E+02,0,0,0,0,0,1,0,-2},
      {0.6346855400965112E-06,0.4208242392372787E+01,0.8649162617477570E+02,0,0,0,0,0,1,0,-2},
      {0.5125234375859194E-06,0.6347454751688458E+00,0.8606700262544587E+02,0,0,0,0,0,1,0,-2},
      {0.3791140650423246E-04,0.3877892946988424E+01,0.2589106383767805E+03,0,0,0,0,0,2,0,-1},
      {0.3479701758905445E-04,0.2913621055460106E+01,0.4790807978427668E+03,0,0,0,0,1,0,0,-1},
      {0.2109797808204992E-04,0.6169137529926802E+01,0.2892654763923579E+02,0,0,0,0,0,0,0,1},
      {0.1178034971556374E-04,0.3767316688774966E+01,0.2891958846576544E+02,0,0,0,0,0,0,0,1},
      {0.1260892982792229E-05,0.2865370330929173E+00,0.2893049702088756E+02,0,0,0,0,0,0,0,1},
      {0.1105411287499222E-05,0.3428232110024022E+01,0.2893049702088756E+02,0,0,0,0,0,0,0,1},
      {0.7170812904592723E-06,0.3420913120960960E+01,0.2893049702088756E+02,0,0,0,0,0,0,0,1},
      {0.6488168332435312E-06,0.4747272264327228E+01,0.2893242256448008E+02,0,0,0,0,0,0,0,1},
      {0.5615646064758847E-06,0.4746191819098314E+01,0.2893242256448008E+02,0,0,0,0,0,0,0,1},
      {0.2035144206960700E-04,0.2388622882333127E+01,0.5721714731919014E+02,0,0,0,0,0,0,0,2},
      {0.2926604917703857E-05,0.2388520458991813E+01,0.5721714731919014E+02,0,0,0,0,0,0,0,2},
      {0.1506081447204865E-05,0.5660579539606869E+00,0.5721907286278267E+02,0,0,0,0,0,0,0,2},
      {0.1870537893668915E-04,0.4970065340019012E+01,0.8095823480294860E+03,0,0,0,1,0,0,0,-1},
      {0.1638556362699022E-04,0.3120590560712127E+01,0.1728544951592234E+03,0,0,0,0,0,1,0,1},
      {0.1529823976752019E-04,0.4943058008729855E+01,0.1728525696156309E+03,0,0,0,0,0,1,0,1},
      {0.4393561481041129E-05,0.1297974022507647E+01,0.1728564207028159E+03,0,0,0,0,0,1,0,1},
      {0.5965719593399348E-06,0.5170692394242208E+01,0.1728615008552262E+03,0,0,0,0,0,1,0,1},
      {0.1557003369506093E-04,0.3258149248363778E+01,0.2299910499110151E+03,0,0,0,0,0,2,0,-2},
      {0.1199902334914149E-05,0.8563423202419649E+00,0.2299840907375447E+03,0,0,0,0,0,2,0,-2},
      {0.1228186282641710E-04,0.1724889079832807E+00,0.5785704466012334E+02,0,0,0,0,0,0,0,2},
      {0.1176258023998037E-04,0.3314184332779403E+01,0.5785704466012334E+02,0,0,0,0,0,0,0,2},
      {0.6557351705394024E-05,0.1491516764202223E+01,0.5785897020371587E+02,0,0,0,0,0,0,0,2},
      {0.6405368264798522E-05,0.3313984899950305E+01,0.5785704466012334E+02,0,0,0,0,0,0,0,2},
      {0.6172682903699512E-05,0.4633213794765795E+01,0.5785897020371587E+02,0,0,0,0,0,0,0,2},
      {0.1849113934605446E-05,0.5952077755114058E+01,0.5786089574730840E+02,0,0,0,0,0,0,0,2},
      {0.7233685566797959E-06,0.5117268855773154E+00,0.5784613610500123E+02,0,0,0,0,0,0,0,2},
      {0.6553814036784504E-06,0.2913580261178442E+01,0.5785309527847157E+02,0,0,0,0,0,0,0,2},
      {0.1129835111020686E-04,0.1931391320176082E+01,0.1186735406726115E+04,0,0,1,0,0,0,0,-1},
      {0.8894451926405188E-05,0.3717778422487947E+01,0.2850389879359876E+02,0,0,0,0,0,0,0,1},
      {0.1279765155817002E-05,0.3717675999146635E+01,0.2850389879359876E+02,0,0,0,0,0,0,0,1},
      {0.6585278743116177E-06,0.1895225923201359E+01,0.2850582433719129E+02,0,0,0,0,0,0,0,1},
      {0.4329731043745186E-05,0.1745631218955876E+01,0.3449865748665226E+03,0,0,0,0,0,3,0,-3},
      {0.3599154632304914E-05,0.2775939218332986E+01,0.7165405266799226E+00,0,0,0,0,0,-1,0,5},
      {0.3515734994495499E-05,0.4598407354081068E+01,0.7146149830873946E+00,0,0,0,0,0,-1,0,5},
      {0.3478141777876746E-05,0.3176429222382267E+01,0.7204899083316938E+00,0,0,0,0,0,-1,0,5},
      {0.2665836454376912E-05,0.1353906429920962E+01,0.7224154519242219E+00,0,0,0,0,0,-1,0,5},
      {0.2424687512275475E-05,0.3741587517563335E+00,0.7095813532095739E+00,0,0,0,0,0,-1,0,5},
      {0.2368323695654563E-05,0.2196618138085234E+01,0.7076558096170459E+00,0,0,0,0,0,-1,0,5},
      {0.1730312424825915E-05,0.4998916170228774E+01,0.7185643647391657E+00,0,0,0,0,0,-1,0,5},
      {0.9236794241568811E-06,0.9533149020652341E+00,0.7184660702724507E+00,0,0,0,0,0,-1,0,5},
      {0.9101734358180643E-06,0.5814438133970226E+01,0.7243409955167499E+00,0,0,0,0,0,-1,0,5},
      {0.6223980017737441E-06,0.4834746214664873E+01,0.7115068968021020E+00,0,0,0,0,0,-1,0,5},
      {0.5354701955778437E-06,0.1796193334454873E+01,0.7037064279652747E+00,0,0,0,0,0,-1,0,5},
      {0.5065090050950890E-06,0.4255501070028474E+01,0.7026221797392250E+00,0,0,0,0,0,-1,0,5},
      {0.4946872838108632E-06,0.6077953687111643E+01,0.7006966361466970E+00,0,0,0,0,0,-1,0,5},
      {0.3458158840283551E-05,0.3480902144089144E+00,0.8635699407207032E+02,0,0,0,0,0,0,0,3},
      {0.3397716237243428E-05,0.1677245754563736E+01,0.5764374554647894E+02,0,0,0,0,0,0,0,2},
      {0.1244982526944973E-05,0.5240566800117683E+01,0.5764374554647894E+02,0,0,0,0,0,0,0,2},
      {0.1091462762348523E-05,0.2099076569869200E+01,0.5764374554647894E+02,0,0,0,0,0,0,0,2},
      {0.6406895152069824E-06,0.3418104295086557E+01,0.5764567109007147E+02,0,0,0,0,0,0,0,2},
      {0.3248586724047530E-05,0.1173515119422923E+01,0.5700384820554574E+02,0,0,0,0,0,0,0,2},
      {0.2861462370993798E-05,0.2306043720186055E+00,0.2010645022717792E+03,0,0,0,0,0,2,0,-3},
      {0.1182264841666031E-05,0.2632429282009304E+01,0.2010714614452496E+03,0,0,0,0,0,2,0,-3},
      {0.2055029475492061E-05,0.2502670659577745E+01,0.2829059967995436E+02,0,0,0,0,0,0,0,1},
      {0.1822472901105999E-05,0.1375578599796670E+00,0.5713655475179166E+02,0,0,0,0,0,1,0,-3},
      {0.1780232418309521E-05,0.4598275031411171E+01,0.5713848029538418E+02,0,0,0,0,0,1,0,-3},
      {0.1648047287035982E-05,0.1857191416947010E+01,0.5714242967703596E+02,0,0,0,0,0,1,0,-3},
      {0.7820099241529981E-06,0.1117332070337260E+01,0.5714938885050630E+02,0,0,0,0,0,1,0,-3},
      {0.4677155865229010E-06,0.1960182176247418E+01,0.5713462920819913E+02,0,0,0,0,0,1,0,-3},
      {0.1430384079869284E-05,0.2331131895479707E+00,0.4599820998220301E+03,0,0,0,0,0,4,0,-4},
      {0.1235836620763296E-05,0.1387597018758499E+01,0.5807034377376775E+02,0,0,0,0,0,0,0,2},
      {0.1083444644978050E-05,0.4529292095689605E+01,0.5807034377376775E+02,0,0,0,0,0,0,0,2},
      {0.6359828738920669E-06,0.5848319820906959E+01,0.5807226931736027E+02,0,0,0,0,0,0,0,2},
      {0.1228324287730070E-05,0.6138699763037207E+01,0.4028257517980534E+03,0,0,0,0,0,3,0,-1},
      {0.1080732052994442E-05,0.6169261486294519E+01,0.4501542502035310E+03,0,0,0,0,1,0,0,-2},
      {0.9898839492003675E-06,0.5001271649790288E+01,0.3160600272272868E+03,0,0,0,0,0,3,0,-4},
      {0.8563952066234920E-06,0.3157716115970889E+01,0.2406215774107413E+04,1,0,0,0,0,0,0,-1},
      {0.8163925621658264E-06,0.3775239311342985E+01,0.1645938776167635E+04,0,1,0,0,0,0,0,-1},
      {0.7902043182815145E-06,0.6166128250563959E+01,0.5828364288741214E+02,0,0,0,0,0,0,0,2},
      {0.6957733951844473E-06,0.1201970451738299E+01,0.5828556843100467E+02,0,0,0,0,0,0,0,2},
      {0.6796470625999134E-06,0.3024438587486380E+01,0.5828364288741214E+02,0,0,0,0,0,0,0,2},
      {0.7283955691107278E-06,0.3393198290814080E+01,0.2821000711255587E+02,0,0,0,0,0,1,0,-4},
      {0.7115131339933769E-06,0.1570730155065997E+01,0.2821193265614840E+02,0,0,0,0,0,1,0,-4},
      {0.6748302380368017E-06,0.5416167758678300E+01,0.8614369495842593E+02,0,0,0,0,0,0,0,3},
      {0.5801464486513693E-06,0.4405137819662329E+01,0.5369397680464826E+03,0,0,0,0,1,0,0,1},
      {0.5667000670514980E-06,0.6227605955410412E+01,0.5369378425028901E+03,0,0,0,0,1,0,0,1},
      {0.5703165858434648E-06,0.5375403960487173E+01,0.3167696085804964E+03,0,0,0,0,0,2,0,1},
      {0.5570980365205931E-06,0.9146867890556700E+00,0.3167676830369039E+03,0,0,0,0,0,2,0,1},
      {0.5433130776641635E-06,0.1942520463673839E+01,0.7806558003902502E+03,0,0,0,1,0,0,0,-2},
      {0.4421327735883800E-06,0.5003780467319657E+01,0.5749776247775377E+03,0,0,0,0,0,5,0,-5}
    };
    Series82=new double[][]{
      {0.1928386916598716E+00,0.1316743285171985E+01,0.1925543592528062E-02,0,0,0,0,0,0,0,0},
      {0.1197700127979115E-02,0.2905816296579413E+01,0.3851087185056125E-02,0,0,0,0,0,0,0,0},
      {0.1125807495942191E-02,0.4414124516924016E+01,0.2132991200778630E+00,0,0,0,0,0,0,0,0},
      {0.7466410913376219E-03,0.3411600296951643E+01,0.8931239595284827E-02,0,0,0,0,0,0,0,0},
      {0.3004295676379663E-03,0.4182760326808072E+01,0.4285237837482541E+00,0,0,0,0,0,0,0,0},
      {0.2400461005136443E-03,0.2926349976058209E+01,0.4265982401557260E+00,0,0,0,0,0,0,0,0},
      {0.1785444267015284E-03,0.3897159948941672E+01,0.6919778790613740E-02,0,0,0,0,0,0,0,0},
      {0.7393695567545098E-04,0.5503253064658139E+01,0.4304493273407821E+00,0,0,0,0,0,0,0,0},
      {0.5283085924212965E-04,0.6218635238921890E+01,0.2113735764853349E+00,0,0,0,0,0,0,0,0},
      {0.4076652529480639E-04,0.3776409831511328E+01,0.1083538245527340E-01,0,0,0,0,0,0,0,0},
      {0.4028991254502335E-04,0.1095989272623461E+01,0.2064679322415834E+00,0,0,0,0,0,0,0,0},
      {0.3811816549252109E-04,0.5449835843337831E+01,0.6418229038261171E+00,0,0,0,0,0,0,0,0},
      {0.3620254646473473E-04,0.6177614480595389E+00,0.1282203186788430E-01,0,0,0,0,0,0,0,0},
      {0.3586013723690129E-04,0.2436062381089369E+01,0.2152246636703911E+00,0,0,0,0,0,0,0,0},
      {0.3490441837523331E-04,0.4159104921593594E+01,0.6398973602335890E+00,0,0,0,0,0,0,0,0},
      {0.2377227624569038E-04,0.3239254695447169E+01,0.1031868899480124E+00,0,0,0,0,0,0,0,0},
      {0.2148685811756090E-04,0.3324109814821024E+01,0.2002818098055070E-01,0,0,0,0,0,0,0,0},
      {0.2023635272434820E-04,0.5342652755830533E+01,0.1786772859246875E-01,0,0,0,0,0,0,0,0},
      {0.1603086203623188E-04,0.2485710326866144E+01,0.4226488585039549E+00,0,0,0,0,0,0,0,0},
      {0.1314054216207817E-04,0.5873663787672587E+01,0.3163918576803169E+00,0,0,0,0,0,0,0,0},
      {0.9878270828624305E-05,0.4692950541308903E+00,0.6437484474186451E+00,0,0,0,0,0,0,0,0},
      {0.7459887995646048E-05,0.4087393199562610E+01,0.2201303079141426E+00,0,0,0,0,0,0,0,0},
      {0.7282549735894645E-03,0.4770667297372782E+01,0.1149955249555075E+03,0,0,0,0,0,1,0,-1},
      {0.3358151723062283E-05,0.6089767182911175E+01,0.1149974504991001E+03,0,0,0,0,0,1,0,-1},
      {0.2940137697333849E-05,0.3451571632901731E+01,0.1149935994119150E+03,0,0,0,0,0,1,0,-1},
      {0.3257749851873783E-06,0.1856841516027055E+01,0.1150044561951028E+03,0,0,0,0,0,1,0,-1},
      {0.1226877582046494E-03,0.3603730645243328E+01,0.5743044643283454E+02,0,0,0,0,0,0,0,2},
      {0.1442654202758791E-04,0.3603627947367320E+01,0.5743044643283454E+02,0,0,0,0,0,0,0,2},
      {0.7287158009695637E-05,0.1781167080644662E+01,0.5743237197642707E+02,0,0,0,0,0,0,0,2},
      {0.2227388462533576E-05,0.4922829307865055E+01,0.5743237197642707E+02,0,0,0,0,0,0,0,2},
      {0.2227388462533576E-05,0.2284631982621601E+01,0.5742852088924202E+02,0,0,0,0,0,0,0,2},
      {0.7085743939411694E-06,0.1781063076666148E+01,0.5743237197642707E+02,0,0,0,0,0,0,0,2},
      {0.6921513653852260E-06,0.3603531212414229E+01,0.5743044643283454E+02,0,0,0,0,0,0,0,2},
      {0.1169604863611512E-03,0.2901894204038109E+01,0.7165405266799226E+00,0,0,0,0,0,-1,0,5},
      {0.1078338305442189E-03,0.4729254738533461E+01,0.7146149830873946E+00,0,0,0,0,0,-1,0,5},
      {0.8881380857833267E-04,0.3150761163748690E+01,0.7204899083316938E+00,0,0,0,0,0,-1,0,5},
      {0.8075655730402778E-04,0.4519505673931613E+00,0.7095813532095739E+00,0,0,0,0,0,-1,0,5},
      {0.7285893683141579E-04,0.2281556670811775E+01,0.7076558096170459E+00,0,0,0,0,0,-1,0,5},
      {0.7271303455844460E-04,0.1353835388413780E+01,0.7224154519242219E+00,0,0,0,0,0,-1,0,5},
      {0.3531961223297060E-04,0.4936014332687674E+01,0.7185643647391657E+00,0,0,0,0,0,-1,0,5},
      {0.3019281277001780E-04,0.1123239346047937E+01,0.7184660702724507E+00,0,0,0,0,0,-1,0,5},
      {0.2328751291389269E-04,0.5814424528754509E+01,0.7243409955167499E+00,0,0,0,0,0,-1,0,5},
      {0.2186693735333124E-04,0.1995274806149824E+01,0.7037064279652747E+00,0,0,0,0,0,-1,0,5},
      {0.1672869957989543E-04,0.4866436698742614E+01,0.7115068968021020E+00,0,0,0,0,0,-1,0,5},
      {0.1475074400837350E-04,0.5719796115875051E+01,0.6967472544949258E+00,0,0,0,0,0,-1,0,5},
      {0.1399238708843930E-04,0.4239759216446882E+01,0.7026221797392250E+00,0,0,0,0,0,-1,0,5},
      {0.1179423115340822E-04,0.6061718986149902E+01,0.7006966361466970E+00,0,0,0,0,0,-1,0,5},
      {0.8812132213666628E-05,0.1185788445220376E+01,0.7185152175058083E+00,0,0,0,0,0,-1,0,5},
      {0.8507522187621048E-05,0.3014198965814164E+01,0.7165896739132802E+00,0,0,0,0,0,-1,0,5},
      {0.6197295348292014E-05,0.3290476399266866E+01,0.7294211479269787E+00,0,0,0,0,0,-1,0,5},
      {0.5971965491672709E-05,0.8753490785501354E+00,0.7057302660245178E+00,0,0,0,0,0,-1,0,5},
      {0.5336777443382800E-05,0.3277820916335360E+01,0.7126894394948665E+00,0,0,0,0,0,-1,0,5},
      {0.3985256112150306E-05,0.3275634661668579E+01,0.6897880810245771E+00,0,0,0,0,0,-1,0,5},
      {0.3608057298728251E-05,0.4863591915141639E+01,0.7235462226826794E+00,0,0,0,0,0,-1,0,5},
      {0.3299741975330665E-05,0.4703710238329596E+01,0.7115560440354595E+00,0,0,0,0,0,-1,0,5},
      {0.3223198173228014E-05,0.2430574184290058E+00,0.7096305004429314E+00,0,0,0,0,0,-1,0,5},
      {0.2688244928253836E-05,0.5275014486169524E+01,0.2919661245834397E+00,0,0,0,0,0,-1,0,5},
      {0.2613243661620941E-05,0.2424238476169638E+01,0.7165870492123307E+00,0,0,0,0,0,-1,0,5},
      {0.2574587837723788E-05,0.5732598943257583E+01,0.7116051912688169E+00,0,0,0,0,0,-1,0,5},
      {0.2569260340418719E-05,0.3452633751557773E+01,0.2938916681759678E+00,0,0,0,0,0,-1,0,5},
      {0.2549155479604930E-05,0.5291014601965657E+01,0.7204407610983363E+00,0,0,0,0,0,-1,0,5},
      {0.2330447358044871E-05,0.2432875200820899E+01,0.7045477233317531E+00,0,0,0,0,0,-1,0,5},
      {0.2254199615438521E-05,0.6141816327520342E+01,0.7056811187911602E+00,0,0,0,0,0,-1,0,5},
      {0.2089500807780357E-05,0.1580270469414437E+01,0.7313466915195067E+00,0,0,0,0,0,-1,0,5},
      {0.2042389544816678E-05,0.5291730688852117E+01,0.7274956043344506E+00,0,0,0,0,0,-1,0,5},
      {0.1915584168637660E-05,0.6159110785889812E+01,0.7134324403946301E+00,0,0,0,0,0,-1,0,5},
      {0.1876817015958310E-05,0.4754449361520011E+00,0.7017808843727467E+00,0,0,0,0,0,-1,0,5},
      {0.1747595341910651E-05,0.4598506696879712E+01,0.7146149830873946E+00,0,0,0,0,0,-1,0,5},
      {0.1576239906932032E-05,0.3002295562826074E+01,0.7254717662752075E+00,0,0,0,0,0,-1,0,5},
      {0.1511339171430308E-05,0.4356355818907831E+01,0.6948217109023977E+00,0,0,0,0,0,-1,0,5},
      {0.1499811815760443E-05,0.3988002288677764E+01,0.7262665391092780E+00,0,0,0,0,0,-1,0,5},
      {0.1460797854077857E-05,0.3748973474201665E+01,0.7293746253945707E+00,0,0,0,0,0,-1,0,5},
      {0.1415742853315232E-05,0.4757124368944763E+01,0.6987710925541689E+00,0,0,0,0,0,-1,0,5},
      {0.1368009711983665E-05,0.1050560920047950E+01,0.2869324947056190E+00,0,0,0,0,0,-1,0,5},
      {0.1352657464923885E-05,0.2873044246870902E+01,0.2850069511130909E+00,0,0,0,0,0,-1,0,5},
      {0.1234180648016043E-05,0.3899910056343316E+01,0.7135307348613450E+00,0,0,0,0,0,-1,0,5},
      {0.1216707168547837E-05,0.7209469663619112E+00,0.6986727980874539E+00,0,0,0,0,0,-1,0,5},
      {0.1191488030171526E-05,0.2272066757023249E+01,0.7203916138649787E+00,0,0,0,0,0,-1,0,5},
      {0.1095526621882867E-05,0.1933069997683063E+01,0.7313001689870987E+00,0,0,0,0,0,-1,0,5},
      {0.1040382661140688E-05,0.2776032023831034E+01,0.7165405266799226E+00,0,0,0,0,0,-1,0,5},
      {0.1036863039010391E-05,0.1630081068326518E+01,0.2958172117684958E+00,0,0,0,0,0,-1,0,5},
      {0.9616747267908099E-06,0.6000060288823683E+00,0.7185125928048588E+00,0,0,0,0,0,-1,0,5},
      {0.9373769411023921E-06,0.2775839264562756E+01,0.7165405266799226E+00,0,0,0,0,0,-1,0,5},
      {0.9196878775751337E-06,0.2473094150998370E+01,0.2810575694613199E+00,0,0,0,0,0,-1,0,5},
      {0.8755936386396388E-06,0.3115210303371935E+01,0.7056319715578028E+00,0,0,0,0,0,-1,0,5},
      {0.8603158836863158E-06,0.4197919449469832E+01,0.7106656014356235E+00,0,0,0,0,0,-1,0,5},
      {0.6705489554710231E-06,0.2196721921176780E+01,0.7076558096170459E+00,0,0,0,0,0,-1,0,5},
      {0.6185477101046089E-06,0.3740148248275139E+00,0.7095813532095739E+00,0,0,0,0,0,-1,0,5},
      {0.6153101453617282E-06,0.4598315612349802E+01,0.7146149830873946E+00,0,0,0,0,0,-1,0,5},
      {0.6144966414607280E-06,0.2242399219818416E-01,0.7096278757419818E+00,0,0,0,0,0,-1,0,5},
      {0.5627708927914463E-06,0.5539620791805293E+01,0.7274490818020426E+00,0,0,0,0,0,-1,0,5},
      {0.5610129338334513E-06,0.3684122942793509E+01,0.7166388211466377E+00,0,0,0,0,0,-1,0,5},
      {0.5398713831902744E-06,0.5177746978176391E+01,0.7234997001502714E+00,0,0,0,0,0,-1,0,5},
      {0.5273506284484312E-06,0.7170338045570530E+00,0.7215741565577434E+00,0,0,0,0,0,-1,0,5},
      {0.5262843436434110E-06,0.2183142522975625E+01,0.8059254809810479E-01,0,0,0,0,0,-1,0,5},
      {0.5227495470767936E-06,0.4005663121141773E+01,0.7866700450557673E-01,0,0,0,0,0,-1,0,5},
      {0.5199527236321857E-06,0.3176522564528122E+01,0.7204899083316938E+00,0,0,0,0,0,-1,0,5},
      {0.4804692875682566E-06,0.9532931667972451E+00,0.7184660702724507E+00,0,0,0,0,0,-1,0,5},
      {0.4764703494829237E-06,0.3176626472799736E+01,0.7204899083316938E+00,0,0,0,0,0,-1,0,5},
      {0.4753480849396105E-06,0.1353820721455883E+01,0.7224154519242219E+00,0,0,0,0,0,-1,0,5},
      {0.4670984240146942E-06,0.3176334540097546E+01,0.7204899083316938E+00,0,0,0,0,0,-1,0,5},
      {0.4654269273348351E-06,0.4999094608547819E+01,0.7185643647391657E+00,0,0,0,0,0,-1,0,5},
      {0.4614651554236785E-06,0.4267748340354923E+01,0.7134842123289370E+00,0,0,0,0,0,-1,0,5},
      {0.4531105084562628E-06,0.6090353248906123E+01,0.7115586687364089E+00,0,0,0,0,0,-1,0,5},
      {0.4077887373315127E-06,0.5689822989499520E+01,0.7076092870846378E+00,0,0,0,0,0,-1,0,5},
      {0.4060252228628880E-06,0.2196491172614561E+01,0.7076558096170459E+00,0,0,0,0,0,-1,0,5},
      {0.3973724314685021E-06,0.4140593867861234E+00,0.2860911993391405E+00,0,0,0,0,0,-1,0,5},
      {0.3969393749935304E-06,0.8738771354003039E+00,0.6828289075542282E+00,0,0,0,0,0,-1,0,5},
      {0.3815771898913724E-06,0.7105474585412466E-01,0.2740983959909710E+00,0,0,0,0,0,-1,0,5},
      {0.3814316630052462E-06,0.3288005910176154E+01,0.7006501136142891E+00,0,0,0,0,0,-1,0,5},
      {0.3799033641308746E-06,0.4259130445062984E+01,0.7255235382095145E+00,0,0,0,0,0,-1,0,5},
      {0.3480378535316622E-06,0.5511199893486148E+01,0.2888580382981470E+00,0,0,0,0,0,-1,0,5},
      {0.3472239690061985E-06,0.1735637572758064E+01,0.2880167429316686E+00,0,0,0,0,0,-1,0,5},
      {0.3451429900993856E-06,0.3742836957045070E+00,0.7095813532095739E+00,0,0,0,0,0,-1,0,5},
      {0.3418187177414854E-06,0.8141915154020281E+00,0.2900405809909117E+00,0,0,0,0,0,-1,0,5},
      {0.3413057991197158E-06,0.4998998912315168E+01,0.7185643647391657E+00,0,0,0,0,0,-1,0,5},
      {0.3242171175776111E-06,0.3907553066682691E+01,0.7255700607419225E+00,0,0,0,0,0,-1,0,5},
      {0.3187588107072781E-06,0.6035967805962264E+01,0.7332722351120348E+00,0,0,0,0,0,-1,0,5},
      {0.3170476726805622E-06,0.4834654034241590E+01,0.7115068968021020E+00,0,0,0,0,0,-1,0,5},
      {0.3140486507800807E-06,0.4482050013324328E+01,0.7115534193345099E+00,0,0,0,0,0,-1,0,5},
      {0.3075011314359275E-06,0.3644290442329441E+01,0.6987219453208114E+00,0,0,0,0,0,-1,0,5},
      {0.3070012836628351E-06,0.5229794370811791E+01,0.7154562784538731E+00,0,0,0,0,0,-1,0,5},
      {0.3062931422970319E-06,0.3916284298317819E+01,0.7135307348613450E+00,0,0,0,0,0,-1,0,5},
      {0.3033297996664303E-06,0.6195403829536804E+01,0.2899422865241966E+00,0,0,0,0,0,-1,0,5},
      {0.2941694888718857E-06,0.3751626473924576E+01,0.7064732669242811E+00,0,0,0,0,0,-1,0,5},
      {0.2637183491724404E-04,0.4683474110615003E+01,0.1439220725947433E+03,0,0,0,0,0,1,0,0},
      {0.8279548593975801E-06,0.1908398224503419E+01,0.1439260219763951E+03,0,0,0,0,0,1,0,0},
      {0.6484210285032441E-06,0.3233671623826318E+01,0.1439279475199876E+03,0,0,0,0,0,1,0,0},
      {0.3741206194503089E-06,0.3334420556287444E+01,0.1439201470511508E+03,0,0,0,0,0,1,0,0},
      {0.3414474608395144E-06,0.5333092134829300E+01,0.1434994237362394E+03,0,0,0,0,0,1,0,0},
      {0.2403370419637644E-04,0.2388622882333125E+01,0.5721714731919014E+02,0,0,0,0,0,0,0,2},
      {0.3186149545425659E-05,0.2388520458991813E+01,0.5721714731919014E+02,0,0,0,0,0,0,0,2},
      {0.1639647596215571E-05,0.5660579539606869E+00,0.5721907286278267E+02,0,0,0,0,0,0,0,2},
      {0.4360169268782757E-06,0.3707721544954854E+01,0.5721907286278267E+02,0,0,0,0,0,0,0,2},
      {0.4360169268782757E-06,0.1069524219711398E+01,0.5721522177559761E+02,0,0,0,0,0,0,0,2},
      {0.2222879678616121E-04,0.7364334077752746E+00,0.2589106383767805E+03,0,0,0,0,0,2,0,-1},
      {0.2181721459549961E-04,0.6055213709049899E+01,0.4790807978427668E+03,0,0,0,0,1,0,0,-1},
      {0.2037342614731350E-04,0.3686574112838511E+01,0.2850389879359876E+02,0,0,0,0,0,0,0,1},
      {0.2578067662242833E-05,0.3717675999146635E+01,0.2850389879359876E+02,0,0,0,0,0,0,0,1},
      {0.1326594499716358E-05,0.1895225923265205E+01,0.2850582433719129E+02,0,0,0,0,0,0,0,1},
      {0.8593669702293824E-06,0.1756009585606166E+00,0.2849994941194699E+02,0,0,0,0,0,0,0,1},
      {0.8157291503991916E-06,0.3317091188809099E+01,0.2849994941194699E+02,0,0,0,0,0,0,0,1},
      {0.8133023485324827E-06,0.5140435273709099E+01,0.2849802386835446E+02,0,0,0,0,0,0,0,1},
      {0.8091467686328637E-06,0.6119791853597793E+01,0.2851085796706911E+02,0,0,0,0,0,0,0,1},
      {0.6861953263277108E-06,0.5038406921824529E+01,0.2850582433719129E+02,0,0,0,0,0,0,0,1},
      {0.5172131934534886E-06,0.1859239820337615E+00,0.2849994941194699E+02,0,0,0,0,0,0,0,1},
      {0.4418741307932034E-06,0.1997951520656880E+01,0.2849802386835446E+02,0,0,0,0,0,0,0,1},
      {0.3504383346317222E-06,0.2398679759866219E+01,0.2850197325000623E+02,0,0,0,0,0,0,0,1},
      {0.3059127278557355E-06,0.3041311214209734E+01,0.2892654765210271E+02,0,0,0,0,0,0,0,1},
      {0.1769563612146425E-04,0.3258149248363778E+01,0.2299910499110151E+03,0,0,0,0,0,2,0,-2},
      {0.6546227444825897E-06,0.4577247910985506E+01,0.2299929754546076E+03,0,0,0,0,0,2,0,-2},
      {0.6546227444825897E-06,0.1939050585742052E+01,0.2299891243674225E+03,0,0,0,0,0,2,0,-2},
      {0.1381584330925251E-04,0.3314184332779403E+01,0.5785704466012334E+02,0,0,0,0,0,0,0,2},
      {0.1340170537299423E-04,0.1724889636822213E+00,0.5785704466012334E+02,0,0,0,0,0,0,0,2},
      {0.6761318001505776E-05,0.4633213511110228E+01,0.5785897020371587E+02,0,0,0,0,0,0,0,2},
      {0.3673272656623667E-06,0.1491416810431993E+01,0.5785897020371587E+02,0,0,0,0,0,0,0,2},
      {0.1184715424009358E-04,0.1828472686429220E+01,0.8095823480294860E+03,0,0,0,1,0,0,0,-1},
      {0.1029453478899256E-04,0.3120590331336322E+01,0.1728544951592234E+03,0,0,0,0,0,1,0,1},
      {0.9488209354297983E-05,0.4943057563632108E+01,0.1728525696156309E+03,0,0,0,0,0,1,0,1},
      {0.2792746040615356E-05,0.1297967072116361E+01,0.1728564207028159E+03,0,0,0,0,0,1,0,1},
      {0.4374967901448417E-06,0.5170827648758129E+01,0.1728615008552262E+03,0,0,0,0,0,1,0,1},
      {0.7269853312811282E-05,0.5072983973765877E+01,0.1186735406726115E+04,0,0,1,0,0,0,0,-1},
      {0.6013024933189873E-05,0.1743122401426509E+01,0.8606897731627176E+02,0,0,0,0,0,1,0,-2},
      {0.4958075120563441E-05,0.4144929329850513E+01,0.8607593648974211E+02,0,0,0,0,0,1,0,-2},
      {0.4353564226390001E-05,0.6257381704689877E+00,0.2891958846576544E+02,0,0,0,0,0,0,0,1},
      {0.2394532800288429E-05,0.3428232110024023E+01,0.2893049702088756E+02,0,0,0,0,0,0,0,1},
      {0.2183715272262793E-05,0.2865370330929173E+00,0.2893049702088756E+02,0,0,0,0,0,0,0,1},
      {0.2113893307520584E-05,0.3027544876345173E+01,0.2892654763923579E+02,0,0,0,0,0,0,0,1},
      {0.1297325525448257E-05,0.4747272264391551E+01,0.2893242256448008E+02,0,0,0,0,0,0,0,1},
      {0.8404065818838439E-06,0.3027647299686485E+01,0.2892654763923579E+02,0,0,0,0,0,0,0,1},
      {0.8404065818838439E-06,0.3027442453003861E+01,0.2892654763923579E+02,0,0,0,0,0,0,0,1},
      {0.6782882728655119E-06,0.1605564541446842E+01,0.2893242256448008E+02,0,0,0,0,0,0,0,1},
      {0.6625672028273012E-06,0.3428032677194925E+01,0.2893049702088756E+02,0,0,0,0,0,0,0,1},
      {0.4324877449044836E-06,0.1204979947972735E+01,0.2892847318282831E+02,0,0,0,0,0,0,0,1},
      {0.4321249719379707E-06,0.4849997861787055E+01,0.2892462209564326E+02,0,0,0,0,0,0,0,1},
      {0.3936684188721603E-06,0.4007371902635274E+01,0.2893938173795043E+02,0,0,0,0,0,0,0,1},
      {0.3845413058184980E-06,0.5829842020175415E+01,0.2893745619435791E+02,0,0,0,0,0,0,0,1},
      {0.3164931584142412E-06,0.5589825798207451E+01,0.2891766292217291E+02,0,0,0,0,0,0,0,1},
      {0.3164896582547253E-06,0.1944834709464376E+01,0.2892151400935796E+02,0,0,0,0,0,0,0,1},
      {0.2917227235442026E-06,0.1708446213723446E+01,0.2892462209564326E+02,0,0,0,0,0,0,0,1},
      {0.2917227235442026E-06,0.4346643538966901E+01,0.2892847318282831E+02,0,0,0,0,0,0,0,1},
      {0.4216532478702710E-05,0.1745631218955876E+01,0.3449865748665226E+03,0,0,0,0,0,3,0,-3},
      {0.4016366955996776E-05,0.2502670659577745E+01,0.2829059967995436E+02,0,0,0,0,0,0,0,1},
      {0.5075529621445196E-06,0.2502568236236433E+01,0.2829059967995436E+02,0,0,0,0,0,0,0,1},
      {0.3544419270066688E-05,0.1677245754563736E+01,0.5764374554647894E+02,0,0,0,0,0,0,0,2},
      {0.1350740909818762E-05,0.5240566800117683E+01,0.5764374554647894E+02,0,0,0,0,0,0,0,2},
      {0.1275135226894657E-05,0.2099076569869200E+01,0.5764374554647894E+02,0,0,0,0,0,0,0,2},
      {0.6951146059901856E-06,0.3418104295086557E+01,0.5764567109007147E+02,0,0,0,0,0,0,0,2},
      {0.4502244058755288E-06,0.1677143331222423E+01,0.5764374554647894E+02,0,0,0,0,0,0,0,2},
      {0.3659914666470186E-06,0.2764090012920215E+00,0.5764567109007147E+02,0,0,0,0,0,0,0,2},
      {0.3575086759063831E-06,0.2098877137040104E+01,0.5764374554647894E+02,0,0,0,0,0,0,0,2},
      {0.3405167226829016E-05,0.1173515119422923E+01,0.5700384820554574E+02,0,0,0,0,0,0,0,2},
      {0.4327155481334157E-06,0.1173412696081610E+01,0.5700384820554574E+02,0,0,0,0,0,0,0,2},
      {0.2569819239848606E-05,0.2306043720186055E+00,0.2010645022717792E+03,0,0,0,0,0,2,0,-3},
      {0.1150895741388962E-05,0.2632411300442608E+01,0.2010714614452496E+03,0,0,0,0,0,2,0,-3},
      {0.2517101053299125E-05,0.1375551717917236E+00,0.5713655475179166E+02,0,0,0,0,0,1,0,-3},
      {0.2480836668098538E-05,0.4598277210434070E+01,0.5713848029538418E+02,0,0,0,0,0,1,0,-3},
      {0.1143486543010702E-05,0.1117389755399236E+01,0.5714938885050630E+02,0,0,0,0,0,1,0,-3},
      {0.7533074112851135E-06,0.1960182176247418E+01,0.5713462920819913E+02,0,0,0,0,0,1,0,-3},
      {0.2502679246525132E-05,0.3480902144089144E+00,0.8635699407207032E+02,0,0,0,0,0,0,0,3},
      {0.1392818914226552E-05,0.3393198290814080E+01,0.2821000711255587E+02,0,0,0,0,0,1,0,-4},
      {0.1360536764325014E-05,0.1570730155065997E+01,0.2821193265614840E+02,0,0,0,0,0,1,0,-4},
      {0.4891864753374598E-06,0.5795005219238083E+01,0.2821696628602622E+02,0,0,0,0,0,1,0,-4},
      {0.4778483243651071E-06,0.3972537083490000E+01,0.2821889182961875E+02,0,0,0,0,0,1,0,-4},
      {0.3574500970589592E-06,0.5215822607081832E+01,0.2820808156896335E+02,0,0,0,0,0,1,0,-4},
      {0.1336264182062455E-05,0.1387597018758499E+01,0.5807034377376775E+02,0,0,0,0,0,0,0,2},
      {0.1261775459260343E-05,0.4529292095689605E+01,0.5807034377376775E+02,0,0,0,0,0,0,0,2},
      {0.6876646318040166E-06,0.5848319820906959E+01,0.5807226931736027E+02,0,0,0,0,0,0,0,2},
      {0.3619662493912897E-06,0.2706624527112425E+01,0.5807226931736027E+02,0,0,0,0,0,0,0,2},
      {0.3535767533823012E-06,0.4529092662860506E+01,0.5807034377376775E+02,0,0,0,0,0,0,0,2},
      {0.1310246341606266E-05,0.2331131895479707E+00,0.4599820998220301E+03,0,0,0,0,0,4,0,-4},
      {0.8529888489883649E-06,0.6166128250563959E+01,0.5828364288741214E+02,0,0,0,0,0,0,0,2},
      {0.6930739384650136E-06,0.1201970451738299E+01,0.5828556843100467E+02,0,0,0,0,0,0,0,2},
      {0.6770101727120896E-06,0.3024438587486380E+01,0.5828364288741214E+02,0,0,0,0,0,0,0,2},
      {0.4389627969127892E-06,0.4343665745532833E+01,0.5828556843100467E+02,0,0,0,0,0,0,0,2},
      {0.4027682153504624E-06,0.3024638020315478E+01,0.5828364288741214E+02,0,0,0,0,0,0,0,2},
      {0.3631832770215205E-06,0.4343463151557861E+01,0.5828556843100467E+02,0,0,0,0,0,0,0,2},
      {0.8221773615274948E-06,0.5879488794129729E+01,0.2935314586652459E+02,0,0,0,0,0,0,0,1},
      {0.7813137857380711E-06,0.2737998563881249E+01,0.2935314586652459E+02,0,0,0,0,0,0,0,1},
      {0.4231066732027226E-06,0.4057026289098603E+01,0.2935507141011711E+02,0,0,0,0,0,0,0,1},
      {0.8149891095115382E-06,0.5001271649790290E+01,0.3160600272272868E+03,0,0,0,0,0,3,0,-4},
      {0.3764251887678508E-06,0.1119909356269040E+01,0.3160669864007572E+03,0,0,0,0,0,3,0,-4},
      {0.7620147658522374E-06,0.1608073358976210E+01,0.2878500201147309E+03,0,0,0,0,0,2,0,0},
      {0.7489157248963648E-06,0.6282589894747257E+00,0.2878371860160163E+03,0,0,0,0,0,2,0,0},
      {0.7443531196416904E-06,0.3430541494724291E+01,0.2878480945711384E+03,0,0,0,0,0,2,0,0},
      {0.7410458807790901E-06,0.2997108078146744E+01,0.4028257517980534E+03,0,0,0,0,0,3,0,-1},
      {0.2917099551383937E-06,0.9555532956830555E-01,0.4028455450702384E+03,0,0,0,0,0,3,0,-1},
      {0.7277204919188508E-06,0.5941165931805280E+01,0.5080073454820026E+03,0,0,0,0,1,0,0,0},
      {0.6286284677985618E-06,0.1791293531808356E+01,0.2871719790724316E+02,0,0,0,0,0,0,0,1},
      {0.4190958483159967E-06,0.1812437113434972E+01,0.2871324852559139E+02,0,0,0,0,0,0,0,1},
      {0.6143353968708967E-06,0.1287562896667545E+01,0.2807730056630996E+02,0,0,0,0,0,0,0,1},
      {0.5608837306250120E-06,0.1612346238109583E-01,0.2406215774107413E+04,1,0,0,0,0,0,0,-1},
      {0.5287253161661598E-06,0.6336466577531914E+00,0.1645938776167635E+04,0,1,0,0,0,0,0,-1},
      {0.4889359735634015E-06,0.5416167758678300E+01,0.8614369495842593E+02,0,0,0,0,0,0,0,3},
      {0.4416345379733718E-06,0.6241592663692312E+01,0.5679054909190134E+02,0,0,0,0,0,0,0,2},
      {0.4101604037784085E-06,0.4242652639255375E+01,0.2913984675288019E+02,0,0,0,0,0,0,0,1},
      {0.3830879278957400E-06,0.5003780467319657E+01,0.5749776247775377E+03,0,0,0,0,0,5,0,-5},
      {0.3811901873161304E-06,0.1714424909184600E+01,0.8385088956687217E+03,0,0,0,1,0,0,0,0},
      {0.3716648744882634E-06,0.4908225137751350E+01,0.1721320797072992E+03,0,0,0,0,0,2,0,-4},
      {0.3630505879727552E-06,0.3085757002003267E+01,0.1721340052508917E+03,0,0,0,0,0,2,0,-4},
      {0.3001367588499240E-06,0.5512961820466838E+01,0.3739061633322880E+03,0,0,0,0,0,3,0,-2}
    };
    Series83=new double[][] {
      {0.1016094463287790E-02,0.5106842617766041E+01,0.0000000000000000E+00,0,0,0,0,0,0,0,0},
      {0.2935648107313708E-01,0.3358649794029711E+01,0.1974690825885564E-02,0,0,0,0,0,0,0,0},
      {0.9953584143544754E-03,0.5831603333509536E+01,0.8933864296234374E-02,0,0,0,0,0,0,0,0},
      {0.7357194732163894E-03,0.6035829207568037E+01,-0.1974690825885564E-02,0,0,0,0,0,0,0,0},
      {0.6699050371985383E-03,0.4753014210656826E+01,-0.3900234418413626E-02,0,0,0,0,0,0,0,0},
      {0.4151869133102979E-03,0.4743964405299185E+01,0.3900234418413626E-02,0,0,0,0,0,0,0,0},
      {0.3789273029962630E-03,0.2745292826757329E+01,0.4246235493298404E+00,0,0,0,0,0,0,0,0},
      {0.1991669418726220E-03,0.4485476749277378E+01,0.7008320703706311E-02,0,0,0,0,0,0,0,0},
      {0.1899854046059933E-03,0.3517567007545812E+01,-0.5825778010941689E-02,0,0,0,0,0,0,0,0},
      {0.1234894258008271E-03,0.8652949986394700E+00,0.1085940788876244E-01,0,0,0,0,0,0,0,0},
      {0.1011804925675502E-03,0.3745348713034283E+01,-0.8933864296234374E-02,0,0,0,0,0,0,0,0},
      {0.6928098928797385E-04,0.5576141930376416E+01,-0.1085940788876244E-01,0,0,0,0,0,0,0,0},
      {0.4889687732101663E-04,0.4014982830991590E+01,0.6379226694077035E+00,0,0,0,0,0,0,0,0},
      {0.2082728796332944E-04,0.1173870037237461E+01,-0.1278495148129050E-01,0,0,0,0,0,0,0,0},
      {0.2039385297517835E-04,0.4585155128610154E+01,0.2113244292519774E+00,0,0,0,0,0,0,0,0},
      {0.1799640976047236E-04,0.2341543068636097E+00,0.4176643758594916E+00,0,0,0,0,0,0,0,0},
      {0.1595251648310000E-04,0.6234432051555658E+01,0.2132991200778630E+00,0,0,0,0,0,0,0,0},
      {0.1343153395169566E-04,0.4649306306086897E+01,0.2152738109037486E+00,0,0,0,0,0,0,0,0},
      {0.1287853944279400E-04,0.5287782933428181E+01,-0.2113244292519774E+00,0,0,0,0,0,0,0,0},
      {0.1130469268372695E-04,0.4574612374891347E+01,0.4226488585039549E+00,0,0,0,0,0,0,0,0},
      {0.1065311292483293E-04,0.3513635458424760E-02,0.4285237837482541E+00,0,0,0,0,0,0,0,0},
      {0.6594412507279831E-05,0.1160392236116490E+01,0.4304984745741396E+00,0,0,0,0,0,0,0,0},
      {0.5041791242559473E-05,0.5286783389563643E+01,0.8512217894855665E+00,0,0,0,0,0,0,0,0},
      {0.4598867989379571E-06,0.1609956090712706E+01,0.6309634959373547E+00,0,0,0,0,0,0,0,0},
      {0.3040421176914689E-06,0.2136467296916470E+01,0.2043652557816286E+00,0,0,0,0,0,0,0,0},
      {0.5937514602713236E-03,0.4936792335851361E+01,0.1439240472855692E+03,0,0,0,0,0,1,0,0},
      {0.5590721162724015E-05,0.6255890970618563E+01,0.1439259728291617E+03,0,0,0,0,0,1,0,0},
      {0.5121289657996665E-05,0.3617695571228794E+01,0.1439221217419767E+03,0,0,0,0,0,1,0,0},
      {0.3508789971395948E-06,0.2022944114109337E+01,0.1439329785251645E+03,0,0,0,0,0,1,0,0},
      {0.2738557431501198E-03,0.1678643034250733E+01,-0.8606700262544587E+02,0,0,0,0,0,-1,0,2},
      {0.2085092385794607E-05,0.3595434709071512E+00,-0.8606892816903840E+02,0,0,0,0,0,-1,0,2},
      {0.1891655464023067E-05,0.2997740360570593E+01,-0.8606507708185333E+02,0,0,0,0,0,-1,0,2},
      {0.2532842572772215E-03,0.1661250302251527E+00,0.2892852233006167E+02,0,0,0,0,0,0,0,1},
      {0.3961485739301291E-05,0.3307820706331927E+01,0.2892852233006167E+02,0,0,0,0,0,0,0,1},
      {0.3961485739301291E-05,0.3307614661297965E+01,0.2892852233006167E+02,0,0,0,0,0,0,0,1},
      {0.2209106205222155E-05,0.1485152755442508E+01,0.2893044787365420E+02,0,0,0,0,0,0,0,1},
      {0.1956549440210912E-05,0.5130279568062417E+01,0.2892659678646914E+02,0,0,0,0,0,0,0,1},
      {0.1058742510185323E-05,0.1485223692846880E+01,0.2893044787365420E+02,0,0,0,0,0,0,0,1},
      {0.1058742510185323E-05,0.5130211674783012E+01,0.2892659678646914E+02,0,0,0,0,0,0,0,1},
      {0.1049147278699332E-03,0.5987172345751204E+01,-0.2850192410277287E+02,0,0,0,0,0,0,0,-1},
      {0.1328065342396536E-04,0.5987275095618058E+01,-0.2850192410277287E+02,0,0,0,0,0,0,0,-1},
      {0.6684462694404047E-05,0.1526550339106239E+01,-0.2850384964636540E+02,0,0,0,0,0,0,0,-1},
      {0.2236294716863211E-05,0.1023085701193345E+01,-0.2849999855918034E+02,0,0,0,0,0,0,0,-1},
      {0.2236294716863211E-05,0.4668073683129476E+01,-0.2850384964636540E+02,0,0,0,0,0,0,0,-1},
      {0.7114033777681735E-06,0.1526654607148799E+01,-0.2850384964636540E+02,0,0,0,0,0,0,0,-1},
      {0.6949147802577597E-06,0.5987371778580302E+01,-0.2850192410277287E+02,0,0,0,0,0,0,0,-1},
      {0.2058105309919415E-04,0.9190948014818194E+00,-0.2828862498912847E+02,0,0,0,0,0,0,0,-1},
      {0.2722012192785058E-05,0.9191972248231336E+00,-0.2828862498912847E+02,0,0,0,0,0,0,0,-1},
      {0.1400794496660462E-05,0.2741659729854258E+01,-0.2829055053272100E+02,0,0,0,0,0,0,0,-1},
      {0.4386017871369636E-06,0.5883181446039679E+01,-0.2829055053272100E+02,0,0,0,0,0,0,0,-1},
      {0.4386017871369636E-06,0.2238193464103548E+01,-0.2828669944553594E+02,0,0,0,0,0,0,0,-1},
      {0.1721342888159264E-04,0.9024276128068640E+00,0.2878391607068422E+03,0,0,0,0,0,2,0,0},
      {0.1645103872537621E-04,0.6221338739275052E+01,0.5080093201728285E+03,0,0,0,0,1,0,0,0},
      {0.1295232370537576E-04,0.4706187935978230E+01,-0.5714045498621007E+02,0,0,0,0,0,-1,0,3},
      {0.3074615033071044E-05,0.5446004958756377E+01,-0.5714741415968042E+02,0,0,0,0,0,-1,0,3},
      {0.4210058117501252E-06,0.1957966659954585E+01,-0.5713650560455830E+02,0,0,0,0,0,-1,0,3},
      {0.3297148610970001E-06,0.3283240059277484E+01,-0.5713458006096577E+02,0,0,0,0,0,-1,0,3},
      {0.1246701893241003E-04,0.3056131321602242E+01,0.7185152175058083E+00,0,0,0,0,0,-1,0,5},
      {0.1141609439284819E-04,0.4878643124436238E+01,0.7165896739132802E+00,0,0,0,0,0,-1,0,5},
      {0.4622849445770262E-05,0.6542846935894391E+00,0.7115560440354595E+00,0,0,0,0,0,-1,0,5},
      {0.4097571772366432E-05,0.2476739673478372E+01,0.7096305004429314E+00,0,0,0,0,0,-1,0,5},
      {0.3059086093929264E-05,0.2076274581959904E+01,0.7056811187911602E+00,0,0,0,0,0,-1,0,5},
      {0.2869127018959028E-05,0.1233485437260538E+01,0.7204407610983363E+00,0,0,0,0,0,-1,0,5},
      {0.1580459593848266E-05,0.5957697356265878E+01,0.6987219453208114E+00,0,0,0,0,0,-1,0,5},
      {0.9865359954273804E-06,0.5114846914000389E+01,0.7134815876279875E+00,0,0,0,0,0,-1,0,5},
      {0.6577318919877076E-06,0.3559551940631996E+01,0.7146641303207522E+00,0,0,0,0,0,-1,0,5},
      {0.5913037768933752E-06,0.5106224293368807E+01,0.7255209135085651E+00,0,0,0,0,0,-1,0,5},
      {0.5346245981429982E-06,0.2130300142857571E+01,0.7205390555650513E+00,0,0,0,0,0,-1,0,5},
      {0.4098021786747177E-06,0.4478092318805028E+01,0.7126402922615090E+00,0,0,0,0,0,-1,0,5},
      {0.3023788452701212E-06,0.3282662154041165E+01,0.7274464571010931E+00,0,0,0,0,0,-1,0,5},
      {0.2905389353328124E-06,0.1636237329753522E+01,0.7243901427501074E+00,0,0,0,0,0,-1,0,5},
      {0.1228187806589623E-04,0.3135228775838881E+01,-0.2892852233006167E+02,0,0,0,0,0,0,0,-1},
      {0.1176259764714520E-04,0.6276718658215128E+01,-0.2892852233006167E+02,0,0,0,0,0,0,0,-1},
      {0.6172687497060316E-05,0.4957689196191843E+01,-0.2893044787365420E+02,0,0,0,0,0,0,0,-1},
      {0.2961985147767668E-05,0.1816200919612724E+01,-0.2893044787365420E+02,0,0,0,0,0,0,0,-1},
      {0.2893333546637261E-05,0.6276918091044227E+01,-0.2892852233006167E+02,0,0,0,0,0,0,0,-1},
      {0.7601575967574296E-06,0.3638825235880475E+01,-0.2893237341724673E+02,0,0,0,0,0,0,0,-1},
      {0.4008228833174511E-06,0.1816300873382952E+01,-0.2893044787365420E+02,0,0,0,0,0,0,0,-1},
      {0.1154499923706900E-04,0.6282630218786875E+00,0.8635896876289621E+02,0,0,0,0,0,0,0,3},
      {0.1660158316268941E-05,0.6281605985373753E+00,0.8635896876289621E+02,0,0,0,0,0,0,0,3},
      {0.8543461484774715E-06,0.5088883400685836E+01,0.8636089430648875E+02,0,0,0,0,0,0,0,3},
      {0.1050421274404971E-04,0.1871271231028191E+00,-0.1439259728291617E+03,0,0,0,0,0,-1,0,0},
      {0.9807167381341471E-05,0.4647844982264678E+01,-0.1439240472855692E+03,0,0,0,0,0,-1,0,0},
      {0.2816558865567409E-05,0.2009743661307299E+01,-0.1439278983727543E+03,0,0,0,0,0,-1,0,0},
      {0.3824414539954584E-06,0.4420210596752325E+01,-0.1439329785251645E+03,0,0,0,0,0,-1,0,0},
      {0.1048627591564010E-04,0.4956843545116611E-01,-0.2010625275809534E+03,0,0,0,0,0,-2,0,3},
      {0.3279448099865014E-06,0.2451375363875169E+01,-0.2010555684074830E+03,0,0,0,0,0,-2,0,3},
      {0.3126490699275733E-06,0.1368667098072893E+01,-0.2010606020373608E+03,0,0,0,0,0,-2,0,3},
      {0.3126490699275733E-06,0.5013655080009026E+01,-0.2010644531245459E+03,0,0,0,0,0,-2,0,3},
      {0.1034350804286399E-04,0.1792690836877676E+01,-0.1149935502646816E+03,0,0,0,0,0,-1,0,1},
      {0.3660318350154349E-06,0.4194707709203449E+01,-0.1149865910912113E+03,0,0,0,0,0,-1,0,1},
      {0.3362081661163648E-06,0.4540912112901320E+01,-0.1149974996463334E+03,0,0,0,0,0,-1,0,1},
      {0.9029410063641440E-05,0.1994597716654372E+01,0.8385108703595477E+03,0,0,0,1,0,0,0,0},
      {0.8024442052834091E-05,0.3193669906570326E+01,0.5785506996929746E+02,0,0,0,0,0,0,0,2},
      {0.4262641945379126E-05,0.7918495104173626E+00,0.5784811079582711E+02,0,0,0,0,0,0,0,2},
      {0.7066767025578607E-05,0.5707015564669242E+01,-0.2299821160467188E+03,0,0,0,0,0,-2,0,2},
      {0.6251473936893123E-05,0.3940966283548398E+00,-0.4501522755127052E+03,0,0,0,0,-1,0,0,2},
      {0.5659369633167750E-05,0.5239109003991030E+01,0.1215663929056177E+04,0,0,1,0,0,0,0,0},
      {0.4290089389673601E-05,0.4557743275927342E+00,0.2850192410277287E+02,0,0,0,0,0,0,0,1},
      {0.3827134505848882E-05,0.3597263996278870E+01,0.2850192410277287E+02,0,0,0,0,0,0,0,1},
      {0.2332336374925157E-05,0.2278233679185805E+01,0.2849999855918034E+02,0,0,0,0,0,0,0,1},
      {0.1476386405746395E-05,0.5419931564856051E+01,0.2849999855918034E+02,0,0,0,0,0,0,0,1},
      {0.1442167364939238E-05,0.3597463429107968E+01,0.2850192410277287E+02,0,0,0,0,0,0,0,1},
      {0.3788966811408025E-06,0.9593705739442162E+00,0.2849807301558782E+02,0,0,0,0,0,0,0,1},
      {0.4165399751611416E-05,0.6159661040037157E+01,0.2935512055735047E+02,0,0,0,0,0,0,0,1},
      {0.3715900456157925E-05,0.3018171371351021E+01,0.2935512055735047E+02,0,0,0,0,0,0,0,1},
      {0.2264548002233273E-05,0.4337201688444086E+01,0.2935704610094300E+02,0,0,0,0,0,0,0,1},
      {0.1433475857771437E-05,0.1195503802773841E+01,0.2935704610094300E+02,0,0,0,0,0,0,0,1},
      {0.1400251378947848E-05,0.3017971938521923E+01,0.2935512055735047E+02,0,0,0,0,0,0,0,1},
      {0.3678842089652509E-06,0.5656064793685675E+01,0.2935897164453553E+02,0,0,0,0,0,0,0,1},
      {0.3500731595844181E-05,0.1909247431651662E+01,0.1149974996463334E+03,0,0,0,0,0,1,0,-1},
      {0.3207481705138808E-05,0.4620837650975520E+01,-0.7806538256994243E+03,0,0,0,-1,0,0,0,2},
      {0.3045177521748787E-05,0.1630471929251210E+01,-0.2871522321641727E+02,0,0,0,0,0,0,0,-1},
      {0.1249614513454947E-05,0.4350336190876849E+01,-0.2871522321641727E+02,0,0,0,0,0,0,0,-1},
      {0.1095523574996029E-05,0.1208641113945746E+01,-0.2871522321641727E+02,0,0,0,0,0,0,0,-1},
      {0.6430732154817181E-06,0.6172798695907975E+01,-0.2871714876000980E+02,0,0,0,0,0,0,0,-1},
      {0.4165209314501650E-06,0.1630574352592522E+01,-0.2871522321641727E+02,0,0,0,0,0,0,0,-1},
      {0.3667610652854651E-06,0.3031308682522925E+01,-0.2871714876000980E+02,0,0,0,0,0,0,0,-1},
      {0.3582604371229245E-06,0.1208840546774842E+01,-0.2871522321641727E+02,0,0,0,0,0,0,0,-1},
      {0.2944808616906725E-05,0.2134202564392020E+01,-0.2807532587548407E+02,0,0,0,0,0,0,0,-1},
      {0.4028078479881629E-06,0.2134304987733334E+01,-0.2807532587548407E+02,0,0,0,0,0,0,0,-1},
      {0.2918130882538270E-05,0.1562086464859070E+01,-0.3160580525364609E+03,0,0,0,0,0,-3,0,4},
      {0.2652490001043860E-05,0.5696340566148072E+01,0.8614566964925181E+02,0,0,0,0,0,0,0,3},
      {0.3239657527107578E-06,0.5696238142806759E+01,0.8614566964925181E+02,0,0,0,0,0,0,0,3},
      {0.2223396437049981E-05,0.4822744530752217E+01,0.1728505949248050E+03,0,0,0,0,0,1,0,1},
      {0.2144377111729538E-05,0.3170159823835280E+01,-0.2820803242172999E+02,0,0,0,0,0,-1,0,4},
      {0.2094675673402415E-05,0.4992627959583361E+01,-0.2820995796532252E+02,0,0,0,0,0,-1,0,4},
      {0.9711472430656398E-06,0.2190338602286141E+01,-0.2822086652044464E+02,0,0,0,0,0,-1,0,4},
      {0.5503284015527491E-06,0.1347535507567527E+01,-0.2820610687813746E+02,0,0,0,0,0,-1,0,4},
      {0.4509478629698950E-06,0.1450547484355915E+01,-0.2821390734697429E+02,0,0,0,0,0,-1,0,4},
      {0.1980399556792357E-05,0.1376326363638862E+01,-0.1157806884396054E+04,0,0,-1,0,0,0,0,2},
      {0.1962428337898891E-05,0.4799234448017168E+01,0.8678749253377755E+02,0,0,0,0,0,0,0,3},
      {0.1651997757838688E-05,0.3480206939663244E+01,0.8678556699018502E+02,0,0,0,0,0,0,0,3},
      {0.1630513899533984E-05,0.3385172765856651E+00,0.8678556699018502E+02,0,0,0,0,0,0,0,3},
      {0.1448288628265927E-05,0.3387167094147629E+00,0.8678556699018502E+02,0,0,0,0,0,0,0,3},
      {0.8501465841371375E-06,0.1657744434632118E+01,0.8678749253377755E+02,0,0,0,0,0,0,0,3},
      {0.3039469315779643E-06,0.2976610131749417E+01,0.8678941807737007E+02,0,0,0,0,0,0,0,3},
      {0.1948656845764241E-05,0.1451237650194714E+00,0.2017830174892851E+03,0,0,0,0,0,1,0,2},
      {0.1903491726480552E-05,0.1967591900767555E+01,0.2017810919456925E+03,0,0,0,0,0,1,0,2},
      {0.5000991669041441E-06,0.4605684755931306E+01,0.2017849430328776E+03,0,0,0,0,0,1,0,2},
      {0.1831410067152233E-05,0.3077113311796339E+01,-0.1721359799417176E+03,0,0,0,0,0,-2,0,4},
      {0.7115752344230280E-06,0.6753063833723361E+00,-0.1721429391151880E+03,0,0,0,0,0,-2,0,4},
      {0.1733359910757746E-05,0.7423107991233067E+00,0.5743242112366043E+02,0,0,0,0,0,0,0,2},
      {0.1242741081493270E-05,0.2092609920904744E+01,0.2871522321641727E+02,0,0,0,0,0,0,0,1},
      {0.4165435585729947E-06,0.2092507497563432E+01,0.2871522321641727E+02,0,0,0,0,0,0,0,1},
      {0.4165435585729947E-06,0.2092712344246056E+01,0.2871522321641727E+02,0,0,0,0,0,0,0,1},
      {0.1231321836921703E-05,0.1920120665056447E+01,-0.2914182144370607E+02,0,0,0,0,0,0,0,-1},
      {0.1079486582646652E-05,0.5061610895304928E+01,-0.2914182144370607E+02,0,0,0,0,0,0,0,-1},
      {0.6336594881351334E-06,0.3742583170087572E+01,-0.2914374698729860E+02,0,0,0,0,0,0,0,-1},
      {0.3613921763521041E-06,0.6010931567025215E+00,-0.2914374698729860E+02,0,0,0,0,0,0,0,-1},
      {0.3530159859578773E-06,0.5061810328134025E+01,-0.2914182144370607E+02,0,0,0,0,0,0,0,-1},
      {0.1224549022763507E-05,0.4522825446725147E+01,0.2914182144370607E+02,0,0,0,0,0,0,0,1},
      {0.4104459208639739E-06,0.4522927870066459E+01,0.2914182144370607E+02,0,0,0,0,0,0,0,1},
      {0.4104459208639739E-06,0.4522723023383834E+01,0.2914182144370607E+02,0,0,0,0,0,0,0,1},
      {0.1155571530124003E-05,0.2959627469406032E+01,-0.5742847174200866E+02,0,0,0,0,0,0,0,-2},
      {0.1093705824191350E-05,0.2826816249991383E+00,0.2589195722410767E+03,0,0,0,0,0,2,0,-1},
      {0.9865831571531203E-06,0.5523851310299774E+01,0.2828862498912847E+02,0,0,0,0,0,0,0,1},
      {0.8649268200054579E-06,0.2382156233368668E+01,0.2828862498912847E+02,0,0,0,0,0,0,0,1},
      {0.5077127357111450E-06,0.1063128508151314E+01,0.2828669944553594E+02,0,0,0,0,0,0,0,1},
      {0.9438814987601647E-06,0.1091584057330117E+01,0.2956841967099487E+02,0,0,0,0,0,0,0,1},
      {0.8274907363514915E-06,0.4233279134261223E+01,0.2956841967099487E+02,0,0,0,0,0,0,0,1},
      {0.4857377246389406E-06,0.5552306859478579E+01,0.2957034521458740E+02,0,0,0,0,0,0,0,1},
      {0.8343508926599710E-06,0.3074604494266973E+01,-0.4310535774919684E+03,0,0,0,0,0,-4,0,5},
      {0.7844727255827545E-06,0.3424774740430573E+01,-0.2935512055735047E+02,0,0,0,0,0,0,0,-1},
      {0.6907267387443708E-06,0.2105747232076648E+01,-0.2935704610094300E+02,0,0,0,0,0,0,0,-1},
      {0.6747173753638274E-06,0.2832790963285650E+00,-0.2935512055735047E+02,0,0,0,0,0,0,0,-1},
      {0.4037032162050010E-06,0.5247237245461699E+01,-0.2935704610094300E+02,0,0,0,0,0,0,0,-1},
      {0.3949980093220293E-06,0.5247439839436669E+01,-0.2935704610094300E+02,0,0,0,0,0,0,0,-1},
      {0.3438693915458760E-06,0.2830796634994670E+00,-0.2935512055735047E+02,0,0,0,0,0,0,0,-1},
      {0.5831454312634436E-06,0.4589631341204242E+01,-0.2871315048972251E+03,0,0,0,0,0,-3,0,5},
      {0.5756485350981315E-06,0.3655807898223860E+01,0.1152855164021320E+03,0,0,0,0,0,0,0,4},
      {0.5754083403713949E-06,0.3163232139672567E+01,0.4317542741281151E+03,0,0,0,0,0,3,0,0},
      {0.4595462189000190E-06,0.5053348902770820E+01,0.3739150971965843E+03,0,0,0,0,0,3,0,-2},
      {0.4231711644426220E-06,0.1822484926062486E+00,0.2435144296437475E+04,1,0,0,0,0,0,0,0},
      {0.4105962555389770E-06,0.5815663679651547E+01,-0.1617010253837573E+04,0,-1,0,0,0,0,0,2},
      {0.3999910250873396E-06,0.7997716879783442E+00,0.1674867298497696E+04,0,1,0,0,0,0,0,0},
      {0.3827903648104360E-06,0.3349310327302221E+01,-0.2786202676183967E+02,0,0,0,0,0,0,0,-1},
      {0.3770599260080091E-06,0.4984963438378681E+01,0.8657226787654062E+02,0,0,0,0,0,0,0,3},
      {0.3591607352372491E-06,0.4481232803237869E+01,0.8593237053560742E+02,0,0,0,0,0,0,0,3},
      {0.3386727736182448E-06,0.5810388343392691E+01,0.5721912201001602E+02,0,0,0,0,0,0,0,2},
      {0.3140166875052082E-06,0.4215499030507360E+01,-0.2878410862504347E+03,0,0,0,0,0,-2,0,0},
      {0.3067385455485728E-06,0.2393030894759276E+01,-0.2878391607068422E+03,0,0,0,0,0,-2,0,0},
      {0.3076753876071480E-06,0.5185765171332203E+01,-0.5080112457164210E+03,0,0,0,0,-1,0,0,0},
      {0.3005442215364578E-06,0.3363297035584122E+01,-0.5080093201728285E+03,0,0,0,0,-1,0,0,0}
    };
    Series84=new double[][] {
      {0.1320165332695350E+00,0.3221523573692266E+01,0.0000000000000000E+00,0,0,0,0,0,0,0,0},
      {0.6794549145709775E-01,0.5047886356662284E+01,-0.1925543592528062E-02,0,0,0,0,0,0,0,0},
      {0.6892434301339112E-03,0.1398038368144667E+01,0.1925543592528062E-02,0,0,0,0,0,0,0,0},
      {0.2730634121085477E-03,0.3074026406224565E+01,-0.8931239595284827E-02,0,0,0,0,0,0,0,0},
      {0.2641112196820165E-03,0.6085250961969193E+01,0.4265982401557260E+00,0,0,0,0,0,0,0,0},
      {0.1816919041955310E-03,0.1123018507358733E+01,0.4285237837482541E+00,0,0,0,0,0,0,0,0},
      {0.4570393856724005E-04,0.3663028182224427E+01,-0.3851087185056125E-02,0,0,0,0,0,0,0,0},
      {0.4494417792607357E-04,0.5092981879591376E+01,-0.2132991200778630E+00,0,0,0,0,0,0,0,0},
      {0.3374620641006819E-04,0.1072212121537805E+01,0.6398973602335890E+00,0,0,0,0,0,0,0,0},
      {0.3010291742672222E-04,0.4323389769715201E+01,0.2132991200778630E+00,0,0,0,0,0,0,0,0},
      {0.2873148969672351E-04,0.1130417841604620E+01,0.3900234418413626E-02,0,0,0,0,0,0,0,0},
      {0.2832841674512957E-04,0.3775418852980096E+01,-0.2152246636703911E+00,0,0,0,0,0,0,0,0},
      {0.2829811667283325E-04,0.3179813769687029E+01,0.2113735764853349E+00,0,0,0,0,0,0,0,0},
      {0.2348785638862567E-04,0.2392069283444719E+01,0.6418229038261171E+00,0,0,0,0,0,0,0,0},
      {0.1915644482319336E-04,0.1666983826523547E+01,0.5874925244299190E-02,0,0,0,0,0,0,0,0},
      {0.1388791091408471E-04,0.6199545784892948E+01,-0.6831187836279569E-02,0,0,0,0,0,0,0,0},
      {0.1363049113672050E-04,0.1573081241951383E+01,-0.1085678318781289E-01,0,0,0,0,0,0,0,0},
      {0.1115402869499410E-04,0.2124370914205684E+01,0.1786772859246875E-01,0,0,0,0,0,0,0,0},
      {0.9901282214730337E-05,0.3022565890567675E+01,0.2152246636703911E+00,0,0,0,0,0,0,0,0},
      {0.9851993483313069E-05,0.3568018055505390E+01,0.8931239595284827E-02,0,0,0,0,0,0,0,0},
      {0.6564159971602750E-05,0.5899199575040269E+01,0.1090855512211994E-01,0,0,0,0,0,0,0,0},
      {0.5927925628073686E-05,0.2236765758751786E+01,-0.5924072477656691E-02,0,0,0,0,0,0,0,0},
      {0.5075828846612914E-05,0.3331278759394790E+00,0.1979327218499681E-01,0,0,0,0,0,0,0,0},
      {0.5003929435494984E-05,0.5249025520673378E+01,-0.4285237837482541E+00,0,0,0,0,0,0,0,0},
      {0.4700210983954140E-05,0.3997089887831509E+01,-0.4304493273407821E+00,0,0,0,0,0,0,0,0},
      {0.3932389676076612E-05,0.5902900444967474E+01,0.4334294279920056E+00,0,0,0,0,0,0,0,0},
      {0.3855202341799354E-05,0.3260409788853447E+01,0.4197670523194464E+00,0,0,0,0,0,0,0,0},
      {0.3494901083668216E-05,0.2384257843599439E+01,0.8531964803114520E+00,0,0,0,0,0,0,0,0},
      {0.6779699256950095E-06,0.3450394767068876E+01,-0.4265982401557260E+00,0,0,0,0,0,0,0,0},
      {0.2987819907657320E-04,0.1592450553501527E+01,-0.1149955249555075E+03,0,0,0,0,0,-1,0,1},
      {0.1598538661690393E-04,0.3414911760531445E+01,-0.1149974504991001E+03,0,0,0,0,0,-1,0,1},
      {0.5196347763201437E-06,0.1364682192599308E+01,-0.1150044561951028E+03,0,0,0,0,0,-1,0,1},
      {0.2011086466288782E-04,0.5022984906438330E+01,0.1728525696156309E+03,0,0,0,0,0,1,0,1},
      {0.1081178008232983E-04,0.3200523219219202E+01,0.1728544951592234E+03,0,0,0,0,0,1,0,1},
      {0.3781314342005297E-06,0.5250621987578875E+01,0.1728615008552262E+03,0,0,0,0,0,1,0,1},
      {0.4023273549659016E-05,0.3393911100068438E+01,0.5785704466012334E+02,0,0,0,0,0,0,0,2},
      {0.3549114063300533E-05,0.2524208698199577E+00,0.5785704466012334E+02,0,0,0,0,0,0,0,2},
      {0.1832371513844348E-05,0.1571448595037313E+01,0.5785897020371587E+02,0,0,0,0,0,0,0,2},
      {0.3828678448728374E-05,0.3683559835873675E+01,0.5743044643283454E+02,0,0,0,0,0,0,0,2},
      {0.2697300374723224E-05,0.5420696056251947E+00,0.5743044643283454E+02,0,0,0,0,0,0,0,2},
      {0.1388077368193791E-05,0.2364532110656320E+01,0.5742852088924202E+02,0,0,0,0,0,0,0,2},
      {0.4332348469960449E-06,0.3683457412532363E+01,0.5743044643283454E+02,0,0,0,0,0,0,0,2},
      {0.1310815780246894E-05,0.3256355709659855E+01,0.7204899083316938E+00,0,0,0,0,0,-1,0,5},
      {0.8833068155471169E-06,0.4678333554199201E+01,0.7146149830873946E+00,0,0,0,0,0,-1,0,5},
      {0.8604417421849733E-06,0.5078832057446901E+01,0.7185643647391657E+00,0,0,0,0,0,-1,0,5},
      {0.6718820321393613E-06,0.1433809611894344E+01,0.7224154519242219E+00,0,0,0,0,0,-1,0,5},
      {0.5950685735865521E-06,0.2276553135246288E+01,0.7076558096170459E+00,0,0,0,0,0,-1,0,5},
      {0.4545649462411443E-06,0.2855871049168076E+01,0.7165405266799226E+00,0,0,0,0,0,-1,0,5},
      {0.3062968607914330E-06,0.4541169170095360E+00,0.7095813532095739E+00,0,0,0,0,0,-1,0,5},
      {0.1141957796039554E-05,0.5137033501494948E+01,0.1439260219763951E+03,0,0,0,0,0,1,0,0},
      {0.5876712089500302E-06,0.3314570996463822E+01,0.1439279475199876E+03,0,0,0,0,0,1,0,0},
      {0.1141957796039554E-05,0.1706497420624183E+01,-0.1439220725947433E+03,0,0,0,0,0,-1,0,0},
      {0.5876712089500301E-06,0.3528959925655309E+01,-0.1439239981383359E+03,0,0,0,0,0,-1,0,0},
      {0.9240554014049475E-06,0.2468452072963474E+01,0.5721714731919014E+02,0,0,0,0,0,0,0,2},
      {0.5270142364742109E-06,0.5610147149894579E+01,0.5721714731919014E+02,0,0,0,0,0,0,0,2},
      {0.8562203475402176E-06,0.5620822378456090E+01,-0.2589106383767805E+03,0,0,0,0,0,-2,0,1},
      {0.4406257818911077E-06,0.1160099576307630E+01,-0.2589125639203730E+03,0,0,0,0,0,-2,0,1},
      {0.8081541157870830E-06,0.3797607613118295E+01,0.2850389879359876E+02,0,0,0,0,0,0,0,1},
      {0.4609125422972075E-06,0.6561173828698139E+00,0.2850389879359876E+02,0,0,0,0,0,0,0,1},
      {0.7978736105198133E-06,0.3079032121013479E+00,-0.4790807978427668E+03,0,0,0,0,-1,0,0,1},
      {0.4105995430914010E-06,0.2130365717132474E+01,-0.4790827233863593E+03,0,0,0,0,-1,0,0,1},
      {0.7962365977686471E-06,0.3664686470645773E+00,0.2893049702088756E+02,0,0,0,0,0,0,0,1},
      {0.4541156536587076E-06,0.3507958877313058E+01,0.2893049702088756E+02,0,0,0,0,0,0,0,1},
      {0.3695428746936740E-06,0.7056695618928304E+00,0.2891958846576544E+02,0,0,0,0,0,0,0,1},
      {0.3695428746936740E-06,0.2768173152147268E+01,0.2893745619435791E+02,0,0,0,0,0,0,0,1},
      {0.7454540946216246E-06,0.1764836475886549E+01,-0.5713848029538418E+02,0,0,0,0,0,-1,0,3},
      {0.3836235546728265E-06,0.6225559278035010E+01,-0.5713655475179166E+02,0,0,0,0,0,-1,0,3},
      {0.7118995492853203E-06,0.2434684834895837E-01,0.5369378425028901E+03,0,0,0,0,1,0,0,1},
      {0.3663558059942478E-06,0.4485069650497419E+01,0.5369397680464826E+03,0,0,0,0,1,0,0,1},
      {0.6998372934208747E-06,0.9946129891738023E+00,0.3167676830369039E+03,0,0,0,0,0,2,0,1},
      {0.3601483607531824E-06,0.5455335791322262E+01,0.3167696085804964E+03,0,0,0,0,0,2,0,1},
      {0.4754237067161859E-06,0.4619994519724736E+01,-0.8606897731627176E+02,0,0,0,0,0,-1,0,2},
      {0.4385541576256689E-06,0.6246560326377261E+01,-0.2299910499110151E+03,0,0,0,0,0,-2,0,2},
      {0.4268928712637706E-06,0.3104364787604514E+01,0.5828364288741214E+02,0,0,0,0,0,0,0,2},
      {0.4090821761277136E-06,0.4534644234722027E+01,-0.8095823480294860E+03,0,0,0,-1,0,0,0,1},
      {0.3930946817313776E-06,0.5320498414089342E+01,0.5764374554647894E+02,0,0,0,0,0,0,0,2},
      {0.3902069246730890E-06,0.1467528632730159E+01,0.5807034377376775E+02,0,0,0,0,0,0,0,2},
      {0.3817968724121190E-06,0.2080791132907865E+01,0.8674393926896093E+03,0,0,0,1,0,0,0,1},
      {0.3730666308347880E-06,0.3531492325926554E+01,0.1149935994119150E+03,0,0,0,0,0,1,0,-1},
      {0.3504051883832708E-06,0.3688750412526320E+00,0.2878480945711384E+03,0,0,0,0,0,2,0,0}
    };
  }
  
  //Data for satellite 607, Hyperion
  static private final double T0Hyperion=51545.00;
  static private final double AMM7=0.2953088138695055E+00;
  static private final int NBTP=109;
  static private final int NBTQ=230;
  static private final int NBTZ=195;
  static private final int NBTZT=61;
  static private final double CSTP=-0.1574686065780747E-02;
  static private final double CSTQ=0.4348683610500939E+01;
  static private final int SER=0;
  static private final int FA=1;
  static private final int FR=2;
  static private final double[][] PTable={
    {   0.5269198501828300E-02,  0.1803677252541800E+01,  0.9810539955099672E-02},
    {  -0.9447929974549504E-03,  0.1379026805952163E+01,  0.9873376502713178E-01},
    {  -0.6016015548174626E-03,  0.5858534860589942E+01,  0.8892322507203211E-01},
    {   0.5148150044366294E-03,  0.3182704058493963E+01,  0.1085443049822315E+00},
    {  -0.1309800881858535E-03,  0.1281528868939203E+01,  0.1072766731000612E-01},
    {   0.1186076675279319E-03,  0.2325825636144398E+01,  0.8893412600193221E-02},
    {  -0.7587557241443920E-04,  0.4054857608048142E+01,  0.7911268511693244E-01},
    {  -0.4394073687298339E-04,  0.4986381311035763E+01,  0.1183548449373311E+00},
    {  -0.2986485373026291E-04,  0.5411031757625401E+01,  0.2943161986529901E-01},
    {   0.2422130320267833E-04,  0.3607354505083601E+01,  0.1962107991019934E-01},
    {   0.1721156885182891E-04,  0.9749793701295940E-01,  0.8800609771712566E-01},
    {  -0.1338883184199104E-04,  0.5336386476987345E+01,  0.8984035242693857E-01},
    {  -0.1302898520468604E-04,  0.2660555674891366E+01,  0.1094614323371379E+00},
    {   0.1289973076525889E-04,  0.1160724111900345E+01,  0.9924156005528160E-02},
    {  -0.1276260623147002E-04,  0.2446630393183256E+01,  0.9696923904671184E-02},
    {  -0.1206158887296372E-04,  0.1901175189554760E+01,  0.9781663767222532E-01},
    {   0.1012836012521588E-04,  0.3704852442096560E+01,  0.1076271776273250E+00},
    {  -0.9589777592386616E-05,  0.5761036923576983E+01,  0.9171273549064512E-03},
    {   0.7719706043802130E-05,  0.2251180355506342E+01,  0.6930214516183278E-01},
    {  -0.5031578459421872E-05,  0.5068732563979834E+00,  0.1281653848924308E+00},
    {  -0.4727461773298300E-05,  0.2767316284539162E+01,  0.1136827010127225E-01},
    {  -0.4490169397561938E-05,  0.4080979669446074E+01,  0.9899519638809918E-02},
    {   0.4445857119863126E-05,  0.5809560142817107E+01,  0.9721560271389426E-02},
    {   0.3929644136581539E-05,  0.8400382205444394E+00,  0.8252809808927092E-02},
    {   0.3847298333647876E-05,  0.4577005991650739E+01,  0.7819555776202598E-01},
    {  -0.3502084178980730E-05,  0.3532709224445545E+01,  0.8002981247183889E-01},
    {   0.2811429184555117E-05,  0.7593804853366057E+00,  0.1164479466491257E-01},
    {   0.2625536042038141E-05,  0.8568784223495651E+00,  0.9965089238203824E-01},
    {   0.2165574780684495E-05,  0.4888883374022804E+01,  0.3034874722020547E-01},
    {   0.2190398167707681E-05,  0.4464232927433166E+01,  0.1192719722922376E+00},
    {  -0.2173672397129618E-05,  0.5933180141227998E+01,  0.2851449251039256E-01},
    {  -0.2086152830538867E-05,  0.5508529694638360E+01,  0.1174377175824247E+00},
    {   0.1758820758802031E-05,  0.5166607002175651E+01,  0.9835176321817914E-02},
    {  -0.1729303956187257E-05,  0.4723932810087529E+01,  0.9785903588381428E-02},
    {   0.1628729863936739E-05,  0.4475031029645411E+00,  0.5949160520673310E-01},
    {  -0.1424540056085036E-05,  0.2183026940518178E+00,  0.8880960902160362E-01},
    {   0.1386736928659767E-05,  0.5215581719948487E+01,  0.8903684112246060E-01},
    {   0.1373772890937228E-05,  0.2539750917852507E+01,  0.1086579210326600E+00},
    {  -0.1342309573473890E-05,  0.3825657199135419E+01,  0.1084306889318030E+00},
    {   0.9845757121969750E-03,  0.2758053611904325E+01,  0.1974675300542636E+00},
    {   0.5722146672387600E-03,  0.4137080417856488E+01,  0.2962012950813954E+00},
    {   0.3064232824160758E-03,  0.5516107223808650E+01,  0.3949350601085272E+00},
    {   0.2328679324996273E-03,  0.6119487225812321E+00,  0.4936688251356589E+00},
    {   0.1830222466848013E-03,  0.1990975528533395E+01,  0.5924025901627907E+00},
    {   0.1428121183641368E-03,  0.3370002334485558E+01,  0.6911363551899224E+00},
    {   0.1030589558021878E-03,  0.4749029140437719E+01,  0.7898701202170543E+00},
    {   0.9162128151198170E-04,  0.2333403165314687E+01,  0.2863907551262957E+00},
    {   0.8420360344777332E-04,  0.3712429971266850E+01,  0.3851245201534275E+00},
    {   0.7477636779983881E-04,  0.6128055946389881E+01,  0.8886038852441861E+00},
    {   0.6245968777723361E-04,  0.5091456777219012E+01,  0.4838582851805592E+00},
    {  -0.5925277975412419E-04,  0.5940757670398288E+01,  0.3060118350364951E+00},
    {  -0.5866195836719566E-04,  0.3280201995506922E+01,  0.1965504026993571E+00},
    {  -0.5838551971068579E-04,  0.1036599169170870E+01,  0.4047456000636268E+00},
    {   0.5517120904542451E-04,  0.1223897445162464E+01,  0.9873376502713178E+00},
    {   0.5291804011662172E-04,  0.1872982759915942E+00,  0.5825920502076911E+00},
    {   0.4785477163890776E-04,  0.1566325081943758E+01,  0.6813258152348228E+00},
    {   0.4648293960962418E-04,  0.9543763593625246E+00,  0.1876569900991639E+00},
    {  -0.4509683939781330E-04,  0.2415625975123033E+01,  0.5034793650907585E+00},
    {   0.4393229670558661E-04,  0.2945351887895920E+01,  0.7800595802619547E+00},
    {   0.4272829406941570E-04,  0.1134097106183830E+01,  0.4927516977807525E+00},
    {   0.4154122350033837E-04,  0.2602924251114628E+01,  0.1086071415298450E+01},
    {  -0.4056750796467554E-04,  0.3794652781075196E+01,  0.6022131301178903E+00},
    {  -0.3857616137051121E-04,  0.5173679587027357E+01,  0.7009468951450221E+00},
    {   0.3815083219130052E-04,  0.4324378693848082E+01,  0.8787933452890865E+00},
    {  -0.3615550506243641E-04,  0.5433884414000304E+01,  0.1778464501440642E+00},
    {  -0.3619913197295652E-04,  0.2695210857999389E+00,  0.7996806601721539E+00},
    {   0.3211045925583688E-04,  0.5703405499800243E+01,  0.9775271103162182E+00},
    {  -0.3191646069822739E-04,  0.1648547891752100E+01,  0.8984144251992857E+00},
    {   0.3106998351274690E-04,  0.3981951057066789E+01,  0.1184805180325582E+01},
    {  -0.2797299420355516E-04,  0.8222280980834551E-01,  0.2170886099644629E+00},
    {  -0.2722632461441940E-04,  0.3027574697704266E+01,  0.9971481902264174E+00},
    {   0.2670876942507434E-04,  0.7992469985728281E+00,  0.1076260875343350E+01},
    {  -0.2297557612018265E-04,  0.4406601503656427E+01,  0.1095881955253549E+01},
    {   0.2299675108056304E-04,  0.5360977863018951E+01,  0.1283538945352713E+01},
    {   0.2232369387111974E-04,  0.2178273804524990E+01,  0.1174994640370482E+01},
    {  -0.1940722128323030E-04,  0.5785628309608589E+01,  0.1194615720280681E+01},
    {   0.1861668359762009E-04,  0.3557300610477151E+01,  0.1273728405397614E+01},
    {   0.1683393793859745E-04,  0.4568193617915357E+00,  0.1382272710379845E+01},
    {  -0.1635856648806699E-04,  0.8814698083811701E+00,  0.1293349485307813E+01},
    {   0.1543735278843180E-04,  0.4936327416429316E+01,  0.1372462170424745E+01},
    {  -0.1367379954537092E-04,  0.2260496614333335E+01,  0.1392083250334945E+01},
    {  -0.1336950395903937E-04,  0.1461249615760508E+01,  0.3158223749915947E+00},
    {  -0.1320526200861202E-04,  0.5297259127728866E+00,  0.2765802151711960E+00},
    {   0.1271138964892950E-04,  0.3216891520189780E-01,  0.1471195935451877E+01},
    {   0.1223977806847720E-04,  0.1835846167743697E+01,  0.1481006475406977E+01},
    {  -0.1131018256569493E-04,  0.3639523420285497E+01,  0.1490817015362076E+01},
    {  -0.1089636645498038E-04,  0.4659228801459085E+01,  0.2952841677264889E+00},
    {   0.1039179172259797E-04,  0.1411195721154059E+01,  0.1569929700479009E+01},
    {  -0.9298418091702524E-05,  0.5018550226237658E+01,  0.1589550780389208E+01},
    {   0.8847111426033778E-05,  0.3214872973695859E+01,  0.1579740240434109E+01},
    {   0.8461716341891088E-05,  0.2790222527106221E+01,  0.1668663465506141E+01},
    {  -0.7901292288839958E-05,  0.4561730864446125E+01,  0.2072780700093633E+00},
    {  -0.7609973567682646E-05,  0.1143917250102398E+00,  0.1688284545416340E+01},
    {   0.7430427565196998E-05,  0.3899728247258444E+01,  0.9677165703611184E+00},
    {   0.7356833812577206E-05,  0.2520701441306281E+01,  0.8689828053339868E+00},
    {   0.7165256187015007E-05,  0.5278755053210607E+01,  0.1066450335388250E+01},
    {   0.6955885415513037E-05,  0.1141674635354119E+01,  0.7702490403068549E+00},
    {   0.6885291075415051E-05,  0.4169249333058383E+01,  0.1767397230533273E+01},
    {   0.6614895521474710E-05,  0.6045833136581537E+01,  0.6715152752797231E+00},
    {   0.6331562819397564E-05,  0.4593899779648020E+01,  0.1678474005461240E+01},
    {  -0.6179612339504240E-05,  0.1493418530962401E+01,  0.1787018310443472E+01},
    {   0.6131629134251356E-05,  0.1753623357935350E+01,  0.1263917865442514E+01},
    {   0.5583256693630428E-05,  0.3132650163887515E+01,  0.1362651630469646E+01},
    {   0.5538856606051041E-05,  0.5548276139010548E+01,  0.1866130995560404E+01},
    {  -0.2676900656082160E-04,  0.2148463129341690E+01,  0.1095544904247980E+01},
    {  -0.1308495447734650E-04,  0.4324969183604490E+01,  0.2000408834733950E+01},
    {  -0.1249786948042500E-04,  0.9654256497292940E+00,  0.2962012605910000E+00},
    {  -0.2961456426220440E-05,  0.1488360573553680E+01,  0.2952841654680000E+00},
    {  -0.8783581973124320E-05,  0.2826260933757870E+00,  0.5894496611094870E+00}
  };
  static private final double[][] QTable= {
    {   0.1591300460227652E+00,0.1803677252541800E+01,0.9810539955099672E-02},
    {   0.4042489669732959E-02,0.2325825636144398E+01,0.8893412600193221E-02},
    {  -0.3674456394728999E-02,0.1281528868939203E+01,0.1072766731000612E-01},
    {   0.1876329764520020E-02,0.1379026805952163E+01,0.9873376502713178E-01},
    {  -0.1559041896665946E-02,0.5858534860589942E+01,0.8892322507203211E-01},
    {   0.1534084173919484E-02,0.5761036923576983E+01,0.9171273549064512E-03},
    {   0.1132234522571428E-02,0.3182704058493963E+01,0.1085443049822315E+00},
    {  -0.3898971540977004E-03,0.2446630393183256E+01,0.9696923904671184E-02},
    {   0.3851351224149008E-03,0.1160724111900345E+01,0.9924156005528160E-02},
    {  -0.3602762810400590E-03,0.5640232166538125E+01,0.1136160504284885E-03},
    {  -0.3108861291415183E-03,0.4054857608048142E+01,0.7911268511693244E-01},
    {  -0.3036800061325884E-03,0.5411031757625401E+01,0.2943161986529901E-01},
    {  -0.1967051700749114E-03,0.3362929749633851E+01,0.2463636671824243E-04},
    {   0.1584700380869004E-03,0.3607354505083601E+01,0.1962107991019934E-01},
    {   0.1443751571550919E-03,0.8400382205444394E+00,0.8252809808927092E-02},
    {  -0.1336552626452528E-03,0.4986381311035763E+01,0.1183548449373311E+00},
    {   0.1354323755526581E-03,0.5809560142817107E+01,0.9721560271389426E-02},
    {  -0.1344254972843197E-03,0.4080979669446074E+01,0.9899519638809918E-02},
    {  -0.1248289604761352E-03,0.2767316284539162E+01,0.1136827010127225E-01},
    {  -0.9343384687595748E-04,0.9636390319973618E+00,0.1557730146172579E-02},
    {   0.8558910102815169E-04,0.2277302416904274E+01,0.8897968371024604E-04},
    {   0.7224202068094286E-04,0.7593804853366057E+00,0.1164479466491257E-01},
    {   0.5296941292250232E-04,0.5166607002175651E+01,0.9835176321817914E-02},
    {  -0.5247540827620734E-04,0.4723932810087529E+01,0.9785903588381428E-02},
    {   0.4189085863844754E-04,0.8568784223495651E+00,0.9965089238203824E-01},
    {   0.4098739653872672E-04,0.9749793701295940E-01,0.8800609771712566E-01},
    {   0.3764144636960646E-04,0.5238888539974385E+01,0.1834254709812902E-02},
    {  -0.3531748699358457E-04,0.2660555674891366E+01,0.1094614323371379E+00},
    {  -0.2947898798731622E-04,0.1482991361185895E+01,0.8139193758498604E-02},
    {   0.2841090381393687E-04,0.3704852442096560E+01,0.1076271776273250E+00},
    {   0.2676761780821079E-04,0.2251180355506342E+01,0.6930214516183278E-01},
    {   0.2465038854019734E-04,0.2124363143897707E+01,0.1148188615170074E-01},
    {   0.2383157781622512E-04,0.3206858913559061E+00,0.1671346196601068E-02},
    {  -0.2358765186013457E-04,0.5336386476987345E+01,0.8984035242693857E-01},
    {  -0.2308684467527070E-04,0.5933180141227998E+01,0.2851449251039256E-01},
    {   0.2173788459895790E-04,0.4888883374022804E+01,0.3034874722020547E-01},
    {   0.1642710507186450E-04,0.2847974019746996E+01,0.7976285245286770E-02},
    {   0.1553609418389716E-04,0.4577005991650739E+01,0.7819555776202598E-01},
    {  -0.1337650472587841E-04,0.3532709224445545E+01,0.8002981247183889E-01},
    {  -0.1132855895484880E-04,0.6385757282977473E+00,0.1084128336043461E-01},
    {   0.1088679752730659E-04,0.1924482009580659E+01,0.1061405125957763E-01},
    {  -0.1000802392783223E-04,0.5068732563979834E+00,0.1281653848924308E+00},
    {   0.8798422861268053E-05,0.4475031029645411E+00,0.5949160520673310E-01},
    {   0.8204317973862647E-05,0.4845921110819746E+01,0.8163830125216846E-02},
    {   0.7724851326708509E-05,0.1682872495502942E+01,0.9007028650621710E-02},
    {  -0.7215481275780168E-05,0.2968778776785854E+01,0.8779796549764733E-02},
    {  -0.6984482201975051E-05,0.3289464668141760E+01,0.1045114274636580E-01},
    {  -0.6882066002549454E-05,0.5044618701443436E+01,0.1145724978498250E-01},
    {   0.6650438793970224E-05,0.4464232927433166E+01,0.1192719722922376E+00},
    {  -0.6296309881986443E-05,0.5508529694638360E+01,0.1174377175824247E+00},
    {   0.6110229923833445E-05,0.1901175189554760E+01,0.9781663767222532E-01},
    {   0.6015743140846809E-05,0.1485787415599959E+01,0.6406027912661278E-03},
    {   0.5443666713560605E-05,0.2245167900936563E+01,0.1228539745617870E-01},
    {  -0.5558326355997969E-05,0.3240941448901636E+01,0.1646709829882825E-02},
    {  -0.3958927857591537E-05,0.2643715473086239E+01,0.1806334976402677E-01},
    {   0.3941866401660965E-05,0.3558831285843477E+01,0.1081664699371637E-01},
    {  -0.3779778844595939E-05,0.5287411759214510E+01,0.1063868762629588E-01},
    {  -0.3613554557427136E-05,0.2183026940518178E+00,0.8880960902160362E-01},
    {   0.3510914428131760E-05,0.5215581719948487E+01,0.8903684112246060E-01},
    {   0.2958781539525876E-05,0.2539750917852507E+01,0.1086579210326600E+00},
    {  -0.2877066321058344E-05,0.3825657199135419E+01,0.1084306889318030E+00},
    {  -0.2684534114394913E-05,0.4603128053048671E+01,0.8982392283903467E-02},
    {   0.2527885180883367E-05,0.4852321924012415E-01,0.8804432916482976E-02},
    {  -0.2228949169524294E-05,0.4768078616983946E+01,0.2954523591572750E-01},
    {   0.2203418048212828E-05,0.6053984898266856E+01,0.2931800381487053E-01},
    {   0.2084495541017897E-05,0.2735200955529421E+01,0.4905269977549836E-01},
    {  -0.2078328653497296E-05,0.2773328739108939E+01,0.6838501780692632E-01},
    {   0.2053262782839373E-05,0.4414906483947636E+00,0.2474857501079031E-02},
    {   0.1926895597615764E-05,0.1729031971903744E+01,0.7021927251673923E-01},
    {   0.1866581488955838E-05,0.4651651272288795E+01,0.1778682520038644E-01},
    {   0.1793161742446505E-05,0.4894895828592580E+01,0.8736549492585954E-01},
    {   0.1728658611291661E-05,0.2310550508939783E+01,0.1379759248475305E+00},
    {   0.1676857763913550E-05,0.4570993537080962E+01,0.2117881005637192E-01},
    {   0.1535772080970505E-05,0.2125944501827351E+01,0.8025577708070115E-02},
    {  -0.1522980567370601E-05,0.4697810748689598E+01,0.7899906906650395E-01},
    {  -0.1495199523539624E-05,0.5960918057894030E+01,0.1784962247029556E-02},
    {  -0.1524956909904084E-05,0.4644458618573054E+01,0.1075230367672436E-01},
    {   0.1476058477371991E-05,0.3411904467406686E+01,0.7922630116736093E-01},
    {   0.1450002439077977E-05,0.2646511527500304E+01,0.1056475879679429E-01},
    {   0.1487447331838316E-05,0.4201784426484933E+01,0.1070303094328788E-01},
    {  -0.1242111402697583E-05,0.1481410003256251E+01,0.1159550220212923E-01},
    {  -0.1224261037265257E-05,0.2372321017340067E+00,0.1256192201981903E-01},
    {   0.1240886283959093E-05,0.3581232443685669E+01,0.8883424538832187E-01},
    {  -0.1221915897644608E-05,0.1852651970314636E+01,0.8901220475574236E-01},
    {  -0.1186953182299199E-05,0.2563057737878406E+01,0.2145533462001224E-01},
    {  -0.1201215374450881E-05,0.4146343090491325E+01,0.1101020351284040E+00},
    {  -0.1129771140532139E-05,0.6196463206155567E+00,0.8708897036221921E-01},
    {  -0.1097793441792323E-05,0.1602214760295107E+01,0.1239901350660719E-01},
    {   0.1028753109593422E-05,0.5688755385778249E+01,0.8918048966911464E-02},
    {  -0.1020891709037016E-05,0.5460006475398236E+01,0.1086332846659417E+00},
    {  -0.1012670463985482E-05,0.5246081193690127E+01,0.8868776233474979E-02},
    {   0.1000328987513614E-05,0.9054016415896892E+00,0.1084553252985212E+00},
    {  -0.1056701003062998E-05,0.4927011157602322E+01,0.4968106525163343E-01},
    {   0.9414415383367402E-06,0.3286668613727695E+01,0.1794973371359828E-01},
    {  -0.9256226899654040E-06,0.4366734990420207E+01,0.3126587457511192E-01},
    {  -0.8873327693660700E-06,0.9696514865671384E+00,0.5857447785182665E-01},
    {  -0.8578646315557892E-06,0.4447392725628039E+01,0.2787388971912643E-01},
    {   0.8458120789255400E-06,0.2964401364442145E+01,0.1973469596062783E-01},
    {  -0.8599515368071076E-06,0.5488874251461201E+01,0.8050214074788358E-02},
    {  -0.8217941676205146E-06,0.5389885854077239E+00,0.9048095521820468E-01},
    {   0.7998618448176001E-06,0.3178898369418413E+00,0.9169937163833544E-02},
    {  -0.7833211544506204E-06,0.1029021640000580E+01,0.1272482575375243E+00},
    {   0.7924651953382082E-06,0.6208540026541524E+01,0.6040873256163955E-01},
    {   0.7948809078699551E-06,0.9148548244318278E-01,0.3098935001147160E-01},
    {   0.7622712310485859E-06,0.2138407291288768E+01,0.1103785596920444E+00},
    {   0.7575143203880983E-06,0.2219065026496601E+01,0.1069865748360589E+00},
    {   0.7730107483777507E-06,0.1405148867350095E+01,0.2952059954900926E-01},
    {  -0.7622738839419112E-06,0.3133729340721127E+01,0.2934264018158877E-01},
    {  -0.7315354488166669E-06,0.4250307645725057E+01,0.1950746385977086E-01},
    {   0.7005448425844999E-06,0.8428342749585034E+00,0.7542188416946162E-03},
    {  -0.6870417571431780E-06,0.4343428170394308E+01,0.1184684609877596E+00},
    {   0.2477704948047725E-02,0.2758053611904325E+01,0.1974675300542636E+00},
    {   0.1177430869304958E-02,0.4137080417856488E+01,0.2962012950813954E+00},
    {   0.7097516189849207E-03,0.5516107223808650E+01,0.3949350601085272E+00},
    {   0.4276809299095375E-03,0.6119487225812321E+00,0.4936688251356589E+00},
    {   0.2883086646474427E-03,0.1990975528533395E+01,0.5924025901627907E+00},
    {   0.2445211406069563E-03,0.9543763593625246E+00,0.1876569900991639E+00},
    {   0.2079619720142985E-03,0.2333403165314687E+01,0.2863907551262957E+00},
    {   0.1997665368454137E-03,0.3370002334485558E+01,0.6911363551899224E+00},
    {   0.1532039999508838E-03,0.3712429971266850E+01,0.3851245201534275E+00},
    {   0.1391797303858132E-03,0.4749029140437719E+01,0.7898701202170543E+00},
    {  -0.1347079373955693E-03,0.5940757670398288E+01,0.3060118350364951E+00},
    {  -0.1168617638452060E-03,0.4561730864446125E+01,0.2072780700093633E+00},
    {   0.1158407408725238E-03,0.5091456777219012E+01,0.4838582851805592E+00},
    {  -0.1110204610592649E-03,0.1036599169170870E+01,0.4047456000636268E+00},
    {   0.9876297087120832E-04,0.6128055946389881E+01,0.8886038852441861E+00},
    {   0.9360835142429720E-04,0.1872982759915942E+00,0.5825920502076911E+00},
    {  -0.8787340150688395E-04,0.2415625975123033E+01,0.5034793650907585E+00},
    {   0.7626783708559587E-04,0.1566325081943758E+01,0.6813258152348228E+00},
    {  -0.7423412446912493E-04,0.3794652781075196E+01,0.6022131301178903E+00},
    {   0.7068861066099871E-04,0.1223897445162464E+01,0.9873376502713178E+00},
    {  -0.6947176438271949E-04,0.3280201995506922E+01,0.1965504026993571E+00},
    {  -0.6514978275214879E-04,0.5433884414000304E+01,0.1778464501440642E+00},
    {  -0.6245521353308536E-04,0.5173679587027357E+01,0.7009468951450221E+00},
    {   0.6236351497568400E-04,0.2945351887895920E+01,0.7800595802619547E+00},
    {  -0.5211914123734037E-04,0.2695210857999389E+00,0.7996806601721539E+00},
    {   0.5087526477014214E-04,0.2602924251114628E+01,0.1086071415298450E+01},
    {   0.5058590687048317E-04,0.4324378693848082E+01,0.8787933452890865E+00},
    {  -0.4813389965573155E-04,0.8222280980834551E-01,0.2170886099644629E+00},
    {  -0.4301007833478336E-04,0.1648547891752100E+01,0.8984144251992857E+00},
    {   0.4104292740965665E-04,0.5703405499800243E+01,0.9775271103162182E+00},
    {   0.3665390355309391E-04,0.3981951057066789E+01,0.1184805180325582E+01},
    {  -0.3538628961664771E-04,0.3027574697704266E+01,0.9971481902264174E+00},
    {   0.3326628366799721E-04,0.7992469985728281E+00,0.1076260875343350E+01},
    {  -0.2902403206479552E-04,0.4406601503656427E+01,0.1095881955253549E+01},
    {   0.2693554901487583E-04,0.2178273804524990E+01,0.1174994640370482E+01},
    {   0.2669007886238697E-04,0.6038255607411246E+01,0.3940179327536207E+00},
    {   0.2640617243698899E-04,0.5360977863018951E+01,0.1283538945352713E+01},
    {  -0.2373722745643357E-04,0.5785628309608589E+01,0.1194615720280681E+01},
    {   0.2176062809432465E-04,0.3557300610477151E+01,0.1273728405397614E+01},
    {  -0.1934646504415605E-04,0.8814698083811701E+00,0.1293349485307813E+01},
    {   0.1897373895483440E-04,0.4568193617915357E+00,0.1382272710379845E+01},
    {   0.1754329413716687E-04,0.4936327416429316E+01,0.1372462170424745E+01},
    {   0.1572430747504168E-04,0.2235905228301728E+01,0.1983846574091700E+00},
    {  -0.1571827863857085E-04,0.2260496614333335E+01,0.1392083250334945E+01},
    {  -0.1539945531353985E-04,0.1461249615760508E+01,0.3158223749915947E+00},
    {   0.1410585893877412E-04,0.3216891520189780E-01,0.1471195935451877E+01},
    {   0.1357189445488529E-04,0.1835846167743697E+01,0.1481006475406977E+01},
    {  -0.1273330553828309E-04,0.3639523420285497E+01,0.1490817015362076E+01},
    {   0.1163556995976533E-04,0.6045833136581537E+01,0.6715152752797231E+00},
    {   0.1139577901854967E-04,0.4666806330629374E+01,0.5727815102525914E+00},
    {   0.1139366110710306E-04,0.1141674635354119E+01,0.7702490403068549E+00},
    {   0.1135306132914049E-04,0.4659228801459085E+01,0.2952841677264889E+00},
    {   0.1131587409293219E-04,0.1411195721154059E+01,0.1569929700479009E+01},
    {   0.1085425461534599E-04,0.2520701441306281E+01,0.8689828053339868E+00},
    {  -0.1056787968358008E-04,0.5297259127728866E+00,0.2765802151711960E+00},
    {   0.1037117124427068E-04,0.3287779524677212E+01,0.4740477452254596E+00},
    {  -0.1027660284731680E-04,0.5018550226237658E+01,0.1589550780389208E+01},
    {   0.1027273355964679E-04,0.4322279757599272E+00,0.1885741174540704E+00},
    {   0.1006767559393374E-04,0.3899728247258444E+01,0.9677165703611184E+00},
    {   0.9633989740567134E-05,0.3214872973695859E+01,0.1579740240434109E+01},
    {   0.9178075378506732E-05,0.5278755053210607E+01,0.1066450335388250E+01},
    {   0.9065206170156284E-05,0.2790222527106221E+01,0.1668663465506141E+01},
    {   0.8851132878153258E-05,0.3614932034253890E+01,0.2971184224363018E+00},
    {   0.8253549910000110E-05,0.3745965519831884E+00,0.1165184100415382E+01},
    {  -0.8257707350863237E-05,0.1143917250102398E+00,0.1688284545416340E+01},
    {   0.7905521775023910E-05,0.1811254781712090E+01,0.2873078824812022E+00},
    {  -0.7853735166683912E-05,0.5083879248048722E+01,0.2063609426544568E+00},
    {   0.7341396041786271E-05,0.1753623357935350E+01,0.1263917865442514E+01},
    {   0.7228880718926971E-05,0.4169249333058383E+01,0.1767397230533273E+01},
    {   0.6764635964795921E-05,0.4593899779648020E+01,0.1678474005461240E+01},
    {  -0.6615254376434763E-05,0.1493418530962401E+01,0.1787018310443472E+01},
    {   0.6543758079294575E-05,0.3452225144293904E+01,0.9082249651543854E+00},
    {   0.6469301087553129E-05,0.3132650163887515E+01,0.1362651630469646E+01},
    {   0.6431124552019571E-05,0.4831251950246065E+01,0.1006958730181517E+01},
    {   0.6326716714869053E-05,0.2073198338341740E+01,0.8094912001272536E+00},
    {  -0.6344479273710200E-05,0.2855551548917284E+01,0.2854736277713893E+00},
    {   0.6126061421202252E-05,0.6210278756198226E+01,0.1105692495208649E+01},
    {   0.5748353093941175E-05,0.5548276139010548E+01,0.1866130995560404E+01},
    {   0.5708428891129938E-05,0.1306120254970812E+01,0.1204426260235781E+01},
    {   0.5753474696726435E-05,0.3190281587664252E+01,0.3860416475083339E+00},
    {   0.5648928770703093E-05,0.6941715323895785E+00,0.7107574351001218E+00},
    {   0.5649105509135010E-05,0.4511676969839675E+01,0.1461385395496778E+01},
    {   0.5432340482357107E-05,0.1908752718725049E+01,0.3753139801983278E+00},
    {   0.5352373966360052E-05,0.4993958840206052E+01,0.3958521874634336E+00},
    {   0.5250152718378343E-05,0.1134097106183830E+01,0.4927516977807525E+00},
    {  -0.5276359293355092E-05,0.2872445336914567E+01,0.1885752075470604E+01},
    {   0.5229887464078713E-05,0.2685147060922970E+01,0.1303160025262913E+01},
    {   0.4890118052517932E-05,0.5890703775791840E+01,0.1560119160523909E+01},
    {   0.4723632226316384E-05,0.4064173866875135E+01,0.1401893790290044E+01},
    {   0.4693369092482988E-05,0.5972926585600182E+01,0.1777207770488372E+01},
    {   0.4561946954009516E-05,0.6441176377831290E+00,0.1964864760587536E+01},
    {  -0.4420710568728507E-05,0.5009233967410666E+01,0.2667696752160964E+00},
    {   0.4337634584565960E-05,0.5598330033616995E+01,0.6120236700729901E+00},
    {  -0.4380976601809811E-05,0.3630207161458504E+01,0.1680359101889646E+00},
    {   0.4260384871171156E-05,0.9865452745644178E+00,0.1658852925551041E+01},
    {   0.4289012976116846E-05,0.4569308393616414E+01,0.4847754125354657E+00},
    {   0.4220548662286636E-05,0.5443200672827300E+01,0.1500627555317176E+01},
    {  -0.4199363160735397E-05,0.4251472142866728E+01,0.1984485840497735E+01},
    {   0.3732023312137390E-05,0.5390421715998777E+00,0.1599361320344308E+01},
    {   0.3635875031439811E-05,0.2023144443735291E+01,0.2063598525614668E+01},
    {   0.3406532781873487E-05,0.8980033897863482E-01,0.4945859524905654E+00},
    {  -0.3500526370624341E-05,0.4234578354869447E+01,0.3842073927985210E+00},
    {   0.3455891490801950E-05,0.5948335199568577E+01,0.5835091775625976E+00},
    {  -0.2230818670346390E-03,0.5533048146779580E+01,0.5839811452565560E-03},
    {   0.1753962398021800E-04,0.2721579555220420E+01,0.5839811452565560E-03},
    {  -0.2328181620162680E-04,0.1648225206082800E+01,0.2952860072513110E-02},
    {  -0.1600410709697660E-04,0.2171160129907190E+01,0.2035764949513110E-02},
    {  -0.3450615143245680E-05,0.2694095053731580E+01,0.1118669826513110E-02},
    {  -0.2988889508569460E-03,0.1971442394820420E+01,0.1167962290513110E-02},
    {  -0.1367821711727360E-04,0.4782910986379580E+01,0.1167962290513110E-02},
    {   0.1184653210332360E-04,0.1971481213000000E+01,0.1167962290513110E-02},
    {   0.1288836483864210E-04,0.1337860308957420E+01,0.1281578356513110E-02},
    {   0.3704937010630530E-05,0.5359801191509000E+01,0.1192598652513110E-02},
    {  -0.4092616660610000E-04,0.1221305234420420E+01,0.1751943435769670E-02},
    {   0.1339823501057820E-04,0.2148463129341690E+01,0.1095544904247980E+01},
    {   0.7254032016403240E-05,0.4324969183604490E+01,0.2000408834733950E+01},
    {   0.1460196299297440E-04,0.9654256497292940E+00,0.2962012605910000E+00},
    {   0.3460035905216260E-05,0.1488360573553680E+01,0.2952841654680000E+00},
    {  -0.1024878235180840E-04,0.2826260933757870E+00,0.5894496611094870E+00}
  };
  static private final double[][] ZTable= {
    {   0.1030661479148230E+00,0.3382691062696734E+01,-0.8924811235147779E-03},
    {   0.2448184191185018E-01,0.2860542679094136E+01, 0.2464623139167320E-04},
    {  -0.2500610695618523E-02,0.5186368315238534E+01, 0.8918058831584894E-02},
    {  -0.1653120911968409E-02,0.1579013810154933E+01,-0.1070302107861445E-01},
    {  -0.1121964769453605E-02,0.4761717868648896E+01, 0.9784128390361700E-01},
    {   0.7518101576911162E-03,0.2003664256744571E+01,-0.9962624615064656E-01},
    {   0.2580134073493171E-03,0.3807341509286372E+01,-0.8981570619554689E-01},
    {  -0.1702244907149874E-03,0.1999870042027707E+00,-0.1094367861057462E+00},
    {  -0.1630491456609473E-03,0.2822098140111162E+00, 0.1076518238587167E+00},
    {   0.1502092233208532E-03,0.2958040616107096E+01, 0.8803074394851733E-01},
    {   0.1080722389283692E-03,0.4664219931635937E+01, 0.9835186186491344E-02},
    {   0.8563197359093362E-04,0.3904839446299331E+01,-0.1809608478421229E-02},
    {  -0.7641879996400636E-04,0.5708516698841132E+01, 0.8000931476678443E-02},
    {   0.6410167941575658E-04,0.4025644203338189E+01,-0.1006097173943266E-02},
    {  -0.6379700394998453E-04,0.2739737922055278E+01,-0.7788650730862894E-03},
    {  -0.3738690787954837E-04,0.7068602606007541E+00, 0.1872859878668457E-01},
    {   0.3177451875567787E-04,0.4239569485046299E+01, 0.9875841125852346E-01},
    {   0.3067833613233151E-04,0.2101162193757531E+01,-0.1162014843352090E-01},
    {   0.2732958451060629E-04,0.4346330094694096E+01, 0.6652490226578010E-03},
    {   0.2559468055750869E-04,0.5611018761828172E+01,-0.8000516624044722E-01},
    {   0.2024562165367701E-04,0.1154363363565295E+01, 0.7822020399341766E-01},
    {  -0.2019380769983444E-04,0.1105388645792460E+01,-0.9814608072250240E-03},
    {   0.2008621003222866E-04,0.5659993479601007E+01,-0.8035014398045319E-03},
    {  -0.1811127409136190E-04,0.6058521864792713E+01,-0.2051356103371412E-01},
    {   0.1235958412008419E-04,0.1056865426552336E+01,-0.9785893723707998E-02},
    {   0.1166603449799361E-04,0.2510537513142555E+01, 0.2853913874178424E-01},
    {   0.1086995985899863E-04,0.4254844612250913E+01,-0.3032410098881379E-01},
    {  -0.1043068210990957E-04,0.3285193125683774E+01,-0.8889857884064044E-01},
    {   0.8240909734627314E-05,0.1976131306288398E-01,-0.9171174902330204E-03},
    {  -0.8205019885615929E-05,0.4624355051510043E+00,-0.8678447567965355E-03},
    {   0.7999837192197997E-05,0.1481515873141974E+01,-0.9870911879574012E-01},
    {   0.7964077512935541E-05,0.2085887066552917E+01, 0.1174623638138164E+00},
    {   0.7236960740797404E-05,0.6043246737588099E+01, 0.1085689512136231E+00},
    {   0.7270826776415052E-05,0.2419052030699373E+01,-0.2450211269687357E-02},
    {   0.6716695065274547E-05,0.4329489892888969E+01,-0.9073283355045334E-01},
    {   0.6736625416531154E-05,0.4868478478296693E+01,-0.2518783322486501E-03},
    {   0.5204438871550596E-05,0.4679495058840550E+01,-0.1192473260608459E+00},
    {  -0.5207388474970705E-05,0.3703376954052640E+01, 0.7788650730862894E-03},
    {   0.5046334690770730E-05,0.9360606695134774E+00,-0.1058940502818596E-01},
    {  -0.5035619025711394E-05,0.2221966950796389E+01,-0.1081663712904294E-01},
    {  -0.4835154150013290E-05,0.8043581976137135E+00, 0.1067346965038102E+00},
    {  -0.4611548111165785E-05,0.4543415174597078E+01, 0.9031674882013382E-02},
    {   0.4448389546383381E-05,0.5829321455879990E+01, 0.8804442781156406E-02},
    {  -0.4153052701221075E-05,0.4222729283241172E+01, 0.7360328685412315E-02},
    {  -0.3876996622725754E-05,0.5633871418203075E+01, 0.6840966403831800E-01},
    {   0.3608611557983843E-05,0.7221353878053680E+00,-0.1103539134606527E+00},
    {  -0.3113267226101596E-05,0.1131510707190392E+01,-0.7019462628534756E-01},
    {  -0.2683799905115401E-05,0.3480188999709693E+01, 0.8711361659361089E-01},
    {   0.2540204369555767E-05,0.3889564319094716E+01, 0.1272729037689160E+00},
    {  -0.2530442498805404E-05,0.5283866252251494E+01, 0.9692415654871056E-01},
    {   0.2471798836623408E-05,0.1847118769981568E+00, 0.1964572614159102E-01},
    {  -0.2393809826972641E-05,0.1229008644203352E+01, 0.1781147143177812E-01},
    {   0.2322013871583706E-05,0.2875817806298750E+01,-0.1290578660159456E+00},
    {  -0.2252919673323646E-05,0.4142071548033339E+01, 0.1075231354139779E-01},
    {  -0.2040203000028772E-05,0.5088870378225574E+01,-0.7908803888554077E-01},
    {   0.1978250600690922E-05,0.6150007347235896E+01, 0.1047578897775747E-01},
    {  -0.1817067803131338E-05,0.3062005171340829E+01,-0.2563827320115845E-02},
    {  -0.1760241959810971E-05,0.4426987829901929E+01,-0.2726735833327680E-02},
    {   0.1708248552006820E-05,0.5584896700430240E+01,-0.1079200076232469E-01},
    {  -0.1707373996050395E-05,0.3856316227059207E+01,-0.1061404139490420E-01},
    {   0.1638819397704571E-05,0.1180485424963227E+01, 0.9007038515295140E-02},
    {  -0.1549153538011889E-05,0.5536373481190116E+01,-0.1959643367880767E-01},
    {  -0.1594635824936384E-05,0.2909065898334261E+01, 0.8829079147874648E-02},
    {   0.1506189225615625E-05,0.3404472044187888E+00, 0.7542287063680470E-03},
    {   0.1387762233686380E-05,0.3503495819735592E+01,-0.8896981903681528E-04},
    {  -0.1337929891212176E-05,0.2542652842152295E+01,-0.9145290932441870E-02},
    {   0.1284796105929756E-05,0.6133167145430770E+01,-0.8092229359535366E-01},
    {   0.1274183971113668E-05,0.5347170429497385E+00,-0.8868766368801548E-02},
    {  -0.1082213439029503E-05,0.1988389129539956E+01, 0.2945626609669069E-01},
    {  -0.1083761451668346E-05,0.1676511747167893E+01, 0.7730307663851121E-01},
    {   0.1074660210279234E-05,0.2974849412157301E+00,-0.2143068838862057E-01},
    {  -0.1060143973086585E-05,0.4225525337655237E+01,-0.1382622818201617E-03},
    {  -0.1050457836219505E-05,0.1896903647096775E+01,-0.1533083914780906E-02},
    {   0.1016870649805557E-05,0.2525812640347169E+01,-0.1005433735055530E+00},
    {   0.9272030248231280E-06,0.4865682423882628E+01, 0.7246712634983826E-02},
    {   0.8522968078631408E-06,0.3032685896745151E+01, 0.2762201138687778E-01},
    {  -0.7513552848257232E-06,0.4776992995853510E+01,-0.3124122834372025E-01},
    {  -0.7452690477984808E-06,0.2935187959732193E+01,-0.6038408633024787E-01},
    {  -0.6901926058355343E-06,0.2435892232504499E+01, 0.8894787130342378E-01},
    {   0.3778282825702326E-03,0.6246374507924086E+00,-0.1983600111777784E+00},
    {  -0.3775434250722031E-03,0.6140744674601058E+01, 0.1965750489307488E+00},
    {  -0.3597821116452316E-03,0.2615612979325803E+01, 0.3940425789850124E+00},
    {  -0.2927952161795262E-03,0.1236586173373641E+01, 0.2953088139578806E+00},
    {   0.2216814079711899E-03,0.5528795952019826E+01,-0.2970937762049101E+00},
    {   0.1403880753848180E-03,0.4149769146067664E+01,-0.3958275412320419E+00},
    {   0.1282680047161120E-03,0.2428314703334209E+01,-0.1885494712226787E+00},
    {  -0.9843461962138636E-04,0.5104145505430188E+01,-0.2081705511328780E+00},
    {   0.9302812855413870E-04,0.2770742340115502E+01,-0.4945613062591737E+00},
    {   0.8608901766955960E-04,0.1049287897382047E+01,-0.2872832362498105E+00},
    {  -0.6976234551437248E-04,0.3725118699478026E+01,-0.3069043161600098E+00},
    {   0.6394074317345045E-04,0.5953446398609464E+01,-0.3860170012769422E+00},
    {   0.6387046194265387E-04,0.1391715534163339E+01,-0.5932950712863055E+00},
    {  -0.5338815029861825E-04,0.2346091893525864E+01,-0.4056380811871416E+00},
    {   0.4908475624063901E-04,0.4574419592657302E+01,-0.4847507663040740E+00},
    {   0.4811302148563021E-04,0.3994639785277966E+01, 0.4927763440121442E+00},
    {   0.4486679433374308E-04,0.1268872821117562E-01,-0.6920288363134373E+00},
    {  -0.4178177074434045E-04,0.9670650875737010E+00,-0.5043718462142733E+00},
    {  -0.3880672848400252E-04,0.8119357267840028E+00, 0.3842320390299127E+00},
    {   0.3819239194924841E-04,0.3195392786705140E+01,-0.5834845313312059E+00},
    {  -0.3553990230264197E-04,0.5716094228011420E+01, 0.2854982740027810E+00},
    {   0.3415645857185234E-04,0.5373666591230129E+01, 0.5915101090392759E+00},
    {   0.3339668674029588E-04,0.4419290231867604E+01, 0.4038531189401121E+00},
    {  -0.3301162776329309E-04,0.5871223588801118E+01,-0.6031056112414052E+00},
    {   0.3180628253542403E-04,0.4916847229438594E+01,-0.7907626013405691E+00},
    {   0.3003432659990370E-04,0.1816365980752976E+01,-0.6822182963583376E+00},
    {   0.2729633446248457E-04,0.1024890671898113E+00,-0.1974428838228719E+00},
    {  -0.2629885713884026E-04,0.4492196782848956E+01,-0.7018393762685369E+00},
    {   0.2473774782254921E-04,0.3040263425915440E+01, 0.3051193539129803E+00},
    {  -0.2417336169407593E-04,0.4337067422059258E+01, 0.1867645089756491E+00},
    {   0.2384156418867662E-04,0.4373391748008135E+00,-0.7809520613854695E+00},
    {   0.2259673699575893E-04,0.3537820423486432E+01,-0.8894963663677009E+00},
    {  -0.2109852115260203E-04,0.3113169976896794E+01,-0.8005731412956687E+00},
    {   0.1902408812662748E-04,0.5341497676028232E+01,-0.8796858264126013E+00},
    {   0.1706204149397460E-04,0.4231991955876010E+01,-0.1787389312675790E+00},
    {  -0.1698064243423227E-04,0.1734143170944632E+01,-0.8993069063228005E+00},
    {   0.1679462249970688E-04,0.2533390169517458E+01, 0.1769539690205495E+00},
    {   0.1603779600347512E-04,0.2158793617534270E+01,-0.9882301313948326E+00},
    {   0.1519891681318009E-04,0.3962470870076070E+01,-0.9784195914397330E+00},
    {   0.1379512640799549E-04,0.2852965149923847E+01,-0.2774726962947108E+00},
    {  -0.1366561535006994E-04,0.3551163649924672E+00,-0.9980406713499322E+00},
    {   0.1279090832954947E-04,0.4606588507859199E+01, 0.9864451691478030E+00},
    {   0.1243806749353366E-04,0.3464913872505078E+01, 0.2161961288409481E+00},
    {   0.1225216491402911E-04,0.3227561701907034E+01, 0.8877114041206713E+00},
    {   0.1225443721836814E-04,0.2370683279557469E+01, 0.7880605117386331E+00},
    {  -0.1221520345313342E-04,0.2190962532736164E+01, 0.4829658040570445E+00},
    {   0.1213225148001182E-04,0.2583444064123905E+01,-0.1077153356466865E+01},
    {   0.1198327722769453E-04,0.1473938343971685E+01,-0.3762064613218426E+00},
    {   0.1135648805958188E-04,0.7797668115821050E+00,-0.1086963896421964E+01},
    {  -0.1098010740280340E-04,0.5259274866219886E+01,-0.1096774436377064E+01},
    {   0.1069580937462716E-04,0.9491153801952202E-01,-0.4749402263489743E+00},
    {   0.1067386083025773E-04,0.5798317037819767E+01, 0.5025868839672437E+00},
    {   0.9693581297810074E-05,0.5985615313811360E+01, 0.1085178934174935E+01},
    {   0.9678835539512692E-05,0.1204417258171743E+01,-0.1175887121493997E+01},
    {   0.9562800946711422E-05,0.4999070039246940E+01,-0.5736739913761062E+00},
    {   0.9468784690060410E-05,0.1848534895954872E+01, 0.7889776390935395E+00},
    {  -0.8797753682670658E-05,0.3880248060267724E+01,-0.1195508201404196E+01},
    {   0.8599214007703230E-05,0.5618596290998461E+01, 0.1974921762856553E+00},
    {   0.8485093197565512E-05,0.3620043233294777E+01,-0.6724077564032379E+00},
    {   0.7994800633732257E-05,0.5683925312809524E+01,-0.1185697661449096E+01},
    {   0.7702743188457386E-05,0.6108575759399161E+01,-0.1274620886521128E+01},
    {   0.7473079103968928E-05,0.2241016427342615E+01,-0.7711415214303697E+00},
    {   0.7388692911769633E-05,0.3912416975469620E+01, 0.2756877340476813E+00},
    {   0.7144251731442444E-05,0.1081456812583942E+01, 0.1183912699202067E+01},
    {  -0.7043838984557596E-05,0.2501221254315563E+01,-0.1294241966431328E+01},
    {   0.6782083637837408E-05,0.1921441446936226E+01,-0.3167148561151095E+00},
    {   0.6750671838792509E-05,0.5424146409840631E+00,-0.4154486211422413E+00},
    {   0.6651880335792781E-05,0.3300468252888388E+01,-0.2179810910879777E+00},
    {   0.6570135525528793E-05,0.5446573142211480E+01,-0.5141823861693731E+00},
    {   0.6551076448609189E-05,0.8619896213904532E+00,-0.8698752864575016E+00},
    {   0.6445693456193227E-05,0.4843940678457241E+01, 0.3149298938680800E+00},
    {   0.6221700343295405E-05,0.4067546336259318E+01,-0.6129161511965049E+00},
    {  -0.6210141308502555E-05,0.4516788168880564E+01, 0.4918592166572376E+00},
    {   0.6128034935351225E-05,0.4729548953446996E+01,-0.1373354651548260E+01},
    {   0.5752222076851271E-05,0.2688519530307156E+01,-0.7116499162236366E+00},
    {   0.5720920731236437E-05,0.5766148122617870E+01,-0.9686090514846332E+00},
    {   0.5708438487292833E-05,0.4949016144640491E+01, 0.6804333341113080E+00},
    {  -0.5618758010321190E-05,0.1122194448363397E+01,-0.1392975731458459E+01},
    {   0.5586433308293342E-05,0.4304898506857362E+01,-0.1284431426476228E+01},
    {   0.5455429436394752E-05,0.2460483618536103E+01, 0.1282646464229199E+01},
    {   0.5234563142340540E-05,0.1309492724354994E+01,-0.8103836812507684E+00},
    {   0.4976902542853610E-05,0.4387121316665708E+01,-0.1067342816511765E+01},
    {   0.4951970227904307E-05,0.1661236619963279E+01, 0.2063855888858485E+00},
    {  -0.4950572722250481E-05,0.4581997121827591E+01,-0.2072534237779716E+00},
    {   0.4864691847876214E-05,0.3350522147494835E+01,-0.1472088416575392E+01},
    {   0.4711996026340074E-05,0.6213651225582409E+01,-0.9091174462779002E+00},
    {   0.4647700174472706E-05,0.5006647568417229E+01,-0.2961766488500037E+00},
    {  -0.4463536929701217E-05,0.6026352949590816E+01,-0.1491709496485591E+01},
    {   0.4423835447429758E-05,0.3839510424488269E+01, 0.1381380229256330E+01},
    {   0.4297419749912965E-05,0.3008094510713546E+01,-0.1166076581538897E+01},
    {   0.4206030088019200E-05,0.4834624419630248E+01,-0.1007851211305032E+01},
    {  -0.4097668847161432E-05,0.2273185342544510E+01, 0.7000544140215073E+00},
    {   0.3894866749084315E-05,0.4695080900027104E+00, 0.6902438740664076E+00},
    {   0.3842326107820616E-05,0.1971495341542673E+01,-0.1570822181602524E+01},
    {   0.3843534248051829E-05,0.2925871700905197E+01,-0.1383165191503360E+01},
    {   0.3724216491694377E-05,0.3455597613678086E+01,-0.1106584976332164E+01},
    {   0.3699201962974299E-05,0.1629067704761385E+01,-0.1264810346566029E+01},
    {  -0.3538397263248576E-05,0.4647326143638654E+01,-0.1590443261512723E+01},
    {   0.3531502882337352E-05,0.5218537230440430E+01, 0.1480113994283462E+01},
    {   0.3397352921611207E-05,0.7144377897710434E+00, 0.2962259413127871E+00},
    {  -0.6917942470086600E-05,0.1411763016727970E+01,-0.2060411181513110E-02},
    {  -0.2954159247722890E-05,0.8888280929035790E+00,-0.1143316058513110E-02},
    {   0.1283396500574140E-04,0.3810125383210770E+01, 0.3084677457434440E-03},
    {  -0.1541304650138550E-05,0.4333060307035160E+01,-0.6086273772565560E-03},
    {  -0.1632299947318510E-04,0.2633068251148390E+01,-0.3084677457434440E-03},
    {   0.2885109484385860E-05,0.2110133327324000E+01, 0.6086273772565560E-03},
    {  -0.8365240462865090E-05,0.5781567778031190E+01, 0.1476430036256560E-02},
    {   0.1927636081642950E-03,0.5031430617631190E+01, 0.2060411181513110E-02},
    {   0.8231557260635240E-04,0.5554365541455580E+01, 0.1143316058513110E-02},
    {   0.5173543661985320E-04,0.5354647806368810E+01, 0.2755133995131110E-03},
    {   0.2832059006428450E-05,0.4831712882544420E+01, 0.1192608522513110E-02},
    {   0.3269372364602020E-04,0.4281293457231190E+01, 0.2644392326769670E-02},
    {   0.1186021515881310E-04,0.4804228381055580E+01, 0.1727297203769670E-02},
    {   0.4042027917523730E-04,0.4348631061277680E+01, 0.2953088117000000E+00},
    {   0.1108533869996140E-04,0.2139088834397980E+00, 0.1390853715947980E+01},
    {   0.8801022040147010E-05,0.4066004967901901E+01,-0.2941408494094870E+00}
  };
  static private final double[][] ZTTable= {
    {   0.4955243850983661E-02,0.3221557438053959E+01, 0.0000000000000000E+00},
    {   0.5948511882002843E-02,0.3864510578695415E+01,-0.1136160504284885E-03},
    {   0.1535891024624297E-02,0.6141812995599688E+01,-0.2463636671824243E-04},
    {  -0.1497345442720214E-03,0.2900871546698053E+01,-0.1671346196601068E-02},
    {  -0.4911846033201391E-04,0.2378723163095456E+01,-0.7542188416946162E-03},
    {   0.2189031408512060E-04,0.4386658962298012E+01,-0.1030743405334940E-02},
    {   0.1496627946379793E-04,0.3543824687339509E+01,-0.1784962247029556E-02},
    {   0.1404188064697451E-04,0.4704548799239854E+01, 0.8139193758498604E-02},
    {  -0.1314294835383983E-04,0.3342362195092817E+01, 0.8035113044779626E-03},
    {   0.8389523097875214E-05,0.1097194294156252E+01,-0.1148188615170074E-01},
    {   0.7467530692297956E-05,0.1856574779492858E+01, 0.1629085132118349E-03},
    {  -0.4916203322979170E-05,0.5243537384647577E+01, 0.9862014897670330E-01},
    {   0.4509531977488889E-05,0.3021676303736911E+01,-0.8678348921231047E-03},
    {  -0.4145588215282021E-05,0.6235691297937791E+00,-0.1760325880311314E-02},
    {   0.3839542395242535E-05,0.2485483772743252E+01,-0.9884738107756028E-01},
    {   0.3566024262476873E-05,0.2060833326153614E+01,-0.9924156005528160E-02},
    {   0.3030592492776347E-05,0.4279898352650216E+01, 0.9706241883053072E-01},
    {  -0.2682452719829683E-05,0.3743705821656556E+01,-0.9171273549064512E-03},
    {  -0.2004460730134680E-05,0.1521844740745890E+01,-0.1004051112237328E+00},
    {  -0.1562250771899869E-05,0.5347501939881310E+01, 0.8025577708070115E-02},
    {  -0.1398643272650284E-05,0.2499527920134314E+01, 0.4929246278334639E-04},
    {   0.1255563077270305E-05,0.2699409054451362E+01, 0.9171273549064512E-03},
    {  -0.1272802367749216E-05,0.1014207461911818E+00,-0.8431985254048623E-03},
    {   0.1221711462471724E-05,0.2250407446020733E+00, 0.1794973371359828E-01},
    {  -0.1189056338328650E-05,0.3325521993287691E+01,-0.9059457126863318E-01},
    {  -0.1121833101144503E-05,0.2476221100108415E+01, 0.8725187887543105E-01},
    {   0.1116583047991100E-05,0.6083575605192015E+01, 0.1068729587856304E+00},
    {  -0.8876506818660424E-06,0.1740147434797708E+01,-0.1159550220212923E-01},
    {   0.8288384247459950E-06,0.6001352795383670E+01,-0.1102156511788325E+00},
    {   0.8308837128254335E-06,0.5750459105536549E+00,-0.1056475879679429E-01},
    {   0.7816180007614254E-06,0.4507463719336871E+01,-0.2272321008569770E-03},
    {  -0.7628434629767395E-06,0.3439860132105777E+01, 0.8880960902160362E-01},
    {   0.7436509468905391E-06,0.1188679776599435E+01, 0.1950746385977086E-01},
    {  -0.7276927363562636E-06,0.2571560736118137E+00,-0.1973469596062783E-01},
    {   0.7144299265706248E-06,0.3807760720227052E+00,-0.9417637216246936E-03},
    {   0.6925549696724206E-06,0.4182400415637256E+01, 0.9056321113405056E-02},
    {   0.6584378044173228E-06,0.3423019930300651E+01,-0.2588473551507519E-02},
    {  -0.6290271131270627E-06,0.6190336214839812E+01, 0.8779796549764733E-02},
    {  -0.5558619276909469E-06,0.5576702348794032E+01,-0.2129242610680041E-01},
    {   0.5420684261778156E-06,0.4600584244006122E+01, 0.9873376502713178E-01},
    {  -0.4533375266393237E-06,0.2582981709756212E+01,-0.1084128336043461E-01},
    {  -0.4241248416918695E-06,0.1842530632101796E+01,-0.9873376502713178E-01},
    {   0.4217192739334928E-06,0.3757749969047618E+01, 0.9797954618543716E-01},
    {   0.4270467112203478E-06,0.2427246382335581E+01, 0.8050214074788358E-02},
    {  -0.4111285058745586E-06,0.1417880185512158E+01,-0.9810539955099672E-02},
    {   0.3911603306295834E-06,0.1538684942551017E+01,-0.9007028650621710E-02},
    {   0.3588448340600037E-06,0.4289161025285053E+01,-0.8903684112246060E-01},
    {  -0.3517973623230076E-06,0.5619664611997091E+01, 0.8924909881882087E-03},
    {   0.3148398194156444E-05,0.1106456966791090E+01,-0.1975811461046921E+00},
    {  -0.2169784851229061E-05,0.3393788834201592E+00, 0.1973539140038351E+00},
    {   0.1834573960516513E-05,0.5658925158602377E+01, 0.1957961838576625E+00},
    {   0.1780425037193046E-05,0.6010615468018507E+01,-0.2963149111318239E+00},
    {  -0.9122789187630200E-05,0.1250154422359160E+01,-0.1167962290513110E-02},
    {  -0.2465654101797190E-04,0.3971733977579580E+01,-0.5839811452565560E-03},
    {   0.2465654101797190E-04,0.2471459656779580E+01, 0.5839811452565560E-03},
    {   0.9485688859717310E-05,0.5943176372400000E+01, 0.5839811452565560E-03},
    {   0.3982965022125390E-05,0.4559457126137000E+01, 0.1281578356513110E-02},
    {   0.3661001499550570E-05,0.5193078030179580E+01, 0.1167962290513110E-02},
    {   0.1144957852083430E-05,0.2298212701509000E+01, 0.1192598652513110E-02},
    {  -0.1609132692775700E-03,0.5193039212000000E+01, 0.1167962290513110E-02},
    {  -0.2213337603547740E-04,0.4442902051600000E+01, 0.1751943435769670E-02}
  };
  static {
    satArray=new Ephemeris[9];
    satArray[0]=null;
    for(int i=1;i<=8;i++) {
      satArray[i]=new SaturnSatTASS17(i-1);
    }
    satMassRatio=new double[] {
    37931284.5,
    2.56,
    5.77,
    41.21,
    73.13,
    154.59,
    8978.03,
    0.72,
    131.72
  }; //GM, km and s

  }
  public MathVector CalcPos(Time TT) {
	return defaultCalcPos(TT);
  }
  public MathVector CalcVel(Time TT) {
	return defaultCalcVel(TT);
  }
}
