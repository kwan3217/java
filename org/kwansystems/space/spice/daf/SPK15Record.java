package org.kwansystems.space.spice.daf;

import java.io.*;
import org.kwansystems.tools.time.*;
import static org.kwansystems.tools.time.TimeUnits.*;
import static org.kwansystems.tools.time.TimeScale.*;
import static org.kwansystems.tools.time.TimeEpoch.*;
import org.kwansystems.tools.vector.*;

public class SPK15Record extends SPKRecord {
  public double spk15epoch;   //Record Epoch, seconds from J2000 TDB
  public MathVector traj_pole,peri,cb_pole;
  public double p,e,j2_flag,mu,j2,re;

  public SPK15Record(double LstartValid, double LendValid, double[] in) {
    super(LstartValid,LendValid);
    spk15epoch=in[0];
    traj_pole=new MathVector(in[1],in[2],in[3]);
    peri=new MathVector(in[4],in[5],in[6]);
    p=in[7];
    e=in[8];
    j2_flag=in[9];
    cb_pole=new MathVector(in[10],in[11],in[12]);
    mu=in[13];
    j2=in[14];
    re=in[15];
  }
  public void printToWriter(PrintWriter ouf) {
    ouf.println("Periapse epoch:  "+new Time(spk15epoch,Seconds,TDB,J2000).toString());
    ouf.println("Trajectory pole: ");
    ouf.printf("  %s\n",traj_pole.toString());
    ouf.println("Periapsis unit vector: ");
    ouf.printf("  %s\n",peri.toString());
    ouf.println("Semi-parameter: ");
    ouf.printf("  %22.15e\n",p);
    ouf.println("Eccentricity: ");
    ouf.printf("  %22.15e\n",e);
    ouf.println("J2 Flag: ");
    ouf.printf("  %22.15e\n",j2_flag);
    ouf.println("Central body pole vector: ");
    ouf.printf("  %s\n",cb_pole.toString());
    ouf.println("Central body GM: ");
    ouf.printf("  %22.15e\n",mu);
    ouf.println("Central body J2: ");
    ouf.printf("  %22.15e\n",j2);
    ouf.println("Central body Re: ");
    ouf.printf("  %22.15e\n",re);
  }

  @Override
  public double epoch() {
    return spk15epoch;
  }

  public double[] Evaluate(double ET) {
    throw new UnsupportedOperationException("Not supported yet.");
//    double[] STATE=new double[7];
//    return STATE;
  }
}
