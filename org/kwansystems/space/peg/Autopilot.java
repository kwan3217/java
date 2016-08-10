package org.kwansystems.space.peg;

import java.lang.reflect.*;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.vector.*;

import static java.lang.Math.*;

public abstract class Autopilot {
  protected PEGVehicleModel veh;
  /** Mission Elapsed Time (s) */
  @VariableDesc(desc="Mission Elapsed Time",units="s",Major=true,Minor=true)
  protected double met; 
  @VariableDesc(desc="Initial MET",units="s",Major=true,Minor=false)
  protected double t0;
  @VariableDesc(desc="Minor cycle count",units="minor cycles",Major=false,Minor=true)
  protected int nStep;
  @VariableDesc(desc="Standard acceleration of gravity",units="m/s^2",Major=true,Minor=false)
  protected final double g0=9.80665;
  @VariableDesc(desc="Gravitational Parameter of Earth",units="m,s",Major=true,Minor=false)
  protected final double mu=3.986004418e14;
  @VariableDesc(desc="Radius of Earth",units="m",Major=true,Minor=false)
  protected final double Re=6371010;
  @VariableDesc(desc="Current position vector",units="m",Major=true,Minor=false)
  protected MathVector rvec;
  @VariableDesc(desc="Current velocity vector",units="m",Major=true,Minor=false)
  protected MathVector vvec;
  @VariableDesc(desc="Thrust direction",units="",Major=true,Minor=true)
  public MathVector fhat;
  @VariableDesc(desc="Minor Cycle Rate",units="Hz",Major=true,Minor=false)
  private double MinorCycleRate;
  @VariableDesc(desc="Major Cycle Rate",units="minor cycle times",Major=true,Minor=false)
  private int MajorCycleRate;
  @VariableDesc(desc="Step number of next major cycle",units="minor cycle times",Major=true,Minor=false)
  private int nStepNextMajorCycle;
  public ChartRecorder MajorRecord,MinorRecord;
  public Autopilot(double LMinorCycleRate, int LMajorCycleRate, PEGVehicleModel Lveh) {
    veh=Lveh;
    MajorCycleRate=LMajorCycleRate;
    MinorCycleRate=LMinorCycleRate;
    MajorRecord=new ArrayListChartRecorder();
    MinorRecord=new ArrayListChartRecorder();
  }
  public abstract void MajorCycle();
  public abstract void MinorCycle();
  public abstract boolean Cutoff();
  public void init(MathVector Lrvec, MathVector Lvvec, double Lt) {
    rvec=new MathVector(Lrvec);
    vvec=new MathVector(Lvvec);
    t0=Lt;
    met=t0;
    nStep=0;
    nStepNextMajorCycle=MajorCycleRate;
    //Run the major cycle before the first propagation, so there is steering
    MajorCycle();
    MinorCycle();
    Record(MajorRecord,true);
    Record(MinorRecord,false);
  };
  public void step() {
    Propagate();
    if(nStep>=nStepNextMajorCycle) {
      System.out.println(met);
      MajorCycle();
      Record(MajorRecord,true);
      nStepNextMajorCycle+=MajorCycleRate;
    }
    MinorCycle();
    Record(MinorRecord,false);
  }
  public void fly() {
    while(!Cutoff()) { //Range safety, in case the thrust vector blows up
      if(Double.isNaN(fhat.X()) || fhat.length()>1.01) {
        System.out.println("Range safety cutoff at t="+met+", vehicle out of control (fhat="+fhat.toString()+")");
        Record(MajorRecord,true);
        Record(MinorRecord,false);
        return;
      }
      step();
    }
    System.out.println("Commanded cutoff at t="+met);
  }
  protected double Mass() {
    return veh.Mass(met);
  }
  protected MathVector Gravity() {
    return rvec.mul(-mu*Mass()/pow(rvec.length(),3));
  }
  protected double ThrustMag() {
    return veh.ThrustMag(met);
  }
  protected MathVector Thrust() {
    if(fhat!=null) return fhat.mul(ThrustMag());
    return new MathVector(0,0,0);
  }
  public void Propagate() {
    nStep++;
    met=t0+((double)nStep)/MinorCycleRate;
    double dt=1.0/MinorCycleRate;
    MathVector Gvec = Gravity();
    MathVector Fvec = Thrust();
    MathVector avec = MathVector.add(Gvec, Fvec).mul(1.0 / Mass());
    vvec = MathVector.add(vvec, avec.mul(dt));
    rvec = MathVector.add(rvec, vvec.mul(dt));
  }

  public void Record(ChartRecorder C, boolean isMajor) {
    Record(C,this.getClass(),isMajor);
    Class cc=this.getClass().getSuperclass();
    Record(C,cc,isMajor);
  }
  private void Record(ChartRecorder C, Class cc, boolean isMajor) {
    for (Field f : cc.getDeclaredFields()) {
      String name = f.getName();
      if(f.isAnnotationPresent(VariableDesc.class)) {
        VariableDesc a=f.getAnnotation(VariableDesc.class);
        boolean matchIsMajor=isMajor && a.Major();
        boolean matchIsMinor=!isMajor && a.Minor();
        boolean matchMajorness=matchIsMajor || matchIsMinor;
        boolean notTime=!name.equals("t"); //Don't need the time field, it's already the range time column
        if(matchMajorness && notTime) {
          try {
            if (f.getType().isArray()) {
              double[] avalue = (double[]) f.get(this);
              for(int i=0;i<avalue.length;i++) {
                C.Record(met, String.format("%s[%02d]",name,i), avalue[i]);
              }
            } else if (f.get(this) instanceof MathVector) {
              MathVector vvalue = (MathVector) f.get(this);
              C.Record(met, name + ".x", vvalue.X());
              C.Record(met, name + ".y", vvalue.Y());
              C.Record(met, name + ".z", vvalue.Z());
            } else {
              C.Record(met, name, f.get(this));
            }
          } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex);
          } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
          }
        }
      }
    }
  }
  //Lunar targeting variables (unused if not targeting the moon)
  @VariableDesc(desc="Target periapse altitude",units="m")
  protected double z_p;
  @VariableDesc(desc="Target time of flight",units="s")
  protected double t_target;
}
