package org.kwansystems.emulator.cips;

import java.util.*;

public class Assembler {
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
  public static int findInStrArr(String[] lookIn, String lookFor) {
    for(int i=0;i<lookIn.length;i++) {
      if(lookFor.equalsIgnoreCase(lookIn[i].trim())) return i;
    }
    return -1;
  }
  public static boolean doesInsHaveImm(long hex) {
    long lit=(hex & 0x8000L)>>>15;
    long src=(hex & 0x001FL)>>> 0;
    return (lit==0 && src==0x1F);
  }
  public static long[] asm(String asmStatement) {
    long hex=asm1(asmStatement);
    long[] result;
    if(doesInsHaveImm(hex)) {
      long imm=asm2(asmStatement);
      result=new long[2];
      result[0]=hex;
      result[1]=imm;
    } else {
      result=new long[1];
      result[0]=hex;
    }
    return result;
  }
  public static long asm1(String asmStatement) {
    String[] parts=asmStatement.split("[ ,]+");
    String mn=parts[0];
    String Dest;
    String Source;
    Dest=(parts.length>=2)?parts[1]:null;
    Source=(parts.length>=2)?parts[2]:null;
    if(mn.equalsIgnoreCase("br")) {
      mn="jc";
    } else if (mn.equalsIgnoreCase("push")) {
      mn="str";
    } else if (mn.equalsIgnoreCase("stri")) {
      mn="str";
    } else if (mn.equalsIgnoreCase("pop")) {
      mn="lr";
    } else if (mn.equalsIgnoreCase("lri")) {
      mn="lr";
    } else if (mn.equalsIgnoreCase("nop")) {
      mn="mov";
      Dest="r0";
      Source="r0";
    }
    int opc;
    int dst;
    int src;
    int lit;
    int val;
    opc=findInStrArr(opcode,mn);
    if (Source.charAt(0)=='+') Source=Source.substring(1);
    if(Source.matches("-?\\d+")) {
      val=(short)Integer.parseInt(Source);
    } else {
      val=0;
    }
    int special;
    int regN;
    switch(opc) {
      case 0x01: //lr
        if (mn.equalsIgnoreCase("pop")) {
          lit=0;
          src=0x17;
        } else if (mn.equalsIgnoreCase("lri")) {
          //The code for what would otherwise be lr, RDn, literal is interpreted as lri, RDn, acc
          lit=1;
          src=0x1F;
        } else {
          lit=0;
          src=0x1F;
        }
        dst=findInStrArr(reg,Dest);
        break;
      case 0x02: //str
        if (mn.equalsIgnoreCase("pop")) {
          lit=0;
          src=0x17;
        } else if (mn.equalsIgnoreCase("stri")) {
          //The code for what would otherwise be str, RDn, literal is interpreted as stri, RDn, acc
          lit=1;
          src=0x1F;
        } else {
          lit=0;
          src=0x1F;
        }
        dst=findInStrArr(reg,Dest);
        break;
      case 0x03: //call
        lit=0;
        regN=findInStrArr(reg,Source);
        if(regN>=0) {
          src=regN;
        } else {
          src=0x1F;
        }
        dst=findInStrArr(reg,Dest);
        break;
      case 0x1F: //jc
        dst=findInStrArr(cond,Dest);
        lit=(Math.abs(val)>15)?0:1;
        src=(Math.abs(val)>15)?0x1F:val & 0x1F;
        break;
      case 0x05: //inr
        special=findInStrArr(inr,Source);
        if(special>=0) {
          lit=1;
          src=special;
        } else {
          lit=0;
          regN=findInStrArr(reg,Source);
          if(regN>=0) {
            lit=0;
            src=regN;
          } else {
            lit=0;
            src=0x1F;
          }
        }
        dst=findInStrArr(reg,Dest);
        break;
      case 0x06: //otr
        special=findInStrArr(otr,Source);
        if(special>=0) {
          lit=1;
          src=special;
        } else {
          lit=0;
          regN=findInStrArr(reg,Source);
          if(regN>=0) {
            lit=0;
            src=regN;
          } else {
            lit=0;
            src=0x1F;
          }
        }
        dst=findInStrArr(reg,Dest);
        break;
      default:
        dst=findInStrArr(reg,Dest);
        regN=findInStrArr(reg,Source);
        if(regN>=0) {
          lit=0;
          src=regN;
        } else {
          lit=(Math.abs(val)>15)?0:1;
          src=(Math.abs(val)>15)?0x1F:val & 0x1F;
        }
        break;        
    }
    long hex=(lit & 0x01L) <<15 |
            (opc & 0x1FL) <<10 |
            (dst & 0x1FL) << 5 |
            (src & 0x1FL) << 0 ;
    return hex;
  }
  public static long asm2(String asmStatement) {
    String[] parts=asmStatement.split("[ ,]+");
    if(parts.length<3) throw new IllegalArgumentException("Not enough parts in asmStatement "+asmStatement);
    if(parts[2].charAt(0)=='+') parts[2]=parts[2].substring(1);
    return Integer.parseInt(parts[2],16);
  }
  
  public static long[] asm(String[] asmStatements) {
    ArrayList<Long> A=new ArrayList<Long>();
    
    for(int i=0;i<asmStatements.length;i++) {
      long[] code=asm(asmStatements[i]);
      for(int j=0;j<code.length;j++) A.add(code[j]);
    }
    long[] result=new long[A.size()];
    for(int i=0;i<result.length;i++) result[i]=A.get(i);
    return result;
  }
  public static void main(String args[]) {
    long[] code=asm(new String[] {
      "nop",
      "add r0,+01",
      "call r1, 0006",
      "br x,-05",
      "nop",
      "call r1, r1",
    });
    for(int i=0;i<code.length;i++) {
      System.out.println(String.format("%04X",code[i]));
    }
    System.out.println(Disassembler.disasm(code,0));
  }
}
