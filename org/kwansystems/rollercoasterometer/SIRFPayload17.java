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
public class SIRFPayload17 extends SIRFPayload {
  public int PLLSTAT;
  public int MSEL;
  public int PSEL;
  public int PLLE;
  public int PLLC;
  public int PLOCK;
  public int CCLK;
  public int PCLK;
  public int VPBDIV;
  public int MAMCR;
  public int MAMTIM;
  public SIRFPayload17(byte[] payload) {
    super(payload,"Machine setup");
    int pos=1;
    MAMCR=payload[pos];pos++;
    MAMTIM=payload[pos];pos++;
    PLLSTAT=(int)(readShort(payload,pos) & 0xFFFF);pos+=2;
    MSEL=((PLLSTAT >>  0) & 0x1F)+1;
    PSEL=1<<((PLLSTAT >>  5) & 0x03);
    PLLE=((PLLSTAT >>  8) & 0x01);
    PLLC=((PLLSTAT >>  9) & 0x01);
    PLOCK=((PLLSTAT >> 10) & 0x01);
    VPBDIV=payload[pos];pos+=1;
    CCLK=readInt(payload,pos);pos+=4;
    PCLK=readInt(payload,pos);pos+=4;
  }
  public String toString() {
    StringBuffer result=new StringBuffer(super.toString());
    result.append(String.format("\nPLLSTAT: %04X",PLLSTAT));
    result.append(String.format("\nMSEL:    %d",MSEL));
    result.append(String.format("\nPSEL:    %d",PSEL));
    result.append(String.format("\nPLLE:    %d",PLLE));
    result.append(String.format("\nPLLC:    %d",PLLC));
    result.append(String.format("\nPLOCK:   %d",PLOCK));
    result.append(String.format("\nVPBDIV:  %d",VPBDIV));
    result.append(String.format("\nCCLK:    %d",CCLK));
    result.append(String.format("\nPCLK:    %d",PCLK));
    return result.toString();
  }
}
