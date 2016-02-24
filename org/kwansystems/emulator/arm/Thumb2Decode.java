package org.kwansystems.emulator.arm;

import static org.kwansystems.emulator.arm.ExecType.*;

public class Thumb2Decode extends ArmDecode {
  public static final int N=-1;
  private ExecType[] exectypeMap=new ExecType[] {
/*0b0000xx*/ shiftByImmediateMoveRegister,    shiftByImmediateMoveRegister,    shiftByImmediateMoveRegister,    shiftByImmediateMoveRegister,
/*0b0001xx*/ shiftByImmediateMoveRegister,    shiftByImmediateMoveRegister,    addSubtractRegister,             addSubtractImmediate,
/*0b001xxx*/ addSubtractCompareMoveImmediate, addSubtractCompareMoveImmediate, addSubtractCompareMoveImmediate, addSubtractCompareMoveImmediate,  
/*0b001xxx*/ addSubtractCompareMoveImmediate, addSubtractCompareMoveImmediate, addSubtractCompareMoveImmediate, addSubtractCompareMoveImmediate,  
/*0b0100xx*/ dataProcessingRegister,          specialDataProcessing/*BLX*/,    loadFromLiteralPool,             loadFromLiteralPool,
/*0b0101xx*/ loadStoreRegisterOffset,         loadStoreRegisterOffset,         loadStoreRegisterOffset,         loadStoreRegisterOffset,
/*0b011xxx*/ loadStoreWordByteImmediateOffset,loadStoreWordByteImmediateOffset,loadStoreWordByteImmediateOffset,loadStoreWordByteImmediateOffset,
/*0b011xxx*/ loadStoreWordByteImmediateOffset,loadStoreWordByteImmediateOffset,loadStoreWordByteImmediateOffset,loadStoreWordByteImmediateOffset,
/*0b1000xx*/ loadStoreHalfwordImmediateOffset,loadStoreHalfwordImmediateOffset,loadStoreHalfwordImmediateOffset,loadStoreHalfwordImmediateOffset,
/*0b1001xx*/ loadFromOrStoreToStack,          loadFromOrStoreToStack,          loadFromOrStoreToStack,          loadFromOrStoreToStack,                    
/*0b1010xx*/ addToSPorPC,                     addToSPorPC,                     addToSPorPC,                     addToSPorPC,                     
/*0b1011xx*/ miscellaneous,                   miscellaneous,                   miscellaneous,                   miscellaneous,                   
/*0b1100xx*/ loadStoreMultiple,               loadStoreMultiple,               loadStoreMultiple,               loadStoreMultiple,               
/*0b1101xx*/ conditionalBranch/*UND/SVC*/,    conditionalBranch,               conditionalBranch,               conditionalBranch,
/*0b1110xx*/ unconditionalBranch,             unconditionalBranch,             thirtyTwoBitInstruction1,        thirtyTwoBitInstruction1,
/*0b1111xx*/ thirtyTwoBitInstruction2,        thirtyTwoBitInstruction2,        thirtyTwoBitInstruction2,        thirtyTwoBitInstruction2        
  };
  public DecodedInstruction decode(int IRlo, int IRhi) {
    DecodedInstruction result=new DecodedInstruction();
    int typeField=parse(IRlo,10,6);
    result.encType=exectypeMap[typeField];
    result.encType.finishDecode(result, IRlo, IRhi);
    return result;
  }
  public static void main(String[] args) {
    Thumb2Decode decode=new Thumb2Decode();
    Datapath datapath=new Datapath();
    DecodedInstruction ins=decode.decode(0xf8df,0x4018); //ldr.w r4,[pc,#24]
    ins.op.execute(datapath, ins);
  }
}

