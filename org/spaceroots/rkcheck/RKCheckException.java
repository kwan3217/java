package org.spaceroots.rkcheck;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

/** This class is the base class for all specific exceptions thrown by
 * the rkcheck classes.

 * <p>When the rkcheck classes throw exceptions that are specific to
 * the package, these exceptions are always subclasses of
 * RKCheckException. When exceptions that are already covered by the
 * standard java API should be thrown, like
 * ArrayIndexOutOfBoundsException or IllegalArgumentException, these
 * standard exceptions are thrown rather than the rkcheck specific ones.</p>

 * @version $Id: RKCheckException.java,v 1.1 2004/05/23 13:30:49 luc Exp $
 * @author L. Maisonobe

 */

public class RKCheckException
  extends Exception {

  private static ResourceBundle resources
    = ResourceBundle.getBundle("org.spaceroots.rkcheck.MessagesResources");

  /** Translate a string.
   * @param s string to translate
   * @return translated string
   */
  private static String translate(String s) {
    try {
      return resources.getString(s);
    } catch (MissingResourceException mre) {
      return s;
    }
  }

  /** Simple constructor.
   * Build an exception with an empty message
   */
  public RKCheckException() {
    super();
  }

  /** Simple constructor.
   * Build an exception by translating the specified message
   * @param message message to translate
   */
  public RKCheckException(String message) {
    super(translate(message));
  }

  /** Simple constructor.
   * Build an exception by translating and formating a message
   * @param specifier format specifier (to be translated)
   * @param parts to insert in the format (no translation)
   */
  public RKCheckException(String specifier, String[] parts) {
    super(new MessageFormat(translate(specifier)).format(parts));
  }

  /** Simple constructor.
   * Build an exception from a cause
   * @param cause cause of this exception
   */
  public RKCheckException(Throwable cause) {
    super(cause);
  }

  /** Simple constructor.
   * Build an exception from a message and a cause
   * @param message message to translate
   * @param cause cause of this exception
   */
  public RKCheckException(String message, Throwable cause) {
    super(translate(message), cause);
  }

  /** Simple constructor.
   * Build an exception from a message and a cause
   * @param specifier format specifier (to be translated)
   * @param parts to insert in the format (no translation)
   * @param cause cause of this exception
   */
  public RKCheckException(String specifier, String[] parts, Throwable cause) {
    super(new MessageFormat(translate(specifier)).format(parts), cause);
  }

}
