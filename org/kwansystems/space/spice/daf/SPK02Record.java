package org.kwansystems.space.spice.daf;

import java.io.*;
import org.kwansystems.tools.Chebyshev;
import org.kwansystems.tools.time.*;
import static org.kwansystems.tools.time.TimeUnits.*;
import static org.kwansystems.tools.time.TimeScale.*;
import static org.kwansystems.tools.time.TimeEpoch.*;

public class SPK02Record extends SPKRecord {
  public double MID;     //Midpoint of record Epoch, seconds from J2000 TDB
  public double RADIUS;  //Radius of record time span, s
  public int N;
  public double[][] CP;     //Chebyshev polynomial coefficients for each component
  
  public SPK02Record(double LstartValid, double LendValid, double[] in) {
    super(LstartValid,LendValid);
    int index=0;
    MID   =in[index]; index++;
    RADIUS=in[index]; index++;
    N=(in.length-2)/3;
    CP=new double[3+1][];
    for(int j=1;j<=3;j++) {
      CP[j]=new double[N+1];
      System.arraycopy(in, index, CP[j], 1, N);
      index+=N;
    }
  }
  public void printToWriter(PrintWriter ouf) {
    super.printToWriter(ouf);
    ouf.printf("Midpoint Epoch:    %s\n",new Time(MID,Seconds,TDB,J2000).toString());
    ouf.printf("Radius:            %f\n",RADIUS);
    ouf.println("Chebyshev coefficients: ");
    for(int i=1;i<=N;i++)  ouf.printf("  %22.15e  %22.15e  %22.15e\n",CP[1][i],CP[2][i],CP[3][i]);
  }
  /** Evaluate SPK type 2 record (Chebyshev Polynomials)
   *
   * @param ET - seconds from J2000 TDB
   * @return
   *   Array of doubles<br>
   *   result[1]=X position, km<br>
   *   result[2]=Y position, km<br>
   *   result[3]=Z position, km<br>
   *   result[4]=X velocity, km/s<br>
   *   result[5]=Y velocity, km/s<br>
   *   result[6]=Z velocity, km/s<br>
   *
   * Copied straight from Fortran SPICE, so has that ol' Fortran
   * Flavor just like mom used to make, including capitalizing
   * everything and using 1-based arrays.
   */
  public double[] Evaluate(double ET) {
    double[] STATE=new double[7];
    int COFLOC;
    int DEGP;
    int NCOF=N;

    /*
      The first number in the record is the record size.  Following it
      are two numbers that will be used later, then the three sets of
      coefficients.  The number of coefficients for each variable can
      be determined from the record size, since there are the same
      number of coefficients for each variable.
     */
      NCOF = N;

    /*
      The degree of each polynomial is one less than the number of
      coefficients.
     */
      DEGP = NCOF - 1;

    /*
      Call CHBINT once for each variable to evaluate the position
      and velocity values.
     */
      for(int I=1;I<=3;I++) {

     /*
         The coefficients for each variable are located contiguously,
         following the first three words in the record.
      */

         COFLOC = NCOF*( I - 1 ) + 4;

     /*
         CHBINT needs as input the coefficients, the degree of the
         polynomial, the epoch, and also two variable transformation
         parameters, which are located, in our case, in the second and
         third slots of the record.
      */ double[] result=Chebyshev.CHBINT(CP[I], DEGP, new double[] {0,MID,RADIUS}, ET);
        /*
         CALL CHBINT ( RECORD( COFLOC ), DEGP, RECORD( 2 ), ET,
                      XYZDOT( I ), XYZDOT( I + 3 )                )
         */
         STATE[I]  =result[1];
         STATE[I+3]=result[2];
      }


    return STATE;
  }

    @Override
    public double epoch() {
      return MID;
    }
}
