package org.kwansystems.tools.integrator;

import org.kwansystems.tools.vector.MathVector;

/**Embedded Runge-Kutta integrator. By default it uses the Cash-Karp tableau, 
 * making it 4th order integrator with a 5th-order error estimator and step size
 * control. Further details are in NRC p715-718 and Vallado p502.
 * <p>
 *The program effectively calculates a higher-order Runge-Kutta, with a set of
 *coefficients such that the same slopes can be used to calculate a lower-order
 *Runge-Kutta. The difference between the two is an error estimate used to
 *control step size. This is implemented by using a tableau with two b rows,
 *one for the lower-order and one for the higher. The difference between the results
 *using each of the two rows is the error estimate.
 *  <p>
 *Vallado's version seems to take the conservative approach and use the 4th order
 *result, while NRC uses the 5th order result. Vallado has a list of coefficients
 *specifically for the delta while NRC uses two lists of
 *coefficients, one for the 4th and one for the 5th, and an algorithm for figuring
 *the delta after both results have been calculated
 *  <p>
 *The idea here is to enter the largest LdT reasonable. Use the LdT needed by your
 *table of results. The routine chops up each user LdT into 4096 nano-steps. It then
 *takes as many of these nano-steps at once as a micro-step. If the error estimate 
 *indicates that the step size is too big or too small, the number of nano-steps
 *in a micro-step is automatically halved or doubled.  This part follows Vallado
 *rather than NRC.
 */
