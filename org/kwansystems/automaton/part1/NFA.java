package org.kwansystems.automaton.part1;

import java.io.*;
import java.util.*;

import org.kwansystems.automaton.Transition;
import org.kwansystems.tools.*;
import org.kwansystems.tools.NullComparator;

/**
 * Nondeterministic finite state machine, NFA. This machine can't actually be
 * run, but exists so it can be converted to a deterministic finite state
 * machine, which can be run.
 * <p>
 * In full formality, a Nondeterministic Finite State Automaton <i>M</i>
 * consists of the following quintuple:
 * <ul>
 * <li>A finite set of input symbols <i>Sigma</i>, the <i>input alphabet</i>.
 * This set may include the null symbol <i>epsilon</i>. This null symbol is
 * like a symbol in that it may cause a transition, but unlike a symbol in that
 * no input symbol are consumed. The null symbol may not occur in the input.</li>
 * <li>A finite set of states <i>Q</i>
 * <li>A starting state <i>q<sub>0</sub></i> which is a member of <i>Q</i>
 * <li>A set of accepting states <i>F</i> which is a subset of <i>Q</i>. When
 * the machine reaches an accepting state, it stops.
 * <li>A state transition function <i>delta</i>(<i>q<sub>i</sub></i>,<i>x</i>)=<i>Q<sub>j</sub></i>
 * which takes as input a member of <i>Q</i> and <i>Sigma</i> and returns
 * subset of <i>Q</i>
 * </ul>
 * so we can say <i>M</i>=(<i>Sigma, Q, q<sub>0</sub>, F, delta</i>)
 * <p>
 * A Moore machine is a finite state machine with the following addition: For
 * each state, there is an output symbol in the set <i>Gamma<sub>Mo</sub></i>
 * associated with it. Each time that the machine enters a state, it sends the
 * associated output symbol to the <i>Moore output stream</i>.
 * <p>
 * An NFA has no analogue to an FSA Mealy machine.
 * <p>
 * For machines implemented by this class, <i>Sigma</i> and <i>Q</i> are
 * subsets of the <b><tt>Object</tt></b>s. These should both be a class of
 * immutable objects, like <tt>String</tt>, <tt>Integer</tt>, or
 * <tt>Enum</tt>. Bad Things will happen if you use an object as as state
 * name or symbol and that object changes in a way that upsets equals(). q<sub>0</sub>
 * is freely specifiable. <i>F</i> is an attribute of each state, stored in the
 * state objects. True means the state is accepting and false means it isn't.
 * The size of the machine is limited by the allowable size of a Java array.
 * <p>
 * <i>delta</i> is specified by a big two-dimensional transition table, which
 * is the heart of the machine. The first index of the table represents a state,
 * and the second index an input symbol. The value contained at each cell
 * contains a set of state names, given that the old state is specified in the
 * first index, and the current symbol in the second.
 * <p>
 * 
 * @param <AlphabetType>
 *          Class to be used for symbols. <i>Sigma</i> is a set of objects of
 *          this class. Objects of this class should be immutable, like String,
 *          Integer, or Enum. You can use any class, but once they are given to
 *          this object, they should not be muted. Any change to an object which
 *          changes the equals() relationship or hashCode() calculated are Bad.
 * @param <StateNameType>
 *          Class to be used for state names. <i>Q</i> is a set of objects of
 *          this class. Same don't mute warning applies here.
 */
public class NFA<AlphabetType, StateNameType> {
  private Map<StateNameType, NFAState<AlphabetType, StateNameType>> NFADelta;

  /**
   *
   */
  public Set<AlphabetType> NFASigma;

  /**
   *
   */
  public StateNameType NFAStartState;

  /**
   *
   */
  public NFA() {
    NFADelta=new LinkedHashMap<StateNameType, NFAState<AlphabetType, StateNameType>>();
    NFASigma=new LinkedHashSet<AlphabetType>();
  }

  /**
   *
   * @return
   */
  public StateNameType firstAcceptState() {
    for(StateNameType s:NFADelta.keySet()) {
      if(NFADelta.get(s).Accept)
        return s;
    }
    return null;
  }

