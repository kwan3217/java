package org.kwansystems.emulator.arm;

import static org.kwansystems.emulator.arm.BitFiddle.*;
import static org.kwansystems.emulator.arm.Operation.*;
import static org.kwansystems.emulator.arm.Decode.*;

import org.kwansystems.emulator.arm.Decode.DecodeShiftReturn;
import org.kwansystems.emulator.arm.DecodedInstruction.SetFlags;

public enum Thumb2DecodeLine implements DecodeLine {
  LDRlitT1("01001/ddd/iiiiiiii") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      ins.Rd=parse(IR,8,3);
      ins.imm=parse(IR,0,8);
      ins.add=true;
      return true;
    }
  },
  LDRlitT2("11111/00/0/U/10/1/1111//dddd/iiiiiiiiiiii") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      ins.Rd=parse(hw2,12,4);
      ins.imm=parse(hw2,0,12);
      ins.add=parseBit(hw1,7);
      return true;
    }
  },
  LDRimmT1("011/0/1/iiiii/nnn/ddd") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      ins.imm=parse(IR,6,5)<<2;
      ins.Rn=parse(IR,3,3);
      ins.Rd=parse(IR,0,3);
      ins.index=true;
      ins.add=true;
      ins.wback=false;
      return true;
    }
  },
  LDRimmT2("1001/1/ddd/iiiiiiii") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      ins.imm=parse(IR,0,8)<<2;
      ins.Rn=13;
      ins.Rd=parse(IR,8,3);
      ins.index=true;
      ins.add=true;
      ins.wback=false;
      return true;
    }
  },
  LDRimmT3("11111/00/0/1/10/1/nnnn//dddd/iiiiiiiiiiii") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      ins.imm=parse(hw2,0,12);
      ins.Rn=parse(hw1,0,4);
      ins.Rd=parse(hw2,12,4);
      ins.index=true;
      ins.add=true;
      ins.wback=false;
      if(ins.Rn==15) return false; //if n==15 then SEE LDR (literal) on page 4-102
      return true; 
    }
  },
  LDRimmT4("11111/00/0/0/10/1/nnnn//dddd/1PUW/iiiiiiii") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      ins.imm=parse(hw2,0,8);
      ins.Rn=parse(hw1,0,4);
      ins.Rd=parse(hw2,12,4);
      int P=parseBit(hw2,10)?1:0;
      int U=parseBit(hw2, 9)?1:0;
      int W=parseBit(hw2, 8)?1:0;
      ins.index=P==1;
      ins.add=U==1;
      ins.wback=W==1;
      if(ins.Rn==15) return false; //SEE LDR(literal) on page 4-102
      if(P==1 && U==1 && W==0) return false; //SEE ldrt on page 4-148
      if(P==0 && W==0)  {ins.op=UNDEFINED; return true;}
      if(ins.wback && ins.Rn==ins.Rd) {ins.op=UNPREDICTABLE; return true;}
      return true; 
    }
  },
  ANDregT1("010000/0000/mmm/dnd") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      ins.Rd=parse(IR,0,3);
      ins.Rn=ins.Rd;
      ins.Rm=parse(IR,3,3);
      ins.setflags=DecodedInstruction.SetFlags.NOT_IN_IT;
      ins.shift_t=SRType.NONE;
      ins.shift_n=0;
      return true; //if n==15 then SEE LDR (literal) on page 4-102
    }
  },
  ANDregT2("11101/01/0000/S/nnnn/o/iii/dddd/ii/tt/mmmm") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      ins.Rd=parse(hw2,8,4);
      ins.Rn=parse(hw1,0,4);
      ins.Rm=parse(IR,3,3);
      ins.setflags=parseBit(hw1,4)?DecodedInstruction.SetFlags.TRUE:DecodedInstruction.SetFlags.FALSE;
      DecodeShiftReturn r=DecodeImmShift(parse(hw2,4,2),parse(hw2,12,3)<<2|parse(hw2,6,2));
      ins.shift_t=r.shift_t;
      ins.shift_n=r.shift_n;
      if(ins.Rd==15 && ins.setflags==DecodedInstruction.SetFlags.TRUE) return false; //SEE TST (register) on page 4-399
      if(BadReg(ins.Rd) || BadReg(ins.Rn) || BadReg(ins.Rm)) {ins.op=UNPREDICTABLE; return true;}
      return true; //if n==15 then SEE LDR (literal) on page 4-102
    }
  },
  STRimmT1("011/0/0/iiiii/nnn/ddd") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      ins.Rd=parse(IR,0,3);
      ins.Rn=parse(IR,3,3);
      ins.imm=parse(IR,6,5)<<2;
      ins.index=true;
      ins.add=true;
      ins.wback=false;
      return true;
    }
  },
  STRimmT2("1001/0/ddd/iiiiiiii") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      ins.Rd=parse(IR,8,3);
      ins.Rn=13;
      ins.imm=parse(IR,0,8)<<2;
      ins.index=true;
      ins.add=true;
      ins.wback=false;
      ins.op=STRimm;
      return true;
    }
  },
  STRimmT3("11111/00/0/1/10/0/nnnn//dddd/iiiiiiiiiiii") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      ins.Rd=parse(hw2,12,4);
      ins.Rn=parse(hw1, 0,4);
      ins.imm=parse(hw2,0,12);
      ins.index=true;
      ins.add=true;
      ins.wback=false;
      if(ins.Rn==15)  {ins.op=UNDEFINED; return true;}
      if(ins.Rd==15)  {ins.op=UNPREDICTABLE; return true;}
      return true;
    }
  },
  STRimmT4("11111/00/0/0/10/0/nnnn//dddd/1/PUW/iiiiiiii") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      ins.Rd=parse(hw2,12,4);
      ins.Rn=parse(hw1, 0,4);
      ins.imm=parse(hw2,0,8);
      ins.index=parseBit(hw2,10);
      ins.add  =parseBit(hw2, 9);
      ins.wback=parseBit(hw2, 8);
      if(ins.index && ins.add && !ins.wback) return false; //SEE STRT on page 4-363
      if(ins.Rn==15 || (!ins.index && !ins.wback))    {ins.op=UNDEFINED;     return true;}
      if(ins.Rd==15 || (ins.wback && ins.Rn==ins.Rd)) {ins.op=UNPREDICTABLE; return true;}
      return true;
    }
  },
  CMPregT1("010000/1010/mmm/nnn") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      ins.Rn=parse(IR,0,3);
      ins.Rm=parse(IR,3,3);
      ins.shift_t=SRType.NONE;
      ins.shift_n=0;
      return true;
    }
  },
  CMPregT2("010001/01/N/mmmm/nnn",CMPreg) {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      ins.Rn=parse(IR,7,1)<<3 | parse(IR,0,3);
      ins.Rm=parse(IR,3,4);
      ins.shift_t=SRType.NONE;
      ins.shift_n=0;
      if(ins.Rn<8 && ins.Rm<8) {ins.op=UNPREDICTABLE; return true;}
      if(ins.Rn==15) {ins.op=UNPREDICTABLE; return true;}
      return true;
    }
  },
  ITT1("1011/1111/cccc/mmmm") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      ins.mask=parse(IR,0,4);
      if(ins.mask==0b0000) return false; //SEE NOP-compatible int instructions on page 3-32
      ins.firstcond=parse(IR,4,4);
      if(ins.firstcond==0b0000) {ins.op=UNPREDICTABLE; return true;}
      if(ins.firstcond==0b1110 && BitCount(ins.mask)!=1) {ins.op=UNPREDICTABLE; return true;}
      return true;
    }
  },
  BT1("1101/cccc/iiiiiiii") { //Conditional branch, not usable inside an IT block
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      ins.imm=signExtend(parse(IR,0,8)<<1,9);
      int cond=parse(IR,8,4);
      if(cond==0x1110) return false; //SEE Permanently Undefined Space on page 3-36
      if(cond==0x1111) return false; //SEE SVC (formerly SWI) on page 4-375
      ins.cond=ConditionCode.enumValues[cond];
      //TODO - if InITBlock() then UNPREDICTABLE;
      return true;
    }
  },
  BT2("11100/iiiiiiiiiii") { //Unconditional branch, usable outside of or as last instruction of IT block 
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      ins.imm=signExtend(parse(IR,0,8)<<1,9);
      //TODO - if InITBlock() && !LastInITBlock() then UNPREDICTABLE;
      return true;
    }
  },
  BT3("11110/S/cccc/iiiiii//10/j/0/J/iiiiiiiiiii") { //32-bit conditional branch, not allowed in IT block
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      int bitpos=1;
      int len=0;
      ins.imm=0;
      len= 1;ins.imm=writeField(ins.imm,bitpos,len,0                );bitpos+=len; //0
      len=11;ins.imm=writeField(ins.imm,bitpos,len,parse(hw2, 0,len));bitpos+=len; //imm11
      len= 6;ins.imm=writeField(ins.imm,bitpos,len,parse(hw1, 0,len));bitpos+=len; //imm6
      len= 1;ins.imm=writeField(ins.imm,bitpos,len,parse(hw2,13,len));bitpos+=len; //J1
      len= 1;ins.imm=writeField(ins.imm,bitpos,len,parse(hw2,11,len));bitpos+=len; //J2
      len= 1;ins.imm=writeField(ins.imm,bitpos,len,parse(hw1,10,len));bitpos+=len; //S
      ins.imm=signExtend(ins.imm,bitpos);
      int cond=parse(IR,8,4);
      if(parse(cond,1,3)==0x111) return false; //SEE branches, miscellaneous control instructions on page 3-31
      ins.cond=ConditionCode.enumValues[cond];
      //TODO - if InITBlock() then UNPREDICTABLE;
      return true;
    }
  },
  BT4("11110/S/iiiiiiiiii//10/j/1/J/iiiiiiiiiii") { //32-bit unconditional branch, allowed outside of or at end of IT block
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      int bitpos=1;
      int len=1;
      ins.imm=0; 
      len= 1;ins.imm=writeField(ins.imm,bitpos,len,0                );bitpos+=len; //0
      len=11;ins.imm=writeField(ins.imm,bitpos,len,parse(hw2, 0,len));bitpos+=len; //imm11
      len=10;ins.imm=writeField(ins.imm,bitpos,len,parse(hw1, 0,len));bitpos+=len; //imm10
      len= 1;ins.imm=writeField(ins.imm,bitpos,len,parse(hw2,11,len));bitpos+=len; //J2
      len= 1;ins.imm=writeField(ins.imm,bitpos,len,parse(hw2,13,len));bitpos+=len; //J1
      len= 1;ins.imm=writeField(ins.imm,bitpos,len,parse(hw1,10,len));bitpos+=len; //S
      ins.imm=signExtend(ins.imm,bitpos);
      //TODO - if InITBlock() && !LastInITBlock() then UNPREDICTABLE;
      return true;
    }
  },
  MOVimmT2("11110/i/0/0010/S/1111/0/iii/dddd/iiiiiiii") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      ins.Rd=parse(hw2,8,4);
      ins.setflags=parseBit(hw1,4)?SetFlags.TRUE:SetFlags.FALSE;
      int bitpos=1;
      int len=1;
      ins.imm=0; 
      len= 8;ins.imm=writeField(ins.imm,bitpos,len,parse(hw2, 0,len));bitpos+=len; //imm8
      len= 3;ins.imm=writeField(ins.imm,bitpos,len,parse(hw2,12,len));bitpos+=len; //imm3
      len= 1;ins.imm=writeField(ins.imm,bitpos,len,parse(hw1,10,len));bitpos+=len; //i
      ins.thumbExpand=true; //Can't evaluate ThumbExpandImmWithC here since APSR.c is an execute stage thing
      return true;
    }    
  };
  private int moneBits, mzeroBits;
  public final Operation mop;
  public int oneBits()  {return moneBits;};
  public int zeroBits() {return mzeroBits;};
  public Operation op() {return mop;};
  private void loadMasks(String bitpattern) {
    int[] masks=DecodeLine.interpretBitPattern(bitpattern);
    moneBits =masks[1];
    mzeroBits=masks[0];
  }
  private static Operation getOp(String name) {
    return Operation.valueOf(name.substring(0,name.length()-2));
  }
  private Thumb2DecodeLine(String bitpattern, Operation op) {
    loadMasks(bitpattern);
    mop=op;
  }
  private Thumb2DecodeLine(String bitpattern) {
    loadMasks(bitpattern);
    mop=getOp(toString());
  }
}
