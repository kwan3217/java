package org.kwansystems.automaton.part1;

import java.util.*;
import java.io.*;

import org.kwansystems.automaton.*;
import org.kwansystems.automaton.tape.*;
import org.kwansystems.tools.*;

/** Finite State Machine, with option to have Mealy machine and Moore machine
 * outputs.
 * <p>
 * In full formality, a Finite State Machine <i>M</i> consists of the following
 * quintuple:
 *  <ul>
 *  <li>A finite set of input symbols <i>Sigma</i>, the <i>input
 * alphabet</i></li>
 *  <li>A finite set of states <i>Q</i>
 *  <li>A starting state <i>q<sub>0</sub></i> which is a member of <i>Q</i>
 *  <li>A set of accepting states <i>F</i> which is a subset of <i>Q</i>. When
 * the machine reaches an accepting state, it stops.
 *  <li>A state transition function
 *      <i>delta</i>(<i>q<sub>i</sub></i>,<i>x</i>)=<i>q<sub>j</sub></i>
 *      which takes as input a member of <i>Q</i> 
 *      and <i>Sigma</i> and returns a member of <i>Q</i>
 *  </ul>
 *  so we can say <i>M</i>=(<i>Sigma, Q, q<sub>0</sub>, F, delta</i>)
 *  <p>
 *  A Moore machine is a finite state machine with the following addition: For 
 *  each state, there is an output symbol in the set <i>Gamma<sub>Mo</sub></i>
 *  associated with it. Each time that the machine enters a state, it sends the
 *  associated output symbol to the <i>Moore output stream</i>.
 *  <p>
 *  A Mealy machine is a finite state machine with the following addition: For 
 *  each transition, there is an output symbol in the set
 *  <i>Gamma<sub>Me</sub></i> associated with it. Each time that the machine
 *  uses a transition, it sends the associated output symbol to the <i>Mealy
 *  output stream</i>.
 *  <p>
 *  For machines implemented by this class, <i>Sigma</i> and <i>Q</i> are 
 *  subsets of the <b><tt>Object</tt></b>s. These should both be a class of
 *  immutable objects, like <tt>String</tt>, <tt>Integer</tt>, or <tt>Enum</tt>,
 *  but this is not enforced. Bad Things will happen if you use an object as as
 *  state name or symbol and that object changes in a way that upsets equals().
 *  q<sub>0</sub> is freely specifiable. <i>F</i> is an attribute of each state,
 *  stored in the state objects. True means the state is accepting and false
 *  means it isn't. The size of the machine is limited by the allowable size of
 *  a Java array.
 *  <p>
 *  <i>delta</i> is specified by a big two-dimensional transition table, which
 *  is the heart of the machine. The first index of the table represents a 
 *  state, and the second index an input symbol. The value contained at each
 *  cell contains the new state, given that the old state is specified in the
 *  first index, and the current symbol in the second.
 *  <p>
 *
 * @param <AlphabetType> Class to be used for symbols. <i>Sigma</i> is a set of objects of this class.
 * Objects of this class should be immutable, like String, Integer, or Enum. You can use any class, but
 * once they are given to this object, they should not be muted. Any change to an object which changes 
 * the equals() relationship or hashCode() calculated are Bad.
 * @param <StateNameType> Class to be used for state names. <i>Q</i> is a set of objects of this class.
 * Same don't mute warning applies here.
 */
public class DFA<AlphabetType,StateNameType> implements Serializable {
  private static final long serialVersionUID = -5710109725892483559L;
  /** Create a blank FiniteStateMachine with a certain tape. 
   * 
   */
  public DFA(Tape<AlphabetType> Ltape) {
    Delta=new LinkedHashMap<StateNameType,State<AlphabetType,StateNameType>>();
    initListeners();
    setTape(Ltape);
  }
  private void writeObject(ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
  }
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    initListeners();
  }
  private void initListeners() {
    automatonListeners=new ArrayList<AutomatonListener<AlphabetType,StateNameType>>();
    mealyListeners=new ArrayList<MealyListener>();
    mooreListeners=new ArrayList<MooreListener>();
    symbolListeners=new ArrayList<SymbolListener<AlphabetType>>();
  }
  /** Input tape */
  private transient Tape<AlphabetType> tape;
  /** Transition table. This is a mapping of String state names to State objects. Each state contains a mapping of 
   * input symbols to transitions. */
  private StateNameType StartState;
  private StateNameType CurrentState;
