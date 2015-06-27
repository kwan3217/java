package org.kwansystems.space.planet.magnetosphere;

import java.io.*;
import static java.lang.Math.*;

public class EarthMagWMM implements Magnetosphere {
/*
     Contact Information

     Software and Model Support
     	National Geophysical Data Center
     	NOAA EGC/2
     	325 Broadway
     	Boulder, CO 80303 USA
     	Attn: Susan McLean or Stefan Maus
     	Phone:  (303) 497-6478 or -6522
     	Email:  Susan.McLean@noaa.gov or Stefan.Maus@noaa.gov
		Web: http://www.ngdc.noaa.gov/seg/WMM/

     Sponsoring Government Agency
	   National Geospatial-Intelligence Agency
    	   PRG / CSAT, M.S. L-41
    	   3838 Vogel Road
    	   Arnold, MO 63010
    	   Attn: Craig Rollins
    	   Phone:  (314) 263-4186
    	   Email:  Craig.M.Rollins@Nga.Mil

      Original Program By:
        Dr. John Quinn
        FLEET PRODUCTS DIVISION, CODE N342
        NAVAL OCEANOGRAPHIC OFFICE (NAVOCEANO)
        STENNIS SPACE CENTER (SSC), MS 39522-5001
      Translated to Java by
        Chris Jeppesen
        Kwan Systems
        Superior, CO 80027
        1 May 2009

*/
  public static void main(String[] args) throws IOException {
    boolean warn_H,warn_H_strong,warn_P;
    int maxdeg;
    double altm, alt, time, dlat, dlon;
    char answer, ans;
    String decd,dipd,modl;
    String goodbye="\n -- End of WMM Point Calculation Program -- \n\n";
    double epochuplim;
    double epochrange = 5.0;
    double warn_H_val, warn_H_strong_val;
    LineNumberReader wmmtemp;
    EarthMagWMM E=new EarthMagWMM(2005);
    E.testFile("Data/EarthMagWMM2005/WMM2006_TestValues.txt");

    boolean done=false;

    while(!done) {

      warn_H = false;
      warn_H_val = 99999.0;
      warn_H_strong = false;
      warn_H_strong_val = 99999.0;
      warn_P = false;

      BufferedReader Br = new BufferedReader(new InputStreamReader(System.in));

      System.out.printf("\n\n\nENTER LATITUDE IN DECIMAL DEGREES ");
      System.out.printf("\n(North latitude positive, South latitude negative \n");
      System.out.printf("i.e. 25.5 for 25 degrees 30 minutes north.) \n");
      dlat=Double.parseDouble(Br.readLine());

      System.out.printf("ENTER LONGITUDE IN DECIMAL DEGREES");
      System.out.printf("(East longitude positive, West negative \n");
      System.out.printf("i.e.- 100.0 for 100.0 degrees west.)\n");
      dlon=Double.parseDouble(Br.readLine());

      System.out.printf("ENTER ALTITUDE IN METERS ABOVE MEAN SEA LEVEL (WGS84)\n");
      altm=Double.parseDouble(Br.readLine());
      alt = altm/1000;

      System.out.printf("ENTER TIME IN DECIMAL YEAR (%-7.2f - %-7.2f)\n",E.getEpoch(),E.getEpoch()+5);
      time=Double.parseDouble(Br.readLine());
      FieldProperties[] result=E.calcPropRates(alt,toRadians(dlat),toRadians(dlon),time);
      if (result[0].dec < 0.0) {
  	    decd="(WEST)";
      } else {
        decd="(EAST)";
      }

      if (result[0].dip < 0.0) {
        dipd="(UP)  ";
      } else {
        dipd="(DOWN)";
      }

      /* deal with geographic and magnetic poles */

      if (result[0].h < 1000.0) {
        warn_H = false;
        warn_H_strong = true;
        warn_H_strong_val = result[0].h;
      } else if (result[0].h < 5000.0 && !warn_H_strong) {
        warn_H = true;
        warn_H_val = result[0].h;
      }

      if (90.0-abs(dlat) <= 0.001) {
        warn_P = true;
      }

      System.out.printf("\n Results For \n");
      if (dlat < 0)
        System.out.printf("\n LATITUDE:     %7.2fS",-dlat);
      else
        System.out.printf("\n LATITUDE:     %7.2fN",dlat);
      if (dlon < 0)
        System.out.printf("\n LONGITUDE:    %7.2fW",-dlon);
      else
        System.out.printf("\n LONGITUDE:    %7.2fE",dlon);

      System.out.printf("\n ALTITUDE:    %8.2f METERS AMSL (WGS84)",altm);
      System.out.printf("\n DATE:         %6.1f\n",time);


      System.out.printf("\n     Main Field                   Secular Change");

      System.out.printf("\n F      =    %9.1f nT            dF  = %8.1f nT/yr",result[0].ti,result[1].ti);
      System.out.printf("\n H      =    %9.1f nT            dH  = %8.1f nT/yr",result[0].h,result[1].h);
      System.out.printf("\n X      =    %9.1f nT            dX  = %8.1f nT/yr ",result[0].x,result[1].x);
      System.out.printf("\n Y      =    %9.1f nT            dY  = %8.1f nT/yr ",result[0].y,result[1].y);
      System.out.printf("\n Z      =    %9.1f nT            dZ  = %8.1f nT/yr ",result[0].z,result[1].z);
      System.out.printf("\n D      =    %9.1f Deg %s    dD  = %8.1f Min/yr",toDegrees(result[0].dec),decd,toDegrees(result[1].dec)*60);
      System.out.printf("\n I      =    %9.1f Deg %s    dI  = %8.1f Min/yr",toDegrees(result[0].dip),dipd,toDegrees(result[1].dip)*60);

      if (warn_H) {
        System.out.printf("\n\nWarning: The horizontal field strength at this location is only %6.1f nT\n",warn_H_val);
        System.out.printf("         Compass readings have large uncertainties in areas where H is\n");
        System.out.printf("         smaller than 5000 nT\n");
      }
      if (warn_H_strong) {
        System.out.printf("\n\nWarning: The horizontal field strength at this location is only %6.1f nT\n",warn_H_strong_val);
        System.out.printf("         Compass readings have VERY LARGE uncertainties in areas where H is\n");
        System.out.printf("         smaller than 1000 nT\n");
      }
      if (warn_P) {
        System.out.printf("\n\nWarning: Location is at geographic pole where X, Y, and Decl are undefined\n");
      }

      System.out.printf("\n\nDO YOU NEED MORE POINT DATA? (y or n) ");
      answer=Br.readLine().charAt(0);

      if ((answer =='y')||(answer == 'Y')) done=false;
      else {
        System.out.printf("%s",goodbye);
        done=true;
      }
    }
  }

