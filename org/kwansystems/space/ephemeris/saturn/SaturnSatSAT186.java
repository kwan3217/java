package org.kwansystems.space.ephemeris.saturn;

import org.kwansystems.space.ephemeris.Ephemeris;
import org.kwansystems.tools.time.*;
import org.kwansystems.tools.vector.*;

import org.kwansystems.space.kepler.*;
import static org.kwansystems.tools.time.Time.*;
import static java.lang.Math.*;
import java.io.*;

public class SaturnSatSAT186 extends Ephemeris {
  KeplerFG K=new KeplerFG();
  public static final Ephemeris[] satArray;
  public static final double[] satMassRatio;
  private double[][] Elements;
  private double Start;
  private double End;
  private static final int JDCT=0;
  private static final int RX=1;
  private static final int RY=2;
  private static final int RZ=3;
  private static final int VX=4;
  private static final int VY=5;
  private static final int VZ=6;
  private static final double SaturnMu=3.7931295257834919E+07;
  private static final String RootPath="Data/";
  private void loadElementsText(String MoonName) {
    try {
      String infn=RootPath+MoonName+".horizons";
      LineNumberReader inf=new LineNumberReader(new FileReader(infn));
      String S="";
      int StartLine=0,EndLine=0;
      while(!S.startsWith("$$SOE")) { 

        StartLine++;
        S=inf.readLine();
      }
      while(!S.startsWith("$$EOE")) { 

        EndLine++;
        S=inf.readLine();
      }
      Elements=new double[7][EndLine-StartLine+1];
      inf.close();
      inf=new LineNumberReader(new FileReader(infn));
      for(int i=0;i<StartLine;i++)inf.readLine();
      for(int j=0;j<Elements[0].length;j++) {
        S=inf.readLine();
        String[] shatter=S.split("\\p{Space}*,\\p{Space}*");
        Elements[0][j]=Double.parseDouble(shatter[0]);
        for(int k=1;k<Elements.length;k++) {
          Elements[0][j]=Double.parseDouble(shatter[0]);
          Elements[k][j]=Double.parseDouble(shatter[k+1]);
        }
      }
      inf.close();
      Start=Elements[0][0];
      End=Elements[0][Elements[0].length-1];
    } catch (IOException e) {}
  }
  private void saveElementsSerial(String MoonName) throws IOException {
    ObjectOutputStream ouf = new ObjectOutputStream(new FileOutputStream(RootPath+MoonName+".bin"));
    ouf.writeObject(Elements);
    ouf.close();
  }
  private void loadElementsSerial(String MoonName) {
    try {
      ObjectInputStream inf = new ObjectInputStream(new FileInputStream(RootPath+MoonName+".bin"));
      Elements=(double[][])inf.readObject();
      inf.close();
    } catch (Throwable e) {e.printStackTrace();}
    Start=Elements[0][0];
    End=Elements[0][Elements[0].length-1];
  }
  public MathState CalcState(Time TT) {
    double T=TT.get(TimeUnits.Days,TimeScale.TDB);
    int a=((int)(T-Start)*24/6);
//    double Ta=Elements[JDCT][a];
    int b=a+1;
//    double Tb=Elements[JDCT][b];
    MathState PosA=K.propagate(
      new MathState(Elements[RX][a],Elements[RY][a],Elements[RZ][a],
                    Elements[VX][a],Elements[VY][a],Elements[VZ][a]),
                    (T-Elements[JDCT][a])*86400,SaturnMu
    );
/*
    MathState PosB=KeplerFG(
      new MathState(Elements[RX][b],Elements[RY][b],Elements[RZ][b],
                    Elements[VX][b],Elements[VY][b],Elements[VZ][b]),
                    (T-Elements[JDCT][b])*86400,SaturnMu
    );
*/
		return PosA;
//    return new MathState(MathVector.linterp(Ta,PosA,Tb,PosB,T));
  }
  public SaturnSatSAT186(String SatName) {
    try {
//      loadElementsText(SatName);
//      saveElementsSerial(SatName);
      loadElementsSerial(SatName);
    } catch (Throwable E) {throw new IllegalArgumentException("Problem loading "+SatName,E);}
  }
  static {
    satArray=new Ephemeris[18];
    satArray[10]=new SaturnSatSAT186("Janus"); 
    satArray[11]=new SaturnSatSAT186("Epimetheus");
    satArray[16]=new SaturnSatSAT186("Prometheus");
    satArray[17]=new SaturnSatSAT186("Pandora");
    satMassRatio=new double[18]; 
    satMassRatio[0]=1.0;
  }
  public static void main(String[] args) throws IOException {
    SaturnSatSAT186 A=new SaturnSatSAT186("Janus");
    for(int i=0;i<1440;i++) {
      Time T=new Time(2453292.5+(((double)(i))/1440.0),TimeUnits.Days,TimeScale.TDB,TimeEpoch.JD);
      T.Units=TimeUnits.Days;
      T.Scale=TimeScale.TDB;
    }
  }
  public MathVector CalcPos(Time TT) {
	return defaultCalcPos(TT);
  }
  public MathVector CalcVel(Time TT) {
	return defaultCalcVel(TT);
  }
}
