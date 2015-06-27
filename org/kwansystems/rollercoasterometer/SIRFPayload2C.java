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
public class SIRFPayload2C extends SIRFPayload {
  public int ctc1;
  public short[] adc;
  public SIRFPayload2C(byte[] payload) {
    super(payload,"ADC readout");
    int pos=1;
    adc=new short[(payload.length-5)/2];
    for(int i=0;i<adc.length;i++) {
      adc[i]=readShort(payload,pos);
      pos+=2;
    }
    ctc1=readInt(payload,pos);
  }
  public String toString() {
    StringBuffer result=new StringBuffer(super.toString());
    result.append("\nADC:");
    for(int i=0;i<adc.length;i++) {
      result.append(String.format(" %5d", adc[i]));
    }
    result.append(String.format("\nCTC1: %5d (%10.8fsec)",ctc1,((double)ctc1)/60e6));
    return result.toString();
  }
}
