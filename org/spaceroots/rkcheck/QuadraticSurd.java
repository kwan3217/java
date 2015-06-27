package org.spaceroots.rkcheck;

import java.math.BigInteger;
import java.math.BigDecimal;

/**
 * This class implements <a
 * href="http://mathworld.wolfram.com/QuadraticIrrationalNumber.html">quadratic
 * irrational numbers</a> (which are also called quadratic surds).

 * <p>Quadratic irrational numbers are roots of quadratic polynoms
 * with integer coefficients. They can be written:
 *   <pre>
 *         p1 + p2 sqrt (d)
 *   z =  ------------------
 *               q
 *   </pre>
 * where p1 and p2 are integers, d is a square free strictly positive
 * integer and q is a strictly positive integer. This number is a root
 * of:
 *   <pre>
 *     2  2                2       2
 *    q  X  - 2 q p1 X + p1  - d p2  = 0
 *   </pre>
 * </p>

 * <p>The set of quadratic irrational numbers associated with a given
 * number d is a field denoted F[sqrt(d)]. This class implements
 * computation within such a field.</p>

 * @version $Id: QuadraticSurd.java,v 1.4 2004/05/23 19:41:36 luc Exp $
 * @author L. Maisonobe

 */

public class QuadraticSurd {

  /**
   * Simple constructor.
   * Build a null quadratic surd
   */
  public QuadraticSurd() {
    p1 = BigInteger.ZERO;
    p2 = BigInteger.ZERO;
    d  = BigInteger.ZERO;
    q  = BigInteger.ONE;
  }

  /**
   * Simple constructor.
   * Build a quadratic surd from a single integer
   * @param l value of the quadratic surd
   */
  public QuadraticSurd(long l) {
    p1 = BigInteger.valueOf(l);
    p2 = BigInteger.ZERO;
    d  = BigInteger.ZERO;
    q  = BigInteger.ONE;
  }

  /**
   * Simple constructor.
   * Build a quadratic surd from a single integer
   * @param l value of the quadratic surd
   */
  public QuadraticSurd(BigInteger l) {
    p1 = l;
    p2 = BigInteger.ZERO;
    d  = BigInteger.ZERO;
    q  = BigInteger.ONE;
  }

  /**
   * Simple constructor.
   * Build a quadratic surd from a numerator and a denominator. This
   * is a simple rational number.
   * @param numerator numerator of the quadratic surd
   * @param denominator denominator of the quadratic surd
   * @exception ArithmeticException if the denominator is zero
   */
  public QuadraticSurd(long numerator, long denominator) {
    this(BigInteger.valueOf(numerator), BigInteger.ZERO, BigInteger.ZERO,
         BigInteger.valueOf(denominator));
  }

  /**
   * Simple constructor.
   * Build a quadratic surd from a numerator and a denominator. This
   * is a simple rational number.
   * @param numerator numerator of the quadratic surd
   * @param denominator denominator of the quadratic surd
   * @exception ArithmeticException if the denominator is zero
   */
  public QuadraticSurd(BigInteger numerator, BigInteger denominator) {
    this(numerator, BigInteger.ZERO, BigInteger.ZERO, denominator);
  }

  /**
   * Simple constructor.
   * Build a quadratic surd from its coordinates
   * @param p1 integer part of the numerator
   * @param p2 coefficient of the root part of the numerator
   * @param d  root
   * @param q denominator
   * @exception ArithmeticException if the root is negative
   * @exception ArithmeticException if the denominator is zero
   */
  public QuadraticSurd(long p1, long p2, long d, long q) {
    this(BigInteger.valueOf(p1), BigInteger.valueOf(p2),
         BigInteger.valueOf(d), BigInteger.valueOf(q));
  }

