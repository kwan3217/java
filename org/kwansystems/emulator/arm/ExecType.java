package org.kwansystems.emulator.arm;

import static org.kwansystems.emulator.arm.ConditionCode.AL;
import static org.kwansystems.emulator.arm.Thumb2Decode.N;
import static org.kwansystems.emulator.arm.ArmDecode.*;
import static org.kwansystems.emulator.arm.Operation.*;

public enum ExecType {          //opcode imm Rm Rn Rd cond B  L  SP DN
  shiftByImmediateMoveRegister   (11,     6,  3, N, 0, N,   N, N, N, N,
                                   2,     5,  3, N, 3, N) {
    private Operation[] opMap=new Operation[] {LSLimm,LSRimm,ASRimm,UNDEF};
    @Override public void finishDecode(DecodedInstruction decode, int IRlo, int IRhi) {
      super.finishDecode(decode,IRlo,IRhi);
      decode.op=opMap[decode.opcode];
    }
  },
                                 //opcode imm Rm Rn Rd cond B  L  SP DN
  addSubtractRegister            ( 9,     N,  6, 3, 0, N,   N, N, N, N,
                                   1,     N,  3, 3, 3, N),
                                 //opcode imm Rm Rn Rd cond B  L  SP DN
  addSubtractImmediate           ( 9,     6,  N, 3, 0, N,   N, N, N, N,
                                   1,     3,  N, 3, 3, N),
  addSubtractCompareMoveImmediate(11,     0,  N, 8, 8, N,   N, N, N, N,  //Yes, we deliberately have Rn and Rd overlap
                                   2,     8,  N, 3, 3, N), 
  dataProcessingRegister         ( 6,     N,  3, 0, 0, N,   N, N, N, N,
                                   4,     N,  3, 3, 3, N),
  specialDataProcessing          ( 8,     N,  3, 0, 0, N,   N, N, N, N, 
                                   2,     N,  4, 3, 3, N) {
    @Override public void finishDecode(DecodedInstruction decode, int IRlo, int IRhi) {
      super.finishDecode(decode,IRlo,IRhi);
      if(decode.opcode==0b11) {
        decode.encType=branchExchangeInstructionSet;
        decode.encType.finishDecode(decode, IRlo, IRhi);
      }
    }  
  },
  branchExchangeInstructionSet   ( N,     N,  3, N, N, N,   N, 7, N, N,
                                   N,     N,  4, N, N, N),
  loadFromLiteralPool            ( N,     0,  N, N, 8, N,   N, N, N, N,
                                   N,     8,  N, N, 3, N),
  
  loadStoreRegisterOffset,
  
                                 //opcode imm Rm Rn Rd cond B  L  SP DN
  loadStoreWordByteImmediateOffset(N,     6,  N, 3, 0, N,   12,11, N, N,
                                   N,     5,  N, 3, 3, N) {
    @Override public void finishDecode(DecodedInstruction decode, int IRlo, int IRhi) {
      super.finishDecode(decode,IRlo,IRhi);
      decode.op=decode.L?LDRimm:STRimm;
    }
  },
  
  loadStoreHalfwordImmediateOffset,
  
  loadFromOrStoreToStack,
  
  addToSPorPC,
  
  miscellaneous,
  
  loadStoreMultiple,
  
  conditionalBranch {
    @Override public void finishDecode(DecodedInstruction decode, int IRlo, int IRhi) {
      super.finishDecode(decode,IRlo,IRhi);
      if(decode.cond==ConditionCode.enumValues[0b1110]) {
        decode.encType=undefinedInstruction;
        decode.encType.finishDecode(decode, IRlo, IRhi);
      }
      if(decode.cond==ConditionCode.enumValues[0b1111]) {
        decode.encType=serviceSystemCall;
        decode.encType.finishDecode(decode, IRlo, IRhi);
      }
    }
  },  
  
  undefinedInstruction,
  
  serviceSystemCall,
  
  unconditionalBranch,
  
