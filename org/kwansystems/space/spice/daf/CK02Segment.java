package org.kwansystems.space.spice.daf;

import java.io.*;

import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.vector.*;

/**
<p>
The following method of storing and evaluating continuous pointing data for a
spacecraft structure defines C-kernel data type 2.
</p><p>
A type 2 segment consists of disjoint intervals of time during which the angular
velocity of the spacecraft is constant. Thus, throughout an interval, the
spacecraft structure rotates from its initial position about a fixed
right-handed axis defined by the direction of the angular velocity vector at a
constant rate equal to the magnitude of that vector.
</p><p>
A type 2 CK segment contains the following information for each interval:
<ol>
 <li>The encoded spacecraft clock START and STOP times for the interval.</li>
 <li>The quaternion representing the C-matrix associated with the start time
       of the interval.</li>
 <li>The constant angular velocity vector, in radians per second, for the
       interval.</li>
 <li>A factor which relates seconds and encoded SCLK ticks. This is necessary
       to convert the difference between the requested and interval start times
       from SCLK to seconds.</li>
</ol>
</p><p>

The orientation of a spacecraft structure may be determined from the above
information at any time that is within the bounds of one of the intervals.
</p><p>
Every type 2 segment is organized into four parts.
<pre>
   +----------------------------------------------------------------+
   |                                                                |
   |                                                                |
   |                          Pointing                              |
   |                                                                |
   |                                                                |
   +----------------------------------------------------------------+
   |                    |
   |                    |
   |  SCLK start times  |
   |                    |
   |                    |
   +--------------------+
   |                    |
   |                    |
   |  SCLK stop times   |
   |                    |
   |                    |
   +--------------------+
   |                    |
   |  SCLK directory    |
   |                    |
   +--------------------+
</pre>
The first part of a segment contains pointing records which are ordered with
respect to their corresponding interval start times. A type 2 pointing record
contains eight double precision numbers in the following form:
<pre>
   +-------+-------+-------+-------+-------+-------+-------+------+
   |       |       |       |       |       |       |       |      |
   |  q0   |  q1   |  q2   |  q3   |  a1   |  a2   |  a3   | rate |
   |       |       |       |       |       |       |       |      |
   +-------+-------+-------+-------+-------+-------+-------+------+
</pre>
The first four elements are the components of the quaternion Q = (q0,q1,q2,q3)
that is used to represent the C-matrix associated with the start time of the
interval. Next are the three components of the angular velocity vector A =
(a1,a2,a3) which are given with respect to the base reference frame specified
in the segment descriptor.
</p><p>

The last element is a rate which converts the difference between the requested
and interval start time from encoded SCLK ticks to seconds.
</p><p>

For segments containing predict data, this factor will be equal to the nominal
amount of time represented by one tick of the particular spacecraft's clock.
The nominal rate is given here for several spacecraft.
<pre>
   spacecraft                   seconds / tick ( sec )
   ---------------------        ----------------------
   Galileo                      1 / 120
   Mars Global Surveyor         1 / 256
   Voyager I and II             0.06
</pre>
For segments based on real rather than predicted pointing, the rate at which the
spacecraft clock runs relative to ephemeris time will deviate from the nominal
rate. The creator of the segment will need to determine an average value for
this rate over the time period of the interval.
</p><p>
Located after the pointing data are the interval START times followed by the
STOP times.

The START and STOP times should be ordered and in encoded SCLK form. The
intervals should be disjoint except for possibly at the endpoints. If an input
request time falls on an overlapping endpoint then the interval used will be the
one corresponding to the start time. Degenerate intervals in which the STOP time
equals the START time are not allowed.

Following the STOP times is a very simple directory of spacecraft clock times
containing <code>INT( (NPREC-1)/100 )</code> entries, where NPREC is the number of pointing
intervals. The Ith directory entry contains the midpoint of the (I*100)th STOP
and the (I*100 + 1)st START SCLK time.

   Thus,

   Directory(1) = ( STOP(100) + START(101) )   / 2

   Directory(2) = ( STOP(200) + START(201) )   / 2

   .
   .
   .

If there are 100 or fewer entries then there is no directory. The directory is
used to narrow down searches for pointing records to groups of 100 or less.
</p>
 */
public class CK02Segment extends CKSegment {
//  private Summary s;
  public int nInstances() throws IOException {
    return (s.length()-sclkDirLength())/10;
  }
  public int sclkDirLength() throws IOException {
    return s.length()/1001;
  }
  public int pointingStart() {
    return 0;
  }
  public int pointingStart(int i) {
    return i*8;
  }
  public int sclkDirStart() throws IOException {
    return s.length()-sclkDirLength()-1;
  }
  public int sclkStartIdx() throws IOException {
    return sclkDirStart()-2*nInstances();
  }
  public int sclkStopIdx() throws IOException {
    return sclkDirStart()-nInstances();
  }
  public double[] sclkDir() throws IOException {
    return s.get(sclkDirStart(),sclkDirLength());
  }
  public double[] sclkStart() throws IOException {
    return s.get(sclkStartIdx(),nInstances());
  }
  public double[] sclkStop() throws IOException {
    return s.get(sclkStopIdx(),nInstances());
  }
  public Quaternion[] q() throws IOException {
    Quaternion[] result=new Quaternion[nInstances()];
    for(int i=0;i<nInstances();i++) {
      double[] thisQ=s.get(pointingStart(i),4);
      result[i]=new Quaternion(thisQ[1],thisQ[2],thisQ[3],thisQ[0]);
    }
    return result;
  }
  public MathVector[] w() throws IOException {
    MathVector[] result=new MathVector[nInstances()];
    for(int i=0;i<nInstances();i++) {
      double[] thisQ=s.get(pointingStart(i)+4,3);
      result[i]=new MathVector(thisQ[0],thisQ[1],thisQ[2]);
    }
    return result;
  }
  public CK02Segment(Summary Ls) {
    super(Ls);
  }
  @Override
  public void printToWriter(PrintWriter ouf) {
    try {
      ouf.println("Number of pointing instances: "+nInstances());
      double[] sS=sclkStart();
      double[] sE=sclkStop();
      Quaternion[] qS=q();
      MathVector[] wS=w();
      double[] sD=sclkDir();
      for(int i=0;i<sS.length;i++) {
        ouf.printf("sclk [%02d]: %25.16e stop: %25.16e q: %s w: %s\n",i,sS[i],sE[i],qS[i].toString(),wS[i].toString());
      }
      for(int i=0;i<sD.length;i++) {
        ouf.printf("sclkD[%02d]: %25.16e\n",i,sD[i]);
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
