package org.kwansystems.space.gear;

import org.kwansystems.space.gear.mass.*;
import org.kwansystems.space.kepler.*;
import org.kwansystems.space.planet.*;
import org.kwansystems.space.universe.*;
import org.kwansystems.tools.*;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeEpoch;
import org.kwansystems.tools.time.TimeScale;
import org.kwansystems.tools.time.TimeUnits;
import org.kwansystems.tools.vector.*;

import static java.lang.Math.*;

public class AIMFreeFloat extends TestFlight {
  Time Epoch;
  public AIMFreeFloat(SixDOFVehicle LTestVehicle) {
    super(LTestVehicle);
  }
  public AIMFreeFloat(SixDOFVehicle LTestVehicle, int x) {
    super(LTestVehicle,x);
  }
  public void RecordAdditionalTestData(double T, MathVector X) {
    MathVector Pos=X.subVector(0,3);
    MathVector Vel=X.subVector(3,3);
    System.out.println(((int)T)/60);
    Time TT=Time.add(Epoch, T);
    MathStateTime ST=new MathStateTime(Pos,Vel,TT);
    Elements E=new Elements(ST,Spheroid.WGS84.GM,"m");
    CR.Record(T,"h","m",Pos.length()-Spheroid.WGS84.Re);
    CR.Record(T,"hp","m",E.Periapse-Spheroid.WGS84.Re);
    CR.Record(T,"ha","m",E.Apoapse-Spheroid.WGS84.Re);
    CR.Record(T, "Integrator Error Estimate",I.getErrorEstimate());
    CR.Record(T, "Integrator microStep size",I.getMicroStepSize());
    CR.Record(T, "Integrator nGood",I.getNGoodSteps());
    CR.Record(T, "Integrator nBad",I.getNBadSteps());
    CR.Record(T, "Integrator nsteps",I.getNSteps());
    CR.Record(T, "Integrator Max Delta",I.getMinDelta());
    CR.Record(T,"ST",ST);
    CR.Record(T,"E",E);
  }
  public static void main(String[] args) {
	  Time Epoch=new Time(2007,4,28,12,0,0,TimeUnits.Seconds,TimeScale.UTC,TimeEpoch.Java);
	  MathVector ECF_r=new MathVector(-3837414,-814607,-5764886);
	  MathVector ECF_v=new MathVector(-6368.035, 803.238, 4125.583);
    MathMatrix M3=MathMatrix.Rot3(-org.kwansystems.space.planet.Planet.Earth.GMST2(Epoch));
    MathState ECF=new MathState(ECF_r,ECF_v);
    System.out.println(ECF);
    MathState ECI=M3.transform(ECF);
    MathVector ECI_r=ECI.R();
    MathVector ECI_v=MathVector.add(ECI.V(),Planet.Earth.Wind(ECI.R()));
	
	  SixDOFState RVEw0=new SixDOFState(ECI_r,ECI_v,new Quaternion(),new MathVector());
	  Universe U=new TwoBody(Planet.Earth);
	  InertSixDOFMass M=InertSixDOFMass.MakeCylinderShell("AIM", 197, 97/2, 90/2, -0.5, 0.5);
	  SixDOFVehicle AIM=new InertSixDOFVehicle(U, M);
	  AIMFreeFloat A=new AIMFreeFloat(AIM,1);
	  A.Epoch=Epoch;
	  A.DoTest(RVEw0, 0, 180, 60*9);
    A.CR.PrintTable(new HTMLPrinter("AimFreeFloat.html"));
	  Object[] EE=A.CR.getColumn("E");
	  OrbitalZoetrope OZ=new OrbitalZoetrope("Orbit Evolution", 100, EE, 1);
	  OZ.setVisible(true);
	  OZ.start();
  }
}
