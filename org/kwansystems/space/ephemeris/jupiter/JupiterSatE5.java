package org.kwansystems.space.ephemeris.jupiter;

import org.kwansystems.space.ephemeris.*;
import org.kwansystems.tools.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeEpoch;
import org.kwansystems.tools.time.TimeScale;
import org.kwansystems.tools.time.TimeUnits;
import org.kwansystems.tools.vector.*;

import java.io.*;
import java.util.*;

import static org.kwansystems.space.Constants.*;
import static java.lang.Math.*;

/**
 Orbital Motions of Galilean satellites of Jupiter. Implementation of Galilean satellite ephemeris E5
  <a href="http://adsabs.harvard.edu/cgi-bin/nph-bib_query?bibcode=1998A%26AS..129..205L&db_key=AST">Paper here.</a>
  Position is returned in units of m, centered on the Jupiter body center
<p>
This is the accuracy rating based on http://ssd.jpl.nasa.gov/sat_eph.html
<table>
<tr><td>Satellite</td><td>Start Time</td><td>Stop Time</td><td>Radial (km)</td><td>Downtrack (km)</td><td>Planar (km)</td><td>Period (s)</td></tr>
<tr><td>Io              </td><td>03-JAN-1894</td><td>03-Nov-2023</td><td> 10</td><td>125</td><td>15</td><td>0.0003</td></tr>
<tr><td>Europa 	        </td><td>03-JAN-1894</td><td>03-Nov-2023</td><td>150</td><td>500</td><td>30</td><td>0.001</td></tr>
<tr><td>Ganymede	    </td><td>03-JAN-1894</td><td>03-Nov-2023</td><td> 60</td><td>300</td><td>30</td><td>0.003</td></tr>
<tr><td>Callisto 	    </td><td>03-JAN-1894</td><td>03-Nov-2023</td><td> 25</td><td>200</td><td>70</td><td>0.02</td></tr>
</table>
This is good enough for most purposes, but not plopping jovocentric vectors
from Horizons of something like Galileo in a close approach of one of the sats.
Satellite centered vectors should be used in this case anyways.
<p>
For comparison, this is the best integrated ephemeris, JUP230
<table>
<tr><td>Io        29-Dec-1924</td><td>01-Jan-2025</td><td>5</td><td>5</td><td>5</td><td>0.0003</td></tr>
<tr><td>Europa    29-Dec-1924</td><td>01-Jan-2025</td><td>5</td><td>5</td><td>5</td><td>0.0003</td></tr>
<tr><td>Ganymede  29-Dec-1924</td><td>01-Jan-2025</td><td>5</td><td>5</td><td>5</td><td>0.0003</td></tr>
<tr><td>Callisto  29-Dec-1924</td><td>01-Jan-2025</td><td>5</td><td>5</td><td>5</td><td>0.0003</td></tr>
</table>
This implementation matches JUP230 to within twice accuracy rating for all four
satellites for at least from 23 May 1968 (JD 2440000.0) to 17 Jun 2009 (JD 2455000.0)
and it is believed to be equally precise over the entire validity range.
 */
