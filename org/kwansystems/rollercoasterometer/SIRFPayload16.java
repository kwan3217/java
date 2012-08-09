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
public class SIRFPayload16 extends SIRFPayload {
  byte port;
  int trybaud;
  int start;
  int chars;
  int errors;
  int result;
  public SIRFPayload16(byte[] payload) {
    super(payload,"Auto-baud attempt");
    int pos=1;
    port=payload[pos];pos++;
    trybaud=readInt(payload,pos);pos+=4;
    start=readInt(payload,pos);pos+=4;
    chars=readInt(payload,pos);pos+=4;
    errors=readInt(payload,pos);pos+=4;
    result=readInt(payload,pos);pos+=4;
  }
  public String toString() {
    StringBuffer result=new StringBuffer(super.toString());
    result.append(String.format("\nPort:     %12d",port));
    result.append(String.format("\nStart:    %12d",start));
    result.append(String.format("\nBaud:     %12d",trybaud));
    result.append(String.format("\nChars:    %12d",chars));
    result.append(String.format("\nErrors:   %12d",errors));
    result.append(String.format("\nresult:   %12d",this.result));
    return result.toString();
  }
}
