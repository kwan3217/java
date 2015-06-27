package org.kwansystems.space.spice.daf;

import java.io.*;

import org.kwansystems.tools.rotation.*;
/**
 * The following method of storing and evaluating discrete pointing data
   for a spacecraft structure defines C-kernel data type 3.
</p><p>

   A type 3 segment consists of discrete pointing instances that are
   partitioned into groups within which linear interpolation between
   adjacent pointing instances is valid. Since the pointing instances in a
   segment are ordered with respect to time, these groups can be thought of
   as representing intervals of time over which the pointing of a
   spacecraft structure is given continuously. Therefore, in the
   description that follows, these groups of pointing instances will be
   referred to as interpolation intervals.

</p><p>

   All of the pointing instances in the segment must be ordered by encoded
   spacecraft clock time and must belong to one and only one interpolation
   interval. The intervals must begin and end at times for which there are
   pointing instances in the segment. The CK software that evaluates the
   data in the segment does not extrapolate pointing past the bounds of the
   intervals.
</p><p>

   A user's view of the time coverage provided by a type 3 segment can be
   viewed pictorially as follows:
</p><p>

</p><pre>
   pointing instances:     0-0-0-0-0----0-0-0-0-0-----0------0-0-0-0
                           |       |    |       |     |      |     |
   interval bounds:       BEG      |   BEG      |    BEG    BEG    |
                                  END          END   END          END

</pre>
   In the above picture, the zeros indicate the times associated with the
   discrete pointing instances and the vertical bars show the bounds of the
   interpolation intervals that they are partitioned into. Note that the
   intervals begin and end at times associated with pointing instances.
   Also note that intervals consisting of just a single pointing instance
   are allowed.
<p>

   When pointing is desired for a time that is within the bounds of one of
   the intervals, the CK reader functions return interpolated pointing at
   the request time. In the example below, the pointing request time is
   indicated by SCLKDP and the user-supplied tolerance is given by TOL. In
   this example the tolerance argument of the CK readers could be set to
   zero and pointing would still be returned.
</p><p>


</p><pre>                                     SCLKDP    TOL
                                          \   /
                                           | |
                                           |/ \
                                       [---+---]
                                       .   .   .
                                       .   .   .
   pointing instances:     0-0-0-0-0----0-0-0-0-0-----0------0-0-0-0
                           |       |    |  ^    |     |      |     |
   interval bounds:       BEG      |   BEG |    |    BEG    BEG    |
                                  END      |   END   END          END
                                           |
            CK reader returns interpolated pointing at this time.
</pre>
   When a request time falls in a gap between intervals, no extrapolation
   is performed. Instead, pointing is returned for the interval endpoint
   closest to the request time, provided that time is within the user
   supplied tolerance. In this example if the tolerance were set to zero no
   pointing would be returned.
<p>

</p><pre>                                            SCLKDP
                                                 \   TOL
                                                  | /
                                                  |/\
                                              [---+---]
                                              .   .   .
                                              .   .   .
   pointing instances:     0-0-0-0-0----0-0-0-0-0-----0------0-0-0-0
                           |       |    |       |     |      |     |
   interval bounds:       BEG      |   BEG      |    BEG    BEG    |
                                  END          END   END          END
                                                ^
                                                |
                                CK reader returns this instance.
</pre>
   The physical structure of the data stored in a type 3 segment is as
   follows:
<p>

</p><pre>   +-----------------------------------------------------------------+
   |                                                                 |
   |                                                                 |
   |                          Pointing                               |
   |                                                                 |
   |                                                                 |
   +-----------------------------------------------------------------+
   |                        |
   |  SCLK times            |
   |                        |
   +------------------------+
   |                        |
   |  SCLK directory        |
   |                        |
   +------------------------+
   |                        |
   |  Interval start times  |
   |                        |
   +------------------------+
   |                        |
   |  Start times directory |
   |                        |
   +------------------------+
   |                        |
   |  Number of intervals   |
   |                        |
   +------------------------+
   |                        |
   |  Number of pointing    |
   |      instances         |
   |                        |
   +------------------------+

</pre>
   In the discussion that follows let NPREC be the number of pointing
   instances in the segment and let NUMINT be the number of intervals into
   which the pointing instances are partitioned.
<p>

   The first part of a segment contains NPREC pointing records which are
   ordered with respect to increasing time. Depending on whether or not the
   segment contains angular velocity data, a type 3 pointing record
   contains either four or seven double precision numbers in the following
   form:
</p><p>

</p><pre>   +--------+--------+--------+--------+--------+--------+--------+
   |        |        |        |        |        |        |        |
   |   q0   |   q1   |   q2   |   q3   |   a1   |   a2   |   a3   |
   |        |        |        |        |        |        |        |
   +--------+--------+--------+--------+--------+--------+--------+
</pre>
   The first four elements are the components of the quaternion Q =
   (q0,q1,q2,q3) that is used to represent the pointing of the instrument
   or spacecraft structure to which the segment applies. Next are the three
   components of the angular velocity vector AV = (a1,a2,a3) which are
   given with respect to the base reference frame specified in the segment
   descriptor. These components are optional and are present only if the
   segment contains angular velocity data as specified by the fourth
   integer component of the segment descriptor.
<p>

   Following the pointing data are the NPREC times associated with the
   pointing instances. These times are in encoded SCLK form and should be
   strictly increasing.
</p><p>


   Immediately following the last time is a very simple directory of the
   SCLK times. The directory contains INT( (NPREC-1) / 100 ) entries. The
   Ith directory entry contains the (I*100)th SCLK time. Thus,
</p><p>

</p><pre>   Directory(1) = SCLKDP(100)

   Directory(2) = SCLKDP(200)

   .
   .
   .
</pre>
   If there are 100 or fewer entries, there is no directory. The directory
   is used to narrow down searches for pointing records to groups of 100 or
   less.
<p>

   Next are the NUMINT start times of the intervals that the pointing
   instances are partitioned into. These times are given in encoded
   spacecraft clock and must be strictly increasing. They must also be
   equal to times for which there are pointing instances in the segment.
   Note that the interval stop times are not stored in the segment. They
   are not needed because the stop time of the Ith interval is simply the
   time associated with the pointing instance that precedes the start time
   of the (I+1)th interval.
</p><p>

   Following the interval start times is a directory of these times. This
   directory is constructed in a form similar to the directory for the
   times associated with the pointing instances. The start times directory
   contains INT ( (NUMINT-1) / 100 ) entries and contains every 100th start
   time. Thus:
</p><p>


</p><pre>   Directory(1) = START(100)

   Directory(2) = START(200)

   .
   .
   .
</pre>
   Finally, the last two words in the segment give the total number of
   interpolation intervals (NUMINT) and the total number of pointing
   instances (NPREC) in the segment.
<p>

   A segment writer function is provided which calls the low level DAF
   functions necessary to write a type 3 segment to a C-kernel. However,
   the creator of the segment is responsible for determining whether or not
   it is valid to interpolate between adjacent pointing instances, and thus
   how they should be partitioned into intervals. See the header of the
   function <a href="../cspice/ckw03_c.html">ckw03_c</a> for a complete description of the inputs required to
   write a segment.
</p>
 */
