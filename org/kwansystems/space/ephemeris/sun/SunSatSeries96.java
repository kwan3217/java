package org.kwansystems.space.ephemeris.sun;

import java.io.*;
import java.text.*;

import org.kwansystems.tools.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeEpoch;
import org.kwansystems.tools.time.TimeScale;
import org.kwansystems.tools.time.TimeUnits;
import org.kwansystems.tools.vector.*;
import org.kwansystems.space.ephemeris.*;

import static org.kwansystems.space.Constants.*;
import static org.kwansystems.tools.time.Time.*;

public class SunSatSeries96 extends TableEphemeris {
  protected String cname;
  protected double tzero,dt;
  protected int mx,imax,iblock;
  protected double[][] fq;
  protected double[][][] sec;
  protected double[][][][] ct,st;
/*
 *     Ref : Bureau des Longitudes - 96.12
 *           J. Chapront, G. Francou (BDL)
 *     Object
 *     ------
 *
 *     Compute planetary ephemerides with series built by a method of
 *     representation using frequency analysis.
 *     Ref : J. Chapront, 1995, Astron. Atrophys. Sup. Ser., 109, 181.
 *
 *     Planetary Ephemerides : rectangular heliocentric coordinates.
 *     Source : DE403 (Jet Propulsion Laboratory).
 *     Frame : Dynamical Mean Equinox and Equator J2000 (DE403).
 *     Origin: Sun body center (heliocentric)
 *     Time  : Barycentric Dynamical Time (TDB).
 *
 *     Input
 *     -----
 *
 *     tjd :       Julian date TDB (double real).
 *
 *     Output
 *     ------
 *
 *     r(6) :      Table of rectangular coordinates (double real).
 *                 r(1) : X  position (au).
 *                 r(2) : Y  position (au).
 *                 r(3) : Z  position (au).
 *                 r(4) : X' velocity (au/day).
 *                 r(5) : Y' velovity (au/day).
 *                 r(6) : Z' velocity (au/day).
 *
 * Translated from Fortran to Java by Chris Jeppesen
 * Also changed some orders of subscripts and made everything zero based
 * 
 * This seems to represent the system barycenters for all the planet/satellite systems,
 * not just Earth. It is hard to tell, since DE403 is known to be far from DE405, at
 * the levels required. No matter...
 */
   public MathState PosVel(double tjd) { //Simple reformatter into a mathstate
     double[] S=SERIES(tjd);
     return new MathState(S[0],S[1],S[2],S[3],S[4],S[5]);   	 
   }
   public MathState PosVelMS(double tjd) { //Simple reformatter into a mathstate
     double[] S=SERIES(tjd);
     return new MathState(S[0]*MPerAU,S[1]*MPerAU,S[2]*MPerAU,
                          S[3]*MPerAU/86400.0,S[4]*MPerAU/86400.0,S[5]*MPerAU/86400.0);   	 
   }
   public MathState PosVelMS(Time T) { //Use a Time object, convert it to JDTDB
     double TT=T.get(TimeUnits.Days,TimeScale.TDB,TimeEpoch.JD);
     return PosVelMS(TT);
   }
   private double[] SERIES(double tjd) {
     double[] r=new double[6];
     double[] v=new double[6];
     double[] w=new double[3];
     double[] ws=new double[3];
    
    //     Check up date
    double tmax=tzero+dt*iblock;
    if (tjd<tzero-0.5|tjd>tmax+0.5) throw new IllegalArgumentException("Date out of bounds");
    
    int nb=(int)((tjd-tzero)/dt);
    if (tjd<=tzero) nb=0;
    if (tjd>=tmax) nb=iblock-1;
    
    //Change variable
    double tdeb=tzero+(nb)*dt;
    double x=2.0*(tjd-tdeb)/dt-1.0;
    double fx=x*dt/2.0;
    
    //     Compute positions (secular terms)
    
    for(int iv=0;iv<3;iv++) {
      v[iv]=0.0;
      double wx=1.0;
      int max=2*imax-1;
      for(int i=0;i<=max;i++) {
        v[iv]=v[iv]+sec[nb][i][iv]*wx;
        wx=wx*x;
      }
    }
    
    //     Compute positions (Poisson terms)
    double wx=1.0;
    for(int m=0;m<=mx;m++) {
      int nw=fq[m].length;
      for(int iv=0;iv<3;iv++) {
        ws[iv]=0.0;
      }
      for(int i=0;i<nw;i++) {
        double f=fq[m][i]*fx;
        double cf=Math.cos(f);
        double sf=Math.sin(f);
        for(int iv=0;iv<3;iv++) {
          ws[iv]=ws[iv]+ct[nb][m][i][iv]*cf+st[nb][m][i][iv]*sf;
        }
      }
      for(int iv=0;iv<3;iv++) {
        v[iv]=v[iv]+ws[iv]*wx;
      }
      wx=wx*x;
    }
    //     Compute velocities (secular terms)
    double wt=2.0/dt;
    for(int iv=0;iv<3;iv++) {
      w[iv]=0.0;
      wx=1.0;
      int max=2*imax-1;
      for(int i=1;i<=max;i++) {
        w[iv]=w[iv]+i*sec[nb][i][iv]*wx;
        wx=wx*x;
      }
      w[iv]=wt*w[iv];
    }
    
    //     Compute velocities (Poisson terms)
    wx=1.0;
    double wy=0;
    
    for(int m=0;m<=mx;m++) {
      int nw=fq[m].length;
      for(int i=0;i<nw;i++) {
        double fw=fq[m][i];
        double f=fw*fx;
        double cf=Math.cos(f);
        double sf=Math.sin(f);
        for(int iv=0;iv<3;iv++) {
          double stw=st[nb][m][i][iv];
          double ctw=ct[nb][m][i][iv];
          w[iv]=w[iv]+fw*(stw*cf-ctw*sf)*wx;
          if (m>0) w[iv]=w[iv]+m*wt*(ctw*cf+stw*sf)*wy;
        }
      }
      wy=wx;
      wx=wx*x;
    }
    //     Stock results
    for(int iv=0;iv<3;iv++) {
      r[iv]=v[iv]/1e10;
      r[iv+3]=w[iv]/1e10;
    }
    //     Exit
    return r;
  }
  
