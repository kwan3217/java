package org.kwansystems.space.porkchop;

import org.kwansystems.space.kepler.Terminal;
import org.kwansystems.space.kepler.Course;
import static java.lang.Math.*;
import static org.kwansystems.space.Constants.*;
import org.kwansystems.space.*;

import java.util.*;

import org.kwansystems.space.ephemeris.*;
import org.kwansystems.space.kepler.*;
import org.kwansystems.space.planet.*;
import org.kwansystems.tools.rootfind.optimize.Amoeba;
import org.kwansystems.tools.rootfind.optimize.OptimizeMultiDFunction;
import org.kwansystems.tools.time.*;
import org.kwansystems.tools.vector.*;

public class PorkchopModel {
  private Planet departPlanet;
  private Planet arrivePlanet;
  private KeplerPolyEphemeris departEph; 
  private KeplerPolyEphemeris arriveEph;
  private double departHp, arriveHp;
  private Time departTime, baseDepartTime;
  private Time arriveTime, baseArriveTime;
  private int launchWindow, transferType;
  private Course course;
  private Set<PorkchopUI> uis;
  private boolean constrainDepart, constrainArrive;
  private double departWeight,arriveWeight;
  
  public PorkchopModel() {
    uis=new HashSet<PorkchopUI>();
    departHp=185000;
    arriveHp=185000;
  }
  public void setDepartPlanet(Object notify, int idx) {
    departPlanet=Planet.Planets[idx];
    departEph=KeplerPolyEphemeris.Planets[idx];
    notifyDepartPlanet();
    if(enable) calcLaunchWindow();
  }
  public void setArrivePlanet(int idx) {
    arrivePlanet=Planet.Planets[idx];
    arriveEph=KeplerPolyEphemeris.Planets[idx];
    notifyArrivePlanet();
    if(enable) calcLaunchWindow();
  }
  public void setLaunchWindow(int LlaunchWindow) {
    launchWindow=LlaunchWindow;
    if(enable) calcLaunchWindow();
  }
  public void setTransferType(int LtransferType) {
    transferType=LtransferType;
    if(enable) calcCourse();
  }
  public void setDepartTime(Time T) {
    departTime=new Time(T);
    notifyDepartTime();
    if(enable) calcCourse();
  }
  public void setArriveTime(Time T) {
    arriveTime=new Time(T);
    notifyArriveTime();
    if(enable) calcCourse();
  }
  public void setConstrainDepart(boolean LconstrainDepart) {
    constrainDepart=LconstrainDepart;
    notifyConstraint();
  }
  public void setConstrainArrive(boolean LconstrainArrive) {
    constrainArrive=LconstrainArrive;
    notifyConstraint();
  }
  private void optimize(OptimizeMultiDFunction f) {
    if(departPlanet==null) return;
    if(arrivePlanet==null) return;
    if(departTime==null) return;
    if(arriveTime==null) return;
    if(constrainDepart && constrainArrive) return;
    double[] Init;
    if(constrainDepart || constrainArrive) {
      Init=new double[] {0};
    } else {
      Init=new double[] {0,0};
    }
    baseDepartTime=new Time(departTime);
    baseArriveTime=new Time(arriveTime);
    double[] Opt=Amoeba.amoeba(Init,1,1e-10,f);
    if(!constrainDepart) {
      setDepartTime(Time.add(baseDepartTime, Opt[0],TimeUnits.Days));
    }
    if(!constrainArrive) {
      setArriveTime(Time.add(baseArriveTime, Opt[constrainDepart?0:1],TimeUnits.Days));
    }
  }
  public void optimizeC3() {
    optimize(optC3);
  }
  private OptimizeMultiDFunction optC3=new OptimizeMultiDFunction() {
    @Override
    public double eval(double[] ofs) {
      double result=0;
      if(!constrainDepart) {
        setDepartTime(Time.add(baseDepartTime, ofs[0],TimeUnits.Days));
      }
      if(!constrainArrive) {
        setArriveTime(Time.add(baseArriveTime, ofs[constrainDepart?0:1],TimeUnits.Days));
      }
      if(departWeight!=0) { 
        Terminal DepartResolve=course.ResolveDeltaVdepart(departPlanet,departHp);
        result+=DepartResolve.getC3()*getDepartWeight();
      }
      if(arriveWeight!=0) {
        Terminal ArriveResolve=course.ResolveDeltaVarrive(arrivePlanet,arriveHp);
        result+=ArriveResolve.getC3()*getArriveWeight();
      }
      return result;
    }
  };
  private OptimizeMultiDFunction optDVesc=new OptimizeMultiDFunction() {
    @Override
    public double eval(double[] ofs) {
      double result=0;
      if(!constrainDepart) {
        setDepartTime(Time.add(baseDepartTime, ofs[0],TimeUnits.Days));
      }
      if(!constrainArrive) {
        setArriveTime(Time.add(baseArriveTime, ofs[constrainDepart?0:1],TimeUnits.Days));
      }
      if(getDepartWeight()>0) {
        Terminal departResolve=course.ResolveDeltaVdepart(departPlanet,departHp);
        result+=departResolve.getDVesc()*getDepartWeight(); 
      }
      if(getArriveWeight()>0) {
        Terminal arriveResolve=course.ResolveDeltaVarrive(arrivePlanet,arriveHp);
        result+=arriveResolve.getDVesc()*getArriveWeight();
      }
      return result;
    }
  };
  public void optimizeDVesc() {
    optimize(optDVesc);
  }
  public Course getCourse() {
    return course;
  }

