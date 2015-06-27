package org.kwansystems.automaton.part2.kyacc;

import java.util.*;


/**
 * Simple LR Parser item. An item is a production decorated with a marker showing
 * progress through the production.
 *
 */
public class LR1Item extends LR0Item {
  public final Set lookahead;
  public LR1Item(Object LleftSide, Object[] LrightSide, int LdotPosition, Set Llookahead) {
    super(LleftSide, LrightSide,LdotPosition);
    lookahead=new LinkedHashSet(Llookahead);
  }
  public LR1Item(Object LleftSide, List LrightSide, int LdotPosition,Set Llookahead) {
    super(LleftSide, LrightSide,LdotPosition);
    lookahead=new LinkedHashSet(Llookahead);
  }
  public LR1Item(Production P, int LdotPosition,Set Llookahead) {
    this(P.leftSide,P.rightSide,LdotPosition,Llookahead);
  }
  public LR1Item(Production P, int LdotPosition,Object Llookahead) {
    super(P.leftSide,P.rightSide,LdotPosition);
    lookahead=new LinkedHashSet();
    lookahead.add(Llookahead);
  }
  public LR1Item(LR0Item core, Set Llookahead) {
    super(core);
    lookahead=new LinkedHashSet(Llookahead);
  }
  public LR1Item(LR1Item Li, LR1Item Lj) {
    super(Li);
    if(!Li.core().equals(Lj.core())) throw new IllegalArgumentException("Cores don't match");
    lookahead=new LinkedHashSet(Li.lookahead);
    lookahead.addAll(Lj.lookahead);
  }
  public LR1Item advance() {
    return new LR1Item(super.advance(),lookahead);
  }
  public String toString() {
    StringBuffer result=new StringBuffer(super.toString());
    result.append("; ");
    result.append(lookahead.toString());
    return result.toString();
  }
  //Just make the hashCode deterministic. In other words, only dependent
  //on the field data, not things like what memory address we happen to be in today.
  public int hashCode() {
    return super.hashCode()^lookahead.hashCode();
  }
  public boolean equals(Object TT) {
    if(!(TT instanceof LR1Item)) return false;
    LR1Item T=(LR1Item)TT;
    if(!super.equals(T)) return false;
    if(!lookahead.equals(T.lookahead)) return false;
    return true;
  }
  public LR0Item core() {
    return new LR0Item(this);
  }
}