  /**
   *
   * @return
   */
  public Map<StateNameType,Integer> stateNumbering() {
    Map<StateNameType,Integer> FwdStateList=new LinkedHashMap<StateNameType,Integer>();
    int i=0;
    if(NFAStartState!=null) {
      FwdStateList.put(NFAStartState, i);
      i++;
    }
    for(StateNameType stateName:NFADelta.keySet()) {
      if(!stateName.equals(NFAStartState)) {
        FwdStateList.put(stateName,i);
        i++;
      }
    }
    return FwdStateList;
    
  }
  /**
   *
   * @return
   */
  public Map<Integer,StateNameType> invStateNumbering() {
    Map<StateNameType,Integer> FwdStateList=stateNumbering();
    Map<Integer,StateNameType> InvStateList=new TreeMap<Integer,StateNameType>();

    for(StateNameType stateName:FwdStateList.keySet()) {
      int i=FwdStateList.get(stateName);
      InvStateList.put(i,stateName);
    }
    return InvStateList;
  }
  /**
   * 
   * @return
   */
  public NFA<AlphabetType,Integer> numberStates() {
    NFA<AlphabetType,Integer> result=new NFA<AlphabetType,Integer>();
    result.NFASigma=NFASigma;
    //Number the states
    Map<StateNameType,Integer> FwdStateList=stateNumbering();

    if(NFAStartState==null) {
      result.NFAStartState=null;
    } else {
      result.NFAStartState=FwdStateList.get(NFAStartState);
    }
      
    // Construct the new state table
    for(StateNameType stateName:FwdStateList.keySet()) {
      NFAState<AlphabetType, Integer> newState=new NFAState<AlphabetType, Integer>();
      NFAState<AlphabetType, StateNameType> oldState=NFADelta.get(stateName);
      newState.Accept=oldState.Accept;
      newState.comment=oldState.comment;
      newState.MooreOutput=oldState.MooreOutput;
      for(AlphabetType symbol:oldState.getAlphabet()) {
        Set<Transition<StateNameType>> oldTrans=oldState.get(symbol);
        Set<Transition<Integer>> newTrans=new LinkedHashSet<Transition<Integer>>();
        for(Transition<StateNameType> oldT:oldTrans) {
          Transition<Integer> newT=new Transition<Integer>(FwdStateList.get(oldT.nextState));
          newTrans.add(newT);
          newT.term=oldT.term;
        }
        newState.put(symbol, newTrans);
      }
      result.NFADelta.put(FwdStateList.get(stateName), newState);
    }
    return result;
  }

