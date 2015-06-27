/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.rollercoasterometer;

/**
 *
 * @author jeppesen
 */
public class SIRFPayload {
  String name;
  byte[] payloadBytes;
  public SIRFPayload(byte[] LpayloadBytes,String Lname) {
    //Nop
    name=Lname;
    payloadBytes=LpayloadBytes;
  }
  //Static factory method
  public static SIRFPayload interpret(byte[] payload) {
    if(payload.length==0) return null;
    switch(payload[0]) {
      case (byte)0x02:
        return new SIRFPayload02(payload);
      case (byte)0x04:
        return new SIRFPayload04(payload);
      case (byte)0x06:
        return new SIRFPayload06(payload);
      case (byte)0x07:
        return new SIRFPayload(payload,"Clock Status");
      case (byte)0x08:
        return new SIRFPayload08(payload);
      case (byte)0x09:
        return new SIRFPayload09(payload);
      case (byte)0x0B:
        return new SIRFPayload0B(payload);
      case (byte)0x0C:
        return new SIRFPayload0C(payload);
      case (byte)0x15:
        return new SIRFPayload15(payload);
      case (byte)0x16:
        return new SIRFPayload16(payload);
//      case (byte)0x17:
//        return new SIRFPayload17(payload);
      case (byte)0x18:
        return new SIRFPayload18(payload);
      case (byte)0x1B:
        return new SIRFPayload(payload,"DGPS Status");
      case (byte)0x1C:
        return new SIRFPayload(payload,"Navigation Library Measurement Data");
      case (byte)0x1E:
        return new SIRFPayload(payload,"Navigation Library SV State Data");
      case (byte)0x29:
        return new SIRFPayload29(payload);
      case (byte)0x2A:
        return new SIRFPayload2A(payload);
      case (byte)0x2B:
        return new SIRFPayload2B(payload);
      case (byte)0x2C:
        return new SIRFPayload2C(payload);
      case (byte)0x2D:
        return new SIRFPayload(payload,"Logomatic UART error");
      case (byte)0xAC:
        return new SIRFPayloadAC(payload);
      case (byte)0xE1:
        return new SIRFPayloadE1(payload);
      case (byte)0xFE:
        return new SIRFPayloadFE(payload);
      case (byte)0xFF:
        return new SIRFPayloadFF(payload);

    }
    return null;
  }
  public String toString() {
    if(payloadBytes.length>0) {
      return String.format("Payload type 0x%02X: %s",payloadBytes[0],name);
    } else {
      return "Zero length payload";
    }
  }
}
