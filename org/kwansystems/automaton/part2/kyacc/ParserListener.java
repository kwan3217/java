package org.kwansystems.automaton.part2.kyacc;

interface ParserListener {
  public void SRReducePrec(int row, Object S, Production R);
}
