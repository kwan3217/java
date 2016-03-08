package org.kwansystems.emulator.arm.peripherals;

import static org.kwansystems.emulator.arm.RegisterDirection.*;

import org.kwansystems.emulator.arm.Datapath;
import org.kwansystems.emulator.arm.DeviceRegister;
import org.kwansystems.emulator.arm.Peripheral;
import org.kwansystems.emulator.arm.RegisterDirection;

public class Timer extends Peripheral {
  //All this state being static is an ugly hack, forced on us by the fact that the elements
  //of the Registers enum only have access to the static fields of UART. We therefore set
  //the activePort static field each time we read or write. Timing will need access to the 
  //datapath cycle count
  public static int activePort;
  public static Datapath datapath;
  public int port;
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
    public int ofs;
    public int val, resetVal;
    public RegisterDirection dir;
    private Registers(RegisterDirection Ldir,int Lofs,int LresetVal) {ofs=Lofs;dir=Ldir;resetVal=LresetVal;}
    private Registers(RegisterDirection Ldir,int Lofs) {this(Ldir,Lofs,0);}
    @Override
    public void reset() {val=resetVal;}
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
  public Timer(int Lport, int base, Datapath Ldatapath) {
    super(String.format("Timer%d", Lport),base,0x4000);
    port=Lport;
    datapath=Ldatapath;
    setupRegs(Registers.values());
  }
  @Override
  public int read(int rel_addr, int bytes) {
    activePort=port;
    System.out.printf("Timer%d ",port);
    return super.read(rel_addr, bytes);
  }
  @Override
  public void write(int rel_addr, int bytes, int value) {
    activePort=port;
    System.out.printf("Timer%d ",port);
    super.write(rel_addr, bytes, value);
  }
  @Override
  public void reset(boolean inReset) {
    reset(inReset,Registers.values());
  }
}
