package edu.colorado.lasp.vsop;

import java.io.*;
import java.util.*;

import java.util.zip.*;
import static java.lang.Math.*;

/**
 * Class which calculates the solar system ephemeris using the VSOP87 theory.
 * <p>
 * This is original Java code implementing the Secular Variations of the Planetary
 * Orbits 1987 (<b>Variations S&eacute;culaires des Orbites Plan&eacute;taires</b> 1987, VSOP87).
 * It is all based on the tables and paper below:
 * <p>
 * P. Bretagnon and G. Francou, "Planetary theories in rectangular and spherical
 * variables. VSOP87 solutions", <i>Astronomy & Astrophysics</i> <b>202</b> (1988) 309-315.
 * <a href="http://articles.adsabs.harvard.edu/cgi-bin/nph-iarticle_query?1988A%26A...202..309B&data_type=PDF_HIGH&type=PRINTER&filetype=.pdf">(PDF 840KB)</a>
 * <p>
 * The tables are available from
 * <a href="ftp://ftp.imcce.fr/pub/ephem/planets/vsop87/">ftp.imcce.fr</a>
 * 
 * @author jeppesen
 */
public class SunSatVSOP87 {
  /** Path to the VSOP87 data files. This can be either an absolute path, or a
   * relative path, in which case it is relative to the current working directory.
   */
  public static String DataPath="Data/SunSatVSOP87/";
  /**
   * Rotation matrix from VSOP87 definition of J2000 ecliptic frame to Fifth
   * Fundamental Catalog (FK5) definition of J2000 equatorial frame. From
   * vsop87.txt, line 273-275
   */
  public static final double[][] MVSOPtoFK5=new double[][] {{+1.000000000000,+0.000000440360,-0.000000190919},
                                                            {-0.000000479966,+0.917482137087,-0.397776982902},
                                                            { 0.000000000000,+0.397776982902,+0.917482137087}};

