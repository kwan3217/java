package org.kwansystems.emulator.arm.peripherals;

import static org.kwansystems.emulator.arm.RegisterDirection.*;

import org.kwansystems.emulator.arm.DeviceRegister;
import org.kwansystems.emulator.arm.Peripheral;
import org.kwansystems.emulator.arm.RegisterDirection;

public class LockBlock extends Peripheral {
  public enum Registers implements DeviceRegister {
    LOCK00 (RW,0x000), 
    LOCK10 (RW,0x010);
    public int ofs;
    public int val;
    public RegisterDirection dir;
    private Registers(RegisterDirection Ldir,int Lofs) {ofs=Lofs;dir=Ldir;}

    @Override
    public int read() {
      if(dir==WO) throw new RuntimeException("Reading from a write-only register "+toString());
      System.out.printf("Reading %s, value 0x%08x\n",toString(),val);
      return val;    
    }

    @Override
    public void write(int Lval) {
      if(dir==RO) throw new RuntimeException("Writing to a read-only register "+toString());
      System.out.printf("Writing %s, value 0x%08x\n",toString(),Lval);
      val=Lval;
    }

    @Override
    public int getOfs() {return ofs;}
    @Override
    public RegisterDirection getDir() {return dir;};
  }
  public LockBlock() {
    super("LockBlock",0x00200000,0x10000);
    setupRegs(Registers.values());
  }
}
