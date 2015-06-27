package org.kwansystems.tools.rotation;

import org.kwansystems.tools.vector.*;

/**
 * Abstract class which rotates from one coordinate frame to another with the same scale and origin.
 * {@link Quaternion} is the common language of the Rotator superclass. All Rotators must
 * be able to create a quaternion version of themselves, and be constructed from an arbitrary
 * Rotator, usually by converting it to Quaternion first.
 * <p>
 * After going through all the rigamarole of which is the direct transformation and which
 * is the inverse, I finally stumbled upon the correct answer: Let the context decide. Therefore
 * this class and its subclasses provide only a "forward" transform from a <i>from</i> frame to a
 * <i>to</i> frame, and an "inverse" transform which goes from the same <i>to</i> frame back to the
 * <i>from</i> frame. It is up to the calling functions to determine what is the <i>from</i> frame
 * and what is the <i>to</i> frame. 
 */
public abstract class Rotator {
  /**
   * Uses this rotator to forward rotate a vector. Subclasses may override this if there is a
   * more direct way to do the transformation 
   */
  public MathVector transform(MathVector in) {
    return toQuaternion().transform(in);
  }
  /**
   * Uses this rotator to inverse rotate a vector. Subclasses may override this if there is a
   * more direct way to do the transformation 
   */
  public MathVector invTransform(MathVector in) {
    return toQuaternion().invTransform(in);
  }
  /** Return the quaternion form of this Rotator. Quaternion is the common language all
   * Rotators must be able to change to and from.
   * @return A quaternion equivalent to this Rotator.
   */
  public abstract Quaternion toQuaternion();
  /**
   * Uses this rotator to forward rotate the two components of a state.
   */
  public MathState transform(MathState in) {
    return new MathState(transform(in.R()),transform(in.V()));
  }
  /**
   * Uses this rotator to inverse rotate the two components of a state.
   */
  public MathState invTransform(MathState in) {
    return new MathState(invTransform(in.R()),invTransform(in.V()));
  }
  /** Combine this Rotator with another leading Rotator into a new Rotator. Notation follows
   * that of matrices (where the first rotation is on the far right)
   * @param R Rotator to do before this one
   * @return A Rotator which is exactly equivalent to performing R's rotation, then performing this rotation.
   */
  public Rotator combine(Rotator R) {
    return toQuaternion().combine(R);
  }
  public double angle() {
    AxisAngle A=new AxisAngle(toQuaternion());
    return A.Angle;
  }
  /** Find a rotator which goes from this rotator to a target. In other words, find the rotator
   * C that makes the following equation true, assuming this rotator is called S
   * <p>
   * <tt>C.combine(S)=T</tt>
   * 
   *  @param T Target rotator
   * @return Rotator C above.
   */
  public Rotator course(Rotator T) {
    //Target quaternion is start quaternion rotated further by quaternion course
    //(qc)(qs)         =(qt)           Quaternion concatenation of start, then course, rotation
    //(qc)(qs)(qs^-1)  =(qt)(qs^-1)    Postmultiply both sides by the inverse of the start quaternion
    //(qc)(1)          =(qt)(qs^-1)    Definition of quaternion inverse
    //(qc)             =(qt)(qs^-1)    Drop the identity quaternion
    //(qc)             =(qt)(qs*)      For unit quaternions, quaternion inverse equals quaternion conjugate
    return T.toQuaternion().mul(this.toQuaternion().conj());
  }
  public abstract Rotator inv();
}
