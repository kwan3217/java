package org.kwansystems.space.peg;

import static org.kwansystems.tools.Scalar.*;
import org.kwansystems.tools.chart.*;
import java.lang.reflect.*;

public class PEGAC10 implements PEGVehicleModel {
  private static final double BoosterThrust0=834955.7; //N, Vacuum thrust per booster engine
  private static final double BoosterIsp0=2824;        //m/s, Vacuum effective exhaust velocity (Isp) for booster engines
  private static final double BoosterMdot=-BoosterThrust0/BoosterIsp0; //kg/s, booster propellant flow rate per engine
  private static final double BoosterEmptyMass=3329;   //kg, inert weight of booster package
  private static final double SustainerEmptyMass=4306;    //kg, inert weight of sustainer engine and tanks, inclding retro propellant
  private static final double Stage1PropMass=112237.2+1305; //kg, stage 1 propellant mass (including ground run propellant)
  private static final double SustainerThrust0=358135; //N, Vacuum thrust for sustainer engine
  private static final double SustainerIsp0=3009;      //m/s, Vacuum effective exhaust velocity (Isp) for sustainer engines
  private static final double SustainerMdot=-SustainerThrust0/SustainerIsp0; //kg/s, booster propellant flow rate per engine
  private static final double VernierThrust0=4448; //Component of thrust in thrust axis. The model cheats in Orbiter
  private static final double VernierIsp0=SustainerIsp0;
  private static final double VernierMdot=-VernierThrust0/VernierIsp0;
  private static final double CentaurThrust0=66700;
  private static final double CentaurIsp0=4354; //Should be 4354
  private static final double CentaurMdot=-CentaurThrust0/CentaurIsp0;
  private static final double CentaurEmptyMass=1866;
  private static final double CentaurPropMass=14200;
  private static final double RCSPropMass=100;
  private static final double FairingMass=891;
  private static final double InsulationMass=532;
  private static final double SurveyorLanderEmptyMass=289.10;
  private static final double SurveyorRetroEmptyMass=64.88;
  private static final double SurveyorAMRMass=3.82;
  private static final double SurveyorRetroPropMass=560.64;
  private static final double SurveyorVernierPropMass=70.98;
  private static final double SurveyorRCSPropMass=2;
  private static final double SurveyorEmptyMass=SurveyorLanderEmptyMass+
                                                SurveyorRetroEmptyMass+
                                                SurveyorAMRMass;
  private static final double SurveyorPropMass=SurveyorRetroPropMass+
                                               SurveyorVernierPropMass+
                                               SurveyorRCSPropMass;
  private static final double PayloadMass=SurveyorEmptyMass+SurveyorPropMass;
  private static final double Stage2InertMass=PayloadMass+CentaurEmptyMass+RCSPropMass;
  private static final double Stage1InertMass=Stage2InertMass+CentaurPropMass+SustainerEmptyMass+FairingMass+InsulationMass;
  private static final double Stage0InertMass=Stage1InertMass+BoosterEmptyMass;
  private static final double GroundMass=Stage0InertMass+Stage1PropMass;
  private static final double GroundStartTime=-2.05;
  private static final double BECOAcc=5.7*9.81;
  private static final double Stage0Mdot=2*BoosterMdot+SustainerMdot+2*VernierMdot;
  private static final double Stage0Thrust=2*BoosterThrust0+SustainerThrust0+2*VernierThrust0;
  private static final double Stage0Isp=-Stage0Thrust/Stage0Mdot;
  private static final double BECOMass=Stage0Thrust/BECOAcc; //Mass of stack at BECO
  private static final double BECOPropMass=BECOMass-Stage0InertMass;
  private static final double BECOPropBurned=Stage1PropMass-BECOPropMass;
  private static final double BECOTime=-BECOPropBurned/Stage0Mdot;
  private static final double BoosterDropTime=BECOTime+3.1;
  private static final double InsulationDropTime=BoosterDropTime+30.9;
  private static final double FairingDropTime=InsulationDropTime+26.5;
  private static final double Stage1Mdot=SustainerMdot+2*VernierMdot;
  private static final double Stage1Thrust=SustainerThrust0+2*VernierThrust0;
  private static final double Stage1Isp=-Stage1Thrust/Stage1Mdot;
  private static final double SECOTime=BECOTime-BECOPropMass/Stage1Mdot;
  private static final double SustainerDropTime=SECOTime+1.9;
  private static final double CentaurStartTime=SustainerDropTime+9.6;
  private static final double Stage2Mdot=2*CentaurMdot;
  private static final double Stage2Thrust=2*CentaurThrust0;
  private static final double Stage2Isp=-Stage2Thrust/Stage2Mdot;
  private static final double CECOTime=CentaurStartTime-CentaurPropMass/Stage2Mdot;
  public double ThrustMag(double t) {
    if(t<GroundStartTime)  return 0;
    if(t<BECOTime)         return Stage0Thrust;
    if(t<SECOTime)         return Stage1Thrust;
    if(t<CentaurStartTime) return 0;
    if(t<CECOTime)         return Stage2Thrust;
    return 0;
  }
  private double emptyMass(double t) {
    double result=Stage2InertMass;
    if(t<SustainerDropTime) result+=SustainerEmptyMass;
    if(t<FairingDropTime) result+=FairingMass;
    if(t<InsulationDropTime) result+=InsulationMass;
    if(t<BoosterDropTime) result+=BoosterEmptyMass;
    return result;
  }
  private double Stage1PropMass(double t) {
    if(t<GroundStartTime) return Stage1PropMass;
    if(t<BECOTime) return linterp(GroundStartTime,Stage1PropMass,BECOTime,BECOPropMass,t);
    if(t<SECOTime) return linterp(BECOTime,BECOPropMass,SECOTime,0,t);
    return 0;
  }
  private double Stage2PropMass(double t) {
    if(t<CentaurStartTime) return CentaurPropMass;
    if(t<CECOTime) return linterp(CentaurStartTime,CentaurPropMass,CECOTime,0,t);
    return 0;
  }
  public double Mass(double t) {
    return emptyMass(t)+Stage1PropMass(t)+Stage2PropMass(t);
  }
  public double Isp(double t) {
    if(t<GroundStartTime)  return 0;
    if(t<BECOTime)         return Stage0Isp;
    if(t<SECOTime)         return Stage1Isp;
    if(t<CentaurStartTime) return 0;
    if(t<CECOTime)         return Stage2Isp;
    return 0;
  }
  private void PrintFields() {
    Class cc=getClass();
    for (Field f : cc.getDeclaredFields()) {
      String name = f.getName();
      try {
        System.out.println(name+": "+f.get(this));
      } catch (IllegalArgumentException ex) {
        throw new RuntimeException(ex);
      } catch (IllegalAccessException ex) {
        throw new RuntimeException(ex);
      }
    }
  }
  public static void main(String[] args) {
    PEGAC10 AC10=new PEGAC10();
    AC10.PrintFields();
    ChartRecorder C=new ArrayListChartRecorder();
    for(int t=-5;t<720;t++) {
      C.Record(t, "Mass",          "100g",  AC10.Mass(t)*10);
      C.Record(t, "Vacuum Thrust", "N",   AC10.ThrustMag(t));
      C.Record(t, "Acc",          "10um/s",  AC10.ThrustMag(t)/AC10.Mass(t)*1e5);
      C.Record(t, "Isp",           "mm/s", AC10.Isp(t)*1000);
    }
    C.PrintTable(new DisplayPrinter());
  }

  public double[] upperIsp() {
    return new double[] {0,0,Stage2Isp};
  }

  public double[] uppera() {
    return new double[] {0,0,Stage2Thrust/Mass(CentaurStartTime)};
  }
  public double[] lowert() {
    return new double[] {0,CentaurStartTime,0};
  }
}
