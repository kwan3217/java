package org.kwansystems.graph;

public class DirectedEdge {
  Vertex from,to;
  public int weight() {
    return 1;
  }
  @Override
  public String toString() {
    return "{"+from.toString()+"->"+to.toString()+")";
  }
  public DirectedEdge(Vertex Lfrom, Vertex Lto) {
    from=Lfrom;
    to=Lto;
  }
  public String DotTransitionTable() {
    return("  \""+from.toString()+"\" -> \""+to.toString()+"\";");
  }
}
