package org.kwansystems.space.launch;

import org.kwansystems.space.planet.*;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.integrator.*;
import org.kwansystems.tools.vector.*;
import java.io.*;

public class Launch {
  public static final double LaunchLat= 28.60832*Math.PI/180.0;
  public static final double LaunchLon=-80.60414*Math.PI/180.0;
  private static ArrayListChartRecorder C=new ArrayListChartRecorder();
  private static Planet E;
  public static MathVector LLAtoXYZ(double Lat, double Lon, double Alt) {
    return new MathVector(Alt*Math.cos(Lat)*Math.cos(Lon),Alt*Math.cos(Lat)*Math.sin(Lon),Alt*Math.sin(Lat));
  }
  public static MathVector PadPos(double T) {
    return LLAtoXYZ(LaunchLat,2*Math.PI*T/E.Trot+LaunchLon,E.R+0.010);
  }
  public static double Downrange(double T,MathState X) {
    return E.R*MathVector.vangle(PadPos(T),X.R());
  }
  public static double Altitude(MathState X) {
    return X.R().length()-E.R;
  }
  public static void main(String args[]) throws IOException {
    E=new SimpleEarth();
    int StepsPerSecond=2;
    MathState Start=new MathState(PadPos(0),E.Wind(new MathState(PadPos(0),new MathVector(0,0,0)))); //Launch from Cape Canaveral
    Rocket Stage1=new SaturnIC(null,E,C);
    Rocket Stage2=new SaturnII(null,E,C);
    Rocket Stage3=new SaturnIVB(null,E,C);
    Integrator I=new RungeKutta(
       0,                         //Initial Time
       (MathState)Start.clone(),  //Initial state
       1.0/(double)StepsPerSecond,                        //Time Step
       new SecondDerivative(3,new DerivativeSet[] {
         new Gravity(E.GM),
         new MultiStage("SA-503",
                        new Rocket[] {Stage1,
                                      new InertStage("Lower Interstage",4677,0,E,C),
                                      new InertStage("Launch Escape System",4061,0,E,C),
                                      Stage2,
                                      Stage3,
                                      new InertStage("Apollo 8 Spacecraft",28816,0,E,C)
                                      },
                        new double[] {154.47, //S-IC sep
                                      184.47, //lower interstage sep
                                      188.60, //LES sep
                                      524.90, //S-II sep
                                      9e99,9e99},  //Spacecraft sep (never)
                        new int[] {0,0,0,0,0,0}, //Columns
                        new SaturnGuidance(192,18.5,196.22,+0.1,E,C),E,C)
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
      C.Record(I.getT(), "Velocity","m/s",S2.V());
      C.Record(I.getT(), "Acceleration","m/s^2",new MathState(I.LastDxDt).V());
      C.Record(I.getT(), "Altitude","m",Altitude(S2));
      C.Record(I.getT(), "Downrange","m",Downrange(I.getT(), S2));
      C.Record(I.getT(), "Shot2",null,new MathVector(I.getT(), Altitude(S2),Downrange(I.getT(), S2)));
    }
    System.out.println("\nDone with integration");
    System.out.println("Writing Position");
    /*
    ouf=new PrintWriter(new FileWriter("SIVBPos.data"));
    C.PrintSubTable(new String[] {"Saturn V Position"},new POVColumnPrinter(ouf,"SIVBPos"));
    ouf.close();
    System.out.println("Writing Ground Velocity");
    ouf=new PrintWriter(new FileWriter("SIVBGroundVel.data"));
    C.PrintSubTable(new String[] {"Saturn V Ground Relative Velocity"},new POVColumnPrinter(ouf,"SIVBGroundVel"));
    ouf.close();
    System.out.println("Writing Acceleration");
    ouf=new PrintWriter(new FileWriter("SIVBAccel.data"));
    C.PrintSubTable(new String[] {"Saturn V Total Rocket Acceleration"},new POVColumnPrinter(ouf,"SIVBAccel"));
    ouf.close();
    System.out.println("Writing Shot data");
    ouf=new PrintWriter(new FileWriter("shot.data"));
    C.PrintSubTable(new String[] {"Position"},new POVColumnPrinter(ouf,"Shot"));
    ouf.close();
    System.out.println("Writing Shot2 data");
    ouf=new PrintWriter(new FileWriter("shot2.data"));
    C.PrintSubTable(new String[] {"Shot2"},new POVColumnPrinter(ouf,"Shot2"));
    ouf.close();
    */
	System.out.println("Writing altitude profile");
	C.PrintSubTable(new String[] {"Altitude"},new StripchartPrinter("alt-SA503.ps"));
    System.out.println("Writing telemetry");
//    C.PrintSubTable(new String[] {"Guidance Mode","Altitude","Downrange","Guidance Angle of Attack","Radial Acceleration", "Transverse Acceleration","Thrust","Guidance Comments"},new HTMLPrinter(ouf));
	C.PrintTable(new HTMLPrinter("telemetry-SA503.html"));
  }
}
