/**
 * 
 */
package org.kwansystems.space.spice.daf;

import java.io.*;
import static org.kwansystems.tools.Endian.*;

public class Header {
  public String Sig1,Sig2;
  public int nd,ni,nc;
  public String internalName;
  public int initialSummary, finalSummary, firstFree;
  public RandomAccessFile inf;
  public String binFormat;
  public boolean bigEndian;
  public short readShort() throws IOException {
    short result=inf.readShort();
    if(!bigEndian) result=swapEndian(result);
    return result;
  }
  public int readInt() throws IOException {
    int result=inf.readInt();
    if(!bigEndian) result=swapEndian(result);
    return result;
  }
  public long readLong() throws IOException {
    long result=inf.readLong();
    if(!bigEndian) result=swapEndian(result);
    return result;
  }
  public float readFloat() throws IOException {
    int i=readInt();
    return Float.intBitsToFloat(i);
  }
  public double readDouble() throws IOException {
    long l=readLong();
    return Double.longBitsToDouble(l);
  }
  public String readString(int length) throws IOException {
    byte[] result=new byte[length];
    inf.read(result);
    return new String(result);
  }
  public void printToStream(OutputStream ouf) {
    printToWriter(new PrintWriter(new OutputStreamWriter(ouf)));
  }
  @Override
  public String toString() {
    StringWriter result=new StringWriter();
    PrintWriter ouf=new PrintWriter(result,true);
    printToWriter(ouf);
    return result.toString();
  }
  public void printToWriter(PrintWriter ouf) {
    ouf.println("Sig1 (should be DAF/): "+Sig1);
    ouf.println("Sig2:                  "+Sig2);
    ouf.println("ND: "+nd);
    ouf.println("NI: "+ni);
    ouf.println("NC: "+nc);
    if(0<=nd && nd <=124) {
      ouf.println("ND constraint satisfied");
    } else {
      ouf.println("ND constraint NOT satisfied");
    }
    if(0<=nd && nd <=124) {
      ouf.println("NI constraint satisfied");
    } else {
      ouf.println("NI constraint NOT satisfied");
    }
    if(nd+(ni+1)/2 <=125) {
      ouf.println("ND and NI constraint satisfied");
    } else {
      ouf.println("ND and NI constraint NOT satisfied");
    }
    ouf.println("Internal Name: "+internalName);
    ouf.println("Initial summary record: "+initialSummary);
    ouf.println("Final summary record:   "+finalSummary);
    ouf.println("First free address:     "+firstFree);
    ouf.print("Binary File Format:     "+binFormat);
    if(bigEndian) {
      ouf.print(" (Big-endian integers)");
    } else {
      ouf.print(" (Little-endian integers)");
    }
  }
  public Header(RandomAccessFile Linf) throws IOException {
    inf=Linf;
    byte[] buf=new byte[4];
    inf.read(buf);
    Sig1=new String(buf);
    inf.read(buf);
    Sig2=new String(buf);
    nd=inf.readInt();
    ni=inf.readInt();
    buf=new byte[60];
    inf.read(buf);
    internalName=new String(buf);
    initialSummary=inf.readInt();
    finalSummary=inf.readInt();
    firstFree=inf.readInt();
    buf=new byte[8];
    inf.read(buf);
    binFormat=new String(buf);
    if(binFormat.substring(0,3).equals("BIG")) {
      bigEndian=true;
    } else {
      bigEndian=Math.abs(nd)<124;
    };
    if(!bigEndian) {
      nd=swapEndian(nd);
      ni=swapEndian(ni);
      initialSummary=swapEndian(initialSummary);
      finalSummary=swapEndian(finalSummary);
      firstFree=swapEndian(firstFree);
    }
    nc=8*(nd+(ni+1)/2);
  }
}