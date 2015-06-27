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
public class SIRFPayload09 extends SIRFPayload {
  public double segStatMax;
  public double segStatLat;
  public double aveTrkTime;
  public short lastMilli;
  public SIRFPayload09(byte[] payload) {
    super(payload,"CPU Throughput");
    int pos=1;
    segStatMax=((double)readShort(payload,pos))/186.0;
    pos+=2;
    segStatLat=((double)readShort(payload,pos))/186.0;
    pos+=2;
    aveTrkTime=((double)readShort(payload,pos))/186.0;
    pos+=2;
    lastMilli=readShort(payload,pos);
    pos+=2;
  }
  public String toString() {
    StringBuffer result=new StringBuffer(super.toString());
    result.append(String.format("\nSegStatMax: %fms",segStatMax));
    result.append(String.format("\nSegStatLat: %fms",segStatLat));
    result.append(String.format("\naveTrkTime: %fms",aveTrkTime));
    result.append(String.format("\nlastMilli:  %dms",lastMilli));
    return result.toString();
  }
}
