package org.kwansystems.emulator.arm.peripherals;

import static org.kwansystems.emulator.arm.RegisterDirection.*;

import org.kwansystems.emulator.arm.Datapath;
import org.kwansystems.emulator.arm.DeviceRegister;
import org.kwansystems.emulator.arm.Peripheral;
import org.kwansystems.emulator.arm.RegisterDirection;
import org.kwansystems.emulator.arm.peripherals.LockBlock.Registers;

public class UART extends Peripheral {
  //All this state being static is an ugly hack, forced on us by the fact that the elements
  //of the Registers enum only have access to the static fields of UART. We therefore set
  //the activePort static field each time we read or write.
  //We only have to do this for registers shadowed by the DLAB, since everything else gets written
  //to the backing store by default. Also, we only have to keep one side of the shadow, since we
  //can let the backing store handle the other side. Finally, RBR() and THR() are going to end up
  //complicated. However, that means that they are backed by functions, and we can use the backing store
  //for UDLL. The RBR() and THR() functions will need access to the cycle counter at least. 
  private boolean DLAB() {
    return (read(Registers.ULCR.getOfs()) & 1<<7)!=0;
  }
  private int DLM;
  private int RBR() {
    //TODO - return the right character at the right time, based
    //       on the cycle counter.
    int val=0;
    System.out.printf("Reading URBR, value 0x%08x\n",val);
    return val;
  }
  private void THR(int val) {
    //TODO - do something with the value that is written
    System.out.printf("Writing UTHR, value 0x%08x\n",val);
  }
  private final int port;
  public enum Registers implements DeviceRegister {
    UDLL     (RW,0x000) { //This also implements URBR (on read)/UTHR (on write)
      @Override
      public int read(Peripheral uart) {
        if(((UART)uart).DLAB()) {
          return super.read(uart);
        } else {
          return ((UART)uart).RBR();
        }
      }
      @Override
      public void write(Peripheral uart, int val) {
        if(((UART)uart).DLAB()) {
          super.write(uart,val);
        } else {
          ((UART)uart).THR(val);
        }
      }
    }, 
    UIER     (RW,0x004) { //This also implements UDLM
      @Override
      public int read(Peripheral uart) {
        if(((UART)uart).DLAB()) {
          System.out.printf("Reading UDLM, value 0x%08x\n",((UART)uart).DLM);
          return ((UART)uart).DLM;
        } else {
          return super.read(uart);
        }
      }
      @Override
      public void write(Peripheral uart, int val) {
        if(((UART)uart).DLAB()) {
          System.out.printf("Writing UDLM, value 0x%08x\n",val);
          ((UART)uart).DLM=val;
        } else {
          super.write(uart,val);
        }
      }
    }, //UDLM(r/w)   when DLAB=1

    UIIR     (RO,0x008),
    UFCR     (WO,0x008),
    ULCR     (RW,0x00C),
    UMCR     (RW,0x010),
    ULSR     (RO,0x014),
    UMSR     (RO,0x018),
    USCR     (RW,0x01C),
    UACR     (RW,0x020),
    UFDR     (RW,0x028),
    UTER     (RW,0x030);
    //Register boilerplate
    public final int ofs;
    public final int resetVal;
    public final RegisterDirection dir;
    private Registers(RegisterDirection Ldir,int Lofs,int LresetVal) {ofs=Lofs;dir=Ldir;resetVal=LresetVal;}
    private Registers(RegisterDirection Ldir,int Lofs) {this(Ldir,Lofs,0);}
    @Override
    public void reset(Peripheral uart) {uart.write(ofs,resetVal);}
    @Override
    public int read(Peripheral uart) {
      if(dir==WO) throw new RuntimeException("Reading from a write-only register "+toString());
      int val=uart.read(ofs, 4);
      System.out.printf("Reading %s, value 0x%08x\n",toString(),val);
      return val;    
    }
    @Override
    public void write(Peripheral uart, int Lval) {
      if(dir==RO) throw new RuntimeException("Writing to a read-only register "+toString());
      System.out.printf("Writing %s, value 0x%08x\n",toString(),Lval);
      uart.write(ofs, Lval);
    }
    @Override
    public int getOfs() {return ofs;}
    @Override
    public RegisterDirection getDir() {return dir;};
  }
  public UART(int Lport, int base) {
    super(String.format("UART%d", Lport),base,0x4000);
    port=Lport;
    setupRegs(Registers.values());
  }
  @Override
  public int read(int rel_addr, int bytes) {
    System.out.printf("UART%d ",port);
    return super.read(rel_addr, bytes);
  }
  @Override
  public void write(int rel_addr, int bytes, int value) {
    System.out.printf("UART%d ",port);
    super.write(rel_addr, bytes, value);
  }
  @Override
  public void reset(boolean inReset) {
    reset(inReset,Registers.values());
  }
}
