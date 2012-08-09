package org.kwansystems.space.asen5050;

import org.kwansystems.space.Constants;
import org.kwansystems.space.kepler.*;
import org.kwansystems.space.planet.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeScale;
import org.kwansystems.tools.time.TimeUnits;
import org.kwansystems.tools.vector.*;
import java.util.*;
import java.io.*;

public class HW10Exact {
  public static void main(String[] args) throws IOException {
    //Patched conic approximation to Mars Odyssey trajectory
    //Launch time from Mars Odyssey website, April 7 first window
    //Departure time from Horizons elements, predicted perigee from state ~1hour after injection
    //Arrival time from Mars Odyssey website
    Time LaunchTime=new Time(2001,4,7,15,02,22,TimeUnits.Seconds,TimeScale.UTC);
    Time Perigee1=new Time(52007.145294165,TimeUnits.Days,TimeScale.TDB);
    Perigee1.Scale=TimeScale.UTC;
    Time DepartTime=new Time(Perigee1.Sec(),TimeUnits.Seconds);
    System.out.println("Depart time: "+DepartTime);
    Time ArriveTime=new Time(TimeUnits.Seconds);
    ArriveTime.setTime(Date.UTC(101,9,24,02,39,00));
    ArriveTime.Scale=TimeScale.TDB;
    //Departure and arrival planet states
    MathStateTime Depart=new MathStateTime(PolyEle.Earth.Propagate(DepartTime),DepartTime);
    System.out.println("Earth state at departure: \n"+Depart.toString());
    MathStateTime Arrive=new MathStateTime(PolyEle.SimpleMars.Propagate(ArriveTime),ArriveTime);
    System.out.println("Mars state at arrival:    \n"+Arrive.toString());
    //Gauss targeter
    double DeltaT=ArriveTime.get()-DepartTime.get();
    MathState[] HeliocentricState=KeplerFG.target(Depart.S.R(),Arrive.S.R(),DeltaT,Constants.SunGM,1);
    System.out.println("Departure heliocentric state: \n"+HeliocentricState[0]);
    System.out.println("Arrival heliocentric state: \n"+HeliocentricState[1]);
    MathVector DepVinf=MathVector.sub(HeliocentricState[0].V(),Depart.S.V());
    System.out.println("Departure asymtote: \n"+DepVinf);
    MathVector ArrVinf=MathVector.sub(HeliocentricState[1].V(),Arrive.S.V());
    System.out.println("Arrival asymtote: \n"+ArrVinf);
    //Rotate DepVinf to ECI equatorial coords
    double epsilon=Math.toRadians(23.45);
    MathMatrix EquToEll=MathMatrix.Rot1(epsilon);
    MathVector Cape=EquToEll.transform(
                      Frames.ECEFtoECI(
                	Frames.LLAtoECEF(Math.toRadians(28.6271),   //Pad 39B latitude
                                	 Math.toRadians(-80.62095), //Pad 39B longitude
                                          0.0186//pad surface above geoid
                                	 +0.0146//platform surface above pad
		                	 -0.02984//geoid height 
					 ),
			Frames.Gst(LaunchTime)
		      )
		    );
    //Injection location
    double Rp=6563.768; //Depart from circular orbit at given departure conic perigee
    double Vcirc=Math.sqrt(Planet.Earth.GM/Rp);
    double Vesc=Math.sqrt(2*Planet.Earth.GM/Rp);
    double DepVinfm=DepVinf.length();
    double Vp=Math.sqrt(DepVinfm*DepVinfm+Vesc*Vesc);
    double DepE=1+Rp*DepVinfm*DepVinfm/Planet.Earth.GM;
    System.out.println("DepE: "+DepE);
    double ThetaInf=Math.acos(-1/DepE);
    System.out.println("ThetaInf: "+Math.toDegrees(ThetaInf));
    MathVector DepH=MathVector.cross(DepVinf,Cape);
    DepH.normalize();
    if(DepH.Z()<0) DepH.negate();
    System.out.println("DepH: "+DepH);
    AxisAngle A=new AxisAngle(DepH,-ThetaInf);
    MathVector DepInjPos=A.transform(DepVinf).normal();
    DepInjPos=DepInjPos.mul(Rp);
    //Pre and Post injection state
    MathVector DepNormalTravel=MathVector.cross(DepH,DepInjPos);
    DepNormalTravel.normalize();
    MathVector DepInjPreVel=MathVector.mul(DepNormalTravel,Vcirc);
    MathVector DepInjPostVel=MathVector.mul(DepNormalTravel,Vp);
    MathStateTime DepInjPre=new MathStateTime(new MathState(DepInjPos,DepInjPreVel),DepartTime);
    MathStateTime DepInjPost=new MathStateTime(new MathState(DepInjPos,DepInjPostVel),DepartTime);
    //Write results to .inc file
    PrintWriter ouf=new PrintWriter(new FileWriter("HW10Exact.inc"));
    ouf.println("//HW10Exact generated results file");
    ouf.println("//State of Earth at departure");
    ouf.println("#declare EarthR=ToLeftHand("+Depart.S.R()+");");
    ouf.println("#declare EarthV=ToLeftHand("+Depart.S.V()+");");
    ouf.println("//State of Mars at Arrival");
    ouf.println("#declare MarsR=ToLeftHand("+Depart.S.R()+");");
    ouf.println("#declare MarsV=ToLeftHand("+Depart.S.V()+");");
    ouf.println("//State of spacecraft at departure");
    ouf.println("#declare DepR=ToLeftHand("+HeliocentricState[0].R()+");");
    ouf.println("#declare DepV=ToLeftHand("+HeliocentricState[0].V()+");");
    ouf.println("//State of spacecraft at Arrival");
    ouf.println("#declare ArrR=ToLeftHand("+HeliocentricState[1].R()+");");
    ouf.println("#declare ArrV=ToLeftHand("+HeliocentricState[1].V()+");");
    ouf.println("//Earth rotation parameters at departure");
    ouf.println("#declare Re="+Planet.Earth.R+"; //km");
    ouf.println("#declare EarthEpsilon="+Math.toDegrees(epsilon)+"; //degrees");
    ouf.println("#declare EarthThetaGLaunch="+Math.toDegrees(Frames.Gst(LaunchTime))+"; //degrees");
    ouf.println("#declare EarthThetaGDepart="+Math.toDegrees(Frames.Gst(DepartTime))+"; //degrees");
    ouf.println("#declare Cape=ToLeftHand("+Cape+"); //Location of Pad 39B at launch time");
    ouf.println("//Departure parameters");
    ouf.println("#declare Rp="+Rp+"; //km");
    ouf.println("#declare DepE="+DepE+";");
    ouf.println("#declare DepH=ToLeftHand("+DepH+");");
    ouf.println("#declare ThetaInfDep="+Math.toDegrees(ThetaInf)+"; //degrees");
    ouf.println("#declare DepInjPos=ToLeftHand("+DepInjPos+"); //km, ECI Elliptic");
    ouf.println("#declare DepInjPreVel=ToLeftHand("+DepInjPreVel+"); //km/s, ECI Elliptic");
    ouf.println("#declare DepInjPostVel=ToLeftHand("+DepInjPostVel+"); //km/s, ECI Elliptic");
    ouf.println("#declare Pos=array[500]");
    ouf.println("#declare Vel=array[500]");
    ouf.println("#declare ThetaG=array[500]");
    for(int i=0;i<500;i++) {
      System.out.print(".");
      Time ThisTime=Time.add(DepartTime,i*30-600);
      MathStateTime ThisStateTime;
      if(i<20) {
        ThisStateTime=DepInjPre;
      } else {
        ThisStateTime=DepInjPost;
      }
      MathState ThisState=KeplerFG.propagate(ThisStateTime,ThisTime,Planet.Earth.GM);
      ouf.println("#declare Pos["+i+"]=ToLeftHand("+ThisState.R()+");");
      ouf.println("#declare Vel["+i+"]=ToLeftHand("+ThisState.V()+");");
      ouf.println("#declare ThetaG["+i+"]="+Math.toDegrees(Frames.Gst(ThisTime))+";");
    }
    ouf.close();
  }
  public static MathVector ToLeftHand(MathVector V) {
    return new MathVector(V.X(),V.Z(),V.Y());
  }
}
