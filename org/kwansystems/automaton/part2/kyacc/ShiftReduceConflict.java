package org.kwansystems.automaton.part2.kyacc;

/**
 * Thrown when a parser generator encouters an unhandled Shift-Reduce conflict
 * when building a parser table. This error indicates that the grammar is not in
 * the class of grammars the parser generator can handle (for instance an SLR(1)
 * parser generator trying to handle a grammar which is not SLR(1)). Frequently
 * conflicts of this kind indicate that some token precedence is needed.
 * @author jeppesen
 */
class ShiftReduceConflict extends IllegalArgumentException {
  int row;
  Object s;
  Production R;
  ShiftReduceConflict(Object LS, Production LR) {
    super("Shift-Reduce Conflict: Shift "+LS.toString()+", Reduce: "+LR.toString());
    s=LS;
    R=LR;
  }

}
