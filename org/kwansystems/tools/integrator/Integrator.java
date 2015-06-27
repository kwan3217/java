package org.kwansystems.tools.integrator;

import org.kwansystems.tools.vector.*;

/**
 * Numerical integrator of a first-order vector ordinary differential equation.
 */
public abstract class Integrator implements Cloneable {
  /**
   * current independent variable value
   */
  protected double T;
  /**
   * current state vector value
   */
  protected MathVector X;
  /**
   * Current integrator step size
   */
  private double dT;
  /**
   * {@link DerivativeSet} describing the differential equations to be solved
   */
  DerivativeSet D;
  /**
   * Derivative of state at most recent time step. Any extension of Integrator 
   * must set LastDxDt to some DxDt during its step
   */
  public MathVector LastDxDt;
  /**
   * Number of steps taken since last adjustment of step size or independent variable
   */
  private int NumSteps;
  /**
   * Value of independent variable at last time it was set
   */
  private double LastTSet; 
  /**
   * Constraint applied to integrator state exactly once each step at earliest 
   * opportunity
   */
  public Constraint con;
  /**
   * Create a new Integrator
   * @param StartT Initial value of independent variable
   * @param LX Initial conditions
   * @param LdT Initial step size
   * @param LD DerivativeSet describing differential equations
   */
  public Integrator(double StartT, MathVector LX, double LdT, DerivativeSet LD) {
    setT(StartT);
    LastTSet=StartT;
    NumSteps=0;
    X=LX;
    dT=LdT;
    D=LD;
    //Often the DerivativeSet itself is its own constraint. Try to install
    //it as a constraint. If it doesn't work, no harm done.
    try{
      con=(Constraint)D;      
    } catch (ClassCastException E) {};
  }
  /**
   * Descendant classes will override this method with the exact mechanism for taking
   * one step forward. 
   * @param LdT Step size
   */
  public abstract void stepGuts(double LdT);
  /**
   * Take one step forward
   */
  public void step() {
    stepGuts(dT);
    if(con!=null) X=con.Constrain(getT(), X);
    NumSteps++;
    setT(LastTSet+dT*NumSteps);
  }
  /**
   * Sets value of independent variable. Use with caution, any discrepancy 
   * between old and new independent variable will not be integrated.
   * @param LT New value of independent variable
   */
  public void setT(double LT) {
    T=LT;
    LastTSet=T;
    NumSteps=0;
  }
  /**
   * Gets the current value of the independent variable
   * @return Current value of independent variable
   */
  public double getT() {
    return T;
  }
  /**
   * Get current step size
   * @return Current step size
   */
  public double getDT() {
    return dT;
  }
  /**
   * Set step size
   * @param LdT New step size
   */
  public void setDT(double LdT) {
    dT=LdT;
    LastTSet=getT();
    NumSteps=0;
  }
  /**
   * Get state vector
   * @return Cloned copy of state vector
   */
  public MathVector getX() {
    return (MathVector)X.clone();
  }
}
