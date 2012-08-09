package org.kwansystems.automaton.part2.kyacc;

import org.kwansystems.automaton.*;

public class CFGStackFrame {
  public int state;
  public Token semanticValue;
  public CFGStackFrame(int Lstate, Token LsemanticValue) {
    state=Lstate;
    semanticValue=LsemanticValue;
  }
}