public class JupiterSatE5 extends TableEphemeris {
  /** Names of each satellite in the satArray list  */
  public static final String[] SatNames={"Jupiter","Io","Europa","Ganymede","Callisto"};
  /** Table of phase constants used in calculating the theory */
  private static final double[][] KTable={
    /*                deg         deg/day */
    /* 0 l1       */{ toRadians(106.077187), toRadians(203.48895579033)}, // Mean Longitude of Io
    /* 1 l2       */{ toRadians(175.731615), toRadians(101.37472473479)}, // Mean Longitude of Europa
    /* 2 l3       */{ toRadians(120.558829), toRadians(50.31760920702)}, // Mean Longitude of Ganymede
    /* 3 l4       */{ toRadians( 84.444587), toRadians(21.57107117668)}, // Mean Longitude of Callisto
    /* 4 philambda*/{ toRadians(199.676608), toRadians( 0.17379190461)}, // Free Libration
    /* 5 pi1      */{ toRadians( 97.088086), toRadians( 0.16138586144)}, // Proper periapse of Io
    /* 6 pi2      */{ toRadians(154.866335), toRadians( 0.04726306609)}, // Proper periapse of Europa
    /* 7 pi3      */{ toRadians(188.184037), toRadians( 0.00712733949)}, // Proper periapse of Ganymede
    /* 8 pi4      */{ toRadians(335.286807), toRadians( 0.00183999637)}, // Proper periapse of Callisto
    /* 9 PIJ      */{ toRadians( 13.469942), toRadians( 0.0          )}, // Longitude of perihelion of Jupiter
    /*10 omega1   */{ toRadians(312.334566), toRadians(-0.13279385940)}, // Proper node of Io
    /*11 omega2   */{ toRadians(100.441116), toRadians(-0.03263063731)}, // Proper node of Europa
    /*12 omega3   */{ toRadians(119.194241), toRadians(-0.00717703155)}, // Proper node of Ganymede
    /*13 omega4   */{ toRadians(322.618633), toRadians(-0.00175933880)}, // Proper node of Callisto
    /*14 psi      */{ toRadians(316.518203), toRadians(-2.08362e-6   )}, // Longitude of origin of coordinates (Jupiter's pole)
    /*15 G'       */{ toRadians( 31.978528), toRadians( 0.03345973390)}, // Mean Anomaly of Saturn
    /*16 G        */{ toRadians( 30.237557), toRadians( 0.08309257010)}, // Mean Anomaly of Jupiter
    /*17 phi1     */{ toRadians(188.374346), toRadians( 0.0          )}, // Phase angle in solar (A/R)^3 with angle 2G'- G
    /*18 phi2     */{ toRadians( 52.224824), toRadians( 0.0          )}, // Phase angle in solar (A/R)^3 with angle 5G'-2G
    /*19 phi3     */{ toRadians(257.184000), toRadians( 0.0          )}, // Phase angle in solar (A/R)^3 with angle  G'- G
    /*20 phi4     */{ toRadians(149.152605), toRadians( 0.0          )}, // Phase angle in solar (A/R)^3 with angle 2G'-2G
    /*21 OmegaJ   */{ toRadians( 99.998526), toRadians( 0.0          )}  // Longitude of Ascending node of Jupiter's orbit on ecliptic
  };
  /** Names of each item in the phase table, to be used when parsing the terms in the text representation of the theory */
  private static final String KTableNames[]={
    "l1","l2","l3","l4",
    "philambda",
    "pi1","pi2","pi3","pi4",
    "PIJ",
    "omega1","omega2","omega3","omega4",
    "psi","G'","G",
    "phi1","phi2","phi3","phi4","OmegaJ"
  };
  /** Zero-based index of this satellite */
  int SatIndex;
  
