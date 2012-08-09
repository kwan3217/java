package org.kwansystems.tools;

import org.kwansystems.tools.vector.MathVector;

public interface Verbose {
  public void PrintNumber(String Label, double Value);
  public void PrintVector(String Label, MathVector Value);
  public void Print(String Label);
  public void Println(String Label);
}
