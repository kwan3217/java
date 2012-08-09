package org.spaceroots.rkcheck;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.io.IOException;

import java.util.TreeSet;
import java.util.Iterator;

/**
 * This application checks the order conditions of Runge-Kutta
 * processes.

 * <p>It reads the description of a Runge-Kutta method from an XML
 * file having a structure similar to this example:
 * <pre>
 * &lt;?xml version="1.0" encoding="ISO-8859-1" ?&gt;
 * &lt;!DOCTYPE Runge-Kutta PUBLIC "-//spaceroots.org//DTD Runge-Kutta V1.2//EN"
 *                              "http://www.spaceroots.org/Runge-Kutta.dtd" &gt;
 * 
 * &lt;Runge-Kutta name="Higham and Hall" &gt;
 * 
 *   &lt;time-steps&gt;
 *     &lt;zero/&gt;
 *     &lt;rational&gt; &lt;p&gt;2&lt;/p&gt; &lt;q&gt;9&lt;/q&gt; &lt;/rational&gt;
 *     &lt;rational&gt; &lt;p&gt;1&lt;/p&gt; &lt;q&gt;3&lt;/q&gt; &lt;/rational&gt;
 *     &lt;rational&gt; &lt;p&gt;1&lt;/p&gt; &lt;q&gt;2&lt;/q&gt; &lt;/rational&gt;
 *     &lt;rational&gt; &lt;p&gt;3&lt;/p&gt; &lt;q&gt;5&lt;/q&gt; &lt;/rational&gt;
 *     &lt;one/&gt;
 *     &lt;one/&gt;
 *   &lt;/time-steps&gt;
 * 
 *   &lt;internal-weights&gt;
 *     &lt;row&gt;
 *       &lt;!-- empty first row --&gt;
 *     &lt;/row&gt;
 *     &lt;row&gt;
 *       &lt;rational&gt; &lt;p&gt;  2&lt;/p&gt; &lt;q&gt;  9&lt;/q&gt; &lt;/rational&gt;
 *     &lt;/row&gt;
 *     &lt;row&gt;
 *       &lt;rational&gt; &lt;p&gt;  1&lt;/p&gt; &lt;q&gt; 12&lt;/q&gt; &lt;/rational&gt;
 *       &lt;rational&gt; &lt;p&gt;  1&lt;/p&gt; &lt;q&gt;  4&lt;/q&gt; &lt;/rational&gt;
 *     &lt;/row&gt;
 *     &lt;row&gt;
 *       &lt;rational&gt; &lt;p&gt;  1&lt;/p&gt; &lt;q&gt;  8&lt;/q&gt; &lt;/rational&gt;
 *       &lt;zero/&gt;
 *       &lt;rational&gt; &lt;p&gt;  3&lt;/p&gt; &lt;q&gt;  8&lt;/q&gt; &lt;/rational&gt;
 *     &lt;/row&gt;
 *     &lt;row&gt;
 *       &lt;rational&gt; &lt;p&gt; 91&lt;/p&gt; &lt;q&gt;500&lt;/q&gt; &lt;/rational&gt;
 *       &lt;rational&gt; &lt;p&gt;-27&lt;/p&gt; &lt;q&gt;100&lt;/q&gt; &lt;/rational&gt;
 *       &lt;rational&gt; &lt;p&gt; 78&lt;/p&gt; &lt;q&gt;125&lt;/q&gt; &lt;/rational&gt;
 *       &lt;rational&gt; &lt;p&gt;  8&lt;/p&gt; &lt;q&gt;125&lt;/q&gt; &lt;/rational&gt;
 *     &lt;/row&gt;
 *     &lt;row&gt;
 *       &lt;rational&gt; &lt;p&gt;-11&lt;/p&gt; &lt;q&gt; 20&lt;/q&gt; &lt;/rational&gt;
 *       &lt;rational&gt; &lt;p&gt; 27&lt;/p&gt; &lt;q&gt; 20&lt;/q&gt; &lt;/rational&gt;
 *       &lt;rational&gt; &lt;p&gt; 12&lt;/p&gt; &lt;q&gt;  5&lt;/q&gt; &lt;/rational&gt;
 *       &lt;rational&gt; &lt;p&gt;-36&lt;/p&gt; &lt;q&gt;  5&lt;/q&gt; &lt;/rational&gt;
 *       &lt;integer&gt;5&lt;/integer&gt;
 *     &lt;/row&gt;
 *     &lt;row&gt;
 *       &lt;rational&gt; &lt;p&gt;  1&lt;/p&gt; &lt;q&gt; 12&lt;/q&gt; &lt;/rational&gt;
 *       &lt;zero/&gt;
 *       &lt;rational&gt; &lt;p&gt; 27&lt;/p&gt; &lt;q&gt; 32&lt;/q&gt; &lt;/rational&gt;
 *       &lt;rational&gt; &lt;p&gt; -4&lt;/p&gt; &lt;q&gt;  3&lt;/q&gt; &lt;/rational&gt;
 *       &lt;rational&gt; &lt;p&gt;125&lt;/p&gt; &lt;q&gt; 96&lt;/q&gt; &lt;/rational&gt;
 *       &lt;rational&gt; &lt;p&gt;  5&lt;/p&gt; &lt;q&gt; 48&lt;/q&gt; &lt;/rational&gt;
 *     &lt;/row&gt;
 *   &lt;/internal-weights&gt;
 * 
 *   &lt;estimation-weights&gt;
 *     &lt;rational&gt; &lt;p&gt;  1&lt;/p&gt; &lt;q&gt; 12&lt;/q&gt; &lt;/rational&gt;
 *     &lt;zero/&gt;
 *     &lt;rational&gt; &lt;p&gt; 27&lt;/p&gt; &lt;q&gt; 32&lt;/q&gt; &lt;/rational&gt;
 *     &lt;rational&gt; &lt;p&gt; -4&lt;/p&gt; &lt;q&gt;  3&lt;/q&gt; &lt;/rational&gt;
 *     &lt;rational&gt; &lt;p&gt;125&lt;/p&gt; &lt;q&gt; 96&lt;/q&gt; &lt;/rational&gt;
 *     &lt;rational&gt; &lt;p&gt;  5&lt;/p&gt; &lt;q&gt; 48&lt;/q&gt; &lt;/rational&gt;
 *     &lt;zero/&gt;
 *   &lt;/estimation-weights&gt;
 * 
 *   &lt;error-weights&gt;
 *     &lt;rational&gt; &lt;p&gt; -1&lt;/p&gt; &lt;q&gt; 20&lt;/q&gt; &lt;/rational&gt;
 *     &lt;zero/&gt;
 *     &lt;rational&gt; &lt;p&gt; 81&lt;/p&gt; &lt;q&gt;160&lt;/q&gt; &lt;/rational&gt;
 *     &lt;rational&gt; &lt;p&gt; -6&lt;/p&gt; &lt;q&gt;  5&lt;/q&gt; &lt;/rational&gt;
 *     &lt;rational&gt; &lt;p&gt; 25&lt;/p&gt; &lt;q&gt; 32&lt;/q&gt; &lt;/rational&gt;
 *     &lt;rational&gt; &lt;p&gt;  1&lt;/p&gt; &lt;q&gt; 16&lt;/q&gt; &lt;/rational&gt;
 *     &lt;rational&gt; &lt;p&gt; -1&lt;/p&gt; &lt;q&gt; 10&lt;/q&gt; &lt;/rational&gt;
 *   &lt;/error-weights&gt;
 * 
 * &lt;/Runge-Kutta&gt;
 *</pre></p>

 * <p>This file describes the various coefficients tables of the
 * process. In addition to the elements displayed in this example,
 * coefficients can also be entered as real numbers (like
 * &lt;real&gt;-0.5&lt;/real&gt; for example) with an arbitrary
 * precision. Real numbers are converted to rational numbers during
 * parsing, using continued fractions. For simple numbers, this is
 * fine and exact arithmetic can still be used afterwards, however,
 * this is not true if too few digits are given. This feature should
 * be used with care and avoided if possible. The
 * (&lt;error-weights&gt;...&lt;/error-weights&gt;) table is optional,
 * it is used only for embedded methods with error control.</p>

 * <p>Given this description, the application checks the homogeneity
 * conditions and the order conditions, increasing the order until it
 * finds contitions that are not met anymore by the coefficients
 * arrays. It then displays the order of the method. If there is an
 * &lt;error-weights&gt;...&lt;/error-weights&gt; entry, the order of
 * the error estimation is also displayed.<p>

 * <p>Checking is done using exact arithmetic by default. However, if
 * a method involves real numbers for which not enough digits are
 * given or which are not really rational numbers, it is likely that
 * all tests will fail and the method will be declared to be of order
 * 0 ! In this case, it is advised to use a tolerance for the final
 * tests. Be aware however that for high orders, the order conditions
 * typically involve small numbers like 1/7983360, so the tolerance
 * should really be small in order to avoid too optimistic results
 * about the order of a given method. For reliable results, it is far
 * better to have an exact representation of the coefficients and to
 * use only exact arithmetic.</p>

 * @version $Id: CheckOrderConditions.java,v 1.6 2004/05/23 13:29:04 luc Exp $
 * @author L. Maisonobe

 */