  /**
   * Simple constructor.
   * Build a quadratic surd from its coordinates
   * @param p1 integer part of the numerator
   * @param p2 coefficient of the root part of the numerator
   * @param d  root
   * @param q denominator
   * @exception ArithmeticException if the root is negative
   * @exception ArithmeticException if the denominator is zero
   */
  public QuadraticSurd(BigInteger p1, BigInteger p2, BigInteger d,
                       BigInteger q) {

    if (d.signum() < 0) {
      throw new ArithmeticException("negative root");
    }

    if (q.signum() == 0) {
      throw new ArithmeticException("divide by zero");
    }

    this.p1 = p1;
    this.p2 = p2;
    this.d  = d;
    this.q  = q;

    simplify();

  }

  /** Simple constructor.
   * Build a quadratic surd by approximating a BigDecimal to a given
   * tolerance using continued fractions
   * @param r real number to approximate
   * @param tolerance tolerance allowed for the approximation
   */
  public QuadraticSurd(BigDecimal r, BigDecimal tolerance) {

    p2 = BigInteger.ZERO;
    d  = BigInteger.ZERO;

    boolean negative = (r.signum() < 0);
    BigDecimal a = negative ? r.negate() : r;

    // initialize the recurrence relation
    BigInteger An2 = BigInteger.ZERO;
    BigInteger Bn2 = BigInteger.ONE;
    BigInteger An1 = BigInteger.ONE;
    BigInteger Bn1 = BigInteger.ZERO;

    BigDecimal zero = new BigDecimal(BigInteger.ZERO);
    BigDecimal one  = new BigDecimal(BigInteger.ONE);

    do {

      BigInteger p = a.toBigInteger();

      BigInteger An = p.multiply(An1).add(An2);
      p1 = negative ? An.negate() : An;
      q  = p.multiply(Bn1).add(Bn2);

      a = a.subtract(new BigDecimal(p));
      if (a.compareTo(zero) == 0) {
        return;
      }
      a = one.divide(a, r.scale(), BigDecimal.ROUND_HALF_EVEN);

      An2 = An1;
      An1 = An;
      Bn2 = Bn1;
      Bn1 = q;

    } while (bigDecimalValue(r.scale()).subtract(r).abs().compareTo(tolerance) > 0);

  }

  /**
   * Copy-constructor.
   * @param qs quadratic surd to copy
   */
  public QuadraticSurd(QuadraticSurd qs) {
    p1 = qs.p1;
    p2 = qs.p2;
    d  = qs.d;
    q  = qs.q;
  }

  /**
   * Check if a quadratic surd belong to the same field as the
   * instance.
   * @param qs quadratic surd to check against the instance
   * @return true if the specified quadratic surd belong to the same
   * field as the instance
   */
  public boolean sameField(QuadraticSurd qs) {
    return (p2.signum() == 0)
      || (qs.p2.signum() == 0)
      || (d.compareTo(qs.d) == 0);
  }

  /**
   * Check if a quadratic surd belong to the same field as the
   * instance.
   * @param qs quadratic surd to check against the instance
   * @exception ArithmeticException if the two quadratic surds do not
   * belong to the same field (i.e. if they do not share the same
   * root)
   */
  private void chokeIfDifferentField(QuadraticSurd qs)
    throws ArithmeticException {
    if (! sameField(qs)) {
      throw new ArithmeticException("forbidden operation between"
                                    + " quadratic surds belonging"
                                    + " to different fields: "
                                    + this + " vs. " + qs);
    }
  }

  /**
   * Negate the instance
   */
  public void negateSelf() {
    p1 = p1.negate();
    p2 = p2.negate();
  }

  /**
   * Negate a quadratic surd.
   * @param qs quadratic surd to negate
   * @return a new quadratic surd which is the opposite of r
   */
  public static QuadraticSurd negate(QuadraticSurd qs) {
    QuadraticSurd copy = new QuadraticSurd(qs);
    copy.negateSelf();
    return copy;
  }