  protected void LoadText() throws IOException {
    //    Read file parameters
    //rewind lu
	  String infn="Data/SunSatSeries96/series96."+suffix[body];
    LineNumberReader lu=new LineNumberReader(new FileReader(infn));
    //read (lu,fmt='(a12,f10.1,f8.0,3i3)') cname,tzero,dt,mx,imax,iblock
    String inl;
    
    inl=lu.readLine();
    cname=inl.substring(0,12);
    tzero=Double.parseDouble(inl.substring(12,22).trim());
    dt=Double.parseDouble(inl.substring(22,30).trim());
    mx=Integer.parseInt(inl.substring(30,33).trim());
    imax=Integer.parseInt(inl.substring(33,36).trim());
    iblock=Integer.parseInt(inl.substring(36,39).trim());
    fq=new double[3][];
    sec=new double[iblock][4][3];
    ct=new double[iblock][3][][]; 
    st=new double[iblock][3][][];
    //     Read frequencies
    for(int m=0;m<=mx;m++) {
      inl=lu.readLine();
      fq[m]=new double[Integer.parseInt(inl.substring(0,4).trim())];
      for(int i=0;i<fq[m].length;i++) {
        inl=lu.readLine();
        fq[m][i]=Double.parseDouble(inl.substring(0,20).trim());
      }
    }
    //     Read separator
    lu.readLine();
    
    //The fortran reader is designed to find the right block and read it.
    //It does so by setting up a loop from 1 to the correct block, then
    //reading the block. Previous reads of the block will be overwritten,
    //so that the correct block is read at the end.
    //This reads all the blocks, and requires one more dimension for 
    //sec, ct, and st than the fortran version
    //     Read series blocks
    for(int nb=0;nb<iblock;nb++) {
      for(int m=0;m<=mx;m++) {
        ct[nb][m]=new double[fq[m].length][3];
        st[nb][m]=new double[fq[m].length][3];
      }
      for(int iv=0;iv<3;iv++) {
        //        Read block and variable numbers
        //        (But since we are looping, we don't need to look at them, just read past)
        inl=lu.readLine();
        //        Read secular terms
        for(int i=0;i<=imax;i+=2) {
          inl=lu.readLine();
          sec[nb][i][iv]=Double.parseDouble(inl.substring(0,14).trim());
          sec[nb][i+1][iv]=Double.parseDouble(inl.substring(14,28).trim());
        }
        //        Read Poisson terms
        for(int m=0;m<=mx;m++) {
          int ip=m%2;
          for(int i=0;i<fq[m].length;i++) {
            inl=lu.readLine();
            double a=Double.parseDouble(inl.substring(0,14).trim());
            double b=Double.parseDouble(inl.substring(14,28).trim());
            if(ip==0) {
              ct[nb][m][i][iv]=a;
              st[nb][m][i][iv]=b;
            } else {
              ct[nb][m][i][iv]=b;
              st[nb][m][i][iv]=a;
            }
          }
        }
      }
    }
  }
  protected String SerialFilenameCore() {
	  return "SunSatSeries96/Series96."+suffix[body];
  }
  protected void LoadSerial(ObjectInputStream inf) throws IOException, ClassNotFoundException {
    cname=(String)inf.readObject();
    tzero=(Double)inf.readObject();
    dt=(Double)inf.readObject();
    mx=(Integer)inf.readObject();
    imax=(Integer)inf.readObject();
    iblock=(Integer)inf.readObject();
    fq=(double[][])inf.readObject();
    sec=(double[][][])inf.readObject();
    ct=(double[][][][])inf.readObject();
    st=(double[][][][])inf.readObject();
  }
  protected void SaveSerial(ObjectOutputStream ouf) throws IOException {
    ouf.writeObject(cname);
    ouf.writeObject(tzero);
    ouf.writeObject(dt);
    ouf.writeObject(mx);
    ouf.writeObject(imax);
    ouf.writeObject(iblock);
    ouf.writeObject(fq);
    ouf.writeObject(sec);
    ouf.writeObject(ct);
    ouf.writeObject(st);
  }
  public static final Ephemeris[] satArray=new Ephemeris[] {
    null, //Sun
    new SunSatSeries96(1),  	
    new SunSatSeries96(2),  	
    new SunSatSeries96(3),  	
    new SunSatSeries96(4),  	
    new SunSatSeries96(5),  	
    new SunSatSeries96(6),  	
    new SunSatSeries96(7),  	
    new SunSatSeries96(8),  	
    new SunSatSeries96(9)
  };
  private static double GMSunDE403=Math.pow(0.01720209895,2)*Math.pow(MPerAU,3)/Math.pow(86400.0,2);
  public static final double[] satGM={
    GMSunDE403,
    GMSunDE403/  6023600.0,
    GMSunDE403/   408523.71,
    GMSunDE403/   332946.048134,
    GMSunDE403/  3098708.0,
    GMSunDE403/     1047.3486,
    GMSunDE403/     3497.898,
    GMSunDE403/    22902.94,
    GMSunDE403/    19412.24,
    GMSunDE403/135000000.0,
  }; //Original data is DE403 planet/Sun mass ratio, with (Earth+Moon)/sun in slot 3.
     //Conversion makes it GM, m and s
  private String[] suffix={"SUN","mer","ven","emb","mar","jup","sat","ura","nep","plu"};
  private int body;
  public SunSatSeries96(int Lbody) {
    super();
    body=Lbody;
    Load();
  }
  public static final double[] satMassRatio=satGM;
   public static void main(String args[]) throws IOException, ClassNotFoundException {
    SunSatSeries96 This=new SunSatSeries96(1);
    double JDTDB=2429520.75000;
    MathState r=This.PosVel(JDTDB);
    DecimalFormat D=new DecimalFormat(" 0.000000000000;-0.000000000000");
    System.out.println("JD:   2429520.75000  Date:   14 Sep 1939  Hour:   06h 00m 00s TDB");
    System.out.println("JD:   "+JDTDB);
    System.out.println("x : -0.263225827763  y :  0.177224307410  z :  0.121993684878 au");
    System.out.println("x : "+D.format(r.R().X())+"  y : "+D.format(r.R().Y())+"  z : "+D.format(r.R().Z())+" au");
    System.out.println("x': -0.023332382826  y': -0.019243450726  z': -0.007853283244 au/d");
    System.out.println("x': "+D.format(r.V().X())+"  y': "+D.format(r.V().Y())+"  z': "+D.format(r.V().Z())+" au/d");
    System.out.println("Done");
  }
 
  private static final Rotator toJ2000Ecl=MathMatrix.Rot1d(epsJ2000D);
  public MathState CalcState(Time TT) {
    return toJ2000Ecl.transform(PosVelMS(TT));
  }
  public MathVector CalcPos(Time TT) {
	  return defaultCalcPos(TT);
  }
  public MathVector CalcVel(Time TT) {
	  return defaultCalcVel(TT);
  }
}
