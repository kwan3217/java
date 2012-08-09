package org.kwansystems.space.planet.atmosphere;
import org.kwansystems.tools.table.*;

import static java.lang.Math.*;
import static org.kwansystems.space.planet.atmosphere.EarthAirConstants.*;
/*
 * Implementation of the lower 86km section of the US Standard Atmosphere
 * of 1976. This should match USSA1976 exactly in every detail, to within
 * a couple of ulps.
 */
public class KwanLowerAtmosphere extends Atmosphere {
  public KwanLowerAtmosphere() {
    Zlimit=Zmax;
  }

  /** Number of geopotential reference levels */
  private static final int bmax = 7;
  /** Maximum geometric altitude of lower atmosphere model, m*/
  private static final double Zmax=86000;
  /** Geopotential of reference levels, m' */
  private static final double[] H_ = {      0,11000, 20000, 32000,47000,  51000,  71000,geopotential(Zmax)}; 
  /** Lapse rates of reference layeres, K/m' */
  private static final double[] Lm = {-0.0065,  0.0,0.0010,0.0028,  0.0,-0.0028,-0.0020,  0.0}; 
  /** Hydrostatic constant. Surface gravity * molecular weight / Gas Constant. Constant across lower atmosphere */
  private static final double As = EarthAirConstants.g0*M0/Rs;
  /** Pressure at reference levels, Pa */
  private static final double[] P_=new double[bmax+1];
  /** Temperature at reference levels, K */
  private static final double[] T_=new double[bmax+1];
  
  private static final String[] GasNames={ "N2",  "O2",   "Ar",   "CO2",   "Ne",    "He",    "Kr",  "Xe",   "CH4",  "H2","O3"};
  /** molecular weight, kg/kmol*/        
  private static final double[] Mgas = {28.0134, 31.9988, 39.948,44.00995,20.183  , 4.0026,  83.80,131.30,16.04303,2.01594,47.998};
  /** Fractional volume composition, unitless. Assumed constant over range of lower atmosphere */
  private static final double[] Fgas = {0.78084,0.209476,9.34e-3,314e-6,  18.18e-6,5.24e-6,1.14e-6, 87e-9,    2e-6,500e-9,Double.NaN}; //Ozone is calculated separately
  
  
  /**
   * Table of Ozone number density (molecule/m^3) vs geometric altitude (m) 
   */
  QuadraticTable OzoneNumberDensity=new QuadraticTable(0,86000,new double[][] {{
	6.80e+17, //    0
	6.80e+17,
	5.80e+17,
	5.70e+17,
	6.50e+17,
	
	1.13e+18, //10000
	2.02e+18,
	2.35e+18,
	2.95e+18,
	4.04e+18,
	
	4.77e+18, //20000
	4.86e+18,
	4.54e+18,
	4.03e+18,
	3.24e+18,
	
	2.52e+18, //30000
	2.03e+18,
	1.58e+18,
	1.22e+18,
	8.73e+17,
	
	6.07e+17, //40000
	3.98e+17,
	2.74e+17,
	1.69e+17,
	1.03e+17,
	
	6.64e+16, //50000
	3.84e+16,
	2.55e+16,
	1.61e+16,
	1.12e+16,
	
	7.33e+15, //60000
	4.81e+15,
	3.17e+15,
	1.72e+15,
	7.50e+14,
	
	5.40e+14, //70000
	2.20e+14,
	1.70e+14,
	0.00e+14,
	0.00e+14,
	
	0.00e+14, //80000
	0.00e+14,
	0.00e+14,
	0.00e+14
  }});
  /** 
   * Calculates temperature at given geopotential in a layer with a 
   * nonzero temperature lapse rate.
   * @param T0 Temperature at base of layer, K
   * @param H0 Geopotential of base of layer, m'
   * @param L Lapse rate, K/m'
   * @param H Geopotential at altitude at which to calculate temperature, m'
   * @return Temperature at H, K
   */
  private static double TLapse(double T0, double H0, double L, double H) {
    return T0+L*(H-H0);
  }
  /**
   * Calculates total pressure at given geopotential in a layer with a 
   * zero temperature lapse rate
   * @param P0 Pressure at base of layer, Pa
   * @param H0 Geopotential of base of layer, m'
   * @param T0 Temperature across layer, K
   * @param H  Geopotential at altitude at which to calculate pressure, m'
   * @return Pressure at H, Pa
   */
  private static double PNoLapse(double P0,double H0,double T0, double H) {
    return P0*exp(-As*(H-H0)/T0);
  }
  
  /**
   * Calculates pressure at given temperature in a layer with a 
   * nonzero temperature lapse rate. How bizarre! Pressure is a function
   * of altitude, but in this formula it is a function of temp, which 
   * makes it an indirect function of alt.
   * @param P0 Pressure at base of layer, Pa
   * @param T0 Temperature of base of layer, K
   * @param L Lapse rate, K/m'
   * @param T  Temperature at altitude at which to calculate pressure, K
   * @return Pressure at T, Pa
   */
  private static double PLapse(double P0,double T0,double L, double T) {
    return P0*exp(As*log(T0/T)/L);
  }

