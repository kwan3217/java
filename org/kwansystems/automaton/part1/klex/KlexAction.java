package org.kwansystems.automaton.part1.klex;

import java.io.*;

import org.kwansystems.automaton.*;

public interface KlexAction<TokenType> extends Serializable {
  public boolean ignore();
  public boolean sendUp();
  public void act(Token T, Object context);
}
