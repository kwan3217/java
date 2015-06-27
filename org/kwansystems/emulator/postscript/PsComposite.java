package org.kwansystems.emulator.postscript;

public abstract class PsComposite implements TreeNodeGenerator {
  ExecContext EC;
  public PsComposite(ExecContext LEC) {
    super();
    EC=LEC;
  }
  public abstract int length();
}
