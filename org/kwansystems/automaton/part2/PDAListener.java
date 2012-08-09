package org.kwansystems.automaton.part2;

import java.util.*;

import org.kwansystems.automaton.AutomatonListener;
import org.kwansystems.automaton.part1.MooreListener;


public interface PDAListener<AlphabetType,StateNameType> extends AutomatonListener<AlphabetType,StateNameType>, MooreListener {
  public void ShowStack(Stack<AlphabetType> stack);

}
