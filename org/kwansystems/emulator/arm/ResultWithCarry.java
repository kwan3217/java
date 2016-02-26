package org.kwansystems.emulator.arm;

public class ResultWithCarry {
  public int result;
  public boolean carry_out;
  public ResultWithCarry(int Lresult, boolean Lcarry_out) {result=Lresult;carry_out=Lcarry_out;};
  public ResultWithCarry() {this(0,false);};
  public String toString() {return String.format("(0x%08x,%d)\n", result,carry_out?1:0);};
}
