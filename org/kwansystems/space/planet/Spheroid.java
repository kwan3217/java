package org.kwansystems.space.planet;

import org.kwansystems.tools.vector.*;

import static java.lang.Math.*;

/**
 * Object representing a prolate spheroid. Contains code and data to perform many calculations concerning geodetic coordinates
 * and second-order zonal gravity field calculations. 
 * <p>
 * The units customarily used are meters for length and seconds for time. If mass entered in, the preferred units would be kilograms, but it doesn't.
 * You can in fact use any length and time unit you like, but the equatorial radius, rotation rate, and gravitational constant should all use consistent
 * units with each other. I mean, you can use kilometers for equatorial radius and minutes for rotation rate, but the gravitational constant then should
 * be in km^3/min^2 instead of the normal m^3/s^2. If this is held to, all the derived constants and functions will have the same units as these three.
 */
public class Spheroid {
  /** WGS-84(G873) ellipsoid. This is defined in <a href="http://earth-info.nga.mil/GandG/publications/tr8350.2/tr8350_2.html">NIMA Technical Report 8350.2</a>, Third Edition, updated 23 Jun 2004 (TR8350.2). */
  public static Spheroid WGS84=     new Spheroid(6378137.0,1.0/298.257223563,398600.4418e9,7.292115e-5,398600.5e9,7.2921151467e-5);
  /** WGS-84(G873) ellipsoid, in uints of km and s. */
  public static Spheroid WGS84km=   new Spheroid(6378.137, 1.0/298.257223563,398600.4418  ,7.292115e-5,398600.5  ,7.2921151467e-5);
  /** WGS-72 ellipsoid. This is defined using the constants from the implementation of SGP4 */
  public static Spheroid WGS72=     new Spheroid(6378135.0,1.0/298.26,       398600.8e9,   7.292115e-5);
  /** Clarke 1866 Ellipsoid. Re and f constants found in TR8350.2, page A.1-1. GM and omega copied from WGS-84(G873) */
  public static Spheroid Clarke1866=new Spheroid(6378206.4,1.0/294.9786982,  398600.4418e9,7.292115e-5);
  /** Surface equatorial radius, m  */
  public final double Re;
  /** Planet gravity constant, m^3/s^2. Mass of planet times Newtonian gravitational constant G. */
  public final double GM;
  /** Planet gravity constant, for use in GPS ephemeris ONLY! */
  public final double GMgps;
  /** Flattening. Equals 1-(polar radius)/(equatorial radius) but is most commonly the defining constant, polar radius being calculated from it. */
  public final double f;
  /** Rotation rate, rad/s. This is in some precessing frame.  */
  public final double omega;
  /** Rotation rate, rad/s. This is in some different precessing frame. This one is used for calculating Trot*/
  public final double omegaPrime;

  //Calculated constants
  /** Rotation period, s */
  public final double Trot;
  /** Polar radius, m */
  public final double Rp;
  /** First eccentricity squared */
  public final double e2;
  /** First eccentricity */
  public final double e;
  /** Second eccentricity */
  public final double ep;
  /** Second eccentricity squared */
  public final double ep2;
  /** Normalized second-order zonal gravity field coefficient. This is calculated from the geometric parameters, and should not be
   * used for satellite orbit determination, but for the Earth, this matches the proper Cbar2,0 from EGM96 to one unit in the 8th significant
   * figure. */
  public final double Cbar20;
  /** Second-order zonal gravity field coefficient. This is calculated from the geometric Cbar2,0, and should not be
   * used for satellite orbit determination, but for the Earth, this matches the proper J2 to one unit in the 8th significant
   * figure. */
  public final double J2;
  /** Constant used to calculate authalic latitudes */
  public final double qp;
  /** Radius of sphere of equal surface area, m */
  public final double Rea;
  /** Radius of sphere of equal volume, m */
  public final double Rev;
  
  public String toString() {
    StringBuffer Result=new StringBuffer(
                  String.format("Equatorial Radius Re:    %11.3f\n",Re));
    Result.append(String.format("Flattening f:            1/%15.11f\n",1.0/f));
    Result.append(String.format("GravConst mu:            %15.12e\n",GM));
    Result.append(String.format("Rotation Rate omega:     %15.11e\n",omega));
    Result.append("---\n");
    Result.append(String.format("GPS GravConst:           %15.12e\n",GMgps));
    Result.append(String.format("omegaPrime:              %15.11e\n",omegaPrime));
    Result.append("---\n");
    Result.append(String.format("Rotation Period Trot:    %11.5f\n",Trot));
    Result.append(String.format("Polar Radius Rp:         %11.3f\n",Rp));
    Result.append(String.format("Equal Area Radius Rea:   %11.3f\n",Rea));
    Result.append(String.format("Equal Volume Radius Rev: %11.3f\n",Rev));
    Result.append(String.format("Geometric Cbar20:        %15.11e\n",Cbar20));
    Result.append(String.format("Geometric J2:            %15.11e\n",J2));
    Result.append(String.format("First Eccentricity e:    %15.11e\n",e));
    Result.append(String.format("                   e^2:  %15.11e\n",e2));
    Result.append(String.format("Second Eccentricity e':  %15.11e\n",ep));
    Result.append(String.format("                   e'^2: %15.11e\n",ep2));
    return Result.toString();
  }
  public static double ab_to_flat(double a, double b) {
    return 1.0-(b/a);
  }
  
