package org.kwansystems.emulator.arm.peripherals;

import static org.kwansystems.emulator.arm.RegisterDirection.*;

import org.kwansystems.emulator.arm.DeviceRegister;
import org.kwansystems.emulator.arm.Emulator;
import org.kwansystems.emulator.arm.Peripheral;
import org.kwansystems.emulator.arm.RegisterDirection;

public class SystemControlBlock extends Peripheral {
  private Peripheral[] resetArray0;
  private static Peripheral[] currentResetArray0;
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
    PCONP      (RW,0x0C4,0x0408829E),
    PCONP1     (RW,0x0C8,0x8),
    PBOOST     (RW,0x1B0,0x3),
    EMCCLKSEL  (RW,0x100),
    CCLKSEL    (RW,0x104,1),
    USBCLKSEL  (RW,0x108),
    CLKSRCSEL  (RW,0x10C),
    PCLKSEL    (RW,0x1A8),
    SPIFICLKSEL(RW,0x1B4),
    EXTINT     (RW,0x140),
    EXTMODE    (RW,0x148),
    EXTPOLAR   (RW,0x14C),
    RSID       (RW,0x180),
    RSTCON     (RW,0x1CC) {
      @Override
      public void write(Peripheral p, int Lval) {
        int oldVal=read(p);
        int val=Lval;
        super.write(p, val);
        for(int i=0;i<32;i++) {
          if((oldVal & (1<<i))!=(val & (1<<i))) {
            boolean inReset=(val & (1<<i))!=0;
            if(inReset) {
              System.out.printf("Putting %s into reset\n", currentResetArray0[i].toString());
            } else {
              System.out.printf("Pulling %s out of reset\n", currentResetArray0[i].toString());
            }
            currentResetArray0[i].reset(inReset);
          }
        }
      }
    },
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
    SCBCRP     (RW,0x184), //Undocumented, seems to be related to CRP. Maybe a write-once register?
    SysCtl1e4  (RO,0x1e4,3), //Undocumented, seems to be related to EMC. Lower two bits describe width of EMC bus
    SysCtl3C0  (RW,0x3C0),
    SysCtl1fc  (WO,0x1fc);

    //Register boilerplate
    public int ofs;
    public int val, resetVal;
    public RegisterDirection dir;
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
  public SystemControlBlock(Peripheral[] LresetArray0) {
    super("SystemControlBlock",0x400FC000);
    resetArray0=LresetArray0;
    setupRegs(Registers.values());
  }
  @Override
  public void reset(boolean inReset) {
    reset(inReset,Registers.values());
  }
  @Override
  public int read(int rel_addr, int bytes) {
    currentResetArray0=resetArray0;
    return super.read(rel_addr, bytes);
  }
  @Override
  public void write(int rel_addr, int bytes, int value) {
    currentResetArray0=resetArray0;
    super.write(rel_addr, bytes, value);
  }
}
