package edu.colorado.lasp.tle;

import static java.lang.Math.*;

/** Two Line Element Set. This class handles parsing of TLEs from their two-line format into
 * a useful structure for other programs. 
 */
public class TwoLineElement implements Comparable<TwoLineElement> {
  public String line0, line1, line2;
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
  private static boolean checksum(String s) {
    byte cksum=((byte)(((byte)(s.charAt(68)))-48));
    int sum=0;
    for(int i=0;i<68;i++) {
      byte t=((byte)(s.charAt(i)));
      if(t>=48 && t<=57) {
        sum+=(t-48);
      } else if(t==45) {
        sum+=1;
      } else {
        sum+=0;
      }
    }
    return (sum%10)==cksum;
  }
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
  public TwoLineElement(String Llongstr0, String Llongstr1, String Llongstr2) {
    this(Llongstr1,Llongstr2,SGP4Core.gravconsttype.wgs72);
    line0=Llongstr0;
  }
  public TwoLineElement(String Llongstr1, String Llongstr2) {
    this(Llongstr1,Llongstr2,SGP4Core.gravconsttype.wgs72);
    line0="";
  }
  private static String strtrim(String s) {
    return s.trim();
  }
  private static String strmid(String s, int start, int len) {
    return s.substring(start, start+len);
  }
  public void setTwoLines(String Llongstr1, String Llongstr2) {
    line1=Llongstr1;
    line2=Llongstr2;
    if (line1.charAt(0)!='1') throw new IllegalArgumentException("This isn't line 1: "+line1);
    if(!checksum(line1)) throw new IllegalArgumentException("Line 1 checksum doesn't check");
    if (line2.charAt(0)!='2') throw new IllegalArgumentException("This isn't line 2: "+line2);
    if(!checksum(line2)) throw new IllegalArgumentException("Line 2 checksum doesn't check");
    satnum=Integer.parseInt(strtrim(strmid(line1,2,5)));
    if(satnum!=Integer.parseInt(strtrim(strmid(line2,2,5)))) throw new IllegalArgumentException("Line 2 sat num not same as line 1");
    epochyr=Integer.parseInt(strmid(line1,18,2));
    epochdays=Double.parseDouble(strmid(line1,20,12));
    ndot=Double.parseDouble(strmid(line1,33,10));
    double ndd6=Double.parseDouble(strtrim(strmid(line1,44,6)));
    int ndd6x=Integer.parseInt(strmid(line1,51,1))*(line1.charAt(50)=='+'?1:-1);
    nddot=ndd6/1e5*pow(10,ndd6x);
    bstar=Double.parseDouble(strtrim(strmid(line1,53,6)));
    int bstarx=Integer.parseInt(strmid(line1,60,1))*(line1.charAt(59)=='+'?1:-1);
    bstar=bstar/1e5*pow(10,bstarx);
    inclo=Double.parseDouble(strtrim(strmid(line2,8,8)));
    nodeo=Double.parseDouble(strtrim(strmid(line2,17,8)));
    ecco=Double.parseDouble(strtrim(strmid(line2,26,7)))/1e7;
    argpo=Double.parseDouble(strtrim(strmid(line2,34,8)));
    mo=Double.parseDouble(strtrim(strmid(line2,43,8)));
    no=Double.parseDouble(strtrim(strmid(line2,52,11)));
    revnum=Integer.parseInt(strtrim(strmid(line2,63,5)));
    if(line2.length()> 70) {
      startmfe=Double.parseDouble(strtrim(strmid(line2,70,12)));
      stopmfe=Double.parseDouble(strtrim(strmid(line2,82,12)));
      deltamin=Double.parseDouble(strtrim(strmid(line2,94,10)));
    } else {
      startmfe = Double.NaN;
      stopmfe = Double.NaN;
      deltamin = Double.NaN;
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
