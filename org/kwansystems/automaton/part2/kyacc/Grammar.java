package org.kwansystems.automaton.part2.kyacc;

import java.util.*;
import java.io.*;

import org.kwansystems.automaton.*;
import org.kwansystems.automaton.part1.*;
import org.kwansystems.automaton.tape.*;
import org.kwansystems.tools.*;

import static org.kwansystems.automaton.part1.DFA.*;

/**
 * Class representing a context-free grammar. Includes methods to compute certain
 * attributes of the grammar such as FIRST and FOLLOW sets, and a method to compute
 * an LR parse table from the grammar, in a form which can directly be used by 
 * LRParser. In other words, this is the hard part of YACC. Ultimately, when the 
 * need to do so is proven, it will do so using the LALR(1) parser generator 
 * algorithm, and will be able to consider operator precedence when necessary. 
 * It currently uses the Simple LR parse table algorithm.
 * <p>
 * This was called Grammar, but nothing else was called Kyacc yet, and this
 * is the closest to it.
 * <p>
 * This class now includes precedence and associativity tables. They shouldn't be
 * needed for unambiguous SLR grammars, but if there is a shift-reduce conflict,
 * the precededence and associativity are used to resolve it. If the tables are
 * not set, SLR table generation will fail, and this is correct. Precedence
 * tables are ONLY used to disambiguate, so if the grammar unambiguously implies
 * different precedence than the precedence tables say, the grammar wins.
 * <p>
 * Precedence is an ugly hack.
 */
public class Grammar implements Serializable {
  private static final long serialVersionUID = 5587955236735102167L;
  /**
   * List of productions. One of the three things which define the entire
   * grammar. Everything else is derivable from these.
   */
  public List<Production> prods;
  /**
   * Grammar start symbol. One of the three things which define the entire
   * grammar. Everything else is derivable from these.
   */
  public Object startSymbol;
  /**
   * Grammar End of Stream symbol. One of the three things which define the
   * entire grammar. Everything else is derivable from these.
   */
  public Object eofSymbol;
  /**
   * Set of grammar nonterminals, calculated by looking at all the production left sides
   */
  public Set nonterminal;
  public Set terminal;
  public boolean isTerminal(Object S) {
    return !nonterminal.contains(S);
  }
  public boolean isNonterminal(Object S) {
    return nonterminal.contains(S);
  }
  
