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
      ins.imm=parse(IR,0,8)<<2;
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
  CMPregT2("010001/01/N/mmmm/nnn") {
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
      int bitpos=0;
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
      int bitpos=0;
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
  MOVimmT1("001/00/ddd/iiiiiiii") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      ins.Rd=parse(IR,8,3);
      ins.setflags=SetFlags.NOT_IN_IT;
      ins.imm=parse(IR,0,8);
      return true;
    }    
  },
  MOVimmT2("1111'0/i/0/0'010/S'1111''0/iii'dddd'iiii'iiii") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      ins.Rd=parse(hw2,8,4);
      ins.setflags=parseBit(hw1,4)?SetFlags.TRUE:SetFlags.FALSE;
      int bitpos=0;
      int len;
      ins.imm=0; 
      len= 8;ins.imm=writeField(ins.imm,bitpos,len,parse(hw2, 0,len));bitpos+=len; //imm8
      len= 3;ins.imm=writeField(ins.imm,bitpos,len,parse(hw2,12,len));bitpos+=len; //imm3
      len= 1;ins.imm=writeField(ins.imm,bitpos,len,parse(hw1,10,len));bitpos+=len; //i
      ins.thumbExpand=true; //Can't evaluate ThumbExpandImmWithC here since APSR.c is an execute stage thing
      return true;
    }    
  },
  MOVimmT3("11110/i/10/0/1/0/0/iiii/0/iii/dddd/iiiiiiii") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      ins.Rd=parse(hw2,8,4);
      if(ins.Rd==13 || ins.Rd==15) {ins.op=UNPREDICTABLE; return true;}
      ins.setflags=SetFlags.FALSE;
      int bitpos=0;
      int len;
      ins.imm=0; 
      len= 8;ins.imm=writeField(ins.imm,bitpos,len,parse(hw2, 0,len));bitpos+=len; //imm8
      len= 3;ins.imm=writeField(ins.imm,bitpos,len,parse(hw2,12,len));bitpos+=len; //imm3
      len= 4;ins.imm=writeField(ins.imm,bitpos,len,parse(hw1, 0,len));bitpos+=len; //imm4
      len= 1;ins.imm=writeField(ins.imm,bitpos,len,parse(hw1,10,len));bitpos+=len; //i
      return true;
    }    
  },
  MOVregT1("010001/10/D/mmmm/ddd") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      ins.Rd=parse(IR,0,3) | parse(IR,7,1)<<3;
      ins.Rm=parse(IR,3,4);
      ins.setflags=SetFlags.FALSE;
      // TODO - if d==15 && InITBlock() && !LastInITBlock() then UNPREDICTABLE;
      return true;
    }
  },
  MOVregT2("000/00/00000/mmm/ddd") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      ins.Rd=parse(IR,0,3);
      ins.Rm=parse(IR,3,3);
      ins.setflags=SetFlags.TRUE;
      // TODO - if InITBlock() then UNPREDICTABLE;
      return true;
    }
  },
  MOVregT3("11101/01/0010/S/1111//o/000/dddd/0000/mmmm") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      ins.Rd=parse(hw2,8,4);
      ins.Rm=parse(hw2,0,4);
      ins.setflags=parseBit(hw1,4)?SetFlags.TRUE:SetFlags.FALSE;
      // TODO - if InITBlock() then UNPREDICTABLE;
      return true;
    }
  },
  SUBimmT1("000/11/1/1/iii/nnn/ddd") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      ins.Rd=parse(IR,0,3);
      ins.Rn=parse(IR,3,3);
      ins.imm=parse(IR,6,3);
      ins.setflags=SetFlags.NOT_IN_IT;
      return true;
    }
  },
  SUBimmT2("001/11/DDD/iiiiiiii") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      ins.Rd=parse(IR,8,3);
      ins.Rn=parse(IR,8,3);
      ins.imm=parse(IR,0,8);
      ins.setflags=SetFlags.NOT_IN_IT;
      return true;
    }
  },
  SUBimmT3("11110/i/0/1101/S/nnnn//0/iii/dddd/iiiiiiii") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      ins.Rd=parse(hw2,8,4);
      ins.Rn=parse(hw1,0,4);
      ins.setflags=parseBit(hw1,4)?SetFlags.TRUE:SetFlags.FALSE;
      if(ins.Rd==0b1111 && ins.setflags==SetFlags.TRUE) return false; //SEE CMP (immediate)
      if(ins.Rn==0b1101) return false; //SEE SUB (SP minus immediate)
      int bitpos=0;
      int len;
      ins.imm=0; 
      len= 8;ins.imm=writeField(ins.imm,bitpos,len,parse(hw2, 0,len));bitpos+=len; //imm8
      len= 3;ins.imm=writeField(ins.imm,bitpos,len,parse(hw2,12,len));bitpos+=len; //imm3
      len= 1;ins.imm=writeField(ins.imm,bitpos,len,parse(hw1,10,len));bitpos+=len; //i
      ins.thumbExpand=true;
      if(ins.Rd==13 || (ins.Rd==15 && ins.setflags==SetFlags.FALSE) || ins.Rn==15) {ins.op=UNPREDICTABLE; return true;}
      return true;
    }
  },
  SUBimmT4("11110/i/1/0101/0/nnnn/0/iii/dddd/iiiiiiii") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      ins.Rd=parse(hw2,8,4);
      ins.Rn=parse(hw1,0,4);
      ins.setflags=SetFlags.FALSE;
      if(ins.Rd==0b1111) return false; //SEE ADR
      if(ins.Rn==0b1101) return false; //SEE SUB (SP minus immediate)
      int bitpos=0;
      int len;
      ins.imm=0; 
      len= 8;ins.imm=writeField(ins.imm,bitpos,len,parse(hw2, 0,len));bitpos+=len; //imm8
      len= 3;ins.imm=writeField(ins.imm,bitpos,len,parse(hw2,12,len));bitpos+=len; //imm3
      len= 1;ins.imm=writeField(ins.imm,bitpos,len,parse(hw1,10,len));bitpos+=len; //i
      if(ins.Rd==13 || ins.Rd==15) {ins.op=UNPREDICTABLE; return true;}
      return true;
    }
  },
  SUBregT1("000/11/0/1/mmm/nnn/ddd") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      ins.Rd=parse(IR,0,3);
      ins.Rn=parse(IR,3,3);
      ins.Rm=parse(IR,6,3);
      ins.setflags=SetFlags.NOT_IN_IT;
      ins.shift_t=SRType.LSL;
      ins.shift_n=0;
      return true;
    }
  },
  SUBregT2("11101/01/1101/S/nnnn/o/iii/dddd/ii/tt/mmmm") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      ins.Rd=parse(hw2,8,4);
      ins.setflags=parseBit(hw1,4)?SetFlags.TRUE:SetFlags.FALSE;
      ins.Rn=parse(hw1,0,4);
      if(ins.Rd==0b1111 && ins.setflags==SetFlags.TRUE) return false; //SEE CMP (register)
      if(ins.Rn==0b1101) return false; //SEE SUB(SP minus register)
      ins.Rm=parse(hw2,6,3);
      DecodeShiftReturn r=DecodeImmShift(parse(hw2,4,2),parse(hw2,6,2) | (parse(hw2,12,3)<<2));
      ins.shift_t=r.shift_t;
      ins.shift_n=r.shift_n;
      if(ins.Rd==13 || (ins.Rd==15 && ins.setflags==SetFlags.FALSE) || ins.Rn==15 || (ins.Rm==13 || ins.Rm==15)) {ins.op=UNPREDICTABLE; return true;}
      return true;
    }
  },
  BXT1("010001/11/0/mmmm/ooo") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      ins.Rm=parse(IR,3,4);
      // TODO - if d==15 && InITBlock() && !LastInITBlock() then UNPREDICTABLE;
      return true;
    }
  },
  STMDBT1("11101/00/100/W/0/nnnn//o/M/o/rrrrrrrrrrrrr") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      ins.wback=parseBit(hw1,5);
      ins.Rn=parse(hw1,0,4);
      if(ins.wback && ins.Rn==0b1101) {
        return false; //SEE PUSH
      }
      //use imm as the register list
      ins.imm=hw2; //Nominally bits 13 and 15 (specifying sp and pc) are supposed to be zero, and it is UNPREDICTABLE if they are not. We will take advantage of that
      if((ins.imm & 0b1010000000000000)!=0) {ins.op=UNPREDICTABLE; return true;}
      if(ins.Rn==15 || BitCount(ins.imm)<2) {ins.op=UNPREDICTABLE; return true;} //If we are using pc as the stack pointer, or storing 1 or 0 registers, it's UNPREDICTABLE
      if(ins.wback && parseBit(ins.imm,ins.Rn)) {ins.op=UNPREDICTABLE; return true;} //If we write back to a register we are also storing, it's UNPREDICTABLE. Actually, the specified operation stores the un-written-back value. 
      return true;
    }
  },
  PUSHT1("1011/0/10/M/rrrrrrrr") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      ins.imm=parse(IR,8,1) << 14 | parse(IR,0,8);
      ins.UnalignedAllowed=false;
      if(BitCount(ins.imm)<2) {ins.op=UNPREDICTABLE; return true;} //If we are pushing 1 or 0 registers, it's UNPREDICTABLE
      return true;
    }
  },
  PUSHT2("11101/00/100/1/0/1101//o/M/o/rrrrrrrrrrrrr") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      //use imm as the register list
      ins.imm=hw2; //Nominally bits 13 and 15 (specifying sp and pc) are supposed to be zero, and it is UNPREDICTABLE if they are not. We will take advantage of that
      if((ins.imm & 0b1010000000000000)!=0) {ins.op=UNPREDICTABLE; return true;}
      if(ins.Rn==15 || BitCount(ins.imm)<2) {ins.op=UNPREDICTABLE; return true;} //If we are using pc as the stack pointer, or storing 1 or 0 registers, it's UNPREDICTABLE
      if(ins.wback && parseBit(ins.imm,ins.Rn)) {ins.op=UNPREDICTABLE; return true;} //If we write back to a register we are also storing, it's UNPREDICTABLE. Actually, the specified operation stores the un-written-back value.
      ins.UnalignedAllowed=false;
      return true;
    }
  },
  PUSHT3("11111/00/0/0/10/0/1101//dddd/1/101/00000100") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      //use imm as the register list
      ins.Rd=parse(hw2,12,4);
      ins.imm=1<<ins.Rd; //Just push one register
      if(ins.Rd==13 || ins.Rd==15) {ins.op=UNPREDICTABLE; return true;}
      ins.UnalignedAllowed=true;
      return true;
    }
  },
  BLT1("11110/S/iiiiiiiiii//11/j/1/J/iiiiiiiiiii") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      boolean S=parseBit(hw1,10);
      boolean J1=parseBit(hw2,13);
      boolean J2=parseBit(hw2,11);
      int I1=(!(J1^S))?1:0;
      int I2=(!(J2^S))?1:0;
      int bitpos=0;
      int len=1;
      ins.imm=0; 
      len= 1;ins.imm=writeField(ins.imm,bitpos,len,0                );bitpos+=len; //0
      len=11;ins.imm=writeField(ins.imm,bitpos,len,parse(hw2, 0,len));bitpos+=len; //imm11
      len=10;ins.imm=writeField(ins.imm,bitpos,len,parse(hw1, 0,len));bitpos+=len; //imm10
      len= 1;ins.imm=writeField(ins.imm,bitpos,len,I2               );bitpos+=len; //I2
      len= 1;ins.imm=writeField(ins.imm,bitpos,len,I1               );bitpos+=len; //I1
      len= 1;ins.imm=writeField(ins.imm,bitpos,len,S?1:0            );bitpos+=len; //S
      ins.imm=signExtend(ins.imm,bitpos);
      return true;
    }    
  },
  BICT1("11110/i/0/0001/S/nnnn//0/iii/dddd/iiiiiiii") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      ins.setflags=parseBit(hw1,4)?SetFlags.TRUE:SetFlags.FALSE;
      ins.Rd=parse(hw2,8,4);
      ins.Rn=parse(hw1,0,4);
      int bitpos=0;
      int len=1;
      ins.imm=0; 
      len= 8;ins.imm=writeField(ins.imm,bitpos,len,parse   (hw2, 0,len)    );bitpos+=len; //imm8
      len= 3;ins.imm=writeField(ins.imm,bitpos,len,parse   (hw2,12,len)    );bitpos+=len; //imm3
      len= 1;ins.imm=writeField(ins.imm,bitpos,len,parseBit(hw1,10    )?1:0);bitpos+=len; //i
      ins.thumbExpand=true;
      if(ins.Rd==13 || ins.Rd==15 || ins.Rn==13 || ins.Rn==15) {ins.op=UNPREDICTABLE; return true;}
      return true;
    }    
  },
  UDIVT1("11111/011101/1/nnnn//llll/dddd/1111/mmmm") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      if(parse(hw2,12,4)!=0b1111) {ins.op=UNPREDICTABLE; return true;}
      ins.Rd=parse(hw2,8,4);
      ins.Rn=parse(hw1,0,4);
      ins.Rm=parse(hw2,0,4);
      if(ins.Rd==13 || ins.Rd==15 || ins.Rn==13 || ins.Rn==15 || ins.Rm==13 || ins.Rm==15) {ins.op=UNPREDICTABLE; return true;}
      return true;
    }    
  },
  ADDregT1("000/11/0/0/mmm/nnn/ddd") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      ins.Rd=parse(IR,0,3);
      ins.Rn=parse(IR,3,3);
      ins.Rm=parse(IR,6,3);
      ins.setflags=SetFlags.NOT_IN_IT;
      ins.shift_t=SRType.LSL;
      ins.shift_n=0;
      return true;
    }    
  },
  ADDregT2("010001/00/D/mmmm/ddd") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int dn=parse(IR,7,1) << 3 | parse(IR,0,3);
      ins.Rm=parse(IR,3,4);
      if(dn==0b1101 || ins.Rm==0b1101) return false; //SEE add (SP plus register)
      ins.Rd=dn;
      ins.Rn=dn;
      ins.Rm=parse(IR,3,4);
      ins.setflags=SetFlags.FALSE;
      ins.shift_t=SRType.LSL;
      ins.shift_n=0;
      // TODO - if d == 15 && InITBlock() && !LastInITBlock() then UNPREDICTABLE;
      if(ins.Rd==15 && ins.Rm==15) {ins.op=UNPREDICTABLE; return true;}
      return true;
    }    
  },
  ADDregT3("11101/01/1000/S/nnnn/o/iii/dddd/ii/tt/mmmm") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      ins.Rd=parse(hw2,8,4);
      ins.Rn=parse(hw1,0,4);
      ins.Rm=parse(hw2,0,4);
      boolean S=parseBit(hw1,4);
      if(ins.Rd==0b1111 && S) return false; //SEE CMN (register)
      if(ins.Rn==0b1101)      return false; //SEE ADD (SP plus register)
      ins.setflags=S?SetFlags.TRUE:SetFlags.FALSE;
      int type=parse(hw2,4,2);
      ins.imm=parse(hw2,12,3)<<2 | parse(hw2,6,2);
      DecodeShiftReturn r=DecodeImmShift(type,ins.imm);
      ins.shift_n=r.shift_n;
      ins.shift_t=r.shift_t;
      if(ins.Rd==13 || (ins.Rd==15 && !S) || ins.Rn==15 || ins.Rm==13 || ins.Rn==15) {ins.op=UNPREDICTABLE;return true;}
      return true;
    }    
  },
  ADDimmT1("000/11/1/0/iii/nnn/ddd") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      ins.Rd=parse(IR,0,3);
      ins.Rn=parse(IR,3,3);
      ins.imm=parse(IR,6,3);
      ins.setflags=SetFlags.NOT_IN_IT;
      return true;
    }    
  },
  ADDimmT2("001/10/ddd/iiiiiiii") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      ins.Rd=parse(IR,8,3);
      ins.Rn=parse(IR,8,3);
      ins.imm=parse(IR,0,8);
      ins.setflags=SetFlags.NOT_IN_IT;
      return true;
    }
  },
  ADDimmT3("11110/i/0/1000/S/nnnn/0/iii/dddd/iiiiiiii") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      ins.Rd=parse(hw2,8,4);
      ins.Rn=parse(hw1,0,4);
      ins.Rm=parse(hw2,0,4);
      boolean S=parseBit(hw1,4);
      if(ins.Rd==0b1111 && S) return false; //SEE CMN (immediate)
      if(ins.Rn==0b1101)      return false; //SEE ADD (SP plus immediate)
      ins.setflags=S?SetFlags.TRUE:SetFlags.FALSE;
      int bitpos=0;
      int len=1;
      ins.imm=0; 
      len= 8;ins.imm=writeField(ins.imm,bitpos,len,parse(hw2, 0,len));bitpos+=len; //imm8
      len= 3;ins.imm=writeField(ins.imm,bitpos,len,parse(hw2,12,len));bitpos+=len; //imm3
      len= 1;ins.imm=writeField(ins.imm,bitpos,len,parse(hw1,10,len));bitpos+=len; //i
      ins.thumbExpand=true;
      if(ins.Rd==13 || (ins.Rd==15 && !S) || ins.Rn==15) {ins.op=UNPREDICTABLE;return true;}
      return true;
    }    
  },
  ADDimmT4("11110/i/1/0000/0/nnnn//0/iii/dddd/iiiiiiii") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      ins.Rd=parse(hw2,8,4);
      ins.Rn=parse(hw1,0,4);
      if(ins.Rn==0b1111) return false; //SEE ADR
      if(ins.Rn==0b1101) return false; //SEE ADD (SP plus immediate)
      ins.setflags=SetFlags.FALSE;
      int bitpos=0;
      int len=1;
      ins.imm=0; 
      len= 8;ins.imm=writeField(ins.imm,bitpos,len,parse(hw2, 0,len));bitpos+=len; //imm8
      len= 3;ins.imm=writeField(ins.imm,bitpos,len,parse(hw2,12,len));bitpos+=len; //imm3
      len= 1;ins.imm=writeField(ins.imm,bitpos,len,parse(hw1,10,len));bitpos+=len; //i
      ins.thumbExpand=true;
      if(ins.Rd==13 || ins.Rd==15) {ins.op=UNPREDICTABLE;return true;}
      return true;
    }    
  },
  CMPimmT1("001/01/nnn/iiiiiiii") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      ins.Rn=parse(IR,8,3);
      ins.imm=parse(IR,0,8);
      return true;
    }
  },
  CMPimmT2("11110/i/0/1101/1/nnnn/0/iii/1111/iiiiiiii") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      ins.Rn=parse(hw1,0,4);
      int bitpos=0;
      int len=1;
      ins.imm=0; 
      len= 8;ins.imm=writeField(ins.imm,bitpos,len,parse(hw2, 0,len));bitpos+=len; //imm8
      len= 3;ins.imm=writeField(ins.imm,bitpos,len,parse(hw2,12,len));bitpos+=len; //imm3
      len= 1;ins.imm=writeField(ins.imm,bitpos,len,parse(hw1,10,len));bitpos+=len; //i
      ins.thumbExpand=true;
      if(ins.Rn==15) {ins.op=UNPREDICTABLE;return true;}
      return true;
    }
  },
  ORRimmT1("11110/i/0/0010/S/nnnn//0/iii/dddd/iiiiiiii") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      ins.Rn=parse(hw1,0,4);
      if(ins.Rn==15) return false; //SEE MOV (immediate)
      ins.Rd=parse(hw2,8,4);
      boolean S=parseBit(hw1,4);
      ins.setflags=S?SetFlags.TRUE:SetFlags.FALSE;
      int bitpos=0;
      int len=1;
      ins.imm=0; 
      len= 8;ins.imm=writeField(ins.imm,bitpos,len,parse(hw2, 0,len));bitpos+=len; //imm8
      len= 3;ins.imm=writeField(ins.imm,bitpos,len,parse(hw2,12,len));bitpos+=len; //imm3
      len= 1;ins.imm=writeField(ins.imm,bitpos,len,parse(hw1,10,len));bitpos+=len; //i
      ins.thumbExpand=true;
      if(ins.Rd==13 || ins.Rd==15 || ins.Rn==15) {ins.op=UNPREDICTABLE;return true;}
      return true;
    }
  },
  ANDimmT1("11110/i/0/0000/S/nnnn//0/iii/dddd/iiiiiiii") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      ins.Rn=parse(hw1,0,4);
      ins.Rd=parse(hw2,8,4);
      boolean S=parseBit(hw1,4);
      if(ins.Rn==0b1111 && S) return false; //SEE TST (immediate)
      ins.setflags=S?SetFlags.TRUE:SetFlags.FALSE;
      int bitpos=0;
      int len=1;
      ins.imm=0; 
      len= 8;ins.imm=writeField(ins.imm,bitpos,len,parse(hw2, 0,len));bitpos+=len; //imm8
      len= 3;ins.imm=writeField(ins.imm,bitpos,len,parse(hw2,12,len));bitpos+=len; //imm3
      len= 1;ins.imm=writeField(ins.imm,bitpos,len,parse(hw1,10,len));bitpos+=len; //i
      ins.thumbExpand=true;
      if(ins.Rd==13 || (ins.Rd==15 && !S)) {ins.op=UNPREDICTABLE;return true;}
      return true;
    }
  },
  ORRregT1("010000/1100/mmm/ddd") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      ins.Rd=parse(IR,0,3);
      ins.Rn=parse(IR,0,3);
      ins.Rm=parse(IR,3,3);
      ins.setflags=SetFlags.NOT_IN_IT;
      ins.shift_t=SRType.LSL;
      ins.shift_n=0;
      return true;
    }    
  },
  ORRregT2("11101/01/0010/S/nnnn//o/iii/dddd/ii/tt/mmmm") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      int hw1=IR & 0xFFFF;
      int hw2=(IR>>16) & 0xFFFF;
      ins.Rd=parse(hw2,8,4);
      ins.Rn=parse(hw1,0,4);
      ins.Rm=parse(hw2,0,4);
      boolean S=parseBit(hw1,4);
      if(ins.Rn==0b1111)      return false; //SEE "Related Encodings"
      ins.setflags=S?SetFlags.TRUE:SetFlags.FALSE;
      int type=parse(hw2,4,2);
      ins.imm=parse(hw2,12,3)<<2 | parse(hw2,6,2);
      DecodeShiftReturn r=DecodeImmShift(type,ins.imm);
      ins.shift_n=r.shift_n;
      ins.shift_t=r.shift_t;
      if(ins.Rd==13 || ins.Rd==15 || ins.Rn==13 || ins.Rm==13 || ins.Rn==15) {ins.op=UNPREDICTABLE;return true;}
      return true;
    }    
  };

  private int moneBits, mzeroBits;
  private String bitpattern;
  public final Operation mop;
  public int oneBits()  {return moneBits;};
  public int zeroBits() {return mzeroBits;};
  public Operation op() {return mop;};
  private Thumb2DecodeLine(String Lbitpattern) {
    bitpattern=Lbitpattern;
    int[] masks=DecodeLine.interpretBitPattern(bitpattern);
    moneBits =masks[1];
    mzeroBits=masks[0];
    mop=Operation.valueOf(toString().substring(0,toString().length()-2));
  }
}