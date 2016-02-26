package org.kwansystems.emulator.arm;

import static org.kwansystems.emulator.arm.BitFiddle.*;
import static org.kwansystems.emulator.arm.Datapath.*;

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
      if(datapath.ConditionPassed(ins.cond)) {
        //Finish up logic which we can't have done in decode, but is in encoding-specific. T1 can't
        //encode d==15, so we don't have to worry that this logic only applies to T2
        if(ins.Rd==15 && datapath.InITBlock() && !datapath.LastInITBlock()) throw new Unpredictable("Trying to branch from within an IT block");
        //From here on is not encoding-specific
        System.out.printf("Using pc%c%d as address\n",ins.add?'+':'-',ins.imm);
        int base=datapath.r[15] & ~(0x03);
        int address=ins.add?base+ins.imm:base-ins.imm;
        if(ins.Rd==15) {
          if((address & 0b11) != 0b00) throw new Unpredictable("Address is not word-aligned");
          int value=datapath.readMem4(address);
          datapath.LoadWritePC(value);
        } else {
          int value=datapath.readMem4(address);
          System.out.printf("Loading r%d with %08x\n", ins.Rd, value);
          datapath.r[ins.Rd]=value;
        }
      }
    }
  },
  LDRimm {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        //Same as LDRlit
        if(ins.Rd==15 && datapath.InITBlock() && !datapath.LastInITBlock()) throw new Unpredictable("Trying to branch from within an IT block");
        //From here on is not encoding-specific
        int offset_addr=ins.add?(datapath.r[ins.Rn]+ins.imm):(datapath.r[ins.Rn]-ins.imm);
        int address=ins.index?offset_addr:datapath.r[ins.Rn];
        if(ins.index) {
          System.out.printf("Using r%d as address\n",ins.Rn);
        } else {
          System.out.printf("Using r%d%c%d as address\n",ins.Rn,ins.add?'+':'-',ins.imm);
        }
        if(ins.wback) datapath.r[ins.Rn]=offset_addr;
        if(ins.Rd==15) {
          if((address & 0b11) != 0b00) throw new Unpredictable("Address is not word-aligned");
          int value=datapath.readMem4(address);
          datapath.LoadWritePC(value);
        } else {
          int value=datapath.readMem4(address);
          System.out.printf("Loading r%d with %08x\n", ins.Rd, value);
          datapath.r[ins.Rd]=datapath.readMem4(address);
        }
      }
    }
  },
  ANDreg {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        System.out.printf("Second argument=r%d %s %d=",ins.Rm,ins.shift_t.toString(),ins.shift_n);
        ResultWithCarry r=Shift_C(datapath.r[ins.Rm],ins.shift_t,ins.shift_n,datapath.APSR_C());
        System.out.printf("%08x with carry %d\n", r.result,r.carry_out?1:0);
        int result=datapath.r[ins.Rn] & r.result;
        System.out.printf("r%d=r%d(%08x) & %08x=%08x", ins.Rd, ins.Rn, datapath.r[ins.Rn],r.result,result);
        datapath.r[ins.Rd]=result;
        if(datapath.shouldSetFlags(ins.setflags)) {
          datapath.APSR_setN(parseBit(result,31));
          datapath.APSR_setZ(result==0);
          datapath.APSR_setC(r.carry_out);
          System.out.printf(", N=%d", datapath.APSR_N()?1:0);
          System.out.printf(", Z=%d", datapath.APSR_Z()?1:0);
          System.out.printf(", C=%d", datapath.APSR_C()?1:0);
        }
        System.out.println();
      }
    }
  },
  STRimm {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        int offset_addr=ins.add?(datapath.r[ins.Rn]+ins.imm):(datapath.r[ins.Rn]-ins.imm);
        int address=ins.index?offset_addr:datapath.r[ins.Rn];
        if(ins.index) {
          System.out.printf("Using r%d as address\n",ins.Rn);
        } else {
          System.out.printf("Using r%d%c%d as address\n",ins.Rn,ins.add?'+':'-',ins.imm);
        }
        if(ins.wback) datapath.r[ins.Rn]=offset_addr;
        System.out.printf("Storing r%d\n", ins.Rd);
        datapath.writeMem4(address, datapath.r[ins.Rd]);
      }
    }
  }, 
  CMPreg {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        System.out.printf("Second argument=r%d %s %d=",ins.Rm,ins.shift_t.toString(),ins.shift_n);
        int shifted=Shift(datapath.r[ins.Rm],ins.shift_t,ins.shift_n,datapath.APSR_C());
        System.out.printf("%08x\n", shifted);
        AddWithCarryReturn r=AddWithCarry(datapath.r[ins.Rn],~shifted,true); //Implement subtract using just the adder
        System.out.printf("result=r%d(%08x) - %08x=%08x", ins.Rn, datapath.r[ins.Rn],shifted,r.result);
        datapath.APSR_setN(parseBit(r.result,31));
        datapath.APSR_setZ(r.result==0);
        datapath.APSR_setC(r.carry_out);
        datapath.APSR_setV(r.overflow);
        System.out.printf(", N=%d", datapath.APSR_N()?1:0);
        System.out.printf(", Z=%d", datapath.APSR_Z()?1:0);
        System.out.printf(", C=%d", datapath.APSR_C()?1:0);
        System.out.printf(", V=%d\n", datapath.APSR_V()?1:0);
      }
    }
  },
  IT {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      System.out.printf("Condition code %s, mask %01x\n",ConditionCode.enumValues[ins.firstcond],ins.mask);
      datapath.StartITBlock(ins.firstcond,ins.mask);
    }
  },
  B {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        //TODO - pick up some UNPREDICTABLE stuff from the encodings
        datapath.BranchWritePC(datapath.r[15]+ins.imm);
      }
    }
  },
  MOVimm {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      //TODO
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
  public abstract void execute(Datapath datapath, DecodedInstruction ins);
}