  /**
   * Construct a grammar
   * @param LstartSymbol 
   * @param Lprods Array of productions in this grammar
   * @param LeofSymbol
   */
  public Grammar(Object LstartSymbol, Object LeofSymbol, Production[] Lprods) {
    startSymbol=LstartSymbol;
    eofSymbol=LeofSymbol;
    prods=new ArrayList<Production>();
    nonterminal=new LinkedHashSet();
    terminal=new LinkedHashSet();
    for(Production L:Lprods){
      if(L!=null) {
        if(L.leftSide==null) throw new IllegalArgumentException("Epsilon is not a nonterminal.");
      } 
      prods.add(L);
      nonterminal.add(L.leftSide);
    }
    //Have to do this again, since we don't know what's terminal until we know
    //all non-terminals.
    for(Production L:prods) {
      if(L!=null) {
        if(L.rightSide!=null) for(Object s:L.rightSide) {
          if(!nonterminal.contains(s)) {
            terminal.add(s);
          }
        }
      }
    }
  }
  /**
   * Map of which nonterminal symbols are nullable
   */
  public Map<Object,Boolean> computeNullable() {
    Map<Object,Boolean> nullable=new LinkedHashMap<Object,Boolean>();
    //Directly nullable nonterminals
    for(Object lhs:nonterminal) {
      nullable.put(lhs, false);
      for(Production P:prods) if(P!=null){
        if(P.leftSide==lhs) {
          if(P.rightSide.size()==0) {
            nullable.put(lhs, true);
          }
        }
      }
    }
    //Indirectly nullable nonterminals
    boolean done=false;
    bigLoop:while(!done) {
      done=true; //If we get any new nonterminals then mark this back to false
      for(Object lhs:nonterminal) {
        if(!nullable.get(lhs)) {   //Only need to check nonterminals that aren't known to be nullable yet
          prodLoop:for(Production P:prods) if (P!=null) {
            if(P.leftSide==lhs) {
              boolean nullableHere=true;
              for(Object rhs:P.rightSide) {
                if(isTerminal(rhs)) {
                  nullableHere=false;       //Terminal in this production, this nonterminal not nullable by this production 
                  continue prodLoop;  
                } else if(!nullable.get(rhs)) {
                  nullableHere=false;       //Non-nullable nonterminal in this production, this nonterminal
                                            //not nullable by this production
                  continue prodLoop;
                }
              }
              if(nullableHere) {
                nullable.put(lhs, true);
                done=false; //At least one more nullable terminal, check for more
                continue bigLoop;
              }
            }
          }
        }
      }
    }
    return nullable;
  }
  public Set computeFIRST(List RHS, Map<Object,Set> FIRST, Map<Object,Boolean> nullable) {
    Set newFIRST=new LinkedHashSet();
    //It's a normal right-hand side. The first set is the union of the first sets of 
    //the nullable symbols before the first non-nullable symbol and the first set first non-nullable symbol. 
    for(Object Sym:RHS) {
      if(isTerminal(Sym)) {
        newFIRST.add(Sym);
        break;
      } else {
        Object Key=Sym;
        Set oldFIRST=FIRST.get(Key);
        if(oldFIRST!=null) newFIRST.addAll(oldFIRST);
        if(!nullable.get(Sym)) {
          break;
        }
      }
    }
    return newFIRST;
  }
  /**
   * Map of FIRST sets. Key is a nonterminal or string,
   * value is a set of terminals
   */
  public Map<Object,Set> computeFIRST(Map<Object,Boolean> nullable) {
    Map<Object,Set> FIRST=new LinkedHashMap<Object,Set>();
    //Initialize the argument array, the list of arguments for which we are generating the FIRST sets for
    //All the non-terminals go in
    for(Object NT:nonterminal) {
      FIRST.put(NT,new LinkedHashSet());
    }
    //Each unique RHS goes in. A ListMap guarantees uniqueness for us. Don't include RHSs which are just
    //a single terminal (already done above)
    for(Production P:prods) if(P!=null){
      if(P.rightSide.size()!=1 || isTerminal(P.rightSide.get(0))) {
        FIRST.put(P.rightSide,new LinkedHashSet());
      }
    }
    
    //Fill in the trivial guys (Iteration 1)
    //An empty RHS has an empty set (already done)
    //An RHS that starts with a terminal has that terminal only (special case of later handling)
    
    //Fill in the non-trivial guys until things stop changing (Iteration 2-N)
    boolean done=false;
    while(!done) {
      done=true; //If anything changes, we'll switch this back to false;
      //Go through all the arguments
      for(Object Arg:FIRST.keySet()) {
        Set thisFIRST=FIRST.get(Arg);
        Set newFIRST=new LinkedHashSet();
        if(Arg instanceof List) {
          List RHS=(List)Arg;
          newFIRST.addAll(computeFIRST(RHS,FIRST,nullable));
          //It's a normal right-hand side. The first set is the union of the first sets of 
          //the nullable symbols before the first non-nullable symbol and the first set first non-nullable symbol. 
        } else {
          //It's a single nonterminal. Its first sets is the union of all the first sets of the RHSs of its productions.
          for(Production P:prods) if(P!=null){
            if(P.leftSide==Arg) {
              if(P.rightSide.size()==1 && isNonterminal(P.rightSide.get(0))) {
            	Set this2FIRST=FIRST.get(P.rightSide.get(0));
                newFIRST.addAll(this2FIRST);
              } else {
                newFIRST.addAll(FIRST.get(P.rightSide));
              }
            }
          }
        }
        //Did we gather any new symbols?
        if(!thisFIRST.containsAll(newFIRST)) {
          //If so, we're not at a fixed point yet...
          done=false;
          //...and we need to add the new ones we gathered. Set mechanism will weed out duplicates.
          thisFIRST.addAll(newFIRST);
        }
      }
    }
    return FIRST;
  }
  public boolean isNullable(Map<Object,Boolean> nullable, List rhs) {
    if(rhs.size()==0) return true;
    for(Object E:rhs) {
      if(isTerminal(E)) return false;
      if(!nullable.get(E)) return false;
    }
    return true;
  }
  /**
   * Compute the FOLLOW sets for this grammar, all at once.
   * @return Map of FOLLOW sets. Key is a nonterminal, value is a
   * set of terminals.
   */
  public Map<Object,Set> computeFOLLOW(Map<Object,Set> FIRST, Map<Object,Boolean> nullable) {
    Map<Object,Set> FOLLOW=new LinkedHashMap<Object,Set>();
    	  
    //This one is needed just long enough to calculate FOLLOW
    prods.add(new Production(specialSymbol.DummyStart2,new Object[] {specialSymbol.DummyStart1,eofSymbol}));
    nonterminal.add(specialSymbol.DummyStart2);

    //Take care of the start symbol and end marker first.
    for(Object NT:nonterminal) {
      Set thisFOLLOW=new LinkedHashSet();
      if(NT==startSymbol) thisFOLLOW.add(eofSymbol);    	
      FOLLOW.put(NT,thisFOLLOW);
    }
    
    boolean done=false;
    while(!done) {
      done=true;
      for(Object N:nonterminal) {
        Set thisFOLLOW=FOLLOW.get(N);
  	    Set newFOLLOW=new LinkedHashSet();
      	for(Production P:prods) if(P!=null){
          for(int i=0;i<P.rightSide.size();i++) {
          	Object Sym=P.rightSide.get(i);
          	if(Sym==N) {
          	  int betaSize=P.rightSide.size()-i-1;
         	    List beta=new ArrayList(P.rightSide.subList(i+1, i+1+betaSize));
              newFOLLOW.addAll(computeFIRST(beta,FIRST,nullable));
              if(isNullable(nullable,beta)) newFOLLOW.addAll(FOLLOW.get(P.leftSide));
          	}
    	    }
    	  }
    	  if(!thisFOLLOW.containsAll(newFOLLOW)) {
      	  done=false;
      	  thisFOLLOW.addAll(newFOLLOW);
      	}
      }
    }
    prods.remove(prods.size()-1);
    nonterminal.remove(specialSymbol.DummyStart2);
    FOLLOW.remove(specialSymbol.DummyStart2);
    return FOLLOW;
  }
  private static enum specialSymbol {
    DummyStart1,DummyStart2;
  };
  public void addDummyFirst() {
    if(prods.get(0)==null) prods.remove(0);
    prods.add(0,new Production(specialSymbol.DummyStart1,new Object[] {startSymbol}));
    nonterminal.add(specialSymbol.DummyStart1);
  }
  public String toString() {
    StringWriter result=new StringWriter();
    PrintWriter ouf=new PrintWriter(result,true);
    
    for(int i=0;i<prods.size();i++) {
      ouf.println(" "+i+": "+prods.get(i));
    }
    return result.toString();
  }

  public static void main(String[] args) throws AutomatonException {
/*
    Grammar Mogensen3_9=new Grammar(T,EOF,new Production[] {
      new Production(T,new Object[] {R}),
      new Production(T,new Object[] {a,T,c}),
      new Production(R,new Object[] {}),
      new Production(R,new Object[] {b,R})
    );
    LRParser LR=new LRParser(Mogensen3_9,"Data/Grammar/Morgensen3_9");
    Tape<Token> T=new SymbolTape(new Object[] {a,a,b,b,b,c,c}, EOF);
    System.out.println(LR.toString());
    LR.parse(T);
*/
    /*
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
    LRParser LR=new LRParser(Mogensen3_2,"Data/Grammar/Morgensen3_2");
    Tape<Token> T=new SymbolTape(new Object[] {i,p,i,t,i}, EOF);
    System.out.println(LR.toString());
    LR.parse(T);
    */
    
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
    System.out.println(Parsons4_3_1);
	  Parsons4_3_1.printNullable();
	  Parsons4_3_1.printFIRST();
    Parsons4_3_1.printFOLLOW();
    LRParser LR=new LRParser(Parsons4_3_1,"Data/Grammar/Parsons4_3_4");
    System.out.println(LR.toString());
     */
  }
}
