package org.kwansystems.tools.time;

import java.io.*;
import java.text.*;
import java.util.*;

import static org.kwansystems.tools.Scalar.*;
import static java.lang.Math.*;

/**
 * Enhanced time object.
 * <p>
 * There are two kinds of time, both measured in the same units.
 * One is Epoch time, or calendar time. It answers the question "What time is it?"
 * One is Interval time, or stopwatch time. It answers the question "How long?"
 * This object represents Epoch time. It is fundamentally a 64-bit count of 
 * microseconds since the GPS epoch, 1980 Jan 06 00:00:00UTC, and includes functions to treat
 * time as measured in various foreign time systems. It can output as a plain double the
 * number of time units since the epoch, which makes working with Interval time a matter
 * of simple subtraction.
 * <p>
 * A time <i>system</i> is a combination of three things:
 * <ul>
 * <li>A time <i>Unit</i>, some multiple of an interval second.</li>
 * <li>A time <i>Scale</i>, defining a possibly time-varying relationship with the system clock, which is assumed to run on UTC.</li>
 * <li>A time <i>Epoch</i>, defining what moment in time is labelled zero.</li>
 * </ul>
 * The <i>native</i> time system is the GPS system. Its fundamental units are microseconds,
 * its scale is GPS, and its epoch is 1980 Jan 06 00:00:00UTC. This is the origin of the 
 * GPS timescale of weeks and seconds as used on the GPS system. GPS time is forced to 
 * follow UTC with nanosecond precision, except it has no leap seconds. Since its ticks
 * are based on the SI second on the geoid, it can easily be extended in both directions indefinitely.
 * <p>
 * <i>Foreign</i> time systems are defined by some combination of units, scale, and epoch 
 * which is different from the native system. For example, the Julian Day number system is
 * defined by units of days of 86400 SI seconds, any scale, and an epoch -4712 Jan 01 12:00:00.
 * The Java system is defined with units of milliseconds, the UTC scale (Matches GPS at the
 * GPS epoch, but has leap seconds) and an epoch of 1970 Jan 01 00:00:00. The parts of
 * a time system are orthogonal, however some systems are much more heavily used than 
 * others, and some just don't make sense. 
 * <p>
 * The Time object has been decoupled from the Date object. As a result, there is considerable
 * freedom to choose the native time scale. The whole reason the Time object was decoupled from
 * the Date object is that it is extremely inconvenient to have the native time scale have leap
 * seconds. UTC time does not keep track of leap seconds, so leap seconds have the
 * effect of moving the Java epoch. Quoting the POSIX warning:
 * <blockquote><i>
 *    Therefore, it is inappropriate to require that a time represented
 *    as seconds since the Epoch precisely represent the number of seconds
 *    between the referenced time and the Epoch.
 * </i></blockquote>
 * The other time scales do not have this problem. GPS microseconds was chosen because it
 * matches the convention used at LASP, and it's epoch is neither in the era of rubber 
 * seconds, nor exactly on a leap second.
 * <p>
 * As time marches on, UTC continually drifts behind the other scales, because
 * only it adds leap seconds.
 * <p>
 * <pre>
 *   U             G             T             TT
 *   T             P             A             DD
 *   C             S             I             TB
 *   |<-DeltaGPS2->|<-DeltaGPS1->|<--DeltaTA-->||--->Higher clock numbers
 *   |  =GPS-UTC      =TAI-GPS   |   =TDT-TAI  ||
 *   |<---------DeltaAT--------->|            >||&lt;DeltaR (Positive on this graph)
 *               =TAI-UTC                         =TDB-TDT
 * </pre>
 * All time scales are a simple count of number of units elapsed since the epoch.
 * Calendar form is just a convenience for human users, but is provided. The
 * native scale is GPS microseconds, and all other definers are relative to these.
 * The relation between a time system and the native GPS system is as follows:
 * <p>
 * <pre>Tforeign=(Tgps+Tscale_ofs(Tgps)+Tepoch)/Factor</pre>
 * <p>
 * where <ul>
 * <li><tt>Tforeign</tt> is the time reading in the foreign system,</li>
 * <li><tt>Tepoch</tt> is the number of microseconds between the foreign epoch and the GPS epoch</li>
 * <li><tt>Tgps</tt> is the time in the native GPS system</li>
 * <li><tt>Tscale_ofs(Tgps)</tt>is the foreign time scale offset, in microseconds, possibly as 
 * a function of <tt>Tgps</tt></li>
 * <li><tt>Factor</tt> is number of microseconds in one foreign time unit</li>
 * </ul> 
 * <p>
 * So, the inverse transformation is as such:
 * <p>
 * <pre>Tforeign*Factor=Tgps+Tscale_ofs(Tgps)+Tepoch</pre><br>
 * <pre>Tforeign*Factor-Tepoch-Tscale_ofs(Tgps)=Tjava</pre><br>
 * <p>
 * Thanks to the wonders of the new Java enum, a lot of this can be encoded in 
 * the enum objects themselves, further generalizing the code and making it easier
 * to add more definers.
 * <ul>
 * <li><tt>Tepoch</tt> is TimeEpoch.EpochOffset</li>
 * <li><tt>Tscale_ofs(Tgps)</tt>is TimeScale.ScaleOffset(long Tgps)</li>
 * <li><tt>Factor</tt> is TimeUnits.Factor</li>
 * </ul> 
 */
