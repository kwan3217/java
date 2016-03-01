package org.kwansystems.emulator.arm;

public class LockBlock extends RandomAccessMemory {
  public LockBlock() {
    super(0x00200000,0x10000);
  }
}
