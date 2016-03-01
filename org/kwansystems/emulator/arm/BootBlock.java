package org.kwansystems.emulator.arm;

public class BootBlock extends RandomAccessMemory {
  public BootBlock() {
    super(0x00100000,0x10000);
  }
  @Override
  public int read(int rel_addr, int bytes) {
    int result;
    switch(rel_addr) {
      case 0x5d0:  
        // must be 0x3456abcd or device will stick in infinite loop in bootloader
        result=0x3456abcd;
        System.out.printf("Returning crp4 key 0x%08x\n",result);
        return result;
      case 0x43c:
        // Used as an initial stack pointer
        result=0x10001fff; //Use the hard-coded value until we have a good reason not to
        System.out.printf("Returning second stack pointer 0x%08x\n",result);
        return result;
    }
    return super.read(rel_addr, bytes); 
  }
}
