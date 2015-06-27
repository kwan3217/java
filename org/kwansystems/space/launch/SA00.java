package org.kwansystems.space.launch;

import org.kwansystems.space.planet.*;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.integrator.*;
import org.kwansystems.tools.table.*;
import org.kwansystems.tools.vector.*;

import java.io.*;

//SA-00 - The launch which never happened. 
//A rocket identical to SA-01 is fired straight up.

public class SA00 {
  //LC 34 coordinates - NAD27 converted to WGS-84
  public static final double LaunchLat= 28.52181*Math.PI/180.0;
//  public static final double LaunchLon=-80.56117*Math.PI/180.0;
  public static final double LaunchLon=0*Math.PI/180.0;
  private static ArrayListChartRecorder C;
  private static Planet E;
  public static MathVector LLAtoXYZ(double Lat, double Lon, double Alt) {
    return new MathVector(Alt*Math.cos(Lat)*Math.cos(Lon),Alt*Math.cos(Lat)*Math.sin(Lon),Alt*Math.sin(Lat));
  }
  public static MathVector PadPos(double T) {
    return E.lla2xyz(LaunchLat,2*Math.PI*T/E.Trot+LaunchLon,10);
  }
  public static double Downrange(double T,MathState X) {
    return E.R*MathVector.vangle(PadPos(T),X.R());
  }
  public static double Altitude(MathVector X) {
    return E.Alt(X);
  }
  public static void main(String args[]) throws IOException {
    E=new SimpleEarth();
    C=new ArrayListChartRecorder();
    int StepsPerSecond=10;
    MathState Start=new MathState(PadPos(0),E.Wind(new MathState(PadPos(0),new MathVector(0,0,0)))); //Launch from Cape Canaveral
    Integrator I=new RungeKutta(
       0,                         //Initial Time
       (MathState)Start.clone(),  //Initial state
       1.0/(double)StepsPerSecond,                       //Time Step
       new SecondDerivative(3,new DerivativeSet[] {
         new Gravity(E.GM),
         new MultiStage("SA-00",
                        new Rocket[] {new SaturnIA(null,E,C)},
                        new double[] {9e99},  //Spacecraft sep (never)
                        new int[] {0}, //Columns
                        new PitchProgramGuidance(
			  new StepTable(new double[] {0},new double[][] {{0}}),
			  100, 
			  E,C
			),
			E,C)
       }) //Equations of motion
    );
    MathState S2;
    int Steps=0;
    while(E.Alt(I.getX())>=10 & I.getT()<800) { //Since we are planning on orbiting, better have two cutoffs.
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
      C.Record(I.getT(), "Altitude","m",Altitude(S2));
      C.Record(I.getT(), "Downrange","m",Downrange(I.getT(), S2));
      C.Record(I.getT(), "Shot","s,m,m",new MathVector(I.getT(), Altitude(S2),Downrange(I.getT(), S2)));
    }
    System.out.println("\nDone with integration");
    System.out.println("Writing Position");
    System.out.println("Writing altitude and Dynamic Pressure profile");
    C.PrintSubTable(new String[] {"Altitude"},new DisplayPrinter());
    System.out.println("Writing Shot data");
    C.PrintSubTable(new String[] {"Shot"},new POVColumnPrinter("shot-SA00.inc"));
    System.out.println("Writing telemetry");
    C.PrintTable(new HTMLPrinter("telemetry-SA00.html"));
  }
}
