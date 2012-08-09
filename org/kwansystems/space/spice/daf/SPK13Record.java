package org.kwansystems.space.spice.daf;

import java.io.*;

public class SPK13Record extends SPK09Record {
  public SPK13Record(double LstartValid, double LendValid, double[] in) {
    super(LstartValid,LendValid,in);
  }
  public double[] Evaluate(double ET) {
    throw new UnsupportedOperationException("Not supported yet.");
//    double[] STATE=new double[7];
//    return STATE;
  }
}
