package org.kwansystems.rocketometer;

import org.kwansystems.space.planet.Planet;
import org.kwansystems.space.planet.Spheroid;
import org.kwansystems.space.planet.magnetosphere.EarthMagKWMM;
import org.kwansystems.tools.rotation.Quaternion;
import org.kwansystems.tools.vector.MathVector;
import org.kwansystems.kalman.*;

public class RocketometerModel extends SimModel {
   private static EarthMagKWMM mag=new EarthMagKWMM(2010);
	@Override
	public MathVector fd(double t, MathVector x) {
	    MathVector r=x.subVector(0,3);
	    MathVector v=x.subVector(3,3);
	    MathVector a=x.subVector(6,3);
	    Quaternion q=new Quaternion(x.subVector(9,4));
	    MathVector w=x.subVector(13,3);
				  
        Quaternion qd=q.mul(w).mul(0.5);

		//return results
        MathVector xd=new MathVector(x.dimension());
		xd.setSubVector(0,v);
		xd.setSubVector(3,a);
		xd.setSubVector(9,qd.toVector());
	  return xd;
	}
	@Override
	public MathVector g(double t, MathVector x) {
	    MathVector r=x.subVector(0,3);
	    MathVector v=x.subVector(3,3);
	    MathVector a=x.subVector(6,3);
	    Quaternion q=new Quaternion(x.subVector(9,4));
	    MathVector w=x.subVector(13,3);
	    a.subEq(Planet.Earth.Gravity(r)); //non-grav acceleration in reference frame
	    a=q.transform(a);
	    MathVector a_dn=a.mul(32768.0/160.0);
	//  a_dn+=randomn(seed,n_elements(a_dn))*11
	//  a_dn=a_dn>(-32768)<(32767)
	//  a_dn=fix(a_dn)

	    //local topocentric system
	    
	    double[] lla=Planet.Earth.S.xyz2lla(r);
	  
	    MathVector z=Spheroid.llr2xyz(lla[0],lla[1],1);
	    MathVector e=MathVector.cross(MathVector.K,z);e.normalEq();
	    MathVector n=MathVector.cross(z,e);           n.normalEq();
	    //magnetic field in local system
	    MathVector b_ned=mag.calcProps(lla[2],lla[0],lla[1],2013.8).B.mul(1e-9*1e4); //convert from nanoTesla to Gauss  
	    MathVector b_i=MathVector.sub(MathVector.add(n.mul(b_ned.X()),e.mul(b_ned.Y())),z.mul(b_ned.Z()));
	    MathVector b_dn=q.transform(b_i).mul(1090); //Rocketometer runs at HMC5883L gain setting 1, 1090DN/Gauss
//	    b_dn=dblarr(3)
	    //body frame rotation rate  
	    MathVector g_dn=w.mul(32768.0/(2000.0*(Math.PI/180.0)));
	    return new MathVector(new MathVector[] {a_dn,b_dn,g_dn}); //accelerometer measurement
	}
}
