package org.kwansystems.emulator.arm.peripherals;

import static org.kwansystems.emulator.arm.RegisterDirection.*;

import org.kwansystems.emulator.arm.Datapath;
import org.kwansystems.emulator.arm.DeviceRegister;
import org.kwansystems.emulator.arm.Peripheral;
import org.kwansystems.emulator.arm.RegisterDirection;

public class GPIO extends Peripheral {
  public enum Registers implements DeviceRegister {
    DIR0     (RW,0x000),
    MASK0    (RW,0x010),
    PIN0     (RW,0x014),
    SET0     (RW,0x018),
    CLR0     (WO,0x01C),
    DIR1     (RW,0x020),
    MASK1    (RW,0x030),
    PIN1     (RW,0x034),
    SET1     (RW,0x038),
    CLR1     (WO,0x03C),
    DIR2     (RW,0x040),
    MASK2    (RW,0x050),
    PIN2     (RW,0x054),
    SET2     (RW,0x058),
    CLR2     (WO,0x05C),
    DIR3     (RW,0x060),
    MASK3    (RW,0x070),
    PIN3     (RW,0x074),
    SET3     (RW,0x078),
    CLR3     (WO,0x07C),
    DIR4     (RW,0x080),
    MASK4    (RW,0x090),
    PIN4     (RW,0x094),
    SET4     (RW,0x098),
    CLR4     (WO,0x09C),
    DIR5     (RW,0x0A0),
    MASK5    (RW,0x0B0),
    PIN5     (RW,0x0B4),
    SET5     (RW,0x0B8),
    CLR5     (WO,0x0BC);
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
  private Datapath datapath;
  public GPIO(Datapath Ldatapath) {
    super("GPIO",0x20098000,0x4000);
    datapath=Ldatapath;
    setupRegs(Registers.values());
  }
}
