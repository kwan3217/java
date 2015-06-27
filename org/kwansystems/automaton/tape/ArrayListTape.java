package org.kwansystems.automaton.tape;

import java.util.*;

import org.kwansystems.automaton.Termination;

/**
 * One-way infinite tape using an ArrayList as a backing store.
 * <p>
 * The tape has an infinite number of cells, but it is one-way infinite. In other words, it has one definite 
 * endpoint to the left of cell 0, but no definite right endpoint. A command to move off the left end of the tape
 * will be detected and should cause the automaton to crash. If it is not detected, the tape will cause a Java 
 * ArrayBoundsException when it tries to read or write off the left end of the tape. 
 * <p>
 * In fact, the right end is limited by available memory first, and the 2^31 limit on array size second. However, 
 * these limits are enforced by the built-in Java exceptions, not anything here.
 * <p>
 * This tape is read/write. It is up to the automaton to not call the write method if the tape is to be 
 * considered read-only.
 */
public class ArrayListTape<AlphabetType> extends ArrayList<AlphabetType> implements Tape<AlphabetType> {
  private static final long serialVersionUID = -5332730588412260021L;
  /** Current location of the tape head */
  protected int tapeHead;
  public boolean isFinite;
  private boolean isCrashed;
  /** Symbol present on all cells of the tape not yet written to. This value may be written back freely at the 
   * discretion of the automaton. This symbol should be set before the automaton runs, outside of the automaton
   * mechanism, and not changed by anything while the automaton is running. */
  protected AlphabetType blankSymbol;
  public void setBlankSymbol(AlphabetType b) {
    blankSymbol=b;
  }
  public ArrayListTape() {
    super();
    isCrashed=false;
  }
  /* (non-Javadoc)
   * @see org.kwansystems.automaton.Tape#read()
   */
  public AlphabetType read() {
    if(tapeHead>=size()) {
      write(blankSymbol);
    }
    return get(tapeHead);
  }
  
  /* (non-Javadoc)
   * @see org.kwansystems.automaton.Tape#write(char)
   */
  public Termination write(AlphabetType newSymbol) {
    while(tapeHead>=size()) add(blankSymbol);
    set(tapeHead,newSymbol);
    return Termination.Continue;
  }
  /** Get cell number of current tape head position. This method is not on the approved list of operations for 
   * automata, and MUST NOT BE USED. It is only appropriate to use this outside of the context of an automaton.
   * For instance, a debugger will want to display the tape, and in order to do so, it must know where the head
   * is so it can properly display it.
   * @return tape position, 0 for left end of tape
   */
  public int getPos() {
    return tapeHead;
  }
  /** Move the tape head an arbitrary distance.
   * @param delta Distance and direction to move. Tape head moves <tt>abs(delta)</tt> spaces, to the left if 
   * delta&lt;0, to the right if delta&gt;0. If delta is zero, the tape head will remain in the same spot, but
   * otherwise the move will complete successfully
   * @return Termination.Crash if the tape head is moved off the left edge (tapeHead&lt;0) of a one-way tape, 
   * Termination.Continue otherwise. The automaton should look at this return value and crash if appropriate.
   * If it doesn't, and it attempts to read or write to the tape after it has moved off the end, it is likely 
   * that the Java machine will throw an Exception on the next read anyway, depending on the underlying tape 
   * storage.
   */
  public Termination Move(int delta) {
    if(isCrashed) return Termination.Crash;
    tapeHead+=delta;
    if(isFinite && tapeHead>=size()) {
      isCrashed=true;
      return Termination.Crash;
    }
    if(tapeHead<0) {
      isCrashed=true;
      return Termination.Crash;
    }
    return Termination.Continue;
  }
  /**
   * @see org.kwansystems.automaton.tape.Tape#Right()
   */
  public Termination Right() {
    return Move(1);
  }
  /** 
   * @see org.kwansystems.automaton.tape.Tape#Left()
   */
  public Termination Left() {
    return Move(-1);
  }
  /**
   * @return the blankSymbol
   */
  public AlphabetType getBlankSymbol() {
    return blankSymbol;
  }
  public TapeDisplay<AlphabetType> getTapeDisplay() {
    TapeDisplay<AlphabetType> result=new TapeDisplay<AlphabetType>();
    result.tapeData=new ArrayList<AlphabetType>(size());
    for(int i=0;i<size();i++) {result.tapeData.add(get(i));}
    result.pointers=new int[] {tapeHead};
    result.pointerNames=new String[] {"Tape Head"};
    return result;
  }
}
