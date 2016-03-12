package org.kwansystems.emulator.arm;

import java.util.*;

public class Peripheral extends RandomAccessMemory {
  String name;
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
  public int read(int rel_addr, int bytes) {
    if(readregs.containsKey(rel_addr)) {
      return readregs.get(rel_addr).read(this);
    } else if(writeregs.containsKey(rel_addr)) {
      throw new RuntimeException("Reading from a write-only register "+writeregs.get(rel_addr).toString());
    } else {
      int result=super.read(rel_addr, bytes);
      System.out.printf("Reading unassigned space in %s, offset 0x%04x, value 0x%08x\n", name, rel_addr, result);
      return result;
    }
  }
  @Override
  public void write(int rel_addr, int bytes, int value) {
    if(writeregs.containsKey(rel_addr)) {
      writeregs.get(rel_addr).write(this,value);
    } else if(readregs.containsKey(rel_addr)) {
      throw new RuntimeException("Writing to a read-only register "+readregs.get(rel_addr).toString());
    } else {
      System.out.printf("Writing to unassigned space in %s, offset 0x%04x, value 0x%08x\n", name, rel_addr, value);
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