public class Time implements Serializable, Comparable<Time>, Cloneable {
  private static final long serialVersionUID = -7691401018296279480L;
  public static boolean TimeDebug=true;
  /** Count of microseconds since the GPS epoch. This is the core of the time object, 
   * all else surrounding it is just to help interpret it. Range is +/-2^63usec, ~2^43s, 
   * approximately +/-292000 years, microsecond precision throughout. Covers historical
   * time just fine, it doesn't make sense to talk about times in the jurassic period to
   * microsecond precision
   * <blockquote>
   * <i>"Is it not a strange fate that we should suffer so much fear and doubt for so
   * small a thing? So small a thing!"</i> 
   * <p>
   * --Boromir, <i>The Fellowship of the Ring</i>
   * </blockquote>
   */
  private long Tgps;
  public long getTgps() {return Tgps;}
  /**
   * Time Units of this Time object
   */
  public TimeUnits Units=TimeUnits.Microseconds;
  /**
   * Time Scale of this Time object.
   */
  public TimeScale Scale=TimeScale.GPS;
  /**
   * Time Scale of this Time object.
   */
  public TimeEpoch Epoch=TimeEpoch.GPS;
  
  private static final long M=1000000L;

  public static final String[] MonthAbbrev={
    "Jan","Feb","Mar","Apr",
    "May","Jun","Jul","Aug",
    "Sep","Oct","Nov","Dec"
  };
  private static final int[] MonthLen={
    31,28,31,30,
    31,30,31,31,
    30,31,30,31
  };
  public static int getMonthLength(int year, int month) {
    if(month!=2) return MonthLen[month-1];
    int monthLen=MonthLen[2-1];
    if(year%4==0 && (year%100!=0||year%400==0)) monthLen++;
    return monthLen;
  }
  /**
   * Convert a foreign timestamp to a GPS timestamp. Use the formula <pre>Tgps=Tforeign*Factor-Tepoch-Tscale_ofs(Tgps)</pre>
   * @param Tforeign Foreign timestamp
   * @param u Foreign time units
   * @param s Foreign time scale
   * @param e Foreign time epoch
   * @return
   *   Equivalent timestamp in microseconds from GPS epoch
   */
  public static long ForeignToGPS(double Tforeign, TimeUnits u, TimeScale s, TimeEpoch e) {
    long Tgps=((long)(Tforeign*u.Factor))-e.Offset;
    Tgps-=s.Offset(Tgps);
    return Tgps;
  }
  /**
   * Convert a GPS timestamp to a foreign timestamp. Use the formula <pre>Tforeign=(Tjava+Tscale_ofs(Tjava)+Tepoch)/Factor</pre>
   * @param Tgps Timestamp in microseconds from GPS epoch
   * @param u Foreign time units
   * @param s Foreign time scale
   * @param e Foreign time epoch
   * @return
   *   Equivalent foreign timestamp
   */
  public static double GPSToForeign(long Tgps, TimeUnits u, TimeScale s, TimeEpoch e) {
    long scaleOffset=s.Offset(Tgps);
    long epochOffset=e.Offset;
    long num=Tgps+scaleOffset+epochOffset;
    long den=u.Factor;
    double Tforeign=((double)num)/((double)den);
    return Tforeign;
  }
  public Time(double T, TimeUnits LUnits, TimeScale LScale, TimeEpoch LEpoch) {
    set(ForeignToGPS(T,LUnits,LScale,LEpoch));
    Units=LUnits;
    Scale=LScale;
    Epoch=LEpoch;
    SetDebugVals();
  }
  public Time(TimeUnits LUnits, TimeScale LScale, TimeEpoch LEpoch) {
    Units=TimeUnits.Milliseconds;
    Scale=TimeScale.UTC;
    Epoch=TimeEpoch.Java;
    set(System.currentTimeMillis());
    Units=LUnits;
    Scale=LScale;
    Epoch=LEpoch;
    SetDebugVals();
  }
  /** Julian Day Number from Calendar Date. Given a year, month, and day
   * calculate the Julian day number of that day. 
   * @param Y Year. No Y2K fixes are applied, so if you want a 4 digit year, enter all four digits.
   * @param M Month, January=1
   * @param D Day of month
   * @return Julian day number, Julian date at 12:00:00 on the given day.
   */
  public static long ymd2JDN(int Y, int M, int D) {
    long Term1=(1461*(Y+4800+(M-14)/12))/4;
    long Term2=(367*(M-2-12*((M-14)/12)))/12;
    long Term3=(3*((Y+4900+(M-14)/12)/100))/4;
    return Term1+Term2-Term3+D-32075;
  }
  public static int[] JDN2ymd(long JDN) {
    long L=JDN+68569;
    long N=(4*L)/146097;
    L=L-(146097*N+3)/4;
    long I=(4000*(L+1))/1461001;
    L=L-(1461*I)/4+31;
    long J=(80*L)/2447;
    int d=((int)(L-(2447*J)/80));
    L=J/11;
    int m=((int)(J+2-12*L));
    int y=((int)(100*(N-49)+I+L));
    return new int[] {y,m,d};
  }
  public static int[] usec2ymd(long usec) {
    return JDN2ymd(Math.round(TimeEpoch.GPS2JD(usec)));
  }
  public static long hnsu2usec(int h, int n, int s, int u) {
    long LTgps=0*24+h; //To hours, then add hours
    LTgps=60*LTgps+n; //To minutes, then add minutes
    LTgps=60*LTgps+s; //To seconds, then add seconds
    LTgps= M*LTgps+u; //To microseconds, then add microseconds
    return LTgps;
  }
  public static int[] usec2hnsu(long usec) {
    int h=(int)mldiv(usec,3600L*M);
    usec=mlmod(usec,3600L*M);
    int n=(int)mldiv(usec,60L*M);
    usec=mlmod(usec,60L*M);
    int s=(int)mldiv(usec,M);
    usec=mlmod(usec,M);
    return new int[] {h,n,s,(int)usec};
  }
  public static long ymdhnsu2usec(int y, int m, int d, int h, int n, int s, int u) {
    long JDN=ymd2JDN(y,m,d);
    long usec=(JDN-TimeEpoch.GPSEpochN)*86400L*M;
    usec+=hnsu2usec(h,n,s,u); //Number of whole days since the epoch
    return usec;
  }
  public static int[] usec2ymdhnsu(long usec) {
    int[] ymd=usec2ymd(usec);
    usec=mlmod(usec,86400L*M);
    int[] hnsu=usec2hnsu(usec);
    int[] result=new int[7];
    System.arraycopy(ymd,  0, result, 0, ymd.length);
    System.arraycopy(hnsu, 0, result, 3, hnsu.length);
    return result;
  }
  public Time(
      int y,
      int m,
      int d,
      int h,
      int n,
      int s,
      int u,
      TimeUnits LUnits,
      TimeScale LScale,
      TimeEpoch LEpoch
  ) {
    set(y,m,d,h,n,s,u,LUnits,LScale);
    Epoch=LEpoch;
  }
  public Time(
      int y,
      int m,
      int d,
      int h,
      int n,
      int s,
      TimeUnits LUnits,
      TimeScale LScale,
      TimeEpoch LEpoch
  ) {
    this(y,m,d,h,n,s,0,LUnits,LScale,LEpoch);
  }
  public Time(
      int y,
      int m,
      int d,
      int h,
      int n,
      double s,
      TimeUnits LUnits,
      TimeScale LScale,
      TimeEpoch LEpoch
    ) {
      this(y,m,d,h,n,(int)floor(s),(int)((s-floor(s))*1000000),LUnits,LScale,LEpoch);
    }
  public Time(String SS, TimeUnits LUnits, TimeScale LScale, TimeEpoch LEpoch) {
    super();
    String[] SP=SS.split("[-: ]+");
    int Y=Integer.parseInt(SP[2]);
    int D=Integer.parseInt(SP[0]);
    int H=Integer.parseInt(SP[3]);
    int N=Integer.parseInt(SP[4]);
    double S=Double.parseDouble(SP[5]);
    String MM=SP[1];
    int m=-1;
    for(int i=0;i<MonthAbbrev.length;i++) {
      if(MM.equalsIgnoreCase(MonthAbbrev[i])) m=i+1;
    }
    if(m<0) throw new IllegalArgumentException("Can't parse month name in date string '"+SS+"'");
    set(Y,m,D,H,N,(int)floor(S),(int)mlmod(S,1e6),LUnits,LScale);
    Epoch=LEpoch;
  }
  public Time() {
    Units=TimeUnits.Milliseconds;
    Scale=TimeScale.UTC;
    Epoch=TimeEpoch.Java;
    set((double)System.currentTimeMillis());
  }
  public Time(Time T) {
    Tgps=T.Tgps;
    Units=T.Units;
    Scale=T.Scale;
    Epoch=T.Epoch;
    SetDebugVals();
  }
  public static Time add(Time T, double dT) {
    Time result;
    result=new Time(T.get()+dT,T.Units,T.Scale,T.Epoch);
    return result;
  }
  public static Time add(Time T, double dT, TimeUnits LUnits) {
    Time result;
    TimeUnits oldUnits=T.Units;
    T.Units = LUnits;
    result=new Time(T.get()+dT,LUnits,T.Scale,T.Epoch);
    T.Units=oldUnits;
    result.Units=oldUnits;
    result.SetDebugVals();
    return result;
  }
  public static Time TimeInput(BufferedReader stdin, PrintStream stdout) throws IOException {
    String digitstring;
    int value;
    int[] ymdhn=new int[5];
    double s;
    Time T;
    TimeScale scale;
    if (stdout!=null)stdout.println("Enter 1 for UTC\n"+
            "      2 for GPS\n"+
            "      3 for TAI\n"+
            "      4 for TDT\n"+
            "      5 for TDB (Horizons CT)\n");
    digitstring = stdin.readLine();
    value = Integer.parseInt(digitstring);
    switch(value) {
      case 1:
        scale=TimeScale.UTC;
        break;
      case 2:
        scale=TimeScale.GPS;
        break;
      case 3:
        scale=TimeScale.TAI;
        break;
      case 4:
        scale=TimeScale.TDT;
        break;
      case 5:
      default:
        scale=TimeScale.TDB;
    }
    if (stdout!=null)stdout.println(
      "Enter 1 for JD, 2 for MJD, 3 for seconds, 4 for ymdHNS"
    );
    digitstring = stdin.readLine();
    value = Integer.parseInt(digitstring);
    if(value==4) {
      if (stdout!=null)stdout.println(
        "Enter Y M D H N S, space seperated. Only S can be a floating point."
      );
      StringTokenizer reader=new StringTokenizer(stdin.readLine());
      int count = 0;
      while (count < 5) {
        while (reader.countTokens() == 0) {
          reader = new StringTokenizer(stdin.readLine());
        }
        ymdhn[count]=Integer.parseInt(reader.nextToken());
        count++;
      }
      while (reader.countTokens() == 0) {
        reader = new StringTokenizer(stdin.readLine());
      }
      s=Double.parseDouble(reader.nextToken());
      T=new Time(
        ymdhn[0],
        ymdhn[1],
        ymdhn[2],
        ymdhn[3],
        ymdhn[4],
        s,
        TimeUnits.Days,
        scale,
        TimeEpoch.GPS
      );
    } else {
      double D;
      StringTokenizer reader=new StringTokenizer(stdin.readLine());
      while (reader.countTokens() == 0) {
        reader = new StringTokenizer(stdin.readLine());
      }
      D=Double.parseDouble(reader.nextToken());
      switch(value) {
        case 1:
          T=new Time(D,TimeUnits.Days,scale,TimeEpoch.JD);
          break;
        case 2:
          T=new Time(D,TimeUnits.Days,scale,TimeEpoch.MJD);
          break;
        case 3:
          T=new Time(D,TimeUnits.Seconds,scale,TimeEpoch.GPS);
          break;
        default:
          T=new Time();
      }
    }
    return T;
  }
  public static Time TimeInput() throws IOException {
    BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
    return TimeInput(stdin,System.out);
  }
  public void add(double dT) {
    set(get()+dT);
    SetDebugVals();
  }
  public void add(double dT,TimeUnits LUnits) {
    TimeUnits oldUnits = Units;
    Units=LUnits;
    set(get()+dT);
    Units=oldUnits;
    SetDebugVals();
  }

