package org.kwansystems.automaton.part1.regexp;

public class Optional extends Choice {
  public Optional(RegExpTree Lchild) {
    super(null);
    children=new RegExpTree[2];
    children[0]=new Epsilon();
    children[1]=Lchild;
  }
}
