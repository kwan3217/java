package org.kwansystems.tools.rotation;

import static java.lang.Math.*;

import org.kwansystems.tools.vector.*;

/**
 * Quaternion -- Four-dimensional complex number. This quaternion class is immutable -
 * You can't change its value once it's created. In this sense it acts like the String
 * class.
 * <p>
 * i^2=j^2=k^2=ijk=-1
 * <p>
 * Also, this class is the common language of the Rotator superclass. All Rotators must
 * be able to create a quaternion version of themselves, and be constructed from a quaternion.
 * All code to do so should be in the other classes.
 */
public class Quaternion extends Rotator implements Lerpable {
  /**
   * Complex component of quaternion in i direction
   */
  private final double x;
  /**
   * Complex component of quaternion in j direction
   */
  private final double y;
  /**
   * Complex component of quaternion in k direction
   */
  private final double z;
  /**
   * Real component of quaternion
   */
  private final double w; 
  /**
   * Quaternion constant i
   */
  public static Quaternion I=new Quaternion(1,0,0,0);
  /**
   * Quaternion constant j
   */
  public static Quaternion J=new Quaternion(0,1,0,0);
  /**
   * Quaternion constant k
   */
  public static Quaternion K=new Quaternion(0,0,1,0);
  /**
   * Quaternion constant 1
   */
  public static Quaternion U=new Quaternion(0,0,0,1);
  /**
   * Constructs a new identity quaternion
   */
  public Quaternion() {
    x=0;y=0;z=0;w=1;
  }
  /**
   * Constructs a new quaternion from the three components of 
   * the vector part and the scalar part
   * @param Lx Component in the i direction
   * @param Ly Component in the j direction
   * @param Lz Component in the k direction
   * @param Lw Scalar component
   */
  public Quaternion(double Lx, double Ly, double Lz, double Lw) {
    x=Lx;y=Ly;z=Lz;w=Lw;
  }
  /**
   * Constructs a new quaternion from the vector part and the scalar part
   * @param LV Vector part
   * @param Lw Scalar part
   */
  public Quaternion(MathVector LV, double Lw) {
    if(LV.dimension()!=3) throw new IllegalArgumentException("Vector part must be 3D");
    x=LV.X();y=LV.Y();z=LV.Z();w=Lw;
  }
  /**
   * Constructs a new quaternion from a vector. If the vector is 3D, treat it as a 
   * quaternion with a zero scalar. If its a 4D vector, treat it as a full 
   * quaternion with last component as the scalar
   * @param LV Vector to turn into a quaternion
   */
  public Quaternion(MathVector LV) {
    if(LV.dimension()!=4 && LV.dimension()!=3) throw new IllegalArgumentException("Vector must be 3D or 4D");
    x=LV.get(0);y=LV.get(1);z=LV.get(2);
    if(LV.dimension()==4) {
      w=LV.get(3);
    } else {
      w=0;
    }
  }
  /**
   * Constructs a new quaternion which is a copy of another quaternion
   * @param LQ Quaternion to copy
   */
  public Quaternion(Quaternion LQ) {
    x=LQ.x;y=LQ.y;z=LQ.z;w=LQ.w;
  }
  /**
   * Constructs a new quaternion from an axis and angle
   * @param R Any kind of Rotator
   */
  public Quaternion(Rotator R) {
    this(R.toQuaternion());
  }
  
  /**
   * Gets the component in the i direction
   */
  public double X() {
    return x;
  }
  /**
   * Gets the component in the j direction
   */
  public double Y() {
    return y;
  }
  /**
   * Gets the component in the k direction
   */
  public double Z() {
    return z;
  }
  /**
   * Gets the scalar component
   */
  public double W() {
    return w;
  }
  /**
   * Gets the vector component
   */
  public MathVector V() {
    return new MathVector(x,y,z);
  }
  /**
   * Gets a representation of the quaternion as a vector [x,y,z,w]
   */
  public MathVector toVector() {
    return new MathVector(new double[] {x,y,z,w});
  }
  
  /**
   * Adds two quaternions
   */
  public static Quaternion add(Quaternion p, Quaternion q) {
    return new Quaternion(MathVector.add(p.V(),q.V()),p.W()+q.W());
  }
  public Quaternion add(Quaternion q) {
    return add(this,q);
  }

