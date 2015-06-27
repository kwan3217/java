package org.kwansystems.emulator.cips;

public class Disassembler {
  public static String[] opcode=new String[] {
    //0        1      2       3       4       5       6       7
    //  8       9       A       B       C       D       E       F
/*0*/"mov  ","lr   ","str  ","call ","movc ","inr  ","otr  ","???  ",
/*0*/  "add  ","addc ","ab   ","addu ","sub  ","subb ","sb   ","cmp  ",
/*1*/"and  ","or   ","xor  ","not  ","rbr  ","sbr  ","tbr  ","!!!  ",
/*1*/  "slr  ","sar  ","scr  ","muls ","movb ","swab ","divs ","jc   "
  };
  public static String[] reg=new String[] {
    //0        1       2       3       4       5       6       7
    //  8       9       A       B       C       D       E       F
/*0*/"r0  ", "r1  ", "r2  ", "r3  ", "r4  ", "r5  ", "r6  ", "r7  ",
/*0*/  "r8  ", "r9  ", "r10 ", "r11 ", "r12 ", "r13 ", "r14 ", "r15 ",
/*1*/"xr0 ", "r16 ", "xr2 ", "r17 ", "xr4 ", "xr16", "xr6 ", "nul ",
/*1*/  "xr8 ", "r18 ", "xr10", "r19 ", "xr12", "xr18", "xr14", "acc "
  };
  
  public static String[] cond=new String[] {
    //0       1       2       3       4       5       6       7
    //  8       9       A       B       C       D       E       F
/*0*/"ncv ", "nv  ", "nc  ", "j   ", "lt  ", "clt ", "vlt ", "vclt",
/*0*/  "eq  ", "ceq ", "veq ", "vceq", "le  ", "cle ", "vle ", "vcle",
/*1*/"gt  ", "cgt ", "vgt ", "vcgt", "ne  ", "cne ", "vne ", "vcne",
/*2*/  "ge  ", "cge ", "vge ", "vcge", "x   ", "c   ", "v   ", "vc  "
  };

  public static String[] inr=new String[] {
    //0       1       2       3       4       5       6       7
    //  8       9       A       B       C       D       E       F
/*0*/"     ","     ","     ","     ","pi   ","mk   ","ft   ","sw   ",
/*0*/  "     ","     ","     ","     ","     ","     ","acc  ","sp   ",
/*1*/"tah  ","tbh  ","ta   ","tb   ","rcvr ","ics  ","stats","     ",
/*1*/  "     ","     ","     ","     ","     ","     ","     ","     "
  };
  public static String[] otr=new String[] {
    //0       1       2       3       4       5       6       7
    //  8       9       A       B       C       D       E       F
/*0*/"rpi  ","dsbl ","rft  ","pipe ","pi   ","mk   ","ft   ","sw   ",
/*0*/  "     ","     ","     ","     ","     ","ccoff","enbl ","sp   ",
/*1*/"tas  ","tbs  ","ta   ","tb   ","txmt ","     ","     ","ccon ",
/*1*/  "     ","     ","     ","     ","     ","     ","     ","     "
  };

  public static String toBin(int data, int bits) {
    StringBuffer result=new StringBuffer("");
    for(int i=bits-1;i>=0;i--) {
      result.append((data & (1<<i))>0?"1":"0");
    }
    return result.toString();
  }
  public static boolean doesInsHaveImm(long hex) {
    long lit=(hex & 0x8000L)>>>15;
    long src=(hex & 0x001FL)>>> 0;
    return (lit==0 && src==0x1F);
  }
  public static long add(long hex, long addr) {
    long lit=(hex & 0x8000L)>>>15;
    long opc=(hex & 0x7C00L)>>>10;
    long src=(hex & 0x001FL)>>> 0;
    if(opc==0x1F) { //jc
      if(lit==1) {
        return addr+1;
      } 
      if(src==0x1F) {
        return addr+2;
      }
    }
    return 0;
  }
  public static String disasm(long hex, long next, long addr) {
    StringBuffer result=new StringBuffer(disasm1(hex,addr));
    if(doesInsHaveImm(hex)) {
      result.append("\n");
      result.append(disasm2(next,add(hex,addr),addr+1));
    }
    return result.toString();
  }
  public static String disasm1(long hex, long addr) {
    StringBuffer result=new StringBuffer("");
    boolean imm=false;
    boolean nolit=false;
    long lit=(hex & 0x8000)>>>15;
    long opc=(hex & 0x7C00)>>>10;
    String mn=opcode[(int)opc];
    long dst=(hex & 0x03E0)>>> 5;
    long src=(hex & 0x001F)>>> 0;
    String dr=reg[(int)dst];
    String sr=reg[(int)src];
    switch((int)opc) {
      case 0x00: //mov
        if(lit==0 && dst==0 && src==0) {
          mn="nop  ";
          sr=null;
          dr="     ";
        }
        break;
      case 0x01: //lr
        if(lit==0 & src==0x17) {
          mn="pop  ";
          sr=null;
        } else {
          if(lit==1 & src==0x1F) {
            mn="lri  ";
            nolit=true;
          }
        }
        break;
      case 0x02: //str
        if(lit==0 & src==0x17) {
          mn="push ";
          sr=null;
        } else {
          if(lit==1 & src==0x1F) {
            mn="stri ";
            nolit=true;
          }
        }
        break;
      case 0x1F: //jc
        if(lit==1) {
          mn="br   ";
          sr=null;
        } 
        dr=cond[(int)dst];
        break;
      case 0x05: //inr
        if(lit==1) {
          sr=inr[(int)src];
          nolit=true;
        }
        break;
      case 0x06: //otr
        if(lit==1) {
          sr=otr[(int)src];
          nolit=true;
        } 
        break;
    }
    imm=doesInsHaveImm(hex);
    if(lit==1) {
      if(!nolit) {
        long thisSrc=src;
        if(src>15) thisSrc-=32;
        sr=String.format("%s%02X",(thisSrc<0?"-":"+"),(int)(Math.abs(thisSrc)));
      }
      nolit=false;
    }
    result.append(String.format("%05X   %04X   %01X %02X %02X %02X    %s %s ",addr,hex & 0x0FFFF,lit,opc,dst,src,mn,dr));
    if(sr!=null) {
      result.append(",  ");
      if(!imm) result.append(sr);
    }
    return result.toString();
  }
  public static String disasm2(long next, long add, long addr) {
    long target=(add+next) & 0xFFFFL;
    return String.format("%05X   %04X                               %04X",addr,next,target);
  }
  
  public static String disasm(long[] data) {
    return disasm(data,0);
  }
  public static String disasm(long[] data, long startAddr) {
    return disasm(data,startAddr,data.length);
  }
  public static String disasm(long[] data, long startAddr, long length) {
    StringBuffer result=new StringBuffer("");
    for(int i=0;i<length;i++) {
      long addr=i+startAddr;
      long hex=data[(int)addr];
      long next=0;
      if(addr+1<startAddr+length) next=data[(int)addr+1];
      result.append(disasm(hex,next,addr));
      result.append("\n");
      if(doesInsHaveImm(hex))i++;
    }
    return result.toString();
  }
}
