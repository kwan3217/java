package org.kwansystems.space.ephemeris;

import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.time.*;

public class ConstRotatorEphemeris extends RotatorEphemeris {
  Rotator R;
  public ConstRotatorEphemeris(Rotator LR) {
    super(null,null);
    R=LR;
  }
  @Override
  public Rotator CalcRotation(Time T) {
    return new MathMatrix(R);
  }

}
