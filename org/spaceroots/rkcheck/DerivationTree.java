package org.spaceroots.rkcheck;

import java.util.Vector;
import java.util.Iterator;
import java.util.Collections;

/**
 * This class implements rooted trees for determining order
 * conditions in Runge-Kutta integrators.

 * <p>The determination of order conditions for Runge-Kutta
 * integrators involves identifying all the terms of two
 * developments:
 * <ul>
 *   <li>the Taylor serie of the continuous flow (the theoretical
 *   solution)</li>
 *   <li>the Taylor serie of the discrete flow (the Runge-Kutta
 *   process)</li>
 * </ul></p>

 * <p>According to a theorem from Butcher (1963), this identification
 * can be done using rooted trees representing the multilinear
 * derivations of the Runge-Kutta process. This class implements this
 * computation process for one term, as explained in the paper <a
 * href="http://wwwzenger.informatik.tu-muenchen.de/selcuk/sjam012101.html">Runge-Kutta
 * Methods, Trees, and Maple</a> by Folkmar Bornemann, Center of
 * Mathematical Sciences, Munich University of Technology, February 9,
 * 2001.</p>

 * <p>The nodes in such trees are either leaf nodes without any
 * subtree or nodes with subtrees. Leaf nodes represent the basic
 * function f by itself. Nodes with subtrees represent an elementary
 * differential written for example f'''(f'(f), f'(f), f), which
 * means we compute the third order derivative according to three
 * arguments which themselves represent other elementary
 * differentials.</p>

 * <p>Only the derivation structure is considered, there is no
 * reference to the base function f which should be differentiated
 * according to this structure (it remains an unknown througout the
 * order conditions determination process). This means the nodes do
 * not contain data per se, so these trees should not be confused with
 * trees used as containers (such as AVL trees or red-black
 * trees).</p>

 * @version $Id: DerivationTree.java,v 1.4 2003/11/07 21:51:27 luc Exp $
 * @author L. Maisonobe

 */

