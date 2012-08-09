package org.kwansystems.space.ephemeris.earth;

import java.io.*;

import org.kwansystems.space.ephemeris.*;
import static org.kwansystems.tools.Scalar.*;
import org.kwansystems.tools.Polynomial;
import org.kwansystems.tools.rotation.MathMatrix;
import org.kwansystems.tools.rotation.Rotator;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeEpoch;
import org.kwansystems.tools.time.TimeScale;
import org.kwansystems.tools.time.TimeUnits;
import org.kwansystems.tools.vector.*;

import static java.lang.Math.*;
import static org.kwansystems.space.ephemeris.RotatorEphemeris.Frame.*;

/** Geocentric moon position from Theory ELP2000-82B. Comparison with Horizons/DE405
 * every day of 1990 at 0:00 shows a maximum error of 216m in position and 0.61mm/s
 * in velocity. Interestingly, there seems to be a scale difference, as the mean radial
 * difference is 20m less than Horizons. In any event, this is more than adequate. 
 */
public class EarthSatELP2000 extends TableEphemeris {
  private byte[][][] ELPInt;
  private double[][][] ELPArg;
  private static int[] NumInt=new int[] {
    0,            //There is no table 0
    4,4,4,        //Table 1-3 
    5,5,5,5,5,5,  //Table 4-9 
    11,11,11,11,11,11, //Tables 10-15
    11,11,11,11,11,11, //Tables 16-21
    5,5,5,5,5,5,5,5,5,5,5,5,5,5,5  //Table 22-36
  };
  private static int[] NumArg=new int[] {
    0,            //There is no table 0
    6,6,6,        //Table 1-3 
    2,2,2,2,2,2,  //Table 4-9 
    2,2,2,2,2,2,  //Tables 10-15
    2,2,2,2,2,2,  //Tables 16-21
    2,2,2,2,2,2,2,2,2,2,2,2,2,2,2  //Table 22-36
  };
  protected void LoadText() throws IOException {
    ELPArg=new double[37][][];
    ELPInt=new byte[37][][];
    for(int i=1;i<=36;i++) {
      String infn=String.format("Data/EarthSatELP2000/ELP%02d.dat",i);
      LineNumberReader inf=new LineNumberReader(new FileReader(infn));
      String S=inf.readLine();
      S=S.substring(2);
      System.out.println(""+i+": x: "+S);
      int x=Integer.parseInt(S);
      S=inf.readLine();
      S=S.substring(2);
      System.out.println(""+i+": y: "+S);
      ELPArg[i]=new double[x][NumArg[i]];
      ELPInt[i]=new byte[x][NumInt[i]];
      for(int j=0;j<x;j++) {
        S=inf.readLine();
        String[] shatter=S.split("\\p{Space}+");
        for(int k=0;k<NumInt[i];k++) {
          ELPInt[i][j][k]=Byte.parseByte(shatter[k+1]);
        }
        for(int k=0;k<NumArg[i];k++) {
          ELPArg[i][j][k]=Double.parseDouble(shatter[k+1+NumInt[i]]);
        }
      }
      inf.close();
    }
  }
  protected String SerialFilenameCore() {
	  return "EarthSatELP2000/ELP2000";
  }
  protected void SaveSerial(ObjectOutputStream ouf) throws IOException {
    ouf.writeObject(ELPInt);
    ouf.writeObject(ELPArg);
  }
  protected void LoadSerial(ObjectInputStream inf) throws IOException, ClassNotFoundException {
    ELPInt=(byte[][][])inf.readObject();
    ELPArg=(double[][][])inf.readObject();
  }
  private static double Nu  = 1732559343.18;   //"/cy
  private static double Nt  =  129597742.34;   //"/cy
  private static double Ms  = Nt/Nu;           // 
  private static double Det = -0.12879;        // "
  private static double DE  =  0.01789;        // "
  private static double DG  = -0.08066;        // "
  private static double Dnt = -0.0642;         // "/cy
  private static double Dnu =  0.55604;        // "/cy
  private static double Alp =  0.0026;         //???
  private static double Mys =206264.81;        //mystery denominator in DA1-3
  
