package org.kwansystems.space.ephemeris.earth;

import java.io.*;
import java.util.zip.*;

import org.kwansystems.space.ephemeris.*;
import org.kwansystems.tools.*;
import org.kwansystems.tools.chart.*;
import org.kwansystems.tools.vector.*;

import static java.lang.Math.*;
import static org.kwansystems.tools.Scalar.*;
import org.kwansystems.tools.rotation.*;
import org.kwansystems.tools.time.*;

import static org.kwansystems.space.ephemeris.RotatorEphemeris.Frame.*;

/**
 * IAU 1980 Nutation theory. Produces rotation matrix from equator
 * and equinox true of date to equator and equinox mean of date.
 *
 */
public class EarthRotNutationIAU1980 extends RotatorEphemeris {
  public EarthRotNutationIAU1980(Frame Lfrom, Frame Lto) {
    super(Lfrom,Lto);
    naturalFrom=MOD;
    naturalTo=TOD;
    setInv(Lfrom,Lto);
  }
  private static byte[][] argMult;
  private static byte[][] arg;
  private static int[][] amp;
  private static final int num_terms=106;
  private static final int num_args=5;
  /**
   * Mean longitude of the Moon minus the mean longitude of the Moon's perigee. 
   * Constants are entered in the form specified in IAU1980, poly takes time in 
   * Julian centuries from J2000, returns arcseconds.
   */
  private static Polynomial lPoly=new Polynomial(new double[] {
      dmsToSeconds(134,57,46.733),+rdmsToSeconds(1325,198,52, 2.633), +31.310,+0.064
  },Polynomial.order.ConstFirst);
  /**
   * Mean longitude of the Sun minus the mean longitude of the Sun's perigee. Pretends
   * that the Sun orbits the Earth. Constants are entered in the form specified in 
   * IAU1980, poly takes time in Julian centuries from J2000, returns arcseconds.
   */
  private static Polynomial lpPoly=new Polynomial(new double[] {
      dmsToSeconds(357,31,39.804),+rdmsToSeconds(  99,359, 3, 1.224), -0.577,-0.012
  },Polynomial.order.ConstFirst);
  /**
   * Mean longitude of the Moon minus the mean longitude of the Moon's node. 
   * Constants are entered in the form specified in IAU1980, poly takes time in 
   * Julian centuries from J2000, returns arcseconds.
   */
  private static Polynomial FPoly=new Polynomial(new double[] {
      dmsToSeconds( 93,16,18.877),+rdmsToSeconds(1342, 82, 1, 3.137), -13.257,+0.011
  },Polynomial.order.ConstFirst);
  /**
   * Mean elongation of the Moon from the Sun. 
   * Constants are entered in the form specified in IAU1980, poly takes time in 
   * Julian centuries from J2000, returns arcseconds.
   */
  private static Polynomial DPoly=new Polynomial(new double[] {
      dmsToSeconds(297,51, 1.307),+rdmsToSeconds(1236,307, 6,41.328), -6.891,+0.019
  },Polynomial.order.ConstFirst);
  /**
   * Longitude of the mean ascending node of the lunar orbit on the ecliptic. 
   * Constants are entered in the form specified in IAU1980, poly takes time in 
   * Julian centuries from J2000, returns arcseconds.
   */
  private static Polynomial OmegaPoly=new Polynomial(new double[] {
      dmsToSeconds(125, 2,40.280),-rdmsToSeconds(   5,134, 8,10.539), +7.455,+0.008
  },Polynomial.order.ConstFirst);
  
