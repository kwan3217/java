package org.kwansystems;

import java.util.*;
import org.kwansystems.tools.vector.*;
import org.kwansystems.tools.*;
import static java.lang.Math.*;

public class GravityTurn {
  class GravTurnRow {
    double time;
    double mass;
    double thrust;
    MathState pv;
    double vi;
    double vrel;
    double pitch;
  }
  public double pitch(double time, MathVector r, MathVector v, MathVector rot, double tpitchstart, double tpitchstop, double ppitchstop) {
    if(time<tpitchstart) return PI/2; //Straight up
    if(time<tpitchstop) return Scalar.linterp(tpitchstart, toDegrees(90), tpitchstop, ppitchstop, time); //Pitchover

    MathVector wind=MathVector.cross(rot, r);
    MathVector vrel=MathVector.sub(v, wind);
    vrel.normalEq();
    return acos(MathVector.dot(vrel,r.normal()));
  }
  public MathVector forceFunction(double time, MathVector r, MathVector v, MathVector rot,
                                  double thr, double m, double az, double tpitchstart, double tpitchstop, double ppitchstop) {
    double thrAcc=thr/m;
    MathVector result=new MathVector(0,0,0);
    double p=pitch(time,r,v,rot);
    if(time<tpitchstop) {
      MathVector E=MathVector.cross(r,rot);
    }

  }
  public GravTurnRow[] gravityTurn(double m0, double thr, double ve, double m1,
                                   double azimuth, double tpitchstart, double tpitchstop,
                                   double ppitchstop, MathVector pos, MathVector rot) {
    List<GravTurnRow> result=new LinkedList<GravTurnRow>();
    
    MathVector vel=MathVector.cross(rot,pos);
    double t=0;
    double m=m0;
    double dt=1;
    while(m>m1) {
      GravTurnRow row=new GravTurnRow();
      time=t;
      mass=m;

        
        
      m-=thr*dt/ve;
    }



    return null;
  }

}
