/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.rollercoasterometer;

/**
 *
 * @author jeppesen
 */
public class NavSubframe1 extends NavSubframe {
  int wn;
  byte L2codes;
  byte URA;
  byte health;
  short iodc;
  boolean L2PhasData;
  double Tgd;
  double toc;
  double af2;
  double af1;
  double af0;
  public NavSubframe1(int[] data) {
    super(data);
    wn=(data[2] >> 14) & 0x3FF;
    L2codes=(byte)((data[2]>>12) & 0x03);
    URA=(byte)((data[2]>>8) & 0x0F);
    health=(byte)((data[2]>>2) & 0x3F);
    short iodcM=(byte)((data[2]>>0) & 0x03);
    //Signed byte for once
    Tgd=(byte)((data[6]>>0) & 0xFF);
    Tgd/=(1<<31);
    short iodcL=(short)((data[7]>>16) & 0xFF);
    iodc=(short)((iodcM<<8) | iodcL);
    toc=(data[7]>>0) & 0xFFFF;
    toc*=(1<<4);
    af2=(byte)((data[8]>>16) & 0xFF);
    af2/=(1L<<55);
    af1=(short)((data[8]>>0) & 0xFFFF);
    af1/=(1L<<43);
    af0=(data[9]>>2) & 0x3FFFFF;
    //Use the proper sign bit
    if(af0>(1<<21)) af0-=1<<22;
    af0/=(1L<<31);
  }
  public String toString() {
    StringBuffer result=new StringBuffer("Health and clock parameters");
    result.append(String.format("\nWeek Number (10 bit): %4d",wn));
    result.append(String.format("\nL2 codes:             %1d",L2codes));
    result.append(String.format("\nURA code:             %2d",URA));
    result.append(String.format("\nHealth code:          %2d",health));
    result.append(String.format("\nIssue of Data Clock:  %4d",iodc));
    result.append(String.format("\nL2 P has nav message: %b",L2PhasData));
    result.append(String.format("\nTgd:             %e s",Tgd));
    result.append(String.format("\ntoc:             %e s",toc));
    result.append(String.format("\naf2:             %e s/s^2",af2));
    result.append(String.format("\naf1:             %e s/s",af1));
    result.append(String.format("\naf0:             %e s",af0));
    return result.toString();
  }
}