  private static double Pc  = sToRadians(5029.0966);   //["/cy] precession constant in J2000.0
  //constant is in "/cy, final Pc is in rad/cy
  
  private static Polynomial W1Poly=  new Polynomial(new double[] {
      sToRadians(- 0.00003169),sToRadians(+ 0.006604),sToRadians(-  5.8883),sToRadians(+1732559343.73604),dmsToRadians(218,18,59.95571)
  },Polynomial.order.ConstLast);
  private static Polynomial W1_lPoly=new Polynomial(new double[] {
                                                                            sToRadians(+1732559343.73604),dmsToRadians(218,18,59.95571)
  },Polynomial.order.ConstLast);
  private static Polynomial  M_lPoly=new Polynomial(new double[] {
                                                                            sToRadians(+ 129597742.2758 ),dmsToRadians(100,27,59.22059)
  },Polynomial.order.ConstLast);
  private static Polynomial  DPoly=  new Polynomial(new double[] {
      sToRadians(- 0.00003184),sToRadians(+ 0.006595),sToRadians(-  5.8681),sToRadians(+1602961601.4603 ),dmsToRadians(297,51, 0.73512)
  },Polynomial.order.ConstLast);
  private static Polynomial  D_lPoly=new Polynomial(new double[] {
                                                                            sToRadians(+1602961601.4603 ),dmsToRadians(297,51, 0.73512)
  },Polynomial.order.ConstLast);
  private static Polynomial LtPoly=  new Polynomial(new double[] {
                               sToRadians(+ 0.000147),sToRadians(-  0.5529),sToRadians(+ 129596581.0474 ),dmsToRadians(357,31,44.79306)
  },Polynomial.order.ConstLast);
  private static Polynomial Lt_lPoly=new Polynomial(new double[] {
                                                                            sToRadians(+ 129596581.0474 ),dmsToRadians(357,31,44.79306)
  },Polynomial.order.ConstLast);
  private static Polynomial  LPoly=  new Polynomial(new double[] {
      sToRadians(- 0.00024470),sToRadians(+ 0.051651),sToRadians(+ 32.3893),sToRadians(+1717915923.4728 ),dmsToRadians(134,57,48.28096)
  },Polynomial.order.ConstLast);
  private static Polynomial  L_lPoly=new Polynomial(new double[] {
                                                                            sToRadians(+1717915923.4728 ),dmsToRadians(134,57,48.28096)
  },Polynomial.order.ConstLast);
  private static Polynomial  FPoly=  new Polynomial(new double[] {
      sToRadians(+ 0.00000417),sToRadians(- 0.001021),sToRadians(- 12.2505),sToRadians(+1739527263.0983 ),dmsToRadians( 93,16,19.55755)
  },Polynomial.order.ConstLast);
  private static Polynomial  F_lPoly=new Polynomial(new double[] {
                                                                            sToRadians(+1739527263.0983 ),dmsToRadians( 93,16,19.55755)
  },Polynomial.order.ConstLast);
  
  private static Polynomial MePoly   = new Polynomial(new double[] {sToRadians(+ 538101628.68898),dmsToRadians(252,15, 3.25986)},Polynomial.order.ConstLast);
  private static Polynomial VPoly    = new Polynomial(new double[] {sToRadians(+ 210664136.43355),dmsToRadians(181,58,47.28305)},Polynomial.order.ConstLast);
  private static Polynomial MaPoly   = new Polynomial(new double[] {sToRadians(+  68905077.59284),dmsToRadians(355,25,59.78866)},Polynomial.order.ConstLast);
  private static Polynomial JPoly    = new Polynomial(new double[] {sToRadians(+  10925660.42861),dmsToRadians( 34,21, 5.34212)},Polynomial.order.ConstLast);
  private static Polynomial SPoly    = new Polynomial(new double[] {sToRadians(+   4399609.65932),dmsToRadians( 50, 4,38.89694)},Polynomial.order.ConstLast);
  private static Polynomial UPoly    = new Polynomial(new double[] {sToRadians(+   1542481.19393),dmsToRadians(314, 3,18.01841)},Polynomial.order.ConstLast);
  private static Polynomial NPoly    = new Polynomial(new double[] {sToRadians(+    786550.32074),dmsToRadians(304,20,55.19575)},Polynomial.order.ConstLast);
  
