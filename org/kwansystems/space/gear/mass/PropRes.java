package org.kwansystems.space.gear.mass;

import org.kwansystems.tools.rotation.MathMatrix;
import org.kwansystems.tools.vector.*;

import static org.kwansystems.space.gear.mass.PropRes.PropPhase.*;

public abstract class PropRes extends SixDOFMass{
  public double FullMass;
  public double Level;
  public PropType type;
  public static enum PropPhase {
  	Solid,Liquid,Gas
  }
  static double LBFT3_PER_KGM3=16.0184634;
  public enum PropType {
  	/** RP-1, hydrocarbon fuel liquid. Basically kerosene. */
  	RP1(49.75*LBFT3_PER_KGM3,Liquid), //AC-10 p37
    /** Liquid Oxygen, cryogen oxidizer */
	  LOX(69.27*LBFT3_PER_KGM3,Liquid), //ibid
    /** Liquid Hydrogen, cryogen fuel */
	  LH2(70.8,Liquid),
    /** Water, ballast/life support liquid */
    Water(1000,Liquid),
    /** Hydrazine, hypergol fuel/monopropellant liquid.
     *  N2H4 H2N-NH2 
     */
    Hydrazine(1010,Liquid),
    /** Unsymetrical Dimethyl Hydrazine, hypergol fuel liquid. 
     * Hydrazine with both hydrogens on one end replaced by methane groups H2N-N(CH3)2
     */
    UDMH(793,Liquid),        
    /** Monomethyl Hydrazine, hypergol fuel liquid. 
     * Hydrazine with one hydrogen replaced by a methane group H2N-NH(CH3)
     */
    MMH(880,Liquid),        
    /** Aerozine-50, hypergol fuel liquid. 
     * A 50/50 mix of Hydrazine and UDMH
     */
    Aerozine50(903,Liquid), 
    /** Nitrogen tetroxide, hypergol oxidizer liquid.
     *  Kinda like hydrazine, but with oxygens instead of hydrogens O2N-NO2
     */
    N2O4(1443,Liquid),  
    /** Nitric acid, hypergol oxidizer liquid.
     *  Original hypergolic oxidizer. Needed to be stabilized by mixing in N2O4. Eventually
     *  it was discovered that N2O4 by itself is a better oxidizer.
     */
    HNO3(1510,Liquid),  
    /** Solid fuel as used on Pegasus motors */
	  PegasusSolid(2350,Solid),
    /** Aluminum metal, solid used in structures */
	  Aluminum(2700,Solid),
    /** Air, pressurant/life support gas */
	  Air(org.kwansystems.space.planet.atmosphere.EarthAirConstants.M0,Gas),
    /** Nitrogen, pressurant/cold gas propellant */
    Nitrogen(14.0067*2,Gas),                                               
    /** Argon, pressurant gas */
    Argon(39.948,Gas),       
    /** Methane, fuel gas */
    Methane(16.0425,Gas),
    /** Oxygen, oxidizer/life support/pressurant gas*/
    Oxygen(15.9994*2,Gas);                                               
	  /** Substance density. Measured in kg/m^3 for solid and liquid, molecular weight (kg/kmol) for gas */
	  public final double density; 
    /** Propellant Phase */
	  public final PropPhase phase; //Propellant phase
	  private PropType(double Ldensity,PropPhase Lphase) {
	    density=Ldensity;
	    phase=Lphase;
	  }
  }
  public double getMass(double T, SixDOFState RVEw) {
    return FullMass*Level;
  }
  public abstract MathVector getCoM(double T, SixDOFState RVEw);
  public abstract MathMatrix getI(double T, SixDOFState RVEw); 
  public PropRes(String LName, double LFullMass, PropType Ltype) {
    super(LName);
    FullMass=LFullMass;
    Level=1.0;
    type=Ltype;
  }
  public PropRes(String LName, double LFullMass, PropType Ltype, PropPhase phase) {
	this(LName,LFullMass,Ltype);
    if (Ltype.phase!=phase) throw new IllegalArgumentException("Propellant resource for "+phase+" phase propellant used to hold "+Ltype.phase+ " phase propellant "+Ltype);
  }
}
