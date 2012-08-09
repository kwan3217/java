package org.kwansystems.space.gear;

import static java.lang.Math.*;

import java.io.*;

import org.kwansystems.space.gear.mass.*;
import org.kwansystems.space.kepler.*;
import org.kwansystems.space.planet.*;
import org.kwansystems.space.universe.*;
import org.kwansystems.tools.*;
import org.kwansystems.tools.integrator.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeEpoch;
import org.kwansystems.tools.time.TimeScale;
import org.kwansystems.tools.time.TimeUnits;
import org.kwansystems.tools.vector.*;

public abstract class ThreeDOFVehicle extends ThreeDOF {
  Universe U;
  public ThreeDOFVehicle(Universe LU) {
	  U=LU;
  }
  public MathVector getForce(double T, MathState RV, boolean IsMajor) {
	  return U.EnvironmentAcc(T,RV).mul(EquivalentMass(T,RV).mass);
  }
  public static void main(String args[]) throws IOException {
	  PrintWriter P=new PrintWriter(new FileWriter("J2.csv"));
	  ThreeDOFVehicle V=new ThreeDOFVehicle(new TwoBody(Planet.Earth)) {
      public InertThreeDOFMass EquivalentMass(double T, MathState RV) {
		    return new InertThreeDOFMass(null,190);
      }
	  };
    Elements J=new Elements();
    J.Epoch=new Time(2007,6,21,0,0,0,TimeUnits.Seconds,TimeScale.UTC,TimeEpoch.Java);
    J.Periapse=510000+Planet.Earth.S.Re;
    J.Apoapse=615000+Planet.Earth.S.Re;
    J.I=toRadians(97.772);
    J.AP=toRadians(240);
    J.TA=toRadians(-240);
    J.LAN=toRadians(0);
    J.GM=Planet.Earth.S.GM;
    J.LengthUnit="m"; 
    J.FillInElements();
    System.out.println(J);
    MathState StartState=J.EleToPosVel();
    System.out.println(StartState);
	  Integrator I=new RungeKutta(0, StartState, 1, V);
    MathState RV=(MathState)I.getX();
    MathVector lla=Planet.Earth.S.xyz2lla(RV.R());
    MathVector xyzCloudOld=Planet.Earth.S.lla2xyz(lla.X(),lla.Y(),83000);
    MathVector xyzCloud;
    P.println("T,X,Y,Z,VX,VY,VZ,A,E,I,LAN,AP,TA,Lat,ECI Lon,Alt,ground spd");
    Elements JThis=new Elements(new MathStateTime((MathState)RV,Time.add(J.Epoch,I.getT())), J.GM, J.LengthUnit);
    P.println(""+I.getT()+","+RV.R()+","+RV.V()+","+JThis.A+","+JThis.E+","+toDegrees(JThis.I)+","+toDegrees(JThis.LAN)+","+toDegrees(JThis.AP)+","+toDegrees(JThis.TA)+","+toDegrees(lla.X())+","+toDegrees(lla.Y())+","+lla.Z());
	  for(int i=0;i<6000;i++) {
	    I.step();
	    RV=(MathState)I.getX();
      JThis=new Elements(new MathStateTime((MathState)RV,Time.add(J.Epoch,I.getT())), J.GM, J.LengthUnit);
      lla=Planet.Earth.S.xyz2lla(RV.R());
      xyzCloud=Planet.Earth.S.lla2xyz(lla.X(),lla.Y(),83000);
      P.println(""+I.getT()+","+RV.R()+","+RV.V()+","+JThis.A+","+JThis.E+","+toDegrees(JThis.I)+","+toDegrees(JThis.LAN)+","+toDegrees(JThis.AP)+","+toDegrees(JThis.TA)+","+toDegrees(lla.X())+","+toDegrees(lla.Y())+","+lla.Z()+","+MathVector.sub(xyzCloud,xyzCloudOld).length());
      xyzCloudOld=xyzCloud;
	  }
    P.close();
  }
}
