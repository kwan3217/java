package org.kwansystems.space.ephemeris.uranus;

import org.kwansystems.space.ephemeris.*;
import org.kwansystems.tools.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeScale;
import org.kwansystems.tools.time.TimeUnits;
import org.kwansystems.tools.vector.*;

import java.io.*;

import static org.kwansystems.space.Constants.*;

public class UranusSatGUST86 extends Ephemeris {
  private int SatIndex;
  public UranusSatGUST86(int LSatIndex) {
    SatIndex=LSatIndex;
    TargetNumber=701+LSatIndex;
    TargetName=satNames[LSatIndex+1];
    CenterNumber=799;
    CenterName="Uranus";
    TheoryName="GUST87";
  }
  public MathState CalcState(Time TT) {
    return CalcState(TT.get(TimeUnits.Days,TimeScale.TDB));
  }
  public MathState CalcState(double JDTDB) {
    return gust86(JDTDB,SatIndex);
  }
  public static final Ephemeris[] satArray;
  public static final double[] satMassRatio=new double[] {
    5794554.5e9,      //This constant is combined mass of Uranus and all satellites (gmsu in original code). Code below changes it to just planet mass.
    86.1e9,
    84.0e9,
    230.0e9,
    200.0e9,
    4.4e9
  };

  public static final String[] satNames=new String[] {
    "Uranus",
    "Ariel",
    "Umbriel",
    "Titania",
    "Oberon",
    "Miranda"
  };
  
  static {
    satArray=new Ephemeris[] {
      null,
      new UranusSatGUST86(0),
      new UranusSatGUST86(1),
      new UranusSatGUST86(2),
      new UranusSatGUST86(3),
      new UranusSatGUST86(4)
    };
    
  }
  //c..declare
  
  //      implicit double precision (a-h,o-z)
  //      save
  
  //      dimension pos(3),vel(3),el(6),xu(6),xe(6)
  //      dimension xp(3),xs(3),yp(3),ys(3),w(3),posvel(6),vp(3),vs(3)
  //      dimension fqn(5),fqe(5),fqi(5),phn(5),phe(5),phi(5)
  //      dimension gms(5),rmu(5)
  //      dimension trans(3,3),rmat(3,3)
  //      common/asat/an(5),ae(5),ai(5)
  
  //c..various data statements
  static final double[] fqn={2492952.519e-06,1516148.111e-06,721718.509e-06,466692.120e-06,4445190.550e-06};
  static final double[] fqe={6.217,2.865,2.078,0.386,20.082};
  static final double[] fqi={-6.288,-2.836,-1.843,-0.259,-20.309};
  static final double[] phn={3098046.e-06,2285402.0e-06,856359.0e-06,-915592.0e-06,-238051.0e-06};
  static final double[] phe={2.408974,2.067774,0.735131,0.426767,0.611392};
  static final double[] phi={0.395757,0.589326,1.746237,4.206896,5.702313};
//  static final double[] gms={86.1e9,84.0e9,230.0e9,200.0e9,4.4e9}; //GM of satellites, m and s
//  static final double gmsu=5794554.5e9;         //GM of Uranus and all satellites, m and s
  static final double alfd=76.60666666666667; //RA of Uranus pole, deg
  static final double deld=15.03222222222222; //Dec of Uranus pole, deg
  static final double ua=MPerAU;             //Astronomical unit, m
  static final double vl=299792458;          //Speed of light, m/s
  static final double t1950=2433282.423;        //B1950 epoch, JD
  static final double t2000=2451545.0;          //J2000 epoch, JD
  static final double t0=2444239.5;             //Theory epoch, JD
  static final int maxit=2;
  static final int nul=10;
  //The Standish B1950->J2000 matrix. From GUST86.f, but compares to the
  //last decimal to Standish in GalileanE5.java, now resident in EphConst.java
  static final Rotator rmat=B1950toJ2000Standish;
  static final double epsJ2000=epsJ2000D; //Obliquity of Earth axis, J2000
  
