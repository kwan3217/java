package org.kwansystems.space.launch;

import org.kwansystems.space.planet.*;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.integrator.*;
import org.kwansystems.tools.table.*;
import org.kwansystems.tools.vector.*;

import java.io.*; 

//SA-01 - The launch which did happen. 
//A rocket identical to SA-01 is fired using SA-01's pitch program.

public class SA01 {
  //LC 34 coordinates - NAD27 converted to WGS-84
  public static final double LaunchLat= 28.52181*Math.PI/180.0;
  public static final double LaunchLon=-80.56117*Math.PI/180.0;
//  public static final double LaunchLon=0*Math.PI/180.0;
  private static ArrayListChartRecorder C;
  private static Planet E;
  public static MathVector LLAtoXYZ(double Lat, double Lon, double Alt) {
    return new MathVector(Alt*Math.cos(Lat)*Math.cos(Lon),Alt*Math.cos(Lat)*Math.sin(Lon),Alt*Math.sin(Lat));
  }
  public static MathVector PadPos(double T) {
    return LLAtoXYZ(LaunchLat,2*Math.PI*T/E.Trot+LaunchLon,E.R+10);
  }
  public static double Downrange(double T,MathState X) {
    return E.R*MathVector.vangle(PadPos(T),X.R());
  }
  public static double Altitude(MathState X) {
    return X.R().length()-E.R;
  }
  public static void main(String args[]) throws IOException {
    E=new SimpleEarth();
    C=new ArrayListChartRecorder();
    int StepsPerSecond=10;
    double LiftoffRangeTime=0.89;
    MathState Start=new MathState(
                          PadPos(LiftoffRangeTime),
			  E.Wind(new MathState(PadPos(LiftoffRangeTime),new MathVector(0,0,0)))
			); //Launch from Cape Canaveral
    Integrator I=new RungeKutta(
       LiftoffRangeTime,          //Initial Time (Range time of liftoff)
       (MathState)Start.clone(),  //Initial state
       1.0/(double)StepsPerSecond,                       //Time Step
       new SecondDerivative(3,new DerivativeSet[] {
         new Gravity(E.GM),
         new MultiStage("SA-01",
                        new Rocket[] {new SaturnIA(null,E,C)},
                        new double[] {9e99},  //Spacecraft sep (never)
                        new int[] {0}, //Columns
                        new PitchProgramGuidance(
			  new LinearTable(new double[] {0,17.89,20.89,32.39,42.89,46.79,85.79,86.99,95.99,97.19,100.19,102.00},
			                  new double[][] {{0, 0.00, 2.00, 2.00, 9.00, 9.00,35.00,35.00,41.00,41.00, 43.00, 43.00}}),
			  100, 
			  E,C
			),
			E,C)
       }) //Equations of motion
    );
    MathState S2=null;
    PrintWriter ouf;
    int Steps=0;
    while(I.getX().length()>E.R-0.010 & I.getT()<800) { //Since we are planning on orbiting, better have two cutoffs.
      I.step();
      Steps++;
      if(Steps%(10*StepsPerSecond)==0) {
        if(Steps%(100*StepsPerSecond)==0) {
          System.out.print((int)(I.getT()+0.5)); 
        } else {
          System.out.print(".");
        }
      }
      S2=(MathState)I.getX();
      C.Record(I.getT(), "Position","m",S2.R());
      C.Record(I.getT(), "Altitude","m",Altitude(S2));
      C.Record(I.getT(), "Downrange","m",Downrange(I.getT(), S2));
      C.Record(I.getT(), "Shot","s,m,m",new MathVector(I.getT(), Altitude(S2),Downrange(I.getT(), S2)));
    }
    System.out.println("\nDone with integration");
    System.out.println("Apogee:      "+C.columnMax("Altitude"));
    System.out.println("Downragne:   "+C.columnMax("Downrange"));
    System.out.println("Impact time: "+C.getTMax());
    System.out.println("Writing altitude profile");
    ouf=new PrintWriter(new FileWriter("alt.ps"));
    StripchartPrinter Ps=new StripchartPrinter(ouf);
    C.PrintSubTable(new String[] {"Altitude"},Ps);
    System.out.println("Writing dynamic pressure profile");
    Ps=new StripchartPrinter("q-SA01.ps");
    C.PrintSubTable(new String[] {"Dynamic pressure"},Ps);
    System.out.println("Writing Shot data");
    C.PrintSubTable(new String[] {"Shot"},new POVColumnPrinter("shot-SA01.inc"));
    C.PrintSubTable(new String[] {"Position"},new POVColumnPrinter("pos-SA01.inc","Pos"));
    C.PrintSubTable(new String[] {"SA-01 Ground Relative Velocity"},new POVColumnPrinter("vel-SA01.inc","AirVel"));
    C.PrintSubTable(new String[] {"SA-01 Acceleration from Thrust"},new POVColumnPrinter("acc-SA01.inc","Acc"));
    C.PrintSubTable(new String[] {"Atmospheric Density"},new POVColumnPrinter("dens-SA01.inc","Dens"));
    System.out.println("Writing telemetry");
    C.PrintTable(new HTMLPrinter("telemetry-SA01.html"));
  }
}
