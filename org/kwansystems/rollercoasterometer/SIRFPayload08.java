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
public class SIRFPayload08 extends SIRFPayload {
  byte channel;
  byte PRN;
  int[] data=new int[10];
  byte[] parity=new byte[10];
  byte[] parityCalc=new byte[10];
  byte[] prevBits=new byte[10];
  boolean[] parityOK=new boolean[10];
  boolean allParityOK;
  boolean tlmOK;
  int tlm;
  int TOWcount;
  boolean isAlert,isAS;
  int subframeNum;
  NavSubframe subframe;
  String versionString;
  private static byte[] PS=new byte[] {29,30,29,30,30,29};
  private static byte[][] P=new byte[][] {
      { 1, 2, 3, 5, 6,10,11,12,13,14,17,18,20,23},
      { 2, 3, 4, 6, 7,11,12,13,14,15,18,19,21,24},
      { 1, 3, 4, 5, 7, 8,12,13,14,15,16,19,20,22},
      { 2, 4, 5, 6, 8, 9,13,14,15,16,17,20,21,23},
      { 1, 3, 5, 6, 7, 9,10,14,15,16,17,18,21,22,24},
      { 3, 5, 6, 8, 9,10,11,13,15,19,22,23,24}
    };
  public static byte calcParity(byte prev, int data, byte parity) {
    boolean[] DS=new boolean[31];
    DS[29]=((prev>>1) & 0x01)!=0;
    DS[30]=((prev>>0) & 0x01)!=0;
    boolean[] d=new boolean[31];
    //Step 1 - fill up d (uninverted data)
    for(int i=1;i<=24;i++) {
      d[i]=((data>>(24-i)) & 0x01)!=0;
    }
    for(int i=1;i<=6;i++) {
      d[i+24]=((parity>>(6-i)) & 0x01)!=0;
    }
    byte result=0;
    for(int i=0;i<6;i++) {
      boolean D=DS[PS[i]];
      for(int j=0;j<P[i].length;j++) {
        D^=d[P[i][j]];
      }
      result=(byte)((result<<1) | (D?1:0));
    }
    return result;
  }
  public SIRFPayload08(byte[] payload) {
    super(payload,"50bps message");
    int pos=1;
    channel=payload[pos];
    pos++;
    PRN=payload[pos];
    pos++;
    allParityOK=true;
    for(int i=0;i<data.length;i++) {
      int word=readInt(payload,pos);
      pos+=4;
      prevBits[i]=(byte)((word>>30) & 0x03);
      data[i]=(int)((word>>6) & 0xFFFFFF);
      //Bits in SIRF packet are as transmitted, so
      //if the old D*30 was set, we need to invert
      //all the data bits in the word to get the
      //intended data back
      if((prevBits[i] & 0x01) !=0 ) {
        data[i]=(~data[i]) & 0xFFFFFF;
      }
      parity[i]=(byte)((word>>0) & 0x3F);
      parityCalc[i]=calcParity(prevBits[i],data[i],parity[i]);
      parityOK[i]=(parity[i]==parityCalc[i]);
      if(!parityOK[i]) allParityOK=false;
    }
    tlmOK=(((data[0]>>16) & 0xFF)==0x8B);
    if(tlmOK) {
      tlm=(data[0]>>2) & 0x3FFF;
      TOWcount=(data[1]>>7) & 0x1FFFF;
      isAlert=((data[1]>>6) & 0x01)==1;
      isAS=((data[1]>>5) & 0x01)==1;
      subframeNum=(data[1] >> 2) & 0x07;
      subframe=interpret();
    } else {
      subframeNum=0;
      subframe=null;
    }
  }
  private NavSubframe interpret() {
    switch(subframeNum) {
      case 1:
        return new NavSubframe1(data);
      case 2:
        return new NavSubframe2(data);
      case 3:
        return new NavSubframe3(data);
      case 4:
      case 5:
        return new NavSubframe45(data);
      default:
        return null;
    }
  }
  public String toString() {
    StringBuffer result=new StringBuffer(super.toString());
    result.append(String.format("\nChannel: %d",channel));
    result.append(String.format("\nPRN:     %d",PRN));
    for(int i=0;i<data.length;i++) {
      result.append(String.format("\nWord[%02d]: %01X %06X %02X (%02X) Parity ",i,prevBits[i],data[i],parity[i],parityCalc[i]));
      result.append(parityOK[i]?"OK":"Bad");
    }
    result.append("\nParity ");
    result.append(allParityOK?"all":"not all");
    result.append(" OK");
    if(tlmOK) {
      result.append("\nTlm word ok");
      result.append(String.format("\nTlm Message 0x%04X",tlm));
      result.append(String.format("\nTime Of Week count %d",TOWcount));
      result.append(isAlert?"\nAlert flag set":"\nAlert flag not set");
      result.append(isAS?"\nA/S flag set":"\nA/S flag not set");
      result.append(String.format("\nsubframe %d: ",subframeNum));
      result.append(subframe);
    } else {
      result.append("\nTlm word bad");
    }
    return result.toString();
  }
}
