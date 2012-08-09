package org.kwansystems.emulator.cips;

public class UndefinedStateException extends IllegalArgumentException {
  private static final long serialVersionUID = -5527256624090793996L;

  public UndefinedStateException(String message, String Operation) {
    super(message+" Operation: "+Operation);
  }
}
