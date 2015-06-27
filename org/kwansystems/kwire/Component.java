package org.kwansystems.kwire;

import java.awt.Graphics;
import java.io.*;
import java.util.*;

public abstract class Component implements Comparable<Component>, Serializable {
  List<Input> inputs;
  List<Output> outputs;
  int x,y;
  public Component(int Lx, int Ly) {
    x=Lx;
    y=Ly;
    inputs=new ArrayList<Input>();
    outputs=new ArrayList<Output>();
  }
  public boolean isSource() {
    return(inputs.size()==0);
  }
  public boolean isSink() {
    return(outputs.size()==0);
  }
  public void reset() {
    for(Input i:inputs) i.reset();
  }
  public abstract Object[] execute();
  public void step() {
    Object[] result=execute();
    for(int i=0;i<result.length;i++) {
      outputs.get(i).transmit(result[i]);
    }
  }

  public int compareTo(Component o) {
    if(x<o.x) return -1;
    if(x>o.x) return 1;
    if(y<o.y) return -1;
    if(y>o.y) return 1;
    return 0;
  }
  public Input getInput(int i) {
    return inputs.get(i);
  }
  public Output getOutput(int i) {
    return outputs.get(i);
  }
  public abstract void drawCore(Graphics G, int xofs, int yofs);
  public void draw(Graphics G, int xofs, int yofs) {
    drawCore(G,xofs,yofs);
    for(Output o:outputs) for(Wire w:o) {
      w.draw(G,xofs,yofs);
    }
  }
}
