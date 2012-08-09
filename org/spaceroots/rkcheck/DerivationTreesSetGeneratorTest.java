package org.spaceroots.rkcheck;

import junit.framework.*;

public class DerivationTreesSetGeneratorTest
  extends TestCase {

  public DerivationTreesSetGeneratorTest(String name) {
    super(name);
  }

  public void testException() {
    try {
      DerivationTreesSetGenerator generator = new DerivationTreesSetGenerator();
      generator.getTreesSet(0);
      fail("an exception should have been thrown");
    } catch (IllegalArgumentException e) {
    } catch (Exception e) {
      fail("wrong exception caught");
    }
  }

  public void testNumber() {
    DerivationTreesSetGenerator generator = new DerivationTreesSetGenerator();
    assertTrue(generator.getTreesSet( 1).size() ==   1);
    assertTrue(generator.getTreesSet( 2).size() ==   1);
    assertTrue(generator.getTreesSet( 3).size() ==   2);
    assertTrue(generator.getTreesSet( 4).size() ==   4);
    assertTrue(generator.getTreesSet( 5).size() ==   9);
    assertTrue(generator.getTreesSet( 6).size() ==  20);
    assertTrue(generator.getTreesSet( 7).size() ==  48);
    assertTrue(generator.getTreesSet( 8).size() == 115);
    assertTrue(generator.getTreesSet( 9).size() == 286);
    assertTrue(generator.getTreesSet(10).size() == 719);
  }

  public static Test suite() {
    return new TestSuite(DerivationTreesSetGeneratorTest.class);
  }

}
