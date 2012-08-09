package org.kwansystems.space.planet.atmosphere;

import static java.lang.Math.*;
import java.io.*;
import java.text.*;

import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.table.*;
import org.kwansystems.tools.vector.*;

/*Determines US Standard Upper Atmosphere 1976 tables. 
  Original Pascal version by Steven S. Pietrobon, 30 Dec 1999.
  Translated to Java, broken off of lower atmosphere by Chris Jeppesen, 2005*/
public class USSA1976Upper {

  public final int N2=0;
  public final int O =1;
  public final int O2=2;
  public final int Ar=3;
  public final int He=4;
  public final int H =5;
  public final int numGases=6;
  public ChartRecorder C;

  //Fundamental physical constants
  /**Boltzmann constant, Nm/K */
  private final double k_ = 1.380622e-23; 
  /**Avogadro constant, 1/kmol */
  private final double Na =  6.022169e26;
  /**Ideal gas constant, Nm/(K kmol) */
  private final double Rs =        k_*Na;
  //Sea level constants
  /**mean molecular weight of air at sea level, kg/kmol*/
  private final double M0 =      28.9644;
  /**acceleration of gravity at 45.5425 deg lat, m/s^2 */
  private final double g0 =      9.80665;
  /**Earth radius at g0, m*/
  private final double r0 =     6356766.; 
  /**Air pressure at sea level, Pa*/
  public final double P0 =      101325.;
  /**Standard sea-level temperature, K */
  public final double T0 =       288.15; 
  /**Ice point temperature, K */
  public final double Td =       273.15; 
  /** molecular weight, kg/kmol*///N2     O        O2    Ar      He      H
  public final double[] M_ = {28.0134,15.9994,31.9988,39.948,4.0026,1.00797};

  //Upper atmosphere constants
  /**Minimum geometric height, m*/
  private final double Z7  =    86000; 
  /**Change of N2 layer, m*/
  private final double Zm  =   100000; 
  /**End of eddy layer, m*/
  private final double Zk  =   115000; 
  /**Start of H layer, m*/
  private final double Zh  =   150000;
  /**End of itegration for H layer, m*/
  private final double Z11 =   500000; 
  /**Maximum geometric height, m*/
  private final double Z12 =  1000000; 
  /**Temperature at Z7, K*/
  private final double T7  = 186.8673; 
  /**Kinetic temperature at Z11, K*/
  private final double T11 = 999.2356; 
  /**number density at 86km*/    //N2       O         O2        Ar        He      H(500km)
  private final double[] n_ = {1.129794e20,8.6e16,3.030898e19,1.3514e18,7.58173e14,8.0e10};
  /**thermal-diffusion coefficient, dimensionless*///N2  O   O2  Ar  He    H
  private final double[] alpha =                    {0.0,0.0,0.0,0.0,-0.40,-0.25}; 
  /**vertical flux of H, 1/(m^2*s)*/
  private final double phi =      7.2e11;
  /**tau integral*/
  private final double taui = 0.40463343;
  /**H integral, 1/m^3 */
  private final double Hi = 9.8776951e10;
  /**integration interval, m*/
  private final int    Zint =         10; 
  /**number of integral regions*/
  private final int    reg =           2; 
  /**a small number*/
  private final double epsilon =     0.1;

  private double tauold,taunew,tauint;
  private PrintWriter table1,table3,table4;
  private double rho0,Zinc;

  public double sqr(double A) {
    return A*A;
  }
  
  public double ln(double A) {
    return log(A);
  }
  
  public double gravity(double Z) {
  /*Input:  Z        geometric height, m
  Output:  gravity  acceleration of gravity, m/s^2*/
    return g0*sqr(r0/(r0+Z));
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
    final double sigma = 3.65e-10;        //m, effective collision diameter
    double Hp,n,V,L,M;
    write_real(table1,Z,7);               //geometric height, m
    table1.print(",");
    write_real(table1,H,7);               //geopotential, m'
    table1.print(",");
    write_real(table1,T,8,2);             //kinetic temperature, K
    table1.print(",");
    write_real(table1,Tm,8,2);            //molecular-scale temperature, K
    table1.print(",");
    write_num(table1,P,1);                //pressure, Pa
    table1.print(",");
    write_num(table1,rho,2);              //mass density, kg/m^3
    table1.print(",");

    table1.print(" "+format_real(g,6,4)); //acceleration due to gravity, m/s^2
    Hp = (Rs/M0)*(Tm/g);
    table1.print(",");
    table1.print("  "+format_real(Hp,6)+" ");   //pressure scale height, m
    table1.print(",");
    n = (Na/Rs)*(P/T);
    write_num(table1,n,2);                //number density, 1/m^3
    V = Math.sqrt((8*Rs/(Math.PI*M0))*Tm);
    write_num(table1,V,1);                //mean air-particle speed, m/s
    L = 1.0/((sqrt(2)*Math.PI*sqr(sigma))*n);
    write_num(table1,V/L,2);              //collision frequency, 1/s
    write_num(table1,L,1);                //mean free path, m
    M = T*M0/Tm;
    table1.print("  "+format_real(M,6,3));             //mean molecular weight, kg/kmol
    table1.println("");
  }

