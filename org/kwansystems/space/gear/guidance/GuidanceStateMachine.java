package org.kwansystems.space.gear.guidance;

import java.util.*;
import org.kwansystems.space.gear.*;
import org.kwansystems.tools.vector.*;

public class GuidanceStateMachine {
  Map<String,GuidanceState> states;
  public String currentStateName;
  public GuidanceState currentState;
  public double timerStart;
  public double timerExp;
  public boolean newState;
  public GuidanceStateMachine() {
    states=new TreeMap<String,GuidanceState>();
    timerStart=0;
    timerExp=Double.POSITIVE_INFINITY;
  }
  public void addState(String name, GuidanceState state) {
    states.put(name, state);
  }
  public void setState(String name) {
    currentStateName=name;
    currentState=states.get(name);
    newState=true;
  }
  public void step(SixDOFVehicle vehicle, double T, SixDOFState RVEw, MathVector fuelLevels) {
    if(newState) {
      newState=false;
      currentState.enterState(this,vehicle,T,RVEw,fuelLevels);
    }
    currentState.step(this,vehicle,T,RVEw,fuelLevels);
  }
  public void setTimer(double Tstart, double Tdur) {
    timerStart=Tstart;
    timerExp=Tdur;
  }
  public double getTimer(double T) {
    return T-timerStart;
  }
  public boolean isTimerExp(double T) {
    return T>timerExp;
  }
}
