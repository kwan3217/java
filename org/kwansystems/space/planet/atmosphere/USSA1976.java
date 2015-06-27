package org.kwansystems.space.planet.atmosphere;

import static java.lang.Math.*;
import java.io.*;
import java.text.*;

public class USSA1976 {
/*Determines U.S. Standard Atmosphere 1976 tables. 
Steven S. Pietrobon, 30 Dec 1999.*/

  public final int N2=0;
  public final int O =1;
  public final int O2=2;
  public final int Ar=3;
  public final int He=4;
  public final int H =5;
  public final int numGases=6;

  public final double k_ = 1.380622e-23; //Nm/K, Boltzmann constant
  public final double Na =  6.022169e26; //1/kmol, Avogadro constant
  public final double Rs =      8314.32; //Nm/(kmol K), gas constant
  public final double M0 =      28.9644; //kg/kmol, mean molecular weight of air
  public final double g0 =      9.80665; //m/s^2, acceleration of gravity at 45.5425 deg lat.
  public final double r0 =     6356766.; //m, Earth radius at g0
  public final double P0 =      101325.; //Pa, air pressure at g0
  public final double T0 =       288.15; //K, standard sea-level temperature
  public final double Td =       273.15; //K, 0 degrees C
  public final double[] M_ = {28.0134,15.9994,31.9988,39.948,4.0026,1.00797};  //molecular weight, kg/kmol
  public final int linespace =    10; //number of lines

  public PrintWriter H_,table1,table3,table4/*,table*/;
  public double rho0,Zinc;
  public int line;
  public char ans;

  public double sqr(double A) {
    return A*A;
  }
  
  public double ln(double A) {
    return Math.log(A);
  }
  
  public double gravity(double Z) {
  /*Input:  Z        geometric height, m
  Output:  gravity  acceleration of gravity, m/s^2*/
    return g0*sqr(r0/(r0+Z));
  }

  public int trunc(double A) {
    //Chops off decimals (rounds towards zero
    return ((int)(floor(abs(A))*signum(A)));
  }
  public NumberFormat NN1=new DecimalFormat("0.0000E0");
  public NumberFormat NN2=new DecimalFormat("0.0000E00");
  public void write_num(PrintWriter table, double num, int width) {
  //Writes reals in scientific form
    table.print(" "+(width>1?NN2:NN1).format(num));
  }
  
  public String format_real(double num, int width, int places) {
    String zeros="";
    for(int i=0;i<places;i++) zeros=zeros+"0";
    String format=new String(places>0?"0."+zeros:"0");
    NumberFormat N=new DecimalFormat(format);
    String sn=N.format(num);
    char[] spaces=new char[width-sn.length()];
    for(int i=0;i<spaces.length;i++) spaces[i]=' ';
    return new String(spaces)+sn;
  }
  
  public String format_real(double num, int width) {
    return format_real(num,width,0);
  }
  
  public void write_real(PrintWriter table,double num, int width, int places) {
    table.print(format_real(num,width,places));
  }

  public void write_real(PrintWriter table,double num, int width) {
    write_real(table,num,width,0);
  }

  public void write_tables(double Z,double H,double T,double Tm,double P,double rho,double g) {
    final double sigma = 3.65e-10;       //m, effective collision diameter
    double Hp,n,V,L,M;
    write_real(table1,Z,7);      //geometric height, m
    table1.print(",");
    write_real(table1,H,7);      //geopotential, m'
    table1.print(",");
    write_real(table1,T,8,2);             //kinetic temperature, K
    table1.print(",");
    write_real(table1,Tm,8,2);            //molecular-scale temperature, K
    table1.print(",");
    write_num(table1,P,1);                //pressure, Pa
    table1.print(",");
    write_num(table1,rho,2);              //mass density, kg/m^3
    table1.print(",");

    table1.print(" "+format_real(g,6,4));       //acceleration due to gravity, m/s^2
    Hp = (Rs/M0)*(Tm/g);
    table1.print(",");
    table1.print("  "+format_real(Hp,6)+" ");   //pressure scale height, m
    table1.print(",");
    n = (Na/Rs)*(P/T);
    write_num(table1,n,2);                //number density, 1/m^3}
    V = Math.sqrt((8*Rs/(Math.PI*M0))*Tm);
    write_num(table1,V,1);               //mean air-particle speed, m/s
    L = 1.0/((sqrt(2)*Math.PI*sqr(sigma))*n);
    write_num(table1,V/L,2);              //collision frequency, 1/s}
    write_num(table1,L,1);                //mean free path, m}
    M = T*M0/Tm;
    table1.print("  "+format_real(M,6,3));             //mean molecular weight, kg/kmol}
    table1.println("");
  }

