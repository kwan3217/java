package org.kwansystems.kalman;

import java.io.*;
import java.util.Random;

import org.kwansystems.tools.vector.*;
import org.kwansystems.tools.rotation.*;
import static org.kwansystems.tools.Ticker.*;

public class UnscentedKalman {
	public MathVector x;
	public MathMatrix PP;
	public MathVector ZZ;
	public MathMatrix KK;
	public int n;
	private SimModel model;
	public UnscentedKalman(MathVector x0, MathMatrix PP0, SimModel Lmodel) {
		x=new MathVector(x0);
		PP=new MathMatrix(PP0);
		model=Lmodel;
		n=x.dimension();
	}

	public void update(double t0, double t1, int nstep, MathVector z,MathMatrix QQ, MathMatrix RR) {
		//Unscented Kalman filter with additive noise, therefore unaugmented
		//General-purpose implementation of the Unscented Kalman filter for a continuous 
		//  process measured at discrete times. The filter has additive noise, and
		//  is therefore unaugmented. See TutorialUKF.pdf on the wiki
		//Two function reference arguments to describe the problem (abstract methods in this Java implementation)
		//  fd -   Physics function dx/dt=fdot(x,t). Figures the derivative of the state 
		//           given the current state and time
		//  g  - Observation function g=g(x,t). Figures the observation vector from the
		//           state given the current time
		//Three arguments describing the current state
		//  t0 - A priori time stamp (presumed completely accurate)
		//  xkm1 - A priori State vector estimate, valid at t0
		//  PPkm1 - A priori state vector estimate covariance, valid at t0
		//Five arguments describing the propagation and observation
		//  t1 - A posteriori time stamp
		//  nstep - Number of integration steps to take between t0 and t1
		//  z - Observation vector, valid at t1
		//  QQ - Process noise covariance between t0 and t1
		//  RR - Observation noise covariance at t1
		//Return arguments (public fields in this Java implementation)
		//  xk - A posteriori state vector estimate, valid at t1
		//  PP= - A posteriori state vector estimate covariance, valid at t1
		//  ZZ= - Measurement innovation
		//  KK= - Kalman gain
		int m=z.dimension();

		//Create the sigma points
		double WW0=1.0/3.0; //Suggested for Gaussian noises
		double WWj=(1-WW0)/(2*n); 
		double[] WW=new double[2*n+1];
		WW[0]=WW0;
		for(int i=1;i<WW.length;i++) WW[i]=WWj;   //  Weighting of sigma points
		MathVector[] xjkm1=new MathVector[n*2+1]; //  Matrix of sigma points [1,state,n_sigma_points]
		xjkm1[0]=new MathVector(x);               //  Each column is a single sigma point
		//  First sigma point is the original state
		MathMatrix rPPkm1=PP.choldc();            //  Matrix square root (lower triangluar, P=A*A')
		rPPkm1.mulEq(Math.sqrt(n/(1-WW0)));              // scale to do sigma points
		//UKF1, p6 (406), footnote 5: 
		//"If the matrix square root [A] of [P] is of the form [P]=[A^T][A], then
		//the sigma points are formed from the ''rows'' of [A]. However, if the
		//matrix square root is of the form [P]=[A][A^T], the ''columns'' are
		//used. "
		//Matlab produces an upper triangular P=A'*A, so it matches the first form.
		//IDL can do either, and we want to use the lower form so we can use cholupdate
		//so we need to use columns. Java MathMatrix does lower, since it follows Wikipedia.
		//This will result in a vector with n nonzero
		//components for the first dimension and 1 nonzero component for the last.
		//Doing this wrong results in 1 nonzero component for the first dimension
		//and n for the last.
		for(int i=0;i<n;i++) {
			//Positive sigma point
			xjkm1[i+1  ]=MathVector.add(x,rPPkm1.getCol(i));   // (7) taking columns of rPPkm1
			//Negative sigma point
			xjkm1[i+1+n]=MathVector.sub(x,rPPkm1.getCol(i));   // (8) taking columns of rPPkm1
		}

		//Model forecast
		MathVector[] xfjk=new MathVector[2*n+1];   //Keep track of each transformed sigma point
		MathVector xfk=new MathVector(n);                  //accumulate forecast estimate
		for(int j=0;j<=2*n;j++) {
			xfjk[j]=model.eval_fd(t0,xjkm1[j],t1,nstep);   // (11)
			xfk.addEq(xfjk[j].mul(WW[j]));              // (12)
		}
		MathMatrix PPfk=new MathMatrix(QQ);           //Accumuate forecast covariance, start with additive process noise
		for(int j=0;j<=2*n;j++) {
			MathVector T1=MathVector.sub(xfjk[j],xfk);
			MathMatrix T2=MathMatrix.mul(new MathMatrix(new MathVector[]{T1},false),new MathMatrix(new MathVector[]{T1},true));
			PPfk.addEq(T2.mul(WW[j]));                  // (13)
		}

		//Measurement forecast (TutorialUKF.pdf is wrong, should put forecast, not original
		//                      estimate, through observation transform. UKF0.pdf 
		//                      shows the algorithm as actually done below.)
		MathVector[] zfjk=new MathVector[2*n+1];     //Keep track of each transformed sigma point
		MathVector zfk=new MathVector(m);            //accumulate forecast measurement
		for(int j=0;j<=2*n;j++) {
			zfjk[j]=model.g(t1,xfjk[j]);                     // (14)
			zfk.addEq(zfjk[j].mul(WW[j]));             // (15)
		}
		MathMatrix Gamma=new MathMatrix(RR);         //Accumulate innovation covariance (paper Cov(z~fkm1)), start with additive measurement noise
		MathMatrix SS=new MathMatrix(n,m);           //Accumulate cross covariance      (paper Cov(x~fk,z~fkm1))
		for(int j=0;j<=2*n;j++) {
			MathVector T1=MathVector.sub(zfjk[j],zfk);
			MathMatrix T2=MathMatrix.mul(new MathMatrix(new MathVector[]{T1},false),new MathMatrix(new MathVector[]{T1},true));
			MathVector T3=MathVector.sub(xfjk[j],xfk);
			MathMatrix T4=MathMatrix.mul(new MathMatrix(new MathVector[]{T3},false),new MathMatrix(new MathVector[]{T1},true));

			Gamma.addEq(T2.mul(WW[j]));                // (16)
			SS.addEq(T4.mul(WW[j]));                   // (17)
		}

		//Data Assimilation
		KK=MathMatrix.mul(SS,Gamma.inv());
		//  ;KKk=SSk/Gammak;                     // (19)
		ZZ=MathVector.sub(z,zfk);     // Innovation, calculated separately so can be returned
		x=MathVector.add(xfk,KK.transform(ZZ));                       // (18)
		PP=MathMatrix.sub(PPfk,MathMatrix.mul(KK,MathMatrix.mul(Gamma,KK.T())));  // (20) This is not the (1-KH)P that the linear filter uses
		//      because H is not directly available. The linear filter
		//      can be manipulated to this form, see the Library for details
	}
	public static void main(String[] args) throws IOException {
		tic();

		Random rand=new Random(3217);

		//timing
		double tf=200;           //Total time of flight, s. Cut this off before it hits the ground, please! Ground is unmodeled
		double fps=10;           //Time between measurements, s
		double deltat=1/fps;
		int nstep=2;
		double deltatstep=deltat/nstep;  //Time between propagation steps, s
		int NN=(int)(tf*fps);            // total dynamic steps

		MathVector s=new MathVector(new double[] {6500.4,349.14,-1.8093,-6.7967,0});  // initial true state
		int n=s.dimension();
		MathVector x=new MathVector(s);
		x.set(4,0.6392);                                    // initial state estimate
		MathMatrix PP = new MathMatrix(new double[] {1e-6,1e-6,1e-6,1e-6,1});// initial state covraiance
		//Time histories of various things. In all cases except time stamp, these are grids of vectors or matrices. 
		//All vectors in this algorithm are column vectors, which are represented in IDL as [1,x] arrays. The grid 
		//indices are always time step third and filter implemnetation (if needed) fourth
		double[] tV  = new double[NN+1];         //Time stamps [time steps]
		MathVector[] sV  = new MathVector[NN+1];     //actual state vector [1,state vector component, step]
		MathVector[] zV  = new MathVector[NN+1];     //actual measurement  [1,measurement component, step]
		MathVector[] xV  = new MathVector[NN+1];  //estmate for each filter [1, state, step, filter]
		MathVector[] ZZV = new MathVector[NN+1];  //Measurement deviation for each filter [1, meas, step, filter]
		MathMatrix[] PPV = new MathMatrix[NN+1];  //Estimate covariance for each filter   [state column, state row, step, filter]
		MathMatrix[] KKV = new MathMatrix[NN+1];  //Kalman gain for each filter           [meas column,  state row, step, filter]

		double[] q=new double[] {0,0,2.4064e-5,2.4064e-5,0}; //Actual process noise - uncorrelated, also used as scaling/mask for simulated process noise
		MathMatrix QQ=new MathMatrix(q);                     //Process noise as the filter sees it
		QQ.set(4,4,1e-6);                                    //A bit of noise added to the beta component, for the filter only, not the sim.
		double[] r=new double[] {0.1,1.7e-3};    //standard deviation of measurements
		int m=r.length;
		for(int i=0;i<r.length;i++) r[i]*=r[i];  //change to variance of measurements
		MathMatrix RR=new MathMatrix(r);
		SimModel reentry=new SimModel() {
			//model constants
			private double beta0=0.59783;  
			private double Gm0=398600;     //Gravitational constant of Earth km and s
			private double R0=6374;        //Surface radius, km
			private double H0=13.406;      //Atmosphere scale height, km

			@Override
			public MathVector fd(double t, MathVector x) {
				double RR=x.subVector(0,2).length();
				double VV=x.subVector(2,2).length();
				double beta=beta0*Math.exp(x.get(4));
				double GG=-Gm0/(RR*RR*RR);
				double DD=-beta*Math.exp((R0-RR)/H0)*VV;
				return new MathVector(new double[] {
						x.get(2),
						x.get(3),
						DD*x.get(2)+GG*x.get(0),
						DD*x.get(3)+GG*x.get(1),
						0});
			}

			@Override
			public MathVector g(double t, MathVector x) {
			  double xr=R0;
			  double yr=0;
			  double rho=Math.sqrt(Math.pow(x.get(0)-xr,2)+Math.pow(x.get(1)-yr,2));
			  double theta=Math.atan2((x.get(1)-yr),(x.get(0)-xr));
			  return new MathVector(new double[] {rho,theta});
			}

		};

		UnscentedKalman lukf=new UnscentedKalman(x,PP,reentry);

		//Store initial history
		tV[0]=0;
		sV[0]= new MathVector(s);       // true state
		zV[0]= reentry.g(tV[0],s);  // measurement
		xV[0]= new MathVector(x);      // estimate
		ZZV[0]=new MathVector(zV[0]);
		PPV[0]=new MathMatrix(PP);

		//Generate all the process noise and measurement noise up front
		MathVector[] vV=new MathVector[NN+1];
		MathVector[] wV=new MathVector[NN+1];
		for(int i=0;i<=NN;i++) {
			double[] thisV=new double[n];
			for(int j=0;j<n;j++) thisV[j]=rand.nextGaussian()*Math.sqrt(q[j]);
			vV[i]=new MathVector(thisV);
			double[] thisW=new double[m];
			for(int j=0;j<m;j++) thisW[j]=rand.nextGaussian()*Math.sqrt(r[j]);
			wV[i]=new MathVector(thisW);

		}
        PrintWriter ouf=new PrintWriter(new FileWriter("Data/Rocketometry/ukfTest.csv"));
        ouf.println("t,s_rx,s_ry,s_vx,s_vy,s_beta,x_rx,x_ry,x_vx,x_vy,x_beta,z_range,z_theta");
		for(int k=1;k<NN;k++) {
			double t1=((double)(k))  /((double)(fps));
			double t0=((double)(k-1))/((double)(fps));
			MathVector v=vV[k];
			s=reentry.eval_fd(t0,s,t1,nstep,v);

			MathVector w=wV[k];
			MathVector z = reentry.g(t1,s,w);

			tV[k]=t1;       // save time stamp
			sV[k]= new MathVector(s);   // save actual state
			zV[k]= new MathVector(z);   // save measurement

			lukf.update(t0,t1,nstep,z,QQ,RR);

			xV [k]=new MathVector(lukf.x);     // save estimate
			PPV[k]=new MathMatrix(lukf.PP);    // save covariance
			ZZV[k]=new MathVector(lukf.ZZ);    // save measurement residual
			KKV[k]=new MathMatrix(lukf.KK);    // save Kalman gain
			ouf.printf("%f,%s,%s,%s\n",t1,s.toString(),lukf.x.toString(),lukf.ZZ.toString());
		}
		ouf.close();
		toc();
	}
}
