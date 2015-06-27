package org.kwansystems.automaton.part1.regexp;

import java.util.*;
import org.kwansystems.automaton.*;
import org.kwansystems.automaton.part1.DFA;
import org.kwansystems.automaton.part1.NFA;
import org.kwansystems.automaton.part1.NFAState;

/** Special case of RegExpChoice, where each character in a string is one choice. Special case with one
 * start state, one final state, and transition from start state to final state for each letter in the
 * string, and no other transitions. OK 4/3/2008 8:20pm
 */
public class ChoiceString extends RegExpTree {
  public static String Range(char Letter0,char Letter2) {
    StringBuffer result=new StringBuffer("");
    for(char c=Letter0;c<=Letter2;c++) {
      result.append(c);
    }
    return result.toString();
  }
  String children;
  public ChoiceString(char letter0, char letter2) {
    this(Range(letter0,letter2));
  }
  public ChoiceString(String Lchildren) {
    children=Lchildren;
  }
  public NFA<Character,String> getNFA(String StatePrefix) {
    NFA<Character,String> result=new NFA<Character,String>();
    for(int i=0;i<children.length();i++) {
      char child=children.charAt(i);
      result.addLetter(child);
    }
    
    //New final state
    NFAState<Character,String> newState=new NFAState<Character,String>();
    newState.Accept=true;
    result.putState(StatePrefix+"_Final", newState);    

    //New start state
    newState=new NFAState<Character,String>();
    result.putState(StatePrefix+"_Start", newState);
    
    for(Character child:result.NFASigma) {
      newState.put(child,new Transition<String>(StatePrefix+"_Final"));
    }

    result.setStartState(StatePrefix+"_Start");
    
    result.checkNFA(true);
    return result;
  }
  public String toString() {
	Set<Character> edges=new TreeSet<Character>();
	for(int i=0;i<children.length();i++) edges.add(children.charAt(i));
	return "["+DFA.makeEdgeLabel(edges, "")+"]";
  }
}
