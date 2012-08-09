package org.kwansystems.automaton.part1.regexp;

import org.kwansystems.automaton.*;
import org.kwansystems.automaton.part1.NFA;
import org.kwansystems.automaton.part1.NFAState;

import java.util.*;

/** Regular Expression Fork. Given an array of token definitions, generate an NFA which 
 * accepts any of them. Different from a regular expression choice in that the generated NFA
 * will have multiple accept states, each marked with a different accept type, and therefore
 * not valid for further Thomson construction.  
 */
public class Fork<E> extends RegExpTree {
  private Collection<TokenDefinition<E>> children;
  public Fork(Collection<TokenDefinition<E>> Lchildren) {
    children=Lchildren;
  }
  public NFA<Character,String> getNFA(String StatePrefix) {
    int digits=Integer.toString(children.size()-1).length();
    NFA<Character,String> result=new NFA<Character,String>();
    result.addLetter(null); 
    Collection<Transition<String>> T=new LinkedList<Transition<String>>();
    int i=0;
    for(TokenDefinition<E> child:children) {
      
      String childPrefix=String.format("%s_F%0"+digits+"d", StatePrefix,i);
      NFA<Character,String> thisChild=child.definition.getNFA(childPrefix);
      String thisStart=thisChild.getStartState();
      String thisFinal=thisChild.firstAcceptState();
      T.add(new Transition<String>(thisStart));
      //Mark this child with the proper AcceptType
      NFAState<Character,String> thisFinalState=thisChild.getState(thisFinal);
      thisFinalState.MooreOutput=child.type;
      //Combine the alphabets
      for(Character a:thisChild.NFASigma) result.addLetter(a);   
      //Put all the states of the right machine into the left machine
      for(String s:thisChild.getStates()) {
        NFAState<Character,String> thisState=thisChild.getState(s);
        result.putState(s, thisState);
      }
      i++;
    }
    
    //New start state
    NFAState<Character,String> newState=new NFAState<Character,String>();
    newState.putCopy(null,T);
    result.putState(StatePrefix+"_Start", newState);    
    result.setStartState(StatePrefix+"_Start");

    result.checkNFA(false); //A fork is not supposed to be Thompson valid
    return result;
  }
  public String toString() {
  	StringBuffer result=new StringBuffer("");
	  for(TokenDefinition<E> t:children) {
	    RegExpTree r=t.definition;
	    result.append("{");result.append(r.toString());result.append("},");
	  }
	  if(result.length()>1) result.deleteCharAt(result.length()-1);
	  return result.toString();
  }
}