  /** Create a spheroid from four fundamental constants and two alternate values.
   * @param LRe Equatorial radius, m
   * @param Lf Flattening (if less than 1.0) or Polar Radius, m (if ge than 1.0)
   * @param LGM Gravitational constant, m^3/s^2
   * @param Lomega Rotation rate, rad/s
   * @param LGMgps Alternate gravitational constant
   * @param LomegaPrime Alternate rotation rate
   */
  public Spheroid(double LRe, double Lf, double LGM, double Lomega, double LGMgps, double LomegaPrime) {
    Re=LRe;    
    GM=LGM;    
    GMgps=LGMgps; 
    if(Lf<1.0) {
      f=Lf;
    } else {
      f=1.0-(Lf/LRe);
    }
    omega=Lomega;    
    omegaPrime=LomegaPrime;    
    
    //Calculate derived constants
    Trot=2*Math.PI/omegaPrime;
    Rp=Re*(1-f);
    e2=f*(2-f);
    e=Math.sqrt(e2);
    ep=e/Math.sqrt(1-e2);
    ep2=ep*ep;
    double twoq0=(1.0+3.0/ep2)*Math.atan(ep)-3.0/ep;
    Cbar20=((4*omega*omega*Re*Re*Re*e*e*e)/(15*GM*twoq0)-e2)/(3*Math.sqrt(5));
    J2=Cbar20/(-Math.sqrt(0.2));
    qp=authalicQ(PI/2);
    Rea=Re*sqrt(qp/2);
    Rev=pow(Re*Re*Rp,1.0/3.0);
  }
  /** Create a spheroid from four fundamental constants. This one duplicates the alternate constants.
   * @param LRe Equatorial radius, m
   * @param Lf Flattening or Polar Radius, m
   * @param LGM Gravitational constant, m^3/s^2
   * @param Lomega Rotation rate, rad/s
   */
  public Spheroid(double LRe, double Lf, double LGM, double Lomega) {
    this(LRe,Lf,LGM,Lomega,LGM,Lomega);
  }
  public static MathVector llr2xyz(double lat, double lon, double r) {
    MathVector result=new MathVector(cos(lat)*cos(lon),cos(lat)*sin(lon),sin(lat));
    return result.mul(r);
  }
  public static MathVector llr2xyz(MathVector llr) {
    return llr2xyz(llr.X(),llr.Y(),llr.Z());
  }
  public static MathVector xyz2llr(MathVector xyz) {
    double r=xyz.length();
    double lat=asin(xyz.Z()/r);
    double lon=atan2(xyz.Y(),xyz.X());
    return new MathVector(lat,lon,r);
  }
  public MathVector lla2xyz(double lat, double lon, double alt) {
    MathVector result=lla2xyz1(lat,lon,alt/Re);
    return result.mul(Re);
  }
  public MathVector lla2xyz(MathVector lla) {
    return lla2xyz(lla.X(),lla.Y(),lla.Z());
  }
  private MathVector lla2xyz1(double lat, double lon, double alt) {
    /* Ellipsoid equatorial radius is 1 unit */
    /* Ellipsoid polar radius, units */
    double b=1-f;
    double b2=b*b;
    /* Square of ellipsoid eccentricity */
    double e2=(1-b2);
    double N = 1.0 / sqrt(1.0 - e2 * pow(sin(lat),2));
    double x = (N              + alt) * cos(lat) * cos(lon);
    double y = (N              + alt) * cos(lat) * sin(lon);
    double z = (N * (1.0 - e2) + alt) * sin(lat);
    return new MathVector(x,y,z);
  }
  /** Converts position vector to navigation coordinates.
     [lat,lon,alt]=xyz2lla(x,y,z) returns geodetic latitude and longitude in radians,
     and altitude above ellipsoid in meters. North latitude and East longitude
     are positive numbers. ALL LOCATIONS IN THE USA WILL HAVE NEGATIVE LONGITUDES!
     This program uses a closed-form algorithm. By default, this uses the size
     and shape of the WGS-84 ellipsoid, in meters.
     <p>
     <a href=http://www.astro.uni.torun.pl/~kb/Papers/geod/Geod-GK.htm>http://www.astro.uni.torun.pl/~kb/Papers/geod/Geod-GK.htm</a> (in polish,
     but contains a fortran script commented in english)<br>
     <a href=http://www.astro.uni.torun.pl/~kb/Papers/ASS/Geod-ASS.htm>http://www.astro.uni.torun.pl/~kb/Papers/ASS/Geod-ASS.htm</a> (Derivation
     in english)
     <p>
     Example: The south goalpost at Folsom Field, University of Colorado
     at Boulder is located at:
     <table>
     <tr><td>X (Meters)</td><td>Y (Meters)</td><td>Z (Meters)</td></tr>  
     <tr><td>-1288488.9373</td><td>-4720620.9617</td><td>4079778.3407</td></tr>
     </table>
     running XYZ2LLA using WGS84 returns the following
     <table>
     <tr><td>Latitude (radians)</td><td>Longitude (radians)</td><td>Altitude (Meters)</td></tr>  
     <tr><td>0.69828684115439</td><td>-1.83725477406124</td><td>1612.59993154183</td></tr>
     </table>
     @param xyz Cartesian spheroid-fixed coordinate to start with. Should be in meters
     @return A MathVector where the first element is the latitude in radians, second is longitude in radians, and third is height above spheroid surface in meters
   */
  public MathVector xyz2lla(MathVector xyz) {
    if(f==0) {
      double lat=Math.PI/2-MathVector.vangle(new MathVector(0,0,1), xyz);
      double lon=Math.atan2(xyz.Y(),xyz.X());
      double alt=xyz.length()-Re;
      return new MathVector(lat,lon,alt);
    }
    double x=xyz.X();
    double y=xyz.Y();
    double z=xyz.Z();
    double a=Re; //To match notation
    //Nothing special about this
    double lon=atan2(y,x);

    // Length of projection of vector to equatorial plane, m
    double r=hypot(x,y);
    // x or y should not appear below here
    // Ellipsoid z radius
    double b=dsign(a*(1.0-f),z);
    double lat,alt;
 
    // On the rotation axis?
    if(0==r) {
      // Yup, we are at a pole. Take the quick way out
      lat=dsign(PI/2,z);
      alt=abs(z)-abs(b);
    } else if (0==z) {// On the equator?
      lat=0;
      alt=r-a;
    } else {
      // Nope, chug through the hard part
      double E=((z+b)*b/a-a)/r;
      double F=((z-b)*b/a+a)/r;
      double P=4.0*(E*F+1.0)/3.0;
      double Q=(E*E-F*F)*2.0;
      double D=P*P*P+Q*Q;
      double v;
      if(D>=0.0) {
        double s=sqrt(D)+Q;
        s=dsign(Math.pow(abs(s),(1.0/3.0)),s);
        v=P/s-s;
        v=-(2*Q+v*v*v)/(3*P);
      } else {
        v=2.0*sqrt(-P)*cos(acos(Q/P/sqrt(-P))/3.0);
      }
      double G=(E+sqrt(E*E+v))/2.0;
      double t=sqrt(G*G+(F-v*G)/(2*G-E))-G;
      lat=atan((1.0-t*t)*a/(2*b*t));
      alt=(r-a*t)*cos(lat)+(z-b)*sin(lat);
    }
    return new MathVector(lat,lon,alt);
  }

