package org.kwansystems.emulator.arm;

import java.io.*;

import static org.kwansystems.emulator.arm.ConditionCode.*;
import static org.kwansystems.emulator.arm.BitFiddle.*;

public class Datapath {
  public static int swapEndian(int val) {
    return (((val & 0x000000FF) << 24) & 0xFF000000) |
           (((val & 0x0000FF00) <<  8) & 0x00FF0000) |
           (((val & 0x00FF0000) >>  8) & 0x0000FF00) |
           (((val & 0xFF000000) >> 24) & 0x000000FF) ;
           
           
  }
  public void flushPipeline() {
    flush=true;
    ins=null;
    insDataValid=false;
  }
  //Pipeline control state
  public boolean flush=false;
  //Pipeline state
  DecodedInstruction ins=null;
  int insData=0;
  boolean insDataValid=false;
  //Program-visible state
  public int r[]=new int[16];
  public int APSR;
  private byte[][] mem=new byte[0x10000][];
  private int getSeg(int address) {
    int seg=parse(address, 16, 16);
    if(mem[seg]==null) mem[seg]=new byte[0x10000];
    return seg;
  }
  void loadBin(String infn, int address) throws IOException {
	  FileInputStream inf=new FileInputStream(infn);
    int b=inf.read();
    while(b>=0) {
      writeMem1(address,b);
	    address++;
	    b=inf.read();
	  }
	  inf.close();
  }
  public boolean APSR_C() {return parseBit(APSR,CPos);};
  public void APSR_setC(boolean C) {APSR=setBit(APSR,CPos,C);}
  public void APSR_setC(int C) {APSR_setC(C!=0);};
  public boolean APSR_N() {return parseBit(APSR,NPos);};
  public void APSR_setN(boolean N) {APSR=setBit(APSR,NPos,N);}
  public void APSR_setN(int N) {APSR_setN(N!=0);};
  public boolean APSR_V() {return parseBit(APSR,VPos);};
  public void APSR_setV(boolean V) {APSR=setBit(APSR,VPos,V);}
  public void APSR_setV(int V) {APSR_setV(V!=0);};
  public boolean APSR_Z() {return parseBit(APSR,ZPos);};
  public void APSR_setZ(boolean Z) {APSR=setBit(APSR,ZPos,Z);}
  public void APSR_setZ(int Z) {APSR_setZ(Z!=0);};
  public boolean inIT=false;
  public int readMem(int address, int bytes) { 
    int result=0;
    for(int i=bytes-1;i>=0;i--) {
      int seg=getSeg(address+i);
      int blockofs=BitFiddle.parse(address+i, 0, 16);
      result=(result<<8) | ((int)(mem[seg][blockofs]) & 0xff);
    }
    return result; 
  };
  public int readMem4(int address) {return readMem(address,4);} 
  public int readMem2(int address) {return readMem(address,2);} 
  public int readMem1(int address) {return readMem(address,1);} 
  public void writeMem(int address, int bytes, int value) { 
    for(int i=0;i<bytes;i++) {
      int seg=getSeg(address);
      int ofs=parse(address, 0, 16);
      mem[seg][ofs]=(byte)((value<<(8*i)) & 0xFF);
    }
  };
  public void writeMem4(int address, int value) {writeMem(address,4,value);} 
  public void writeMem2(int address, int value) {writeMem(address,2,value);} 
  public void writeMem1(int address, int value) {writeMem(address,1,value);} 
  public void LoadWritePC(int value) {
    //A.6.43 LoadWritePC()
    //This procedure writes a value to the PC with the correct semantics for such writes by load instructions. That
    //is, with BX-like interworking behavior in ARMv5 and above, and just a change to the PC in ARMv4T.
    if(value %2==0) throw new Unpredictable(); //"Going to ARM state on a machine that doesn't support it"
    r[15]=value & ~1;
    flushPipeline();
  }
  public void setIT(int firstcond, int mask) {
    APSR   
  }
  public boolean inIT() {
    return false;
  }
}
