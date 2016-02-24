package org.kwansystems.emulator.arm;

import static org.kwansystems.emulator.arm.Operation.*;
import static org.kwansystems.emulator.arm.BitFiddle.*;

/** The documentation for an ARM processor is quite complete, and appears to be sufficient to completely 
 * emulate the chip. It is documented in an extremely structured manner. The document is full of pseudocode,
 * which details exactly what the hardware is doing in an unambiguous manner.
 * 
 * Assembly opcodes are only loosely related to actual instructions. Sometimes the same mnemonic is used
 * in two completely different encodings, and sometimes the same encoding has two completely different mnemonic.
 * For instance, there is no difference between a mov Rd, Rm instruction and a lsl Rd, Rm, #0 instruction,
 * so in a sense the mov instruction doesn't even exist. Conversely, the ldr instruction encodes completely
 * different based on whether it is a register, immediate, or literal ldr. However in that case, ldr, while
 * documented as being a different encoding, is almost exactly the same as register, with the pc being the register
 * in question.
 * 
 * There are also two kinds of documentation for the encodings. One is near the front of the book, and starts with
 * a plot, showing bit positions, required bits, and fields, for groups of related instructions. These then point at
 * the second kind of documentation, which is a detailed list of each encoding of each type of instruction. So
 * for instance, LDR immediate might have a T1 and T2 encoding. Each encoding has a bitmask, which details what
 * bits need to be 1, what need to be 0, and what the other bits mean. Each encoding also has a routine called 
 * EncodingSpecificOperations(). The specified pseudocode method is to go through all the bitmasks, and identify
 * those that match the instruction to be decoded. Then, independently and in parallel, run all the encoding
 * specific operations. If there is more than one encoding that matches the bit mask, it is required that all but
 * one of them have an operation which says "Don't use this encoding", in which case the decoding of that bitmask
 * ends and has no further effect on the computation. Precisely one instruction is allowed to both match the bitmask
 * and have its encoding-specific operation complete. The other purpose of the encoding-specific operation is to 
 * set up all the fields that the actual execution will use. For instance, an instruction might have an ARM 32-bit
 * encoding which specifies some fields from the instruction data. Conversely, the same instruction might have a
 * THUMB 16-bit encoding where there isn't enough space for the field, and consequently it has a constant value.
 * The encoding-specific operations either set up the default values or pull them from the correct part of the
 * instruction data, so that the execution unit doesn't know or care which encoding was used.
 * 
 *  To model this, we will borrow a concept from the 8051 control store. That chip has a ROM built into it. When an
 *  instruction is decoded, it is checked against each row of the ROM. Each row has a field describing what bits
 *  in the instruction must be 1, what must be 0 (and also what phase of the instruction we are on, but we don't
 *  use the concept of phasing in ARM). Each row controls one of the control signals, so if that row's bits match
 *  (and the phase matches) then that control signal is asserted. We will have an enum, each of which will have
 *  associated with it a one mask and a zero mask. It will also have a bit of code which does the encoding-specific
 *  operations and returns true or false. The rows will be searched in order. The first one that matches will
 *  have its encoding-specific routine run, and if that routine returns true, then we are done, otherwise we
 *  keep looking at the next one that matches. The routine will also set the field values in the decoded instruction
 *  and if it passes, will set a field with the actual operation (another enum) which will be used to execute the 
 *  instruction.
 *  
 *  Using this method, we could in principle have one table for THUMB mode and a different one for ARM mode. The 
 *  current processor mode selects which table is used.
 */
public class Thumb2Decode extends Decode {
  public static final int N=-1;
  public void flushPipeline() {
    hw1Thumb=0;
    hw1ThumbValid=false;
  }
  private int hw1Thumb;
  private boolean hw1ThumbValid=false;

