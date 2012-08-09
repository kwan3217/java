package org.kwansystems.automaton.part2.kyacc;

import java.util.*;

public abstract class ParsonsLR0 extends ParserGenerator {
  protected final Set calcOutgoing(Set<LR0Item> C) {
    //Find the outgoing symbols
    Set outgoing=new LinkedHashSet();
    for(LR0Item I:C) {
      if(!I.isCompleted()) {
        outgoing.add(I.next());
      }
    }
    return outgoing;
  }
  private void close(Set<LR0Item> thisV,Grammar G) {
    //Do the closure rule on the current V set
    boolean done=false;
    Set<LR0Item> newItems=new LinkedHashSet<LR0Item>();
    while(!done) {
      //For all items in the set so far
      newItems.clear();
      for(LR0Item I:thisV) {
        //If the dot precedes a nonterminal D, include all the initial D
        //items in the grammar in this set
        if(!I.isCompleted()) {
          Object D=I.next();
          if(G.isNonterminal(D)) {
            for(Production P:G.prods) {
              if(P.leftSide.equals(D)) newItems.add(new LR0Item(P,0));
            }
          }
        }
      }
      //If we haven't added any new items, we're done.
      done=!thisV.addAll(newItems);
    }
  }

  @Override
  public void GenerateShift(Grammar G, List context, List<Map<Object, LRTransition>> delta, String DataPrefix) {
    List<Set<LR0Item>> V=new LinkedList<Set<LR0Item>>();
    context.add(V);

    //Put the first item in the first state
    Set<LR0Item> C=new LinkedHashSet<LR0Item>();
    C.add(new LR0Item(G.prods.get(0),0));
    close(C,G);
    V.add(C);
    int currentState=0;
    while(currentState<V.size()) {
      delta.add(new LinkedHashMap<Object,LRTransition>());
      C=V.get(currentState);
      //Find the outgoing symbols
      Set outgoing=calcOutgoing(C);

      for(Object X:outgoing) {
        Set<LR0Item> nextV=new LinkedHashSet<LR0Item>();
        for(LR0Item I:C) {
          if(!I.isCompleted() && I.next().equals(X)) {
            LR0Item J=I.advance();
            nextV.add(J);
          }
        }
        close(nextV,G);
        int nextNum=V.indexOf(nextV);
        if(nextNum<0) {
          //The set is not there yet
          V.add(nextV);
          nextNum=V.size()-1; //Index of last thing added
        }
        if(G.isTerminal(X)) {
          delta.get(currentState).put(X,new LRTransition(LRTransition.LRTransitionType.S,nextNum));
        } else {
          delta.get(currentState).put(X,new LRTransition(LRTransition.LRTransitionType.G,nextNum));
        }
      }
      currentState++;
    }
  }
}