  static {
    Load();
  }
  private static File SerialFilename() throws IOException {
	  return new File("Data/"+SerialFilenameCoreS+".serial.gz");
  }
  private static void LoadSerial() throws IOException, ClassNotFoundException {
    ObjectInputStream inf = new ObjectInputStream(new GZIPInputStream(new FileInputStream(SerialFilename())));
    LoadSerial(inf);
    inf.close(); 
  }
  private static void SaveSerial() throws IOException {
    ObjectOutputStream ouf = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(SerialFilename())));
    ouf.writeObject(argMult);
    ouf.writeObject(arg);
    ouf.writeObject(amp);
    ouf.close();
  }
  protected static void Load() {
	  try {
  	  if(!SerialFilename().canRead()) {
	      LoadText();
  	    SaveSerial();
	    }
      LoadSerial();
	  } catch (IOException E) {
	    throw new RuntimeException(E);
	  } catch (ClassNotFoundException E) {
	    throw new RuntimeException(E);
	  }
  }

  protected static void LoadSerial(ObjectInputStream inf) throws IOException, ClassNotFoundException {
    argMult=(byte[][])inf.readObject();
    arg=(byte[][])inf.readObject();
    amp=(int[][])inf.readObject();
  }
  /**
   * Mean longitude of the Moon minus the mean longitude of the Moon's perigee. 
   * @param T Time in Julian centuries from J2000 TDB
   * @return l(T) in radians
   */
  public static double l(Time T) {
    return sToRadians(lPoly.eval(T.get(TimeUnits.Centuries,TimeScale.TDB,TimeEpoch.J2000)));
  }
  /**
   * Mean longitude of the Sun minus the mean longitude of the Sun's perigee.  Pretends
   * that the Sun orbits the Earth.
   * @param T Time in Julian centuries from J2000 TDB
   * @return l'(T) in radians
   */
  public static double lp(Time T) {
    return sToRadians(lpPoly.eval(T.get(TimeUnits.Centuries,TimeScale.TDB,TimeEpoch.J2000)));
  }
  /**
   * Mean longitude of the Moon minus the mean longitude of the Moon's node. 
   * @param T Time in Julian centuries from J2000 TDB
   * @return F(T) in radians
   */
  public static double F(Time T) {
    return sToRadians(FPoly.eval(T.get(TimeUnits.Centuries,TimeScale.TDB,TimeEpoch.J2000)));
  }
  /**
   * Mean elongation of the Moon from the Sun. 
   * @param T Time in Julian centuries from J2000 TDB
   * @return D(T) in radians
   */
  public static double D(Time T) {
    return sToRadians(DPoly.eval(T.get(TimeUnits.Centuries,TimeScale.TDB,TimeEpoch.J2000)));
  }
  /**
   * Longitude of the mean ascending node of the lunar orbit on the ecliptic. 
   * @param T Time in Julian centuries from J2000 TDB
   * @return Omega(T) in radians
   */
  public static double Omega(Time T) {
    return sToRadians(OmegaPoly.eval(T.get(TimeUnits.Centuries,TimeScale.TDB,TimeEpoch.J2000)));
  }
  /**
   * 
   * @param T Time in Julian centuries from J2000 TDB
   * @return {l,l',F,D,Omega}(T), each in radians
   */
  private static double[] evalK(Time T) {
    return new double[] {l(T),lp(T),F(T),D(T),Omega(T)};
  }
  /**
   * Mean Obliquity of the ecliptic. This is the angle between the equator and the ecliptic. 
   * Constants are entered in the form specified in IAU1976, poly takes time in 
   * Julian centuries from J2000, returns arcseconds. This is part of the IAU1976 theory
   * but used only in IAU1980
   */
  private static Polynomial epsilonAPoly=new Polynomial(new double[] {
      dmsToSeconds( 23,26,21.448),-46.8150,-0.00059,+0.001813
  },Polynomial.order.ConstFirst);
  public static double epsilonA(double T) {
    return Math.toRadians(epsilonAPoly.eval(T)/3600.0);
  }
  public static double epsilonA(Time T) {
    return epsilonA(T.get(TimeUnits.Centuries,TimeScale.TDB,TimeEpoch.J2000));
  }
  private static double runSeries(double[] k, double T, int constAmp, int TAmp, TrigType f) {
    double result=0;
    for(int i=0;i<num_terms;i++) {
      if(amp[i][constAmp]!=0 || amp[i][TAmp]!=0) {
        double Arg=0;
        for(int j=0;j<arg[i].length;j++) {
          Arg+=argMult[i][j]*k[arg[i][j]];
        }
        double Amp=amp[i][constAmp]+((double)amp[i][TAmp]*T)/10.0;
        result+=Amp*f.eval(Arg);
      }
    }
    return sToRadians(result/10000.0);
  }
  private static double runSeries2(double[] k, double T, int constAmp, int TAmp, TrigType f) {
    double[] delta=new double[k.length];
    double result=0;
    for(int i=0;i<num_terms;i++) {
      if(amp[i][constAmp]!=0 || amp[i][TAmp]!=0) {
        double Arg=0;
        for(int j=0;j<arg[i].length;j++) {
          Arg+=argMult[i][j]*k[arg[i][j]];
        }
        double Amp=amp[i][constAmp]+((double)amp[i][TAmp]*T)/10.0;
        result+=Amp*f.eval(Arg);
      }
    }
    return sToRadians(result/10000.0);
  }
  /** Nutation in longitude, Delta Psi. 
   * @param T Time in Julian Centuries from J2000
   * @return Nutation in longitude in radians
   */
  private static double deltaPsi(double[] k, double T) {
    return runSeries(k,T,0,1,TrigType.SIN);
  }
  public static double deltaPsi(Time T) {
    return deltaPsi(evalK(T),T.get(TimeUnits.Centuries,TimeScale.TDB,TimeEpoch.J2000));
  }
  /** Nutation in Obliquity, Delta epslion. 
   * @param T Time in Julian Centuries from J2000
   * @return Nutation in obliquity in radians
   */
  private static double deltaEpsilon(double[] k, double T) {
    return runSeries(k,T,2,3,TrigType.COS);
  }
  public static double deltaEpsilon(Time T) {
    return deltaEpsilon(evalK(T),T.get(TimeUnits.Centuries,TimeScale.TDB,TimeEpoch.J2000));
  }
  protected static void LoadText() throws IOException {
    LineNumberReader inf=new LineNumberReader(new FileReader("Data/EarthRotIAU1980/nutate_data.txt"));
    argMult=new byte[num_terms][];
    arg=new byte[num_terms][];
    amp=new int[num_terms][4];
    String S=inf.readLine();
    int term=0;
    while(S!=null) {
      S=S.trim();
      if(S.length()>0 && S.charAt(0)!='!') {
        //Read terms coefficients
        String[] T=S.split("\\s+");
        byte[] argMult_row=new byte[num_args];
        byte[] arg_row=new byte[num_args];
        int num_args_row=0;
        for(byte i=0;i<num_args;i++) {
          if(!T[i+1].equals("0")) {
            argMult_row[num_args_row]=Byte.parseByte(T[i+1],10);
            arg_row[num_args_row]=i;
            num_args_row++;
          }
        }
        argMult[term]=new byte[num_args_row];
        System.arraycopy(argMult_row, 0, argMult[term], 0, num_args_row);
        arg[term]=new byte[num_args_row];
        System.arraycopy(arg_row, 0, arg[term], 0, num_args_row);
        
        //Read amplitudes
        for(int i=0;i<4;i++)amp[term][i]=Integer.parseInt(T[7+i]);
        term++;
      }
      S=inf.readLine();      
    }
  }
  
  protected static String SerialFilenameCoreS="EarthRotIAU1980/EarthRotNutationIAU1980";

  @Override
  public MathMatrix CalcRotation(Time JD) {
    double T=JD.get(TimeUnits.Centuries,TimeScale.TDB,TimeEpoch.J2000);
    double epsA=epsilonA(T);
    double[] k=evalK(JD);
    double deps=deltaEpsilon(k,T);
    double dpsi=deltaPsi(k,T);
    MathMatrix Reps=MathMatrix.Rot1(-epsA);
    MathMatrix Rpsi=MathMatrix.Rot3(dpsi);
    MathMatrix Rdeps=MathMatrix.Rot1(epsA+deps);
    return MathMatrix.mul(Reps,MathMatrix.mul(Rpsi,Rdeps));
  }
  public static void main(String[] args) {
    EarthRotPrecessionIAU1976 P=new EarthRotPrecessionIAU1976(MOD,J2000Ecl);
    EarthRotNutationIAU1980 N=new EarthRotNutationIAU1980(TOD,MOD);
    Time T1990=new Time(1990,1,1, 0,0,0,0,TimeUnits.Centuries,TimeScale.TDB,TimeEpoch.J2000);
    MathMatrix RTestOut1990=new MathMatrix(new double[][] {
      {+0.99999717,+0.00218346,+0.00094891},
      {-0.00218343,+0.99999762,-0.00003207},
      {-0.00094898,+0.00003000,+0.99999955}
    });
    System.out.println("Nutation in Longitude DeltaPsi, should be +11.836: "+String.format("%+7.3f",toDegrees(deltaPsi(T1990))*3600.0));
    System.out.println("Nutation in Obliquity DeltaEps, should be  +6.401: "+String.format("%+7.3f",toDegrees(deltaEpsilon(T1990))*3600.0));
    MathMatrix R1990=MathMatrix.mul(P.CalcRotation(T1990),N.CalcRotation(T1990));
    //Not good in the last place, but close enough. I think the Almanac matrix is wrong, or 
    //perhaps uses a different model. Off by about 8+-2 mas
    System.out.println("Test matrix: ");
    System.out.println(RTestOut1990.toString("%11.8f"));
    System.out.println("Calc matrix: ");
    System.out.println(R1990.T().toString("%11.8f"));
    System.out.println("Difference: ");
    System.out.println(MathMatrix.sub(R1990.T(), RTestOut1990).toString("%11.8f"));
    System.out.println("Difference: ");
    System.out.println(new AxisAngle(MathMatrix.mul(R1990, RTestOut1990)));
        
    MathVector TestVin =new MathVector(-0.373798720,-0.312714392,-0.873203428); //p2, 1990 almanac B41
    MathVector TestVout=new MathVector(-0.37530905 ,-0.31186948 ,-0.87285769 ); //p3, ibid
    System.out.println("Input vector: ");
    System.out.println(TestVin.toString("%12.9f"));
    System.out.println("Test output vector: ");
    System.out.println(TestVout.toString("%11.8f"));
    System.out.println("Calc output vector: ");
    System.out.println(R1990.T().transform(TestVin).toString("%11.8f"));
    System.out.println("Difference: ");
    System.out.println(MathVector.sub(TestVout, R1990.T().transform(TestVin)).toString("%11.8f"));

    ChartRecorder C=new ArrayListChartRecorder();

  }
}
