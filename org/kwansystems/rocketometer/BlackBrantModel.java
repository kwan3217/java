package org.kwansystems.rocketometer;

import org.kwansystems.kalman.SimModel;
import org.kwansystems.space.planet.Planet;
import org.kwansystems.space.planet.Spheroid;
import org.kwansystems.space.planet.magnetosphere.EarthMagKWMM;
import org.kwansystems.tools.rotation.MathMatrix;
import org.kwansystems.tools.rotation.Quaternion;
import org.kwansystems.tools.vector.MathVector;

public class BlackBrantModel extends RocketometerModel {
	  public static double blackbrant_drymass(double t) {
		    double m=443.89;     //payload mass
		    if(t<90) m+=273.784; //black brant shell mass
		    if(t<6.2)m+=320.504; //terrier shell mass
		    return m;
		  }

	@Override
	public MathVector fd(double t, MathVector x) {
    MathVector r=x.subVector(0,3);
	  MathVector v=x.subVector(3,3);
	  MathVector a=x.subVector(6,3);
	  Quaternion q=new Quaternion(x.subVector(9,4));
	  MathVector w=x.subVector(13,3);
	  double m=x.get(16)+blackbrant_drymass(t);
		  
	  //gravity
	  MathVector a_grav=Planet.Earth.Gravity(r);
		  
    //ground support
	  MathVector a_ground;
	  if(t<0) {
		//centripetal force keeping the rocket the same distance from the axis
		double v_cf=v.subVector(0,2).length();
		double r_cf=r.subVector(0,2).length();
		MathVector a_cf=new MathVector(-r.get(0)*v_cf*v_cf/(r_cf*r_cf),-r.get(1)*v_cf*v_cf/(r_cf*r_cf),0);
		a_ground=MathVector.sub(a_cf,a_grav);
	  } else {
		a_ground=new MathVector(0,0,0);
	  }

    //drag
    MathVector vrel=MathVector.sub(v,Planet.Earth.Wind(r));
    MathVector a_drag;
	  double v2=vrel.lensq();
	  if(v2==0) { 
		a_drag=new MathVector(0,0,0);
	  } else {
		MathVector vh=vrel.normal();
		double rho=Planet.Earth.Air(r).Density;
		double CdA=0.045;
		if(t<6.2) CdA*=2;
		if(t>400) CdA=2;
		double dynpres=rho*v2*0.5;
		a_drag=vh.mul(-CdA*dynpres/m);
	  }

	  //thrust
	  double v_e,md;
	  MathVector a_thrust=new MathVector(0,0,0);
	  if(t<0) {
		v_e=0;
		md=0;
	  } else if(t<6.2) {
		v_e=2305.3782;
		md=-110.56021;
	  } else if(t<12) {
		v_e=0;
		md=0;
	  } else if(t<50.22) {
		v_e=2305.3782;
		md=-26.548466d;
	  } else {
		v_e=0;
		md=0;
	  }
	  if(v_e>0) {
	    double FF=-md*v_e;
	    double aa=FF/m;
  	  //state quaternion is r2b, so use conjugate to get b2r
        MathVector f=q.invTransform(MathVector.K); //Thrust direction is body +Z axis
    	a_thrust=f.mul(aa);
	  }
	  //roll rate
    double wzd;
    if(t<0) {
		wzd=0;
    } else if(t<6.2) {
		double dwz=2.0*(2.0*Math.PI); //change in rotation rate over this segment, rad/s
		double dt=6.2;                //length of this segment, s
		wzd=dwz/dt;
    } else if(t<12) {
		double dwz=(1.4d -2d)*(2.0*Math.PI);
		double dt=12.0-6.2;
		wzd=dwz/dt;
    } else if(t<50.22) {
		double dwz=(4.4 -1.4)*(2.0*Math.PI);
		double dt=50.22-12.0;
      wzd=dwz/dt;
    } else if(t<90) {
		double dwz=0;
		double dt=90.0-50.22;
		wzd=dwz/dt;
    } else if (t<95) {
		double dwz=(0.0 - 4.4)*(2.0*Math.PI);
		double dt=95.0-90.0;
		wzd=dwz/dt;
    } else {
		wzd=0;
    }
	  MathVector wd=MathVector.K.mul(wzd);
	  Quaternion qd=q.mul(w).mul(0.5);

	  //return results
	  MathVector vd=new MathVector(a_grav);
	  vd.addEq(a_drag);
	  vd.addEq(a_thrust);
	  vd.addEq(a_ground);
	  MathVector xd=new MathVector(new MathVector[] {v,vd,new MathVector(0,0,0),qd.toVector(),wd,new MathVector(new double[] {md})});
	  return xd;
	}

	public MathVector x0(double launch_el,double launch_az,double rail_exit_spd, double rail_alt,double launch_lat,double launch_lon,double t0) {
		    //state at rail exit
		    MathVector r0=Planet.Earth.S.lla2xyz(launch_lat,launch_lon+t0*Planet.Earth.S.omegaPrime,rail_alt);
		    double m0=1700.15+Math.max(t0,0)*(-110.56021d); //mass of fuel only, at rail exit (or fully loaded if t0 lt 0)
		  
		    //local topocentric system - Z points at zenith, E points at East in horizon, N points at North in horizon
		    MathVector z=r0.normal();
		    MathVector e=MathVector.cross(MathVector.K,z);e.normalEq();
		    MathVector n=MathVector.cross(z,e);           n.normalEq();
		    MathVector launch_nez=Spheroid.llr2xyz(launch_el,launch_az,1);
		    MathVector p_r=MathVector.add(MathVector.add(z.mul(launch_nez.Z()),e.mul(launch_nez.Y())),n.mul(launch_nez.X()));
		    MathVector t_r=new MathVector(e);
		  
		    //;body axis - Z is towards nose, X and Y are perpendicular. X is arbitrarily "left wing" and Y is arbitrarily "tail"
		    MathVector p_b=new MathVector(MathVector.K);
		    MathVector t_b=new MathVector(MathVector.J);
		    //point_toward returns a b2r matrix, we want an r2b quaternion
		    Quaternion q0=MathMatrix.pointToward(p_r,t_r,p_b,t_b).toQuaternion().conj();
		    //test case - this quaternion should point p_r at K in body frame
		    System.out.println(q0.transform(p_r));
		    
		    MathVector v0=MathVector.add(p_r.mul(rail_exit_spd),Planet.Earth.Wind(r0));
		    MathVector a0=new MathVector(0,0,0);
		    MathVector w0=new MathVector(0,0,0);  //body rotation rates, rad/s
		    MathVector x0=new MathVector(new MathVector[] {r0,v0,a0,q0.toVector(),w0,new MathVector(new double[] {m0})});
		    MathVector xd=fd(t0,x0);
		    x0.setSubVector(6,xd.subVector(3,3)); //stick acceleration into state vector

		    return x0;
		  }
	  
}
