package org.kwansystems.rollercoasterometer;

public class SpecialMessage extends Nav45Payload {
  public String message;
  public SpecialMessage(int[] data) {
    super(data,"Special Message");
    StringBuffer msg=new StringBuffer();
    msg.append((char)((data[2]>>8) & 0xFF));
    msg.append((char)((data[2]>>0) & 0xFF));
    for(int i=3;i<9;i++) {
      msg.append((char)((data[i]>>16) & 0xFF));
      msg.append((char)((data[i]>> 8) & 0xFF));
      msg.append((char)((data[i]>> 0) & 0xFF));
    }
    msg.append((char)((data[9]>>16) & 0xFF));
    msg.append((char)((data[9]>>8) & 0xFF));
    message=msg.toString();
  }
  public String toString() {
    StringBuffer result=new StringBuffer(super.toString());
    result.append(String.format("\n%s",message));
    return result.toString();
  }
}
