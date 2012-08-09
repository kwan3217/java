package org.kwansystems.space.planet.atmosphere;

import static java.lang.Math.*;

import org.kwansystems.tools.table.*;
import org.kwansystems.tools.vector.*;

import static org.kwansystems.space.planet.atmosphere.EarthAirConstants.*;

/**
 *Determines Kwan Standard Upper Atmosphere 1976 tables. 
 *Kwan Standard Upper Atmosphere is meant to match the USSA1976 model
 *above 86km, but uses an exponential-quadratic model instead of 
 *numerical integrations for the variables fint and tauint.
 *Translated to Java, broken off of lower atmosphere by Chris Jeppesen, 2005
 */
public class KwanUpperAtmosphere extends Atmosphere {
  public static final int N2=0;
  public static final int O =1;
  public static final int O2=2;
  public static final int Ar=3;
  public static final int He=4;
  public static final int H =5;
  public static final int numGases=6;

  private static final String[] GasNames={ "N2",  "O",     "O2", "Ar",   "He",   "H"};
  /** molecular weight, kg/kmol*/         //N2     O        O2    Ar      He      H
  private static final double[] Mgas = {28.0134,15.9994,31.9988,39.948,4.0026,1.00797};

  //Upper atmosphere constants
  /**Top of temperature layer A, m*/
  private static final double Z8  =    91000; 
  /**Top of temperature layer B, m*/
  private static final double Z9  =   110000;
  /**Top of temperature layer C, m*/
  private static final double Z10 =   120000;
  /**Maximum geometric height, m*/
  private static final double Z12 =  1000000; 
  /**Start of H layer, m*/
  private static final double Zh  =   150000;
  /**Temperature at Z7,  K*/
  private static final double T7  = 186.8673; 
  /**Temperature at Z9,  K*/
  private static final double T9  =     240.0;
  /**Temperature at Z10, K*/
  private static final double T10 =     360.0;
  /**Kinetic temperature at Z11, K*/
  private static final double T11 = 999.2356; 
  private static final double Tinf =   1000.0; //K, defined temperature
  /**number density at 86km, 1/m^3*///N2       O         O2        Ar        He      H(500km)
  private static final double[] N0 = {1.129794e20,8.6e16,3.030898e19,1.3514e18,7.58173e14,8.0e10};
  /**thermal-diffusion coefficient, dimensionless*///N2  O   O2  Ar  He    H
  private static final double[] alpha =            {0.0,0.0,0.0,0.0,-0.40,-0.25}; 
  /**tau integral*/
  private static final double taui = 0.40463343;
  /**H integral, 1/m^3 */
  private static final double Hi = 9.8776951e10;
  private static final double Tc  =  263.1905; //K
  private static final double T8Slope =  -76.3232; // K/f(m)
  private static final double T8ScaleHeight  =   19942.9; //m
  private static final double Lk9 =     0.012; //K/m, kinetic temperature gradient
  private static final double lambda = 1.875e-5; //1/m

  /**
   *Integration approximations
   */
  private static final MathVector coeffFit0=new MathVector(new double[]{
    -5.600483682147745,29.59036581653099,-4.408061926298018E-6,0.03226986245954074
  });
  private static final MathVector coeffFit1=new MathVector(new double[]{
    -2.032008502653108,39.922873579438814,2.4442511869954244,102.90051279135236,-2.502677354745075E-6,0.018399548299667716
  });
  private static final MathVector coeffFit2=new MathVector(new double[]{
    -6.011977708816795,20.257738786330027,-6.86917460238112E-6,0.03894988274997198
  });
  private static final MathVector coeffFit3=new MathVector(new double[]{
    -6.370885936793021,20.968415231363657,-8.56819930636239E-6,0.04863479301960152
  });
  private static final MathVector coeffFit4=new MathVector(new double[]{
    -4.674949556894024,4.910481317706189,2.5847574108535585,14.426977713030332,-2.603693137350917E-7,0.0041826081761595435
  });
  private static final MathVector coeffFit5=new MathVector(new double[]{
    -0.8379484327456073,18.243157250631974,-6.018655776194384E-7,6.618682286344792E-4
  });
  private static final MathVector coeffFitT=new MathVector(new double[]{
    0.05614874050834712,17.1450034393195,-1.7986894327299037E-7,0.0011867822842692101
  });
  private static final NonlinearCurveFit CF1=new NonlinearCurveFit(new MathVector(4)) {
    public double Evaluate(double x,MathVector coeff) {
      double a=coeff.get(0);
      double b=coeff.get(1);
      double c=coeff.get(2);
      double d=coeff.get(3);
      double e=-a;
      return a*exp(-(x-86)/b)+c*pow(x-86,2)+d*(x-86)+e;
    }
  };
  private static final NonlinearCurveFit CF2=new NonlinearCurveFit(new MathVector(6)) {
    public double Evaluate(double x,MathVector coeff) {
      double a=coeff.get(0);
      double b=coeff.get(1);
      double c=coeff.get(2);
      double d=coeff.get(2);
      double e=coeff.get(4);
      double f=coeff.get(5);
      double g=-a-c;
      return a*exp(-(x-86)/b)+c*exp(-(x-86)/d)+e*pow(x-86,2)+f*(x-86)+g;
    }
  };
  private static final NonlinearCurveFit CF5=new NonlinearCurveFit(new MathVector(4)) {
    public double Evaluate(double x,MathVector coeff) {
      if(x<150) return 0;
      double a=coeff.get(0);
      double b=coeff.get(1);
      double c=coeff.get(2);
      double d=coeff.get(3);
      double e=-a;
      return a*exp(-(x-150)/b)+c*pow(x-150,2)+d*(x-150)+e;
    }
  };
  private static final MathVector[] coeffFit=new MathVector[] {
    coeffFit0,coeffFit1,coeffFit2,coeffFit3,coeffFit4,coeffFit5,coeffFitT
  };
  private static final CurveFit[] CF=new CurveFit[] {CF1,CF2,CF1,CF1,CF2,CF5,CF1};
  private double getIntegral(double Z, int which) {
    double result=CF[which].Evaluate(Z/1000.0, coeffFit[which]);
    if(which==5) result*=Hi;
    return result;
  }

