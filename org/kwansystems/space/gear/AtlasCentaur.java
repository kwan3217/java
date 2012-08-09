package org.kwansystems.space.gear;

import org.kwansystems.space.gear.actuator.*;
import org.kwansystems.space.gear.guidance.*;
import org.kwansystems.space.gear.mass.*;
import org.kwansystems.space.planet.atmosphere.*;
import org.kwansystems.space.universe.*;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.vector.*;

import java.io.*;
import java.util.*;
import static org.kwansystems.space.gear.mass.PropRes.PropType.*;

public class AtlasCentaur extends SixDOFVehicle {
  GuidanceStateMachine gsm;
  LiquidPropRes AtlasFuel,AtlasLOX,CentaurFuel,CentaurLOX;
  Thruster Sustainer, Booster1,Booster2,Vernier1,Vernier2,Centaur1,Centaur2;
  SixDOFMass AtlasInert,BoosterInert,CentaurInert;
  //Propellant Constants
  static double RP1Mix=2.25; //Oxidizer to fuel mass ratio
  static double LH2Mix=6; //Oxidizer to fuel mass ratio
  static double KG_PER_LB=0.45359237;

  //Booster constants
  static double BoosterAeroAft=0;
  static double BoosterAeroFwd=3.4;
  static double BoosterR=3.048/2;
  static double BoosterThrustPt=0;
  static double BoosterEmptyMass=3329;
  static double BoosterEngineMass=720;
  static double BoosterShellMass=BoosterEmptyMass-2*BoosterEngineMass;
  static double BoosterIsp0=2824;
  static double BoosterIsp1=2469;
  static double BoosterFlowRate=289; //kg/s, flow rate per booster engine (2 engines on booster)

  //Atlas constants
  static double AtlasR=BoosterR;
  static double AtlasAeroAft=BoosterAeroFwd;
  static double AtlasAeroFwd=28.85;
  static double AtlasThrustPt=0;
  static double AtlasFuelMass=75648*KG_PER_LB; //AC-10 p120, Flight main impulse 
  static double AtlasLOXMass=171594*KG_PER_LB; //ibid, LOX main impulse
  static double AtlasPropMass=AtlasFuelMass+AtlasLOXMass; 
  static double AtlasFuelAft=BoosterAeroFwd;
  static double AtlasLOXVol=AtlasLOXMass/LOX.density;
  static double AtlasInertMass=4306;
  static double AtlasEngineMass=460;
  static double AtlasTankMass=AtlasInertMass-AtlasEngineMass;
  static double AtlasIsp0=3009;
  static double AtlasIsp1=2089;
  static double AtlasFlowRate=120; //kg/s
  
  //Centaur constants
  static double CentaurR=AtlasR;
  static double CentaurAeroAft=AtlasAeroFwd;
  static double CentaurAeroFwd=28.85;
  static double CentaurThrustPt=CentaurAeroAft-2;
  static double CentaurPropMass=14200; 
  static double CentaurFuelMass=CentaurPropMass*1/(RP1Mix+1);
  static double CentaurLOXMass=AtlasPropMass*RP1Mix/(RP1Mix+1);
  static double CentaurFuelAft=CentaurAeroAft;
  static double CentaurInertMass=1866;
  static double CentaurEngineMass=131;
  static double CentaurTankMass=CentaurInertMass-2*CentaurEngineMass;
  
  //Fairing
  static double FairingAft=CentaurAeroFwd;
  static double FairingLen=20.7;
  static double FairingFwd=FairingAft+FairingLen;
  static double FairingR=BoosterR;
  static double FairingMass=1500;

