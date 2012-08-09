package org.kwansystems.space.kepler;

import org.kwansystems.tools.vector.*;
import org.kwansystems.tools.time.*;

public class EarthLambert {
  public static void main(String[] args) {
    MathVector MECO1=new MathVector(5615074.791,   -2404971.169,    2326289.920); //Cartesian J2000
    MathVector MECO2=new MathVector(-7666799.559,    -4606197.718,    1541089.996 );  //Likewise
    MathVector VMECO2=new MathVector(5358.435867,-5537.128759,3566.102824);
    GaussFG Earth=new GaussFG(3.986004418e14);
    Course C=Earth.target(new MathStateTime(MECO1,new MathVector(),new Time(2009,11,24,20,47,50.956,TimeUnits.Seconds,TimeScale.UTC,TimeEpoch.J2000)),
                          new MathStateTime(MECO2,VMECO2,new Time(2009,11,24,22,18,03.256,TimeUnits.Seconds,TimeScale.UTC,TimeEpoch.J2000)), -1);
    System.out.println(C);
    System.out.println(MathVector.sub(C.arrive.S.V(),VMECO2));
  }
}
