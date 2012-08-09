package org.kwansystems.space.spice.daf;

import java.io.*;

public class SPK09Record extends SPKRecord {
  public double spk09epoch;   //Record Epoch, seconds from J2000 TDB
  public double X,dX;    //Components of
  public double Y,dY;    //Chebyshev polynomial coefficients for X component
  public double Z,dZ;    //Chebyshev polynomial coefficients for X component

  public SPK09Record(double LstartValid, double LendValid, double[] in) {
    super(LstartValid,LendValid);
    spk09epoch=LstartValid;
    int index=0;
    X =in[index]; index++;
    Y =in[index]; index++;
    Z =in[index]; index++;
    dX=in[index]; index++;
    dY=in[index]; index++;
    dZ=in[index]; index++;
  }
  public void printToWriter(PrintWriter ouf) {
    super.printToWriter(ouf);
    ouf.println("Position Vector: ");
    ouf.printf("  %22.15e  %22.15e  %22.15e\n",X,Y,Z);
    ouf.println("Velocity Vector: ");
    ouf.printf("  %22.15e  %22.15e  %22.15e\n",dX,dY,dZ);
  }

  @Override
  public double epoch() {
    return spk09epoch;
  }

  public double[] Evaluate(double ET) {
    throw new UnsupportedOperationException("Not supported yet.");
//    double[] STATE=new double[7];
//    return STATE;
  }
}
