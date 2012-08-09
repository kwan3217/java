package org.kwansystems.automaton.part2;

import java.util.*;
import java.io.*;

import org.kwansystems.automaton.part1.TextDFAListener;

public class TextPDAListener<AlphabetType,StateNameType> extends TextDFAListener<AlphabetType,StateNameType> implements PDAListener<AlphabetType,StateNameType> {
  public TextPDAListener(PrintStream Louf) {
    super(Louf);
  }
  public void ShowStack(Stack<AlphabetType> stack) {
    ouf.print("Stack: ");
    for(AlphabetType c : stack) ouf.print(c);
    ouf.println();
  }
  Object nextReadSource;
  public void ShowRead(PDA<AlphabetType,StateNameType> source, char c) {
    ouf.println(""+nextReadSource+" "+c);
  }
  public void OutputMoore(Object MooreOutput) {
    nextReadSource=MooreOutput;
    
  }

}
