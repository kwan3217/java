package org.kwansystems.emulator.arm.peripherals;

import static org.kwansystems.emulator.arm.RegisterDirection.*;

import org.kwansystems.emulator.arm.DeviceRegister;
import org.kwansystems.emulator.arm.Peripheral;
import org.kwansystems.emulator.arm.RegisterDirection;

public class SystemControlBlock extends Peripheral {
  public enum Registers implements DeviceRegister {
    PLLCON0    (RW,0x080), 
    PLLCON1    (RW,0x0A0),
    PLLCFG0    (RW,0x084),
    PLLCFG1    (RW,0x0A4),
    PLLSTAT0   (RO,0x088),
    PLLSTAT1   (RO,0x088),
    PLLFEED0   (WO,0x08C),
    PLLFEED1   (WO,0x0AC),
    PCON       (RW,0x0C0),
    PCONP      (RW,0x0C4),
    PCONP1     (RW,0x0C8),
    PBOOST     (RW,0x1B0),
    EMCCLKSEL  (RW,0x100),
    CCLKSEL    (RW,0x104),
    USBCLKSEL  (RW,0x108),
    PCLKSEL    (RW,0x1A8),
    SPIFICLKSEL(RW,0x1B4),
    EXTINT     (RW,0x140),
    EXTMODE    (RW,0x148),
    EXTPOLAR   (RW,0x14C),
    RSID       (RW,0x180),
    RSTCON0    (RW,0x1CC),
    RSTCON1    (RW,0x1D0),
    EMCDLYCTL  (RW,0x1DC),
    EMCCAL     (RW,0x1E0),
    SCS        (RW,0x1A0),
    IRCCTRL    (RW,0x1A4),
    LCD_CFG    (RW,0x1B8),
    CANSLEEPCLR(RW,0x110),
    CANWAKEFLGS(RW,0x114),
    USBINTST   (RW,0x1C0),
    DMACREQSEL (RW,0x1C4),
    CLKOUTCFG  (RW,0x1C8),
    MEMMAP     (RW,0x040), //Documented in UM10562 39.8.1
    MATRIXARB  (RW,0x188), //Documented in UM10562  2.5.1
    SCBCRP     (RW,0x184),
    SysCtl3C0  (RW,0x3C0);
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
  public SystemControlBlock() {
    super("SystemControlBlock",0x400FC000);
    setupRegs(Registers.values());
  }
}
