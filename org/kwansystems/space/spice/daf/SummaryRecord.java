package org.kwansystems.space.spice.daf;

import java.io.*;
import java.util.*;

public class SummaryRecord {
  public SummaryRecord(Header Lh) throws IOException {
    h=Lh;
    int currentBlock=(int)((h.inf.getFilePointer()/1024)+1);
    double D=h.readDouble();
    next=(int)D;
    D=h.readDouble();
    prev=(int)D;
    D=h.readDouble();
    nSummaries=(int)D;
    summaries=new ArrayList<Summary>(nSummaries);
    
    for(int j=0;j<nSummaries;j++) {
      summaries.add(new Summary(h));  
    }
    DoubleArrayFile.seekBlock(h.inf,currentBlock+1);
    for(Summary s:summaries) {
      s.readComment();
    }
  }
  public String toString() {
    StringWriter result=new StringWriter();
    PrintWriter ouf=new PrintWriter(result,true);
    ouf.println("Next summary record:      "+next);
    ouf.println("Prev summary record:      "+prev);
    ouf.println("Summaries in this record: "+nSummaries);
    for(int j=0;j<nSummaries;j++) {
      ouf.println("Summary "+j);
      ouf.println(summaries.get(j));
    }
    return result.toString();
  }
  private Header h;
  public int next,prev,nSummaries;
  public List<Summary> summaries; 
}
