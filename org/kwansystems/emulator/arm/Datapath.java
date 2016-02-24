package org.kwansystems.emulator.arm;

import static org.kwansystems.emulator.arm.ConditionCode.*;
import static org.kwansystems.emulator.arm.ArmDecode.*;

public class Datapath {
  public int r[]=new int[16];
  public int APSR;
  void loadMem(String infn, int ofs) {
	int seg=ArmDecode.parse(ofs, 16, 16);
  }
  public boolean APSR_C() {return parseBit(APSR,CPos);};
  public void APSR_setC(boolean C) {APSR=setBit(APSR,CPos,C);}
  public void APSR_setC(int C) {APSR_setC(C!=0);};
  public boolean APSR_N() {return parseBit(APSR,NPos);};
  public void APSR_setN(boolean N) {APSR=setBit(APSR,NPos,N);}
  public void APSR_setN(int N) {APSR_setN(N!=0);};
  public boolean APSR_V() {return parseBit(APSR,VPos);};
  public void APSR_setV(boolean V) {APSR=setBit(APSR,VPos,V);}
  public void APSR_setV(int V) {APSR_setV(V!=0);};
  public boolean APSR_Z() {return parseBit(APSR,ZPos);};
  public void APSR_setZ(boolean Z) {APSR=setBit(APSR,ZPos,Z);}
  public void APSR_setZ(int Z) {APSR_setZ(Z!=0);};
  public boolean inIT=false;
  public int readMem4(int address) { return 0; };
  public void LoadWritePC(int value) {
    //A.6.43 LoadWritePC()
    //This procedure writes a value to the PC with the correct semantics for such writes by load instructions. That
    //is, with BX-like interworking behavior in ARMv5 and above, and just a change to the PC in ARMv4T.
    r[15]=value;
  }
}
