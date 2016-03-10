package org.kwansystems.emulator.arm;

public interface DeviceRegister {
  public int read(Peripheral p);
  public void write(Peripheral p, int val);
  public int getOfs();
  public RegisterDirection getDir();
  public void reset(Peripheral p);
}
