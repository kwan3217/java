package org.spaceroots.rkcheck;

/**
 * This class represents a Runge-Kutta method.
 * <p>It is a simple placeholder for the various Butcher arrays, it
 * does not provide any active methods.</p>
 * @version $Id: RungeKuttaMethod.java,v 1.1 2004/05/23 13:32:01 luc Exp $
 * @author L. Maisonobe
 */
public class RungeKuttaMethod {

  /** Simple constructor.
   * <p>Build a non-initialized Runge-Kutta method</p>
   */
  public RungeKuttaMethod() {
    name              = null;
    timeSteps         = null;
    internalWeights   = null;
    estimationWeights = null;
    errorWeights      = null;
  }

  /** Get the name of the method.
   * @return method name
   */
  public String getName() {
    return name;
  }

  /** Set the name of the method.
   * @return method name
   */
  public void setName(String name) {
    this.name = name;
  }

  /** Get the time steps table.
   * @return time steps table
   */
  public QuadraticSurd[] getTimeSteps() {
    return timeSteps;
  }

  /** Set the time steps table.
   * @param timeSteps time steps table
   */
  public void setTimeSteps(QuadraticSurd[] timeSteps) {
    this.timeSteps = timeSteps;
  }

  /** Get the internal weights table.
   * @return internal weights table
   */
  public QuadraticSurd[][] getInternalWeights() {
    return internalWeights;
  }

  /** Set the internal weights table.
   * @param internalWeights internal weights table
   */
  public void setInternalWeights(QuadraticSurd[][] internalWeights) {
    this.internalWeights = internalWeights;
  }

  /** Get the estimation weights table.
   * @return estimation weights table
   */
  public QuadraticSurd[] getEstimationWeights() {
    return estimationWeights;
  }

  /** Set the estimation weights table.
   * @param estimationWeights estimation weights table
   */
  public void setEstimationWeights(QuadraticSurd[] estimationWeights) {
    this.estimationWeights = estimationWeights;
  }

  /** Get the error weights table.
   * @return error weights table
   */
  public QuadraticSurd[] getErrorWeights() {
    return errorWeights;
  }

  /** Set the error weights table.
   * @param errorWeights error weights table
   */
  public void setErrorWeights(QuadraticSurd[] errorWeights) {
    this.errorWeights = errorWeights;
  }

  /** Name. */
  private String name;

  /** Time steps. */
  private QuadraticSurd[] timeSteps;

  /** Internal weights. */
  private QuadraticSurd[][] internalWeights;

  /** Estimation weights. */
  private QuadraticSurd[] estimationWeights;

  /** Error weights. */
  private QuadraticSurd[] errorWeights;

}
