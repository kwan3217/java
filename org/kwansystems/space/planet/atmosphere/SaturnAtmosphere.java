package org.kwansystems.space.planet.atmosphere;

import org.kwansystems.tools.table.QuadraticTable;
import org.kwansystems.tools.table.Table;

public class SaturnAtmosphere extends SimpleAtmosphere {
  public static double HFraction=0.96;
  // http://en.wikipedia.org/wiki/Heat_capacity_ratio
  public static final double HGamma=1.5; //Cold hydrogen has a higher gamma than the idealized formula says
  public static final double HeGamma=5.0/3.0; //Theoretical result, good for measured result at room temperature
  public static final double HeFraction=1.0-HFraction;
  public static final double He3Fraction=2.17e-4;
  public static final double M_H2=2*1.00794;
  public static final double M_He=4.002602;
  public static final double M_He3=3.016;
  public static final double M0=HFraction*M_H2+(1.0-HFraction)*((1-He3Fraction)*M_He+He3Fraction*M_He3);
  public static final double HeSigma=2.576e-10; // http://www.osti.gov/energycitations/Fservlets/purl/15005357-pWVbpt/native/15005357.pdf
  public static final double HSigma=2.928e-10; // http://www.osti.gov/energycitations/Fservlets/purl/15005357-pWVbpt/native/15005357.pdf
  public static final double Tinf=140;
  public static final double ScaleHeight;
  public SaturnAtmosphere() {
    super();
    gamma=HGamma*HFraction+HeGamma*(1-HFraction);
    sigma=HSigma*HFraction+HeSigma*(1-HeFraction);
    Zlimit=2000000;
  }
  private static double[] H=new double[] { //Height of sampled points, km
    -14.1,    -12.5,     -9.9,     -7.4,     -4.9,     -2.4,      0.0,      2.4,      4.7,      7.0,      9.3,
     11.6,     13.8,     16.0,     18.1,     20.2,     22.3,     24.4,     26.4,     28.4,     30.3,     32.3,
     34.2,     37.9,     41.6,     45.1,     48.6,     52.0,     55.4,     58.7,     62.0,     66.9,     71.6,
     76.3,     80.9,     85.5,     90.0,     94.4,    100.4,    106.3,    112.2,    118.1,    124.1,    130.2,
    136.4,    142.8,    151.0,    159.8,    168.9,    178.8,    189.0,    199.9,    211.3,    223.1,    235.3,
    247.7,    260.2,    273.0,    285.8,    298.6,    311.4,    324.3,    337.4,    350.7,    363.9,    376.7
  };
  private static double[] T=new double[] { //Temperature at altitudes, K
    146.2,    145.0,    142.9,    140.9,    138.9,    136.8,    134.8,    132.8,    130.8,    128.9,    127.0,
    125.0,    123.2,    121.3,    119.4,    117.6,    115.8,    114.1,    112.4,    110.7,    109.1,    107.5,
    106.0,    103.2,    100.7,     98.4,     96.5,     94.8,     93.5,     92.4,     91.4,     89.8,     87.9,
     86.3,     85.2,     84.3,     83.4,     82.7,     82.4,     82.0,     82.3,     83.1,     83.9,     85.3,
     87.6,     90.5,     95.0,     99.5,    104.8,    112.0,    117.7,    123.5,    129.0,    133.4,    136.9,
    137.7,    140.3,    142.5,    141.5,    141.2,    142.2,    143.8,    146.2,    147.3,    143.2,    138.7
  };
  private static double[] P=new double[] { //Pressure at sampled altitudes, Pa
   129848,   125893,   120226,   114815,   109648,   104713,   100000,    95499,    91201,    87096,    83176,
    79433,    75858,    72444,    69183,    66069,    63096,    60256,    57544,    54954,    52481,    50119,
    47863,    43652,    39811,    36308,    33113,    30200,    27542,    25119,    22909,    19953,    17378,
    15136,    13183,    11482,    10000,     8710,     7244,     6026,     5012,     4169,     3467,     2884,
     2399,     1995,     1585,     1259,     1000,      794,      631,      501,      398,      316,      251,
      200,      158,      126,      100,       79,       63,       50,       40,       32,       25,       20
  };
  static {
    double dZ=(H[H.length-1]-H[H.length-2])*1000;
    double dP=P[H.length-1]-P[H.length-2];
    ScaleHeight=-P[H.length-2]/(dP/dZ);
  }
  private static Table TTable=new QuadraticTable(H,new double[][] {T,P});
  public double Temp(double Alt) {
    if(Alt>H[H.length-1]*1000) {
      return Tinf;
    }
    return TTable.Interp(Alt/1000,0);
  }
  public double Pressure(double Alt) {
    if(Alt>H[H.length-1]*1000) {
      double dZ=Alt-H[H.length-1]*1000;
      return P[H.length-1]*Math.exp(-dZ/ScaleHeight);
    }
    return TTable.Interp(Alt/1000,1);
  }
  public double MolWeight(double Alt) {
    return M0;
  }
  public static void main(String[] args) {
    Atmosphere A=new SaturnAtmosphere();
    System.out.println("Alt m  |T K  |P Pa    |rho kg/m3|Cs m/s|M kg/kmol|N /m3   |L m     |mu");
    for(double Alt : H) {
      AirProperties P=A.calcProps(Alt*1000);
      System.out.printf("%7.0f|%5.1f|%8.2E| %8.2E|   %3.0f|   %6.3f|%8.2E|%8.2E|%8.2E\n",
       P.Altitude, P.Temperature, P.Pressure,P.Density,P.VSound,P.MolWt,P.NumberDensity,P.MeanFreePath,P.Viscosity
      );
    }
    for(double Alt=380000;Alt<A.Zlimit;Alt+=10000) {
      AirProperties P=A.calcProps(Alt);
      System.out.printf("%7.0f|%5.1f|%8.2E| %8.2E|   %3.0f|   %6.3f|%8.2E|%8.2E|%8.2E\n",
       P.Altitude, P.Temperature, P.Pressure,P.Density,P.VSound,P.MolWt,P.NumberDensity,P.MeanFreePath,P.Viscosity
      );
    }
  }}
