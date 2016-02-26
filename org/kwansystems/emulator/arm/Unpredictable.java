package org.kwansystems.emulator.arm;

public class Unpredictable extends RuntimeException {
  public Unpredictable(String s) {super(s);}
  public Unpredictable() {super("Unpredictable");}

}
