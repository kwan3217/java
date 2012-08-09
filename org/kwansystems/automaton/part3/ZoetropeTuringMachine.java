package org.kwansystems.automaton.part3;

import java.awt.Graphics;
import java.io.*;
import java.util.*;

import org.kwansystems.automaton.Termination;
import org.kwansystems.automaton.Transition;
import org.kwansystems.automaton.tape.ArrayListTwoWayTape;
import org.kwansystems.automaton.tape.Tape;
import org.kwansystems.tools.zoetrope.*;

public class ZoetropeTuringMachine extends TuringMachine {
  Graphics G;  
  Zoetrope Z;
  protected void ShowTape() {
    /*
    ouf.print("Tape: ");
    for(char c : getTape()) ouf.print(c);
    ouf.println();
    ouf.print("      ");
    for(int i=0;i<getTape().getPos();i++) ouf.print(" ");
    ouf.println("^");
    */
  }
  protected void ShowCurrentState() {
    /*
    G.drawString(getCurrentState(), Z.X(0), Z.Y(-1));
    */
  }
  protected void ShowTransition(Transition T) {
    /*
    ouf.println("Transition: "+T);
    */
  }
  public ZoetropeTuringMachine() {
    super();
  }
  public ZoetropeTuringMachine(Tape Ltape) {
    super(Ltape);
  }
  public void run() {
    Zoetrope LZ=new Zoetrope("Zoetrope Trace", 1000) {
      Termination T=Termination.Continue;
      @Override
      protected void paintFrame(Graphics LG) {
        G=LG;
        Termination T=step();
        if(T!=Termination.Continue) {
          
        }
      }
    };
    Z=LZ;
    Z.start();
  }
  public static void main(String[] args) throws IOException {
    ZoetropeTuringMachine TM=new ZoetropeTuringMachine(new ArrayListTwoWayTape());
    TM.LoadTransitionTable(new LineNumberReader(new FileReader("Data/Turing/Wolfram23.turing")));
    TM.getTape().setBlankSymbol('W');
    TM.run();
  }
}