  /** Rotates one or more vectors from VSOP87 ecliptic frame to FK5 equatorial
   * frame. Quoting vsop87.txt:
   * <blockquote>
   * <i>The rectangular coordinates of VSOP87A and VSOP87E defined in dynamical
   * ecliptic frame J2000 can be connected to the equatorial frame FK5 J2000
   * with the following rotation:</i>
   * <pre>
  [X]       [ +1.000000000000  +0.000000440360  -0.000000190919 ][X]
  [Y]     = [ -0.000000479966  +0.917482137087  -0.397776982902 ][Y]
  [Z] FK5   [  0.000000000000  +0.397776982902  +0.917482137087 ][Z] VSOP87A
     </pre></blockquote>
   * @param VVSOP Rectangular vector(s) in VSOP87 J2000 Ecliptic frame (natural
   *        frame of VSOP87A and E). Array should have 3N elements, where N is 
   *        some integer number of vectors to be independently transformed. This
   *        means you can pass in the results of VSOP87A or E, which are already
   *        a position and velocity vector in this form, and get a transformed
   *        pair of vectors in the same form.
   * @return A copy of the same vector(s), with its components expressed in FK5
   *         J2000 equatorial frame.
   */
  public static double[] convertToFK5(double[] VVSOP) {
    double[] VFK5=new double[VVSOP.length];
    int NN=VVSOP.length/3;
    for(int k=0;k<NN;k++) {
      for(int r=0;r<3;r++) {
        for(int c=0;c<3;c++) {
          VFK5[k*3+r]+=MVSOPtoFK5[r][c]*VVSOP[k*3+c];
        }
      }
    }
    return VFK5;
  }
  /**
   * Number of meters in an astronomical unit, as defined in DE200, the base
   * ephemeris that VSOP87 is derived from.
   */
  public static final double MinAU=149597870660.0;
  /**
   * Seconds in day times days in a kiloyear
   */
  public static final double Sinkyr=365250.0*86400.0;                                                       //
  /** Convert a vector or position/velocity pair to SI units. This multiplies
   * all distance units by the number of meters in an AU, and all time units
   * by that, then dividing by the number of seconds in a kiloyear.
   *
   * @param VVSOP Rectangular vector(s). If more than 3 elements, it is presumed
   *        that the input is pairs of vectors. The zeroth, second, etc every
   *        even-numbered vector is considered to be distance, and the first,
   *        third, etc every odd-numbered vector is considered to be speed.
   * @return A copy of the same vectors with their units converted from VSOP87
   *         natural units to SI units.
   */
  public static final double[] convertToSI(double[] VVSOP) {
    double[] result=new double[VVSOP.length];
    for(int i=0;i<VVSOP.length/6;i++) {
      for(int j=0;j<3;j++) {
        if(i*6+j  <VVSOP.length) result[i*6+j  ]=VVSOP[i*6+j  ]*MinAU;
        if(i*6+j+3<VVSOP.length) result[i*6+j+3]=VVSOP[i*6+j+3]*MinAU/Sinkyr;
      }
    }
    return result;
  }
  private static final char[] theories=new char[] {' ','A','B','C','D','E'};
  private static final String[][] suffix = {{"dum","mer", "ven", "emb", "mar", "jup", "sat", "ura", "nep"},
                                              {"dum","mer", "ven", "ear", "mar", "jup", "sat", "ura", "nep", "emb"},
                                              {"dum","mer", "ven", "ear", "mar", "jup", "sat", "ura", "nep"},
                                              {"dum","mer", "ven", "ear", "mar", "jup", "sat", "ura", "nep"},
                                              {"dum","mer", "ven", "ear", "mar", "jup", "sat", "ura", "nep"},
                                              {"dum","mer", "ven", "ear", "mar", "jup", "sat", "ura", "nep", "sun"}};
  private static final String[][] worldName={
/*_*/{"DUMMY","MERCURY","VENUS","EARTH-MOON","MARS","JUPITER","SATURN","URANUS","NEPTUNE"},
/*A*/{"DUMMY","MERCURY","VENUS","EARTH",     "MARS","JUPITER","SATURN","URANUS","NEPTUNE","EARTH-MOON"},
/*B*/{"DUMMY","MERCURY","VENUS","EARTH",     "MARS","JUPITER","SATURN","URANUS","NEPTUNE"},
/*C*/{"DUMMY","MERCURY","VENUS","EARTH",     "MARS","JUPITER","SATURN","URANUS","NEPTUNE"},
/*D*/{"DUMMY","MERCURY","VENUS","EARTH",     "MARS","JUPITER","SATURN","URANUS","NEPTUNE"},
/*E*/{"DUMMY","MERCURY","VENUS","EARTH",     "MARS","JUPITER","SATURN","URANUS","NEPTUNE","SUN"}
  };
  private static final int[] numWorlds=new int[] {
    worldName[0].length,worldName[1].length,worldName[2].length,worldName[3].length,worldName[4].length,worldName[5].length
  };
  //Number of variables in the theory file, indexed by theory number
  private static final int[] nVars=new int[] {6,3,3,3,3,3};
  private static final String[][] varLabels=new String[][] {
    {"dummy","a","lambda","k","h","q","p"},
    {"dummy","x","y","z","vx","vy","vz"},
    {"dummy","L","B","r","vL","vB","vR"},
    {"dummy","x","y","z","vx","vy","vz"},
    {"dummy","L","B","r","vL","vB","vR"},
    {"dummy","x","y","z","vx","vy","vz"}};
  //If it's not a distance, it's an angle
  private static final boolean[][] isDistance = new boolean[][]{
    {false,true, false,false,false,false,false},
    {false,true, true, true, true, true, true },
    {false,false,false,true, false,false,true },
    {false,true, true, true, true, true, true },
    {false,false,false,true, false,false,true },
    {false,true, true, true, true, true, true }
  };
  //If it's an angle, true means represent as 0 to 2*pi, false as -pi to pi
  private static final boolean[][] isWrapZero = new boolean[][]{
    {false,false,true, false,false,false,false},
    {false,false,false,false,false,false,false},
    {false,true, false,false,false,false,false},
    {false,false,false,false,false,false,false},
    {false,true, false,false,false,false,false},
    {false,false,false,false,false,false,false},
  };
  //If it's a rate
  private static final boolean[][] isRate = new boolean[][]{
    {false,false,false,false,false,false,false},
    {false,false,false,false,true, true, true },
    {false,false,false,false,true, true, true },
    {false,false,false,false,true, true, true },
    {false,false,false,false,true, true, true },
    {false,false,false,false,true, true, true }
  };
  //indexes
  //  Variable
  //  Time Power
  //  Term
  //  S,K,A,B,C index
  private static final int S = 0;
  private static final int K = 1;
  private static final int A = 2;
  private static final int B = 3;
  private static final int C = 4;
  /**
   * Mean longitude rates at J2000 TDB epoch, rad/kyr (365250 day)
   */
  private static final double[] N = {
        0,           //    dummy
    26087.9031415742,//    Mercury
    10213.2855462110,//    Venus
     6283.0758499914,//    Earth
     3340.6124266998,//    Mars
      529.6909650946,//    Jupiter
      213.2990954380,//    Saturn
       74.7815985673,//    Uranus
       38.1330356378,//    Neptune
    77713.7714681205,//    Moon D
    84334.6615813083,//    Moon F
    83286.9142695536,//    Moon l
    83997.0911355954,//    Moon Lm
  };
  /**
   * Mean longitudes at J2000 TDB epoch, rad
   */
  private static final double[] lambda = {
    0,            //    dummy
    4.40260884240,//    Mercury
    3.17614669689,//    Venus
    1.75347045953,//    Earth
    6.20347611291,//    Mars
    0.59954649739,//    Jupiter
    0.87401675650,//    Saturn
    5.48129387159,//    Uranus
    5.31188628676,//    Neptune
    5.19846674103,//    Moon D
    1.62790523337,//    Moon F
    2.35555589827,//    Moon l
    3.81034454697 //    Moon Lm
  };
  /**
   * Loads the theory test case for the body and theory for this object. These
   * test cases were published along with the  raw data as vsop87.chk, and this
   * method reads that file in its original form.
   * @return Array of test cases. Each test case is a 7-element array of numbers.
   * First number is time argument in kiloyears, next are given values as written
   * in the file. Speed variables are written as AU/day or rad/day, and these
   * are not converted to whatever/kyr, so your test program will have to do it.
   * @throws java.io.IOException
   */
  private double[][] loadTestCase() throws IOException {
    List<double[]> result = new ArrayList<double[]>();
    String infn = DataPath+"vsop87.chk";
    String header="VSOP87"+TheoryLetter+"  "+worldName[TheoryNumber][World]+" "; //So that Earth doesn't match Earth-Moon
    LineNumberReader inf = new LineNumberReader(new FileReader(infn));
    String line = inf.readLine().trim();
    String[] part;
    while (line != null) {
      line = line.trim();
      if (line.startsWith(header)) {
        part = line.split(" +");
        double[] vars = new double[7];
        vars[0] = VSOP87TimeArg(Double.parseDouble(part[2].substring(2)));
        line = inf.readLine().trim();
        part = line.split(" +");
        if(TheoryNumber==0) {
          vars[1] = Double.parseDouble(part[1]);
          vars[3] = Double.parseDouble(part[4]);
          vars[5] = Double.parseDouble(part[7]);
        } else {
          vars[1] = Double.parseDouble(part[1]);
          vars[2] = Double.parseDouble(part[4]);
          vars[3] = Double.parseDouble(part[7]);
        }
        line = inf.readLine().trim();
        part = line.split(" +");
        if(TheoryNumber==0) {
          vars[2] = Double.parseDouble(part[1]);
          vars[4] = Double.parseDouble(part[4]);
          vars[6] = Double.parseDouble(part[7]);
        } else {
          vars[4] = Double.parseDouble(part[1]);
          vars[5] = Double.parseDouble(part[4]);
          vars[6] = Double.parseDouble(part[7]);
        }
        result.add(vars);
      }
      line = inf.readLine();
    }
    return result.toArray(new double[][] {});
  }

