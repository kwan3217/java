package org.spaceroots.rkcheck;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
  public static Test suite() { 

    TestSuite suite = new TestSuite("org.spaceroots.rkcheck"); 

    suite.addTest(QuadraticSurdTest.suite());
    suite.addTest(DerivationTreeTest.suite());
    suite.addTest(DerivationTreesSetGeneratorTest.suite());

    return suite; 

  }
}