  /**
    This calculates the values of the phase constant table at a particular time.
    Such things as mean longitude, orbit precession, etc.
   @param T Time in days from theory epoch at which to calculate the series
   @return evaluated K from K table
  */
  private static double[] EvalK(double T) {
    double[] K=new double[KTable.length];
    for(int i=0;i<KTable.length;i++) {
      K[i]=KTable[i][0]+KTable[i][1]*T;
    }
    return K;
  }
  /**Sums a series and calculates its derivative with respect to time.  
   @param TermArg A table of term phase coefficients 
   @param TermMult A table of term phase multiple coefficients 
   @param Amp A table of amplitudes
   @param T Time in days from theory epoch at which to calculate the series
   @param type Trigonometric series type
   @return a two-element array with the value of the series as the first element,
           and the derivative of the series with respect to time as the second element, at T 
 */
  private double[] Series(byte[][] TermArg, byte[][] TermMult, int[] Amp, double T, TrigType type) {
    double[] K=EvalK(T);
    double[] Accumulator=new double[2];
    for(int i=0;i<TermArg.length;i++) {
      double Arg=0;
      double Argd=0;
      for(int j=0;j<TermArg[i].length;j++) {
        Arg+= TermMult[i][j]*K[TermArg[i][j]];
        Argd+=TermMult[i][j]*KTable[TermArg[i][j]][1];
      }
      Accumulator[0]+=type.eval(Arg)*(((double)Amp[i])/1e7);
      Accumulator[1]+=type.evald(Arg)*(((double)Amp[i])/1e7)*Argd;
    }
    return Accumulator;
  }
  /** Guts of the ephemeris. Evaluate all the series at the right time
    and combine them properly to form position and velocity vectors.

    @param JDTDB Julian date of the chosen moment in TDB
    @return Position and velocity in natural frame
   */
  public MathState E5Ephemeris(double JDTDB) {
    double d=JDTDB-Epoch;
    double[] K=EvalK(d);
    double xi[]=Series(XiTableTermArg,XiTableTermMult,XiTableAmp,d,TrigType.COS);
    //There is no reference for this anywhere, but v is an angle
    //directly in radians, different from all other angles in the theory.
    double v[]=Series(VTableTermArg,VTableTermMult,VTableAmp,d,TrigType.SIN);
    //Time completed Tau for the satellite in question
    double tau[]=new double[] {d+v[0]/KTable[SatIndex-1][1],1+v[1]/KTable[SatIndex-1][1]};
    //s(t)=zeta(tau)
    double s[]=Series(ZetaTableTermArg,ZetaTableTermMult,ZetaTableAmp,tau[0],TrigType.SIN);
    double R[]=new double[] {a[SatIndex]*(1+xi[0]),a[SatIndex]*xi[1]};
    double Angle[]=new double[] {K[SatIndex-1]-K[14]+v[0],KTable[SatIndex-1][1]-KTable[14][1]+v[1]};
    MathVector r=new MathVector(
      R[0]*cos(Angle[0]),
      R[0]*sin(Angle[0]),
      R[0]*s[0]
    );
    MathVector rd=new MathVector(
      -R[0]*sin(Angle[0])*Angle[1]+R[1]*cos(Angle[0]),
       R[0]*cos(Angle[0])*Angle[1]+R[1]*sin(Angle[0]),
       R[0]*s[1]+R[1]*s[0]
    );
    return new MathState(r,rd);
  }
  /** Calls the actual ephemeris code and scales the result from m/day to m/s.
   * @see org.kwansystems.space.ephemeris.Ephemeris#CalcState(org.kwansystems.tools.time.Time)
   */
  public MathState CalcState(Time TT) {
    MathState result=E5Ephemeris(TT.get(TimeUnits.Days,TimeScale.TDB));
    result=result.replaceV(result.V().mul(1.0/86400.0));
    return result;
  }
  //data for coordinate transformation
  private static final double epsilon25= 0.005110;
  private static final double epsilon26=-0.000137;
  private static final double epsilon27=-0.000000;
  private static final double IJ=toRadians(3.10401)*(1+epsilon25);  //Inclination of Jupiter orbit to Jupiter equator
  private static final double J=toRadians(1.30691)*(1+epsilon26);   //Inclination of Jupiter orbit to ecliptic
  private static final double epsB1950=epsB1950R*(1+epsilon27); //Obliquity of Earth axis, B1950
  //Coordinate transformation routines
  private class ToB1950Equ extends RotatorEphemeris {
    @Override
    public Rotator CalcRotation(Time TT) {
      double T=TT.get(TimeUnits.Days,TimeScale.TDB,TimeEpoch.JD)-Epoch;
      double[] K=EvalK(T);
      double OmegaJ=K[21];
      double psi=K[14];
      Rotator result=MathMatrix.Rot1(-IJ);
      result=MathMatrix.Rot3(-psi+OmegaJ).combine(result);
      result=MathMatrix.Rot1(-J).combine(result);
      result=MathMatrix.Rot3(-OmegaJ).combine(result);
      result=MathMatrix.Rot1(-epsB1950).combine(result);
      return result;
    }
  }
  private class ToJ2000Equ extends ChainRotatorEphemeris {
    public ToJ2000Equ() {
      super(new RotatorEphemeris[] {new ToB1950Equ(),new ConstRotatorEphemeris(B1950toJ2000)});
    }
  }
  public static Ephemeris[] satArray=new Ephemeris[] {
      null,
      new JupiterSatE5(1),
      new JupiterSatE5(2),
      new JupiterSatE5(3),
      new JupiterSatE5(4)
    };
  public static void main(String[] args) throws IOException {
    //    planetocentric position and velocity J2000 (JD=Julian Date)
    //    position (in au) and velocity (in au per julian year of 365.25d),
    //    referred to the mean Equinox and Ecliptic J2000
    for(int i=1;i<=4;i++) {
      satArray[i].testHorizons("Data/JupiterSatE5/JupiterSatE5.50"+i+".FK5.csv",0,15000);
    }
    System.out.println("Done");
  }
  
