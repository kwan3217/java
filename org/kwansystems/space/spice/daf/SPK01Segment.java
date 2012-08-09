package org.kwansystems.space.spice.daf;

import java.io.*;
import org.kwansystems.tools.time.*;
import static org.kwansystems.tools.time.TimeUnits.*;
import static org.kwansystems.tools.time.TimeScale.*;
import static org.kwansystems.tools.time.TimeEpoch.*;

public class SPK01Segment extends SPKSegment {
  int N() throws IOException {
    return (int)s.get(s.length()-1);
  };
  int NDirEntries() throws IOException {
    return N()/100;
  }
  double[] Dir() throws IOException {
    double[] result=new double[NDirEntries()];
    for(int i=0;i<result.length;i++) result[i]=s.get(s.length()-result.length-1+i);
    return result;
  }
  double Dir(int i) throws IOException {
    return s.get(s.length()-NDirEntries()-1+i);
  }
  double[] Epoch() throws IOException {
    return s.get(N()*71,N());
  }
  double Epoch(int i) throws IOException {
    return s.get(N()*71+i);
  }
  public DAFRecord[] Record() throws IOException {
    SPK01Record[] result=new SPK01Record[N()];
    double[] e=Epoch();
    for(int i=0;i<result.length;i++) {
      double endValid=e[i];
      double startValid=(i==0)?s.summaryD[0]:e[i-1];
      result[i]=new SPK01Record(startValid,endValid,s.get(i*71,71));
    }
    return result;
  }
  public DAFRecord Record(int i) throws IOException {
    double endValid=Epoch(i);
    double startValid=(i==0)?s.summaryD[0]:Epoch(i-1);
    return new SPK01Record(startValid,endValid,s.get(i*71,71));
  }
  protected SPK01Segment(Summary Ls) {
    super(Ls);
  }
  public void printToWriter(PrintWriter ouf) {
    super.printToWriter(ouf);
    try {
      ouf.printf("Number of records: %d\n",N());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public DAFRecord Record(double t) throws IOException {
    int startSearch,endSearch;
    //Does the segment have a directory?
//    if(NDirEntries()>0) {
//      double[] D=Dir();
//      for(double d:D) {
//        if()
//      }
//    } else {
      startSearch=0;
      endSearch=N()-1;
//    }
    double[] E=Epoch();
    for(int i=startSearch;i<=endSearch;i++) {
      if(!(E[i]<t)) {
        return Record(i); 
      }
    }
    throw new IllegalArgumentException(String.format("No record found for time %s",new Time(t,Seconds,TDB,J2000).toString()));
  }
}
