package org.kwansystems.space.gear;

import org.kwansystems.space.universe.*;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.integrator.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.vector.*;

import java.io.*;
import static java.lang.Math.*;

public abstract class TestStand {
  public SixDOFVehicle TestVehicle;
  MathVector R0;
  public ChartRecorder CR;
  public TestStand(SixDOFVehicle LTestVehicle, MathVector LR0, ChartRecorder LCR) {
    TestVehicle=LTestVehicle;
    R0=LR0;
    CR=LCR;
  }
  public abstract void RecordTestData(double T, MathVector X);
  public void DoTest(final SixDOFState RVEw0, double RangeZero, int fps, int duration, double[] PropLevels) throws IOException {
    Integrator I=new RungeKutta(
      RangeZero, 
      new MathVector(
        new MathVector[] {
          RVEw0,                     //State (Nailed to the test stand)
          new MathVector(PropLevels) //Prop Levels
        }
      ), 
      1.0/((double)(fps)),
      TestVehicle
    );
    int NumSteps;
    NumSteps=duration*fps;
    for(int i=0;i<NumSteps;i++) {
      RecordTestData(I.getT(),I.getX());
      if(true) {
        if(i % (fps*10) == 0) {
          System.out.println(I.getT());
        } else if(i % fps == 0) {
          System.out.println(".");
        }
      }
      I.step(); 
    }
    RecordTestData(I.getT(),I.getX());
    CR.EndOfData();
  }
  public final static SixDOFState RVEw0=new SixDOFState(new MathVector(),new MathVector(), new Quaternion(new AxisAngle(new MathVector(0,1,0),Math.toRadians(-90))),new MathVector());
}
