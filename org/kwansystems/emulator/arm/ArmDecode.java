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
  public static boolean parseBit(int val, int bit) {
    return parse(val,bit,1)==1;
  }
  public static int setBit(int val, int pos, boolean bit) {
    return (val&~(1<<pos)) | //Clear whatever value was there...
           ((bit?1:0)<<pos); //...and set the value coming in
  }
  public void decode(int IR) {};
}
