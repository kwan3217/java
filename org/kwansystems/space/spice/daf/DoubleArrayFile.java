package org.kwansystems.space.spice.daf;

import java.io.*;
import java.util.*;
import org.kwansystems.tools.time.*;
import org.kwansystems.tools.rotation.*;
import static org.kwansystems.tools.time.TimeUnits.*;
import static org.kwansystems.tools.time.TimeScale.*;
import static org.kwansystems.tools.time.TimeEpoch.*;

public abstract class DoubleArrayFile {
  public static double getSlot(Header h,int slot) throws IOException {
    h.inf.seek((slot-1)*8);
    return h.readDouble();
  }
  public abstract DAFSegment getSegment(Summary S);
  private Summary findSummary(int body, double t) throws IOException {
    //Find the correct segment summary (last one in the file which covers this time)
    Summary segsum=null;
    for(SummaryRecord SR:sr) {
      for(Summary sum:SR.summaries) {
        if(sum.summaryI[0]==body && sum.summaryD[0]<=t && sum.summaryD[1]>=t) {
          segsum=sum;
        }
      }
    }
    if(segsum==null) {
      throw new IllegalArgumentException(
        String.format("No segment found which covers body %d and time %s", body,new Time(t,Seconds,TDB,J2000).toString())
      );
    }
    return segsum;
  }
  private double[] Evaluate(int body, double t) throws IOException {
    DAFSegment seg=getSegment(findSummary(body,t));
    return seg.Record(t).Evaluate(t);
  }
  private void readSummaryRecords(RandomAccessFile inf) throws IOException {
    seekBlock(inf,h.initialSummary);
    sr=new LinkedList<SummaryRecord>();
    do {
      sr.add(new SummaryRecord(h));
      if(sr.getLast().next!=0) seekBlock(inf,sr.getLast().next);
    } while (sr.getLast().next!=0);
  }
  public static void seekBlock(RandomAccessFile inf, int initialSummary) throws IOException {
    inf.seek((initialSummary-1)*1024);
  }
  Header h;
  LinkedList<SummaryRecord> sr;
  public DoubleArrayFile(RandomAccessFile inf) throws IOException {
    h=new Header(inf);
    readSummaryRecords(inf); 
  }
  public void printToWriter(PrintWriter ouf) {
    h.printToWriter(ouf);
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
  public abstract void dump(PrintStream ouf) throws IOException;
  public static DoubleArrayFile loadKernel(RandomAccessFile inf) throws IOException {
    //Remember where we are in the file, so we can go back
    Long ptr=inf.getFilePointer();
    //Read the header, including the kernel type
    Header h=new Header(inf);
    //Rewind the file
    inf.seek(ptr);
    //Now with the file rewound, create the right type of kernel object and use it to load the file
    if(h.Sig2.startsWith("SPK")) {
      return new SPKFile(inf);
    } else if(h.Sig2.startsWith("/DAF")) { //Older planet SPKs are like this
      return new SPKFile(inf);
    } else if(h.Sig2.startsWith("CK")) {
      return new CKFile(inf);
    } else {
      throw new IllegalArgumentException("Unexpected kernel type "+h.Sig1+h.Sig2);
    }
  }
  public String[] comments() throws IOException {
    List<String> result=new ArrayList<String>();
    byte[] commentData=new byte[1000];
    long ptr=h.inf.getFilePointer();
    int block=1;
    h.inf.seek(block*1024);
    h.inf.read(commentData);
    boolean done=false;
    StringBuffer thisLine=new StringBuffer();
    int i=0;
    while(!done) {
      switch(commentData[i]) {
        case 4:
          result.add(thisLine.toString());
          done=true;
          break;
        case 0:
          result.add(thisLine.toString());
          thisLine=new StringBuffer();
          break;
        default:
          thisLine.append((char)commentData[i]);
      }
      i++;
      if(i>=1000) {
        i=0;
        block++;
        h.inf.seek(block*1024);
        h.inf.read(commentData);
      }
    }
    h.inf.seek(ptr);
    return result.toArray(new String[0]);
  }
  public static void main(String[] args) throws IOException {
//    RandomAccessFile inf=new RandomAccessFile("Data/spice/spk_2009067000000_2009070185721_kplr.bsp","r");
//    RandomAccessFile inf=new RandomAccessFile("Data/spice/spk_090307_090308_od004_v1.bsp","r");
/*
    RandomAccessFile inf=new RandomAccessFile("Data/spice/de405.bsp","r");
//    RandomAccessFile inf=new RandomAccessFile("Data/spice/phx_edl_rec_traj.bsp","r");
    DoubleArrayFile daf=DoubleArrayFile.loadKernel(inf);
    String[] comment=daf.comments();
    for(int i=0;i<comment.length;i++) {
      System.out.println(comment[i]);
    }
//    daf.dump(System.out);
    System.out.println(daf.getSegment(daf.findSummary(3,400000000.0)));
    System.out.println(new Time(400000000.0,Seconds,TDB,J2000));
    double[] result=daf.Evaluate(3,400000000.0);
    double[] horizons_resultDE405=new double[] {
      0,
      1.430297984922756E+08,-4.348854751726753E+07,-1.885766967422374E+07,
      8.819060986300114E+00, 2.586034213332400E+01, 1.121065525103080E+01
    };
    for(int i=1;i<result.length;i++) {
      System.out.printf("Horizons:   %25.7f\n",horizons_resultDE405[i]);
      System.out.printf("Calculated: %25.7f\n",result[i]);
      System.out.printf("Difference: %25.7f\n",horizons_resultDE405[i]-result[i]);
    }
*/
   RandomAccessFile inf=new RandomAccessFile("c:/jeppesen/workspace/Data/spice/MSL/spk/msl_edl_v01.bsp","r");
 
//    RandomAccessFile inf=new RandomAccessFile("c:\\Program Files\\Celestia\\extras\\voyager-full\\data\\vgr2-nep081.bsp","r");
//    RandomAccessFile inf=new RandomAccessFile("Data/spice/phx_edl_rec_traj.bsp","r");
    DoubleArrayFile daf=DoubleArrayFile.loadKernel(inf);
//    String[] comment=daf.comments();
//    for(int i=0;i<comment.length;i++) {
//      System.out.println(comment[i]);
//    }
//   daf.dump(System.out);
    int i=1;
    DataOutputStream ouf1=new DataOutputStream(new FileOutputStream("c:/jeppesen/workspace/Data/msl_edl_v01_bsp.dat"));
    DataOutputStream ouf2=new DataOutputStream(new FileOutputStream("c:/jeppesen/workspace/Data/msl_skycrane_v01_bsp.dat"));
    /*
    for(SummaryRecord SR:daf.sr) {
      for(Summary S:SR.summaries) {
        System.out.println(S);
        SPKSegment SPKS=SPKSegment.loadSegment(S);
        DAFRecord[] SPKR=SPKS.Record();
        if(SPKS.target==-76031 && SPKS.center==499) {
          for(int j=0;j<SPKR.length;j++) {
        	SPK13Record spkRecord=(SPK13Record)SPKR[j];
            ouf1.writeDouble(spkRecord.epoch());
            ouf1.writeDouble(spkRecord.X);
            ouf1.writeDouble(spkRecord.Y);
            ouf1.writeDouble(spkRecord.Z);
            ouf1.writeDouble(spkRecord.dX);
            ouf1.writeDouble(spkRecord.dY);
            ouf1.writeDouble(spkRecord.dZ);
          }
        } else if(SPKS.target==-76 && SPKS.center==-76031) {
          for(int j=0;j<SPKR.length;j++) {
        	SPK13Record spkRecord=(SPK13Record)SPKR[j];
            ouf2.writeDouble(spkRecord.epoch());
            ouf2.writeDouble(spkRecord.X);
            ouf2.writeDouble(spkRecord.Y);
            ouf2.writeDouble(spkRecord.Z);
            ouf2.writeDouble(spkRecord.dX);
            ouf2.writeDouble(spkRecord.dY);
            ouf2.writeDouble(spkRecord.dZ);
            System.out.printf("%20.10fd,%20.15fd,%20.15fd,%20.15fd,%20.15fd,%20.15fd,%20.15fd\n",spkRecord.epoch(),spkRecord.X,spkRecord.Y,spkRecord.Z,spkRecord.dX,spkRecord.dY,spkRecord.dZ);
          }
        }
        i++;
      }
    }
    */
    ouf1.close();
    ouf2.close();
    inf.close();
    inf=new RandomAccessFile("c:/jeppesen/workspace/Data/spice/MSL/ck/msl_edl_v01.bc","r");
    
//  RandomAccessFile inf=new RandomAccessFile("c:\\Program Files\\Celestia\\extras\\voyager-full\\data\\vgr2-nep081.bsp","r");
//  RandomAccessFile inf=new RandomAccessFile("Data/spice/phx_edl_rec_traj.bsp","r");
    daf=DoubleArrayFile.loadKernel(inf);
//  String[] comment=daf.comments();
//  for(int i=0;i<comment.length;i++) {
//    System.out.println(comment[i]);
//  }
// daf.dump(System.out);
  i=1;
  ouf1=new DataOutputStream(new FileOutputStream("c:/jeppesen/workspace/Data/msl_edl_v01_bc.dat"));
  for(SummaryRecord SR:daf.sr) {
    for(Summary S:SR.summaries) {
      System.out.println(S);
      CK03Segment CKS=(CK03Segment)CKSegment.loadSegment(S);
//      DAFRecord[] CKR=CKS.Record();
      if(CKS.frame==1) {
      double[] sclk=CKS.sclk();
      Quaternion[] q=CKS.q();
      for(int j=0;j<sclk.length;j++) {
        ouf1.writeDouble(sclk[j]);
        ouf1.writeDouble(q[j].X());
        ouf1.writeDouble(q[j].Y());
        ouf1.writeDouble(q[j].Z());
        ouf1.writeDouble(q[j].W());
      }
      }
      i++;
    }
  }
  ouf1.close();
  ouf2.close();
  }

}