public class CheckOrderConditions {

  /** Check the homogeneity conditions.
   * These conditions bind the time steps to the internal weights
   * @param c times steps table
   * @param a internal weights table
   * @param tolerance if tolerance is negative, computation is done
   * analytically and an exact match is expected, if tolerance is
   * positive, the final test is done using floating point arithmetic
   * and checked against the specified tolerance
   * @return an error string if condition are not fulfilled and an
   * empty string if they are fulfilled
   */
  private static String checkHomogeneityConditions(QuadraticSurd[]   c,
                                                   QuadraticSurd[][] a,
                                                   double tolerance) {

    StringBuffer sb = new StringBuffer();

    if (! c[0].isZero()) {
      if (Math.abs(c[0].doubleValue()) > tolerance) {
        sb.append("c[1] = ");
        if (tolerance <= 0) {
          sb.append(c[0].toString());
        } else {
          sb.append(c[0].doubleValue());
        }
        sb.append(" (should be null)");
      }
    }

    for (int i = 1; i < c.length; ++i) {
      QuadraticSurd sum = new QuadraticSurd(0);
      for (int j = 0; j < i; ++j) {
        sum.addToSelf(a[i][j]);
      }
      QuadraticSurd delta = QuadraticSurd.subtract(c[i], sum);
      if (! delta.isZero()) {
        if (Math.abs(delta.doubleValue()) > tolerance) {
          if (sb.length() != 0) {
            sb.append(System.getProperty("line.separator"));
          }
          sb.append("c[");
          sb.append(i + 1);
          sb.append("] - sum(a[");
          sb.append(i + 1);
          sb.append("][j]) = ");
          if (tolerance <= 0) {
            sb.append(delta.toString());
          } else {
            sb.append(delta.doubleValue());
          }
          sb.append(" (should be null)");
        }
      }
    }

    return sb.toString();

  }

