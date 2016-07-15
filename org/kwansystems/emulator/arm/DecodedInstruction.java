package org.kwansystems.emulator.arm;

public class DecodedInstruction {
  public enum SetFlags {TRUE,FALSE,IN_IT,NOT_IN_IT};
  public Operation op;
  public int opcode;
  public int imm,lsbit,widthm1;
  public int Rm,Rn;
  public int Rd;
  public int Ra;
  public ConditionCode cond=ConditionCode.Thumb;
  public SRType shift_t=SRType.NONE;
  public int shift_n;
  public boolean add,index,wback,UnalignedAllowed,nonzero,is_tbh;
  public boolean thumbExpand;
  public SetFlags setflags;
  public int mask, firstcond; //used in IT instruction
  public int pc; //pc of this instruction, used for reporting position of errors
  public boolean is32;
  public boolean single_reg;
  public void execute(Datapath datapath) {op.execute(datapath, this);};
}
