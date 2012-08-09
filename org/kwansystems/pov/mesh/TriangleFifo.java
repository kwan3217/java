package org.kwansystems.pov.mesh;

import java.util.ArrayList;


public class TriangleFifo {
  ArrayList<Triangle> A;
  int NextPointer;
  public TriangleFifo() {
    A=new ArrayList<Triangle>();
    NextPointer=0;
  }
  public boolean hasStuff() {
    return(NextPointer<A.size());
  }
  public void add(Triangle T) {
    A.add(T);
  }
  public Triangle get() {
    Triangle T=A.get(NextPointer);
    NextPointer++;
    return T;
  }
}