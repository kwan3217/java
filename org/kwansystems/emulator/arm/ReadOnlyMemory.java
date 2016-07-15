package org.kwansystems.emulator.arm;

import java.io.FileInputStream;
import java.io.IOException;

public class ReadOnlyMemory implements MemoryMappedDevice {
  private byte mem[];
  protected int base;
  protected final String name;
  public String getName() {return name;}
  public final void dump(int start, int len) {
    for(int i=0;i<len;i+=16) {
      System.out.printf("%08x:", base+start+i);
      for(int j=0;j<4;j++) {
        System.out.printf(" %08x",peek(start+i+j*4,4));
      }
      System.out.println();
    }
  }
  public final void dump() {
    dump(0,mem.length);
  }
  public ReadOnlyMemory(String Lname, int Lbase, int size) {
    base=Lbase;
    mem=new byte[size];
    name=Lname;
  }
  public ReadOnlyMemory(String Lname, int Lbase, int size, String image) throws IOException {
    this(Lname,Lbase,size);
    loadBin(image);
  }
  @Override
  public int read(int rel_addr, int bytes) {
    int result=peek(rel_addr,bytes);
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
  public void loadBin(String infn) throws IOException {
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
  public final void poke(int rel_addr, int bytes, int val) {
    for(int i=0;i<bytes;i++) {
      mem[rel_addr]=(byte)((val >> (i*8)) & 0xFF);
      rel_addr++;
    }
  }
  public final void poke(int rel_addr, int val) {
    poke(rel_addr,4,val);
  }
  /** Peeks at a value in memory. This is intended to be lower-level IE less 
   * debugging/side effects */
  public final int peek(int rel_addr, int bytes) {
    int result=0;
    for(int i=bytes-1;i>=0;i--) {
      result=(result<<8) | ((int)(mem[rel_addr+i]) & 0xff);
    }
    return result;
  }
  public final int peek(int rel_addr) {
    return peek(rel_addr,4);
  }
  protected int pclk;
  @Override
  public void tick(int Lpclk) {pclk=Lpclk;}
  @Override
  public int compareTo(MemoryMappedDevice that) {
    return base-that.getBase();
  }
}