//  Set<AlphabetType> Sigma;        //Input alphabet
  private transient ArrayList<AutomatonListener<AlphabetType,StateNameType>> automatonListeners;
  private transient ArrayList<MealyListener> mealyListeners;
  private transient ArrayList<MooreListener> mooreListeners;
  private transient ArrayList<SymbolListener<AlphabetType>> symbolListeners;
  /** Do something with a Mealy output. Pass this along to a MealyListener if one is present
   * @param S Mealy output to be processed
   */
  protected void OutputMealy(Object S) {
    for(MealyListener ML:mealyListeners) ML.OutputMealy(S); 
  }; 
  public void addMealyListener(MealyListener ML) {
    mealyListeners.add(ML);
  }
  /** Do something with a Moore output. Pass this along to a MooreListener if one is present
   * @param S Mealy output to be processed
   */
  protected void OutputMoore(Object S) {
    for(MooreListener ML:mooreListeners) ML.OutputMoore(S); 
  }; 
  public void addMooreListener(MooreListener ML) {
    mooreListeners.add(ML);
  }
  protected void ShowMachineState() {
    for(AutomatonListener<AlphabetType,StateNameType> AL:automatonListeners) AL.ShowMachineState(this);
  }
  protected void ShowComment(String s) {
    for(AutomatonListener<AlphabetType,StateNameType> AL:automatonListeners) AL.ShowComment(this,s);
  }
  protected void ShowTransition(Transition<StateNameType> T) {
    for(AutomatonListener<AlphabetType,StateNameType> AL:automatonListeners) AL.ShowTransition(this,T);
  }
  public void addAutomatonListener(AutomatonListener<AlphabetType,StateNameType> AL) {
    automatonListeners.add(AL);
  }
  protected void ShowCurrentSymbol(AlphabetType c) {
    for(SymbolListener<AlphabetType> SL:symbolListeners) SL.ShowCurrentSymbol(c);
  }
  public void addSymbolListener(SymbolListener<AlphabetType> AL) {
    symbolListeners.add(AL);
  }
  protected Termination AdvanceTape() {
    return getTape().Right();
  }
  /** Read the next input symbol. By default, this reads from a tape. 
   * 
   * @return Next input symbol to process
   */
  protected AlphabetType read() throws AutomatonException {
    return getTape().read();
  }
  public Termination step() {
    Termination T;
    ShowComment("--- Step "+stepsTaken);
    stepsTaken++;
    ShowMachineState();
    State<AlphabetType,StateNameType> S=Delta.get(CurrentState);
    //Moore output for this state
    OutputMoore(S.MooreOutput);
    //Read the current symbol on the tape
    AlphabetType currentSymbol;
    try {
      currentSymbol=read();
    } catch (AutomatonException E) {return Termination.Crash;}
    ShowCurrentSymbol(currentSymbol);
    //Read the transition table and get the proper transition
    Transition<StateNameType> currentTransition=readStateTable(CurrentState,currentSymbol);
    ShowTransition(currentTransition);
    //If the transition is blank, crash
    if(currentTransition==null) {
      T=Termination.Crash;
    } else {
      //Mealy output for this transition
      OutputMealy(currentTransition.MealyOutput);
      //Transition to the new state
      CurrentState=currentTransition.nextState; 
      //Advance the tape
      AdvanceTape();
      T=Termination.Continue;
    }
    ShowComment(T.toString());
    return T;
  }
  public void Reset() {
    CurrentState=StartState;
    stepsTaken=0;
  }
  public Termination Start() {
    Reset();
    return RunToHalt();
  }
  public Termination StartFor(int maxSteps) {
    Reset();
    return RunFor(maxSteps);
  }
  int stepsTaken;
  public Termination RunToHalt() {
    Termination T=Termination.Continue;
    while(T==Termination.Continue) {
      T=step();
    }
    ShowComment("Ran for "+stepsTaken+" steps.");
    ShowComment(""+numTransitionsExercised+" of "+numTransitions+" transitions exercised");
    ShowComment(""+numStatesExercised+" of "+numStates+" states exercised");
    return T;
  }
  public Termination RunToCrash() {
    Termination T=Termination.Continue;
    while(T!=Termination.Crash) {
      T=step();
    }
    ShowComment("Ran for "+stepsTaken+" steps.");
    ShowComment(""+numTransitionsExercised+" of "+numTransitions+" transitions exercised");
    ShowComment(""+numStatesExercised+" of "+numStates+" states exercised");
    return T;
  }
  public Termination RunFor(int steps) {
    Termination T=Termination.Continue;
    while(T==Termination.Continue && steps>0) {
      T=step();
      steps--;
    }
    ShowComment("Ran for "+stepsTaken+" steps.");
    ShowComment(""+numTransitionsExercised+" of "+numTransitions+" transitions exercised");
    ShowComment(""+numStatesExercised+" of "+numStates+" states exercised");
    return T;
  }
  private Transition<StateNameType> readStateTable(StateNameType stateName, AlphabetType symbol) {
    State<AlphabetType,StateNameType> S=Delta.get(stateName);
    if(S==null) return null;
    if(S.useCount==0) numStatesExercised++;
    S.useCount++;
    Transition<StateNameType> T=Delta.get(stateName).get(symbol);
    if(T==null) return null;
    if(T.useCount==0) {
      numTransitionsExercised++;
    }
    T.useCount++;
    return T;
  }
  public int numTransitions=0;
  public int numStates=0;
  int numStatesExercised=0;
  int numTransitionsExercised=0;
  /** Read a state transition table from a specially formatted text file. The table is a subset of Mediawiki
   * table definition language, such that a properly formatted table can be pulled directly from a wiki and
   * run as is, and vice versa. The format is as follows:
   * <ul>
   * <li>Any line which doesn't have a cell delimiter || is treated as a comment and ignored, except
   * for a line which starts with the table end delimiter |}
   * <li>The first defines the tape alphabet. It contains one cell on the left, whose contents
   * are ignored, then one cell for each symbol in the alphabet. The symbol is the first nonblank
   * character (as determined by {@link java.lang.String.trim()}) in the cell. All other data in 
   * the cell is ignored.</li>
   * <li>Each subsequent line defines a state. The first cell on the left contains the following:
   * <ol>
   * <li>An optional accepting state indicator, *</li>
   * <li>The name of the state, an arbitrary length string, terminated by either a comma or
   * the end of the cell</li>
   * <li>If this state has Moore output, a comma followed by the Moore output string, which runs to the
   * end of the cell.</li>
   * </ol>
   * <li>Subsequently there is a cell for each transition from this state. The cells are matched up to 
   * tape symbols in the same order that the symbols are defined in the first row. There must be at least
   * as many cells for transitions as there are symbols in the tape alphabet. A cell may be blank, which
   * means that this is a halting transition, and acceptance or rejection depends on whether this state is
   * marked as accepting. If it is not blank, it consists of the following:
   * <ol>
   * <li>The name of the state to transition to</li>
   * <li>Optionally, a comma followed by the Mealy output for this transition.</li>
   * </ol>
   * </li>
   * <li>If there are more cells than needed to describe all the transitions, then the next cell
   * is used as a comment for what this state "means." This comment does not affect the operation
   * of the machine at all, but may be included in traces and such.</li>
   * <li> Once either the table end delimiter |} is found at the beginning of the line, or the 
   * end-of-file is reached, the reader stops successfully.</li>
   * </ul>
   * <p>
   * Notice that because of this formatting, certain character sequences are reserved and therefore cannot
   * be included in the names of states, symbols, or outputs. Notably, ||, comma, and |} cannot occur in 
   * a state name, and blanks cannot be symbols. This is a limitation of the reader, not the machine. Some other
   * reader could be made without this limitation.
   * @param inf stream to read the table from
   * @throws IOException if there is a problem reading the input stream.
   */
  public static DFA<Character,String> LoadTransitionTable(LineNumberReader inf) throws IOException {
    DFA<Character,String> result=new DFA<Character,String>(null);
    String S=inf.readLine();
    while(!S.contains("||")) S=inf.readLine()+"  ";
    //Get symbol names
    String[] parts=S.split("\\|\\|");
    Set<Character> Sigma=new LinkedHashSet<Character>();
    for(int i=1;i<parts.length-1;i++) Sigma.add(parts[i].trim().charAt(0));
    result.CurrentState="";
    //Read states
    bigloop: for(;;) { //Infinite loop, but we will either find the end-of-table char or the end of file. and break out of the loop
      S=inf.readLine();
      if(S==null) return result;
      while(!S.contains("||")) {
        if(S.startsWith("|}")) break bigloop; 
        S=inf.readLine();
        if(S==null) break bigloop;
      }
      //Now we have a line with a row in it.
      result.numStates++;
      parts=S.split("\\|\\|");
      for(int i=0;i<parts.length;i++)parts[i]=parts[i].trim();
      State<Character,String> thisState=new State<Character,String>();
      if(parts.length>=Sigma.size()+2)thisState.comment=parts[Sigma.size()+1];
      String stateName=parts[0].substring(1).trim();
      String[] parts2=stateName.split(",");
      if(parts2.length>1) {
        stateName=parts2[0];
        thisState.MooreOutput=parts2[1]; 
      } else {
        thisState.MooreOutput=null;
      }
      if(stateName.charAt(0)=='*') {
        thisState.Accept=true;
        stateName=stateName.substring(1).trim();
      }
  //    StateList.add(stateName);
      if(result.StartState==null) result.StartState=stateName; 
      int j=1;
      for(Character C:Sigma) {
        if(parts[j].length()>0) {
          Transition<String> T=new Transition<String>();
          result.numTransitions++;
          T.term=Termination.Continue;
          int commaPos=parts[j].indexOf(',');
          if(commaPos>=0) {
            T.nextState=parts[j].substring(0,commaPos).trim();
            T.MealyOutput=parts[j].substring(commaPos+1).trim();
          } else {
            T.nextState=parts[j].trim();
            T.MealyOutput=null;
          }
          thisState.put(C,T);
        }
        j++;
      }
      result.Delta.put(stateName,thisState);
    }
    if(result.StartState!=null)result.CurrentState=result.StartState;
    return result;
  }
  public String WikiTransitionTable() {
    StringWriter result=new StringWriter();
    PrintWriter ouf=new PrintWriter(result,true);
    ouf.println("{| border=1");
    ouf.println("|-");
    ouf.print("| ||");
    for(AlphabetType S:getAlphabet()) {
      char c=S.toString().charAt(0);
      if(c<' ') {
        ouf.print(escape(c)+" ||");
      } else {
        ouf.print(S+" ||");
      }
    }
    ouf.println();
    Set<StateNameType> StateList=getStateNames();
    int i=0;
    SortedSet<AlphabetType> Sigma=new TreeSet<AlphabetType>(NullComparator.NC);
    Sigma.addAll(getAlphabet());
    for(StateNameType sn:StateList) {
      ouf.println("|-");
      State<AlphabetType,StateNameType> S=Delta.get(sn);
      String prefix="";
      if(sn.equals(StartState)) prefix+=">";
      prefix+=(S.Accept?"*":" ");
      ouf.print("|"+prefix+sn+(S.MooreOutput!=null?(","+S.MooreOutput):"")+"||");
      
      for(AlphabetType j:Sigma) {
        Transition<StateNameType> T=S.get(j);
        if(T!=null) {
          switch(T.term) {
            case Continue:
              ouf.print(T.nextState+(T.MealyOutput!=null?(","+T.MealyOutput):""));
              break;
            case Crash:
            default:
              
          }
        }
        ouf.print(" ||");
      }
      if(S.comment!=null) ouf.print(S.comment);
      ouf.println();
      i++;
    }
    ouf.println("|}");
    return result.toString();
  }
  public String DotTransitionTable(String objectName) {
    StringWriter result=new StringWriter();
    PrintWriter ouf=new PrintWriter(result,true);
    //Header
    ouf.println("digraph "+objectName+" {");
    ouf.println("  rankdir=LR;");
    ouf.println("  {node [shape = point style=invis label=\"\"]; "+objectName+"_phantomStart;}");

    //Labels for all states including Moore output
    Set<StateNameType> StateList=getStateNames();
    for(StateNameType sn:StateList) {
      State<AlphabetType,StateNameType> S=Delta.get(sn);
      String label=sn+(S.MooreOutput!=null?"/"+S.MooreOutput:"");
      String shape="ellipse";
      String color=S.Accept?"peripheries=2":"";
      ouf.println("  {node [shape = "+shape+" label=\""+label+"\" "+color+"]; \""+sn+"\";}");
    }
    
    
    //Draw edges
    for(StateNameType sn:StateList) {
      if(sn.equals(StartState)) {
        ouf.println("  "+objectName+"_phantomStart -> \""+sn+"\" ;");
        
      }
      State<AlphabetType,StateNameType> S=Delta.get(sn);
      Map<StateNameType,Set<AlphabetType>> edgeGrouping=new LinkedHashMap<StateNameType,Set<AlphabetType>>();
      for(AlphabetType j:S.getAlphabet()) {
        Transition<StateNameType> T=S.get(j);
        Set<AlphabetType> thisEdgeGroup=edgeGrouping.get(T.nextState);
        if(thisEdgeGroup==null) {
          thisEdgeGroup=new LinkedHashSet<AlphabetType>();
          edgeGrouping.put(T.nextState, thisEdgeGroup);
        }
        thisEdgeGroup.add(j);
      }
      for(StateNameType next:edgeGrouping.keySet()) {
        ouf.println("  \""+sn+"\" -> \""+next+"\" [ label=\""+makeEdgeLabel(edgeGrouping.get(next))+"\" ];");
      }
    }
    ouf.println("}");
    return result.toString();
  }
  public String toString() {
	  return DotTransitionTable("this");
  }
  protected static String makeEdgeLabel(List<Character> edges,String delimiter) {
	  String result=null;
	  String thisGroup=null;
	  Character firstChar=null;
	  Character lastChar=null;
    edges=new ArrayList(edges);
    Collections.sort(edges);
	  int nGroup=0;
	  for(Character a:edges) {
	    if(nGroup==0) {
	      firstChar=a;
  	    lastChar=a;
	    } else if (((int)(a)-(int)lastChar)>1) {
	      //We have a group
	      thisGroup=makeGroup(firstChar,lastChar,nGroup);
  	    if(result==null) {
	        result=thisGroup;
	      } else {
	        result=result+delimiter+thisGroup;
	      }

  	    //Reset the group
	      firstChar=(Character)a;
	      nGroup=0; //Will be incremented later
  	  }
	    lastChar=(Character)a;
	    nGroup++;
  	}
	  thisGroup=makeGroup(firstChar,lastChar,nGroup);
  	if(result==null) {
	    result=thisGroup;
  	} else {
	    result=result+delimiter+thisGroup;
  	}
    return result;
  }
  public static String makeEdgeLabel(Set<?> edges) {
  	return makeEdgeLabel(edges,",");
  }
  //This is a static method, but edges should be Set<AlphabetType>
  public static String makeEdgeLabel(Set<?> edges,String delimiter) {
	  StringBuffer result=new StringBuffer("");
	  List<Character> Chars=null;
	  for(Object a:edges) {
      if(a instanceof Character) {
    	if(Chars==null) Chars=new LinkedList<Character>();
    	  Chars.add((Character)a);
        } else {
      	//Close off character group if any
      	if(Chars!=null) {
    	    result.append(delimiter+makeEdgeLabel(Chars,delimiter));
      	  Chars=null;
      	}
      	if(a==null) {
    	    result.append(delimiter+"null");
      	} else {
      	  result.append(delimiter+a.toString());
      	}
      }
	  }
	//Close off character group if any
	if(Chars!=null) {
	  result.append(delimiter+makeEdgeLabel(Chars,delimiter));
	  Chars=null;
	}
	if(delimiter.length()>0) {
	  if(result.length()>=delimiter.length()) {
  	    return(result.toString().substring(delimiter.length()));
	  } else {
	    return "";
	  }
	} else {
	  return result.toString();
	}
  }
  protected static String makeGroup(Character firstChar, Character lastChar, int nGroup) {
    String thisGroup;
    if(nGroup==1) {
      thisGroup=eescape(firstChar);
    } else if (nGroup==2) {
      thisGroup=eescape(firstChar)+","+eescape(lastChar);
    } else {
      thisGroup=eescape(firstChar)+"-"+eescape(lastChar);
    }
    return thisGroup;
  }
  protected static String escape(Object O) {
    return eescape(new Character(O.toString().charAt(0)));
  }
  protected static String eescape(Character C) {
    if(C==null) return "null";
    char c=C;
    if(c=='\0') return "\\\\0";
    if(c=='\t') return "\\\\t";
    if(c=='\n') return "\\\\n";
    if(c=='\r') return "\\\\r";
    if(c=='\f') return "\\\\f";
    if(c<=' ' || c>'~') return "\\\\u"+String.format("%04X", (int)c);
    if(c=='"') return "\\\"";
    if(c=='\\') return "\\\\";
    return ""+c;
  }
  protected static String escape(Character C) {
    if(C==null) return "null";
    char c=C;
    if(c=='\0') return "\\0";
    if(c=='\t') return "\\t";
    if(c=='\n') return "\\n";
    if(c=='\r') return "\\r";
    if(c=='\f') return "\\f";
    if(c<=' ' || c>'~') return "\\u"+String.format("%04X", (int)c);
    if(c=='"') return "\\\"";
    if(c=='\\') return "\\";
    return ""+c;
  }
