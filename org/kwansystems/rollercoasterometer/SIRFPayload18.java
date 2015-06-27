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
public class SIRFPayload18 extends SIRFPayload {
  byte progress;
  byte MID;
  String OID;
  String PNM;
  byte PRV;
  long PSN;
  int MDT;
  byte CSD_STRUCTURE;
  byte TAAC;
  byte NSAC;
  byte TRAN_SPEED;
  short CCC;
  byte READ_BL_LEN;
  public SIRFPayload18(byte[] payload) {
    super(payload,"Memory Card Identification");
    int pos=1;
    progress=payload[pos];pos++;
    MID=payload[pos];pos++;
    OID="";
    for(int i=0;i<2;i++) {
      OID+=(char)payload[pos];pos++;
    }
    PNM="";
    for(int i=0;i<5;i++) {
      PNM+=(char)payload[pos];pos++;
    }
    PRV=payload[pos];pos++;
    PSN=((long)readInt(payload,pos)) & 0xFFFFFFFF;pos+=4;
    MDT=readShort(payload,pos) & 0xFFF;pos+=2;
    pos++; //skip the CRC;
    pos+=2; //skip the other CRC;
    CSD_STRUCTURE=(byte)((payload[pos]>>6) & 0x0F);pos++;
    TAAC=payload[pos];pos++;
    NSAC=(byte)(payload[pos] & 0xFF);pos++;
    TRAN_SPEED=payload[pos];pos++;
    CCC=readShort(payload,pos);pos+=2;
    READ_BL_LEN=(byte)((CCC >> 0) & 0x0F);
    CCC=(short)((CCC >> 4) & 0xFFF);
    short read=readShort(payload,pos);pos+=2;
  }
  public String toString() {
    StringBuffer result=new StringBuffer(super.toString());
    result.append("\nCID Register:");
    result.append(String.format("\n Manufacturer ID:        %d",MID));
    result.append(String.format("\n OEM/Application ID:     %s",OID));
    result.append(String.format("\n Product Name:           %s",PNM));
    result.append(String.format("\n Product Revision:       %d.%d",(PRV>>4) & 0x0F,(PRV >> 0) & 0x0F));
    result.append(String.format("\n Serial Number:          %8X",PSN));
    result.append(String.format("\n Manufacturer Date Code: 20%02d-%02d",(MDT>>4) & 0x0F,(MDT >> 0) & 0x0F));
    result.append("\nCSD Register:");
    result.append(String.format("\n CSD_STRUCTURE: %d",CSD_STRUCTURE));
    result.append(String.format("\n TAAC:          %d",TAAC));
    result.append(String.format("\n NSAC:          %d",NSAC));
    result.append(String.format("\n TRAN_SPEED:    %d",TRAN_SPEED));
    result.append(String.format("\n CCC:           %d",CCC));
    result.append(String.format("\n READ_BL_LEN:   %d",READ_BL_LEN));
    return result.toString();
  }
}