  /** Check the order conditions
   * @param generator generator for the derivation trees
   * @param c time steps table
   * @param a internal weights table
   * @param b estimation weights table
   * @param tolerance if tolerance is negative, computation is done
   * analytically and an exact match is expected, if tolerance is
   * positive, the final test is done using floating point arithmetic
   * and checked against the specified tolerance
   * @param verbose indicator for adding the description of conditions
   * <strong>not</strong> met in the returned string
   * @return a description string for the order conditions met
   */
  private static String checkOrderConditions(DerivationTreesSetGenerator generator,
                                             QuadraticSurd[]   c,
                                             QuadraticSurd[][] a,
                                             QuadraticSurd[]   b,
                                             double            tolerance,
                                             boolean           verbose) {

    int order = 1;
    StringBuffer sb = new StringBuffer();
    while (sb.length() == 0) {

      TreeSet set = generator.getTreesSet(order);
      for (Iterator iter = set.iterator(); iter.hasNext();) {

        DerivationTree dt = (DerivationTree) iter.next();
        QuadraticSurd residual = dt.orderConditionResidual(c, a, b);

        if (! residual.isZero()) {
          if (Math.abs(residual.doubleValue()) > tolerance) {
            if (sb.length() == 0) {
              sb.append(" is of order " + (order - 1));
            }
            if (verbose) {
              sb.append(System.getProperty("line.separator"));
              sb.append("residual for tree " + dt.toString() + " = ");
              if (tolerance <= 0) {
                sb.append(residual.toString());
              } else {
                sb.append(residual.doubleValue());
              }
            }
          }
        }

      }
      ++order;

    }

    return sb.toString();

  }

