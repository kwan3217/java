package org.kwansystems.pov;

import java.sql.*;
import java.io.*;
import org.kwansystems.tools.*;
import org.kwansystems.tools.table.*;
import org.kwansystems.tools.vector.*;
import org.kwansystems.space.planet.*;

/** Given a look vector and radius, return a list of star unit vectors, brightnesses, and colors
 * usable by a POV script.
 *
 */
public class GetMilkyWay {
  static Table Colors=new LinearTable(new double[] {0,64,128},
    new double[][] {{0,1,1},{0,1,0.5},{0,1,0}}
  );
  static double MaxMag=3; //Stars above this magnitude are signified by stars of larger radius
  static double LimitMag=12; //Stars below this magnitude are black
  static double BrightMax=1;
  static double ColorSat=1;
  static double Gamma=0.4;
  static double CelestialSphereRad=1.1e6;
  static double PointAspect=0.05;
  public static void main(String args[]) throws ClassNotFoundException,SQLException,IOException {
    Statement stmt;
    PrintStream ouf=new PrintStream(new FileOutputStream("/usr/codebase/pov/Cygnus/MilkyWay.inc"));
    Class.forName("com.mysql.jdbc.Driver");
    String url = "jdbc:mysql://dejiko:3306/hip";
    Connection con = DriverManager.getConnection(url,"root", "meyow");
    stmt = con.createStatement();
    double centerRA=Scalar.hmsToRadians(20,30,00);
    double centerDE=Scalar.dmsToRadians(42,00,00);
    double RAradius=Scalar.hmsToRadians( 3,30,00);
    double DEradius=Scalar.dmsToRadians(40,00,00);
    String sql="select * from milkyway"+
               " where RArad between "+(centerRA-RAradius)+" and "+(centerRA+RAradius)+
               " and DErad between "+(centerDE-DEradius)+" and "+(centerDE+DEradius)+
               " order by Brightness desc";
    ouf.println("//"+sql);
    ResultSet srs = stmt.executeQuery(sql);
    while (srs.next()) {
      int Brightness=srs.getInt("Brightness");
      double OuterDiscRad=Math.toRadians(0.212)*CelestialSphereRad*2;
      ouf.println("disc { 0,x,"+OuterDiscRad+" finish {StarFin} pigment {wood color_map {[0.5 color rgbft <"+Colors.Interp(Brightness, 0)*0.5+","+Colors.Interp(Brightness, 1)*0.5+","+Colors.Interp(Brightness, 2)*0.5+",0,0>][1 color rgbft <0,0,0,0,1>]} rotate -y*90 scale 4*"+OuterDiscRad+"}");
      ouf.println("translate x*"+CelestialSphereRad);
      CelestialSphereRad*=1.00005;
      ouf.println("rotate -y*"+Math.toDegrees(srs.getDouble("DErad")));
      ouf.println("rotate  z*"+Math.toDegrees(srs.getDouble("RArad")));
      ouf.println("}");
    }
  }
}