package org.kwansystems.space.gear;

import org.kwansystems.space.gear.actuator.Actuator;
import org.kwansystems.space.gear.actuator.Rocket;
import org.kwansystems.space.gear.actuator.Thruster;
import org.kwansystems.space.gear.guidance.Guidance;
import org.kwansystems.space.gear.mass.InertSixDOFMass;
import org.kwansystems.space.gear.mass.LiquidPropRes;
import org.kwansystems.space.gear.mass.PropRes;
import org.kwansystems.space.gear.mass.SixDOFMass;
import org.kwansystems.space.universe.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.table.*;
import org.kwansystems.tools.vector.*;

import static org.kwansystems.space.gear.mass.PropRes.PropType.*;

import java.util.*;
import static java.lang.Math.*;

public class Saturn1A extends SixDOFVehicle {
  double IECOTime=149.09;
  double OECOTime=141.66;
  public Saturn1A(Universe LU, Guidance LG) {
    super(LU,LG);
    //Set up arrays
    ArrayList<PropRes>  PropA=new ArrayList<PropRes>();
    ArrayList<SixDOFMass>     NonPropA=new ArrayList<SixDOFMass>();
    ArrayList<Thruster> ThrusterA=new ArrayList<Thruster>();
    ArrayList<Actuator> NonThrusterA=new ArrayList<Actuator>();
    
    //Stations
    double InchToMeter=0.0254;
    double STAEngineBells=20*InchToMeter;
    double STAEngineGimbal=100*InchToMeter;
    double STAThrustTop=178.75*InchToMeter;
    double STAFuelTankBase=189.5*InchToMeter;
    double STAFuelTankTop=889.3*InchToMeter;
    double STAS4Bottom=970.3*InchToMeter;
    double STAS4Top=1319.37*InchToMeter;
    double STAS5Bottom=1506*InchToMeter;
    double STAS5Top=1731.12*InchToMeter;
    double STAVehicleTop=1951.90*InchToMeter;
    
    //Radii
    double S1AThrustRadius=270.0/2.0*InchToMeter;
    double S1ARadius=257.0/2.0*InchToMeter;
    double S1AOuterRadius=70.0/2.0*InchToMeter;
    double S1ACenterRadius=105.0/2.0*InchToMeter;
    double S1APolyInRad=S1ARadius-S1AOuterRadius;
    double S1APolySideLength=2.0*S1APolyInRad/(1.0+sqrt(2.0));
    double S1AOuterLocation=hypot(S1APolyInRad,S1APolySideLength/2.0);
    double S1AInEngine=32*InchToMeter;
    double S1AOutEngine=95*InchToMeter;
    double S1ACenterCS=PI*S1ACenterRadius*S1ACenterRadius;
    double S1AOuterCS=PI*S1AOuterRadius*S1AOuterRadius;
    double S4Radius=220.0/2.0*InchToMeter;
    double S5Radius=120.0/2.0*InchToMeter;
    double S4BallastRadius=S1ACenterRadius;
    
    //Mass Values
    //Derived from SA-03 flight, since that has an explicit mass for ballast.
    //These are all first motion values (Range time=0.10s)
    //Also these are the "actual" values
    double S1ALOXLoad=239413+125;  //Includes gas at first motion
    double S1ALOXAtIECO=8295+1403; //This includes gas in LOX tanks. 
                                   //This gas is oxygen used to pressurize 
                                   //the LOX. It comes from the engine heat
                                   //exchangers, which boil some of the LOX
                                   //and route it back to the tanks.
    double S1ALOXUsed=(S1ALOXLoad-S1ALOXAtIECO);
    double SA03RangeZero=0.10;
    double S1AMainStage=141.66-SA03RangeZero;    //Amount of time from first motion to IECO 
    double S1ALOXFlowRate=S1ALOXUsed/S1AMainStage; //mean vehicle consumption of LOX, kg/s 
    double S1ACenterLoad=S1ALOXLoad*S1ACenterCS/(S1ACenterCS+S1AOuterCS*4);
    double S1AOuterLOXLoad=(S1ALOXLoad-S1ACenterLoad)/4;
    double S1AFuelLoad=108675;
    double S1AFuelAtIECO=7264;
    double S1AFuelUsed=(S1AFuelLoad-S1AFuelAtIECO);
    double S1AFuelFlowRate=S1AFuelUsed/S1AMainStage; //Mean vehicle consumption of fuel, kg/s 
     
    double S1AOuterFuelLoad=S1AFuelLoad/4;
    double S4BallastMass=41112; //Ballasts added up to predicted, not flight values
    double S5BallastMass=46217; //These are adjusted to sum to flight
    double BallastMass=S4BallastMass+S5BallastMass;
    double VehicleMass=143598+344+27;
    double DryMass=VehicleMass-BallastMass;
    //Thrust values
    double H1IspSl=2858;
    double H1Isp0=2858;
    double S1AFlowRate=S1AFuelFlowRate+S1ALOXFlowRate;
    double H1FlowRate=(S1AFlowRate)/8.0;
    double H1CenterPortion=S1ACenterLoad/4.0;
    double H1CenterRatio=H1CenterPortion/(H1CenterPortion+S1AOuterLOXLoad);
    double H1OuterRatio=1.0-H1CenterRatio;
    double H1RP1Ratio=S1AFuelUsed/S1ALOXUsed;
    
    //Saturn 1A Stage
    double Small=S1APolySideLength/2.0;
    double Large=S1APolyInRad;
    //Lox Tanks
    PropA.add(new LiquidPropRes(
      "S-1ALOXCenter",
      S1ACenterLoad,
      LOX,
      S1ACenterRadius,
      STAFuelTankBase,0,0
    ));
    PropA.add(new LiquidPropRes(
      "S1ALOX1",
      S1AOuterLOXLoad,
      LOX,
      S1AOuterRadius,
      STAFuelTankBase, Small,-Large
    ));
    PropA.add(new LiquidPropRes(
      "S1ALOX2",
      S1AOuterLOXLoad,
      LOX,
      S1AOuterRadius,
      STAFuelTankBase, -Large,-Small
    ));
    PropA.add(new LiquidPropRes(
      "S1ALOX3",
      S1AOuterLOXLoad,
      LOX,
      S1AOuterRadius,
      STAFuelTankBase,-Small, Large
    ));
    PropA.add(new LiquidPropRes(
      "S1ALOX4",
      S1AOuterLOXLoad,
      LOX,
      S1AOuterRadius,
      STAFuelTankBase,  Large, Small
    ));
    //Fuel
    PropA.add(new LiquidPropRes(
      "S1AFuel1",
      S1AOuterFuelLoad,
      RP1,
      S1AOuterRadius,
      STAFuelTankBase,-Small,-Large
    ));
    PropA.add(new LiquidPropRes(
      "S1AFuel2",
      S1AOuterFuelLoad,
      RP1,
      S1AOuterRadius,
      STAFuelTankBase, -Large,Small
    ));
    PropA.add(new LiquidPropRes(
      "S1AFuel3",
      S1AOuterFuelLoad,
      RP1,
      S1AOuterRadius,
      STAFuelTankBase,Small, Large
    ));
    PropA.add(new LiquidPropRes(
      "S1AFuel4",
      S1AOuterFuelLoad,
      RP1,
      S1AOuterRadius,
      STAFuelTankBase,  Large, -Small
    ));
    //Inert Mass
    NonPropA.add(InertSixDOFMass.CombineMass(
      "SixDOFVehicle Inert Weight",
      new SixDOFMass[] {InertSixDOFMass.MakeSolidCylinder(
        "S4 Ballast",
        S4BallastMass,
        S1ACenterRadius,
        STAS4Bottom,
        STAS4Top
      ),
      InertSixDOFMass.MakeSolidCylinder(
        "S5 Ballast",
        S5BallastMass,
        S5Radius,
        STAS5Bottom,
        STAS5Top
      ),
      InertSixDOFMass.MakeCylinderShell2(
        "SixDOFVehicle Shell",
        DryMass,
        2700, //Aluminum density
        S1ARadius,
        STAEngineBells,
        STAVehicleTop
      )}
    ));
    //Outboard Engines
    Large=sqrt(2)*S1AOutEngine/2.0;
    ThrusterA.add(new Rocket(
      "Engine1", 
      new MathVector(STAEngineGimbal,-Large,-Large),
      new int[] {0,2,5}, 
      new double[] {H1CenterRatio,H1OuterRatio,H1RP1Ratio},
      new MathVector(1,0,0),
      H1FlowRate,
      H1Isp0, 
      H1IspSl
    ));
    ThrusterA.add(new Rocket(
      "Engine2", 
      new MathVector(STAEngineGimbal,-Large,Large),
      new int[] {0,3,6}, 
      new double[] {H1CenterRatio,H1OuterRatio,H1RP1Ratio},
      new MathVector(1,0,0),
      H1FlowRate,
      H1Isp0, 
      H1IspSl
    ));
    
    ThrusterA.add(new Rocket(
      "Engine3", 
      new MathVector(STAEngineGimbal,Large,Large),
      new int[] {0,4,7}, 
      new double[] {H1CenterRatio,H1OuterRatio,H1RP1Ratio},
      new MathVector(1,0,0),
      H1FlowRate,
      H1Isp0, 
      H1IspSl
    ));
    ThrusterA.add(new Rocket(
      "Engine4", 
      new MathVector(STAEngineGimbal,Large,-Large),
      new int[] {0,1,8}, 
      new double[] {H1CenterRatio,H1OuterRatio,H1RP1Ratio},
      new MathVector(1,0,0),
      H1FlowRate,
      H1Isp0, 
      H1IspSl
    ));
    //Inboard Engines
    Large=S1AInEngine;
    ThrusterA.add(new Rocket(
      "Engine5", 
      new MathVector(STAEngineGimbal,0,-Large),
      new int[] {0,1,5}, 
      new double[] {H1CenterRatio,H1OuterRatio,H1RP1Ratio},
      new MathVector(1,0,0),
      H1FlowRate,
      H1Isp0, 
      H1IspSl
    ));
    ThrusterA.add(new Rocket(
      "Engine6", 
      new MathVector(STAEngineGimbal,-Large,Large),
      new int[] {0,2,6}, 
      new double[] {H1CenterRatio,H1OuterRatio,H1RP1Ratio},
      new MathVector(1,0,0),
      H1FlowRate,
      H1Isp0, 
      H1IspSl
    ));
    
    ThrusterA.add(new Rocket(
      "Engine7", 
      new MathVector(STAEngineGimbal,Large,Large),
      new int[] {0,3,7}, 
      new double[] {H1CenterRatio,H1OuterRatio,H1RP1Ratio},
      new MathVector(1,0,0),
      H1FlowRate,
      H1Isp0, 
      H1IspSl
    ));
    ThrusterA.add(new Rocket(
      "Engine8", 
      new MathVector(STAEngineGimbal,Large,-Large),
      new int[] {0,4,8}, 
      new double[] {H1CenterRatio,H1OuterRatio,H1RP1Ratio},
      new MathVector(1,0,0),
      H1FlowRate,
      H1Isp0, 
      H1IspSl
    ));
    
    //Unpack array lists
    SetupStuff(PropA,NonPropA,ThrusterA,NonThrusterA);
  }
  
  Table CutoffThrottle=new LinearTable(
    new double[] {-0.1,0.0,0.4,1.9,2.0},
    new double[][] {{ 1.0,1.0,0.1,0.0,0.0}}
  );
  
  public void Steer(double T, SixDOFState RVEw, boolean IsMajor, double P, double Y, double R) {
    //Outboard engines
    for(int i=0;i<4;i++) {
      Thrusters[i].Throttle=(T<OECOTime)?1.0:CutoffThrottle.Interp(T-OECOTime,0);
    }
    //Inboard engines
    for(int i=4;i<8;i++) {
      Thrusters[i].Throttle=(T<IECOTime)?1.0:CutoffThrottle.Interp(T-IECOTime,0);
    }
  }
  
  public Quaternion Navigate(double T, SixDOFState RVEw, MathVector FuelLevels, boolean IsMajor) {
    return Quaternion.U;
  }

  @Override
  public void Discrete(double T, SixDOFState RVEw, MathVector FuelLevels) {
	  // TODO Auto-generated method stub
	
  }
}
