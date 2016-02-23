package org.kwansystems.emulator.arm;

public class ArmDecode {
  public static int parse(int val, int lobit, int len) {
	return ((val>>lobit) & ((1<<len)-1)); 
  }
  public static int signExtend(int val, int len) {
	if(( val & (1<<(len-1)) )==1) {
	  val=0xFFFFFFFF & ~((1<<len)-1) | val;
	}
	return val;
  }
  public static int parseSignExtend(int val, int lobit, int len) {
	return signExtend(parse(val,lobit,len),len);
  }
  int encType; // 0 - Shift by immediate, move register
               // 1 - Add/subtract register
  // 2 - Add/subtract immediate
  // 3 - Add/subtract/compare/move immediate
  // 4 - Data-processing register
  // 5 - Special data processing
  // 6 - Branch/exchange instruction set
  // 7 - Load from literal pool
  // 8 - Load/store register offset
  // 9 - Load/store word/byte immediate offset
  //10 - Load/store halfword immediate offset
  //11 - Load from or store to stack
  //12 - Add to SP or PC
  //13 - Miscellaneous
  //14 - Load/store multiple
  //15 - Conditional branch
  //16 - Undefined instruction
  //17 - Service (system) call
  //18 - Unconditional branch
  //19 - 32-bit instruction 1
  //20 - 32-bit instruction 2
  int opcode;
  int imm;
  int Rm,Rn,Rd;
  boolean B,L,SP,DN;
}
