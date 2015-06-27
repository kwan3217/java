package org.kwansystems.emulator.cips;

import java.io.*;


public class CIPSInstructionMemory extends Memory {
  protected long[] page=new long[1<<20];
  int currentAddr;
  public long[] getData() {return page;}
  public void loadBlock(long startAddr, long[] data) {
    for(int i=0;i<data.length;i++) {
      page[i+(int)startAddr]=data[i];
    }
  }
  public void loadBlock(long startAddr, int[] data) {
    for(int i=0;i<data.length;i++) {
      page[i+(int)startAddr]=(short)(data[i] & 0xFFFF);
    }
  }
  public void loadFile(long startAddr, long length, String Infn) throws IOException {
    loadFile(startAddr,length,new FileInputStream(Infn));
  }
  public void loadFile(long startAddr, long length, InputStream Inf) throws IOException {
    for(int i=0;length<0||i<length;i++) {
      //Inf.read returns a 32 bit between 0 and 255, so works ok unsigned
      int HiByte=Inf.read();
      if(HiByte<0) return;
      int LoByte=Inf.read();
      if(LoByte<0) return;
      long data=(HiByte<<8 | LoByte);
      page[i+(int)startAddr]=data;
    }
  }
  public String dump(int startAddr, int length) {
    StringBuffer result=new StringBuffer("");
    for(int i=0;i<length;i++) {
      result.append(String.format("%05X %04X\n",i+startAddr,page[i+startAddr]));
    }
    return result.toString();
  }
  public void address(long addr, int nBits) throws MemoryAccessNotSupportedException {
    if(nBits!=20) throw new MemoryAccessNotSupportedException("Can only address with 20 bits, not "+nBits+" bits");
    currentAddr=(int)(SignCondense(addr,20));
  }
  public long get(long nBits) throws MemoryAccessNotSupportedException {
    if(nBits!=16) throw new MemoryAccessNotSupportedException("Can only read by 16 bits, not "+nBits+" bits");
    return page[currentAddr];
  }
  public void set(long data, int nBits) throws MemoryAccessNotSupportedException {
    if(nBits!=16) throw new MemoryAccessNotSupportedException("Can only read by 16 bits, not "+nBits+" bits");
    page[currentAddr]=(SignCondense(data,16));
  };
  public void poke(long addr, short data) {
    page[(int)addr]=data;
  }
  public long peek(long addr) {
    return page[(int)addr];
  }
  public CIPSInstructionMemory(java.io.PrintStream Louf) {
  	super(Louf);
  }
  public CIPSInstructionMemory(java.io.PrintStream Louf, long startAddr, long[] Data) {
	  super(Louf);
	  loadBlock(startAddr,Data);
  }
}
