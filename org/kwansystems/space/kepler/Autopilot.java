package org.kwansystems.space.kepler;

import org.kwansystems.space.ephemeris.*;
import org.kwansystems.space.planet.*;
import org.kwansystems.tools.rootfind.*;
import org.kwansystems.tools.time.*;
import org.kwansystems.tools.vector.*;

import static org.kwansystems.space.Constants.*;

import java.io.*;

public class Autopilot {
  public static final CircleEphemeris Io=new CircleEphemeris(
    new MathVector(0,0,0),
    2.819353e-3*MPerAU,
    Math.toRadians(203.48895579033)/86400.0,
    Math.toRadians(106.077187),
    new Time(2.084832E8,TimeUnits.Seconds,TimeScale.UTC,TimeEpoch.Java), //This is the same as JD2443000.5
    null
  );
  public static final CircleEphemeris Europa=new CircleEphemeris(
    new MathVector(0,0,0),
    4.485883e-3*MPerAU,
    Math.toRadians(101.37472473479)/86400.0,
    Math.toRadians(175.731615),
    new Time(2.084832E8,TimeUnits.Seconds,TimeScale.UTC,TimeEpoch.Java),
    null
  );
  public static final CircleEphemeris Ganymede=new CircleEphemeris(
    new MathVector(0,0,0),
    7.155366e-3*MPerAU,
    Math.toRadians( 50.31760920702)/86400.0,
    Math.toRadians(120.558829),
    new Time(2.084832E8,TimeUnits.Seconds,TimeScale.UTC,TimeEpoch.Java),
    null
  );
  public static final CircleEphemeris Callisto=new CircleEphemeris(
    new MathVector(0,0,0),
    12.585464e-3*MPerAU,
    Math.toRadians( 21.57107117668)/86400.0,
    Math.toRadians( 84.444587),
    new Time(2.084832E8,TimeUnits.Seconds,TimeScale.UTC,TimeEpoch.Java),
    null
  );
  public static final CircleEphemeris[] SatTable={Io,Europa,Ganymede,Callisto};
  public static final double G=6.672; //G in km^3/(10^-20kg*s^2)
  public static final double[] mu={ 893.3*G,479.7*G,1482*G,1076*G}; //mass in 10^20kg, total in km^3/s^2
//  public static MathVector SatPos(int SatNum, Time T) {
//    double TT=T.JD()-2443000.5;
//    double arg=KTable[SatNum][0]+TT*KTable[SatNum][1];
//    return new MathVector(Math.cos(arg)*a[SatNum],Math.sin(arg)*a[SatNum],0);
//  }
  public static int I;
  public static Elements InitialConditions(double Periapse, double Apoapse, int CurrentMoon) {
    //Find Initial Condition
    //Periapse is given, should be inside orbit of Io. This gives 2 Io chances each pass.
    //Apoapse is arbitrarily picked
    //Time of impact with current moon is determined to align Periapse along +x
    //Also picked to be close to 1 Jan 2003
    double A=(Periapse+Apoapse)/2;
    double e=(Apoapse-Periapse)/(Apoapse+Periapse);
    //Negative TA means collision on inbound leg, positive->outbound
    double TA=-Math.acos((A*(1-e*e) - SatTable[CurrentMoon].Radius)/(e*SatTable[CurrentMoon].Radius));    
    Time CurrentTime=new Time(2452640.5,TimeUnits.Days,TimeScale.UTC,TimeEpoch.JD); //JD of 1 Jan 2003
    CurrentTime.Units=TimeUnits.Seconds;
    double ThetaTarget=SatTable[CurrentMoon].Theta(CurrentTime);
    ThetaTarget-=2*Math.PI*Math.floor(ThetaTarget/(2*Math.PI)); //Modulo 360deg
    System.out.println("TA:    "+Math.toDegrees(TA));    
    System.out.println("Theta: "+Math.toDegrees(ThetaTarget));  
    //Now, fix it up
    CurrentTime.add((TA-ThetaTarget)/SatTable[CurrentMoon].omega);
    System.out.println("Periapse: "+CurrentTime);  
    ThetaTarget=SatTable[CurrentMoon].Theta(CurrentTime);
    ThetaTarget-=2*Math.PI*Math.floor(ThetaTarget/(2*Math.PI)); //Modulo 360deg
    System.out.println("TA:    "+Math.toDegrees(TA));    
    System.out.println("Theta: "+Math.toDegrees(ThetaTarget));
    Elements E=new Elements();
    E.LengthUnit="km";
    E.A=A;
    E.E=e;
    E.I=0;
    E.LAN=0;
    E.AP=0;
    E.TA=TA;
    E.Epoch=new Time(CurrentTime);
    E.GM=Planet.Jupiter.S.GM/1e9;
    E.FillInElements();
    return E;
  }
  static int NextMoon=3;
  static double NextPeriod=2*Math.PI/SatTable[NextMoon].omega;
  static Time NextTime=new Time();
  static Elements E=new Elements();
  static Elements E2=new Elements();
  static MathStateTime CurrentMoonPos=new MathStateTime(new MathState(),new Time());
  static MathStateTime NextMoonPos=new MathStateTime(new MathState(),new Time());
  static MathStateTime[] Leg1={};
  static int Revs;
  static MathVector VinfIn=new MathVector();
  static MathVector VinfOut=new MathVector();
  static int CurrentMoon=2;
  static double vangle=0;
  public static void main(String[] args) throws IOException {
    final KeplerFG KG=new KeplerFG();
    E=InitialConditions(4e5,15e6,CurrentMoon);
    //Find path to next moon
    Revs=-3;
    double Offset=new Bisector(
      new RootFunction() {
        public double F(double X) {
          Time NextTime=Time.add(E.Epoch,E.Period+10.0*86400.0+X+Revs*NextPeriod);
          System.out.println(NextTime);
          CurrentMoonPos=new MathStateTime(SatTable[CurrentMoon].getState(E.Epoch),E.Epoch);
          NextMoonPos=new MathStateTime(SatTable[NextMoon].getState(NextTime),NextTime);
          Leg1=KG.target(CurrentMoonPos,NextMoonPos,Planet.Jupiter.S.GM/1e9,3);
          E2=new Elements();
          E2.PosVelToEle(Leg1[0],Planet.Jupiter.S.GM/1e9,"km");
          VinfIn=MathVector.sub(E.Propagate(E.Epoch).V(),CurrentMoonPos.S.V());
          VinfOut=MathVector.sub(Leg1[0].S.V(),CurrentMoonPos.S.V());
      	  double Diff=VinfIn.length()-VinfOut.length();
	        vangle=MathVector.vangle(VinfIn,VinfOut);
          System.out.println("Revs:    "+Revs);
          System.out.println("Offset:  "+X);
          System.out.println("VinfIn:  "+VinfIn);
          System.out.println("VinfOut: "+VinfOut);
          System.out.println("vangle:  "+Math.toDegrees(vangle));
          System.out.println("Diff:    "+Diff);
          return Diff;
	      }
      }
    ).Find(
      0,
      114813.76507230819-3600,
      114813.76507230819+3600
    );
    NextTime=Time.add(E.Epoch,E.Period+10.0*86400.0+Offset+Revs*NextPeriod);
    //Determine flyby conditions
    double FlybyE=1/Math.sin(vangle/2);
    System.out.println("FlybyE: "+FlybyE);
    double FlybyA=-mu[CurrentMoon]/Math.pow(VinfIn.length(),2);
    System.out.println("FlybyA: "+FlybyA);
    double FlybyB=Math.sqrt(Math.pow(mu[CurrentMoon]/VinfIn.length(),2)*(FlybyE*FlybyE-1))/VinfIn.length();
    System.out.println("FlybyB: "+FlybyB);
    double FlybyRp=FlybyA*(1-FlybyE*FlybyE)/(1+FlybyE);
    System.out.println("FlybyRp: "+FlybyRp);
    MathVector AimPoint=MathVector.cross(new MathVector(0,0,1),VinfIn); //perpendicular to incoming V
    AimPoint=AimPoint.normal();
    AimPoint=AimPoint.mul(FlybyB);
    AimPoint=MathVector.add(AimPoint,CurrentMoonPos.S.R());
    System.out.println("Ganymede pos: \n"+CurrentMoonPos.S.R());
    System.out.println("Aimpoint: \n"+AimPoint);
    //Target the new aimpoint
    Time ApojoveTime=Time.add(E.TP,-E.Period/2);
    MathStateTime Apojove=new MathStateTime(E.Propagate(ApojoveTime),ApojoveTime);
    MathStateTime AimStateTime=new MathStateTime(new MathState(AimPoint,new MathVector(0,0,0)),E.Epoch);
    System.out.println("Apojove: \n"+Apojove);
    System.out.println("Aimpoint: \n"+AimPoint);
    MathStateTime Leg0[]=KG.target(Apojove,AimStateTime,Planet.Jupiter.S.GM/1e9,1);
    System.out.println("New apojove: "+Leg0[0]);
    System.out.println("DeltaV at Apojove"+(MathVector.sub(Leg0[0].S.V(),Apojove.S.V())));
    //Plot
    /*
    PrintWriter ouf=new PrintWriter(new FileWriter("Autopilot.inc"));
    ouf.println("#declare GanPos="+CurrentMoonPos.S.R()+";");
    ouf.println("#declare CalPos="+NextMoonPos.S.R()+";");
    ouf.println("#include \"PointCount.inc\"\n#declare Stuff=array[PointCount] {");
    int count=0;
    for(double i=E.TP.get()-E.Period/2;i<E.Epoch.get();i+=3600.0) {
      ouf.println(E.Propagate(new Time(i,Time.InSeconds)).R());
      count++;
    }
    for(double i=E.Epoch.get();i<NextTime.get();i+=3600.0) {
      ouf.println(E2.Propagate(new Time(i,Time.InSeconds)).R());
      count++;
    }
    ouf.println("}");
    ouf.close();
    ouf=new PrintWriter(new FileWriter("PointCount.inc"));
    ouf.println("#declare PointCount="+count+";");
    ouf.close();
    ouf=new PrintWriter(new FileWriter("Ballisticity.csv"));
    //Print flyby window ballisticity chart for this flyby
    for(double X=-NextPeriod/4;X<=NextPeriod/4;X+=600) {
      Time NextTime=Time.add(E.Epoch,E.Period+10.0*86400.0+X+Revs*NextPeriod);
      System.out.println(NextTime);
      CurrentMoonPos=new MathStateTime(SatTable[CurrentMoon].getState(E.Epoch),E.Epoch);
      NextMoonPos=new MathStateTime(SatTable[NextMoon].getState(NextTime),NextTime);
      Leg1=KeplerGaussFG.GaussFG(CurrentMoonPos,NextMoonPos,PolyEle.Jupiter.mu,3);
      E2=new Elements();
      E2.PosVelToEle(Leg1[0],PolyEle.Jupiter.mu,"km");
      VinfIn=MathVector.sub(E.Propagate(E.Epoch).V(),CurrentMoonPos.S.V());
      VinfOut=MathVector.sub(Leg1[0].S.V(),CurrentMoonPos.S.V());
      double Diff=VinfIn.length()-VinfOut.length();
      vangle=MathVector.vangle(VinfIn,VinfOut);
      System.out.println("Revs:    "+Revs);
      System.out.println("Offset:  "+X);
      System.out.println("VinfIn:  "+VinfIn);
      System.out.println("VinfOut: "+VinfOut);
      System.out.println("vangle:  "+Math.toDegrees(vangle));
      System.out.println("Diff:    "+Diff);
      ouf.println(NextTime+","+Diff);
    }
    ouf.close();
    */
  }
}
