package org.kwansystems.automaton.part3;

import java.io.*;

import org.kwansystems.automaton.AutomatonException;
import org.kwansystems.automaton.Termination;
import org.kwansystems.automaton.part1.DFA;
import org.kwansystems.automaton.part1.MealyListener;
import org.kwansystems.automaton.part1.TextDFAListener;
import org.kwansystems.automaton.tape.ArrayListTwoWayTape;
import org.kwansystems.automaton.tape.Tape;

/**
 * Implements a Turing Machine, and in the process proves the JVM and Java 
 * language is Turing complete. This builds on top of the DFA
 * class, and uses the DFA's Mealy output to hold the action to perform
 * on each transition.
 */
public class TuringMachine<StateNameType> extends DFA<Character,StateNameType> implements MealyListener {
  private static final long serialVersionUID = 7848145381498027970L;
  char Direction;
  char newSymbol;
  /** Catch the Mealy output of the controller.
   * <p>
   * A Turing Machine controller is implemented as a FiniteStateMachine
   * with the Mealy output reserved to control the tape output and movement.
   *
   * @param O Mealy output for this transition. The Mealy output for each 
   * transition is expected to be of the form D,n where D is either the 
   * character 'L' or 'R', and n is the character to be written.
   */
  public void OutputMealy(Object O) {
    String S=(String)O;
    newSymbol=S.charAt(0);
    Direction=S.charAt(2);
  }
  protected Termination AdvanceTape() {
    //Write the new symbol on the tape
    try {
      getTape().write(newSymbol);
    } catch (AutomatonException e) {throw new RuntimeException(e);}
    //Move the tape in the indicated direction
    try {
      switch(Direction) {
        case 'L':
          getTape().Left();
          break;
        case 'R':
          getTape().Right();
          break;
      }
    } catch(IllegalArgumentException E) {return Termination.Crash;}
    return Termination.Continue;
  }
  public TuringMachine(Tape<Character> Ltape) {
    super(Ltape);
    addMealyListener(this);
  }
  public TuringMachine() {
    this((Tape<Character>)null);
  }
  public TuringMachine(DFA<Character,StateNameType> source) {
    this(source.getTape());
    Delta=source.getDelta();
    setStartState((StateNameType) source.getStartState());
    setCurrentState((StateNameType) source.getCurrentState());
  }
  public static void main(String[] args) throws IOException {
//  TextTuringMachine TM=new TextTuringMachine(new PrintStream(new FileOutputStream("Data/Turing/UTM.blank.turing.trace")));
//  TM.LoadTransitionTable(new LineNumberReader(new FileReader("Data/Turing/UTM.blank.turing")));
//  TM.tape.Load("ccK0c11R0c11R1cc0c0c111R1cc1111R_c111R0c111R1cc0c0c0ccc@10");
    TuringMachine<String> TM=new TuringMachine<String>(DFA.LoadTransitionTable(new LineNumberReader(new FileReader("Data/Turing/BusyBeaver4.turing"))));
    TM.setTape(new ArrayListTwoWayTape<Character>());
    TM.addAutomatonListener(new TextDFAListener<Character,String>(System.out/*new PrintStream(new FileOutputStream("Data/Turing/Wolfram23.turing.trace"))*/));
//TM.LoadTransitionTable(new LineNumberReader(new FileReader("Data/Turing/BusyBeaver3.turing")));
//TM.tape.blankSymbol='0';
    TM.getTape().setBlankSymbol('0');
//    TM.getTape().Load(" ");
//TM.tape.Load("ccK0c11R0c11R1cc0c0c111R1cc1111R_c111R0c111R1cc0c0c0ccc@10");
  
//  TextTuring TM=new TextTuring(new PrintStream(new FileOutputStream("anbn.turing.trace")));
//  TM.LoadTransitionTable(new LineNumberReader(new FileReader("anbn.turing")));
    TM.ShowComment("Number of states: "+TM.numStates);
    TM.ShowComment("Number of transitions: "+TM.numTransitions);
//  TM.optimize();
    TM.ShowComment(TM.WikiTransitionTable());
//  TM.tape.Load("aaabbb");
//  TM.flattenSymbols(new char[] {'0','1'});
    TM.RunToAccept();
  }
}
  
