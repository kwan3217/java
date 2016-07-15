package org.kwansystems.emulator.arm;

public interface Operation {
  public void execute(Datapath datapath, DecodedInstruction ins);
}
