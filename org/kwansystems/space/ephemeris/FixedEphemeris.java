package org.kwansystems.space.ephemeris;

import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.vector.*;

public class FixedEphemeris extends Ephemeris {
  MathState State;
  public FixedEphemeris(MathVector Loc, Ephemeris LReference) {
    super(LReference);
    State=new MathState(Loc,new MathVector(0,0,0));
  }
  public FixedEphemeris(MathVector Loc) {
    this(Loc,null);
  }
  public FixedEphemeris() {
    this(new MathVector(0,0,0));
  }
  protected MathState CalcState(Time T) {
    return State;
  }
  public MathState getState() {
    return State;
  }
  @Override
  public MathVector CalcPos(Time TT) {
    return defaultCalcPos(TT);
  }
  @Override
  public MathVector CalcVel(Time TT) {
    return defaultCalcVel(TT);
  }
}
