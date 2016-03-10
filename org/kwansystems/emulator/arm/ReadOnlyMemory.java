package org.kwansystems.emulator.arm;

import java.io.FileInputStream;
import java.io.IOException;

public class ReadOnlyMemory implements MemoryMappedDevice {
  private byte mem[];
  protected int base;
  public ReadOnlyMemory(int Lbase, int size) {
    base=Lbase;
    mem=new byte[size];
  }
  public ReadOnlyMemory(int Lbase, int size, String image) throws IOException {
    this(Lbase,size);
    loadBin(image);
  }
  @Override
  public int read(int rel_addr, int bytes) {
    int result=0;
    for(int i=bytes-1;i>=0;i--) {
      result=(result<<8) | ((int)(mem[rel_addr+i]) & 0xff);
    }
    System.out.printf("readMem(%08x)=%0"+String.format("%d", bytes*2)+"x\n",rel_addr+base,result);
    return result; 
  };
  public final int read(int rel_addr) {
    return read(rel_addr,4);
  }
  @Override
  public void write(int rel_addr, int bytes, int value) {
    throw new IllegalArgumentException("Writing to ROM");
  }
  @Override
  public int getSize() {
    return mem.length;
  }
  @Override
  public int getBase() {
    return base;
  }
  private void loadBin(String infn) throws IOException {
    int address=0;;
    FileInputStream inf=new FileInputStream(infn);
    int b=inf.read();
    while(b>=0) {
      mem[address]=(byte)b;
      address++;
      b=inf.read();
    }
    inf.close();
  }
  /** Pokes a value into the memory, despite the fact that it is read-only. This is used by 
   * RAM as the actual write mechanism, and is intended to be lower-level IE less 
   * debugging/side effects */
  public void poke(int address, int bytes, int val) {
    for(int i=0;i<bytes;i++) {
      mem[address]=(byte)((val >> (i*8)) & 0xFF);
      address++;
    }
  }
  public void poke(int address, int val) {
    poke(address,4,val);
  }
}
