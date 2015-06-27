/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.rollercoasterometer;

/**
 *
 * @author jeppesen
 */
public class Nav45Payload {
  public byte SVID;
  public String name;
  public Nav45Payload(int[] data, String Lname) {
    SVID  =(byte)((data[2]>>16) & 0x3F);
    name=Lname;
  }
  public String toString() {
    return String.format("SVID %d: %s",SVID,name);
  }
}
