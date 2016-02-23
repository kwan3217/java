package org.kwansystems.emulator.arm;

public class Datapath {
  public int r[]=new int[16];
  public int mem[][]=new int[0xffff][];
  
  void loadMem(String infn, int ofs) {
	int seg=ArmDecode.parse(ofs, 16, 16);
  }
}
