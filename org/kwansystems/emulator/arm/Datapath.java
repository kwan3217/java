package org.kwansystems.emulator.arm;

import java.io.*;
import java.util.ArrayList;

import org.kwansystems.emulator.arm.DecodedInstruction.SetFlags;

import static org.kwansystems.emulator.arm.ConditionCode.*;
import static org.kwansystems.emulator.arm.BitFiddle.*;

public class Datapath {
  ArrayList<MemoryMappedDevice> devices=new ArrayList<MemoryMappedDevice>();
  public void addDevice(MemoryMappedDevice dev) {
    devices.add(dev);
  }
  public static int swapEndian(int val) {
    return (((val & 0x000000FF) << 24) & 0xFF000000) |
           (((val & 0x0000FF00) <<  8) & 0x00FF0000) |
           (((val & 0x00FF0000) >>  8) & 0x0000FF00) |
           (((val & 0xFF000000) >> 24) & 0x000000FF) ;
  }
  private void flushPipeline() {
    System.out.println("flushPipeline()");
    flush=true;
    ins=null;
    insDataValid=false;
  }
  //Pipeline control state
  public boolean flush=false;
  //Pipeline state
  DecodedInstruction ins=null;
  int insData=0;
  boolean insDataValid=false;
  //Program-visible state
  public int r[]=new int[16];
  public int APSR;
  public boolean APSR_C() {return parseBit(APSR,CPos);};
  public void APSR_setC(boolean C) {APSR=setBit(APSR,CPos,C);}
  public void APSR_setC(int C) {APSR_setC(C!=0);};
  public boolean APSR_N() {return parseBit(APSR,NPos);};
  public void APSR_setN(boolean N) {APSR=setBit(APSR,NPos,N);}
  public void APSR_setN(int N) {APSR_setN(N!=0);};
  public boolean APSR_V() {return parseBit(APSR,VPos);};
  public void APSR_setV(boolean V) {APSR=setBit(APSR,VPos,V);}
  public void APSR_setV(int V) {APSR_setV(V!=0);};
  public boolean APSR_Z() {return parseBit(APSR,ZPos);};
  public void APSR_setZ(boolean Z) {APSR=setBit(APSR,ZPos,Z);}
  public void APSR_setZ(int Z) {APSR_setZ(Z!=0);};
  public boolean inIT=false;
  public int cycles=0; //Number of cycles since reset
  public int readMem(int address, int bytes) { 
    for(MemoryMappedDevice i:devices) {
      if(address>=i.getBase() && address<i.getBase()+i.getSize()) {
        return i.read(address-i.getBase(), bytes);
      }
    }
    throw new RuntimeException(String.format("Memory at 0x%08x not mapped to anything",address));
  };
  public int readMem4(int address) {return readMem(address,4);} 
  public int readMem2(int address) {return readMem(address,2);} 
  public int readMem1(int address) {return readMem(address,1);}
  public void writeMem(int address, int bytes, int value, boolean debug) {
    if(debug) {
      System.out.printf("writeMem(%08x,%0"+String.format("%d", bytes*2)+"x)\n",address,value);
    }
    for(MemoryMappedDevice i:devices) {
      if(address>=i.getBase() && address<i.getBase()+i.getSize()) {
        i.write(address-i.getBase(), bytes,value);
        return;
      }
    }
    throw new RuntimeException(String.format("Memory at 0x%08x not mapped to anything",address));
  }
  public void writeMem(int address, int bytes, int value) { 
    writeMem(address,bytes,value,true);    
  };
  public void writeMem4(int address, int value) {writeMem(address,4,value);} 
  public void writeMem2(int address, int value) {writeMem(address,2,value);} 
  public void writeMem1(int address, int value) {writeMem(address,1,value);} 
  public boolean shouldSetFlags(SetFlags setflags) {
    switch(ins.setflags) {
      case TRUE:
        return true;
      case FALSE:
        return false;
      case IN_IT:
        return InITBlock();
      case NOT_IN_IT:
        return !InITBlock();
    }
    return false; //Above case statement is exhaustive, but compiler doesn't see it that way.
  }
  private int getIT() {
    return parse(APSR,25,2) | (parse(APSR,10,6)<<2);
  }
  public void shiftIT() {
    if(InITBlock()) {
      if(LastInITBlock()) {
        System.out.println("End of IT block, IT no longer in effect");
        setIT(0);
      } else {
        int oldmask=parse(getIT(),0,5);
        int newmask=parse(oldmask<<1,0,5);
        int newIT=writeField(getIT(),0,5,newmask);
        System.out.printf("Shifting mask from %02x to %02x, IT is now %02d\n",oldmask,newmask,newIT);
        setIT(newIT);
      }
    }
  }
  public void setIT(int IT) {
    APSR=writeField(APSR,25,2,parse(IT,0,2));
    APSR=writeField(APSR,10,6,parse(IT,2,6));
  }
  //Pseudocode functions used in the execute phase which directly affect datapath state
  /** This is the ONLY valid way to change PC, because any such change to PC will require a datapath flush */
  public void BranchWritePC(int value) {
    //A.6.6 BranchWritePC()
    //This procedure writes a value to the PC with the correct semantics for such writes by simple branches - that
    //is, just a change to the PC in all circumstances.
    System.out.printf("Jumping to %08x\n",value);
    r[15]=value & ~1;
    flushPipeline();
  }
  // A2.3.1 
  //   ...
  // The LoadWritePC() and ALUWritePC() functions are used for two cases where the behavior was systematically
  // modified between architecture versions. The functions simply to aliases of the branch functions in the M-profile
  // architecture variants:
  public void ALUWritePC(int value) {
    BranchWritePC(value);
  }
  public void BXWritePC(int value) {
    // TODO - This code is from an older version of the book. It should work for now.
    if(value %2==0) throw new Unpredictable("Going to ARM state on a machine that doesn't support it"); //In a machine that handled ARM, we would set the T bit with bit 0 here
    System.out.printf("LoadWritePC(%08x)\n",value);
    BranchWritePC(value & ~1);
  }
  public void LoadWritePC(int value) {
    BXWritePC(value);
  }
  public void StartITBlock(int firstcond, int mask) {
    //The condition codes are set up such that the high three bits specifies a pattern of flags,
    //and the last bit specifies whether the instruction is executed if the pattern does or does not match. So, we have:
    // EQ, NE - Z
    // CS, CC - C 
    // MI, PL - N
    // VS, VC - V
    // HI, LS - C&(!Z)
    // GE, LT - N==V
    // GT, LE - (!Z)&(N==V)
    // AL,(NV)- 1 (NV (never) is not allowed, is used to encode other instructions or is UNPREDICTABLE.) 
    //In each case, if the low bit is cleared, then the instruction is executed if the pattern matches ([T]hen case),
    //and cleared if the instruction is not to be executed if the pattern matches ([E]lse case).
    //So, how IT works is that it writes the high 3 bits into the high 3 bits of the IT field in APSR (IT[7:5]) and 
    //the low bit of the condition in the highest bit of the mask part of the IT field. The mask is used to set the next
    //3 bits of the mask, and a 1 bit is written in the lowest bit.
    //
    //During execution, if all the bits of the IT field are cleared, there is no IT block in effect and the instruction
    //is normally executed. If any bits are set, then the upper 4 bits are the condition for this instruction. After
    //execting the instruction, the mask field is shifted left 1 bit. If there is only a single bit (stop bit) left in
    //the highest bit of the mask part, then the block is ended and the whole IT field is cleared.
    //
    //Note that Bad Things happen if you jump out of the middle of an IT block. The effect is specified to be UNPREDICTABLE
    //but the naive implementation will treat the instructions at the jump target as being in the IT block (maybe, depending
    //on the pipeline).
    //
    //All we do here is set up the IT field properly by copying the firstcond and mask fields into the IT field. It is the
    //responsibility of the assembler to encode the mask field such that the stop bit is in the right place.

    setIT(firstcond<<4 | mask);
  }
  //Pseudocode functions used in the execute phase which depend on datapath state
  public boolean InITBlock() {
    return getIT()!=0;
  }
  public boolean LastInITBlock() {
    return parse(getIT(),0,4)==0b1000;
  }
  public boolean ConditionPassed(ConditionCode cond) {
    if(cond==ConditionCode.Thumb) {
      if(!InITBlock()) {
        System.out.println("No IT block in effect");
        return true;
      }
      ConditionCode localCond=ConditionCode.enumValues[parse(getIT(),4,4)];
      System.out.printf("Checking IT stuff: IT=%02x, Code=%s\n",getIT(),localCond.toString());
      return localCond.shouldExecute(APSR);
    } else {
      return cond.shouldExecute(APSR);
    }
  }
  public int ThumbExpandImm(int imm) {
    return ThumbExpandImmWithC(imm).result;
  }
  public ResultWithCarry ThumbExpandImmWithC(int imm) {
    System.out.printf("ThumbExpandImmWithC(0x%03x): ",imm);
    if(parse(imm,10,2)==0b00) {
      ResultWithCarry r=new ResultWithCarry();
      int imm8=parse(imm,0,8);
      switch(parse(imm,8,2)) {
        case 0b00:
          r.result=imm8;
          System.out.printf("8-bit 0x%02x\n", r.result);
          break;
        case 0b01:
          if(imm8==0) throw new Unpredictable();
          r.result=imm8<<16 | imm8 << 0;
          System.out.printf("xx=0x%02x 00|xx|00|xx 0x%08x\n", imm8, r.result);
          break;
        case 0b10:
          if(imm8==0) throw new Unpredictable();
          r.result=imm8<<24 | imm8 << 8;
          System.out.printf("xx=0x%02x xx|00|xx|00 0x%08x\n", imm8, r.result);
          break;
        case 0b11:
          if(imm8==0) throw new Unpredictable();
          r.result=imm8<<24 | imm8 << 16 | imm8 << 8 | imm8 << 0;
          System.out.printf("xx=0x%02x xx|xx|xx|xx 0x%08x\n", imm8, r.result);
          break;
      }
      r.carry_out=APSR_C();
      return r;
    } else {
      int unrotated_value=1<<7 | parse(imm,0,7);
      int rot_amount=parse(imm,7,5);
      ResultWithCarry r=ROR_C(unrotated_value,rot_amount);
      System.out.printf("%02x ROR %d=0x%08x\n",unrotated_value,rot_amount,r.result);
      return r;       
    }
  }
  //Pseudocode functions which don't rely on datapath state, but are used in execute stage
  public static int Shift(int value, SRType type, int amount, boolean carry_in) {
    return Shift_C(value,type,amount,carry_in).result;
  }
  public static ResultWithCarry Shift_C(int value, SRType type, int amount, boolean carry_in) {
    ResultWithCarry r;
    switch(type) {
      case LSL:
        if(amount==0) {
          r=new ResultWithCarry(value,carry_in);
        } else {
          r=LSL_C(value,amount);
        }
        return r;
      case NONE: //Intentionally catches case NONE:
        return new ResultWithCarry(value,carry_in);
      default:
        //TODO
        throw new RuntimeException("TODO: implement case "+type.toString());
    }
  };
  public static ResultWithCarry LSL_C(int x, int n) {
    return new ResultWithCarry(x << n, //Shift the word the given number of bits
                              (x & (1<<(32-n)))!=0); //pick the carry bit out of the original answer. 
                                                     //If the word is shifted n bits, the carry is the nth bit from the left. 
                                                     //IE shifting 1 bit  means that the leftmost (most significant, bit 31) bit is it,
                                                     //   shifting 2 bits means that the second leftmost bit (bit 30)
                                                     //Therefore select bit 32-n by calculating 1<<(32-n) as the mask, ANDing with the 
                                                     //original number, and returning true or false if the result is nonzero or zero.
  }
  public static int LSL(int x, int n) {
    if(n==0) return x;
    return LSL_C(x,n).result;
  }
  public static ResultWithCarry LSR_C(int x, int n) {
    return new ResultWithCarry(x >>> n, //Logical-shift the word the given number of bits
                               (x & (1<<(n-1)))!=0); //Dig out the carry bit, which is the bit to the right of the lowest bit in the result.
                                                      //IE shifting 1 bit  means that the        rightmost (least significant, bit 0) bit is it,
                                                      //   shifting 2 bits means that the second rightmost bit (bit 1), etc.
                                                      //Therefore select bit n-1 by calculating 1<<(n-1) as the mask, ANDing with the 
                                                      //original number, and returning true or false if the result is nonzero or zero.
  }
  public static int LSR(int x, int n) {
    if(n==0) return x;
    return LSR_C(x,n).result;
  }
  public static ResultWithCarry ROR_C(int x, int n) {
    int m=n%32;
    ResultWithCarry r=new ResultWithCarry();
    r.result=(m==0)?x:LSR(x,m) | LSL(x,32-m);
    r.carry_out=parseBit(r.result,31);
    return r;
  }
  public static int ROR(int x, int n) {
    if(n==0) return x;
    return ROR_C(x,n).result;
  }
  public static class AddWithCarryReturn {
    public int result;
    public boolean carry_out, overflow;
  }
  public static long UInt(int x) {
    long result=x;
    result=result & 0xFFFFFFFFL;
    return result;
  }
  public static AddWithCarryReturn AddWithCarry(int x, int y, boolean carry_in) {
    long unsigned_sum=UInt(x)+UInt(y)+UInt(carry_in?1:0);
    int signed_sum=x+y+(carry_in?1:0);
    AddWithCarryReturn r=new AddWithCarryReturn();
    r.result=signed_sum;
    long ur=UInt(r.result);
    int sr=(int)r.result;
    r.carry_out=!(ur==unsigned_sum);
    r.overflow=!(sr==signed_sum);
    return r;
  }
  public static void main(String[] args) {
    AddWithCarry(8,~8,true);
  }
}