  /**
   * Add a quadratic surd to the instance.
   * @param qs quadratic surd to add.
   * @exception ArithmeticException if the two quadratic surds do not
   * belong to the same field (i.e. if they do not share the same
   * root)
   */
  public void addToSelf(QuadraticSurd qs)
    throws ArithmeticException {

    // safety check
    chokeIfDifferentField(qs);

    // computation
    if (p2.signum() == 0) {
      d = qs.d;
    }
    p1 = p1.multiply(qs.q).add(q.multiply(qs.p1));
    p2 = p2.multiply(qs.q).add(q.multiply(qs.p2));
    q  = q.multiply(qs.q);

    // simplification
    simplify();

  }

  /** Add two quadratic surds.
   * @param qs1 first quadratic surd
   * @param qs2 second quadratic surd
   * @return a new quadratic surd which is the sum of qs1 and qs2
   * @exception ArithmeticException if the two quadratic surds do not
   * belong to the same field (i.e. if they do not share the same
   * root)
   */
  public static QuadraticSurd add(QuadraticSurd qs1, QuadraticSurd qs2)
    throws ArithmeticException {
    QuadraticSurd copy = new QuadraticSurd(qs1);
    copy.addToSelf(qs2);
    return copy;
  }

  /**
   * Subtract a quadratic surd to the instance.
   * @param qs quadratic surd to subtract.
   * @exception ArithmeticException if the two quadratic surds do not
   * belong to the same field (i.e. if they do not share the same
   * root)
   */
  public void subtractFromSelf(QuadraticSurd qs)
    throws ArithmeticException {

    // safety check
    chokeIfDifferentField(qs);

    // computation
    if (p2.signum() == 0) {
      d = qs.d;
    }
    p1 = p1.multiply(qs.q).subtract(q.multiply(qs.p1));
    p2 = p2.multiply(qs.q).subtract(q.multiply(qs.p2));
    q  = q.multiply(qs.q);

    // simplification
    simplify();

  }

  /** Subtract two quadratic surds.
   * @param qs1 first quadratic surd
   * @param qs2 second quadratic surd
   * @return a new quadratic surd which is the difference qs1 minus qs2
   * @exception ArithmeticException if the two quadratic surds do not
   * belong to the same field (i.e. if they do not share the same
   * root)
   */
  public static QuadraticSurd subtract(QuadraticSurd qs1, QuadraticSurd qs2)
    throws ArithmeticException {
    QuadraticSurd copy = new QuadraticSurd(qs1);
    copy.subtractFromSelf(qs2);
    return copy;
  }

  /** Multiply the instance by an integer.
   * @param l integer to multiply by
   */
  public void multiplySelf(long l) {
    BigInteger bl = BigInteger.valueOf(l);
    p1 = p1.multiply(bl);
    p2 = p2.multiply(bl);
    simplify();
  }

  /** Multiply a quadratic surd by an integer.
   * @param qs quadratic surd
   * @param l integer to multiply by
   */
  public static QuadraticSurd multiply(QuadraticSurd qs, long l) {
    QuadraticSurd copy = new QuadraticSurd(qs);
    copy.multiplySelf(l);
    return copy;
  }

  /** Multiply the instance by a quadratic surd.
   * @param qs quadratic surd to multiply by
   * @exception ArithmeticException if the two quadratic surds do not
   * belong to the same field (i.e. if they do not share the same
   * root)
   */
  public void multiplySelf(QuadraticSurd qs)
    throws ArithmeticException {

    // safety check
    chokeIfDifferentField(qs);

    // computation
    if (p2.signum() == 0) {
      d = qs.d;
    }
    BigInteger newP1 = p1.multiply(qs.p1).add(p2.multiply(qs.p2).multiply(d));
    p2 = p1.multiply(qs.p2).add(p2.multiply(qs.p1));
    p1 = newP1;
    q  = q.multiply(qs.q);

    // simplification
    simplify();

  }