  private double mol(double Z) {
    //mean molecular weight M from 80 to 86 km
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
      I = trunc(Zi);
      return M0*((f[I+1]-f[I])*(Zi-I) + f[I]);
    }
  }

  private void write_table3(double Z,double H,double T,double Tm,double rho) {
    final double gamma =     1.40; //Ratio of Specific heats for ideal diatomic gas
    final double beta  = 1.458e-6; //kg/(s m K^1/2)
    final double S     =    110.4; //Sutherland's constant
    double Cs,mu,eta,kt;
    table3.print(format_real(Z,7));      //geometric height, m
    table3.print(format_real(H,7));      //geopotential height, m'
    Cs = sqrt((gamma*Rs/M0)*Tm);
    table3.print(" "+format_real(Cs,6,2));      //speed of sound, m/s
    mu = beta*sqrt(T)/(1+S/T);
    write_num(table3,mu,1);        //dynamic viscosity, Ns/m^2
    eta = mu/rho;
    write_num(table3,eta,1);       //kinematic viscosity, m^2/s
    kt = 2.64638e-3*sqrt(T)/(1+245.4*exp(-12*ln(10)/T)/T);
    write_num(table3,kt,1);        //coef. of thermal conductivity, W/(m K)
    table3.println("");
    if(line==linespace-1) table3.println("");
  }

  public void lower_atmosphere() throws IOException {
    final int bmax = 7; //number of geopotential reference levels
    final double[] H_ = {0,11000,20000,32000,47000,51000,71000,84852};       //geopotential height of base of each layer, m'=geopotential meter
    final double[] Lm = {-0.0065,0.0,0.0010,0.0028,0.0,-0.0028,-0.0020,0.0}; //molecular-scale temperature gradiant, K/m'}
    final double Zs = -5000;                                                 //m, starting altitude
    double As,H,Z,P,Tm,T,M,rho,g;
    int b,Zo,Zi,Zt;
    double[] P_=new double[bmax+1]; //Pressure at base of each level
    double[] T_=new double[bmax+1]; //Temperature at base of each level

    table3=new PrintWriter("table3.dat");
    table3.println("\"Altitude(m)\",\"Geopotential(m')\",\"Vsound(m/s)\",\"DVisc(Ns/m^2)\",\"KVisc(m^2/s)\",\"TCond(W/(m*K))\"");
    T_[0] = T0;
    P_[0] = P0;
    As = g0*M0/Rs; //Hydroconstant Surface gravity * molecular weight / Gas Constant. Constant across lower atmosphere
    Zo = (int)round(Zs/Zinc);
    for(b=0;b<bmax;b++) {
      //Calculate temperature at base of next layer, by linear extrapolation
      T_[b+1] = T_[b] + Lm[b]*(H_[b+1]-H_[b]); 
      
      if (Lm[b]== 0.0) { //If there is no temp gradient in this layer
        P_[b+1] = P_[b]*exp(-As*(H_[b+1]-H_[b])/T_[b]);
      } else {           //If there is
        P_[b+1] = P_[b]*exp(As*ln(T_[b]/T_[b+1])/Lm[b]);
      }
    }
    for(b=0;b<bmax;b++) {
      Zt = trunc(r0*H_[b+1]/((r0-H_[b+1])*Zinc));
      for(Zi=Zo;Zi<=Zt;Zi++) {
        Z = Zi*Zinc;
        H = r0*Z/(r0+Z);
        Tm = T_[b] + Lm[b]*(H-H_[b]);
        if(Lm[b]== 0.0) {
          P = P_[b]*exp(-As*(H-H_[b])/T_[b]);
        } else {
          P = P_[b]*exp(As*ln(T_[b]/Tm)/Lm[b]);
        }
        M = mol(Z);
        T = Tm*M/M0;
        rho = (P/Tm)*(M0/Rs);
        g = gravity(Z);
        write_table3(Z,H,T,Tm,rho);
        write_tables(Z,H,T,Tm,P,rho,g);
      }
      Zo = Zt+1;
    }
    table3.close();
  }

  final double Z7  =    86000; //m, minimum geometric height
  final double Zm  =   100000; //m, change of N2 layer
  final double Zk  =   115000; //m, end of eddy layer
  final double Zh  =   150000; //m, start of H layer
  final double Z11 =   500000; //m, end of itegration for H layer
  final double Z12 =  1000000; //m, maximum geometric height
  final double T7  = 186.8673; //K, temperature at Z7
  final double T11 = 999.2356; //K, kinetic temperature at Z11
  final double[] n_ = {1.129794e20,8.6e16,3.030898e19,1.3514e18,7.58173e14,8.0e10}; //number density for N2..He at 86 km and H at 500 km, 1/m^3
  final double[] alpha = {0.0,0.0,0.0,0.0,-0.40,-0.25}; //thermal-diffusion coefficient, dimensionless
  final double phi =      7.2e11; //1/m^2 1/s, vertical flux of H
  final double taui = 0.40463343; //dimensionless, tau integral
  final double Hi = 9.8776951e10; //1/m^3, H integral
  final int     Zint =         10; //m, integration interval
  final int    reg =           2; //number of integral regions
  final double epsilon =     0.1; //a small number
  double tauold,taunew,tauint;

  private double eddy(double Z) {
    //Input: Z     geometric height, m
    //Output: eddy  eddy-diffusion coefficient, m^2/s
    final double K7  =  120.0; //m^2/s, eddy diffusion coefficient at 86 km
    final double K10 =    0.0; //m^2/s, eddy diffusion coefficient at 115 km
    final double Ze  =  95000; //m, geometric height
    final double A   = 4.00e8; //m^2
    if(Z < Ze) {
      return K7;
    } else if (Z < Zk-epsilon) {
      return K7*exp(1-A/(A-sqr(Z-Ze)));
    } else {
      return K10;
    }
  }

  private double[] temperature(double Z) {
    /*Input:  Z:double      geometric height, m
      Output: T:double      kinetic temperature, K
          dT_dZ:double  kinetic temperature gradiant, K/m */
    double T,dT_dZ;
    final double T9  =     240.0; //K, temperature at 110 km
    final double T10 =     360.0; //K, temperature at 120 km
    final double Tinf =   1000.0; //K, defined temperature
    final double Z8  =     91000; //m, geometric height
    final double Z9  =    110000;
    final double Z10 =    120000;
    final double Tc  =  263.1905; //K
    final double A   =  -76.3232; //K
    final double a_  =   19942.9; //m
    final double Lk9 =     0.012; //K/m, kinetic temperature gradient
    final double lambda = 1.875e-5; //1/m
    double B,b_,eta;
    if (Z < Z8-epsilon) {  //Temperature layer A (86-91km)
      T = T7;
      dT_dZ = 0.0;
    } else if (Z < Z9-epsilon) {  //Temperature layer B (91-110km)
      b_ = (Z-Z8)/a_;
      B = sqrt(1-sqr(b_));
      T = Tc + A*B;
      dT_dZ = -A*b_/(a_*B);
    } else if (Z < Z10-epsilon) { //Temperature layer C (110-120km)
      T = T9 + Lk9*(Z-Z9);
      dT_dZ = Lk9;
    } else {                      //Temperature layer D (120- km)
      b_ = (r0+Z10)/(r0+Z);
      B = (Tinf-T10)*exp(-lambda*(Z-Z10)*b_);
      T = Tinf - B;
      dT_dZ = lambda*sqr(b_)*B;
    }
    return new double[] {T,dT_dZ};
  }

  private double flux(double Z,int gas) {
    //Input:  Z     geometric height, m
    //        gas   current gas
    //Output: flux  nu_i/(D_i+K), 1/m
    final double qO = -3.416248e-12; //1/m^3
    final double uO = 97000;         //m
    final double wO = 5.008765e-13;  //1/m^3
    final double[] Q=new double[] {0,-5.809644e-13,1.366212e-13,9.434079e-14,-2.457369e-13}; //coefficient, 1/m^3
    final double[] U=new double[] {0,56903.11,86000,86000,86000};                            //coefficient, m
    final double[] W=new double[] {0,2.706240e-14,8.333333e-14,8.333333e-14,6.666667e-13};   //coefficient, 1/m^3
    double X;
    double A,a_;
    a_ = Z-U[gas];
    A = sqr(a_);
    X = Q[gas]*A*exp(-W[gas]*A*a_);
    if((Z < uO) & (gas == O)) {
      a_ = uO-Z;
      A = sqr(a_);
      X = X + qO*A*exp(-wO*A*a_);
    } 
    return X;
  }

  private double[] weight(double Z,double T,int gas,double[] n) {
    /*Input: Z    geometric height, m
        T    kinetic temperature, K
        gas  current gas
        n    number density, 1/m^3
Output: M    mean molecular weight, kg/kmol
        D    molecular-diffusion coefficient, m^2/s */
    double M,D;
    final double[] a = {0,6.986e20,4.863e20,4.487e20,1.700e21,3.305e21};  //coefficient, 1/m 1/s
    final double[] b = {0,0.750,0.750,0.870,0.691,0.500};                 //coefficient, dimensionless
    int maxgas=N2,igas;
    double sumn;
    switch(gas) {
      case O:
      case O2:
        maxgas = N2;
        break;
      case Ar:
      case He:
        maxgas = O2;
        break;
      case H:
        maxgas = He;
        break;
    }
    sumn = 0;
    for(igas=N2;igas<=maxgas;igas++) sumn = sumn + n[igas];
    if(Z < Zm-epsilon) {
      M = M0;
    } else {
      M = 0;
      for(igas=N2;igas<=maxgas;igas++) M = M + n[igas]*M_[igas];
      M = M/sumn;
    }
    D = a[gas]*exp(b[gas]*ln(T/Td))/sumn;
    return new double[] {M,D};
  }

  private double fcalc(double Z,double g,double K,double T,double dT_dZ,int gas,double[] n) {
    double M,D,f;
    switch(gas) {
      case N2: 
        if(Z < Zm-epsilon) {
          M = M0;
        } else {
          M = M_[N2];
        }
        return M*g/(Rs*T);
      case O:
      case O2:
      case Ar:
      case He:
        if(Z < Zk-epsilon) {
          double[] result=weight(Z,T,gas,n);
          M=result[0];
          D=result[1];
          f = D*(M_[gas]+M*K/D+alpha[gas]*Rs*dT_dZ/g)/(D+K);
        } else {
          f = M_[gas]+alpha[gas]*Rs*dT_dZ/g;
        }
        return g*f/(Rs*T) + flux(Z,gas);
      case H:
        if (Z < Zh-epsilon) {
          return 0;
        } else if (Z < Zh+epsilon) {
          taunew = (g/T)*(M_[H]/Rs);
          tauint = 0;
          return 0;
        } else {
          //Keep last value of tau
          tauold = taunew;
          taunew = (g/T)*(M_[H]/Rs);
          //Trapezoidal rule integration of tau
          tauint = tauint + Zint*(tauold+taunew)/(2*reg);
          if (Z < Z11-epsilon) {
            double[] result=weight(Z,T,gas,n);
            M=result[0];
            D=result[1];
            return  phi*exp((1+alpha[H])*ln(T/T11) + tauint - taui)/D;
          } else {
            return 0;             
          }          
        }
      default: return 0;
    }
  }

  private void write_data(double Z,double T,double g,double[] n) {
    int gas;
    double ns,nms,P,rho,H_,Tm;
    ns = 0;
    nms = 0;
    for(gas=N2;gas<=H;gas++) {
      ns = ns + n[gas];
      nms = nms + n[gas]*M_[gas];
    }
    H_ = r0*Z/(r0+Z);
    Tm = T*M0*ns/nms;
    P = ns*k_*T;
    rho = nms/Na;
    table4.print(format_real(Z,7));      //geometric height, m
    table4.print(format_real(H_,7));     //geopotential height, m'
    for(gas=N2;gas<=H;gas++) write_num(table4,n[gas],2);
    table4.println("");
    if(line == linespace-1) table4.println("");
    write_tables(Z,H_,T,Tm,P,rho,g);
  }

  public void upper_atmosphere () throws IOException {
    double[] n=new double[numGases],fint=new double[numGases];
    int gas;
    double[] Z=new double[reg+1],T=new double[reg+1],dT_dZ=new double[reg+1],g=new double[reg+1],K=new double[reg+1];
    double[][] f=new double[reg+1][numGases];
    int I;


    table4=new PrintWriter("table4.dat");
    table4.println("   Altitude   |                     Number density (1/m^3)");
    table4.println("-------+------+----------+----------+----------+----------+----------+----------");
    table4.println(" Z (m) | H(m')|    N2    |    O     |    O2    |    Ar    |   He     |    H");
    table4.println("-------+------+----------+----------+----------+----------+----------+----------");
    for(I = 1;I<=reg;I++) Z[I] = Z7 - (reg-I)*Zint/reg;
    g[reg] = gravity(Z7);
    K[reg] = eddy(Z7);
    T[reg] = T7;
    dT_dZ[reg] = 0;
    for(gas = N2;gas<=He;gas++) n[gas] = n_[gas];
    n[H] = 0;
    for(gas = N2;gas<=H;gas++) {
      f[reg][gas] = fcalc(Z[reg],g[reg],K[reg],T[reg],dT_dZ[reg],gas,n);
      fint[gas] = 0;
    }
    write_data(Z[reg],T[reg],g[reg],n);
    while(!(Z[reg]+epsilon > Z12)) {
      for(I = 1;I<=reg;I++) {
        Z[I] = Z[I] + Zint;    //Trivially integrated
        g[I] = gravity(Z[I]);  //Not integrated
        K[I] = eddy(Z[I]);     //Not integrated
        double[] result=temperature(Z[I]);
        T[I]=result[0];        //Not integrated
        dT_dZ[I]=result[1];    //Not integrated
      }
      f[0] = f[reg];
      for(gas = N2;gas<=H;gas++) { 
        for(I = 1;I<=reg;I++) f[I][gas] = fcalc(Z[I],g[I],K[I],T[I],dT_dZ[I],gas,n);
         //Simpson's rule integration of f[:][gas]
        fint[gas] = fint[gas] + Zint*(f[0][gas]+4*f[1][gas]+f[2][gas])/(reg*3);
        switch(gas) {
          case N2:
          case O:
          case O2:
          case Ar:
          case He: 
            //n_[gas]=n of gas at standart alt (const)
            //
            n[gas] = n_[gas]*T7*exp(-fint[gas])/T[reg];
            break;
          case H:
            if(Z[reg] < Zh-epsilon) {
              n[H] = 0;
            } else {
              n[H] = (n_[H]+Hi-fint[H])*exp((1+alpha[H])*ln(T11/T[reg]) + taui - tauint);
            }
        }
      }
      if ((round(Z[reg])%round(Zinc))==0) write_data(Z[reg],T[reg],g[reg],n);
    } 
    table4.close();
  }

  public static void main(String[] args)  throws IOException {
    USSA1976 A=new USSA1976();
    A.main2();
  }
  public void main2() throws IOException {
    table1=new PrintWriter("table1.dat");
    table1.println("\"Altitude Z(m)\",\"Geopotential H(m')\",\"Temperature(K)\",\"Molecular Temperature(K')\",\"Pressure(Pa)\",\"Density(rho)\","+
                   "\"Grav(m/s^2)\",\"PresScaleHeight(m)\",\"NumDens(1/m^3)\",\"ParticleSpd(m/s)\",\"CollisionFreq(Hz)\",\"MeanFreePath(m)\",\"MolWt(kg/kmol)\"");

    rho0 = P0*M0/(Rs*T0);
    line = 0;

    System.out.println("");
    System.out.println("U.S. Standard Atmosphere, 1976");
    System.out.print("Unofficial implementation by Steven S. Pietrobon ");
    System.out.println("<steven@sworld.com.au>");
    System.out.print("30 December 1999      ");
    System.out.println("http://www.sworld.com.au/steven/space/atmosphere/");
    System.out.println("");
    while(!((ans == 'L') | (ans == 'U') | (ans == 'B'))) {
      System.out.print("Lower: -5 to 86 km (L), Upper: 86 to 1000 km (U), or Both (B) ");
      System.out.print("atmospheres? ");
      ans='B';
      System.out.println(ans);
      switch(ans) {
        case 'l': ans = 'L'; break;
        case 'u': ans = 'U'; break;
        case 'b': ans = 'B'; break;
      }
    }
    System.out.print("Enter altitude increment in metres");
    if (ans == 'L') {
      System.out.print(": ");
    } else {
      System.out.print(" (must be multiple of 10 m): ");
    }
    Zinc=100;
    System.out.println(Zinc);
    System.out.println("Output:");
    System.out.println("TABLE1.DAT - Z, H, T, t, T_M, P, P/P_0, rho, rho/rho_0");
    System.out.println("TABLE2.DAT - Z, H, g, H_P, n, V, nu, L, M");
    System.out.println("TABLE3.DAT - Z, H, C_s, mu, eta, k_t (lower atmosphere only)");
    System.out.println("TABLE4.DAT - Z, H, n of N2, O, O2, Ar, He, H (upper atmosphere only)");

    System.out.println("Doing lower atmosphere");
    if ((ans == 'L') | (ans == 'B')) lower_atmosphere();
    System.out.println("Doing upper atmosphere");
    if ((ans == 'U') | (ans == 'B')) upper_atmosphere();

    table1.close();
  }
}
