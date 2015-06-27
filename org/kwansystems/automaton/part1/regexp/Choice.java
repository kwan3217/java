package org.kwansystems.automaton.part1.regexp;

import java.util.*;

import org.kwansystems.automaton.*;
import org.kwansystems.automaton.part1.NFA;
import org.kwansystems.automaton.part1.NFAState;

/** Regular expression part, representing a choice between sub-expressions. 
 */
public class Choice extends RegExpTree {
  RegExpTree children[];
  public Choice(RegExpTree Lchild1, RegExpTree Lchild2) {
    children=new RegExpTree[2];
    children[0]=Lchild1;
    children[1]=Lchild2;
  }
  public Choice(RegExpTree[] Lchildren) {
    children=Lchildren;
  }
  public NFA<Character,String> getNFA(String StatePrefix) {
    NFA<Character,String> result=new NFA<Character,String>();
    NFAState<Character,String> newState=new NFAState<Character,String>();

    //New final state
    newState=new NFAState<Character,String>();
    newState.Accept=true;
    String finalStateName=StatePrefix+"_Final";
    result.putState(finalStateName, newState);



    result.addLetter(null);
    int digits=Integer.toString(children.length).length();
    Collection<Transition<String>> startTrans=new LinkedList<Transition<String>>();
    for(int i=0;i<children.length;i++) {
      RegExpTree child=children[i];
      NFA<Character,String> thisNFA=child.getNFA(String.format(StatePrefix+"_C%0"+digits+"d",i));
      startTrans.add(new Transition<String>(thisNFA.getStartState()));
      for(Character a:thisNFA.NFASigma) result.addLetter(a); //Combine two alphabets  
      //Remember old final state name for later
      String thisFinal=thisNFA.firstAcceptState();
      //Child no longer has a final state
      thisNFA.getState(thisFinal).Accept=false;
      //Put all the states of child machine into the result machine
      for(String s:thisNFA.getStates()) {
        result.putState(s, thisNFA.getState(s));
      }
        
      //Point the old final states at the new final state
      NFAState<Character,String> oldThisFinalState=result.getState(thisFinal);
      Set<Transition<String>> nullTransition=oldThisFinalState.get(null);
      if(nullTransition==null) {
        nullTransition=new HashSet<Transition<String>>();
        oldThisFinalState.put(null, nullTransition);
      }
      nullTransition.add(new Transition<String>(finalStateName));
    }
    
    newState=new NFAState<Character,String>();
    newState.putCopy(null,startTrans);
    result.putState(StatePrefix+"_Start", newState);
    result.setStartState(StatePrefix+"_Start");
    
    result.checkNFA(true);
    return result;
  }
  public String toString() {
	  StringBuffer result=new StringBuffer("");
	  for(RegExpTree r:children) {
	    result.append("(");result.append(r.toString());result.append(")|");
	  }
	  if(result.length()>1) result.deleteCharAt(result.length()-1);
	  return result.toString();
  }
  public static void main(String[] args) {
    Choice C=new Choice(new Letter('a'),new Letter('b'));
    System.out.println(C.getNFA("C").DotNFATransitionTable("C"));
  }
}
