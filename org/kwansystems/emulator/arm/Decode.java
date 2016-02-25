package org.kwansystems.emulator.arm;

public class Decode {
  protected static class DecodeShiftReturn {
    public SRType shift_t;
    public int shift_n;
    public DecodeShiftReturn(SRType Lshift_t, int Lshift_n) {shift_t=Lshift_t;shift_n=Lshift_n;};
    public DecodeShiftReturn() {this(SRType.NONE,0);};
  }
  protected static DecodeShiftReturn DecodeImmShift(int type, int imm) {
    DecodeShiftReturn r=new DecodeShiftReturn();
    switch(type) {
      case 0:
        r.shift_t=SRType.LSL;
        r.shift_n=imm;
      case 1:
        r.shift_t=SRType.LSR;
        r.shift_n=(imm==0)?32:imm;
      case 2:
        r.shift_t=SRType.ASR;
        r.shift_n=(imm==0)?32:imm;
      default: //case 3
        if(imm==0) {
          r.shift_t=SRType.RRX;
          r.shift_n=1;
        } else {
          r.shift_t=SRType.ROR;
          r.shift_n=imm;
        }
    }
    return r;
  }
  protected static boolean BadReg(int n) {
    return n==13 || n==15; //Can't use sp or pc in lots of places
  }
}
