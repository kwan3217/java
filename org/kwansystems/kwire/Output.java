package org.kwansystems.kwire;

import java.util.*;

public class Output extends ArrayList<Wire>{
  private Component parent;
  Object lastTransmit;
  int rx,ry;
  public Output(Component Lparent, int Lrx, int Lry) {
    parent=Lparent;
    rx=Lrx;
    ry=Lry;
  }
  public void transmit(Object out) {
    lastTransmit=out;
    for(Wire w:this) {
      w.transmit(out);
    }
  }
  public void addWire(Input input) {
    add(new Wire(this,input));
  }
  public void addWire(Input input, int[] x, int[] y) {
    add(new Wire(this,x,y,input));
  }
  public void addWire(Input input, int[][] xy) {
    add(new Wire(this,xy,input));
  }
  public Component getParent() {
    return parent;
  }
}
