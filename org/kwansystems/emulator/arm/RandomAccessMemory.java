package org.kwansystems.emulator.arm;

public class RandomAccessMemory extends ReadOnlyMemory {
  public RandomAccessMemory(int Lbase, int size) {
    super(Lbase,size);
  }
  @Override
  public void write(int rel_addr, int bytes, int value) {
    poke(rel_addr,bytes,value);
  }
  public final void write(int rel_addr, int value) {
    write(rel_addr,4,value);
  }
}
