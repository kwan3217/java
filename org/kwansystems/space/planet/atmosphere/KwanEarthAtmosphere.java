package org.kwansystems.space.planet.atmosphere;

public class KwanEarthAtmosphere extends Atmosphere {
  Atmosphere Lower=new KwanLowerAtmosphere();
  Atmosphere Upper=new KwanUpperAtmosphere();
  public KwanEarthAtmosphere() {
    Zlimit=Upper.Zlimit;
  }
  public AirProperties calcProps(double Z) {
    if(Z>Zlimit)return AirProperties.Vacuum;
    if(Z>Lower.Zlimit) return Upper.calcProps(Z);
    return Lower.calcProps(Z);
  }
  public static void main(String[] args) {
    double[] alt=new double[] {
    0,58.93769,146.7639,
    0,1.0,2,3,4,5,6,7,8,9,70,72,74,76,78,80,82,84,85.999,1000
    };
    Atmosphere KEA=new KwanEarthAtmosphere();
    for (double A:alt) {
      System.out.println(KEA.calcProps(A*1000));
    }
  }
}
