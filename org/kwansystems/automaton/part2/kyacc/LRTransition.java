package org.kwansystems.automaton.part2.kyacc;

public class LRTransition {
  public enum LRTransitionType {S,G,R,ACC}
  public LRTransitionType type;
  public int to;
  public LRTransition(LRTransitionType Ltype,int Lto) {
    type=Ltype;
    to=Lto;
  }
  public LRTransition(LRTransitionType Ltype) {
    this(Ltype,-1);
  }
  public LRTransition(int Lto) {
    this(LRTransitionType.G,Lto);
  }
  public String toString() {
    if(type!=LRTransitionType.ACC) {
      return String.format("%s%02d", type.toString(),to);
    } else {
      return type.toString();
    }
  }
}
