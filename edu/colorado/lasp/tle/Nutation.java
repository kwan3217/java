/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.colorado.lasp.tle;
import static java.lang.Math.*;

/**
 *
 * @author jeppesen
 */
public class Nutation {
  private static double[] poly(double x, double[] Coeffs) {
    int nd=1;
    double[] pd=new double[nd+1];
    int nnd;
    int nc=Coeffs.length-1;
    pd[0]=Coeffs[nc];
    for(int i=nc;i>=0;i--) {
      nnd=min(nd,nc-i);
      for(int j=nnd;j>=1;j--) {
        pd[j]=pd[j]*x+pd[j-1];
      }
      pd[0]=pd[0]*x+Coeffs[i];
    }
    double cnst=1.0;
    for(int i=2;i<=nd;i++) {
      cnst*=1;
      pd[i]*=cnst;
    }
    return pd;
  }
  public static double[] Nutate(double T) {
    final double D2R = 3.14159265358979324/180.0;
    final double S2R = 1.e-4/3600.0*D2R; //(CONVERT UNITS OF 10000 ARC SECONDS TO RADIANS)

    //Column labels for nutate_table
    final int CM=1;
    final int CE=2;
    final int CF=3;
    final int CD=4;
    final int CO=5;
    final int PSI0=7;
    final int PSI1=8;
    final int EPS0=9;
    final int EPS1=10;

    //Polynomials, all in deg/kiloyear^i
    //     MEAN ANOMALY OF THE MOON
    final double[] PM=new double[]{1.349629813888889e+2,  4.771988673980556e+6, 8.697222222222222e-1,  1.777777777777778e-2};

    //     MEAN ANOMALY OF THE SUN (EARTH)
    final double[] PE=new double[]{3.575277233333333e+2,  3.599905034000000e+5,-1.602777777777778e-2, -3.333333333333333e-3};

    //     MOON'S ARGUMENT OF LATITUDE
    final double[] PF=new double[]{9.327191027777778e+1,  4.832020175380556e+6,-3.682500000000000e-1,  3.055555555555556e-3};

    //     MEAN ELONGATION OF THE MOON FROM THE SUN
    final double[] PD=new double[]{2.978503630555556e+2,  4.452671114800000e+6,-1.914166666666667e-1,  5.277777777777778e-3};

    //   LONGITUDE OF THE ASCENDING NODE OF THE MOON'S MEAN ORBIT ON THE
    //   ECLIPTIC, MEASURED FROM THE MEAN EQUINOX OF THE DATE
    final double[] PO=new double[]{1.250445222222222e+2, -1.934136260833333e+4, 2.070833333333333e-1,  2.222222222222222e-3};

    final double[][] n=nutation_const;

    //   EVALUATE THE FUNDAMENTAL ARGUMENTS
    double[] M   = poly (T,PM);
    double[] E   = poly (T,PE);
    double[] F   = poly (T,PF);
    double[] D   = poly (T,PD);
    double[] O   = poly (T,PO);

    // COMPUTE THE SUM OF ALL TERMS
    double DPSI = 0.0;
    double DEPS = 0.0;
    double DDPSI = 0.0;
    double DDEPS = 0.0;
    for(int i=0;i<n.length;i++) {
      double L  =  ((n[i][CM]* M[0] + n[i][CE]* E[0] + n[i][CF]* F[0] + n[i][CD]* D[0] + n[i][CO]* O[0]) % 360.0 )*D2R;
      double D_L = ( n[i][CM]* M[1] + n[i][CE]* E[1] + n[i][CF]* F[1] + n[i][CD]* D[1] + n[i][CO]* O[1]          )*D2R;
      double SL = sin( L );
      double CL = cos( L );
      double PSI = n[i][PSI0] + n[i][PSI1]*T;
      double EPS = n[i][EPS0] + n[i][EPS1]*T;
      DPSI+= PSI*SL;
      DEPS+= EPS*CL;
      DDPSI+= PSI*CL*D_L + SL*n[i][PSI1];
      DDEPS+= EPS*SL*D_L + CL*n[i][EPS1];
    }
    return new double[] {DPSI*S2R,DEPS*S2R,DDPSI*S2R,DDEPS*S2R};
  }
  public static double[][] Nutate_Matrix(double T) {

//     OBTAIN THE 3 X 3 ROTATION MATRIX WHICH WILL APPLY THE NUTATION
//     IN LONGITUDE (DPSI) AND IN OBLIQUITY (DEPS) AT DYNAMIC TIME T
//     (JULIAN MILLENIA SINCE J2000) TO AN OBJECT WHOSE POSITION IS
//     EXPRESSED IN RECTANGULAR EQUATORIAL COORDINATES IN THE FK5
//     SYSTEM.

//     B. KNAPP, 1992-06-23, 1999-06-18
//     Translated to IDL by C. Jeppesen, 2009-10-08
//     Translated to Java by C. Jeppesen, 2009-10-12

//     REFERENCE: P. K. SEIDELMANN, EXPLANATORY SUPPLEMENT TO THE ASTRO-
//     NOMICAL ALMANAC, UNIVERSITY SCIENCE BOOKS, 1992 (P. 115).
      double[][] N=new double[3][3];

      double[] nut=Nutate( T);
      double DPSI=nut[0];
      double DEPS=nut[1];

//     NEED THE OBLIQUITY OF THE ECLIPTIC WITH AND WITHOUT NUTATION
      double EPS0 = Obliq( T );
      double EPS1 = EPS0+DEPS;

//     COMPUTE THE ROTATION MATRIX
      double SDP = sin(DPSI);
      double CDP = cos(DPSI);
      double SE0 = sin(EPS0);
      double CE0 = cos(EPS0);
      double SE1 = sin(EPS1);
      double CE1 = cos(EPS1);

      N[0][0] =  CDP;
      N[0][1] = -CE0*SDP;
      N[0][2] = -SE0*SDP;
      N[1][0] =  CE1*SDP;
      N[1][1] =  CE0*CE1*CDP+SE0*SE1;
      N[1][2] =  SE0*CE1*CDP-CE0*SE1;
      N[2][0] =  SE1*SDP;
      N[2][1] =  CE0*SE1*CDP-SE0*CE1;
      N[2][2] =  SE0*SE1*CDP+CE0*CE1;

      return N;

  }
  public static double Obliq(double T) {
//
//     GIVEN DYNAMICAL TIME T IN JULIAN MILLENIA SINCE J2000, RETURNS
//     THE MEAN OBLIQUITY OF THE ECLIPTIC (NEGLECTING NUTATION), IN
//     RADIANS.  (ALGORITHM OF J. LASKAR, ASTRONOMY AND ASTROPHYSICS
//     157 (1986) P. 68, AS PRESENTED BY JEAN MEEUS, "ASTRONOMICAL
//     ALGORITHMS", WILLMANN-BELL, 1991.)
//
//     B. KNAPP, 1992-05-02, 2000-09-01
//     Translated to IDL C. Jeppesen, 2009-10-08
//
//
//     CAUTION: THE ABSOLUTE VALUE OF T MUST BE LESS THAN OR EQUAL TO 10.
//
     final double D2R = 3.14159265358979324/180.0;
     final double U = T/10.0;
     return ( 23.43929111 +
         U*( -4680.93 +
         U*(    -1.55 +
         U*(  1999.25 +
         U*(   -51.38 +
         U*(  -249.67 +
         U*(   -39.05 +
         U*(     7.12 +
         U*(    27.87 +
         U*(     5.79 +
         U*      2.45 ) ) ) ) ) ) ) ) )/3600.0 )*D2R;
  }
  private static final double[][] nutation_const=new double[][] {
    {  1,  0, 0, 0, 0, 1,  6798.4,  -171996,  -1742,  92025,     89},
    {  2,  0, 0, 0, 0, 2,  3399.2,     2062,      2,   -895,      5},
    {  3, -2, 0, 2, 0, 1,  1305.5,       46,      0,    -24,      0},
    {  4,  2, 0,-2, 0, 0,  1095.2,       11,      0,      0,      0},
    {  5, -2, 0, 2, 0, 2,  1615.7,       -3,      0,      1,      0},
    {  6,  1,-1, 0,-1, 0,  3232.9,       -3,      0,      0,      0},
    {  7,  0,-2, 2,-2, 1,  6786.3,       -2,      0,      1,      0},
    {  8,  2, 0,-2, 0, 1,   943.2,        1,      0,      0,      0},
    {  9,  0, 0, 2,-2, 2,   182.6,   -13187,    -16,   5736,    -31},
    { 10,  0, 1, 0, 0, 0,   365.3,     1426,    -34,     54,     -1},

    { 11,  0, 1, 2,-2, 2,   121.7,     -517,     12,    224,     -6},
    { 12,  0,-1, 2,-2, 2,   365.2,      217,     -5,    -95,      3},
    { 13,  0, 0, 2,-2, 1,   177.8,      129,      1,    -70,      0},
    { 14,  2, 0, 0,-2, 0,   205.9,       48,      0,      1,      0},
    { 15,  0, 0, 2,-2, 0,   173.3,      -22,      0,      0,      0},
    { 16,  0, 2, 0, 0, 0,   182.6,       17,     -1,      0,      0},
    { 17,  0, 1, 0, 0, 1,   386.0,      -15,      0,      9,      0},
    { 18,  0, 2, 2,-2, 2,    91.3,      -16,      1,      7,      0},
    { 19,  0,-1, 0, 0, 1,   346.6,      -12,      0,      6,      0},
    { 20, -2, 0, 0, 2, 1,   199.8,       -6,      0,      3,      0},

    { 21,  0,-1, 2,-2, 1,   346.6,       -5,      0,      3,      0},
    { 22,  2, 0, 0,-2, 1,   212.3,        4,      0,     -2,      0},
    { 23,  0, 1, 2,-2, 1,   119.6,        4,      0,     -2,      0},
    { 24,  1, 0, 0,-1, 0,   411.8,       -4,      0,      0,      0},
    { 25,  2, 1, 0,-2, 0,   131.7,        1,      0,      0,      0},
    { 26,  0, 0,-2, 2, 1,   169.0,        1,      0,      0,      0},
    { 27,  0, 1,-2, 2, 0,   329.8,       -1,      0,      0,      0},
    { 28,  0, 1, 0, 0, 2,   409.2,        1,      0,      0,      0},
    { 29, -1, 0, 0, 1, 1,   388.3,        1,      0,      0,      0},
    { 30,  0, 1, 2,-2, 0,   117.5,       -1,      0,      0,      0},

    { 31,  0, 0, 2, 0, 2,    13.7,    -2274,     -2,    977,     -5},
    { 32,  1, 0, 0, 0, 0,    27.6,      712,      1,     -7,      0},
    { 33,  0, 0, 2, 0, 1,    13.6,     -386,     -4,    200,      0},
    { 34,  1, 0, 2, 0, 2,     9.1,     -301,      0,    129,     -1},
    { 35,  1, 0, 0,-2, 0,    31.8,     -158,      0,     -1,      0},
    { 36, -1, 0, 2, 0, 2,    27.1,      123,      0,    -53,      0},
    { 37,  0, 0, 0, 2, 0,    14.8,       63,      0,     -2,      0},
    { 38,  1, 0, 0, 0, 1,    27.7,       63,      1,    -33,      0},
    { 39, -1, 0, 0, 0, 1,    27.4,      -58,      1,     32,      0},
    { 40, -1, 0, 2, 2, 2,     9.6,      -59,      0,     26,      0},

    { 41,  1, 0, 2, 0, 1,     9.1,      -51,      0,     27,      0},
    { 42,  0, 0, 2, 2, 2,     7.1,      -38,      0,     16,      0},
    { 43,  2, 0, 0, 0, 0,    13.8,       29,      0,     -1,      0},
    { 44,  1, 0, 2,-2, 2,    23.9,       29,      0,    -12,      0},
    { 45,  2, 0, 2, 0, 2,     6.9,      -31,      0,     13,      0},
    { 46,  0, 0, 2, 0, 0,    13.6,       26,      0,     -1,      0},
    { 47, -1, 0, 2, 0, 1,    27.0,       21,      0,    -10,      0},
    { 48, -1, 0, 0, 2, 1,    32.0,       16,      0,     -8,      0},
    { 49,  1, 0, 0,-2, 1,    31.7,      -13,      0,      7,      0},
    { 50, -1, 0, 2, 2, 1,     9.5,      -10,      0,      5,      0},

    { 51,  1, 1, 0,-2, 0,    34.8,       -7,      0,      0,      0},
    { 52,  0, 1, 2, 0, 2,    13.2,        7,      0,     -3,      0},
    { 53,  0,-1, 2, 0, 2,    14.2,       -7,      0,      3,      0},
    { 54,  1, 0, 2, 2, 2,     5.6,       -8,      0,      3,      0},
    { 55,  1, 0, 0, 2, 0,     9.6,        6,      0,      0,      0},
    { 56,  2, 0, 2,-2, 2,    12.8,        6,      0,     -3,      0},
    { 57,  0, 0, 0, 2, 1,    14.8,       -6,      0,      3,      0},
    { 58,  0, 0, 2, 2, 1,     7.1,       -7,      0,      3,      0},
    { 59,  1, 0, 2,-2, 1,    23.9,        6,      0,     -3,      0},
    { 60,  0, 0, 0,-2, 1,    14.7,       -5,      0,      3,      0},

    { 61,  1,-1, 0, 0, 0,    29.8,        5,      0,      0,      0},
    { 62,  2, 0, 2, 0, 1,     6.9,       -5,      0,      3,      0},
    { 63,  0, 1, 0,-2, 0,    15.4,       -4,      0,      0,      0},
    { 64,  1, 0,-2, 0, 0,    26.9,        4,      0,      0,      0},
    { 65,  0, 0, 0, 1, 0,    29.5,       -4,      0,      0,      0},
    { 66,  1, 1, 0, 0, 0,    25.6,       -3,      0,      0,      0},
    { 67,  1, 0, 2, 0, 0,     9.1,        3,      0,      0,      0},
    { 68,  1,-1, 2, 0, 2,     9.4,       -3,      0,      1,      0},
    { 69, -1,-1, 2, 2, 2,     9.8,       -3,      0,      1,      0},
    { 70, -2, 0, 0, 0, 1,    13.7,       -2,      0,      1,      0},

    { 71,  3, 0, 2, 0, 2,     5.5,       -3,      0,      1,      0},
    { 72,  0,-1, 2, 2, 2,     7.2,       -3,      0,      1,      0},
    { 73,  1, 1, 2, 0, 2,     8.9,        2,      0,     -1,      0},
    { 74, -1, 0, 2,-2, 1,    32.6,       -2,      0,      1,      0},
    { 75,  2, 0, 0, 0, 1,    13.8,        2,      0,     -1,      0},
    { 76,  1, 0, 0, 0, 2,    27.8,       -2,      0,      1,      0},
    { 77,  3, 0, 0, 0, 0,     9.2,        2,      0,      0,      0},
    { 78,  0, 0, 2, 1, 2,     9.3,        2,      0,     -1,      0},
    { 79, -1, 0, 0, 0, 2,    27.3,        1,      0,     -1,      0},

    { 80,  1, 0, 0,-4, 0,    10.1,       -1,      0,      0,      0},
    { 81, -2, 0, 2, 2, 2,    14.6,        1,      0,     -1,      0},
    { 82, -1, 0, 2, 4, 2,     5.8,       -2,      0,      1,      0},
    { 83,  2, 0, 0,-4, 0,    15.9,       -1,      0,      0,      0},
    { 84,  1, 1, 2,-2, 2,    22.5,        1,      0,     -1,      0},
    { 85,  1, 0, 2, 2, 1,     5.6,       -1,      0,      1,      0},
    { 86, -2, 0, 2, 4, 2,     7.3,       -1,      0,      1,      0},
    { 87, -1, 0, 4, 0, 2,     9.1,        1,      0,      0,      0},
    { 88,  1,-1, 0,-2, 0,    29.3,        1,      0,      0,      0},
    { 89,  2, 0, 2,-2, 1,    12.8,        1,      0,     -1,      0},
    { 90,  2, 0, 2, 2, 2,     4.7,       -1,      0,      0,      0},

    { 91,  1, 0, 0, 2, 1,     9.6,       -1,      0,      0,      0},
    { 92,  0, 0, 4,-2, 2,    12.7,        1,      0,      0,      0},
    { 93,  3, 0, 2,-2, 2,     8.7,        1,      0,      0,      0},
    { 94,  1, 0, 2,-2, 0,    23.8,       -1,      0,      0,      0},
    { 95,  0, 1, 2, 0, 1,    13.1,        1,      0,      0,      0},
    { 96, -1,-1, 0, 2, 1,    35.0,        1,      0,      0,      0},
    { 97,  0, 0,-2, 0, 1,    13.6,       -1,      0,      0,      0},
    { 98,  0, 0, 2,-1, 2,    25.4,       -1,      0,      0,      0},
    { 99,  0, 1, 0, 2, 0,    14.2,       -1,      0,      0,      0},
    {100,  1, 0,-2,-2, 0,     9.5,       -1,      0,      0,      0},

    {101,  0,-1, 2, 0, 1,    14.2,       -1,      0,      0,      0},
    {102,  1, 1, 0,-2, 1,    34.7,       -1,      0,      0,      0},
    {103,  1, 0,-2, 2, 0,    32.8,       -1,      0,      0,      0},
    {104,  2, 0, 0, 2, 0,     7.1,        1,      0,      0,      0},
    {105,  0, 0, 2, 4, 2,     4.8,       -1,      0,      0,      0},
    {106,  0, 1, 0, 1, 0,    27.3,        1,      0,      0,      0}
  };
}