  /** Entry point of the application.

   * @param args application arguments. This application supports the
   * following arguments :
   * <ul>
   *  <li><code>-help</code> to display the list of supported arguments</li>
   *  <li><code>-verbose</code> to display the residuals for
   *      conditions <strong>not</strong> met</li>
   *  <li><code>-tolerance</code> to set the tolerance for use when
   *      non-exact checking is desired</li>
   *  <li>the uri of the XML file to check</li>
   * </ul>

   */
  public static void main(String[] args) {

    try {

      String uri = null;
      boolean verbose = false;
      double tolerance = -1.0;
      for (int i = 0; i < args.length; ++i) {
        if (args[i].equals("-help")) {
          usage(0);
        } else if (args[i].equals("-verbose")) {
          verbose = true;
        } else if (args[i].equals("-tolerance")) {
          if (++i == args.length) {
            System.err.println("missing argument for -tolerance switch");
            usage(1);
          }
          tolerance = Double.parseDouble(args[i]);
        } else {
          uri = args[i];
        }
      }

      if (uri == null) {
        usage(1);
      }

      RungeKuttaMethod method = new RungeKuttaMethod();
      new RungeKuttaFile(method).read(uri);

      checkHomogeneityConditions(method.getTimeSteps(),
                                 method.getInternalWeights(),
                                 tolerance);

      DerivationTreesSetGenerator generator = new DerivationTreesSetGenerator();
      System.out.print(method.getName() + " method");
      System.out.println(checkOrderConditions(generator,
                                              method.getTimeSteps(),
                                              method.getInternalWeights(),
                                              method.getEstimationWeights(),
                                              tolerance, verbose));

      QuadraticSurd[] e = method.getErrorWeights();
      if (e != null) {

        QuadraticSurd[] b = method.getEstimationWeights();
        QuadraticSurd[] bHat = new QuadraticSurd[b.length];
        for (int i = 0; i < bHat.length; ++i) {
          bHat[i] = QuadraticSurd.add(b[i], e[i]);
        }

        System.out.print("error estimation");
        System.out.println(checkOrderConditions(generator,
                                                method.getTimeSteps(),
                                                method.getInternalWeights(),
                                                bHat, tolerance, verbose));

      }

    } catch (NumberFormatException nfe) {
      System.err.println("error parsing number: " + nfe.getMessage());
      System.exit(1);
    } catch (IOException ioe) {
      System.err.println(ioe.getMessage());
      System.exit(1);
    } catch (FactoryConfigurationError fce) {
      System.err.println(fce.getMessage());
      System.exit(1);
    } catch (RKCheckException rke) {
      System.err.println(rke.getMessage());
      System.exit(1);
    }

  }

  /** Display usage and exit.
   * @param exit exit code
   */
  private static void usage(int exit) {
    System.out.println("usage: java CheckOrderConditions [-help] [-verbose] [-tolerance t] uri");
    System.exit(exit);
  }

}
