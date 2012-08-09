package org.kwansystems.pov;
//Duck full is 3857x7804

import java.sql.*;
import java.io.*;
import org.kwansystems.tools.*;
import org.kwansystems.tools.vector.*;
import org.kwansystems.space.planet.*;

/** Given a look vector and radius, return a list of star unit vectors, brightnesses, and colors
 * usable by a POV script.
 *
 */
public class GetStars {
  static MathVector[] Colors=new MathVector[] {
    new MathVector(155,176,255).div(255),   //O  
    new MathVector(170,191,255).div(255),   //B  
    new MathVector(202,215,255).div(255),   //A  
    new MathVector(248,247,255).div(255),   //F  
    new MathVector(255,244,234).div(255),   //G  
    new MathVector(255,210,161).div(255),   //K  
    new MathVector(255,204,111).div(255),   //M
    new MathVector(255,198,109).div(255),   //(M10)
  };
  static int[] NeededHR={
    7924, //0 Alpha (Deneb)
    7796, //1 Gamma
    7615, //2 Eta
    7417, //3 Beta1
    7949, //4 Epsilon
    8115, //5 Zeta
    7528, //6 Delta
    7420, //7 Iota2
    7328, //8 Kappa
  };
  static int lines[][]=new int[][]{
    {0,1},
    {1,2},
    {2,3},
    {1,4},
    {4,5},
    {1,6},
    {6,7},
    {7,8}
  };
  static double[][] HRCoord=new double[9][2];                      
  static double MaxMag=3; //Stars above this magnitude are signified by stars of larger radius
  static double LimitMag=12; //Stars below this magnitude are black
  static double BrightMax=1;
  static double ColorSat=1;
  static double Gamma=0.4;
  static double StarRad=200;
  static double PointLimitMag=3;
  static double PointSizeMag=2.0;
  static double PointSizeRad=3000;
  static double CelestialSphereRad=1e6;
  static double PointAspect=0.05;
  static double OuterDiscLimitMag=3;
  static double OuterDiscBrightMag=-2;
  static double OuterDiscRad=PointSizeRad;
  static double GetRadFactor(double Mag) {
    double result;

    if(Mag<MaxMag) {
      result=(MaxMag-Mag+1);
    } else {
      result=1;
    }
    return result;
  }
  static MathVector GetColor(int SpIndex, double Mag) {
    //returns a 3vector for color
    double Bright;
    if(Mag>LimitMag) {
      return null;
    } else {
      Bright=Scalar.linterp(MaxMag,BrightMax,LimitMag,0,Mag);
    }
    if(Bright>BrightMax) {
      Bright=BrightMax;
    }
    int Type=SpIndex/10;
    int Subtype=SpIndex%10;
    MathVector Color=MathVector.linterp(0,Colors[Type],10,Colors[Type+1],Subtype);
    Color=MathVector.linterp(0,new MathVector(1,1,1),1,Color,ColorSat).mul(Bright);
    Color.powEq(Gamma);
    return Color;
  }
  
