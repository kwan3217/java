/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.rollercoasterometer;

/**
 *
 * @author jeppesen
 */
public class Almanac extends Nav45Payload {
  double e;
  double toa;
  double delta_i;
  double Omegad;
  byte health;
  double sqrtA;
  double Omega_0;
  double omega;
  double M_0;
  double a_f0;
  double a_f1;
  public Almanac(int[] data) {
    super(data,"Almanac");
    //unsigned 16 bit scale 21
    e=(int)((data[2]>>0) & 0xFFFF);
    e/=(1L<<21);
    //unsigned 8 bit scale 12
    toa=((short)((data[3] >> 16) & 0xFF));
    toa*=(1L<<12);
    //  Signed 16 bit scale -19
    delta_i=(short)((data[3]>>8) & 0xFFFF);
    delta_i/=(1L<<19);
    //  Signed 16 bit scale -38
    Omegad=(short)((data[4]>>8) & 0xFFFF);
    Omegad/=(1L<<38);
    //Bitfield  8 bit
    health=(byte)((data[4]>>0) & 0xFF);
    //unsigned 24 bit scale -11
    sqrtA=data[5];
    sqrtA/=(1L<<23);
    //  signed 24 bit scale -23
    Omega_0=data[6];
    if(Omega_0>0x800000) Omega_0-=0x1000000;
    Omega_0/=(1L<<23);
    //  signed 24 bit scale -23
    omega=data[7];
    if(omega>0x800000) omega-=0x1000000;
    omega/=(1L<<23);
    //  signed 24 bit scale -23
    M_0=data[8];
    if(M_0>0x800000) M_0-=0x1000000;
    M_0/=(1L<<23);

  }
  public String toString() {
    StringBuffer result=new StringBuffer(super.toString());
    result.append(String.format("\ne:                         %e",e));
    result.append(String.format("\ntoa:                       %f",toa));
    result.append(String.format("\ndelta_i:                   %e",delta_i));
    result.append(String.format("\nOmegad:                    %e",Omegad));
    result.append(String.format("\nhealth:                    %04X",health));
    result.append(String.format("\nsqrtA:                     %e",sqrtA));
    result.append(String.format("\nOmega_0:                   %e",Omega_0));
    result.append(String.format("\nomega:                     %e",omega));
    result.append(String.format("\nM_0:                       %e",M_0));
    return result.toString();
  }
}
