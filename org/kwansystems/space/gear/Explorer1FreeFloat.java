package org.kwansystems.space.gear;

import org.kwansystems.space.gear.mass.*;
import org.kwansystems.space.kepler.*;
import org.kwansystems.space.planet.*;
import org.kwansystems.space.universe.*;
import org.kwansystems.tools.*;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.time.*;
import org.kwansystems.tools.vector.*;

import static java.lang.Math.*;
import static org.kwansystems.space.planet.Planet.*;

public class Explorer1FreeFloat extends TestFlight {
  Time Epoch;
  public Explorer1FreeFloat(SixDOFVehicle LTestVehicle) {
    super(LTestVehicle);
  }
  public Explorer1FreeFloat(SixDOFVehicle LTestVehicle, int x) {
    super(LTestVehicle,x);
  }
  public void RecordAdditionalTestData(double T, MathVector X) {
    MathVector Pos=X.subVector(0,3);
    MathVector Vel=X.subVector(3,3);
    Time TT=Time.add(Epoch, T);
    MathStateTime ST=new MathStateTime(Pos,Vel,TT);
    Elements E=new Elements(ST,Spheroid.WGS84.GM,"m");
    CR.Record(T,"h","m",Pos.length()-Spheroid.WGS84.Re);
    CR.Record(T,"hp","m",E.Periapse-Spheroid.WGS84.Re);
    CR.Record(T,"ha","m",E.Apoapse-Spheroid.WGS84.Re);
    CR.Record(T,"ST",ST);
    CR.Record(T,"E",E);
  }
  public static void main(String[] args) {
    Time Epoch1=new Time(1958,2,1,3,58,0,TimeUnits.Seconds,TimeScale.UTC,TimeEpoch.Java);
    Time Epoch2=new Time(1958,2,1,3,59,0,TimeUnits.Seconds,TimeScale.UTC,TimeEpoch.Java);
    Time Epoch3=new Time(1958,2,1,4,51,0,TimeUnits.Seconds,TimeScale.UTC,TimeEpoch.Java);
    System.out.println(Epoch1);
    System.out.println(Epoch2);
    System.out.println(Epoch3);
    double Theta1=Earth.Rot.Theta(Epoch1);
    double Theta2=Earth.Rot.Theta(Epoch2);
    double Theta3=Earth.Rot.Theta(Epoch3);
    Theta2-=Theta1;
    Theta3-=Theta1;
    Theta1=0;
    MathVector LLA1=new MathVector(Scalar.dmsToRadians( 20, 24, 17),Scalar.dmsToRadians(-61,-16,-48)+Theta1, 408423.9);
    MathVector LLA2=new MathVector(Scalar.dmsToRadians( 18, 29, 10),Scalar.dmsToRadians(-57,-39,-43)+Theta2, 431181.7);
    MathVector LLA3=new MathVector(Scalar.dmsToRadians(-27,-56,-29),Scalar.dmsToRadians( 85, 55, 10)+Theta3,2553709.4);
    MathVector XYZ1=Planet.Earth.lla2xyz(LLA1);
    MathVector XYZ2=Planet.Earth.lla2xyz(LLA2);
    MathVector XYZ3=Planet.Earth.lla2xyz(LLA3);
    MathVector LLA_B1=Planet.Earth.xyz2lla(XYZ1);
    System.out.println(MathVector.mul(LLA_B1,new MathVector(toDegrees(1),toDegrees(1),1)));
    boolean done=false;
    GaussFG g=new GaussFG(Planet.Earth.S.GM);
    Universe U=new TwoBody(Planet.Earth);
    MathVector XYZt=new MathVector(XYZ3);
    Course course=null;
    Explorer1FreeFloat A=null;
    while(!done) {
      course=g.target(new MathStateTime(XYZ1,Epoch1), new MathStateTime(XYZt, Epoch3), -1);
      SixDOFState RVEw0=new SixDOFState(course.depart.S,new Quaternion(),new MathVector());
      InertSixDOFMass M=InertSixDOFMass.MakeCylinderShell("Explorer1", 14, 97/2, 90/2, -0.5, 0.5);
      SixDOFVehicle Explorer1=new InertSixDOFVehicle(U, M);
      A=new Explorer1FreeFloat(Explorer1,1);
      A.Epoch=Epoch1;
      A.DoTest(RVEw0, 0, 60, (int)(Epoch3.get(TimeUnits.Minutes)-Epoch1.get(TimeUnits.Minutes))+1);
      MathStateTime STf=(MathStateTime)(A.CR.Playback((int)(Epoch3.get(TimeUnits.Minutes)-Epoch1.get(TimeUnits.Minutes)), "ST"));
      MathVector TargetDiff=MathVector.sub(XYZ3, STf.S.R());
      System.out.println(TargetDiff.toString());
      XYZt.addEq(TargetDiff);
      done=TargetDiff.length()<0.0001;
    }
    System.out.println(XYZt);
    System.out.println(XYZ3);
    System.out.println(MathVector.sub(XYZ3,XYZt));
    System.out.println(MathVector.sub(XYZ3,XYZt).length());
    System.out.println(course);
    System.out.println(new Elements(course.depart.S,Epoch1,Spheroid.WGS84.GM,"m"));
    A.CR.PrintTable(new HTMLPrinter("Explorer1FreeFloat.html"));
  }
}
