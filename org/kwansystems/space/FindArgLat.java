package org.kwansystems.space;

import java.io.*;
import java.util.*;
import static java.lang.Math.*;

import org.kwansystems.space.kepler.*;
import org.kwansystems.space.planet.*;
import org.kwansystems.tools.*;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeEpoch;
import org.kwansystems.tools.time.TimeScale;
import org.kwansystems.tools.time.TimeUnits;
import org.kwansystems.tools.vector.*;

public class FindArgLat {
  public static void main(String args[]) throws IOException {
    findArgLat("AIM117_1200");
  }
  public static void findArgLat(String fn) throws IOException {
    CsvReader C=new CsvReader("c:\\documents and settings\\jeppesen\\"+fn+".PosVel.txt");
    C.readHeaders();
    ChartRecorder CR=new ArrayListChartRecorder();
    String[] H=C.getHeaders();
    for(String i:H) {
      System.out.println(i);
    }
    ArrayList<MathStateTime> S=new ArrayList<MathStateTime>();
    ArrayList<Elements> E=new ArrayList<Elements>();
    ArrayList<MathVector> LLA=new ArrayList<MathVector>();
    double Tfirst=-1;
    while(C.readRecord()) {
      String V[]=C.getValues();
      Time T=new Time(V[0],TimeUnits.Seconds,TimeScale.UTC,TimeEpoch.Java);
      if(Tfirst<0) Tfirst=T.get();
      MathVector Pos=new MathVector(Double.parseDouble(V[1]),Double.parseDouble(V[2]),Double.parseDouble(V[3]));
      MathVector Vel=new MathVector(Double.parseDouble(V[4]),Double.parseDouble(V[5]),Double.parseDouble(V[6]));
      MathStateTime SS=new MathStateTime(Pos,Vel,T);
      MathVector LLAA=Spheroid.WGS84km.xyz2lla(Pos);
      Elements EE=new Elements(SS,Spheroid.WGS84.GM/1e9,"km");
      System.out.println(SS);
      System.out.println("Geodetic Lat: "+toDegrees(LLAA.X())+", Geocentric RA: "+toDegrees(LLAA.Y())+", Alt: "+LLAA.Z());
      System.out.println(EE);
      E.add(EE);    
      S.add(SS);
      LLA.add(LLAA);
      CR.Record(T.get()-Tfirst, "x", "km", Pos.X());
      CR.Record(T.get()-Tfirst, "y", "km", Pos.Y());
      CR.Record(T.get()-Tfirst, "z", "km", Pos.Z());
      CR.Record(T.get()-Tfirst, "vx", "km/s", Vel.X());
      CR.Record(T.get()-Tfirst, "vy", "km/s", Vel.Y());
      CR.Record(T.get()-Tfirst, "vz", "km/s", Vel.Z());
      CR.Record(T.get()-Tfirst, "Geodetic latitude", "deg", toDegrees(LLAA.X()));
      CR.Record(T.get()-Tfirst, "Geocentric right ascension", "deg", toDegrees(LLAA.Y()));
      CR.Record(T.get()-Tfirst, "Altitude", "km", LLAA.Z());
      CR.Record(T.get()-Tfirst, "Semimajor axis", "km", EE.A);
      CR.Record(T.get()-Tfirst, "Eccentricity", null, EE.E);
      CR.Record(T.get()-Tfirst, "Inclination", "deg", toDegrees(EE.I));
      CR.Record(T.get()-Tfirst, "RAAN", "deg", toDegrees(EE.LAN));
      CR.Record(T.get()-Tfirst, "Argument of Perigee", "deg", toDegrees(EE.AP));
      CR.Record(T.get()-Tfirst, "True Anomaly", "deg", toDegrees(EE.TA));
      CR.Record(T.get()-Tfirst, "Mean Anomaly", "deg", toDegrees(EE.M));
      CR.Record(T.get()-Tfirst, "Argument of Latitude", "deg", toDegrees(EE.AP+EE.TA) % 360.0);
      CR.Record(T.get()-Tfirst, "Period", "s", EE.Period);
    }
    CR.PrintSubTable(new String[] {"Argument of Perigee","True Anomaly","Argument of Latitude"}, new DisplayPrinter());
    CR.PrintTable(new CSVPrinter(fn+".csv"));
  }
}