  private double EvalTable01_03(int n, TrigType mode, double D, double Lt, double L, double F) {
    double S=0;
    for(int i=0;i<ELPArg[n].length;i++) {
      double DA = -Ms*(ELPArg[n][i][1] + 2*Alp*ELPArg[n][i][5]/(3*Ms))*(Dnu/Nu) + (ELPArg[n][i][1] + 2*Alp*ELPArg[n][i][5]/(3*Ms))*(Dnt/Nu) + (ELPArg[n][i][2]*DG + ELPArg[n][i][3]*DE + ELPArg[n][i][4]*Det)/Mys;
      double arg=ELPInt[n][i][0]*D + ELPInt[n][i][1]*Lt + ELPInt[n][i][2]*L + ELPInt[n][i][3]*F;
      arg=mode.eval(arg); 
      S+=((ELPArg[n][i][0]+DA))*arg;
    }
    return S;
  }
  
  private double EvalTable04_09_22_36(int n, double T, double Zeta, double D_l, double Lt_l, double L_l, double F_l) {
    double S=0;
    for(int i=0;i<ELPArg[n].length;i++) {
      S+=T*(ELPArg[n][i][1])*Math.sin(ELPInt[n][i][0]*Zeta + ELPInt[n][i][1]*D_l + ELPInt[n][i][2]*Lt_l + ELPInt[n][i][3]*L_l + ELPInt[n][i][4]*F_l + toRadians(ELPArg[n][i][0]));
    }
    return S;
  }
  private double EvalTable10_21(int n, double T, double Me, double V, double M_l, double Ma, double J, double S, double U, double N, double D_l, double L_l, double F_l) {
    double SS=0;
    for(int i=0;i<ELPArg[n].length;i++) {
      SS+=T*(ELPArg[n][i][1])*Math.sin(ELPInt[n][i][0]*Me + ELPInt[n][i][1]*V +ELPInt[n][i][2]*M_l + ELPInt[n][i][3]*Ma + ELPInt[n][i][4]*J + ELPInt[n][i][5]*S + ELPInt[n][i][6]*U + ELPInt[n][i][7]*N + ELPInt[n][i][8]*D_l + ELPInt[n][i][9]*L_l + ELPInt[n][i][10]*F_l + toRadians(ELPArg[n][i][0]));
    }
    return SS;
  }	
  public MathVector CalcPos(Time TT) {
    // Julian century
    //Strangely enough, this really is TDB. I would have thought the theory used TDT,
    //as all lunar theories I had seen use TDT.
	  double T=TT.get(TimeUnits.Centuries,TimeScale.TDB,TimeEpoch.J2000);

	  double W1  = mlmod(W1Poly.eval(T),2*Math.PI);
    double W1_l= mlmod(W1_lPoly.eval(T),2*Math.PI);
    double M_l = mlmod(M_lPoly.eval(T),2*Math.PI);
    double D   = mlmod(DPoly.eval(T),2*Math.PI);
    double D_l = mlmod(D_lPoly.eval(T),2*Math.PI);
    double Lt  = mlmod(LtPoly.eval(T),2*Math.PI);
    double Lt_l= mlmod(Lt_lPoly.eval(T),2*Math.PI);
    double L   = mlmod(LPoly.eval(T),2*Math.PI);
    double L_l = mlmod(L_lPoly.eval(T),2*Math.PI);
    double F   = mlmod(FPoly.eval(T),2*Math.PI);
    double F_l = mlmod(F_lPoly.eval(T),2*Math.PI);
    
    double Zeta = W1_l + (Pc*T);
    
    // planetary longitude (VSOP82)
    double Me   = mlmod(MePoly.eval(T),2*Math.PI);
    double V    = mlmod(VPoly.eval(T), 2*Math.PI);
    double Ma   = mlmod(MaPoly.eval(T),2*Math.PI);
    double J    = mlmod(JPoly.eval(T), 2*Math.PI);
    double S    = mlmod(SPoly.eval(T), 2*Math.PI);
    double U    = mlmod(UPoly.eval(T), 2*Math.PI);
    double N    = mlmod(NPoly.eval(T), 2*Math.PI);
    
    double T2  = T*T;
    
    double Sl=0;
    double Sb=0;
    double Sr=0;
    Sl+=EvalTable01_03(1,TrigType.SIN,D,Lt,L,F);
    Sb+=EvalTable01_03(2,TrigType.SIN,D,Lt,L,F);
    Sr+=EvalTable01_03(3,TrigType.COS,D,Lt,L,F);
    
    Sl+=EvalTable04_09_22_36( 4,1,Zeta,D_l,Lt_l,L_l,F_l);
    Sb+=EvalTable04_09_22_36( 5,1,Zeta,D_l,Lt_l,L_l,F_l);
    Sr+=EvalTable04_09_22_36( 6,1,Zeta,D_l,Lt_l,L_l,F_l);

    Sl+=EvalTable04_09_22_36( 7,T,Zeta,D_l,Lt_l,L_l,F_l);
    Sb+=EvalTable04_09_22_36( 8,T,Zeta,D_l,Lt_l,L_l,F_l);
    Sr+=EvalTable04_09_22_36( 9,T,Zeta,D_l,Lt_l,L_l,F_l);
    
    Sl+=EvalTable10_21(10,1,Me,V,M_l,Ma,J,S,U,N,D_l,L_l,F_l);
    Sb+=EvalTable10_21(11,1,Me,V,M_l,Ma,J,S,U,N,D_l,L_l,F_l);
    Sr+=EvalTable10_21(12,1,Me,V,M_l,Ma,J,S,U,N,D_l,L_l,F_l);

    Sl+=EvalTable10_21(13,T,Me,V,M_l,Ma,J,S,U,N,D_l,L_l,F_l);
    Sb+=EvalTable10_21(14,T,Me,V,M_l,Ma,J,S,U,N,D_l,L_l,F_l);
    Sr+=EvalTable10_21(15,T,Me,V,M_l,Ma,J,S,U,N,D_l,L_l,F_l);

    Sl+=EvalTable10_21(16,1,Me,V,M_l,Ma,J,S,U,D_l,Lt_l,L_l,F_l);
    Sb+=EvalTable10_21(17,1,Me,V,M_l,Ma,J,S,U,D_l,Lt_l,L_l,F_l);
    Sr+=EvalTable10_21(18,1,Me,V,M_l,Ma,J,S,U,D_l,Lt_l,L_l,F_l);

    Sl+=EvalTable10_21(19,T,Me,V,M_l,Ma,J,S,U,D_l,Lt_l,L_l,F_l);
    Sb+=EvalTable10_21(20,T,Me,V,M_l,Ma,J,S,U,D_l,Lt_l,L_l,F_l);
    Sr+=EvalTable10_21(21,T,Me,V,M_l,Ma,J,S,U,D_l,Lt_l,L_l,F_l);
    
    Sl+=EvalTable04_09_22_36(22,1 ,0,D_l,Lt_l,L_l,F_l);
    Sb+=EvalTable04_09_22_36(23,1 ,0,D_l,Lt_l,L_l,F_l);
    Sr+=EvalTable04_09_22_36(24,1 ,0,D_l,Lt_l,L_l,F_l);

    Sl+=EvalTable04_09_22_36(25,T ,0,D_l,Lt_l,L_l,F_l);
    Sb+=EvalTable04_09_22_36(26,T ,0,D_l,Lt_l,L_l,F_l);
    Sr+=EvalTable04_09_22_36(27,T ,0,D_l,Lt_l,L_l,F_l);

    Sl+=EvalTable04_09_22_36(28,1 ,0,D_l,Lt_l,L_l,F_l);
    Sb+=EvalTable04_09_22_36(29,1 ,0,D_l,Lt_l,L_l,F_l);
    Sr+=EvalTable04_09_22_36(30,1 ,0,D_l,Lt_l,L_l,F_l);

    Sl+=EvalTable04_09_22_36(31,1 ,0,D_l,Lt_l,L_l,F_l);
    Sb+=EvalTable04_09_22_36(32,1 ,0,D_l,Lt_l,L_l,F_l);
    Sr+=EvalTable04_09_22_36(33,1 ,0,D_l,Lt_l,L_l,F_l);

    Sl+=EvalTable04_09_22_36(34,T2,0,D_l,Lt_l,L_l,F_l);
    Sb+=EvalTable04_09_22_36(35,T2,0,D_l,Lt_l,L_l,F_l);
    Sr+=EvalTable04_09_22_36(36,T2,0,D_l,Lt_l,L_l,F_l);

    // convert arcsec to radians
    double MoonLon = W1 + sToRadians(Sl);
    double MoonLat = sToRadians(Sb);
    double MoonRad = Sr*1000;
    
    // coordinates are in the inertial mean ecliptic of date and departure point \gamma_{2000}^{'}
    
    // convert to range [0, 2*Math.PI]
    MoonLon = mlmod(MoonLon,2*Math.PI);
    MoonLat = mlmod(MoonLat,2*Math.PI);
    
    return elpNatural(MoonLon,MoonLat,MoonRad);
    
  }
  
  
  
/*
%--------------------------------------------
% coordinates conversion section
%--------------------------------------------
 */
  
