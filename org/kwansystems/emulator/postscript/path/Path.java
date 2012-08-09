package org.kwansystems.emulator.postscript.path;

import java.util.*;

public class Path implements Iterable<PathElement> {

  private LinkedList<PathElement> elements;

  public boolean isClosed() {
    return elements.getLast() instanceof ClosePath;
  }
  public Iterator<PathElement> iterator() {
    return elements.iterator();
  }
  public double[] getCurrentPoint() {
    return elements.getLast().getEnd();
  }
  public double[] getCurrentSubpathStart() {
    double[] start=null;
    for(PathElement pe:elements) {
      if(pe instanceof MoveTo || pe instanceof ClosePath) {
        start=pe.getEnd();
      }
    }
    return start;
  }
  public Path() {
    elements=new LinkedList<PathElement>();
  }
  public Path(Path Lsource) {
    elements=new LinkedList<PathElement>(Lsource.elements);
  }
  public void add(PathElement P) {
    elements.add(P);
  }
  public int size() {
    return elements.size();
  }
}