  private double eddy(double Z) {
    //Pure analytical function
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
    //Pure analytical function
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
    double B,b_;
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
    //Pure analytical function
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
    //Pure analytical function
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
    D = a[gas]*exp(b[gas]*log(T/Td))/sumn;
    return new double[] {M,D};
  }

  private double fcalc(double Z,double g,double K,double T,double dT_dZ,int gas,double[] n) {
    //Requires numerical integration for gas=H! Only takes one step, but keeps state 
    //in taunew and integration total in taunew. Step size is unsynchronized here: 
    //Constant Zint is the multiplier in the trapezoidal rule, but the actual gap 
    //depends on what Z is used this time and the last time this function was called.
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
            return  phi*exp((1+alpha[H])*log(T/T11) + tauint - taui)/D;
          } else {
            return 0;             
          }          
        }
      default: return 0;
    }
  }

  private void write_data(double Z,double T,double g,double[] n) {
    //Pure analytical function in Z
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
    for(gas=N2;gas<=H;gas++) {
      table4.print(",");
      write_num(table4,n[gas],2);
    }
    table4.println("");
    write_tables(Z,H_,T,Tm,P,rho,g);
  }

  public void upper_atmosphere() throws IOException {
    double[] n=new double[numGases],fint=new double[numGases];
    int gas;
    double[] Z=new double[reg+1],T=new double[reg+1],dT_dZ=new double[reg+1],g=new double[reg+1],K=new double[reg+1];
    double[][] f=new double[reg+1][numGases];
    int I;


    table4=new PrintWriter("table4JUSA.csv");
    table4.println(" Z(m) H(m')  N2      O         O2        Ar       He         H");
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
        //f[I][H] is a function of numerical integration in  in fcalc
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
            n[gas] = n_[gas]*T7*exp(-fint[gas])/T[reg];
            break;
          case H:
            if(Z[reg] < Zh-epsilon) {
              n[H] = 0;
            } else {
              n[H] = (n_[H]+Hi-fint[H])*exp((1+alpha[H])*log(T11/T[reg]) + taui - tauint);
            }
        }
      }
      if ((round(Z[reg])%round(Zinc))==0) {
        write_data(Z[reg],T[reg],g[reg],n);
        C.Record(Z[reg]/1000.0,"tauint",null,tauint);
        for(gas=N2;gas<=He;gas++)C.Record(Z[reg]/1000.0,"fint["+gas+"]",null,fint[gas]);
        C.Record(Z[reg]/1000.0,"fint[5]",null,fint[5]/Hi);
      }
    } 
    table4.close();
  }

  public static void main(String[] args)  throws IOException {
    USSA1976Upper A=new USSA1976Upper();
    A.main2();
  }
  public void fitModel() {
    System.out.println("tauint range:  "+C.columnMin("tauint")+"-"+C.columnMax("tauint"));
    for(int i=0;i<6;i++) {
      String fint="fint["+i+"]";
      System.out.println(fint+" range: "+C.columnMin(fint)+"-"+C.columnMax(fint));
    }
    C.PrintSubTable(new String[] {"tauint","fint[0]","fint[1]","fint[2]","fint[3]","fint[4]","fint[5]"},new CSVPrinter("Int.csv"));
    table1.close();
    double[] Z=C.getT();
    double[] Y0=C.getDoubleColumn("fint[0]");
    double[] Y1=C.getDoubleColumn("fint[1]");
    double[] Y2=C.getDoubleColumn("fint[2]");
    double[] Y3=C.getDoubleColumn("fint[3]");
    double[] Y4=C.getDoubleColumn("fint[4]");
    double[] Y5=C.getDoubleColumn("fint[5]");
    double[] YT=C.getDoubleColumn("tauint");
    NonlinearCurveFit CF1=new NonlinearCurveFit(new MathVector(4)) {
      public double Evaluate(double x,MathVector coeff) {
        double a=coeff.get(0);
        double b=coeff.get(1);
        double c=coeff.get(2);
        double d=coeff.get(3);
        double e=-a;
        return a*exp(-(x-86)/b)+c*pow(x-86,2)+d*(x-86)+e;
      }
    };
    NonlinearCurveFit CF2=new NonlinearCurveFit(new MathVector(6)) {
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
    NonlinearCurveFit CF5=new NonlinearCurveFit(new MathVector(4)) {
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
    CF1.setGuess(new MathVector(new double[]{-5,20,0,0}));
    MathVector coeffFit0=CF1.Fit(Z,Y0,4);
    System.out.println("MathVector coeffFit0=new MathVector(new double[]{\n  "+coeffFit0+"\n});");
    CF2.setGuess(new MathVector(new double[]{1,40,1,100,0,0}));
    MathVector coeffFit1=CF2.Fit(Z,Y1,7);
    System.out.println("MathVector coeffFit1=new MathVector(new double[]{\n  "+coeffFit1+"\n});");
    CF1.setGuess(new MathVector(new double[]{-5,20,0,0}));
    MathVector coeffFit2=CF1.Fit(Z,Y2,4);
    System.out.println("MathVector coeffFit2=new MathVector(new double[]{\n  "+coeffFit2+"\n});");
    CF1.setGuess(new MathVector(new double[]{-5,20,0,0}));
    MathVector coeffFit3=CF1.Fit(Z,Y3,4);
    System.out.println("MathVector coeffFit3=new MathVector(new double[]{\n  "+coeffFit3+"\n});");
    CF2.setGuess(new MathVector(new double[]{-5,10,3,10.5,0,0.01}));
    MathVector coeffFit4=CF2.Fit(Z,Y4,7);
    System.out.println("MathVector coeffFit4=new MathVector(new double[]{\n  "+coeffFit4+"\n});");
    CF5.setGuess(new MathVector(new double[]{-5,20,0,0}));
    MathVector coeffFit5=CF5.Fit(Z,Y5,7);
    System.out.println("MathVector coeffFit5=new MathVector(new double[]{\n  "+coeffFit5+"\n});");
    CF1.setGuess(new MathVector(new double[]{-5,20,0,0}));
    MathVector coeffFitT=CF1.Fit(Z,YT,4);
    System.out.println("MathVector coeffFitT=new MathVector(new double[]{\n  "+coeffFitT+"\n});");
    ChartRecorder C2=new ArrayListChartRecorder(Z.length,4,"Z");
    for(int i=0;i<Z.length;i++) {
      C2.Record(Z[i], "fint[0].data",  null, Y0[i]);
      C2.Record(Z[i], "fint[0].model", null, CF1.Evaluate(Z[i],coeffFit0));
      C2.Record(Z[i], "fint[1].data",  null, Y1[i]);
      C2.Record(Z[i], "fint[1].model", null, CF2.Evaluate(Z[i],coeffFit1));
      C2.Record(Z[i], "fint[2].data",  null, Y2[i]);
      C2.Record(Z[i], "fint[2].model", null, CF1.Evaluate(Z[i],coeffFit2));
      C2.Record(Z[i], "fint[3].data",  null, Y3[i]);
      C2.Record(Z[i], "fint[3].model", null, CF1.Evaluate(Z[i],coeffFit3));
      C2.Record(Z[i], "fint[4].data",  null, Y4[i]);
      C2.Record(Z[i], "fint[4].model", null, CF2.Evaluate(Z[i],coeffFit4));
      C2.Record(Z[i], "fint[5].data",  null, Y5[i]);
      C2.Record(Z[i], "fint[5].model", null, CF5.Evaluate(Z[i],coeffFit5));
      C2.Record(Z[i], "tauint.data",  null, YT[i]);
      C2.Record(Z[i], "tauint.model", null, CF1.Evaluate(Z[i],coeffFitT));
    }
    C2.PrintSubTable(new String[] {
      "fint[0].data","fint[0].model",
      "fint[1].data","fint[1].model",
      "fint[2].data","fint[2].model",
      "fint[3].data","fint[3].model",
      "fint[4].data","fint[4].model",
      "fint[5].data","fint[5].model",
      "tauint.data","tauint.model",
    },new DisplayPrinter());
  }
  public void main2() throws IOException {
    C=new ArrayListChartRecorder(10000, 10,"Altitude");
    table1=new PrintWriter("table1.dat");
    table1.println("\"Altitude Z(m)\",\"Geopotential H(m')\",\"Temperature(K)\",\"Molecular Temperature(K')\",\"Pressure(Pa)\",\"Density(rho)\","+
                   "\"Grav(m/s^2)\",\"PresScaleHeight(m)\",\"NumDens(1/m^3)\",\"ParticleSpd(m/s)\",\"CollisionFreq(Hz)\",\"MeanFreePath(m)\",\"MolWt(kg/kmol)\"");

    System.out.println("");
    System.out.println("U.S. Standard Atmosphere, 1976");
    System.out.print("Unofficial implementation by Steven S. Pietrobon ");
    System.out.println("<steven@sworld.com.au>");
    System.out.print("30 December 1999      ");
    System.out.println("http://www.sworld.com.au/steven/space/atmosphere/");
    System.out.println("");
    System.out.print("Enter altitude increment in metres");
    Zinc=100;
    System.out.println(Zinc);
    System.out.println("Output:");
    System.out.println("TABLE1.DAT - Z, H, T, t, T_M, P, P/P_0, rho, rho/rho_0");
    System.out.println("TABLE2.DAT - Z, H, g, H_P, n, V, nu, L, M");
    System.out.println("TABLE3.DAT - Z, H, C_s, mu, eta, k_t (lower atmosphere only)");
    System.out.println("TABLE4.DAT - Z, H, n of N2, O, O2, Ar, He, H (upper atmosphere only)");

    System.out.println("Doing upper atmosphere");
    upper_atmosphere();
    C.EndOfData();
    fitModel();
  }
}
