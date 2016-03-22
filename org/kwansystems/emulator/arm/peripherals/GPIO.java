package org.kwansystems.emulator.arm.peripherals;

import static org.kwansystems.emulator.arm.RegisterDirection.*;

import org.kwansystems.emulator.arm.Datapath;
import org.kwansystems.emulator.arm.DeviceRegister;
import org.kwansystems.emulator.arm.Peripheral;
import org.kwansystems.emulator.arm.RegisterDirection;
import org.kwansystems.emulator.arm.peripherals.UART.Registers;

public class GPIO extends Peripheral {
  private static int[][] inputData;
  private static int inputDataRow=0;
  private static int cycles;
  private static int[] getInputData() {
    while(inputDataRow<inputData.length-1 && cycles>inputData[inputDataRow+1][0]) {
      inputDataRow++;
      if(inputDataRow<inputData.length-1) {
        System.out.printf("Getting GPIO state, current cycle=%d, current row %d, next row cycle=%d\n",cycles,inputDataRow,inputData[inputDataRow+1][0]);
      } else {
        System.out.printf("On last GPIO line\n");
      }
    }
    System.out.printf("Getting GPIO state, current cycle=%d, current row %d (started on cycle %d)",cycles,inputDataRow,inputData[inputDataRow][0]);
    if(inputDataRow<inputData.length-1) {
      System.out.printf(", next row cycle=%d\n",inputData[inputDataRow+1][0]);
    } else {
      System.out.printf(", last row\n");
    }
    int[] result=inputData[inputDataRow];
    return result;
  }
  public enum Registers implements DeviceRegister {
    DIR0     (RW,0x000), //DIRx and MASKx don't need any code to support.
    MASK0    (RW,0x010),
    //This one actually is used to set and get the value of the pins, respecting the direction flags and mask bits
    PIN0     (RW,0x014) {@Override public int read(Peripheral gpio) {return PINxread(this,gpio);} @Override public void write(Peripheral gpio, int val) {PINxwrite(this,gpio,val);} },
    //This one just reroutes to PINx for read. When written, it only sets the bits which are set in Lval, leaving the other bits in their current state
    SET0     (RW,0x018) {@Override public int read(Peripheral gpio) {return SETxread(this,gpio);} @Override public void write(Peripheral gpio, int val) {SETx(this,gpio,val);} },
    //This only clears the bits which are set in Lval, leaving the other bits in their current state
    CLR0     (WO,0x01C) {@Override public void write(Peripheral gpio, int Lval) {CLRx(this,gpio,Lval);} },
    DIR1     (RW,0x020),
    MASK1    (RW,0x030),
    PIN1     (RW,0x034) {@Override public int read(Peripheral gpio) {return PINxread(this,gpio);} @Override public void write(Peripheral gpio, int val) {PINxwrite(this,gpio,val);} },
    SET1     (RW,0x038) {@Override public int read(Peripheral gpio) {return SETxread(this,gpio);} @Override public void write(Peripheral gpio, int val) {SETx(this,gpio,val);} },
    CLR1     (WO,0x03C) {@Override public void write(Peripheral gpio, int Lval) {CLRx(this,gpio,Lval);} },
    DIR2     (RW,0x040),
    MASK2    (RW,0x050),
    PIN2     (RW,0x054) {@Override public int read(Peripheral gpio) {return PINxread(this,gpio);} @Override public void write(Peripheral gpio, int val) {PINxwrite(this,gpio,val);} },
    SET2     (RW,0x058) {@Override public int read(Peripheral gpio) {return SETxread(this,gpio);} @Override public void write(Peripheral gpio, int val) {SETx(this,gpio,val);} },
    CLR2     (WO,0x05C) {@Override public void write(Peripheral gpio, int Lval) {CLRx(this,gpio,Lval);} },
    DIR3     (RW,0x060),
    MASK3    (RW,0x070),
    PIN3     (RW,0x074) {@Override public int read(Peripheral gpio) {return PINxread(this,gpio);} @Override public void write(Peripheral gpio, int val) {PINxwrite(this,gpio,val);} },
    SET3     (RW,0x078) {@Override public int read(Peripheral gpio) {return SETxread(this,gpio);} @Override public void write(Peripheral gpio, int val) {SETx(this,gpio,val);} },
    CLR3     (WO,0x07C) {@Override public void write(Peripheral gpio, int Lval) {CLRx(this,gpio,Lval);} },
    DIR4     (RW,0x080),
    MASK4    (RW,0x090),
    PIN4     (RW,0x094) {@Override public int read(Peripheral gpio) {return PINxread(this,gpio);} @Override public void write(Peripheral gpio, int val) {PINxwrite(this,gpio,val);} },
    SET4     (RW,0x098) {@Override public int read(Peripheral gpio) {return SETxread(this,gpio);} @Override public void write(Peripheral gpio, int val) {SETx(this,gpio,val);} },
    CLR4     (WO,0x09C) {@Override public void write(Peripheral gpio, int Lval) {CLRx(this,gpio,Lval);} },
    DIR5     (RW,0x0A0),
    MASK5    (RW,0x0B0),
    PIN5     (RW,0x0B4) {@Override public int read(Peripheral gpio) {return PINxread(this,gpio);} @Override public void write(Peripheral gpio, int val) {PINxwrite(this,gpio,val);} },
    SET5     (RW,0x0B8) {@Override public int read(Peripheral gpio) {return SETxread(this,gpio);} @Override public void write(Peripheral gpio, int val) {SETx(this,gpio,val);} },
    CLR5     (WO,0x0BC) {@Override public void write(Peripheral gpio, int Lval) {CLRx(this,gpio,Lval);} };
    //Pin-handling code (same for all pins)
    private static int PINxread(Registers PINx, Peripheral gpio) {
      //Which bits are input?
      int port=(PINx.ofs-0x14)/0x20;
      int dir =Registers.values()[port*5+0].read(gpio);
      int mask=Registers.values()[port*5+1].read(gpio);
      int outMask=dir;
      //IF a bit not visible through MASKx, then its value will be 0.
      //Otherwise, if direction is input, the current input will be visible
      //           otherwise the last output will be visible
      return (~(outMask) & getInputData()[port+1] | (outMask) & gpio.peek(PINx.ofs)) & ~mask; 
    };
    private static void PINxwrite(Registers PINx, Peripheral gpio, int Lval) {
      int port=(PINx.ofs-0x14)/0x20;
      int dir =Registers.values()[port*5+0].read(gpio);
      int mask=Registers.values()[port*5+1].read(gpio);
      int val=gpio.peek(PINx.ofs);
      gpio.poke(PINx.ofs,(val & mask) | (Lval & ~mask)); 
    }
    private static int SETxread(Registers SETx, Peripheral gpio) {
      int port=(SETx.ofs-0x18)/0x20;
      return Registers.values()[port*2+1].read(gpio);
    }
    private static void SETx(Registers SETx, Peripheral gpio, int Lval) {
      int port=(SETx.ofs-0x18)/0x20;
      Registers MASKx=Registers.values()[port*5+1];
      Registers PINx =Registers.values()[port*5+2];
      gpio.poke(PINx.ofs, gpio.read(PINx.ofs)|(Lval&~MASKx.read(gpio)));
    }
    private static void CLRx(Registers CLRx, Peripheral gpio, int Lval) {
      int port=(CLRx.ofs-0x1C)/0x20;
      Registers MASKx=Registers.values()[port*5+1];
      Registers PINx =Registers.values()[port*5+2];
      gpio.poke(PINx.ofs, gpio.read(PINx.ofs)&~(Lval&~MASKx.read(gpio)));
    }
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
  @Override
  public void reset(boolean inReset) {
    reset(inReset,Registers.values());
  }
  public GPIO(int[][] LinputData) {
    super("GPIO",0x20098000,0x4000);
    inputData=LinputData;
    setupRegs(Registers.values());
  }
  public void tick(int Lpclk) {
    super.tick(Lpclk);
    cycles=pclk;
  }
}