  //c..one time initializations
  static final double pi    = Math.PI;
  static final double dpi   = 2.0*pi;
  static final double dgrad = pi/180.0;
  static final double sej   = 86400.0;
  static final double sej2  = sej*sej;
  static final double anj   = 365.25;
  static double gmu   = satMassRatio[0];  //GM of just Uranus (will be calculated later)
  static final double[] rmu=new double[5]; //Combined GM of Uranus and each satellite
  static {
    //Shave off the mass of each satellite from the mass of the planet
    for (int i=0;i<5;i++) {
      gmu=gmu-satMassRatio[i+1];
    }
    satMassRatio[0]=gmu;
    for (int i=0;i<5;i++) {
      rmu[i]=gmu+satMassRatio[i+1];
    }
    
  }
  static final double alf = alfd*dgrad;
  static final double del = deld*dgrad;
  static final double sa  = Math.sin(alf);
  static final double ca  = Math.cos(alf);
  static final double sd  = Math.sin(del);
  static final double cd  = Math.cos(del);
  static final MathMatrix trans=new MathMatrix(new double[][] {
    { sa,ca*sd,ca*cd},
    {-ca,sa*sd,sa*cd},
    {  0,-cd,  sd   }
  });
  
  static double toint(double A) {
    return Math.floor(Math.abs(A))*(A>0?1:-1);
  }
  
  static double dmod(double A,double P) {
    return A - (toint(A / P) * P);
  }
  
  public static MathState gust86(double tjj,int isat) {
    
/*---- Satellites of Uranus (Laskar 1986, Laskar and Jacobson, 1987)
 *
 *     Preliminary version 0.0 - G. Francou June 88.
 *     Java version 0.1 - Chris Jeppesen 3 Nov 2003.
 *        Original trnslation effort
 *     Java version 0.2 - Chris Jeppesen 5 Dec 2003.
 *        Match up with Fortran output (At least for some sats)
 *        Corrected some table entries. Matches fortran calculation to better
 *        than 1mm for firts 3 satellites
 *     Java version 0.3 - Chris Jeppesen 7 Dec 2003.
 *        Change Miranda from the first object to the last
 *        This involved moving the first elements of the short tables to last,
 *        moving the first subtable of each long table to last,
 *        and reordering the columns of the long tables.
 *
 *     On entry :
 *
 *     tjj    JD TDB
 *
 *     isat   Satellite index
 *            0 Ariel
 *            1 Umbriel
 *            2 Titania
 *            3 Oberon
 *            4 Miranda
 *
 *     Returns :
 *            uranocentric J2000Equ state vector, m and m/s
 */
    
    //calculate the arguments
    double t = tjj - t0;
    double[] a=new double[15];
    for(int i=0;i<5;i++) {
      
      double an = fqn[i]*t+phn[i];
      double ae = fqe[i]*dgrad/anj*t+phe[i];
      double ai = fqi[i]*dgrad/anj*t+phi[i];
      an=dmod(an,dpi);
      ae=dmod(ae,dpi);
      ai=dmod(ai,dpi);
      a[i]=an;
      a[i+5]=ae;
      a[i+10]=ai;
    }
    
    //calculate the ume50 elliptical elements
    double[] el=calcel(t,a,isat);
    double rn=el[0];
    el[0]=Math.pow((rmu[isat]*sej2/(rn*rn)),(1.0/3.0));
    double rl=dmod(el[1],dpi);
    if (rl<0.0) rl=rl+dpi;
    el[1]=rl;
    
    //calculate the rectangular ume50 state vector xu
    MathState state=ellipx(el,rmu[isat]);
    //calculate the rectangular eme50 state vector
    state=trans.transform(state);
    //fk5 solution
    state=rmat.transform(state);
    //J2000Ecl solution
    state=MathMatrix.Rot1d(epsJ2000).transform(state);
    return state;
  }
  
  static final int SIN=1;
  static final int COS=2;
  static double Series(double t,double[] a,int[][] S,int fn) {
    double Ax=0.0;
    for (int i=0;i<S.length;i++) {
      double Amp=S[i][0];
      Amp=Amp*1.0e-8;
      if (Math.abs(S[i][16]+S[i][17])>0) {
        //A line either has constant and linear terms...
        Ax=Ax+Amp*(S[i][16]*t+S[i][17]);
      } else {
        //or periodic terms.
        double Arg=0.0;
        for(int j=1;j<=5;j++) {
          Arg=Arg+S[i][j]*a[j-1]+S[i][j+5]*a[j+4]+S[i][j+10]*a[j+9];
        }
        double q=Amp*(fn==SIN?Math.sin(Arg):Math.cos(Arg));
        Ax=Ax+q;
        System.out.print("");
      }
    }
    return Ax;
  }
  
