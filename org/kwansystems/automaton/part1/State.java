package org.kwansystems.automaton.part1;

import java.util.*;

import org.kwansystems.automaton.Transition;


/**
 * Class representing a state and transition table row. This includes information
 * on whether the state is accepting, a mapping of input symbols to transitions, and what if
 * any Moore output to produce when this state is entered.
 *
 */
public class State<AlphabetType,StateNameType> {
//  private static final long serialVersionUID = 4901615316402865300L;
  private /*Hash*/Map<AlphabetType,Transition<StateNameType>> rowTable;
  public boolean Accept;
  public String comment;
  public int useCount=0;
  public Object MooreOutput;
  public State() {
    rowTable=new HashMap<AlphabetType,Transition<StateNameType>>();
  }
  public String toString() {
    return (MooreOutput!=null?","+MooreOutput:"")+(comment!=null?" - "+comment:"");
  }
  public Transition<StateNameType> get(AlphabetType a) {
    return rowTable.get(a);
  }
  public void put(AlphabetType a,Transition<StateNameType> T) {
    rowTable.put(a,T);
  }
  public Collection<Transition<StateNameType>> getTransitions() {
    return rowTable.values();
  }
  public Set<AlphabetType> getAlphabet() {
    return rowTable.keySet();
  }
}