package org.kwansystems.automaton.part1;

import java.io.*;

import org.kwansystems.automaton.Transition;
import org.kwansystems.automaton.tape.Tape;
import org.kwansystems.automaton.tape.TapeDisplay;

public class TextDFAListener<AlphabetType,StateNameType> extends DFAListener<AlphabetType,StateNameType> {
  public PrintStream ouf;
  public TextDFAListener(PrintStream Louf) {
    ouf=Louf;
  }
  public void ShowCurrentState(DFA<AlphabetType,StateNameType> source) {
    State<AlphabetType,StateNameType> state=source.get(source.getCurrentState());
    ouf.println("Current State: "+source.getCurrentState()+(state!=null?state.toString():""));
  }
  public void ShowTape(Tape<AlphabetType> tape) {
    TapeDisplay<AlphabetType> T=tape.getTapeDisplay();
    ouf.print("Tape:  ");
    for(AlphabetType c : T.tapeData) ouf.print(c);
    ouf.println();
    for(int i=0;i<T.pointers.length;i++) {
      ouf.print("       ");
      for(int j=0;j<T.pointers[i];j++) ouf.print(" ");
      ouf.println("^"+T.pointerNames[i]);
    }
  }
  public void ShowCurrentSymbol(AlphabetType c) {
    ouf.println("Read "+c);
  }
  public void ShowTransition(DFA<AlphabetType,StateNameType> source, Transition<StateNameType> T) {
    ouf.println("Transition: "+T);
  }
  public void ShowComment(DFA<AlphabetType,StateNameType> source, String comment) {
    ouf.println(comment);
  }
}