  /**
   * Subtracts two quaternions
   */
  public static Quaternion sub(Quaternion p, Quaternion q) {
    return new Quaternion(MathVector.sub(p.V(),q.V()),p.W()-q.W());
  }
  public Quaternion sub(Quaternion q) {
    return Quaternion.sub(this,q);
  }
  
  /**
   * Multiplies this quaternion by a scalar
   */
  public static Quaternion mul(Quaternion Q, double s) {
    return new Quaternion(Q.V().mul(s),Q.W()*s);
  }
  public Quaternion mul(double s) {
    return Quaternion.mul(this,s);
  }
  
  /**
   * multiplies two quaternions p*q
   */
  public static Quaternion mul(Quaternion p, Quaternion q) {
    double a=p.w,b=p.x,c=p.y,d=p.z;
    double e=q.w,f=q.x,g=q.y,h=q.z;
    double x=b*e+a*f-d*g+c*h;
    double y=c*e+d*f+a*g-b*h;
    double z=d*e-c*f+b*g+a*h;
    double w=a*e-b*f-c*g-d*h;
    return new Quaternion(new MathVector(x,y,z),w);
  }
  public Quaternion mul(Quaternion q) {
    return mul(this,q);
  }
  public Quaternion rmul(Quaternion p) {
    return mul(p,this);
  }
  
  /**
   * Multiplies a quaternion and vector p*v
   */
  public static Quaternion mul(Quaternion p, MathVector v) {
    Quaternion q=new Quaternion(v);
    return mul(p,q);
  }
  public Quaternion mul(MathVector v) {
    return mul(this,v);
  }
  /**
   * Multiplies a vector and quaternion v*q
   */
  public static Quaternion mul(MathVector v, Quaternion q) {
    Quaternion p=new Quaternion(v);
    return mul(p,q);
  }
  public Quaternion rmul(MathVector v) {
    return mul(v,this);
  }

