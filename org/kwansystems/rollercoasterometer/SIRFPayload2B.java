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
public class SIRFPayload2B extends SIRFPayload {
  public byte port;
  public int baud;
  public int divisor;
  public int fdl,fdlm,fdld;
  public SIRFPayload2B(byte[] payload) {
    super(payload,"UART setup");
    int pos=1;
    port=payload[pos];pos++;
    baud=(readInt(payload,pos));pos+=4;
    divisor=(int)(readShort(payload,pos) & 0xFFFF);pos+=2;
    fdl=(int)(payload[pos] & 0xFF);pos+=1;
    fdld=(fdl >> 0) & 0x0F;
    fdlm=(fdl >> 4) & 0x0F;
  }
  public String toString() {
    StringBuffer result=new StringBuffer(super.toString());
    result.append(String.format("\nPort:     %5d",port));
    result.append(String.format("\nBaud:     %5d",baud));
    result.append(String.format("\nDivisor:  %5d",divisor));
    result.append(String.format("\nFine div: %5d",fdld));
    result.append(String.format("\nFine mul: %5d",fdlm));
    return result.toString();
  }
}
