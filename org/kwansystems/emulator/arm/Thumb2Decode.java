package org.kwansystems.emulator.arm;

import static org.kwansystems.emulator.arm.BitFiddle.*;

/** The documentation for an ARM processor is quite complete, and appears to be sufficient to completely 
 * emulate the chip. It is documented in an extremely structured manner. The document is full of pseudocode,
 * which details exactly what the hardware is doing in an unambiguous manner.
 * 
 * Assembly opcodes are only loosely related to actual instructions. Sometimes the same mnemonic is used
 * in two completely different encodings, and sometimes the same encoding has two completely different mnemonic.
 * For instance, there is no difference between a mov Rd, Rm instruction and a lsl Rd, Rm, #0 instruction,
 * so in a sense the mov instruction doesn't even exist. Conversely, the ldr instruction encodes completely
 * different based on whether it is a register, immediate, or literal ldr. However in that case, ldr, while
 * documented as being a different encoding, is almost exactly the same as register, with the pc being the register
 * in question.
 * 
 * There are also two kinds of documentation for the encodings. One is near the front of the book, and starts with
 * a plot, showing bit positions, required bits, and fields, for groups of related instructions. These then point at
 * the second kind of documentation, which is a detailed list of each encoding of each type of instruction. So
 * for instance, LDR immediate might have a T1 and T2 encoding. Each encoding has a bitmask, which details what
 * bits need to be 1, what need to be 0, and what the other bits mean. Each encoding also has a routine called 
 * EncodingSpecificOperations(). The specified pseudocode method is to go through all the bitmasks, and identify
 * those that match the instruction to be decoded. Then, independently and in parallel, run all the encoding
 * specific operations. If there is more than one encoding that matches the bit mask, it is required that all but
 * one of them have an operation which says "Don't use this encoding", in which case the decoding of that bitmask
 * ends and has no further effect on the computation. Precisely one instruction is allowed to both match the bitmask
 * and have its encoding-specific operation complete. The other purpose of the encoding-specific operation is to 
 * set up all the fields that the actual execution will use. For instance, an instruction might have an ARM 32-bit
 * encoding which specifies some fields from the instruction data. Conversely, the same instruction might have a
 * THUMB 16-bit encoding where there isn't enough space for the field, and consequently it has a constant value.
 * The encoding-specific operations either set up the default values or pull them from the correct part of the
 * instruction data, so that the execution unit doesn't know or care which encoding was used.
 * 
 *  To model this, we will borrow a concept from the 8051 control store. That chip has a ROM built into it. When an
 *  instruction is decoded, it is checked against each row of the ROM. Each row has a field describing what bits
 *  in the instruction must be 1, what must be 0 (and also what phase of the instruction we are on, but we don't
 *  use the concept of phasing in ARM). Each row controls one of the control signals, so if that row's bits match
 *  (and the phase matches) then that control signal is asserted. We will have an enum, each of which will have
 *  associated with it a one mask and a zero mask. It will also have a bit of code which does the encoding-specific
 *  operations and returns true or false. The rows will be searched in order. The first one that matches will
 *  have its encoding-specific routine run, and if that routine returns true, then we are done, otherwise we
 *  keep looking at the next one that matches. The routine will also set the field values in the decoded instruction
 *  and if it passes, will set a field with the actual operation (another enum) which will be used to execute the 
 *  instruction.
 *  
 *  Using this method, we could in principle have one table for THUMB mode and a different one for ARM mode. The 
 *  current processor mode selects which table is used.
 */
public class Thumb2Decode extends Decode {
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
    return super.decode(IR, pc-(thumb32?4:2));
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
  public Thumb2Decode() {
    lines=Thumb2DecodeLine.values();
  }
}

