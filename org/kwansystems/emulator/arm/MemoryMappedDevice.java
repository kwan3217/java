package org.kwansystems.emulator.arm;

public interface MemoryMappedDevice {
  public int read(int rel_addr, int bytes);
  public void write(int rel_addr, int bytes, int value);
  public int getSize();
  public int getBase();
}
