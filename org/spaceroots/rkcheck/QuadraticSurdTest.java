package org.spaceroots.rkcheck;

import java.math.BigDecimal;
import junit.framework.*;

public class QuadraticSurdTest
  extends TestCase {

  public QuadraticSurdTest(String name) {
    super(name);
  }

  public void testNullDenominator() {
    try {
      QuadraticSurd f = new QuadraticSurd(1l, 0);
      fail("an exception should have been thrown");
    } catch (ArithmeticException e) {
    } catch (Exception e) {
      fail("wrong exception caught");
    }
  }

  public void testToString() {
    checkValue(new QuadraticSurd(1, 2),  "1 / 2");
    checkValue(new QuadraticSurd(-1, 2), "-1 / 2");
    checkValue(new QuadraticSurd(1, -2), "-1 / 2");
    checkValue(new QuadraticSurd(-1, -2), "1 / 2");
    checkValue(new QuadraticSurd(0, 500), "0");
    checkValue(new QuadraticSurd(-12), "-12");
    checkValue(new QuadraticSurd(12), "12");
  }

  public void testSimplification() {
    checkValue(new QuadraticSurd(2, 4), "1 / 2");
    checkValue(new QuadraticSurd(307692, 999999), "4 / 13");
    checkValue(new QuadraticSurd(999999, 307692), "13 / 4");
  }

  public void testInvert() {

    QuadraticSurd f = new QuadraticSurd(2, 4);
    f.invertSelf();
    checkValue(f, "2");
    f.invertSelf();
    checkValue(f, "1 / 2");

    f = new QuadraticSurd(120);
    f.invertSelf();
    checkValue(f, "1 / 120");

    f = new QuadraticSurd(0, 4);
    try {
      f.invertSelf();
      fail("an exception should have been thrown");
    } catch (ArithmeticException e) {
    } catch (Exception e) {
      fail("wrong exception caught");
    }

    f = new QuadraticSurd(307692, 999999);
    QuadraticSurd fInverse = QuadraticSurd.invert(f);
    checkValue(fInverse, "13 / 4");
    checkValue(f, "4 / 13");

    f = new QuadraticSurd(1, 2, 3, 4);
    checkValue(QuadraticSurd.invert(f), "(-4 + 8 sqrt(3)) / 11");

  }

  public void testAddition() {

    QuadraticSurd f1 = new QuadraticSurd(4, 6);
    f1.addToSelf(f1);
    checkValue(f1, "4 / 3");

    checkValue(QuadraticSurd.add(new QuadraticSurd(17, 3),
                                 new QuadraticSurd(-17, 3)),
               "0");
    checkValue(QuadraticSurd.add(new QuadraticSurd(2, 3),
                                 new QuadraticSurd(3, 4)),
               "17 / 12");
    checkValue(QuadraticSurd.add(new QuadraticSurd(1, 6),
                                 new QuadraticSurd(2, 6)),
               "1 / 2");
    checkValue(QuadraticSurd.add(new QuadraticSurd(4, 5),
                                 new QuadraticSurd(-3, 4)),
               "1 / 20");
    checkValue(QuadraticSurd.add(new QuadraticSurd(-3, 4),
                                 new QuadraticSurd(4, 5)),
               "1 / 20");

  }

  public void testSubtraction() {

    QuadraticSurd f1 = new QuadraticSurd(4, 6);
    f1.subtractFromSelf(f1);
    checkValue(f1, "0");

    checkValue(QuadraticSurd.subtract(new QuadraticSurd(7, 3),
                                      new QuadraticSurd(-7, 3)),
               "14 / 3");

    checkValue(QuadraticSurd.subtract(new QuadraticSurd(3, 4),
                                      new QuadraticSurd(2, 3)),
               "1 / 12");
    checkValue(QuadraticSurd.subtract(new QuadraticSurd(3, 4),
                                      new QuadraticSurd(-2, 3)),
               "17 / 12");
    checkValue(QuadraticSurd.subtract(new QuadraticSurd(-3, 4),
                                      new QuadraticSurd(2, 3)),
               "-17 / 12");
    checkValue(QuadraticSurd.subtract(new QuadraticSurd(-3, 4),
                                      new QuadraticSurd(-2, 3)),
               "-1 / 12");

    checkValue(QuadraticSurd.subtract(new QuadraticSurd(2, 3),
                                      new QuadraticSurd(3, 4)),
               "-1 / 12");
    checkValue(QuadraticSurd.subtract(new QuadraticSurd(-2, 3),
                                      new QuadraticSurd(3, 4)),
               "-17 / 12");
    checkValue(QuadraticSurd.subtract(new QuadraticSurd(2, 3),
                                      new QuadraticSurd(-3, 4)),
               "17 / 12");
    checkValue(QuadraticSurd.subtract(new QuadraticSurd(-2, 3),
                                      new QuadraticSurd(-3, 4)),
               "1 / 12");

    checkValue(QuadraticSurd.subtract(new QuadraticSurd(1, 6),
                                      new QuadraticSurd(2, 6)),
               "-1 / 6");
    checkValue(QuadraticSurd.subtract(new QuadraticSurd(1, 2),
                                      new QuadraticSurd(1, 6)),
               "1 / 3");

  }

  public void testMultiplication() {

    QuadraticSurd f = new QuadraticSurd(2, 3);
    f.multiplySelf(new QuadraticSurd(9,4));
    checkValue(f, "3 / 2");

    checkValue(QuadraticSurd.multiply(new QuadraticSurd(1, 2),
                                      new QuadraticSurd(0)),
               "0");
    checkValue(QuadraticSurd.multiply(new QuadraticSurd(4, 15),
                                      new QuadraticSurd(-5, 2)),
               "-2 / 3");
    checkValue(QuadraticSurd.multiply(new QuadraticSurd(-4, 15),
                                      new QuadraticSurd(5, 2)),
               "-2 / 3");
    checkValue(QuadraticSurd.multiply(new QuadraticSurd(4, 15),
                                      new QuadraticSurd(5, 2)),
               "2 / 3");
    checkValue(QuadraticSurd.multiply(new QuadraticSurd(-4, 15),
                                      new QuadraticSurd(-5, 2)),
               "2 / 3");


    QuadraticSurd qs1 = new QuadraticSurd(1, 2, 5, 3);
    QuadraticSurd qs2 = new QuadraticSurd(3, -1, 5, 2);
    checkValue(QuadraticSurd.multiply(qs1, qs2), "(-7 + 5 sqrt(5)) / 6");

    qs1 = new QuadraticSurd(2, -3, 11, 5);
    qs2 = new QuadraticSurd(2,  3, 11, 19);
    assertTrue(QuadraticSurd.negate(QuadraticSurd.multiply(qs1, qs2)).isOne());

  }

  public void testDivision() {

    QuadraticSurd f = new QuadraticSurd(2, 3);
    f.divideSelf(new QuadraticSurd(4,9));
    checkValue(f, "3 / 2");

    try {
      QuadraticSurd.divide(new QuadraticSurd(1, 2),
                           new QuadraticSurd(0));
      fail("an exception should have been thrown");
    } catch (ArithmeticException e) {
    } catch (Exception e) {
      fail("wrong exception caught");
    }

    checkValue(QuadraticSurd.divide(new QuadraticSurd(4, 15),
                                    new QuadraticSurd(-2, 5)),
               "-2 / 3");
    checkValue(QuadraticSurd.divide(new QuadraticSurd(-4, 15),
                                    new QuadraticSurd(2, 5)),
               "-2 / 3");
    checkValue(QuadraticSurd.divide(new QuadraticSurd(4, 15),
                                    new QuadraticSurd(2, 5)),
               "2 / 3");
    checkValue(QuadraticSurd.divide(new QuadraticSurd(-4, 15),
                                    new QuadraticSurd(-2, 5)),
               "2 / 3");
    checkValue(QuadraticSurd.divide(new QuadraticSurd(2, 5, 2, 3),
                                    new QuadraticSurd(1, 4, 2, 2)),
               "(76 + 6 sqrt(2)) / 93");

  }

  public void testDormandPrince() {
    QuadraticSurd r = new QuadraticSurd(0);
    r.addToSelf(new QuadraticSurd(104257l, 1920240l));
    r.addToSelf(new QuadraticSurd(3399327l, 763840l));
    r.addToSelf(new QuadraticSurd(66578432l, 35198415l));
    r.addToSelf(new QuadraticSurd(-1674902723l, 288716400l));
    r.addToSelf(new QuadraticSurd(54980371265625l, 176692375811392l));
    r.addToSelf(new QuadraticSurd(-734375l, 4826304l));
    r.addToSelf(new QuadraticSurd(171414593l, 851261400l));
    r.addToSelf(new QuadraticSurd(137909l, 3084480l));
    checkValue(r, "1");
  }

  public void testDifferentFields() {
    try {
      QuadraticSurd.add(new QuadraticSurd(1, 2, 3, 4),
                        new QuadraticSurd(4, 3, 2, 1));
      fail("an exception should have been thrown");
    } catch (ArithmeticException e) {
    } catch (Exception e) {
      fail("wrong exception caught");
    }
  }

  public void testApproximation() {
    BigDecimal minusPi = new BigDecimal(-3.14159265358979323846264338328);
    checkValue(new QuadraticSurd(minusPi, new BigDecimal(0.2)),    "-3");
    checkValue(new QuadraticSurd(minusPi, new BigDecimal(1.0e-2)), "-22 / 7");
    checkValue(new QuadraticSurd(minusPi, new BigDecimal(1.0e-4)), "-333 / 106");
    checkValue(new QuadraticSurd(minusPi, new BigDecimal(1.0e-6)), "-355 / 113");
    checkValue(new QuadraticSurd(minusPi, new BigDecimal(1.0e-9)), "-103993 / 33102");
  }

  private void checkValue(QuadraticSurd f, String reference) {
    assertTrue(f.toString().equals(reference));
  }

  public static Test suite() {
    return new TestSuite(QuadraticSurdTest.class);
  }

}
