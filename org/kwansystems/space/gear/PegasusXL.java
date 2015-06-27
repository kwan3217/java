package org.kwansystems.space.gear;

import org.kwansystems.space.gear.actuator.Actuator;
import org.kwansystems.space.gear.actuator.Airfoil;
import org.kwansystems.space.gear.actuator.FlatPlateAirfoil;
import org.kwansystems.space.gear.actuator.PegasusXLWing;
import org.kwansystems.space.gear.actuator.Rocket;
import org.kwansystems.space.gear.actuator.ThrustTableRocket;
import org.kwansystems.space.gear.actuator.Thruster;
import org.kwansystems.space.gear.guidance.Guidance;
import org.kwansystems.space.gear.mass.GasPropRes;
import org.kwansystems.space.gear.mass.InertSixDOFMass;
import org.kwansystems.space.gear.mass.PropRes;
import org.kwansystems.space.gear.mass.SixDOFMass;
import org.kwansystems.space.gear.mass.SolidPropRes;
import org.kwansystems.space.kepler.*;
import org.kwansystems.space.planet.*;
import org.kwansystems.space.planet.atmosphere.*;
import org.kwansystems.space.universe.*;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.integrator.*;
import org.kwansystems.tools.rootfind.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.table.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeUnits;
import org.kwansystems.tools.vector.*;

import static org.kwansystems.space.gear.mass.PropRes.*;
import static org.kwansystems.space.gear.mass.PropRes.PropType.*;

import java.io.*;
import java.util.*;
import static java.lang.Math.*;

