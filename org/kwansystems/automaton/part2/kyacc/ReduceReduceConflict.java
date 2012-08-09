package org.kwansystems.automaton.part2.kyacc;

class ReduceReduceConflict extends IllegalArgumentException {
  int row;
  Object symbol;
  Production R1,R2;
  ReduceReduceConflict(int Lrow, Object Lsymbol, Production LR1, Production LR2) {
    super("Reduce-Reduce Conflict on row "+Lrow+", symbol "+Lsymbol+": Old production: "+LR1.toString()+", New production: "+LR2.toString());
    R1=LR1;
    R2=LR2;
    row=Lrow;
    symbol=Lsymbol;
  }
}
