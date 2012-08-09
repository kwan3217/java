//MathVector.java - implements arbirtary dimension vector and vector ops

package org.kwansystems.tools.vector;

import java.io.*;

import org.kwansystems.tools.Scalar;

import static java.lang.Math.*;

/**
 * Arbitrary-dimension vector. 
 * All mutability starts with 'set' or ends with 'Eq'. So, we have {@link org.kwansystems.tools.vector.MathVector#set(double[])}
 * which can set individual vector elements, and {@link org.kwansystems.tools.vector.MathVector#addEq(MathVector)} which adds
 * two vectors and saves the result back into the first vector. 
 */
public class MathVector implements Cloneable, Serializable, Lerpable {
  /**
   * You need to change this only if the serializable form of a vector changes
   * (which should never happen)
   */
  private static final long serialVersionUID=-4344466443705010622L;
  /**
   * Components of this vector
   */
  protected double[] elements;
  /**
   * Checks two vectors for equal dimensionality, and throws 
   * an exception if they are not.
   * @param A Vector to check
   * @param B Vector to check
   * @throws java.lang.IllegalArgumentException if A and B do not have the same dimension
   */
  public static void AssertEqualLength(MathVector A, MathVector B) throws IllegalArgumentException {
    if(A.dimension()!=B.dimension()) throw new IllegalArgumentException("Vector lengths do not match (A="+A.dimension()+", B="+B.dimension()+")");
  }
  /**
   * Creates new MathVector from a double array
   * @param E Elements of array
   */
  public MathVector(double[] E) {
    set(E);
  }
  /**
   * Creates a copy of an existing MathVector
   * @param E MathVector to copy
   */
  public MathVector(MathVector E) {
    elements=(double[])E.elements.clone();
  }
  /**
   * Creates a 2D MathVector with specifiend elements
   * @param X Component of Vector
   * @param Y Component of Vector
   */
  public MathVector(double X, double Y) {
    this(new double[]{X,Y});
  }
  /**
   * Creates a 3D MathVector with specifiend elements
   * @param X Component of Vector
   * @param Y Component of Vector
   * @param Z Component of Vector
   */
  public MathVector(double X, double Y, double Z) {
    this(new double[]{X,Y,Z});
  }
  /**
   * Creates a zero 3D MathVector
   */
  public MathVector() {
    this(0,0,0);
  }
  /**
   * Creates a zero MathVector of arbitrary dimension
   * @param N dimension of vector
   */
  public MathVector(int N) {
    this(new double[N]);
  }
  /**
   * Creates a MathVector full of ones of arbitrary dimension
   * @param N dimension of vector
   */
  public static MathVector I(int N) {
    double[] c=new double[N];
    for(int i=0;i<c.length;i++) c[i]=1;
    return new MathVector(c);
  }
  /**
   * Creates a new MathVector from an array of MathVectors. The new vector 
   * is the concatenation of all the given MathVectors in the appropriate order.
   * @param E Array of MathVectors to concatenate
   */
  public MathVector(MathVector[] E) {
    int totalDimension=0;
    for(int i=0;i<E.length;i++) totalDimension+=E[i].dimension();
    elements=new double[totalDimension];
    int pointer=0;
    for(int i=0;i<E.length;i++) {
      System.arraycopy(E[i].elements,0,elements,pointer,E[i].elements.length);
      pointer+=E[i].elements.length;
    }
  }
  /**
   * Creates a copy of this MathVector
   * @return A copy of this MathVector
   */
  public Object clone() {
    return new MathVector(elements);
  }
  /* Get/Set individual Elements */
  /**
   * Gets an element
   * @param N Element number to get
   * @return Nth element
   */
  public double get(int N) {
    return elements[N];
  }
  /**
   * Gets all element
   * @return A copy of the element array
   */
  public double[] get() {
    return (double[])elements.clone();
  }
  /**
   * Gets the X component of a vector
   * @return The X component of the vector
   */
  public double X() {
    return get(0);
  }
  public double Y() {
    return get(1);
  }
  public double Z() {
    return get(2);
  }
  public MathVector replace(int i, double S) {
	double[] newElements=elements.clone();
	newElements[i]=S;
	return new MathVector(newElements);
  }
  /**
   * Gets number of dimensions of this vector
   * @return Number of dimensions of this vector
   */
  public int dimension() {
    return elements.length;
  }
  public MathVector subVector(int[] index) {
    double[] newElements=new double[index.length];
    for(int i=0;i<index.length;i++) newElements[i]=elements[index[i]];
    return new MathVector(newElements);
  }
  public MathVector subVector(int start, int length) {
    double[] newElements=new double[length];
    System.arraycopy(elements,start,newElements,0,length);
    return new MathVector(newElements);
  }
  public MathVector subVector(int start) {
    return subVector(start,dimension()-start);
  }
  public MathVector replaceSubVector(int start, MathVector B) {
  	double[] newElements=elements.clone();
	  for(int i=0;i<B.length();i++) newElements[i+start]=B.get(i);
	  return new MathVector(newElements);
  }
  /* Vector Products */
  public static double dot(MathVector A, MathVector B) {
    AssertEqualLength(A,B);
    int i;
    double acc=0;
    for(i=0;i<A.elements.length;i++) acc+=A.elements[i]*B.elements[i];
    return acc;
  }
  public static MathVector cross(MathVector A, MathVector B) {
    MathVector result=new MathVector(A);
    result.crossEq(B);
    return result;
  }
  /* Derived operations */
  public static double vangle(MathVector A,MathVector B) throws IllegalArgumentException {
    double D=dot(A,B);
    double C=D/(A.length()*B.length());
    return Math.acos(C);
  }
  /**
   * Calculates component of this vector in the direction of another vector
   * @param W Reference vector
   * @return component of this vector in direction of W
   */
  public double Comp(MathVector W) {
    return dot(this,W)/W.length();
  }
  /**
   * Calculates projection of this vector onto another vector
   * @param W Reference vector
   * @return Projection of this vector onto W
   */
  public MathVector Proj(MathVector W) {
    return W.normal().mul(Comp(W));
  }
  /**
   * Calculates component of this vector perpendicular to another vector
   * @param W Reference vector
   * @return component of this vector perpendicular to W
   */
  public double CompPerp(MathVector W) {
    return ProjPerp(W).length();
  }
  /**
   * Calculates projection of this vector perpendicular to another vector
   * @param W Reference vector
   * @return Projection of this vector onto W
   */
  public MathVector ProjPerp(MathVector W) {
    return sub(this,Proj(W));
  }
  /* Vector Length */
  public double lensq() {
    return dot(this,this);
  }
  public double length() {
    return Math.sqrt(lensq());
  }

