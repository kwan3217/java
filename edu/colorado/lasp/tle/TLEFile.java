/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.colorado.lasp.tle;

import java.io.*;
import java.util.*;
import static java.lang.Math.*;
import static edu.colorado.lasp.tle.Nutation.*;

/**
 *
 * @author jeppesen
 */
public class TLEFile {
  private List<TwoLineElement> TLE;
  private List<Double> range;
  public TLEFile(String infn) throws IOException, SGP4Exception {
    this(new LineNumberReader(new FileReader(infn)));
  }
  public TLEFile(LineNumberReader inf) throws IOException, SGP4Exception {
    TLE=new ArrayList<TwoLineElement>();
    String line0="";
    String line1="";
    String line2="";
    TwoLineElement thisTLE=null;
    do {
      line2=inf.readLine();
      if(line1.length()>0     &&
         line1.charAt(0)=='1' &&
         line2!=null          &&
         line2.length()>0     &&
         line2.charAt(0)=='2'
      ){
        boolean doAdd=true;
        try {
          thisTLE=new TwoLineElement(line0,line1,line2);
        } catch (IllegalArgumentException E) {
          doAdd=false;
        }
        if(doAdd)TLE.add(thisTLE);
        line0="";
        line1="";
        line2="";
      }
      line0=line1;
      line1=line2;
    } while(line2!=null);

    Collections.sort(TLE);
    filterTLE();
    calc_voronoi2();
  }
  private void calc_voronoi2() {
    range=new ArrayList<Double>(TLE.size());
    range.add(TLE.get(0).jdsatepoch);
    for(int i=1;i<TLE.size()-1;i++) {
      range.add((TLE.get(i-1).jdsatepoch+TLE.get(i+1).jdsatepoch)/2);
    }
    range.add(Double.POSITIVE_INFINITY);
  }
  private double[] Pair_Difference(int i, int j) throws SGP4Exception {
    TwoLineElement TLE1=TLE.get(i);
    TwoLineElement TLE2=TLE.get(j);
    double dT = TLE2.jdsatepoch-TLE1.jdsatepoch;

    double[] rvj1=new SGP4Core(TLE1).sgp4(dT*1440.0);
    double[] rvj2=new SGP4Core(TLE2).sgp4(0.0);

    double[] rd=new double[3];
    double[] vd=new double[3];
    for(int k=0;k<3;k++) {
      rd[k]=rvj2[k  ]-rvj1[k  ];
      rd[k]*=rd[k];
      vd[k]=rvj2[k+3]-rvj1[k+3];
      vd[k]*=vd[k];
    }
    double dr=sqrt(rd[0]+rd[1]+rd[2]);
    double dv=sqrt(vd[0]+vd[1]+vd[2]);
    double drdt=dr/dT;
    double dvdt=dv/dT;
    return new double[] {drdt,dvdt};
  }
  private void filterTLE() throws SGP4Exception {
    //Now for the hard part. Check that each tle is consistent with its two neighbors
    //   Outlier Threshold
    final double MaxSD = 5.0;
    double[][] crv=neighbor_consistency();

    //The following values of rMean, rSd, vMean, vSD were
    //obtained from the second of two rounds of this filter
    //on the first 3996 TLE sets for the UARS orbit.
    final double rMean=1.243155E+00;
    final double rSD=8.018418E-01;
    final double vMean=1.257520E+00;
    final double vSD=8.275024E-01;
    double[] rdev=new double[crv.length];
    double[] vdev=new double[crv.length];
    double[] tdev=new double[crv.length];
    for(int i=0;i<rdev.length;i++) {
      rdev[i] = (crv[i][0]-rMean)/rSD;
      vdev[i] = (crv[i][1]-vMean)/vSD;
      tdev[i] = abs( rdev[i] )*abs( vdev[i]);
    }
    for(int i=crv.length-2;i>=1;i--) {
      if((abs( rdev[i] ) > MaxSD*4) || (abs( vdev[i] ) > MaxSD*4) || (tdev[i] > MaxSD*8)) {
        TLE.remove(i);
      }
    }
  }
  private double[][] neighbor_consistency() throws SGP4Exception {
    double[][] crv=new double[TLE.size()][2];
    for(int j=1;j<=crv.length-2;j++) {
      //Get 3 distances (in both dRdT and dVdt)
      double[] ddT12=Pair_Difference(j-1,j  );
      double[] ddT23=Pair_Difference(j  ,j+1);
      double[] ddT13=Pair_Difference(j-1,j+1);

      //Set j is inconsistent if sets j-1 and j+1 agree with each
      //other, but not with j
      crv[j][0] = (ddT12[0] + ddT23[0] + ddT13[0])/(3.0*ddT13[0]);
      crv[j][1] = (ddT12[1] + ddT23[1] + ddT13[1])/(3.0*ddT13[1]);
    }
    return crv;
  }
  public double[][] spacecraft_pv(double jd) throws SGP4Exception {
    return spacecraft_pv(jd,false);
  }
  public double[][] spacecraft_pv(double jd, boolean no_nutation) throws SGP4Exception {
    double[] pv=new double[6];
    double[] dpv=new double[6];
    int i=Collections.binarySearch(range, jd);
    if(i<0) i=-i-2;

    TwoLineElement TLE0=TLE.get(i);
    TwoLineElement TLE1=TLE.get(i+1);
    double tmin0=(jd-TLE0.jdsatepoch)*1440.0;
    double tmin1=(jd-TLE1.jdsatepoch)*1440.0;
    double[] pv0=new SGP4Core(TLE0).sgp4(tmin0);
    double[] pv1=new SGP4Core(TLE1).sgp4(tmin1);
    double f=(jd-TLE0.jdsatepoch)/(TLE1.jdsatepoch-TLE0.jdsatepoch);
    double g=(f-0.5);
    double dy=1.0-4.0*g*g*(1.0-2.0*g*g);
    for(int j=0;j<6;j++) {
      dpv[j]=abs(pv1[j]-pv0[j])*dy;
      pv[j]=(1.0-f)*pv0[j]+f*pv1[j];
    }
    if(!no_nutation) {
      //Make nutation correction
      //Build nutation matrix based on epoch of first element,
      //the Earth just doesn't nutate that much in a day
      //Also, use UTC in place of dynamical time, the Earth
      //REALLY doesn't nutate that much in 34s
      double tn = (TLE0.jdsatepoch-2451545.0)/365250.0;

      double[][] nu=Nutate_Matrix(tn);
      double[] pvret=new double[6];
      for(int ii=0;ii<=2;ii++) {
        pvret[ii] = 0.0;
        pvret[ii+3] = 0.0;
        for(int jj=0;jj<=2;jj++) {
          pvret[jj]   = pvret[jj  ]+nu[ii][jj]*pv[jj  ];
          pvret[jj+3] = pvret[jj+3]+nu[ii][jj]*pv[jj+3];
        }
      }
      pv=pvret;
      //Don't bother to nutate the uncertainty, it's just not that big
    }
    return new double[][] {pv,dpv};
  }
  public static void main(String[] args) throws IOException, SGP4Exception {
    TLEFile F=new TLEFile("00031304.tle");
    double[][] result=F.spacecraft_pv(2454316.6);
    for(int i=0;i<2;i++) {
      for(int j=0;j<6;j++) {
        System.out.printf("%20.10f",result[i][j]);
      }
      System.out.println();
    }
  }
}