  /** Multiply two quadratic surds.
   * @param qs1 first quadratic surd
   * @param qs2 second quadratic surd
   * @return a new quadratic surd which is the product of qs1 and qs2
   * @exception ArithmeticException if the two quadratic surds do not
   * belong to the same field (i.e. if they do not share the same
   * root)
   */
  public static QuadraticSurd multiply(QuadraticSurd qs1, QuadraticSurd qs2)
    throws ArithmeticException {
    QuadraticSurd copy = new QuadraticSurd(qs1);
    copy.multiplySelf(qs2);
    return copy;
  }

  /** Divide the instance by an integer.
   * @param l integer to divide by
   * @exception ArithmeticException if l is zero
   */
  public void divideSelf(long l)
    throws ArithmeticException {

    if (l == 0l) {
      throw new ArithmeticException("divide by zero");
    }

    q = q.multiply(BigInteger.valueOf(l));

    simplify();

  }

  /** Divide a quadratic surd by an integer
   * @param qs quadratic surd
   * @param l integer
   * @return a new quadratic surd which is the quotient of qs by l
   * @exception ArithmeticException if l is zero
   */
  public static QuadraticSurd divide(QuadraticSurd qs, long l)
    throws ArithmeticException {
    QuadraticSurd copy = new QuadraticSurd(qs);
    copy.divideSelf(l);
    return copy;
  }

  /** Divide the instance by a quadratic surd.
   * @param qs quadratic surd to divide by
   * @exception ArithmeticException if qs is zero
   * @exception ArithmeticException if the two quadratic surds do not
   * belong to the same field (i.e. if they do not share the same
   * root)
   */
  public void divideSelf(QuadraticSurd qs)
    throws ArithmeticException {
    multiplySelf(QuadraticSurd.invert(qs));
  }

  /** Divide two quadratic surds.
   * @param qs1 first quadratic surd
   * @param qs2 second quadratic surd
   * @return a new quadratic surd which is the quotient of qs1 by qs2
   * @exception ArithmeticException if qs2 is zero
   * @exception ArithmeticException if the two quadratic surds do not
   * belong to the same field (i.e. if they do not share the same
   * root)
   */
  public static QuadraticSurd divide(QuadraticSurd qs1, QuadraticSurd qs2) {
    QuadraticSurd copy = new QuadraticSurd(qs1);
    copy.divideSelf(qs2);
    return copy;
  }

  /** Invert the instance.
   * Replace the instance by its inverse.
   * @exception ArithmeticException if the instance is zero
   */
  public void invertSelf()
    throws ArithmeticException {

    // safety check
    if (isZero()) {
      throw new ArithmeticException("divide by zero");
    }

    // computation
    BigInteger newP1 = q.multiply(p1);
    BigInteger newP2 = q.multiply(p2).negate();
    q  = p1.multiply(p1).subtract(d.multiply(p2).multiply(p2));
    p1 = newP1;
    p2 = newP2;

    // simplification
    simplify();

  }

  /** Invert a quadratic surd.
   * @param qs quadratic surd to invert
   * @return a new quadratic surd which is the inverse of qs
   * @exception ArithmeticException if qs is zero
   */
  public static QuadraticSurd invert(QuadraticSurd qs)
    throws ArithmeticException {
    QuadraticSurd copy = new QuadraticSurd(qs);
    copy.invertSelf();
    return copy;
  }

  /** Check if the instance is equal to 0.
   * @return true if the instance is equal to 0
   */
  public boolean isZero() {
    return (p1.signum() == 0) && (p2.signum() == 0);
  }

  /** Check if the instance is equal to 1.
   * @return true if the instance is equal to 1
   */
  public boolean isOne() {
    return (p1.compareTo(BigInteger.ONE) == 0)
      && (p2.signum() == 0)
      && (q.compareTo(BigInteger.ONE) == 0);
  }

