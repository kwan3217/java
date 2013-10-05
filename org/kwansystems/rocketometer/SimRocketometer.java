/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kwansystems.rocketometer;

import java.io.*;
import java.util.zip.GZIPOutputStream;

import org.kwansystems.tools.vector.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.kalman.*;
import org.kwansystems.space.planet.*;
import org.kwansystems.space.planet.magnetosphere.*;

import static org.kwansystems.tools.Ticker.*;

/**
 *
 * @author chrisj
 */
public class SimRocketometer {

  
  public static BlackBrantModel blackBrant=new BlackBrantModel();
  public static void main(String[] args) throws IOException {
	tic();
    double t0=-1.0;
    double dt=0.003;    //match the sample rate of the sensors
    double t1=1000.0;   
    int n=(int)Math.floor((t1-t0)/dt);
    double[] t=new double[n];for(int i=0;i<n;i++)t[i]=i*dt+t0;

    MathVector x0=blackBrant.x0(Math.toRadians(86.6),Math.toRadians(9),0,1200,Math.toRadians(32.41785),Math.toRadians(-106.31994),t0);
  
    MathVector[] x=new MathVector[n];
    x[0]=x0;
    MathVector[] xd=new MathVector[n];
    xd[0]=blackBrant.fd(t0,x0);
    MathVector z0=blackBrant.g(t0,x0); //measurement vector, for size only
    MathVector[] z=new MathVector[n];
    toc("Allocated memory");
    z[0]=z0;
    int i=1;
    while(x[i-1].subVector(0,3).length()>(x0.subVector(0,3).length()-1) && i<n) {
      xd[i]=blackBrant.fd(t[i],x[i-1]);
      x[i]=MathVector.add(x[i-1],xd[i].mul(dt));
      x[i].setSubVector(6,xd[i].subVector(3,3)); //stick acceleration into state vector
      x[i].setSubVector(9,x[i].subVector(9,4).normal()); //renormalize quaternion
      z[i]=blackBrant.g(t[i],x[i]);
      i++;
    }
    toc("Finished simulation");
    n=i;
    double[] tn=new double[n];System.arraycopy(t,0,tn,0,n);t=tn;
    t1=t[n-1];
    MathVector[] xdn=new MathVector[n];System.arraycopy(xd,0,xdn,0,n);xd=xdn;
    MathVector[] xn =new MathVector[n];System.arraycopy(x ,0,xn ,0,n);x =xn;
    MathVector[] zn =new MathVector[n];System.arraycopy(z ,0,zn ,0,n);z =zn;
    toc(String.format("Truncated results to %d records",n));
    PrintWriter ouf=new PrintWriter(new FileWriter("Data/Rocketometry/SimRocketometerX.csv"));
    ouf.println("t,x_rx,x_ry,x_rz,x_vx,x_vy,x_vz,x_ax,x_ay,x_az,x_qx,x_qy,x_qz,x_qw,x_wx,x_wy,x_wz,x_m,xd_rx,xd_ry,xd_rz,xd_vx,xd_vy,xd_vz,xd_ax,xd_ay,xd_az,xd_qx,xd_qy,xd_qz,xd_qw,xd_wx,xd_wy,xd_wz,xd_m,z_ax,z_ay,z_az,z_bx,z_by,z_bz,z_gx,z_gy,z_gz,lat,lon,alt");
    for(i=0;i<n;i++) {
    	double[] lla=Planet.Earth.S.xyz2lla(x[i].subVector(0,3));
    	ouf.printf("%8.4f,%s,%s,%s,%f,%f,%f\n", t[i],x[i].toString(),xd[i].toString(),z[i].toString(),lla[0],lla[1],lla[2]);
    }
    ouf.close();
    toc("Finished writing CSV");
    ObjectOutputStream ouf2=new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream("Data/Rocketometry/SimRocketometer.serial")));
    ouf2.writeObject(t);
    ouf2.writeObject(x);
    ouf2.writeObject(xd);
    ouf2.writeObject(z);
    ouf2.close();
    toc("Finished writing serial");
  }
}