  /* Negative Vectors */
  public MathVector opp() {
    double[] result=elements.clone();
    for(int i=0;i<result.length;i++) result[i]*=-1;
    return new MathVector(result);
  }

  /* Vector Element and Scalar Addition */
  public static MathVector add(MathVector A,MathVector B) {
    AssertEqualLength(A,B);
    double[] result=A.elements.clone();
    for(int i=0;i<A.elements.length;i++) result[i]+=B.get(i);
    return new MathVector(result);
  }
  public MathVector add(double B) {
    double[] result=elements.clone();
    for(int i=0;i<elements.length;i++) result[i]+=B;
    return new MathVector(result);
  }

  /* Vector Element and Scalar Subtraction */
  public static MathVector sub(MathVector A,MathVector B) {
    AssertEqualLength(A,B);
    double[] result=A.elements.clone();
    for(int i=0;i<A.elements.length;i++) result[i]-=B.get(i);
    return new MathVector(result);
  }
  public MathVector sub(double B) {
    double[] result=elements.clone();
    for(int i=0;i<elements.length;i++) result[i]-=B;
    return new MathVector(result);
  }
  /* Vector Element and Scalar Multiplication */
  public static MathVector mul(MathVector A,MathVector B) {
    AssertEqualLength(A,B);
    double[] result=A.elements.clone();
    for(int i=0;i<A.elements.length;i++) result[i]*=B.get(i);
    return new MathVector(result);
  }
  public MathVector mul(double B) {
    double[] result=elements.clone();
    for(int i=0;i<elements.length;i++) result[i]*=B;
    return new MathVector(result);
  }
  /* Vector element power */
  public MathVector pow(double B) {
    double[] result=elements.clone();
    for(int i=0;i<elements.length;i++) result[i]=Math.pow(result[i],B);
    return new MathVector(result);
  }

