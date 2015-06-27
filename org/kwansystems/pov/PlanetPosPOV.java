package org.kwansystems.pov;

import org.kwansystems.space.ephemeris.*;
import org.kwansystems.space.ephemeris.earth.*;
import org.kwansystems.space.ephemeris.jupiter.*;
import org.kwansystems.space.ephemeris.mars.*;
import org.kwansystems.space.ephemeris.neptune.*;
import org.kwansystems.space.ephemeris.pluto.*;
import org.kwansystems.space.ephemeris.saturn.*;
import org.kwansystems.space.ephemeris.sun.*;
import org.kwansystems.space.ephemeris.uranus.*;
import org.kwansystems.tools.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeEpoch;
import org.kwansystems.tools.time.TimeScale;
import org.kwansystems.tools.time.TimeUnits;
import org.kwansystems.tools.vector.*;

public class PlanetPosPOV {
  public static void buildSystem(Ephemeris[] SolarSystem, Ephemeris[] Sat, double[] MassRatio, int bary, int center, int firstSat, int satInterval) {
    Ephemeris Center=new CentralBody(SolarSystem[bary],Sat,MassRatio);
    for(int i=1;i<Sat.length;i++) {
      if(Sat[i]!=null)Sat[i].setReference(Center);
      SolarSystem[firstSat+satInterval*(i-1)]=Sat[i];
    }
    SolarSystem[center]=Center;
  }
  
  public static void main(String[] args) {
    Time T1,T2;
    int Steps;
    int[] Obj;
    if(args.length>0) {
      T1=new Time(Double.parseDouble(args[0]),TimeUnits.Days,TimeScale.TDB,TimeEpoch.JD);
      T2=new Time(Double.parseDouble(args[1]),TimeUnits.Days,TimeScale.TDB,TimeEpoch.JD);
      Steps=Integer.parseInt(args[2]);
      Obj=new int[args.length-3];
      for(int i=0;i<Obj.length;i++) Obj[i]=Integer.parseInt(args[3+i]);
    } else {
      T1=new Time(2007,2,27,0,0,0,TimeUnits.Days,TimeScale.TDB,TimeEpoch.JD);
      T2=new Time(2007,2,29,0,0,0,TimeUnits.Days,TimeScale.TDB,TimeEpoch.JD);
      Steps=101;
      Obj=new int[]{99,399,799};
    }
    double DeltaT=(T2.get()-T1.get())/((double)(Steps));
    
    
    Ephemeris SolarSystem[]=new Ephemeris[1000]; 
    System.out.println("//Solar System positions");
    System.out.println("//Start: "+T1);
    System.out.println("//Stop:  "+T2);
    SolarSystem[0]=new FixedEphemeris(new MathVector());
    //Heliocentric Planet system barycenters

    buildSystem(SolarSystem,SunSatSeries96.satArray,SunSatSeries96.satMassRatio,0,99,100,100);
    SolarSystem[199]=SolarSystem[100];
    SolarSystem[299]=SolarSystem[200];
    buildSystem(SolarSystem,EarthSatELP2000.satArray,EarthSatELP2000.satMassRatio,300,399,301,1);
    buildSystem(SolarSystem,MarsSatKepler.satArray,MarsSatKepler.satMassRatio,400,499,401,1);
//    buildSystem(SolarSystem,JupiterSatE5.satArray,JupiterSatE5.satMassRatio,500,599,501,1);
    buildSystem(SolarSystem,SaturnSatSystem.satArray,SaturnSatSystem.satMassRatio,600,699,601,1);
    buildSystem(SolarSystem,UranusSatGUST86.satArray,UranusSatGUST86.satMassRatio,700,799,701,1);
    buildSystem(SolarSystem,NeptuneSatKepler.satArray,NeptuneSatKepler.satMassRatio,800,899,801,1);
    buildSystem(SolarSystem,PlutoSatKepler.satArray,PlutoSatKepler.satMassRatio,900,999,901,1);

    //Write it all out
    System.out.print("#declare ObjIndex=array["+Obj.length+"] {");
    for(int i=0;i<Obj.length;i++)System.out.print(""+Obj[i]+",");
    System.out.println("}");
    System.out.println("#declare PlanetPos=array["+Obj.length+"]["+Steps+"] {");
    for(int i=0;i<Obj.length;i++) {
      System.out.println("{");
      for(int j=0;j<Steps;j++) {
        Time T=Time.add(T1,j*DeltaT);
        System.out.println(SolarSystem[Obj[i]].getState(T).R().toString(true,true,null)+",");
      }
      System.out.println("}");
    }
    System.out.println("}");
    System.out.println(Rotation.PovVectors(T1.get(TimeUnits.Days,TimeScale.TDB,TimeEpoch.JD),Obj));
  }
}