  //Masses of Jupiter and the satellites
  
  public static final double JupiterGM=1.26686537E17; //+-100e9, Jupiter GM is known more accurately than either G or M
  public static final double JupiterMass=JupiterGM/Gkgms;
  public static final double IoMass=893.3e20;      //kg
  public static final double EuropaMass=479.7e20;
  public static final double GanymedeMass=1482e20;
  public static final double CallistoMass=1076e20;
  
  //All GM's are in m^3/s^2
  public static final double IoGM=IoMass*Gkgms;
  public static final double EuropaGM=EuropaMass*Gkgms;
  public static final double GanymedeGM=GanymedeMass*Gkgms;
  public static final double CallistoGM=CallistoMass*Gkgms;
    
  //Sizes of Jupiter and the satellites, m
  public static final double JupiterRadius=71492000;
  public static final double IoRadius=1821300;
  public static final double EuropaRadius=1565000;
  public static final double GanymedeRadius=2634000;
  public static final double CallistoRadius=2403000;
  
  public static final double[] satMass=new double[] {JupiterMass,IoMass,EuropaMass,GanymedeMass,CallistoMass};
  public static final double[] satGM=new double[] {JupiterGM,IoGM,EuropaGM,GanymedeGM,CallistoGM};
  public static final double[] satMassRatio=satMass;
  public static final double[] satRadius=new double[] {JupiterRadius,IoRadius,EuropaRadius,GanymedeRadius,CallistoRadius};
  public static final double[] a={0,2.819353e-3*MPerAU,4.485883e-3*MPerAU,7.155366e-3*MPerAU,12.585464e-3*MPerAU};
  /** TDB Julian Date of Theory epoch */
  private static final double Epoch=2443000.5; 
  
