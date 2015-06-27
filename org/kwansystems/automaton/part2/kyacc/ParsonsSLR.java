package org.kwansystems.automaton.part2.kyacc;

import java.util.*;
import org.kwansystems.automaton.*;
import org.kwansystems.automaton.tape.*;
import static org.kwansystems.automaton.part2.kyacc.ParsonsSLR.GType.*;

public class ParsonsSLR extends ParsonsLR0 {
  @Override
  public void GenerateReduce(Grammar G, List context, List<Map<Object, LRTransition>> delta, Map<Object, Integer> prec, Map<Object, Assoc> assoc, String DataPrefix) {
    List<Set<LR0Item>> V=(List<Set<LR0Item>>)context.get(0);

    //We need first and follow sets
    Map<Object,Boolean> nullable=G.computeNullable();
    Map<Object,Set> FIRST=G.computeFIRST(nullable);
    Map<Object,Set> FOLLOW=G.computeFOLLOW(FIRST,nullable);

    //Add reduce items
    for(int currentState=0;currentState<delta.size();currentState++) {
      for(LR0Item I:V.get(currentState)) {
        if(I.isCompleted()) {
          int prodNum=G.prods.indexOf(new Production(I));
          if(prodNum==0) {
            delta.get(currentState).put(G.eofSymbol, new LRTransition(LRTransition.LRTransitionType.ACC,0));
          } else {
            LRTransition newTrans=new LRTransition(LRTransition.LRTransitionType.R,prodNum);
            for(Object X:FOLLOW.get(I.leftSide)) {
              LRTransition oldTrans=delta.get(currentState).get(X);
              if(oldTrans!=null) {
                //There's a conflict. See what kind it is...
                if(oldTrans.type==LRTransition.LRTransitionType.S) {
                  //It's shift-reduce. Let's see if the precedence table helps us...
                  delta.get(currentState).put(X, resolvePrec(currentState,oldTrans,X,newTrans,G,prec,assoc));
                } else if(oldTrans.type==LRTransition.LRTransitionType.R) {
                  System.out.println(new ReduceReduceConflict(currentState,X,G.prods.get(oldTrans.to),G.prods.get(newTrans.to)).toString());
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
  protected enum GType {
    E,T,F,Q,R,S,
    i,p,m,t,d,l,r,a,b,c,e,eq,x,y,
    EOF {@Override public String toString() {return "$";}};
  }
  public static void main(String[] args) {
    ParserGenerator PSLR=new ParsonsSLR();
    List<Map<Object,LRTransition>> table;
    LRParser LR;
    Tape<Token> Ta;
/*
    Grammar Parsons4_3_1=new Grammar(E,EOF,new Production[] {
      new Production(E,new Object[] {E,p,T}),
      new Production(E,new Object[] {E,m,T}),
      new Production(E,new Object[] {T}),
      new Production(T,new Object[] {T,t,F}),
      new Production(T,new Object[] {T,d,F}),
      new Production(T,new Object[] {F}),
      new Production(F,new Object[] {l,E,r}),
      new Production(F,new Object[] {i})}
    );
    table=PSLR.Generate(Parsons4_3_1, "Data/Grammar/Parsons4_3_1");
    LR=new LRParser(Parsons4_3_1,table);
    System.out.println(LR);

    Ta=new SymbolTape(new Object[] {l,i,p,i,r,d,i}, EOF);
    LR.verbose=true;
    LR.parse(Ta);

    Grammar White=new Grammar(E,EOF,new Production[] {
      new Production(E,new Object[] {E,p,T}),
      new Production(E,new Object[] {T}),
      new Production(T,new Object[] {T,t,F}),
      new Production(T,new Object[] {F}),
      new Production(F,new Object[] {l,E,r}),
      new Production(F,new Object[] {i})}
    );
    table=PSLR.Generate(White, "Data/Grammar/White");
    LR=new LRParser(White,table);
    System.out.println(LR);

    Ta=new SymbolTape(new Object[] {i,p,i}, EOF);
    LR.verbose=true;
    LR.parse(Ta);
    */

    Grammar Parsons4_3_3=new Grammar(S,EOF,new Production[] {
      new Production(S,new Object[] {i,E,t,S}),
      new Production(S,new Object[] {i,E,t,S,e,S}),
      new Production(S,new Object[] {a}),
      new Production(S,new Object[] {b}),
      new Production(E,new Object[] {x}),
      new Production(E,new Object[] {y}),
	  });
    table=PSLR.Generate(Parsons4_3_3, new Object[][]{{e,t}}, new Assoc[] {Assoc.RIGHT},"Data/Grammar/Parsons4_3_3");
    LR=new LRParser(Parsons4_3_3,table);
    System.out.println(LR);

    Ta=new SymbolTape(new Object[] {i,x,t,i,y,t,a,e,b}, EOF);
    LR.verbose=true;
    LR.parse(Ta);
/*
    Grammar Parsons4_3_4=new Grammar(S,EOF,new Production[] {
      new Production(S,new Object[] {E,eq,E}),
      new Production(S,new Object[] {i}),
      new Production(E,new Object[] {E,p,i}),
      new Production(E,new Object[] {i})
	  });
    table=PSLR.Generate(Parsons4_3_4, "Data/Grammar/Parsons4_3_4");
    LR=new LRParser(Parsons4_3_4,table);
    System.out.println(LR);

    Ta=new SymbolTape(new Object[] {i,eq,i,p,i}, EOF);
    LR.parse(Ta);
 * */
  }
}
