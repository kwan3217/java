package org.kwansystems.automaton.part2.kyacc;

import java.util.*;
import org.kwansystems.automaton.*;
import org.kwansystems.automaton.part1.*;
import org.kwansystems.automaton.tape.*;
import org.kwansystems.tools.*;
import static org.kwansystems.automaton.part1.DFA.*;
import static org.kwansystems.automaton.part2.kyacc.MogensenSLR.GType.*;

public class MogensenSLR extends ParserGenerator {
  /**
   * Produce an SLR(1) parse table from the grammar. This is a (S)imple
   * (L)eft-to-right reading (R)ightmost derivation parser which looks ahead
   * up to (1) symbol. Precedence and associativity is used only when necessary
   * to resolve shift/reduce conflicts.
   * <p>
   * This parse table can be used by a generic LR table driver function, which
   * does not change with the grammar. The driver does not care that this is an
   * SLR(1) table, only that it is an LR table.
   * <p>
   * The driver does not have to be Java code, although
   * @see org.kwansystems.automaton.part2.kyacc.LRParser for a driver in Java.
   * If the driver is in another language, you will need to write all the
   * semantic actions in that other language, and write your driver such that it
   * knows how to call the correct action and pass it the handle. For example,
   * in C or Pascal, an array of function pointers to functions taking arrays of
   * tokens may be appropriate. You will also have to write out this table in
   * some form that the driver can read it.
   * @param DataPrefix Path and filename prefix for temporary debug files that
   * the algorithm produces.
   * @return A parse table. This is in the form of a map from integers to states,
   * each state of which is a map from symbols to transitions.
   */

  @Override
  public void GenerateShift(Grammar G, List context, List<Map<Object, LRTransition>> delta, String DataPrefix) {
    NFA<Object,LR0Item> parseNFA=new NFA<Object,LR0Item>();

    //2. Make an NFA branch for each production
    for(int i=0;i<G.prods.size();i++) {
      Production P=G.prods.get(i);
      LR0Item item0=new LR0Item(P,0);
      if(i==0) {
        parseNFA.NFAStartState=item0;
      }
      for(int j=1;j<=P.rightSide.size();j++) {
        NFAState<Object,LR0Item> state0=new NFAState<Object,LR0Item>();
        LR0Item item1=new LR0Item(P,j);
        parseNFA.NFASigma.add(P.rightSide.get(j-1));
        state0.addTrans(P.rightSide.get(j-1), new Transition<LR0Item>(item1));
        if(G.isNonterminal(P.rightSide.get(j-1))) {
          Object NT=P.rightSide.get(j-1);
          //Add epsilon transitions
          for(Production P2:G.prods) {
            if(P2.leftSide==NT) {
              LR0Item I=new LR0Item(P2,0);
              parseNFA.NFASigma.add(null);
              state0.addTrans(null,new Transition<LR0Item>(I));
            }
          }
        }
        parseNFA.putState(item0, state0);
        item0=item1;
      }
      NFAState<Object,LR0Item> state0=new NFAState<Object,LR0Item>();
      state0.Accept=true;
      state0.MooreOutput=i;
      parseNFA.putState(item0, state0);
    }
    printDebug(DataPrefix+"GrammarNFAS.dot",parseNFA.DotNFATransitionTable("Grammar"));

    DFA<Object,Set<LR0Item>> parseDFA=parseNFA.Kleene(new DFA<Object,Set<LR0Item>>(null) {
      private static final long serialVersionUID=-2660151620695037739L;
      public Set<LR0Item> mergeStateNames(Set<LR0Item> state1, Set<LR0Item> state2) {
        Set<LR0Item> newStateName=new LinkedHashSet<LR0Item>();
        newStateName.addAll(state1);
        newStateName.addAll(state2);
        return newStateName;
      }
    });

    parseNFA=null;
    printDebug(DataPrefix+"GrammarDFAS.dot",parseDFA.DotTransitionTable("Grammar"));

    //You could put an optimizer in here, if you kept track of which states were merged
    //parseDFA.optimize(Crash);

    Map<Set<LR0Item>,Integer> stateNum=parseDFA.stateNumbering();
    Map<Integer,Set<LR0Item>> invStateNum=parseDFA.invStateNumbering(stateNum);
    DFA<Object,Integer> numDFA=parseDFA.numberStates(stateNum);
    parseDFA=null;
    printDebug(DataPrefix+"GrammarDFA.dot",numDFA.DotTransitionTable("Grammar"));
    printDebug(DataPrefix+"GrammarDFA.txt",numDFA.WikiTransitionTable());

    List MooreOutput=new ArrayList();
    for(int i=0;i<numDFA.numStates;i++) {
      MooreOutput.add(numDFA.get(i).MooreOutput);
    }

    //Write the shift actions and goto part of the table
    List<Map<Object,LRTransition>> result=new ArrayList<Map<Object,LRTransition>>();
    for(int I=0;I<delta.size();I++) {
      Map<Object,LRTransition> oldRow=delta.get(I);
      Map<Object,LRTransition> newRow=new LinkedHashMap<Object,LRTransition>();
      result.add(newRow);
      for(Object S:delta.get(I).keySet()) {
        if(G.isTerminal(S)) {
          newRow.put(S,new LRTransition(LRTransition.LRTransitionType.S,oldRow.get(S).to));
        } else {
          newRow.put(S,new LRTransition(LRTransition.LRTransitionType.G,oldRow.get(S).to));
        }
      }
    }
    printDebug(DataPrefix+"GrammarParse1.txt",LRParser.toString(result));
    context.add(invStateNum);
    context.add(MooreOutput);
  }

