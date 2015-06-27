package org.kwansystems.kwire;

import java.io.*;
import java.awt.*;

public class Wire implements Serializable {
  Output start;
  Input end;
  Object lastTransmit;
  int[] x,y;
  public Wire(Output Lstart, int[] Lx, int[] Ly, Input Lend) {
    start=Lstart;
    end=Lend;
    x=Lx;
    y=Ly;
  }
  public Wire(Output Lstart, int[][] Lxy, Input Lend) {
    start=Lstart;
    end=Lend;
    x=new int[Lxy.length];
    y=new int[Lxy.length];
    for(int i=0;i<x.length;i++) {
      x[i]=Lxy[i][0];
      y[i]=Lxy[i][1];
    }
  }
  public Wire(Output Lstart, Input Lend) {
    this(Lstart,new int[] {}, new int[] {},Lend);
  }
  public void transmit(Object out) {
    lastTransmit=out;
    end.set(out);
  }
  public boolean stimulate() {
    return end.stimulate();
  }
  public void draw(Graphics G, int xofs, int yofs) {
    if(lastTransmit instanceof Boolean) {
      if((Boolean)lastTransmit) {
        G.setColor(Color.RED);
      } else {
        G.setColor(Color.BLACK);
      }
    } else {
      
    }
    int lastx=start.rx+start.getParent().x;
    int lasty=start.ry+start.getParent().y;
    for(int i=0;i<x.length;i++) {
      G.drawLine(lastx+xofs, lasty+yofs, x[i]+xofs, y[i]+yofs);
      lastx=x[i];
      lasty=y[i];
    }
    G.drawLine(lastx+xofs,lasty+yofs,end.rx+end.getParent().x+xofs,end.ry+end.getParent().y+yofs);
  }
}
