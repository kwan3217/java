package org.kwansystems.space.ephemeris.saturn;

import org.kwansystems.space.ephemeris.Ephemeris;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.vector.*;

public class SaturnSatSystem extends Ephemeris {
  public static final Ephemeris[] satArray;
  public static final double[] satMassRatio;
  public SaturnSatSystem(int i) {}
  public MathState CalcState(Time T) {
    return new MathState();
  }
  static {
    satArray=new Ephemeris[18];
    satMassRatio=new double[18];
    satArray[0]=null;
    for(int i=0;i<=8;i++) {
      satArray[i]=SaturnSatTASS17.satArray[i];
      satMassRatio[i]=SaturnSatTASS17.satMassRatio[i];
      
    }
    /*
    satArray[10]=SaturnSatSAT186.satArray[10];
    satArray[11]=SaturnSatSAT186.satArray[11];
    satArray[16]=SaturnSatSAT186.satArray[16];
    satArray[17]=SaturnSatSAT186.satArray[17];
    satMassRatio[10]=SaturnSatSAT186.satMassRatio[10];
    satMassRatio[11]=SaturnSatSAT186.satMassRatio[11];
    satMassRatio[16]=SaturnSatSAT186.satMassRatio[16];
    satMassRatio[17]=SaturnSatSAT186.satMassRatio[17];
    */
  }
  public MathVector CalcPos(Time TT) {
	return defaultCalcPos(TT);
  }
  public MathVector CalcVel(Time TT) {
	return defaultCalcVel(TT);
  }
}
