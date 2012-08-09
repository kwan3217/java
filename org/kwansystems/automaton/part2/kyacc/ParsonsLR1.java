package org.kwansystems.automaton.part2.kyacc;

import java.util.*;
import org.kwansystems.automaton.*;
import org.kwansystems.automaton.tape.*;
import static org.kwansystems.automaton.part2.kyacc.ParsonsLR1.GType.*;

public class ParsonsLR1 extends ParserGenerator {
  protected final void close(Set<LR1Item> thisV, Map<Object,Set> FIRST, Map<Object,Boolean> nullable, Grammar G) {
    //Do the closure rule on the current V set
    boolean done=false;
    Set<LR1Item> newItems=new LinkedHashSet<LR1Item>();
    while(!done) {
      //For all items in the set so far
      newItems.clear();
      for(LR1Item I:thisV) {
        //If the dot precedes a nonterminal D, include all the initial D
        //items in the grammar in this set
        if(!I.isCompleted()) {
          Object B=I.next();
          if(G.isNonterminal(B)) {
            //We'll need gamma to do the FIRST set
            List gamma=I.advance().allAfter();
            //Build the lookahead set for the new item
            for(Production P:G.prods) {
              if(P.leftSide.equals(B)) {
                Set t=new LinkedHashSet();
                for(Object x:I.lookahead) {
                  List gammax=new ArrayList(gamma);
                  gammax.add(x);
                  t.addAll(G.computeFIRST(gammax,FIRST,nullable));
                }
                LR1Item J=new LR1Item(P,0,t);
                //Is there another item with the same core already in the set?
                boolean hasMatch=false;
                for(LR1Item K:thisV) {
                  if(K.core().equals(J.core())) {
                    hasMatch=true;
                    K.lookahead.addAll(t);
                    break;
                  }
                }
                if(!hasMatch) newItems.add(J);
              }
            }
          }
        }
      }
      //If we haven't added any new items, we're done.
      done=!thisV.addAll(newItems);
    }
  }
  protected final Set calcOutgoing(Set<LR1Item> C) {
    //Find the outgoing symbols
    Set outgoing=new LinkedHashSet();
    for(LR1Item I:C) {
      if(!I.isCompleted()) {
        outgoing.add(I.next());
      }
    }
    return outgoing;
  }
  @Override
  protected void GenerateShift(Grammar G, List context, List<Map<Object, LRTransition>> delta, String DataPrefix) {
    List<Set<LR1Item>> V=new LinkedList<Set<LR1Item>>();
    context.add(V);
    Map<Object,Boolean> nullable=G.computeNullable();
    Map<Object,Set> FIRST=G.computeFIRST(nullable);

    //Put the first item in the first state
    Set<LR1Item> C=new LinkedHashSet<LR1Item>();
    C.add(new LR1Item(G.prods.get(0),0,G.eofSymbol));
    close(C,FIRST,nullable,G);
    V.add(C);
    int currentState=0;
    while(currentState<V.size()) {
      delta.add(new LinkedHashMap<Object,LRTransition>());
      C=V.get(currentState);
      //Find the outgoing symbols
      Set outgoing=calcOutgoing(C);

      for(Object X:outgoing) {
        Set<LR1Item> nextV=advanceItems(C,X);
        close(nextV,FIRST,nullable,G);
        int nextNum=V.indexOf(nextV);
        if(nextNum<0) {
          //The set is not there yet
          V.add(nextV);
          nextNum=V.size()-1; //Index of last thing added
        } else {
          Set<LR1Item> oldV=V.get(nextNum);
          V.remove(nextNum);
          V.add(nextNum,oldV);
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
  public void GenerateReduce(Grammar G, List context, List<Map<Object, LRTransition>> delta, Map<Object, Integer> prec, Map<Object, Assoc> assoc, String DataPrefix) {
    List<Set<LR1Item>> V=(List<Set<LR1Item>>)context.get(0);

    //Add reduce items
    for(int currentState=0;currentState<delta.size();currentState++) {
      for(LR1Item I:V.get(currentState)) {
        if(I.isCompleted()) {
          int prodNum=G.prods.indexOf(new Production(I));
          if(prodNum==0) {
            delta.get(currentState).put(G.eofSymbol, new LRTransition(LRTransition.LRTransitionType.ACC,0));
          } else {
            LRTransition newTrans=new LRTransition(LRTransition.LRTransitionType.R,prodNum);
            for(Object X:I.lookahead) {
              LRTransition oldTrans=delta.get(currentState).get(X);
              if(oldTrans!=null) {
                //There's a conflict. See what kind it is...
                if(oldTrans.type==LRTransition.LRTransitionType.S) {
                  //It's shift-reduce. Let's see if the precedence table helps us...
                  delta.get(currentState).put(X, resolvePrec(currentState,oldTrans,X,newTrans,G,prec,assoc));
                } else if(oldTrans.type==LRTransition.LRTransitionType.R) {
                  if(oldTrans.to==newTrans.to) {
                    //Not really a conflict, newTrans is same as oldTrans
                  } else {
                    System.out.println(new ReduceReduceConflict(currentState,X,G.prods.get(oldTrans.to),G.prods.get(newTrans.to)).toString());
                  }
                }
              } else {
                //No conflict
                delta.get(currentState).put(X, newTrans);
              }
            }
          }
        }
      }
    }
  }

  protected Set<LR1Item> advanceItems(Set<LR1Item> C, Object X) {
    Set<LR1Item> nextV=new LinkedHashSet<LR1Item>();
    for(LR1Item I:C) {
      if(!I.isCompleted() && I.next().equals(X)) {
        LR1Item J=I.advance();
        nextV.add(J);
      }
    }
    return nextV;
  }
  protected enum GType {
    S,E,
    i,
    e {public String toString() {return "=";}},
    p {public String toString() {return "+";}},
    EOF {@Override public String toString() {return "$";}};
  }
  public static void main(String[] args) {
    ParserGenerator PLR1=new ParsonsLR1();
    List<Map<Object,LRTransition>> table;
    LRParser LR;
    Tape<Token> Ta;

    Grammar Parsons4_3_4=new Grammar(S,EOF,new Production[] {
      new Production(S,new Object[] {E,e,E}),
      new Production(S,new Object[] {i}),
      new Production(E,new Object[] {E,p,i}),
      new Production(E,new Object[] {i})
	  });
    table=PLR1.Generate(Parsons4_3_4, "Data/Grammar/Parsons4_3_4");
    LR=new LRParser(Parsons4_3_4,table);
    for(String S:PLR1.warnings) System.out.println(S);
    System.out.println(LR);

    Ta=new SymbolTape(new Object[] {i,e,i,p,i}, EOF);
    LR.verbose=true;
    LR.parse(Ta);
  }
}
