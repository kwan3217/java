package org.kwansystems.emulator.arm;

public class SystemControlBlock extends RandomAccessMemory {
  public SystemControlBlock() {
    super(0x400FC000,0x4000);
  }
  @Override
  public int read(int rel_addr, int bytes) {
    int result=super.read(rel_addr, bytes); 
    System.out.printf("Reading sysctl[0x%03x], value=0x%08x\n", rel_addr,result);
    return result;
  }
}
