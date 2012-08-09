package org.kwansystems.automaton.part2.kyacc;

import java.util.*;
import org.kwansystems.automaton.*;
import org.kwansystems.automaton.tape.*;
import static org.kwansystems.automaton.part2.kyacc.ParsonsLALR1.GType.*;

/**
 * Brute force LALR(1) parser generator. Given a canonical LR(1) state table V,
 * merge the states where all the core sets are alike.
 * @author chrisj
 */
public class ParsonsLALR1 extends ParsonsLR1 {
  protected Set<LR1Item> merge(Set<LR1Item> oldV, Set<LR1Item> nextV) {
    Set<LR1Item> newV=new LinkedHashSet<LR1Item>();
    for(LR1Item R:oldV) {
      for(LR1Item S:nextV) {
        if(R.core().equals(S.core())) {
          newV.add(new LR1Item(R,S));
          break;
        }
      }
    }
    System.out.println("Merging "+oldV+" with "+nextV+", result is "+newV);
    return newV;
  }
  protected int checkIfThere(Set<LR1Item> nextV, List<Set<LR1Item>> V) {
    for(int i=0;i<V.size();i++) {
      Set<LR1Item> oldV=V.get(i);

      if(oldV.size()==nextV.size()) {
        boolean foundAll=true;
        for(LR1Item R:oldV) {
          boolean found=false;
          for(LR1Item S:nextV) {
            if(R.core().equals(S.core())) {
              found=true;
              break;
            }
          }
          if(!found) {
            foundAll=false;
          }
        }
        if(foundAll) return i;
      }
    }
    return -1;
  }
  protected void GenerateShift(Grammar G, List context, List<Map<Object, LRTransition>> delta, String DataPrefix) {
    super.GenerateShift(G, context, delta, DataPrefix);
    List<Set<LR1Item>> V=(LinkedList<Set<LR1Item>>)context.get(0);
    List<Set<LR1Item>> newV= new LinkedList<Set<LR1Item>>();
    List<Integer> newnumber=new LinkedList<Integer>();
    List<Integer> drop=new ArrayList<Integer>();
    Map<Integer,Set<Integer>> mergeMap=new LinkedHashMap<Integer,Set<Integer>>();
    for(int i=0;i<V.size();i++) {
      Set<LR1Item> thisV=V.get(i);
      int j=checkIfThere(thisV,newV);
      if(j>=0) {
        newV.set(j, merge(newV.get(j),thisV));
        newV.add(merge(newV.get(j),thisV));
        drop.add(i);
        newnumber.add(j);
      } else {
        newV.add(thisV);
        newnumber.add(i);
      }
    }
    System.out.println(drop);
  }
  protected enum GType {
    S,E,
    i,
    e {public String toString() {return "=";}},
    p {public String toString() {return "+";}},
    EOF {@Override public String toString() {return "$";}};
  }
  public static void main(String[] args) {
    ParserGenerator PLALR1=new ParsonsLALR1();
    List<Map<Object,LRTransition>> table;
    LRParser LR;
    Tape<Token> Ta;

    Grammar Parsons4_3_4=new Grammar(S,EOF,new Production[] {
      new Production(S,new Object[] {E,e,E}),
      new Production(S,new Object[] {i}),
      new Production(E,new Object[] {E,p,i}),
      new Production(E,new Object[] {i})
	  });
    table=PLALR1.Generate(Parsons4_3_4, "Data/Grammar/Parsons4_3_4");
    LR=new LRParser(Parsons4_3_4,table);
    System.out.println(LR);

    Ta=new SymbolTape(new Object[] {i,e,i,p,i}, EOF);
    LR.verbose=true;
    LR.parse(Ta);
  }
}
