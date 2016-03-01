package org.kwansystems.emulator.arm;

import java.io.FileInputStream;
import java.io.IOException;

public class ReadOnlyMemory implements MemoryMappedDevice {
  byte mem[];
  int base;
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
}
