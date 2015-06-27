
package org.kwansystems;

import java.io.*;

/**Input stream which supports reading blocks of bits (1-32 bits in length)
 */
public class BitInputStream extends FilterInputStream {
  enum Endian {Small,Big}
  public static long reverseBits(long data, int bits) {
    if(bits>64) throw new IllegalArgumentException("Too many bits!");
    long result=0;
    for(int i=0;i<bits;i++) {
      int bit=(int)(data & 1);
      data=data>>1;
      result=result << 1 | bit;
    }
    return result;
  }
  private byte[] buf=new byte[0];
  private int bufsize=1024;
  private int bufptr=0;
  public BitInputStream(InputStream Lin) {
    super(Lin);
  }
  private void makeBufFull() {
    if(bufptr>=buf.length) {
      buf=new byte[bufsize];

    }
  }
  /** Read the next 8 bits of the input stream
   *
   * @return
   * @throws java.io.IOException
   */
  @Override
  public int read() throws IOException {
    makeBufFull();
  }
  public static void main(String[] args) {
    System.out.println(reverseBits(20,5));
  }
}
