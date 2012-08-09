/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.rollercoasterometer;

import org.kwansystems.*;
import java.io.*;

/**
 *
 * @author jeppesen
 */
public class SIRFPacket {
  public byte head1,head2;
  public short length;
  public byte[] payloadBytes;
  public SIRFPayload payload;
  public short checksum;
  public byte foot1,foot2;
  public String packetName;
  public boolean isComplete() {
    if(foot1!=(byte)0xB0) return false;
    if(foot2!=(byte)0xB3) return false;
    return true;
  }
  public SIRFPacket(RandomAccessFile inf) throws IOException {
    while(head1!=(byte)0xA0) {
      head1=inf.readByte();
    }
    long rewind=inf.getFilePointer();
    head2=inf.readByte();
    length=inf.readShort();
    if(length>=0) {
      payloadBytes=new byte[length];
      inf.read(payloadBytes);
      checksum=inf.readShort();
      foot1=inf.readByte();
      foot2=inf.readByte();
    }
    if(isComplete()) {
      payload=SIRFPayload.interpret(payloadBytes);
    } else {
      inf.seek(rewind);
    }
  }
  public SIRFPacket(String S) {
    if(S.length()>=4) head1=(byte)Short.parseShort(S.substring(2,4), 16);
    if(S.length()>=6) head2=(byte)Short.parseShort(S.substring(4,6), 16);
    if(S.length()>=10) length=(short)Integer.parseInt(S.substring(6,10), 16); else length=0;
    payloadBytes=new byte[length];
    if(S.length()>=10+length*2) for(int i=0;i<length;i++) {
      payloadBytes[i]=(byte)Short.parseShort(S.substring(10+i*2,12+i*2),16);
    }
    if(S.length()>=14+length*2) checksum=(short)Integer.parseInt(S.substring(10+length*2,14+length*2),16);
    if(S.length()>=16+length*2) foot1=(byte)Short.parseShort(S.substring(14+length*2,16+length*2), 16);
    if(S.length()>=18+length*2) foot2=(byte)Short.parseShort(S.substring(16+length*2,18+length*2), 16);
    if(isComplete()) {
      payload=SIRFPayload.interpret(payloadBytes);
    }
  }
  public String toStringHeader() {
    StringBuffer result=new StringBuffer();
    result.append(String.format("Header (should be 0xA0A2): %02X%02X\n",head1,head2));
    result.append(String.format("Length:                    %d\n",length));
    if(payloadBytes.length>0) {
      result.append(String.format("Decimal message id:        %d\n",(payloadBytes[0]<0)?256+(int)payloadBytes[0]:payloadBytes[0]));
    }
    return result.toString();
  }
  public String toStringFooter() {
    StringBuffer result=new StringBuffer();
    result.append(String.format("Checksum:                  %04X\n",checksum));
    result.append(String.format("Footer (should be 0xB0B3): %02X%02X\n",foot1,foot2));
    return result.toString();
  }
  public String toString() {
    StringBuffer result=new StringBuffer(toStringHeader());
    result.append("Payload: \n");
    for(int i=0;i<length;i++) result.append(String.format("%02X",payloadBytes[i]));
    result.append("\n");
    if(payload!=null) result.append(payload.toString()+"\n");
    result.append(toStringFooter());
    return result.toString();
  }
  public static void main(String[] args) throws IOException {
    int filenum=3;
    RandomAccessFile inf=new RandomAccessFile(String.format("g:\\lok1_%03d.bin",filenum),"r");
    PrintWriter gpx=new PrintWriter(new FileWriter(String.format("Data/rollercoasterometry/lok1_%03d.gpx",filenum)));
    PrintWriter csv=new PrintWriter(new FileWriter(String.format("Data/rollercoasterometry/lok1_%03d.csv",filenum)));
    gpx.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>");
    gpx.println("<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"Rollercoasterometer\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">");
    gpx.println(" <trk>");
    gpx.println("  <trkseg>");
    double adcBin=1;
    try {
      while(true) {
        SIRFPacket thisPacket=new SIRFPacket(inf);

        if(thisPacket.isComplete() /*&&
           (!(thisPacket.payload instanceof SIRFPayload2C))  || /&&
           (thisPacket.payload instanceof SIRFPayload15))*/
          )
        System.out.println(thisPacket);
        if(thisPacket.isComplete()) {
          if(thisPacket.payload instanceof SIRFPayload29) {
            SIRFPayload29 P=(SIRFPayload29)thisPacket.payload;
            gpx.printf ("   <trkpt lat=\"%12.7f\" lon=\"%12.7f\">",P.Lat,P.Lon);gpx.println();
            gpx.printf ("    <ele>%8.2f</ele>",P.Alt);gpx.println();
            gpx.printf ("    <time>%04d-%02d-%02dT%02d:%02d:%06.3f</time>",P.UTCYear,P.UTCMonth,P.UTCDay,P.UTCHour,P.UTCMinute,P.UTCSecond);gpx.println();
            gpx.println("   </trkpt>");
          } else if(thisPacket.payload instanceof SIRFPayload2A) {
            SIRFPayload2A C=(SIRFPayload2A)thisPacket.payload;
            csv.print("Logomatic subsec");
            for(int i=0;i<C.channel.length;i++) {
              csv.printf(",AD%d (%d.%d)",C.pinLabel[i],C.side[i],C.channel[i]);
            }
            adcBin=C.adcBin;
            csv.printf(",Sample Hz:,%d,Bin Rate:,%d", C.adcFreq,C.adcBin);
            csv.println();
          } else if(thisPacket.payload instanceof SIRFPayload2C) {
            SIRFPayload2C A=(SIRFPayload2C)thisPacket.payload;
            csv.printf("%10.6f",((double)A.ctc1)/60e6);
            for(int i=0;i<A.adc.length;i++) {
              csv.printf(",%8.6f",((double)A.adc[i])/1024.0/adcBin);
            }
            csv.println();
          }
        }
      }
    } catch (IOException e) {

    }
    gpx.println("  </trkseg>");
    gpx.println(" </trk>");
    gpx.println("</gpx>");
    gpx.close();
    csv.close();
    inf.close();
  }
}
