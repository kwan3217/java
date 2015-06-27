package org.kwansystems.space.ephemeris;

import static org.kwansystems.space.Constants.*;
import static org.kwansystems.tools.time.Time.*;
import static java.lang.Math.*;

import org.kwansystems.tools.time.TimeEpoch;

public class Rotation {
  //J2000.0 Rotation Elements
  //Sets up an array of vectors
  
  //Based upon Bureau des Longitudes constants
  // http://www.bdl.fr/solarsys/projet/chap10/10_1_eng.html for planets
  // http://www.bdl.fr/solarsys/projet/chap10/10_2_eng.html for moons
  // Those were based on http://astrogeology.usgs.gov/Projects/WGCCRE/
  
  public static enum TermType {
    Const,Linear,Sin,Cos;
  };

  private static double RotEpoch=TimeEpoch.J2000.JDEpoch();

  // Long period terms. All rates are converted to degrees per day. Terms
  // have the same index here as they do in the documentation, which means
  // that most have no zero term.
  private static double[][] E=new double[][] {
    {},
    {125.0450, -  0.05299210*36525.0 },
    {250.0890, -  0.10598420*36525.0 },
    {260.0080, + 13.0120009 *36525.0 },
    {176.6250, + 13.3407154 *36525.0 },
    {357.5290, +  0.98560030*36525.0 },
    {311.5890, + 26.4057084 *36525.0 },
    {134.9630, + 13.0649930 *36525.0 },
    {276.6170, +  0.32871460*36525.0 },
    { 34.2260, +  1.74848770*36525.0 },
    { 15.1340, -  0.15897630*36525.0 },
    {119.7430, +  0.00360960*36525.0 },
    {239.9610, +  0.16435730*36525.0 },
    {25.0530 , + 12.9590088 *36525.0 }
  };
  private static double[][] M=new double[][] {
    {},
    {169.51,-0.4357640*36525.0},
    {192.93,+1128.4096700*36525.0,+8.864},
    {53.47 ,-0.0181510*36525.0}
  };
  private static double[][] J=new double[][] {
    {},
    { 73.32, + 91472.9},
    {  4.62, + 45137.2},
    {283.90, +  4850.7},
    {355.80, +  1191.3},
    {119.90, +   262.1},
    {229.80, +    64.3},
    {352.25, +  2382.6},
    {113.35, +  6070.0}
  };
  private static double[][] S=new double[][] {
    {},
    {353.32, + 75706.7},
    { 28.72, + 75706.7},
    {177.40, - 36505.5},
    {300.00, -  7225.9},
    {316.45, +   506.2},
    {345.20, -  1016.3},
    { 29.80, -    52.1},
  };
  private static double[][] U=new double[][] {
    {},
    {115.75, + 54991.87},
    {141.69, + 41887.66},
    {135.03, + 29927.35},
    { 61.77, + 25733.59},
    {249.32, + 24471.46},
    { 43.86, + 22278.41},
    { 77.66, + 20289.42},
    {157.36, + 16652.76},
    {101.81, + 12872.63},
    {138.64, +  8061.81},
    {102.23, -  2024.22},
    {316.41, +  2863.96},
    {304.01, -    51.94},
    {308.71, -    93.17},
    {340.82, -    75.32},
    {259.14, -   504.81}
  };
  private static double[][] N=new double[][] {
    {357.00,+52.316}, //This is correct, there is a term called N in the documentation
    {323.92,+62606.6},
    {220.51,+55064.2},
    {354.27,+46564.5},
    { 75.31,+26109.4},
    { 35.36,+14325.4},
    {142.61,+ 2824.6},
    {177.85,+   52.316}
  };
  private static double P(double[][] table, int i, double T) {
    double acc=table[i][table[i].length-1];
    for(int j=table[i].length-2;j>=0;j--) acc=acc*T+table[i][j];
    return toRadians(acc);
  }
  private static double[] RA0(double Time) {
    double D=Time-RotEpoch;
    double T=D/36525;
    double[] answer=new double[1000];
    //Sun
    answer[ 99]=(286.13);
    //Planets
    answer[199]=(281.01-0.033*T);
    answer[299]=(272.76);
    answer[399]=(0-0.641*T);
    answer[499]=(317.681-0.108*T);
    answer[599]=(268.05-0.009*T);
    answer[699]=(40.589-0.036*T);
    answer[799]=(257.311);
    answer[899]=(299.36+0.70*sin(P(N,0,T)));
    answer[999]=(313.02);
    //Moons
    answer[301]=(269.9949
            + 0.0031*T
            - 3.8787*sin(P(E,1,T))
            - 0.1204*sin(P(E,2,T))
            + 0.0700*sin(P(E,3,T))
            - 0.0172*sin(P(E,4,T))
            + 0.0072*sin(P(E,6,T))
            - 0.0052*sin(P(E,10,T))
            + 0.0043*sin(P(E,13,T)));
    answer[401]=(317.68-0.108*T+1.79*sin(P(M,1,T)));
    answer[402]=(316.65-0.108*T+2.98*sin(P(M,3,T)));
    answer[501]=(268.05 - 0.009*T + 0.094*sin(P(J,3,T)) + 0.024*sin(P(J,4,T)));
    answer[502]=(268.08 - 0.009*T + 1.086*sin(P(J,4,T)) + 0.060*sin(P(J,5,T)) + 0.015*sin(P(J,6,T)) + 0.009*sin(P(J,7,T)));
    answer[503]=(268.20 - 0.009*T - 0.037*sin(P(J,4,T)) + 0.431*sin(P(J,5,T))+ 0.091*sin(P(J,6,T)));
    answer[504]=(268.72 - 0.009*T - 0.068*sin(P(J,5,T)) + 0.590*sin(P(J,6,T))+ 0.010*sin(P(J,8,T)));
    answer[601]=  40.66 - 0.036*T + 13.56*sin(P(S,3,T));
    answer[602]=  40.66 - 0.036*T;
    answer[603]=  40.66 - 0.036*T +  9.66*sin(P(S,4,T));
    answer[604]=  40.66 - 0.036*T;
    answer[605]=  40.38 - 0.036*T +  3.10*sin(P(S,6,T));
    answer[606]=  36.41 - 0.036*T +  2.66*sin(P(S,7,T));
    answer[608]= 318.16 - 3.949*T;
    answer[610]=  40.58 - 0.036*T-1.623*sin(P(S,2,T))-0.023*sin(2*P(S,2,T));
    answer[611]=  40.58 - 0.036*T+3.133*sin(P(S,1,T))-0.086*sin(2*P(S,1,T));
    answer[616]=  40.58 - 0.036*T;
    answer[617]=  40.58 - 0.036*T;
    answer[701]=(257.43 + 0.29*sin(P(U,13,T)));
    answer[702]=(257.43 + 0.21*sin(P(U,14,T)));
    answer[703]=(257.43 + 0.29*sin(P(U,15,T)));
    answer[704]=(257.43 + 0.16*sin(P(U,16,T)));
    answer[705]=(257.43 + 4.41*sin(P(U,11,T)) - 0.04*sin(2*P(U,11,T)));
    answer[801]=(299.36
            -32.35*sin(1*P(N,7,T)) - 6.28*sin(2*P(N,7,T)) - 2.08*sin(3*P(N,7,T))
            - 0.74*sin(4*P(N,7,T)) - 0.28*sin(5*P(N,7,T)) - 0.11*sin(6*P(N,7,T))
            - 0.07*sin(7*P(N,7,T)) - 0.02*sin(8*P(N,7,T)) - 0.01*sin(9*P(N,7,T)));
    answer[901]=313.02;
    //Asteroids
    answer[1]=(348.76);
    answer[2]=(9.47);
    return answer;
  }
  
