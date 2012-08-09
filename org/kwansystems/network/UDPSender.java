package org.kwansystems.network;

import java.io.*;
import java.net.*;

public class UDPSender {
  public static void main(String[] args) throws IOException {
    DatagramSocket sock=new DatagramSocket();
    byte[] data=new byte[1024];
    DatagramPacket pack=new DatagramPacket(data,1024);
    pack.setAddress(InetAddress.getByName("127.0.0.1"));
    pack.setData(new byte[] {94,93,92,91,90,100,99,98,97,96,95});
    pack.setPort(1234);
    sock.send(pack);
    sock.close();
  }

}
