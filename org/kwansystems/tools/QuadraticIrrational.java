package org.kwansystems.tools;

import java.util.*;
import java.math.*;

/**
 * Immutable rational number class. Stored as two <b><tt>BigInteger</tt></b>s with no common factors. This is an immutable class, settable once but
 * not changeable after creation. In this sense it is like the String class.
 *
 */
public class QuadraticIrrational extends Rational {
  private static final long serialVersionUID = -8071662591036833415L;
  protected BigInteger r, d;
  public QuadraticIrrational(BigInteger Lp, BigInteger Lq) {
    this(Lp,BigInteger.ZERO,BigInteger.ONE,Lq);
  }
  public QuadraticIrrational(Rational LR) {
    this(LR.p,LR.q);
  }
  public QuadraticIrrational(QuadraticIrrational LQR) {
    this(LQR.p,LQR.r,LQR.d,LQR.q);
  }
  public QuadraticIrrational(BigInteger Lp, BigInteger Lr, BigInteger Ld, BigInteger Lq) {
    if(Lq.equals(BigInteger.ZERO)) throw new IllegalArgumentException("Can't divide by zero! q="+Lq);
    if(Ld.compareTo(BigInteger.ZERO)<0) throw new IllegalArgumentException("No imaginary numbers! d="+Ld);
    p=Lp;
    q=Lq;
    r=Lr;
    d=Ld;
  }
  /**
   * Create a new rational number from two Java <b><tt>long</tt></b>s. 
   */
  public QuadraticIrrational(long Ln, long Ld) {
    this(BigInteger.valueOf(Ln),BigInteger.valueOf(Ld));
  }
  public QuadraticIrrational(String Ln, String Ld) {
    this(new BigInteger(Ln),new BigInteger(Ld));
  }
  /** Get the value of this rational number in the form of a Java <tt><b>double</b></tt>.
   * @see java.lang.Number#doubleValue()
   */
  @Override
  public double doubleValue() {
    return (p.doubleValue()+r.doubleValue()*Math.sqrt(d.doubleValue()))/q.doubleValue();
  }

  /** Get the value of this rational number in the form of a Java <tt><b>float</b></tt>.
   * @see java.lang.Number#floatValue()
   */
  @Override
  public float floatValue() {
    return (float)doubleValue();
  }

  /** Get the value of this rational number in the form of a Java <tt><b>int</b></tt>.
   * @see java.lang.Number#intValue()
   */
  @Override
  public int intValue() {
    return (int)longValue();
  }

  /** Get the value of this rational number in the form of a Java <tt><b>long</b></tt>.
   * @see java.lang.Number#longValue()
   */
  @Override
  public long longValue() {
    return (long)doubleValue();
  }
  /** Get the numerator.
   * @return The numerator of this number in the form of a Java <tt><b>long</b></tt>.
   */
  public long getNumerator() {
    return p.longValue();
  }
  /** Get the denominator.
   * @return The denominator of this number in the form of a Java <tt><b>long</b></tt>.
   */
  public long getDenominator() {
    return q.longValue();
  }
  public BigInteger getBigNumerator() {
    return p;
  }
  public BigInteger getBigDenominator() {
    return q;
  }
  /** Check equality against another Rational.
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object Lother) {
    QuadraticIrrational other;
    try {
      other=(QuadraticIrrational)Lother;
    } catch (ClassCastException E) {return false;}
    return p.equals(other.p) && q.equals(other.q);
  }
  /** Find the least common multiple of two BigIntegers. 
   * @param a First input argument
   * @param b Second input argument
   * @return The smallest number which is acceptable as a common denominator of the input arguments.
   */
  private static BigInteger lcm(BigInteger a, BigInteger b) {
    return a.multiply(b).divide(a.gcd(b));
  }
  public QuadraticIrrational add(QuadraticIrrational b) {
    if(d.compareTo(b.d)!=0) {
      throw new IllegalArgumentException("Radical parts not compatible, this d="+d.toString()+", b.d="+b.d.toString());
    }
    BigInteger l=q.multiply(b.q);
    BigInteger af=l.divide(q);
    BigInteger bf=l.divide(b.q);
    BigInteger ap=p.multiply(af);
    BigInteger ar=r.multiply(af);
    BigInteger bp=b.p.multiply(bf);
    BigInteger br=b.r.multiply(bf);
    return new QuadraticIrrational(ap.add(bp),ar.add(br),d,l);
  }
  public QuadraticIrrational negate() {
    return new QuadraticIrrational(p.negate(),r.negate(),d,q);
  }
  public QuadraticIrrational subtract(QuadraticIrrational b) {
    return add(b.negate());
  }
  public String toString() {
    return String.format("(%s+%s*sqrt(%s)/%s", p.toString(),r.toString(),d.toString(),q.toString());
  }
  public static void main(String[] args) {
    QuadraticIrrational A=new QuadraticIrrational(2,1);
    QuadraticIrrational B=new QuadraticIrrational(1,6);
    System.out.println(""+A+"+"+B+"="+A.add(B));
    System.out.println(""+A+"-"+B+"="+A.subtract(B));
    System.out.println(""+A+"*"+B+"="+A.multiply(B));
    System.out.println("("+A+")/("+B+")="+A.divide(B));
    QuadraticIrrational[] C=new QuadraticIrrational[5];
    C[0]=new QuadraticIrrational(2825,27648);
    C[1]=new QuadraticIrrational(18575,48384);
    C[2]=new QuadraticIrrational(13525,55296);
    C[3]=new QuadraticIrrational(277,14336);
    C[4]=new QuadraticIrrational(1,4);
    QuadraticIrrational Acc=new QuadraticIrrational(0,1);
    System.out.println(Acc);
    for(int i=0;i<5;i++) {
      Acc=Acc.add(C[i]);
      System.out.println(Acc);
    }
    System.out.println("("+C[1]+").compareTo("+C[2]+")="+C[1].compareTo(C[2]));
  }
  /** Compare this number to another Rational number. This comparison is exact.
   * @param b Number to compare this to
   * @return Negative if this is less than b, zero if this is exactly equal to b, positive if this is greater than b.
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(QuadraticIrrational b) {
    BigInteger l=lcm(q,b.q);
    BigInteger af=l.divide(q);
    BigInteger bf=l.divide(b.q);
    BigInteger an=p.multiply(af);
    BigInteger bn=b.p.multiply(bf);
    return an.compareTo(bn);
  }
  public String toTeX() {
    if(q.equals(BigInteger.ONE)) {
      return p.toString();
    } else {
      return "\\frac{"+p.toString()+(r.compareTo(BigInteger.ZERO)>0?"+":"")+r.toString()+"\\sqrt{"+d.toString()+"}}{"+q.toString()+"}";
    }
  }
}
