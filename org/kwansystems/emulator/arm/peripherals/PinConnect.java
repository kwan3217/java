package org.kwansystems.emulator.arm.peripherals;

import static org.kwansystems.emulator.arm.RegisterDirection.*;
import static org.kwansystems.emulator.arm.BitFiddle.*;

import org.kwansystems.emulator.arm.Datapath;
import org.kwansystems.emulator.arm.DeviceRegister;
import org.kwansystems.emulator.arm.Peripheral;
import org.kwansystems.emulator.arm.RegisterDirection;

public class PinConnect extends Peripheral {
  public enum PinType{
    D(0x030) {
      @Override
      public String toString(int IOCON, String[] pinModeNames) {
        int FUNC=parse(IOCON, 0,3);
        int MODE=parse(IOCON, 3,2);
        int HYS =parse(IOCON, 5,1);
        int INV =parse(IOCON, 6,1);
        int SLEW=parse(IOCON, 9,1);
        int OD  =parse(IOCON,10,1);
        String result=String.format("Digital pin: Function %x",FUNC);
        if(pinModeNames!=null && parse(IOCON,0,3)<pinModeNames.length) result+="("+pinModeNames[FUNC]+")";
        result+=",";
        result+=String.format("Mode: %d (%s),",MODE,MODEname[MODE]);
        return result;
      }
      
    },
    A(0x1B0) {
      @Override
      public String toString(int IOCON, String[] pinModeNames) {
        int FUNC  =parse(IOCON, 0,3);
        int MODE  =parse(IOCON, 3,2);
        int INV   =parse(IOCON, 6,1);
        int ADMODE=parse(IOCON, 7,1);
        int FILTER=parse(IOCON, 8,1);
        int OD    =parse(IOCON,10,1);
        int DACEN =parse(IOCON,16,1);
        String result=String.format("Analog pin: Function %x",FUNC);
        if(pinModeNames!=null && parse(IOCON,0,3)<pinModeNames.length) result+="("+pinModeNames[FUNC]+")";
        result+=",";
        result+=String.format("Mode: %d (%s),",MODE,MODEname[MODE]);
        return result;
      }
    },
    U(0x000) {
      @Override
      public String toString(int IOCON, String[] pinModeNames) {
        int FUNC  =parse(IOCON, 0,3);
        String result=String.format("Analog pin: Function %x",FUNC);
        if(pinModeNames!=null && parse(IOCON,0,3)<pinModeNames.length) result+="("+pinModeNames[FUNC]+")";
        return result;
      }
    },
    I(0x000) {
      @Override
      public String toString(int IOCON, String[] pinModeNames) {
        int FUNC  =parse(IOCON, 0,3);
        String result=String.format("Analog pin: Function %x",FUNC);
        if(pinModeNames!=null && parse(IOCON,0,3)<pinModeNames.length) result+="("+pinModeNames[FUNC]+")";
        return result;
      }
    },
    W(0x0A0) {
      @Override
      public String toString(int IOCON, String[] pinModeNames) {
        int FUNC  =parse(IOCON, 0,3);
        String result=String.format("Analog pin: Function %x",FUNC);
        if(pinModeNames!=null && parse(IOCON,0,3)<pinModeNames.length) result+="("+pinModeNames[FUNC]+")";
        return result;
      }
    };
    private static String[] MODEname=new String[] {"No pullup/pulldown","Pulldown enabled","Pullup enabled","Repeater"};
    public abstract String toString(int IOCON, String[] pinModeNames); 
    public int resetValue;
    private PinType(int LresetValue) {resetValue=LresetValue;};
  }
  private static int regOffset=0;
  public enum Registers implements DeviceRegister {
    IOCON_P0_00(RW,PinType.D,new String[] {"P0[0]","CAN_RD1","U3_TXD","I2C1_SDA","U0_TXD"}),
    IOCON_P0_01(RW,PinType.D,new String[] {"P0[1]","CAN_TD1","U3_RXD","I2C1_SCL","U0_RXD"}),
    IOCON_P0_02(RW,PinType.D),
    IOCON_P0_03(RW,PinType.D),
    IOCON_P0_04(RW,PinType.D),
    IOCON_P0_05(RW,PinType.D),
    IOCON_P0_06(RW,PinType.D),
    IOCON_P0_07(RW,PinType.W),
    IOCON_P0_08(RW,PinType.W),
    IOCON_P0_09(RW,PinType.W),
    IOCON_P0_10(RW,PinType.D),
    IOCON_P0_11(RW,PinType.D),
    IOCON_P0_12(RW,PinType.A),
    IOCON_P0_13(RW,PinType.A),
    IOCON_P0_14(RW,PinType.D),
    IOCON_P0_15(RW,PinType.D),
    IOCON_P0_16(RW,PinType.D),
    IOCON_P0_17(RW,PinType.D),
    IOCON_P0_18(RW,PinType.D),
    IOCON_P0_19(RW,PinType.D),
    IOCON_P0_20(RW,PinType.D),
    IOCON_P0_21(RW,PinType.D),
    IOCON_P0_22(RW,PinType.D),
    IOCON_P0_23(RW,PinType.A),
    IOCON_P0_24(RW,PinType.A),
    IOCON_P0_25(RW,PinType.A),
    IOCON_P0_26(RW,PinType.A),
    IOCON_P0_27(RW,PinType.I),
    IOCON_P0_28(RW,PinType.I),
    IOCON_P0_29(RW,PinType.U),
    IOCON_P0_30(RW,PinType.U),
    IOCON_P0_31(RW,PinType.U),

