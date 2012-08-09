package org.kwansystems.space.gator;

import java.io.*;
import java.util.*;

import org.kwansystems.space.ephemeris.*;
import org.kwansystems.space.ephemeris.jupiter.JupiterSatE5;
import org.kwansystems.space.gear.*;
import org.kwansystems.space.gear.actuator.Thruster;
import org.kwansystems.tools.integrator.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeScale;
import org.kwansystems.tools.time.TimeUnits;
import org.kwansystems.tools.vector.*;

import static org.kwansystems.tools.time.Time.*;

public class Physics extends Thread {
  Time CurrentTime;  //Simulator current time, in TDB, advances by current TimeRate
  Time Epoch;        //Epoch for railmovers
  Date LastTime;     //Wall time that last step occurred, used to calculate stepSize
  MathState ProbeState;
  double ScanPlatformAz=0;
  double ScanPlatformEl=0;
  double ScanPlatformFov=45;
  double TimeRate=1.0;
  Ephemeris Jupiter,Io,Europa,Ganymede,Callisto;
  Ephemeris[] Movers;
  Ephemeris Platform;
  DerivativeSet ProbeEquations;
  Integrator Stepper;
  Thruster ProbeThruster;
  private boolean ThrustActive;
  private double ThrustAzimuth;
  private double ThrustAmount;
  private String SaveFn;
  int UpdateCount;
  public Physics() throws IOException {
    ProbeState=null;
    StartPhysics();
  }
  public void StartPhysics() {
    CurrentTime=new Time(TimeUnits.Seconds,TimeScale.UTC); 
    Epoch=new Time(1100000000,TimeUnits.Seconds);
    Jupiter =new FixedEphemeris(new MathVector(0,0,0),null);
    Io      =JupiterSatE5.satArray[1];
    Europa  =JupiterSatE5.satArray[2];
    Ganymede=JupiterSatE5.satArray[3];
    Callisto=JupiterSatE5.satArray[4];
    Movers=new Ephemeris[] {Jupiter,Io,Europa,Ganymede,Callisto};

    UpdateCount=0;  
    int OrbitCenter=3;
    double R=2700;      //Platform orbital radius, km (skimming a mere 66km over the icy surface of Ganymede)
    double CenterGM=JupiterSatE5.satGM[OrbitCenter];
    Ephemeris CenterMover=Movers[OrbitCenter];
    double V=Math.sqrt(CenterGM/R);
    double T=(2*Math.PI*R)/V;
    System.out.println("Platform V: "+V+"km/sec T: "+T+"sec ("+(T/3600.0)+"hr)");

    //Platform is presumed to expend whatever energy necessary to maintain 
    //perfectly circular orbit about chosen body
    double AngularVel=Math.toRadians(360/T);
    Platform=new CircleEphemeris(new MathVector(0,0,0),R,AngularVel,0,CurrentTime,CenterMover);
    if(ProbeState==null) ProbeState=Platform.getState(CurrentTime);
    ProbeThruster=new Thruster();
    ProbeEquations=new ThreeDOF(
      new MoverGravity(Jupiter, JupiterSatE5.JupiterGM, new Time(Epoch)),
      new MoverGravity(Io,      JupiterSatE5.IoGM,      new Time(Epoch)),
      new MoverGravity(Europa,  JupiterSatE5.EuropaGM,  new Time(Epoch)),
      new MoverGravity(Ganymede,JupiterSatE5.GanymedeGM,new Time(Epoch)),
      new MoverGravity(Callisto,JupiterSatE5.CallistoGM,new Time(Epoch)),
      ProbeThruster
    );                       //Equations of motion
    Stepper=new RungeKutta(0,null,0,null);    
    System.out.println("Probe:    "+((MathState)(MathVector.sub(ProbeState,Ganymede.getState(CurrentTime)))));
    System.out.println("Platform: "+((MathState)(MathVector.sub(Platform.getState(CurrentTime),Ganymede.getState(CurrentTime)))));
    System.out.println("Rel:      "+((MathState)(MathVector.sub(ProbeState,Platform.getState(CurrentTime)))));
    LastTime=null;
  }
  public void Update() {
    Date Now=new Date(); 
    double stepSize;
    if(LastTime!=null) {
      stepSize=(double)(Now.getTime()-LastTime.getTime())*TimeRate/1000.0;
      System.out.println("StepSize: "+stepSize);
      int NumSteps=10;
      double TSinceEpoch=CurrentTime.sub(Epoch);
      for(int i=0;i<NumSteps;i++) {
        ProbeState=(MathState)Stepper.stepGuts(
          ProbeEquations,
          TSinceEpoch+i*stepSize/NumSteps,
          ProbeState,
          stepSize/NumSteps
        );
      }
      CurrentTime.add(stepSize);
    }
    System.out.println("TimeRate: "+TimeRate);
    System.out.println("CurrentTime: "+CurrentTime);
    System.out.println("Thruster: "+ThrustActive);
    LastTime=Now;
    if(SaveFn != null) {
      try {
        ObjectOutputStream Ouf=new ObjectOutputStream(new FileOutputStream(SaveFn));
        Ouf.writeObject(CurrentTime);
        Ouf.writeObject(ProbeState);
        Ouf.close();
      } catch (Exception E) {E.printStackTrace();}
    }
    UpdateCount++;
  }