  public static void main(String args[]) throws ClassNotFoundException,SQLException,IOException {
    
    Statement stmt;
    PrintStream ouf=new PrintStream(new FileOutputStream("/usr/codebase/pov/Cygnus/StarsKSC.pov"));
    Class.forName("com.mysql.jdbc.Driver");
    String url = "jdbc:mysql://dejiko:3306/hip";
    Connection con = DriverManager.getConnection(url,"root", "meyow");
    stmt = con.createStatement();
//    double centerRA=Scalar.hmsToRadians(03,47,24);
//    double centerDE=Scalar.dmsToRadians(24,07,00);
//    double RAradius=Scalar.dmsToRadians(01,00,00);
//    double DEradius=Scalar.dmsToRadians(01,00,00);
    double centerRA=Scalar.hmsToRadians(20,30,00);
    double centerDE=Scalar.dmsToRadians(42,00,00);
    double RAradius=Scalar.hmsToRadians(03,30,00);
    double DEradius=Scalar.dmsToRadians(40,00,00);
    ouf.println("#version unofficial Megapov 1.22;");
    ouf.println("global_settings{right_handed}");
    ouf.println("camera {location 0 look_at x rotate -y*"+Math.toDegrees(centerDE)+" rotate z*"+Math.toDegrees(centerRA)+"}");
    ouf.println("#include \"CygOut.inc\"");
    ouf.println("#local StarFin=finish {ambient 1 diffuse 0 specular 0}");
 //   ouf.println("#include \"MilkyWay.inc\"");
    String sql="select * from KSC"+
               " where RArad between "+(centerRA-RAradius)+" and "+(centerRA+RAradius)+
               " and DErad between "+(centerDE-DEradius)+" and "+(centerDE+DEradius)+
                " and Vmag<"+LimitMag;
    ouf.println("//"+sql);
    ResultSet srs = stmt.executeQuery(sql);
    while (srs.next()) {
      double Vmag=srs.getDouble("Vmag");
      for(int i=0;i<NeededHR.length;i++) {
        if(srs.getInt("HR")==NeededHR[i]) {
          HRCoord[i][0]=srs.getDouble("DErad");   
          HRCoord[i][1]=srs.getDouble("RArad");   
          Vmag-=2;
        }
      }
      MathVector Color=GetColor(srs.getInt("SpIndex"),Vmag);
      if(Color!=null) {
        String starName="HR"+srs.getInt("HR")+" ";
        if(srs.getInt("Flamsteed")>0) starName+=srs.getInt("Flamsteed")+" ";
        if(srs.getString("Bayer")!=null) starName+=srs.getString("Bayer")+" ";
        if(srs.getString("Constellation")!=null) starName+=srs.getString("Constellation");
        
        ouf.println("sphere{<"+srs.getDouble("x")*CelestialSphereRad+","+srs.getDouble("y")*CelestialSphereRad+","+srs.getDouble("z")*CelestialSphereRad+">," +
                                      StarRad*GetRadFactor(Vmag)+" no_shadow finish {StarFin} "+
                                      "pigment {color rgb <"+Color+">}} "+
                                   "//"+starName);
        if(Vmag<PointLimitMag) {
          double PointLen=Scalar.linterp(PointLimitMag, StarRad, PointSizeMag, PointSizeRad, Vmag);
          ouf.println("union {");
          ouf.println("cone { x*"+CelestialSphereRad+","+PointLen*PointAspect+", x*"+CelestialSphereRad+"+z*"+PointLen+",0 finish {StarFin} pigment {color rgb <"+Color+">}}");
          ouf.println("cone { x*"+CelestialSphereRad+","+PointLen*PointAspect+", x*"+CelestialSphereRad+"-z*"+PointLen+",0 finish {StarFin} pigment {color rgb <"+Color+">}}");
          ouf.println("cone { x*"+CelestialSphereRad+","+PointLen*PointAspect+", x*"+CelestialSphereRad+"+y*"+PointLen+",0 finish {StarFin} pigment {color rgb <"+Color+">}}");
          ouf.println("cone { x*"+CelestialSphereRad+","+PointLen*PointAspect+", x*"+CelestialSphereRad+"-y*"+PointLen+",0 finish {StarFin} pigment {color rgb <"+Color+">}}");
          ouf.println("rotate -y*"+Math.toDegrees(srs.getDouble("DErad")));
          ouf.println("rotate  z*"+Math.toDegrees(srs.getDouble("RArad")));
          ouf.println("}");
        }
        if(Vmag<OuterDiscLimitMag) {
          double BaseBright=Scalar.linterp(OuterDiscLimitMag,0,0,1,Vmag);
          ouf.println("disc { 0,x,"+OuterDiscRad+" finish {StarFin} pigment {wood color_map{ [0.95 color rgb "+BaseBright+"*<"+Color+">][0.95 color rgb 2*"+BaseBright+"*<"+Color+">]} rotate y*90 scale 2*"+OuterDiscRad+"}");
          ouf.println("translate x*"+CelestialSphereRad);
          ouf.println("rotate -y*"+Math.toDegrees(srs.getDouble("DErad")));
          ouf.println("rotate  z*"+Math.toDegrees(srs.getDouble("RArad")));
          ouf.println("}");
        }
      }
    }
/*
    for(int i=0;i<lines.length;i++) {
      ouf.println("cylinder {");
      double lat0=HRCoord[lines[i][0]][0];
      double lon0=HRCoord[lines[i][0]][1];
      double lat1=HRCoord[lines[i][1]][0];
      double lon1=HRCoord[lines[i][1]][1];
      ouf.println("  <"+Spheroid.llr2xyz(lat0,lon0,CelestialSphereRad)+">,");
      ouf.println("  <"+Spheroid.llr2xyz(lat1,lon1,CelestialSphereRad)+">,"+StarRad*3);
      ouf.println("  pigment {color rgb 1}");
      ouf.println("  finish {StarFin}");
      ouf.println("}");
    }
    */
  }
}