  @Override
  public void GenerateReduce(Grammar G, List context, List<Map<Object, LRTransition>> delta, Map<Object, Integer> prec, Map<Object, Assoc> assoc, String DataPrefix) {
    //We need first and follow sets
    Map<Object,Boolean> nullable=G.computeNullable();
    Map<Object,Set> FIRST=G.computeFIRST(nullable);
    Map<Object,Set> FOLLOW=G.computeFOLLOW(FIRST,nullable);
    Map<Integer,Set<LR0Item>> invStateNum=(Map<Integer,Set<LR0Item>>)context.get(0);
    List MooreOutput=(List)context.get(1);
    //Write the reduce actions in the table. For each state, if it contains
    //a completed production, you put in a reduce on that production for each
    //symbol in the FOLLOW set of the left side nonterminal for that production.
    for(int I=0;I<delta.size();I++) {
      Set<LR0Item> itemSet=invStateNum.get(I);
      Map<Object,LRTransition> row=delta.get(I);
      for(LR0Item J:itemSet) {
        if(J.isCompleted()) {
          Set follow=FOLLOW.get(J.leftSide);
          Integer pn=(Integer)MooreOutput.get(I);
          LRTransition newTrans;
          if(pn==0) {
        	  newTrans=new LRTransition(LRTransition.LRTransitionType.ACC);
          } else {
          	newTrans=new LRTransition(LRTransition.LRTransitionType.R,pn);
          }
          for(Object s:follow) {
            LRTransition oldTrans=row.get(s);
        	  if(oldTrans!=null) {
              //Uh oh, conflict!
              if(oldTrans.type==oldTrans.type.S) {
                //See if the precedence table helps us out
                row.put(s,resolvePrec(I, oldTrans, s, newTrans, G, prec, assoc));
              } else {
                System.out.println("Warning: Reduce-Reduce Conflict");//throw new ReduceReduceConflict(prods.get(oldTrans.to),prods.get(newTrans.to));
              }
        	  } else {
              //Luke... There IS no conflict.
              row.put(s,newTrans);
            }
          }
        }
      }
    }
    printDebug(DataPrefix+"GrammarParse1.txt",LRParser.toString(delta));
  }
  protected enum GType {
    E,T,F,Q,R,S,
    i,p,m,t,d,l,r,a,b,c,eq,
    EOF {@Override public String toString() {return "$";}};
  }
  public static void main(String[] args) throws AutomatonException {
    ParserGenerator MSLR=new MogensenSLR();
    List<Map<Object,LRTransition>> table;
    LRParser LR;
    Tape<Token> Ta;
    Grammar Mogensen3_9=new Grammar(T,EOF,new Production[] {
      new Production(T,new Object[] {R}),
      new Production(T,new Object[] {a,T,c}),
      new Production(R,new Object[] {}),
      new Production(R,new Object[] {b,R})}
    );
    table=MSLR.Generate(Mogensen3_9, "Data/Grammar/Morgensen3_9");
    LR=new LRParser(Mogensen3_9,table);

    Ta=new SymbolTape(new Object[] {a,a,b,b,b,c,c}, EOF);
    System.out.println(LR.toString());
    LR.parse(Ta);

    Grammar Mogensen3_2=new Grammar(E,EOF,new Production[] {
      new Production(E,new Object[] {E,p,E}),
      new Production(E,new Object[] {E,m,E}),
      new Production(E,new Object[] {E,t,E}),
      new Production(E,new Object[] {E,d,E}),
      new Production(E,new Object[] {l,E,r}),
      new Production(E,new Object[] {i})
	  });
//     new Object[][] {{p,m},{t,d}}, //Precedence table
//     new Assoc[] {Assoc.LEFT,Assoc.LEFT}  //Association table
    table=MSLR.Generate(Mogensen3_2,"Data/Grammar/Mogensen3_2");
    LR=new LRParser(Mogensen3_2,table);
    Ta=new SymbolTape(new Object[] {i,p,i,t,i}, EOF);
    System.out.println(LR.toString());
    LR.parse(Ta);

  }

}
