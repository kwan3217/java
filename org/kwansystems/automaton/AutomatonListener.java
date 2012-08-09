package org.kwansystems.automaton;

import org.kwansystems.automaton.part1.DFA;

public interface AutomatonListener<AlphabetType,StateNameType> extends SymbolListener<AlphabetType> {
  public void ShowMachineState(DFA<AlphabetType,StateNameType> source);
  public void ShowTransition(DFA<AlphabetType,StateNameType> source, Transition<StateNameType> T);
  public void ShowComment(DFA<AlphabetType,StateNameType> source, String comment);
}
