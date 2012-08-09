package org.kwansystems.automaton.part1.regexp;

public class Star extends Optional {
  public Star(RegExpTree Lchild) {
    super(new Plus(Lchild));
  }
}
