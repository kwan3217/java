package org.kwansystems.space.spice.daf;

import java.io.*;

public abstract class DAFRecord {
  public void printToStream(OutputStream ouf) {
    printToWriter(new PrintWriter(new OutputStreamWriter(ouf)));
  }
  @Override
  public String toString() {
    StringWriter result=new StringWriter();
    PrintWriter ouf=new PrintWriter(result,true);
    printToWriter(ouf);
    return result.toString();
  }
  public abstract void printToWriter(PrintWriter ouf);
  public abstract double[] Evaluate(double ET);
}
