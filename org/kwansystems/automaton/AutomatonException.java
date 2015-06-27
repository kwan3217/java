package org.kwansystems.automaton;

public class AutomatonException extends RuntimeException {
  private static final long serialVersionUID = 4532036155856734080L;

  public AutomatonException(String message) {
    super(message);
  }
  public AutomatonException(Throwable e) {
    super(e);
  }
}
