package org.kwansystems.automaton.part1.regexp;

import org.kwansystems.automaton.*;
import org.kwansystems.automaton.part1.NFA;
import org.kwansystems.automaton.part1.NFAState;
import org.kwansystems.tools.*;
import java.util.*;

public class Plus extends RegExpTree {
  RegExpTree child;
  public Plus(RegExpTree Lchild) {
    child=Lchild;
  }
  public NFA<Character,String> getNFA(String StatePrefix) {
    NFA<Character,String> result=child.getNFA(StatePrefix+"_P");
    result.addLetter(null); //If it already had a null, no biggie.

    //Just add a null transition from final back to start
    String thisFinal=result.firstAcceptState();
    NFAState<Character,String> thisFinalState=result.getState(thisFinal);
    thisFinalState.Accept=false;
    Set<Transition<String>> nullTransition=thisFinalState.get(null);
    if(nullTransition==null) {
      nullTransition=new TreeSet<Transition<String>>(NullComparator.NC);
      thisFinalState.put(null, nullTransition);
    }
    nullTransition.add(new Transition<String>(result.getStartState()));
    nullTransition.add(new Transition<String>(StatePrefix+"_Final"));
    
    NFAState<Character,String> newState=new NFAState<Character,String>();
    newState.Accept=true;
    result.putState(StatePrefix+"_Final", newState);
    
    newState=new NFAState<Character,String>();
    newState.put(null, new Transition<String>(result.getStartState()));
    result.putState(StatePrefix+"_Start", newState);
    result.setStartState(StatePrefix+"_Start");
    
    result.checkNFA(true);
    return result;
  }
  public String toString() {
	  return "("+child.toString()+")+";
  }
  public static void main(String[] args) {
    Plus C=new Plus(new Letter('a'));
    System.out.println(C.getNFA("P"));
  }
}
