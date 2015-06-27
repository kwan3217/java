package org.kwansystems.space.gear;

import org.kwansystems.space.gear.actuator.Actuator;
import org.kwansystems.space.gear.actuator.Rocket;
import org.kwansystems.space.gear.actuator.ThrustTableRocket;
import org.kwansystems.space.gear.actuator.Thruster;
import org.kwansystems.space.gear.mass.LiquidPropRes;
import org.kwansystems.space.gear.mass.PropRes;
import org.kwansystems.space.gear.mass.SixDOFMass;
import org.kwansystems.space.kepler.*;
import org.kwansystems.space.planet.*;
import org.kwansystems.space.planet.atmosphere.*;
import org.kwansystems.space.universe.*;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.integrator.*;
import org.kwansystems.tools.rootfind.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.table.*;
import org.kwansystems.tools.vector.*;
import java.io.*;
import java.util.*;
import static java.lang.Math.*;

public class AtlasV extends SixDOFVehicle {
  public AtlasV(Universe LU) {
    super(LU);
    //Control law constants
    //Elements end up being <Roll,Pitch,Yaw>
    //Proportional and damping coeffs should have opposite signs
    //Positive roll coeff, negative pitch, positive yaw
    //Go lots easier on coeff to adapt to lesser I around roll
    Kp=new MathVector(3,-50,  2);
    Kd=new MathVector(-0.5,5,-1);
    //Set up arrays
    ArrayList<PropRes>  PropA=new ArrayList<PropRes>();
    ArrayList<SixDOFMass>     NonPropA=new ArrayList<SixDOFMass>();
    ArrayList<Thruster> ThrusterA=new ArrayList<Thruster>();
    ArrayList<Actuator> NonThrusterA=new ArrayList<Actuator>();
    

    
    //Propellant Constants
    double RP1Mix=2.72; //Oxidizer to fuel ratio
    double LH2Mix=6; //Oxidizer to fuel ratio
    double RP1Dens=810;       //kg/m^3
    double LOXDens=1140;      //kg/m^3
    double LH2Dens=71;        //kg/m^3
    
    //Atlas constants
    double AtlasROuter=3.81/2;
    double AtlasRMid=3.80/2;
    double AtlasAeroAft=0;
    double AtlasAeroFwd=32.46;
    double AtlasThrustPt=0;
    double AtlasPropMass=284089; //kg
    double AtlasRP1Mass=AtlasPropMass*1/(RP1Mix+1);
    double AtlasLOXMass=AtlasPropMass*RP1Mix/(RP1Mix+1);
    double AtlasRP1Vol=AtlasRP1Mass/RP1Dens;
    double AtlasRP1Area=Math.PI*AtlasRMid*AtlasRMid;
    double AtlasRP1Len=AtlasRP1Vol/AtlasRP1Area;
    double AtlasRP1Aft=3;
    double AtlasLOXVol=AtlasLOXMass/LOXDens;
    double AtlasLOXArea=Math.PI*AtlasRMid*AtlasRMid;
    double AtlasLOXLen=AtlasLOXVol/AtlasLOXArea;
    double AtlasLOXAft=AtlasRP1Aft+AtlasRP1Len+1;
    double AtlasInertMass=21336;
    double AtlasEngineMass=5393;
    double AtlasTankMass=AtlasInertMass-AtlasEngineMass;
    
    //SRB Constants
    double SRBROuter=1.55/2;
    double SRBRInner=1.54/2;
    double SRBAft=0;
    double SRBLen=19.5;
    double SRBFwd=SRBAft+SRBLen;
    double SRBTotalMass=46559;
    double SRBEmptyMass=4000;
    double SRBPropMass=SRBTotalMass-SRBEmptyMass;
    double SRBOutboard=SRBROuter+AtlasROuter;
    
    //CBC Cylindrical ISA
    double CBCCylISAAft=AtlasAeroFwd;
    double CBCCylISALen=0.32;
    double CBCCylISAFwd=CBCCylISAAft+CBCCylISALen;
    double CBCCylISAR=AtlasROuter;
    double CBCCylISAMass=282;
    
    //Centaur ISA
    double CentaurISAAft=CBCCylISAFwd;
    double CentaurISALen=4.46;
    double CentaurISAFwd=CentaurISAAft+CentaurISALen;
    double CentaurISAR=AtlasROuter;
    double CentaurISAMass=2292;
    
    //Short Fairing
    double FairingAft=CentaurISAFwd;
    double FairingLen=20.7;
    double FairingFwd=FairingAft+FairingLen;
    double FairingR=5.4/2;
    double FairingMass=3540;

    //Centaur constants
    double CentaurROuter=3.05/2;
    double CentaurRMid=3.04/2;
    double CentaurAft=FairingAft-3;
    double CentaurLen=12.68;
    double CentaurFwd=CentaurAft+CentaurLen;
    double CentaurPropMass=20830; //kg
    double CentaurLH2Mass=CentaurPropMass*1/(LH2Mix+1);
    double CentaurLOXMass=CentaurPropMass*LH2Mix/(LH2Mix+1);
    double CentaurLH2Vol=CentaurLH2Mass/LH2Dens;
    double CentaurLH2Area=Math.PI*CentaurRMid*CentaurRMid;
    double CentaurLH2Len=CentaurLH2Vol/CentaurLH2Area;
    double CentaurLOXVol=CentaurLOXMass/LOXDens;
    double CentaurLOXArea=Math.PI*CentaurRMid*CentaurRMid;
    double CentaurLOXLen=CentaurLOXVol/CentaurLOXArea;
    double CentaurInertMass=2138+50; //(For the C22)
    double CentaurEngineMass=167;
    double CentaurTankMass=CentaurInertMass-CentaurEngineMass;
    
    //Star48 Constants
    double Star48ROuter=49.0/39.37/2.0;
    double Star48RInner=48.0/39.37/2.0;
    double Star48Aft=CentaurFwd;
    double Star48Len=80.0/39.37;
    double Star48Fwd=Star48Aft+Star48Len;
    double Star48TotalMass=2141.3+34.4;
    double Star48EmptyMass=116.9+34.4; //(for the B1194)
    double Star48PropMass=Star48TotalMass-Star48EmptyMass;

    //Stage 1
    PropRes AtlasRP1=new LiquidPropRes("Stage1Fuel",15014,2350,Stage1RMid, Stage1FuelAft,Stage1FuelFwd);
    PropRes AtlasLOX=new LiquidPropRes("Stage1Fuel",15014,2350,Stage1RMid, Stage1FuelAft,Stage1FuelFwd);
    PropA.add(AtlasRP1); 
    Rocket Stage1Rocket=new ThrustTableRocket(
      "Stage1Rocket", 
      new MathVector(Stage1AeroAft,0,0),
      new int[] {PropA.size()-1}, 
      new double[] {1.0},
      new MathVector(1,0,0),
      15014.0/68.6,
      2903, 
      2903,
      new LinearTable(
        new double[] {5.0,15.0,20.0,73.6},
        new double[] {2.0, 2.0, 2.0, 1.0}
      )
    );
    ThrusterA.add(Stage1Rocket);
    /*
    double FinLength=1.542; 
    double FinAft=0.712;
    double FinMid=1.183;
    double FinFore=2.32;
    double FinThickness=0.2;
    double FinOut=0.64;
    double FinChord=(FinMid-FinAft)+(FinFore-FinMid)/2;
    double FinCoLX=FinAft+FinChord*0.75;
    double FinCoLZ=FinOut+FinLength/3.0;
    //Don't use PegasusXLFin, as that wing stalls which will confuse the control
    //mechanism to no end.
    double FinAngle=toRadians(23);
    double FinC=cos(FinAngle);
    double FinS=sin(FinAngle);
    NonThrusterA.add(new FlatPlateAirfoil(
      "LFin",
      new MathVector(FinCoLX,FinCoLZ*FinC,-FinCoLZ*FinS),
      new MathVector(0,FinS,FinC), //Normal
      new MathVector(1,0,0),       //ChordLine
      FinChord,                    //Chord
      FinLength                    //Span
    ));
    NonThrusterA.add(new FlatPlateAirfoil(
      "RFin",
      new MathVector(FinCoLX,-FinCoLZ*FinC,-FinCoLZ*FinS),
      new MathVector(0,-FinS,FinC),//Normal
      new MathVector(1,0,0),       //ChordLine
      FinChord,                    //Chord
      FinLength                    //Span
    ));
    NonThrusterA.add(new FlatPlateAirfoil(
      "VFin",
      new MathVector(FinCoLX,0,FinCoLZ),
      new MathVector(0,1,0),       //Normal
      new MathVector(1,0,0),       //ChordLine
      1.5,                         //Chord
      1.5                          //Span
    ));

    double FinBoxVolume=FinLength*(FinMid-FinAft)*FinThickness;
    double FinTriVolume=FinLength*(FinFore-FinMid)*FinThickness/2;
    double FinVolume=FinBoxVolume+FinTriVolume;
    double FinMass=31;
    double FinBoxMass=FinMass*FinBoxVolume/FinVolume;
    double FinTriMass=FinMass*FinTriVolume/FinVolume;
    SixDOFMass FinBox=new InertSixDOFMass(
      "FinBox",
      FinBoxMass,
      new MathVector(FinAft+(FinMid-FinAft)/2,FinLength/2+FinOut,0),
      SixDOFMass.RectangularPrismI(FinBoxMass,FinMid-FinAft, FinLength,FinThickness)
    );
    SixDOFMass FinTri=new InertSixDOFMass(
      "FinTri",
      FinTriMass,
      new MathVector(FinMid+(FinFore-FinMid)/3,FinLength/2+FinOut,0),
      SixDOFMass.TriangularPrismI(FinTriMass,FinFore-FinMid, FinLength/2,FinThickness)
    );
    InertSixDOFMass RFin=InertSixDOFMass.CombineMass(
      "RFin",
      new SixDOFMass[] {FinBox,FinTri},
      0,
      null
    );
    InertSixDOFMass LFin=new InertSixDOFMass("LFin",RFin);
    InertSixDOFMass VFin=new InertSixDOFMass("VFin",RFin);
    RFin.Rotate(MathMatrix.Rot1d(FinAngle));
    LFin.Rotate(MathMatrix.Rot1d(180-FinAngle));
    VFin.Rotate(MathMatrix.Rot1d(-90));
    InertSixDOFMass Fins=InertSixDOFMass.CombineMass(
      "Fins",
      new SixDOFMass[] {LFin,RFin,VFin},
      0,
      null
    );
    double MainWingSpan=6.7;
    double MainWingAft=5.7;
    double MainWingMid=6.00;
    double MainWingFore=9.70;
    double MainWingThickness=0.2;
    double MainWingZ=Stage1ROuter+MainWingThickness/2;
    double MainWingChord=(MainWingMid-MainWingAft)+(MainWingFore-MainWingMid)/2;
    double MainWingCoL=MainWingAft+MainWingChord*0.75;
    NonThrusterA.add(new PegasusXLWing(
      "LWing", 
      new MathVector(MainWingCoL,MainWingSpan/6,MainWingZ),       
      new MathVector(0,0,1),
      new MathVector(1,0,0), 
      MainWingChord,               //Chord
      MainWingSpan/2                 //Span
    ));
    NonThrusterA.add(new PegasusXLWing(
      "RWing", 
      new MathVector(MainWingCoL,-MainWingSpan/6,MainWingZ),       
      new MathVector(0,0,1),
      new MathVector(1,0,0), 
      MainWingChord,               //Chord
      MainWingSpan/2                 //Span
    ));
    NonThrusterA.add(new FlatPlateAirfoil(
      "Stage1HAero", 
      new MathVector((Stage1AeroAft+Stage1AeroFwd)/2,0,0),
      new MathVector(0,0,1),
      new MathVector(1,0,0), 
      Stage1AeroFwd-Stage1AeroAft,   //Chord
      Stage1ROuter*2*StageAeroWeight //Span
    ));
    if(useVAero) {
      NonThrusterA.add(new FlatPlateAirfoil(
        "Stage1VAero", 
        new MathVector((Stage1AeroAft+Stage1AeroFwd)/2,0,0),
        new MathVector(0,1,0),
        new MathVector(1,0,0), 
        Stage1AeroFwd-Stage1AeroAft,   //Chord
        Stage1ROuter*2*StageAeroWeight //Span
      ));
    }
    double MainWingBoxVolume=MainWingSpan*(MainWingMid-MainWingAft)*MainWingThickness;
    double MainWingTriVolume=MainWingSpan*(MainWingFore-MainWingMid)*MainWingThickness/2;
    double MainWingVolume=MainWingBoxVolume+MainWingTriVolume;
    double MainWingMass=450;
    double MainWingSaddleMass=450;
    double MainWingBoxMass=MainWingMass*MainWingBoxVolume/MainWingVolume;
    double MainWingTriMass=MainWingMass*MainWingTriVolume/MainWingVolume;
    SixDOFMass MainWingBox=new InertSixDOFMass(
      "MainWingBox",
      MainWingBoxMass,
      new MathVector(MainWingAft+(MainWingMid-MainWingAft)/2,0,MainWingZ),
      SixDOFMass.RectangularPrismI(MainWingBoxMass,MainWingMid-MainWingAft, MainWingSpan,MainWingThickness)
    );
    SixDOFMass MainWingRight=new InertSixDOFMass(
      "MainWingRight",
      MainWingTriMass/2,
      new MathVector(MainWingMid+(MainWingFore-MainWingMid)/3,MainWingSpan/6,MainWingZ),
      SixDOFMass.TriangularPrismI(MainWingTriMass/2,MainWingFore-MainWingMid, MainWingSpan/2,MainWingThickness)
    );
    SixDOFMass MainWingLeft=new InertSixDOFMass(
      "MainWingLeft",
      MainWingTriMass/2,
      new MathVector(MainWingMid+(MainWingFore-MainWingMid)/3,-MainWingSpan/6,MainWingZ),
      SixDOFMass.TriangularPrismI(MainWingTriMass/2,MainWingFore-MainWingMid, -MainWingSpan/2,MainWingThickness)
    );
    SixDOFMass MainWingSaddle=new InertSixDOFMass(
      "MainWingSaddle",
      MainWingMass/2,
      new MathVector((MainWingAft+MainWingFore)/2, 0,Stage1ROuter/2),
      SixDOFMass.RectangularPrismI(MainWingSaddleMass, (MainWingFore-MainWingAft)/2, Stage1ROuter*2,Stage1ROuter)
    );
    InertSixDOFMass MainWing=InertSixDOFMass.CombineMass(
      "MainWing",
      new SixDOFMass[] {MainWingBox,MainWingLeft,MainWingRight,MainWingSaddle},
      0,
      null
    );
    InertSixDOFMass Stage1Case=InertSixDOFMass.MakeCylinderShell(
      "Stage1Case", 
      1369, 
      Stage1ROuter,
      Stage1RMid, 
      Stage1AeroAft, 
      Stage1AeroFwd
    );
    SixDOFMass Stage1Inert=InertSixDOFMass.CombineMass(
      "Stage1Inert",
      new SixDOFMass[] {Fins,MainWing,Stage1Case},
      0,
      null
    );
    NonPropA.add(Stage1Inert);

    //Interstage
    SixDOFMass Interstage=InertSixDOFMass.MakeCylinderShell(
      "InterstageInert", 
      151, 
      Stage1ROuter,
      Stage1RMid, 
      Stage1AeroFwd, 
      Stage2AeroAft
    );
    NonPropA.add(Interstage);
    NonThrusterA.add(new FlatPlateAirfoil(
      "InterstageHAero", 
      new MathVector((Stage1AeroFwd+Stage2AeroFwd)/2,0,0),
      new MathVector(0,0,1),
      new MathVector(1,0,0), 
      Stage2AeroAft-Stage1AeroFwd,   //Chord
      Stage1ROuter*2*StageAeroWeight //Span
    ));
    if(useVAero) {
      NonThrusterA.add(new FlatPlateAirfoil(
        "InterstageVAero", 
        new MathVector((Stage1AeroFwd+Stage2AeroFwd)/2,0,0),
        new MathVector(0,1,0),
        new MathVector(1,0,0), 
        Stage2AeroAft-Stage1AeroFwd,   //Chord
        Stage1ROuter*2*StageAeroWeight //Span
      ));
    }
    //Stage 2
    PropA.add(new SolidPropRes("Stage2Fuel",3925,2350,Stage1RMid, Stage2FuelAft, Stage2FuelFwd)); 
    Rocket Stage2Rocket=new Rocket(
      "Stage2Rocket", 
      new MathVector(Stage2ThrustPt,0,0),
      new int[] {PropA.size()-1}, 
      new double[] {1.0},
      new MathVector(1,0,0),
      3925.0/69.4,
      2858, 
      2858
    );
    ThrusterA.add(Stage2Rocket);
    SixDOFMass Stage2Inert=InertSixDOFMass.MakeCylinderShell(
      "Stage2Inert", 
      416, 
      Stage1ROuter,
      Stage1RMid, 
      10.81, 
      12.997
    );
    NonPropA.add(Stage2Inert);
    NonThrusterA.add(new FlatPlateAirfoil(
      "Stage2HAero", 
      new MathVector((Stage2AeroAft+Stage2AeroFwd)/2,0,0),
      new MathVector(0,0,1),
      new MathVector(1,0,0), 
      Stage2AeroFwd-Stage2AeroAft,  //Chord
      Stage1ROuter*StageAeroWeight  //Span
    ));
    if(useVAero) {
      NonThrusterA.add(new FlatPlateAirfoil(
        "Stage2VAero", 
        new MathVector((Stage2AeroAft+Stage2AeroFwd)/2,0,0),
        new MathVector(0,1,0),
        new MathVector(1,0,0), 
        Stage2AeroFwd-Stage2AeroAft,  //Chord
        Stage1ROuter*StageAeroWeight  //Span
      ));
    }
    //Stage 3
    double Stage3ROuter=0.97/2;
    double Stage3RMid=0.96/2;
    PropRes Stage3Fuel=new SolidPropRes("Stage3Fuel",770,2350,Stage1RMid, 13.541,14.755);
    PropA.add(Stage3Fuel); 
    Rocket Stage3Rocket=new Rocket(
      "Stage3Rocket", 
      new MathVector(12.997,0,0),
      new int[] {PropA.size()-1}, 
      new double[] {1.0},
      new MathVector(1,0,0),
      770.0/68.5, 
      2838, 
      2838
    );
    double RCSX=14.25;
    ThrusterA.add(Stage3Rocket);
    PropRes RCSFuel=new GasPropRes("RCSFuel", 10, new MathVector(14.0,0,0),0.1,1);
    PropA.add(RCSFuel);
    MathVector RCSL=new MathVector(RCSX, Stage3ROuter,0);
    MathVector RCSR=new MathVector(RCSX,-Stage3ROuter,0);
    MathVector RCST=new MathVector(RCSX,0, Stage3ROuter);
    MathVector RCSB=new MathVector(RCSX,0,-Stage3ROuter);
    int[] Tank=new int[] {PropA.size()-1};
    double[] Mix=new double[] {1.0};
    double RCSIsp=68*9.81;
    double RCSFlow=0.1;
    Rocket RCSLPZ=new Rocket("RCSLPZ",RCSL,Tank,Mix,new MathVector( 0, 0, 1),RCSFlow,RCSIsp,RCSIsp);ThrusterA.add(RCSLPZ);
    Rocket RCSLMZ=new Rocket("RCSLMZ",RCSL,Tank,Mix,new MathVector( 0, 0,-1),RCSFlow,RCSIsp,RCSIsp);ThrusterA.add(RCSLMZ);
    Rocket RCSLPX=new Rocket("RCSLPX",RCSL,Tank,Mix,new MathVector( 1, 0, 0),RCSFlow,RCSIsp,RCSIsp);ThrusterA.add(RCSLPX);
    Rocket RCSLMX=new Rocket("RCSLMX",RCSL,Tank,Mix,new MathVector(-1, 0, 0),RCSFlow,RCSIsp,RCSIsp);ThrusterA.add(RCSLMX);
    Rocket RCSRPZ=new Rocket("RCSRPZ",RCSR,Tank,Mix,new MathVector( 0, 0, 1),RCSFlow,RCSIsp,RCSIsp);ThrusterA.add(RCSRPZ);
    Rocket RCSRMZ=new Rocket("RCSRMZ",RCSR,Tank,Mix,new MathVector( 0, 0,-1),RCSFlow,RCSIsp,RCSIsp);ThrusterA.add(RCSRMZ);
    Rocket RCSRPX=new Rocket("RCSRPX",RCSR,Tank,Mix,new MathVector( 1, 0, 0),RCSFlow,RCSIsp,RCSIsp);ThrusterA.add(RCSRPX);
    Rocket RCSRMX=new Rocket("RCSRMX",RCSR,Tank,Mix,new MathVector(-1, 0, 0),RCSFlow,RCSIsp,RCSIsp);ThrusterA.add(RCSRMX);
    Rocket RCSTPX=new Rocket("RCSTPX",RCST,Tank,Mix,new MathVector( 1, 0, 0),RCSFlow,RCSIsp,RCSIsp);ThrusterA.add(RCSTPX);
    Rocket RCSTMX=new Rocket("RCSTMX",RCST,Tank,Mix,new MathVector(-1, 0, 0),RCSFlow,RCSIsp,RCSIsp);ThrusterA.add(RCSTMX);
    Rocket RCSBPX=new Rocket("RCSBPX",RCSB,Tank,Mix,new MathVector( 1, 0, 0),RCSFlow,RCSIsp,RCSIsp);ThrusterA.add(RCSBPX);
    Rocket RCSBMX=new Rocket("RCSBMX",RCSB,Tank,Mix,new MathVector(-1, 0, 0),RCSFlow,RCSIsp,RCSIsp);ThrusterA.add(RCSBMX);
    double Stage3AeroAft=13.541;
    double Stage3AeroFwd=14.755;
    SixDOFMass Stage3Inert=InertSixDOFMass.MakeCylinderShell( 
      "Stage3Inert", 
      126, 
      Stage3ROuter,
      Stage3RMid, 
      13.541, 
      14.755
    );
    NonPropA.add(Stage3Inert);
    NonThrusterA.add(new FlatPlateAirfoil(
      "Stage3HAero", 
      new MathVector((Stage1AeroAft+Stage1AeroFwd)/2,0,0),
      new MathVector(0,0,1),
      new MathVector(1,0,0), 
      Stage3AeroFwd-Stage3AeroAft,  //Chord
      Stage3ROuter*StageAeroWeight  //Span
    ));
    if(useVAero) {
      NonThrusterA.add(new FlatPlateAirfoil(
        "Stage3VAero", 
        new MathVector((Stage1AeroAft+Stage1AeroFwd)/2,0,0),
        new MathVector(0,1,0),
        new MathVector(1,0,0), 
        Stage3AeroFwd-Stage3AeroAft,  //Chord
        Stage3ROuter*StageAeroWeight  //Span
      ));
    }      
    //Fairing
    NonPropA.add(InertSixDOFMass.MakeCylinderShell(
      "FairingInert", 
      301, 
      Stage1ROuter,
      Stage1RMid, 
      12.997, 
      14.755
    ));
    NonThrusterA.add(new FlatPlateAirfoil(
      "FairingHAero", 
      new MathVector(13,0,0),
      new MathVector(0,0,1),
      new MathVector(1,0,0), 
      6,                            //Chord
      Stage1ROuter*StageAeroWeight  //Span
    ));
    if(useVAero) {
      NonThrusterA.add(new FlatPlateAirfoil(
        "FairingVAero", 
        new MathVector(13,0,0),
        new MathVector(0,1,0),
        new MathVector(1,0,0), 
        6,                            //Chord
        Stage1ROuter*StageAeroWeight  //Span
      ));
    }    
    //Payload
    SixDOFMass Payload=new InertSixDOFMass(
      "PayloadInert",
      233, 
      new MathVector(14,0,0),
      SixDOFMass.SolidCylinderI(233, Stage3ROuter, 1)
    );
    NonPropA.add(Payload);
    NonThrusterA.add(new FlatPlateAirfoil(
      "PayloadHAero", 
      new MathVector(14,0,0),
      new MathVector(0,0,1),
      new MathVector(1,0,0), 
      2,   //Chord
      Stage3ROuter*2*StageAeroWeight //Span
    ));
    if(useVAero) {
      NonThrusterA.add(new FlatPlateAirfoil(
        "PayloadVAero", 
        new MathVector(14,0,0),
        new MathVector(0,1,0),
        new MathVector(1,0,0), 
        2,   //Chord
        Stage3ROuter*2*StageAeroWeight //Span
      ));
    }    
    //Unpack mass array lists
    SetupStuff(PropA,NonPropA,ThrusterA,NonThrusterA);
    //Set 15 thruster indexes
    Stage1RocketIdx=FindActuator("Stage1Rocket");
    Stage2RocketIdx=FindActuator("Stage2Rocket");
    Stage3RocketIdx=FindActuator("Stage3Rocket");
    RCSLPXIdx=FindActuator("RCSLPX");
    RCSLMXIdx=FindActuator("RCSLMX");
    RCSLPZIdx=FindActuator("RCSLPZ");
    RCSLMZIdx=FindActuator("RCSLMZ");
    RCSRPXIdx=FindActuator("RCSRPX");
    RCSRMXIdx=FindActuator("RCSRMX");
    RCSRPZIdx=FindActuator("RCSRPZ");
    RCSRMZIdx=FindActuator("RCSRMZ");
    RCSTPXIdx=FindActuator("RCSTPX");
    RCSTMXIdx=FindActuator("RCSTMX");
    RCSBPXIdx=FindActuator("RCSBPX");
    RCSBMXIdx=FindActuator("RCSBMX");
    //Set 17 aero indexes
    Stage1HAeroIdx=FindActuator("Stage1HAero");
    Stage2HAeroIdx=FindActuator("Stage2HAero");
    Stage3HAeroIdx=FindActuator("Stage3HAero");
    FairingHAeroIdx=FindActuator("FairingHAero");
    InterstageHAeroIdx=FindActuator("InterstageHAero");
    PayloadHAeroIdx=FindActuator("PayloadHAero");
    if(useVAero) {
      Stage1VAeroIdx=FindActuator("Stage1VAero");
      Stage2VAeroIdx=FindActuator("Stage2VAero");
      Stage3VAeroIdx=FindActuator("Stage3VAero");
      FairingVAeroIdx=FindActuator("FairingVAero");
      InterstageVAeroIdx=FindActuator("InterstageVAero");
      PayloadVAeroIdx=FindActuator("PayloadVAero");
    }
    LFinIdx=FindActuator("LFin");
    RFinIdx=FindActuator("RFin");
    VFinIdx=FindActuator("VFin");
    LWingIdx=FindActuator("LWing");
    RWingIdx=FindActuator("RWing");
    //Set 4 fuel indexes
    Stage1FuelIdx=FindMass("Stage1Fuel");
    Stage2FuelIdx=FindMass("Stage2Fuel");
    Stage3FuelIdx=FindMass("Stage3Fuel");
    RCSFuelIdx=FindMass("RCSFuel");
    //Set 6 inert indexes
    Stage1InertIdx=FindMass("Stage1Inert");
    Stage2InertIdx=FindMass("Stage2Inert");
    Stage3InertIdx=FindMass("Stage3Inert");
    InterstageInertIdx=FindMass("InterstageInert");
    FairingInertIdx=FindMass("FairingInert");
    PayloadInertIdx=FindMass("PayloadInert"); 
    
    //Turn off inactive actuators (ones under the fairing)
    ActuatorActive[Stage3HAeroIdx]=false;
    ActuatorActive[PayloadHAeroIdx]=false;
    if(useVAero) {
      ActuatorActive[Stage3VAeroIdx]=false;
      ActuatorActive[PayloadVAeroIdx]=false;
    }
    ActuatorActive[RCSLPXIdx]=false;
    ActuatorActive[RCSLMXIdx]=false;
    ActuatorActive[RCSLPZIdx]=false;
    ActuatorActive[RCSLMZIdx]=false;
    ActuatorActive[RCSRPXIdx]=false;
    ActuatorActive[RCSRMXIdx]=false;
    ActuatorActive[RCSRPZIdx]=false;
    ActuatorActive[RCSRMZIdx]=false;
    ActuatorActive[RCSTPXIdx]=false;
    ActuatorActive[RCSTMXIdx]=false;
    ActuatorActive[RCSBPXIdx]=false;
    ActuatorActive[RCSBMXIdx]=false;
    */
  }
  public Quaternion Navigate(double T, SixDOFState RVEw, MathVector FuelLevels, boolean IsMajor) {
    /*
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
    */
    return null;
  }
  public void Steer(double T, SixDOFState RVEw, boolean IsMajor, double PitchCommand, double YawCommand, double RollCommand) {
    /*
    //Command the main engines
    Thrusters[Stage1RocketIdx].Throttle=(T>5 && FuelLevels.get(0)>0)?1.0:0.0;
    Thrusters[Stage2RocketIdx].Throttle=(T>95.3 && FuelLevels.get(1)>0)?1.0:0.0;
    Thrusters[Stage3RocketIdx].Throttle=(T>600 && FuelLevels.get(2)>0)?1.0:0.0;

    //Command the steering actuators
    if(T<95) {
      //Shake a tail feather!
      double FinStop=toRadians(45);
      double LFinCommand=PitchCommand-RollCommand;
      double RFinCommand=PitchCommand+RollCommand;
      double VFinCommand=YawCommand;//+RollCommand;
      //Stop locations for fins, in radians
      if(abs(LFinCommand)>FinStop) LFinCommand=FinStop*((LFinCommand>0)?1.0:-1.0);
      if(abs(RFinCommand)>FinStop) RFinCommand=FinStop*((RFinCommand>0)?1.0:-1.0);
      if(abs(VFinCommand)>FinStop) VFinCommand=FinStop*((VFinCommand>0)?1.0:-1.0);
      ((Airfoil)Actuators[LFinIdx]).setAirfoilAngle(LFinCommand);
      if(IsMajor)Record(T, "LFinCmd", "deg", toDegrees(LFinCommand));
      ((Airfoil)Actuators[RFinIdx]).setAirfoilAngle(RFinCommand);
      if(IsMajor)Record(T, "RFinCmd", "deg", toDegrees(RFinCommand));
      ((Airfoil)Actuators[VFinIdx]).setAirfoilAngle(VFinCommand);
      if(IsMajor)Record(T, "VFinCmd", "deg", toDegrees(VFinCommand));
    } else if (FuelLevels.get(1)>0) {
      //Use the second stage TVC to maneuver
      double TVCStop=tan(toRadians(3));
      if(abs(PitchCommand)>TVCStop) PitchCommand=TVCStop*((PitchCommand>0)?1.0:-1.0);
      if(abs(YawCommand)>TVCStop) YawCommand=TVCStop*((YawCommand>0)?1.0:-1.0);
      Thrusters[Stage2RocketIdx].Direction=new MathVector(1, YawCommand, PitchCommand).normal();
      if(IsMajor)Record(T,"YawTVC", "deg", toDegrees(atan(YawCommand)));
      if(IsMajor)Record(T,"PitchTVC", "deg", toDegrees(atan(PitchCommand)));
      if(!HasStaged1) {
        HasStaged1=true;
        MassAttached[Stage1FuelIdx]=false;   //Stage1Fuel
        MassAttached[Stage1InertIdx]=false;   //Stage1Inert
        ActuatorActive[Stage1RocketIdx]=false; //Stage1Rocket
        ActuatorActive[Stage1HAeroIdx]=false; //Stage1 Aero
        ActuatorActive[Stage1VAeroIdx]=false; //Stage1 Aero
        ActuatorActive[LFinIdx]=false; //Left Fin
        ActuatorActive[RFinIdx]=false; //Right Fin
        ActuatorActive[VFinIdx]=false; //Vertical Fin
        ActuatorActive[LWingIdx]=false; //Main Wing
        ActuatorActive[RWingIdx]=false; //Main Wing
      }
      if(!HasDroppedInterstage&&T>96) {
        MassAttached[InterstageInertIdx]=false;   //Interstage
        ActuatorActive[InterstageHAeroIdx]=false;   //Interstage
        ActuatorActive[InterstageVAeroIdx]=false;   //Interstage
        HasDroppedInterstage=true;
      }
      if(!HasDroppedFairing && T<105) {
        MassAttached[FairingInertIdx]=false;   //Fairing 
        ActuatorActive[FairingHAeroIdx]=false; //Fairing Aero
        ActuatorActive[FairingVAeroIdx]=false; //Fairing Aero
        ActuatorActive[Stage3HAeroIdx]=true; //Stage3 Aero uncovered
        ActuatorActive[PayloadVAeroIdx]=true; //Payload Aero uncovered
        ActuatorActive[Stage3HAeroIdx]=true; //Stage3 Aero uncovered
        ActuatorActive[PayloadVAeroIdx]=true; //Payload Aero uncovered
//        ActuatorActive[RCSLPXIdx]=true;
//        ActuatorActive[RCSLMXIdx]=true;
//        ActuatorActive[RCSLPZIdx]=true;
//        ActuatorActive[RCSLMZIdx]=true;
//        ActuatorActive[RCSRPXIdx]=true;
//        ActuatorActive[RCSRMXIdx]=true;
//        ActuatorActive[RCSRPZIdx]=true;
//        ActuatorActive[RCSRMZIdx]=true;
//        ActuatorActive[RCSTPXIdx]=true;
//        ActuatorActive[RCSTMXIdx]=true;
//        ActuatorActive[RCSBPXIdx]=true;
//        ActuatorActive[RCSBMXIdx]=true;
        HasDroppedFairing=true;
      }
    }
    */
  }
  public static void main(String[] args) throws IOException {
    AtlasV AV010=new AtlasV(null);
  }
}