  public AtlasCentaur(Universe LU, Guidance LG) {
    super(LU, LG);
    //Set up arrays
    
    //Atlas
    AtlasFuel=(LiquidPropRes)AddProp(new LiquidPropRes("AtlasFuel",AtlasFuelMass,RP1, AtlasFuelAft,AtlasR));
    AtlasLOX=(LiquidPropRes)AddProp(new LiquidPropRes("AtlasLOX",AtlasLOXMass,LOX,AtlasFuelAft+AtlasFuel.L,AtlasR));
    Sustainer=AddThruster(new Rocket( 
      "Sustainer", 
      new MathVector(AtlasThrustPt,0,0),
      this,
      new String[] {"AtlasFuel","AtlasLOX"}, 
      new double[] {1,RP1Mix},
      new MathVector(1,0,0),
      AtlasFlowRate,
      AtlasIsp0, 
      AtlasIsp1
    ));
    List<SixDOFMass> MassList=new ArrayList<SixDOFMass>();
    MassList.add(
  	  InertSixDOFMass.MakeCylinderShell2(
    	"AtlasTank",
    	AtlasTankMass, 
    	Aluminum.density,
    	AtlasR,
    	AtlasAeroAft,
    	AtlasAeroFwd
      )
    );
    MassList.add(
  	  InertSixDOFMass.MakeCylinderShell2(
    	"AtlasEngine",
    	AtlasEngineMass, 
    	Aluminum.density,
    	0.4,
    	AtlasThrustPt,
    	AtlasThrustPt+1.5
      )
    );
    AtlasInert=AddMass(InertSixDOFMass.CombineMass(
      "AtlasInert",
      MassList,
      0,
      null
    ));
    MassList.clear();

    //Booster
    Booster1=AddThruster(new Rocket(
      "Booster1", 
      new MathVector(AtlasThrustPt,1.3,0),
      this,
      new String[] {"AtlasFuel","AtlasLOX"}, 
      new double[] {1,RP1Mix},
      new MathVector(1,0,0),
      BoosterFlowRate,
      BoosterIsp0, 
      BoosterIsp1
    ));
    Booster2=AddThruster(new Rocket(
      "Booster2", 
      new MathVector(AtlasThrustPt,-1.3,0),
      this,
      new String[] {"AtlasFuel","AtlasLOX"}, 
      new double[] {1,RP1Mix},
      new MathVector(1,0,0),
      BoosterFlowRate,
      BoosterIsp0, 
      BoosterIsp1
	  ));
    MassList.add(
  	  InertSixDOFMass.MakeCylinderShell2(
    	"BoosterShell",
    	BoosterShellMass, 
    	Aluminum.density,
    	BoosterR,
    	BoosterAeroAft,
    	BoosterAeroFwd
      )
    );
    MassList.add(InertSixDOFMass.MakeCylinderShell2(
      "Booster1Mass",
      BoosterEngineMass, 
      Aluminum.density,
      0.5,
      new MathVector(AtlasThrustPt,1.3,0),
      AtlasThrustPt+1.5
    ));
    MassList.add(InertSixDOFMass.MakeCylinderShell2(
      "Booster2Mass",
      BoosterEngineMass, 
      Aluminum.density,
      0.5,
      new MathVector(AtlasThrustPt,-1.3,0),
      AtlasThrustPt+1.5
    ));
    BoosterInert=AddMass(InertSixDOFMass.CombineMass(
      "BoosterInert",
      MassList,
      0,
      null
    ));
    MassList.clear();
    gsm=new GuidanceStateMachine();
    gsm.addState("Start", new GuidanceState() {
      public void enterState(GuidanceStateMachine parent, SixDOFVehicle vehicle, double T, SixDOFState RVEw, MathVector fuelLevels) {
        AtlasCentaur AC=(AtlasCentaur)vehicle;
        AC.Booster1.Throttle=1;
        AC.Booster2.Throttle=1;
        AC.Sustainer.Throttle=1;
        parent.setTimer(T,142.04);
      }
      public void step(GuidanceStateMachine parent, SixDOFVehicle vehicle, double T, SixDOFState RVEw, MathVector fuelLevels) {
        if(parent.isTimerExp(T)) {//vehicle.StrapdownAccelerometer(T, RVEw).get(0)>56.8) {
          parent.setState("BoosterShutdown");
        }
      }
    });
    gsm.addState("BoosterShutdown", new GuidanceState() {
      public void enterState(GuidanceStateMachine parent, SixDOFVehicle vehicle, double T, SixDOFState RVEw, MathVector fuelLevels) {
        parent.setTimer(T,3.0);
        AtlasCentaur AC=(AtlasCentaur)vehicle;
        AC.Booster1.Throttle=0;
        AC.Booster2.Throttle=0;
        AC.Sustainer.Throttle=1;
      }
      public void step(GuidanceStateMachine parent, SixDOFVehicle vehicle, double T, SixDOFState RVEw, MathVector fuelLevels) {
        if(parent.isTimerExp(T)) {
          parent.setState("BoosterDrop");
        }
      }
    });
    gsm.addState("BoosterDrop", new GuidanceState() {
      public void enterState(GuidanceStateMachine parent, SixDOFVehicle vehicle, double T, SixDOFState RVEw, MathVector fuelLevels) {
        AtlasCentaur AC=(AtlasCentaur)vehicle;
        AC.Booster1.Active=false;
        AC.Booster2.Active=false;
        AC.BoosterInert.Active=false;
        AC.Sustainer.Throttle=1;
      }
      public void step(GuidanceStateMachine parent, SixDOFVehicle vehicle, double T, SixDOFState RVEw, MathVector fuelLevels) {}
    });
    gsm.setState("Start");
  }
  public void Steer(double T, SixDOFState RVEw, boolean IsMajor, MathVector SteerVector) {
  }
  public void Discrete(double T, SixDOFState RVEw, MathVector FuelLevels) {
    gsm.step(this, T, RVEw, FuelLevels);
  }
  public static void main(String[] args) throws IOException {
    ChartRecorder C=new ArrayListChartRecorder(1000,20);
    Universe U=new FlatEarth(
      9.80665,
      new KwanEarthAtmosphere()
    );
    SixDOFVehicle TV=new AtlasCentaur(U,null) {
  /*    public MathVector Constrain(double T, MathVector X) {
   	    MathVector SuperConstrain=super.Constrain(T,X);
   	    MathVector NonRVEw=SuperConstrain.subVector(13);
   	    return new MathVector(new MathVector[]{TestStand.RVEw0,NonRVEw});
      } */
    };
    System.out.println(TV.EquivalentMass(0,null));
    TestStand T=new TestStand(TV,new MathVector(0,0,0),C) {
      public void RecordTestData(double T, MathVector X) {
        SixDOFState RVEw=new SixDOFState(X);
        double Z=RVEw.R().Z();
        if(Double.isNaN(Z) || Double.isInfinite(Z)) {
          System.out.println("Problem! Z="+RVEw.R().Z());
        }
        SixDOFMass M=TestVehicle.EquivalentMass(T,RVEw); 
        MathVector Fuel=X.subVector(13);
        CR.Record(T,"CoM X","10m",M.getCoM(T,RVEw).X()*10);
        CR.Record(T,"CoM Y","10m",M.getCoM(T,RVEw).Y()*10);
        CR.Record(T,"CoM Z","10m",M.getCoM(T,RVEw).Z()*10);
        CR.Record(T,"Mass","Mg",M.getMass(T,RVEw)/1000.0);
        CR.Record(T,"Ax", TestVehicle.StrapdownAccelerometer(T, RVEw).get(0));
        CR.Record(T,"State","m,s",RVEw);
        CR.Record(T,"Altitude","km",Z/1000.0);
        CR.Record(T, "AtlasFuel", Fuel.get(0)*AtlasFuelMass/1000.0);
        CR.Record(T, "AtlasLOX", Fuel.get(1)*AtlasLOXMass/1000.0);
//        for(Thruster Th:this.TestVehicle.Thrusters) {
//          CR.Record(T,, Th.Name+" Thrust", "kN", Th.)	
//        }
      }
    };
    T.DoTest(TestStand.RVEw0,0.10,100,300,new double[] {1,1});
    C.PrintTable(new HTMLPrinter("AtlasCentaurTelementry.html"));
    C.PrintSubTable(new String[] {"CoM X","CoM Y","CoM Z","Mass","Altitude","Ax","AtlasFuel","AtlasLOX"} , new DisplayPrinter());
  }
}