  static double[] ExpSeries(double t,double[] a,int[][] S) {
    double Ax=0.0;
    double Bx=0.0;
    for (int i=0;i<S.length;i++) {
      double Amp=S[i][0];
      Amp=Amp*1.0e-8;
      if (Math.abs(S[i][16]*t+S[i][17])>0) {
        Ax=Ax+Amp*(S[i][16]*t+S[i][17]);
        Bx=Bx+Amp*(S[i][16]*t+S[i][17]);
      } else {
        double Arg=0.0;
        for(int j=1;j<=5;j++) {
          Arg=Arg+S[i][j]*a[j-1]+S[i][j+5]*a[j+4]+S[i][j+10]*a[j+9];
        }
        double q=Amp*Math.cos(Arg);
        Ax=Ax+q;
        q=Amp*Math.sin(Arg);
        Bx=Bx+q;
      }
    }
    return new double[] {Ax,Bx};
  }
  
  
  static double[] calcel(double t, double[] a,int i) {
/*
 *---- calculate the elliptic elements of a satellite (gust86)
 *---- rn => mean motion (rad/day)
 *---- rl => mean longitude (rad)
 *---- z => k + ih
 *---- zeta => q + ip
 */
    double rn=Series(t,a,RnSeries[i],COS);
    double rl=Series(t,a,RlSeries[i],SIN);
    double[] Result=ExpSeries(t,a,RzSeries[i]);
    double rk=Result[0];
    double rh=Result[1];
    Result=ExpSeries(t,a,RzetaSeries[i]);
    double rq=Result[0];
    double rp=Result[1];
    return new double[] {rn,rl,rk,rh,rq,rp};
  }
  
  static MathState ellipx(double[] ell,double rmu) {
/*
 *---- ellipx  1.1  18 march 1986  j. laskar -----------------------------
 *
 *     calculate the rectangular coordinates (position and velocity) from the
 *     elliptic elements.
 *
 *     ell(6)     : elliptic elements    a: semimajor axis
 *                                       l: longitude moyenne
 *                                       k: exc*cos(long node+ arg peri)
 *                                       h: exc*sin(long node+ arg peri)
 *                                       q: sin(i/2)*cos(long node)
 *                                       p: sin(i/2)*sin(long node)
 *     rmu        : two body gravitational constant
 *                  rmu = g*m1*(1+m2/m1) m1 mass of central body
 *                                       m2 mass of body under consideration
 *     returns
 *     xyz(6)     : (1:3) position and (4:6) velocity
 *
 *     Uses subroutine: keplkh
 *
 *
 *---- declarations -----------------------------------------------------
 */
    //      implicit double precision (a-h,o-y)
    //      save
    //      dimension ell(6),xyz(6),dxyz(6,7)
    //      dimension rot(3,2),drotp(3,2),drotq(3,2)
    //      dimension tx1(2),tx1t(2),dtx1k(2),dtx1h(2),dtx1tk(2),dtx1th(2)
    //      dimension dexy1(2,7),dexy1t(2,7)
/*
 *---- Get the elements
 */
    double ra=ell[0];
    double rl=ell[1];
    double rk=ell[2];
    double rh=ell[3];
    double rq=ell[4];
    double rp=ell[5];
    double rn=Math.sqrt(rmu/Math.pow(ra,3));
    double phi=Math.sqrt(1.0-rk*rk-rh*rh);
    double rki=Math.sqrt(1.0-rq*rq-rp*rp);
    double psi=1.0/(1.0+phi);
/*
 *---- rotation matrix
 */
    double[][] rot=new double[3+1][2+1];
    rot[1][1]=1.0-2.0*rp*rp;
    rot[1][2]=2.0*rp*rq;
    rot[2][1]=2.0*rp*rq;
    rot[2][2]=1.0-2.0*rq*rq;
    rot[3][1]=-2.0*rp*rki;
    rot[3][2]= 2.0*rq*rki;
/*
 *---- calculate the eccentric longitude f
 *---- f = eccentric anomaly e + longitude of periapse omegapi
 */
    double f=keplkh(rl,rk,rh);
    double sf    =Math.sin(f);
    double cf    =Math.cos(f);
    double rlmf  =-rk*sf+rh*cf;
    double umrsa =rk*cf+rh*sf;
    double asr   =1.0/(1.0-umrsa);
    double rna2sr=rn*ra*asr;
/*
 *---- calculate tx1 and tx1t
 */
    double[] tx1=new double[2+1];
    double[] tx1t=new double[2+1];
    tx1[1] =ra*(cf-psi*rh*rlmf-rk);
    tx1[2] =ra*(sf+psi*rk*rlmf-rh);
    tx1t[1]=rna2sr*(-sf+psi*rh*umrsa);
    tx1t[2]=rna2sr*( cf-psi*rk*umrsa);
/*
 *---- calculate xyz
 */
    double[] xyz=new double[6+1];
    for(int i=1;i<=3;i++) {
      xyz[i]  =0.0;
      xyz[i+3]=0.0;
      for(int j=1;j<=2;j++) {
        xyz[i]  =xyz[i]  +rot[i][j]*tx1[j];
        xyz[i+3]=xyz[i+3]+rot[i][j]*tx1t[j];
      }
    }
    return new MathState(xyz[1],xyz[2],xyz[3],xyz[4],xyz[5],xyz[6]);
    //    return new MathState(tx1[1],tx1[2],0,tx1t[1],tx1t[2],0);
  }
  
