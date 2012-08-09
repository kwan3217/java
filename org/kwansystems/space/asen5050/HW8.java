// HW8.java - Driver program
package org.kwansystems.space.asen5050;

import org.kwansystems.space.kepler.*;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.integrator.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeUnits;
import org.kwansystems.tools.vector.*;
import java.io.*;

public class HW8 {
  public static void main(String args[]) throws IOException {
    double StepsPerSecond=1.0;
    double EarthGM=398600.4415; // Units of km and seconds
    MathState Start=new MathState(new MathVector(5492.0, 3984.001, 2.955), // r0
                                                                            // (km)
        new MathVector(-3.931, 5.498, 3.665) // v0 (km/sec)
    );
    Integrator I=new RungeKutta(
      0, // Initial Time
      (MathState)Start.clone(), // Initial state
      1.0/(double)StepsPerSecond, // Time Step
      new SecondDerivative(
        3, // Number of space dimensions
        new DerivativeSet[] { // Equations of motion
          new CentralGravity(EarthGM)
        }
      )
    );
    System.out.print("Integrating");
    ArrayListChartRecorder C=new ArrayListChartRecorder();
    int Steps=0;
    Elements Ekep=new Elements();
    Ekep.PosVelToEle(new MathStateTime(Start, new Time(0, TimeUnits.Seconds)), EarthGM, "km");
    double OrigM=Ekep.M;
    Elements Eint=new Elements();
    while(I.getT()<14400) {
      I.step();
      Steps++;
      if(Steps%(10*StepsPerSecond)==0) {
        if(Steps%(720*StepsPerSecond)==0) {
          if(Steps%(3600*StepsPerSecond)==0) {
            System.out.print((int)(Steps/(3600*StepsPerSecond)));
          } else {
            System.out.print(".");
          }
        }
        MathState StateInt=(MathState)I.getX();
        C.Record(I.getT(), "IntRx", "km", StateInt.R().X());
        C.Record(I.getT(), "IntRy", "km", StateInt.R().Y());
        C.Record(I.getT(), "IntRz", "km", StateInt.R().Z());
        C.Record(I.getT(), "IntVx", "km/s", StateInt.V().X());
        C.Record(I.getT(), "IntVy", "km/s", StateInt.V().Y());
        C.Record(I.getT(), "IntVz", "km/s", StateInt.V().Z());
        Eint.PosVelToEle(new MathStateTime(StateInt, new Time(I.getT(), TimeUnits.Seconds)), EarthGM, "km");
        C.Record(I.getT(), "Inta", "km", Eint.A);
        C.Record(I.getT(), "Inte", null, Eint.E);
        C.Record(I.getT(), "Inti", "deg", Math.toDegrees(Eint.I));
        C.Record(I.getT(), "IntLAN", "deg", Math.toDegrees(Eint.LAN));
        C.Record(I.getT(), "IntAP", "deg", Math.toDegrees(Eint.AP));
        C.Record(I.getT(), "IntTA", "deg", Math.toDegrees(Eint.TA));
        // Keplerian elements propagated forward
        Ekep.M=OrigM+I.getT()*Ekep.N;
        Ekep.MeanToTrue();
        MathState StateKep=Ekep.EleToPosVel();
        C.Record(I.getT(), "KepRx", "km", StateKep.R().X());
        C.Record(I.getT(), "KepRy", "km", StateKep.R().Y());
        C.Record(I.getT(), "KepRz", "km", StateKep.R().Z());
        C.Record(I.getT(), "KepVx", "km/s", StateKep.V().X());
        C.Record(I.getT(), "KepVy", "km/s", StateKep.V().Y());
        C.Record(I.getT(), "KepVz", "km/s", StateKep.V().Z());
      }
    }
    System.out.println(" hours, Done.");
    System.out.print("Writing tables");
    PrintWriter ouf;
    ouf=new PrintWriter(new FileWriter("curve1.ps"));
    StripchartPrinter P=new StripchartPrinter(ouf);
    P.PrintTitles("Position vector by Numerical Integration");
    P.XAxis(0, 4, 4, "Time since epoch (hours)");
    P.YAxis(-7000, 7000, 14, "Position vector component (km)");
    P.setSize(0, 14400, -7000, 7000, ((int)(10*72.0)), ((int)(7.5*72.0)));
    P.PrintColumns(new String[] {"IntRx", "IntRy", "IntRz"}, C);
    P.EndPage();
    System.out.print(".");
    P.NewPage();
    P.PrintTitles("Velocity vector by Numerical Integration");
    P.XAxis(0, 4, 4, "Time since epoch (hours)");
    P.YAxis(-8, 8, 16, "Velocity vector component (km/s)");
    P.setSize(0, 14400, -8, 8, ((int)(10*72.0)), ((int)(7.5*72.0)));
    P.PrintColumns(new String[] {"IntVx", "IntVy", "IntVz"}, C);
    P.EndPage();
    System.out.print(".");
    P.NewPage();
    P.PrintTitles("Position vector by Kepler Propagation");
    P.XAxis(0, 4, 4, "Time since epoch (hours)");
    P.YAxis(-7000, 7000, 14, "Position vector component (km)");
    P.setSize(0, 14400, -7000, 7000, ((int)(10*72.0)), ((int)(7.5*72.0)));
    P.PrintColumns(new String[] {"KepRx", "KepRy", "KepRz"}, C);
    P.EndPage();
    System.out.print(".");
    P.NewPage();
    P.PrintTitles("Velocity vector by Kepler Propagation");
    P.XAxis(0, 4, 4, "Time since epoch (hours)");
    P.YAxis(-8, 8, 16, "Velocity vector component (km/s)");
    P.setSize(0, 14400, -8, 8, ((int)(10*72.0)), ((int)(7.5*72.0)));
    P.PrintColumns(new String[] {"KepVx", "KepVy", "KepVz"}, C);
    P.EndPage();
    System.out.print(".");
    P.NewPage();
    P.PrintTitles("Semimajor axis by Numerical Integration");
    P.XAxis(0, 4, 4, "Time since epoch (hours)");
    P.YAxis(0, 8000, 8, "Semimajor Axis (km)");
    P.setSize(0, 14400, 0, 8000, ((int)(10*72.0)), ((int)(7.5*72.0)));
    P.PrintColumns(new String[] {"Inta"}, C);
    P.EndPage();
    System.out.print(".");
    P.NewPage();
    P.PrintTitles("Eccentricity by Numerical Integration");
    P.XAxis(0, 4, 4, "Time since epoch (hours)");
    P.YAxis(0, 0.02, 4, "Eccentricity");
    P.setSize(0, 14400, 0, 0.02, ((int)(10*72.0)), ((int)(7.5*72.0)));
    P.PrintColumns(new String[] {"Inte"}, C);
    P.EndPage();
    System.out.print(".");
    P.NewPage();
    P.PrintTitles("Angular Elements by Numerical Integration");
    P.XAxis(0, 4, 4, "Time since epoch (hours)");
    P.YAxis(0, 360, 8, "Element Angle (degrees)");
    P.setSize(0, 14400, 0, 360, ((int)(10*72.0)), ((int)(7.5*72.0)));
    P.PrintColumns(new String[] {"Inti", "IntLAN", "IntAP", "IntTA"}, C);
    P.EndPage();
    System.out.print(".");
    ouf.close();
    System.out.println("Done.");
  }
}
