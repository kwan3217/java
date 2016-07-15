package org.kwansystems.emulator.arm.peripherals;

import static org.kwansystems.emulator.arm.RegisterDirection.*;

import org.kwansystems.emulator.arm.Datapath;
import org.kwansystems.emulator.arm.DeviceRegister;
import org.kwansystems.emulator.arm.Peripheral;
import org.kwansystems.emulator.arm.RegisterDirection;

public class Timer extends Peripheral {
  public final int port;
  public void tick(int Lpclk) {
    super.tick(Lpclk);
    int TTCR=peek(Registers.TCR.ofs);
    if((TTCR & (1<<1))>0) {
      //If the timer is in reset...
      poke(Registers.PR.ofs,0);
      poke(Registers.TC.ofs,0);
      
    }
    if((TTCR & (1<<0))>0) {
      //If the timer is enabled...
      int TPR=peek(Registers.PR.ofs);
      int TTC=peek(Registers.TC.ofs);
      int TPC=peek(Registers.PC.ofs);
      if(TPR==TPC) {
        TPC=0;
        TTC++;
      } else {
        TPC++;
      }
      poke(Registers.PC.ofs,TPC);
      //TODO - check match and capture registers
      //Write back TTC, since matching might reset the timer
      poke(Registers.TC.ofs,TTC);
    }
  }
  public enum Registers implements DeviceRegister {
    IR      (RW,0x000), // TODO - Lots of these will need to be functions of the datapath cycle count
    TCR     (RW,0x004),
    TC      (RW,0x008),
    PR      (RW,0x00C),
    PC      (RW,0x010),
    MCR     (RW,0x014),
    MR0     (RW,0x018),
    MR1     (RW,0x01C),
    MR2     (RW,0x020),
    MR3     (RW,0x024),
    CCR     (RW,0x028),
    CR0     (RO,0x02C),
    CR1     (RO,0x030),
    EMR     (RW,0x03C),
    CTCR    (RW,0x070);

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
  public Timer(Datapath Ldatapath, int Lport, int base) {
    super(Ldatapath, String.format("Timer%d", Lport),base,0x80);
    port=Lport;
    setupRegs(Registers.values());
  }
  @Override
  public void reset(boolean inReset) {
    reset(inReset,Registers.values());
  }
}
