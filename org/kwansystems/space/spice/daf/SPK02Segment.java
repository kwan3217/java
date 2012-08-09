package org.kwansystems.space.spice.daf;

import java.io.*;

import org.kwansystems.tools.vector.*;
import org.kwansystems.tools.time.*;
import static org.kwansystems.tools.time.TimeUnits.*;
import static org.kwansystems.tools.time.TimeScale.*;
import static org.kwansystems.tools.time.TimeEpoch.*;

public class SPK02Segment extends SPKSegment {
  int N() throws IOException {
    return (int)s.get(s.length()-1);
  };
  int RSIZE() throws IOException {
    return (int)s.get(s.length()-2);
  };
  double INTLEN() throws IOException {
    return s.get(s.length()-3);
  };
  double INIT() throws IOException {
    return s.get(s.length()-4);
  };
  public DAFRecord[] Record() throws IOException {
    SPK02Record[] result=new SPK02Record[N()];
    double init=INIT();
    double intlen=INTLEN();
    for(int i=0;i<result.length;i++) {
      double startValid=init+i*intlen;
      double endValid=init+(i+1)*intlen;
      result[i]=new SPK02Record(startValid,endValid,s.get(i*RSIZE(),RSIZE()));
    }
    return result;
  }
  public DAFRecord Record(int i) throws IOException {
    double init=INIT();
    double intlen=INTLEN();
    double startValid=init+i*intlen;
    double endValid=init+(i+1)*intlen;
    return new SPK02Record(startValid,endValid,s.get(i*RSIZE(),RSIZE()));

  }
  protected SPK02Segment(Summary Ls) {
    super(Ls);
  }
  public void printToWriter(PrintWriter ouf) {
    super.printToWriter(ouf);
    try {
      ouf.printf("Number of records: %d\n",N());
      ouf.printf("Record size:       %d\n",RSIZE());
      ouf.printf("Initial Epoch:     %s\n",new Time(INIT(),Seconds,TDB,J2000));
      ouf.printf("Interval Length:   %f\n",INTLEN());
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
    int i=(int)((t-INIT())/INTLEN());
    return Record(i);
  }
}