  /**
   * Calculates the VSOP87 Time Argument. This is the number of
   * kiloyears (kyr, 365250 julian days) from J2000
   * @param JD TDB Julian date to convert
   * @return Time Argument
   */
  public static double VSOP87TimeArg(double JD) {
    return (JD - 2451545.0) / 365250.0;
  }

  private static double calcPhidot(byte[] a) {
    double result = 0;
    for (int i = 1; i <= 12; i++) {
      if (a[i] > 0) {
        result += a[i] * N[i];
      }
    }
    return result;
  }

  private static double calcPhi(byte[] a, double T) {
    double result = 0;
    for (int i = 1; i <= 12; i++) {
      if (a[i] > 0) {
        result += a[i] * (lambda[i] + T * N[i]);
      }
    }
    result=result % (2 * PI);
    if(result<0) result+=2*PI;
    return result;
  }
  private double[][][][] terms;
  private String Suffix;
  private char TheoryLetter;
  private int TheoryNumber,World;
  //indexes
  //  Variable
  //  Time Power
  //  Term
  //  a index
  private byte[][][][] a;
  private static final boolean useSK=false;

  private double calcVariableABC(int var, double T) {
    double result = 0;
    for (int alpha = 0; alpha < terms[var].length; alpha++) {
      double Tcoeff = pow(T, alpha);
      if(terms[var][alpha]!=null) {
        for (int term = 1; term < terms[var][alpha].length; term++) {
          result += Tcoeff * terms[var][alpha][term][A] * cos(terms[var][alpha][term][B] + T * terms[var][alpha][term][C]);
        }
      }
    }
    return result;
  }
  private double[] calcVariableABCdot(int var, double T) {
    double result = 0;
    double resultdot = 0;
    for (int alpha = 0; alpha < terms[var].length; alpha++) {
      double Tcoeff = pow(T, alpha);
      double Tcoeffdot = alpha * pow((T == 0) ? 1 : T, alpha - 1);
      if(terms[var][alpha]!=null) {
        for (int term = 1; term < terms[var][alpha].length; term++) {
          result += Tcoeff * terms[var][alpha][term][A] * cos(terms[var][alpha][term][B] + T * terms[var][alpha][term][C]);
          resultdot += Tcoeffdot * terms[var][alpha][term][A] * cos(terms[var][alpha][term][B] + T * terms[var][alpha][term][C]) - Tcoeff * terms[var][alpha][term][A] * terms[var][alpha][term][C] * sin(terms[var][alpha][term][B] + T * terms[var][alpha][term][C]);
        }
      }
    }
    return new double[]{result, resultdot};
  }