  private enum DecodeLine {
    LDRlitT1("01001/ddd/iiiiiiii") {
      @Override public boolean decode(int IR, DecodedInstruction ins) {
        ins.Rd=parse(IR,8,3);
        ins.imm=parse(IR,0,8);
        ins.add=true;
        ins.op=LDRlit;
        return true;
      }
    },
    LDRlitT2("11111/00/0/u/10/1/1111//dddd/iiiiiiiiiiii") {
      @Override public boolean decode(int IR, DecodedInstruction ins) {
        int hw1=IR & 0xFFFF;
        int hw2=(IR>>16) & 0xFFFF;
        ins.Rd=parse(hw2,12,4);
        ins.imm=parse(hw2,0,12);
        ins.add=parseBit(hw1,7);
        ins.op=LDRlit;
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
        ins.op=LDRimm;
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
        ins.op=LDRimm;
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
        ins.op=LDRimm;
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
        ins.op=LDRimm;
        return true; 
      }
    },
    ANDimmT1("010000/0000/mmm/dnd") {
      @Override public boolean decode(int IR, DecodedInstruction ins) {
        ins.Rd=parse(IR,0,3);
        ins.Rn=ins.Rd;
        ins.Rm=parse(IR,3,3);
        ins.setflags=true;
        ins.shift_t=SRType.NONE;
        ins.shift_n=0;
        ins.op=ANDimm;
        return true; //if n==15 then SEE LDR (literal) on page 4-102
      }
    },
    ANDimmT2("11101/01/0000/S/nnnn/o/iii/dddd/ii/tt/mmmm") {
      @Override public boolean decode(int IR, DecodedInstruction ins) {
        int hw1=IR & 0xFFFF;
        int hw2=(IR>>16) & 0xFFFF;
        ins.Rd=parse(hw2,8,4);
        ins.Rn=parse(hw1,0,4);
        ins.Rm=parse(IR,3,3);
        ins.setflags=parseBit(hw1,4);
        DecodeShiftReturn r=DecodeImmShift(parse(hw2,4,2),parse(hw2,12,3)<<2|parse(hw2,6,2));
        ins.shift_t=r.shift_t;
        ins.shift_n=r.shift_n;
        if(ins.Rd==15 && ins.setflags) return false; //SEE TST (register) on page 4-399
        if(BadReg(ins.Rd) || BadReg(ins.Rn) || BadReg(ins.Rm)) {ins.op=UNPREDICTABLE; return true;}
        ins.op=ANDimm;
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
        ins.op=STRimm;
        return true;
      }
    },
    CMPregT1("010000/1010/mmm/nnn") {
      @Override public boolean decode(int IR, DecodedInstruction ins) {
        ins.Rn=parse(IR,0,3);
        ins.Rm=parse(IR,3,3);
        ins.shift_t=SRType.NONE;
        ins.shift_n=0;
        ins.op=CMPreg;
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
        ins.op=CMPreg;
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
        ins.op=IT;
        return true;
      }
    };
    public int oneBits, zeroBits;
    public boolean thumb32;
    private DecodeLine(int LoneBits, int LzeroBits, boolean Lthumb32) {oneBits=LoneBits; zeroBits=LzeroBits; thumb32=Lthumb32;}
    /** 
     * @param bitpattern Encoding of 1 bit, 0 bit, and don't care bits. For each bit position, the
     *        one bit is set if the string is 1 in the corresponding character slot, the zero bit 
     *        if 0, and neither if any other character is specified (don't care). The traditional
     *        symbol for don't care is X, but it seems like _ or - might be more like the documentation.
     *        An encoding string is either exactly 16 or 32 characters long. For 16, the string starts
     *        with the most-significant bit (bit 15) at the left and proceeds to bit 0 at the right. For
     *        32, the first 16 are the least significant half-word, and the second 16 are the most.
     */
    private DecodeLine(String bitpattern) {
      oneBits=0;
      zeroBits=0;
      thumb32=false;
      int j=0;
      for(int i=0;i<16;i++) {
        while(bitpattern.charAt(j)=='/') j++; //Use slash as field delimiter
        if(bitpattern.charAt(j)=='1') {
          oneBits |=(1<<(15-i));
        }
        if(bitpattern.charAt(j)=='0') {
          zeroBits|=(1<<(15-i));
        }
        j++;
        //Any other character leaves both bits cleared, exactly as desired for don't care
      }
      if(bitpattern.length()>j) {
        thumb32=true;
        for(int i=16;i<32;i++) {
          while(bitpattern.charAt(j)=='/') j++;
          if(bitpattern.charAt(j)=='1') {
            oneBits |=(1<<(15-i+16));
          }
          if(bitpattern.charAt(j)=='0') {
            zeroBits|=(1<<(15-i+16));
          }
          j++;
        }
      }
    }
    public abstract boolean decode(int IR, DecodedInstruction ins);
    public static final DecodeLine[] enumValues=DecodeLine.values();
  }
  public DecodedInstruction decode(int IR, int pc) {
    if(hw1ThumbValid) {
      IR=hw1Thumb | IR<<16;
      hw1ThumbValid=false;
    } else {
      //special case: is this the first halfword of a thumb32?
      if(parse(IR,11,5)==0b11101 || parse(IR,12,4)==0b1111) {
        hw1Thumb=IR;
        hw1ThumbValid=true;
        return null;
      }
    }
    DecodedInstruction ins=new DecodedInstruction();
    ins.pc=pc;
    for(DecodeLine line:DecodeLine.enumValues) {
      if(((line.oneBits  &  IR ) == line.oneBits ) && //the one  bits match
         ((line.zeroBits &(~IR)) == line.zeroBits) && //the zero bits match
         line.decode(IR, ins)) {                      //the encoding-specific routine says OK
        return ins; //The instruction is decoded, we're outtahere.
      }
    }
    ins.imm=IR;
    ins.op=UNDEFINED;
    return ins;
  }
  /** Only for human consumption, allows entry of instruction data in the same form that 
   * assembly listings display it
   * @param IRlo Low halfword of instruction, always printed on the left
   * @param IRhi High halfword, always printed on the right.
   * @return
   */
  public DecodedInstruction decode(int IRlo, int IRhi, int pc) {
    hw1ThumbValid=false;
    return decode(IRlo | IRhi<<16,pc);
  }
}

