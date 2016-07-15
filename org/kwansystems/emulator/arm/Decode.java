package org.kwansystems.emulator.arm;

import static org.kwansystems.emulator.arm.IntegerOperation.UNDEFINED;
import java.util.*;

public abstract class Decode {
  public static class DecodeShiftReturn {
    public SRType shift_t;
    public int shift_n;
    public DecodeShiftReturn(SRType Lshift_t, int Lshift_n) {shift_t=Lshift_t;shift_n=Lshift_n;};
    public DecodeShiftReturn() {this(SRType.NONE,0);};
  }
  public static DecodeShiftReturn DecodeImmShift(int type, int imm) {
    DecodeShiftReturn r=new DecodeShiftReturn();
    switch(type) {
      case 0:
        r.shift_t=SRType.LSL;
        r.shift_n=imm;
        break;
      case 1:
        r.shift_t=SRType.LSR;
        r.shift_n=(imm==0)?32:imm;
        break;
      case 2:
        r.shift_t=SRType.ASR;
        r.shift_n=(imm==0)?32:imm;
        break;
      default: //case 3
        if(imm==0) {
          r.shift_t=SRType.RRX;
          r.shift_n=1;
        } else {
          r.shift_t=SRType.ROR;
          r.shift_n=imm;
        }
    }
    return r;
  }
  public static boolean BadReg(int n) {
    return n==13 || n==15; //Can't use sp or pc in lots of places
  }
  
  public Map<String,DecodeLine[]> lines=new LinkedHashMap<String,DecodeLine[]>();

  public DecodedInstruction decode(int IR, int pc) {
    DecodedInstruction ins=new DecodedInstruction();
    ins.pc=pc;
    for(DecodeLine[] table:lines.values()) {
      for(DecodeLine line:table) {
        if(((line.oneBits () &  IR ) == line.oneBits ()) && //the one  bits match
           ((line.zeroBits() &(~IR)) == line.zeroBits()) && //the zero bits match
             line.decode(IR, ins)) {                        //the encoding-specific routine says OK
          System.out.printf("Decoded %08x as %s\n",IR,line.toString());
          if(ins.op==null) ins.op=line.op();
          return ins; //The instruction is decoded, we're outtahere.
        }
      }
    }
    System.out.printf("Decoded %08x as UNDEFINED\n",IR);
    ins.imm=IR;
    ins.op=UNDEFINED;
    return ins;
  }
  public abstract void flushPipeline();
}
