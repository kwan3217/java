package org.kwansystems.emulator.arm;

import static org.kwansystems.emulator.arm.BitFiddle.*;

public enum Operation {
  /*
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
  */
  LDRlit {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      int base=datapath.r[15] & ~(0x03);
      int address=ins.add?base+ins.imm:base-ins.imm;
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
      if(ins.cond.shouldExecute(datapath.APSR)) {
        int offset_addr=ins.add?(datapath.r[ins.Rn]+ins.imm):(datapath.r[ins.Rn]-ins.imm);
        int address=ins.index?offset_addr:datapath.r[ins.Rn];
        if(ins.wback) datapath.r[ins.Rn]=offset_addr;
        if(ins.Rd==15) {
          if((address & 0b11) != 0b00) throw new RuntimeException("Unpredictable"); //Address must be word-aligned
          datapath.LoadWritePC(datapath.readMem4(address));
        } else {
          datapath.r[ins.Rd]=datapath.readMem4(address);
        }
      }
    }
  },
  ANDimm {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(ins.cond.shouldExecute(datapath.APSR)) {
        ShiftReturn r=Shift_C(datapath.r[ins.Rm],ins.shift_t,ins.shift_n,datapath.APSR_C());
        int result=datapath.r[ins.Rn] & r.result;
        datapath.r[ins.Rd]=result;
        if(ins.setflags) {
          datapath.APSR_setN(parseBit(result,31));
          datapath.APSR_setZ(result==0);
          datapath.APSR_setC(r.carry_out);
        }
      }
    }
  },
  STRimm {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      int offset_addr=ins.add?(datapath.r[ins.Rn]+ins.imm):(datapath.r[ins.Rn]-ins.imm);
      int address=ins.index?offset_addr:datapath.r[ins.Rn];
      if(ins.wback) datapath.r[ins.Rn]=offset_addr;
      datapath.writeMem4(address, datapath.r[ins.Rd]);
    }
  }, 
  CMPreg {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      int shifted=Shift(datapath.r[ins.Rm],ins.shift_t,ins.shift_n,datapath.APSR_C());
      AddWithCarryReturn r=AddWithCarry(datapath.r[ins.Rn],~shifted,true); //Implement subtract using just the adder
      datapath.APSR_setN(parseBit(r.result,31));
      datapath.APSR_setZ(r.result==0);
      datapath.APSR_setC(r.carry_out);
      datapath.APSR_setV(r.overflow);
    }
  },
  IT {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      datapath.setIT(ins.firstcond,ins.mask);
    }
  },
  UNDEFINED {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      throw new Undefined(String.format("Undefined instruction %08x at pc %08x",ins.imm,ins.pc));
    }
  },
  UNPREDICTABLE {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      throw new Unpredictable();
    }
  };
  private static class ShiftReturn {
    public int result;
    public boolean carry_out;
    public ShiftReturn(int Lresult, boolean Lcarry_out) {result=Lresult;carry_out=Lcarry_out;};
    public ShiftReturn() {this(0,false);};
    public String toString() {return String.format("(0x%08x,%d)\n", result,carry_out?1:0);};
  };
  private static int Shift(int value, SRType type, int amount, boolean carry_in) {
    return Shift_C(value,type,amount,carry_in).result;
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
  private static class AddWithCarryReturn {
    public int result;
    public boolean carry_out, overflow;
  }
  private static AddWithCarryReturn AddWithCarry(int x, int y, boolean carry_in) {
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
  public void execute(Datapath datapath, DecodedInstruction ins) {
    throw new RuntimeException("Unimplemented Instruction");
  };
  public static void main(String args[]) {
    System.out.println("LSL_C(0xC0000000,1) should be (0x80000000,1)");
    ShiftReturn r=LSL_C(0xC0000000,1);
    System.out.println(r);
  }
}