  public Planet getDepartPlanet() {
    return departPlanet;
  }
  public Planet getArrivePlanet() {
    return arrivePlanet;
  }
  private void calcLaunchWindow() {
    if(departEph==null) return;
    if(arriveEph==null) return;
    Time[] TimeHohman=calcLaunchWindow(departEph,arriveEph,launchWindow);
    departTime=TimeHohman[0];
    arriveTime=TimeHohman[1];
    notifyDepartTime();
    notifyArriveTime();
    calcCourse();
  }
  private void calcCourse() {
    if(departPlanet==null) return;
    if(arrivePlanet==null) return;
    if(departTime==null) return;
    if(arriveTime==null) return;
    MathStateTime departST=departPlanet.Orbit.getStateTime(departTime);
    MathStateTime arriveST=arrivePlanet.Orbit.getStateTime(arriveTime);
    course=GaussFG.Sun.target(departST,arriveST,transferType);
    notifyCourse();
  }
  private static Time[] calcLaunchWindow(KeplerPolyEphemeris P1,KeplerPolyEphemeris P2,int Window) {
    /* Terminals semimajor axes, km */
    double a1=P1.a.eval(0);
    double a2=P2.a.eval(0);  
    
    /* Reverse window sense if going outward, so that higher (input) windows
     * always give times farther into the future
     */
    if(a2>a1) {
      Window=-Window;
    }

    /* Transfer semimajor axis, km */
    double TransferA=(a1+a2)/2;
    /* Transfer time, day */
    double TransferT=PI*sqrt(pow(TransferA,3)/SunGM)/86400;  
    /* Current longitude, rad */
    double L1=P1.lm.Coeffs[0];
    double L2=P2.lm.Coeffs[0];
    /* Longitude rate, rad/day */
    double w1=P1.lm.Coeffs[1]/36525;
    double w2=P2.lm.Coeffs[1]/36525;
    /* Synodic period, day */
    //double SynodicPeriod=2*PI/(abs(w1-w2));
    /* Dropback angle, rad */
    double DropBack=w2*TransferT;
    /* Phase angle, rad */
    double Phase=PI-DropBack;
    Time t1=new Time((L2-Phase-L1-2*PI*Window)/(w1-w2), TimeUnits.Days, TimeScale.UTC,TimeEpoch.J2000);
    Time t2=Time.add(t1,TransferT);
    t1.Epoch=TimeEpoch.MJD;
    t2.Epoch=TimeEpoch.MJD;
    return new Time[] {t1,t2};
  }
  public void addView(PorkchopUI v) {
    uis.add(v);
  }
  public void notifyDepartPlanet() {
    for(PorkchopUI v:uis) v.updateDepartPlanet(departPlanet);    
  }
  public void notifyArrivePlanet() {
    for(PorkchopUI v:uis) v.updateArrivePlanet(arrivePlanet);    
  }
  public void notifyDepartHp() {
    for(PorkchopUI v:uis) v.updateDepartHp(departHp);    
  }
  public void notifyArriveHp() {
    for(PorkchopUI v:uis) v.updateArriveHp(arriveHp);    
  }
  public void notifyDepartTime() {
    for(PorkchopUI v:uis) v.updateDepartTime(departTime);    
  }
  public void notifyArriveTime() {
    for(PorkchopUI v:uis) v.updateArriveTime(arriveTime);    
  }
  public void notifyCourse() {
    for(PorkchopUI v:uis) v.updateCourse(course);    
  }
  public void notifyConstraint() {
    for(PorkchopUI v:uis) v.updateConstrain(constrainDepart,constrainArrive);    
  }
  private boolean enable=true;
  public void enableCalc(boolean Lenable) {enable=Lenable;}
  public static void main(String[] args) {
    PorkchopUI V=new BlankPorkchopUI() {
      Planet departPlanet;
      public void updateArrivePlanet(Planet arrivePlanet) {
        System.out.println(arrivePlanet.toString());
      }
      public void updateCourse(Course course) {
        System.out.println(course.toString());
        Terminal result=model.getDepart();
        System.out.println("Pro:  "+result.getPro()+"m/s");
        System.out.println("Out:  "+result.getOut()+"m/s");
        System.out.println("ChPl: "+result.getChPl()+"m/s");
      }
      public void updateDepartPlanet(Object notify, Planet LdepartPlanet) {
        departPlanet=LdepartPlanet;
        System.out.println(departPlanet.toString());
      }
    };
    PorkchopModel M=new PorkchopModel();
    V.setModel(M);
    M.addView(V);
    M.setDepartPlanet(null,3);
    M.setArrivePlanet(4);
    M.setLaunchWindow(6);
    M.setDepartTime(new Time(2013,11,18,13,43,0,0,TimeUnits.Days,TimeScale.EST,TimeEpoch.MJD));
    M.constrainDepart=true;
    M.setDepartWeight(1);
    M.setArriveWeight(0);
    M.optimizeDVesc();
  }

    public double getDepartHp() {
        return departHp;
    }

    public void setDepartHp(double departHp) {
        this.departHp = departHp;
    }

    public double getArriveHp() {
        return arriveHp;
    }

  public void setArriveHp(double arriveHp) {
    this.arriveHp = arriveHp;
  }
  public Terminal getDepart() {
    return course.ResolveDeltaVdepart(departPlanet, departHp);
  }
  public Terminal getArrive() {
    return course.ResolveDeltaVdepart(arrivePlanet, arriveHp);
  }

  public double getDepartWeight() {
    return departWeight;
  }

  public void setDepartWeight(double departWeight) {
    this.departWeight = departWeight;
  }

  public double getArriveWeight() {
    return arriveWeight;
  }

  public void setArriveWeight(double arriveWeight) {
    this.arriveWeight = arriveWeight;
  }
}
