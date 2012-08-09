package org.kwansystems.space.ephemeris.earth.sgp4Vallado;

import static java.lang.Math.*;

import java.io.*;
import org.kwansystems.tools.cformat.*;

/** Two Line Element Set. This class handles parsing of TLEs from their two-line format into
 * a useful structure for other programs. 
 */
public class TwoLineElement implements Comparable<TwoLineElement> {
  public String line1, line2;
  /** NORAD catalog number. Serial number of objects, in the order that they were detected by NORAD.
   * This may have no relation to the launch order, since if an object breaks up, the fragments are 
   * newly detected.
   */
  public int satnum;
  /** NORAD security classification for these elements. All published elements are marked 'U', for
   * unclassified. I've never seen a classified element, but I suppose they are things like 'C' for
   * classified, 'S' for secret, 'T' for top secret, etc. */
  public char classification;
  /** International designation. Consists of launch year, launch serial number in the year, and
   * fragment/payload letter for the launch. If a satellite splits/deploys payloads, all the fragments
   * are considered to be from the same launch. One of them is designated A, and usually this is the
   * one which keeps the NORAD catalog number of the original object, while the other(s) get letters
   * B,C,etc. and the next available NORAD catalog number. Usually the primary payload gets A,
   * secondary payloads B and so on, and the upper stage gets the next letter after the last
   * payload. On Space Shuttles, the Orbiter gets A, while any free-flying payloads get B, C, etc.
   * The tank doesn't get a letter as it never completes a full orbit. Pieces attached to the station
   * used to get their own track, but not any more. So Destiny is 2001-006B, but Quest isn't tracked.
   * Debris or jettisons from the Space Station are considered to be launched in 1998 (with Zarya)
   * regardless of when that particular object was launched.*/
  public String intldesg;
  /** Year of epoch. Stored in the TLE as a two-digit year, stored internally as a 4-digit year. Two-digit
   * years are assumed to occur between 1950 and 2049. */
  public int epochyr;
  /** Day of epoch. This is the number of days since the beginning of the year, in UTC, with 1 January 00:00:00UTC
   * being exactly 1.0 .  */
  public double epochdays;
  /** Epoch time in julian date utc */
  public double jdsatepoch;
  /** Secular acceleration. This is the rate of change of the mean motion of the satellite, divided 
   * by two, in days per day. It is used with the Brouwer model, and not directly by SGP4. */
  public double ndot;
  /** Secular acceleration rate. This is the acceleration of the mean motion of the satellite, divided
   * by six, in days/day^2. It is used with the older Brouwer propagator, and not directly by SGP4. */
  public double nddot;
  public double bstar;
  public int numb;
  public int elnum;
  public int check1;
  public double inclo;
  public double nodeo;
  public double ecco;
  public double argpo;
  public double mo;
  public double no;
  public int revnum;
  public int check2;
  public double startmfe;
  public double stopmfe;
  public double deltamin;
  public SGP4Core.gravconsttype whichconst;
  public String toString() {
    StringBuffer result=new StringBuffer(line1);result.append('\n');
    result.append(line2);result.append('\n');
    result.append("NORAD catalog number:      ");result.append(satnum);result.append('\n');
    result.append("Classification:            ");result.append(classification);result.append('\n');
    result.append("International Designation: ");result.append(intldesg);result.append('\n');
    result.append("Element Type:              ");result.append(numb);result.append('\n');
    result.append("Element Number:            ");result.append(elnum);result.append('\n');
    result.append("Epoch:   ");result.append(String.format("%d/%012.8f", epochyr,epochdays));result.append('\n');
    result.append("ndot/2:  ");result.append(ndot);result.append('\n');
    result.append("nddot/6: ");result.append(nddot);result.append('\n');
    result.append("B*:      ");result.append(String.format("%8.4e",bstar));result.append('\n');
    result.append("Check1:  ");result.append(check1);result.append('\n');
    result.append("Inclination:  ");result.append(inclo);result.append("deg\n");
    result.append("RAAN:         ");result.append(nodeo);result.append("deg\n");
    result.append("Eccentricity: ");result.append(ecco);result.append('\n');
    result.append("Arg Perigee:  ");result.append(argpo);result.append("deg\n");
    result.append("Mean Anomaly: ");result.append(mo);result.append("deg\n");
    result.append("Mean Motion:  ");result.append(no);result.append("rev/day\n");
    
    return result.toString();
  }
  public TwoLineElement(String Llongstr1, String Llongstr2, SGP4Core.gravconsttype Lwhichconst) {
    whichconst=Lwhichconst;
    setTwoLines(Llongstr1, Llongstr2);
  }
  public TwoLineElement(String Llongstr1, String Llongstr2) {
    this(Llongstr1,Llongstr2,SGP4Core.gravconsttype.wgs72);
  }

