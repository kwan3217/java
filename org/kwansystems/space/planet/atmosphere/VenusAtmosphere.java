package org.kwansystems.space.planet.atmosphere;

import org.kwansystems.tools.table.*;

public class VenusAtmosphere extends CarbonDioxideAtmosphere {
  public VenusAtmosphere() {
    super();
    gamma=1.20;  //Ratio of specific heats for carbon dioxide
    //S=231.381;
    sigma=3.897e-10; //Collision diameter for carbon dioxide
    Zlimit=H[H.length-1]*1000;
  }
  private static double[] H=new double[] {
    -4,    0,    4,    8,    12,    16,    20,    24,    28,    32,    36,
    40,    44,    48,    52,    56,    60,    64,    68,    72,    76,    
    80,    84,    88,    92,    96,    
    100,    110,    120,    130,    140,    150,    160,    170,    180,    190,
    200,    210,    220,    230,    240,    250,    260,    270,    280,    290,
    300,    310,    320,    330,    340,    350
  };
  private static double[] T=new double[] {
    798.1,    767.5,    736.5,    705.2,    673.4,    641.2,    608.5,    575.3,    541.4,    506.8,    471.4,
    433,    397.6,    371.4,    336.8,    299.6,    267.6,    246.2,    231.9,    217,    200.4,
    187.9,    180.1,    175.2,    171.4,    168.3,
    166.5,    171,    203.9,    214,    268,    378.4,    502.4,    591,    641.4,    674.9,
    691.5,    700.8,    705.5,    707.8,    709,    709.4,    709.4,    709.4,    709.4,    709.4,
    709.4,    709.5,    709.5,    709.5,    709.5,    709.5
  };
  private static double[] P=new double[] {
    1.20E+07,    9.49E+06,    7.41E+06,    5.73E+06,    4.38E+06,    3.30E+06,    2.46E+06,
    1.80E+06,    1.29E+06,    9.10E+05,    6.25E+05,    4.16E+05,    2.67E+05,    1.66E+05,
    9.91E+04,    5.57E+04,    2.93E+04,    1.44E+04,    6.71E+03,    2.99E+03,    1.25E+03,
    4.92E+02,    1.84E+02,    6.65E+01,    2.35E+01,    8.11E+00,    2.77E+00,    1.86E-01,
    1.59E-02,    1.91E-03,    3.01E-04,    7.79E-05,    2.98E-05,    1.41E-05,    7.51E-06,
    4.28E-06,    2.58E-06,    1.62E-06,    1.06E-06,    7.20E-07,    5.07E-07,    3.69E-07,
    2.75E-07,    2.10E-07,    1.64E-07,    1.30E-07,    1.05E-07,    8.51E-08,    7.01E-08,
    5.82E-08,    4.89E-08,    4.14E-08

  };
  private static double[] M=new double[] {
    43.531,    43.531,    43.531,    43.531,    43.531,    43.531,    43.531,
    43.531,    43.531,    43.531,    43.531,    43.531,    43.531,    43.531,
    43.531,    43.531,    43.531,    43.531,    43.531,    43.531,    43.531,
    43.531,    43.531,    43.531,    43.531,    43.531,    43.531,    43.531,
    43.531,    43.531,    42.963,    42.015,    40.818,    39.404,    37.732,
    35.781,    33.576,    31.179,    28.700,    26.266,    23.994,    21.963,
    20.207,    18.719,    17.467,    16.409,    15.499,    14.699,    13.976,
    13.306,    12.672,    12.063
  };
  private static Table TTable=new QuadraticTable(H,new double[][] {T,P,M});
  public double Temp(double Alt) {
    return TTable.Interp(Alt/1000,0);
  }
  public double Pressure(double Alt) {
    return TTable.Interp(Alt/1000,1);
  }
  public double MolWeight(double Alt) {
    return TTable.Interp(Alt/1000,2);
  }
  public static void main(String[] args) {
    Atmosphere A=new VenusAtmosphere();
    System.out.println("Alt km |T K  |P mb    |rho g/cm3|Cs m/s|MolWt kg/kmol|N /m3   |L m     |mu kg/(m s)");
    for(double Alt : H) {
      AirProperties P=A.calcProps(Alt*1000);
      System.out.printf("%7.0f|%5.1f|%8.2E| %8.2E|   %3.0f|       %6.3f|%8.2E|%8.2E|%8.2E\n",
        P.Altitude/1000, P.Temperature, P.Pressure/100.0,P.Density/1000.0,P.VSound,P.MolWt,P.NumberDensity,P.MeanFreePath,P.Viscosity
      );
    }
  }
}
