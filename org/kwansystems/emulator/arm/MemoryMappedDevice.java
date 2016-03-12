package org.kwansystems.emulator.arm;

public interface MemoryMappedDevice {
  public int read(int rel_addr, int bytes);
  public void write(int rel_addr, int bytes, int value);
  public int getSize();
  public int getBase();
  /** This routine should be called each time the PCLK value increments, which should be once
   * per cycle unless something weird is done with the PCLK/CCLK ratio. Peripherals don't have
   * to do anything with this, but things like Timer might, since it makes the logic easier.
   * @param pclk elapsed number of PCLK cycles
   */
  public void tick(int pclk); 
}