  public double getTime() {
    return CurrentTime.JD();
  }

  public MathVector getPos() {
    return ProbeState.R();
  }

  public MathVector getVel() {
    return ProbeState.V();
  }

  public MathStateTime getStateTime() {
    Update();
    return new MathStateTime(ProbeState,CurrentTime);
  }

  public void run() {
    while(true) {
      UpdateCount=0;
      try {
        sleep(5000);
      } catch (InterruptedException E) {;}
      if(UpdateCount==0) Update();
    }
  }

  public void setTimeRate(double TR) {
    Update();
    TimeRate=TR;
  }

  public double getTimeRate() {
    return TimeRate;
  }

  public void setScanPlatformAz(double Az) {
    ScanPlatformAz=Az;
  }

  public double getScanPlatformAz() {
    return ScanPlatformAz;
  }

  public void setScanPlatformEl(double El) {
    ScanPlatformEl=El;
  }
  public double getScanPlatformEl() {
    return ScanPlatformEl;
  }
  public void setScanPlatformFov(double Fov) {
    ScanPlatformFov=Fov;
  }
  public double getScanPlatformFov() {
    return ScanPlatformFov;
  }
  public void AimScanPlatform(double Az, double El, double Fov) {
    setScanPlatformAz(Az);
    setScanPlatformEl(El);
    setScanPlatformFov(Fov);
  }
  public static double SphereOfInfluence(double r,double M, double m) {
    return r*Math.pow(m/M,0.4);
  }
  public void Photograph() throws IOException {
    Photograph(ScanPlatformAz,ScanPlatformEl,ScanPlatformFov,1000,1000);
  }
  public void Photograph(double Az, double El, double Angle, int Width, int Height) throws IOException {
    Update();
    Photograph(Az,El,Angle,Width,Height,"photograph.pov","photograph.png");
  }
  public void writeStateGuts(PrintWriter Ouf) throws IOException {
    Ouf.println("//Physics package generated scene for a Photograph");
    Ouf.println("//Time: "+CurrentTime+"("+getTime()+")");
    Ouf.println("#declare JupiterPos="+Jupiter.getState(CurrentTime).R()+";");
    Ouf.println("#declare JupiterVel="+Jupiter.getState(CurrentTime).V()+";");
    Ouf.println("#declare IoPos="+Io.getState(CurrentTime).R()+";");
    Ouf.println("#declare IoVel="+Io.getState(CurrentTime).V()+";");
    Ouf.println("//Sphere of influence (km): "+SphereOfInfluence(Io.getState(CurrentTime).length(),JupiterSatE5.JupiterGM,JupiterSatE5.IoGM));
    Ouf.println("#declare EuropaPos="+Europa.getState(CurrentTime).R()+";");
    Ouf.println("#declare EuropaVel="+Europa.getState(CurrentTime).V()+";");
    Ouf.println("//Sphere of influence (km): "+SphereOfInfluence(Europa.getState(CurrentTime).length(),JupiterSatE5.JupiterGM,JupiterSatE5.EuropaGM));
    Ouf.println("#declare GanymedePos="+Ganymede.getState(CurrentTime).R()+";");
    Ouf.println("#declare GanymedeVel="+Ganymede.getState(CurrentTime).V()+";");
    Ouf.println("//Sphere of influence (km): "+SphereOfInfluence(Ganymede.getState(CurrentTime).length(),JupiterSatE5.JupiterGM,JupiterSatE5.GanymedeGM));
    Ouf.println("#declare CallistoPos="+Callisto.getState(CurrentTime).R()+";");
    Ouf.println("#declare CallistoVel="+Callisto.getState(CurrentTime).V()+";");
    Ouf.println("//Sphere of influence (km): "+SphereOfInfluence(Callisto.getState(CurrentTime).length(),JupiterSatE5.JupiterGM,JupiterSatE5.CallistoGM));
    Ouf.println("#declare ProbePos="+ProbeState.R()+";");
    Ouf.println("#declare ProbeVel="+ProbeState.V()+";");
  }
  public void writeCameraGuts(PrintWriter Ouf,double Az, double El, double Angle, int Width, int Height) throws IOException {
    Ouf.println("#declare Angle="+ScanPlatformFov+";");
    Ouf.println("#declare El="+El+";");
    Ouf.println("#declare Az="+Az+";");
  }
  public void writeState() throws IOException {
    PrintWriter Ouf=new PrintWriter(new FileWriter("state.inc"));
    writeStateGuts(Ouf);
    Ouf.close();
  }
  public void writeStateCamera(double Az, double El, double Angle, int Width, int Height) throws IOException {
    PrintWriter Ouf=new PrintWriter(new FileWriter("state.inc"));
    writeStateGuts(Ouf);
    writeCameraGuts(Ouf,Az,El,Angle,Width,Height);
    Ouf.close();
  }
  public void Photograph(double Az, double El, double Angle, int Width, int Height, String SceneFilename, String ImageFilename) throws IOException {
    writeStateCamera(Az,El,Angle,Width,Height);
    String CmdLine="/usr/bin/megapov +d +w"+Width+" +h"+Height+" +i"+SceneFilename+" +o"+ImageFilename+" +p +fn +l/mnt/transfer/codebase/Other/pov/PovSolar";
    Runtime.getRuntime().exec(CmdLine);
  }
  public void Quit() {
    System.exit(0);
  }
  private void setThrust() {
    Update();
    if (ThrustActive) {
      ProbeThruster.setThrust(new MathVector(Math.sin(Math.toRadians(ThrustAzimuth))*ThrustAmount,0,Math.cos(Math.toRadians(ThrustAzimuth))*ThrustAmount));
    } else {
      ProbeThruster.setThrust(new MathVector(0,0,0));
    }
  }
  public boolean getThrustActive() {
    return ThrustActive;
  }
  public void setThrustActive(boolean LThrustActive) {
    ThrustActive = LThrustActive;
    setThrust();
  }
  public double getThrustAzimuth() {
    return ThrustAzimuth;
  }
  public void setThrustAzimuth(double LThrustAzimuth) {
    ThrustAzimuth = LThrustAzimuth;
    setThrust();
  }
  public double getThrustAmount() {
    return ThrustAmount;
  }
  public void setThrustAmount(double LThrustAmount) {
    ThrustAmount = LThrustAmount;
    setThrust();
  }
}
