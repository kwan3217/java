package org.kwansystems.space.planet;

import org.kwansystems.space.ephemeris.*;
import org.kwansystems.space.ephemeris.earth.*;
import org.kwansystems.space.ephemeris.sun.*;
import org.kwansystems.space.planet.atmosphere.*;
import org.kwansystems.tools.vector.*;

import static java.lang.Math.*;

import static org.kwansystems.space.ephemeris.RotatorEphemeris.Frame.*;

/**
 * Object representing a planet and its atmosphere
 */
public class Planet{
  public Spheroid S;
  /**
   *Planet atmosphere object
   */
  public Atmosphere Atm;
  /**
   *Planet orbit object
   */
  public Ephemeris Orbit;
  /**
   * Rotator object
   */
  public RotatorEphemeris Rot;
  /**
   *constant vector used for calculating wind velocity. Points towards North Pole, 
   *length is speed of wind at equator 1m from planet center in m/s
   */
  MathVector WindVec;
  private static final MathVector Pole=new MathVector(0,0,1);
  public Planet(Spheroid LS, Atmosphere LAtm, Ephemeris LOrbit, RotatorEphemeris LRot) {
    S=LS;
    Atm=LAtm;
    Orbit=LOrbit;
    Rot=LRot;
    setWindVec();
  }
  /**
   * Calculates wind at given point
   * @param xyz Radius vector, in planet-centered inertial frame, m
   * @return wind speed, in inertial coordinates, m/s
   */
  public MathVector Wind(MathVector xyz) {
    return MathVector.cross(WindVec,xyz.subVector(0,3));
  }
  /**
   * Get air properties for a point
   * @param xyz Radius vector, in planet-centered inertial frame, m
   * @return An AirProperties object representing the free atmosphere at this point
   */
  public AirProperties Air(MathVector xyz) {
    return Atm.calcProps(Alt(xyz));
  }
  public double Alt(MathVector xyz) {
    if(S.f==0) {
      return xyz.length()-S.Re;
    }
    return xyz2lla(xyz)[2];
  }
  /**
   * Acceleration of gravity, m/s^2
   * @param xyz Radius vector, in planet-centered inertial frame, m
   * @return Acceleration vector, inertial frame, m/s^2
   */
  public MathVector Gravity(MathVector xyz) {
    MathVector result=xyz.mul(-S.GM/Math.pow(xyz.length(),3));
    if(S.J2!=0) {
      MathVector J2Grav=J2Gravity(xyz);
      result=MathVector.add(result,J2Grav);
    }
    return result;
  }
  /** Calculates a dynamic pressure vector. This vector has a length
   *  equal to the dynamic pressure in N/m^2, and a direction in the direction
   *  of the pressure in inertial space. This will be exactly opposite
   *  the velocity vector in ECF space.
   * @param rv State vector, m and m/s ECI
   * @return Dynamic pressure vector, in N/m^2
   */
  public MathVector DynPres(MathState rv) {
    MathVector W=Wind(rv.R());
    MathVector Vrel=MathVector.sub(rv.V(),W);
    AirProperties A=Air(rv.R());
    double vr=Vrel.length();
    double q=0.5*vr*vr*A.Density;
    return Vrel.normal().mul(-q);
    
  }
  /** Calculates the drag acceleration on an object
   * @param rv State vector, m and m/s ECEF
   * @param A  Effective aerodynamic cross section, m^2
   * @param m  mass, kg
   * @param Cd Drag coefficient
   * @return Drag acceleration on object, m/s^2
   */
  public MathVector DragAcc(MathState rv, double A, double m, double Cd) {
    return DynPres(rv).mul(A*Cd/m);
  }
  /**
   *Calculates wind vector based on planet rotation speed
   */
  public void setWindVec() {
    WindVec=new MathVector(0,0,S.omegaPrime);
  }
  public MathVector J2Gravity(MathVector r) {
    double Coeff1=-3.0*S.J2*S.GM*S.Re*S.Re/(2.0*Math.pow(r.length(),5));
    double Coeff2=1.0-5.0*r.Z()*r.Z()/r.lensq();
    double Coeff3=3.0-5.0*r.Z()*r.Z()/r.lensq();
    return new MathVector(
      Coeff1*r.X()*Coeff2,
      Coeff1*r.Y()*Coeff2,
      Coeff1*r.Z()*Coeff3
    );
  }
  public double[] xyz2lla(MathVector xyz) {
    return S.xyz2lla(xyz);
  }
  public MathVector LocalVertical(MathVector xyz) {
    if(S.f==0) {
      return xyz.normal();
    }
    double[] lla=xyz2lla(xyz);
    return lla2xyz(lla).normal();
  }
  /**
   * Convert a navigation coordinates into a cartesian vector
   * @param lla A vector of [Latitude (rad), Longitude (rad), Altitude (m)] 
   * @return Radius vector, in planet-centered inertial frame, m
   */
  public MathVector lla2xyz(double[] lla) {
    return lla2xyz(lla[0],lla[1],lla[2]);
  }
  /**
   * Convert a latitude, longitude, and altitude triplet into a rectangular vector
   * @param lat Latutde, rad
   * @param lon Longitude, rad
   * @param alt Altitude, m
   * @return Radius vector, in planet-centered inertial frame, m
   */
  public MathVector lla2xyz(double lat, double lon, double alt) {
    double Rad=S.Re+alt;
    return new MathVector(
      cos(lon)*cos(lat)*Rad,
      sin(lon)*cos(lat)*Rad,
      sin(lat)*Rad
    );
      
  }
  /**
   * 
   * @param xyz Radius vector, in planet-centered inertial frame, m
   * @param Spd Airspeed, m/s. 
   * @param Hdg Heading, rad. North is 0, east is pi/2, south is pi (-pi), 
   * west is 3*pi/2 (-pi/2)
   * @return inertial velocity, m/s
   */
  public MathVector RelHdg2vxyz(MathVector xyz, double Spd, double Hdg) {
    MathVector Sky=LocalVertical(xyz);
    MathVector East=MathVector.cross(Pole,Sky).normal();
    MathVector North=MathVector.cross(Sky,East).normal();
    MathVector Rel=MathVector.add(
      North.mul(cos(Hdg)*Spd),
      East.mul(sin(Hdg)*Spd)
    );
    MathVector W=Wind(xyz);
    MathVector Ine=MathVector.add(Rel,W);
    return Ine;
  }
  public static final String OrbiterConfigPath="Data/OrbiterConfig/";
  public static final Planet Sun    =new Planet(new Spheroid(695000e3,0,SunSatSeries96.satGM[0],2*Math.PI/(360.0/  14.1844000)), new NoAtmosphere(),         new FixedEphemeris(),      new EarthRotIAU1980(J2000Equ,PEF));
  public static final Planet Mercury=new Planet(new Spheroid(  2439e3,0,SunSatSeries96.satGM[1],2*Math.PI/(360.0/   6.1385025)), new NoAtmosphere(),         SunSatSeries96.satArray[1],new OrbiterRotatorEphemeris(OrbiterConfigPath+"Mercury.cfg",MercuryCenteredFixed));
  public static final Planet Venus  =new Planet(new Spheroid( 6051840,0,SunSatSeries96.satGM[2],2*Math.PI/(360.0/  -1.4813688)), new VenusAtmosphere(),      SunSatSeries96.satArray[2],new OrbiterRotatorEphemeris(OrbiterConfigPath+"Venus.cfg",VenusCenteredFixed));
  public static final Planet Earth  =new Planet(Spheroid.WGS84,                                                                  new KwanEarthAtmosphere(),  SunSatSeries96.satArray[3],new EarthRotIAU1980(J2000Equ,PEF));
  public static final Planet Mars   =new Planet(new Spheroid( 3396190,0,SunSatSeries96.satGM[4],2*Math.PI/(360.0/ 350.89198226)),new OrbiterMarsAtmosphere(),SunSatSeries96.satArray[4],new OrbiterRotatorEphemeris(OrbiterConfigPath+"Mars.cfg",MarsCenteredFixed));
  public static final Planet Jupiter=new Planet(new Spheroid( 71492e3,0,SunSatSeries96.satGM[5],2*Math.PI/(360.0/ 870.5366420)), new NoAtmosphere(),         SunSatSeries96.satArray[5],new OrbiterRotatorEphemeris(OrbiterConfigPath+"Jupiter.cfg",JupiterCenteredFixed));
  public static final Planet Saturn =new Planet(new Spheroid( 60268e3,0,SunSatSeries96.satGM[6],2*Math.PI/(360.0/ 810.7939024)), new NoAtmosphere(),         SunSatSeries96.satArray[6],new OrbiterRotatorEphemeris(OrbiterConfigPath+"Saturn.cfg",SaturnCenteredFixed));
  public static final Planet Uranus =new Planet(new Spheroid( 25559e3,0,SunSatSeries96.satGM[7],2*Math.PI/(360.0/-501.1600928)), new NoAtmosphere(),         SunSatSeries96.satArray[7],new OrbiterRotatorEphemeris(OrbiterConfigPath+"Uranus.cfg",UranusCenteredFixed));
  public static final Planet Neptune=new Planet(new Spheroid( 24764e3,0,SunSatSeries96.satGM[8],2*Math.PI/(360.0/ 536.3128492)), new NoAtmosphere(),         SunSatSeries96.satArray[8],new OrbiterRotatorEphemeris(OrbiterConfigPath+"Neptune.cfg",NeptuneCenteredFixed));
  public static final Planet Pluto  =new Planet(new Spheroid(  1151e3,0,SunSatSeries96.satGM[9],2*Math.PI/(360.0/- 56.3623195)), new NoAtmosphere(),         SunSatSeries96.satArray[9],new OrbiterRotatorEphemeris(OrbiterConfigPath+"Pluto.cfg",PlutoCenteredFixed));
  public static Planet[] Planets={Sun,Mercury,Venus,Earth,Mars,Jupiter,Saturn,Uranus,Neptune,Pluto};
  public static String[] PlanetNames={"Sun","Mercury","Venus","Earth","Mars","Jupiter","Saturn","Uranus","Neptune","Pluto"};
}
