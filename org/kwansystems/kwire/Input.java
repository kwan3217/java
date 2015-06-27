package org.kwansystems.kwire;

public class Input {
  private Component parent;
  private Object state;
  private boolean stimulated;
  int rx,ry;
  public Input(int Lrx, int Lry, Component Lparent, Object Lstate) {
    rx=Lrx;
    ry=Lry;
    parent=Lparent;
    state=Lstate;
  }
  public Object getState() {
    return state;
  }
  public void reset() {
    stimulated=false;
  }
  public void set(Object newState) {
    state=newState;
  }
  public boolean stimulate() {
    stimulated=true;
    for(Input i:parent.inputs) {
      if(!i.stimulated) return false;
    }
    return true;
  }
  public Component getParent() {
    return parent;
  }
}
