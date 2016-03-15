package org.kwansystems.emulator.arm;

import static org.kwansystems.emulator.arm.BitFiddle.*;
import static org.kwansystems.emulator.arm.Datapath.*;

import org.kwansystems.emulator.arm.Datapath.AddWithCarryReturn;

import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;

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
        int base=ins.pc+4 & ~(0x03);
        int address=ins.add?base+ins.imm:base-ins.imm;
        System.out.printf("Using pc%c%d=0x%08x as address\n",ins.add?'+':'-',ins.imm,address);
        int data=datapath.readMem4(address);
        if(ins.Rd==15) {
          if((address & 0b11) != 0b00) throw new Unpredictable("Address is not word-aligned");
          datapath.LoadWritePC(data);
        } else {
          System.out.printf("Loading r%d with %08x\n", ins.Rd, data);
          datapath.r[ins.Rd]=data;
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
          System.out.printf("Using r%d=0x%08x as address\n",ins.Rn,address);
        } else {
          System.out.printf("Using r%d%c%d=0x%08x as address\n",ins.Rn,ins.add?'+':'-',ins.imm,address);
        }
        if(ins.wback) datapath.r[ins.Rn]=offset_addr;
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
          System.out.printf("Using r%d(0x%08x)%c%d=0x%08x as address\n",ins.Rn,datapath.r[ins.Rn],ins.add?'+':'-',ins.imm,address);
        } else {
          System.out.printf("Using r%d(0x%08x) as address\n",ins.Rn,address);
        }
        if(ins.wback) {
          datapath.r[ins.Rn]=offset_addr;
          System.out.printf("Writing back 0x%08x to r%d\n", offset_addr,ins.Rn);
        }
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
        int pc=datapath.r[15];
        if(ins.is32) {pc-=2;}
        int target=pc+ins.imm;
        System.out.printf("Jumping to r15(0x%08x)+0x%08x=0x%08x\n",datapath.r[15],ins.imm,target);
        datapath.BranchWritePC(target);
      }
    }
  },
  MOVimm {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      ResultWithCarry r;
      if(ins.thumbExpand) {
        //Finish up decode here now that ASPR.C is available
        r=datapath.ThumbExpandImmWithC(ins.imm);
      } else {
        r=new ResultWithCarry();
        r.result=ins.imm;
        r.carry_out=datapath.APSR_C();
      }
      if(datapath.ConditionPassed(ins.cond)) {
        System.out.printf("Moving %08x into r%d", r.result,ins.Rd);
        datapath.r[ins.Rd]=r.result;
        if(datapath.shouldSetFlags(ins.setflags)) {
          datapath.APSR_setN(parseBit(r.result,31));
          datapath.APSR_setZ(r.result==0);
          datapath.APSR_setC(r.carry_out);
          System.out.printf(", N=%d, Z=%d, C=%d", datapath.APSR_N()?1:0, datapath.APSR_Z()?1:0, datapath.APSR_C()?1:0);
        }
      }
      System.out.println();
    }
  },
  SUBimm {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      //Pick up things that couldn't be done in decode
      if(ins.thumbExpand) ins.imm=datapath.ThumbExpandImm(ins.imm,false).result;
      if(datapath.ConditionPassed(ins.cond)) {
        System.out.printf("Second argument=0x%08x\n",ins.imm);
        AddWithCarryReturn r=AddWithCarry(datapath.r[ins.Rn],~ins.imm,true); //Implement subtract using just the adder
        System.out.printf("r%d=r%d(%08x) - %08x=%08x", ins.Rd, ins.Rn, datapath.r[ins.Rn],ins.imm,r.result);
        datapath.r[ins.Rd]=r.result;
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
  SUBreg {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        int shifted=Shift(datapath.r[ins.Rm],ins.shift_t, ins.shift_n,datapath.APSR_C());
        System.out.printf("Right argument=r%d(0x%08x) %s %d=0x%08x\n", ins.Rm,datapath.r[ins.Rm],ins.shift_t.toString(),ins.shift_n,shifted);
        AddWithCarryReturn r=AddWithCarry(datapath.r[ins.Rn],~shifted,true);
        System.out.printf("r%d=r%d(0x%08x) - 0x%08x=0x%08x", ins.Rd,ins.Rn,datapath.r[ins.Rn],shifted,r.result);
        if(ins.Rd==15) {
          datapath.ALUWritePC(r.result); //setflags is always FALSE here
        } else {
          datapath.r[ins.Rd]=r.result;
          if(datapath.shouldSetFlags(ins.setflags)) {
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
        System.out.println();
      }
    }
  },
  MOVreg {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        int result=datapath.r[ins.Rm];
        if(ins.Rd==15) {
          System.out.printf("Jumping to r%d=0x%08x\n",ins.Rm,result);
          datapath.ALUWritePC(result);
        } else {
          datapath.r[ins.Rd]=result;
          System.out.printf("r%d=r%d=0x%08x", ins.Rd, ins.Rm, result);
          if(datapath.shouldSetFlags(ins.setflags)) {
            datapath.APSR_setN(parseBit(result,31));
            datapath.APSR_setZ(result==0);
            System.out.printf(", N=%d, Z=%d", datapath.APSR_N()?1:0, datapath.APSR_Z()?1:0);
          }
          System.out.println();
        }
      }
    }
  },
  BX {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        System.out.printf("Jumping to r%d=0x%08x\n",ins.Rm,datapath.r[ins.Rm]);
        datapath.BXWritePC(datapath.r[ins.Rm]);
      }
    }
  },
  STMDB {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        int address=datapath.r[ins.Rn]-4*BitCount(ins.imm);
        System.out.println("Storing multiple registers:");
        for(int i=0;i<=14;i++) {
          if(parseBit(ins.imm,i)) {
            System.out.printf(" r%d ",i);
            datapath.writeMem4(address, datapath.r[i]);
            address+=4;
          }
        }
        if(ins.wback) { //Write back last, because otherwise we would push the changed value
          datapath.r[ins.Rn]=datapath.r[ins.Rn]-4*BitCount(ins.imm);
          System.out.printf("Writing back, r%d=0x%08x\n", ins.Rn, datapath.r[ins.Rn]);
        }
      }
    }
  },
  PUSH {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      //TODO - something about UnalignedAllowed
      if(datapath.ConditionPassed(ins.cond)) {
        int address=datapath.r[13]-4*BitCount(ins.imm);
        System.out.println("Pushing register(s):");
        for(int i=0;i<=14;i++) {
          if(parseBit(ins.imm,i)) {
            System.out.printf(" r%d ",i);
            datapath.writeMem4(address, datapath.r[i]);
            address+=4;
          }
        }
        datapath.r[13]=datapath.r[13]-4*BitCount(ins.imm);
        System.out.printf("Writing back, r%d=0x%08x\n", 13, datapath.r[13]);
      }
    }
  },
  BL {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        int next_ins_addr=datapath.r[15]-2;
        datapath.r[14]=next_ins_addr | 1;
        System.out.printf("lr=pc=0x%08x\n",datapath.r[14],ins.imm>=0?"+":"",ins.imm);
        int target_addr=next_ins_addr+ins.imm;
        System.out.printf("Branch and link, pc%s0x%06x=0x%08x\n",ins.imm>=0?"+":"",ins.imm,target_addr);
        datapath.BranchWritePC(target_addr);
      }
    }
  },
  BIC {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      //Pick up things that couldn't be done in decode
      ResultWithCarry r=new ResultWithCarry();
      if(ins.thumbExpand) {
        r=datapath.ThumbExpandImmWithC(ins.imm);
        ins.imm=r.result;
      }
      if(datapath.ConditionPassed(ins.cond)) {
        int result=datapath.r[ins.Rn] & ~ins.imm;
        System.out.printf("r%d=r%d(%08x) AND NOT(%08x)=%08x", ins.Rd, ins.Rn, datapath.r[ins.Rn],ins.imm,result);
        datapath.r[ins.Rd]=result;
        if(datapath.shouldSetFlags(ins.setflags)) {
          datapath.APSR_setN(parseBit(result,31));
          datapath.APSR_setZ(result==0);
          datapath.APSR_setC(r.carry_out);
          System.out.printf(", N=%d", datapath.APSR_N()?1:0);
          System.out.printf(", Z=%d", datapath.APSR_Z()?1:0);
          System.out.printf(", C=%d", datapath.APSR_C()?1:0);
        }
      }
      System.out.println();
    }
  },
  UDIV {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        if(datapath.r[ins.Rm]==0) {
          // TODO - if IntegerZeroDivideTrappingEnabled() then
          //          GenerateIntegerZeroDivide();
          //        else
          //          result=0;
          throw new IllegalArgumentException("Division by zero -- write some code to handle this");
        }
        int result=(int)((((long)datapath.r[ins.Rn])&0xFFFFFFFF)/(((long)datapath.r[ins.Rm])&0xFFFFFFFF));
        System.out.printf("r%d=UInt(r%d(%08x)) / UInt(r%d(%08x))=%08x", ins.Rd, ins.Rn, datapath.r[ins.Rn],ins.Rm, datapath.r[ins.Rm],result);
        datapath.r[ins.Rd]=result;
      }
      System.out.println();
    }
  },
  ADDreg {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        int shifted=Shift(datapath.r[ins.Rm],ins.shift_t, ins.shift_n,datapath.APSR_C());
        System.out.printf("Right argument=r%d(0x%08x) %s %d=0x%08x\n", ins.Rm,datapath.r[ins.Rm],ins.shift_t.toString(),ins.shift_n,shifted);
        AddWithCarryReturn r=AddWithCarry(datapath.r[ins.Rn],shifted,false);
        System.out.printf("r%d=r%d(0x%08x) + 0x%08x=0x%08x", ins.Rd,ins.Rn,datapath.r[ins.Rn],shifted,r.result);
        if(ins.Rd==15) {
          datapath.ALUWritePC(r.result); //setflags is always FALSE here
        } else {
          datapath.r[ins.Rd]=r.result;
          if(datapath.shouldSetFlags(ins.setflags)) {
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
        System.out.println();
      }
    }
  },
  ADDimm {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        AddWithCarryReturn r=AddWithCarry(datapath.r[ins.Rn],ins.imm,false);
        System.out.printf("r%d=r%d(0x%08x) + 0x%08x=0x%08x", ins.Rd,ins.Rn,datapath.r[ins.Rn],ins.imm,r.result);
        datapath.r[ins.Rd]=r.result;
        if(datapath.shouldSetFlags(ins.setflags)) {
          datapath.APSR_setN(parseBit(r.result,31));
          datapath.APSR_setZ(r.result==0);
          datapath.APSR_setC(r.carry_out);
          datapath.APSR_setV(r.overflow);
          System.out.printf(", N=%d", datapath.APSR_N()?1:0);
          System.out.printf(", Z=%d", datapath.APSR_Z()?1:0);
          System.out.printf(", C=%d", datapath.APSR_C()?1:0);
          System.out.printf(", V=%d\n", datapath.APSR_V()?1:0);
        }
        System.out.println();
      }
    }
  },
  CMPimm {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        AddWithCarryReturn r=AddWithCarry(datapath.r[ins.Rn],~ins.imm,true);
        System.out.printf("result=r%d(0x%08x) - 0x%08x=0x%08x", ins.Rn,datapath.r[ins.Rn],ins.imm,r.result);
        datapath.APSR_setN(parseBit(r.result,31));
        datapath.APSR_setZ(r.result==0);
        datapath.APSR_setC(r.carry_out);
        datapath.APSR_setV(r.overflow);
        System.out.printf(", N=%d", datapath.APSR_N()?1:0);
        System.out.printf(", Z=%d", datapath.APSR_Z()?1:0);
        System.out.printf(", C=%d", datapath.APSR_C()?1:0);
        System.out.printf(", V=%d\n", datapath.APSR_V()?1:0);
        System.out.println();
      }
    }
  },
  ORRimm {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      //Pick up things that couldn't be done in decode
      ResultWithCarry r=new ResultWithCarry();
      if(ins.thumbExpand) {
        r=datapath.ThumbExpandImmWithC(ins.imm);
        ins.imm=r.result;
      }
      if(datapath.ConditionPassed(ins.cond)) {
        int result=datapath.r[ins.Rn] | ins.imm;
        System.out.printf("r%d=r%d(0x%08x) | 0x%08x=0x%08x", ins.Rd,ins.Rn,datapath.r[ins.Rn],ins.imm,result);
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
  ANDimm {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      //Pick up things that couldn't be done in decode
      ResultWithCarry r=new ResultWithCarry();
      if(ins.thumbExpand) {
        r=datapath.ThumbExpandImmWithC(ins.imm);
        ins.imm=r.result;
      }
      if(datapath.ConditionPassed(ins.cond)) {
        int result=datapath.r[ins.Rn] & ins.imm;
        System.out.printf("r%d=r%d(0x%08x) & 0x%08x=0x%08x", ins.Rd,ins.Rn,datapath.r[ins.Rn],ins.imm,result);
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
  ORRreg {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        int shifted=Shift(datapath.r[ins.Rm],ins.shift_t, ins.shift_n,datapath.APSR_C());
        System.out.printf("Right argument=r%d(0x%08x) %s %d=0x%08x\n", ins.Rm,datapath.r[ins.Rm],ins.shift_t.toString(),ins.shift_n,shifted);
        AddWithCarryReturn r=AddWithCarry(datapath.r[ins.Rn],shifted,false);
        System.out.printf("r%d=r%d(0x%08x) | 0x%08x=0x%08x", ins.Rd,ins.Rn,datapath.r[ins.Rn],shifted,r.result);
        if(ins.Rd==15) {
          datapath.ALUWritePC(r.result); //setflags is always FALSE here
        } else if(datapath.shouldSetFlags(ins.setflags)) {
          datapath.r[ins.Rd]=r.result;
          datapath.APSR_setN(parseBit(r.result,31));
          datapath.APSR_setZ(r.result==0);
          datapath.APSR_setC(r.carry_out);
          datapath.APSR_setV(r.overflow);
          System.out.printf(", N=%d", datapath.APSR_N()?1:0);
          System.out.printf(", Z=%d", datapath.APSR_Z()?1:0);
          System.out.printf(", C=%d", datapath.APSR_C()?1:0);
          System.out.printf(", V=%d\n", datapath.APSR_V()?1:0);
        }
        System.out.println();
      }
    }
  },
  STRDimm {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        int offset_addr=ins.add?(datapath.r[ins.Rn]+ins.imm):(datapath.r[ins.Rn]-ins.imm);
        int address=ins.index?offset_addr:datapath.r[ins.Rn];
        if(ins.index) {
          System.out.printf("Using r%d as address\n",ins.Rn);
        } else {
          System.out.printf("Using r%d%c%d as address\n",ins.Rn,ins.add?'+':'-',ins.imm);
        }
        System.out.printf("Storing r%d(0x%08x) at 0x%08x\n", ins.Rd,datapath.r[ins.Rd],address);
        datapath.writeMem4(address  , datapath.r[ins.Rd]);
        System.out.printf("Storing r%d(0x%08x) at 0x%08x\n", ins.Rm,datapath.r[ins.Rd],address+4);
        datapath.writeMem4(address+4, datapath.r[ins.Rm]);
        if(ins.wback) {
          System.out.printf("Writing back r%d=0x%08x\n", ins.Rn,offset_addr);
          datapath.r[ins.Rn]=offset_addr;
        }
      }
    }
  }, 
  LSLimm {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        ResultWithCarry r=Shift_C(datapath.r[ins.Rm],SRType.LSL,ins.shift_n,datapath.APSR_C());
        System.out.printf("r%d=r%d(0x%08x) << %d=0x%08x", ins.Rd,ins.Rm,datapath.r[ins.Rm],ins.imm,r.result);
        datapath.r[ins.Rd]=r.result;
        if(datapath.shouldSetFlags(ins.setflags)) {
          datapath.APSR_setN(parseBit(r.result,31));
          datapath.APSR_setZ(r.result==0);
          datapath.APSR_setC(r.carry_out);
          System.out.printf(", N=%d", datapath.APSR_N()?1:0);
          System.out.printf(", Z=%d", datapath.APSR_Z()?1:0);
          System.out.printf(", C=%d", datapath.APSR_C()?1:0);
        }
        System.out.println();
      }
    }
  },
  LDM {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        int address=datapath.r[ins.Rn];
        System.out.println("Loading multiple registers:");
        for(int i=0;i<=14;i++) {
          if(parseBit(ins.imm,i)) {
            System.out.printf(" r%d ",i);
            datapath.r[i]=datapath.readMem4(address);
            address+=4;
          }
        }
        if(parseBit(ins.imm,15)) {
          System.out.printf(" r%d ",15);
          datapath.LoadWritePC(datapath.readMem4(address));
        }
        if(ins.wback && parseBit(ins.imm,ins.Rn)) { //Write back last, because otherwise we would push the changed value
          datapath.r[ins.Rn]=datapath.r[ins.Rn]+4*BitCount(ins.imm);
          System.out.printf("Writing back, r%d=0x%08x\n", ins.Rn, datapath.r[ins.Rn]);
        }
      }
    }
  },
  POP {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        int address=datapath.r[13];
        datapath.r[13]=datapath.r[13]+4*BitCount(ins.imm);
        System.out.printf("Writing back, r13=0x%08x\n", datapath.r[13]);
        System.out.println("Popping multiple registers:");
        for(int i=0;i<=14;i++) {
          if(parseBit(ins.imm,i)) {
            System.out.printf(" r%d ",i);
            datapath.r[i]=datapath.readMem4(address);
            address+=4;
          }
        }
        if(parseBit(ins.imm,15)) {
          System.out.printf(" r%d ",15);
          datapath.LoadWritePC(datapath.readMem4(address));
        }
      }
    }
  },
  TSTreg {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        System.out.printf("Second argument=r%d %s %d=",ins.Rm,ins.shift_t.toString(),ins.shift_n);
        ResultWithCarry r=Shift_C(datapath.r[ins.Rm],ins.shift_t,ins.shift_n,datapath.APSR_C());
        System.out.printf("%08x\n", r.result);
        int result=datapath.r[ins.Rn] & r.result; 
        System.out.printf("result=r%d(%08x) & %08x=%08x", ins.Rn, datapath.r[ins.Rn],r.result,result);
        datapath.APSR_setN(parseBit(result,31));
        datapath.APSR_setZ(result==0);
        datapath.APSR_setC(r.carry_out);
        System.out.printf(", N=%d", datapath.APSR_N()?1:0);
        System.out.printf(", Z=%d", datapath.APSR_Z()?1:0);
        System.out.printf(", C=%d\n", datapath.APSR_C()?1:0);
      }
    }
  },
  STMIA {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        int address=datapath.r[ins.Rn];
        System.out.println("Storing multiple registers:");
        for(int i=0;i<=14;i++) {
          if(parseBit(ins.imm,i)) {
            System.out.printf(" r%d ",i);
            //TODO - Catch UNKNOWN condition -- write LowestSetBit()
            //if i == n && wback && i != LowestSetBit(registers) then
            //  MemA[address,4] = bits(32) UNKNOWN; // Encoding T1 only
            //else
            datapath.writeMem4(address, datapath.r[i]);
            address+=4;
          }
        }
        if(ins.wback) { 
          datapath.r[ins.Rn]=datapath.r[ins.Rn]+4*BitCount(ins.imm);
          System.out.printf("Writing back, r%d=0x%08x\n", ins.Rn, datapath.r[ins.Rn]);
        }
      }
    }
  },
  LDRreg {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        int offset=Shift(datapath.r[ins.Rm],ins.shift_t,ins.shift_n,datapath.APSR_C());
        int offset_addr=ins.add?(datapath.r[ins.Rn]+offset):(datapath.r[ins.Rn]-offset);
        int address;
        if(ins.index) {
          address=offset_addr;
          System.out.printf("address=r%d(0x%08x)%cr%d(0x%08x) %s %d=0x%08x\n",
                             ins.Rn,datapath.r[ins.Rn], 
                             ins.add?'+':'-',
                             ins.Rm,datapath.r[ins.Rm], 
                             ins.shift_t.toString(),ins.shift_n,
                             address);
        } else {
          address=datapath.r[ins.Rn];
          System.out.printf("address=r%d(0x%08x)\n",
                              ins.Rn,datapath.r[ins.Rn]);
        }
        int data=datapath.readMem4(address);
        if(ins.Rd==15) {
          if(parse(address,0,2)==0b00) {
            datapath.LoadWritePC(data);
          } else {
            throw new Unpredictable("Unaligned address");
          }
        } else {
          System.out.printf(" r%d=0x%08x\n",ins.Rd,data);
          datapath.r[ins.Rd]=data;
        }
      }
    }
  },
  CBZ {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.InITBlock()) throw new Unpredictable(); //This is specified in the encoding-specific operations
      System.out.printf("ins.nonzero: %s r%d: 0x%08x\n", ins.nonzero?"true":"false",ins.Rn, datapath.r[ins.Rn]);
      if(ins.nonzero!=(datapath.r[ins.Rn]==0)) {
        System.out.println("Taking branch");
        datapath.BranchWritePC(datapath.r[15]+ins.imm);
      } else {
        System.out.println("Not taking branch");
      }
    }
  },
  ADDspimm {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        AddWithCarryReturn r=AddWithCarry(datapath.r[13],ins.imm,false);
        System.out.printf("r%d=r%d(0x%08x) + 0x%08x=0x%08x", ins.Rd,13,datapath.r[13],ins.imm,r.result);
        datapath.r[ins.Rd]=r.result;
        if(datapath.shouldSetFlags(ins.setflags)) {
          datapath.APSR_setN(parseBit(r.result,31));
          datapath.APSR_setZ(r.result==0);
          datapath.APSR_setC(r.carry_out);
          datapath.APSR_setV(r.overflow);
          System.out.printf(", N=%d", datapath.APSR_N()?1:0);
          System.out.printf(", Z=%d", datapath.APSR_Z()?1:0);
          System.out.printf(", C=%d", datapath.APSR_C()?1:0);
          System.out.printf(", V=%d\n", datapath.APSR_V()?1:0);
        }
        System.out.println();
      }
    }
  },
  ASRimm {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        ResultWithCarry r=Shift_C(datapath.r[ins.Rm],SRType.ASR,ins.shift_n,datapath.APSR_C());
        System.out.printf("r%d=r%d(0x%08x) >> %d=0x%08x", ins.Rd,ins.Rm,datapath.r[ins.Rm],ins.imm,r.result);
        datapath.r[ins.Rd]=r.result;
        if(datapath.shouldSetFlags(ins.setflags)) {
          datapath.APSR_setN(parseBit(r.result,31));
          datapath.APSR_setZ(r.result==0);
          datapath.APSR_setC(r.carry_out);
          System.out.printf(", N=%d", datapath.APSR_N()?1:0);
          System.out.printf(", Z=%d", datapath.APSR_Z()?1:0);
          System.out.printf(", C=%d", datapath.APSR_C()?1:0);
        }
        System.out.println();
      }
    }
  },
  BLX {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        int target=datapath.r[ins.Rm];
        int next_ins_addr=datapath.r[15]-2;
        datapath.r[14]=next_ins_addr | 1;
        System.out.printf("lr=pc=0x%08x\n",datapath.r[14],ins.imm>=0?"+":"",ins.imm);
        datapath.BLXWritePC(target);
      }
    }
  },
  MLS {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        int operand1=datapath.r[ins.Rn]; 
        int operand2=datapath.r[ins.Rm]; 
        int addend=datapath.r[ins.Ra]; 
        int result=addend-operand1*operand2;
        System.out.printf("r%d=r%d(0x%08x)-r%d(0x%08x)*r%d(0x%08x)=0x%08x\n",ins.Rd,ins.Ra,datapath.r[ins.Ra],
            ins.Rn,datapath.r[ins.Rn],
            ins.Rm,datapath.r[ins.Rm],result
            );
        datapath.r[ins.Rd]=result;
      }
    }
  },
  LDRBimm {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        int offset_addr=ins.add?(datapath.r[ins.Rn]+ins.imm):(datapath.r[ins.Rn]-ins.imm);
        int address;
        if(ins.index) {
          address=offset_addr;
          System.out.printf("address=r%d(0x%08x)%cr%d(0x%08x) %s %d=0x%08x\n",
                             ins.Rn,datapath.r[ins.Rn], 
                             ins.add?'+':'-',
                             ins.Rm,datapath.r[ins.Rm], 
                             ins.shift_t.toString(),ins.shift_n,
                             address);
        } else {
          address=datapath.r[ins.Rn];
          System.out.printf("address=r%d(0x%08x)\n",
                              ins.Rn,datapath.r[ins.Rn]);
        }
        datapath.r[ins.Rd]=datapath.readMem1(address) & 0xFF;
        System.out.printf(" r%d=0x%08x\n",ins.Rd,datapath.r[ins.Rd]);
      }
    }
  },
  UXTB {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        int rotated=ROR(datapath.r[ins.Rm],ins.imm);
        datapath.r[ins.Rd]=rotated & 0xFF;
        System.out.printf("r%d=(r%d(0x%08x) ROR %d)<7:0>=%02x\n",ins.Rd,ins.Rm,datapath.r[ins.Rm],ins.imm,datapath.r[ins.Rd]);
      }
    }
  },
  STRBreg {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        int offset=Shift(datapath.r[ins.Rm],ins.shift_t,ins.shift_n,datapath.APSR_C());
        int address=datapath.r[ins.Rn]+offset;
        System.out.printf("address=r%d(0x%08x)+r%d(0x%08x) %s %d=0x%08x\n",
                           ins.Rn,datapath.r[ins.Rn], 
                           ins.Rm,datapath.r[ins.Rm], 
                           ins.shift_t.toString(),ins.shift_n,
                           address);
        System.out.printf("Writing low byte of r%d(0x%08x)=0x%02x\n",ins.Rd,datapath.r[ins.Rd],datapath.r[ins.Rd] & 0xFF);
        datapath.writeMem1(address,datapath.r[ins.Rd] & 0xFF);
      }
    }
  },
  LSRimm {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        ResultWithCarry r=Shift_C(datapath.r[ins.Rm],SRType.LSR,ins.shift_n,datapath.APSR_C());
        System.out.printf("r%d=r%d(0x%08x) >>> %d=0x%08x", ins.Rd,ins.Rm,datapath.r[ins.Rm],ins.imm,r.result);
        datapath.r[ins.Rd]=r.result;
        if(datapath.shouldSetFlags(ins.setflags)) {
          datapath.APSR_setN(parseBit(r.result,31));
          datapath.APSR_setZ(r.result==0);
          datapath.APSR_setC(r.carry_out);
          System.out.printf(", N=%d", datapath.APSR_N()?1:0);
          System.out.printf(", Z=%d", datapath.APSR_Z()?1:0);
          System.out.printf(", C=%d", datapath.APSR_C()?1:0);
        }
        System.out.println();
      }
    }
  },
  LDRBreg {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        int offset=Shift(datapath.r[ins.Rm],ins.shift_t,ins.shift_n,datapath.APSR_C());
        int offset_addr=ins.add?(datapath.r[ins.Rn]+offset):(datapath.r[ins.Rn]-offset);
        int address;
        if(ins.index) {
          address=offset_addr;
          System.out.printf("address=r%d(0x%08x)%cr%d(0x%08x) %s %d=0x%08x\n",
                             ins.Rn,datapath.r[ins.Rn], 
                             ins.add?'+':'-',
                             ins.Rm,datapath.r[ins.Rm], 
                             ins.shift_t.toString(),ins.shift_n,
                             address);
        } else {
          address=datapath.r[ins.Rn];
          System.out.printf("address=r%d(0x%08x)\n",
                              ins.Rn,datapath.r[ins.Rn]);
        }
        datapath.r[ins.Rd]=datapath.readMem1(address) & 0xFF;
        System.out.printf(" r%d=0x%08x\n",ins.Rd,datapath.r[ins.Rd]);
      }
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