  public void dumpIDL(String oufn) throws IOException {
    PrintWriter ouf=new PrintWriter(new FileWriter(oufn));
    int maxTerms=0;
    for(int var=1;var<=3;var++) {
      for(int alpha=0;alpha<5;alpha++) {
        if(terms[var][alpha].length-1>maxTerms) maxTerms=terms[var][alpha].length-1;
      }
    }
    ouf.println("function buildTheory_"+suffix);
    ouf.printf("  theory=dblarr(3,6,%d,3)\n",maxTerms);
    for(int var=1;var<=3;var++) {
      for(int alpha=0;alpha<5;alpha++) {
        for(int term=1;term<=terms[var][alpha].length-1;term++) {
          ouf.printf("  theory[%d,%d,%4d,*]=[%18.11fd,%18.11fd,%18.11fd]\n",var-1,alpha,term-1,terms[var][alpha][term][A],terms[var][alpha][term][B],terms[var][alpha][term][C]);
        }
      }
    }
    ouf.println("  return,theory");
    ouf.println("end");
    ouf.close();
  }

  private double[] checkSKtoABCterm(int var, int alpha, int term) {
    double So=terms[var][alpha][term][S];
    double Ko=terms[var][alpha][term][K];
    double Ao=terms[var][alpha][term][A];
    double Bo=terms[var][alpha][term][B];
    double Co=terms[var][alpha][term][C];
    double Ac=sqrt(So*So+Ko*Ko);
    double sinbeta=So/-Ac;
    double cosbeta=Ko/Ac;
    double betao=atan2(sinbeta,cosbeta);
    double Bc=betao,Cc=0;
    for(int i=0;i<12;i++) {
      Bc+=a[var][alpha][term][i]*lambda[i];
      Cc+=a[var][alpha][term][i]*N[i];
    }
    Bc%=2*PI; if(Bc<0) Bc+=2*PI;
    double Ad=Ao-Ac;
    double Bd=Bo-Bc;
    double Cd=Co-Cc;
    if(abs(Ad)>1e-11 || abs(Bd)>1e-11 || abs(Cd)>1e-11) {
      System.out.printf(" %d%d%d%d%5d",TheoryNumber,World+1,var+1,alpha,term+1);
      for(int i=0;i<12;i++) System.out.printf("%3d",a[var][alpha][term][i]);System.out.println();
      System.out.printf("A: obs=%20.11f calc=%20.11f diff=%16.11f\n",Ao,Ac,Ad);
      System.out.printf("B: obs=%20.11f calc=%20.11f diff=%16.11f\n",Bo,Bc,Bd);
      System.out.printf("C: obs=%20.11f calc=%20.11f diff=%16.11f\n",Co,Cc,Cd);
    }
    return new double[] {Ad,Bd,Cd};
  }

