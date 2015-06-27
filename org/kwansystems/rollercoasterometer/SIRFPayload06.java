/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.rollercoasterometer;

/**
 *
 * @author jeppesen
 */
public class SIRFPayload06 extends SIRFPayload {
  String versionString;
  public SIRFPayload06(byte[] payload) {
    super(payload,"Software Version");
    versionString=new String(payload).substring(1);
  }
  public String toString() {
    StringBuffer result=new StringBuffer(super.toString());
    result.append("\n");result.append(versionString);
    return result.toString();
  }
}
