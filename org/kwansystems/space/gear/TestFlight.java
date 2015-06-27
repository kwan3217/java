package org.kwansystems.space.gear;

import java.util.*;

import org.kwansystems.space.gear.mass.*;
import org.kwansystems.space.universe.EmptyUniverse;
import org.kwansystems.space.universe.Universe;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.integrator.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.vector.*;

import static java.lang.Math.*;

public class TestFlight {
  public SixDOFVehicle TestVehicle;
  public ChartRecorder CR;
  public SixDOFIntegrator I;
  public int nChartStep=0;
  public int ChartStepInterval=-1;
  public TestFlightListener listener;
  public TestFlight(SixDOFVehicle LTestVehicle, int LChartStepInterval) {
    TestVehicle=LTestVehicle;
    CR=new ArrayListChartRecorder();
    nChartStep=0;
    ChartStepInterval=LChartStepInterval;    
  }
  public TestFlight(SixDOFVehicle LTestVehicle) {
    this(LTestVehicle,-1);
  }
  public void RecordAdditionalTestData(double T, MathVector X) {}
  public void RecordTestData(double T, MathVector X) {
    if(nChartStep % ChartStepInterval == 0) {
      CR.Record(T, "RVEw", new SixDOFState(X));
      RecordAdditionalTestData(T,X);
    }
  }
  public void StartTest(SixDOFState RVEw0, double RangeZero, double stepSize) {
    I=new SixDOFIntegrator(
      RangeZero, 
      RVEw0,                     
      stepSize,
      TestVehicle
    );
  }
  public void StepTest() {
    double T=I.getT();
    MathVector X=I.getX();
    if(ChartStepInterval>0)RecordTestData(T,X);
    if(listener!=null)listener.StepEventHandler(T,X);
    I.step(); 
    nChartStep++;
  }
  public void DoTest(SixDOFState RVEw0, double RangeZero, double stepTime, int NumSteps) {
    StartTest(RVEw0,RangeZero,stepTime);
    for(int i=0;i<NumSteps;i++) {
      StepTest();
    }
    EndTest();
  }
  public void EndTest() {
    CR.EndOfData();
  }
  public static void main(String[] args) {
    SixDOFState RVEw0=new SixDOFState(new MathVector(), new MathVector(), Quaternion.U, new MathVector(Math.toRadians(1),Math.toRadians(100),Math.toRadians(0)));
    Universe U=new EmptyUniverse();
    InertSixDOFMass M=new InertSixDOFMass("Tumbler",0.5,new MathVector(),SixDOFMass.RectangularPrismI(0.5,0.01,0.075,0.10));
    System.out.println(M);
    SixDOFVehicle Tumbler=new InertSixDOFVehicle(U, M);
    TestFlight A=new TestFlight(Tumbler,1) {
      public void RecordAdditionalTestData(double T, MathVector X) {
        SixDOFState RVEw=new SixDOFState(X);
        MathVector w=RVEw.w();
        CR.Record(T, "w.x", "deg/s",toDegrees(w.X()));
        CR.Record(T, "w.y", "deg/s",toDegrees(w.Y()));
        CR.Record(T, "w.z", "deg/s",toDegrees(w.Z()));
      }
    };
    A.DoTest(RVEw0, 0, 50,50*100);
    A.CR.PrintTable(new HTMLPrinter("Tumbler.html"));
    A.CR.PrintSubTable(new String[]{"w.z"}, new DisplayPrinter());
  }
}
