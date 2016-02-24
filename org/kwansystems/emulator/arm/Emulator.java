package org.kwansystems.emulator.arm;

public class Emulator {
  public ArmDecode decode=new Thumb2Decode();
  public Operation execute=new Operation();
  public Fetch fetch=new Fetch();
  public Datapath datapath=new Datapath();
}
