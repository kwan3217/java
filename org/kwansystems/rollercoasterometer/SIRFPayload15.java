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
public class SIRFPayload15 extends SIRFPayload {
  int[] load=new int[16];
  int PLLSTAT,VPBDIV;
  static final int FCO=12000000; //Value stamped on the side of my Logomatic
  static final int CCLK=FCO*5,PCLK=CCLK;
  int YEAR,MONTH,DOM,HOUR,MIN,SEC,CTC;
  public SIRFPayload15(byte[] payload) {
    super(payload,"Logomatic clock and load");
    int pos=1;
    YEAR=readShort(payload,pos);pos+=2;
    MONTH=payload[pos];pos++;
    DOM=payload[pos];pos++;
    HOUR=payload[pos];pos++;
    MIN=payload[pos];pos++;
    SEC=payload[pos];pos++;
    CTC=((int)readShort(payload,pos)) & 0xFFFF;pos+=2;
    for(int i=0;i<16;i++) {
      load[i]=readInt(payload,pos);pos+=4;
    }
  }
  String[] Task={" UART"," ADC"," FlushSD"," WriteLoad"};
  public String toString() {
    StringBuffer result=new StringBuffer(super.toString());
    result.append(String.format("\n%04d/%02d/%02d %02d:%02d:%02d/%04X",YEAR,MONTH,DOM,HOUR,MIN,SEC,CTC));
    for(int i=0;i<16;i++) {
      result.append(String.format("\nLoad[%02d]: %8d (%6.3f%%)",i,load[i],(double)load[i]*100.0/(double)PCLK));
      boolean hasTask=false;
      for(int j=0;j<4;j++) {
        if((i & (1<<j))!=0) {
          hasTask=true;
          result.append(Task[j]);
        }
      }
      if(!hasTask) result.append(" Idle");
    }
    return result.toString();
  }
}
