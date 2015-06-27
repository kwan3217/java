package org.kwansystems.emulator.cips;

public abstract class UART {
  public abstract int get();
  public abstract void set(long data);
  public abstract boolean hasGet();
  public abstract boolean canPut();
}
