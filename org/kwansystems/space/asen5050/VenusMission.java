package org.kwansystems.asen5050;

import org.kwansystems.kepler.*;
import org.kwansystems.ephemeris.*;
import org.kwansystems.vector.*;
import java.util.*;
import java.io.*;

public class VenusMission {
  public static Time DepTimeCalc(long DepIndex) {
    Time DepartTime=new Time(Time.TimeUnits.Seconds);
    DepartTime.setTime(Date.UTC(107,5-1,24,5,57,00)+DepIndex*14400000L);
	return DepartTime;
  }
  public static Time ArrTimeCalc(long ArrIndex) {
    Time ArriveTime=new Time(Time.TimeUnits.Seconds);
    ArriveTime.setTime(Date.UTC(107,10-1,17,07,45,00)+ArrIndex*14400000L);
	return ArriveTime;
  }
  public static double[] Vhe(long DepIndex,long ArrIndex) {
    Time DepartTime=DepTimeCalc(DepIndex);
    Time ArriveTime=ArrTimeCalc(ArrIndex);
//    System.out.println("Arrive time: "+ArriveTime);
    //Departure and arrival planet states
    MathStateTime Depart=new MathStateTime(PolyEle.Earth.Propagate(DepartTime),DepartTime);
//    System.out.println("Earth state at departure: \n"+Depart.toString());
    MathStateTime Arrive=new MathStateTime(PolyEle.Venus.Propagate(ArriveTime),ArriveTime);
//    System.out.println("Venus state at arrival:    \n"+Arrive.toString());
    //Gauss targeter
    double DeltaT=ArriveTime.get()-DepartTime.get();
    MathState[] HeliocentricState=KeplerGaussFG.GaussFG(Depart.S.R(),Arrive.S.R(),DeltaT,EphConst.SunGM,-1);
//    System.out.println("Departure heliocentric state: \n"+HeliocentricState[0]);
//    System.out.println("Arrival heliocentric state: \n"+HeliocentricState[1]);
    MathVector DepVinf=MathVector.sub(HeliocentricState[0].V(),Depart.S.V());
//    System.out.println("Departure asymtote: \n"+DepVinf);
    MathVector ArrVinf=MathVector.sub(HeliocentricState[1].V(),Arrive.S.V());
//    System.out.println("Arrival asymtote: \n"+ArrVinf);
    double result[]=new double [] {DepVinf.length(),ArrVinf.length()};
    return result;
  }
  public static double[] DeltaV(long DepIndex, long ArrIndex) {
	double vhe[]=Vhe(DepIndex,ArrIndex);
	double Hep=185;
	double Hvp=300;
	double result[]=new double[4];
	//Calculate perigee velocity increase needed to depart
	result[0]=Math.sqrt(2*PolyEle.Earth.mu/(PolyEle.Earth.R+Hep)+vhe[0]*vhe[0])-Math.sqrt(2*PolyEle.Earth.mu/(PolyEle.Earth.R+Hep));
	//calculate minimum capture velocity needed
	result[1]=Math.sqrt(2*PolyEle.Venus.mu/(PolyEle.Venus.R+Hvp)+vhe[1]*vhe[1])-Math.sqrt(2*PolyEle.Venus.mu/(PolyEle.Venus.R+Hvp));
	result[2]=vhe[0];
	result[3]=vhe[1];
	return result;
  }
  public static void main(String[] args) throws IOException {
	KeplerGaussFG.verbose=false;
	PrintWriter Vhe1=new PrintWriter(new FileWriter("..\\vhe1.dat"));
	PrintWriter Vhe2=new PrintWriter(new FileWriter("..\\vhe2.dat"));
	PrintWriter DeltaV1=new PrintWriter(new FileWriter("..\\deltav1.dat"));
	PrintWriter DeltaV2=new PrintWriter(new FileWriter("..\\deltav2.dat"));
	PrintWriter DepTime=new PrintWriter(new FileWriter("..\\deptime.dat"));
	PrintWriter ArrTime=new PrintWriter(new FileWriter("..\\arrtime.dat"));
	double MinDV1=0,MinDV2=0,MinDV=Double.POSITIVE_INFINITY;
long MinDepIndex=0,MinArrIndex=0;
	Time MinDep=null,MinArr=null;
	for(long DepIndex=-160;DepIndex<=160;DepIndex++) {
	  for(long ArrIndex=-160;ArrIndex<=160;ArrIndex++) {
        double V[]=DeltaV(DepIndex,ArrIndex);
        DeltaV1.print(V[0]+" ");
        DeltaV2.print(V[1]+" ");
        Vhe1.print(V[2]+" ");
        Vhe2.print(V[3]+" ");
        if(V[0]+V[1]<MinDV) {
		  MinDV1=V[0];
		  MinDV2=V[1];
		  MinDV=V[0]+V[1];
MinDepIndex=DepIndex;
MinArrIndex=ArrIndex;
          MinDep=DepTimeCalc(DepIndex);
          MinArr=ArrTimeCalc(ArrIndex);
	    }
        DepTime.print(DepIndex+" ");
        ArrTime.print(ArrIndex+" ");
	  }
	  DeltaV1.print("\n");
	  DeltaV2.print("\n");
	  Vhe1.print("\n");
	  Vhe2.print("\n");
	  DepTime.print("\n");
	  ArrTime.print("\n");
    }
    DeltaV1.close();
    DeltaV2.close();
    DepTime.close();
    ArrTime.close();
    Vhe1.close();
    Vhe2.close();
    System.out.println("Optimum departure index:"+MinDepIndex);
    System.out.println("Optimum Arrival index:  "+MinArrIndex);
    System.out.println("Optimum departure time: "+MinDep);
    System.out.println("Optimum Arrival time:   "+MinArr);
    System.out.println("Departure DV(over esc): "+MinDV1);
    System.out.println("Arrival MinDV:          "+MinDV2);
    MathStateTime Depart=new MathStateTime(PolyEle.Earth.Propagate(MinDep),MinDep);
    MathStateTime Arrive=new MathStateTime(PolyEle.Venus.Propagate(MinArr),MinArr);
    System.out.println("Earth StateTime at dep: "+Depart);
    System.out.println("Venus StateTime at arr: "+Arrive);
  }
  public static MathVector ToLeftHand(MathVector V) {
    return new MathVector(V.X(),V.Z(),V.Y());
  }
}
