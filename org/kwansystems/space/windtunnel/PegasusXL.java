
package org.kwansystems.space.windtunnel;

import org.kwansystems.vector.*;
import static java.lang.Math.*;


public class PegasusXL extends Airfoils {
  private static double midpoint(double x,double y) {
    return (((x)+(y))/2);
  }
  
  private static double midrad(double x,double y) {
    return (abs(((x)-(y))/2));
  }
  
  private static double COG2(double x1,double m1,double x2,double m2) {
    return (((x1)*(m1)+(x2)*(m2))/((m1)+(m2)));
  }
  private static double COG3(double x1,double m1,double x2,double m2,double x3,double m3) {
    return (((x1)*(m1)+(x2)*(m2)+(x3)*(m3))/((m1)+(m2)+(m3)));
  }
  private static double COG4(double x1,double m1,double x2,double m2,double x3,double m3,double x4,double m4) {
    return (((x1)*(m1)+(x2)*(m2)+(x3)*(m3)+(x4)*(m4))/((m1)+(m2)+(m3)+(m4)));
  }
  
  public PegasusXL() {
    super(null,null,null);
    final double STAGE1_EMPTY_MASS = 1369+755+151;
    final double STAGE1_PROP_MASS = 15014;
    final double STAGE1_MASS=STAGE1_EMPTY_MASS+STAGE1_PROP_MASS;
    final double STAGE1_BURN_TIME=68.6;
    final double STAGE1_ISP0 = 2846;
    final double STAGE1_ISP1 = STAGE1_ISP0*0.8;
    final double STAGE1_THRUST = STAGE1_ISP0*STAGE1_PROP_MASS/STAGE1_BURN_TIME;
    
    final double STAGE1_DIA           =   1.28;
    final double STA_STAGE1_AFT_POINT =   0.47;
    final double STA_STAGE1_ENGINE =     STA_STAGE1_AFT_POINT;
    final double STA_STAGE1_FWD_MOUNT=   10.81;
    final double STA_STAGE1_FWD_POINT=   STA_STAGE1_FWD_MOUNT;
    
    final double FinAngle=Math.toRadians(23);
    final double SFA=Math.sin(FinAngle);
    final double CFA=Math.cos(FinAngle);
    final double STA_FIN_AFT  =            0.83;
    final double STA_FIN_MID  =            1.54;
    final double STA_FIN_FWD  =            2.25;
    final double FIN_LEN      =            1.53;
    final double FIN_RECT_AREA   =         (STA_FIN_MID-STA_FIN_AFT)*FIN_LEN;
    final double FIN_TRI_AREA   =          (STA_FIN_FWD-STA_FIN_MID)*FIN_LEN/2;
    final double FIN_AREA       =          FIN_RECT_AREA+FIN_TRI_AREA;
    final double FIN_CHORD        =        FIN_AREA/FIN_LEN;
    final double STA_FIN_RECT_COL   =      midpoint(STA_FIN_MID,STA_FIN_AFT);
    final double STA_FIN_TRI_COL    =      (STA_FIN_FWD-STA_FIN_MID)/3+STA_FIN_AFT;
    final double STA_FIN_COL    =          COG2(STA_FIN_RECT_COL,FIN_RECT_AREA,STA_FIN_TRI_COL,FIN_TRI_AREA);
    final double BODY_WT = 0.2;
    
    final double STAGE2_EMPTY_MASS = 416;
    final double STAGE2_PROP_MASS = 3925;
    final double STAGE2_MASS=STAGE2_EMPTY_MASS+STAGE2_PROP_MASS;
    final double STAGE2_BURN_TIME=69.4;
    final double STAGE2_ISP0 = 2838;
    final double STAGE2_ISP1 = STAGE2_ISP0*0.8;
    final double STAGE2_THRUST = STAGE2_ISP0*STAGE2_PROP_MASS/STAGE2_BURN_TIME;
    
    final double STAGE2_DIA           =   1.28;
    final double STA_STAGE2_AFT_POINT =  10.43;
    final double STA_STAGE2_ENGINE =     STA_STAGE2_AFT_POINT;
    final double STA_STAGE2_AFT_MOUNT=   STA_STAGE1_FWD_MOUNT;
    final double STA_STAGE2_MID_MOUNT=   12.99;
    final double STA_STAGE2_FWD_MOUNT=   13.54;
    final double STA_STAGE2_FWD_POINT=   STA_STAGE2_FWD_MOUNT;
    
    final double RCS_ISP0 = 500.0;
    final double RCS_ISP1 = 500.0;
    final double RCS_PROP_MASS=50;
    
    final double STAGE3_DIA           =   0.97;
    final double STAGE3_EMPTY_MASS = 151+126-RCS_PROP_MASS;
    final double STAGE3_PROP_MASS = 770;
    final double STAGE3_MASS=STAGE3_EMPTY_MASS+STAGE3_PROP_MASS;
    final double STAGE3_BURN_TIME=68.5;
    final double STAGE3_ISP0 = 2817;
    final double STAGE3_ISP1 = STAGE3_ISP0*0.8;
    final double STAGE3_THRUST = STAGE3_ISP0*STAGE3_PROP_MASS/STAGE3_BURN_TIME;
    
    final double STA_STAGE3_AFT_POINT=   13.44;
    final double STA_STAGE3_ENGINE =     STA_STAGE3_AFT_POINT;
    final double STA_STAGE3_AFT_MOUNT=   STA_STAGE2_FWD_POINT;
    final double STA_STAGE3_FWD_MOUNT=   14.75;
    final double STA_STAGE3_FWD_POINT=   STA_STAGE3_FWD_MOUNT;
    
    final double FAIRING_MASS = 302;
    
    final double STA_FAIRING_AFT_POINT=   STA_STAGE2_MID_MOUNT;
    final double STA_FAIRING_AFT_MOUNT=   STA_STAGE2_MID_MOUNT;
    final double STA_FAIRING_FWD_POINT=   17.41;

    final double STA_STAGE1_COM  = 5.60; //217000 pt MC
    final double STA_STAGE2_COM  = 12.08;  //202000 pt MC
    final double STA_STAGE3_COM  = 14.28; //225000 pt MC
    final double STA_FAIRING_COM = 15.32; //232000 pt MC

    ForceMomentGenerator Wing=new PegasusXLWing(new MathVector(0,0,1),new MathVector(1,0,0),new MathVector(7.7,0,0));
    ForceMomentGenerator FinL=new FlatPlateAirfoil(new MathVector(0,-SFA,CFA),new MathVector(1,0,0),new MathVector(STA_FIN_COL,0,0),FIN_CHORD,FIN_LEN,0.6);
    ForceMomentGenerator FinR=new FlatPlateAirfoil(new MathVector(0,SFA,CFA),new MathVector(1,0,0),new MathVector(STA_FIN_COL,0,0),FIN_CHORD,FIN_LEN,0.6);
    ForceMomentGenerator FinV=new FlatPlateAirfoil(new MathVector(0,1,0),new MathVector(1,0,0),new MathVector(STA_FIN_COL,0,0),FIN_CHORD,FIN_LEN,0.6);
    ForceMomentGenerator Stage1Engine=new Thruster(STAGE1_THRUST, new MathVector(STA_STAGE1_ENGINE,0,0),new MathVector(1,0,0));
    ForceMomentGenerator Stage1Empty=new PointMass(STAGE1_EMPTY_MASS,new MathVector(STA_STAGE1_COM,0,0));
    ForceMomentGenerator Stage1Fuel=new PointMass(STAGE1_PROP_MASS,new MathVector(STA_STAGE1_COM,0,0));
    ForceMomentGenerator Stage1FoilH=new FlatPlateAirfoil(
      new MathVector(0,0,1),new MathVector(1,0,0),
      new MathVector(midpoint(STA_STAGE1_AFT_POINT,STA_STAGE1_FWD_POINT),0,0),
      (STA_STAGE1_FWD_POINT-STA_STAGE1_AFT_POINT)*BODY_WT,STAGE1_DIA,0.6
    );
    ForceMomentGenerator Stage1FoilV=new FlatPlateAirfoil(
      new MathVector(0,1,0),new MathVector(1,0,0),
      new MathVector(midpoint(STA_STAGE1_AFT_POINT,STA_STAGE1_FWD_POINT),0,0),
      (STA_STAGE1_FWD_POINT-STA_STAGE1_AFT_POINT)*BODY_WT,STAGE1_DIA,0.6
    );
    ForceMomentGenerator Stage2Empty=new PointMass(STAGE2_EMPTY_MASS,new MathVector(STA_STAGE2_COM,0,0));
    ForceMomentGenerator Stage2Fuel=new PointMass(STAGE2_PROP_MASS,new MathVector(STA_STAGE2_COM,0,0));
    ForceMomentGenerator Stage2FoilH=new FlatPlateAirfoil(
      new MathVector(0,0,1),new MathVector(1,0,0),
      new MathVector(midpoint(STA_STAGE2_AFT_POINT,STA_STAGE2_MID_MOUNT),0,0),
      (STA_STAGE2_FWD_POINT-STA_STAGE2_MID_MOUNT)*BODY_WT,STAGE2_DIA,0.6
    );
    ForceMomentGenerator Stage2FoilV=new FlatPlateAirfoil(
      new MathVector(0,1,0),new MathVector(1,0,0),
      new MathVector(midpoint(STA_STAGE2_AFT_POINT,STA_STAGE2_MID_MOUNT),0,0),
      (STA_STAGE2_MID_MOUNT-STA_STAGE2_AFT_POINT)*BODY_WT,STAGE2_DIA,0.6
    );
    ForceMomentGenerator Stage3Empty=new PointMass(STAGE3_EMPTY_MASS,new MathVector(STA_STAGE3_COM,0,0));
    ForceMomentGenerator Stage3Fuel=new PointMass(STAGE3_PROP_MASS,new MathVector(STA_STAGE3_COM,0,0));
    ForceMomentGenerator FairingMass=new PointMass(FAIRING_MASS,new MathVector(STA_FAIRING_COM,0,0));
    ForceMomentGenerator FairingFoilH=new FlatPlateAirfoil(
      new MathVector(0,0,1),new MathVector(1,0,0),
      new MathVector(midpoint(STA_FAIRING_AFT_POINT,STA_FAIRING_FWD_POINT),0,0),
      (STA_FAIRING_FWD_POINT-STA_FAIRING_AFT_POINT)*BODY_WT,STAGE2_DIA,0.6
    );
    ForceMomentGenerator FairingFoilV=new FlatPlateAirfoil(
      new MathVector(0,1,0),new MathVector(1,0,0),
      new MathVector(midpoint(STA_FAIRING_AFT_POINT,STA_FAIRING_FWD_POINT),0,0),
      (STA_FAIRING_FWD_POINT-STA_FAIRING_AFT_POINT)*BODY_WT,STAGE2_DIA,0.6
    );
    Name="PegasusXL";
    Names=new String[] {"Wing","FinL","FinR","FinV",
    "Stage 1 casing","Stage 1 fuel","Stage 1 H","Stage 1 V","Stage 1 Engine",
    "Stage 2 casing","Stage 2 fuel","Stage 2 H","Stage 2 V",
    "Stage 3 casing","Stage 3 fuel",
    "Fairing Mass","Fairing H","Fairing V"};
    Foils=new ForceMomentGenerator[] {Wing,FinL,FinR,FinV,
      Stage1Empty,Stage1Fuel,Stage1FoilH,Stage1FoilV,Stage1Engine,
      Stage2Empty,Stage2Fuel,Stage2FoilH,Stage2FoilV,
      Stage3Empty,Stage3Fuel,
      FairingMass,FairingFoilH,FairingFoilV};
  }
}