  /**
   * @param Z Geometric altitude, m
   * @return {T,dT_dZ} where T is kinetic temperature, K, and 
   * dT_dZ is the kinetic temperature gradient, K/m
   */
  private double[] temperature(double Z) {
    double T,dT_dZ;
    double B,b;
    if (Z < Z8) {  //Temperature layer A (86-91km)
      T = T7;
      dT_dZ = 0.0;
    } else if (Z < Z9) {  //Temperature layer B (91-110km)
      b = (Z-Z8)/T8ScaleHeight;
      B = sqrt(1-b*b);
      T = Tc + T8Slope*B;
      dT_dZ = -T8Slope*b/(T8ScaleHeight*B);
    } else if (Z < Z10) { //Temperature layer C (110-120km)
      T = T9 + Lk9*(Z-Z9);
      dT_dZ = Lk9;
    } else {                      //Temperature layer D (120- km)
      b = (r0+Z10)/(r0+Z);
      B = (Tinf-T10)*exp(-lambda*(Z-Z10)*b);
      T = Tinf - B;
      dT_dZ = lambda*b*b*B;
    }
    return new double[] {T,dT_dZ};
  }

 
  public AirProperties calcProps(double Z) {
    double[] n=new double[numGases];
    double fint;
    int gas;
    double T,g;
    double ns,nms,P,rho,Tm;

    g = gravity(Z);  
    double[] result=temperature(Z);
    T=result[0];        
    ns = 0;
    nms = 0;
    for(gas = N2;gas<=H;gas++) { 
      fint = getIntegral(Z,gas);
      switch(gas) {
        case N2:
        case O:
        case O2:
        case Ar:
        case He: 
          //n_[gas]=n of gas at standart alt (const)
          n[gas] = N0[gas]*T7*exp(-fint)/T;
          break;
        case H:
          if(Z < Zh) {
            n[H] = 0;
          } else {
            double tauint=getIntegral(Z, 6);
            n[H] = (N0[H]+Hi-fint)*exp((1+alpha[H])*log(T11/T) + taui - tauint);
          }
      }
      ns = ns + n[gas];
      nms = nms + n[gas]*Mgas[gas];
    }
    Tm = T*M0*ns/nms;
    P = ns*k*T;
    rho = nms/Na;
    AirProperties A=new AirProperties();
    double Hp,V,L,M;
    A.Altitude=Z;
    A.Geopotential=geopotential(Z);
    A.Temperature=T;
    A.MolTemp=Tm;
    A.Pressure=P;
    A.Density=rho;
    A.Gravity=g;
    Hp = (Rs/M0)*(Tm/g);
    A.PScaleHeight=Hp;
    A.NumberDensity=(Na/Rs)*(P/T);
    V = Math.sqrt((8*Rs/(Math.PI*M0))*Tm);
    A.MolVel=V;
    L = 1.0/((sqrt(2)*Math.PI*sigma*sigma)*A.NumberDensity);
    A.ColFreq=V/L;
    A.MeanFreePath=L;
    M = T*M0/Tm;
    A.MolWt=M;
    A.SpecHeatRatio=gamma;
    //Note that this does not set Vsound and other related "table 3" properties.
    //As far as this is concerned, sound does not travel through the upper atm.
    A.GasNumberDensity=n;
    A.GasMolWeight=Mgas;
    A.GasName=GasNames;
    return A;
  }
  public KwanUpperAtmosphere() {
    super();
    Zlimit=Z12;
  }
}