  /**
   * Calculate base temperatures and pressures, given lapse rates and
   * temperature and pressure at Z=0
   */
  static {
    T_[0] = T0;
    P_[0] = P0;
    for(int b=0;b<bmax;b++) {
      //Calculate temperature at base of next layer, by linear extrapolation
      T_[b+1] = TLapse(T_[b],H_[b],Lm[b],H_[b+1]); 
      if (Lm[b]== 0.0) { //If there is no temp gradient in this layer
        P_[b+1]=PNoLapse(P_[b],H_[b],T_[b],H_[b+1]);
      } else {           //If there is
        P_[b+1] = PLapse(P_[b],T_[b],Lm[b],T_[b+1]);
      }
    }
  }
  
  /**
   * mean molecular weight M from 80 to 86 km
   * @param Z Altitude to calculate at, m
   * @return Molecular weight at Z, kg/kmol
   */
  private double mol(double Z) {
    final double[] f= {1.000000,0.999996,0.999989,0.999971,0.999941,0.999909,
                       0.999870,0.999829,0.999786,0.999741,0.999694,0.999641,0.999579};
    final double Zinc = 500; //m, incremental height
    final double Zm = 80000; //m, initial altitude
    int I;
    double Zi;
    if (Z < Zm) {
      return M0;
    } else {
      Zi = (Z-Zm)/Zinc;
      I = (int)floor(Zi);
      return M0*((f[I+1]-f[I])*(Zi-I) + f[I]);
    }
  }
  public AirProperties calcProps(double Z) {
    int b=0;
    double H,Tm,P,T,M,rho,g;
    double Hp,n,V,L,f;
    double Cs,mu,eta,kt;
    AirProperties A=new AirProperties();
    A.Altitude=Z;                           //Geometric altitude, m
    H = geopotential(Z);                    //Geopotential, m'
    A.Geopotential=H;
    for(int bi=1;bi<bmax;bi++) if(H>H_[bi]) b=bi;
    if(Z>Zlimit) {
      return A;
    }
      
    Tm = TLapse(T_[b],H_[b],Lm[b],H); 
    A.MolTemp=Tm;                           //Molecular temperature, K
    if(Lm[b]== 0.0) {
      P = PNoLapse(P_[b],H_[b],T_[b],H);   
    } else {
      P = PLapse(P_[b],T_[b],Lm[b],Tm);
    }
    A.Pressure=P;                           //Pressure, Pa=N/m^2
    M = mol(Z);                             //Molecular weight, kg/kmol
    A.MolWt=M;                              
    T = Tm*M/M0;                            //Temperature, K
    A.Temperature=T;
    rho = (P/Tm)*(M0/Rs);                   //Density, kg/m^3
    A.Density=rho;
    g = gravity(Z);                         //Acceleration of gravity, m/s^2
    A.Gravity=g;
    Hp = (Rs/M0)*(Tm/g);                    //pressure scale height, m
    A.PScaleHeight=Hp;
    n = (Na/Rs)*(P/T);                      //number density, 1/m^3
    A.NumberDensity=n;
    V = Math.sqrt((8*Rs/(PI*M0))*Tm);       //mean air-particle speed, m/s
    A.MolVel=V;
    L = 1.0/((sqrt(2)*PI*pow(sigma,2))*n);  //mean free path, m
    A.MeanFreePath=L;
    f=V/L;                                  //collision frequency, 1/s
    A.ColFreq=f;
    Cs = sqrt((gamma*Rs/M0)*Tm);            //speed of sound, m/s
    A.VSound=Cs;
    mu = beta*sqrt(T)/(1+S/T);              //dynamic viscosity, Ns/m^2
    A.Viscosity=mu;
    eta = mu/rho;                           //kinematic viscosity, m^2/s
    A.KinViscosity=eta;
    kt = 2.64638e-3*sqrt(T)/(1+245.4*exp(-12*log(10)/T)/T);   //coef. of thermal conductivity, W/(m K)
    A.ThermalCond=kt;
    A.SpecHeatRatio=gamma;
    A.GasNumberDensity=new double[GasNames.length];
    A.GasName=GasNames;
    A.GasMolWeight=Mgas;
    for(int i=0;i<GasNames.length;i++) {
      A.GasNumberDensity[i]=Fgas[i]*A.NumberDensity;
    }
    A.GasNumberDensity[GasNames.length-1]=OzoneNumberDensity.Interp(Z,0);
    if(A.GasNumberDensity[GasNames.length-1]<0)A.GasNumberDensity[GasNames.length-1]=0;
    return A;
  }
  public static void main(String[] args) {
    KwanLowerAtmosphere A=new KwanLowerAtmosphere();
    AirProperties AP=A.calcProps(0);
    System.out.println(AP);
    AP=A.calcProps(5334.0*0.3048); //Boulder (Folsom field level)
    System.out.println(AP);
    AP=A.calcProps(6885.0*0.3048); //Top of the Flatirons(?)
    System.out.println(AP);
    AP=A.calcProps(14200.0*0.3048); //Top of Mt Evans
    System.out.println(AP);
    AP=A.calcProps(29035.0*0.3048); //Top of Mt Everest
    System.out.println(AP);
    AP=A.calcProps(49112.48);       //"50km" height in calc_albedo model (2.4e23 /cm^2 coldens)
    System.out.println(AP);
    AP=A.calcProps(83000);          //PMC Cloud Deck
    System.out.println(AP);
  }
}