  /**
   * Kleene this machine. Use the Subset Construction (documented in Parsons
   * pp33-39) to generate a deterministic transition table which is equivalent
   * to the non-deterministic transition function already set up in this
   * machine. NFAs as presently defined by this class have no Mealy output. If
   * we add this, Parsons' algorithm may have to be extended to handle output
   * properly.
   * 
   * @return A DFA equivalent to this NFA
   */
  public DFA<AlphabetType, Set<StateNameType>> Kleene() {
    return Kleene(new DFA<AlphabetType, Set<StateNameType>>(null));
  }
  /**
   *
   * @param Mprime
   * @return
   */
  public DFA<AlphabetType, Set<StateNameType>> Kleene(DFA<AlphabetType, Set<StateNameType>> Mprime) {
    // NFA is called M in comments
    // DFA is called M'

    // Collect the non-null elements of the M alphabet as the elements of the M'
    // alphabet.
    // An NFA alphabet can have at most one null element, representing epsilon
    // (spontaneous
    // transition without consuming any input).

    Set<AlphabetType> MprimeSigma=new LinkedHashSet<AlphabetType>();
    MprimeSigma.addAll(NFASigma);
    if(MprimeSigma.contains(null)) MprimeSigma.remove(null);

    // 0. Initially the equivalent machine M' is empty.
    Map<Set<StateNameType>, State<AlphabetType, Set<StateNameType>>> table=new LinkedHashMap<Set<StateNameType>, State<AlphabetType, Set<StateNameType>>>();

    // 1. Create a starting state named after the epsilon-closure of the M start
    // state
    Set<StateNameType> StartClosure=findEpsClosure(NFAStartState, true);
    Mprime.setStartState(StartClosure);
    table.put(StartClosure, new State<AlphabetType, Set<StateNameType>>());

    // 2. While there is an uncompleted row in the table for M' do:
    // a. Let x be the state for this row
    Set<StateNameType> x=StartClosure;
    while(x!=null) {
      // b. For each input symbol a do:
      for(AlphabetType a:MprimeSigma) {
        // i. Find the epsilon closure of N(x,a) = some set we'll call T
        Set<StateNameType> transitionsOut=new LinkedHashSet<StateNameType>();
        for(StateNameType s:x) {
          Set<Transition<StateNameType>> q=NFADelta.get(s).get(a);
          if(q!=null)
            for(Transition<StateNameType> t:q) {
              transitionsOut.add(t.nextState);
            }
        }
        Set<StateNameType> T=findEpsClosure(transitionsOut);
        // ii. Create the M' state y=[T] corresponding to T
        // iii. If y is not yet in the list of M' states, add it to the list.
        // (This results in a new row in the table.)
        if(!table.containsKey(T)) {
          table.put(T, new State<AlphabetType, Set<StateNameType>>());
        }
        // iv. Add the rule N'(x,a)=y to the list of transition rules for M'
        table.get(x).put(a, new Transition<Set<StateNameType>>(T));
      }

      // See if there are any states left with no transition for some input
      // symbol. If so, this state is unfinished.
      // This does the work of step 2 and 2a above for the next time around.
      x=null;
      for(Set<StateNameType> SS:table.keySet()) {
        State<AlphabetType, Set<StateNameType>> TT=table.get(SS);
        for(AlphabetType c:MprimeSigma) {
          if(TT.get(c)==null)
            x=SS;
        }
      }
    }

    // 3. Identify the accepting states in M'.
    Set<Set<StateNameType>> acc=new LinkedHashSet<Set<StateNameType>>();
    for(Set<StateNameType> state:table.keySet()) {
      for(StateNameType s:state) {
        if(NFADelta.get(s).Accept)
          acc.add(state);
      }
    }

    // Put it in the correct format for an FSM table.
    Mprime.clear();
    for(Set<StateNameType> state:table.keySet()) {
      State<AlphabetType, Set<StateNameType>> FSAState=new State<AlphabetType, Set<StateNameType>>();
      for(AlphabetType a:MprimeSigma) {
        FSAState.put(a, new Transition<Set<StateNameType>>(table.get(state).get(a).nextState));
      }
      FSAState.Accept=acc.contains(state);
      if(FSAState.Accept) {
        for(StateNameType s:state) {
          NFAState<AlphabetType, StateNameType> acceptState=NFADelta.get(s);
          if(acceptState.Accept) {
            if((FSAState.MooreOutput==null&&acceptState.MooreOutput!=null)||NullComparator.NC.compare(FSAState.MooreOutput,acceptState.MooreOutput)>0) {
              FSAState.MooreOutput=acceptState.MooreOutput;
            }
          }
        }
      }
      Mprime.put(state, FSAState);
    }

    // Drop the crash state []
    Mprime.eraseState(new LinkedHashSet<StateNameType>());

    return Mprime;
  }

  /**
   *
   * @param ouf
   */
  public void PrintNFATransitionTable(PrintStream ouf) {
    ouf.println(toString());
  }

  /**
   * Find the epsilon-closure set as defined by Parsons p36. This is the set of
   * states which can be reached from a given state on an epsilon transition,
   * including the given state.
   * 
   * @param root
   *          State to search for closure
   * @return epsilon-closure of this set
   */
  public Set<StateNameType> findEpsClosure(StateNameType root) {
    return findEpsClosure(root, true);
  }

