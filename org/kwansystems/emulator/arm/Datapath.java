package org.kwansystems.emulator.arm;

import java.io.*;

import org.kwansystems.emulator.arm.DecodedInstruction.SetFlags;

import static org.kwansystems.emulator.arm.ConditionCode.*;
import static org.kwansystems.emulator.arm.BitFiddle.*;

public class Datapath {
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
  private byte[][] mem=new byte[0x10000][];
  private int getSeg(int address) {
    int seg=parse(address, 16, 16);
    if(mem[seg]==null) mem[seg]=new byte[0x10000];
    return seg;
  }
  void loadBin(String infn, int address) throws IOException {
	  FileInputStream inf=new FileInputStream(infn);
    int b=inf.read();
    while(b>=0) {
      writeMem(address,1,b,false);
	    address++;
	    b=inf.read();
	  }
	  inf.close();
  }
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
  public int readMem(int address, int bytes) { 
    int result=0;
    for(int i=bytes-1;i>=0;i--) {
      int seg=getSeg(address+i);
      int blockofs=BitFiddle.parse(address+i, 0, 16);
      result=(result<<8) | ((int)(mem[seg][blockofs]) & 0xff);
    }
    System.out.printf("readMem(%08x)=%0"+String.format("%d", bytes*2)+"x\n",address,result);
    return result; 
  };
  public int readMem4(int address) {return readMem(address,4);} 
  public int readMem2(int address) {return readMem(address,2);} 
  public int readMem1(int address) {return readMem(address,1);}
  public void writeMem(int address, int bytes, int value, boolean debug) {
    for(int i=0;i<bytes;i++) {
      int seg=getSeg(address);
      int ofs=parse(address, 0, 16);
      mem[seg][ofs]=(byte)((value<<(8*i)) & 0xFF);
    }
    if(debug)System.out.printf("writeMem(%08x,%0"+String.format("%d", bytes*2)+"x)\n",address,value);
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
  public void LoadWritePC(int value) {
    //A.6.43 LoadWritePC()
    //This procedure writes a value to the PC with the correct semantics for such writes by load instructions. That
    //is, with BX-like interworking behavior in ARMv5 and above, and just a change to the PC in ARMv4T.
    if(value %2==0) throw new Unpredictable("Going to ARM state on a machine that doesn't support it"); //In a machine that handled ARM, we would set the T bit with bit 0 here
    System.out.printf("LoadWritePC(%08x)\n",value);
    BranchWritePC(value & ~1);
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
  public int ThumbExpandWithImm(int imm) {
    return ThumbExpandWithC(imm).result;
  }
  public boolean ThumbExpandImmWithC(int imm) {
    if(parse(imm,10,2)==0b00) {
      ResultWithCarry r=new ResultWithCarry();
      int imm8=parse(imm,8,2);
      switch(parse(imm,8,2)) {
        case 0b00:
          r.result=imm8;
          break;
        case 0b01:
          if(imm8==0) throw new Unpredictable();
          r.result=imm8<<16 | imm8 << 0;
          break;
        case 0b10:
          if(imm8==0) throw new Unpredictable();
          r.result=imm8<<24 | imm8 << 8;
          break;
        case 0b11:
          if(imm8==0) throw new Unpredictable();
          r.result=imm8<<24 | imm8 << 16 | imm8 << 8 | imm8 << 0;
          break;
      }
      r.carry_out=APSR_C();
    } else {
      int unrotated_value=1<<7 | parse(imm,0,7);
      return ROR_C(unrotated_value,parse(imm,7,5));      
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
    return new ResultWithCarry(x << n,(x & (1<<(32-n)))!=0);
  }
  public static int LSL(int x, int n) {
    return LSL_C(x,n).result;
  }
  public static class AddWithCarryReturn {
    public int result;
    public boolean carry_out, overflow;
  }
  public static AddWithCarryReturn AddWithCarry(int x, int y, boolean carry_in) {
    long ux=((long)x) & 0xFFFFFFFF;
    long uy=((long)y) & 0xFFFFFFFF;
    long uc=carry_in?1:0;
    long unsigned_sum=ux+uy+uc;
    int signed_sum=x+y+(carry_in?1:0);
    AddWithCarryReturn r=new AddWithCarryReturn();
    r.result=signed_sum;
    long ur=((long)r.result) & 0xFFFFFFFF;
    int sr=(int)r.result;
    r.carry_out=!(ur==unsigned_sum);
    r.overflow=!(sr==signed_sum);
    return r;
  }

}