package org.kwansystems.space.spice.daf;

import java.io.*;

import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.vector.*;

public class SPK09Segment extends SPKSegment {
  int N() throws IOException {
    return (int)s.get(s.length()-1);
  };
  int POLYDEG() throws IOException {
    return (int)s.get(s.length()-2);
  };
  double epoch(int i) throws IOException {
    return s.get(N()*6+i);
  }
  public SPKRecord[] Record() throws IOException {
    SPK09Record[] result=new SPK09Record[N()];
    for(int i=0;i<result.length;i++) {
      double startValid=epoch(i);
      double endValid=(i==result.length)?s.summaryD[1]:epoch(i+1);
      result[i]=new SPK09Record(startValid,endValid,s.get(i*6,6));
    }
    return result;
  }
  protected SPK09Segment(Summary Ls) {
    super(Ls);
  }
  public void printToWriter(PrintWriter ouf) {
    super.printToWriter(ouf);
    try {
      ouf.printf("Number of records: %d\n",N());
//      SPK02Record[] records=Records();
//      for(int i=0;i<N();i++) {
//        ouf.printf("Record %0"+(1+(int)Math.log10(N()))+"d\n",i+1);
//        ouf.printf("End Epoch:       %s\n",new Time(e[i],Seconds,TDB,J2000));
//        if(i>0) ouf.printf("Time since last: %f\n", e[i]-e[i-1]);
//        ouf.println(records[i]);
//      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public DAFRecord Record(double t) throws IOException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