  private static double[] De0(double Time) {
    double D=Time-RotEpoch;
    double T=D/36525;
    double[] answer=new double[1000];
    for(int i=0;i<1000;i++) answer[i]=Double.NaN;
    //Sun
    answer[ 99]=			(63.87);
    //Planets
    answer[199]=			(61.45-0.005*T);
    answer[299]=			(67.16);
    answer[399]=			(90-0.557*T);
    answer[499]=			(52.886-0.061*T);
    answer[599]=			(64.49+0.003*T);
    answer[699]=			(83.537-0.004*T);
    answer[799]=			(-15.175);
    answer[899]=			(43.46-0.51*cos(P(N,0,T)));
    answer[999]=			(9.09);
    //Moons
    answer[301]=			(66.5392
            + 0.0130*T
            + 1.5419*cos(P(E,1,T))
            + 0.0239*cos(P(E,2,T))
            - 0.0278*cos(P(E,3,T))
            + 0.0068*cos(P(E,4,T))
            - 0.0029*cos(P(E,6,T))
            + 0.0009*cos(P(E,7,T))
            + 0.0008*cos(P(E,10,T))
            - 0.0009*cos(P(E,13,T)));
    answer[401]=			(52.90-0.061*T-1.08*cos(P(M,1,T)));
    answer[402]=			(53.52-0.061*T-1.78*cos(P(M,3,T)));
    answer[501]=			(64.50 + 0.003*T + 0.040*cos(P(J,3,T)) + 0.011*cos(P(J,4,T)));
    answer[502]=			(64.51 + 0.003*T + 0.468*cos(P(J,4,T)) + 0.026*cos(P(J,5,T)) + 0.007*cos(P(J,6,T)) + 0.002*cos(P(J,7,T)));
    answer[503]=			(64.57 + 0.003*T - 0.016*cos(P(J,4,T)) + 0.186*cos(P(J,5,T)) + 0.039*cos(P(J,6,T)));
    answer[504]=			(64.83 + 0.003*T - 0.029*cos(P(J,5,T)) + 0.254*cos(P(J,6,T)) - 0.004*cos(P(J,8,T)));
    
    answer[601]=  83.52 - 0.004*T -  1.53*cos(P(S,3,T));
    answer[602]=  83.52 - 0.004*T;
    answer[603]=  83.52 - 0.004*T -  1.09*cos(P(S,4,T));
    answer[604]=  83.52 - 0.004*T;
    answer[605]=  83.55 - 0.004*T -  0.35*cos(P(S,6,T));
    answer[606]=  83.94 - 0.004*T -  0.30*cos(P(S,7,T));
    answer[608]=  75.03 - 1.143*T;
    answer[610]=  83.52 - 0.004*T -0.183*cos(P(S,2,T))+0.001*cos(2*P(S,2,T));
    answer[611]=  83.52 - 0.004*T -0.356*cos(P(S,1,T))-0.001*cos(2*P(S,1,T));
    answer[616]=  83.53 - 0.004*T;
    answer[617]=  83.53 - 0.004*T;
    answer[701]=(-15.10 + 0.28*cos(P(U,13,T)));
    answer[702]=(-15.10 + 0.20*cos(P(U,14,T)));
    answer[703]=(-15.10 + 0.28*cos(P(U,15,T)));
    answer[704]=(-15.10 + 0.16*cos(P(U,16,T)));
    answer[705]=(-15.08 + 4.25*cos(P(U,11,T)) - 0.02*cos(2*P(U,11,T)));
    answer[801]=(41.17 + 22.55*cos(1*P(N,7,T)) + 2.10*cos(2*P(N,7,T)) + 0.55*cos(3*P(N,7,T))
    +  0.16*cos(4*P(N,7,T)) + 0.05*cos(5*P(N,7,T)) + 0.02*cos(6*P(N,7,T))
    +  0.01*cos(7*P(N,7,T)));
    answer[901]= 9.09;
    //Asteroids
    answer[1]=			(87.12);
    answer[2]=			(26.70);
    return answer;
  }
  
