package org.kwansystems.tools;

import org.kwansystems.tools.vector.MathVector;

public class SystemOutVerbose implements Verbose {
  public void PrintNumber(String Label, double Value) {
    System.out.println(Label+Value);
  }
  public void PrintVector(String Label, MathVector Value) {
    System.out.println(Label+Value.toString());
  }
  public void Print(String Label) {
    System.out.print(Label);
  }
  public void Println(String Label) {
    System.out.println(Label);
  }
}