    IOCON_P1_00(RW,PinType.D),
    IOCON_P1_01(RW,PinType.D),
    IOCON_P1_02(RW,PinType.D),
    IOCON_P1_03(RW,PinType.D),
    IOCON_P1_04(RW,PinType.D),
    IOCON_P1_05(RW,0x0B0,PinType.W),
    IOCON_P1_06(RW,0x0B0,PinType.W),
    IOCON_P1_07(RW,0x0B0,PinType.W),
    IOCON_P1_08(RW,PinType.D),
    IOCON_P1_09(RW,PinType.D),
    IOCON_P1_10(RW,PinType.D),
    IOCON_P1_11(RW,PinType.D),
    IOCON_P1_12(RW,PinType.D),
    IOCON_P1_13(RW,PinType.D),
    IOCON_P1_14(RW,0x0B0,PinType.W),
    IOCON_P1_15(RW,PinType.D),
    IOCON_P1_16(RW,0x0B0,PinType.W),
    IOCON_P1_17(RW,0x0B0,PinType.W),
    IOCON_P1_18(RW,PinType.D),
    IOCON_P1_19(RW,PinType.D),
    IOCON_P1_20(RW,PinType.D),
    IOCON_P1_21(RW,PinType.D),
    IOCON_P1_22(RW,PinType.D),
    IOCON_P1_23(RW,PinType.D),
    IOCON_P1_24(RW,PinType.D),
    IOCON_P1_25(RW,PinType.D),
    IOCON_P1_26(RW,PinType.D),
    IOCON_P1_27(RW,PinType.D),
    IOCON_P1_28(RW,PinType.D),
    IOCON_P1_29(RW,PinType.D),
    IOCON_P1_30(RW,PinType.A),
    IOCON_P1_31(RW,PinType.A),

