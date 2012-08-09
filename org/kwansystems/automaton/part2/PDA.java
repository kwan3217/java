package org.kwansystems.automaton.part2;

import java.io.*;
import java.util.*;

import org.kwansystems.automaton.AutomatonException;
import org.kwansystems.automaton.Termination;
import org.kwansystems.automaton.part1.DFA;
import org.kwansystems.automaton.part1.MealyListener;
import org.kwansystems.automaton.part1.MooreListener;
import org.kwansystems.automaton.tape.StringTape;
import org.kwansystems.automaton.tape.Tape;

/** Deterministic Pushdown Automaton (PDA) with a single stack. A Pushdown Automaton controller is implemented as a 
 * FiniteStateMachine with the Mealy output reserved to push stuff onto the stack, and the Moore output reserved. 
 * to tell which input source to read from, stack or tape. 
 */
public class PDA<AlphabetType,StateNameType> extends DFA<AlphabetType,StateNameType> implements MealyListener, MooreListener {
  private static final long serialVersionUID = -7889973056523506194L;
  Stack<AlphabetType> stack;
  AlphabetType stackBlank;
  String nextReadSource=null;
  public PDA(Tape<AlphabetType> Ltape) {
    super(Ltape);
    addMealyListener(this);
    addMooreListener(this);
    stack=new Stack<AlphabetType>();
    addAutomatonListener(new TextPDAListener<AlphabetType,StateNameType>(System.out));
  }
  public PDA(DFA<AlphabetType,StateNameType> source) {
    this(source.getTape());
    Delta=source.getDelta();
  }
  /** Catch the Mealy output of the controller.
   *
   * @param O Mealy output for this transition. The Mealy output for each 
   * transition is expected to be a String. Each character in the string will
   * be pushed onto the stack, left-most first. A null or zero-length string is valid
   * and will result in no character being pushed onto the stack. Mealy output other
   * than a string will be converted to a string using the toString method of the object.
   */
  public void OutputMealy(Object O) {
    if(O==null) return;
    String S=O.toString();
    if(O instanceof String) {
      for(Character c:S.toCharArray()) {
        stack.push((AlphabetType)c);
      }      
    } else {
      stack.push((AlphabetType)O);
    }
  }
  /** Catch the Moore output of the controller.
   *
   * @param O Moore output for this state. The Moore output for each 
   * state is expected to be a string, either "Read", "Halt", or "Pop".
   */
  public void OutputMoore(Object O) {
    if(O==null) throw new IllegalArgumentException("You must specify either Read or Pop in every state.");
    nextReadSource=(String)O; 
  }
  protected Termination AdvanceTape() {
    if(nextReadSource.equalsIgnoreCase("Halt")) return Termination.Accept;
    if(nextReadSource.equalsIgnoreCase("Read")) getTape().Right();
    return Termination.Continue;
  }
  protected AlphabetType read() throws AutomatonException {
    if(nextReadSource.equalsIgnoreCase("Read")) return getTape().read();
    if(nextReadSource.equalsIgnoreCase("Pop") && !stack.empty()) return stack.pop();
    return stackBlank;
  }
  PrintStream ouf=System.out;
  public static void main(String args[]) throws IOException {
    PDA<Character,String> Test1=new PDA<Character,String>(DFA.LoadTransitionTable(new LineNumberReader(new StringReader(
        "{| border=1"+"\n"+
        "|        || (  || )|| _ ||"+"\n"+    
        "|  1,Read|| 1,(|| 2|| 3 ||"+"\n"+
        "|  2,Pop || 1  ||  || 4 ||"+"\n"+
        "|  3,Pop ||    ||  || 5 ||"+"\n"+
        "|  4,Halt||    ||  ||   ||"+"\n"+
        "|* 5,Halt||    ||  ||   ||"+"\n"
    ))));
    Test1.stackBlank='_';
    Test1.setTape(new StringTape("((())())"));
    Test1.setStartState("1");
    Test1.Start();
  }
}
