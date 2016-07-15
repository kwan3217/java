package org.kwansystems.emulator.arm;

import static org.kwansystems.emulator.arm.BitFiddle.*;
import static org.kwansystems.emulator.arm.Datapath.*;

import org.kwansystems.emulator.arm.Datapath.AddWithCarryReturn;

import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;

public enum FloatingPointOperation implements Operation {
  VLDR {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        DatapathF datapathF=((DatapathF)datapath);
        datapathF.ExecuteFPCheck();
        int base=/*(ins.Rn==15)?datapath.Align(datapath.r[15],4):*/datapathF.r[ins.Rn];
        int address=ins.add?base+ins.imm:base-ins.imm;
        System.out.printf("Loading from r%d(0x%08x)%c%d=0x%08x ",ins.Rn,base,ins.add?'+':'-',ins.imm,address);
        if(ins.single_reg) {
          datapathF.s[ins.Rd]=datapath.readMem4(address);
          System.out.printf("s%d=0x%08x=%f\n",ins.Rd,datapathF.s[ins.Rd],datapathF.S(ins.Rd));
        } else {
          throw new RuntimeException("Not Supported - loading of double-precision registers");
        }
      } 
    }
  },
  VADD {
    @Override public void execute(Datapath datapath, DecodedInstruction ins) {
      if(datapath.ConditionPassed(ins.cond)) {
        DatapathF datapathF=((DatapathF)datapath);
        datapathF.ExecuteFPCheck();
        boolean dp_operation=!ins.single_reg;
        if(dp_operation) {
          throw new RuntimeException("Not Supported - loading of double-precision registers");
        } else {
          float n=datapathF.S(ins.Rn);
          float m=datapathF.S(ins.Rm);
          float result=n+m;
          System.out.printf("s%d=s%d(%f)+s%d(%f)=%f\n",ins.Rd,ins.Rn,n,ins.Rm,m,n+m);
          datapathF.setS(ins.Rd,result);
        }
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
}