  /**
   * Find the epsilon-closure set as defined by Parsons p36. This is the set of
   * states which can be reached from a given state, including the given state.
   * This form does the actual work of finding a closure. This function uses
   * recursion, and calls itself with the top argument set to false. All outside
   * calls to this function should set the top parameter to true.
   * 
   * @param root
   *          State to search for closure
   * @param top
   *          True if this is the top of a search, false otherwise. If it is the
   *          top of the search, all the marks on all the states are cleared
   *          before searching for the epsilon closure. Otherwise, existing
   *          marks are obeyed.
   * @return epsilon-closure of this set
   */
  private Set<StateNameType> findEpsClosure(StateNameType root, boolean top) {
    NFAState<AlphabetType, StateNameType> S, nextS;
    if(top) {
      // Clear all previous state markings
      for(NFAState<AlphabetType, StateNameType> s:NFADelta.values()) {
        s.Marked=false;
      }
    }
    Set<StateNameType> Closure=new LinkedHashSet<StateNameType>();
    S=NFADelta.get(root);
    // Put yourself in the list
    Closure.add(root);
    S.Marked=true;
    // Now, for all the epsilon transitions...
    Set<Transition<StateNameType>> TT=S.get(null);
    if(TT!=null) {
      for(Transition<StateNameType> T:TT)
        if(T!=null) {
          nextS=NFADelta.get(T.nextState);
          // If the target of this transition is not marked yet...
          if(!nextS.Marked) {
            // Go get its epsilon closure...
            Set<StateNameType> nextEpsClosure=findEpsClosure(T.nextState, false);
            // And add it to this one's closure
            Closure.addAll(nextEpsClosure);
          }
        }
    }
    return Closure;
  }

  /**
   *
   * @param roots
   * @return
   */
  public Set<StateNameType> findEpsClosure(Set<StateNameType> roots) {
    Set<StateNameType> result=new LinkedHashSet<StateNameType>();
    for(StateNameType S:roots)
      result.addAll(findEpsClosure(S));
    return result;
  }

  /**
   *
   * @param oldName
   * @param newName
   */
  public void renameNFAState(StateNameType oldName, StateNameType newName) {
    // Rename all the transitions to the old name
    for(NFAState<AlphabetType, StateNameType> S:NFADelta.values()) {
      for(Set<Transition<StateNameType>> TT:S.getTransitions()) {
        for(Transition<StateNameType> T:TT) {
          if(T.nextState.equals(oldName))
            T.nextState=newName;
        }
      }
    }
    // Rename the start state and current state if necessary
    if(NFAStartState.equals(oldName))
      NFAStartState=newName;

    // Rename the state table row
    NFAState<AlphabetType, StateNameType> S=NFADelta.get(oldName);
    NFADelta.remove(oldName);
    NFADelta.put(newName, S);

  }

  /**
   *
   * @param objectName
   * @return
   */
  public String DotNFATransitionTable(String objectName) {
    StringWriter result=new StringWriter();
    Map<StateNameType,Integer> FwdStateNum=stateNumbering();
    PrintWriter ouf=new PrintWriter(result, true);
    // Header
    ouf.println("digraph "+objectName+"_NFA {");
    ouf.println("  rankdir=LR;");
    ouf.println("  {node [shape = point style=invis label=\"\"]; "+objectName
        +"_phantomStart;}");

    // Labels for all states including Moore output
    Set<StateNameType> StateList=NFADelta.keySet();
    for(StateNameType sn:StateList) {
      NFAState<AlphabetType, StateNameType> S=NFADelta.get(sn);
      String label=sn+(S.MooreOutput!=null?"/"+S.MooreOutput:"");
      String shape="ellipse";
      String color=S.Accept?"peripheries=2":"";
      ouf.println("  {node [shape = "+shape+" label=\""+label+"\" "+color+"]; \""+FwdStateNum.get(sn)+"\";}");
    }

    // Draw edges
    for(StateNameType sn:StateList) {
      if(sn.equals(NFAStartState)) {
        ouf.println("  "+objectName+"_phantomStart -> \""+FwdStateNum.get(sn)+"\" ;");

      }
      NFAState<AlphabetType, StateNameType> S=NFADelta.get(sn);
      Map<StateNameType, Set<AlphabetType>> edgeGrouping=new LinkedHashMap<StateNameType, Set<AlphabetType>>();
      for(AlphabetType a:NFASigma) {
        Set<Transition<StateNameType>> TT=S.get(a);
        if(TT!=null) {
          for(Transition<StateNameType> T:TT) {
            Set<AlphabetType> thisEdgeGroup=edgeGrouping.get(T.nextState);
            if(thisEdgeGroup==null) {
              thisEdgeGroup=new LinkedHashSet<AlphabetType>();
              edgeGrouping.put(T.nextState, thisEdgeGroup);
            }
            thisEdgeGroup.add(a);
          }
        }
      }
      for(Object next:edgeGrouping.keySet()) {
        Integer snNum=FwdStateNum.get(sn);
        Integer nextNum=FwdStateNum.get(next);
        ouf.println("  \""+snNum+"\" -> \""+nextNum+"\" [ label=\""+DFA.makeEdgeLabel(edgeGrouping.get(next))+"\" ];");
      }
    }
    ouf.println("}");
    return result.toString();
  }

