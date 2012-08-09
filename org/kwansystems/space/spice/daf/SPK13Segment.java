package org.kwansystems.space.spice.daf;

import java.io.*;

import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.vector.*;

public class SPK13Segment extends SPK09Segment {
  public SPKRecord[] Record() throws IOException {
    SPK09Record[] result=new SPK13Record[N()];
    for(int i=0;i<result.length;i++) {
      double startValid=epoch(i);
      double endValid=(i==result.length)?s.summaryD[1]:epoch(i+1);
      result[i]=new SPK13Record(startValid,endValid,s.get(i*6,6));
    }
    return result;
  }
  protected SPK13Segment(Summary Ls) {
    super(Ls);
  }

  @Override
  public DAFRecord Record(double t) throws IOException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
