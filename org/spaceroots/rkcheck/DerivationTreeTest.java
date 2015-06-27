package org.spaceroots.rkcheck;

import junit.framework.*;

public class DerivationTreeTest
  extends TestCase {

  public DerivationTreeTest(String name) {
    super(name);
  }

  public void testEmptyTree() {
    DerivationTree dt = new DerivationTree();
    assertTrue(dt.getOrder() == 1);
    assertTrue(dt.getFactorial() == 1l);
    assertTrue(dt.getAlpha().toString().equals("1"));
    assertTrue(dt.toString().equals("f"));
  }

  public void testOrder8Tree() {
    DerivationTree f0       = new DerivationTree();
    DerivationTree[] array1 = { f0 };
    DerivationTree f1       = new DerivationTree(array1);
    DerivationTree[] array2 = { f1, f1, f0 };
    DerivationTree f3       = new DerivationTree(array2);
    DerivationTree[] array3 = { f3, f0 };
    DerivationTree dt       = new DerivationTree(array3);
    assertTrue(dt.getOrder() == 8);
    assertTrue(dt.getFactorial() == 192l);
    assertTrue(dt.getAlpha().toString().equals("1 / 2"));
    assertTrue(dt.toString().equals("f''(f, f'''(f, f'(f), f'(f)))"));
  }

  public static Test suite() {
    return new TestSuite(DerivationTreeTest.class);
  }

}
