/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.rollercoasterometer;

import static org.kwansystems.tools.Endian.*;

/**
 *
 * @author jeppesen
 */
public class SIRFPayload04 extends SIRFPayload {
  short GPSWeek;
  double GPSTOW;
  byte nChannels;
  byte[] svid;
  double[] az;
  double[] el;
  boolean[] hasAcq;
  boolean[] hasPhase;
  boolean[] inBitSync;
  boolean[] inSubframeSync;
  boolean[] inCostasLock;
  boolean[] inCodeLock;
  boolean[] isFailed;
  boolean[] hasEphem;
  short[][] CN0;
  public SIRFPayload04(byte[] payload) {
    super(payload,"Measured Tracker Data");
    int pos=1;
    GPSWeek=readShort(payload,pos);
    pos+=2;
    //Following is unsigned but never exceeds 60,480,000
    GPSTOW=0.01*(double)readInt(payload,pos);
    pos+=4;
    nChannels=payload[pos];
    pos+=1;
    svid=new byte[nChannels];
    az=new double[nChannels];
    el=new double[nChannels];
    hasAcq=new boolean[nChannels];
    hasPhase=new boolean[nChannels];
    inBitSync=new boolean[nChannels];
    inSubframeSync=new boolean[nChannels];
    inCostasLock=new boolean[nChannels];
    inCodeLock=new boolean[nChannels];
    isFailed=new boolean[nChannels];
    hasEphem=new boolean[nChannels];
    CN0=new short[nChannels][10];
    for(int i=0;i<nChannels;i++) {
      svid[i]=payload[pos];
      pos++;
      az[i]=((double)(((long)payload[pos])&0xff))*3.0/2.0;
      pos++;
      el[i]=((double)(((long)payload[pos])&0xff))*0.5;
      pos++;
      pos++; //reserved empty flags
      byte state=payload[pos];
      pos+=1;
      hasAcq[i]=((state >> 0) & 1)!=0;
      hasPhase[i]=((state >> 1) & 1)!=0;
      inBitSync[i]=((state >> 2) & 1)!=0;
      inSubframeSync[i]=((state >> 3) & 1)!=0;
      inCostasLock[i]=((state >> 4) & 1)!=0;
      inCodeLock[i]=((state >> 5) & 1)!=0;
      isFailed[i]=((state >> 6) & 1)!=0;
      hasEphem[i]=((state >> 7) & 1)!=0;
      for(int j=0;j<10;j++) {
        CN0[i][j]=(short)(payload[pos] & 0xFF);
        pos++;
      }
    }
  }
  public String toString() {
    StringBuffer result=new StringBuffer(super.toString());
    result.append(String.format("\nGPS Week:    %10d",GPSWeek));
    result.append(String.format("\nGPS TOW:     %10.2f",GPSTOW));
    result.append(String.format("\nNumber of channels: %d",nChannels));
    for(int i=0;i<nChannels;i++) if(svid[i]!=0) {
      result.append(String.format("\nChannel %02d\n",i));
      result.append(String.format("\n  SVID:            %02d",svid[i]));
      result.append(String.format("\n  Azimuth (deg):   %4.1f",az[i]));
      result.append(String.format("\n  Elevation (deg): %4.1f",el[i]));
      result.append(String.format("\n  Status:"));
      if(hasAcq[i]) result.append(" hasAcq");
      if(hasPhase[i]) result.append(" hasPhase");
      if(inBitSync[i]) result.append(" inBitSync");
      if(inSubframeSync[i]) result.append(" inSubframeSync");
      if(inCostasLock[i]) result.append(" inCostasLock");
      if(inCodeLock[i]) result.append(" inCodeLock");
      if(isFailed[i]) result.append(" isFailed");
      if(hasEphem[i]) result.append(" hasEphem");
      result.append(String.format("\n  C/N_0:"));
      for(int j=0;j<10;j++) {
        result.append(String.format(" %3d", CN0[i][j]));
      }
    }
    return result.toString();
  }
}