    IOCON_P2_00(RW,PinType.D),
    IOCON_P2_01(RW,PinType.D),
    IOCON_P2_02(RW,PinType.D),
    IOCON_P2_03(RW,PinType.D),
    IOCON_P2_04(RW,PinType.D),
    IOCON_P2_05(RW,PinType.D),
    IOCON_P2_06(RW,PinType.D),
    IOCON_P2_07(RW,PinType.D),
    IOCON_P2_08(RW,PinType.D),
    IOCON_P2_09(RW,PinType.D),
    IOCON_P2_10(RW,PinType.D),
    IOCON_P2_11(RW,PinType.D),
    IOCON_P2_12(RW,PinType.D),
    IOCON_P2_13(RW,PinType.D),
    IOCON_P2_14(RW,PinType.D),
    IOCON_P2_15(RW,PinType.D),
    IOCON_P2_16(RW,PinType.D),
    IOCON_P2_17(RW,PinType.D),
    IOCON_P2_18(RW,PinType.D),
    IOCON_P2_19(RW,PinType.D),
    IOCON_P2_20(RW,PinType.D),
    IOCON_P2_21(RW,PinType.D),
    IOCON_P2_22(RW,PinType.D),
    IOCON_P2_23(RW,PinType.D),
    IOCON_P2_24(RW,PinType.D),
    IOCON_P2_25(RW,PinType.D),
    IOCON_P2_26(RW,PinType.D),
    IOCON_P2_27(RW,PinType.D),
    IOCON_P2_28(RW,PinType.D),
    IOCON_P2_29(RW,PinType.D),
    IOCON_P2_30(RW,PinType.D),
    IOCON_P2_31(RW,PinType.D),

    IOCON_P3_00(RW,PinType.D,new String[] {"P3[00]","EMC_D[00]"}),
    IOCON_P3_01(RW,PinType.D,new String[] {"P3[01]","EMC_D[01]"}),
    IOCON_P3_02(RW,PinType.D,new String[] {"P3[02]","EMC_D[02]"}),
    IOCON_P3_03(RW,PinType.D,new String[] {"P3[03]","EMC_D[03]"}),
    IOCON_P3_04(RW,PinType.D,new String[] {"P3[04]","EMC_D[04]"}),
    IOCON_P3_05(RW,PinType.D,new String[] {"P3[05]","EMC_D[05]"}),
    IOCON_P3_06(RW,PinType.D,new String[] {"P3[06]","EMC_D[06]"}),
    IOCON_P3_07(RW,PinType.D,new String[] {"P3[07]","EMC_D[07]"}),
    IOCON_P3_08(RW,PinType.D,new String[] {"P3[08]","EMC_D[08]"}),
    IOCON_P3_09(RW,PinType.D,new String[] {"P3[09]","EMC_D[09]"}),
    IOCON_P3_10(RW,PinType.D,new String[] {"P3[10]","EMC_D[10]"}),
    IOCON_P3_11(RW,PinType.D,new String[] {"P3[11]","EMC_D[11]"}),
    IOCON_P3_12(RW,PinType.D,new String[] {"P3[12]","EMC_D[12]"}),
    IOCON_P3_13(RW,PinType.D,new String[] {"P3[13]","EMC_D[13]"}),
    IOCON_P3_14(RW,PinType.D,new String[] {"P3[14]","EMC_D[14]"}),
    IOCON_P3_15(RW,PinType.D,new String[] {"P3[15]","EMC_D[15]"}),
    IOCON_P3_16(RW,PinType.D,new String[] {"P3[16]","EMC_D[16]","PWM0[1]","U1_TXD"}),
    IOCON_P3_17(RW,PinType.D,new String[] {"P3[17]","EMC_D[17]","PWM0[2]","U1_RXD"}),
    IOCON_P3_18(RW,PinType.D,new String[] {"P3[18]","EMC_D[18]","PWM0[3]","U1_CTS"}),
    IOCON_P3_19(RW,PinType.D,new String[] {"P3[19]","EMC_D[19]","PWM0[4]","U1_DCD"}),
    IOCON_P3_20(RW,PinType.D,new String[] {"P3[20]","EMC_D[20]","PWM0[5]","U1_DSR"}),
    IOCON_P3_21(RW,PinType.D,new String[] {"P3[21]","EMC_D[21]","PWM0[6]","U1_DTR"}),
    IOCON_P3_22(RW,PinType.D,new String[] {"P3[22]","EMC_D[22]","PWM0_CAP0","U1_RI"}),
    IOCON_P3_23(RW,PinType.D,new String[] {"P3[23]","EMC_D[23]","PWM1_CAP0","T0_CAP0"}),
    IOCON_P3_24(RW,PinType.D,new String[] {"P3[24]","EMC_D[24]","PWM1[1]","T0_CAP1"}),
    IOCON_P3_25(RW,PinType.D,new String[] {"P3[25]","EMC_D[25]","PWM2[1]","T0_MAT0"}),
    IOCON_P3_26(RW,PinType.D,new String[] {"P3[26]","EMC_D[26]","PWM3[1]","T0_MAT1"}),
    IOCON_P3_27(RW,PinType.D,new String[] {"P3[27]","EMC_D[27]","PWM4[1]","T1_CAP0"}),
    IOCON_P3_28(RW,PinType.D,new String[] {"P3[28]","EMC_D[28]","PWM5[1]","T1_CAP1"}),
    IOCON_P3_29(RW,PinType.D,new String[] {"P3[29]","EMC_D[29]","PWM6[1]","T1_MAT0"}),
    IOCON_P3_30(RW,PinType.D,new String[] {"P3[30]","EMC_D[30]","U1_RTS","T1_MAT1"}),
    IOCON_P3_31(RW,PinType.D,new String[] {"P3[31]","EMC_D[31]",null,"U1_TXD","T1_MAT2"}),

