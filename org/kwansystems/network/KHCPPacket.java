package org.kwansystems.network;

import java.net.*;
import java.util.*;

public class KHCPPacket {
  public static int getInt(byte[] data,int ofs) {
    long result=0;
    for(int i=0;i<4;i++) {
      result=result << 8;
      result=result | (data[ofs+i] & 0xFF);
    }
    return (int)result;
  }
  public static short getShort(byte[] data,int ofs) {
    long result=0;
    for(int i=0;i<2;i++) {
      result=result << 8;
      result=result | (data[ofs+i] & 0xFF);
    }
    return (short)result;
  }
  public static void setInt(byte[] data,int ofs, int value) {
    for(int i=0;i<4;i++) {
      data[ofs+i]=(byte)((value >>> ((3-i)*8)) & 0xFF);
    }
  }
  public static void setShort(byte[] data,int ofs,short value) {
    for(int i=0;i<2;i++) {
      data[ofs+i]=(byte)((value >>> ((1-i)*8)) & 0xFF);
    }
  }
  public static byte[] packInt(int value) {
    byte[] data=new byte[4];
    for(int i=0;i<4;i++) {
      data[i]=(byte)((value >>> ((3-i)*8)) & 0xFF);
    }
    return data;
  }
  public static byte[] packShort(short value) {
    byte[] data=new byte[2];
    for(int i=0;i<2;i++) {
      data[i]=(byte)((value >>> ((1-i)*8)) & 0xFF);
    }
    return data;
  }
  public static byte[] subarray(byte[] data, int ofs, int len) {
    byte[] result=new byte[len];
    System.arraycopy(data,ofs,result,0,len);
    return result;
  }
  public static byte[] trimarray(byte[] data, int len) {
    return subarray(data,len,data.length-len);
  }
  InetAddress origin;
  byte op,htype,hlen,hops;
  int xid;
  short secs,flags;
  byte[] ciaddr;
  byte[] yiaddr;
  byte[] siaddr;
  byte[] giaddr;
  String sname;
  String file;
  byte[] chaddr;
  byte[] opMagic;
  List<KHCPOption> options;
  public byte[] pack() {
    byte[] data=new byte[576];
    data[0]=op;data[1]=htype;data[2]=hlen;data[3]=hops;
    setInt(data,4,xid);
    setShort(data,8,secs);setShort(data,10,flags);
    System.arraycopy(ciaddr,0,data,12,4);
    System.arraycopy(yiaddr,0,data,16,4);
    System.arraycopy(siaddr,0,data,20,4);
    System.arraycopy(giaddr,0,data,24,4);
    if(chaddr!=null)System.arraycopy(chaddr,0,data,28,16);
    if(sname !=null)System.arraycopy(sname.getBytes(),0,data,44,sname.length());
    if(file  !=null)System.arraycopy(file.getBytes(),0,data,108,sname.length());
    data[236]=opMagic[0];
    data[237]=opMagic[1];
    data[238]=opMagic[2];
    data[239]=opMagic[3];
    if(options!=null) {
      int ptr=240;
      for(KHCPOption O:options) {
        System.arraycopy(O.pack(),0,data,ptr,O.totalLength());
        ptr+=O.totalLength();
      }
    }
    return data;
  }
  public KHCPPacket() {
    ciaddr=new byte[4];
    yiaddr=new byte[4];
    siaddr=new byte[4];
    giaddr=new byte[4];
    chaddr=new byte[16];
    opMagic=new byte[4];
    opMagic[0]=(byte)99;
    opMagic[1]=(byte)130;
    opMagic[2]=(byte)83;
    opMagic[3]=(byte)99;
    options=new ArrayList<KHCPOption>();
  };
  public KHCPPacket(InetAddress Lorigin,byte[] data) {
    origin=Lorigin;
    op=data[0];htype=data[1];hlen=data[2];hops=data[3];
    xid=getInt(data,4);
    secs=getShort(data,8);flags=getShort(data,10);
    ciaddr=subarray(data,12,4);
    yiaddr=subarray(data,16,4);
    siaddr=subarray(data,20,4);
    giaddr=subarray(data,24,4);
    chaddr=subarray(data,28,16);
    sname=new String(subarray(data,44,64));
    file=new String(subarray(data,108,128));
    opMagic=new byte[4];
    opMagic[0]=data[236];
    opMagic[1]=data[237];
    opMagic[2]=data[238];
    opMagic[3]=data[239];
    options=new ArrayList<KHCPOption>();
    data=trimarray(data,240);
    KHCPOption thisOption;
    do {
      thisOption=new KHCPOption(data);
      options.add(thisOption);
      data=trimarray(data,thisOption.totalLength());
    } while(thisOption.tag!=255);
  }
  public byte[] getOption(int Ltag) {
    for(KHCPOption O:options) {
      if(O.tag==Ltag) return O.data;
    }
    return null;
  }
  public void addOption(KHCPOption O) {
    options.add(O);
  }
  public String toString() {
    StringBuffer result=new StringBuffer();
    if(origin!=null)result.append(String.format("origin: %s\n",origin.toString()));
    result.append(String.format("op: %02X htype: %02X hlen: %02X hops: %02X\n",op,htype,hlen,hops));
    result.append(String.format("xid: %08X\n",xid));
    result.append(String.format("secs: %04X flags: %04X\n",secs,flags));
    result.append(String.format("ciaddr: %s\n",toDottedQuad(ciaddr)));
    result.append(String.format("yiaddr: %s\n",toDottedQuad(yiaddr)));
    result.append(String.format("siaddr: %s\n",toDottedQuad(siaddr)));
    result.append(String.format("giaddr: %s\n",toDottedQuad(giaddr)));
    result.append(String.format("chaddr: %02X",chaddr[0]));
    for(int i=1;i<hlen;i++) result.append(String.format(":%02X",chaddr[i]));
    result.append(String.format("\n"));
    result.append(String.format("sname: %s\n",sname));
    result.append(String.format("fname: %s\n",sname));
    for(KHCPOption O : options) {
      result.append(O.toString()+"\n");
    }
    return result.toString();
  }
  public static String toDottedQuad(byte[] inetaddr) {
    if(inetaddr==null)return "";
    StringBuffer result=new StringBuffer();
    for(int i=0;i<inetaddr.length;i++) {
      result.append(inetaddr[i]<0?256+inetaddr[i]:inetaddr[i]);
      if(i<inetaddr.length-1) result.append(".");
    }
    return result.toString();
  }
  public static final byte BOOTREQUEST=1;
  public static final byte BOOTREPLY=2;
}