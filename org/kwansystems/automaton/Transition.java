package org.kwansystems.automaton;

import java.io.*;
import org.kwansystems.tools.*;

/**
 * Class representing an entry in a transition table. This includes information
 * on whether the transition is terminal, what the next state is, and what if
 * any Mealy output to produce when this transition is followed.
 *
 */
public class Transition<StateNameType> implements Serializable {
  private static final long serialVersionUID = -3291681952652778436L;
  public StateNameType nextState;
  public Object MealyOutput;
  public Termination term;
  public int useCount=0;
  public String toString() {
    switch(term) {
      case Continue:
        return nextState+(MealyOutput!=null?","+MealyOutput:"");
      default:
        return term.toString();
    }
  }
  public Transition() {
    term=Termination.Continue;
  }
  public Transition(StateNameType LnextState) {
    this();
    nextState=LnextState;
  }
  //Just make the hashCode deterministic. In other words, only dependent
  //on the field data, not things like what memory address we happen to be in today.
  public int hashCode() {
    int result=nextState.hashCode();
    if(MealyOutput!=null) result^=MealyOutput.toString().hashCode();
    if(term!=null) result^=term.toString().hashCode();
    return result;
  }
  public boolean equals(Object TT) {
    if(!(TT instanceof Transition)) return false;
    Transition<StateNameType> T=(Transition<StateNameType>)TT;
    if(!NullComparator.NC.equals(nextState,T.nextState)) return false;
    if(!NullComparator.NC.equals(MealyOutput, T.MealyOutput)) return false;
    if(term!=T.term) return false;
    return true;
  }
}