  public void setTwoLines(String Llongstr1, String Llongstr2) {
    line1 = Llongstr1;
    line2 = Llongstr2;
    // set the implied decimal points since doing a formated read
    // fixes for bad input data values (missing, ...)
    StringBuffer L1 = new StringBuffer(line1);
    StringBuffer L2 = new StringBuffer(line2);
    int j, cardnumb, nexp;
    if (L1.charAt(44) != ' ')
      L1.setCharAt(43, L1.charAt(44));
    L1.setCharAt(44, '.');
    if (L1.charAt(7) == ' ')
      L1.setCharAt(7, 'U');
    if (L1.charAt(9) == ' ')
      L1.setCharAt(9, '_');
    for (j = 45; j <= 49; j++) {
      if (L1.charAt(j) == ' ')
        L1.setCharAt(j, '0');
    }
    if (L1.charAt(51) == ' ')
      L1.setCharAt(51, '0');
    if (L1.charAt(53) != ' ')
      L1.setCharAt(52, L1.charAt(53));
    L1.setCharAt(53, '.');
    L2.setCharAt(25, '.');
    for (j = 26; j <= 32; j++)
      if (L2.charAt(j) == ' ')
        L2.setCharAt(j, '0');
    if (L1.charAt(62) == ' ')
      L1.setCharAt(62, '0');
    if (L1.charAt(68) == ' ')
      L1.setCharAt(68, '0');

    // %2d %5ld %1c %10s %2d %12lf %11lf %7lf %2d %7lf %2d %2d %6ld
    ScanfReader R = new ScanfReader(new StringReader(L1.toString()));
    try {
      cardnumb = R.scanInt("%2d");
      if (cardnumb!=1) throw new IllegalArgumentException("This isn't line 1: "+line1);
      satnum = R.scanInt("%5d");
      classification = R.scanChar("%1c");
      intldesg = R.scanString("%10s");
      epochyr = R.scanInt("%2d");
      epochdays = R.scanDouble("%12f");
      ndot = R.scanDouble("%11f");
      nddot = R.scanDouble("%7f");
      nexp = R.scanInt("%2d");
      nddot*=pow(10.0, nexp);
      bstar = R.scanDouble("%7f");
      nexp = R.scanInt("%2d");
      bstar*=pow(10.0,nexp);
      numb = R.scanInt("%2d");
      elnum = R.scanInt("%6d");
      check1=elnum % 10;
      elnum/=10;
    } catch (IOException e) {
      throw new IllegalArgumentException("Mal-formatted line 1 detected '" + line1 + "'", e);
    }

    // "%2d %5ld %9lf %9lf %8lf %9lf %9lf %10lf %6ld|%lf %lf %lf \n",
    // "%2d %5ld %9lf %9lf %8lf %9lf %9lf %11lf %6ld %lf %lf %lf \n",
    // ^
    R = new ScanfReader(new StringReader(L2.toString()));
    try {
      cardnumb = R.scanInt("%2d");
      if (cardnumb!=2) throw new IllegalArgumentException("This isn't line 2: "+line2);
      int satnum2 = R.scanInt("%5d");
      if (satnum2 != satnum) throw new IllegalArgumentException("Not the same catalog number for each line");
      inclo = R.scanDouble("%9f");
      nodeo = R.scanDouble("%9f");
      ecco = R.scanDouble("%8f");
      argpo = R.scanDouble("%9f");
      mo = R.scanDouble("%9f");
      no = R.scanDouble(line2.charAt(52) == ' ' ? "%10f" : "%11f");
      revnum = R.scanInt("%6d");
      if (L2.length() > 70) {
        // Then there must be timing info on line 2
        startmfe = R.scanDouble("%f");
        stopmfe = R.scanDouble("%f");
        deltamin = R.scanDouble("%f");
      } else {
        startmfe = Double.NaN;
        stopmfe = Double.NaN;
        deltamin = Double.NaN;
      }
    } catch (IOException e) {
      throw new IllegalArgumentException("Mal-formatted line 2 detected '" + line2 + "'", e);
    }
    // Y2K fix -- Assumes year is between 1950 and 2049
    if (epochyr < 50) epochyr += 2000; else epochyr += 1900;
    jdsatepoch=(epochyr-1948)/4; //leap days from 1950
    if((epochyr-1948)%4==0) jdsatepoch-=1;
    jdsatepoch+=365.0*(epochyr-1950); //Days from julian date below
    jdsatepoch+=2433281.5; //1950 Jan 0
    jdsatepoch+=epochdays; //Julian rules in effect during 1950-2049!
  }
  public int compareTo(TwoLineElement arg0) {
    if(jdsatepoch<arg0.jdsatepoch) return -1;
    if(jdsatepoch==arg0.jdsatepoch) return 0;
    return 1;
  }
  
}
