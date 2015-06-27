package org.kwansystems.automaton.part2.kyacc;

import java.util.*;


/**
 * Simple LR Parser item. An item is a production decorated with a marker showing
 * progress through the production.
 *
 */
public class LR0Item extends Production {
  private final int dotPosition;
  public LR0Item(Object LleftSide, Object[] LrightSide, int LdotPosition) {
    super(LleftSide, LrightSide);
    dotPosition=LdotPosition;
  }
  public LR0Item(Object LleftSide, List LrightSide, int LdotPosition) {
    super(LleftSide, LrightSide);
    dotPosition=LdotPosition;
  }
  public LR0Item(Production P, int LdotPosition) {
    this(P.leftSide,P.rightSide,LdotPosition);
  }
  public LR0Item(LR0Item R) {
    this(R.leftSide,R.rightSide,R.dotPosition);
  }
  public String toString() {
    StringBuffer result=new StringBuffer(leftSide.toString()+"->");
    for(int i=0;i<rightSide.size();i++) {
      if(dotPosition==i) result.append("."); else result.append(" ");
      result.append(rightSide.get(i).toString());
    }
    if(dotPosition==rightSide.size()) {
      result.append("."); 
    } else {
      result.append(" ");
    }
    return result.toString();
  }
  //Just make the hashCode deterministic. In other words, only dependent
  //on the field data, not things like what memory address we happen to be in today.
  public int hashCode() {
    return leftSide.toString().hashCode()^rightSide.hashCode()^dotPosition;
  }
  public boolean equals(Object TT) {
    if(!(TT instanceof LR0Item)) return false;
    LR0Item T=(LR0Item)TT;
    if(leftSide!=T.leftSide) return false;
    if(!rightSide.equals(T.rightSide)) return false;
    if(leftSide!=T.leftSide) return false;
    if(dotPosition!=T.dotPosition) return false;
    return true;
  }
  public boolean isCompleted() {
    return dotPosition==rightSide.size();
  }

  LR0Item advance() {
    return new LR0Item(this,dotPosition+1);
  }

  List<Object> allAfter() {
    if(isCompleted()) return new ArrayList();
    return rightSide.subList(dotPosition,rightSide.size()-1);
  }

  Object next() {
    return rightSide.get(dotPosition);
  }
  Object prev() {
    return rightSide.get(dotPosition-1);
  }
  
}
