package org.kwansystems.emulator.arm;

public enum Operation {
  LSLimm {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      boolean setFlags=!datapath.inIT;
      DecodeShiftReturn r=DecodeImmShift(0b00,ins.imm);
      if(ins.cond.shouldExecute(datapath.APSR)) {
        ShiftReturn result=Shift_C(datapath.r[ins.Rm],SRType.LSL,r.shift_n,datapath.APSR_C());
        datapath.r[ins.Rd]=result.result;
        if(setFlags) {
          datapath.APSR_setN((result.result>>31) & 1);
          datapath.APSR_setZ(result.result==0);
          datapath.APSR_setC(result.carry_out);
        }
      }
    }
  },
  LSRimm {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      boolean setFlags=!datapath.inIT;
      DecodeShiftReturn r=DecodeImmShift(0b01,ins.imm);
      if(ins.cond.shouldExecute(datapath.APSR)) {
        ShiftReturn result=Shift_C(datapath.r[ins.Rm],SRType.LSR,r.shift_n,datapath.APSR_C());
        datapath.r[ins.Rd]=result.result;
        if(setFlags) {
          datapath.APSR_setN((result.result>>31) & 1);
          datapath.APSR_setZ(result.result==0);
          datapath.APSR_setC(result.carry_out);
        }
      }
    }
  },
  ASRimm {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      boolean setFlags=!datapath.inIT;
      DecodeShiftReturn r=DecodeImmShift(0b10,ins.imm);
      if(ins.cond.shouldExecute(datapath.APSR)) {
        ShiftReturn result=Shift_C(datapath.r[ins.Rm],SRType.ASR,r.shift_n,datapath.APSR_C());
        datapath.r[ins.Rd]=result.result;
        if(setFlags) {
          datapath.APSR_setN((result.result>>31) & 1);
          datapath.APSR_setZ(result.result==0);
          datapath.APSR_setC(result.carry_out);
        }
      }
    }
  },
  LDRreg {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      DecodeShiftReturn r=new DecodeShiftReturn();
      int address=datapath.r[ins.Rn]+LSL(datapath.r[ins.Rm],r.shift_n);
      if(ins.Rd==15) {
        if((address & 0b11) != 0b00) throw new RuntimeException("Unpredictable"); //Address must be word-aligned
        datapath.LoadWritePC(datapath.readMem4(address));
      } else {
        datapath.r[ins.Rd]=datapath.readMem4(address);
      }
    }
  },
  LDRimm {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      boolean index=true;
      boolean add=ins.U;
      boolean wback=false;
      int imm32=(ins.imm & ((1<<5)-1)) * 4;
      if(ins.cond.shouldExecute(datapath.APSR)) {
        int offset_addr=add?(datapath.r[ins.Rn]+imm32):(datapath.r[ins.Rn]-imm32);
        int address=index?offset_addr:datapath.r[ins.Rn];
        if(wback) datapath.r[ins.Rn]=offset_addr;
        if(ins.Rd==15) {
          if((address & 0b11) != 0b00) throw new RuntimeException("Unpredictable"); //Address must be word-aligned
          datapath.LoadWritePC(datapath.readMem4(address));
        } else {
          datapath.r[ins.Rd]=datapath.readMem4(address);
        }
      }
    }
  },
  STRimm {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
    }
  },
  LDR1,LDR2,LDR3,LDR4,LDR5,LDR6,LDR7,
  UNDEF;
  private enum SRType {NONE,LSL,LSR,ASR,ROR,RRX};
  private static class ShiftReturn {
    public int result;
    public boolean carry_out;
    public ShiftReturn(int Lresult, boolean Lcarry_out) {result=Lresult;carry_out=Lcarry_out;};
    public ShiftReturn() {this(0,false);};
    public String toString() {return String.format("(0x%08x,%d)\n", result,carry_out?1:0);};
  };
  private static class DecodeShiftReturn {
    public SRType shift_t;
    public int shift_n;
    public DecodeShiftReturn(SRType Lshift_t, int Lshift_n) {shift_t=Lshift_t;shift_n=Lshift_n;};
    public DecodeShiftReturn() {this(SRType.NONE,0);};
  }
  private static DecodeShiftReturn DecodeImmShift(int type, int imm) {
    DecodeShiftReturn r=new DecodeShiftReturn();
    switch(type) {
      case 0:
        r.shift_t=SRType.LSL;
        r.shift_n=imm;
      case 1:
        r.shift_t=SRType.LSR;
        r.shift_n=(imm==0)?32:imm;
      case 2:
        r.shift_t=SRType.ASR;
        r.shift_n=(imm==0)?32:imm;
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
  private static ShiftReturn Shift_C(int value, SRType type, int amount, boolean carry_in) {
    ShiftReturn r;
    int carry_out;
    switch(type) {
      case LSL:
        if(amount==0) {
          r=new ShiftReturn(value,carry_in);
        } else {
          r=LSL_C(value,amount);
        }
      default: //Intentionally catches case NONE:
        r=new ShiftReturn(value,carry_in);
    }
    return r;
  };
  private static ShiftReturn LSL_C(int x, int n) {
    return new ShiftReturn(x << n,(x & (1<<(32-n)))!=0);
  }
  private static int LSL(int x, int n) {
    return LSL_C(x,n).result;
  }
  public void execute(Datapath datapath, DecodedInstruction ins) {
    throw new RuntimeException("Unimplemented Instruction");
  };
  public static void main(String args[]) {
    System.out.println("LSL_C(0xC0000000,1) should be (0x80000000,1)");
    ShiftReturn r=LSL_C(0xC0000000,1);
    System.out.println(r);
  }
}
