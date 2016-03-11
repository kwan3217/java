package org.kwansystems.emulator.arm.peripherals;

import static org.kwansystems.emulator.arm.RegisterDirection.*;

import org.kwansystems.emulator.arm.Datapath;
import org.kwansystems.emulator.arm.DeviceRegister;
import org.kwansystems.emulator.arm.Peripheral;
import org.kwansystems.emulator.arm.RegisterDirection;

public class Timer extends Peripheral {
  public final int port;
  public final Datapath datapath;
  public enum Registers implements DeviceRegister {
    TIR      (RW,0x000), // TODO - Lots of these will need to be functions of the datapath cycle count
    TTCR     (RW,0x004),
    TTC      (RW,0x008),
    TPR      (RW,0x00C),
    TPC      (RW,0x010),
    TMCR     (RW,0x014),
    TMR0     (RW,0x018),
    TMR1     (RW,0x01C),
    TMR2     (RW,0x020),
    TMR3     (RW,0x024),
    TCCR     (RW,0x028),
    TCR0     (RO,0x02C),
    TCR1     (RO,0x030),
    TEMR     (RW,0x03C),
    TCTCR    (RW,0x070);

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
      int val=p.read(ofs);
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
  public Timer(int Lport, int base, Datapath Ldatapath) {
    super(String.format("Timer%d", Lport),base,0x4000);
    port=Lport;
    datapath=Ldatapath;
    setupRegs(Registers.values());
  }
  @Override
  public void reset(boolean inReset) {
    reset(inReset,Registers.values());
  }
}
