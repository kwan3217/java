package org.kwansystems.space.asen5050;

import org.kwansystems.tools.rotation.MathMatrix;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.vector.*;

public class Frames {
  public static MathVector ECItoECEF(MathVector PosECI,double ThetaGST) {
	return MathMatrix.Rot3(ThetaGST).transform(PosECI);
  }
  public static MathVector ECEFtoECI(MathVector PosECEF,double ThetaGST) {
	return MathMatrix.Rot3(-ThetaGST).transform(PosECEF);
  }
  public static final double WGSRe=6378.137;          //WGS84 ellipsoid equatorial radius, km
  public static final double WGSE2=0.00669437999013;  //WGS84 Square of meridian eccentricity
  public static final double JGMRe=6378.1363;         //JGM2 ellipsoid equatorial radius, km
  public static final double JGME2=0.006694385000;    //JGM2 Square of meridian eccentricity
  //Input: geodetic latitude, longitude in radians, Altitude in km above ellipsoid
  //Output: ECEF vector in km
  public static MathVector LLAtoECEF(double Lat, double Lon, double Alt) {
    double C=JGMRe/Math.sqrt(1-JGME2*Math.pow(Math.sin(Lat),2));
    double S=C*(1-JGME2);
    double R=(C+Alt)*Math.cos(Lat);
    return new MathVector(R*Math.cos(Lon),
                          R*Math.sin(Lon),
                          (S+Alt)*Math.sin(Lat));
  }
  public static double ECEFtoAlt(MathVector PosECEF) {
	return PosECEF.length()-JGMRe;
  }
  public static double SEZtoR(MathVector PosSEZ) {
	return PosSEZ.length();
  }
  public static double SEZtoEl(MathVector PosSEZ) {
	double r=Math.sqrt(Math.pow(PosSEZ.X(),2)+Math.pow(PosSEZ.Y(),2));
	return Math.atan2(PosSEZ.Z(),r);
  }
  public static double SEZtoAz(MathVector PosSEZ) {
	double Az=Math.atan2(PosSEZ.Y(),-PosSEZ.X());
	if(Az<0) Az+=2*Math.PI;
	return Az;
  }
  public static MathVector ECEFtoSEZ(MathVector PosECEF, MathVector ToposECEF) {
	MathVector PosToposIJK=MathVector.sub(PosECEF,ToposECEF);
	double Lat=ECEFtoLat(ToposECEF);
	double Lon=ECEFtoLon(ToposECEF);
	MathMatrix IJKtoSEZ=MathMatrix.mul(MathMatrix.Rot2(Math.PI/2-Lat),MathMatrix.Rot3(Lon));
	return IJKtoSEZ.transform(PosToposIJK);
  }
  public static double ECEFtoLat(MathVector PosECEF) {
    double r=Math.sqrt(Math.pow(PosECEF.X(),2)+Math.pow(PosECEF.Y(),2));
    return Math.atan2(PosECEF.Z(),r);
  }
  public static double ECEFtoLon(MathVector PosECEF) {
    return Math.atan2(PosECEF.Y(),PosECEF.X());
  }
  private static double Modulus(double arg1,double arg2) {
    double modu;
    modu = arg1 - Math.floor(arg1/arg2) * arg2;
    if(modu >= 0) {
      return modu;
    } else {
      return modu + arg2;
    }
  } //Function Modulus
  public static double Gst(double JD) {
    double Epoch=2451910.5; //From MICA - JD of 1 Jan 2001 0:00UT1 is 2451910.5
    //From MICA - GMST at 1 Jan 2001 0:00UT1 is 6h42m51.5354s
    double GMSTEpoch=2*Math.PI*(6*3600+42*60+51.5354)/86400.0; //radians
    double OmegaE=7.2921158553E-05; //radians/sec
    return Modulus((JD-Epoch)*86400.0*OmegaE+GMSTEpoch,2*Math.PI);
  }
  public static double Gst(Time T) {
    return Gst(T.JD());
  }
}
