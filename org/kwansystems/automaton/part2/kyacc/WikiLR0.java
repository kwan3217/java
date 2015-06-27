package org.kwansystems.automaton.part2.kyacc;

import java.util.*;
import org.kwansystems.automaton.part2.kyacc.ParserGenerator.Assoc;
import static org.kwansystems.automaton.part2.kyacc.WikiLR0.GType.*;
import org.kwansystems.automaton.*;
import org.kwansystems.automaton.tape.*;

public class WikiLR0 extends ParsonsLR0 {

  @Override
  public void GenerateReduce(Grammar G, List context, List<Map<Object, LRTransition>> delta, Map<Object, Integer> prec, Map<Object, Assoc> assoc, String DataPrefix) {
    List<Set<LR0Item>> V=(List<Set<LR0Item>>)context.get(0);
    
    //We need first and follow sets
    Map<Object,Boolean> nullable=G.computeNullable();
    Map<Object,Set> FIRST=G.computeFIRST(nullable);
    Map<Object,Set> FOLLOW=G.computeFOLLOW(FIRST,nullable);
    //Set of all terminals including EOF
    Set Gp=new LinkedHashSet(G.terminal);
    Gp.add(G.eofSymbol);

    //Add reduce items
    for(int currentState=0;currentState<delta.size();currentState++) {
      for(LR0Item I:V.get(currentState)) {
        if(I.isCompleted()) {
          int prodNum=G.prods.indexOf(new Production(I));
          if(prodNum==0) {
            delta.get(currentState).put(G.eofSymbol, new LRTransition(LRTransition.LRTransitionType.ACC,0));
          } else {
            LRTransition newTrans=new LRTransition(LRTransition.LRTransitionType.R,prodNum);
            for(Object X:Gp) {
              LRTransition oldTrans=delta.get(currentState).get(X);
              if(oldTrans!=null) {
                //There's a conflict. See what kind it is...
                if(oldTrans.type==LRTransition.LRTransitionType.S) {
                  //It's shift-reduce. Let's see if the precedence table helps us...
                  delta.get(currentState).put(X, resolvePrec(currentState,oldTrans,X,newTrans,G,prec,assoc));
                } else if(oldTrans.type==LRTransition.LRTransitionType.R) {
                  throw new ReduceReduceConflict(currentState,X,G.prods.get(oldTrans.to),G.prods.get(newTrans.to));
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
    E,B,
    a {public String toString() {return "0";}},
    b {public String toString() {return "1";}},
    t {public String toString() {return "*";}},
    p {public String toString() {return "+";}},
    EOF {@Override public String toString() {return "$";}};
  }
  public static void main(String[] args) {
    ParserGenerator WLR0=new WikiLR0();
    ParserGenerator PSLR=new ParsonsSLR();
    List<Map<Object,LRTransition>> table;
    LRParser LR;
    Tape<Token> Ta;

    Grammar WikiGrammar=new Grammar(E,EOF,new Production[] {
      new Production(E,new Object[] {E,t,B}),
      new Production(E,new Object[] {E,p,B}),
      new Production(E,new Object[] {B}),
      new Production(B,new Object[] {a}),
      new Production(B,new Object[] {b})
	  });
    table=WLR0.Generate(WikiGrammar,"Data/Grammar/WikiGrammar");
    LR=new LRParser(WikiGrammar,table);
    System.out.println(LR);

//    Ta=new SymbolTape(new Object[] {a,p,a}, EOF);
//    LR.verbose=true;
//    LR.parse(Ta);

    table=PSLR.Generate(WikiGrammar,"Data/Grammar/WikiGrammar");
    LR=new LRParser(WikiGrammar,table);
    System.out.println(LR);

//    Ta=new SymbolTape(new Object[] {a,p,a}, EOF);
//    LR.verbose=true;
//    LR.parse(Ta);
  }
}
