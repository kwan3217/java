package org.kwansystems.kalman;

import org.kwansystems.tools.vector.MathVector;

public abstract class SimModel {
  public abstract MathVector fd(double t, MathVector x);
  public abstract MathVector g(double t, MathVector x);
  public MathVector fd(double t, MathVector x, MathVector v) {
	return fd(t,x).add(v);
  }
  public MathVector g(double t, MathVector x, MathVector w) {
	return g(t,x).add(w);
  }
	/**Process propagator for a continuous process from a given time to another time. This one is a simple
  N-step Euler integrator.
One function name argument to describe the process (fd abstract method in this Java implementation)
fd -   Physics function dx/dt=fdot(x,t). Figures the derivative of the state 
        given the current state and time
Two arguments describing the current state
t0 - A priori time stamp (presumed completely accurate)
xkm - A priori State vector, valid at t0
Two arguments describing the propagation and observation
t1 - A posteriori time stamp
nstep - Number of integration steps to take between t0 and t1
One argument describing the process noise (optional, not available in this Java implementation)
v - process noise vector, the thing usually considered "unknown" by the filter
   If not passed, zero vector the size of the state is used
Return arguments
xk - A posteriori state vector, valid at t1. IFF t1 eq t0, then xkm will be returned
    unmodified, even by process noise.
*/
public MathVector eval_fd(double t0, MathVector xkm, double t1, int nstep, MathVector v) {
	MathVector xk=new MathVector(xkm);
	double deltat=t1-t0;
	if(deltat==0) return xk;
	for(int i=0;i<nstep;i++) {	
		double t=t0+(deltat*i)/nstep;
		xk.addEq(fd(t,xk,v).mul(deltat/nstep));
	}
	return xk;
}
public MathVector eval_fd(double t0, MathVector xkm, double t1, int nstep) {
	return eval_fd(t0,xkm,t1,nstep,new MathVector(xkm.dimension()));
}
}
