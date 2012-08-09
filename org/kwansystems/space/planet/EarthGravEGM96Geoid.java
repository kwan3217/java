package org.kwansystems.space.planet;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import org.kwansystems.tools.Scalar;
import org.kwansystems.tools.cformat.*;
import org.kwansystems.tools.vector.*;

import static java.lang.Math.*;
import static org.kwansystems.space.planet.Spheroid.*;

public class EarthGravEGM96Geoid {
  /*
   * translation of the well-known f477.f to C actually this source is folklore
   * d.ineiev<ineiev@yahoo.co.uk> wrote down and put under zlib/libpng license
   * (see README.txt)
   */
  /*
   * this program is designed for the calculation of a geoid undulation at a
   * point whose latitude and longitude is specified. the program is designed to
   * use the potential coefficient model egm96 and a set of spherical harmonic
   * coefficients of a correction term. the correction term is composed of
   * several different components the primary one being the conversion of a
   * height anomaly to a geoid undulation. the principles of this procedure were
   * initially described in the paper: use of potential coefficient models for
   * geoid undulation determination using a spherical harmonic representation of
   * the height anomaly/geoid undulation difference by R.H. Rapp, Journal of
   * Geodesy, 1996. this program is designed to be used with the constants of
   * egm96 and those of the wgs84(g873) system. the undulation will refer to the
   * wgs84 ellipsoid. specific details on the undulation computation will be
   * found in the joint project report describing the development of egm96. this
   * program is a modification of the program described in the following report:
   * a fortran program for the computation of gravimetric quantities from high
   * degree spherical harmonic expansions, Richard H. Rapp, report 334,
   * Department of Geodetic Science and Surveying, the Ohio State University,
   * Columbus, 1982 this program was put in this form in Dec 1996.
   * rhrapp.f477.nonly
   * 
   * 
   * 
   * ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
   * the input files consist of:
   * 
   * correction coefficient set ("CORRCOEF") => unit = 1 potential coefficient
   * set ("EGM96") => unit = 12 points at which to compute ("INPUT.dat") => unit =
   * 14
   * 
   * the output file is:
   * 
   * computed geoid heights ("OUTF477") => unit = 20
   * 
   * file assignment revisions at NIMA, December 1996.
   * 
   * ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
   * 
   * dimensions of p,q,hc,hs must be at least ((maxn+1)*(maxn+2))/2, dimensions
   * of sinml,cosml must be at least maxn, where maxn is maximum order of
   * computation the current dimensions are set for a maximum degree of 360
   */

