package org.kwansystems.network;

import java.net.*;
import java.io.*;

import static org.kwansystems.network.KHCPOption.*;
import static org.kwansystems.network.KHCPPacket.*;

public class KHCPServer {
  public static String hexDump(byte[] data) {
    if(data==null) return "";
    StringBuffer result=new StringBuffer("");
    for(int row=0;row*16<data.length;row++) {
      result.append(String.format("%08X ",row*16));
      for(int col=0;col<16;col++) {
        if(row*16+col<data.length) {
          result.append(String.format("%02X",data[row*16+col]%256 & 255));
        } else {
          result.append("  ");
        }
        if(col%4==3) result.append(" ");
      }
      result.append("|");
      for(int col=0;col<16;col++) {
        if(row*16+col<data.length) {
          int d=data[row*16+col]%256 & 255;
          if(d<32 | d>127) {
            result.append(".");
          } else {
            result.append(new String(Character.toChars(d)));
          }
        } else {
          result.append(" ");
        }
      }
      result.append("\n");
    }
    return result.toString();
  }
  public void run() throws IOException {
    byte[] data=new byte[576]; 
    DatagramPacket packet=new DatagramPacket(data,576);
    DatagramSocket sock=new DatagramSocket(67);
    for(;;) {
      sock.receive(packet);
      KHCPPacket kp=new KHCPPacket(packet.getAddress(),packet.getData());
      System.out.println(kp);
      reply(kp);
    }
  }
  public void reply(KHCPPacket kp) throws IOException {
    int msgType=kp.getOption(53)[0];
    switch(msgType) {
      case DHCPDISCOVER:
        //Check the host name, give back an address
        replyDISCOVER(kp);
        break;
      case DHCPOFFER:
        //(Server ignores this)
        break;
      case DHCPREQUEST:
        //Ack/Nak the request
        replyREQUEST(kp);
        break;
      case DHCPDECLINE:
        //(Server ignores this)
      
        break;
      case DHCPACK:
        // (Server ignores this)
        
        break;
      case DHCPNAK:
        //(Server ignores this)
        
        break;
      case DHCPRELEASE:
        //Release this address
        replyRELEASE(kp);
        break;
    } 
  }
  public void fillInRequests(KHCPPacket kp, byte[] requests) {
    for(int i=0;i<requests.length;i++) {
      int request=requests[i] & 0xFF;
      switch(request) {
        case 1:
          kp.addOption(new KHCPOption(request,255,255,255,0));
          break;
        case 3:
        case 4:
        case 6:
          kp.addOption(new KHCPOption(request,192,168,1,1));
          break;
        case 15:
          kp.addOption(new KHCPOption(request,"kwansystems.org"));
          break;
      }
    }
  }
  public void replyDISCOVER(KHCPPacket kp) throws IOException {
    //Check the host name, give back an address
    KHCPPacket kpResponse=new KHCPPacket();
    kpResponse.op=BOOTREPLY;        
    kpResponse.htype=kp.htype;
    kpResponse.hlen=kp.hlen;
    kpResponse.hops=0;
    kpResponse.xid=kp.xid;
    kpResponse.secs=0;
    kpResponse.flags=kp.flags;
    kpResponse.ciaddr=new byte[4];
    kpResponse.yiaddr=getAddressFromHostName(new String(kp.getOption(12))).getAddress();
    kpResponse.siaddr=new byte[4];
    kpResponse.giaddr=new byte[4];
    kpResponse.chaddr=kp.chaddr;
    kpResponse.sname="";
    kpResponse.file="";
    kpResponse.addOption(new KHCPOption(53,new byte[] {DHCPOFFER})); //DHCP Message Type
    kpResponse.addOption(new KHCPOption(51,60));
    kpResponse.addOption(new KHCPOption(54,192,168,1,1));
    fillInRequests(kpResponse,kp.getOption(55));
    kpResponse.addOption(new KHCPOption(255,(byte[])null));                  //End
    byte[] byteOut=kpResponse.pack()   ; 
    DatagramPacket packetResponse=new DatagramPacket(
      byteOut,576,
      InetAddress.getByAddress(new byte[]{-1,-1,-1,-1}),68
    );
    System.out.println(new KHCPPacket(null,byteOut).toString());
    System.out.println(hexDump(byteOut));
    DatagramSocket socket=new DatagramSocket(6800,InetAddress.getByName("192.168.1.1"));
    socket.send(packetResponse);
    socket.close();
  }
  public void replyREQUEST(KHCPPacket kp) throws IOException {
    //Check the host name, give back an address
    KHCPPacket kpResponse=new KHCPPacket();
    kpResponse.op=BOOTREPLY;        
    kpResponse.htype=kp.htype;
    kpResponse.hlen=kp.hlen;
    kpResponse.hops=0;
    kpResponse.xid=kp.xid;
    kpResponse.secs=0;
    kpResponse.flags=kp.flags;
    kpResponse.giaddr=kp.giaddr;
    kpResponse.chaddr=kp.chaddr;
    kpResponse.sname="";
    kpResponse.file="";
    if(isCorrectRequestAddress(kp)) {
      kpResponse.ciaddr=kp.ciaddr;
      kpResponse.yiaddr=getAddressFromHostName(new String(kp.getOption(12))).getAddress();
      kpResponse.siaddr=new byte[4];
      kpResponse.addOption(new KHCPOption(53,new byte[] {DHCPACK})); //DHCP Message Type
      kpResponse.addOption(new KHCPOption(51,60));
      fillInRequests(kpResponse,kp.getOption(55));
    } else {
      kpResponse.ciaddr=kp.ciaddr;
      kpResponse.yiaddr=new byte[4];
      kpResponse.siaddr=new byte[4];
      kpResponse.giaddr=new byte[4];
      kpResponse.addOption(new KHCPOption(53,new byte[] {DHCPNAK})); //DHCP Message Type
    }
    kpResponse.addOption(new KHCPOption(54,192,168,1,1));
    kpResponse.addOption(new KHCPOption(255,(byte[])null));                  //End
    byte[] byteOut=kpResponse.pack(); 
    DatagramPacket packetResponse=new DatagramPacket(
      byteOut,576,
      InetAddress.getByName("255.255.255.255"),68
    );
    System.out.println(hexDump(byteOut));
    System.out.println(new KHCPPacket(null,byteOut).toString());
    DatagramSocket socket=new DatagramSocket(6800,InetAddress.getByName("192.168.1.1"));
    socket.send(packetResponse);
    socket.close();
  }
  public void replyRELEASE(KHCPPacket kp) {
    //Release this address
    
  }
  public static void main(String[] args) throws SocketException,IOException {
    KHCPServer K=new KHCPServer();
    K.run();
  }
  public static final String[] hostList=new String[] {
    "network",
    "dejiko",
    "hyatt",
    "koboshi",
    "nukunuku",
    "mieko",
    "excel",
    "nephi",
    "sasami"
  };
  public static InetAddress getAddressFromHostName(String HostName) throws UnknownHostException {
    for(int i=0;i<hostList.length;i++) {
      if(HostName.trim().equalsIgnoreCase(hostList[i].trim())) {
        return InetAddress.getByName(String.format("192.168.1.%d",i));
      }
    }
    System.out.println("No host name found for:");
    System.out.println(hexDump(HostName.trim().getBytes()));
    return InetAddress.getByName(String.format("192.168.1.%d",10));

  }
  public static boolean isCorrectRequestAddress(KHCPPacket kp) throws UnknownHostException {
    byte[] dapatAddress=getAddressFromHostName(new String(kp.getOption(12))).getAddress();
    byte[] requestAddress=kp.getOption(50);
    if(requestAddress==null) return false;
    for(int i=0;i<4;i++) if(dapatAddress[i]!=requestAddress[i]) return false;
    return true;
  }
}