public class CK03Segment extends CKSegment {
//  private Summary s;
  public int nInstances() throws IOException {
    return (int)s.get(s.length()-1);
  }
  public int nIntervals() throws IOException {
    return (int)s.get(s.length()-2);
  }
  public int intervalDirLength() throws IOException {
    return (nIntervals()-1)/100;
  }
  public int sclkDirLength() throws IOException {
    return (nIntervals()-1)/100;
  }
  public int pointingStart() {
    return 0;
  }
  public boolean hasRates() {
    return s.summaryI[3]==1;
  }
  public int pointingStart(int i) {
    if(hasRates()) return i*7;
    return i*4;
  }
  public int intervalDirStart() throws IOException {
    return s.length()-intervalDirLength()-2;
  }
  public int intervalStart() throws IOException {
    return intervalDirStart()-nIntervals();
  }
  public int sclkDirStart() throws IOException {
    return intervalStart()-sclkDirLength();
  }
  public int sclkStart() throws IOException {
    return sclkDirStart()-nInstances();
  }
  public double[] intervalDir() throws IOException {
    return s.get(intervalDirStart(),intervalDirLength());
  }
  public double[] interval() throws IOException {
    return s.get(intervalStart(),nIntervals());
  }
  public double[] sclkDir() throws IOException {
    return s.get(sclkDirStart(),sclkDirLength());
  }
  public double[] sclk() throws IOException {
    return s.get(sclkStart(),nInstances());
  }
  public Quaternion[] q() throws IOException {
    Quaternion[] result=new Quaternion[nInstances()];
    for(int i=0;i<nInstances();i++) {
      double[] thisQ=s.get(pointingStart(i),4);
      result[i]=new Quaternion(thisQ[1],thisQ[2],thisQ[3],thisQ[0]);
    }
    return result;
  }
  public CK03Segment(Summary Ls) {
    super(Ls);
  }
  public void printToWriter(PrintWriter ouf) {
    try {
      ouf.println("Number of pointing instances: "+nInstances());
      ouf.println("Number of intervals:          "+nIntervals());
      double[] sS=sclk();
      Quaternion[] qS=q();
      double[] sD=sclkDir();
      double[] iS=interval();
      double[] iD=intervalDir();
      for(int i=0;i<sS.length;i++) {
        ouf.printf("sclk [%02d]: %25.16e q: %s\n",i,sS[i],qS[i].toString());
      }
      for(int i=0;i<iS.length;i++) {
        ouf.printf("i    [%02d]: %25.16e\n",i,iS[i]);
      }
      for(int i=0;i<sD.length;i++) {
        ouf.printf("sclkD[%02d]: %25.16e\n",i,sD[i]);
      }
      for(int i=0;i<iD.length;i++) {
        ouf.printf("iD   [%02d]: %25.16e\n",i,iD[i]);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
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

  @Override
  public DAFRecord[] Record() throws IOException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public DAFRecord Record(double t) throws IOException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
