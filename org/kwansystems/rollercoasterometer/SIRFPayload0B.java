/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.rollercoasterometer;

/**
 *
 * @author jeppesen
 */
public class SIRFPayload0B extends SIRFPayload {
  byte command;
  public SIRFPayload0B(byte[] payload) {
    super(payload,"Command Ack");
    command=payload[1];
  }
  public String toString() {
    StringBuffer result=new StringBuffer(super.toString());
    result.append(String.format("\nAcknowledging command type %02X", command));
    return result.toString();
  }
}
