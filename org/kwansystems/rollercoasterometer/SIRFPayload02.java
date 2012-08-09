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
public class SIRFPayload02 extends SIRFPayload {
  public double rx,ry,rz;
  public double vx,vy,vz;
  public boolean isDGPSUsed;
  public boolean isDOPMaskExceeded;
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
  public boolean isTricklePower;
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
  public double HDOP;
  public long GPSWeek;
  public double GPSTOW;
  public byte[] PRN;
  public SIRFPayload02(byte[] payload) {
    super(payload,"Measured Navigation Data");
    int pos=1;
    rx=readInt(payload,pos);
    pos+=4;
    ry=readInt(payload,pos);
    pos+=4;
    rz=readInt(payload,pos);
    pos+=4;
    vx=0.125*(double)readShort(payload,pos);
    pos+=2;
    vy=0.125*(double)readShort(payload,pos);
    pos+=2;
    vz=0.125*(double)readShort(payload,pos);
    pos+=2;
    byte mode1=payload[pos];
    pos+=1;
    isDGPSUsed=        ((mode1 & 0x80) != 0);
    isDOPMaskExceeded= ((mode1 & 0x40) != 0);
    AltMode=AltModes.values()[(byte)((mode1>>4) & 0x03)];
    isTricklePower=    ((mode1 & 0x08) != 0);
    PositionMode=PosModes.values()[(byte)((mode1>>4) & 0x07)];
    HDOP=0.2*(double)payload[pos];
    pos+=1;
    byte mode2=payload[pos];

    pos+=1;
    GPSWeek=readShort(payload,pos);
    pos+=2;
    //Following is unsigned but never exceeds 60,480,000
    GPSTOW=0.01*(double)readInt(payload,pos);
    pos+=4;
    byte SVs=payload[pos];
    pos+=1;
    PRN=new byte[SVs];
    for(int i=0;i<SVs;i++) {
      PRN[i]=payload[pos];
      pos++;
    }
  }
  public String toString() {
    StringBuffer result=new StringBuffer(super.toString());
    result.append(String.format("\nX pos (m):   %10.1f",rx));
    result.append(String.format("\nY pos (m):   %10.1f",ry));
    result.append(String.format("\nZ pos (m):   %10.1f",rz));
    result.append(String.format("\nX vel (m/s): %10.3f",vx));
    result.append(String.format("\nY vel (m/s): %10.3f",vy));
    result.append(String.format("\nZ vel (m/s): %10.3f",vz));
    result.append(isDGPSUsed?"\nDifferential corrections applied":"\nNo differential corrections applied");
    result.append(isDOPMaskExceeded?"\nDOP Mask exceeded":"\nDOP Mask not exceeded");
    result.append("\n");result.append(AltMode.msg);
    result.append(isTricklePower?"\nTrickle Power":"\nFull Power");
    result.append("\n");result.append(PositionMode.msg);
    result.append(String.format("\nHDOP:        %10.1f",HDOP));
    result.append(String.format("\nGPS Week:    %10d",GPSWeek));
    result.append(String.format("\nGPS TOW:     %10.2f",GPSTOW));
    return result.toString();
  }
}
