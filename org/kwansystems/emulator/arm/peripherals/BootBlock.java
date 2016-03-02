package org.kwansystems.emulator.arm.peripherals;

import static org.kwansystems.emulator.arm.RegisterDirection.*;

import org.kwansystems.emulator.arm.DeviceRegister;
import org.kwansystems.emulator.arm.Peripheral;
import org.kwansystems.emulator.arm.RegisterDirection;
import org.kwansystems.emulator.arm.peripherals.SystemControlBlock.Registers;

public class BootBlock extends Peripheral {
  public enum Registers implements DeviceRegister {
    CRP4    (RO,0x5d0) {public int read() {val=0x3456abcd;return super.read();}},
    STACK2  (RO,0x43c) {public int read() {val=0x10001fff;return super.read();}},
    Boot5E8 (RO,0x5e8) {public int read() {val=0xffffffff;return super.read();}};
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
  public BootBlock() {
    super("Boot Block",0x00100000,0x10000);
    setupRegs(Registers.values());
  }
}
