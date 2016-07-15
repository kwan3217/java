package org.kwansystems.emulator.arm;

import static org.kwansystems.emulator.arm.BitFiddle.*;

public class DatapathF extends Datapath {
  /** FPCA, Floating Point Coprocessor Active. If this bit is set, then the coprocessor has changed state and automatic state preservation will need to save the state */
  public static final int FPCA=2;
  public int s[]=new int[32]; //Integer version of floating-point registers
  public float S(int index) {
    return Float.intBitsToFloat(s[index]);
  }
  public void setS(int index,float val) {
    s[index]=Float.floatToIntBits(val);
  }
  //We don't implement the double-precision stuff here. Extend this class if you want it.
  
  //Reset values for these registers are set in SystemControlSpace peripheral
  public int FPCCR;
  public static final int LSPACT=0; 
  public static final int USER=1;
  public static final int THREAD=3;
  public static final int HFRDY=4;
  public static final int MMRDY=5;
  public static final int BFRDY=6;
  public static final int MONRDY=8;
  public static final int LSPEN=30;
  public static final int ASPEN=31;
  public int FPCAR;
  public int FPSCR; //Not memory mapped
  public int FPDSCR;
  public int MVFR0;
  public int MVFR1;
  public int MVFR2;
  public void ExecuteFPCheck() {
    CheckVFPEnabled();
    if(parseBit(FPCCR,LSPACT)) {
      PreserveFPState();
    }
    if(parseBit(FPCCR,ASPEN) && !parseBit(CONTROL,FPCA)) {
      FPSCR=BitFiddle.writeField(FPSCR, 22, 5, BitFiddle.parse(FPDSCR, 22, 5));        
      CONTROL=BitFiddle.setBit(CONTROL, FPCA, true);
   }
  }
  
  public void CheckVFPEnabled() {
    if(parse(CPACR,10*2,2)!=parse(CPACR,11*2,2)) throw new Unpredictable("Coprocessor 10 and 11 settings are different");
    switch(parse(CPACR,10*2,2)) {
      case 0b00:
        CFSR=setBit(CFSR,NOCP,true);
        throw new RuntimeException("Usage Fault: FPU disabled");
      case 0b01:
        if(!CurrentModeIsPrivileged()) {
          CFSR=setBit(CFSR,NOCP,true);
          throw new RuntimeException("Usage Fault: FPU Privileged and current thread is not");
        }
        return; //allow the access
      case 0b10:
        throw new Unpredictable("Invalid CP10 mode");
      default:
        return; //allow the access
    }
  }
  private enum Acctype {NORMAL,UNPRIV};
  public void PreserveFPState() {
    //Acctype acctype=parseBit(FPCCR,USER)?Acctype.UNPRIV:Acctype.NORMAL;
    // TODO - implement this routine
  }
}
