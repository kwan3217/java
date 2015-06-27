package org.kwansystems.automaton.tape;

import org.kwansystems.automaton.Termination;

/**
 * Two-way infinite tape. The tape has an infinite number of cells, and no end in either direction. A command to
 * move off the "left end" of the tape will automatically insert enough blanks on the left end of the tape to
 * allow the next read to succeed. Note that this renumbers all the cells of the tape. Your automaton should not
 * be using tape cell numbers, remember? 
 * <p>
 * In fact, the total tape size is limited by available memory first, and the 2^31 limit on array size second. 
 * However, these limits are enforced by the built-in Java exceptions, not anything here.
 */
public class ArrayListTwoWayTape<AlphabetType> extends ArrayListTape<AlphabetType> {
  private static final long serialVersionUID = 7873849191173806450L;
  public int tapeOffset=0;
  /** Move the tape head an arbitrary distance. If the move is off the left end, add enough blanks to the left
   * end to allow the move.
   * @param delta Distance and direction to move. Tape head moves <tt>abs(delta)</tt> spaces, to the left if 
   * delta&lt;0, to the right if delta&gt;0. If delta is zero, the tape head will remain in the same spot, but
   * the move will still complete successfully
   * @return Termination.Continue always, since the tape head can't fall off of the left end.
   */
  public Termination Move(int delta) {
    tapeHead+=delta;
    while(tapeHead<0) {
      add(0,getBlankSymbol());
      tapeHead++;
      tapeOffset++;
    }
    return Termination.Continue;
  }
}
