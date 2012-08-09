package org.kwansystems.emulator.cips;


/**
 * Implements a memory model emulating the CIPS Operand Memory. This memory consists of 16 pages, each of which contains 32768 16-bit
 * words. Page zero is always mapped to the lower half of a 16-bit address space, while one of the other pages (including possibly page 
 * zero again) are mapped to the upper half. In the real hardware, the processor FPGA contains a paging register which is used
 * to control the upper address lines and chip select lines of four 128KiW memory chips (three SRAM, one EEPROM). Here, the memory 
 * actually is implemented as a 2D array of 16 bit words, the first index of which is the page number, and the second is the 
 * 15 bit address on the page. 
 * 
 * Aside from paging, this memory appears to the outside world to be a 64KiW memory with 16 bit words. It must be addressed with
 * 16 bits and read or written 16 bits at a time.
 */
public class CIPSOperandMemory extends Memory {
  /**
   * Actual storage locations. Since this represents both the SRAM and EEPROM of the CIPS Processor Assembly, the EEPROM
   * part must be pre-loaded before running any cycles on the main CPA loop.
   */
  private short[][] page=new short[16][32768];
  /**
   * Current page register. This is actually inside the Processor FPGA in the real CPA, but we can consider this page
   * control logic to be part of the ProcFPGA, not the memory. To actually match the hardware, all accesses of this memory
   * would have to pass through the ProcFPGA. The lower 15 bits would be put directly on the operand address bus, while 
   * the upper bit would be used to either use the paging register or not, and thereby indirectly control the chip select lines
   * and upper address lines of the operand address bus. Only the ProcFPGA will call the getter and setter of this memory  
   */
  private int currentPage=0;
  /**
   * Currently addressed memory location  
   */
  private int currentAddr;
  /**
   * Create a new CIPSOperandMemory
   *  @param Louf
   */
  public CIPSOperandMemory(java.io.PrintStream Louf) {
    super(Louf);
  }
  /**
   * Get the current page number. ONLY ProcFPGA should call this.
   * @return current page number
   */
  public int getCurrentPage() {
    return currentPage;
  }
  /**
   * Set the current page. ONLY ProcFPGA should call this.
   * @param newPage new current page
   */
  public void setCurrentPage(int newPage) {
    currentPage=newPage;
  }
  /**
   * Address the memory. Future gets and sets will be to this address.
   * @param addr Address to use for access.
   * @param nBits Number of significant bits in address. Must be 16 for this memory.
   * @throws MemoryAccessNotSupportedException if the memory addressed by other than 16 bits
   * @see org.kwansystems.emulator.cips.Memory#address(long, int)
   */
  public void address(long addr, int nBits) throws MemoryAccessNotSupportedException {
    if(nBits!=16) throw new MemoryAccessNotSupportedException("Can only address with 16 bits, not "+nBits+", bad addr"+String.format("%0"+nBits/4+"X",addr));
    currentAddr=(int)(addr & ((1<<nBits)-1));
  }
  /**
   * Reads the value at the current address.
   * @param nBits Number of significant bits of data to get. Must be 16 for this memory.
   * @throws MemoryAccessNotSupportedException if the data size is other than 16 bits
   * @see org.kwansystems.emulator.cips.Memory#get(long)
   */
  public long get(long nBits) throws MemoryAccessNotSupportedException {
    if(nBits!=16) throw new MemoryAccessNotSupportedException("Can only read by 16 bits, not "+nBits+" bits");
    short addrUnsigned=(short)(currentAddr & 0x7fff);
    int thisPage=(currentAddr<0 || currentPage==0)?0:currentPage;
    short result=page[currentPage][addrUnsigned];
    trace(String.format("op[%1X][%04X]->%04X",thisPage,addrUnsigned,result));
    return result;
  }
  /**
   * Writes the value to the current address.
   * @param data Value to write. Bits above 16 will be discarded.
   * @param nBits Number of significant bits of data to get. Must be 16 for this memory.
   * @throws MemoryAccessNotSupportedException if the data size is other than 16 bits
   * @see org.kwansystems.emulator.cips.Memory#get(long)
   */
  public void set(long data, int nBits) throws MemoryAccessNotSupportedException {
    if(nBits!=16) throw new MemoryAccessNotSupportedException("Can only write by 16 bits, not "+nBits+" bits");
    data=(data & 0xFFFF);
    short addrUnsigned=(short)(currentAddr & 0x7fff);
    int thisPage=(currentAddr<0 || currentPage==0)?0:currentPage;
    page[thisPage][addrUnsigned]=(short)data;
    trace(String.format("%04X->op[%1X][%04X]",data,thisPage,addrUnsigned));
  };
}