  /** Check if the number is integer.
   * @return true if the number is an integer
   */
  public boolean isInteger() {
    return (p2.signum() == 0) && (q.compareTo(BigInteger.ONE) == 0);
  }

  /** Check if the instance holds a rational number.
   * @return true if the instance holds a rational number
   */
  public boolean isRational() {
    return p2.signum() == 0;
  }

  /** Check if the instance is equal to another quadratic surd.
   * Equality here is having the same value.
   * @return true if the object is a quadratic surd which has the
   * same value as the instance
   */
  public boolean equals(Object o) {
    if (o instanceof QuadraticSurd) {
      QuadraticSurd qs = (QuadraticSurd ) o;
      return (p1.compareTo(qs.p1) == 0)
        && (p2.compareTo(qs.p2) == 0)
        && (d.compareTo(qs.d) == 0)
        && (q.compareTo(qs.q) == 0);
    } else {
      return false;
    }
  }

  /** Returns a hash code value for the object.
   * The hash code value is computed from the reduced coefficents,
   * hence equal quadratic surds have the same hash code, as required
   * by the method specification.
   * @return a hash code value for this object.
   */
  public int hashCode() {
    return (int)((p1.hashCode() << 12) ^ (p2.hashCode() << 8)
                 ^ (d.hashCode() << 4) ^ q.hashCode());
  }

  /** Simplify a quadratic surd.
   * Simplification involves normalizing the signs, removing small
   * square factors from the root and removing common factors.
   */
  private void simplify() {

    // handle specific case
    if (isZero()) {
      d = BigInteger.ZERO;
      q = BigInteger.ONE;
      return;
    }

    // normalize signs
    if (q.signum() < 0) {
      p1 = p1.negate();
      p2 = p2.negate();
      q  = q.negate();
    }

    // try to remove exact square terms from d
    // using the primes smaller than 5000
    int k = 0;
    long squaredPrime;
    do {
      squaredPrime = primes[k] * primes[k];
      if (d.remainder(BigInteger.valueOf(squaredPrime)).signum() == 0) {
        d  = d.divide(BigInteger.valueOf(squaredPrime));
        p2 = p2.multiply(BigInteger.valueOf(primes[k]));
      } else {
        ++k;
      }
    } while ((k < primes.length)
             && (d.compareTo(BigInteger.valueOf(squaredPrime)) >= 0));

    // remove common factors
    BigInteger gcd = q.gcd(p1.gcd(p2));
    if (gcd.compareTo(BigInteger.ONE) > 0) {
      p1 = p1.divide(gcd);
      p2 = p2.divide(gcd);
      q  = q.divide(gcd);
    }

  }

  /** Get the numerator of the rational part.
   * <p>If the instance is a number of the form:
   * <pre>
   *         p1 + p2 sqrt (d)
   *   z =  ------------------
   *               q
   * </pre>
   * then this method returns p1</p>
   * @return numerator of the rational part
   */
  public BigInteger getRationalNumerator() {
    return p1;
  }

  /** Get the coefficient of the irrational part.
   * <p>If the instance is a number of the form:
   * <pre>
   *         p1 + p2 sqrt (d)
   *   z =  ------------------
   *               q
   * </pre>
   * then this method returns p2</p>
   * @return coefficient of the irrational part
   */
  public BigInteger getRootCoefficient() {
    return p2;
  }

  /** Get the root element of the irrational part.
   * <p>If the instance is a number of the form:
   * <pre>
   *         p1 + p2 sqrt (d)
   *   z =  ------------------
   *               q
   * </pre>
   * then this method returns d</p>
   * @return root element of the irrational part
   */
  public BigInteger getRootElement() {
    return d;
  }

  /** Get the denominator.
   * <p>If the instance is a number of the form:
   * <pre>
   *         p1 + p2 sqrt (d)
   *   z =  ------------------
   *               q
   * </pre>
   * then this method returns q</p>
   * @return denominator
   */
  public BigInteger getDenominator() {
    return q;
  }