  static double keplkh(double rl,double rk,double rh) {
/*
 *---- keplkh  1.0  12 decembre 1985 j. laskar --------------------------
 *
 *     Solve the Kepler equation in variables longitude, k, h
 *
 *-----------------------------------------------------------------------
    implicit double precision (a-h,o-y)
    save
 */
    double f=0;
    if (rl==0.0) {
      return f;
    } else {
      int itmax=20;
      double eps=1.0e-16;
      double f0=rl;
      double e0=Math.abs(rl);
      for (int it=1;it<=itmax;it++) {
        int k=0;
        double sf=Math.sin(f0);
        double cf=Math.cos(f0);
        double ff0 =f0-rk*sf+rh*cf-rl;
        double fpf0=1.0-rk*cf-rh*sf;
        double sdir=ff0/fpf0;
        double e=e0+1;
        while(e>e0) {
          f=f0-sdir*Math.pow((0.5),k);
          e=Math.abs(f-f0);
          if (e>e0) {
            k=k+1;
          }
        }
        if (k==0 & e<=eps & ff0<eps) {
          return f;
        } else {
          f0=f;
          e0=e;
        }
      }
    }
    return f;
  }
  
  //      Amp   |      an      |     ae       |    ai        | t| constant
  //            | A  U  T  O  M| A  U  T  O  M| A  U  T  O  M|  |
  static final int[][][] RnSeries={
    {{ 249254257, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
     {      +255,-3, 2, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -4216, 1,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {    -10256, 2,-2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    },
    
    {{ 151595490, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
     {      +974, 0, 1,-2, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {    -10600, 1,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +5416, 2,-2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -2359, 0, 1,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -7070, 0, 2,-2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -3628, 0, 3,-3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    },
    
    {{  72166316, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
     {      -264, 0, 1,-2, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {      -216, 0, 0, 2,-3, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
     {      +645, 0, 0, 2,-3, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {      -111, 0, 0, 2,-3, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -6223, 1, 0,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -5613, 0, 1,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -3994, 0, 0, 1,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -9185, 0, 0, 2,-2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -5831, 0, 0, 3,-3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -3860, 0, 0, 4,-4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -2618, 0, 0, 5,-5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -1806, 0, 0, 6,-6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    },
    
    {{  46658054, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
     {      +208, 0, 0, 2,-3, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
     {      -622, 0, 0, 2,-3, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {      +107, 0, 0, 2,-3, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -4310, 1, 0, 0,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -3894, 0, 1, 0,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -8011, 0, 0, 1,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +5906, 0, 0, 2,-2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +3749, 0, 0, 3,-3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +2482, 0, 0, 4,-4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +1684, 0, 0, 5,-5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    },
    
    {{ 444352267, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
     {     -3492,-3, 2, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {      +847,-6, 4, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {      +131,-9, 6, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -5228,-1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {    -13665,-2, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    }
  };
  static final int[][][] RlSeries={
    {{ 309804641, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
     {+249295252, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
     {   -186050,-3, 2, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {    +21999,-6, 4, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +2310,-9, 6, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {      +430,-12,8, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -9011, 1,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -9107, 2,-2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -4275, 3,-3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -1649, 2, 0,-2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    },
    
    {{ 228540169, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
     {+151614811, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
     {    +66057,-3, 2, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -7651,-6, 4, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {      -896,-9, 6, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {      -253,-12,8, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -5291, 0, 1,-4, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {      -734, 0, 1,-2, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
     {      -183, 0, 1,-2, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {    +14791, 0, 1,-2, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {      -777, 0, 1,-2, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +9776, 1,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +7313, 2,-2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +3471, 3,-3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +1889, 4,-4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -6789, 0, 1,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -8286, 0, 2,-2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -3381, 0, 3,-3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -1579, 0, 4,-4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -1021, 0, 1, 0,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -1708, 0, 2, 0,-2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    },
    
    {{  85635879, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
     { +72171851, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
     {     +2061, 0, 1,-4, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {      -207, 0, 1,-2, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
     {      -288, 0, 1,-2, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -4079, 0, 1,-2, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {      +211, 0, 1,-2, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -5183, 0, 0, 2,-3, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
     {    +15987, 0, 0, 2,-3, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -3505, 0, 0, 2,-3, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {      -156, 0, 0, 3,-4, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +4054, 1, 0,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +4617, 0, 1,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {    -31776, 0, 0, 1,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {    -30559, 0, 0, 2,-2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {    -14836, 0, 0, 3,-3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -8292, 0, 0, 4,-4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -4998, 0, 0, 5,-5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -3156, 0, 0, 6,-6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -2056, 0, 0, 7,-7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -1369, 0, 0, 8,-8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    },
    
    {{ -91559180, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
     { +46669212, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
     {      -782, 0, 1,-4, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +5129, 0, 0, 2,-3, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
     {    -15824, 0, 0, 2,-3, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +3451, 0, 0, 2,-3, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +4751, 1, 0, 0,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +3896, 0, 1, 0,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {    +35973, 0, 0, 1,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     28278, 0, 0, 2,-2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {    +13860, 0, 0, 3,-3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +7803, 0, 0, 4,-4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +4729, 0, 0, 5,-5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +3000, 0, 0, 6,-6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +1962, 0, 0, 7,-7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +1311, 0, 0, 8,-8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    },
    
    {{ -23805158, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
     {+444519055, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
     {  +2547217,-3, 2, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {   -308831,-6, 4, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {    -31810,-9, 6, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -3749,-12,8, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -5785,-1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -6232,-2, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -2795,-3, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    }
  };
  static final int[][][] RzSeries={
    {{      -335, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
     {   +118763, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {    +86159, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +7150, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +5559, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -8460,-1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +9181,-2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +2003,-1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +8977, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    },
    
    {{       -21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
     {    -22795, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {   +390469, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {    +30917, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {    +22192, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +2934, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +2620, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +5119,-1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {    -10386,-2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -2716,-3, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -1622, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {    +54923, 0,-1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +3470, 0,-2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +1281, 0,-3, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +2181, 0,-1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +4625, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    },
    
    {{        -2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
     {      -129, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {    -32451, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {    +93281, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {   +112089, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +3386, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +1746, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +1658,-1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +2889, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -3586, 0,-1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -1786, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -3210, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {    -17783, 0, 0,-1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {    +79343, 0, 0,-2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +9948, 0, 0,-3, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +4483, 0, 0,-4, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +2513, 0, 0,-5, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +1543, 0, 0,-6, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    },
    
    {{         0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
     {       -35, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +7453, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {    -75868, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {   +139734, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +3900, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +1766,-1, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +3242, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +7975, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +7566, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {    +13404, 0, 0,-1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {    -98726, 0, 0,-2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {    -12609, 0, 0,-3, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -5742, 0, 0,-4, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -3241, 0, 0,-5, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -1999, 0, 0,-6, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     -1294, 0, 0,-7, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    },
    
    {{    131238, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
     {     +7181, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +6977, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {      +675, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {      +627, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
     {    -12331, 2, 0, 0, 0,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {     +3952, 3, 0, 0, 0,-2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
     {    +19410, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    },
  };
  static final int[][][] RzetaSeries={
    {{    -12175, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
     {    +35825, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
     {    +29008, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
     {     +9778, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
     {     +3397, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0}
    },
    
    {{     -1086, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
     {     -8151, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
     {   +111336, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
     {    +35014, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
     {    +10650, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0}
    },
    
    {{      -143, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
     {      -106, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
     {    -14013, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
     {    +68572, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
     {    +37832, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0}
    },
    
    {{       -44, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
     {       -31, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
     {     +3689, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
     {    -59633, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
     {    +45169, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0}
    },
    
    {{   3787171, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
     {     +2701, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
     {     +3076, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
     {     +1218, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
     {      +537, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0}
    }
  };
  public static void main(String args[]) throws IOException {
    satArray[1].printTheory(System.out,1.0);
  }
  public MathVector CalcPos(Time TT) {
	return defaultCalcPos(TT);
  }
  public MathVector CalcVel(Time TT) {
	return defaultCalcVel(TT);
  }
}
