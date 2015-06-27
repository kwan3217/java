package org.kwansystems.space.spice.daf;

import java.io.*;

public abstract class DAFSegment {
  protected Summary s;
  public double start,stop;
  protected DAFSegment(Summary Ls) {
    s=Ls;
    start=s.summaryD[0];
    stop= s.summaryD[1];
  }
  public abstract DAFRecord[] Record() throws IOException;

  public abstract DAFRecord Record(double t) throws IOException;
}
