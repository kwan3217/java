package org.kwansystems.tools.vector;

import org.kwansystems.tools.rotation.*;

/**
 * Object describing 6DOF state (Position and its time derivative, and orientation 
 * and its time derivative)
 */
public class SixDOFState extends MathState {
  private static final long serialVersionUID=5044465772891482941L;
  /**
   * Quaternion describing rotation from body frame to inertial frame
   */
  public Quaternion E() {
    return new Quaternion(subVector(6, 4));
  }
  /**
   * Angular velocity in body axes, rad/s
   */
  public MathVector w() {
    return subVector(10,3);
  }
  /**
   * Creates a new SixDOFState object from a MathVector. This is designed to be used
   * on a MathVector carried by an {@link org.kwansystems.tools.integrator.Integrator}.
   * @param X Input MathVector. First 13 elements are used, as follows:<br>
   * [R.X,R.Y,R.Z,
   *  V.X,V.Y,V.Z,
   *  E.X,E.Y,E.Z,E.W,
   *  w.X,w.Y,w.Z]
   */
  public SixDOFState(MathVector X) {
    super(X);
  }
  /**
   * Creates a new SixDOFState object from individual state component vectors.
   * @param LR Position vector, in inertial frame, m
   * @param LV Velocity vector, in inertial frame, m/s
   * @param LE Quaternion describing rotation from body frame to inertial frame
   * @param Lw Angular velocity in body axes, rad/s
   */
  public SixDOFState(MathVector LR, MathVector LV, Quaternion LE, MathVector Lw) {
    super(new MathVector[] {LR,LV,LE.toVector(),Lw});
  }
  public SixDOFState(MathVector LR, MathVector LV) {
    super(new MathVector[] {LR,LV,new Quaternion(Quaternion.U).toVector(),new MathVector()});
  }
  /**
   * Creates a new SixDOFState object from individual state component vectors.
   * @param LRV 3DOF state vector, in inertial frame, m and s
   * @param LE Quaternion describing rotation from body frame to inertial frame
   * @param Lw Angular velocity in body axes, rad/s
   */
  public SixDOFState(MathState LRV, Quaternion LE, MathVector Lw) {
    super(new MathVector[] {LRV,LE.toVector(),Lw});
  }
  /**
   * Creates a new SixDOFState object from a ThreeDOF object. E and w are zero.
   * @param LRV 3DOF state vector, in inertial frame, m and s
   */
  public SixDOFState(MathState LRV) {
    super(new MathVector[] {LRV,new MathVector(4),new MathVector(3)});
  }
  public SixDOFState() {
	this(new MathVector(), new MathVector());
  }
/**
   * Converts state into a string for debugging and other purposes.
   * @return A string representation of this state
   */
  public String toString() {
    return R().toString()+"\n"+V().toString()+"\n"+E().toString()+"\n"+w().toString();
  }
}
