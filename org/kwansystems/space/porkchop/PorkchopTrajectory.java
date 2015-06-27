package org.kwansystems.space.porkchop;

import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.vector.*;

import java.awt.*;

public class PorkchopTrajectory {
  MathStateTime Start;
  Time Stop;
  Color color;
  double DeltaT;
  public PorkchopTrajectory(MathStateTime LStart, Time LStop, Color Lcolor, int steps) {
    Start=LStart;
    Stop=LStop;
    color=Lcolor;
    DeltaT=Time.difference(Start.T, Stop, Start.T.Units)/steps;
  }
  public PorkchopTrajectory(MathStateTime LStart, Time LStop, Color Lcolor) {
    this(LStart,LStop,Lcolor,100);
  }
}
