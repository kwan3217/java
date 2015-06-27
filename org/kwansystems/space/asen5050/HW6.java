package org.kwansystems.space.asen5050;

import java.io.*;

import org.kwansystems.space.kepler.*;
import org.kwansystems.tools.time.TimeScale;
import org.kwansystems.tools.time.TimeUnits;
import org.kwansystems.tools.vector.*;

public class HW6 {
  public static void main(String[] args)  throws IOException {
    PrintWriter Ouf=new PrintWriter(new FileWriter("HW6.ps"));
    Ouf.println("%!PS");
    Ouf.println("gsave 126 36 translate 2 2 scale 90 0 translate 90 rotate 0.25 setlinewidth");
    Ouf.println("0 90 moveto 0 -90 lineto 360 -90 lineto 360 90 lineto closepath stroke");
    //Problem 1
    System.out.print("Printing map");
    PrintMap(Ouf);
    System.out.println("Done.");
    Elements E=new Elements();
    E.parseTwoLine(
      "1 25544U 98067A 02274.13393519 .00043050 00000-0 51123-3 0 2056",
      "2 25544 51.6350 241.0263 0017405 320.8649 217.1627 15.60554328220558"
    );
    System.out.print("Printing ground track");
    PrintGroundTrack(Ouf,3*3600,E);
    System.out.println("Done.");
    Ouf.println("showpage grestore");
    //Problem 2
    System.out.println("Printing polar grid...");
    PrintPolarPlotGrid(Ouf);
    MathVector BoulderECEF=Frames.LLAtoECEF(Math.toRadians(40.01),Math.toRadians(254.83),1.615);
    System.out.println("Printing next Boulder pass...");
    PrintPass(Ouf,E,BoulderECEF);
    Ouf.println("showpage");
    Ouf.close();
    System.out.println("Done");
  }
  public static void PrintMap(PrintWriter Ouf)  throws IOException {
    LineNumberReader Inf=new LineNumberReader(new FileReader("Coast.dat"));
    String Ins;	
    boolean first=true;
    int i=0;
    do {
      if((i%1000)==0) System.out.print(".");
      Ins=Inf.readLine();
      double x,y;
      if(Ins!=null) {
        if(Ins.equals("nan nan")) {
          Ouf.println("stroke");
	  first=true;
        } else {
          int SpacePos=Ins.indexOf('\t');
          x=Double.parseDouble(Ins.substring(0,SpacePos+1));
          y=Double.parseDouble(Ins.substring(SpacePos+1));
	  Ouf.println(x+" "+y+(first?" moveto":" lineto"));
          first=false;
        }
      }
      i++;
    } while(Ins!=null);
    Ouf.println("stroke");
  }
  public static void PrintPolarPlotGrid(PrintWriter Ouf) {
    Ouf.println("306 396 translate /maxrad 270 def");
    Ouf.println("/Helvetica findfont 8 scalefont setfont ");
    Ouf.println("maxrad 3 div dup maxrad {");
    Ouf.println("  dup ");
    Ouf.println("  0 exch 0 exch 0 360 arc closepath stroke");
    Ouf.println("  dup 90 exch 3 div sub cvi 20 string cvs exch neg 0 exch moveto show 0 0 moveto");
    Ouf.println("} for");
    Ouf.println("gsave");
    Ouf.println("0 30 360 {pop 30 rotate maxrad neg 0 moveto maxrad 0 lineto stroke} for");
    Ouf.println("grestore");
    Ouf.println("/Helvetica findfont 24 scalefont setfont ");
    Ouf.println("-8 maxrad 10 add moveto (N) show ");
    Ouf.println("-8 maxrad neg 34 sub moveto (S) show");
    Ouf.println("maxrad 10 add -12 moveto (E) show");
    Ouf.println("maxrad neg 26 sub -12 moveto (W) show");
  }
  public static void PrintGroundTrack(PrintWriter Ouf, double TimeFromEpoch, Elements E) {
    System.out.println("Epoch UT: "+E.Epoch.toString(TimeUnits.Days, TimeScale.UTC));
    System.out.println(E.toString());
    double OrigM=E.M;
    boolean first=true;
    double OldLon=0;
    for(int i=0;i<TimeFromEpoch;i++) {
      if((i%100)==0) System.out.print(".");
      E.M=OrigM+E.N*i; //Advance
      E.MeanToTrue();
      MathVector SatECI=E.EleToPosVel().R();
      MathVector SatECEF=Frames.ECItoECEF(SatECI,Frames.Gst(E.Epoch.get()+i/86400.0));
      double Lat=Math.toDegrees(Frames.ECEFtoLat(SatECEF));
      double Lon=Math.toDegrees(Frames.ECEFtoLon(SatECEF));
      if(Lon<0) Lon+=360;
      if(!first & OldLon>Lon+300) {//wrap
        Ouf.println("stroke");
       	first=true;
      }
      Ouf.println(Lon+" "+Lat+(first?" moveto":" lineto"));
      OldLon=Lon;
      first=false;
    }
    Ouf.println("stroke");
    //Return E to its original state
    E.M=OrigM;
    E.MeanToTrue();
  }
  public static void PrintPass(PrintWriter Ouf,Elements E,MathVector ToposECEF) {
    double Elevation,Azimuth;
    double OrigM=E.M;
    MathVector SatSEZ;
    int i=0;
    do {
      E.M=OrigM+E.N*i; //Advance
      E.MeanToTrue();
      MathVector SatECI=E.EleToPosVel().R();
      MathVector SatECEF=Frames.ECItoECEF(SatECI,Frames.Gst(E.Epoch.get()+i/86400.0));
      SatSEZ=Frames.ECEFtoSEZ(SatECEF,ToposECEF);
      if((i%100)==0) System.out.println(i+": "+SatSEZ.Z()+"km above horizon plane");
      i++;
    } while(SatSEZ.Z()<0);
    System.out.println("Satellite rises at "+i+" seconds after Epoch");
    Elevation=Math.toDegrees(Frames.SEZtoEl(SatSEZ));
    Azimuth=Frames.SEZtoAz(SatSEZ);
    Ouf.println((90-Elevation)*3*Math.sin(Azimuth)+" "+(90-Elevation)*3*Math.cos(Azimuth)+" moveto");
    do {
      E.M=OrigM+E.N*i; //Advance
      E.MeanToTrue();
      MathVector SatECI=E.EleToPosVel().R();
      MathVector SatECEF=Frames.ECItoECEF(SatECI,Frames.Gst(E.Epoch.get()+i/86400.0));
      SatSEZ=Frames.ECEFtoSEZ(SatECEF,ToposECEF);
      Elevation=Math.toDegrees(Frames.SEZtoEl(SatSEZ));
      Azimuth=Frames.SEZtoAz(SatSEZ);
      Ouf.println((90-Elevation)*3*Math.sin(Azimuth)+" "+(90-Elevation)*3*Math.cos(Azimuth)+" lineto");
      if((i%100)==0) System.out.println(i+": "+Elevation+"deg above horizon");
      i++;
    } while(Elevation>0);
    System.out.println("Satellite sets at "+i+" seconds after Epoch");
    Ouf.println("stroke");
    //Return E to its original state
    E.M=OrigM;
    E.MeanToTrue();
  }
}
