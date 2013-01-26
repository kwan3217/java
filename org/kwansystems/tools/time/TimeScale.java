package org.kwansystems.tools.time;


/**
 * Enumerated type describing time scales.
 * <p>
 * As time marches on, UTC continually drifts behind the other scales, because
 * only it adds leap seconds.
 * <p>
 * <pre>
 *   U             G             T             TT
 *   T             P             A             DD
 *   C             S             I             TB
 *   |--DeltaGPS2->|--DeltaGPS1->|---DeltaTA-->||--->Higher clock numbers
 *   |  =GPS-UTC      =TAI-GPS   |   =TDT-TAI  ||
 *   |----------DeltaAT--------->|           ->||DeltaR (Positive on this graph)
 *               =TAI-UTC                        =TDB-TDT
 * </pre>
 * All time scales are a simple count of number of units elapsed since the epoch.
 * 
 */
public enum TimeScale {
  /**
   * Coordinated Universal Time scale. Atomic time scale with leap seconds. UTC is 
   * steered so that 12:00:00UTC every day is within 900ms of mean solar noon, by adding
   * (or subtracting, but this has never happened) a second in between 23:59:59UTC and 
   * 00:00:00UTC on December 31/January 1, or June 30/July 1. The fractional second 
   * part of UTC exactly matches TAI by definition, so the difference is always 
   * an integral number of seconds. When setting the Time object based on the system
   * clock, it is assumed that the system clock uses this time scale. This time scale 
   * follows TAI in that the duration of a UTC second is precisely that of an SI seconds
   * observed by a clock fixed to the geoid. 
   */
  UTC() {
   /** Offset for UTC is -DeltaGPS2 */
    public long Offset(long Tgps) {
      return -DeltaGPS2(Tgps);
    }
  },
  JST() {public long Offset(long Tgps) {return -DeltaGPS2(Tgps)+9*3600*M;}},  //Japan Stadard time, 9 hours ahead of UTC (year round)
  PHT() {public long Offset(long Tgps) {return -DeltaGPS2(Tgps)+8*3600*M;}},  //Philippine Time, 8 hours ahead of UTC (year round)
  CET() {public long Offset(long Tgps) {return -DeltaGPS2(Tgps)+1*3600*M;}},  //Central European Time, 1 hour ahead of UTC (winter)
  CEST() {public long Offset(long Tgps) {return -DeltaGPS2(Tgps)+2*3600*M;}}, //Central European Summer Time, 2 hours ahead of UTC (summer)
  MSK() {public long Offset(long Tgps) {return -DeltaGPS2(Tgps)+3*3600*M;}},  //Moscow Standard Time, 3 hours ahead of UTC (winter)
  MSD() {public long Offset(long Tgps) {return -DeltaGPS2(Tgps)+4*3600*M;}},  //Moscow Summer Time, 4 hours ahead of UTC (summer)
  EDT() {public long Offset(long Tgps) {return -DeltaGPS2(Tgps)-4*3600*M;}},  //Eastern daylight time, 4 hours behind UTC (summer)
  EST() {public long Offset(long Tgps) {return -DeltaGPS2(Tgps)-5*3600*M;}},
  CDT() {public long Offset(long Tgps) {return -DeltaGPS2(Tgps)-5*3600*M;}},
  CST() {public long Offset(long Tgps) {return -DeltaGPS2(Tgps)-6*3600*M;}},
  MDT() {public long Offset(long Tgps) {return -DeltaGPS2(Tgps)-6*3600*M;}},
  MST() {public long Offset(long Tgps) {return -DeltaGPS2(Tgps)-7*3600*M;}},
  PDT() {public long Offset(long Tgps) {return -DeltaGPS2(Tgps)-7*3600*M;}},
  PST() {public long Offset(long Tgps) {return -DeltaGPS2(Tgps)-8*3600*M;}},
  /**
   * Global Positioning System time scale. Atomic time scale with no leap seconds, used by the GPS system. The GPS system clock 
   * matched UTC at the GPS epoch, but since leap seconds are added to UTC but not GPS,
   * GPS has diverged from UTC by several seconds (14 in 2006). The GPS system actually
   * uses weeks and seconds in its broadcasts, but it is common (especially at LASP) to 
   * keep a GPS second count running continuously since the GPS epoch. GPS time is a weighted
   * average of the time kept by the atomic clocks on board the GPS satellites and at the 
   * GPS monitoring stations, and is actively steered to keep within 100ns of UTC, except 
   * for the leap second jumps. In principle, this time scale follows TAI in that the duration 
   * of a GPS second is precisely that of an SI seconds observed by a clock fixed to the geoid.
   * Clocks on board the GPS spacecraft are specifically tuned by whatever special and general 
   * relativity effects are necessary so that when they are observed from the geoid, one GPS 
   * second appears to match one SI second exactly. This means that the GPS clocks do not
   * tick in proper SI seconds, as observed on board the spacecraft.
   */
  GPS() {
    public long Offset(long Tjava) {
      return 0;
    }
  },
  /**
   * International Atomic Time scale. Atomic time scale with no leap seconds. This time scale is a "paper 
   * clock" composed of the weighted averages of hundreds of atomic clocks from dozens of timekeeping 
   * laboratories all over the world, each compensated for their temperature and height above the geoid. 
   * In principle, one TAI second is precisely one proper SI second at the geoid, subject only to how well
   * the atomic clocks in the scale function as ideal clocks. TAI matched UTC on 1958 Jan 01, but since 
   * leap seconds are added to UTC but not TAI, TAI has diverged from UTC by several seconds (33 in 2006).
   * It is our most accurate and precise measure of proper time on the geoid, and for most purposes, can 
   * be considered "perfect".   
   */
  TAI() {
    /**
     * Difference between GPS and TAI in microseconds, TAI=GPS+DeltaGPS1, constant by definition
     */
    private static final long DeltaGPS1=19L*M;
    public long Offset(long Tgps) {
      return DeltaGPS1;
    }
  },
  /**
   * TDT, Terrestrial Dynamic Time. Dynamical time, kept by comparing the observed positions of the planets
   * with those calculated from an ephemeris theory. Since this is dynamical time, general relativity must be 
   * taken into account, so TDT is the proper time of a reference frame moving with the Earth geoid, in SI seconds.
   * In practice, TDT is realized as a fixed offset from TAI, TDT-TAI=32.184s  
   */
  TDT() {
    /**
     * Difference between TDT and TAI in microseconds, TDT=TAI+DeltaTA, constant by definition
     */
    private static final long DeltaTA=32184000;
    public long Offset(long Tgps) {
      return TAI.Offset(Tgps)+DeltaTA;
    }
  },
  /**
   * TDB, Barycentric Dynamic Time. Dynamical time, kept by comparing the observed positions of the planets
   * with those calculated from an ephemeris theory. Since this is dynamical time, general relativity must be 
   * taken into account, so TDB is the proper time of a reference frame moving with the solar system barycenter,
   * but not down in the gravity well of the Sun.
   * <p>
   * Since the barycenter is accelerating relative to the Earth geoid, time at the barycenter does not elapse
   * at the same rate as time on the geoid. TDB is scaled such that its mean rate is equal to that of TAI, by
   * whatever relativity things are necessary, so a second of TDB at the barycenter is not an SI second.
   * Specifically, TDB is distinct from TCB, barycentric coordinate time, which does tick with SI seconds.
   * In practice, TDB is realized as a variable offset from TDT, calculated from the orbit of the Earth,
   * and has a maximum value of around 3ms. Each ephemeris theory has its own time scale, which can be
   * considered TDB. In practice, this TDB is the one used by the JPL epheperis DE405, as reported by
   * Horizons (where it is called CT, not to be confused with TCB.).
   * <p>
   * From the Horizons help page:
   * <blockquote>
   *    UNIVERSAL TIME (TDB to UT Conversion):
   *    <p>
   *    This program [Horizons] internally uses the TDB time-scale of the
   *    ephemerides (the independent variable in the equations of motion). To
   *    produce the more familiar Universal Time (UT) output tied to the Earth's
   *    rotation, it is necessary to use historical reconstructions of old or
   *    ancient observations of constrained events, such as eclipses, to derive a
   *    TDB-UT difference. This program currently uses the analyses of [12-15] as
   *    follows:
   *    <table>
   *    <tr><th>Span              </th><th>TDB-UT offset  ("delta-t")</th><th>Type</th><th>Argument (T=...)</th></tr>
   *    <tr><td>3000 BC to AD  948</td><td>31*T*T                 </td><td>UT1</td><td>cent. since 1820</td></tr>
   *    <tr><td>AD  948 to AD 1620</td><td>50.6+67.5*T+22.5*T*T   </td><td>UT1</td><td>cent. since J2000.0</td></tr>
   *    <tr><td>AD 1620 to AD 1972</td><td>Smoothed table         </td><td>UT1</td></tr>
   *    <tr><td>AD 1972 to Present</td><td>Leap-second based table</td><td>UTC</td></tr>
   *    </table>
   *     For the modern UTC era specifically, the calculation is as follows:
   *    <p>
   *    <pre>
   *                  TDB-UTC= DeltaTA + DeltaAT + DeltaR
   *    ... where
   *                  DeltaTA  = (TDT-TAI) [=32.184s CDJ]
   *                  DeltaAT  = DeltaTDT-DeltaTA
   *                  DeltaTDT = TDT-UTC
   *                  DeltaR   = K*sin(E)=TDB-TDT,
   *                             relativity correction surface of earth to
   *                             solar system barycenter
   *                  K        = Constant, nominally 0.001657.
   *                  E        = eccentric anomaly of heliocentric orbit of
   *                             Earth-Moon barycenter.
   *    </pre>
   *    As one progresses to earlier times, particularly those prior to the
   *    1620 telescopic data span, uncertainties in UT determination generally
   *    (though not always and not uniformly) increase due to less precise
   *    observations and sparser records. At A.D. 948, uncertainty (not necessarily
   *    error) can be a few minutes.  At 3000 B.C., the uncertainty in UT is about
   *    4 hours.  The TT time scale, being uniform, does not have this uncertainty,
   *    but is not directly related to Earth's rotation (local time) either.
   * </blockquote>
   * <p>
   * There are all sorts of discussions, dissensions, contentions, and confusions
   * about what TDB really means, and what time scales the ephemerides are in,
   * but for a practical matter, TDT is the only physically realizable dynamic time
   * scale, since it is based on TAI. All significant observations are ultimately based on the VLBI
   * catalogs which are solidly based in geoid atomic clock measurements, so even if
   * it is mathematically convenient to run an integration in TDB, TDB is really just
   * reduced TDT since there is no clock at the barycenter which keeps TDB.
   * <a href="http://www.ucolick.org/~sla/leapsecs/timescales.html">More info on time
   * scales</a>
   * <p>
   * So: By TDB I mean the timescale any particular ephemeris uses. In practical fact, TDB is based on
   * a simplified formula from the Explanatory Supplement. This matches T<sub><i>eph</i></sub> in the 
   * JPL DE405 integrated ephemeris to within a microsecond, and so is close enough for our purposes.
   */
  TDB() {
    private long DeltaR(double JD) {
      double D=(JD-TimeEpoch.J2000.JDEpoch());
      double Mearth=Math.toRadians((D/36525.0)*35999.05034+357.5277233);
      double result=1658*Math.sin(Mearth)+13.85*Math.sin(2*Mearth);
      return (long)(result);
    }
    public long Offset(long Tgps) {
      long TDTOffset=TDT.Offset(Tgps);
      long DeltaROffset=DeltaR(TimeEpoch.GPS2JD(Tgps));
      return TDTOffset+DeltaROffset;
    }
  };
  /** Exactly one million. Used so as to not have to count zeros in defining these time units. */
  public static final long M=1000000;
  /**
   * Table for calculating DeltaAT (TAI=UTC+DeltaAT)
   * DeltaAT=Offset+(MJD-EpochMJD)*Rate
   * Data from ftp://maia.usno.navy.mil/ser7/tai-utc.dat
   * Current up to 1 Jul 2012
   */
  public static final double[][] DeltaATTable={
    //StartJD     Offset      EpochMJD  Rate        StartDate   EpochDate
    {2437300.5, 1.4228180,  37300.0,  0.001296 }, //1 Jan 1961  1 Jan 1961
    {2437512.5, 1.3728180,  37300.0,  0.001296 }, //1 Aug 1961  1 Jan 1961
    {2437665.5, 1.8458580,  37665.0,  0.0011232}, //1 Jan 1962  1 Jan 1962
    {2438334.5, 1.9458580,  37665.0,  0.0011232}, //1 Nov 1963  1 Jan 1962
    {2438395.5, 3.2401300,  38761.0,  0.001296 }, //1 Jan 1964  1 Jan 1965
    {2438486.5, 3.3401300,  38761.0,  0.001296 }, //1 Apr 1964  1 Jan 1965
    {2438639.5, 3.4401300,  38761.0,  0.001296 }, //1 Sep 1964  1 Jan 1965
    {2438761.5, 3.5401300,  38761.0,  0.001296 }, //1 Jan 1965  1 Jan 1965
    {2438820.5, 3.6401300,  38761.0,  0.001296 }, //1 Mar 1965  1 Jan 1965
    {2438942.5, 3.7401300,  38761.0,  0.001296 }, //1 Jul 1965  1 Jan 1965
    {2439004.5, 3.8401300,  38761.0,  0.001296 }, //1 Sep 1965  1 Jan 1965
    {2439126.5, 4.3131700,  39126.0,  0.002592 }, //1 Jan 1966  1 Jan 1966
    {2439887.5, 4.2131700,  39126.0,  0.002592 }, //1 Feb 1968  1 Jan 1966
    {2441317.5,10.0      ,  41317.0,  0.0     },  //1 Jan 1972  1 Jan 1972
    {2441499.5,11.0      ,  41317.0,  0.0     },  //1 Jul 1972
    {2441683.5,12.0      ,  41317.0,  0.0     },  //1 Jan 1973
    {2442048.5,13.0      ,  41317.0,  0.0     },  //1 Jan 1974
    {2442413.5,14.0      ,  41317.0,  0.0     },  //1 Jan 1975
    {2442778.5,15.0      ,  41317.0,  0.0     },  //1 Jan 1976
    {2443144.5,16.0      ,  41317.0,  0.0     },  //1 Jan 1977
    {2443509.5,17.0      ,  41317.0,  0.0     },  //1 Jan 1978
    {2443874.5,18.0      ,  41317.0,  0.0     },  //1 Jan 1979
    {2444239.5,19.0      ,  41317.0,  0.0     },  //1 Jan 1980
    {2444786.5,20.0      ,  41317.0,  0.0     },  //1 Jul 1981
    {2445151.5,21.0      ,  41317.0,  0.0     },  //1 Jul 1982
    {2445516.5,22.0      ,  41317.0,  0.0     },  //1 Jul 1983
    {2446247.5,23.0      ,  41317.0,  0.0     },  //1 Jul 1985
    {2447161.5,24.0      ,  41317.0,  0.0     },  //1 Jan 1988
    {2447892.5,25.0      ,  41317.0,  0.0     },  //1 Jan 1990
    {2448257.5,26.0      ,  41317.0,  0.0     },  //1 Jan 1991
    {2448804.5,27.0      ,  41317.0,  0.0     },  //1 Jul 1992
    {2449169.5,28.0      ,  41317.0,  0.0     },  //1 Jul 1993
    {2449534.5,29.0      ,  41317.0,  0.0     },  //1 Jul 1994
    {2450083.5,30.0      ,  41317.0,  0.0     },  //1 Jan 1996
    {2450630.5,31.0      ,  41317.0,  0.0     },  //1 Jul 1997
    {2451179.5,32.0      ,  41317.0,  0.0     },  //1 Jan 1999
    {2453736.5,33.0      ,  41317.0,  0.0     },  //1 Jan 2006
    {2454832.5,34.0      ,  41317.0,  0.0     },  //1 Jan 2009
    {2456109.5,35.0      ,  41317.0,  0.0     }   //1 Jul 2012
  };
  public static long DeltaAT(long Tgps) {
    double JD=TimeEpoch.GPS2JD(Tgps);
    if(JD>DeltaATTable[0][0]) {
      int i;
      for(i=DeltaATTable.length-1;DeltaATTable[i][0]>JD;i--) {}
      double MJD=JD-2400000.5;
      long deltaAT=(long)(M*(DeltaATTable[i][1]+(MJD-DeltaATTable[i][2])*DeltaATTable[i][3]));
      return deltaAT;
    }
    return 0;
  }
  public static long DeltaGPS2(long Tgps) {
    return DeltaAT(Tgps)-TAI.Offset(Tgps);
  }
 /**
   * Calculate timescale offset.
   * @param Tgps GPS timestamp at which to calculate time scale offset 
   * @return
   *   Offset from GPS in microseconds. If the scale reads higher numbers
   *   than GPS at the same time, then Offset is positive.
   */
  public abstract long Offset(long Tgps);
}