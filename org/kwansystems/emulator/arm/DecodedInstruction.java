package org.kwansystems.emulator.arm;

public class DecodedInstruction {
  public enum SetFlags {TRUE,FALSE,IN_IT,NOT_IN_IT};
  Operation op;
  int opcode;
  int imm,lsbit,widthm1;
  int Rm,Rn,Rd,Ra;
  ConditionCode cond=ConditionCode.Thumb;
  SRType shift_t=SRType.NONE;
  int shift_n;
  boolean add,index,wback,UnalignedAllowed,nonzero,is_tbh;
  boolean thumbExpand;
  SetFlags setflags;
  int mask, firstcond; //used in IT instruction
  int pc; //pc of this instruction, used for reporting position of errors
  boolean is32;
  void execute(Datapath datapath) {op.execute(datapath, this);};
}
