package org.spaceroots.rkcheck;

import java.util.ListResourceBundle;

/** This class gather the message resources for the rkcheck application.

 * @version $Id: MessagesResources.java,v 1.2 2004/05/23 18:36:02 luc Exp $
 * @author L. Maisonobe

 */

public class MessagesResources
  extends ListResourceBundle {

  /** Simple constructor.
   */
  public MessagesResources() {
  }

  public Object[][] getContents() {
    return contents;
  }

  static final Object[][] contents = {
    { "element {0} cannot be found in file {1}",
      "element {0} cannot be found in file {1}" },
    { "missing {0} attribute in {1}",
      "missing {0} attribute in {1}" },
    { "unexpected content in element {0} of file {1}",
      "unexpected content in element {0} of file {1}" },
    { "internal weights array has {0} rows,"
      + " but time steps array has {1} elements",
      "internal weights array has {0} rows,"
      + " but time steps array has {1} elements" },
    { "row {0} has {1} elements, but should have {2} elements",
      "row {0} has {1} elements, but should have {2} elements" },
    { "estimation weights array has {0} rows,"
      + " but time steps array has {1} elements",
      "estimation weights array has {0} rows,"
      + " but time steps array has {1} elements" },
    { "error weights array has {0} rows,"
      + " but time steps array has {1} elements",
      "error weights array has {0} rows,"
      + " but time steps array has {1} elements" }
  };

}
