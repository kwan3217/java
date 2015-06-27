package org.kwansystems.space.gear;

import org.kwansystems.space.gear.actuator.*;
import org.kwansystems.space.gear.guidance.*;
import org.kwansystems.space.gear.mass.*;
import org.kwansystems.space.planet.atmosphere.*;
import org.kwansystems.space.universe.*;
import org.kwansystems.tools.vector.*;

import java.io.*;
import java.util.*;
import static java.lang.Math.*;
import static org.kwansystems.space.gear.mass.PropRes.PropType.*;

public class TestJoystickRCS extends SixDOFVehicle {
  MagicThruster UF,UB,UL,UR,LF,LB,LU,LD,DF,DB,DL,DR,RF,RB,RU,RD;
  SixDOFMass Inert;

  public TestJoystickRCS(Universe Lu) throws IOException {
    super(Lu, new JoystickGuidance());
    MathVector U_=new MathVector( 0, 0, 1);
    MathVector D_=new MathVector( 0, 0,-1);
    MathVector L_=new MathVector( 0, 1, 0);
    MathVector R_=new MathVector( 0,-1, 0);
    
    MathVector _U=new MathVector( 0, 0, 1);
    MathVector _D=new MathVector( 0, 0,-1);
    MathVector _L=new MathVector( 0, 1, 0);
    MathVector _R=new MathVector( 0,-1, 0);
    MathVector _F=new MathVector( 1, 0, 0);
    MathVector _B=new MathVector(-1, 0, 0);
    double RCSThrust=0.001;
    
    //Up thruster quad
    UF=(MagicThruster)AddActuator(new MagicThruster( 
        "UF", 
        U_,
        _F,
        RCSThrust
      ));
    UB=(MagicThruster)AddActuator(new MagicThruster( 
        "UB", 
        U_,
        _B,
        RCSThrust
      ));
    UL=(MagicThruster)AddActuator(new MagicThruster( 
        "UL", 
        U_,
        _L,
        RCSThrust
      ));
    UR=(MagicThruster)AddActuator(new MagicThruster( 
        "UR", 
        U_,
        _R,
        RCSThrust
      ));
    //Down thruster quad
    DF=(MagicThruster)AddActuator(new MagicThruster( 
        "DF", 
        D_,
        _F,
        RCSThrust
      ));
    DB=(MagicThruster)AddActuator(new MagicThruster( 
        "DB", 
        D_,
        _B,
        RCSThrust
      ));
    DL=(MagicThruster)AddActuator(new MagicThruster( 
        "DL", 
        D_,
        _L,
        RCSThrust
      ));
    DR=(MagicThruster)AddActuator(new MagicThruster( 
        "DR", 
        D_,
        _R,
        RCSThrust
      ));
    //Left thruster quad
    LF=(MagicThruster)AddActuator(new MagicThruster( 
        "LF", 
        L_,
        _F,
        RCSThrust
      ));
    LB=(MagicThruster)AddActuator(new MagicThruster( 
        "LB", 
        L_,
        _B,
        RCSThrust
      ));
    LU=(MagicThruster)AddActuator(new MagicThruster( 
        "LU", 
        L_,
        _U,
        RCSThrust
      ));
    LD=(MagicThruster)AddActuator(new MagicThruster( 
        "LD", 
        L_,
        _D,
        RCSThrust
      ));
    //Right thruster quad
    RF=(MagicThruster)AddActuator(new MagicThruster( 
        "RF", 
        R_,
        _F,
        RCSThrust
      ));
    RB=(MagicThruster)AddActuator(new MagicThruster( 
        "RB", 
        R_,
        _B,
        RCSThrust
      ));
    RU=(MagicThruster)AddActuator(new MagicThruster( 
        "RU", 
        R_,
        _U,
        RCSThrust
      ));
    RD=(MagicThruster)AddActuator(new MagicThruster( 
        "RD", 
        R_,
        _D,
        RCSThrust
      ));
    Inert=AddMass(new InertSixDOFMass(
      "TestJoystickRCS",
      0.5,
      new MathVector(),
      SixDOFMass.RectangularPrismI(0.5,0.01,0.04,0.09)
    ));
  }
  @Override
  public void Steer(double T, SixDOFState RVEw, boolean IsMajor, MathVector SteerVector) {
    double R=SteerVector.get(0);
    double P=SteerVector.get(1);
    double Y=SteerVector.get(2);
    UF.setThrottle(-P);
    UB.setThrottle(P);
    DF.setThrottle(P);
    DB.setThrottle(-P);
  }
  @Override
  public void Discrete(double T, SixDOFState RVEw, MathVector FuelLevels) {
    // TODO Auto-generated method stub
    
  }
}
