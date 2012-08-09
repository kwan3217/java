package org.kwansystems.automaton.part1.regexp;

import org.kwansystems.automaton.*;
import org.kwansystems.automaton.part1.NFA;
import org.kwansystems.automaton.part1.NFAState;

/** Checked 4/3/2008 10:48pm
 * @author chrisj
 *
 */
public class Letter extends RegExpTree {
  Character letter;
  public Letter(Character Lletter) {
    letter=Lletter;
  }
  public NFA<Character,String> getNFA(String StatePrefix) {
    NFA<Character,String> result=new NFA<Character,String>();
    NFAState<Character,String> newState;
    result.addLetter(letter);

    newState=new NFAState<Character,String>();
    newState.put(letter,new Transition<String>(StatePrefix+"_Final"));
    result.putState(StatePrefix+"_Start", newState);    
    result.setStartState(StatePrefix+"_Start");
    
    newState=new NFAState<Character,String>();
    newState.Accept=true;
    result.putState(StatePrefix+"_Final", newState);
    
    result.checkNFA(true);

    return result;
  }
  public String toString() {
  	return letter.toString();
  }
  public static void main(String[] args) {
    Letter L=new Letter('a');
    System.out.println(L.getNFA("L").DotNFATransitionTable("L"));
  }
}
