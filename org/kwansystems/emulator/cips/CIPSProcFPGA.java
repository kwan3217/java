package org.kwansystems.emulator.cips;

import java.io.*;


public class CIPSProcFPGA extends Memory {
  int currentAddr;
  CIPSOperandMemory Op;
  public CIPSProcFPGA(PrintStream Louf, CIPSOperandMemory LOp) {
    super(Louf);
	Op=LOp;
  }
  public void address(long addr, int nBits) throws MemoryAccessNotSupportedException {
    if(nBits!=16) throw new MemoryAccessNotSupportedException("Can only address with 16 bits, not "+nBits+", bad addr"+String.format("%0"+nBits/4+"X",addr));
    currentAddr=(int)(addr & ((1<<nBits)-1));
  }
  public long get(long nBits) throws MemoryAccessNotSupportedException {
    if(nBits!=16) throw new MemoryAccessNotSupportedException("Can only read by 16 bits, not "+nBits+" bits");
    trace(String.format("io[%04X]->%04X",currentAddr,0));
    return 0;
  }
  public void set(long data, int nBits) throws MemoryAccessNotSupportedException {
    if(nBits!=16) throw new MemoryAccessNotSupportedException("Can only write by 16 bits, not "+nBits+" bits");
    trace(String.format("%04X->io[%04X]",data,currentAddr));
    switch((int)currentAddr) {
      case 0x0003:
    	trace(String.format("ProcFPGA: Page switch to page %1X",(short)data & 0x000F));
    	Op.setCurrentPage((short)data & 0x000F);
    }
  };
  public void step() {};
}