  /**
   * Makes a string representation of this quaternion in the form
   * x*i+y*j+z*k+w
   */
  public String toString() {
    StringBuffer result=new StringBuffer();
    if(x!=0) {
      result.append(Double.toString(x));
      result.append("i");
    }
    if(y!=0) {
      if(y>0 && result.length()>0) result.append('+');
      result.append(Double.toString(y));
      result.append("j");
    }
    if(z!=0) {
      if(z>0 && result.length()>0) result.append('+');
      result.append(Double.toString(z));
      result.append("k");
    }
    if(w!=0) {
      if(w>0 && result.length()>0) result.append('+');
      result.append(Double.toString(w));
    }
    if(result.length()==0)result.append("0.0");
//    result.append("\n"+this.toAnA());
//    result.append("\n"+new EulerAngle(this));
    return result.toString();
  }
  /**
   * Gets the conjugate of a quaternion.
   */
  public static Quaternion conj(Quaternion Q) {
    return new Quaternion(-Q.x,-Q.y,-Q.z, Q.w);
  }
  public Quaternion conj() {
    return conj(this);
  }
  /**
   * Gets the length of this quaternion.
   */
  public double length() {
    return sqrt(x*x+y*y+z*z+w*w);
  }
  /**
   * Calculates the normalized quaternion in the same direction as this one
   */
  public static Quaternion norm(Quaternion Q) {
    return Q.mul(1/Q.length());
  }
  public Quaternion norm() {
    return norm(this);
  }
  public static Quaternion inv(Quaternion q) {
    return q.conj().mul(1.0/pow(q.length(),2));
  }
  public Quaternion inv() {
    return inv(this);
  }
  /**
   * Uses this quaternion to rotate a vector. Quaternion must be unit length
   * (see {@link #norm(Quaternion)}). This transform is equivalent to a right-handed
   * frame transform using the equivalent AxisAngle representation, and is also
   * considered from2to where that matters. Convention is v_to=q'*v_from*q
   */
  public MathVector transform(MathVector in) {
    Quaternion Q1=this.conj().mul(in);
    Quaternion Q2=Q1.mul(this);
    return Q2.V();
  }
  /**
   * Uses this quaternion to rotate a vector. Quaternion must be unit length
   * (see {@link #norm(Quaternion)}). This transform is equivalent to a left-handed
   * frame transform using the equivalent AxisAngle representation, and is also
   * considered to2from where that matters.
   */
  @Override
  public MathVector invTransform(MathVector in) {
    Quaternion Q1=this.mul(in);
    Quaternion Q2=Q1.mul(this.conj());
    return Q2.V();
  }
  public static void main(String[] args) {
    System.out.println("Should be -1 i^2="+I.mul(I));
    System.out.println("Should be -1 j^2="+J.mul(J));
    System.out.println("Should be -1 k^2="+K.mul(K));
    System.out.println("Should be  k ij="+I.mul(J));
    System.out.println("Should be  i jk="+J.mul(K));
    System.out.println("Should be  j ki="+K.mul(I));
    System.out.println("Should be -j ik="+I.mul(K));
    System.out.println("Should be -k ji="+J.mul(I));
    System.out.println("Should be -i kj="+K.mul(J));
    System.out.println("Should be -1 ijk="+I.mul(J).mul(K));
    MathMatrix M=MathMatrix.Rot3d(30);
    Quaternion Q=new Quaternion(new AxisAngle(new MathVector(0,0,1),toRadians(30)));
    MathVector V=new MathVector(3,4,5);
    System.out.println(M);
    System.out.println(new MathMatrix(Q));
    System.out.println(Q);
    System.out.println(V);
    Quaternion Q2=Q.conj().mul(V);
    System.out.println(Q2);
    Quaternion Q3=Q2.mul(Q);
    System.out.println(Q3);
    System.out.println(Q.transform(V));
    System.out.println(M.transform(V));
  }
  public static double dot(Quaternion A, Quaternion B) {
    return A.w*B.w+A.x*B.x+A.y*B.y+A.z*B.z;
  }
  public double dot(Quaternion B) {
    return dot(this,B);
  }
  public Lerpable Lerp(Lerpable B, double t) {
    Quaternion q0=norm();
    Quaternion q1=((Quaternion)B).norm();
    double c_theta0=q0.dot(q1); //dot product as if quaternions were 4D vectors.
                                //This is not necessarily related to the rotation between
                                //the two quaternions.
    if (c_theta0 < 0) {         //Make sure we're going the short way around
      q1=q1.mul(-1.0);            //Rotationally, q==-q
      c_theta0=q0.dot(q1);
    }
    double theta0=acos(c_theta0);
    double theta=theta0*t;
    Quaternion q2=q1.sub(q0.mul(c_theta0)).norm();
    return add(q0.mul(cos(theta)),q2.mul(sin(theta)));
  }
  
  public static Quaternion Point(MathVector p_b, MathVector p_r) {
	  MathVector a=MathVector.cross(p_b, p_r);
	  if(a.length()!=0) a.normalEq();
	  double cphi=MathVector.dot(p_r,p_b);
	  double phi=acos(cphi);
	  Quaternion p=new Quaternion(a.mul(sin(phi/2)),cos(phi/2));
	  return p;
  }
  
  public static Quaternion Toward(MathVector p_b, MathVector t_b, MathVector t_rt) {
	  MathVector y_s=MathVector.cross(p_b,t_b);
	  y_s.normalEq();
	  MathVector x_s=MathVector.cross(y_s,p_b);
	  double x=MathVector.dot(t_rt,x_s);
	  double y=MathVector.dot(t_rt,y_s);
	  double theta=Math.atan2(y, x);
	  return new Quaternion(p_b.mul(sin(theta/2)),cos(theta/2));
  }
  
  public static Quaternion PointToward(MathVector p_b, MathVector p_r, MathVector t_b, MathVector t_r) {
	  Quaternion p=Point(p_b,p_r);
	  MathVector t_rt=p.invTransform(t_r);
	  Quaternion t=Toward(p_b,t_b,t_rt);
	  return Quaternion.mul(p, t);
  }
  
  @Override
  public Quaternion toQuaternion() {
    return this;
  }
  
  /**
   * Combine this Rotator with another leading Rotator into a new Rotator. Notation follows
   * that of matrices (where the first rotation is on the far right)
   * @param R Rotator to do before this one
   * @return A Rotator which is exactly equivalent to performing R's rotation, then performing this rotation.   
   * This one is 
   */
  @Override
  public Rotator combine(Rotator R) {
    return mul(R.toQuaternion(),this);
  }
}
