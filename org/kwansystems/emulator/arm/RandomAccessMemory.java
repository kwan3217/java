package org.kwansystems.emulator.arm;

public class RandomAccessMemory extends ReadOnlyMemory {
  public RandomAccessMemory(int Lbase, int size) {
    super(Lbase,size);
  }
  @Override
  public void write(int rel_addr, int bytes, int value) {
    for(int i=0;i<bytes;i++) {
      mem[rel_addr+i]=(byte)((value>>(8*i)) & 0xFF);
    }
  }
}
