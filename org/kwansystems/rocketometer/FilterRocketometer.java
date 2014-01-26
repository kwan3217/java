package org.kwansystems.rocketometer;

import java.io.*;
import java.util.zip.*;
import static org.kwansystems.tools.Ticker.*;

import org.kwansystems.kalman.*;
import org.kwansystems.tools.rotation.MathMatrix;
import org.kwansystems.tools.vector.*;

public class FilterRocketometer {
  public static void main(String args[]) throws IOException, ClassNotFoundException {
	tic();
	ObjectInputStream inf=new ObjectInputStream(new GZIPInputStream(new FileInputStream("Data/Rocketometry/SimRocketometer.serial")));
	double[] t=(double[])inf.readObject();
	MathVector[] s=(MathVector[])inf.readObject();
	MathVector[] sd=(MathVector[])inf.readObject();
	MathVector[] z=(MathVector[])inf.readObject();
	inf.close();
	System.out.println(t.length);
	MathVector x0=s[0];
	MathMatrix PP=new MathMatrix(new double[] {1,1,1,0.1,0.1,0.1,0.1,0.1,0.1,1e-3,1e-3,1e-3,1e-3,1e-3,1e-3,1e-3,1});
	MathMatrix QQ=new MathMatrix(new double[] {
		0,0,0, //no process noise on position
        0,0,0, //no process noise on velocity
       10,10,10, //Lots of process noise on acceleration
        0,0,0,0, //no process noise on quaternion
       10,10,10, //Lots of process noise on rotation rate
       0});      //no process noise on mass 
	MathMatrix RR=new MathMatrix(new double[] {100,100,100,100,100,100,100,100,100}); //measurement noise, 10DN on each axis, uncorrelated
    MathVector[] x=new MathVector[t.length];
    MathVector[] ZZ=new MathVector[t.length];
	SimModel model=new RocketometerModel();
	UnscentedKalman filter=new UnscentedKalman(x0,PP,model);
	for(int i=1;i<t.length;i++) {
	  filter.update(t[i-1], t[i], 1, z[i], QQ, RR);
	  x[i]=new MathVector(filter.x);
	  ZZ[i]=new MathVector(filter.ZZ);
	  if(i%1000==0) toc(String.format("Done with %d points, %f seconds",i,t[i]));
	}
	ObjectOutputStream ouf=new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream("Data/Rocketometry/FilterRocketometer.serial")));
	ouf.writeObject(t);
	ouf.writeObject(s);
	ouf.writeObject(sd);
	ouf.writeObject(z);
	ouf.writeObject(x);
	ouf.writeObject(ZZ);
	toc("Done with saving data");
  }
}
