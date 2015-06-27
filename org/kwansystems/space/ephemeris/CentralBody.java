package org.kwansystems.space.ephemeris;

import org.kwansystems.space.ephemeris.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.vector.*;

/**
 * Motion of the central body of a multi-body system. Examples include the Sun and all the multiple-moon planets.
 * Given a bunch of planetocentric satellite orbits, this class calculates the coordinates of the 
 * planet in barycentric coordinates.
 */
public class CentralBody extends Ephemeris {
  /**
   * This is a zero based array with the zeroth element null, representing the center
   */
  Ephemeris[] Satellites;
  /** 
   * This is a zero based array with the zeroth element the mass ratio of the center
   */
  double[] MassRatio;
  
  /**
   * Calculate the position and velocity of the Center of Mass 
   * in planetocentric coordinates
   * @param Mass Array of object masses. Element zero is the mass of the central
   * body, the others are in one-to-one correspondence with the other objects
   * @param Pos Array of planetocentric object positions. Naturally, object 0
   * will have a zero (<0,0,0>, not null) position vector
   * @return 
   *   Location of system barycenter, in planetocentric coordinates
   */
  public static MathState CoM(double[] Mass, MathState[] Pos) {
    MathState Answer=new MathState();
    double MassSum=0;
    for(int i=0;i<Mass.length;i++) {
      if(Pos[i]!=null) {
    	MathVector A=Pos[i].mul(Mass[i]);
    	MathVector B=MathVector.add(Answer,A);
        Answer=new MathState(B);
        MassSum+=Mass[i];
      }
    }
    return new MathState(Answer.div(MassSum));
  }
  /**
   * Create a CentralBody where the barycenter is at the origin
   * @param LSatellites Planetocentric paths of satellites. Zero element should be null, as it represents the 
   *        central body path in planetocentric coordinates
   * @param LMassRatio Satellite mass ratios. Zero element represents the central body
   */
  public CentralBody(Ephemeris[] LSatellites, double[] LMassRatio) {
	  this(null,LSatellites,LMassRatio);
  }
  /**
   * Create a CentralBody where the barycenter is a RailMover
   * @param LReference Path of barycenter
   * @param LSatellites Planetocentric paths of satellites. Zero element should be null, as it represents the 
   *        central body path in planetocentric coordinates
   * @param LMassRatio Satellite mass ratios. Zero element represents the central body
   */
  public CentralBody(Ephemeris LReference, Ephemeris[] LSatellites, double[] LMassRatio) {
    super(LReference);
    Satellites=LSatellites;
    MassRatio=LMassRatio;
  }
  public MathState CalcState(Time TT) {
    MathState[] Pos=new MathState[Satellites.length];
    Pos[0]=new MathState();
    for(int i=1;i<Pos.length;i++) if(Satellites[i]!=null) Pos[i]=Satellites[i].relGetState(TT);
    MathState result=CoM(MassRatio,Pos);
    return new MathState(result.opp());
  }
  public MathVector CalcPos(Time TT) {
  	return defaultCalcPos(TT);
  }
}
