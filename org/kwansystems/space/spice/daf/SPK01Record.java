package org.kwansystems.space.spice.daf;

import java.io.*;
import org.kwansystems.tools.time.*;
import static org.kwansystems.tools.time.TimeUnits.*;
import static org.kwansystems.tools.time.TimeScale.*;
import static org.kwansystems.tools.time.TimeEpoch.*;

public class SPK01Record extends SPKRecord {
  public double TL;                        //Reference Epoch
  public double[] G=new double[16];        //Step size vector, 15 elements
  public double[] REFPOS=new double[4];    //Reference position
  public double[] REFVEL=new double[4];    //Reference velocity
  public double[][] DT=new double[16][4];  //MDA component, 15 elements
  public int   KQMAX1;                     //Maximum integration order plus 1
  public int[] KQ=new int[4];              //Integration order - 3 elements

  //Total of 71 elements -- Arrays are one-based, to match the FORTRAN. Variable
  //names match those of SPKE01.f
  public SPK01Record(double LstartValid, double LendValid, double[] in) {
    super(LstartValid,LendValid);
    int index=0;
    TL=in[index]; index++;
    for(int i=0;i<15;i++) G[i+1]=in[index+i]; index+=15;
    REFPOS[1]=in[index]; index++;
    REFVEL[1]=in[index]; index++;
    REFPOS[2]=in[index]; index++;
    REFVEL[2]=in[index]; index++;
    REFPOS[3]=in[index]; index++;
    REFVEL[3]=in[index]; index++;
    for(int j=1;j<=3;j++) {
      for(int i=0;i<15;i++) DT[i+1][j]=in[index+i]; index+=15;
    }
    KQMAX1=(int)in[index]; index++;
    for(int i=0;i<3;i++) KQ[i+1]=(int)in[index+i]; index+=3;
  }
  public void printToWriter(PrintWriter ouf) {
    super.printToWriter(ouf);
    ouf.printf("Reference Epoch:   %s\n",new Time(TL,Seconds,TDB,J2000).toString());
    ouf.println("Step size:");
    for(int i=1;i<=15;i++) ouf.printf("  %22.15e\n",G[i]);
    ouf.println("Reference Position:");
    for(int i=1;i<=3;i++)  ouf.printf("  %22.15e\n",REFPOS[i]);
    ouf.println("Reference Velocity:");
    for(int i=1;i<=3;i++)  ouf.printf("  %22.15e\n",REFVEL[i]);
    ouf.println("Difference Array:");
    for(int i=1;i<=15;i++) ouf.printf("  %22.15e  %22.15e  %22.15e\n",DT[i][1],DT[i][2],DT[i][3]);
    ouf.printf("MaxInt+1:       %d\n",KQMAX1);
    ouf.println("Integration Order:");
    for(int i=1;i<=3;i++) ouf.printf("  %d\n",KQ[i]);
  }
  public double[] Evaluate(double ET) {
    double[] STATE=new double[7];
    double DELTA     = ET     - TL;
    double TP        = DELTA;
    int MQ2       = KQMAX1 - 2;
    int KS        = KQMAX1 - 1;

    //This is clearly collecting some kind of coefficients.
    //The problem is that we have no idea what they are...
    //
    //The G coefficients are supposed to be some kind of step size
    //vector.
    //
    //TP starts out as the delta t between the request time
    //and the time for which we last had a state in the MDL file.
    //We then change it from DELTA  by the components of the stepsize
    //vector G.

    double[] FC=new double[MQ2+2];
    double[] WC=new double[MQ2+1];

    for(int J = 1;J<=MQ2;J++) {
      FC[J+1] = TP    / G[J];
      WC[J]   = DELTA / G[J];
      TP      = DELTA + G[J];
    }

    //Collect KQMAX1 reciprocals.
    double[] W=new double[KQMAX1+1];
    for(int J = 1;J<=KQMAX1;J++) {
      W[J] = 1.0/(double)J;
    }

    //Compute the W(K) terms needed for the position interpolation
    //(Note,  it is assumed throughout this routine that KS, which
    //starts out as KQMAX1-1 (the ``maximum integration'')
    //is at least 2.
    int JX  = 0;
    int KS1 = KS - 1;

    while( KS >= 2 ) {
      JX  = JX+1;
      for(int J = 1;J<=JX;J++) {
        W[J+KS] = FC[J+1]*W[J+KS1] - WC[J]*W[J+KS];
      }

      KS  = KS1;
      KS1 = KS1 - 1;
    }

    //Perform position interpolation: (Note that KS = 1 right now.
    //We don't know much more than that.)
    for(int I = 1;I<=3;I++) {
      int KQQ = KQ[I];
      double SUM = 0.0;

      for(int J = KQQ;J>=1;J--) {
        SUM = SUM + DT[J][I]*W[J+KS];
      }

      STATE[I] = REFPOS[I]  +  DELTA * ( REFVEL[I] + DELTA*SUM );
    }

    //Again we need to compute the W(K) coefficients that are
    //going to be used in the velocity interpolation.
    //(Note, at this point, KS = 1, KS1 = 0.)
    for(int J = 1;J<=JX;J++) {
      W[J+KS] = FC[J+1]*W[J+KS1] - WC[J]*W[J+KS];
    }

    KS = KS - 1;


    //Perform velocity interpolation:

    for(int I = 1;I<=3;I++) {
      int KQQ = KQ[I];
      double SUM = 0.0;

      for(int J=KQQ;J>=1;J--) {
        SUM = SUM + DT[J][I]*W[J+KS];
      }
      STATE[I+3] = REFVEL[I] + DELTA*SUM;

    }

    //That's all folks.  We don't know why we did anything, but
    //at least we can tell structurally what we did.
    return STATE;
  }

    @Override
    public double epoch() {
      return TL;
    }
}
