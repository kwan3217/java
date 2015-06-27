package org.kwansystems.kwire;

import java.awt.Graphics;
import java.util.*;

import javax.swing.*;

public class Board extends JPanel {
  List<Component> components;
  public Board() {
    components=new ArrayList<Component>();
  }
  public void compile() {
    // Input is an initial list containing all components, not necessarily sorted. 
    // Two sorted sets are created, called ready and unready. Sorting order is always left to right, then top to bottom.
    SortedSet<Component> ready=new TreeSet<Component>(); //Contains all components which have all their inputs stimulated 
    SortedSet<Component> unready=new TreeSet<Component>();  //Contains all components which do not have all their inputs stimulated, which have not been run
    // One initially empty list is created, called ''finished''
    List<Component> finished=new ArrayList<Component>(); //Contains all components which have been run, in the order that they are to be run

    // For each component in the input list...
    for(Component c:components) {
      c.reset();
    // *If it is a source, it is ready, and vice versa
      if(c.isSource()) {
        ready.add(c);
      } else {
        unready.add(c);
      }
    }
    
    // While there are still items not in the finished list
    while(finished.size()!=components.size()) {
      //Deal with all the ready items
      while(ready.size()>0) {
        Component c=ready.first();
        ready.remove(c);
        finished.add(c);
        for(Output o:c.outputs) {
          for(Wire w:o) {
            if(w.end.stimulate() && unready.contains(w.end.getParent())) {
              ready.add(w.end.getParent());
              unready.remove(w.end.getParent());
            }
          }
        }
      }
      //Take the first item out of the unready set and deal with it
      if(unready.size()>0) {
        Component c=unready.first();
        unready.remove(c);
        finished.add(c);
        for(Output o:c.outputs) {
          for(Wire w:o) {
            if(w.end.stimulate() && unready.contains(w.end.getParent())) {
              ready.add(w.end.getParent());
              unready.remove(w.end.getParent());
            }
          }
        }
      }
    }
    
    //finished is now in proper execution order
    components=finished;
  }
  public void step() {
    for(Component c:components) {
      c.step();
    }
  }
  public static void main(String[] args) throws InterruptedException {
    Board B=new Board();
    JFrame F=new JFrame("KWire");
    F.add(B);
    Component O1=new OR(20,20);
    Component O2=new OR(20,60);
    Component N1=new NOT(50,20);
    Component N2=new NOT(50,60);
    B.components.add(N1);
    B.components.add(N2);
    B.components.add(O1);
    B.components.add(O2);
    O1.getOutput(0).addWire(N1.getInput(0));
    O2.getOutput(0).addWire(N2.getInput(0));
    N1.getOutput(0).addWire(O2.getInput(0),new int[][] {{80, 30},{80,45},{10,55},{10,65}});
    N2.getOutput(0).addWire(O1.getInput(1),new int[][] {{80, 70},{80,55},{10,45},{10,35}});
    Switch S=new Switch(0,20,false);
    B.components.add(S);
    S.getOutput(0).addWire(O1.getInput(0));
    Switch R=new Switch(0,70,false);
    B.components.add(R);
    R.getOutput(0).addWire(O2.getInput(1));
    Light Q=new Light(90,25);
    Light Qb=new Light(90,65);
    N1.getOutput(0).addWire(Q.getInput(0));
    N2.getOutput(0).addWire(Qb.getInput(0));
    B.components.add(Q);
    B.components.add(Qb);
    B.compile();
    B.setPreferredSize(new java.awt.Dimension(400, 400));
    F.pack();
    F.setVisible(true);
    F.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    while(true) {
      for(int i=0;i<10;i++) {
        S.set(i==2);
        R.set(i==6);
        B.step();
        B.repaint();
//        try {
          Thread.sleep(1000);
//        }
 //       System.out.printf("%d %b\n",i,Q.getInput(0).getState());
      }
    }
  }
  public void paintComponent(Graphics G) {
    super.paintComponent(G);
    for(Component c:components) {
      c.draw(G,0,0);
    }
  }
}
