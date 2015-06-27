/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kwansystems.space.ephemeris.sun;

import java.io.*;
import java.util.*;
import org.kwansystems.tools.time.*;
import org.kwansystems.space.ephemeris.*;

import static java.lang.Math.*;
import static org.kwansystems.tools.time.TimeUnits.*;
import static org.kwansystems.tools.time.TimeScale.*;
import static org.kwansystems.tools.time.TimeEpoch.*;
import static org.kwansystems.space.Constants.*;
import org.kwansystems.tools.vector.MathState;
import org.kwansystems.tools.vector.MathVector;
import org.kwansystems.tools.rotation.MathMatrix;

/**
 * This has the guts of reading the theory data files and
 * calculating the variables from the series. Descendants
 * of this class will implement the theories.
 * @author jeppesen
 */
public abstract class SunSatVSOP87 extends TableEphemeris {
  public static final MathMatrix VSOPtoFK5=new MathMatrix(new double[][] {{+1.000000000000,+0.000000440360,-0.000000190919},
                                                                          {-0.000000479966,+0.917482137087,-0.397776982902},
                                                                          { 0.000000000000,+0.397776982902,+0.917482137087}});

  protected static final String[][] suffix = {{"dum","mer", "ven", "emb", "mar", "jup", "sat", "ura", "nep"},
                                              {"dum","mer", "ven", "ear", "mar", "jup", "sat", "ura", "nep", "emb"},
                                              {"dum","mer", "ven", "ear", "mar", "jup", "sat", "ura", "nep"},
                                              {"dum","mer", "ven", "ear", "mar", "jup", "sat", "ura", "nep"},
                                              {"dum","mer", "ven", "ear", "mar", "jup", "sat", "ura", "nep"},
                                              {"dum","mer", "ven", "ear", "mar", "jup", "sat", "ura", "nep", "sun"}};
  protected static final String[][] worldName={
/*Z*/{"DUMMY","MERCURY","VENUS","EARTH-MOON","MARS","JUPITER","SATURN","URANUS","NEPTUNE"},
/*A*/{"DUMMY","MERCURY","VENUS","EARTH",     "MARS","JUPITER","SATURN","URANUS","NEPTUNE","EARTH-MOON"},
/*B*/{"DUMMY","MERCURY","VENUS","EARTH",     "MARS","JUPITER","SATURN","URANUS","NEPTUNE"},
/*C*/{"DUMMY","MERCURY","VENUS","EARTH",     "MARS","JUPITER","SATURN","URANUS","NEPTUNE"},
/*D*/{"DUMMY","MERCURY","VENUS","EARTH",     "MARS","JUPITER","SATURN","URANUS","NEPTUNE"},
/*E*/{"DUMMY","MERCURY","VENUS","EARTH",     "MARS","JUPITER","SATURN","URANUS","NEPTUNE","SUN"}
  };
  //Number of variables in the theory file, indexed by theory number
  public static final int[] nVars=new int[] {6,3,3,3,3,3};
  public static final String[][] varLabels=new String[][] {
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
  protected static final int S = 0;
  protected static final int K = 1;
  protected static final int A = 2;
  protected static final int B = 3;
  protected static final int C = 4;
  /**
   * Mean longitude rates at J2000 TDB epoch, rad/kyr (365250 day)
   */
  protected static final double[] N = {
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
  protected static final double[] lambda = {
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

  public double[][] LoadTestCase() throws IOException {
    List<double[]> result = new ArrayList<double[]>();
    String infn = "Data/SunSatVSOP87/vsop87.chk";
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
   * @param TT Time to convert
   * @return Time Argument
   */
  public static double VSOP87TimeArg(Time TT) {
    return TT.get(Kiloyears, TDB, J2000);
  }

  /**
   * Calculates the VSOP87 Time Argument. This is the number of
   * kiloyears (kyr, 365250 julian days) from J2000
   * @param JD Julian date to convert
   * @return Time Argument
   */
  public static double VSOP87TimeArg(double JD) {
    return (JD - J2000.JDEpoch()) / 365250.0;
  }

  protected static double CalcPhidot(byte[] a) {
    double result = 0;
    for (int i = 1; i <= 12; i++) {
      if (a[i] > 0) {
        result += a[i] * N[i];
      }
    }
    return result;
  }

  protected static double CalcPhi(byte[] a, double T) {
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
  protected double[][][][] terms;
  public String Suffix;
  private char TheoryLetter;
  private int TheoryNumber,World;
  private double prec;
  //indexes
  //  Variable
  //  Time Power
  //  Term
  //  a index
  protected byte[][][][] a;
  private static final boolean useSK=false;

  protected double CalcVariableABC(int var, double T) {
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

  protected double[] CalcVariableABCdot(int var, double T) {
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
    ouf.println("function buildTheoryEar");
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

  public double[] checkSKtoABCterm(int var, int alpha, int term) {
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

  public void checkSKtoABCalpha(int var,int alpha) {
    for(int term=0;term<terms[var][alpha].length;term++) {
      checkSKtoABCterm(var,alpha,term);
    }
  }

  public void checkSKtoABCvar(int var) {
    for(int alpha=0;alpha<terms[var].length;alpha++) {
      checkSKtoABCalpha(var,alpha);
    }
  }

  public void checkSKtoABC() {
    for(int var=0;var<terms.length;var++) {
      checkSKtoABCvar(var);
    }
  }

  protected double CalcVariableSK(int var, double T) {
    double result = 0;
    for (int alpha = 0; alpha < terms[var].length; alpha++) {
      double Tcoeff = pow(T, alpha);
      for (int term = 0; term < terms[var][alpha].length; term++) {
        double phi = CalcPhi(a[var][alpha][term], T);
        result += Tcoeff * (terms[var][alpha][term][S] * sin(phi) + terms[var][alpha][term][K] * cos(phi));
      }
    }
    return result;
  }

  protected double[] CalcVariableSKdot(int var, double T) {
    double result = 0;
    double resultdot = 0;
    for (int alpha = 0; alpha < terms[var].length; alpha++) {
      double Tcoeff = pow(T, alpha);
      double Tcoeffdot = alpha * pow(T, alpha - 1);
      for (int term = 0; term < terms[var][alpha].length; term++) {
        double phi = CalcPhi(a[var][alpha][term], T);
        double phidot = CalcPhidot(a[var][alpha][term]);
        result += Tcoeff * (terms[var][alpha][term][S] * sin(phi) + terms[var][alpha][term][K] * cos(phi));
        resultdot += Tcoeffdot * (terms[var][alpha][term][S] * sin(phi) + terms[var][alpha][term][K] * cos(phi)) + Tcoeff * (-terms[var][alpha][term][K] * sin(phi) + terms[var][alpha][term][S] * cos(phi)) * phidot;
      }
    }
    return new double[]{result, resultdot};
  }

  protected void LoadSerial(ObjectInputStream inf) throws IOException, ClassNotFoundException {
    a = (byte[][][][]) inf.readObject();
    terms = (double[][][][]) inf.readObject();
  }

  public void LoadText() throws IOException {
    String infn = "Data/"+SerialFilenameCore();
    //alpha could range from 0 to 5
    a = new byte[nVars[TheoryNumber]+1][6][][];
    terms = new double[nVars[TheoryNumber]+1][6][][];

    LineNumberReader inf = new LineNumberReader(new FileReader(infn));
    String line = inf.readLine();

    int termsRetained = 0;
    int termsTruncated = 0;
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

  protected void SaveSerial(ObjectOutputStream ouf) throws IOException {
    ouf.writeObject(a);
    ouf.writeObject(terms);
  }

  protected String SerialFilenameCore() {
    String result= "SunSatVSOP87/VSOP87";
    if(TheoryLetter!=' ') result+=TheoryLetter;
    result=result+ "." + Suffix;
    return result;
  }

  private boolean shouldRetain(double[] LTerm, int Lvar, int Lalpha) {
    /*    double thisprec=prec*(isDistance[Ltheory][Lvar]?precDistance[Lbody]:1);
    return pow(precT[Lbody],Lalpha)*(abs(LTerm.S)+abs(LTerm.K))>thisprec;
     */
    return true;
  }

  protected SunSatVSOP87(char LTheoryLetter, String LSuffix, double Lprec) {
    super();
    TheoryLetter = LTheoryLetter;
    setTheoryNumber();
    Suffix = LSuffix;
    World=0;
    while(!Suffix.equals(suffix[TheoryNumber][World])) World++;
    prec = Lprec;
    Load();
  }
  private void setTheoryNumber() {
    if (TheoryLetter==' ') {
      TheoryNumber = 0;
    } else {
      TheoryNumber = TheoryLetter - 'A' + 1;
    }
  }

  protected SunSatVSOP87(char LTheoryLetter, int LWorld, double Lprec) {
    super();
    TheoryLetter = LTheoryLetter;
    setTheoryNumber();
    World=LWorld;
    Suffix = suffix[TheoryNumber][World];
    prec = Lprec;
    Load();
  }

  public MathVector CalcPos(Time TT) {
    return defaultCalcPos(TT);
  }

  /**
   * Generate rectangular coordinates. This should perform the theory
   * transformation from the given values of the variables to rectangular
   * coordinates, in the theory natural units, AU and kyr.
   * @param TT Time at which to calculate the state
   * @return rectangular coordinates for position and velocity
   */
  public MathState CalcState(Time TT) {
    double[] Parameters = new double[terms.length];
    double T = VSOP87TimeArg(TT);
    for (int var = 0; var < Parameters.length; var++) {
      Parameters[var] = CalcVariableABC(var, T);
    }
    MathState result = CalcState(Parameters);
    //Convert AU to m
    result = (MathState) result.mul(MPerAU);
    //Convert distance/kyr to distance/s
    result = new MathState(result.R(), result.V().div(365250.0 * 86400.0));
    return result;
  }

  public MathState CalcState(double JD) {
    return CalcState(CalcVariables(JD));
  }

  public MathVector CalcVel(Time TT) {
    return defaultCalcVel(TT);
  }

  public double[] CalcVariables(double T) {
    double[] Parameters = new double[7];
    Parameters[0]=T;
    for (int var = 1; var <= terms.length-1; var++) {
      if (terms.length-1 == 3) {
        double[] thisParameter;
        if(useSK) {
          thisParameter=CalcVariableSKdot(var,T);
        } else {
          thisParameter = CalcVariableABCdot(var, T);
        }
        Parameters[var]   = thisParameter[0];
        Parameters[var+3] = thisParameter[1];
      } else {
        if(useSK) {
          Parameters[var] = CalcVariableSK(var, T);
        } else {
          Parameters[var] = CalcVariableABC(var, T);
        }
      }
    }
    return Parameters;
  }

  public void DisplayVariables(double JD) {
    DisplayVariables(CalcVariables(JD));
  }

  public abstract MathState CalcState(double[] Parameters);

  public void DisplayVariables(double[] Parameters) {
    for(int i=0;i<6;i++) {
      double P=Parameters[i];
      if(!isDistance[TheoryNumber][i] && !isRate[TheoryNumber][i]) {
        P = P % (2 * PI);
        if (P < 0) P += 2 * PI;
      }
      String units=isDistance[TheoryNumber][i]?"au":"rad";
      if(isRate[TheoryNumber][i]) {
        units+="/day";
      }
      System.out.print(String.format("%6s: %16.10f %-7s    ",varLabels[TheoryNumber][i],P,units));
      if(i%3==2) System.out.println();
    }
  }
  public void CheckVariables(double[][] theoryCheck, int t) {
    double[] obs=theoryCheck[t];
    double[] calc=CalcVariables(obs[0]);
    for(int var=1;var<=6;var++) {
      double O=obs[var];
      double C=calc[var];
      if(!isDistance[TheoryNumber][var] && !isRate[TheoryNumber][var]) {
        C = IEEEremainder(C,2 * PI);
        if(isWrapZero[TheoryNumber][var] && C<0) C+=2*PI;
      }
      String units=isDistance[TheoryNumber][var]?"au":"rad";
      if(isRate[TheoryNumber][var]) {
        units+="/day";
        C/=365250.0;
      }
      System.out.printf("%6s: obs %16.10f %-7s  calc %16.10f %-7s  diff %16.10f %-7s\n",varLabels[TheoryNumber][var],O,units,C,units,O-C,units);
    }
  }
}
