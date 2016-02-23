package org.kwansystems.emulator.arm;

public class Emulator {
  public ArmDecode decode=new Thumb2Decode();
  public Execute execute=new Execute();
  public Fetch fetch=new Fetch();
  public Datapath datapath=new Datapath();
}
