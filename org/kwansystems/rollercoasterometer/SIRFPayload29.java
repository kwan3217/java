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
public class SIRFPayload29 extends SIRFPayload {
  public boolean navValid;
  public enum PosModes {
    NO_SOLUTION("No Solution"),
    KF_1_SV("1 SV solution (Kalman)"),
    KF_2_SV("2 SV solution (Kalman)"),
    KF_3_SV("3 SV solution (Kalman)"),
    KF_4_SV("4+SV solution (Kalman)"),
    PT_2("2D point solution (Least Squares)"),
    PT_3("3D point solution (Least Squares)"),
    DR("Dead Reckoning (No Sats)");
    public final String msg;
    private PosModes(String Lmsg) {
      msg=Lmsg;
    }
  }
  public PosModes PositionMode;
  public boolean isTricklePower;
  public enum AltModes {
    NO_ALT_HOLD("No altitude hold applied"),
    HOLD_KF("Holding of altitude from KF"),
    HOLD_USER("Holding of altitude from user input"),
    ALWAYS_HOLD_USER("Always hold altitude from user input");
    public final String msg;
    private AltModes(String Lmsg) {
      msg=Lmsg;
    }
  };
  public AltModes AltMode;
  public boolean isDOPMaskExceeded;
  public boolean isDGPSUsed;
  public long GPSWeek;
  public double GPSTOW;
  public int UTCYear;
  public int UTCMonth;
  public int UTCDay;
  public int UTCHour;
  public int UTCMinute;
  public double UTCSecond;
  public boolean[] PRNUsed=new boolean[33];
  public double Lat; //Geodetic latitude in degrees
  public double Lon; //Longitude in degrees
  public double Alt; //Ellipsoid alt in meters
  public double AltMSL; //MSL alt in meters
  public byte mapDatum;
  public double SOG; //Speed over ground, m/s
  public double COG; //Course over ground, deg
  public double ClimbRate; // m/s
  public double HeadingRate; // deg/s
  public double EHPE; //Estimated horizontal position error, m
  public double EVPE; //Estimated vertical position error, m
  public double ETE;  //Estimated time error, s
  public double EHVE; //Estimated horizontal velocity error, m/s
  public double ClockBias; //Clock bias in meters (not seconds)
  public double ClockBiasError; //Clock bias error in meters
  public double ClockDrift;     //Clock drift in m/s, not rate
  public double ClockDriftError;//Clock drift error in m/s
  public byte NumPRNs;
  public double HDOP; //Horizontal dillution of precision
  public SIRFPayload29(byte[] payload) {
    super(payload,"Geodetic Navigation Data");
    int pos=1;
    short nav=readShort(payload,pos);
    navValid=(nav==0);
    pos+=2;
    short mode1=readShort(payload,pos);
    pos+=2;
    isDGPSUsed=        ((mode1 & 0x80) != 0);
    isDOPMaskExceeded= ((mode1 & 0x40) != 0);
    AltMode=AltModes.values()[(byte)((mode1>>4) & 0x03)];
    isTricklePower=    ((mode1 & 0x08) != 0);
    PositionMode=PosModes.values()[(byte)((mode1>>0) & 0x07)];
    GPSWeek=readShort(payload,pos);
    pos+=2;
    //Following is unsigned but never exceeds 604,800,000
    GPSTOW=0.001*(double)readInt(payload,pos);
    pos+=4;
    UTCYear=readShort(payload,pos);
    pos+=2;
    UTCMonth=payload[pos];
    pos++;
    UTCDay=payload[pos];
    pos++;
    UTCHour=payload[pos];
    pos++;
    UTCMinute=payload[pos];
    pos++;
    UTCSecond=(double)readShort(payload,pos);
    pos+=2;
    if(UTCSecond<0) UTCSecond+=65536;
    UTCSecond*=1e-3;
    int prnUsed=readInt(payload,pos);
    pos+=4;
    Lat=1e-7*(double)readInt(payload,pos);
    pos+=4;
    Lon=1e-7*(double)readInt(payload,pos);
    pos+=4;
    Alt=1e-2*(double)readInt(payload,pos);
    pos+=4;
  }
  public String toString() {
    StringBuffer result=new StringBuffer(super.toString());
    result.append(navValid?"\nPosition Valid":"\nPosition not valid");
    result.append(isDGPSUsed?"\nDifferential corrections applied":"\nNo differential corrections applied");
    result.append(isDOPMaskExceeded?"\nDOP Mask exceeded":"\nDOP Mask not exceeded");
    result.append("\n");result.append(AltMode.msg);
    result.append(isTricklePower?"\nTrickle Power":"\nFull Power");
    result.append("\n");result.append(PositionMode.msg);
    result.append(String.format("\nHDOP:        %10.1f",HDOP));
    result.append(String.format("\nGPS Week:    %10d",GPSWeek));
    result.append(String.format("\nGPS TOW:     %10.3f",GPSTOW));
    result.append(String.format("\nUTC Time: %02d/%02d/%04d %02d:%02d:%06.3f",UTCMonth, UTCDay, UTCYear, UTCHour, UTCMinute, UTCSecond));
    result.append(String.format("\nLatitude:    %11.7f°",Lat));
    result.append(String.format("\nLongitude:   %11.7f°",Lon));
    result.append(String.format("\nAltitude:    %11.2fm",Alt));
    return result.toString();
  }
}
