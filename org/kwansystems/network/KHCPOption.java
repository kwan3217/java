package org.kwansystems.network;

import static org.kwansystems.network.KHCPPacket.*;
import java.net.*;

public class KHCPOption {
  int tag;
  int length;
  byte[] data;
  static String[] optionNames=new String[256];
  public int dataLength() {
    if(tag==0) return 0;
    if(tag==255) return 0;
    return length;
  }
  public boolean isFixedTag() {
    if(tag==0) return true;
    if(tag==255) return true;
    return false;
  }
  public int totalLength() {
    if(isFixedTag()) return 1+dataLength();
    return 2+dataLength();
  }
  public KHCPOption(byte[] Ldata) {
    tag=Ldata[0];
    if(tag<0) tag+=256;
    if(!isFixedTag()) {
      length=Ldata[1];
      data=KHCPPacket.subarray(Ldata,2,length);
    } else if(tag==1 | tag==2) {
      length=4;
      data=KHCPPacket.subarray(Ldata,1,length);
    } else {
      length=0;
    }
  }
  public KHCPOption(int Ltag, byte[] Ldata) {
    tag=Ltag;
    data=Ldata;
    if(isFixedTag()) {
      length=dataLength();
    } else {
      length=data.length;
    }
  }
  public KHCPOption(int LTag, byte Ldata) {
    this(LTag,new byte[] {Ldata});
  }
  public KHCPOption(int LTag, short Ldata) {
    this(LTag,packShort(Ldata));
  }
  public KHCPOption(int LTag, int Ldata) {
    this(LTag,packInt(Ldata));
  }
  public KHCPOption(int LTag, String Ldata) {
    this(LTag,Ldata.getBytes());
  }
  public KHCPOption(int LTag, InetAddress Ldata) {
    this(LTag,Ldata.getAddress());
  }
  public KHCPOption(int LTag, int quad0, int quad1, int quad2, int quad3) {
    this(LTag,new byte[] {(byte)quad0,(byte)quad1,(byte)quad2,(byte)quad3});
  }
  public String interpret() {
    StringBuffer result=new StringBuffer("");
    switch(tag) {
      case 55:
        for(int i=0;i<data.length;i++) {
          int d=data[i];
          if(d<0)d+=256;
          result.append(String.format("%3d - %s\n",d,optionNames[d]));
        }
        return result.toString();
      case 53:
        return msgTypeNames[data[0]];
      case 50:
        return KHCPPacket.toDottedQuad(data);
      default:
        return KHCPServer.hexDump(data);
    }
  }
  public byte[] pack() {
    byte[] result=new byte[totalLength()];
    int ptr=0;
    result[ptr]=(byte)tag;
    ptr++;
    if(!isFixedTag()) {
      result[ptr]=(byte)length;
      ptr++;
    }
    for(int i=0;i<dataLength();i++) {
      result[ptr]=data[i];
      ptr++;
    }
    return result;
  }
  public String toString() {
    return String.format("Tag: %d %s\nLength %d\n%s",tag,optionNames[tag],length,interpret());
  }
  static {
    optionNames[  0]="Pad Option";
    optionNames[  1]="Subnet Mask";
    optionNames[  2]="Time Offset";
    optionNames[  3]="Router Option";
    optionNames[  4]="Time Server Option";
    optionNames[  5]="Name Server Option";
    optionNames[  6]="Domain Name Server Option";
    optionNames[  7]="Log Server Option";
    optionNames[  8]="Cookie Server Option";
    optionNames[  9]="LPR Server Option";
    optionNames[ 10]="Impress Server Option";
    optionNames[ 11]="Resource Location Server Option";
    optionNames[ 12]="Host Name Option";
    optionNames[ 13]="Boot File Size Option";
    optionNames[ 14]="Merit Dump File";
    optionNames[ 15]="Domain Name";
    optionNames[ 16]="Swap Server";
    optionNames[ 17]="Root Path";
    optionNames[ 18]="Extensions Path";
    optionNames[ 19]="IP Forwarding Enable/Disable Option";
    optionNames[ 20]="Non-Local Source Routing Enable/Disable Option";
    optionNames[ 21]="Policy Filter Option";
    optionNames[ 22]="Maximum Datagram Reassembly Size";
    optionNames[ 23]="Default IP Time-to-live";
    optionNames[ 24]="Path MTU Aging Timeout Option";
    optionNames[ 25]="Path MTU Plateau Table Option";
    optionNames[ 26]="Interface MTU Option";
    optionNames[ 27]="All Subnets are Local Option";
    optionNames[ 28]="Broadcast Address Option";
    optionNames[ 29]="Perform Mask Discovery Option";
    optionNames[ 30]="Mask Supplier Option";
    optionNames[ 31]="Perform Router Discovery Option";
    optionNames[ 32]="Router Solicitation Address Option";
    optionNames[ 33]="Static Route Option";
    optionNames[ 34]="Trailer Encapsulation Option";
    optionNames[ 35]="ARP Cache Timeout Option";
    optionNames[ 36]="Ethernet Encapsulation Option";
    optionNames[ 37]="TCP Default TTL Option";
    optionNames[ 38]="TCP Keepalive Interval Option";
    optionNames[ 39]="TCP Keepalive Garbage Option";
    optionNames[ 40]="Network Information Service Domain Option";
    optionNames[ 41]="Network Information Servers Option";
    optionNames[ 42]="Network Time Protocol Servers Option";
    optionNames[ 43]="Vendor Specific Information";
    optionNames[ 44]="NetBIOS over TCP/IP Name Server Option";
    optionNames[ 45]="NetBIOS over TCP/IP Datagram Distribution Server Option";
    optionNames[ 46]="NetBIOS over TCP/IP Node Type Option";
    optionNames[ 47]="NetBIOS over TCP/IP Scope Option";
    optionNames[ 48]="X Window System Font Server Option";
    optionNames[ 49]="X Window System Display Manager Option";
    optionNames[ 50]="Requested IP Address";
    optionNames[ 51]="IP Address Lease Time";
    optionNames[ 52]="Option Overload";
    optionNames[ 53]="DHCP Message Type";
    optionNames[ 54]="Server Identifier";
    optionNames[ 55]="Parameter Request List";
    optionNames[ 56]="Message";
    optionNames[ 57]="Maximum DHCP Message Size";
    optionNames[ 58]="Renewal (T1) Time Value";
    optionNames[ 59]="Rebinding (T2) Time Value";
    optionNames[ 60]="Class-identifier";
    optionNames[ 61]="Client-identifier";
    optionNames[255]="End Option";
  }
  public static String[] msgTypeNames=new String[] {
    "INVALID",
    "DISCOVER",
    "OFFER",
    "REQUEST",
    "DECLINE",
    "ACK",
    "NAK",
    "RELEASE",
    "INFORM"
  };
  public static final byte DHCPDISCOVER=1;
  public static final byte DHCPOFFER=2;
  public static final byte DHCPREQUEST=3;
  public static final byte DHCPDECLINE=4;
  public static final byte DHCPACK=5;
  public static final byte DHCPNAK=6;
  public static final byte DHCPRELEASE=7;
  public static final byte DHCPINFORM=8;
}