  private static Polynomial PPoly=new Polynomial(new double[] {0.463486e-14,0.2507948e-11,0.5417367e-9,0.47020439e-6, 0.101803910e-4,0},Polynomial.order.ConstLast);
  private static Polynomial QPoly=new Polynomial(new double[] {0.320334e-14,0.1371808e-11,0.1265417e-8,0.12372674e-6,-0.113469002e-3,0},Polynomial.order.ConstLast);
  private static MathVector elpNatural(double MoonLon,double MoonLat,double MoonRad) {
    double Xe = MoonRad*Math.cos(MoonLon)*Math.cos(MoonLat);
    double Ye = MoonRad*Math.sin(MoonLon)*Math.cos(MoonLat);
    double Ze = MoonRad*Math.sin(MoonLat);
    return new MathVector(Xe,Ye,Ze);
  }
  class elpNaturalToFK5 extends RotatorEphemeris {
    public elpNaturalToFK5() {
      super(ELP2000Natural,J2000Ecl);
    }
    public Rotator CalcRotation(Time TT) {
      double T=TT.get(TimeUnits.Centuries,TimeScale.TDB,TimeEpoch.J2000);
      
      double P = PPoly.eval(T);
      double P2=P*P;
      double Q = QPoly.eval(T);
      double Q2=Q*Q;
      MathMatrix RotM = new MathMatrix(new double[][]{
          {1-2*P2,                  2*P*Q,                  2*P*Math.sqrt(1-P2-Q2)},
          {  2*P*Q,                 1-2*Q2,                -2*Q*Math.sqrt(1-P2-Q2)},
          { -2*P*Math.sqrt(1-P2-Q2),2*Q*Math.sqrt(1-P2-Q2), 1-2*P2-2*Q2}
        });
      return RotM;
    }
    
  }
  public static void main(String[] args) throws IOException,ClassNotFoundException {
    satArray[1].testHorizons("Data/EarthSatELP2000/EarthSatHorizons.FK5.csv",0,1000);    
  }
  public static final double[] satMassRatio={
    81.300587,
    1
  }; //mass ratio
  public static final Ephemeris[] satArray={
    null,
    new EarthSatELP2000()
  };
  public EarthSatELP2000() {
    Load();
    TheoryName="ELP2000-82";
    CenterName="Earth";
    CenterNumber=399;
    TargetName="Moon";
    TargetNumber=301;
    NaturalToFK5=new elpNaturalToFK5();    
  }
}
