package org.kwansystems.space.planet.atmosphere;

public class TitanAtmosphere extends SimpleAtmosphere {
  public TitanAtmosphere() {
    Zlimit=1350660;
  }

//------------------------------------
  
  public static final double[] TempAlt={43.6,68.43,116.84,259.22,442.84,590.94,682.23,827.1,969.03,1049.69,1350.66};
  public static final double[] TK3={ 1.12159164222825E-4,
                                     2.15947502784268E-3,
                                     5.36557311821800E-4,
                                    -1.74230776296378E-7,
                                     6.48888705712931E-7,
                                     3.36906860810806E-6,
                                    -4.67470805258642E-6,
                                    -8.87202947604352E-7,
                                     1.84021160339355E-6,
                                    -9.52776874085679E-7,
                                     7.73890211658185E-9};
  public static final double[] TK2={ 4.21258542300712E-3,
                                    -2.98959750217254E-1,
				    -1.78370807497344E-1,
				    -1.26141753206307E-3,
				    -8.74501152859699E-4,
				    -4.87814876110702E-3,
				     1.01428779275457E-2,
				     1.14257463879438E-3,
				    -4.85466054767683E-3,
				     3.06759814095579E-3,
				    -9.42124923782709E-5};
  public static final double[] TK1={-9.80594405594406E-1,
				     1.37804365699160E+1,
				     2.00753733424664E+1,
				     6.70160889373581E-1,
				     3.03639400215927E-1,
				     2.24924888609206E+0,
				    -7.07689837627021E+0,
				    -8.48883736313521E-2,
				     4.23831092755835E+0,
				    -3.24749282453456E+0,
				     2.15318863479549E-1};
  public static final double[] TK0={ 9.67000000000000E+1,
				    -1.40247805968598E+2,
				    -6.15648691333229E+2,
				     8.99466581214149E+1,
				     1.44620175284157E+2,
				    -1.68060832906314E+2,
				     1.75757014733985E+3,
				    -2.61754693467094E+1,
				    -1.04242920851596E+3,
				     1.31383221548198E+3,
				     5.18590777471313E+1};
  public static final double Pm=-5.700505921864640E-002;
  public static final double Pb= 1.189136190069050E+001;
  public static final double PLinAlt=65.7;
  public static final double PK2= 7.04535214336469E-6;
  public static final double PK1=-2.70700574676826E-2;
  public static final double PK0= 9.89422109357884E+0;
  public static final double WLinAlt0=2.86;
  public static final double WLinAlt1=33.17;
  public static final double Wsurf=25.83;
  public static final double Wup=27.23;
  public static final double Wm=4.20266777265815E-1;
  public static final double Wb=2.62683745822507E+1;				     
  public double Temp(double Alt) {
    Alt/=1000;
    if (Alt>Zlimit) {
      return 0;
    }
    int layer=0;
    for(layer=0;Alt>TempAlt[layer];layer++) ;
    return TK3[layer]*Alt*Alt*Alt+TK2[layer]*Alt*Alt+TK1[layer]*Alt+TK0[layer];
  }
  public double Pressure(double Alt) {
    Alt/=1000;
    if(Alt<PLinAlt) {
      return Math.exp(Pm*Alt+Pb);
    } else {
      return Math.exp(PK2*Alt*Alt+PK1*Alt+PK0);
    }
  }
  public double MolWeight(double Alt) {
    Alt/=1000;
    if(Alt<WLinAlt0) {
      return Wsurf;
    } else if (Alt<WLinAlt1) {
      return Math.log(Alt)*Wm+Wb;
    } else {
      return Wup;
    }
  }
}
