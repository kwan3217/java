package org.kwansystems.emulator.arm;

public class DecodedInstruction {
  ExecType encType;
  Operation op;
  int opcode;
  int imm;
  int Rm,Rn,Rd;
  int size, shift;
  ConditionCode cond;
  boolean B,L,SP,DN,S,U;
}
