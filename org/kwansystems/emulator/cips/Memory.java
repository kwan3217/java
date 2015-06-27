package org.kwansystems.emulator.cips;

/**
 * General purpose flat memory model. It has an up to 64 bit address bus and an up to 64 bit data bus. Since this is an abbstract class,
 * it is up to the subclasses to actually arrange for the appropriate amount of storage.
 * <p>
 * The memory access model runs in two steps. First, the memory is addressed, then the value at that address can be read or written.
 * The same memory cell can be accessed any number of times
 *
 */
public abstract class Memory {
  /**
   * Stream for printing out trace messages
   */
  private java.io.PrintStream ouf;
  /**
   * @param Louf Stream to print trace messages on. Set to null to not generate traces.
   */
  public Memory(java.io.PrintStream Louf) {
    ouf=Louf;
  }
  /** 
   * Address the memory. Future gets and sets will be to this address.
   * @param addr Address to use for access
   * @param nBits Number of significant bits in address. Bits above this should be zero.
   * @throws MemoryAccessNotSupportedException if the memory is not accessible with the combination of address and bits given
   */
  public abstract void address(long addr, int nBits) throws MemoryAccessNotSupportedException;
  public abstract long get(long nBits) throws MemoryAccessNotSupportedException;
  public abstract void set(long data, int nBits) throws MemoryAccessNotSupportedException;
  public static long SignExtend(long Data, int nBits) {
    if(Data>(1<<(nBits-1))) Data=Data-(1<<nBits);
    return Data;
  }
  public static long SignCondense(long Data, int nBits) {
    return Data & ((1<<nBits)-1);
  }
  protected void trace(String s) {
	if(ouf!=null) ouf.println(s);
  }
}
