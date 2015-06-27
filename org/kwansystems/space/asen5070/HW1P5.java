package org.kwansystems.space.asen5070;

import org.kwansystems.tools.rotation.MathMatrix;
import org.kwansystems.tools.vector.*;

//Program to estimate initial state in uniform gravity field given initial guess Xstar, station coords 
//Xs and Ys, and range measurements Y=[rho] at time t=[t]

//Step 1: calculate ranges Ystar based on state guess Xstar (function Ystar)
//Step 2: calculate O-C J (function J)
//Step 3: calculate dJ/dX matrix (function dJdX)
//Step 4: Iterate. Xn+1=Xstar-(dJ/dX^-1)(J). Xstar=Xn+1 (function main)
//Step 5: repeat steps 1-4 until ||J|| is less than error limit (function main)

public class HW1P5 {
  public final static double Xs=1;
  public final static double Ys=1;
  
  //Calculate partial derivative dRho/dX[i] where i selects the variable by which to differentiate
  //and individual components of the state vector are passed.
  //0=X,1=Y,2=Xdot,3=Ydot,4=g
  //Formulas from Mathematica processing
  public static double Partial(double X, double Y, double Xdot, double Ydot, double g, double t, int i) {
    switch(i) {
      case 0:
        return (-1 + X + t*Xdot)/
	Math.sqrt(Math.pow(-1 + X + t*Xdot,2) + Math.pow(-1 - (g*Math.pow(t,2))/2. + Y + t*Ydot,2));
      case 1:
        return (-1 - (g*Math.pow(t,2))/2. + Y + t*Ydot)/
	Math.sqrt(Math.pow(-1 + X + t*Xdot,2) + Math.pow(-1 - (g*Math.pow(t,2))/2. + Y + t*Ydot,2));
      case 2:
        return (t*(-1 + X + t*Xdot))/
	Math.sqrt(Math.pow(-1 + X + t*Xdot,2) + Math.pow(-1 - (g*Math.pow(t,2))/2. + Y + t*Ydot,2));
      case 3:
        return (t*(-1 - (g*Math.pow(t,2))/2. + Y + t*Ydot))/
	Math.sqrt(Math.pow(-1 + X + t*Xdot,2) + Math.pow(-1 - (g*Math.pow(t,2))/2. + Y + t*Ydot,2));
      case 4:
        return (Math.pow(t,2)*(g*Math.pow(t,2) - 2*(-1 + Y + t*Ydot)))/
	(4.*Math.sqrt(Math.pow(-1 + X + t*Xdot,2) + Math.pow(-1 - (g*Math.pow(t,2))/2. + Y + t*Ydot,2)));
    }
    throw new IllegalArgumentException("Bad value of which ("+i+")");
  }
  
  //Calculate partial derivative dRho/dX[i] from a state vector X0
  public static double Partial(MathMatrix X0,double t,int i) {
    return Partial(X0.get(0,0),X0.get(1,0),X0.get(2,0),X0.get(3,0),X0.get(4,0),t,i);
  }
  
  //Calculate range from station given individual state vector components and time t
  public static double Rho(double X, double Y, double Xdot, double Ydot, double g, double t) {
    return Math.sqrt(Math.pow(X+Xdot*t-Xs,2)+Math.pow(Y+Ydot*t-g*t*t/2.0-Ys,2));
  }
  
  //Calculate range from station given initial state vector X0 and time t
  public static double Rho(MathMatrix X0,double t) {
    return Rho(X0.get(0,0),X0.get(1,0),X0.get(2,0),X0.get(3,0),X0.get(4,0),t);
  }
  
  //Fill a matrix with partial derivatives. 
  // [dRho1/dX,dRho1/dY,dRho1/dXdot,dRho1/dYdot,dRho1/dg]
  // [dRho2/dX,dRho2/dY,dRho2/dXdot,dRho2/dYdot,dRho2/dg]
  //-[dRho3/dX,dRho3/dY,dRho3/dXdot,dRho3/dYdot,dRho3/dg]
  // [dRho4/dX,dRho4/dY,dRho4/dXdot,dRho4/dYdot,dRho4/dg]
  // [dRho5/dX,dRho5/dY,dRho5/dXdot,dRho5/dYdot,dRho5/dg]
  //where Rhoi is the range at time ti
  public static MathMatrix dJdX(MathMatrix X0, MathMatrix t) {
    MathMatrix result=new MathMatrix(new double[5][5]);
    for(int row=0;row<5;row++) {
      for(int col=0;col<5;col++) {
        result.set(row,col,-Partial(X0,t.get(row,0),col));
      }
    }
    return result;
  }
  //Fill a vector with ranges at time vector t
  public static MathMatrix Ystar(MathMatrix Xstar,MathMatrix t) {
    MathMatrix result=new MathMatrix(new double[5][1]);
    for(int row=0;row<5;row++) {
      result.set(row,0,Rho(Xstar,t.get(row,0)));
    }
    return result;
  }
  
  //Fill a vector with range residuals O-C at time vector t
  public static MathMatrix J(MathMatrix Xstar,MathMatrix Y,MathMatrix t) {
    return MathMatrix.sub(Y,Ystar(Xstar,t));
  }
  
  //Main program
  public static void main(String args[]) {
    //Time at which measurements are taken
    MathMatrix t=new MathMatrix(new double[][] {{0},
                                                {1},
						{2},
						{3},
						{4}});
    //Measurement values						
    MathMatrix Y=new MathMatrix(new double[][] {{7.0},
                                                {8.00390597},
						{8.94427191},
						{9.801147892},
						{10.630145813}});
    //Initial state estimate						
    MathMatrix Xstar=new MathMatrix(new double[][] {{1.5},
                                                    {10.0},
						    {2.2},
						    {0.5},
						    {0.3}});
    //Next state estimate						    
    MathMatrix Xnp1;
    boolean done;
    int i=0;
    do {
      //Steps 1-4 are done by this line and subfunctions
      //Xnp1=Xstar-(dJdX^-1)(J)
      Xnp1=MathMatrix.sub(Xstar,MathMatrix.mul(MathMatrix.inverse(dJdX(Xstar,t)),J(Xstar,Y,t)));
      
      //Print out results of this iteration
      i++;
      System.out.println("Iteration "+i);
      System.out.println("Initial state estimate X*");
      System.out.println(Xnp1);
      System.out.println("range residual J");
      System.out.println(J(Xnp1,Y,t));

      //step 5: check convergence by ||J||, which should be 0
      done=(J(Xnp1,Y,t).Norm()<1e-9);
      Xstar=Xnp1;
    } while (!done);
  }
}