  /**
   *This is a standard function in FORTRAN to return a number with the same
   *magnitude as a, with the sign of b.
   * @param a
   * @param b
   * @return number with magnitude of a, and sign of b
   */
  private static double dsign(double a,double b) {
    return abs(a)*signum(b);
  }

  /** Auxiliary function used in calculation of authalic (equal-area) latitude and related constants
   * @param phi Geodetic latitude, radians
   * @return Authalic Q parameter
   */
  public double authalicQ(double phi) {
    double s=sin(phi);
    return (1-e*e)*(s/(1-e*e*s*s)-1/(2*e)*log((1-e*s)/(1+e*s)));
  }
  /** Calculate the Authalic Latitude. This latitude is defined as such: The area of a zone on a spheroid between two geodetic latitudes is equal to the
   * area of the zone between the corresponding authalic latitudes (used as geocentric latitudes) on a sphere of equal total surface area to that of the
   * ellipsoid.
   * @param phi Geodetic latitude, radians
   * @return Authalic latitude, radians
   */
  public double authalic(double phi) {
    return asin(authalicQ(phi)/qp);
  }
  
  public double curvatureM(double phi) {
    double s=sin(phi);
    return (Re*(1-e2))/pow(sqrt(1-e2*s*s),3);
  }

  public double curvatureN(double phi) {
    double s=sin(phi);
    return Re/sqrt(1-e2*s*s);
  }

  public double curvatureR(double phi) {
    double s=sin(phi);
    return (Re*sqrt(1-e2))/sqrt(1-e2*s*s);
  }

  public static void main(String[] args) {
    MathVector result=WGS84.xyz2lla(new MathVector(-1288488.9373,-4720620.9617,4079778.3407));
    MathVector test=new MathVector(0.69828684115439,-1.83725477406124,1612.59993154183);
    System.out.println("Result of computation: "+result);
    System.out.println("Comparison value:      "+test);
  }

}
