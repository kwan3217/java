package org.kwansystems.emulator.arm;

public class BitFiddle {
  public static int parse(int val, int lobit, int len) {
    return ((val>>lobit) & ((1<<len)-1)); 
  }
  public static int signExtend(int val, int len) {
    if(( val & (1<<(len-1)) )!=0) {
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
  public static int writeField(int val, int pos, int len, int field) {
    return (val & ~((((1<<len)-1)<<pos))) |
           (field << pos);
  }
  public static int BitCount(int n) {
    int result=0;
    for(int i=0;i<32;i++) if(BitFiddle.parseBit(n,i)) result++;
    return result;
  }

}