  public double get() {
    return GPSToForeign(Tgps,Units,Scale,Epoch);
  }
  public double get(TimeUnits LUnits, TimeScale LScale, TimeEpoch LEpoch) {
    TimeUnits oldUnits=Units;
    TimeScale oldScale=Scale;
    TimeEpoch oldEpoch=Epoch;
    Units=LUnits;
    Scale=LScale;
    Epoch=LEpoch;
    double result=get();
    Units=oldUnits;
    Scale=oldScale;
    Epoch=oldEpoch;
    return result;
  }
  public double get(TimeUnits LUnits, TimeScale LScale) {
    return get(LUnits,LScale,Epoch);
  }
  public double get(TimeUnits LUnits) {
    return get(LUnits,Scale);
  }
  public double get(TimeScale LMode) {
    return get(Units,LMode);
  }
  public double get(Time LTime) {
    return get(LTime.Units,LTime.Scale,LTime.Epoch);
  }
  public double sub(Time LTime) {
    return get()-LTime.get(this);
  }
  public void set(double T) {
    Tgps=ForeignToGPS(T,Units,Scale,Epoch);
    SetDebugVals();
  }
  public void set(
    int y,
    int m,
    int d,
    int h,
    int n,
    int s,
    int u,
    TimeUnits LUnits,
    TimeScale LScale
  ) {
    Units=TimeUnits.Microseconds;
    Scale=LScale;
    TimeEpoch oldEpoch=Epoch;
    Epoch=TimeEpoch.GPS;
    set(ymdhnsu2usec(y,m,d,h,n,s,u));
    Units=LUnits;
    Epoch=oldEpoch;
    SetDebugVals();
  }
  public TimeUnits getUnits() {
    return Units;
  }
  public long getTime() {
    return Tgps+Scale.Offset(Tgps);
  }
  public int[] getymdhnsu() {
    return usec2ymdhnsu(getTime());
  }
  public String toDateString() {
    int ymdhnsu[]=getymdhnsu();
    return String.format("%04d %s %02d %02d:%02d:%02d.%06d", 
        ymdhnsu[0],
        MonthAbbrev[ymdhnsu[1]-1],
        ymdhnsu[2],
        ymdhnsu[3],
        ymdhnsu[4],
        ymdhnsu[5],
        ymdhnsu[6]
    );
  }
  public String toString() {
    if(TimeDebug) return Units.Format.format(get())+" "+Units.toString()+" "+Epoch+" "+Scale+" ("+toDateString()+Scale+") ["+String.format("%18d", Tgps)+"]";
                  return Units.Format.format(get())+" "+Units.toString()+" "+Epoch+" "+Scale+" ("+toDateString()+Scale+")";
  }
  public String toStringHorizons() {
    DecimalFormat FormatSeconds=new DecimalFormat("0.000000000");
    int ymdhnsu[]=getymdhnsu();
    String HorizonsDateFormat=String.format("A.D. %04d-%s-%02d %02d:%02d:%02d.%04d", 
        ymdhnsu[0],
        MonthAbbrev[ymdhnsu[1]-1],
        ymdhnsu[2],
        ymdhnsu[3],
        ymdhnsu[4],
        ymdhnsu[5],
        ymdhnsu[6]/100
    );
    return FormatSeconds.format(get())+", "+HorizonsDateFormat;
  }
  public String toString(TimeUnits LUnits, TimeScale LScale, TimeEpoch LEpoch) {
    TimeUnits OldUnits=Units;
    Units=LUnits;
    TimeScale OldScale=Scale;
    Scale=LScale;
    TimeEpoch OldEpoch=Epoch;
    Epoch=LEpoch;
    String result = toString();
    Units=OldUnits;
    Scale=OldScale;
    Epoch=OldEpoch;
    return result;
  }
  public String toString(TimeUnits LUnits, TimeScale LMode) {
    return toString(LUnits,LMode,Epoch);
  }
  public String toString(TimeUnits LUnits) {
    return toString(LUnits, Scale);
  }
  public String toString(TimeScale LScale) {
    return toString(Units, LScale);
  }
  private double DebugVal;
  private String DebugStr;
  private void SetDebugVals() {
    DebugVal=get();
    DebugStr=toString();
  }
  public static void main(String[] args) {
    System.out.println(-1/3);
    System.out.println(mlmod(-1,3));
    System.out.println(mldiv(-1,3));
    System.out.println(ymd2JDN(1990,1,1));
    /* Check TDT. "At the instant 1977 Jan 01 00:00:00 TAI, the value of the new timescale 
     * for apparent geocentric ephemerides [TDT] will be 1977 Jan 01.0003725 (Jan 01 00:00:32.184)
     * exactly" 
     */
    Time T=new Time(1977,1,1,0,0,0,0,TimeUnits.Days,TimeScale.TAI,TimeEpoch.TAI);
    System.out.println(T);
    T.Scale=TimeScale.TDT;
    System.out.println(T);
    //Test smallest distinguishable day fraction
    Time T0=new Time(1980,1,6,0,0,0,0,TimeUnits.Days, TimeScale.GPS, TimeEpoch.JD);
    Time T1=new Time(1980,1,6,0,0,0,1,TimeUnits.Days, TimeScale.GPS, TimeEpoch.JD);
    System.out.println(T0);
    System.out.println(T1);
    T0=new Time(2451597.5,TimeUnits.Days, TimeScale.TDT, TimeEpoch.JD);
    System.out.println(T0);
    T0.Scale=TimeScale.TAI;
    System.out.println(T0);
    T0.Scale=TimeScale.UTC;
    System.out.println(T0);
    T0.Scale=TimeScale.TDB;
    System.out.println(T0);
    System.out.println(new Time());
    T0=new Time(2005,12,31,23,59,59,0,TimeUnits.Days,TimeScale.UTC,TimeEpoch.GPS);
    System.out.println(T0);
    T0.add(1.0/86400.0);
    System.out.println(T0);
    T0.add(1.0/86400.0);
    System.out.println(T0);
  }
  public static double difference(Time time1, Time time2, TimeUnits units) {
    return ((double)(time2.Tgps-time1.Tgps))/units.Factor;
  }
  public int compareTo(Time o) {
    if (Tgps<o.Tgps) return -1;
    if (Tgps>o.Tgps) return +1;
    return 0;
  }
}
