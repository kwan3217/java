package org.kwansystems.space.spice.daf;

import java.io.*;

public class SPKFile extends DoubleArrayFile {
  public SPKFile(RandomAccessFile inf) throws IOException {
    super(inf);
  }

  public void dump(PrintStream ouf) throws IOException {
    ouf.println(this);
    int i=1;
    for(SummaryRecord SR:sr) {
      for(Summary S:SR.summaries) {
        System.out.printf("Segment %d\n",i);
        System.out.println(S);
        SPKSegment SPKS=SPKSegment.loadSegment(S);
        System.out.println(SPKS);
//        DAFRecord[] SPKR=SPKS.Record();
//        for(int j=0;j<SPKR.length;j++) {
//          System.out.printf("Record %d\n",j);
//          System.out.println(SPKR[j]);
//        }
        i++;
      }
    }
  }

  @Override
  public DAFSegment getSegment(Summary S) {
    return SPKSegment.loadSegment(S);
  }
}
