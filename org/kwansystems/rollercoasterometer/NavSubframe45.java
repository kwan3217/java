/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.rollercoasterometer;

/**
 *
 * @author jeppesen
 */
class NavSubframe45 extends NavSubframe {
  public byte DataID;
  public byte SVID;
  public Nav45Payload payload;
  public NavSubframe45(int[] data) {
    super(data);
    DataID=(byte)((data[2] >> 22) & 0x03);
    SVID  =(byte)((data[2]>>16) & 0x3F);
    if(SVID<=32) {
      payload=new Almanac(data);
    } else switch(SVID) {
      case 55:
        payload=new SpecialMessage(data);
        break;
      default:
        payload=new Nav45Payload(data,"Unparsed");
    }
  }
  public String toString() {
    if(payload==null) {
      return "Empty payload";
    } else {
      return payload.toString();
    }
  }
}
