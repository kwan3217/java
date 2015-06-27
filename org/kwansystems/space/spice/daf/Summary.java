package org.kwansystems.space.spice.daf;

import java.io.*;

public class Summary {
  double[] summaryD;
  int[]    summaryI;
  String   summaryC;
  Header h;
  int firstSlot() {
    return summaryI[summaryI.length-2];
  }
  int lastSlot() {
    return summaryI[summaryI.length-1];
  }
  int length() {
    return lastSlot()-firstSlot()+1;
  }
  public Summary(Header Lh) throws IOException {
    h=Lh;
    summaryD=new double[h.nd];
    summaryI=new int[h.ni];
    for(int i=0;i<h.nd;i++) summaryD[i]=h.readDouble();
    for(int i=0;i<h.ni;i++) summaryI[i]=h.readInt();
  }
  public void readComment() throws IOException {
    summaryC=h.readString(h.nc);
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
    for(int i=0;i<h.nd;i++) {
      ouf.printf("summaryD[%02d]=%25.16e\n",i,summaryD[i]);
    }
    for(int i=0;i<h.ni;i++) {
      ouf.printf("summaryI[%02d]=%25d\n",i,summaryI[i]);
    }
    ouf.println("summaryC: "+summaryC);
  }
  public double get(int i) throws IOException {
    return DoubleArrayFile.getSlot(h, firstSlot()+i);
  }
  public double[] get() throws IOException {
    return get(0,length());
  }
  public double[] get(int first, int len) throws IOException {
    double[] result=new double[len];
    for(int i=0;i<len;i++) result[i]=get(i+first);
    return result;
  }
}