  /* Vector Element and Scalar Division */
  public static MathVector div(MathVector A,MathVector B) {
    AssertEqualLength(A,B);
    double[] result=A.elements.clone();
    for(int i=0;i<A.elements.length;i++) result[i]/=B.get(i);
    return new MathVector(result);
  }
  public MathVector div(double B) {
    double[] result=elements.clone();
    for(int i=0;i<elements.length;i++) result[i]/=B;
    return new MathVector(result);
  }

  /* Unit Vector Creation */
  public MathVector normal() {
    return div(length());
  }
  public String toString() {
    return toString(false,null);
  }
  public String toString(String format) {
    return toString(false,format);
  }
  public String toString(boolean printLength,String format) {
    return toString(printLength,false,format);
  }
  public String toString(boolean printLength, boolean PovBracket,String format) {
    StringBuffer S=new StringBuffer();
    if(PovBracket) S.append("<");
    for(int i=0;i<dimension();i++) {
      if(format!=null) {
        S.append(String.format(format,get(i)));
      } else {
        S.append(get(i));
      }
      if(i<dimension()-1)S.append(",");
    }
    if(printLength) S.append(" /*length: "+length()+"*/ ");
    if(PovBracket) S.append(">");
    return S.toString();
  }
  public static MathVector linterp(double SA, MathVector A, double SB,MathVector B, double S) {
    double[] stuff=new double[A.elements.length];
    for(int i=0;i<A.elements.length;i++) stuff[i]=Scalar.linterp(SA,A.elements[i],SB,B.elements[i],S);
    return new MathVector(stuff);
  }
  public Lerpable Lerp(Lerpable B, double t) {
    return linterp(0,this,1,(MathVector)B,t);
  }
  // Mutable stuff
  public void set(int i, double newValue) {
    elements[i]=newValue;
  }
  public void set(double newX, double newY, double newZ) {
    elements[0]=newX;
    elements[1]=newY;
    elements[2]=newZ;
  }
  public void set(double[] newElements) {
    elements=(double[])newElements.clone();
  }
  public void set(MathVector B) {
    setSubVector(0,B);
  }
  public void setX(double newValue) {
    set(0,newValue);
  }
  public void setY(double newValue) {
    set(1,newValue);
  }
  public void setZ(double newValue) {
    set(2,newValue);
  }
  public void setSubVector(int start, MathVector B) {
    for(int i=0;i<B.elements.length;i++) elements[i+start]=B.get(i);
  }
  public void setSubVector(int srcPos, double[] B, int destPos, int length) {
    System.arraycopy(B, srcPos, elements, destPos, length);
  }
  public void setSubVector(int start, double[] B) {
    setSubVector(0, elements, start, B.length);
  }
  public void setSubVector(int[] index, double[] B) {
    for(int i=0;i<index.length;i++) elements[index[i]]=B[i];
  }
  public void setSubVector(int[] index, MathVector B) {
    setSubVector(index,B.elements);
  }

  // Accumulative Vector Element and Scalar Addition
  public void addEq(MathVector B) {
    AssertEqualLength(this,B);
    for(int i=0;i<elements.length;i++) elements[i]+=B.get(i);
  }
  public void addScaleEq(MathVector B, double s) {
    AssertEqualLength(this,B);
    for(int i=0;i<elements.length;i++) elements[i]+=B.get(i)*s;
  }
  public void addEq(double B) {
    for(int i=0;i<elements.length;i++) elements[i]+=B;
  }