  /**
   *
   * @return
   */
  public String WikiNFATransitionTable() {
    StringWriter result=new StringWriter();
    PrintWriter ouf=new PrintWriter(result, true);
    ouf.println("{| border=1");
    ouf.println("|-");
    ouf.print("| ||");
    for(Object a:NFASigma) {
      ouf.print(a+" ||");
    }
    ouf.println();
    Set<StateNameType> StateList=NFADelta.keySet();
    int i=0;
    for(Object sn:StateList) {
      ouf.println("|-");
      NFAState<AlphabetType, StateNameType> S=NFADelta.get(sn);
      String prefix="";
      if(sn.equals(NFAStartState))
        prefix+=">";
      if(S.Accept)
        prefix+="*";
      ouf.print("|"+prefix+sn+(S.MooreOutput!=null?(","+S.MooreOutput):"")+"||");

      for(AlphabetType a:NFASigma) {
        Set<Transition<StateNameType>> TT=S.get(a);
        ouf.print("{");
        if(TT!=null) {
          boolean first=true;
          for(Transition<StateNameType> T:TT)
            if(T!=null) {
              if(!first) {
                ouf.print(",");
              } else {
                first=false;
              }
              ouf.print(T.nextState
                  +(T.MealyOutput!=null?(","+T.MealyOutput):""));
            }
        }
        ouf.print("} ||");
      }
      if(S.comment!=null)
        ouf.print(S.comment);
      ouf.println();
      i++;
    }
    ouf.println("|}");
    return result.toString();
  }

  @Override
  public String toString() {
    return WikiNFATransitionTable();
    //return DotNFATransitionTable("this");
  }

  /**
   *
   * @param newStateName
   * @param newState
   */
  public void putState(StateNameType newStateName, NFAState<AlphabetType, StateNameType> newState) {
    NFADelta.put(newStateName, newState);
  }

  /**
   *
   * @param stateName
   * @return
   */
  public NFAState<AlphabetType, StateNameType> getState(StateNameType stateName) {
    return NFADelta.get(stateName);
  }

  /**
   *
   * @param stateName
   */
  public void removeNFAState(StateNameType stateName) {
    NFADelta.remove(stateName);
  }

  /**
   *
   * @param a
   * @return
   */
  public boolean addLetter(AlphabetType a) {
    return NFASigma.add(a);
  }

  /*
   * public boolean removeLetter(AlphabetType a) { return NFASigma.remove(a); }
   */
  /**
   *
   * @param a
   * @return
   */
  public boolean containsLetter(AlphabetType a) {
    return NFASigma.contains(a);
  }

  /**
   *
   * @param newState
   */
  public void setStartState(StateNameType newState) {
    NFAStartState=newState;
  }

  /**
   *
   * @return
   */
  public StateNameType getStartState() {
    return NFAStartState;
  }

  /**
   *
   * @return
   */
  public Set<StateNameType> getStates() {
    return NFADelta.keySet();
  }

