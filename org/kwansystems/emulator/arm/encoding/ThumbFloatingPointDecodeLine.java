package org.kwansystems.emulator.arm.encoding;

import static org.kwansystems.emulator.arm.Operation.*;
import static org.kwansystems.emulator.arm.Decode.*;
import static org.kwansystems.emulator.arm.DecodeLine.*;

import org.kwansystems.emulator.arm.DecodeLine;
import org.kwansystems.emulator.arm.DecodedInstruction;
import org.kwansystems.emulator.arm.FloatingPointOperation;
import org.kwansystems.emulator.arm.Operation;
import org.kwansystems.emulator.arm.Decode.DecodeShiftReturn;
import org.kwansystems.emulator.arm.DecodedInstruction.SetFlags;

import java.util.*;

public enum ThumbFloatingPointDecodeLine implements DecodeLine {
  VLDRT1("1110/1101/U/D/0/1/nnnn//dddd/1011/iiiiiiii") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      ins.single_reg=false;
      ins.add=parseBit(this,IR,"U");
      ins.imm=parse(this,IR,"i")<<2;
      ins.Rd=parse(this,IR,"D")<<4 | parse(this,IR,"d");
      ins.Rn=parse(this,IR,"n");
      return true;
    }    
  },
  VLDRT2("1110/1101/U/D/0/1/nnnn//dddd/1010/iiiiiiii") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      ins.single_reg=true;
      ins.add=parseBit(this,IR,"U");
      ins.imm=parse(this,IR,"i")<<2;
      ins.Rd=parse(this,IR,"d")<<1 | parse(this,IR,"D");
      ins.Rn=parse(this,IR,"n");
      return true;
    }    
  },
  VADDT1("1110/11100/D/11/nnnn//dddd/101/z/N/0/M/0/mmmm") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      boolean dp_operation=parseBit(this,IR,"z"); 
      ins.single_reg=!dp_operation;
      if(dp_operation) {
        //bits D, N, and M had better be 0, because only 16 double-precision registers
        ins.Rd=parse(this,IR,"D")<<4|parse(this,IR,"dddd");
        ins.Rn=parse(this,IR,"N")<<4|parse(this,IR,"nnnn");
        ins.Rm=parse(this,IR,"M")<<4|parse(this,IR,"mmmm");
      } else {
        //the 4-bit field selects which double-precision register is used, and the 1-bit field selects which half of it
        ins.Rd=parse(this,IR,"dddd")<<1|parse(this,IR,"D");
        ins.Rn=parse(this,IR,"nnnn")<<1|parse(this,IR,"N");
        ins.Rm=parse(this,IR,"mmmm")<<1|parse(this,IR,"M");
      }
      return true;
    }    
  }
  //DecodeLine boilerplate
  ,
  UNDEFINEDT1("1111/1111/1111/1111") {
    @Override public boolean decode(int IR, DecodedInstruction ins) {
      return false;
    }    
  };
  private int moneBits, mzeroBits;
  private String bitpattern;
  private final Operation mop;
  private Map<String,int[]> fieldMap=new HashMap<String,int[]>();
  @Override public int oneBits()  {return moneBits;};
  @Override public int zeroBits() {return mzeroBits;};
  @Override public Map<String,int[]> getFieldMap() {return fieldMap;};
  @Override public String bitPattern() {return bitpattern;};
  @Override public void setOneBits(int LoneBits)  {moneBits=LoneBits;};
  @Override public void setZeroBits(int LzeroBits)  {mzeroBits=LzeroBits;};
  public Operation op() {return mop;};
  private ThumbFloatingPointDecodeLine(String Lbitpattern) {
    bitpattern=Lbitpattern;
    int[] masks=DecodeLine.interpretBitPattern(bitpattern);
    moneBits =masks[1];
    mzeroBits=masks[0];
    mop=FloatingPointOperation.valueOf(toString().substring(0,toString().length()-2));
  }
}
