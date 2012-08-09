package org.kwansystems.automaton.part1.regexp;

import org.kwansystems.automaton.*;
import org.kwansystems.automaton.part1.NFA;

public class Concat extends RegExpTree {
  RegExpTree[] children;
  public Concat(RegExpTree[] Lchildren) {
    children=Lchildren;
  }
  public Concat(RegExpTree Lchild1, RegExpTree Lchild2) {
    children=new RegExpTree[2];
    children[0]=Lchild1;
    children[1]=Lchild2;
  }
  public NFA<Character,String> getNFA(String StatePrefix) {
    
    int digits=Integer.toString(children.length).length();
    NFA<Character,String> result=children[0].getNFA(String.format(StatePrefix+"_K%0"+digits+"d",0));
    
    for(int i=1;i<children.length;i++) {
      RegExpTree child=children[i];
      NFA<Character,String> thisNFA=child.getNFA(String.format(StatePrefix+"_K%0"+digits+"d",i));
      String oldFinal=result.firstAcceptState();
      for(Character a:thisNFA.NFASigma) result.addLetter(a); //Combine two alphabets  
      //old final state no longer final
      result.getState(oldFinal).Accept=false;
        
      //Merge the final state on the left with the start state on the right 
      String bridge=thisNFA.getStartState();
      result.renameNFAState(oldFinal,bridge); //Rename it to the same as the right start state
      result.removeNFAState(thisNFA.getStartState());  //Now we can drop the state. Transitions will be 
                                                       //dangling momentarily, but we'll fix that soon.
                                                       //We are dropping the right start state from the left machine.

      //Put all the states of the right machine into the left machine. The dangling pointers in the left machine
      //will now point to the start state of the right machine, and no longer dangle.
      for(String s:thisNFA.getStates()) {
        result.putState(s, thisNFA.getState(s));
      }
    }
    
    result.checkNFA(true);
    return result;
  }
  public String toString() {
	  StringBuffer result=new StringBuffer("");
	  for(RegExpTree r:children) {
	    result.append("(");result.append(r.toString());result.append(")");
	  }
	  return result.toString();
  }
  public static void main(String[] args) {
    Concat C=new Concat(new Letter('a'),new Letter('b'));
    System.out.println(C.getNFA("P").DotNFATransitionTable("C"));
  }
}
