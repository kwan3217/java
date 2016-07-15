package org.kwansystems.emulator.arm;

import static org.kwansystems.emulator.arm.BitFiddle.*;

public class ThumbFloatingPointDecode extends Decode {
  public static final int N=-1;
  public void flushPipeline() {
    hw1Thumb=0;
    hw1ThumbValid=false;
  }
  private int hw1Thumb;
  private boolean hw1ThumbValid=false;

  public DecodedInstruction decode(int IR, int pc) {
    boolean thumb32=false;
    if(hw1ThumbValid) {
      System.out.printf("Stacking up Thumb32 instruction from %04x and %04x\n",hw1Thumb,IR);
      IR=hw1Thumb | IR<<16;
      hw1ThumbValid=false;
      thumb32=true;
    } else {
      //special case: is this the first halfword of a thumb32?
      if(parse(IR,11,5)==0b11101 || parse(IR,12,4)==0b1111) {
        hw1Thumb=IR;
        hw1ThumbValid=true;
        System.out.printf("Instruction %04x is first half of Thumb32 instruction\n",IR);
        return null;
      }
    }
    DecodedInstruction result=super.decode(IR, pc-(thumb32?4:2));
    result.is32=thumb32;
    return result;
  }
  /** Only for human consumption, allows entry of instruction data in the same form that 
   * assembly listings display it
   * @param IRlo Low halfword of instruction, always printed on the left
   * @param IRhi High halfword, always printed on the right.
   * @return
   */
  public DecodedInstruction decode(int IRlo, int IRhi, int pc) {
    hw1ThumbValid=false;
    return decode(IRlo | IRhi<<16,pc);
  }
}

