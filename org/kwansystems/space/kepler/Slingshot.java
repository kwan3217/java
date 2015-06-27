package org.kwansystems.space.kepler;

import org.kwansystems.space.*;
import org.kwansystems.space.planet.*;
import org.kwansystems.tools.rootfind.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.vector.*;

import static org.kwansystems.space.Constants.*;
import static org.kwansystems.tools.time.TimeScale.*;
import static org.kwansystems.tools.time.TimeUnits.*;

import java.io.*;

public class Slingshot {
  public static GaussFG G=new GaussFG(SunGM);
  public static double DeltaV(MathStateTime[] State, MathVector InB, MathVector OutB) {
    Course Leg1=G.target(State[0],State[1].S.rAddEq(InB),-1);
    Course Leg2=G.target(State[1].S.rAddEq(OutB),State[2],-1);
    double VinfI=MathVector.sub(State[1].S.V(),Leg1.arrive.S.V()).length();
    double VinfO=MathVector.sub(State[1].S.V(),Leg2.depart.S.V()).length();
    return VinfO-VinfI;
  }
  public static double DeltaV(MathStateTime[] State) {
    return DeltaV(State,new MathVector(), new MathVector());
  }
  public static double DeltaV(Planet[] Mover, Time[] T, MathVector InB, MathVector OutB) {
    MathStateTime[] State=new MathStateTime[Mover.length];
    for(int i=0;i<State.length;i++) State[i]=Mover[i].Orbit.getStateTime(T[i]); 
    return DeltaV(State,InB,OutB);
  }
  public static double DeltaV(Planet[] Mover, Time[] T) {
    return DeltaV(Mover,T,new MathVector(), new MathVector());
  }
  private static Time convergeSlingshot(final Planet[] Mover, Time[] FixedTime, MathVector[] BPoint, final int Which, double WindowSize) {
    if(Mover.length!=FixedTime.length) throw new IllegalArgumentException("Can't do that!");
    if(Mover.length!=3) throw new IllegalArgumentException("Can't do that either!");
    if(Which>=3 | Which<0) throw new IllegalArgumentException("Nor that!");
    final MathVector InB, OutB;
    if(BPoint==null) {
      InB=new MathVector();
      OutB=new MathVector();
    } else {
      InB=BPoint[0];
      OutB=BPoint[1];
    }
    
    final MathStateTime State[]=new MathStateTime[Mover.length];
    for(int i=0;i<Mover.length;i++) {
      State[i]=Mover[i].Orbit.getStateTime(FixedTime[i]);
      System.out.println("State["+i+"] "+State[i].toString());
    }
    
    Time TWlo=Time.add(FixedTime[Which],-WindowSize/2);  //Search the window around the given end encounter
    Time TWhi=Time.add(FixedTime[Which],WindowSize/2);
    
    //Use Bisection root-finding to get zero DeltaV
    
    Time T=new Time(RootFind.Find(
      new Crenshaw(0.01,
        new RootFunction() {
          public double F(double X) {
            Time T=new Time(X,Seconds,UTC);
            State[Which]=Mover[Which].Orbit.getStateTime(T);
            return DeltaV(State,InB,OutB);
          }
        }
      ),
      0,TWlo.get(),TWhi.get()
    ),Seconds,UTC);
    System.out.println("Converged T: "+T);
    return T;
  }
  public static MathStateTime[][] calcSlingshot(final Planet[] Mover, Time[] FixedTime, MathVector[] BPoint, final int Which, double WindowSize) {
    Time T=convergeSlingshot(Mover,FixedTime,BPoint,Which,WindowSize);
    FixedTime[Which]=T;
    MathStateTime[] ST=new MathStateTime[3];
    for(int i=0;i<3;i++) ST[i]=Mover[i].Orbit.getStateTime(FixedTime[i]);
    MathStateTime[][] Course=new MathStateTime[2][];
    Course[0]=KeplerFG.target(ST[0],ST[1],SunGM,-1);
    Course[1]=KeplerFG.target(ST[1],ST[2],SunGM,-1);
    MathVector VV=Generic.resolvePOC(ST[0],Course[0][0].S.V());
    return Course;
  }
  public static MathVector[] slingshotElements(MathStateTime[] ST, MathStateTime[][] Course, double PlanetMu) {
    MathVector[] BPoint=new MathVector[2];
    System.out.println("Sun-centered velocity gain: "+(Course[1][0].S.V().length()-Course[0][1].S.V().length())+"m/s");
    MathVector VinfI=MathVector.sub(ST[1].S.V(),Course[0][1].S.V());
    System.out.println("VinfI (m/s):            "+VinfI.toString(true));
    MathVector VinfO=MathVector.sub(ST[1].S.V(),Course[1][0].S.V());
    System.out.println("VinfO (m/s):            "+VinfO.toString(true));
    double delta=MathVector.vangle(VinfI,VinfO);
    System.out.println("Turning angle:          "+Math.toDegrees(delta)+"deg");
    double ThetaInf=(delta+Math.PI)/2;
    System.out.println("Asymptote True Anomaly: "+Math.toDegrees(ThetaInf)+"deg");
    double a=-PlanetMu/Math.pow(VinfI.length(),2);
    System.out.println("Semimajor Axis:         "+a+"m");
    double e=-1/Math.cos(ThetaInf);
    System.out.println("Eccentricity:           "+e);
    double rp=a*(1-e);
    System.out.println("Periapse Radius:        "+rp+"m");
    double c=a*e;
    double b=Math.sqrt(c*c-a*a);
    System.out.println("Semiminor axis:         "+b+"m");
    MathVector H=MathVector.cross(VinfI,VinfO).normal();
    System.out.println("Orbit Normal Vector:    "+H.toString(true));
    BPoint[0]=MathVector.cross(VinfI,H).normal().mul(b);
    System.out.println("Incoming B point (m):   "+BPoint[0].toString(true));
    BPoint[1]=MathVector.cross(VinfO,H).normal().mul(b);
    System.out.println("Outgoing B point (m):   "+BPoint[1].toString(true));
    System.out.print("Planetocentric periapse state (m,s)");
    return BPoint;
  }
  public static MathVector[] slingshotElements(Planet[] Mover, MathStateTime[][] Course, double PlanetMu) {
    MathStateTime[] ST=new MathStateTime[3];
    ST[0]=Mover[0].Orbit.getStateTime(Course[0][0].T);
    ST[1]=Mover[1].Orbit.getStateTime(Course[1][0].T);
    ST[2]=Mover[2].Orbit.getStateTime(Course[1][1].T);
    return slingshotElements(ST,Course,PlanetMu);
  }
  public static void main(String[] args) throws IOException {
    //Patched conic approximation to Voyager 2 trajectory, Jupiter and Saturn legs
    Time LaunchTime=new Time(2006,1,19,19,00,0,Seconds,UTC);
    Time JupiterTime=new Time(2007,2,28,5,41,0,Seconds,UTC);
    Time PlutoTime=new Time(2015,7,14,11,59,0,Seconds,UTC);
    PlutoTime.Scale=TDB;
    Planet[] Mover=new Planet[]{Planet.Earth,Planet.Jupiter,Planet.Pluto};
    Time[] FixedTime= new Time[]     {LaunchTime,        JupiterTime,         PlutoTime};
    System.out.println("Nominal DeltaV: "+DeltaV(Mover,FixedTime)+"m/s");   
    //Departure and arrival planet states
    int VaryPoint=2;

    MathStateTime[][] Course=calcSlingshot(Mover,FixedTime,null,VaryPoint,100*86400);
    System.out.println("Leg 0 start: "+Course[0][0]);
    System.out.println("Leg 0 end:   "+Course[0][1]);
    System.out.println("Leg 1 start: "+Course[1][0]);
    System.out.println("Leg 1 end:   "+Course[1][1]);
    MathVector[] BPoint=slingshotElements(Mover,Course,Planet.Jupiter.S.GM);
    Course=calcSlingshot(Mover,FixedTime,BPoint,VaryPoint,100*86400);
    System.out.println("Leg 0 start: "+Course[0][0]);
    System.out.println("Leg 0 end:   "+Course[0][1]);
    System.out.println("Leg 1 start: "+Course[1][0]);
    System.out.println("Leg 1 end:   "+Course[1][1]);
    BPoint=slingshotElements(Mover,Course,Planet.Jupiter.S.GM);
    for(int i=0;i<3;i++)System.out.println(FixedTime[i].get(Days,UTC)-2400000.5);
  }
}