    IOCON_P4_00(RW,PinType.D),
    IOCON_P4_01(RW,PinType.D),
    IOCON_P4_02(RW,PinType.D),
    IOCON_P4_03(RW,PinType.D),
    IOCON_P4_04(RW,PinType.D),
    IOCON_P4_05(RW,PinType.D),
    IOCON_P4_06(RW,PinType.D),
    IOCON_P4_07(RW,PinType.D),
    IOCON_P4_08(RW,PinType.D),
    IOCON_P4_09(RW,PinType.D),
    IOCON_P4_10(RW,PinType.D),
    IOCON_P4_11(RW,PinType.D),
    IOCON_P4_12(RW,PinType.D),
    IOCON_P4_13(RW,PinType.D),
    IOCON_P4_14(RW,PinType.D),
    IOCON_P4_15(RW,PinType.D),
    IOCON_P4_16(RW,PinType.D),
    IOCON_P4_17(RW,PinType.D),
    IOCON_P4_18(RW,PinType.D),
    IOCON_P4_19(RW,PinType.D),
    IOCON_P4_20(RW,PinType.D),
    IOCON_P4_21(RW,PinType.D),
    IOCON_P4_22(RW,PinType.D),
    IOCON_P4_23(RW,PinType.D),
    IOCON_P4_24(RW,PinType.D),
    IOCON_P4_25(RW,PinType.D),
    IOCON_P4_26(RW,PinType.D),
    IOCON_P4_27(RW,PinType.D),
    IOCON_P4_28(RW,PinType.D),
    IOCON_P4_29(RW,PinType.D),
    IOCON_P4_30(RW,PinType.D),
    IOCON_P4_31(RW,PinType.D),

    IOCON_P5_00(RW,PinType.D),
    IOCON_P5_01(RW,PinType.D),
    IOCON_P5_02(RW,PinType.D),
    IOCON_P5_03(RW,PinType.D),
    IOCON_P5_04(RW,PinType.D);
    public final int ofs;
    public final int resetVal;
    public final RegisterDirection dir;
    public final PinType pinType;
    public final String[] pinModeNames;
    private Registers(RegisterDirection Ldir,int LresetVal,PinType LpinType,String[] LpinModeNames) {
      ofs=regOffset;
      regOffset+=4;
      dir=Ldir;
      resetVal=LresetVal;
      pinModeNames=LpinModeNames;
      pinType=LpinType;
    }
    private Registers(RegisterDirection Ldir,int LresetVal,PinType type) {this(Ldir,LresetVal,type,null);}
    private Registers(RegisterDirection Ldir,              PinType type) {this(Ldir,type.resetValue,type,null);}
    private Registers(RegisterDirection Ldir,              PinType type,String[] LpinModeNames) {this(Ldir,type.resetValue,type,LpinModeNames);}
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
  public PinConnect() {
    super("Pin Connect",0x4002C000,0x4000);
    setupRegs(Registers.values());
    reset(false); //Bring the part out of reset
  }
}
