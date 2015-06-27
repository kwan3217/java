package org.kwansystems.tools;

import java.util.*;
import java.math.*;

/**
 * Immutable rational number class. Stored as two <b><tt>BigInteger</tt></b>s with no common factors. This is an immutable class, settable once but
 * not changeable after creation. In this sense it is like the String class.
 *
 */
public class Rational extends Number implements Comparable<Rational> {
  private static final long serialVersionUID = -8071662591036833415L;
  protected BigInteger p, q;
  protected Rational() {
    
  }
  public Rational(BigInteger Lp, BigInteger Lq) {
    if(Lq.equals(BigInteger.ZERO)) throw new IllegalArgumentException("Can't divide by zero! p="+Lp+", q="+Lq);
    long sign=(Lp.compareTo(BigInteger.ZERO)>=0)?1:-1;
    p=Lp.abs().multiply(BigInteger.valueOf(sign));
    q=Lq.abs();
    reduce();
  }
  /**
   * Create a new rational number from two Java <b><tt>long</tt></b>s. 
   */
  public Rational(long Ln, long Ld) {
    this(BigInteger.valueOf(Ln),BigInteger.valueOf(Ld));
  }
  public Rational(String Ln, String Ld) {
    this(new BigInteger(Ln),new BigInteger(Ld));
  }
  /**
   * Reduces a rational number to its lowest-terms form.
   */
  private void reduce() {
    BigInteger g=p.gcd(q);
    p=p.divide(g);
    q=q.divide(g);
  }
  /** Get the value of this rational number in the form of a Java <tt><b>double</b></tt>.
   * @see java.lang.Number#doubleValue()
   */
  @Override
  public double doubleValue() {
    return p.doubleValue()/q.doubleValue();
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
    Rational other;
    try {
      other=(Rational)Lother;
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
  public Rational add(Rational b) {
    BigInteger l=lcm(q,b.q);
    BigInteger af=l.divide(q);
    BigInteger bf=l.divide(b.q);
    BigInteger an=p.multiply(af);
    BigInteger bn=b.p.multiply(bf);
    return new Rational(an.add(bn),l);
  }
  public Rational negate() {
    return new Rational(p.negate(),q);
  }
  public Rational subtract(Rational b) {
    return add(b.negate());
  }
  public Rational multiply(Rational b) {
    return new Rational(p.multiply(b.p),q.multiply(b.q));
  }
  public Rational inverse() {
    return new Rational(q,p);
  }
  public Rational divide(Rational b) {
    return multiply(b.inverse());
  }
  public String toString() {
    return String.format("%s/%s", p.toString(),q.toString());
  }
  public static void main(String[] args) {
    Rational A=new Rational(2,1);
    Rational B=new Rational(1,6);
    System.out.println(""+A+"+"+B+"="+A.add(B));
    System.out.println(""+A+"-"+B+"="+A.subtract(B));
    System.out.println(""+A+"*"+B+"="+A.multiply(B));
    System.out.println("("+A+")/("+B+")="+A.divide(B));
    Rational[] C=new Rational[5];
    C[0]=new Rational(2825,27648);
    C[1]=new Rational(18575,48384);
    C[2]=new Rational(13525,55296);
    C[3]=new Rational(277,14336);
    C[4]=new Rational(1,4);
    Rational Acc=new Rational(0,1);
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
  public int compareTo(Rational b) {
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
      return ((p.compareTo(BigInteger.ZERO)<0)?"-":"")+"\\frac{"+p.abs().toString()+"}{"+q.toString()+"}";
    }
  }
}