  private void checkSKtoABCalpha(int var,int alpha) {
    for(int term=0;term<terms[var][alpha].length;term++) {
      checkSKtoABCterm(var,alpha,term);
    }
  }

  private void checkSKtoABCvar(int var) {
    for(int alpha=0;alpha<terms[var].length;alpha++) {
      checkSKtoABCalpha(var,alpha);
    }
  }

  private void checkSKtoABC() {
    for(int var=0;var<terms.length;var++) {
      checkSKtoABCvar(var);
    }
  }

  private double calcVariableSK(int var, double T) {
    double result = 0;
    for (int alpha = 0; alpha < terms[var].length; alpha++) {
      double Tcoeff = pow(T, alpha);
      for (int term = 0; term < terms[var][alpha].length; term++) {
        double phi = calcPhi(a[var][alpha][term], T);
        result += Tcoeff * (terms[var][alpha][term][S] * sin(phi) + terms[var][alpha][term][K] * cos(phi));
      }
    }
    return result;
  }

  private double[] calcVariableSKdot(int var, double T) {
    double result = 0;
    double resultdot = 0;
    for (int alpha = 0; alpha < terms[var].length; alpha++) {
      double Tcoeff = pow(T, alpha);
      double Tcoeffdot = alpha * pow(T, alpha - 1);
      for (int term = 0; term < terms[var][alpha].length; term++) {
        double phi = calcPhi(a[var][alpha][term], T);
        double phidot = calcPhidot(a[var][alpha][term]);
        result += Tcoeff * (terms[var][alpha][term][S] * sin(phi) + terms[var][alpha][term][K] * cos(phi));
        resultdot += Tcoeffdot * (terms[var][alpha][term][S] * sin(phi) + terms[var][alpha][term][K] * cos(phi)) + Tcoeff * (-terms[var][alpha][term][K] * sin(phi) + terms[var][alpha][term][S] * cos(phi)) * phidot;
      }
    }
    return new double[]{result, resultdot};
  }

