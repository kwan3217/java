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
    while(datapath.cycles<inputData[inputDataRow][0]) inputDataRow++;
    int[] result=inputData[inputDataRow];
    return result;
  }
  private static Datapath datapath;
  public enum Registers implements DeviceRegister {
    DIR0     (RW,0x000),
    MASK0    (RW,0x010),
    PIN0     (RW,0x014) {
      @Override
      public int read() {
        //Which bits are input?
        int outMask=DIR0.val;
        //IF a bit not visible through MASKx, then its value will be 0.
        //Otherwise, if direction is input, the current input will be visible
        //           otherwise the last output will be visible
        return (~(outMask) & getInputData()[0+1] | (outMask) & val) & ~MASK0.val; 
      };
      @Override
      public void write(int Lval) {
        val=(val & MASK0.val) | (Lval & ~MASK0.val); 
      }
    },
    SET0     (RW,0x018) {
      //This one doesn't do anything on its own. It is specified to return the same
      //value as PINx on read, so we just use PINx.read() and directly set PINx.val to avoid
      //double-counting things like the mask.
      @Override
      public int read() {
        return PIN0.read();
      }
      @Override
      public void write(int Lval) {
        PIN0.val|=(Lval&~MASK0.val);
      }
    },
    CLR0     (WO,0x01C) {
      //Likewise, this one just 
      @Override
      public void write(int Lval) {
        PIN0.val&=~(Lval&~MASK0.val);
      }
    },
    DIR1     (RW,0x020),
    MASK1    (RW,0x030),
    PIN1     (RW,0x034),
    SET1     (RW,0x038),
    CLR1     (WO,0x03C),
    DIR2     (RW,0x040),
    MASK2    (RW,0x050),
    PIN2     (RW,0x054) {
      @Override
      public int read() {
        //Which bits are input?
        int outMask=DIR2.val;
        //IF a bit not visible through MASKx, then its value will be 0.
        //Otherwise, if direction is input, the current input will be visible
        //           otherwise the last output will be visible
        return (~(outMask) & getInputData()[2+1] | (outMask) & val) & ~MASK2.val; 
      };
      @Override
      public void write(int Lval) {
        val=(val & MASK2.val) | (Lval & ~MASK2.val); 
      }
    },
    SET2     (RW,0x058),
    CLR2     (WO,0x05C),
    DIR3     (RW,0x060),
    MASK3    (RW,0x070),
    PIN3     (RW,0x074),
    SET3     (RW,0x078),
    CLR3     (WO,0x07C),
    DIR4     (RW,0x080),
    MASK4    (RW,0x090),
    PIN4     (RW,0x094),
    SET4     (RW,0x098),
    CLR4     (WO,0x09C),
    DIR5     (RW,0x0A0),
    MASK5    (RW,0x0B0),
    PIN5     (RW,0x0B4),
    SET5     (RW,0x0B8),
    CLR5     (WO,0x0BC);
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