public class DerivationTree
  implements Comparable {

  /**
   * Simple constructor.
   * Build a derivation tree of order 0 (i.e. no derivation at all).
   */
  public DerivationTree() {
    subtrees = null;
  }

  /**
   * Copy constructor.
   * Build a derivation tree from another one. This is a deep copy, no
   * data is shared between the original tree and the instance.
   * @param tree derivation tree to copy
   */
  public DerivationTree(DerivationTree tree) {
    if (tree.isLeaf()) {
      subtrees = null;
    } else {
      subtrees = new Vector(tree.subtrees.size());
      for (Iterator iter = tree.subtrees.iterator(); iter.hasNext();) {
        subtrees.add(new DerivationTree((DerivationTree) iter.next()));
      }
    }
  }

  /**
   * Build a derivation tree from an array of sub-trees.
   * @param trees array of sub-trees (null elements will be skipped)
   */
  public DerivationTree(DerivationTree[] trees) {
    if (trees == null) {
      subtrees = null;
    } else {
      subtrees = new Vector(trees.length);
      for (int i = 0; i < trees.length; ++i) {
        if (trees[i] != null) {
          subtrees.add(new DerivationTree(trees[i]));
        }
      }
      Collections.sort(subtrees);
    }
  }

  /** List one order upper trees.
   * This method lists all the derivation trees which have an order
   * exactly one unit larger than the order of this tree.
   * @return a vector of derivation trees
   */
  public Vector listUpperTrees() {
    Vector list = new Vector();

    if (isLeaf()) {

      DerivationTree[] array = new DerivationTree[1];
      array[0] = new DerivationTree();
      list.add(new DerivationTree(array));

    } else {

      // first upper tree: prepend f to the current subtrees
      DerivationTree[] array = new DerivationTree[subtrees.size() + 1];
      array[0] = new DerivationTree();
      for (int i = 0; i < subtrees.size(); ++i) {
        array[i+1] = (DerivationTree) subtrees.elementAt(i);
      }
      list.add(new DerivationTree(array));

      // all other upper trees: increase the order of any of the subtrees
      array = (DerivationTree[]) subtrees.toArray(array);
      for (int i = 0; i < subtrees.size(); ++i) {

        DerivationTree dt = array[i];

        Vector upperSub = dt.listUpperTrees();
        for (Iterator iter = upperSub.iterator(); iter.hasNext();) {
          array[i] = (DerivationTree) iter.next();
          list.add(new DerivationTree(array));
        }

        array[i] = dt;

      }

    }

    return list;

  }

  /** Check if a tree is a leaf.
   * A tree is a leaf if it is an order 1 tree (i.e. it represents the
   * base function f itself, not any of its derivatives).
   * @return true if the tree is a leaf
   */
  public boolean isLeaf() {
    return subtrees == null;
  }

  /** Check if the instance is equal to another tree.

   * Two different trees are considered equals if the same derivations
   * are performed in a similar structure. As an example f'''(f'(f),
   * f'(f), f) is the same as f'''(f'(f), f, f'(f)) due to the
   * multilinearity.

   * @param o object against which we want to check equality
   * @return true if o is a derivation tree having the same structure
   * as the instance
   */
  public boolean equals(Object o) {
    if (! (o instanceof DerivationTree)) {
      return false;
    }

    if (subtrees == null) {
      return ((DerivationTree) o).subtrees == null;
    } else if (((DerivationTree) o).subtrees == null) {
      return false;
    }

    // the following method works only because all subtrees are sorted
    // (this is the reason why we sort them).
    Iterator iter1 = subtrees.iterator();
    Iterator iter2 = ((DerivationTree) o).subtrees.iterator();
    while (iter1.hasNext()) {
      if (! iter2.hasNext()) {
        return false;
      }
      if (! ((DerivationTree) iter1.next()).equals(iter2.next())) {
        return false;
      }
    }
    if (iter2.hasNext()) {
      return false;
    }

    return true;

  }

  /** Returns the hash code value for this tree.
   * @return the hash code value for this tree.
   */
  public int hashCode() {
    if (isLeaf()) {
      return 1;
    } else {
      int n = 0;
      for (Iterator iter = subtrees.iterator(); iter.hasNext();) {
        n = (n << 4) ^ ((DerivationTree) iter.next()).hashCode();
      }
      return n;
    }
  }

  /** Compares this tree with the specified object for order.
   * Comparison is done with respect to the order first, then to the
   * number of subtrees, then using lexicographic order of subtrees.
   * @param o the Object to be compared.
   * @return a negative integer, zero, or a positive integer as this
   * object is less than, equal to, or greater than the specified
   * object.
   * @exception ClassCastException if the specified object's type
   * prevents it from being compared to this Object.
   */
  public int compareTo(Object o) {

    DerivationTree dt = (DerivationTree) o;

    // first use the order
    int order1 = getOrder();
    int order2 = dt.getOrder();
    if (order1 != order2) {
      return order1 - order2;
    }

    if (isLeaf()) {
      return 0;
    }

    // second use the number of subtrees
    int sub1 = subtrees.size();
    int sub2 = dt.subtrees.size();
    if (sub1 != sub2) {
      return sub1 - sub2;
    }

    // last use the subtrees
    for (int i = 0; i < sub1; ++i) {
      DerivationTree dt1 = (DerivationTree) subtrees.elementAt(i);
      DerivationTree dt2 = (DerivationTree) dt.subtrees.elementAt(i);
      int result = dt1.compareTo(dt2);
      if (result != 0) {
        return result;
      }
    }

    return 0;

  }

  /** Get the depth of the tree.
  * The depth is recursively computed as one plus the maximum
  * of the depths of all subtrees. The depth of a leaf tree is 0.
  * @return order of the tree
  */
  public int getDepth() {
    if (isLeaf()) {
      return 0;
    } else {
      int maxDepth = 0;
      for (Iterator iter = subtrees.iterator(); iter.hasNext();) {
        maxDepth = Math.max(maxDepth,
                            ((DerivationTree) iter.next()).getDepth());
      }
      return maxDepth + 1;
    }
  }

  /** Get the order of the tree.
  * The order of the tree is the parameter #beta in Folkmar
  * Bornemann's paper. It is recursively computed as one plus the sum
  * of the orders of all subtrees. The order of a leaf tree is 1.
  * @return order of the tree
  */
  public int getOrder() {
    if (isLeaf()) {
      return 1;
    } else {
      int order = 1;
      for (Iterator iter = subtrees.iterator(); iter.hasNext();) {
        order += ((DerivationTree) iter.next()).getOrder();
      }
      return order;
    }
  }

  /** Get the factorial of the tree.
  * The factorial of the tree is the parameter beta! in Folkmar
  * Bornemann's paper. It is recursively computed as the order of the
  * tree times the factorial of all subtrees. The factorial of a leaf
  * tree is 1.
  * @return factorial of the tree
  */
  public long getFactorial() {
    if (isLeaf()) {
      return 1l;
    } else {
      long factorial = getOrder();
      for (Iterator iter = subtrees.iterator(); iter.hasNext();) {
        factorial *= ((DerivationTree) iter.next()).getFactorial();
      }
      return factorial;
    }
  }

  /** Get the alpha factor of the tree.
  * The alpha factor of the tree is the parameter alpha_beta in
  * Folkmar Bornemann's paper (note that it is not used for the
  * computation of order conditions, it has been added in the paper
  * and in this class only for completeness). It is recursively
  * computed as delta_beta/n! times the alpha factor of all
  * subtrees. The alpha factor of a leaf tree is 1. Quoting the paper
  * of Folkmar Bornemann, <i>by delta_beta we denote the number of
  * different ordered tuples (beta_1, ..., beta_n) which correspond to
  * the same unordered list beta= [beta_1, ... beta_n]</i>.
  * @return alpha factor of the tree
  */
  public QuadraticSurd getAlpha() {
    if (isLeaf()) {
      return new QuadraticSurd(1);
    } else {
      QuadraticSurd alpha = deltaOnFact();
      for (Iterator iter = subtrees.iterator(); iter.hasNext();) {
        alpha.multiplySelf(((DerivationTree) iter.next()).getAlpha());
      }
      return alpha;
    }
  }

  /** Compute the residual on an order condition
   * @param c time steps array
   * @param a internal weights array
   * @param b estimation weights array
   * @return order condition residual (should be null if condition is
   * fulfilled)
   */
  public QuadraticSurd orderConditionResidual(QuadraticSurd[] c,
                                              QuadraticSurd[][] a,
                                              QuadraticSurd[] b) {

    // left hand side of the order condition
    QuadraticSurd left = new QuadraticSurd(0);
    QuadraticSurd[] table = contribution(c, a);
    for (int i = 0; i < b.length; ++i) {
      left.addToSelf(QuadraticSurd.multiply(b[i], table[i]));
    }

    // right hand side of the order condition
    QuadraticSurd right = new QuadraticSurd(1l, getFactorial());

    // check the condition
    return QuadraticSurd.subtract(left, right);

  }

  /** Get the contribution of this tree to the order condition of any
   * parent tree.
   * @param c time steps array
   * @param a internal weights array
   * @return contribution of this tree
   */
  private QuadraticSurd[] contribution(QuadraticSurd[] c,
                                       QuadraticSurd[][] a) {

    QuadraticSurd[] table = new QuadraticSurd[c.length];
    for (int k = 0; k < table.length; ++k) {
      table[k] = new QuadraticSurd(1);
    }

    if (isLeaf()) {
      return table;
    }

    for (int i = 0; i < subtrees.size(); ++i) {
      DerivationTree tree = (DerivationTree) subtrees.elementAt(i);
      QuadraticSurd[] sum = new QuadraticSurd[c.length];
      int exp = 1;
      while (((i+1) < subtrees.size())
             && tree.equals(subtrees.elementAt(i+1))) {
        ++exp;
        ++i;
      }

      if (tree.isLeaf()) {
        for (int k = 0; k < sum.length; ++k) {
          sum[k] = c[k];
        }
      } else {
        QuadraticSurd[] sub = tree.contribution(c, a);
        for (int k = 0; k < sum.length; ++k) {
          sum[k] = new QuadraticSurd(0);
          for (int j = 0; j < k; ++j) {
            sum[k].addToSelf(QuadraticSurd.multiply(a[k][j], sub[j]));
          }
        }
      }

      for (int j = 0; j < exp; ++j) {
        for (int k = 0; k < sum.length; ++k) {
          table[k].multiplySelf(sum[k]);
        }
      }

    }

    return table;

  }

  /** Get a TeX string representation of the order condition of this
   * derivation tree.
   * @param interpolatorCondition indicator if the condition should be
   * displayed for an interpolator (using a theta parameter between 0
   * and 1) or for an integrator (with the constant 1)
   * @return a string representation (using TeX) of the order
   * condition of the instance
   */
  public String orderConditionAsTeXString(boolean interpolatorCondition) {
    int order = getOrder();
    String numerator = ((interpolatorCondition && (order > 1))
                        ? ("\\theta"
                           + ((order > 2) ? ("^{" + (order-1) + "}") : ""))
                        : ("1"));
    return "\\sum_{i=" + Math.max(1, getDepth()) + "}^{i=s}\\left(b_{i} "
      + contributionAsTeXString(0)
      + "\\right) ="
      + ((getFactorial() == 1l)
         ? numerator
         : (" \\frac{" + numerator + "}{" + getFactorial() + "}"));
  }

  /** Get the contribution of this tree to the order condition of any
   * parent tree.
   * @return contribution of this tree as a part of a TeX equation
   */
  private String contributionAsTeXString(int index) {

    if (isLeaf()) {
      return "";
    }

    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < subtrees.size(); ++i) {
      DerivationTree tree = (DerivationTree) subtrees.elementAt(i);
      int exp = 1;
      while (((i+1) < subtrees.size())
             && tree.equals(subtrees.elementAt(i+1))) {
        ++exp;
        ++i;
      }
      if (tree.isLeaf()) {
        sb.append("c_{");
        sb.append(labels[index]);
        sb.append("}");
      } else {
        if (exp != 1) {
          sb.append("\\left(");
        }
        sb.append("\\sum_{");
        sb.append(labels[index+1]);
        sb.append("=");
        sb.append(Math.max(1, tree.getDepth()));
        sb.append("}^{");
        sb.append(labels[index+1]);
        sb.append("=");
        sb.append(labels[index]);
        sb.append("-1}{\\left(a_{");
        sb.append(labels[index]);
        sb.append(",");
        sb.append(labels[index+1]);
        sb.append("} ");
        sb.append(tree.contributionAsTeXString(index+1));
        sb.append(" \\right)}");
        if (exp != 1) {
          sb.append(" \\right)");
        }
      }
      if (exp != 1) {
        sb.append("^{");
        sb.append(Integer.toString(exp));
        sb.append("}");
      }
    }

    return sb.toString();

  }

  /** Get a Maxima string representation of the order condition of this
   * derivation tree.
   * @param interpolatorCondition indicator if the condition should be
   * displayed for an interpolator (using a theta parameter between 0
   * and 1) or for an integrator (with the constant 1)
   * @return a string representation (using Maxima) of the order
   * condition of the instance
   */
  public String orderConditionAsMaximaString(boolean interpolatorCondition) {
    int order = getOrder();
    String numerator = ((interpolatorCondition && (order > 1))
                        ? ("theta"
                           + ((order > 2) ? ("^" + (order-1)) : ""))
                        : ("1"));
    return "sum(b[i]"
      + contributionAsMaximaString(0)
      + ",i," + Math.max(1, getDepth()) + ",s) = "
      + ((getFactorial() == 1l)
         ? numerator
         : (numerator + " / " + getFactorial()));
  }

  /** Get the contribution of this tree to the order condition of any
   * parent tree.
   * @return contribution of this tree as a part of a Maxima equation
   */
  private String contributionAsMaximaString(int index) {

    if (isLeaf()) {
      return "";
    }

    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < subtrees.size(); ++i) {
      DerivationTree tree = (DerivationTree) subtrees.elementAt(i);
      int exp = 1;
      while (((i+1) < subtrees.size())
             && tree.equals(subtrees.elementAt(i+1))) {
        ++exp;
        ++i;
      }
      sb.append(" * ");
      if (tree.isLeaf()) {
        sb.append("t[");
        sb.append(labels[index]);
        sb.append("]");
      } else {
        if (exp != 1) {
          sb.append("(");
        }
        sb.append("sum(");
        sb.append("(a[");
        sb.append(labels[index]);
        sb.append(",");
        sb.append(labels[index+1]);
        sb.append("]");
        sb.append(tree.contributionAsMaximaString(index+1));
        sb.append("),");
        sb.append(labels[index+1]);
        sb.append(",");
        sb.append(Math.max(1, tree.getDepth()));
        sb.append(",");
        sb.append(labels[index]);
        sb.append("-1)");
        if (exp != 1) {
          sb.append(")");
        }
      }
      if (exp != 1) {
        sb.append("^");
        sb.append(Integer.toString(exp));
      }
    }

    return sb.toString();

  }

  /** Get a string representation of this tree.
   * The string representation is a parenthesized expression including
   * the derivation order at the tree level with primes charaters and
   * the arguments like this : f'''(f, f''(f, f'(f)), f''(f, f'(f)))
   * @return a string representation of the tree.
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("f");

    if (! isLeaf()) {

      for (int i = 0; i < subtrees.size(); ++i) {
        sb.append('\'');
      }

      sb.append('(');

      for (Iterator iter = subtrees.iterator(); iter.hasNext();) {
        sb.append(((DerivationTree)iter.next()).toString());
        if (iter.hasNext()) {
          sb.append(", ");
        }
      }

      sb.append(")");

    }

    return sb.toString();

  }

  /** Compute the delta_beta/n! factor of the instance.
   * The delta_beta/n! factor is used in the computation of the alpha
   * factor by the {@link #getAlpha getAlpha} method.
   * @return delta_beta/n! factor
   */
  private QuadraticSurd deltaOnFact() {

    // set up an array to count the repetition of the same subtree in
    // the instance. The array holds a count number of each
    // subtree. The count is done at the index of the first
    // occurrence only. So if all subtrees are different the array
    // will be filled with 1. If on the other hand the 2nd and 4th
    // entry are the same as the 1st, the array will be : [3, 0, 1, 0],
    // which means there are three occurrences of the first subtree and
    // one occurrence of the third subtree, for a total a 4 subtrees.
    int[] countSame = new int[subtrees.size()];
    for (int i = 0; i < countSame.length; ++i) {
      countSame[i] = 0;
    }

    // count all subtrees
    for (int i = 0; i < subtrees.size(); ++i) {
      DerivationTree dt = (DerivationTree) subtrees.elementAt(i);
      boolean stillLooking = true;

      // look if the current subtree is the same as an already encountered one
      for (int j = 0; stillLooking && (j < i); ++j) {
        if ((countSame[j] > 0)
            && (dt.equals((DerivationTree) subtrees.elementAt(j)))) {
          // this subtree as already been seen,
          // increment the count on the first occurrence
          countSame[j] += 1;
          stillLooking = false;
        }
      }

      if (stillLooking) {
        // this subtree has never been seen before,
        // set up a new count for it at index i (first occurrence)
        countSame[i] = 1;
      }

    }

    // the number of different permutations is
    // delta_beta = n! / (p1! p2! ... pq!)
    // where the pi denote the size of groups of equal subtrees
    // since we want delta_beta/n!, we can simplify by n! and consider
    // only the pi terms
    QuadraticSurd q = new QuadraticSurd(1);
    for (int i = 0; i < countSame.length; ++i) {
      if (countSame[i] > 1) {
        q.divideSelf(fact(countSame[i]));
      }
    }

    return q;

  }

  /** Compute the factorial of an integer
   * @param n number for which we want the factorial
   * @return factorial of n
   */
  private static long fact(int n) {
    long f = 1l;
    while (n > 1) {
      f *= n--;
    }
    return f;
  }

  /** Table of the subtrees of the instance. */
  private Vector subtrees;

  /** labels to use in the summations. */
  private static final char[] labels = { 'i', 'j', 'k', 'l', 'm', 'p', 'q',
                                         'r', 'u', 'v', 'w', 'e', 'f', 'g' };

}
