package org.kwansystems.emulator.cips;

import java.io.*;


public class UT69R000 {
  /** General purpose 16bit registers */
  public short[] r=new short[20];
  /** General purpose 32bit registers */
  public void setXr(int Xr, int data) {
    if(Xr%2==1) throw new IllegalArgumentException("No odd XR register access allowed");
    r[Xr]=(short)(data >>> 16);
    r[Xr+1]=(short)(data  & 0xFFFF);
  }
  public int getXr(int Xr) {
    if(Xr%2==1) throw new IllegalArgumentException("No odd XR register access allowed");
    return r[Xr]<<16+r[Xr+1];
  } 
  // Debug stream
  PrintStream ouf;
  /* Special purpose registers */
  public int   acc;    //Accumulator
  public short sp;     //Stack Pointer
  public short status; //Status
  public short rcvr() {return (short)(DebugPort.get());} //UART receive register
  public void  txmt(short data) {DebugPort.set(data);}   //UART transmit register
  public short mk;     //Interrupt Mask
  public short pi;     //Pending Interrupts
  public short ft;     //Fault
  public short sw;     //Status Word
  public short tb;     //Timer B
  public short ta;     //Timer A
  public short pir;    //Prefetch Instruction register
  public short irl;    //Instruction register
  public int   ic;     //Instruction counter
  public int   ics;    //Instruction counter save
  
  /** External data ports */
  public Memory Op;
  public Memory Ins;
  public Memory IO;
  UART DebugPort;
  
  /** Documented machine state not kept in registers */
  //public boolean TimerARunning=false;
  //public boolean TimerBRunning=false;
  //public boolean ConditionCodeEnabled=true;
  
  /** Undocumented machine state */
  private boolean useIns;
  private boolean isImm;
  private boolean useOp;
  private boolean useIo;
  private int TmpSrc;
  boolean ConditionMet=false;
  private int LiteralFlag,Opcode,Dest,Src; //Decoded instruction
  private int icirl,icpir;                 //For display only - The address current item in irl and pir were taken from
  
  //Main processor cycle
  private void incIc() {
    ic=(ic+1) & 0xFFFFF;
  }
  private void Clock1() {
    //Copy prefetch into main instruction register
    irl=pir;
    icirl=icpir;
    //inc the instruction counter
    incIc();
    //Address the instruction bus
    Ins.address(ic,20);
    //Break the instruction up into its fields
    Decode();
    //reset undocumented state
    useIns=false;
    isImm=false;
    useOp=false;
    useIo=false;
    //Execute1: Figure out what data is going where. Set up operand bus reading if needed
    microcode[Opcode].Execute1();
  }
  private void Clock2() {
    //Execute2: 
    //Get control of the operand address bus if needed (not yet simulated)
    if(useIns) {
      //Do immediate or LRI ins bus access
      //LRI has already re-addressed the Ins bus in Clock1;
      TmpSrc=(int)Ins.get(16);
      if(isImm) {
        ouf.println(Disassembler.disasm2(TmpSrc,Disassembler.add(irl,ic),ic));
        incIc();
      }
      Ins.address(ic,20);
    }
    microcode[Opcode].Execute2();
  }
  private void Clock3() {
    //Do the instruction prefetch
    pir=(short)Ins.get(16);
    icpir=ic;
    //Execute3:
    //Address the operand bus as necessary
    if(useOp) {
      Op.address(TmpSrc,16);
    }
    if(useIo) {
      IO.address(TmpSrc,16);
    }
    microcode[Opcode].Execute3();
  }
  private void Clock4() {
    //Execute4: Do computations, read/write the op bus, make sure all data is home.
    //STRI writes to the instruction bus
    //CALL does a prefetch flush
    microcode[Opcode].Execute4();
  }
  public void cycle() {
    Clock1();
    Clock2();
    Clock3();
    Clock4();
  }
  