// State reduction stuff
  private class Pair {
    StateNameType i,j;
    public Pair(StateNameType Li,StateNameType Lj) {i=Li;j=Lj;}
  }
  private void markDif(Map<StateNameType,Map<StateNameType,Boolean>> dif, Map<StateNameType,Map<StateNameType,List<Pair>>> asn, StateNameType i, StateNameType j) {
    dif.get(i).put(j,true);
    //If this pair has anything associated, mark all of them
    //as distinguishable also
    Map<StateNameType,List<Pair>> a=asn.get(i);
    if(a==null) return;
    List<Pair> PL=a.get(j);
    if(PL==null) return;
    for(Pair P:PL) markDif(dif,asn,P.i,P.j);
  }
  public String difToString(Map<StateNameType,Map<StateNameType,Boolean>> dif) {
    StringBuffer result=new StringBuffer("   ");
    Set<StateNameType> StateList=getStateNames();
    for(Object j:StateList) {
      result.append(j+" ");
    }
    int ii=0;
    for(Object i:StateList) {
      int jj=0;
      result.append("\n"+i+"|");
      for(Object j:StateList) {
        if(jj>ii) {
          result.append(dif.get(i).get(j)?" 1":" 0");
        } else {
          result.append("  ");
        }
        jj++;
      }
      ii++;
    }
    return result.toString();
  }
  boolean objDif(Object A, Object B) {
    if(A==null && B==null) return false;
    if(A==null || B==null) return true;
    return !A.equals(B);
  }
  static boolean optimizeVerbose=false;
  /** Performs an optimize-in-place. This uses the algorithm in the back of <a href="http://books.google.com/books?id=eijaAAAACAAJ&dq=introduction+to+Compiler+Construction">Parsons</a>
   * to find and remove redundant states. This includes an extension to the algorithm which distinguishes two states which are
   * otherwise indistinguishable, but have different Moore outputs, or transitions with different Mealy outputs. In this
   * way, the algorithm is extended to work properly on Mealy and Moore machines. The algorithm also considers two 
   * accepting states to be equivalent iff their Moore outputs are equal (including both null)
   */
  public void optimize(StateNameType Crash) {
    //Temporarily add a crash state, the state where you go if there are no transitions
    Set<StateNameType> StateList=getStateNames();
    State<AlphabetType,StateNameType> CrashState=new State<AlphabetType,StateNameType>();
    CrashState.Accept=true;
    for(AlphabetType c:getAlphabet())CrashState.put(c,new Transition<StateNameType>(Crash));
    Delta.put(Crash,CrashState);
    Map<StateNameType,Map<StateNameType,Boolean>> dif=new LinkedHashMap<StateNameType,Map<StateNameType,Boolean>>();
    
    for(StateNameType i:StateList) {
      dif.put(i,new LinkedHashMap<StateNameType,Boolean>());
    }
    Map<StateNameType,Map<StateNameType,List<Pair>>> asn=new LinkedHashMap<StateNameType,Map<StateNameType,List<Pair>>>();
    //Step 1 - Mark all distinguishable pairs
    int ii=0;
    for(StateNameType i:StateList) {
      int jj=0;
      for(StateNameType j:StateList) {
          State<AlphabetType,StateNameType> Si=Delta.get(i);
          State<AlphabetType,StateNameType> Sj=Delta.get(j);
          boolean isDif=Si.Accept!=Sj.Accept; //Accept states are different from non-accept states
          //Step 1b - If the Moore output of the states are different, the states are different
          isDif|=objDif(Si.MooreOutput,Sj.MooreOutput);
          //Step 1c - For transitions which have Mealy output, if the mealy outputs for any
          //          character are different, the states are different
          for(AlphabetType c:getAlphabet()) {
            Transition<StateNameType> Ti,Tj;
            Object Mei=null,Mej=null;
            Ti=Si.get(c);
            Tj=Sj.get(c);
            if(Ti!=null)Mei=Ti.MealyOutput;
            if(Tj!=null)Mej=Tj.MealyOutput;
            isDif|=objDif(Mei,Mej);
          }
          dif.get(i).put(j,isDif);
        jj++;
      }
      ii++;
    }
    //Make the crash state distinguishable from all others
    for(StateNameType j:StateList) {
      dif.get(j).put(Crash,true);
      dif.get(Crash).put(j,true);
    }
    if(optimizeVerbose)System.out.println(difToString(dif));
    //Step 2 - Deduce other distinguishable states
    ii=0;
    for(StateNameType i:StateList) {
      int jj=0;
      for(StateNameType j:StateList) {
        if(jj>ii) {
          //Is this pair known to be distinguishable yet?
          if(!dif.get(i).get(j)) {
            for(AlphabetType sym:getAlphabet()) {
              StateNameType pair1,pair2;
              State<AlphabetType,StateNameType> row=Delta.get(i);
              if(row!=null) {
                Transition<StateNameType> trans=row.get(sym);
                if(trans!=null) {
                  pair1=trans.nextState;
                } else {
                  pair1=Crash;
                }
              } else {
                pair1=Crash;
              }
              row=Delta.get(j);
              if(row!=null) {
                Transition<StateNameType> trans=row.get(sym);
                if(trans!=null) {
                  pair2=trans.nextState;
                } else {
                  pair2=Crash;
                }
              } else {
                pair2=Crash;
              }
              //Make sure to order the pair properly. All that matters is that the order is consistent.
              if(NullComparator.NC.compare(pair2,pair1)<0) {
                StateNameType t=pair2;
                pair2=pair1;
                pair1=t;
              }
              if(pair1.equals(i) && pair2.equals(j)) {
                // This tells us nothing, as the new pair is the same as the old
              } else if(pair1.equals(pair2)) {
                // These two are not distinguishable from this letter, but might be
                // for others, do nothing
              } else if(dif.get(pair1).get(pair2)) {
                // The new pair is distinguishable, so the old one is too.
                // Mark it and all its associated stuff such, recursively
                markDif(dif,asn,i,j);
              } else {
                //Don't know yet. Put the old pair into the new pairs list
                if(asn.get(pair1)==null) asn.put(pair1,new LinkedHashMap<StateNameType,List<Pair>>());
                if(asn.get(pair1).get(pair2)==null) asn.get(pair1).put(pair2,new ArrayList<Pair>());
                asn.get(pair1).get(pair2).add(new Pair(i,j));
              }
            }
          }
        }
        jj++;
      }
      ii++;
    }
    //We're done with the associations at this point, so let it go
    asn=null;
    if(optimizeVerbose)System.out.println(difToString(dif));
    //Step 3 - Remove the redundant states
    List<Set<StateNameType>> statesToMerge=new ArrayList<Set<StateNameType>>();
    ii=0;
    for(StateNameType state1:StateList) {
      int jj=0;
      for(StateNameType state2:StateList) {
        if(jj>ii) {
          Map<StateNameType,Boolean> dd=dif.get(state1);
          if(dd!=null) {
            Boolean bb=dd.get(state2); 
            if(bb!=null && !bb) {
              //These two states are equivalent
              //Remove the second row from the table
              if(optimizeVerbose)System.out.println("State "+state2+" is equivalent to state "+state1);
              boolean found=false;
              for(Set<StateNameType> states:statesToMerge) {
                if(states.contains(state1)) {
                  states.add(state2);
                  found=true;
                } else if(states.contains(state2)) {
                  states.add(state1);
                  found=true;
                }
              }
              if(!found) {
                Set<StateNameType> states=new LinkedHashSet<StateNameType>();
                states.add(state1);
                states.add(state2);
                statesToMerge.add(states);
              }
            }
          }
        }
        jj++;
      }
      ii++;
    }
    for(Set<StateNameType> states:statesToMerge) {
      StateNameType state1=null;
      for(StateNameType state2:states) {
        if(state1==null) {
          state1=state2; //Keep the first state
        } else {
          //Mercilessly <strike>purge</strike>merge the other states
          if(optimizeVerbose)System.out.println("State "+state2+" is merged into state "+state1);
          mergeState(state1,state2);
        }
      }
    }
    removeState(Crash);
  }

  public Map<StateNameType,Integer> stateNumbering() {
    Map<StateNameType,Integer> FwdStateList=new LinkedHashMap<StateNameType,Integer>();
    int i=0;
    if(StartState!=null) {
      FwdStateList.put(StartState, i);
      i++;
    }
    for(StateNameType stateName:getStateNames()) {
      if(!stateName.equals(StartState)) {
        FwdStateList.put(stateName,i);
        i++;
      }
    }
    return FwdStateList;
    
  }
  public Map<Integer,StateNameType> invStateNumbering() {
    return invStateNumbering(stateNumbering());
  }
  public Map<Integer,StateNameType> invStateNumbering(Map<StateNameType,Integer> FwdStateList) {
    Map<Integer,StateNameType> InvStateList=new TreeMap<Integer,StateNameType>();

    for(StateNameType stateName:FwdStateList.keySet()) {
      int i=FwdStateList.get(stateName);
      InvStateList.put(i,stateName);
    }
    return InvStateList;
  }
  public DFA<AlphabetType,Integer> numberStates() {
    return numberStates(stateNumbering());
  }
  public DFA<AlphabetType,Integer> numberStates(Map<StateNameType,Integer> FwdStateList) {
	  DFA<AlphabetType,Integer> result=new DFA<AlphabetType,Integer>(tape);

    if(StartState==null) {
      result.StartState=null;
    } else {
      result.StartState=FwdStateList.get(StartState);
    }
      
    if(CurrentState==null) {
      result.CurrentState=null;
    } else {
      result.CurrentState=FwdStateList.get(CurrentState);
    }
      
    //Construct the new state table
    for(StateNameType stateName:FwdStateList.keySet()) {
      State<AlphabetType,Integer> newState=new State<AlphabetType,Integer>();
      State<AlphabetType,StateNameType> oldState=Delta.get(stateName);
      newState.Accept=oldState.Accept;
      newState.comment=oldState.comment;
      newState.MooreOutput=oldState.MooreOutput;
      newState.useCount=oldState.useCount;
      for(AlphabetType symbol:oldState.getAlphabet()) {
    	  Transition<StateNameType> oldTrans=oldState.get(symbol);
    	  Transition<Integer> newTrans=new Transition<Integer>(FwdStateList.get(oldTrans.nextState));
    	  newTrans.MealyOutput=oldTrans.MealyOutput;
    	  newTrans.term=oldTrans.term;
    	  newTrans.useCount=oldTrans.useCount;
        newState.put(symbol,newTrans);
      }
      result.Delta.put(FwdStateList.get(stateName), newState);
    }
    return result;
  }
  public static void printDebug(String fn, String output) {
    PrintStream ouf;
    boolean gotFile=false;
    try {
      ouf=new PrintStream(new FileOutputStream(fn));
      gotFile=true;
    } catch (IOException e) {ouf=System.out;}
    ouf.println(output);
    if(gotFile) ouf.close();
  }
  /**
   * @param tape the tape to set
   */
  public void setTape(Tape<AlphabetType> tape) {
    this.tape = tape;
  }
  /**
   * @return the tape
   */
  public Tape<AlphabetType> getTape() {
    return tape;
  }
  /**
   * @return the currentState
   */
  public StateNameType getCurrentState() {
    return CurrentState;
  }
  public void setStartState(StateNameType startState) {
    StartState = startState;
  }
  public StateNameType getStartState() {
    return StartState;
  }
  public Set<AlphabetType> getAlphabet() {
    Set<AlphabetType> result=new TreeSet<AlphabetType>();
    for(State<AlphabetType,StateNameType> s:Delta.values()) {
      result.addAll(s.getAlphabet());
    }
    return result;
  }
  protected Map<StateNameType,State<AlphabetType,StateNameType>> Delta;
  public State<AlphabetType,StateNameType> get(StateNameType sn) {
    return Delta.get(sn);
  }
  public Transition<StateNameType> get(StateNameType sn, AlphabetType a) {
    return Delta.get(sn).get(a);
  }
  public void put(StateNameType sn, State<AlphabetType,StateNameType> newState) {
    Delta.put(sn,newState);
  }
  public void put(StateNameType sn, AlphabetType a, Transition<StateNameType> T) {
    Delta.get(sn).put(a,T);
  }
  public Set<StateNameType> getStateNames() {
    return Delta.keySet();
  }
  public void removeState(StateNameType stateToRemove) {
    //Remove a row of the state transition table
    Delta.remove(stateToRemove);
  }
  public void removeTransitions(StateNameType stateToRemove) {
    for(State<AlphabetType,StateNameType> S:Delta.values()) {
      for(Iterator<Transition<StateNameType>> I=S.getTransitions().iterator();I.hasNext();) {
        Transition<StateNameType> T=I.next();
        if(T.nextState.equals(stateToRemove)) I.remove();
      }
    }
    
  }
  public void eraseState(StateNameType stateToRemove) {
    removeState(stateToRemove);
    removeTransitions(stateToRemove);
  }
  public StateNameType mergeStateNames(StateNameType state1, StateNameType state2) {
    return state1;
  }
  public StateNameType mergeState(StateNameType state1, StateNameType state2) {
    for(State<AlphabetType,StateNameType> S:Delta.values()) {
      for(Transition<StateNameType> T:S.getTransitions()) {
        if(NullComparator.NC.compare(T.nextState,state2)==0)T.nextState=state1;
      }
    }
    removeState(state2);
    StateNameType stateNew=mergeStateNames(state1,state2);
    if(state1!=stateNew) {
      renameState(state1,stateNew);
    }
    if(NullComparator.NC.compare(StartState,state2)==0) StartState=stateNew;
    if(NullComparator.NC.compare(StartState,state1)==0) StartState=stateNew;
    if(CurrentState!=null && NullComparator.NC.compare(CurrentState,state2)==0) CurrentState=state1;
    return stateNew;
  }
  public void renameState(StateNameType oldName, StateNameType newName) {
    //Rename all the transitions to the old name
    for(State<AlphabetType,StateNameType> S:Delta.values()) {
      for(Transition<StateNameType> T:S.getTransitions()) {
        if(T.nextState.equals(oldName))T.nextState=newName;
      }
    }
    
    //Rename the state table row
    State<AlphabetType,StateNameType> S=Delta.get(oldName);
    Delta.remove(oldName);
    Delta.put(newName, S);

    //Rename the start state and current state if necessary
    if(StartState.equals(oldName)) StartState=newName;
    if(CurrentState!=null && CurrentState.equals(oldName)) CurrentState=newName;
  }
  public void clear() {
    Delta.clear();
  }
  public Map<StateNameType, State<AlphabetType, StateNameType>> getDelta() {
    return Delta;
  }
  public static void main(String args[]) throws IOException {
    DFA PascalLexer=DFA.LoadTransitionTable(new LineNumberReader(new StringReader(
        "{| border=1"+"\n"+
        "|     || l|| d|| {|| }|| (|| *|| )|| :|| =|| <|| >|| _|| p||"+"\n"+    
        "|  1  || 2|| 4|| 6||19|| 8||19||19||12||19||14||17|| 1||19||Starting state"+"\n"+
        "|  2  || 2|| 2|| 3|| 3|| 3|| 3|| 3|| 3|| 3|| 3|| 3|| 3|| 3||In identifier"+"\n"+
        "|* 3,y||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||End of Identifier"+"\n"+
        "|  4  || 5|| 4|| 5|| 5|| 5|| 5|| 5|| 5|| 5|| 5|| 5|| 5|| 5||In Number"+"\n"+
        "|* 5,y||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||End of Number"+"\n"+
        "|  6  || 6|| 6|| 6|| 7|| 6|| 6|| 6|| 6|| 6|| 6|| 6|| 6|| 6||In { comment"+"\n"+
        "|* 7,n||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||End of } comment"+"\n"+
        "|  8  ||20||20||20||20||20|| 9||20||20||20||20||20||20||20||Found ("+"\n"+
        "|  9  || 9|| 9|| 9|| 9|| 9||10|| 9|| 9|| 9|| 9|| 9|| 9|| 9||In (* comment"+"\n"+
        "| 10  || 9|| 9|| 9|| 9|| 9|| 9||11|| 9|| 9|| 9|| 9|| 9|| 9||Found * in (* comment"+"\n"+
        "|*11,n||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||End of *) comment"+"\n"+
        "| 12  ||20||20||20||20||20||20||20||20||13||20||20||20||20||Found :"+"\n"+
        "|*13,n||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||Token :="+"\n"+
        "| 14  ||20||20||20||20||20||20||20||20||15||20||16||20||20||Found <"+"\n"+
        "|*15,n||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||Token <="+"\n"+
        "|*16,n||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||Token <>"+"\n"+
        "| 17  ||20||20||20||20||20||20||20||20||18||20||20||20||20||Found >"+"\n"+
        "|*18,n||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||Token >="+"\n"+
        "|*19,n||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||General punctuation, no pushback"+"\n"+
        "|*20,y||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||  ||General punctuation, with pushback"+"\n"
    )));
    System.out.println(PascalLexer.WikiTransitionTable());

    DFA<Character,String> Minimize1=DFA.LoadTransitionTable(new LineNumberReader(new StringReader(
        "{| border=1"+"\n"+
        "|   || a|| b||"+"\n"+    
        "|  1|| 1|| 2||"+"\n"+
        "|  2|| 2|| 3||"+"\n"+
        "|* 3|| 1|| 4||"+"\n"+
        "|  4|| 1|| 5||"+"\n"+
        "|  5|| 1|| 5||"+"\n"
    )));
    
    DFA<Character,String> Minimize2=DFA.LoadTransitionTable(new LineNumberReader(new StringReader(
        "{| border=1"+"\n"+
        "|  ||a||b||c||"+"\n"+    
        "| 1||2||3||4||"+"\n"+
        "| 2||5||6||7||"+"\n"+
        "|*3||8||8||9||"+"\n"+
        "|*4||8||8||9||"+"\n"+
        "| 5||5||6||7||"+"\n"+
        "|*6||8||8||8||"+"\n"+
        "|*7||8||8||8||"+"\n"+
        "| 8||8||8||8||"+"\n"+
        "|*9||8||8||9||"+"\n"
    )));
    System.out.println(Minimize2.WikiTransitionTable());
    Minimize2.optimize("Crash");
    System.out.println(Minimize2.WikiTransitionTable());

    DFA<Character,String> Minimize3=DFA.LoadTransitionTable(new LineNumberReader(new StringReader(
        "{| border=1"+"\n"+
        "|   || a|| b||"+"\n"+    
        "|  1|| 2|| 3||"+"\n"+
        "|  2|| 5|| 3||"+"\n"+
        "|  3|| 5||  ||"+"\n"+
        "|  4|| 5|| 3||"+"\n"+
        "| *5|| 2|| 4||"+"\n"
    )));
    System.out.println(Minimize3.DotTransitionTable("Minimize3"));
    Minimize3.optimize("Crash");
    System.out.println(Minimize3.DotTransitionTable("Minimize3"));
  }
}
