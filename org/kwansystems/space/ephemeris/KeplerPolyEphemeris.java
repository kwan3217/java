package org.kwansystems.space.ephemeris;


import org.kwansystems.space.kepler.*;
import org.kwansystems.tools.Polynomial;
import org.kwansystems.tools.Polynomial.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.vector.*;

import static org.kwansystems.space.Constants.*;
import static org.kwansystems.tools.time.TimeEpoch.*;
import static org.kwansystems.tools.time.TimeScale.*;
import static org.kwansystems.tools.time.TimeUnits.*;

public class KeplerPolyEphemeris extends KeplerEphemeris {
  public Polynomial a;
  public Polynomial e;
  public Polynomial i;
  public Polynomial lan;
  public Polynomial lp;
  public Polynomial lm;
  public String Name;
  public static KeplerPolyEphemeris Mercury=new KeplerPolyEphemeris(
    "Mercury",
    new double[] {0.387098310},
    new double[] {0.20563175,+0.000020406,-0.000000028,-0.00000000017},
    new double[] {7.004986,-0.0059516,+0.00000081,+0.000000041},
    new double[] {48.330893,-0.1254229,-0.00008833,-0.000000196},
    new double[] {77.456119,+0.1588643,-0.00001343,+0.000000039},
    new double[] {252.250906,+149472.6746358,-0.00000535,+0.000000002}
  );
  public static KeplerPolyEphemeris Venus=new KeplerPolyEphemeris(
    "Venus",
    new double[] {0.723329820},
    new double[] {0.00677188,-0.000047766,+0.000000097,+0.00000000044},
    new double[] {3.394662,-0.0008568,-0.00003244,+0.000000010},
    new double[] {76.679920,-0.278008,-0.00014256,-0.000000198},
    new double[] {131.563707,0.0048646,-0.00138232,-0.000005332},
    new double[] {181.979801,58517.8156760,0.00000165,-0.000000002}
  );
  public static KeplerPolyEphemeris Earth=new KeplerPolyEphemeris(
    "Earth",
    new double[] {1.000001018},
    new double[] {0.01670862,-0.000042037,-0.0000001236,+0.00000000004},
    new double[] {0,+0.0130546,-0.00000931,-0.000000034},
    new double[] {0.0},
    new double[] {102.937348,+0.3225557,+0.00015026,+0.000000478},
    new double[] {100.466449,+35999.3728519,-0.00000568,0}
  );
  public static KeplerPolyEphemeris Mars=new KeplerPolyEphemeris(
    "Mars",
    new double[] {1.523679342},
    new double[] {0.09340062,+0.000090483,-0.0000000806,-0.00000000035},
    new double[] {1.849726,-0.0081479,-0.00002255,-0.000000027},
    new double[] {49.558093,-0.2949846,-0.00063993,-0.000002143},
    new double[] {336.060234,+0.4438898,-0.00017321,+0.000000300},
    new double[] {355.433275,+19140.2993313,+0.00000261,-0.000000003}
  );
  public static KeplerPolyEphemeris Jupiter=new KeplerPolyEphemeris(
    "Jupiter",
    new double[] {5.202603191,0.0000001913},
    new double[] {0.04849485,+0.000163244,-0.0000004719,-0.00000000197},
    new double[] {1.303270,-0.0019872,+0.00003318,+0.000000092},
    new double[] {100.464441,+0.1766828,+0.00090387,-0.000007032},
    new double[] {14.331309,+0.2155525,+0.00072252,-0.000004590},
    new double[] {34.351484,+3034.9056746,-0.00008501,+0.000000004}
  );
  public static KeplerPolyEphemeris Saturn=new KeplerPolyEphemeris(
    "Saturn",
    new double[] {9.554909596,-0.0000021389},
    new double[] {0.05550862,-0.000346818,-0.0000006456,+0.00000000338},
    new double[] {2.488878,+0.0025515,-0.00004903,+0.000000018},
    new double[] {113.665524,-0.2566649,-0.00018345,+0.000000357},
    new double[] {93.056787,+0.5665496,+0.00052809,+0.000004882},
    new double[] {50.077471,+1222.1137943,+0.00021004,-0.000000019}
  );
  public static KeplerPolyEphemeris Uranus=new KeplerPolyEphemeris(
    "Uranus",
    new double[] {19.218466062,-0.0000000372,0.00000000098},
    new double[] {0.04629590,-0.000027337,+0.0000000790,0.00000000025},
    new double[] {0.773196,-0.0016869,+0.00000349,+0.000000016},
    new double[] {74.005947,+0.0741461,+0.00040540,+0.000000104},
    new double[] {173.005159,0.0893206,-0.00009470,+0.000000413},
    new double[] {314.055005,+428.4669983,-0.00000486,+0.000000006}
  );
  public static KeplerPolyEphemeris Neptune=new KeplerPolyEphemeris(
    "Neptune",
    new double[] {30.110386869,-0.0000001663,+0.00000000069},
    new double[] {0.00898809,+0.000006408,-0.0000000008},
    new double[] {1.769952,+0.0002257,+0.00000023},
    new double[] {131.784057,-0.0061651,-0.00000219,-0.000000078},
    new double[] {48.123691,+0.0291587,0.00007051},
    new double[] {304.348665,218.4862002,+0.00000059,-0.000000002}
  );
  public static KeplerPolyEphemeris Pluto=new KeplerPolyEphemeris(
    "Pluto",
    // elements from JPL SSD, http://ssd.jpl.nasa.gov/elem_planets.html
    new double[] {39.48168677},
    new double[] {0.24880766},
    new double[] {17.14175},
    new double[] {110.30347},
    new double[] {224.06676},
    new double[] {238.92881,522747.90/3600.0}
  );
  public static KeplerPolyEphemeris Planets[]=new KeplerPolyEphemeris[] {
    null,Mercury,Venus,Earth,Mars,Jupiter,Saturn,Uranus,Neptune,Pluto
  };
  public KeplerPolyEphemeris(String LName, double[] La, double[] Le, double[] Li,
                double[] Llan, double[] Llp, double[] Llm) {
    Name=LName;
    a=new Polynomial(La,order.ConstFirst, MPerAU);
    e=new Polynomial(Le,order.ConstFirst, 1);
    i=new Polynomial(Li,order.ConstFirst,Math.toRadians(1));
    lan=new Polynomial(Llan,order.ConstFirst,Math.toRadians(1));
    lp=new Polynomial(Llp,order.ConstFirst,Math.toRadians(1));
    lm=new Polynomial(Llm,order.ConstFirst,Math.toRadians(1));
  }
  public Elements CalcElements(Time T) {
	Elements EE=new Elements();
    double Ttdb=T.get(Centuries,TDB,J2000);  //Julian centuries since J2000 TDB
    EE.Epoch=T;
    EE.GM=SunGM;
    EE.A=a.eval(Ttdb);
    EE.E=e.eval(Ttdb);
    EE.I=i.eval(Ttdb);
    EE.LAN=lan.eval(Ttdb);
    EE.AP=lp.eval(Ttdb)-EE.LAN;
    EE.M=lm.eval(Ttdb)-EE.AP-EE.LAN;
    EE.FillInElements();
	  return EE;
  }
  public MathState RotateState(Time T, MathState S) {
	  return S;
  }
}