  private int maxord;
  private final int epoch;
  private double[][] c=new double[13][13];
  private double[][] cd=new double[13][13];
  private double[][] tc=new double[13][13];
  private double[][] dp=new double[13][13];
  private double[] snorm=new double[169];
  private double[] sp=new double[13];
  private double[] cp=new double[13];
  private double[] fn=new double[13];
  private double[] fm=new double[13];
  private double[] pp=new double[13];
  private double[][] k=new double[13][13];
  private static final double a=6378.137;
  private static final double b=6356.7523142;
  private static final double re=6371.2;
  private static final double a2=a*a;
  private static final double b2=b*b;
  private static final double c2=a2-b2;
  private static final double a4=a2*a2;
  private static final double b4=b2*b2;
  private static final double c4=a4-b4;
  public EarthMagWMM(int Lepoch) throws IOException {
/* INITIALIZE CONSTANTS */
    sp[0] = 0.0;
    cp[0] = 1.0;
    snorm[0]=1.0;
    pp[0] = 1.0;
    dp[0][0] = 0.0;
    epoch=Lepoch;
    geomag();
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
        FieldProperties[] f=calcPropRates(alt,lat,lon,year);
        System.out.printf("Calc: %9.1f  %9.1f %9.1f %9.1f %9.1f %9.1f %9.1f %9.1f %9.1f\n",
              toDegrees(lat),toDegrees(lon),
              toDegrees(f[0].dec),toDegrees(f[0].dip),
              f[0].h,f[0].x,f[0].y,f[0].z,f[0].ti
              );
        System.out.printf("File:                      %9.1f %9.1f %9.1f %9.1f %9.1f %9.1f %9.1f\n",
              toDegrees(D),toDegrees(I),
              H,X,Y,Z,F
              );
        System.out.printf("Diff:                      %9.1f %9.1f %9.1f %9.1f %9.1f %9.1f %9.1f\n",
              toDegrees(f[0].dec-D),toDegrees(f[0].dip-I),
              f[0].h-H,f[0].x-X,f[0].y-Y,f[0].z-Z,f[0].ti-F
              );
        System.out.printf("Rate:                      %9.1f %9.1f %9.1f %9.1f %9.1f %9.1f %9.1f\n",
              toDegrees(f[1].dec)*60,toDegrees(f[1].dip)*60,
              f[1].h,f[1].x,f[1].y,f[1].z,f[1].ti
              );
        System.out.printf("File:                      %9.1f %9.1f %9.1f %9.1f %9.1f %9.1f %9.1f\n",
              toDegrees(dD)*60,toDegrees(dI)*60,
              dH,dX,dY,dZ,dF
              );
        System.out.printf("Diff:                      %9.1f %9.1f %9.1f %9.1f %9.1f %9.1f %9.1f\n",
              toDegrees(f[1].dec-dD)*60,toDegrees(f[1].dip-dI)*60,
              f[1].h-dH,f[1].x-dX,f[1].y-dY,f[1].z-dZ,f[1].ti-dF
              );
      } else {
        return;
      }
    }
  }
  public int readWMMCOF(LineNumberReader wmmdat) throws IOException {
    wmmdat.readLine();

    String c_str;
    String c_new;
    int m,n=0;
    while(true) {
      c_str=wmmdat.readLine();
      c_new=c_str.substring(0,4);
      if (c_new.compareTo("9999") == 0) break;
      /* END OF FILE NOT ENCOUNTERED, GET VALUES */
      String[] parts=c_str.split("\\s+");
      n=Integer.parseInt(parts[1]);
      m=Integer.parseInt(parts[2]);
      double gnm=Double.parseDouble(parts[3]);
      double hnm=Double.parseDouble(parts[4]);
      double dgnm=Double.parseDouble(parts[5]);
      double dhnm=Double.parseDouble(parts[6]);
      //Trying to be clever here by storing two triangular arrays in one square array.
      //I hate this kind of clever. To save time, maybe. To save memory at the expense
      //of time and clarity? never.
      //Jackson's rules of optimization:
      //  Rule 1: Don't do it!
      //  Rule 2 (for advanced programmers only): Don't do it yet.
      if (m <= n) {
        c[m][n] = gnm;
        cd[m][n] = dgnm;
        if (m != 0) {
          c[n][m-1] = hnm;
          cd[n][m-1] = dhnm;
        }
      }
    }
    return n;
  }

  private void geomag() throws IOException {

    LineNumberReader wmmdat = new LineNumberReader(
       new FileReader(String.format("Data/EarthMagWMM%04d/WMM.COF",(int)epoch)));


/* READ WORLD MAGNETIC MODEL SPHERICAL HARMONIC COEFFICIENTS */
    c[0][0] = 0.0;
    cd[0][0] = 0.0;
    maxord=readWMMCOF(wmmdat);

/* CONVERT SCHMIDT NORMALIZED GAUSS COEFFICIENTS TO UNNORMALIZED */
    snorm[0] = 1.0;
    for (int n=1; n<=maxord; n++) {
      snorm[n] = snorm[n-1]*(double)(2*n-1)/(double)n;
      int j = 2;
      //m is zero during initialization, D1 never changes, so this simplifies to
      //for(int m=0;m<=n;m++) {
      for(int m=0,D1=1,D2=(n-m+D1)/D1; D2>0; D2--,m+=D1) {
        k[m][n] = (double)(((n-1)*(n-1))-(m*m))/(double)((2*n-1)*(2*n-3));
        if (m > 0) {
          double flnmj = (double)((n-m+1)*j)/(double)(n+m);
          snorm[n+m*13] = snorm[n+(m-1)*13]*sqrt(flnmj);
          j = 1;
          //H coefficient
          c[n][m-1] = snorm[n+m*13]*c[n][m-1];
          cd[n][m-1] = snorm[n+m*13]*cd[n][m-1];
        }
        //G coefficient
        c[m][n] = snorm[n+m*13]*c[m][n];
        cd[m][n] = snorm[n+m*13]*cd[m][n];
      }
      fn[n] = (double)(n+1);
      fm[n] = (double)n;
    }
    k[1][1] = 0.0;

    wmmdat.close();
    return;
  }

  public double[] geomg1(double alt, double rlat, double rlon, double time) {
    double q,q1,q2,ct,st,r2,r,d,ca,sa,aor,ar,br,bt,bp,bpp,
    par,temp1,temp2,parp,bx,by,bz,bh;
    //return is array of [dec, dip, ti, gv]
    double dec,dip,ti;
    double dt = time - epoch;

    double srlon = sin(rlon);
    double srlat = sin(rlat);
    double crlon = cos(rlon);
    double crlat = cos(rlat);
    double srlat2 = srlat*srlat;
    double crlat2 = crlat*crlat;
    sp[1] = srlon;
    cp[1] = crlon;

/* CONVERT FROM GEODETIC COORDS. TO SPHERICAL COORDS. */
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
    for (int m=2; m<=maxord; m++) {
      sp[m] = sp[1]*cp[m-1]+cp[1]*sp[m-1];
      cp[m] = cp[1]*cp[m-1]-sp[1]*sp[m-1];
    }
    aor = re/r;
    ar = aor*aor;
    br = bt = bp = bpp = 0.0;
    for (int n=1; n<=maxord; n++) {
      ar = ar*aor;
      //m is zero during initialization, D3 never changes, so this simplifies to
      //for(int m=0;m<=n;m++) {
      for (int m=0,D3=1,D4=(n+m+D3)/D3; D4>0; D4--,m+=D3) {
/*
   COMPUTE UNNORMALIZED ASSOCIATED LEGENDRE POLYNOMIALS
   AND DERIVATIVES VIA RECURSION RELATIONS
*/
        if (n == m) {
          snorm[n+m*13] = st*snorm[n-1+(m-1)*13];
          dp[m][n] = st*dp[m-1][n-1]+ct*snorm[n-1+(m-1)*13];
        } else if (n == 1 && m == 0) {
          snorm[n+m*13] = ct*snorm[n-1+m*13];
          dp[m][n] = ct*dp[m][n-1]-st*snorm[n-1+m*13];
        } else if (n > 1 && n != m) {
          if (m > n-2) snorm[n-2+m*13] = 0.0;
          if (m > n-2) dp[m][n-2] = 0.0;
          snorm[n+m*13] = ct*snorm[n-1+m*13]-k[m][n]*snorm[n-2+m*13];
          dp[m][n] = ct*dp[m][n-1] - st*snorm[n-1+m*13]-k[m][n]*dp[m][n-2];
        }
/*
    TIME ADJUST THE GAUSS COEFFICIENTS
*/
        //G coefficient
        tc[m][n] = c[m][n]+dt*cd[m][n];
        //H coefficient
        if (m != 0) tc[n][m-1] = c[n][m-1]+dt*cd[n][m-1];
/*
    ACCUMULATE TERMS OF THE SPHERICAL HARMONIC EXPANSIONS
*/
        par = ar*snorm[n+m*13];
        if (m == 0) {
          //G coefficient
          temp1 = tc[m][n]*cp[m];
          temp2 = tc[m][n]*sp[m];
        } else {
          //G then H coefficient
          temp1 = tc[m][n]*cp[m]+tc[n][m-1]*sp[m];
          temp2 = tc[m][n]*sp[m]-tc[n][m-1]*cp[m];
        }
        bt = bt-ar*temp1*dp[m][n];
        bp += (fm[m]*temp2*par);
        br += (fn[n]*temp1*par);
/*
    SPECIAL CASE:  NORTH/SOUTH GEOGRAPHIC POLES
*/
        if (st == 0.0 && m == 1) {
          if (n == 1) {
              pp[n] = pp[n-1];
          } else {
            pp[n] = ct*pp[n-1]-k[m][n]*pp[n-2];
          }
          parp = ar*pp[n];
          bpp += (fm[m]*temp2*parp);
        }
      }
    }

    if (st == 0.0) bp = bpp;
    else bp /= st;
/*
    ROTATE MAGNETIC VECTOR COMPONENTS FROM SPHERICAL TO
    GEODETIC COORDINATES
*/
    bx = -bt*ca-br*sa;
    by = bp;
    bz = bt*sa-br*ca;
/*
    COMPUTE DECLINATION (DEC), INCLINATION (DIP) AND
    TOTAL INTENSITY (TI)
*/
    bh = sqrt((bx*bx)+(by*by));
    ti = sqrt((bh*bh)+(bz*bz));
    dec = atan2(by,bx);
    dip = atan2(bz,bh);

    return new double[] {dec, dip, ti};
  }

  public FieldProperties[] calcPropRates(double alt, double rlat, double rlon, double time) {
    double time1,dec1,dip1,ti1,time2,dec2,dip2,ti2;
    double[] result=geomg1(alt,rlat,rlon,time);
    time1 = time;
    dec1 = result[0];
    dip1 = result[1];
    ti1 = result[2];

    time = time1 + 1.0;
    result=geomg1(alt,rlat,rlon,time);
    time2 = time;
    dec2 = result[0];
    dip2 = result[1];
    ti2 = result[2];

/*COMPUTE X, Y, Z, AND H COMPONENTS OF THE MAGNETIC FIELD*/

    double x1=ti1*(cos(dec1)*cos(dip1));
    double x2=ti2*(cos(dec2)*cos(dip2));
    double y1=ti1*(cos(dip1)*sin(dec1));
    double y2=ti2*(cos(dip2)*sin(dec2));
    double z1=ti1*(sin(dip1));
    double z2=ti2*(sin(dip2));
    double h1=ti1*(cos(dip1));
    double h2=ti2*(cos(dip2));

/*  COMPUTE ANNUAL CHANGE FOR TOTAL INTENSITY  */
    double ati = ti2 - ti1;

/*  COMPUTE ANNUAL CHANGE FOR DIP & DEC  */
    double adip = (dip2 - dip1);
    double adec = (dec2 - dec1);

/*  COMPUTE ANNUAL CHANGE FOR X, Y, Z, AND H */
    double ax = x2-x1;
    double ay = y2-y1;
    double az = z2-z1;
    double ah = h2-h1;
    return new FieldProperties[] {new FieldProperties(ti1,h1,x1,y1,z1,dec1,dip1),
                                  new FieldProperties(ati,ah,ax,ay,az,adec,adip)};
  }

  public double getEpoch() {
    return epoch;
  }

  public FieldProperties calcProps(double alt, double rlat, double rlon, double time) {
    return calcPropRates(alt,rlat,rlon,time)[0];
  }

  public FieldProperties calcRates(double alt, double rlat, double rlon, double time) {
    return calcPropRates(alt,rlat,rlon,time)[0];
  }
}