  /** Returns the value of the underlying number as a double.
   * @return the numeric value represented by this object after
   * conversion to type double.
   */
  public double doubleValue() {
    if (p2.signum() == 0) {
      return p1.doubleValue() / q.doubleValue();
    } else {
      return (p1.doubleValue() + p2.doubleValue() * Math.sqrt(d.doubleValue()))
        / q.doubleValue();
    }
  }

  /** Returns the value of the underlying number as a BigDecimal.
   * @param scale scale of the BigDecimal to return
   * @return the numeric value represented by this object after
   * conversion to type BigDecimal.
   */
  public BigDecimal bigDecimalValue(int scale) {
    if (p2.signum() == 0) {
      return new BigDecimal(p1).divide(new BigDecimal(q),
                                       scale, BigDecimal.ROUND_HALF_EVEN);
    } else {

      // compute sqrt(d)
      BigDecimal s = new BigDecimal(Math.sqrt(d.doubleValue()));
      BigDecimal bdD = new BigDecimal(d);
      s = s.setScale(scale, BigDecimal.ROUND_HALF_EVEN);
      BigDecimal oldS;
      do {
        oldS = s;
        BigDecimal s2  = s.multiply(s);
        BigDecimal num = s.multiply(s2.add(BigDecimal.valueOf(3l).multiply(bdD)));
        BigDecimal den = s2.multiply(BigDecimal.valueOf(3l)).add(bdD);
        s = num.divide(den, scale, BigDecimal.ROUND_HALF_EVEN);
      } while (s.subtract(oldS).abs().signum() != 0);

      // compute the quadratic surd value
      BigDecimal a = new BigDecimal(p1).add(new BigDecimal(p2).multiply(s));
      return a.divide(new BigDecimal(q), scale, BigDecimal.ROUND_HALF_EVEN);

    }
  }

  /** Returns a string representation of the underlying number.
   * @return  a string representation of the underlying number
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();

    if ((p1.signum() == 0) && (p2.signum() == 0)) {
      return "0";
    }

    if (p1.signum() != 0) {
      sb.append(p1);
      if (p2.signum() != 0) {
        sb.append((p2.signum() < 0) ? " -" : " +");
        if (p2.abs().compareTo(BigInteger.ONE) != 0) {
          sb.append(' ');
          sb.append(p2.abs());
        }
        sb.append(" sqrt(");
        sb.append(d);
        sb.append(')');
      }
    } else {
      if (p2.signum() != 0) {
        sb.append((p2.signum() < 0) ? '-' : '+');
        if (p2.abs().compareTo(BigInteger.ONE) != 0) {
          sb.append(' ');
          sb.append(p2.abs());
        }
        sb.append(" sqrt(");
        sb.append(d);
        sb.append(')');
      }
    }

    if (q.compareTo(BigInteger.ONE) != 0) {
      if (sb.indexOf(" ") > 0) {
        sb.insert(0, '(');
        sb.append(')');
      }

      sb.append(" / ");
      sb.append(q);

    }

    return sb.toString();

  }

  /** Rational numerator. */
  private BigInteger p1;

  /** Root coefficient. */
  private BigInteger p2;

  /** Root element. */
  private BigInteger d;

  /** Denominator. */
  private BigInteger q;

