package org.kwansystems.emulator.arm;

import java.util.*;
import java.io.*;

public class Peripheral extends RandomAccessMemory {
  String name;
  public static PrintWriter ouf; 
  public Peripheral(String Lname, int Lbase, int size) {
    super(Lbase, size);
    name=Lname;
  }
  public Peripheral(String Lname, int Lbase) {
    this(Lname,Lbase,0x4000); //Default address space size
  }
  protected Map<Integer,DeviceRegister> readregs=new HashMap<Integer,DeviceRegister>();
  protected Map<Integer,DeviceRegister> writeregs=new HashMap<Integer,DeviceRegister>();
  @Override
  final public int read(int rel_addr, int bytes) {
    if(readregs.containsKey(rel_addr)) {
      DeviceRegister reg=readregs.get(rel_addr);
      int val=reg.read(this);
      if(ouf!=null) {
        ouf.printf("Cycle %d, reading register %s.%s(%04x), got value 0x%08x\n", pclk, name, reg.toString(),rel_addr, val);
        ouf.flush();
      }
      return val;
    } else if(writeregs.containsKey(rel_addr)) {
      DeviceRegister reg=writeregs.get(rel_addr);
      if(ouf!=null) {
        ouf.printf("Cycle %d, attempting to read write-only register %s.%s(%04x)\n", pclk, name, rel_addr, reg.toString());
        ouf.flush();
      }
      throw new RuntimeException("Reading from a write-only register "+reg.toString());
    } else {
      int result=super.read(rel_addr, bytes);
      System.out.printf("Reading unassigned space in %s, offset 0x%04x, value 0x%08x\n", name, rel_addr, result);
      if(ouf!=null) {
        ouf.printf("Cycle %d, reading register %s[%04x], value 0x%08x\n", pclk, name, rel_addr, result);
        ouf.flush();
      }
      return result;
    }
  }
  @Override
  final public void write(int rel_addr, int bytes, int value) {
    if(writeregs.containsKey(rel_addr)) {
      DeviceRegister reg=writeregs.get(rel_addr);
      reg.write(this,value);
      if(ouf!=null) {
        ouf.printf("Cycle %d, writing register %s.%s(%04x), got value 0x%08x\n", pclk, name, reg.toString(),rel_addr, value);
        ouf.flush();
      }
    } else if(readregs.containsKey(rel_addr)) {
      DeviceRegister reg=readregs.get(rel_addr);
      if(ouf!=null) {
        ouf.printf("Cycle %d, attempting to write read-only register %s.%s(%04x) with value 0x%08x\n", pclk, name, reg.toString(),rel_addr,value);
        ouf.flush();
      }
      throw new RuntimeException("Writing to a read-only register "+readregs.get(rel_addr).toString());
    } else {
      System.out.printf("Writing to unassigned space in %s, offset 0x%04x, value 0x%08x\n", name, rel_addr, value);
      if(ouf!=null) {
        ouf.printf("Cycle %d, writing register %s[%04x], value 0x%08x\n", pclk, name, rel_addr, value);
        ouf.flush();
      }
      super.write(rel_addr, bytes, value);
    }
  }
  public void reset(boolean inReset, DeviceRegister[] registers) {
    if(!inReset) for(DeviceRegister r:registers) r.reset(this);
  }
  public void reset(boolean inReset) {}
  protected void setupRegs(DeviceRegister[] registers) {
    for(DeviceRegister r:registers) {
      if(r.getDir()==RegisterDirection.RO || r.getDir()==RegisterDirection.RW) readregs.put(r.getOfs(), r);
      if(r.getDir()==RegisterDirection.WO || r.getDir()==RegisterDirection.RW) writeregs.put(r.getOfs(), r);
    }
    reset(false); //Bring the part out of reset
  }
  public String toString() {return name;}
}
