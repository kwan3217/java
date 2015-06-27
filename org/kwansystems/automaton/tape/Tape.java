package org.kwansystems.automaton.tape;

import org.kwansystems.automaton.*;

/**
 * Automaton tape. Represents an input or read/write tape with tape head as used
 * by the automata in this package. Conceptually, a tape consists of a
 * (potentially infinite) string of cells, each of which can hold a single
 * symbol from the tape alphabet. A tape also includes a read/write head, which
 * can move back and forth along the tape, read the symbol in the cell currently
 * under the tape head, and perhaps write a new symbol on the tape head,
 * obliterating the old contents in the process.
 * <p>
 * Whenever the tape head is commanded to move, it may at its option <i>crash</i>
 * instead. This can be to enforce things like ends of the tape, or due to an
 * error in the backing store, or whatever. The Tape must make a best-effort to
 * crash on moving, but sometimes it can't, so it can crash when it reads
 * instead.
 * <p>
 * The general contract on crashing is that once the tape crashes, it stays
 * crashed. There is no way, short of throwing the tape away and starting over,
 * to un-crash a tape. In particular, if any of read, write, left, or right
 * decide to crash, any further call to any of read, write, left, or right must
 * crash as well. Furthermore, any call to read which crashes, either originally
 * or due to a pre-existing crash condition, must return the blank symbol.
 * This can only be reversed with a well-defined reset mechanism, which
 * effectively reconstructs the whole tape.
 * <p>
 * Tapes have a <i>backing store</i>, some kind of memory where the tape symbols
 * are kept. The implementations of this interface may use any backing store
 * they feel like, including such things as arrays, java Collections, streams,
 * readers, whatever. Implementations are free even to not keep track of the
 * entire tape, but to return Crash terminations when the automaton tries to
 * move the tape head to a part of the tape which is no longer available. This
 * will limit the usefulness of the tape to some automata, but other types will
 * be able to handle this kind of limitation without issue.
 * <p>
 * In accordance with automaton theory, the tape can only be manipulated by a 
 * certain small set of primitives. The tape head may be moved a definite number
 * of cells in either direction, but not commanded to move to a particular cell,
 * or to search for and stop on a particular symbol. This move will fail if it
 * is off the end of the tape.
 * <p>
 * The tape may have an infinite number of cells, but may be one-way infinite.
 * In other words, it may have one definite left end, but no definite right end.
 * A command to move off of either finite end the tape will be detected and
 * should cause the automaton to crash.
 * <p>
 * This interface specifies a read/write tape. It is up to the automaton to not 
 * call the write method if the tape is to be considered read-only. Specific
 * implementations of Tape may enforce this by throwing an exception when
 * its write method is called. It makes no logical sense to define a write-only
 * tape, but if it did, you could accomplish this by throwing an exception in
 * the read method.
 * <p>
 * If the tape is considered to be infinite, then all tape cells must have some
 * symbol written in them, even if they are considered to be blank. Therefore,
 * this interface supports the concept of a <i>blank symbol</i>, the symbol
 * written to all but a finite number of cells before an automaton starts
 * working on it. This symbol is just like all other symbols. How it works in
 * fact is up to the implementation, but typically it could be something like
 * detecting if you are off the end of the backing store, and either extending
 * the  backing store and writing a blank symbol on it, or just remembering that
 * you are off the backing store and returning the blank symbol instead of
 * reading the backing store. This blank symbol should probably not be changed
 * during the run of an automaton.  
 *
 * @param <AlphabetType> Type of symbol stored on the tape. It is good if these 
 * symbols are immutable. It probably will jam the works of whatever machine is
 * reading this tape if the symbols muted during their run. The automaton may
 * choose to only accept or write a subset of this alphabet as its own input
 * and/or output alphabet.
 */
public interface Tape<AlphabetType> {
  /** Read the tape.
   * @return Value previously written on the tape at this point, or the blank 
   * symbol if this cell has never been written to. If the tape crashes due
   * to this read, or has ever crashed in the past, the read will be the
   * blank symbol, for automata which wish to believe the tape is infinite.
   */
  public AlphabetType read();

  /** Write on the tape. If the tape head is to the right of any previously
   * written cells, the cells between there and here will be filled in with
   * blanks.
   * @param newSymbol new value to be written on the tape.
   * @return Termination.Continue if the write worked, otherwise
   * Termination.Crash
   */
  public Termination write(AlphabetType newSymbol);

  /** Move the tape head one space to the right. 
   * @return Termination.Crash if the tape head is moved off the right edge (if
   * there is one), Termination.Continue otherwise.
   */
  public Termination Right();

  /** Move the tape head one space to the left.
   * @return Termination.Crash if the tape head is moved off the left edge if
   * there is one, Termination.Continue otherwise.
   */
  public Termination Left();
  public TapeDisplay<AlphabetType> getTapeDisplay();
  public void setBlankSymbol(AlphabetType blank);
  public AlphabetType getBlankSymbol();
}