  // Accumulative Vector Element and Scalar Subtraction
  public void subEq(MathVector B) {
    addScaleEq(B,-1.0);
  }
  public void subScaleEq(MathVector B, double s) {
    addScaleEq(B,-s);
  }
  public void subEq(double B) {
    for(int i=0;i<elements.length;i++) elements[i]-=B;
  }

  // Accumulative Vector Element and Scalar Multiplication
  public void mulEq(MathVector B) {
    AssertEqualLength(this,B);
    for(int i=0;i<elements.length;i++) elements[i]*=B.get(i);
  }
  public void mulEq(double B) {
    for(int i=0;i<elements.length;i++) elements[i]*=B;
  }

  // Accumulative Vector Element and Scalar Division
  public void divEq(MathVector B) {
    AssertEqualLength(this,B);
    for(int i=0;i<elements.length;i++) elements[i]/=B.get(i);
  }
  public void divEq(double B) {
    for(int i=0;i<elements.length;i++) elements[i]/=B;
  }

  //Accumulative Element Power
  public void powEq(double B) {
    for(int i=0;i<elements.length;i++) elements[i]=Math.pow(elements[i], B);
  }

  // Accumulative normalization
  public void normalEq() {
    divEq(length());
  }

  // Accumulative negation
  public void negEq() {
    mulEq(-1.0);
  }

  // Accumulative cross product (accumulate in left side)
  public void crossEq(MathVector B) {
    if(this.dimension()!=3) throw new IllegalArgumentException("Cross products can only be taken on 3-vectors, but this is a "+this.dimension()+"-vector");
    if(B.dimension()!=3) throw new IllegalArgumentException("Cross products can only be taken on 3-vectors, but B is a "+B.dimension()+"-vector");
    double X=Y()*B.Z()-Z()*B.Y();
    double Y=Z()*B.X()-X()*B.Z();
    double Z=X()*B.Y()-Y()*B.X();
    elements[0]=X;
    elements[1]=Y;
    elements[2]=Z;
  }
  // Accumulative cross product (accumulate in right side)
  public void rcrossEq(MathVector A) {
    if(A.dimension()!=3) throw new IllegalArgumentException("Cross products can only be taken on 3-vectors, but A is a "+A.dimension()+"-vector");
    if(this.dimension()!=3) throw new IllegalArgumentException("Cross products can only be taken on 3-vectors, but this is a "+this.dimension()+"-vector");
    double X=A.Y()*Z()-A.Z()*Y();
    double Y=A.Z()*X()-A.X()*Z();
    double Z=A.X()*Y()-A.Y()*X();
    elements[0]=X;
    elements[1]=Y;
    elements[2]=Z;
  }
  //Return minimum element
  public double min() { 
    double result=elements[0];
    for(int i=1;i<elements.length;i++) if(elements[i]<result) result=elements[i];
    return result;
  }
  //Return maximum element
  public double max() { 
    double result=elements[0];
    for(int i=1;i<elements.length;i++) if(elements[i]>result) result=elements[i];
    return result;
  }
  //Return minimum element
  public double minAbs() { 
    double result=abs(elements[0]);
    for(int i=1;i<elements.length;i++) if(abs(elements[i])<result) result=abs(elements[i]);
    return result;
  }
  //Return maximum element
  public double maxAbs() { 
    double result=abs(elements[0]);
    for(int i=1;i<elements.length;i++) if(abs(elements[i])>result) result=abs(elements[i]);
    return result;
  }
  //Accumulative absolute value 
  public void absEq() {
    for(int i=0;i<elements.length;i++) elements[i]=abs(elements[i]);
  }
  public MathVector vabs() {
    MathVector result=new MathVector(this);
    result.absEq();
    return result;
  }

  public MathVector sub(MathVector B) {
    return MathVector.sub(this, B);
  }

  public MathVector add(MathVector B) {
    return MathVector.add(this,B);
  }
}
