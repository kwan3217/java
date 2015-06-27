package org.kwansystems.space.spice.daf;

import java.io.*;

import org.kwansystems.tools.time.*;
import static org.kwansystems.tools.time.TimeUnits.*;
import static org.kwansystems.tools.time.TimeScale.*;
import static org.kwansystems.tools.time.TimeEpoch.*;

public abstract class SPKSegment extends DAFSegment {
  public int target,center,frame,spkType;
  protected SPKSegment(Summary Ls) {
    super(Ls);
    target=s.summaryI[0];
    center=s.summaryI[1];
    frame=s.summaryI[2];
    spkType=s.summaryI[3];
  }
  public static SPKSegment loadSegment(Summary Ls) {
    switch(Ls.summaryI[3]) {
      case 1:
        return new SPK01Segment(Ls);
      case 2:
        return new SPK02Segment(Ls);
      case 3:
        return new SPK03Segment(Ls);
      case 9:
        return new SPK09Segment(Ls);
      case 10:
        return new SPK10Segment(Ls);
      case 13:
        return new SPK13Segment(Ls);
      case 15:
        return new SPK15Segment(Ls);
      default:
        throw new IllegalArgumentException("Unrecognized SPK type "+Ls.summaryI[3]);
    }
  }
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
  public void printToWriter(PrintWriter ouf) {
    ouf.println("Start time:  "+new Time(start,Seconds,TDB,J2000).toString());
    ouf.println("Stop time:   "+new Time(stop,Seconds,TDB,J2000).toString());
    ouf.println("Target body: "+target);
    ouf.println("Center body: "+center);
    ouf.println("Frame:       "+frame);
    ouf.println("SPK Type:    "+spkType);
  }
}