  thirtyTwoBitInstruction1{
    @Override public void finishDecode(DecodedInstruction decode, int hw1, int hw2) {
      //Don't need to run super, since nothing there will carry over
      if(parse(hw1,9,3)==0b101) {
        decode.encType=dataProcessingNoImmediate;
      } else if(parse(hw1,9,3)==0b100) {
        if(parse(hw1,6,1)==1) {
          decode.encType=loadStoreDoubleExclusiveTableBranch;
        } else {
          decode.encType=loadStoreMultipleRFESRS;
        }
      } else if(parse(hw1,8,4)==0b1111) {
        decode.encType=coprocessor;
      }
      decode.encType.finishDecode(decode, hw1, hw2);
    }    
  },
  thirtyTwoBitInstruction2 {
    @Override public void finishDecode(DecodedInstruction decode, int hw1, int hw2) {
      //Don't need to run super, since nothing there will carry over
      if(parse(hw1,11,1)==0b0) {
        if(parse(hw2,15,1)==0b0) {
          decode.encType=dataProcessingImmediateBitfieldSaturate;
        } else {
          decode.encType=branchesMiscellaneousControl;
        }
      } else if(parse(hw1,9,3)==0b101) {
        decode.encType=dataProcessingNoImmediate;
      } else if(parse(hw1,9,3)==0b100) {
        decode.encType=loadStoreSingleMemoryHint;
      } else if(parse(hw1,8,4)==0b1111) {
        decode.encType=coprocessor;
      }
      decode.encType.finishDecode(decode, hw1, hw2);
    }    
  },
  dataProcessingImmediateBitfieldSaturate,
  dataProcessingNoImmediate,
  loadStoreSingleMemoryHint {
    @Override public void finishDecode(DecodedInstruction decode, int hw1, int hw2) {
      //Don't need to run super, since nothing there will carry over
      decode.S=parseBit(hw1,8);
      decode.L=parseBit(hw1,4);
      decode.Rn=parse(hw1,0,4);
      decode.Rd=parse(hw2,12,4);
      decode.size=parse(hw1,5,2);
      if(decode.L && decode.Rn==0b1111) {
        //PC+-imm12
        decode.op=LDR1;
        decode.U=parseBit(hw1,7);
        decode.imm=parse(hw2,0,12);
      } else if(parse(hw1,7,1)==0b1) {
        //Rn+imm12
        decode.op=LDR2;
        decode.imm=parse(hw2,0,12);
      } else {
        if(parse(hw2,8,4)==0b1100) {
          //Rn-imm8
          decode.op=LDR3;
          decode.imm=parse(hw2,0,8);
        } else if(parse(hw2,8,4)==0b1110) {
          //Rn+imm8, user privilege
          decode.op=LDR4;
          decode.imm=parse(hw2,0,8);
        } else if(parse(hw2,10,2)==0b10) {
          if(parse(hw2,8,1)==0b1) {
            //Rn post-indexed by +-imm8
            decode.op=LDR5;
            decode.imm=parse(hw2,0,8);
          } else {
            throw new RuntimeException("Reserved");
          }
        } else if(parse(hw2,10,2)==0b11) {
          decode.op=LDR6;
          decode.imm=parse(hw2,0,8);
        } else if(parse(hw2,6,6)==0b000000) {
          decode.op=LDR7;
          decode.Rm=parse(hw2,0,3);
          decode.shift=parse(hw2,3,2);
        } else {
          throw new RuntimeException("Reserved");
        }
      }
    }
  },
  loadStoreDoubleExclusiveTableBranch,
  loadStoreMultipleRFESRS,
  branchesMiscellaneousControl,
  coprocessor;
  public void finishDecode(DecodedInstruction decode, int IRlo, int IRhi) {
    decode.opcode=-1;
    decode.imm=-1;
    decode.Rm=-1;
    decode.Rn=-1;
    decode.Rd=-1;
    decode.B=false;
    decode.L=false;
    decode.SP=false;
    decode.DN=false;
    decode.S=false;
    decode.U=true; //Carries over to add in LDR, on by default
    decode.cond=AL;
    decode.size=-1;
    decode.shift=-1;
    if(bopcode>=0) decode.opcode=                         ArmDecode.parse          (IRlo, bopcode, lopcode);
    if(bimm   >=0) decode.imm   =                         ArmDecode.parseSignExtend(IRlo, bimm   , limm   );
    if(bRm    >=0) decode.Rm    =                         ArmDecode.parse          (IRlo, bRm    , lRm    );
    if(bRn    >=0) decode.Rn    =                         ArmDecode.parse          (IRlo, bRn    , lRn    );
    if(bRd    >=0) decode.Rd    =                         ArmDecode.parse          (IRlo, bRd    , lRd    );
    if(bcond  >=0) decode.cond  =ConditionCode.enumValues[ArmDecode.parse          (IRlo, bcond  , lcond  )];
    if(bB     >=0) decode.B     =                         ArmDecode.parseBit       (IRlo, bB     );
    if(bL     >=0) decode.L     =                         ArmDecode.parseBit       (IRlo, bL     );
    if(bSP    >=0) decode.SP    =                         ArmDecode.parseBit       (IRlo, bSP    );
    if(bDN    >=0) decode.DN    =                         ArmDecode.parseBit       (IRlo, bDN    );
  };
  private int bopcode,bimm,bRm,bRn,bRd,bcond,bB,bL,bSP,bDN;
  private int lopcode,limm,lRm,lRn,lRd,lcond;
  ExecType() {this(0,0,0,0,0,0,0,0,0,0,
                   0,0,0,0,0,0); }
  ExecType(int Lbopcode, int Lbimm, int LbRm, int LbRn, int LbRd, int Lbcond, int LbB, int LbL, int LbSP, int LbDN,
           int Llopcode, int Llimm, int LlRm, int LlRn, int LlRd, int Llcond) {
    bopcode=Lbopcode;
    bimm   =Lbimm;
    bRm    =LbRm;
    bRn    =LbRn;
    bRd    =LbRd;
    bcond  =Lbcond;
    bB     =LbB;
    bL     =LbL;
    bSP    =LbSP;
    bDN    =LbDN;

    lopcode=Llopcode;
    limm   =Llimm;
    lRm    =LlRm;
    lRn    =LlRn;
    lRd    =LlRd;
    lcond  =Llcond;
    
  }
}
