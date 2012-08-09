package org.kwansystems.space.spice.daf;

import java.io.*;

public class CKFile extends DoubleArrayFile {
  public CKFile(RandomAccessFile inf) throws IOException {
    super(inf);
  }
  public void dump(PrintStream ouf) throws IOException {
    ouf.println(this);
    int i=1;
    for(SummaryRecord SR:sr) {
      for(Summary S:SR.summaries) {
        System.out.printf("Segment %d\n",i);
        System.out.println(S);
        CKSegment CKS=CKSegment.loadSegment(S);
        System.out.println(CKS);
/*        DAFRecord[] CKR=CKS.Record();
        for(int j=0;j<CKR.length;j++) {
          System.out.printf("Record %d\n",j);
          System.out.println(CKR[j]);
        } */
        i++;
      }
    }
  }

  @Override
  public DAFSegment getSegment(Summary S) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
