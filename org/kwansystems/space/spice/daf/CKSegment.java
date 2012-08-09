package org.kwansystems.space.spice.daf;

import java.io.*;
import org.kwansystems.tools.time.*;
import static org.kwansystems.tools.time.TimeUnits.*;
import static org.kwansystems.tools.time.TimeScale.*;
import static org.kwansystems.tools.time.TimeEpoch.*;

public abstract class CKSegment extends DAFSegment {
  public double start,stop;
  public int instrument,frame,ckType,hasRates;
  protected CKSegment(Summary Ls) {
    super(Ls);
    start=s.summaryD[0];
    stop= s.summaryD[1];
    instrument=s.summaryI[0];
    frame=s.summaryI[1];
    ckType=s.summaryI[2];
    hasRates=s.summaryI[3];
  }
  public static CKSegment loadSegment(Summary Ls) {
    switch(Ls.summaryI[2]) {
      case 1:
        return new CK01Segment(Ls);
      case 3:
        return new CK03Segment(Ls);
      default:
        throw new IllegalArgumentException("Unrecognized CK type "+Ls.summaryI[2]);
    }
  }
  public String toString() {
    StringWriter result=new StringWriter();
    PrintWriter ouf=new PrintWriter(result,true);
    printToWriter(ouf);
    return result.toString();
  }
  public void printToWriter(PrintWriter ouf) {
    ouf.println("Start time:  "+new Time(start,Seconds,TDB,J2000).toString());
    ouf.println("Stop time:   "+new Time(stop,Seconds,TDB,J2000).toString());
    ouf.println("Instrument:  "+instrument);
    ouf.println("Relative Frame: "+frame);
    ouf.println("CK Type:     "+ckType);
    ouf.println("Has Rates:   "+hasRates);
  }
}