  private byte[][] VTableTermArg,XiTableTermArg,ZetaTableTermArg;
  private byte[][] VTableTermMult,XiTableTermMult,ZetaTableTermMult;
  private int[] VTableAmp,XiTableAmp,ZetaTableAmp;
  public MathVector CalcPos(Time TT) {
	  return defaultCalcPos(TT);
  }
  public MathVector CalcVel(Time TT) {
	  return defaultCalcVel(TT);
  }
  private void ParseTerm(String S, List<byte[]> Arg, List<byte[]> Mult) {
	  if(!S.startsWith("-")) S="+"+S;
	  for(int i=0;i<KTableNames.length;i++) {
	    S=S.replace(KTableNames[i], "~"+i+"~");
	  }
	  String[] T=S.split("~");
    byte[] arg=new byte[T.length/2];
    byte[] mult=new byte[T.length/2];
	  for(int i=0;i<T.length/2;i++) {
      if (T[i*2].length()==1)T[i*2]=T[i*2]+"1";
      if(T[i*2].charAt(0)=='+')T[i*2]=T[i*2].substring(1);
  	  mult[i]=Byte.parseByte(T[i*2]);
	    arg[i]=Byte.parseByte(T[i*2+1]);
	  }
	  Arg.add(arg);
    Mult.add(mult);
  }
  private void ReadBlock(String var,LineNumberReader inf, List<Integer> Amp, List<byte[]> Arg, List<byte[]> Mult) throws IOException {
    Amp.clear();
    Arg.clear();
    Mult.clear();
	  String line=inf.readLine();
    while(!line.equals("S"+var+"-"+SatIndex)) {
      line=inf.readLine();
    }
    line=inf.readLine();
    while(line!=null && line.trim().length()>0 && line.charAt(0)!='S') {
      line=line.trim();
      String[] T=line.split("\\s+");
      ParseTerm(T[2],Arg,Mult);
      Amp.add(Integer.parseInt(T[1],10));
      line=inf.readLine();
    }
  }
  protected void LoadText() throws IOException {
    LineNumberReader inf=new LineNumberReader(new FileReader("Data/JupiterSatE5/E5Series.txt"));
    List<Integer> Amp=new ArrayList<Integer>();
    List<byte[]> Arg=new ArrayList<byte[]>();
    List<byte[]> Mult=new ArrayList<byte[]>();

    ReadBlock("Xi",inf,Amp,Arg,Mult);
    XiTableAmp=new int[Amp.size()];
    for(int i=0;i<Amp.size();i++)XiTableAmp[i]=Amp.get(i);
    XiTableTermArg=Arg.toArray(new byte[][]{});
    XiTableTermMult=Mult.toArray(new byte[][]{});

    ReadBlock("V",inf,Amp,Arg,Mult);
    VTableAmp=new int[Amp.size()];
    for(int i=0;i<Amp.size();i++)VTableAmp[i]=Amp.get(i);
    VTableTermArg=Arg.toArray(new byte[][]{});
    VTableTermMult=Mult.toArray(new byte[][]{});

    ReadBlock("Zeta",inf,Amp,Arg,Mult);
    ZetaTableAmp=new int[Amp.size()];
    for(int i=0;i<Amp.size();i++)ZetaTableAmp[i]=Amp.get(i);
    ZetaTableTermArg=Arg.toArray(new byte[][]{});
    ZetaTableTermMult=Mult.toArray(new byte[][]{});

    inf.close();
  }

  protected void SaveSerial(ObjectOutputStream ouf) throws IOException {
    ouf.writeObject(XiTableTermArg);
    ouf.writeObject(XiTableTermMult);
    ouf.writeObject(XiTableAmp);
    ouf.writeObject(VTableTermArg);
    ouf.writeObject(VTableTermMult);
    ouf.writeObject(VTableAmp);
    ouf.writeObject(ZetaTableTermArg);
    ouf.writeObject(ZetaTableTermMult);
    ouf.writeObject(ZetaTableAmp);
  }
  protected void LoadSerial(ObjectInputStream inf) throws IOException, ClassNotFoundException {
    XiTableTermArg=(byte[][])inf.readObject();
    XiTableTermMult=(byte[][])inf.readObject();
    XiTableAmp=(int[])inf.readObject();
    VTableTermArg=(byte[][])inf.readObject();
    VTableTermMult=(byte[][])inf.readObject();
    VTableAmp=(int[])inf.readObject();
    ZetaTableTermArg=(byte[][])inf.readObject();
    ZetaTableTermMult=(byte[][])inf.readObject();
    ZetaTableAmp=(int[])inf.readObject();
  }
  protected String SerialFilenameCore() {
	  return "JupiterSatE5/JupiterSatE5_"+SatIndex;
  }
  public JupiterSatE5(int LSatNumber) {
    SatIndex=LSatNumber;
    TargetNumber=500+SatIndex;
    TargetName=SatNames[SatIndex];
    CenterNumber=599;
    CenterName="Jupiter";
    TheoryName="Galilean E5";
    NaturalToFK5=new ToJ2000Equ();
    Load();
  }
}
