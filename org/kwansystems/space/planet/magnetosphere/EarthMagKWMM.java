package org.kwansystems.space.planet.magnetosphere;

import java.io.*;
import static java.lang.Math.*;

/**
 * This is intended to match WMM-2005 but use less Fortran-inspired code, as well
 * as have the coefficients embedded.
 */
public class EarthMagKWMM implements Magnetosphere {
  public static final double[][][] WM2005Cof=new double[][][] {
/*  0  0 */{{      0.0,       0.0,        0.0,        0.0}},
/*  1  0 */{{ -29556.8,       0.0,        8.0,        0.0},
/*  1  1 */ {  -1671.7,    5079.8,       10.6,      -20.9}},
/*  2  0 */{{  -2340.6,       0.0,      -15.1,        0.0},
/*  2  1 */ {   3046.9,   -2594.7,       -7.8,      -23.2},
/*  2  2 */ {   1657.0,    -516.7,       -0.8,      -14.6}},
/*  3  0 */{{   1335.4,       0.0,        0.4,        0.0},
/*  3  1 */ {  -2305.1,    -199.9,       -2.6,        5.0},
/*  3  2 */ {   1246.7,     269.3,       -1.2,       -7.0},
/*  3  3 */ {    674.0,    -524.2,       -6.5,       -0.6}},
/*  4  0 */{{    919.8,       0.0,       -2.5,        0.0},
/*  4  1 */ {    798.1,     281.5,        2.8,        2.2},
/*  4  2 */ {    211.3,    -226.0,       -7.0,        1.6},
/*  4  3 */ {   -379.4,     145.8,        6.2,        5.8},
/*  4  4 */ {    100.0,    -304.7,       -3.8,        0.1}},
/*  5  0 */{{   -227.4,       0.0,       -2.8,        0.0},
/*  5  1 */ {    354.6,      42.4,        0.7,        0.0},
/*  5  2 */ {    208.7,     179.8,       -3.2,        1.7},
/*  5  3 */ {   -136.5,    -123.0,       -1.1,        2.1},
/*  5  4 */ {   -168.3,     -19.5,        0.1,        4.8},
/*  5  5 */ {    -14.1,     103.6,       -0.8,       -1.1}},
/*  6  0 */{{     73.2,       0.0,       -0.7,        0.0},
/*  6  1 */ {     69.7,     -20.3,        0.4,       -0.6},
/*  6  2 */ {     76.7,      54.7,       -0.3,       -1.9},
/*  6  3 */ {   -151.2,      63.6,        2.3,       -0.4},
/*  6  4 */ {    -14.9,     -63.4,       -2.1,       -0.5},
/*  6  5 */ {     14.6,      -0.1,       -0.6,       -0.3},
/*  6  6 */ {    -86.3,      50.4,        1.4,        0.7}},
/*  7  0 */{{     80.1,       0.0,        0.2,        0.0},
/*  7  1 */ {    -74.5,     -61.5,       -0.1,        0.6},
/*  7  2 */ {     -1.4,     -22.4,       -0.3,        0.4},
/*  7  3 */ {     38.5,       7.2,        1.1,        0.2},
/*  7  4 */ {     12.4,      25.4,        0.6,        0.3},
/*  7  5 */ {      9.5,      11.0,        0.5,       -0.8},
/*  7  6 */ {      5.7,     -26.4,       -0.4,       -0.2},
/*  7  7 */ {      1.8,      -5.1,        0.6,        0.1}},
/*  8  0 */{{     24.9,       0.0,        0.1,        0.0},
/*  8  1 */ {      7.7,      11.2,        0.3,       -0.2},
/*  8  2 */ {    -11.6,     -21.0,       -0.4,        0.1},
/*  8  3 */ {     -6.9,       9.6,        0.3,        0.3},
/*  8  4 */ {    -18.2,     -19.8,       -0.3,        0.4},
/*  8  5 */ {     10.0,      16.1,        0.2,        0.1},
/*  8  6 */ {      9.2,       7.7,        0.4,       -0.2},
/*  8  7 */ {    -11.6,     -12.9,       -0.7,        0.4},
/*  8  8 */ {     -5.2,      -0.2,        0.4,        0.4}},
/*  9  0 */{{      5.6,       0.0,        0.0,        0.0},
/*  9  1 */ {      9.9,     -20.1,        0.0,        0.0},
/*  9  2 */ {      3.5,      12.9,        0.0,        0.0},
/*  9  3 */ {     -7.0,      12.6,        0.0,        0.0},
/*  9  4 */ {      5.1,      -6.7,        0.0,        0.0},
/*  9  5 */ {    -10.8,      -8.1,        0.0,        0.0},
/*  9  6 */ {     -1.3,       8.0,        0.0,        0.0},
/*  9  7 */ {      8.8,       2.9,        0.0,        0.0},
/*  9  8 */ {     -6.7,      -7.9,        0.0,        0.0},
/*  9  9 */ {     -9.1,       6.0,        0.0,        0.0}},
/* 10  0 */{{     -2.3,       0.0,        0.0,        0.0},
/* 10  1 */ {     -6.3,       2.4,        0.0,        0.0},
/* 10  2 */ {      1.6,       0.2,        0.0,        0.0},
/* 10  3 */ {     -2.6,       4.4,        0.0,        0.0},
/* 10  4 */ {      0.0,       4.8,        0.0,        0.0},
/* 10  5 */ {      3.1,      -6.5,        0.0,        0.0},
/* 10  6 */ {      0.4,      -1.1,        0.0,        0.0},
/* 10  7 */ {      2.1,      -3.4,        0.0,        0.0},
/* 10  8 */ {      3.9,      -0.8,        0.0,        0.0},
/* 10  9 */ {     -0.1,      -2.3,        0.0,        0.0},
/* 10 10 */ {     -2.3,      -7.9,        0.0,        0.0}},
/* 11  0 */{{      2.8,       0.0,        0.0,        0.0},
/* 11  1 */ {     -1.6,       0.3,        0.0,        0.0},
/* 11  2 */ {     -1.7,       1.2,        0.0,        0.0},
/* 11  3 */ {      1.7,      -0.8,        0.0,        0.0},
/* 11  4 */ {     -0.1,      -2.5,        0.0,        0.0},
/* 11  5 */ {      0.1,       0.9,        0.0,        0.0},
/* 11  6 */ {     -0.7,      -0.6,        0.0,        0.0},
/* 11  7 */ {      0.7,      -2.7,        0.0,        0.0},
/* 11  8 */ {      1.8,      -0.9,        0.0,        0.0},
/* 11  9 */ {      0.0,      -1.3,        0.0,        0.0},
/* 11 10 */ {      1.1,      -2.0,        0.0,        0.0},
/* 11 11 */ {      4.1,      -1.2,        0.0,        0.0}},
/* 12  0 */{{     -2.4,       0.0,        0.0,        0.0},
/* 12  1 */ {     -0.4,      -0.4,        0.0,        0.0},
/* 12  2 */ {      0.2,       0.3,        0.0,        0.0},
/* 12  3 */ {      0.8,       2.4,        0.0,        0.0},
/* 12  4 */ {     -0.3,      -2.6,        0.0,        0.0},
/* 12  5 */ {      1.1,       0.6,        0.0,        0.0},
/* 12  6 */ {     -0.5,       0.3,        0.0,        0.0},
/* 12  7 */ {      0.4,       0.0,        0.0,        0.0},
/* 12  8 */ {     -0.3,       0.0,        0.0,        0.0},
/* 12  9 */ {     -0.3,       0.3,        0.0,        0.0},
/* 12 10 */ {     -0.1,      -0.9,        0.0,        0.0},
/* 12 11 */ {     -0.3,      -0.4,        0.0,        0.0},
/* 12 12 */ {     -0.1,       0.8,        0.0,        0.0}}};
  public static final double[][][] WM2010Cof=new double[][][] { 
    {{        0.0d,      0.0  ,      0.0 ,       0.0}}, //  0  0 
    {{  -29496.6 ,      0.0  ,     11.6 ,       0.0},   //  1  0
     {   -1586.3 ,   4944.4  ,     16.5 ,     -25.9}},  //  1  1
    {{   -2396.6 ,      0.0  ,    -12.1 ,       0.0},   //  2  0
     {    3026.1 ,  -2707.7  ,     -4.4 ,     -22.5},   //  2  1
     {    1668.6 ,   -576.1  ,      1.9 ,     -11.8}},  //  2  2
    {{    1340.1 ,      0.0  ,      0.4 ,       0.0},   //  3  0
     {   -2326.2 ,   -160.2  ,     -4.1 ,       7.3},   //  3  1
     {    1231.9 ,    251.9  ,     -2.9 ,      -3.9},   //  3  2
     {     634.0 ,   -536.6  ,     -7.7 ,      -2.6}},  //  3  3
    {{     912.6 ,      0.0  ,     -1.8 ,       0.0},   //  4  0
     {     808.9 ,    286.4  ,      2.3 ,       1.1},   //  4  1
     {     166.7 ,   -211.2  ,     -8.7 ,       2.7},   //  4  2
     {    -357.1 ,    164.3  ,      4.6 ,       3.9},   //  4  3
     {      89.4 ,   -309.1  ,     -2.1 ,      -0.8}},  //  4  4
    {{    -230.9 ,      0.0  ,     -1.0 ,       0.0},   //  5  0
     {     357.2 ,     44.6  ,      0.6 ,       0.4},   //  5  1
     {     200.3 ,    188.9  ,     -1.8 ,       1.8},   //  5  2
     {    -141.1 ,   -118.2  ,     -1.0 ,       1.2},   //  5  3
     {    -163.0 ,      0.0  ,      0.9 ,       4.0},   //  5  4
     {      -7.8 ,    100.9  ,      1.0 ,      -0.6}},  //  5  5
    {{      72.8 ,      0.0  ,     -0.2 ,       0.0},   //  6  0
     {      68.6 ,    -20.8  ,     -0.2 ,      -0.2},   //  6  1
     {      76.0 ,     44.1  ,     -0.1 ,      -2.1},   //  6  2
     {    -141.4 ,     61.5  ,      2.0 ,      -0.4},   //  6  3
     {     -22.8 ,    -66.3  ,     -1.7 ,      -0.6},   //  6  4
     {      13.2 ,      3.1  ,     -0.3 ,       0.5},   //  6  5
     {     -77.9 ,     55.0  ,      1.7 ,       0.9}},  //  6  6
    {{      80.5 ,      0.0  ,      0.1 ,       0.0},   //  7  0
     {     -75.1 ,    -57.9  ,     -0.1 ,       0.7},   //  7  1
     {      -4.7 ,    -21.1  ,     -0.6 ,       0.3},   //  7  2
     {      45.3 ,      6.5  ,      1.3 ,      -0.1},   //  7  3
     {      13.9 ,     24.9  ,      0.4 ,      -0.1},   //  7  4
     {      10.4 ,      7.0  ,      0.3 ,      -0.8},   //  7  5
     {       1.7 ,    -27.7  ,     -0.7 ,      -0.3},   //  7  6
     {       4.9 ,     -3.3  ,      0.6 ,       0.3}},  //  7  7
    {{      24.4 ,      0.0  ,     -0.1 ,       0.0},   //  8  0
     {       8.1 ,     11.0  ,      0.1 ,      -0.1},   //  8  1
     {     -14.5 ,    -20.0  ,     -0.6 ,       0.2},   //  8  2
     {      -5.6 ,     11.9  ,      0.2 ,       0.4},   //  8  3
     {     -19.3 ,    -17.4  ,     -0.2 ,       0.4},   //  8  4
     {      11.5 ,     16.7  ,      0.3 ,       0.1},   //  8  5
     {      10.9 ,      7.0  ,      0.3 ,      -0.1},   //  8  6
     {     -14.1 ,    -10.8  ,     -0.6 ,       0.4},   //  8  7
     {      -3.7 ,      1.7  ,      0.2 ,       0.3}},  //  8  8
    {{       5.4 ,      0.0  ,      0.0 ,       0.0},   //  9  0
     {       9.4 ,    -20.5  ,     -0.1 ,       0.0},   //  9  1
     {       3.4 ,     11.5  ,      0.0 ,      -0.2},   //  9  2
     {      -5.2 ,     12.8  ,      0.3 ,       0.0},   //  9  3
     {       3.1 ,     -7.2  ,     -0.4 ,      -0.1},   //  9  4
     {     -12.4 ,     -7.4  ,     -0.3 ,       0.1},   //  9  5
     {      -0.7 ,      8.0  ,      0.1 ,       0.0},   //  9  6
     {       8.4 ,      2.1  ,     -0.1 ,      -0.2},   //  9  7
     {      -8.5 ,     -6.1  ,     -0.4 ,       0.3},   //  9  8
     {     -10.1 ,      7.0  ,     -0.2 ,       0.2}},  //  9  9
    {{      -2.0 ,      0.0  ,      0.0 ,       0.0},   // 10  0
     {      -6.3 ,      2.8  ,      0.0 ,       0.1},   // 10  1
     {       0.9 ,     -0.1  ,     -0.1 ,      -0.1},   // 10  2
     {      -1.1 ,      4.7  ,      0.2 ,       0.0},   // 10  3
     {      -0.2 ,      4.4  ,      0.0 ,      -0.1},   // 10  4
     {       2.5 ,     -7.2  ,     -0.1 ,      -0.1},   // 10  5
     {      -0.3 ,     -1.0  ,     -0.2 ,       0.0},   // 10  6
     {       2.2 ,     -3.9  ,      0.0 ,      -0.1},   // 10  7
     {       3.1 ,     -2.0  ,     -0.1 ,      -0.2},   // 10  8
     {      -1.0 ,     -2.0  ,     -0.2 ,       0.0},   // 10  9
     {      -2.8 ,     -8.3  ,     -0.2 ,      -0.1}},  // 10 10
    {{       3.0 ,      0.0  ,      0.0 ,       0.0},   // 11  0
     {      -1.5 ,      0.2  ,      0.0 ,       0.0},   // 11  1
     {      -2.1 ,      1.7  ,      0.0 ,       0.1},   // 11  2
     {       1.7 ,     -0.6  ,      0.1 ,       0.0},   // 11  3
     {      -0.5 ,     -1.8  ,      0.0 ,       0.1},   // 11  4
     {       0.5 ,      0.9  ,      0.0 ,       0.0},   // 11  5
     {      -0.8 ,     -0.4  ,      0.0 ,       0.1},   // 11  6
     {       0.4 ,     -2.5  ,      0.0 ,       0.0},   // 11  7
     {       1.8 ,     -1.3  ,      0.0 ,      -0.1},   // 11  8
     {       0.1 ,     -2.1  ,      0.0 ,      -0.1},   // 11  9
     {       0.7 ,     -1.9  ,     -0.1 ,       0.0},   // 11 10
     {       3.8 ,     -1.8  ,      0.0 ,      -0.1}},  // 11 11
    {{      -2.2 ,      0.0  ,      0.0 ,       0.0},   // 12  0
     {      -0.2 ,     -0.9  ,      0.0 ,       0.0},   // 12  1
     {       0.3 ,      0.3  ,      0.1 ,       0.0},   // 12  2
     {       1.0 ,      2.1  ,      0.1 ,       0.0},   // 12  3
     {      -0.6 ,     -2.5  ,     -0.1 ,       0.0},   // 12  4
     {       0.9 ,      0.5  ,      0.0 ,       0.0},   // 12  5
     {      -0.1 ,      0.6  ,      0.0 ,       0.1},   // 12  6
     {       0.5 ,      0.0  ,      0.0 ,       0.0},   // 12  7
     {      -0.4 ,      0.1  ,      0.0 ,       0.0},   // 12  8
     {      -0.4 ,      0.3  ,      0.0 ,       0.0},   // 12  9
     {       0.2 ,     -0.9  ,      0.0 ,       0.0},   // 12 10
     {      -0.8 ,     -0.2  ,     -0.1 ,       0.0},   // 12 11
     {       0.0 ,      0.9  ,      0.1 ,       0.0}}}; // 12 12
   public static void main(String[] args) throws IOException {
    EarthMagKWMM E=new EarthMagKWMM(2005);
    E.testFile("Data/EarthMagWMM2005/WMM2006_TestValues.txt");
  }
  private final double[][][] WMMCof;
  private int maxord;
  private final int epoch;
  private double[][] g=new double[13][];
  private double[][] gd=new double[13][];
  private double[][] h=new double[13][];
  private double[][] hd=new double[13][];
  private double[] fn=new double[13];
  private double[] fm=new double[13];
  private double[][] k;
  private static final double a=6378.137;
  private static final double b=6356.7523142;
  private static final double re=6371.2;
  private static final double a2=a*a;
  private static final double b2=b*b;
  private static final double c2=a2-b2;
  private static final double a4=a2*a2;
  private static final double b4=b2*b2;
  private static final double c4=a4-b4;
  public EarthMagKWMM(int Lepoch) {
/* INITIALIZE CONSTANTS */
    epoch=Lepoch;
    if(epoch==2005) {
      WMMCof=WM2005Cof;
    } else {
      WMMCof=WM2010Cof;
    }
    maxord=WMMCof.length-1;
    normalizeCof();
  }
  public void testFile(String infn) throws IOException {
    LineNumberReader inf=new LineNumberReader(new FileReader(infn));
    String S=inf.readLine();
    while(!S.trim().startsWith("Lat")) S=inf.readLine();
    S=inf.readLine();
    double alt=0;
    double year=2006;
    System.out.printf("          Lat         Lon        D          I         H         X         Y         Z       F\n");
    while(S!=null) {
      String[] parts=S.split("\\s+");
      if(parts.length==9) {
        double lat=toRadians(Double.parseDouble(parts[0]));
        double lon=toRadians(Double.parseDouble(parts[1]));
        double D=toRadians(Double.parseDouble(parts[2]));
        double I=toRadians(Double.parseDouble(parts[3]));
        double H=Double.parseDouble(parts[4]);
        double X=Double.parseDouble(parts[5]);
        double Y=Double.parseDouble(parts[6]);
        double Z=Double.parseDouble(parts[7]);
        double F=Double.parseDouble(parts[8]);
        S=inf.readLine();
        parts=S.split("\\s+");
        double dD=toRadians(Double.parseDouble(parts[2]))/60.0;
        double dI=toRadians(Double.parseDouble(parts[3]))/60.0;
        double dH=Double.parseDouble(parts[4]);
        double dX=Double.parseDouble(parts[5]);
        double dY=Double.parseDouble(parts[6]);
        double dZ=Double.parseDouble(parts[7]);
        double dF=Double.parseDouble(parts[8]);
        S=inf.readLine();
        FieldProperties[] f=calcPropRates(alt,lat,lon,year+1);
        System.out.printf("Calc: %9.1f  %9.1f %9.1f %9.1f %9.1f %9.1f %9.1f %9.1f %9.1f\n",
              toDegrees(lat),toDegrees(lon),
              toDegrees(f[0].dec),toDegrees(f[0].dip),
              f[0].h,f[0].B.X(),f[0].B.Y(),f[0].B.Z(),f[0].ti
              );
        System.out.printf("File:                      %9.1f %9.1f %9.1f %9.1f %9.1f %9.1f %9.1f\n",
              toDegrees(D),toDegrees(I),
              H,X,Y,Z,F
              );
        System.out.printf("Diff:                      %9.1f %9.1f %9.1f %9.1f %9.1f %9.1f %9.1f\n",
              toDegrees(f[0].dec-D),toDegrees(f[0].dip-I),
              f[0].h-H,f[0].B.X()-X,f[0].B.Y()-Y,f[0].B.Z()-Z,f[0].ti-F
              );
        System.out.printf("Rate:                      %9.1f %9.1f %9.1f %9.1f %9.1f %9.1f %9.1f\n",
              toDegrees(f[1].dec)*60,toDegrees(f[1].dip)*60,
              f[1].h,f[1].B.X(),f[1].B.Y(),f[1].B.Z(),f[1].ti
              );
        System.out.printf("File:                      %9.1f %9.1f %9.1f %9.1f %9.1f %9.1f %9.1f\n",
              toDegrees(dD)*60,toDegrees(dI)*60,
              dH,dX,dY,dZ,dF
              );
        System.out.printf("Diff:                      %9.1f %9.1f %9.1f %9.1f %9.1f %9.1f %9.1f\n",
              toDegrees(f[1].dec-dD)*60,toDegrees(f[1].dip-dI)*60,
              f[1].h-dH,f[1].B.X()-dX,f[1].B.Y()-dY,f[1].B.Z()-dZ,f[1].ti-dF
              );
      } else {
        return;
      }
    }
  }
  private void normalizeCof() {
    double[][] snorm=new double[13][13];
    k=new double[13][];
/* CONVERT SCHMIDT NORMALIZED GAUSS COEFFICIENTS TO UNNORMALIZED */
    snorm[0][0] = 1.0;
    for (int n=1; n<=maxord; n++) {
      g[n]=new double[n+1];
      gd[n]=new double[n+1];
      h[n]=new double[n+1];
      hd[n]=new double[n+1];
      k[n]=new double[n+1];
      snorm[n][0] = snorm[n-1][0]*(double)(2*n-1)/(double)n;
      int j = 2;
      for(int m=0; m<=n; m++) {
        k[n][m] = (double)(((n-1)*(n-1))-(m*m))/(double)((2*n-1)*(2*n-3));
        if (m > 0) {
          double flnmj = (double)((n-m+1)*j)/(double)(n+m);
          snorm[n][m] = snorm[n][m-1]*sqrt(flnmj);
          j = 1;
          h[n][m] = snorm[n][m]*WMMCof[n][m][1];
          hd[n][m] = snorm[n][m]*WMMCof[n][m][3];
        }
        g[n][m] = snorm[n][m]*WMMCof[n][m][0];
        gd[n][m] = snorm[n][m]*WMMCof[n][m][2];
      }
      fn[n] = (double)(n+1);
      fm[n] = (double)n;
    }
  }
  /**
   * The heart of the WMM model
   * @param alt Altitude above WGS-84 ellipsoid in km
   * @param rlat Geodetic latitude using WGS-84 ellipsoid and frame in radians
   * @param rlon Longitude using WGS-84 frame in radians
   * @param time time to compute model in decimal years (IE 1 Jul 2008~=2008.5)
   * @return an array of [bx, by, bz]
   *   <ul>
   * <li>bx - north component of field in local WGS-84 horizon frame in nanoTeslas
   * <li>by - east component of field in local WGS-84 horizon frame in nanoTeslas
   * <li>bz - down component of field in local WGS-84 horizon frame in nanoTeslas
   * </ul>
   * <p>
   * All interesting field data is encoded in the bx, by, and bz components. You can get
   * <ul>
   * <li>Compass horizontal declination in radians, poisitive east =atan2(by/bx)</li>
   * <li>Vertical inclination in radians, positive down=atan2(bz/bh)</li>
   * <li>horizontal field intensity =sqrt(bx^2+by^2)</li>
   * <li>Total field intensity =sqrt(bh^2+bz^2)</li>
   * </li>
   */
  public double[] geomg1(double alt, double rlat, double rlon, double time) {
    double q,q1,q2,ct,st,r2,r,d,ca,sa,aor,ar,
    par,temp1,temp2,parp,bx,by,bz;
    double dt = time - epoch;
    //Some conevnience values for
    double srlon = sin(rlon);
    double srlat = sin(rlat);
    double crlon = cos(rlon);
    double crlat = cos(rlat);
    double srlat2 = srlat*srlat;
    double crlat2 = crlat*crlat;

    //Convert from geodetic latitude and altitude to geocentric latitude and radius
    //TRWMM eq 17 and 18
    q = sqrt(a2-c2*srlat2);
    q1 = alt*q;
    q2 = ((q1+a2)/(q1+b2))*((q1+a2)/(q1+b2));
    ct = srlat/sqrt(q2*crlat2+srlat2);
    st = sqrt(1.0-(ct*ct));
    r2 = (alt*alt)+2.0*q1+(a4-c4*srlat2)/(q*q);
    r = sqrt(r2);
    d = sqrt(a2*crlat2+b2*srlat2);
    ca = (alt+d)/r;
    sa = c2*crlat*srlat/(r*d);
    //Normalize radius
    aor = re/r;
    ar = aor*aor;

    double[] sp=new double[13];
    double[] cp=new double[13];
    sp[0] = 0.0;
    cp[0] = 1.0;
    sp[1] = srlon;
    cp[1] = crlon;
    for (int m=2; m<=maxord; m++) {
      sp[m] = sp[1]*cp[m-1]+cp[1]*sp[m-1];
      cp[m] = cp[1]*cp[m-1]-sp[1]*sp[m-1];
    }
    double br=0;
    double bt=0;
    double bp=0;
    double bpp=0;
    double[][] snorm=new double[13][];
    snorm[0]=new double[1];
    snorm[0][0]=1.0;
    double[][] dp=new double[13][13];
    dp[0]=new double[1];
    dp[0][0] = 0.0;
    double[][] tg=new double[13][];
    double[][] th=new double[13][];
    double[] pp=new double[13];
    pp[0] = 1.0;
    for (int n=1; n<=maxord; n++) {
      ar = ar*aor;
      snorm[n]=new double[n+1];
      dp[n]=new double[n+1];
      tg[n]=new double[n+1];
      th[n]=new double[n+1];
      for (int m=0; m<=n; m++) {
/*
   COMPUTE UNNORMALIZED ASSOCIATED LEGENDRE POLYNOMIALS
   AND DERIVATIVES VIA RECURSION RELATIONS
*/
        if (n == m) {
          snorm[n][m] = st*snorm[n-1][m-1];
          dp[n][m] = st*dp[n-1][m-1]+ct*snorm[n-1][m-1];
        } else if (n == 1 && m == 0) {
          snorm[n][m] = ct*snorm[n-1][m];
          dp[n][m] = ct*dp[n-1][m]-st*snorm[n-1][m];
        } else if (n > 1) {
          snorm[n][m] = ct*snorm[n-1][m];
          dp[n][m] = ct*dp[n-1][m] - st*snorm[n-1][m];
          if(m<=n-2) {
            snorm[n][m]-=k[n][m]*snorm[n-2][m];
            if(m<=n-2) dp[n][m]-=k[n][m]*dp[n-2][m];
          }
        }
/*
    TIME ADJUST THE GAUSS COEFFICIENTS
*/
        tg[n][m] = g[n][m]+dt*gd[n][m];
        th[n][m] = h[n][m]+dt*hd[n][m];
/*
    ACCUMULATE TERMS OF THE SPHERICAL HARMONIC EXPANSIONS
*/
        par = ar*snorm[n][m];
        temp1 = tg[n][m]*cp[m]+th[n][m]*sp[m];
        temp2 = tg[n][m]*sp[m]-th[n][m]*cp[m];
        bt = bt-ar*temp1*dp[n][m];
        bp += (fm[m]*temp2*par);
        br += (fn[n]*temp1*par);
/*
    SPECIAL CASE:  NORTH/SOUTH GEOGRAPHIC POLES
*/
        if (st == 0.0 && m == 1) {
          if (n == 1) {
              pp[n] = pp[n-1];
          } else {
            pp[n] = ct*pp[n-1]-k[n][m]*pp[n-2];
          }
          parp = ar*pp[n];
          bpp += (fm[m]*temp2*parp);
        }
      }
    }
    if (st == 0.0) {
      bp = bpp;
    } else {
      bp /= st;
    }
/*
    ROTATE MAGNETIC VECTOR COMPONENTS FROM SPHERICAL TO
    GEODETIC COORDINATES
*/
    bx = -bt*ca-br*sa;
    by = bp;
    bz = bt*sa-br*ca;

    return new double[] {bx,by,bz};
  }

  public FieldProperties calcProps(double alt, double rlat, double rlon, double time) {
    double[] result=geomg1(alt,rlat,rlon,time);
    double bx1=result[0];
    double by1=result[1];
    double bz1=result[2];
    double bh1 = sqrt(bx1*bx1+by1*by1);
    double ti1 = sqrt(bh1*bh1+bz1*bz1);
    double dec1 = atan2(by1,bx1);
    double dip1 = atan2(bz1,bh1);

    return new FieldProperties(ti1,bh1,bx1,by1,bz1,dec1,dip1);
  }

  public double getEpoch() {
    return epoch;
  }
  public FieldProperties calcRates(double alt, double rlat, double rlon, double time) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  public FieldProperties[] calcPropRates(double alt, double rlat, double rlon, double time) {
    FieldProperties r0=calcProps(alt,rlat,rlon,time);
    FieldProperties r1=calcProps(alt,rlat,rlon,time+1.0);

    return new FieldProperties[] {r0,FieldProperties.diff(r0,r1)};
  }
}
