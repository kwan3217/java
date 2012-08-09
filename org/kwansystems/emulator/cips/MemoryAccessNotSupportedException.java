package org.kwansystems.emulator.cips;

public class MemoryAccessNotSupportedException extends IllegalArgumentException {
  private static final long serialVersionUID=-6592287158211838658L;
  public MemoryAccessNotSupportedException(String message, long addr, long data) {
    super(message+" Addr: "+addr+" Data: "+data);
  }
  public MemoryAccessNotSupportedException(String message, long addr) {
    super(message+" Addr: "+addr);
  }
  public MemoryAccessNotSupportedException(String message) {
    super(message);
  }
}
