package org.kwansystems.tools.time;

import java.text.*;

/** Time units. Time units are a constant number of timescale microseconds, and are used
 * to work with time in more conventional units like minutes, days, etc.  */
public enum TimeUnits {
  /** One millionth part of a timescale second.  */
  Microseconds(1,false,"0"),
  /** One thousandth part of a timescale second. */
  Milliseconds(1000,false,"0.000"),
  /** One timescale second. */
  Seconds(1,true,"0.000000"),
  /** One minute of exactly sixty timescale seconds.  */
  Minutes(60L,true),
  /** One hour of exactly sixty timescale minutes.  */
  Hours(3600L,true),
  /** One julian day of exactly 24 timescale hours. Note that this is not the length of the mean solar
   * day. It is within a couple of milliseconds, but the difference is what gives us leap seconds. Ephemeris
   * theories which use days are unanimous in defining days this way. */
  Days(86400L,true),
  /** One week of exactly 7 julian days. This is mostly used in terms of GPS */
  Weeks(86400L*7,true),
  /** One month of exactly 30 julian days. This is used merely for convenience */
  Months(86400L*30,true),
  /** One year of exactly 365.25 julian days. This is used merely for convenience */
  Years((86400L*36525L)/100L,true),
  /** One julian century of exactly 36525 julian days. This is the distance between corresponding days
   * 100 years apart using the julian leap-year rules. Ephemeris theories which use centuries are unanimous
   * in defining centuries this way. */
  Centuries(36525L*86400,true),
  /** One julian milennium of exactly 365250 julian days. Called kiloyears instead of millenia since the latter
   * is hard to spell. This is the distance between corresponding days 1000 years apart using the julian 
   * leap-year rules. Ephemeris theories which use kiloyears are unanimous in defining kiloyears this way. */
  Kiloyears(365250L*86400,true);
  /** Exactly one million. Used so as to not have to count zeros in defining these time units. */
  public static final long M=1000000;
 
  /** Number of microseconds per time unit. */
  public final long Factor;
  public final DecimalFormat Format;
  /** Construct a time unit.
   * @param LFactor Number of seconds or microseconds per time unit
   * @param inSeconds True if LFactor is expressed in seconds, false otherwise.
   */
  private TimeUnits(long LFactor,boolean inSeconds, String LFormat) {
    Factor=LFactor*(inSeconds?M:1);
    Format=new DecimalFormat(LFormat);
  }
  private TimeUnits(long LFactor,boolean inSeconds) {
    this(LFactor,inSeconds,"0.000000000");
  }
}