package org.kwansystems.space.ephemeris;

import java.io.*;
import java.util.*;

import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeEpoch;
import org.kwansystems.tools.vector.*;

public class PlanetRotation {
  protected static RotationTerm[][][] VarSeries=new RotationTerm[3][1000][];
  protected static RotationTerm[][][] PerSeries=new RotationTerm[10][100][];
  public double RA(double T, int bodyNum) {
    return RotationTerm.Evaluate(T-RotEpoch,PerSeries[bodyNum/100],VarSeries[0][bodyNum]);
  }
  public double De(double T, int bodyNum) {
    return RotationTerm.Evaluate(T-RotEpoch,PerSeries[bodyNum/100],VarSeries[1][bodyNum]);
  }
  public double W(double T, int bodyNum) {
    return RotationTerm.Evaluate(T-RotEpoch,PerSeries[bodyNum/100],VarSeries[2][bodyNum]);
  }
  private static double RotEpoch=TimeEpoch.J2000.JDEpoch();
  public MathVector PoleVector(Time T) {
    return null;
  }
  public Rotator R(Time T) {
    Rotator Q=new Quaternion();
    return Q;
  }
  protected static String[] PlName=new String[] {
    "Sun",
    "Mercury",
    "Venus",
    "Earth",
    "Mars",
    "Jupiter",
    "Saturn",
    "Uranus",
    "Neptune",
    "Pluto"
  };
  protected static char[] PlChar=new char[] {
    '\0', //Sun has no periodic terms
    'H',  //Mercury has no periodic terms, but IAU assigned H(ermes) to it
    'V',  //Venus has no periodic terms, but nothing else uses V
    'E',
    'M',
    'J',
    'S',
    'U',
    'N',
    'P'
  };
  private static void ReadPlanetTable(LineNumberReader Inf) throws IOException {
    String S=Inf.readLine();
    String currentPlanetName="";
    int currentPlanetNum=0;
    String currentMoonName;
    int currentMoonNum=0;
    boolean isPeriodic=false;
    int currentMainVar=0;
    int currentPeriodicNum=0;
    List<RotationTerm> thisSeries=new ArrayList<RotationTerm>();
    while (S!=null) {
      System.out.println(" ~~ "+S);
      if(S.matches("((a0)|(d0)|W)=\\s*")) {
        PutAwaySeries(thisSeries,currentMainVar,currentPlanetNum,currentMoonNum,currentPeriodicNum,isPeriodic);
        isPeriodic=false;
    	//It's a main var label 
        switch (S.charAt(0)) {
          case 'a':
            currentMainVar=0;
            System.out.println("RA series start");
            break;
          case 'd':
            currentMainVar=1;
            System.out.println("De series start");
            break;
          case 'W':
        	  currentMainVar=2;
            System.out.println("W series start");
            break;
        }
      } else if (S.matches("[-+][0-9].*")) {
        //It's a number term
    	  RotationTerm thisTerm=new RotationTerm(S);
        thisSeries.add(thisTerm);
    	  System.out.println(thisTerm.toString());
      } else if (S.matches("[A-Z][0-9]+=\\s*")) {
        PutAwaySeries(thisSeries,currentMainVar,currentPlanetNum,currentMoonNum,currentPeriodicNum,isPeriodic);
        //It's a periodic series name
        isPeriodic=true;
        String[] parts=S.split("[A-Z]|=");
        currentPeriodicNum=Integer.parseInt(parts[1]);
        System.out.println("Periodic term "+currentPeriodicNum+" for "+currentPlanetNum+" "+currentPlanetName);
      } else if (S.matches("[0-9]+\\s[A-Za-z]+.*")){
        PutAwaySeries(thisSeries,currentMainVar,currentPlanetNum,currentMoonNum,currentPeriodicNum,isPeriodic);
    	  //It's a moon name
        String[] parts=S.split(" ");
        currentMoonName=parts[1];
        currentMoonNum=Integer.parseInt(parts[0]);
        System.out.println("Moon #"+currentMoonNum+": "+currentMoonName);
      } else {
        PutAwaySeries(thisSeries,currentMainVar,currentPlanetNum,currentMoonNum,currentPeriodicNum,isPeriodic);
        //It's a planet name
        currentPlanetName=S;
        currentMoonName=S;
        currentMoonNum=99;
        currentPlanetNum=-1;
        for(int i=0;i<PlName.length;i++) {
          if(S.startsWith(PlName[i])) currentPlanetNum=i;
        }
        if(currentPlanetNum<0) {
          System.out.println("Unrecognized line "+Inf.getLineNumber()+": "+S);
          throw new IllegalArgumentException("Unrecognized line: "+S);
        }
        System.out.println("Planet #"+currentPlanetNum+": "+currentPlanetName);
      }
      S=Inf.readLine();
    }
    PutAwaySeries(thisSeries,currentMainVar,currentPlanetNum,currentMoonNum,currentPeriodicNum,isPeriodic);
  }
  private static void PutAwaySeries(List<RotationTerm> thisSeries, int currentMainVar, int currentPlanetNum, int currentMoonNum, int currentPeriodicNum, boolean isPeriodic) {
    if(thisSeries.size()==0) {
      System.out.println("Nothing to put away");
      return;
    }
    RotationTerm[] Stuff=thisSeries.toArray(new RotationTerm[] {});
    if(isPeriodic) {
      PerSeries[currentPlanetNum][currentPeriodicNum]=Stuff;
      System.out.println("Put away periodic series "+currentPeriodicNum+" for planet #"+currentPlanetNum);
    } else {
      VarSeries[currentMainVar][currentPlanetNum*100+currentMoonNum]=Stuff;
      String seriesName="";
      if(currentMainVar==0) seriesName="RA"; 
      if(currentMainVar==1) seriesName="De"; 
      if(currentMainVar==2) seriesName="W"; 
      System.out.println("Put away "+seriesName+" series for body #"+(currentPlanetNum*100+currentMoonNum));
    }
    thisSeries.clear();
  }
  public static void main(String[] args) throws IOException {
    LineNumberReader Inf=new LineNumberReader(new FileReader("Data/PlanetRotation.txt"));
    PlanetRotation.ReadPlanetTable(Inf);
    Inf=new LineNumberReader(new FileReader("Data/MoonRotation.txt"));
    PlanetRotation.ReadPlanetTable(Inf);
  }
}
