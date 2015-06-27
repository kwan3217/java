package org.kwansystems.space.spice.daf;

import java.io.*;

import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.vector.*;

public class SPK15Segment extends SPKSegment {
  double epoch() throws IOException {
    return s.get(0);
  }
  public SPKRecord[] Record() throws IOException {
    SPK15Record[] result=new SPK15Record[1];
    result[0]=new SPK15Record(start,stop,s.get(0,16));
    return result;
  }
  protected SPK15Segment(Summary Ls) {
    super(Ls);
  }
  public void printToWriter(PrintWriter ouf) {
    super.printToWriter(ouf);
  }

  @Override
  public DAFRecord Record(double t) throws IOException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