  private static double[] W0(double Time) {
    double D=Time-RotEpoch;
    double T=D/36525;
    double[] answer=new double[1000];
    //Sun
    answer[ 99]=			(84.10+14.1844*D);
    //Planets
    answer[199]=			(329.55+6.1385025*D);
    answer[299]=			(160.20-1.4813688*D);
    answer[399]=			(190.16+360.9856235*D);
    answer[499]=			(176.901+350.891983*D);
    answer[599]=			(43.30+870.270*D) ;
    answer[699]=			(38.90+810.7939024*D);
    answer[799]=			(203.81-501.1600928*D);
    answer[899]=			(253.81+536.3128492*D-0.48*sin(P(N,0,T)));
    answer[999]=			(236.77-56.3623195*D);
    //Moons
    answer[301]=			(38.3213
            + 13.17635815*D
            - 1.4e-12*D*D
            + 3.5610*sin(P(E,1,T))
            + 0.1208*sin(P(E,2,T))
            - 0.0642*sin(P(E,3,T))
            + 0.0158*sin(P(E,4,T))
            + 0.0252*sin(P(E,5,T))
            - 0.0066*sin(P(E,6,T))
            - 0.0047*sin(P(E,7,T))
            - 0.0046*sin(P(E,8,T))
            + 0.0028*sin(P(E,9,T))
            + 0.0052*sin(P(E,10,T))
            + 0.0040*sin(P(E,11,T))
            + 0.0019*sin(P(E,12,T))
            - 0.0044*sin(P(E,13,T))
            );
    answer[401]=			(35.06+1128.8445850*D+8.864*T*T-1.24*sin(P(M,1,T))-0.78*sin(P(M,2,T)));
    answer[402]=			(79.41+ 285.1618970*D-0.520*T*T-2.58*sin(P(M,3,T))+0.19*sin(P(M,3,T)));
    answer[501]=			(200.39 + 203.4889538*D - 0.085*sin(P(J,3,T)) - 0.022*sin(P(J,4,T)));
    answer[502]=			(35.67 + 101.3747235*D - 0.980*sin(P(J,4,T)) - 0.054*sin(P(J,5,T))
    - 0.014*sin(P(J,6,T)) - 0.008*sin(P(J,7,T)));
    answer[503]=			(44.04 +  50.3176081*D + 0.033*sin(P(J,4,T)) - 0.389*sin(P(J,5,T))
    - 0.082*sin(P(J,6,T)));
    answer[504]=			(259.73 + 21.5710715*D + 0.061*sin(P(J,5,T)) - 0.533*sin(P(J,6,T))
    - 0.009*sin(P(J,8,T)));
    answer[601]= 337.46 + 381.9945550*D - 13.48*sin(P(S,3,T)) - 44.85*sin(P(S,5,T));
    answer[602]=   2.82 + 262.7318996*D;
    answer[603]=  10.45 + 190.6979085*D -  9.60*sin(P(S,4,T)) +  2.23*sin(P(S,5,T));
    answer[604]= 357.00 + 131.5349316*D;
    answer[605]= 235.16 +  79.6900478*D -  3.08*sin(P(S,6,T));
    answer[606]= 189.64 +  22.5769768*D -  2.64*sin(P(S,7,T));
    answer[608]= 350.20 +   4.5379572*D;
    answer[610]=  58.83 + 518.2359876*D+1.613*sin(P(S,2,T))-0.023*sin(2*P(S,2,T));
    answer[611]= 293.87 + 518.4907239*D+3.133*sin(P(S,1,T))-0.086*sin(2*P(S,1,T));
    answer[616]= 296.14 + 587.289    *D;
    answer[617]= 162.92 + 572.7891   *D;
    answer[701]=(156.22 - 142.8356681*D + 0.05*sin(P(U,12,T)) + 0.08*sin(P(U,13,T)));
    answer[702]=(108.05 -  86.8688923*D - 0.09*sin(P(U,12,T)) + 0.06*sin(P(U,14,T)));
    answer[703]=( 77.74 -  41.3514316*D + 0.08*sin(P(U,15,T)));
    answer[704]=(  6.77 -  26.7394932*D + 0.04*sin(P(U,16,T)));
    answer[705]=( 30.70 - 254.6906892*D - 1.27*sin(P(U,12,T)) + 0.15*sin(2*P(U,12,T))
    + 1.15*sin(P(U,11,T)) - 0.09*sin(2*P(U,11,T)));
    answer[801]=(296.53 -  61.2572637*D
            +22.25*sin(1*P(N,7,T)) + 6.73*sin(2*P(N,7,T)) + 2.05*sin(3*P(N,7,T))
            + 0.74*sin(4*P(N,7,T)) + 0.28*sin(5*P(N,7,T)) + 0.11*sin(6*P(N,7,T))
            + 0.05*sin(7*P(N,7,T)) + 0.02*sin(8*P(N,7,T)) + 0.01*sin(9*P(N,7,T)));
    answer[901]=56.77 - 56.3623195*D;
    //Asteroids
    answer[1]=			(265.95 - 1864.6280070*D);
    answer[2]=			(83.67 + 1226.9114850*D);
    return answer;
  }
  
