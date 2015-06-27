package org.kwansystems.automaton.part2.kyacc;

import java.util.*;
import org.kwansystems.automaton.*;

public class Production  {
  public Object leftSide;
  public List rightSide;
  public Token act(Token[] rightSide) {
    if (rightSide.length>0 && rightSide[0]!=null) return new Token(this.leftSide,rightSide[0].value);
    return new Token(this.leftSide,null);
  }
  public Production(Production P) {
    leftSide=P.leftSide;
	  rightSide=new ArrayList();
	  if(P.rightSide!=null) rightSide.addAll(P.rightSide);
  }
  public Production(Object LleftSide, List LrightSide) {
	  leftSide=LleftSide;
	  rightSide=new ArrayList();
	  if(LrightSide!=null) rightSide.addAll(LrightSide);
  }
  public Production(Object LleftSide, Object[] LrightSide) {
	  this(LleftSide,(List)null);
	  rightSide=new ArrayList(LrightSide.length);
	  for(Object C:LrightSide) rightSide.add(C);
  }
  public String toString() {
    String result=leftSide.toString()+"->";
    if(rightSide.size()>0) {
      for(int i=0;i<rightSide.size();i++) {
        result+=rightSide.get(i).toString()+" ";
      }
    } else {
      result+="epsilon";
    }
    return result;
  }
  //Just make the hashCode deterministic. In other words, only dependent
  //on the field data, not things like what memory address we happen to be in today.
  public int hashCode() {
    return leftSide.toString().hashCode()^rightSide.hashCode();
  }
  public boolean equals(Object TT) {
    if(!(TT instanceof Production)) return false;
    Production T=(Production)TT;
    if(leftSide!=T.leftSide) return false;
    if(!rightSide.equals(T.rightSide)) return false;
    return true;
  }
}
