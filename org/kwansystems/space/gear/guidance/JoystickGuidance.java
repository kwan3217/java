package org.kwansystems.space.gear.guidance;

import java.io.*;

import org.kwansystems.space.gear.SixDOFVehicle;
import org.kwansystems.tools.vector.MathVector;
import org.kwansystems.tools.vector.SixDOFState;

import com.centralnexus.input.*;

public class JoystickGuidance extends Guidance {
  Joystick stick;
  boolean hasPoll=false;
  public JoystickGuidance() throws IOException {
    super();
    stick=Joystick.createInstance();
  }
  public JoystickGuidance(SixDOFVehicle LV) throws IOException {
    super(LV);
    stick=Joystick.createInstance();
  }

  @Override
  public void Guide(double T, SixDOFState RVEw, MathVector FuelLevels, boolean IsMajor) {
    if(!hasPoll || IsMajor) {
      stick.poll();
      hasPoll=true;
    }
    MathVector ControlFactor=new MathVector(stick.getX(),stick.getY(),stick.getZ());
    V.Steer(T,RVEw,IsMajor,ControlFactor);
  }

}
