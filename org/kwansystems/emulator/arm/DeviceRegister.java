package org.kwansystems.emulator.arm;

public interface DeviceRegister {
  public int read();
  public void write(int val);
  public int getOfs();
  public RegisterDirection getDir();
  public void reset();
}
