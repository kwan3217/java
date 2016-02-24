package org.kwansystems.emulator.arm;

public class DecodedInstruction {
  Operation op;
  int opcode;
  int imm;
  int Rm,Rn,Rd;
  ConditionCode cond=ConditionCode.AL;
  SRType shift_t=SRType.NONE;
  int shift_n;
  boolean add,index,wback,setflags;
  int mask, firstcond; //used in IT instruction
  int pc; //pc of this instruction, used for reporting position of errors
  void execute(Datapath datapath) {op.execute(datapath, this);};
}
