package org.kwansystems.tools.time;

public enum TimeEpoch {
  Java(2440587.5),
  JD(0.0),
  /**
   * Julian Date of Modified Julian Day Epoch, 17 Nov 1858 00:00:00 (any time scale)
   */
  MJD(2400000.5),
  /**
   * GPS Epoch, 6 Jan 1980 00:00:00UTC
   */
  GPS(0L),
  /**
   * J2000 epoch, 1 Jan 2000 12:00:00 (any time scale)
   */
  J2000(2451545.0),
  /**
   * TAI epoch, 1 Jan 1958 0:00:00 (any time scale)
   * As used by the SDO project
   */
  TAI(2436204.5);
  public static final long GPSEpochN=2444245;
  public static final double GPSEpoch=((double)GPSEpochN)-0.5;
  public static final double GPS2JD(long Tgps) {
    return GPSEpoch+((double)Tgps)/86400e6;
  }
  public final long Offset; 
  private TimeEpoch(long LOffset) {
    Offset=LOffset;
  } 
  private TimeEpoch(double EpochJD) {
    this(((long)((GPSEpoch-EpochJD)*86400L*1000000L)));
  }
  public double JDEpoch() {
    return -((double)Offset)/86400.0/1e6+GPSEpoch;
  }
}