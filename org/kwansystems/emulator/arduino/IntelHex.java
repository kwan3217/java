package org.kwansystems.emulator.arduino;

import java.io.*;

public class IntelHex {
  static class IntelHexLine {
    byte byteCount;
    int address;
    byte recordType;
    byte[] data;
    byte checksum;
    private boolean valid;
    public boolean isValid() {
      return true;
    }
    public IntelHexLine(String s) {
      if(s.charAt(0)!=':') {
        valid=false;
      } else {
        String ss=s.substring(1,3);
        byteCount=Byte.parseByte(ss, 16);
        ss=s.substring(3,7);
        address=(int)(Short.parseShort(ss,16) & 0xFFFF);
        ss=s.substring(7,9);
        recordType=Byte.parseByte(ss,16);
        data=new byte[(int)(byteCount & 0xFF)];
        for(int i=0;i<data.length;i++) {
          ss="00"+s.substring(9+i*2,11+i*2);
          data[i]=(byte)Integer.parseInt(ss,16);
        }
        ss="00"+s.substring(9+data.length*2,11+data.length*2);
        checksum=(byte)Integer.parseInt(ss,16);
      }
    }
    private static final String[] typeName={
      "Data",
      "End of File",
      "Extended Segment",
      "Start Segment Address",
      "Extended Linear Address",
      "Start Linear Address"
    };
    public String toString() {
      String result="";
      for(int i=0;i<data.length;i++) {
        result+=String.format(" %02X",data[i]);
      }
      return String.format("%s%s",typeName[recordType],result);
    }
  }
  public static byte[] makeLength(byte[] in, int length) {
    if(in.length>=length) return in;
    byte[] out=new byte[length];
    System.arraycopy(in, 0, out, 0, in.length);
    return out;
  }
  public static void main(String[] args) throws IOException {
    byte[] stuff=readFile("C:\\Documents and Settings\\chrisj\\My Documents\\Arduino\\DontDoAnything\\applet\\DontDoAnything.hex");
    
  }
  public static byte[] writeLine(byte[] in, IntelHexLine L) {
    if(L.recordType!=0) return in;
    byte[] result=makeLength(in,L.address+L.data.length);
    System.arraycopy(L.data,0,result,L.address,L.data.length);
    return result;
  }
  public static byte[] writeLine(byte[] in, String s) {
    return writeLine(in,new IntelHexLine(s));
  }
  public static byte[] readFile(byte[] in, LineNumberReader inf) throws IOException {
    String s=inf.readLine();
    if(s==null) return in;
    IntelHexLine L=new IntelHexLine(s);
    byte[] result=in;
    while(L.recordType!=1) {
      result=writeLine(result,L);
      s=inf.readLine();
      L=new IntelHexLine(s);
    }
    return result;
  }
  public static byte[] readFile(LineNumberReader inf) throws IOException {
    return readFile(new byte[0],inf);
  }
  public static byte[] readFile(byte[] in, File infn) throws IOException {
    return readFile(in,new LineNumberReader(new FileReader(infn)));
  }
  public static byte[] readFile(File infn) throws IOException {
    return readFile(new byte[0],infn);
  }
  public static byte[] readFile(byte[] in, String infn) throws IOException {
    return readFile(in,new File(infn));
  }
  public static byte[] readFile(String infn) throws IOException {
    return readFile(new byte[0],infn);
  }
}
