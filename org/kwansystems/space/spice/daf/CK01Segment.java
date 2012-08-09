package org.kwansystems.space.spice.daf;

import java.io.*;

import org.kwansystems.tools.rotation.*;
/**
 * The following method of storing and evaluating discrete pointing and
   angular rate values defines C-kernel data type 1.

</p><p>

   Each pointing instance is stored as a four-tuple called a
   ``quaternion.'' Quaternions are widely used to represent rotation
   matrices. They require less than half the space of 3x3 matrices and
   finding the rotation matrix given by a quaternion is faster and easier
   than finding it from, say, RA, Dec, and Twist. In addition, other
   computations involving rotations, such as finding the rotation
   representing two successive rotations, may be performed on the
   quaternions directly.
</p><p>

   The four numbers of a quaternion represent a unit vector and an angle.
   The vector represents the axis of a rotation, and the angle represents
   the magnitude of that rotation. If the vector is U = (u1, u2, u3), and
   the angle is T, then the quaternion Q is given by:
</p><p>

</p><pre>        Q = ( q0, q1, q2, q3 )
          = ( cos(T/2), sin(T/2)*u1, sin(T/2)*u2, sin(T/2)*u3 )
</pre>
   The details of quaternion representations of rotations, and the
   derivations of those representations are documented in the CSPICE
   Required Reading file ROTATIONS, <a href="http://naif.jpl.nasa.gov/pub/naif/toolkit_docs/C/req/rotation.html">rotation.req</a>.
<p>

   Data type 1 provides the option of including angular velocity data. If
   such data is included, the angular velocity vector A = (a1, a2, a3 )
   corresponding to each pointing instance will be stored as itself. The
   coordinates of the vector will be in the same base reference frame as
   that of the C-matrix quaternions.

</p><p>

   A type 1 pointing record consists of either four or seven double
   precision numbers; four for the C-matrix quaternion, and, optionally,
   three for the angular velocity vector.
</p><p>

</p><pre>   +--------+--------+--------+--------+--------+--------+--------+
   |   q    |   q    |   q    |   q    |   a    |   a    |   a    |
   |    0   |    1   |    2   |    3   |    1   |    2   |    3   |
   +--------+--------+--------+--------+--------+--------+--------+
</pre>
   Every type 1 segment has four parts to it:
<p>

</p><pre>   +----------------------------------------------------------------+
   |                                                                |
   |                                                                |
   |                           Pointing                             |
   |                                                                |
   |                                                                |
   +----------------------------------------------------------------+
   |                  |
   |                  |
   |    SCLK times    |
   |                  |
   |                  |
   +------------------+
   |                  |
   |  SCLK directory  |
   |                  |
   +------------------+
   |      NPREC       |
   +------------------+
</pre>
   The final component, NPREC, gives the total number of pointing instances
   described by the segment.

<p>

   Preceding it, starting from the top, are NPREC pointing records, ordered
   with respect to time, each consisting of the four or seven double
   precision numbers described above.
</p><p>

   Following the pointing section are the NPREC encoded spacecraft clock
   times corresponding to the pointing records. These must be in strictly
   increasing order.
</p><p>

   Following the SCLK times is a very simple SCLK directory. The directory
   contains INT( (NPREC-1) / 100 ) entries. The Ith directory entry
   contains the midpoint of the (I*100)th and the (I*100 + 1)st SCLK time.
   Thus,
</p><p>

</p><pre>   Directory(1) = ( SCLKDP(100) + SCLKDP(101) )   / 2

   Directory(2) = ( SCLKDP(200) + SCLKDP(201) )   / 2
</pre>
   and so on.
<p>


   If there are 100 or fewer entries, there is no directory. The directory
   is used to narrow down searches for pointing records to groups of 100 or
   less. Midpoints of adjacent times are used so that if an input time
   falls on one side of the directory time, then the group represented by
   that side is guaranteed to contain the time closest to the input time.
</p>
 */
public class CK01Segment extends CKSegment {
//  private Summary s;
  public int nInstances() throws IOException {
    return (int)s.get(s.length()-1);
  }
  public int sclkDirLength() throws IOException {
    return (nInstances()-1)/100;
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
  public int sclkDirStart() throws IOException {
    return s.length()-sclkDirLength()-1;
  }
  public int sclkStart() throws IOException {
    return sclkDirStart()-nInstances();
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
  public CK01Segment(Summary Ls) {
    super(Ls);
  }
  public void printToWriter(PrintWriter ouf) {
    try {
      ouf.println("Number of pointing instances: "+nInstances());
      double[] sS=sclk();
      Quaternion[] qS=q();
      double[] sD=sclkDir();
      for(int i=0;i<sS.length;i++) {
        ouf.printf("sclk [%02d]: %25.16e q: %s\n",i,sS[i],qS[i].toString());
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
