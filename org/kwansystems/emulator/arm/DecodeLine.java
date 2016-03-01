package org.kwansystems.emulator.arm;

public interface DecodeLine {
  public boolean decode(int IR, DecodedInstruction ins);
  public int oneBits();
  public int zeroBits();
  public Operation op();
  public static final String delimiters="|/:'";
  /** 
   * @param bitpattern Encoding of 1 bit, 0 bit, and don't care bits. For each bit position, the
   *        one bit is set if the string is 1 in the corresponding character slot, the zero bit 
   *        if 0, and neither if any other character is specified (don't care). The traditional
   *        symbol for don't care is X, but it seems like _ or - might be more like the documentation.
   *        An encoding string is either exactly 16 or 32 characters long. For 16, the string starts
   *        with the most-significant bit (bit 15) at the left and proceeds to bit 0 at the right. For
   *        32, the first 16 are the least significant half-word, and the second 16 are the most.
   */
  public static int[] interpretBitPattern(String bitpattern) {
    int oneBits=0;
    int zeroBits=0;
    int j=0;
    for(int i=0;i<16;i++) {
      while(delimiters.indexOf(bitpattern.charAt(j))>=0) j++; //Use slash as field delimiter"
          
      if(bitpattern.charAt(j)=='1') {
        oneBits |=(1<<(15-i));
      }
      if(bitpattern.charAt(j)=='0') {
        zeroBits|=(1<<(15-i));
      }
      j++;
      //Any other character leaves both bits cleared, exactly as desired for don't care
    }
    if(bitpattern.length()>j) {
      for(int i=16;i<32;i++) {
        while((delimiters.indexOf(bitpattern.charAt(j))>=0)) j++;
        if(bitpattern.charAt(j)=='1') {
          oneBits |=(1<<(47-i));
        }
        if(bitpattern.charAt(j)=='0') {
          zeroBits|=(1<<(47-i));
        }
        j++;
      }
      while(j<bitpattern.length() && (delimiters.indexOf(bitpattern.charAt(j))>=0)) j++;
      if(j!=bitpattern.length()) throw new IllegalArgumentException("Bit Pattern "+bitpattern+" is the wrong size");
    }
    return new int[] {zeroBits,oneBits};
  }
}
