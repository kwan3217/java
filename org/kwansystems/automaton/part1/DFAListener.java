package org.kwansystems.automaton.part1;

import org.kwansystems.automaton.AutomatonListener;
import org.kwansystems.automaton.Transition;
import org.kwansystems.automaton.tape.Tape;

public abstract class DFAListener<AlphabetType,StateNameType> implements AutomatonListener<AlphabetType,StateNameType> {

  public void ShowMachineState(DFA<AlphabetType,StateNameType> source) {
    ShowTape(source.getTape());
    ShowCurrentState(source);
 
  }
  public abstract void ShowCurrentSymbol(AlphabetType c);
  public abstract void ShowTape(Tape<AlphabetType> tape);
  public abstract void ShowCurrentState(DFA<AlphabetType,StateNameType> source);
  public abstract void ShowTransition(DFA<AlphabetType,StateNameType> Source, Transition<StateNameType> T);
}
