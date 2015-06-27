package org.kwansystems.space.spice.daf;

import java.io.*;
import org.kwansystems.tools.vector.*;
import org.kwansystems.tools.time.*;
import static org.kwansystems.tools.time.TimeUnits.*;
import static org.kwansystems.tools.time.TimeScale.*;
import static org.kwansystems.tools.time.TimeEpoch.*;

public abstract class SPKRecord extends DAFRecord {
  public double startValid,endValid;
  public abstract double epoch();
  public SPKRecord(double LstartValid, double LendValid) {
    startValid=LstartValid;
    endValid=LendValid;
  }
  public void printToWriter(PrintWriter ouf) {
    ouf.printf("Start valid:       %s\n",new Time(startValid,Seconds,TDB,J2000));
    ouf.printf("End valid:         %s\n",new Time(endValid,Seconds,TDB,J2000));
  };
  public MathState Evaluate(Time ET) {
    double[] STATE=Evaluate(ET.get(Seconds,TDB,J2000));
    return new MathState(STATE[1],STATE[2],STATE[3],STATE[4],STATE[5],STATE[6]);
  }
}
