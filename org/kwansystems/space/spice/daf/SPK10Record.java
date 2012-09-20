package org.kwansystems.space.spice.daf;

import java.io.*;

public class SPK10Record extends SPKRecord {
  public double NDT20,NDD60,BSTAR,INCL,NODE0,ECC,OMEGA,M0,N0,EPOCH,NU_Obl,NU_Lon,dObl_dt,dLon_dt;
  public SPK10Record(double LstartValid, double LendValid, double[] in) {
    super(LstartValid,LendValid);
    int index=0;
    NDT20=  in[index]; index++;
    NDD60=  in[index]; index++;
    BSTAR=  in[index]; index++;
    INCL=   in[index]; index++;
    NODE0=  in[index]; index++;
    ECC=    in[index]; index++;
    OMEGA=  in[index]; index++;
    M0=     in[index]; index++;
    N0=     in[index]; index++;
    EPOCH=  in[index]; index++;
    NU_Obl= in[index]; index++;
    NU_Lon= in[index]; index++;
    dObl_dt=in[index]; index++;
    dLon_dt=in[index]; index++;
  }
  public void printToWriter(PrintWriter ouf) {
    super.printToWriter(ouf);
    ouf.printf("NDT20: %f\n",NDT20);
    ouf.printf("NDD60: %f\n",NDD60);
    ouf.printf("BSTAR: %f\n",BSTAR);
    ouf.printf("INCL: %f\n",INCL);
    ouf.printf("NODE0: %f\n",NODE0);
    ouf.printf("ECC: %d\n",ECC);
    ouf.printf("OMEGA: %d\n",OMEGA);
    ouf.printf("M0: %d\n",M0);
    ouf.printf("N0: %d\n",N0);
    ouf.printf("EPOCH: %d\n",EPOCH);
    ouf.printf("NU_Obl: %d\n",NU_Obl);
    ouf.printf("NU_Lon: %d\n",NU_Lon);
    ouf.printf("dObl_dt: %d\n",dObl_dt);
    ouf.printf("dLon_dt: %d\n",dLon_dt);
  }

  @Override
  public double epoch() {
    return EPOCH;
  }

  public double[] Evaluate(double ET) {
    throw new UnsupportedOperationException("Not supported yet.");
//    double[] STATE=new double[7];
//    return STATE;
  }
}
