package org.kwansystems.emulator.arm.peripherals;

import static org.kwansystems.emulator.arm.RegisterDirection.*;

import org.kwansystems.emulator.arm.*;

public class SystemControlSpace extends Peripheral {
  public enum Registers implements DeviceRegister {
    VTOR    (RW,0xD08,0x1FFF0000), 
    CFSR    (RW,0xD28,0x00000000) {
      @Override public void write(Peripheral p, int val) {super.write(p,val); ((CortexM)(p.getDatapath())).CFSR  =val;}
      @Override public int read(Peripheral p)                        {int val=((CortexM)(p.getDatapath())).CFSR  ;p.poke(ofs,val);return val;}
      @Override public void reset(Peripheral p) {super.reset(p);              ((CortexM)(p.getDatapath())).CFSR  =resetVal;} 
    },
    CPACR   (RW,0xD88)           {
      @Override public void write(Peripheral p, int val) {super.write(p,val); ((CortexM)(p.getDatapath())).CPACR =val;}
      @Override public int read(Peripheral p)                        {int val=((CortexM)(p.getDatapath())).CPACR ;p.poke(ofs,val);return val;}
      @Override public void reset(Peripheral p) {super.reset(p);              ((CortexM)(p.getDatapath())).CPACR =resetVal;} 
    },
    FPCCR   (RW,0xF34,0xC000000) {
      @Override public void write(Peripheral p, int val) {super.write(p,val); ((CortexM)(p.getDatapath())).FPCCR =val;}
      @Override public int read(Peripheral p)                        {int val=((CortexM)(p.getDatapath())).FPCCR ;p.poke(ofs,val);return val;}
      @Override public void reset(Peripheral p) {super.reset(p);              ((CortexM)(p.getDatapath())).FPCCR =resetVal;} 
    },
    FPCAR   (RW,0xF38)           {
      @Override public void write(Peripheral p, int val) {super.write(p,val); ((CortexM)(p.getDatapath())).FPCAR =val;}
      @Override public int read(Peripheral p)                        {int val=((CortexM)(p.getDatapath())).FPCAR ;p.poke(ofs,val);return val;}
      @Override public void reset(Peripheral p) {super.reset(p);              ((CortexM)(p.getDatapath())).FPCAR =resetVal;} 
    },
    FPDSCR  (RW,0xF3C,0x0000000) {
      @Override public void write(Peripheral p, int val) {super.write(p,val); ((CortexM)(p.getDatapath())).FPDSCR=val;}
      @Override public int read(Peripheral p)                        {int val=((CortexM)(p.getDatapath())).FPDSCR;p.poke(ofs,val);return val;}
      @Override public void reset(Peripheral p) {super.reset(p);              ((CortexM)(p.getDatapath())).FPDSCR=resetVal;} 
    },
    MVFR0   (RO,0xF40,0x10110021) {
      @Override public int read(Peripheral p)                        {int val=((CortexM)(p.getDatapath())).MVFR0;p.poke(ofs,val);return val;}
      @Override public void reset(Peripheral p) {super.reset(p);              ((CortexM)(p.getDatapath())).MVFR0=resetVal;} 
    },
    MVFR1   (RO,0xF44,0x11000011)  {
      @Override public int read(Peripheral p)                        {int val=((CortexM)(p.getDatapath())).MVFR1;p.poke(ofs,val);return val;}
      @Override public void reset(Peripheral p) {super.reset(p);              ((CortexM)(p.getDatapath())).MVFR1=resetVal;} 
    },
    MVFR2   (RO,0xF48,0x00000000) {
      @Override public int read(Peripheral p)                        {int val=((CortexM)(p.getDatapath())).MVFR2;p.poke(ofs,val);return val;}
      @Override public void reset(Peripheral p) {super.reset(p);              ((CortexM)(p.getDatapath())).MVFR2=resetVal;} 
    },
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
  public SystemControlSpace(Datapath Ldatapath) {
    super(Ldatapath,"SystemControlSpace",0xE000E000,0x1000);
    setupRegs(Registers.values());
  }
  @Override
  public void reset(boolean inReset) {
    reset(inReset,Registers.values());
  }
}