  private void loadSerial(ObjectInputStream inf) throws IOException, ClassNotFoundException {
    a = (byte[][][][]) inf.readObject();
    terms = (double[][][][]) inf.readObject();
  }
  private File serialFilename() throws IOException {
	  return new File(DataPath+serialFilenameCore()+".serial.gz");
  }
  private void loadSerial() throws IOException, ClassNotFoundException {
    ObjectInputStream inf = new ObjectInputStream(new GZIPInputStream(new FileInputStream(serialFilename())));
    loadSerial(inf);
    inf.close();
  }
  private void saveSerial() throws IOException {
    ObjectOutputStream ouf = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(serialFilename())));
    saveSerial(ouf);
    ouf.close();
  }
  private void load() {
	  try {
  	  if(!serialFilename().canRead()) {
	      loadText();
  	    saveSerial();
	    }
      loadSerial();
	  } catch (IOException E) {
	    throw new RuntimeException(E);
	  } catch (ClassNotFoundException E) {
	    throw new RuntimeException(E);
	  }
  }
  private void loadText() throws IOException {
    String infn = DataPath+serialFilenameCore();
    //alpha could range from 0 to 5
    a = new byte[nVars[TheoryNumber]+1][6][][];
    terms = new double[nVars[TheoryNumber]+1][6][][];

    LineNumberReader inf = new LineNumberReader(new FileReader(infn));
    String line = inf.readLine();

    int maxa = 0;
    int mina = 0;
    while (line != null) {
      char firstchar = line.charAt(1);
      if (firstchar >= '0' & firstchar <= '9') {
        String Id = line.substring(1, 5);
        int var = Integer.parseInt(Id.substring(2, 3));
        int alpha = Integer.parseInt(Id.substring(3, 4));
        int term=Integer.parseInt(line.substring(6,10).trim());
        for (int i = 1; i < 12; i++) {
          String Sq = line.substring(7 + 3 * i, 10 + 3 * i);
          Sq = Sq.trim();
          byte thisa=Byte.parseByte(Sq);
          a[var][alpha][term][i] = thisa;
          if (thisa > maxa) {
            maxa = thisa;
          }
          if (thisa < mina) {
            mina = thisa;
          }
        }
        double[] newTerm = new double[]{Double.parseDouble(line.substring(46, 61).trim()), Double.parseDouble(line.substring(61, 79).trim()), Double.parseDouble(line.substring(79, 97).trim()), Double.parseDouble(line.substring(97, 111).trim()), Double.parseDouble(line.substring(111, 131).trim())};
        terms[var][alpha][term]=newTerm;
      } else if (firstchar=='V') {
        line=line.trim();
        String[] part=line.split(" +");
        int var=Integer.parseInt(part[5]);
        int alpha=part[7].charAt(4)-'0';
        int nTerms=Integer.parseInt(part[8]);
        a[var][alpha]=new byte[nTerms+1][13];
        terms[var][alpha]=new double[nTerms+1][5];
      }
      line = inf.readLine();
    }
  }

  private void saveSerial(ObjectOutputStream ouf) throws IOException {
    ouf.writeObject(a);
    ouf.writeObject(terms);
  }

  private String serialFilenameCore() {
    String result= "VSOP87";
    if(TheoryLetter!=' ') result+=TheoryLetter;
    result=result+ "." + Suffix;
    return result;
  }

  private void setTheoryNumber() {
    if (TheoryLetter==' ') {
      TheoryNumber = 0;
    } else {
      TheoryNumber = TheoryLetter - 'A' + 1;
    }
  }
  private boolean searchString(String[] search, String target) {
    for(World=0;World<search.length;World++) {
      if(search[World].equalsIgnoreCase(target)) return true;
    }
    return false;
  }
  /**
   * Create an instance of the theory, using a theory letter and world name or abbreviation.
   * Planet names and abbreviations do not change from theory to theory. Not all bodies
   * are supported by all theories.
   * <p>
   * In the process of loading the theory, it checks if there is a compressed
   * serialized version of the theory. If there is, it just loads that file.
   * If not, it reads the text version of the theory and writes the compressed
   * serialized version for next time.
   * @param LTheoryLetter A to E, or space ' ' for the no-letter theory
   * @param LPlanet Name of planet or first three letter abbreviation of the name
   * of the planet. Earth-Moon Barycenter is abbreviated EMB.
   */
  public SunSatVSOP87(char LTheoryLetter, String LPlanet) {
    super();
    TheoryLetter = LTheoryLetter;
    setTheoryNumber();
    if(!searchString(suffix[TheoryNumber],LPlanet) && !searchString(worldName[TheoryNumber],LPlanet)) {
      throw new IllegalArgumentException("Unrecognized planet name "+LPlanet);
    }
    Suffix=suffix[TheoryNumber][World];
    load();
  }
  /**
   * Evaluate the theory at the given time
   * @param T Time in kiloyears from theory epoch
   * @return Theory variables for this theory, body, and time. Theory variables
   * can are as follows:
   * <table border=1>
   * <tr><th>Theory Letter</th><th>0</th><th>1</th><th>2</th><th>3</th><th>4</th><th>5</th></tr>
   * <tr><th>No Letter</th><td><i>a</i> (semimajor axis, AU)</td><td><i>&lambda;</i> (mean longitude, rad)</td><td><i>k</i></td><td><i>h</i></td><td><i>q</i></td><td><i>p</i></td></tr>
   * <tr><th>A</th><td><i>x</i></td><td><i>y</i></td><td><i>z</i></td><td><i>dx/dt</i></td><td><i>dy/dt</i></td><td><i>dz/dt</i></td></tr>
   * <tr><th>B</th><td><i>L</i> (longitude)</td><td><i>B</i> (latitude)</td><td><i>r</i> (radial distance)</td><td><i>dL/dt</i></td><td><i>dB/dt</i></td><td><i>dR/dt</i></td></tr>
   * <tr><th>C</th><td><i>x</i></td><td><i>y</i></td><td><i>z</i></td><td><i>dx/dt</i></td><td><i>dy/dt</i></td><td><i>dz/dt</i></td></tr>
   * <tr><th>D</th><td><i>L</i> (longitude)</td><td><i>B</i> (latitude)</td><td><i>r</i> (radial distance)</td><td><i>dL/dt</i></td><td><i>dB/dt</i></td><td><i>dR/dt</i></td></tr>
   * <tr><th>E</th><td><i>x</i></td><td><i>y</i></td><td><i>z</i></td><td><i>dx/dt</i></td><td><i>dy/dt</i></td><td><i>dz/dt</i></td></tr>
   * </table>
   * 
   * The meanings of the orbital elements for the no-letter theory are defined 
   * in the VSOP87 paper. All units are AU and AU/kyr for distance coordinates,
   * rad and rad/kyr for angular coordinates. Certain orbital elements are
   * unitless. Rates are not calculated for orbital elements.
   */
  public double[] calcVariables(double T) {
    double[] Parameters = new double[7];
    Parameters[0]=T;
    for (int var = 1; var <= terms.length-1; var++) {
      if (terms.length-1 == 3) {
        double[] thisParameter;
        if(useSK) {
          thisParameter=calcVariableSKdot(var,T);
        } else {
          thisParameter = calcVariableABCdot(var, T);
        }
        Parameters[var]   = thisParameter[0];
        Parameters[var+3] = thisParameter[1];
      } else {
        if(useSK) {
          Parameters[var] = calcVariableSK(var, T);
        } else {
          Parameters[var] = calcVariableABC(var, T);
        }
      }
    }
    return Parameters;
  }

  private void checkVariables(double[][] theoryCheck, int t) {
    double[] obs=theoryCheck[t];
    double[] calc=calcVariables(obs[0]);
    for(int var=1;var<=6;var++) {
      double OO=obs[var];
      double CC=calc[var];
      if(!isDistance[TheoryNumber][var] && !isRate[TheoryNumber][var]) {
        CC = IEEEremainder(CC,2 * PI);
        if(isWrapZero[TheoryNumber][var] && CC<0) CC+=2*PI;
      }
      String units=isDistance[TheoryNumber][var]?"au":"rad";
      if(isRate[TheoryNumber][var]) {
        units+="/day";
        CC/=365250.0;
      }
      System.out.printf("%6s: obs %16.10f %-7s  calc %16.10f %-7s  diff %16.10f %-7s\n",varLabels[TheoryNumber][var],OO,units,CC,units,OO-CC,units);
    }
  }
  public static void main(String[] args) throws IOException {
    int j=0;
    for(char c:theories) {
      for(int w=1;w<numWorlds[0];w++) {
        SunSatVSOP87 V = new SunSatVSOP87(c,worldName[j][w]);
        double[][] testCase=V.loadTestCase();
//        V.checkSKtoABC();
        for (int i = 0; i < testCase.length; i++) {
          System.out.printf("%-9s JD%9.1f\n",worldName[1][w],testCase[i][0]*365250.0+2451545.0);
          V.checkVariables(testCase, i);
        }
      }
      j++;
    }
  }
}
