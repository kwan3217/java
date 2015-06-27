package org.spaceroots.rkcheck;

import java.util.Vector;
import java.util.TreeSet;
import java.util.Iterator;

/**
 * This class generates all trees set for any order.
 * @see DerivationTree
 * @version $Id: DerivationTreesSetGenerator.java,v 1.2 2002/08/18 13:42:25 luc Exp $
 * @author L. Maisonobe
 */

public class DerivationTreesSetGenerator {

  /** Build an empty generator.
   */
  public DerivationTreesSetGenerator() {
    all = new Vector();
  }

  /** Build the derivation trees up to a specified order.
   * @param maxOrder maximal order for which to build the trees
   */
  private void buildTrees(int maxOrder) {

    TreeSet oldTrees = null;
    if (all.isEmpty()) {
      // first call, we have no trees set for now
      // begin with one set containing one basic tree
      oldTrees = new TreeSet();
      oldTrees.add(new DerivationTree());
      all.add (oldTrees);
    } else {
      // get the last computed set
      oldTrees = (TreeSet) all.elementAt(all.size() - 1);
    }

    // add new sets as needed
    for (int order = all.size(); order < maxOrder; ++order) {

      TreeSet newTrees = new TreeSet();
      for (Iterator iter = oldTrees.iterator(); iter.hasNext();) {
        DerivationTree dt = (DerivationTree) iter.next();
        newTrees.addAll(dt.listUpperTrees());
      }

      oldTrees = newTrees;
      all.add (oldTrees);

    }
    
  }

  /** Get the trees set for the specified order.
   * @param order order of the desired trees (must be strictly positive)
   * @return the set of trees of the specified order, this set
   * contains {@link DerivationTree} elements
   */
  public TreeSet getTreesSet(int order) {
    if (order < 1) {
      throw new IllegalArgumentException("invalid order: " + order
                                         + " (should be strictly positive)");
    }
    if (all.size() < order) {
      buildTrees(order);
    }
    return (TreeSet) all.elementAt(order - 1);
  }

  /** Table of the derivation trees sets. */
  Vector all;

}