  //-1 is SP(read) or NUL(write)
  //-2 is ACC or IMM (Must have code in operation
  private static int[] regCodeMap=new int[] {
    0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,
    0,16, 2,17, 4,16, 6,-1, 8,18,10,19,12,18,14,-2
  };
  private static String[] regName=new String[] {
    "r0", "r1", "r2", "r3", "r4", "r5",  "r6", "r7",    "r8", "r9", "r10", "r11","r12", "r13", "r14",    "r15", 
    "xr0","r16","xr2","r17","xr4","xr16","xr6","SP/NUL","xr8","r18","xr10","r19","xr12","xr18","xr14","ACC/IMM"
  };    
  private static final boolean T=true;
  private static final boolean F=false;
  private static boolean[] isReg32=new boolean[] {
    F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F,
    T, F, T, F, T, T, T, F, T, F, T, F, T, T, T, T
  };
  private int getReg(int regCode) {
    int regNum=regCodeMap[regCode];
    if(isReg32[regCode]) {
      if(regCode==31) return acc;
      return getXr(regNum);
    } else {
      if(regCode==23) return sp;
      return r[regNum];
    }
  }
  private void setReg(int regCode, int data) {
    int regNum=regCodeMap[regCode];
    if(isReg32[regCode]) {
      if(regCode==31) {
        acc=data;
        return;
      }
      setXr(regNum,data);
    } else {
      if(regCode==23) return; //Treat special reg SP/NUL as NUL.
      r[regNum]=(short)data;
    }
  }
  private static final byte C=15;
  private static final byte P=14;
  private static final byte Z=13;
  private static final byte N=12;
  private static final byte V=11;
  private static final byte J=10;
  private void setFlag(byte flagBit, int value) {
    short flagMask=(short)(1<<flagBit);
    status=(short)((value<<flagBit) | (status & ~flagMask));
  }
  private boolean getFlag(byte flagBit) {
    short flagMask=(short)(1<<flagBit);
    return (status & flagMask)!=0;
  }
  private abstract class Operation {
    public abstract void Execute1();
    public abstract void Execute2();
    public abstract void Execute3();
    public abstract void Execute4();
  }
  private void getSrc() {
    if(LiteralFlag==1) {
      //Literal 5-bit signed
      TmpSrc=(int)Memory.SignExtend(Src,5);
    } else if(Src==31) {
      //Immediate
      useIns=true;
      isImm=true;
      //TmpSrc will be set in Clock2()
    } else {
      //Register
      TmpSrc=getReg(Src);
    }
  }
  private int isJ(int value) {
    int d31=(value >>> 30) & 1;
    int d32=(value >>> 31) & 1;
    return d31^d32;
  }
  private void setFlags1(int R) {
    setFlag(C,0);
    setFlag(P,(R>0)?1:0);
    setFlag(Z,(R==0)?1:0);
    setFlag(N,(R<0)?1:0);
    setFlag(V,0);
    setFlag(J,isJ(R));
  }
  private void setFlags2(int S, int D, int R) {
    //Sign extension will take care and convert all numbers to 32bit, so
    //Xm for all of these is X31
    int Sm=(S >>> 31) & 1;
    int Rm=(R >>> 31) & 1;
    int Dm=(D >>> 31) & 1;
    setFlag(C,(Sm & Dm) | ((1-Rm) & Dm) | (Sm & (1-Rm)));
    setFlag(N,R<0?1:0);
    setFlag(Z,R==0?1:0);
    setFlag(P,R>0?1:0);
    setFlag(V,(Sm & Dm & (1-Rm)) | ((1-Sm) & (1-Dm) & Rm));
    setFlag(J,isJ(R));
  }
  private void setFlags3(int S, int D, int R) {
    //Sign extension will take care and convert all numbers to 32bit, so
    //Xm for all of these is X31
    int Sm=(S >>> 31) & 1;
    int Rm=(R >>> 31) & 1;
    int Dm=(D >>> 31) & 1;
    setFlag(C,((1-Dm) & (1-Sm) & (1-Rm)) | ( Dm & (1-Sm) & (1-Rm)) | (Dm & Sm & Rm));
    setFlag(N,R<0?1:0);
    setFlag(Z,R==0?1:0);
    setFlag(P,R>0?1:0);
    setFlag(V,(Sm & (1-Dm) & (1-Rm)) | ((1-Sm) & Dm & Rm));
    setFlag(J,isJ(R));
  }
  private void setFlags4(int S, int D, int R) {
    //Sign extension will take care and convert all numbers to 32bit, so
    //Xm for all of these is X31
    int Sm=(S >>> 31) & 1;
    int Rm=(R >>> 31) & 1;
    int Dm=(D >>> 31) & 1;
    setFlag(C,(Sm & Dm) | ((1-Rm) & Dm) | (Sm & (1-Rm)));
    setFlag(N,R<0?1:0);
    setFlag(Z,R==0?1:0);
    setFlag(P,R>0?1:0);
    setFlag(V,0);
    setFlag(J,isJ(R));
  }
  private void setFlags5(int S, int D, int R) {
    //Sign extension will take care and convert all numbers to 32bit, so
    //Xm for all of these is X31
    int Sm=(S >>> 31) & 1;
    int Rm=(R >>> 31) & 1;
    int Dm=(D >>> 31) & 1;
    setFlag(C,0);
    setFlag(N,R<0?1:0);
    setFlag(Z,R==0?1:0);
    setFlag(P,R>0?1:0);
    setFlag(V,((1-Sm) & Dm & (1-Rm)) | (Sm & Dm & Rm));
    setFlag(J,isJ(R));
  }
  private Operation[] microcode=new Operation[32];
  private void Decode() {
    LiteralFlag=(irl & 0x8000) >>> 15;
    Opcode=(irl & 0x7C00) >>> 10;
    Dest=(irl & 0x03E0) >>> 5;
    Src=(irl & 0x001F) >>> 0;
    ouf.println(toString());
    ouf.println(Disassembler.disasm1(irl,icirl));
  }
  public String toString() {
    StringBuffer result=new StringBuffer("");
    for(int i=0;i<20;i++) {
      result.append(String.format("r%2d  ",i));
    }
    result.append("\n");
    for(int i=0;i<20;i++) {
      result.append(String.format("%04X ",r[i]));
    }
    result.append("\nCPZNVJ acc      sp   stat mk   pi   ft   sw   tb   ta   ic     ics  irl\n");
    for(byte i=C;i>=J;i--) {
      result.append(getFlag(i)?'1':'0');
    }
    result.append(String.format(" %08X ",acc));
    result.append(String.format("%04X ",sp));
    result.append(String.format("%04X ",status));
    result.append(String.format("%04X ",mk));
    result.append(String.format("%04X ",pi));
    result.append(String.format("%04X ",ft));
    result.append(String.format("%04X ",sw));
    result.append(String.format("%04X ",tb));
    result.append(String.format("%04X ",ta));
    result.append(String.format("%05X ",ic));
    result.append(String.format("%05X ",ics));
    result.append(String.format("%04X ",irl));
    return result.toString();
  }
  {
    microcode[0x00]=new Operation() { //  mov
      public void Execute1() {
        getSrc();
      }
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {
        setReg(Dest,TmpSrc);
      };
    };
    microcode[0x01]=new Operation() { // lr/lri/pop
      public void Execute1() {
        if(LiteralFlag==0) {
          //LR or Pop
          // On Pop, Preincrement the stack pointer, 
          if(Src==23) sp++;
          if(Src==31) throw new UndefinedStateException("Tried to use acc as address register","LR");
          TmpSrc=(short)getReg(Src);
          useOp=true;
        } else if(Src==31) {
          // LR - Immediate
          useIns=true;
          isImm=true;
        } else {
          // LRI - Load from instruction memory
          if(Src!=31) throw new UndefinedStateException("Tried to use non-acc "+regName[(int)Src]+" as address register","LRI");
          Ins.address(acc,20);
          useIns=true;
          isImm=false;
        }
      }
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {
        int value;
        if(LiteralFlag==0 | Src==31) {
          //Clock2 used TmpSrc to address Op bus
          value=(int)Op.get(16);
        } else {
          // LRI - Load from instruction memory
          //Clock2 loaded correct value into TmpSrc
          value=TmpSrc;
        }
        setReg(Dest,value);
      };
    };
    microcode[0x02]=new Operation() { // str/stri/push
      public void Execute1() {
        if(LiteralFlag==0) {
          //STR or Push
          if(Src==31) throw new UndefinedStateException("Tried to use acc as address register","STR");
          TmpSrc=(short)getReg(Src);
          // On Push, postdecrement the stack pointer, 
          if(Src==23) sp--;
          useOp=true;
        } else if(Src==31) {
          // STR - Immediate
          useIns=true;
          isImm=true;
        } else {
          // STRI - Store to instruction memory
          if(Src!=31) throw new UndefinedStateException("Tried to use non-acc "+regName[Src]+" as address register","LRI");
        }
      }
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {
        int value=getReg(Dest);
        if(LiteralFlag==0 | Src==31) {
          //STR or Push
          //Clock2 used TmpSrc to address Op bus
          Op.set(value,16);
        } else {
          // STRI - Store to instruction memory
          Ins.address(acc,20);
          Ins.set(value,16);
        }
      };
    };
    microcode[0x03]=new Operation() { // call
      public void Execute1() {
        getSrc();
      }
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {
        //Wow, I thought this would be hard
        setReg(Dest,ic);
        ic=TmpSrc-1;
        pir=0;
        icpir=0;
      };
    };
    microcode[0x04]=new Operation() { //  movc
      public void Execute1() {
        getSrc();
      }
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {
        setReg(Dest,TmpSrc);
        setFlags1(TmpSrc);
      };
    };
    microcode[0x05]=new Operation() { // inr
      public void Execute1() {
        if(LiteralFlag==1) {
          //Pure reg-to-reg move, do it later
          return;
        } else if(Src==31) {
          // INR - Immediate
          useIns=true;
          isImm=true;
          useIo=true;
        } else {
          // INR - Register
          useIo=true;
          TmpSrc=getReg(Src);
        }
      }
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {
        if(LiteralFlag==0) {
          //INR - IO bus
          setReg(Dest,(int)IO.get(16));          
        } else {
          //INR - Special Register
          switch(Src) {
            case 4:
              setReg(Dest,pi);
              break;
            case 5:
              setReg(Dest,mk);
              break;
            case 6:
              setReg(Dest,ft);
              break;
            case 7:
              setReg(Dest,sw);
              break;
            case 14:
              setReg(Dest,acc);
              break;
            case 15:
              setReg(Dest,sp);
              break;
            case 16:
              //             TimerARunning=false;
              setReg(Dest,0);
              break;
            case 17:
              //             TimerBRunning=false;
              setReg(Dest,0);
              break;
            case 18:
              setReg(Dest,ta);
              break;
            case 19:
              setReg(Dest,tb);
              break;
            case 20:
              setReg(Dest,rcvr());
              break;
            case 21:
              setReg(Dest,ics);
              break;
            case 22:
              setReg(Dest,status);
              break;
            default:
              throw new UndefinedStateException("Unknown literal "+Src,"INR");
          }
        }
      };
    };
    microcode[0x06]=new Operation() { // otr
      public void Execute1() {
        if(LiteralFlag==1) return; //Pure reg-to-reg, do it later
        if(Src==31) {
          // OTR - Immediate
          useIns=true;
          isImm=true;
          useIo=true;
        } else {
          // OTR - Register
          IO.address((short)getReg(Src),16);
        }
      }
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {
        if(LiteralFlag==0) {
          IO.set(getReg(Dest),16);          
        } else {
          //OTR - Special Register
          switch(Src) {
            case 0:
              pi=(short)(pi & ~getReg(Dest));
              break;
            case 1:
              //InterruptsEnabled=false;
              break;
            case 2:
              ft=0;
              break;
            case 3:
              throw new UndefinedStateException("Wrote to PIPE: "+(short)getReg(Dest),"OTR");
            case 4:
              pi=(short)getReg(Dest);
              break;
            case 5:
              mk=(short)getReg(Dest);
              break;
            case 6:
              ft=(short)getReg(Dest);
              break;
            case 7:
              sw=(short)getReg(Dest);
              break;
            case 13:
              //ConditionCodeEnabled=false;
              break;
            case 14:
              //InterruptsEnabled=true;
              break;
            case 15:
              sp=(short)getReg(Dest);
              break;
            case 16:
              //TimerARunning=true;
              break;
            case 17:
              //TimerBRunning=true;
              break;
            case 18:
              ta=(short)getReg(Dest);
              break;
            case 19:
              tb=(short)getReg(Dest);
              break;
            case 20:
              txmt((short)getReg(Dest));
              break;
            case 23:
              //ConditionCodeEnabled=true;
              break;
            default:
              throw new UndefinedStateException("Unknown literal "+Src,"OTR");
          }
        }
      };
    };
    microcode[0x07]=new Operation() { // ??? (Spare1)
      public void Execute1() {throw new UndefinedStateException("Opcode not yet implemented",Disassembler.opcode[0x07]);};
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {};
    };
    microcode[0x08]=new Operation() { // add
      public void Execute1() {
        getSrc();
      }
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {
        long DestValue;
        DestValue=getReg(Dest);
        if(LiteralFlag==1) {
          //Literal 5-bit signed
        } else if(Src==31) {
          //Immediate
        } else {
          //Reg-to-reg
        }
        long result=DestValue+TmpSrc;
        setFlags2(TmpSrc,(int)DestValue,(int)result);
        setReg(Dest,(int)result);
      };
    };
    microcode[0x09]=new Operation() { // addc
      public void Execute1() {
        getSrc();
      }
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {
        long DestValue;
        DestValue=getReg(Dest);
        if(LiteralFlag==1) {
          //Literal 5-bit signed
        } else if(Src==31) {
          //Immediate
        } else {
          //Reg-to-reg
        }
        long result=DestValue+TmpSrc+(getFlag(C)?1:0);
        setFlags2(TmpSrc,(int)DestValue,(int)result);
        setReg(Dest,(int)result);
      };
    };
    microcode[0x0A]=new Operation() { // ab
      public void Execute1() {
        getSrc();
      }
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {
        long DestValue;
        DestValue=getReg(Dest);
        if(LiteralFlag==1) {
          //Literal 5-bit signed
        } else if(Src==31) {
          //Immediate
        } else {
          //Reg-to-reg
        }
        long result=(DestValue & 0xFF)+(TmpSrc & 0xFF);
        if(result>127) result=result-256;
        setFlags2(TmpSrc,(int)DestValue,(int)result);
        setReg(Dest,(int)((DestValue & 0xFFFFFF00) | result));
      };
    };
    microcode[0x0B]=new Operation() { // addu
      public void Execute1() {
        getSrc();
      }
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {
        long DestValue;
        DestValue=getReg(Dest);
        if(LiteralFlag==1) {
          //Literal 5-bit signed
        } else if(Src==31) {
          //Immediate
        } else {
          //Reg-to-reg
        }
        long result=(DestValue & 0xFF)+(TmpSrc & 0xFF);
        if(result>127) result=result-256;
        setFlags4(TmpSrc,(int)DestValue,(int)result);
        setReg(Dest,(int)((DestValue & 0xFFFFFF00) | result));
      };
    };
    microcode[0x0C]=new Operation() { // sub
      public void Execute1() {
        getSrc();
      }
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {
        long DestValue;
        DestValue=getReg(Dest);
        if(LiteralFlag==1) {
          //Literal 5-bit signed
        } else if(Src==31) {
          //Immediate
        } else {
          //Reg-to-reg
        }
        long result=DestValue-TmpSrc;
        setFlags3(TmpSrc,(int)DestValue,(int)result);
        setReg(Dest,(int)result);
      };
    };
    microcode[0x0D]=new Operation() { // subb
      public void Execute1() {throw new UndefinedStateException("Opcode not yet implemented",Disassembler.opcode[0x0D]);};
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {};
    };
    microcode[0x0E]=new Operation() { // sb
      public void Execute1() {throw new UndefinedStateException("Opcode not yet implemented",Disassembler.opcode[0x0E]);};
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {};
    };
    microcode[0x0F]=new Operation() { // cmp
      public void Execute1() {
        getSrc();
      }
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {
        long DestValue;
        DestValue=getReg(Dest);
        long result=DestValue-TmpSrc;
        setFlags1((int)result);
      };
    };
    microcode[0x10]=new Operation() { //and
      public void Execute1() {
        getSrc();
      }
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {
        long DestValue;
        DestValue=getReg(Dest);
        if(LiteralFlag==1) {
          //Literal 5-bit signed
        } else if(Src==31) {
          //Immediate
        } else {
          //Reg-to-reg
        }
        long result=DestValue & TmpSrc;
        setFlags2(TmpSrc,(int)DestValue,(int)result);
        setReg(Dest,(int)result);
      };
    };
    microcode[0x11]=new Operation() { //or
      public void Execute1() {throw new UndefinedStateException("Opcode not yet implemented",Disassembler.opcode[0x11]);};
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {};
    };
    microcode[0x12]=new Operation() { //xor
      public void Execute1() {throw new UndefinedStateException("Opcode not yet implemented",Disassembler.opcode[0x12]);};
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {};
    };
    microcode[0x13]=new Operation() { 
      public void Execute1() {throw new UndefinedStateException("Opcode not yet implemented",Disassembler.opcode[0x13]);};
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {};
    };
    microcode[0x14]=new Operation() { 
      public void Execute1() {throw new UndefinedStateException("Opcode not yet implemented",Disassembler.opcode[0x14]);};
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {};
    };
    microcode[0x15]=new Operation() { 
      public void Execute1() {throw new UndefinedStateException("Opcode not yet implemented",Disassembler.opcode[0x15]);};
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {};
    };
    microcode[0x16]=new Operation() { 
      public void Execute1() {throw new UndefinedStateException("Opcode not yet implemented",Disassembler.opcode[0x16]);};
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {};
    };
    microcode[0x17]=new Operation() { 
      public void Execute1() {throw new UndefinedStateException("Opcode not yet implemented",Disassembler.opcode[0x17]);};
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {};
    };
    microcode[0x18]=new Operation() { // slr
      public void Execute1() {
        getSrc();
      }
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {
        long DestValue;
        DestValue=getReg(Dest);
        if(LiteralFlag==1) {
          //Literal 5-bit signed
        } else if(Src==31) {
          //Immediate
        } else {
          //Reg-to-reg
        }
        boolean shiftRight=(TmpSrc<0);
        int shiftAmount=shiftRight?-TmpSrc:TmpSrc+1;
        long result;
        if(shiftRight) {
          result=DestValue>>>shiftAmount;
        } else {
          result=DestValue<<shiftAmount;
        }
        setFlags2(TmpSrc,(int)DestValue,(int)result);
        setReg(Dest,(int)result);
      };
    };
    microcode[0x19]=new Operation() { //sar 
      public void Execute1() {
        getSrc();
      }
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {
        long DestValue;
        DestValue=getReg(Dest);
        if(LiteralFlag==1) {
          //Literal 5-bit signed
        } else if(Src==31) {
          //Immediate
        } else {
          //Reg-to-reg
        }
        boolean shiftRight=(TmpSrc<0);
        int shiftAmount=shiftRight?-TmpSrc:TmpSrc+1;
        long result;
        if(shiftRight) {
          result=DestValue>>shiftAmount;
        } else {
          result=DestValue<<shiftAmount;
        }
        setFlags2(TmpSrc,(int)DestValue,(int)result);
        setReg(Dest,(int)result);
      };
    };
    microcode[0x1A]=new Operation() { 
      public void Execute1() {throw new UndefinedStateException("Opcode not yet implemented",Disassembler.opcode[0x1A]);};
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {};
    };
    microcode[0x1B]=new Operation() { 
      public void Execute1() {throw new UndefinedStateException("Opcode not yet implemented",Disassembler.opcode[0x1B]);};
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {};
    };
    microcode[0x1C]=new Operation() { 
      public void Execute1() {throw new UndefinedStateException("Opcode not yet implemented",Disassembler.opcode[0x1C]);};
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {};
    };
    microcode[0x1D]=new Operation() { 
      public void Execute1() {throw new UndefinedStateException("Opcode not yet implemented",Disassembler.opcode[0x1D]);};
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {};
    };
    microcode[0x1E]=new Operation() { 
      public void Execute1() {throw new UndefinedStateException("Opcode not yet implemented",Disassembler.opcode[0x1E]);};
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {};
    };
    microcode[0x1F]=new Operation() { // br/jc
      public void Execute1() {
        getSrc(); //Gotta get the source anyway, since even if jump not taken
        //if there is an imm, the imm is not to be directly executed
      }
      public void Execute2() {};
      public void Execute3() {};
      public void Execute4() {
        switch(Dest) {
          case 0x00: //ncv
            ConditionMet=!getFlag(C) & !getFlag(V);  break;
          case 0x01: //nv
            ConditionMet=!getFlag(V);    break;
          case 0x02: //nc      
            ConditionMet=!getFlag(C);            break;
          case 0x03: //j
            ConditionMet=getFlag(J);            break;
          case 0x04: //lt     
            ConditionMet=getFlag(N);            break;
          case 0x05: //clt   
            ConditionMet=getFlag(C) & getFlag(N);            break;
          case 0x06: //vlt      
            ConditionMet=getFlag(V) & getFlag(N);            break;
          case 0x07: //vclt
            ConditionMet=getFlag(V) & getFlag(C) & getFlag(N);            break;
          case 0x08: //eq
            ConditionMet=getFlag(Z);            break;
          case 0x09: //ceq
            ConditionMet=getFlag(C) & getFlag(Z);            break;
          case 0x0A: //veq
            ConditionMet=getFlag(V) & getFlag(Z);            break;
          case 0x0B: //vceq
            ConditionMet=getFlag(V) & getFlag(C) & getFlag(Z);            break;
          case 0x0C: //le
            ConditionMet=getFlag(N) | getFlag(Z);            break;
          case 0x0D: //cle
            ConditionMet=getFlag(C) & (getFlag(N) | getFlag(Z));            break;
          case 0x0E: //vle
            ConditionMet=getFlag(V) & (getFlag(N) | getFlag(Z));            break;
          case 0x0F: //vcle
            ConditionMet=getFlag(V) & getFlag(C) & (getFlag(N) | getFlag(Z));            break;
          case 0x10: //gt
            ConditionMet=getFlag(P);            break;
          case 0x11: //cgt
            ConditionMet=getFlag(C) & getFlag(P);            break;
          case 0x12: //vgt
            ConditionMet=getFlag(V) & getFlag(P);            break;
          case 0x13: //vcgt
            ConditionMet=getFlag(V) & getFlag(C) & getFlag(P);            break;
          case 0x14: //ne
            ConditionMet=!getFlag(Z);            break;
          case 0x15: //cne
            ConditionMet=getFlag(C) & !getFlag(Z);            break;
          case 0x16: //vne
            ConditionMet=getFlag(V) & !getFlag(Z);            break;
          case 0x17: //vcne
            ConditionMet=getFlag(V) & getFlag(C) & !getFlag(Z);            break;
          case 0x18: //ge
            ConditionMet=getFlag(P) | getFlag(Z);            break;
          case 0x19: //cge
            ConditionMet=getFlag(C) & (getFlag(P) | getFlag(Z));            break;
          case 0x1A: //vge
            ConditionMet=getFlag(V) & (getFlag(P) | getFlag(Z));            break;
          case 0x1B: //vcge
            ConditionMet=getFlag(V) & getFlag(C) & (getFlag(P) | getFlag(Z));            break;
          case 0x1C: //x
            ConditionMet=true;            break;
          case 0x1D: //c
            ConditionMet=getFlag(C);            break;
          case 0x1E: //v
            ConditionMet=getFlag(V);            break;
          case 0x1F: //vc       
            ConditionMet=getFlag(V) & getFlag(C);            break;
        }
        if(ConditionMet) {
          ouf.println("Jump taken");
          ics=ic+1;
          ic=(short)((ic+TmpSrc) & 0xFFFF);
        } else {
          ouf.println("Jump not taken");
        }
      };
    };
  };
  public UT69R000(PrintStream Louf) {
    ouf=Louf;
  }
  public static void main(String[] args) throws IOException {
    PrintStream ouf=new PrintStream(new FileOutputStream("surom.trace"));
    UT69R000 Proc=new UT69R000(ouf);
/*
    long[] Program1=Assembler.asm(new String[] {
            "nop",
            "add r0,1",
            "call r1,1000",
            "br x,-2",
            "nop"
        });
    Proc.Ins=new SUROM(ouf,0,Program1);
    ((CIPSInstructionMemory)(Proc.Ins)).loadBlock(0x1000,Assembler.asm("call r1,r1"));
*/
    Proc.Ins=new SUROM(ouf,0,"surom/surom.code.bin");
    ouf.println(Disassembler.disasm(((CIPSInstructionMemory)(Proc.Ins)).page,0x0000,0x6));
    ouf.println(Disassembler.disasm(((CIPSInstructionMemory)(Proc.Ins)).page,0x1D47,0x10));
    Proc.Op=new CIPSOperandMemory(ouf);
    Proc.IO=new CIPSProcFPGA(ouf, (CIPSOperandMemory)Proc.Op);
    for(int i=0;;i++) {
      ouf.println("Cycle "+i);
      Proc.cycle();     
    }
  }
}
