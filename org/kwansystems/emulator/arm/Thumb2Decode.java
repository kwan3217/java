package org.kwansystems.emulator.arm;

public class Thumb2Decode extends ArmDecode {
  public void decode(int IR) {
	opcode=-1;
	imm=-1;
	Rm=-1;
	Rn=-1;
	Rd=-1;
	B=false;
	L=false;
	SP=false;
	DN=false;
	short IRlo=(short)(IR & 0xFFFF);
	short IRhi=(short)((IR >> 16) & 0xFFFF);
	           //bitpos of low bit
	                      //length of field
	if(parse(IRlo,13,3) == 0b000) {
	  encType=0;
	  opcode=parse(IRlo,11,2);
	  if(opcode!=0b11) {
		//Really is encoding type 0, parse the immediate and registers
		imm=parseSignExtend(IRlo,6,5);
		Rm=parse(IRlo,3,3);
		Rd=parse(IRlo,0,3);
	  } else {
		opcode=parse(IRlo,9,1);
		if(parse(IRlo,10,1)==0b0) {
		  //really encoding 1
		  encType=1;
          Rm=parse(IRlo,6,3);
		} else {
		  encType=2;
          // 2 - Add/subtract immediate
          imm=parseSignExtend(IRlo,6,3);
		}
		Rn=parse(IRlo,3,3);
		Rd=parse(IRlo,0,3);
	  }
	} else if(parse(IRlo,13,3) == 0b001) {
	  encType=3;
      // 3 - Add/subtract/compare/move immediate
      opcode=parse(IRlo,11,2);
	  Rd=parse(IRlo,8,3);
	  Rn=Rd;
	  imm=parseSignExtend(IRlo,0,8);
	} else if(parse(IRlo,10,6) == 0b010000) {
	  encType=4;
      // 4 - Data-processing register
      opcode=parse(IRlo,6,4);
	  Rm=((IRlo>>3) & ((1<<3)-1));
	  Rd=((IRlo>>0) & ((1<<3)-1));
	  Rn=Rd;
	} else if(parse(IRlo,10,6) == 0b010001) {
	  encType=5;
      // 5 - Special data processing
      opcode=parse(IRlo,6,4);
      if(opcode!=0b11) {
    	DN=parse(IRlo,7,1)==1;
    	Rd=((IRlo>>0) & ((1<<3)-1));
    	Rn=Rd;
      } else {
    	encType=6;
        // 6 - Branch/exchange instruction set
      }
	  Rm=((IRlo>>3) & ((1<<3)-1));
	} else if(parse(IRlo,11,5) == 0b01001) {
	  encType=7;
      // 7 - Load from literal pool
      opcode=parse(IRlo,6,4);
	  Rd=parse(IRlo,8,3);
	  imm=parseSignExtend(IRlo,0,8);
	} else if(parse(IRlo,12,4) == 0b0101) {
	  encType=8;
      // 8 - Load/store register offset
      opcode=parse(IRlo,9,3);
	  Rm=parse(IRlo,6,3);
	  Rn=parse(IRlo,3,3);
	  Rd=parse(IRlo,0,3);
	} else if(parse(IRlo,13,3) == 0b011) {
	  encType=9;
      // 9 - Load/store word/byte immediate offset
	  B=parse(IRlo,12,1)==1;
	  L=parse(IRlo,11,1)==1;
      imm=parseSignExtend(IRlo,9,3);
	  Rm=parse(IRlo,6,3);
	  Rn=parse(IRlo,3,3);
	  Rd=parse(IRlo,0,3);
	} else if(parse(IRlo,12,4) == 0b1000) {
      encType=10; // Load/store halfword immediate offset
	  L=parse(IRlo,11,1)==1;
      imm=parseSignExtend(IRlo,9,3);
	  Rm=parse(IRlo,6,3);
	  Rn=parse(IRlo,3,3);
	  Rd=parse(IRlo,0,3);
	} else if(parse(IRlo,12,4) == 0b1001) {
      encType=11; // Load from or store to stack
	  Rd=parse(IRlo,8,3);
	  L=parse(IRlo,11,1)==1;
      imm=parseSignExtend(IRlo,0,8);
	} else if(parse(IRlo,12,4) == 0b1001) {
      encType=12; // Add to SP or PC
	  Rd=parse(IRlo,8,3);
	  SP=parse(IRlo,11,1)==1;
      imm=parseSignExtend(IRlo,0,8);
	} else if(parse(IRlo,12,4) == 0b1001) {
  	  encType=13; // Miscellaneous
  	  // TODO
	} else if(parse(IRlo,12,4) == 0b1001) {
	  encType=12; // Add to SP or PC
	  Rd=parse(IRlo,8,3);
	  SP=parse(IRlo,11,1)==1;
	  imm=parseSignExtend(IRlo,0,8);
	}
  }
}

encType=14; // Load/store multiple
encType=15; // Conditional branch
encType=16; // Undefined instruction
encType=17; // Service (system) call
encType=18; // Unconditional branch
encType=19; // 32-bit instruction 1
encType=20; // 32-bit instruction 2
