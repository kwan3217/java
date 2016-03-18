package org.kwansystems.emulator.arm.peripherals;

import static org.kwansystems.emulator.arm.RegisterDirection.*;

import org.kwansystems.emulator.arm.DeviceRegister;
import org.kwansystems.emulator.arm.Peripheral;
import org.kwansystems.emulator.arm.RegisterDirection;
import org.kwansystems.emulator.arm.peripherals.SystemControlBlock.Registers;

public class BootBlock extends Peripheral {
  public enum Registers implements DeviceRegister {
    CRP4           (RO,0x5d0,0x3456abcd), //Code will lock up if this is not this value
    ISPSTACK       (RO,0x43c,0x10001fff),
    BOOT_MATRIXARB (RO,0x5b4),
    BOOT_ADTRM     (RO,0x5d8),
    BOOT_PCONP     (RO,0x5b0,0x0408829E),
    BOOT_IRCCTRL   (RO,0x5bc),
    BOOT5CC        (RO,0x5cc),             //Pointer to a vector table
    BOOT_SYSCTL1FC (RO,0x5e0),
    BOOT_PBOOST    (RO,0x5e4),
    BOOT5E8        (RO,0x5e8,0xFFFFFFFF), //used as a pointer in reset2_cont, but not used if value is 0xFFFFFFFF
    BOOT_EMCDLYCTL (RO,0x5f0),
    BOOT_DEVICEID  (RO,0x430,0x47193F47),
    BOOT_CODEVER   (RO,0x434,0x0102)      //Sets this to be code version 1.2. Curious that this is in the boot block and not the code itself.
    ;
    //Register boilerplate
    public final int ofs;
    public final int resetVal;
    public final RegisterDirection dir;
    private Registers(RegisterDirection Ldir,int Lofs,int LresetVal) {ofs=Lofs;dir=Ldir;resetVal=LresetVal;}
    private Registers(RegisterDirection Ldir,int Lofs) {this(Ldir,Lofs,0);}
    @Override
    public void reset(Peripheral p) {p.poke(ofs, resetVal);}
    @Override
    public int read(Peripheral p) {
      if(dir==WO) throw new RuntimeException("Reading from a write-only register "+toString());
      int val=p.peek(ofs);
      System.out.printf("Reading %s, value 0x%08x\n",toString(),val);
      return val;    
    }
    @Override
    public void write(Peripheral p, int val) {
      if(dir==RO) throw new RuntimeException("Writing to a read-only register "+toString());
      System.out.printf("Writing %s, value 0x%08x\n",toString(),val);
      p.poke(ofs,val);
    }
    @Override
    public int getOfs() {return ofs;}
    @Override
    public RegisterDirection getDir() {return dir;};
  }
  public BootBlock() {
    super("Boot Block",0x00100000,0x10000);
    setupRegs(Registers.values());
  }
  @Override
  public void reset(boolean inReset) {
    reset(inReset,Registers.values());
  }
}