public class PegasusXL extends SixDOFVehicle {
  final boolean useVAero=false;
  //15 Thruster indexes
  Thruster Stage1Rocket,Stage2Rocket,Stage3Rocket;
  Thruster RCSLPX,RCSLMX,RCSLPZ,RCSLMZ;
  Thruster RCSRPX,RCSRMX,RCSRPZ,RCSRMZ;
  Thruster RCSTPX,RCSTMX,RCSBPX,RCSBMX;
  //17 Aero indexes
  Actuator Stage1HAero,Stage2HAero,Stage3HAero;
  Actuator Stage1VAero,Stage2VAero,Stage3VAero;
  Actuator LWing,RWing;
  Actuator InterstageHAero,FairingHAero;
  Actuator InterstageVAero,FairingVAero;
  Airfoil LFin,RFin,VFin;
  Actuator PayloadHAero,PayloadVAero;
  //4 Fuel indexes
  PropRes Stage1Fuel,Stage2Fuel,Stage3Fuel,RCSFuel;
  //6 Inert indexes
  SixDOFMass Stage1Inert,Stage2Inert,Stage3Inert,InterstageInert,FairingInert,PayloadInert;
  double StageAeroWeight;
  public PegasusXL(Universe LU, Guidance LG, double LStageAeroWeight) {
    super(LU,LG);
    StageAeroWeight=LStageAeroWeight;
    
    //Stage 1 constants
    double Stage1ROuter=1.27/2;
    double Stage1RMid=1.26/2;
    double Stage1FuelAft=1.905;
    double Stage1FuelFwd=10.005;
    double Stage1AeroAft=0.47;
    double Stage1AeroFwd=5.7;

    //Stage 2 constants
    double Stage2ThrustPt=Stage1AeroFwd;
    double Stage2AeroAft=10.81;
    double Stage2AeroFwd=12.997;
    double Stage2FuelAft=Stage2AeroAft;
    double Stage2FuelFwd=Stage2AeroFwd;

    //Stage 1
    Stage1Fuel=AddProp(new SolidPropRes("Stage1Fuel",15014,PegasusSolid,Stage1RMid, Stage1FuelAft,Stage1FuelFwd)); 
    Stage1Rocket=AddThruster(new ThrustTableRocket(
      "Stage1Rocket", 
      new MathVector(Stage1AeroAft,0,0),
      this,
      new String[] {"Stage1Fuel"}, 
      new double[] {1},
      new MathVector(1,0,0),
      15014.0/68.6,
      2903, 
      2903,
      new LinearTable(
        new double[] {5.0,15.0,20.0,73.6},
        new double[][] {{2.0, 2.0, 2.0, 1.0}}
      )
    ));
    double FinLength=1.542; 
    double FinAft=0.712;
    double FinMid=1.183;
    double FinFore=2.32;
    double FinThickness=0.2;
    double FinOut=0.64;
    double FinChord=(FinMid-FinAft)+(FinFore-FinMid)/2;
    double FinCoLX=FinAft+FinChord*0.75;
    double FinCoLZ=FinOut+FinLength/3.0;
    //Don't use PegasusXLWing, as that wing stalls which will confuse the control
    //mechanism to no end.
    double FinAngle=toRadians(23);
    double FinC=cos(FinAngle);
    double FinS=sin(FinAngle);
    LFin=(Airfoil)AddActuator(new FlatPlateAirfoil(
      "LFin",
      new MathVector(FinCoLX,FinCoLZ*FinC,-FinCoLZ*FinS),
      new MathVector(0,FinC,-FinS), //Axis
      new MathVector(1,0,0),       //ChordLine
      FinChord,                    //Chord
      FinLength                    //Span
, 0.25
    ));
    RFin=(Airfoil)AddActuator(new FlatPlateAirfoil(
      "RFin",
      new MathVector(FinCoLX,-FinCoLZ*FinC,-FinCoLZ*FinS),
      new MathVector(0,-FinC,-FinS),//Axis
      new MathVector(1,0,0),       //ChordLine
      FinChord,                    //Chord
      FinLength                    //Span
, 0.25
    ));
    VFin=(Airfoil)AddActuator(new FlatPlateAirfoil(
      "VFin",
      new MathVector(FinCoLX,0,FinCoLZ),
      new MathVector(0,0,1),       //Axis
      new MathVector(1,0,0),       //ChordLine
      1.5,                         //Chord
      1.5                          //Span
, 0.25
    ));

    double FinBoxVolume=FinLength*(FinMid-FinAft)*FinThickness;
    double FinTriVolume=FinLength*(FinFore-FinMid)*FinThickness/2;
    double FinVolume=FinBoxVolume+FinTriVolume;
    double FinMass=31;
    double FinBoxMass=FinMass*FinBoxVolume/FinVolume;
    double FinTriMass=FinMass*FinTriVolume/FinVolume;
    List<SixDOFMass> MassList=new ArrayList<SixDOFMass>();
    MassList.add(new InertSixDOFMass(
      "FinBox",
      FinBoxMass,
      new MathVector(FinAft+(FinMid-FinAft)/2,FinLength/2+FinOut,0),
      SixDOFMass.RectangularPrismI(FinBoxMass,FinMid-FinAft, FinLength,FinThickness)
    ));
    MassList.add(new InertSixDOFMass(
      "FinTri",
      FinTriMass,
      new MathVector(FinMid+(FinFore-FinMid)/3,FinLength/2+FinOut,0),
      SixDOFMass.TriangularPrismI(FinTriMass,FinFore-FinMid, FinLength/2,FinThickness)
    ));
    InertSixDOFMass RFinMass=InertSixDOFMass.CombineMass(
      "RFin",
      MassList,
      0,
      null
    );
    MassList.clear();
    InertSixDOFMass LFinMass=new InertSixDOFMass("LFin",RFinMass);
    InertSixDOFMass VFinMass=new InertSixDOFMass("VFin",RFinMass);
    RFinMass.Rotate(MathMatrix.Rot1d(FinAngle));
    LFinMass.Rotate(MathMatrix.Rot1d(180-FinAngle));
    VFinMass.Rotate(MathMatrix.Rot1d(-90));
    MassList.add(RFinMass);
    MassList.add(LFinMass);
    MassList.add(VFinMass);
    InertSixDOFMass Fins=InertSixDOFMass.CombineMass(
      "Fins",
      MassList,
      0,
      null
    );
    MassList.clear();
    double MainWingSpan=6.7;
    double MainWingAft=5.7;
    double MainWingMid=6.00;
    double MainWingFore=9.70;
    double MainWingThickness=0.2;
    double MainWingZ=Stage1ROuter+MainWingThickness/2;
    double MainWingChord=(MainWingMid-MainWingAft)+(MainWingFore-MainWingMid)/2;
    double MainWingCoL=MainWingAft+MainWingChord*0.75;
    AddActuator(new PegasusXLWing(
      "LWing", 
      new MathVector(MainWingCoL,MainWingSpan/6,MainWingZ), //CoL       
      new MathVector(0,1,0),
      new MathVector(1,0,0), 
      MainWingChord,               //Chord
      MainWingSpan/2                 //Span
    ));
    AddActuator(new PegasusXLWing(
      "RWing", 
      new MathVector(MainWingCoL,-MainWingSpan/6,MainWingZ),       
      new MathVector(0,1,0),
      new MathVector(1,0,0), 
      MainWingChord,               //Chord
      MainWingSpan/2                 //Span
    ));
    AddActuator(new FlatPlateAirfoil(
      "Stage1Aero", 
      new MathVector((Stage1AeroAft+Stage1AeroFwd)/2,0,0),
      new MathVector(0,1,0),
      new MathVector(1,0,0), 
      Stage1AeroFwd-Stage1AeroAft,   //Chord
      Stage1ROuter*2, //Span
      Airfoil.LiftModel.Axial, 0
    ));
    double MainWingBoxVolume=MainWingSpan*(MainWingMid-MainWingAft)*MainWingThickness;
    double MainWingTriVolume=MainWingSpan*(MainWingFore-MainWingMid)*MainWingThickness/2;
    double MainWingVolume=MainWingBoxVolume+MainWingTriVolume;
    double MainWingMass=450;
    double MainWingSaddleMass=450;
    double MainWingBoxMass=MainWingMass*MainWingBoxVolume/MainWingVolume;
    double MainWingTriMass=MainWingMass*MainWingTriVolume/MainWingVolume;
    MassList.add(new InertSixDOFMass(
      "MainWingBox",
      MainWingBoxMass,
      new MathVector(MainWingAft+(MainWingMid-MainWingAft)/2,0,MainWingZ),
      SixDOFMass.RectangularPrismI(MainWingBoxMass,MainWingMid-MainWingAft, MainWingSpan,MainWingThickness)
    ));
    MassList.add(new InertSixDOFMass(
      "MainWingRight",
      MainWingTriMass/2,
      new MathVector(MainWingMid+(MainWingFore-MainWingMid)/3,MainWingSpan/6,MainWingZ),
      SixDOFMass.TriangularPrismI(MainWingTriMass/2,MainWingFore-MainWingMid, MainWingSpan/2,MainWingThickness)
    ));
    MassList.add(new InertSixDOFMass(
      "MainWingLeft",
      MainWingTriMass/2,
      new MathVector(MainWingMid+(MainWingFore-MainWingMid)/3,-MainWingSpan/6,MainWingZ),
      SixDOFMass.TriangularPrismI(MainWingTriMass/2,MainWingFore-MainWingMid, -MainWingSpan/2,MainWingThickness)
    ));
    MassList.add(new InertSixDOFMass(
      "MainWingSaddle",
      MainWingMass/2,
      new MathVector((MainWingAft+MainWingFore)/2, 0,Stage1ROuter/2),
      SixDOFMass.RectangularPrismI(MainWingSaddleMass, (MainWingFore-MainWingAft)/2, Stage1ROuter*2,Stage1ROuter)
    ));
    InertSixDOFMass MainWing=InertSixDOFMass.CombineMass(
      "MainWing",
      MassList,
      0,
      null
    );
    MassList.clear();
    InertSixDOFMass Stage1Case=InertSixDOFMass.MakeCylinderShell(
      "Stage1Case", 
      1369, 
      Stage1ROuter,
      Stage1RMid, 
      Stage1AeroAft, 
      Stage1AeroFwd
    );
    MassList.add(Fins);
    MassList.add(MainWing);
    MassList.add(Stage1Case);
    Stage1Inert=AddMass(InertSixDOFMass.CombineMass(
      "Stage1Inert",
      MassList,
      0,
      null
    ));
    MassList.clear();

    //Interstage
    AddMass(InertSixDOFMass.MakeCylinderShell(
      "InterstageInert", 
      151, 
      Stage1ROuter,
      Stage1RMid, 
      Stage1AeroFwd, 
      Stage2AeroAft
    ));
    AddActuator(new FlatPlateAirfoil(
      "InterstageHAero", 
      new MathVector((Stage1AeroFwd+Stage2AeroFwd)/2,0,0),
      new MathVector(0,0,1),
      new MathVector(1,0,0), 
      Stage2AeroAft-Stage1AeroFwd,   //Chord
      Stage1ROuter*2*StageAeroWeight //Span
, 0.25
    ));
    if(useVAero) {
      AddActuator(new FlatPlateAirfoil(
        "InterstageVAero", 
        new MathVector((Stage1AeroFwd+Stage2AeroFwd)/2,0,0),
        new MathVector(0,1,0),
        new MathVector(1,0,0), 
        Stage2AeroAft-Stage1AeroFwd,   //Chord
        Stage1ROuter*2*StageAeroWeight //Span
, 0.25
      ));
    }
    //Stage 2
    AddProp(new SolidPropRes("Stage2Fuel",3925,PegasusSolid,Stage1RMid, Stage2FuelAft, Stage2FuelFwd)); 
    AddThruster(new Rocket(
      "Stage2Rocket", 
      new MathVector(Stage2ThrustPt,0,0),
      this,
      new String[] {"Stage2Fuel"}, 
      new double[] {1},
      new MathVector(1,0,0),
      3925.0/69.4,
      2858, 
      2858
    ));
    AddMass(InertSixDOFMass.MakeCylinderShell(
      "Stage2Inert", 
      416, 
      Stage1ROuter,
      Stage1RMid, 
      10.81, 
      12.997
    ));
    AddActuator(new FlatPlateAirfoil(
      "Stage2HAero", 
      new MathVector((Stage2AeroAft+Stage2AeroFwd)/2,0,0),
      new MathVector(0,0,1),
      new MathVector(1,0,0), 
      Stage2AeroFwd-Stage2AeroAft,  //Chord
      Stage1ROuter*StageAeroWeight  //Span
, 0.25
    ));
    if(useVAero) {
      AddActuator(new FlatPlateAirfoil(
        "Stage2VAero", 
        new MathVector((Stage2AeroAft+Stage2AeroFwd)/2,0,0),
        new MathVector(0,1,0),
        new MathVector(1,0,0), 
        Stage2AeroFwd-Stage2AeroAft,  //Chord
        Stage1ROuter*StageAeroWeight  //Span
, 0.25
      ));
    }
    //Stage 3
    double Stage3ROuter=0.97/2;
    double Stage3RMid=0.96/2;
    AddProp(new SolidPropRes("Stage3Fuel",770,PegasusSolid,Stage1RMid, 13.541,14.755));
    AddThruster(new Rocket(
      "Stage3Rocket", 
      new MathVector(12.997,0,0),
      this,
      new String[] {"Stage3Fuel"}, 
      new double[] {1},
      new MathVector(1,0,0),
      770.0/68.5, 
      2838, 
      2838
    ));
    double RCSX=14.25;
    AddProp(new GasPropRes("RCSFuel", 10, Nitrogen, new MathVector(14.0,0,0),0.1,1));
    MathVector RCSL=new MathVector(RCSX, Stage3ROuter,0);
    MathVector RCSR=new MathVector(RCSX,-Stage3ROuter,0);
    MathVector RCST=new MathVector(RCSX,0, Stage3ROuter);
    MathVector RCSB=new MathVector(RCSX,0,-Stage3ROuter);
    String[] Tank=new String[] {"RCSFuel"};
    double[] Mix=new double[] {1.0};
    double RCSIsp=68*9.81;
    double RCSFlow=0.1;
    RCSLPZ=AddThruster(new Rocket("RCSLPZ",RCSL,this,Tank,Mix,new MathVector( 0, 0, 1),RCSFlow,RCSIsp,RCSIsp));
    RCSLMZ=AddThruster(new Rocket("RCSLMZ",RCSL,this,Tank,Mix,new MathVector( 0, 0,-1),RCSFlow,RCSIsp,RCSIsp));
    RCSLPX=AddThruster(new Rocket("RCSLPX",RCSL,this,Tank,Mix,new MathVector( 1, 0, 0),RCSFlow,RCSIsp,RCSIsp));
    RCSLMX=AddThruster(new Rocket("RCSLMX",RCSL,this,Tank,Mix,new MathVector(-1, 0, 0),RCSFlow,RCSIsp,RCSIsp));
    RCSRPZ=AddThruster(new Rocket("RCSRPZ",RCSR,this,Tank,Mix,new MathVector( 0, 0, 1),RCSFlow,RCSIsp,RCSIsp));
    RCSRMZ=AddThruster(new Rocket("RCSRMZ",RCSR,this,Tank,Mix,new MathVector( 0, 0,-1),RCSFlow,RCSIsp,RCSIsp));
    RCSRPX=AddThruster(new Rocket("RCSRPX",RCSR,this,Tank,Mix,new MathVector( 1, 0, 0),RCSFlow,RCSIsp,RCSIsp));
    RCSRMX=AddThruster(new Rocket("RCSRMX",RCSR,this,Tank,Mix,new MathVector(-1, 0, 0),RCSFlow,RCSIsp,RCSIsp));
    RCSTPX=AddThruster(new Rocket("RCSTPX",RCST,this,Tank,Mix,new MathVector( 1, 0, 0),RCSFlow,RCSIsp,RCSIsp));
    RCSTMX=AddThruster(new Rocket("RCSTMX",RCST,this,Tank,Mix,new MathVector(-1, 0, 0),RCSFlow,RCSIsp,RCSIsp));
    RCSBPX=AddThruster(new Rocket("RCSBPX",RCSB,this,Tank,Mix,new MathVector( 1, 0, 0),RCSFlow,RCSIsp,RCSIsp));
    RCSBMX=AddThruster(new Rocket("RCSBMX",RCSB,this,Tank,Mix,new MathVector(-1, 0, 0),RCSFlow,RCSIsp,RCSIsp));
    double Stage3AeroAft=13.541;
    double Stage3AeroFwd=14.755;
    AddMass(InertSixDOFMass.MakeCylinderShell( 
      "Stage3Inert", 
      126, 
      Stage3ROuter,
      Stage3RMid, 
      13.541, 
      14.755
    ));
    AddActuator(new FlatPlateAirfoil(
      "Stage3HAero", 
      new MathVector((Stage1AeroAft+Stage1AeroFwd)/2,0,0),
      new MathVector(0,0,1),
      new MathVector(1,0,0), 
      Stage3AeroFwd-Stage3AeroAft,  //Chord
      Stage3ROuter*StageAeroWeight  //Span
, 0.25
    ));
    if(useVAero) {
      AddActuator(new FlatPlateAirfoil(
        "Stage3VAero", 
        new MathVector((Stage1AeroAft+Stage1AeroFwd)/2,0,0),
        new MathVector(0,1,0),
        new MathVector(1,0,0), 
        Stage3AeroFwd-Stage3AeroAft,  //Chord
        Stage3ROuter*StageAeroWeight  //Span
, 0.25
      ));
    }      
    //Fairing
    AddMass(InertSixDOFMass.MakeCylinderShell(
      "FairingInert", 
      301, 
      Stage1ROuter,
      Stage1RMid, 
      12.997, 
      14.755
    ));
    AddActuator(new FlatPlateAirfoil(
      "FairingHAero", 
      new MathVector(13,0,0),
      new MathVector(0,0,1),
      new MathVector(1,0,0), 
      6,                            //Chord
      Stage1ROuter*StageAeroWeight  //Span
, 0.25
    ));
    if(useVAero) {
      AddActuator(new FlatPlateAirfoil(
        "FairingVAero", 
        new MathVector(13,0,0),
        new MathVector(0,1,0),
        new MathVector(1,0,0), 
        6,                            //Chord
        Stage1ROuter*StageAeroWeight  //Span
, 0.25
      ));
    }    
    //Payload
    AddMass(new InertSixDOFMass(
      "PayloadInert",
      233, 
      new MathVector(14,0,0),
      SixDOFMass.SolidCylinderI(233, Stage3ROuter, 1)
    ));
    AddActuator(new FlatPlateAirfoil(
      "PayloadHAero", 
      new MathVector(14,0,0),
      new MathVector(0,0,1),
      new MathVector(1,0,0), 
      2,   //Chord
      Stage3ROuter*2*StageAeroWeight //Span
, 0.25
    ));
    if(useVAero) {
      AddActuator(new FlatPlateAirfoil(
        "PayloadVAero", 
        new MathVector(14,0,0),
        new MathVector(0,1,0),
        new MathVector(1,0,0), 
        2,   //Chord
        Stage3ROuter*2*StageAeroWeight //Span
, 0.25
      ));
    }    
    
    //Turn off inactive actuators (ones under the fairing)
    Stage3HAero.Active=false;
    PayloadHAero.Active=false;
    if(useVAero) {
      Stage3VAero.Active=false;
   	  PayloadVAero.Active=false;
    }
    RCSLPX.Active=false;
    RCSLMX.Active=false;
    RCSLPZ.Active=false;
    RCSLMZ.Active=false;
    RCSRPX.Active=false;
    RCSRMX.Active=false;
    RCSRPZ.Active=false;
    RCSRMZ.Active=false;
    RCSTPX.Active=false;
    RCSTMX.Active=false;
    RCSBPX.Active=false;
    RCSBMX.Active=false;
  }
/*
  public Quaternion Navigate(double T, SixDOFState RVEw, MathVector FuelLevels, boolean IsMajor) {
    Quaternion Target;
    double PitchDesired;
    double TargetPitch=GuidanceCommand;
    if(T<5) {
      PitchDesired=0;
    } else if (T<20) {
      //Pull up gradually. Only do this after Stage1 engine is lit, or you will 
      //probably stall.
      PitchDesired=Scalar.linterp(5,0,20,-TargetPitch,T);
    } else if (T<40) {
      //Keep nose up.
      PitchDesired=-TargetPitch;
    } else {
      MathVector VNorm=RVEw.V();
      VNorm.sub(U.getWind(T,RVEw));
      VNorm.normalize();
      double VelPitch=90-toDegrees(MathVector.vangle(VNorm,U.LocalVertical(T,RVEw)));
      if (T<50) {
        //Smoothly transition from target pitch to zero AoA
        PitchDesired=Scalar.linterp(40,-TargetPitch,50,-VelPitch,T);
      } else {
        //Zero AoA
        PitchDesired=-VelPitch;
      }
    }
    Quaternion SA=StraightAhead(T,RVEw,true);
    if(IsMajor)Record(T,"PitchDesired", "deg", PitchDesired);
    Quaternion SAPlus=Quaternion.AnA(new MathVector(0, 1,0),toRadians(PitchDesired));
    Target=Quaternion.mul(SA,SAPlus);
    return Target;
  }
*/
  private void DropStage1() {
    Stage1Fuel.Active=false;  
    Stage1Inert.Active=false; 
    Stage1Rocket.Active=false;
    Stage1HAero.Active=false;
    if(Stage1VAero!=null)Stage1VAero.Active=false; 
    LFin.Active=false; 
    RFin.Active=false; 
    VFin.Active=false; 
    LWing.Active=false; 
    RWing.Active=false; 
  }
  private void DropInterstage() {
    InterstageInert.Active=false; 
    InterstageHAero.Active=false;
    if(InterstageVAero!=null)InterstageVAero.Active=false;
  }
  private void DropFairing() {
    FairingInert.Active=false;   
    FairingHAero.Active=false;
    FairingVAero.Active=false;
    Stage3HAero.Active=true; //Stage3 Aero uncovered
    PayloadVAero.Active=true; //Payload Aero uncovered
    Stage3HAero.Active=true; //Stage3 Aero uncovered
    PayloadVAero.Active=true; //Payload Aero uncovered
    RCSLPX.Active=true; //RCS uncovered
    RCSLMX.Active=true;
    RCSLPZ.Active=true;
    RCSLMZ.Active=true;
    RCSRPX.Active=true;
    RCSRMX.Active=true;
    RCSRPZ.Active=true;
    RCSRMZ.Active=true;
    RCSTPX.Active=true;
    RCSTMX.Active=true;
    RCSBPX.Active=true;
    RCSBMX.Active=true;
  }
  private void DropStage2() {
    Stage2Fuel.Active=false;  
    Stage2Inert.Active=false; 
    Stage2Rocket.Active=false;
    Stage2HAero.Active=false;
    if(Stage2VAero!=null)Stage1VAero.Active=false; 
	  
  }
  private void DropStage3() {
    Stage2Fuel.Active=false;  
    Stage2Inert.Active=false; 
    Stage2Rocket.Active=false;
    Stage2HAero.Active=false;
    if(Stage2VAero!=null)Stage2VAero.Active=false; 
  }