  /** Non trivial primes (i.e. 1 is not included) smaller than 5000. */
  private static long[] primes = {
      2l,    3l,    5l,    7l,   11l,   13l,   17l,   19l,   23l,   29l,   31l,
     37l,   41l,   43l,   47l,   53l,   59l,   61l,   67l,   71l,   73l,   79l,
     83l,   89l,   97l,  101l,  103l,  107l,  109l,  113l,  127l,  131l,  137l,
    139l,  149l,  151l,  157l,  163l,  167l,  173l,  179l,  181l,  191l,  193l,
    197l,  199l,  211l,  223l,  227l,  229l,  233l,  239l,  241l,  251l,  257l,
    263l,  269l,  271l,  277l,  281l,  283l,  293l,  307l,  311l,  313l,  317l,
    331l,  337l,  347l,  349l,  353l,  359l,  367l,  373l,  379l,  383l,  389l,
    397l,  401l,  409l,  419l,  421l,  431l,  433l,  439l,  443l,  449l,  457l,
    461l,  463l,  467l,  479l,  487l,  491l,  499l,  503l,  509l,  521l,  523l,
    541l,  547l,  557l,  563l,  569l,  571l,  577l,  587l,  593l,  599l,  601l,
    607l,  613l,  617l,  619l,  631l,  641l,  643l,  647l,  653l,  659l,  661l,
    673l,  677l,  683l,  691l,  701l,  709l,  719l,  727l,  733l,  739l,  743l,
    751l,  757l,  761l,  769l,  773l,  787l,  797l,  809l,  811l,  821l,  823l,
    827l,  829l,  839l,  853l,  857l,  859l,  863l,  877l,  881l,  883l,  887l,
    907l,  911l,  919l,  929l,  937l,  941l,  947l,  953l,  967l,  971l,  977l,
    983l,  991l,  997l, 1009l, 1013l, 1019l, 1021l, 1031l, 1033l, 1039l, 1049l,
   1051l, 1061l, 1063l, 1069l, 1087l, 1091l, 1093l, 1097l, 1103l, 1109l, 1117l,
   1123l, 1129l, 1151l, 1153l, 1163l, 1171l, 1181l, 1187l, 1193l, 1201l, 1213l,
   1217l, 1223l, 1229l, 1231l, 1237l, 1249l, 1259l, 1277l, 1279l, 1283l, 1289l,
   1291l, 1297l, 1301l, 1303l, 1307l, 1319l, 1321l, 1327l, 1361l, 1367l, 1373l,
   1381l, 1399l, 1409l, 1423l, 1427l, 1429l, 1433l, 1439l, 1447l, 1451l, 1453l,
   1459l, 1471l, 1481l, 1483l, 1487l, 1489l, 1493l, 1499l, 1511l, 1523l, 1531l,
   1543l, 1549l, 1553l, 1559l, 1567l, 1571l, 1579l, 1583l, 1597l, 1601l, 1607l,
   1609l, 1613l, 1619l, 1621l, 1627l, 1637l, 1657l, 1663l, 1667l, 1669l, 1693l,
   1697l, 1699l, 1709l, 1721l, 1723l, 1733l, 1741l, 1747l, 1753l, 1759l, 1777l,
   1783l, 1787l, 1789l, 1801l, 1811l, 1823l, 1831l, 1847l, 1861l, 1867l, 1871l,
   1873l, 1877l, 1879l, 1889l, 1901l, 1907l, 1913l, 1931l, 1933l, 1949l, 1951l,
   1973l, 1979l, 1987l, 1993l, 1997l, 1999l, 2003l, 2011l, 2017l, 2027l, 2029l,
   2039l, 2053l, 2063l, 2069l, 2081l, 2083l, 2087l, 2089l, 2099l, 2111l, 2113l,
   2129l, 2131l, 2137l, 2141l, 2143l, 2153l, 2161l, 2179l, 2203l, 2207l, 2213l,
   2221l, 2237l, 2239l, 2243l, 2251l, 2267l, 2269l, 2273l, 2281l, 2287l, 2293l,
   2297l, 2309l, 2311l, 2333l, 2339l, 2341l, 2347l, 2351l, 2357l, 2371l, 2377l,
   2381l, 2383l, 2389l, 2393l, 2399l, 2411l, 2417l, 2423l, 2437l, 2441l, 2447l,
   2459l, 2467l, 2473l, 2477l, 2503l, 2521l, 2531l, 2539l, 2543l, 2549l, 2551l,
   2557l, 2579l, 2591l, 2593l, 2609l, 2617l, 2621l, 2633l, 2647l, 2657l, 2659l,
   2663l, 2671l, 2677l, 2683l, 2687l, 2689l, 2693l, 2699l, 2707l, 2711l, 2713l,
   2719l, 2729l, 2731l, 2741l, 2749l, 2753l, 2767l, 2777l, 2789l, 2791l, 2797l,
   2801l, 2803l, 2819l, 2833l, 2837l, 2843l, 2851l, 2857l, 2861l, 2879l, 2887l,
   2897l, 2903l, 2909l, 2917l, 2927l, 2939l, 2953l, 2957l, 2963l, 2969l, 2971l,
   2999l, 3001l, 3011l, 3019l, 3023l, 3037l, 3041l, 3049l, 3061l, 3067l, 3079l,
   3083l, 3089l, 3109l, 3119l, 3121l, 3137l, 3163l, 3167l, 3169l, 3181l, 3187l,
   3191l, 3203l, 3209l, 3217l, 3221l, 3229l, 3251l, 3253l, 3257l, 3259l, 3271l,
   3299l, 3301l, 3307l, 3313l, 3319l, 3323l, 3329l, 3331l, 3343l, 3347l, 3359l,
   3361l, 3371l, 3373l, 3389l, 3391l, 3407l, 3413l, 3433l, 3449l, 3457l, 3461l,
   3463l, 3467l, 3469l, 3491l, 3499l, 3511l, 3517l, 3527l, 3529l, 3533l, 3539l,
   3541l, 3547l, 3557l, 3559l, 3571l, 3581l, 3583l, 3593l, 3607l, 3613l, 3617l,
   3623l, 3631l, 3637l, 3643l, 3659l, 3671l, 3673l, 3677l, 3691l, 3697l, 3701l,
   3709l, 3719l, 3727l, 3733l, 3739l, 3761l, 3767l, 3769l, 3779l, 3793l, 3797l,
   3803l, 3821l, 3823l, 3833l, 3847l, 3851l, 3853l, 3863l, 3877l, 3881l, 3889l,
   3907l, 3911l, 3917l, 3919l, 3923l, 3929l, 3931l, 3943l, 3947l, 3967l, 3989l,
   4001l, 4003l, 4007l, 4013l, 4019l, 4021l, 4027l, 4049l, 4051l, 4057l, 4073l,
   4079l, 4091l, 4093l, 4099l, 4111l, 4127l, 4129l, 4133l, 4139l, 4153l, 4157l,
   4159l, 4177l, 4201l, 4211l, 4217l, 4219l, 4229l, 4231l, 4241l, 4243l, 4253l,
   4259l, 4261l, 4271l, 4273l, 4283l, 4289l, 4297l, 4327l, 4337l, 4339l, 4349l,
   4357l, 4363l, 4373l, 4391l, 4397l, 4409l, 4421l, 4423l, 4441l, 4447l, 4451l,
   4457l, 4463l, 4481l, 4483l, 4493l, 4507l, 4513l, 4517l, 4519l, 4523l, 4547l,
   4549l, 4561l, 4567l, 4583l, 4591l, 4597l, 4603l, 4621l, 4637l, 4639l, 4643l,
   4649l, 4651l, 4657l, 4663l, 4673l, 4679l, 4691l, 4703l, 4721l, 4723l, 4729l,
   4733l, 4751l, 4759l, 4783l, 4787l, 4789l, 4793l, 4799l, 4801l, 4813l, 4817l,
   4831l, 4861l, 4871l, 4877l, 4889l, 4903l, 4909l, 4919l, 4931l, 4933l, 4937l,
   4943l, 4951l, 4957l, 4967l, 4969l, 4973l, 4987l, 4993l, 4999l
  };

}