public class EmbeddedRungeKutta extends RungeKutta {
  private int useRow;
  private MathVector Delta0;
  private int[] careVector;
  private int microStepSize;
  private int numNanoSteps;
  private int nSteps,nGoodSteps,nBadSteps;
  private MathVector Delta1;
  private double relativeStepSize;
  protected double[] e;
  /**
   * Construct a new EmbeddedRungeKutta, specifying everything.
   * @param StartT Starting independent variable value
   * @param LX Initial Conditions
   * @param LdT Step Size
   * @param LD Differential Equation to solve
   * @param B Butcher Tableau specifying which Runge-Kutta method is used
   * @param LuseRow Which <i>b</i> row to use to calculate the final slope
   * @param LcareVector Which elements of the state vector are important for determining step size
   * @param LDelta0 Maximum allowed error. The maximum allowed error in X[LcareVector[i]] is LDelta0[i], and 
   * therefore LDelta0 should be the same length as LcareVector.
   */
  public EmbeddedRungeKutta(double StartT, MathVector LX, double LdT, DerivativeSet LD, ButcherTableau B, int LuseRow, int[] LcareVector, MathVector LDelta0) {
    super(StartT, LX, LdT, LD, B);
    e=B.getE();
    careVector=LcareVector;
    useRow=LuseRow;
    Delta0=LDelta0;
    Delta1=new MathVector(LX.dimension());
    numNanoSteps=4096;
    relativeStepSize=0;
    microStepSize=numNanoSteps;
  }
  /**
   * Construct a new EmbeddedRungeKutta, using some defaults. In this case, using the Cash-Karp tableau as the default tableau.
   * @param StartT Starting independent variable value
   * @param LX Initial Conditions
   * @param LdT Step Size
   * @param LD Differential Equation to solve
   * @param LuseRow Which <i>b</i> row to use to calculate the final slope
   * @param LcareVector Which elements of the state vector are important for determining step size
   * @param LDelta0 Maximum allowed error. The maximum allowed error in X[LcareVector[i]] is LDelta0[i], and 
   * therefore LDelta0 should be the same length as LcareVector.
   */
  public EmbeddedRungeKutta(double StartT, MathVector LX, double LdT, DerivativeSet LD,int LuseRow, int[] LcareVector, MathVector LDelta0) {
    this(StartT, LX, LdT, LD, RationalButcherTableau.CashKarpTableau,LuseRow,LcareVector, LDelta0);
  }
  /**
   * Construct a new EmbeddedRungeKutta, using some defaults. In this case, using the Cash-Karp tableau as the default tableau, and the
   * 5th order final step weighting row of the tableau.
   * @param StartT Starting independent variable value
   * @param LX Initial Conditions
   * @param LdT Step Size
   * @param LD Differential Equation to solve
   * @param LcareVector Which elements of the state vector are important for determining step size
   * @param LDelta0 Maximum allowed error. The maximum allowed error in X[LcareVector[i]] is LDelta0[i], and 
   * therefore LDelta0 should be the same length as LcareVector.
   */
  public EmbeddedRungeKutta(double StartT, MathVector LX, double LdT, DerivativeSet LD, int[] LcareVector,MathVector LDelta0) {
    this(StartT, LX, LdT, LD, 1,LcareVector,LDelta0);
  }
  public static int[] IdentityCareVector(MathVector LDelta0) {
    int[] result=new int[LDelta0.dimension()];
    for(int i=0;i<result.length;i++) result[i]=i;
    return result;
  }
  /**
   * Construct a new EmbeddedRungeKutta, using some defaults. In this case, using the Cash-Karp tableau as the default tableau, and the
   * 5th order final step weighting row of the tableau, and the care vector says we care about all the elements.
   * @param StartT Starting independent variable value
   * @param LX Initial Conditions
   * @param LdT Step Size
   * @param LD Differential Equation to solve
   * @param LDelta0 Maximum allowed error. The maximum allowed error in X[i] is LDelta0[i], and 
   * therefore LDelta0 should be the same length as LX.
   */
  public EmbeddedRungeKutta(double StartT, MathVector LX, double LdT, DerivativeSet LD, MathVector LDelta0) {
    this(StartT, LX, LdT, LD, 1,IdentityCareVector(LDelta0),LDelta0);
  }
  /** Determines if this error estimate is acceptable. 
   * @return Negative if micro step size should be shrunk, 0 if micro step size is ok, positive if micro step size should be grown.
   */
  public int errorOK() {
    MathVector DeltaCare1=Delta1.subVector(careVector);
    MathVector DeltaRatio=MathVector.div(Delta0,DeltaCare1);
    relativeStepSize=Math.pow(DeltaRatio.minAbs(),0.2);
    if (relativeStepSize<0.75) return -1;
    if (relativeStepSize>1.50) return 1;
    return 0; 
  }
  /** Take one micro-step. This step is composed of {@link EmbeddedRungeKutta.numNanoSteps} steps. After the step is taken, 
   * determine if the step size needs to be changed. If so, change the step size, and if it needs to be shrunk, take the step
   * back and tell the driver to try again with the smaller step. 
   * @param nanoStep Starting nano-step
   * @param h Size of complete macro-step.
   * @return True if the step was taken, false otherwise.
   */
  public boolean microStep(int nanoStep, double h) {
    int nanoStepsLeft=numNanoSteps-nanoStep;
    if(nanoStepsLeft<microStepSize) {
      microStepSize=numNanoSteps-nanoStep;
    }
    MathVector[] k=calcSlopes(h*(double)microStepSize/(double)numNanoSteps);
    MathVector K=combineSlopes(k,useRow);
    Delta1.set(combineSlopes(k,e));
    int ok=errorOK();
    if(ok<0 && microStepSize>1) {
      microStepSize/=2;
      return false; //Don't let the driver increment the micro steps taken, we'll do this step again from here
    }
    if(ok>0) microStepSize*=2; //it's ok to make the microStepSize bigger than nanoStepsLeft, next time around it will be shrunk back down.
    X.addScaleEq(K,h);
    return true;
  }
  /**
   * Take exactly one step forward. Do this by taking as many micro-steps as necessary. Watch the micro-stepper to see if it really took the step,
   * and track statistics on how many good, bad, and total steps were taken.
   * @param h Step size
   */
  public void stepGuts(double h) {
    int nanoStep=0;
    nSteps=0;
    nGoodSteps=0;
    nBadSteps=0;
    double T0=T; //We're going to be messing around with T, so save a copy of the old value
    while(nanoStep<numNanoSteps) {
      T=T0+h*(double)nanoStep/(double)numNanoSteps;
      if(microStep(nanoStep,h)) {
        nanoStep+=microStepSize;
        nGoodSteps++;
      } else {
        nBadSteps++;
      }
      nSteps++;
    }
    T=T0; //Restore the old value
  }
  //Publically accessable information about the last step taken
  public int getMicroStepSize() {
    return microStepSize;
  }
  public int getNSteps() {
    return nSteps;
  }
  public int getNGoodSteps() {
    return nGoodSteps;
  }
  public int getNBadSteps() {
    return nBadSteps;
  }
  public double getMinDelta() {
    return relativeStepSize;
  }
  public MathVector getErrorEstimate() {
    return new MathVector(Delta1);
  }

}