  public void Discrete(double T, SixDOFState RVEw, MathVector FuelLevels) {
    //Drop stages as needed
	if(T>95 && Stage1Inert.Active) DropStage1();
	if(T>96 && InterstageInert.Active) DropInterstage();
	if(T>105 && FairingInert.Active) DropFairing();
	//Command the main engines (These burn to depletion, no need to turn off)
    Stage1Rocket.Throttle=(T>5)?1.0:0.0;
    Stage2Rocket.Throttle=(T>95.3)?1.0:0.0;
    Stage3Rocket.Throttle=(T>600)?1.0:0.0;
	
  }
  public void Steer(double T, SixDOFState RVEw, boolean IsMajor, double PitchCommand, double YawCommand, double RollCommand) {

    //Command the steering actuators
    if(Stage1Inert.Active) { //If we still have the first stage...
      //Shake a tail feather!
      double FinStop=toRadians(45);
      double LFinCommand=PitchCommand-RollCommand;
      double RFinCommand=PitchCommand+RollCommand;
      double VFinCommand=YawCommand;//+RollCommand;
      //Stop locations for fins, in radians
      if(abs(LFinCommand)>FinStop) LFinCommand=FinStop*((LFinCommand>0)?1.0:-1.0);
      if(abs(RFinCommand)>FinStop) RFinCommand=FinStop*((RFinCommand>0)?1.0:-1.0);
      if(abs(VFinCommand)>FinStop) VFinCommand=FinStop*((VFinCommand>0)?1.0:-1.0);
      LFin.setAirfoilAngle(LFinCommand);
      if(IsMajor)Record(T, "LFinCmd", "deg", toDegrees(LFinCommand));
      RFin.setAirfoilAngle(RFinCommand);
      if(IsMajor)Record(T, "RFinCmd", "deg", toDegrees(RFinCommand));
      VFin.setAirfoilAngle(VFinCommand);
      if(IsMajor)Record(T, "VFinCmd", "deg", toDegrees(VFinCommand));
      return;
    } 
    if (Stage2Fuel.Level>0) {
      //Use the second stage TVC to maneuver
      double TVCStop=tan(toRadians(3));
      if(abs(PitchCommand)>TVCStop) PitchCommand=TVCStop*((PitchCommand>0)?1.0:-1.0);
      if(abs(YawCommand)>TVCStop) YawCommand=TVCStop*((YawCommand>0)?1.0:-1.0);
      Stage2Rocket.setDirection(new MathVector(1, YawCommand, PitchCommand).normal());
      if(IsMajor)Record(T,"YawTVC", "deg", toDegrees(atan(YawCommand)));
      if(IsMajor)Record(T,"PitchTVC", "deg", toDegrees(atan(PitchCommand)));
    }
  }
  public static Elements DoLaunch(double GuidanceCommand, double StageAeroWeight, double Azimuth, ChartRecorder C) throws IOException {
    Planet E=new SimpleEarth();
    PegasusXL Peg;
    int fps=24;
    MathVector R0,V0;
    if(true) {
      Peg=new PegasusXL(new TwoBody(E),GuidanceCommand,StageAeroWeight);
      Peg.C=C;
      R0=E.lla2xyz(toRadians(36),toRadians(-123),12000);  //Initial position
      V0=E.RelHdg2vxyz(R0,250,toRadians(Azimuth));
    } else {
      Peg=new PegasusXL(new FlatEarth(9.80665,new KwanEarthAtmosphere()),GuidanceCommand,StageAeroWeight);
      Peg.C=C;
      R0=new MathVector(0,0,12000);  //Initial position
      V0=new MathVector(250,0,0);
    }
    MathState RV0=new MathState(R0,V0);
    Quaternion E0=
//      Quaternion.mul(
      Peg.G.StraightAhead(0,RV0,true)
//      ,new Quaternion(new AxisAngle(new MathVector(1,0,0), toRadians(180)))
//    )
    ;
    MathVector w0=new MathVector(0,0,0);
    Integrator I=new RungeKutta(
      0, 
      new MathVector(
        new MathVector[] {
          R0,            //Position
          V0,            //Velocity
          E0.toVector(), //Orientation
          w0,            //Angular Velocity
          new MathVector(new double[]{1.0,1.0,1.0,1.0}) //Prop Levels
        }
      ), 
      1.0/((double)(fps)),
      Peg
    );
    int NumSteps;
    NumSteps=520*fps;
    for(int i=0;i<NumSteps;i++) {
      if(C!=null) {
        SixDOFState RVEw=new SixDOFState(I.getX());
        C.Record(I.getT(),"Altitude","km",Peg.U.Altitude(I.getT(),RVEw)/1000.0);
        C.Record(I.getT(),"Speed","km/s",RVEw.V().length()/1000.0);
        C.Record(I.getT(),"Position","m",RVEw.R());
        C.Record(I.getT(),"Velocity","m/s",RVEw.V());
        C.Record(I.getT(),"Orientation","deg",RVEw.E().toAnA());
        C.Record(I.getT(),"Nose", null,RVEw.E().Body2Inertial(new MathVector(1,0,0)));
        C.Record(I.getT(),"Tail", null,RVEw.E().Body2Inertial(new MathVector(0,0,1)));
        C.Record(I.getT(),"AngularRate","rad/s",RVEw.w());
        C.Record(I.getT(),"TotalCoM","kg,m",Peg.EquivalentMass(I.getT(), RVEw).CoM);
      }
      if(true) {
        if(i % (fps*10) == 0) {
          System.out.println(I.getT());
        } else if(i % fps == 0) {
          System.out.println(".");
        }
      }
      I.step(); 
    }
    if(C!=null) {
      C.EndOfData();
      int SegLength=10*fps;
      for (int j=0;j<52;j++) {
        System.out.println("POV Output segment "+j);
        PrintWriter Inc=new PrintWriter(new FileWriter("PegasusState"+j+".inc"));
        for(int i=0;i<Peg.Actuators.length;i++) {
          Inc.println("#declare "+Peg.Actuators[i].Name+"CoF=<"+Peg.Actuators[i].CoF+">;");
        }
        C.PrintSubTable(j*SegLength,SegLength,new String[] {"Nose"}, new POVColumnPrinter(Inc));
        C.PrintSubTable(j*SegLength,SegLength,new String[] {"Tail"}, new POVColumnPrinter(Inc));
        C.PrintSubTable(j*SegLength,SegLength,new String[] {"Position"}, new POVColumnPrinter(Inc));
        C.PrintSubTable(j*SegLength,SegLength,new String[] {"Velocity"}, new POVColumnPrinter(Inc));
        C.PrintSubTable(j*SegLength,SegLength,new String[] {"LFinCmd"}, new POVColumnPrinter(Inc));
        C.PrintSubTable(j*SegLength,SegLength,new String[] {"RFinCmd"}, new POVColumnPrinter(Inc));
        C.PrintSubTable(j*SegLength,SegLength,new String[] {"VFinCmd"}, new POVColumnPrinter(Inc));
        C.PrintSubTable(j*SegLength,SegLength,new String[] {"TotalCoM"}, new POVColumnPrinter(Inc));
//        for(int i=0;i<Peg.Actuators.length;i++) {
//          C.PrintSubTable(j*SegLength,SegLength,new String[] {Peg.Actuators[i].Name+"Force"}, new POVColumnPrinter(Inc));
//        }
        Inc.close();
      }
    }
    SixDOFState RVEw=new SixDOFState(I.getX()); 
    MathStateTime Epoch=new MathStateTime(RVEw.R(),RVEw.V(),new Time(0,TimeUnits.Seconds)); 
    return new Elements(Epoch, Planet.Earth.GM, "m");
  }
  public static double getAlt(double GuidanceCommand, double StageAeroWeight, ChartRecorder C)  throws IOException {
    Elements E=DoLaunch(GuidanceCommand, StageAeroWeight, 192.84281886274206,C);
    double Alt=E.Apoapse-Planet.Earth.R;
    return Alt;
  }
  public static double getDVDiff(final double LStageAeroWeight, final ChartRecorder C) throws IOException {
    RootFind R=new Crenshaw(new RootFunction() {
      public double F(double X) {
        try {
          Time T1=new Time(TimeUnits.Seconds);
          System.out.println("Angle:   "+X);
          double Alt=getAlt(X, LStageAeroWeight,C);
          System.out.println("Alt: "+Alt);
          Time T2=new Time(TimeUnits.Seconds);
          System.out.println("Time: "+(T2.get()-T1.get()));
          return Alt;
        } catch(Throwable E) {throw new RuntimeException("Blah!", E);}
      }
    });
    double Converge=R.Find(600000,38.0,50.0);
    System.out.println(Converge);
    Elements E=DoLaunch(Converge, LStageAeroWeight, 192.84281886274206,C);
    double Va=E.VelocityFromRadius(E.Apoapse);
    double Vcirc=sqrt(E.GM/E.Apoapse);
    double DVNeeded=Vcirc-Va;
    PegasusXL Peg=new PegasusXL(null, 0, LStageAeroWeight);
    SixDOFMass Stage3StackInert=InertSixDOFMass.CombineMass(
      "Stage3Inert", 
      new SixDOFMass[] {
        Peg.Masses[Stage3InertIdx],
        Peg.Masses[RCSFuelIdx],
        Peg.Masses[PayloadInertIdx],
      }, 
      0, 
      null
    );
    double Stage3FuelMass=Peg.Masses[Stage3FuelIdx].getMass(0,null);
    double Stage3StackInertMass=Stage3StackInert.getMass(0,null);
    double Stage3TotalMass=Stage3StackInertMass+Stage3FuelMass;
    double DVAvailable=((Rocket)Peg.Actuators[Stage3RocketIdx]).Isp0*Math.log(Stage3TotalMass/Stage3StackInertMass);
    return DVAvailable-DVNeeded;
  }
  public static void main(String[] args) throws IOException {
    if(false) {
	  RootFind R=new Crenshaw(new RootFunction() {
      public double F(double X) {
        Time T1=new Time(TimeUnits.Seconds);
        System.out.println("Weight:   "+X);
        double DVDiff;
        try {DVDiff=getDVDiff(X,null);} catch (Throwable E) {throw new RuntimeException("Blah!", E);}
        System.out.println("DVDiff: "+DVDiff);
        Time T2=new Time(TimeUnits.Seconds);
        System.out.println("MTime: "+(T2.get()-T1.get()));
        return DVDiff;
      }
    });
      double Converge=R.Find(0,0,1.6);
    }
    
    if(false) {
    RootFind S=new Crenshaw(new RootFunction() {
      public double F(double X) {
        System.out.println("Azimuth: "+X);
        Elements E;
        try{E=DoLaunch(44.7,0.6, X, null);} catch (Throwable Q) {throw new RuntimeException("Blah!", Q);}
        System.out.println("Inclination: "+toDegrees(E.I));
        System.out.println(E);
        return toDegrees(E.I);
      }
    });
    double Converge=S.Find(97.7876,180,200);
    }
    ChartRecorder C=new ArrayListChartRecorder(52000,50);
    Elements E=DoLaunch(48.60545380626142, 1.5472919252937463, 192.84281886274206,C);
    System.out.println(E);
    C.EndOfData();
    C.PrintSubTable(new String[] {
      "PitchDesired",
      "PitchError","YawError","RollError",
      "LFinCmd","RFinCmd","VFinCmd",
      "Altitude",
      "PitchTVC","YawTVC"
    },new DisplayPrinter());
//    C.PrintSubTable(new String[] {"Equivalent Mass","Left FinForceTorque","Right FinForceTorque","Vertical FinForceTorque","Main WingForceTorque","FairingForceTorque","Stage1ForceTorque","TotalForceTorque","Position","Velocity","Orientation","Angular Rate"},new HTMLPrinter("PegTlm.html"));
  }
}