  private static double mod(double x, double y) {
    double n=floor(x/y);
    return x - n*y;
  }
  public static String PovVectors(double TDB) {
    double[] De=De0(TDB);
    double[] RA=RA0(TDB);
    double[] W=W0(TDB);
    double[] W1=W0(TDB+1);
    StringBuffer S=new StringBuffer();
    S.append("#declare RotVec=array[1000]\n");
    S.append("#declare RotWdot=array[1000]\n");
    S.append("#declare EarthTilt="+epsJ2000D+";\n");
    S.append("#declare RotEpoch="+TDB+";\n");
    for(int i=0;i<1000;i++) {
      if(!Double.isNaN(De[i])) {
        S.append("#declare RotVec["+i+"]=<"+RA[i]+","+De[i]+","+mod(W[i],360)+">;");
        S.append("#declare RotWdot["+i+"]="+(W1[i]-W[i])+";\n");
      }
    }
    return S.toString();
  }
  public static String PovVectors(double TDB, int[] Obj) {
    double[] De=De0(TDB);
    double[] RA=RA0(TDB);
    double[] W=W0(TDB);
    double[] W1=W0(TDB+1);
    StringBuffer S=new StringBuffer();
    S.append("#declare RotVec=array[1000]\n");
    S.append("#declare RotWdot=array[1000]\n");
    S.append("#declare EarthTilt="+epsJ2000D+";\n");
    S.append("#declare RotEpoch="+TDB+";\n");
    for(int j=0;j<Obj.length;j++) {
      int i=Obj[j];
      if(!Double.isNaN(De[i])) {
        S.append("#declare RotVec["+i+"]=<"+RA[i]+","+De[i]+","+mod(W[i],360)+">;");
        S.append("#declare RotWdot["+i+"]="+(W1[i]-W[i])+";\n");
      }
    }
    return S.toString();
  }
  public static void main(String[] args) {
    System.out.println(PovVectors(2450000.0));
  }
}
