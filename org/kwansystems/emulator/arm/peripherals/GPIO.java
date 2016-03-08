package org.kwansystems.emulator.arm.peripherals;

import static org.kwansystems.emulator.arm.RegisterDirection.*;

import org.kwansystems.emulator.arm.Datapath;
import org.kwansystems.emulator.arm.DeviceRegister;
import org.kwansystems.emulator.arm.Peripheral;
import org.kwansystems.emulator.arm.RegisterDirection;

public class GPIO extends Peripheral {
  private static int[][] inputData;
  private static int inputDataRow=0;

  private static int[] getInputData() {
    System.out.printf("Getting GPIO state, current cycle=%d, current row %d, next row cycle=%d\n",datapath.cycles,inputDataRow,inputData[inputDataRow+1][0]);
    while(datapath.cycles>inputData[inputDataRow+1][0]) {
      inputDataRow++;
      System.out.printf("Getting GPIO state, current cycle=%d, current row %d, next row cycle=%d\n",datapath.cycles,inputDataRow,inputData[inputDataRow+1][0]);
    }
    int[] result=inputData[inputDataRow];
    return result;
  }
  private static Datapath datapath;
  public enum Registers implements DeviceRegister {
    DIR0     (RW,0x000), //DIRx and MASKx don't need any code to support.
    MASK0    (RW,0x010),
    //This one actually is used to set and get the value of the pins, respecting the direction flags and mask bits
    PIN0     (RW,0x014) {@Override public int read() {return read(PIN0,DIR0,MASK0,0);} @Override public void write(int Lval) {write(PIN0,MASK0,Lval);} },
    //This one just reroutes to PINx for read. When written, it only sets the bits which are set in Lval, leaving the other bits in their current state
    SET0     (RW,0x018) {@Override public int read() {return PIN0.read();} @Override public void write(int Lval) {set(PIN0,MASK0,Lval);} },
    //This only clears the bits which are set in Lval, leaving the other bits in their current state
    CLR0     (WO,0x01C) {@Override public void write(int Lval) {clear(PIN0,MASK0,Lval);} },
    DIR1     (RW,0x020),
    MASK1    (RW,0x030),
    PIN1     (RW,0x034) {@Override public int read() {return read(PIN1,DIR1,MASK1,1);} @Override public void write(int Lval) {write(PIN1,MASK1,Lval);} },
    SET1     (RW,0x038) {@Override public int read() {return PIN1.read();} @Override public void write(int Lval) {set(PIN1,MASK1,Lval);} },
    CLR1     (WO,0x03C) {@Override public void write(int Lval) {clear(PIN1,MASK1,Lval);} },
    DIR2     (RW,0x040),
    MASK2    (RW,0x050),
    PIN2     (RW,0x054) {@Override public int read() {return read(PIN2,DIR2,MASK2,2);} @Override public void write(int Lval) {write(PIN2,MASK2,Lval);} },
    SET2     (RW,0x058) {@Override public int read() {return PIN2.read();} @Override public void write(int Lval) {set(PIN2,MASK2,Lval);} },
    CLR2     (WO,0x05C) {@Override public void write(int Lval) {clear(PIN2,MASK2,Lval);} },
    DIR3     (RW,0x060),
    MASK3    (RW,0x070),
    PIN3     (RW,0x074) {@Override public int read() {return read(PIN3,DIR3,MASK3,3);} @Override public void write(int Lval) {write(PIN3,MASK3,Lval);} },
    SET3     (RW,0x078) {@Override public int read() {return PIN3.read();} @Override public void write(int Lval) {set(PIN3,MASK3,Lval);} },
    CLR3     (WO,0x07C) {@Override public void write(int Lval) {clear(PIN3,MASK3,Lval);} },
    DIR4     (RW,0x080),
    MASK4    (RW,0x090),
    PIN4     (RW,0x094) {@Override public int read() {return read(PIN4,DIR4,MASK4,4);} @Override public void write(int Lval) {write(PIN4,MASK4,Lval);} },
    SET4     (RW,0x098) {@Override public int read() {return PIN4.read();} @Override public void write(int Lval) {set(PIN4,MASK4,Lval);} },
    CLR4     (WO,0x09C) {@Override public void write(int Lval) {clear(PIN4,MASK4,Lval);} },
    DIR5     (RW,0x0A0),
    MASK5    (RW,0x0B0),
    PIN5     (RW,0x094) {@Override public int read() {return read(PIN5,DIR5,MASK5,5);} @Override public void write(int Lval) {write(PIN5,MASK5,Lval);} },
    SET5     (RW,0x098) {@Override public int read() {return PIN5.read();} @Override public void write(int Lval) {set(PIN5,MASK5,Lval);} },
    CLR5     (WO,0x09C) {@Override public void write(int Lval) {clear(PIN5,MASK5,Lval);} };
    //Pin-handling code (same for all pins)
    private static int read(Registers PINx, Registers DIRx, Registers MASKx, int port) {
      //Which bits are input?
      int outMask=DIRx.val;
      //IF a bit not visible through MASKx, then its value will be 0.
      //Otherwise, if direction is input, the current input will be visible
      //           otherwise the last output will be visible
      return (~(outMask) & getInputData()[port+1] | (outMask) & PINx.val) & ~MASKx.val; 
    };
    private static void write(Registers PINx, Registers MASKx, int Lval) {
      PINx.val=(PINx.val & MASKx.val) | (Lval & ~MASKx.val); 
    }
    private static void set(Registers PINx, Registers MASKx, int Lval) {
      PINx.val|=(Lval&~MASKx.val);
    }
    private static void clear(Registers PINx, Registers MASKx, int Lval) {
      PINx.val&=~(Lval&~MASKx.val);
    }
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
  public void reset() {
    for(Registers r:Registers.values()) {
      r.val=r.resetVal;
    }
  }
  public GPIO(Datapath Ldatapath, int[][] LinputData) {
    super("GPIO",0x20098000,0x4000);
    datapath=Ldatapath;
    inputData=LinputData;
    setupRegs(Registers.values());
    reset();
  }
}
