package org.kwansystems.emulator.arm;

public class Undefined extends RuntimeException {
  public Undefined(String s) {super(s);}
  public Undefined() {super("Undefined");}
}
