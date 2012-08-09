package org.kwansystems.space.launch;

import org.kwansystems.space.planet.*;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.vector.*;

public abstract class Guidance {
  protected Planet P;
  protected ArrayListChartRecorder C;
  public Guidance(Planet LP, ArrayListChartRecorder LC) {P=LP;C=LC;}
  public abstract MathVector ThrustVector(double T, MathState X, double AccAvailable, boolean IsMajor);
}
