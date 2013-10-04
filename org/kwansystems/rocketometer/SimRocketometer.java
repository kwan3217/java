/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kwansystems.rocketometer;

import org.kwansystems.tools.vector.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.space.planet.*;
import org.kwansystems.space.planet.magnetosphere.*;

/**
 *
 * @author chrisj
 */
public class SimRocketometer {
  public static void main(String[] args) {
    double t0=-1.0;
    double dt=0.003;    //match the sample rate of the sensors
    double t1=1000.0;   
    int n=(int)Math.floor((t1-t0)/dt);
    double[] t=new double[n];for(int i=0;i<n;i++)t[i]=i*dt+t0;

    MathVector x0=blackbrant_x0(el=86.6d*!dtor,az=9d*!dtor,spd=0d,alt=1200d,lat=32.41785d*!dtor,lon=-106.31994d*!dtor,t0=t0);
  
    MathVector[] x=new MathVector[n];
    MathVector[] xd=new MathVector[n];
    x[0]=x0;
    MathVector z0=g_rocketometer(t0,x0); //measurement vector, for size only
    MathVector[] z=new MathVector[n];
    z[0]=z0;
    int i=1;
    while(x[i-1].subVector(0,3).length()>(x0.subVector(0,3).length()-1) && i<n) {
      xd[i]=fd_blackbrant(t[i],x[i-1]);
      x[i]=MathVector.add(x[i-1],xd[i].mul(dt));
      x[i].setSubVector(6,xd[i].subVector(3,3)); //stick acceleration into state vector
      x[i].setSubVector(9,x[i].subVector(9,4).normal()); //renormalize quaternion
      z[i]=g_rocketometer(t[i],x[i]);
      i++;
    }
    n=i;
    double[] tn=new double[n];System.arraycopy(t,0,tn,0,n);t=tn;
    t1=t[n-1];
    MathVector[] xdn=new MathVector[n];System.arraycopy(xd,0,xdn,0,n);xd=xdn;
    MathVector[] xn =new MathVector[n];System.arraycopy(x ,0,xn ,0,n);x =xn;
    MathVector[] zn =new MathVector[n];System.arraycopy(z ,0,zn ,0,n);z =zn;
    //save,filename='BlackBrantClean.sav',/compress
    //toc,'Done saving simulation'
  }
  private static MathVector blackbrant_x0(double launch_el,double launch_az,double rail_exit_spd, double rail_alt,double launch_lat,double launch_lon,double t0) {
    //state at rail exit
    MathVector r0=Planet.Earth.lla2xyz(launch_lat,launch_lon,rail_alt);
    double m0=1700.15+Math.max(t0,0)*(-110.56021d); //mass of fuel only, at rail exit (or fully loaded if t0 lt 0)
  
    //local topocentric system - Z points at zenith, E points at East in horizon, N points at North in horizon
    MathVector z=r0.normal();
    MathVector e=MathVector.cross(MathVector.K,z);e.normalEq();
    MathVector n=MathVector.cross(z,e);n.normalEq();
  
  p_r=transpose(z*sin(launch_el)+e*sin(launch_az)*cos(launch_el)+n*cos(launch_az)*cos(launch_el))
  t_r=transpose(e)
  
  ;body axis - Z is towards nose, X and Y are perpendicular. X is arbitrarily "left wing" and Y is arbitrarily "tail"
  p_b=transpose([0d,0,1])
  t_b=transpose([0d,1,0])
  ;point_toward returns a b2r matrix, we want an r2b quaternion
  q0=quat_to_mtx(/inv,transpose(point_toward(p_r=p_r,p_b=p_b,t_r=t_r,t_b=t_b)))
    
  v0=p_r*rail_exit_spd+vwind(r0)
  w0=[0d,0,0]  ;body rotation rates, rad/s
  x0=transpose([r0,v0[*],[0,0,0],q0[*],w0,m0])
  xd=call_function('fd_blackbrant',t0,x0)
  x0[6:8]=xd[3:5] ;stick acceleration into state vector

  return,x0
end
  public static double blackbrant_drymass(double t) {
    double m=443.89;     //payload mass
    if(t<90) m+=273.784; //black brant shell mass
    if(t<6.2)m+=320.504; //terrier shell mass
    return m;
  }
  private static EarthMagKWMM mag=new EarthMagKWMM(2010);
  public static MathVector g_rocketometer(double t,MathVector x) {
    MathVector r=x.subVector(0,3);
    MathVector v=x.subVector(3,3);
    MathVector a=x.subVector(6,3);
    Quaternion q=new Quaternion(x.subVector(9,4));
    MathVector w=x.subVector(13,3);
    a.sub(Planet.Earth.Gravity(r)); //non-grav acceleration in reference frame
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
//    b_dn=dblarr(3)
    //body frame rotation rate  
    MathVector g_dn=w.mul(32768.0/(2000.0*(Math.PI/180.0)));
    return new MathVector(new MathVector[] {a_dn,b_dn,g_dn}); //accelerometer measurement
  }



}
