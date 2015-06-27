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
public class SIRFPayloadAC extends SIRFPayload {
  byte subId;
  int Lat;
  int Lon;
  int Alt;
  short TrueHdg;
  int ClockDrift;
  short GPSWeek;
  double GPSTOW;
  byte nChannels;
  byte resetConfig;
  boolean useData;
  boolean ClearEphemeris;
  boolean ClearMemory;
  boolean FactoryReset;
  boolean EnableNavLib;
  boolean EnableDebug;
  public SIRFPayloadAC(byte[] payload) {
    super(payload,"Init GPS/DR Navigation");
    int pos=1;
    subId=payload[pos];pos++;
    Lat=readInt(payload,pos);pos+=4;
    Lon=readInt(payload,pos);pos+=4;
    Alt=readInt(payload,pos);pos+=4;
    TrueHdg=readShort(payload,pos);pos+=2;
    ClockDrift=readInt(payload,pos);pos+=4;
    //Following is unsigned but never exceeds 60,480,000
    GPSTOW=0.01*(double)readInt(payload,pos);pos+=4;
    GPSWeek=readShort(payload,pos);pos+=2;
    nChannels=payload[pos];pos+=1;
    resetConfig=payload[pos];pos+=1;
    useData=(resetConfig & 0x01)!=0;
    ClearEphemeris=(resetConfig & 0x02)!=0;
    ClearMemory=(resetConfig & 0x04)!=0;
    FactoryReset=(resetConfig & 0x08)!=0;
    EnableNavLib=(resetConfig & 0x10)!=0;
    EnableDebug=(resetConfig & 0x20)!=0;
  }
  public String toString() {
    StringBuffer result=new StringBuffer(super.toString());
    if(useData) {
      result.append(String.format("\nLat:           %10d",Lat));
      result.append(String.format("\nLon:           %10d",Lon));
      result.append(String.format("\nAlt:           %10d",Alt));
      result.append(String.format("\nTrueHdg:       %10d",TrueHdg));
      result.append(String.format("\nClockDrift:    %10d",ClockDrift));
      result.append(String.format("\nGPS TOW:     %10.2f",GPSTOW));
      result.append(String.format("\nGPS Week:    %10d",GPSWeek));
    }
    result.append(String.format("\nNumber of channels: %d",nChannels));
    result.append(String.format("\nResetConfig:  %02X",resetConfig));
    result.append(String.format("\n  useData:        %b",useData));
    result.append(String.format("\n  ClearEphemeris: %b",ClearEphemeris));
    result.append(String.format("\n  ClearMemory:    %b",ClearMemory));
    result.append(String.format("\n  FactoryReset:   %b",FactoryReset));
    result.append(String.format("\n  EnableNavLib:   %b",EnableNavLib));
    result.append(String.format("\n  EnableDebug:    %b",EnableDebug));
    return result.toString();
  }
}