  /**
   * Check if an NFA is internally consistent, and optionally if it can be used
   * in Thompson's construction.
   * <p>
   * It is hereby decreed that any nondeterministic finite automaton:
   * <ul>
   * <li>Shall have exactly ONE (1) start state, and that the start state shall
   * be a state in the machine.</li>
   * <li>Shall have no transition sets for symbols not in the declared alphabet
   * NFASigma.</li>
   * <li>Shall have no null or empty transition sets.</li>
   * <li>Shall have no null transitions.</li>
   * <li>Every transition shall point to a valid state.</li>
   * </ul>
   * <p>
   * Further, a machine to be used in Thompson's construction:
   * <ul>
   * <li>Shall have no transitions into the start state.</li>
   * <li>Shall have exactly ONE (1) accepting state, called the final state.</li>
   * <li>Shall have no transitions out of the final state.</li>
   * </ul>
   * <p>
   * Violators are guilty of Treason class SS/2, failure to obey direct orders
   * of The Computer, punishable by censure to termination.
   * 
   * @param checkThompson
   * @throws IllegalNFAException
   *           if there is something wrong with the NFA in general. Message
   *           explains why.
   * @throws NonThompsonException
   *           if the machine is unsuitable for Thompson's construction. Message
   *           explains why.
   */
  public void checkNFA(boolean checkThompson) {
    try {
      // Presumed valid until proven otherwise
      Set<StateNameType> StateNames=NFADelta.keySet();

      // A machine shall have exactly ONE (1) start state... check that
      // startState is non-null, and that it
      // is one of the states
      if(NFAStartState==null)
        throw new IllegalNFAException("No start state defined");
      if(!NFADelta.containsKey(NFAStartState))
        throw new NonThompsonException("Start state is not a state");

      StateNameType finalState=null;
      for(StateNameType sn:StateNames) {
        NFAState<AlphabetType, StateNameType> thisState=NFADelta.get(sn);
        if(thisState.Accept) {
          if(checkThompson&&finalState!=null) {
            throw new NonThompsonException(
                "Multiple accepting states. At least "+finalState+" and "+sn
                    +" are marked accepting");
          }
          finalState=sn;
        }

        // State shall have no transition sets on a symbol not in NFASigma
        Set<AlphabetType> thisStateSigma=thisState.getAlphabet();
        if(!NFASigma.containsAll(thisStateSigma)) {
          StringBuffer thisStateString=new StringBuffer();
          for(Object a:thisStateSigma) {
            if(thisStateString.length()>0)
              thisStateString.append(", ");
            if(a==null) {
              thisStateString.append("null");
            } else {
              thisStateString.append(DFA.escape(a));
            }
          }
          StringBuffer NFAString=new StringBuffer();
          for(Object a:NFASigma) {
            if(NFAString.length()>0)
              NFAString.append(", ");
            if(a==null) {
              NFAString.append("null");
            } else {
              NFAString.append(DFA.escape(a));
            }
          }
          throw new IllegalNFAException("State "+sn
              +" has transitions for symbol(s) ["+thisStateString.toString()
              +"] some of which are not in NFASigma ["+NFAString.toString()+"]");
        }

        // In a Thompson machine, Final state shall have no transitions
        if(checkThompson&&thisState.Accept&&thisStateSigma.size()>0) {
          throw new NonThompsonException("Accepting state "+sn
              +" has outbound transitions");
        }

        // Transition sets shall not be empty. Either state.get(some char in
        // NFASigma) shall return
        // null, or shall return a Set<Transition> with a size greater than
        // zero.
        for(AlphabetType a:thisStateSigma) {
          Set<Transition<StateNameType>> TT=thisState.get(a);
          if(TT!=null) {
            if(TT.size()==0) {
              throw new IllegalNFAException("Empty transition set on state "+sn
                  +", character "+a);
            }
            for(Transition<StateNameType> T:TT) {
              if(T==null) {
                throw new IllegalNFAException(
                    "Null transition in set for state "+sn+", character "+a);
              }
              if(!NFADelta.containsKey(T.nextState)) {
                throw new IllegalNFAException("Transition in set for state "+sn
                    +", character "+a+", to non-existent state "+T.nextState);
              }
              if(checkThompson&&T.nextState.equals(NFAStartState)) {
                throw new NonThompsonException(
                    "Transition back to start on state "+sn+", character "+a);
              }

            }
          }
        }
      }
      if(checkThompson&&finalState==null) {
        throw new NonThompsonException("No accepting states.");
      }
    } catch (IllegalNFAException e) {
      System.err.println(DotNFATransitionTable("BadMachine"));
      PrintNFATransitionTable(System.err);
      throw e;
    }
  }
}
