package org.spaceroots.rkcheck;

import java.util.TreeSet;
import java.util.Iterator;

/**
 * This application computes the order conditions of Runge-Kutta
 * methods.
 * @version $Id: ComputeOrderConditions.java,v 1.4 2004/05/23 19:41:19 luc Exp $
 * @author L. Maisonobe

 */

public class ComputeOrderConditions {

  /** Entry point of the application.
   * @param args application arguments
   */
  public static void main(String[] args) {

    try {

      int     maxOrder              = -1;
      boolean outputTeXEquations    = false;
      boolean outputMaximaEquations = false;
      boolean interpolatorCondition = false;
      for (int i = 0; i < args.length; ++i) {
        if (args[i].equals("-h")) {
          usage(0);
        } else if (args[i].equals("-t")) {
          outputTeXEquations    = true;
          outputMaximaEquations = false;
        } else if (args[i].equals("-m")) {
          outputTeXEquations    = false;
          outputMaximaEquations = true;
        } else if (args[i].equals("-i")) {
          interpolatorCondition = true;
        } else {
          maxOrder = Integer.parseInt(args[i]);
        }
      }

      if (maxOrder < 0) {
        usage(1);
      }

      DerivationTreesSetGenerator generator = new DerivationTreesSetGenerator();

      if (outputTeXEquations) {
        System.out.println("\\documentclass[a4paper,10pt,leqno]{article}");
        System.out.println("\\usepackage{amsmath}");
        System.out.println("\\numberwithin{equation}{section}");
        System.out.println("\\addtolength{\\oddsidemargin}{-70pt}");
        System.out.println("\\addtolength{\\marginparwidth}{-60pt}");
        System.out.println("\\addtolength{\\textwidth}{160pt}");
        System.out.println("\\begin{document}");
        System.out.println("\\tableofcontents");
        System.out.println("\\section*{homogeneity conditions ($s-1$ equations)}");
        System.out.println("\\begin{equation*}");
        System.out.println("\\sum_{j=1}^{j=s}a_{i,j} = c_{i}");
        System.out.println("\\end{equation*}");
        for (int i = 1; i <= maxOrder; ++i) {
          TreeSet set = generator.getTreesSet(i);
          System.out.println();
          System.out.print("\\section{conditions for order " + i);
          if (set.size() == 1) {
            System.out.print(" (1 equation)");
          } else {
            System.out.print(" (" + set.size() + " equations)");
          }
          System.out.println("}");
          for (Iterator iter = set.iterator(); iter.hasNext();) {
            System.out.println("\\begin{equation}");
            DerivationTree dt = (DerivationTree) iter.next();
            System.out.println(dt.orderConditionAsTeXString(interpolatorCondition));
            System.out.println("\\end{equation}");
          }
        }
        System.out.println("\\end{document}");
      } else if (outputMaximaEquations) {
        for (int i = 1; i <= maxOrder; ++i) {
          System.out.println();
          System.out.println("/* trees for order " + i + " */");
          TreeSet set = generator.getTreesSet(i);
          int j = 1;
          for (Iterator iter = set.iterator(); iter.hasNext(); ++j) {
            DerivationTree dt = (DerivationTree) iter.next();
            System.out.println("condEqn[" + i + "," + j + "] : "
                               + dt.orderConditionAsMaximaString(interpolatorCondition)
                               + ";");
          }
        }
      } else {
        for (int i = 1; i <= maxOrder; ++i) {
          System.out.println();
          System.out.println("trees for order " + i);
          TreeSet set = generator.getTreesSet(i);
          for (Iterator iter = set.iterator(); iter.hasNext();) {
            DerivationTree dt = (DerivationTree) iter.next();
            System.out.println(" " + dt);
          }
        }
      }

    } catch (NumberFormatException e) {
      System.err.println("unable to parse number " + e.getMessage());
      System.exit(1);
    }
  }

  /** Display usage and exit.
   * @param exit exit code
   */
  private static void usage(int exit) {
    System.out.println("usage: java ComputeOrderConditions [-h] [-t|-m] [-i] maxOrder");
    System.exit(exit);
  }

}
