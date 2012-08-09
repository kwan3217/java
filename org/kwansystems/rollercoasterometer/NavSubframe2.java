/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.rollercoasterometer;

/**
 *
 * @author jeppesen
 */
class NavSubframe2 extends NavSubframe {
  short iode;
  double Crs;
  double Deltan;
  double M0;
  double CUC;
  double e;
  double CUS;
  double sqrtA;
  double toe;
  boolean fitInterval;
  int AODO;
  public NavSubframe2(int[] data) {
    super(data);
    iode=((short)((data[2] >> 16) & 0xFF));
    //  Signed 16 bit scale -5
    Crs=(short)((data[2]>>0) & 0xFFFF);
    Crs/=(1L<<5);
    //  Signed 16 bit scale -43
    Deltan=(short)((data[3]>>8) & 0xFFFF);
    Deltan/=(1L<<43);
    //  Signed 32 bit scale -31
    M0=(long)(((((data[3]>>0) & 0xFF)<<24) | data[4]) & 0xFFFFFFFF);
    M0/=(1L<<31);
    //  Signed 16 bit scale -29
    CUC=(short)((data[5]>>0) & 0xFFFF);
    CUC/=(1L<<29);
    //Unsigned 32 bit scale -33
    e=(long)(((((data[5]>>0) & 0xFF)<<24) | data[6]) & 0xFFFFFFFF);
    e/=(1L<<33);
    //  Signed 16 bit scale -29
    CUS=(short)((data[7]>>0) & 0xFFFF);
    CUS/=(1L<<29);
    //Unsigned 32 bit scale -19
    sqrtA=(long)(((((data[7]>>0) & 0xFF)<<24) | data[8]) & 0xFFFFFFFF);
    sqrtA/=(1L<<19);
    //Unsigned 16 bit scale +4
    toe=(int)((data[9]>>0) & 0xFFFF);
    toe*=(1L<<4);
    fitInterval=((data[9]>>5) & 0x01) == 1;
    AODO=(short)((data[9]>>0) & 0x1F)*900;
  }
  public String toString() {
    StringBuffer result=new StringBuffer("Ephemeris part A (1 of 2)");
    result.append(String.format("\nIssue of Data Ephemeris:  %4d",iode));
    result.append(String.format("\nC_rs:                      %e ",Crs));
    result.append(String.format("\nDelta_n:                   %e ",Deltan));
    result.append(String.format("\nM_0:                       %e ",M0));
    result.append(String.format("\nC_UC:                      %e ",CUC));
    result.append(String.format("\ne:                         %e ",e));
    result.append(String.format("\nC_US:                      %e ",CUS));
    result.append(String.format("\nsqrtA:                     %e ",sqrtA));
    result.append(String.format("\ntoe:                       %e ",toe));
    result.append(String.format("\nFit interval flag:         %b ",fitInterval));
    result.append(String.format("\nAODO:                      %d ",AODO));
    return result.toString();
  }

}