  static final int l_value =(65341);
  static final int nmax=360;
  static final double[] drts=new double[1301],dirt=new double[1301];
  private static final String infnCorr="Data/EarthGravEGM96/EGM96_Correction.serial.gz";
  private static final String infnCorrText="Data/EarthGravEGM96/CORCOEF";
  private static final String infnEGM="Data/EarthGravEGM96/EGM96.serial.gz";
  private static final String infnEGMText="Data/EarthGravEGM96/EGM96";
  static final double j2=0.108262982131e-2;
  static final double j4=-.237091120053e-05;
  static final double j6=0.608346498882e-8;
  static final double j8=-0.142681087920e-10;
  static final double j10=0.121439275882e-13;
  static void read_egm_text() throws IOException {
    int n,m;
    double c,s;
    System.out.println("Reading EGM in text mode");
    /*
     * the even degree zonal coefficients given below were computed for the
     * wgs84(g873) system of constants and are identical to those values used in
     * the NIMA gridding procedure. computed using subroutine grs written by
     * N.K. PAVLIS
     */
    m=((nmax+1)*(nmax+2))/2;
    for(n=1;n<=m;n++){
      hc[n]=0;
      hs[n]=0;
    }
    LineNumberReader infEGM=new LineNumberReader(new FileReader(infnEGMText));/*
     * potential coefficient
     * file
     */
    String S=infEGM.readLine();
    ScanfReader R = new ScanfReader(new StringReader(S));
    while(S!=null) {
      n=R.scanInt("%d");
      m=R.scanInt("%d");
      c=R.scanDouble("%f");
      s=R.scanDouble("%f");
      if(n>nmax)continue;
      n=(n*(n+1))/2+m+1;
      hc[n]=c;
      hs[n]=s;
      S=infEGM.readLine();
      if(S!=null)R = new ScanfReader(new StringReader(S));
    }
    infEGM.close();
    hc[4]+=j2/sqrt(5);
    hc[11]+=j4/3;
    hc[22]+=j6/sqrt(13);
    hc[37]+=j8/sqrt(17);
    hc[56]+=j10/sqrt(21);
  }
  private static void write_egm() throws IOException {
    ObjectOutputStream ouf_egm=new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(infnEGM)));
    ouf_egm.writeObject(hc);
    ouf_egm.writeObject(hs);
    ouf_egm.close();
  }
  private static void read_egm() throws IOException, ClassNotFoundException {
    System.out.println("Reading EGM in stream mode");
    ObjectInputStream inf_egm=new ObjectInputStream(new GZIPInputStream(new FileInputStream(infnEGM)));
    hc=(double[])inf_egm.readObject();
    hs=(double[])inf_egm.readObject();
    inf_egm.close();
  }
  private static void read_correction_text() throws IOException {
    System.out.println("Reading corrections in text mode");
    int ig,i,n,m;
    double t1,t2;
    LineNumberReader infCorr=new LineNumberReader(new FileReader(infnCorrText));/*
     * correction
     * coefficient file:
     * modified with 'sed
     * -e"s/D/e/g"' to be
     * read with fscanf
     */
    for(i=1;i<=l_value;i++)cc[i]=cs[i]=0;
    String S=infCorr.readLine();
    ScanfReader R = new ScanfReader(new StringReader(S));
    while(S!=null) {
      n=R.scanInt("%i");
      m=R.scanInt("%i");
      t1=R.scanDouble("%f");
      t2=R.scanDouble("%f");
      ig=(n*(n+1))/2+m+1;
      cc[ig]=t1;
      cs[ig]=t2;
      S=infCorr.readLine();
      if(S!=null)R = new ScanfReader(new StringReader(S));
    }
    infCorr.close();
  }
  private static void write_correction() throws IOException {
    ObjectOutputStream ouf_corr=new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(infnCorr)));
    ouf_corr.writeObject(cc);
    ouf_corr.writeObject(cs);
    ouf_corr.close();
  }
  private static void read_correction() throws IOException, ClassNotFoundException {
    System.out.println("Reading corrections in stream mode");
    ObjectInputStream inf_corr=new ObjectInputStream(new GZIPInputStream(new FileInputStream(infnCorr)));
    cc=(double[])inf_corr.readObject();
    cs=(double[])inf_corr.readObject();
    inf_corr.close();
  }
  static {
    for(int n=1;n<=2*nmax+1;n++){
      drts[n]=sqrt(n);
      dirt[n]=1/drts[n];
    }
  }
  /**
   * Calculates geoid height undulation.
   * 
   * @param nmax
   * @param p
   * @param hc
   * @param hs
   * @param sinml
   * @param cosml
   * @param gr
   * @param re
   * @param cc
   * @param cs
   * @return Geoid height undulation, m. Positive if geoid is farther from
   *         center of Earth than ellipsoid at this point, negative otherwise.
   */
  private static double hundu(int nmax,double[] p,
      double[] hc,double[] hs,
      double[] sinml,double[] cosml,double gr,double re,
      double[] cc,double[] cs) {
    /* constants for wgs84(g873);gm in units of m**3/s**2 */
    final double gm=WGS84.GM,ae=WGS84.Re;
    double arn,ar,ac,a,b,sum,sumc,tempc,temp;
    int k,n,m;
    ar=ae/re;arn=ar;ac=a=b=0;k=3;
    for(n=2;n<=nmax;n++) {
      arn*=ar;
      k++;
      sum=p[k]*hc[k];
      sumc=p[k]*cc[k];
      for(m=1;m<=n;m++) {
        k++;tempc=cc[k]*cosml[m]+cs[k]*sinml[m];
        temp=hc[k]*cosml[m]+hs[k]*sinml[m];
        sumc+=p[k]*tempc;
        sum+=p[k]*temp;
      }
      ac+=sumc;
      a+=sum*arn;
    }
    ac+=cc[1]+p[2]*cc[2]+p[3]*(cc[3]*cosml[1]+cs[3]*sinml[1]);
    /*
     * add haco=ac/100 to convert height anomaly on the ellipsoid to the
     * undulation add -0.53m to make undulation refer to the wgs84 ellipsoid.
     */
    return a*gm/(gr*re)+ac/100-.53;
  }
  static void dscml(double rlon,int nmax,double[] sinml,double[] cosml) {
    double a,b;
    int m;
    a=sin(rlon);
    b=cos(rlon);
    sinml[1]=a;
    cosml[1]=b;
    sinml[2]=2*b*a;
    cosml[2]=2*b*b-1;
    for(m=3;m<=nmax;m++) {
      sinml[m]=2*b*sinml[m-1]-sinml[m-2];
      cosml[m]=2*b*cosml[m-1]-cosml[m-2];
    }
  }
  /**  Computes all normalized legendre function in "rleg".
     * order is always m, and colatitude is always theta (radians). All 
     * calculations in double precision. The dimensions of arrays rleg
     * must be at least equal to nmx+1. Original programmer :Oscar L. Colombo,
     * Dept. of Geodetic Science the Ohio State University, August 1980
     * ineiev: I removed the derivatives, for they are never computed here

   * @param m Order
   * @param theta Colatitude, radians
   * @param rleg normalized legendre function of different degrees
   */
  static void legfdn(int m,double theta,double[] rleg) {
    /*
     */
    int m1=m+1,m2=m+2,m3=m+3,n,n1,n2;
    double cothet,sithet;
    double[] rlnn=new double[362];
    cothet=cos(theta);
    sithet=sin(theta);
    /* compute the legendre functions */
    rlnn[1]=1;
    rlnn[2]=sithet*drts[3];
    for(n1=3;n1<=m1;n1++) {
      n=n1-1;
      n2=2*n;
      rlnn[n1]=drts[n2+1]*dirt[n2]*sithet*rlnn[n];
    }
    switch(m) {
      case 1:
        rleg[2]=rlnn[2];
        rleg[3]=drts[5]*cothet*rleg[2];
        break;
      case 0:
        rleg[1]=1;
        rleg[2]=cothet*drts[3];
        break;
    }
    rleg[m1]=rlnn[m1];
    if(m2<=nmax+1) {
      rleg[m2]=drts[m1*2+1]*cothet*rleg[m1];
      if(m3<=nmax+1)for(n1=m3;n1<=nmax+1;n1++) {
        n=n1-1;
        if((m==0&&n<2)||(m==1&&n<3))continue;
        n2=2*n;
        rleg[n1]=drts[n2+1]*dirt[n+m]*dirt[n-m]*(drts[n2-1]*cothet*rleg[n1-1]-drts[n+m-1]*drts[n-m-1]*dirt[n2-3]*rleg[n1-2]);
      }
    }
  }
  static void radgra(double lat,double lon,double[] rlat,double[] gr,double[] re) {
    /*
     * this subroutine computes geocentric distance to the point, the geocentric
     * latitude,and an approximate value of normal gravity at the point based
     * the constants of the wgs84(g873) system are used
     */
    final double a=6378137.,e2=.00669437999013,geqt=9.7803253359,k=.00193185265246;
    double n,t1=sin(lat)*sin(lat),t2,x,y,z;
    n=a/sqrt(1-e2*t1);
    t2=n*cos(lat);
    x=t2*cos(lon);
    y=t2*sin(lon);
    z=(n*(1-e2))*sin(lat);
    re[0]=sqrt(x*x+y*y+z*z);/* compute the geocentric radius */
    rlat[0]=atan(z/sqrt(x*x+y*y));/* compute the geocentric latitude */
    gr[0]=geqt*(1+k*t1)/sqrt(1-e2*t1);/* compute normal gravity:units are m/sec**2 */
  }
  static double[] cc=new double[l_value+1],cs=new double[l_value+1],hc=new double[l_value+1],hs=new double[l_value+1];
  static double[] p=new double[l_value+1],sinml=new double[362],cosml=new double[362],rleg=new double[362];
  static double undulation(double lat,double lon,int nmax,int k) {
    double[] rlat=new double[1],gr=new double[1],re=new double[1];
    int i,j,m;
    radgra(lat,lon,rlat,gr,re);
    rlat[0]=PI/2-rlat[0];
    for(j=1;j<=k;j++) {
      m=j-1;
      legfdn(m,rlat[0],rleg);
      for(i=j;i<=k;i++) p[(i-1)*i/2+m+1]=rleg[i];
    }
    dscml(lon,nmax,sinml,cosml);
    return hundu(nmax,p,hc,hs,sinml,cosml,gr[0],re[0],cc,cs);
  }
  static void init_arrays_text() throws IOException {
    /* the correction coefficients are now read in */
    /*
     * the potential coefficients are now read in and the reference even
     * degree zonal harmonic coefficients removed to degree 6
     */
    read_correction_text();
    read_egm_text();
  }
  static void init_arrays() throws IOException, ClassNotFoundException {
    if(new File(infnCorr).exists()) {
      read_correction();
    } else {
      read_correction_text();
      write_correction();
    }
    if(new File(infnEGM).exists()) {
      read_egm();
    } else {
      read_egm_text();
      write_egm();
    }
  }
  public static double undulation(double lat, double lon) {
    return undulation(lat,lon,nmax,nmax+1);
  }
  public static void main(String[] args) throws IOException, ClassNotFoundException {
    double[] flat=new double[] {38.6281550,-14.6212170,46.8743190,-23.6174460,38.6254730,-0.4667440};
    double[] flon=new double[] {269.779155,305.021114 ,102.448729,133.874712 ,359.999500 ,0.002300};
    double[] u=new double[43200];
    ObjectOutputStream ouf_u=new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream("EGM96.grid.serial.gz")));
    System.out.println("Reading EGM");
    init_arrays();
    System.out.println("Done reading EGM");
    /* read geodetic latitude,longitude at point undulation is wanted */
    for(int i=0;i<flat.length;i++) {
      /* compute the geocentric latitude,geocentric radius,normal gravity */
      u[i]=undulation(toRadians(flat[i]),toRadians(flon[i]));
      /*
       * u is the geoid undulation from the egm96 potential coefficient model
       * including the height anomaly to geoid undulation correction term and a
       * correction term to have the undulations refer to the wgs84 ellipsoid.
       * the geoid undulation unit is meters.
       */
      System.out.printf("%14.7f %14.7f %10.7f\n",flat[i],flon[i],u[i]);
    }
    long T0=new Date().getTime();
    for(int i=0;i<21601;i++) {
      double lat=Scalar.linterp(0, PI/2, 720, -PI/2, i);
      System.out.println(i);
      for(int j=0;j<43200;j++) {
        double lon=Scalar.linterp(0, -PI, 1440, PI, j);
        u[j]=undulation(lat,lon);
      }
      ouf_u.writeObject(u);
      long ncalc=(i+1)*u.length;
      long tn=new Date().getTime()-T0;
      System.out.printf("%d points in %d ms, %f points/s\n",ncalc,tn,1000.0*ncalc/tn);
    }
    ouf_u.close();
  }
}
