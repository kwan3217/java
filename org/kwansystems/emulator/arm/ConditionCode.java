package org.kwansystems.emulator.arm;

import static org.kwansystems.emulator.arm.BitFiddle.*;

public enum ConditionCode {
  EQ {@Override public boolean shouldExecute(boolean Z, boolean C, boolean N, boolean V) {return Z;};},
  NE {@Override public boolean shouldExecute(boolean Z, boolean C, boolean N, boolean V) {return !Z;};},
  CS {@Override public boolean shouldExecute(boolean Z, boolean C, boolean N, boolean V) {return C;};},
  CC {@Override public boolean shouldExecute(boolean Z, boolean C, boolean N, boolean V) {return !C;};},
  MI {@Override public boolean shouldExecute(boolean Z, boolean C, boolean N, boolean V) {return N;};},
  PL {@Override public boolean shouldExecute(boolean Z, boolean C, boolean N, boolean V) {return !N;};},
  VS {@Override public boolean shouldExecute(boolean Z, boolean C, boolean N, boolean V) {return V;};},
  VC {@Override public boolean shouldExecute(boolean Z, boolean C, boolean N, boolean V) {return !V;};},
  HI {@Override public boolean shouldExecute(boolean Z, boolean C, boolean N, boolean V) {return C&(!Z);};},
  LS {@Override public boolean shouldExecute(boolean Z, boolean C, boolean N, boolean V) {return (!C)|Z;};},
  GE {@Override public boolean shouldExecute(boolean Z, boolean C, boolean N, boolean V) {return N==V;};},
  LT {@Override public boolean shouldExecute(boolean Z, boolean C, boolean N, boolean V) {return N!=V;};},
  GT {@Override public boolean shouldExecute(boolean Z, boolean C, boolean N, boolean V) {return (!Z)&(N==V);};},
  LE {@Override public boolean shouldExecute(boolean Z, boolean C, boolean N, boolean V) {return (Z)|(N!=V);};},
  AL {@Override public boolean shouldExecute(boolean Z, boolean C, boolean N, boolean V) {return true;};},
  UNDEF,
  Thumb; //In this case, the instruction doesn't specify a condition code, so use the ambient IT block. This is the most common case for Thumb instructions.
  public static final int ZPos=30;
  public static final int CPos=29;
  public static final int NPos=31;
  public static final int VPos=28;
  public boolean shouldExecute(boolean Z, boolean C, boolean N, boolean V) {return false;};
  public boolean shouldExecute(int PSR) {
    boolean result=shouldExecute(parseBit(PSR,ZPos),parseBit(PSR,CPos),parseBit(PSR,NPos),parseBit(PSR,VPos));
    System.out.printf("Checking condition %s with Z=%d, C=%d, N=%d, V=%d, result is should%s execute\n",toString(), parse(PSR,ZPos,1),parse(PSR,CPos,1),parse(PSR,NPos,1),parse(PSR,VPos,1),result?"":" not");
    return result;
  };
  public static final ConditionCode[] enumValues=ConditionCode.values();
}