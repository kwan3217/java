package org.kwansystems.emulator.arm.peripherals;

import static org.kwansystems.emulator.arm.RegisterDirection.*;

import org.kwansystems.emulator.arm.DeviceRegister;
import org.kwansystems.emulator.arm.Peripheral;
import org.kwansystems.emulator.arm.RegisterDirection;

public class Watchdog extends Peripheral {
  public enum Registers implements DeviceRegister {
    WDMOD    (RW,0x000),
    WDTC     (RW,0x004),
    WDFEED   (WO,0x008),
    WDTV     (WO,0x00c),
    WDWARNINT(RW,0x014),
    WDWINDOW (RW,0x018);
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
  public Watchdog() {
    super("Watchdog",0x40000000,0x4000);
    setupRegs(Registers.values());
  }
}
