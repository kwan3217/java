package org.kwansystems.automaton;

import java.io.*;



public class TextSymbolListener<AlphabetType> implements SymbolListener<AlphabetType> {
  PrintStream ouf;
  public TextSymbolListener(PrintStream Louf) {
    ouf=Louf;
  }
  public void ShowCurrentSymbol(AlphabetType c) {
    ouf.println("Read "+c);

  }

}
