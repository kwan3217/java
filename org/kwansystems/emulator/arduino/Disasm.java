package org.kwansystems.emulator.arduino;

import java.io.*;
import java.util.*;

public class Disasm {
  public static String printBinary(byte b) {
    String result="";
    for(int i=0;i<4;i++) {
      result=(((b>>>i) & 0x01)>0?"1":"0")+result;
    }
    result="|"+result;
    for(int i=4;i<8;i++) {
      result=(((b>>>i) & 0x01)>0?"1":"0")+result;
    }
    return result;
  }
  SortedSet<Integer> labels=new TreeSet<Integer>();
//  int getField(byte lobyte, byte hibyte, )
  public void disasm2(byte hibyte, byte lobyte) {
    switch(hibyte) {
      case (byte)0x24:
      case (byte)0x25:
      case (byte)0x26:
      case (byte)0x27:
        byte r=(byte)(((lobyte>>>0) & 0x0F)+((hibyte>>>1) & 0x01));
        byte d=(byte)(((lobyte>>>4) & 0x0F)+((hibyte>>>0) & 0x01));
        System.out.printf("   EOR   R%02d,   R%02d\n",r,d);
        return;
      default:
        System.out.println("   ???");
    }
  }
  public void disasmB(byte hibyte, byte lobyte) {
    switch(hibyte) {
      case (byte)0xB8:
      case (byte)0xB9:
      case (byte)0xBA:
      case (byte)0xBB:
      case (byte)0xBC:
      case (byte)0xBD:
      case (byte)0xBE:
      case (byte)0xBF:
        byte r=(byte)(((lobyte>>>4) & 0x0F)+((hibyte>>>0) & 0x01));
        byte A=(byte)((lobyte>>>0) & 0x0F);
        byte B=(byte)((hibyte>>>1) & 0x03);
        A+=B;
        System.out.printf("   OUT   %02x,   R%02d\n",A,r);
        return;
      default:
        System.out.println("   ???");
    }
  }
  public void disasm9(byte hibyte, byte lobyte) {
    switch(hibyte) {
      case (byte)0x94:
      case (byte)0x95:
        if((lobyte & 0x0E)==0x0C) {
          System.out.println("   JMP");
          pc++;
          System.out.printf("%04X:%02X %02X %s %s",pc,hinext,lonext,printBinary(hinext),printBinary(lonext));
          int addr=(((lobyte & 0x1)<<21)+(((lobyte>>>4) & 0x0F)<<17)+((lobyte & 0x01)<<16)+(hinext<<8)+lonext)&0x3FFFFF;
          System.out.printf("         %06X\n",addr);
          labels.add(addr);
          return;
        }
      default:
        System.out.println("   ???");
    }
  }
  byte[] programMemory;
  boolean isExt;
  byte hibyte,lobyte;
  byte hinext,lonext;
  int pc;
  public Disasm(String s) throws IOException {
    programMemory=IntelHex.readFile(s);
  }
  public void doIt() {
    isExt=false;
    pc=0;
    while(pc*2<programMemory.length) {
      lobyte=programMemory[pc*2];
      hibyte=programMemory[pc*2+1];
      lonext=(pc*2+2<programMemory.length)?programMemory[pc*2+2]:0;
      hinext=(pc*2+3<programMemory.length)?programMemory[pc*2+3]:0;
      System.out.printf("%04X:%02X %02X %s %s",pc,hibyte,lobyte,printBinary(hibyte),printBinary(lobyte));
      if(!isExt){
        switch ((hibyte>>>4) & 0x0F) {
          case 0x2:
            disasm2(hibyte,lobyte);
            break;
          case 0x9:
            disasm9(hibyte,lobyte);
            break;
          case 0xB:
            disasmB(hibyte,lobyte);
            break;
          default:
            System.out.println("   ???");
        }
        pc++;
      }
    }
  }
  public static void main(String args[]) throws IOException {
    Disasm D=new Disasm("C:\\Documents and Settings\\chrisj\\My Documents\\Arduino\\DontDoAnything\\applet\\DontDoAnything.hex");
    D.doIt();
  }
}
