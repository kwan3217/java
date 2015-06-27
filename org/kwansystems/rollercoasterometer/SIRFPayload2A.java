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
public class SIRFPayload2A extends SIRFPayload {
  public int adcFreq;
  public int adcBin;
  public int adcDiv;
  public byte[] pinLabel;
  public byte[] side;
  public byte[] channel;
  public SIRFPayload2A(byte[] payload) {
    super(payload,"ADC columns");
    int pos=1;
    adcFreq=(int)(readShort(payload,pos) & 0xFFFF);pos+=2;
    adcBin=(int)(readShort(payload,pos) & 0xFFFF);pos+=2;
    adcDiv=payload[pos];pos++;
    pinLabel=new byte[(payload.length-pos)/2];
    side=new byte[pinLabel.length];
    channel=new byte[pinLabel.length];
    for(int i=0;i<pinLabel.length;i++) {
      pinLabel[i]=payload[pos];pos++;
      side[i]=(byte)(payload[pos]>>4);
      channel[i]=(byte)(payload[pos] & 0x0f);
      pos++;
    }
  }
  public String toString() {
    StringBuffer result=new StringBuffer(super.toString());
    result.append(String.format("\nADC Frequency: %5d",adcFreq));
    result.append(String.format("\nADC Binning:   %5d",adcBin));
    result.append(String.format("\nADC Clock:     %5d",60000000/adcDiv));
    result.append("\nColumns: ");
    for(int i=0;i<pinLabel.length;i++) {
      result.append(String.format(" %1d (%1d.%1d) ", pinLabel[i],side[i],channel[i]));
    }
    return result.toString();
  }
}
