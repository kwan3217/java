/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kwansystems.rollercoasterometer;

import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;

/**
 *
 * @author jeppesen
 */
public class NMEALevel0B {

  public static boolean checkNMEA(String nmea) {
    try {
      String checksumPart = nmea.substring(nmea.length() - 2);
      String dataPart = nmea.substring(1, nmea.length() - 3);
      int checksum = Integer.parseInt(checksumPart, 16);
      int datasum = 0;
      for (int i = 0; i < dataPart.length(); i++) {
        datasum ^= dataPart.codePointAt(i);
      }
      return datasum == checksum;
    } catch (NumberFormatException E) {
      return false;
    } catch (StringIndexOutOfBoundsException E) {
      return false;
    }
  }

  public static void main(String[] args) throws IOException {
//    process("Data/Rollercoasterometry/CandaceEvening","RKTO_","001");
//    process("Data/Rollercoasterometry/CandaceEvening","RKTO_","002");
//    process("Data/Rollercoasterometry/Cand7aceEvening","RKTO_","003");
//    process("Data/Rollercoasterometry/CandaceEvening","RKTO_","004");
//    process("Data/Rollercoasterometry/SATtoDEN100808","RKTO_","005");
//    process("Data/Rollercoasterometry/SATtoDEN100808","RKTO_","006");
//    process("Data/Rollercoasterometry/SATtoDEN100808","RKTO_","007");
//    process("Data/Rollercoasterometry/UltimateCalDance","RKTO_","000");
//    process("Data/Rollercoasterometry/UltimateCalDance","RKTO_","001");
//    process("Data/Rollercoasterometry/UltimateCalDance","RKTO_","Silent");
//    process("Data/Rollercoasterometry/UltimateCalDance","RKTO_","002");
//    process("Data/Rollercoasterometry/UltimateCalDance","RKTO_","004");
//    process("Data/Rollercoasterometry/UltimateCalDance","RKTO_","005");
    //process("Data/Rollercoasterometry/Flight21Aug2010","RKTO_","000");
//    process("Data/Rollercoasterometry/Flight21Aug2010","RKTO_","001");
    process("Data/Rollercoasterometry/RocketometerMk6","RKTO_","000");
  }
  public static void process(String Path,String Prefix, String Serial) throws IOException {
    LineNumberReader inf = new LineNumberReader(new FileReader(Path + "/Level0A/RKTO_" + Serial + ".txt"));
    String S = inf.readLine();
    int oufnum = 0;
    int linenum = 0;
    int anum = 0;
    int min0 = oufnum * 5;
    int hr0 = min0 / 60;
    min0 %= 60;
    int min1 = (oufnum + 1) * 5;
    int hr1 = min1 / 60;
    min1 %= 60;
    long[] pwr = new long[9];
    long[] abg = new long[9];
    int gpsChunk = 0;
    int gpsLines = 0;

    if(!(new File(Path+"/Level0B").exists())) new File(Path+"/Level0B").mkdirs();
    if(!(new File(Path+"/Level0C").exists())) new File(Path+"/Level0C").mkdirs();
    if(!(new File(Path+"/Level0D").exists())) new File(Path+"/Level0D").mkdirs();
    if(!(new File(Path+"/Level1" ).exists())) new File(Path+"/Level1" ).mkdirs();
    String oufnFormat = Path + "/Level0B/RCO_" + Serial + "_0B_%02d%02d00_%02d%02d00.nmea";
    String oufn = String.format(oufnFormat, hr0, min0, hr1, min1);
    String lcfnFormat = Path + "/Level0C/RCO_" + Serial + "_0C_Analog_%02d%02d00_%02d%02d00.nmea";
    String lcfn = String.format(lcfnFormat, hr0, min0, hr1, min1);
    String clfn = Path + "/Level0D/RCO_" + Serial + "_0D_Clock.csv";
    String anfn = Path + "/Level1/RCO_" + Serial + "_1A_Analog.csv";
    String pwfn = Path + "/Level1/RCO_" + Serial + "_1B_Power.csv";
    String acfn = Path + "/Level1/RCO_" + Serial + "_1B_Accel.csv";
    String bffn = Path + "/Level1/RCO_" + Serial + "_1B_BField.csv";
    String gyfn = Path + "/Level1/RCO_" + Serial + "_1B_Gyro.csv";
    String gpxfn = Path + "/Level1/RCO_" + Serial + "_1C_Track.gpx";
    String nmfn = Path + "/Level0D/RCO_" + Serial + "_0D_GPS.nmea";
    String abgfn = Path + "/Level1/RCO_" + Serial + "_1C_ABG.csv";
    String gpfnFormat = Path + "/Level0D/RCO_" + Serial + "_0D_GPS_%02d.csv";
    String gpfn = String.format(gpfnFormat, gpsChunk);
    PrintWriter ouf = new PrintWriter(new FileWriter(oufn));
    PrintWriter gpxf = new PrintWriter(new FileWriter(gpxfn));
    PrintWriter clf = new PrintWriter(new FileWriter(clfn));
    PrintWriter lcf = new PrintWriter(new FileWriter(lcfn));
    PrintWriter pwf = new PrintWriter(new FileWriter(pwfn));
    PrintWriter acf = new PrintWriter(new FileWriter(acfn));
    PrintWriter bff = new PrintWriter(new FileWriter(bffn));
    PrintWriter gyf = new PrintWriter(new FileWriter(gyfn));
    PrintWriter abgf = new PrintWriter(new FileWriter(abgfn));
    PrintWriter anf = new PrintWriter(new FileWriter(anfn));
    long binlimit=1;

    pwf.println("\"Rollercoasterometer power\"");
    pwf.println("\"Each row is the sum of "+binlimit+" consecutive $PKWNA rows\"");
    pwf.println("\"Column A is the internal battery voltage measurement, VBat=Vcc/2.2628\"");
    pwf.println("\"Column B is the reference voltage generated by the IDG300, 1.23V\"");
    pwf.println("\"Column C is the ground voltage, nominally 0V\"");
    pwf.println("\"Each cell is the sum of 1600 consecutive measurements between 0(=0V) and 1023(=Vcc)\"");
    pwf.println("\"Together this is enough information to track the battery voltage.\"");

    anf.println("\"Rollercoasterometer Analog Summary\"");
    anf.println("\"Each row is the sum of "+binlimit+" consecutive $PKWNA rows\"");
    anf.println("\"Column A is the internal battery voltage measurement, VBat=Vcc/2.2628\"");
    anf.println("\"Column B is the Accelerometer X axis\"");
    anf.println("\"Column C is the Accelerometer Y axis\"");
    anf.println("\"Column D is the Accelerometer Z axis\"");
    anf.println("\"Column E is the reference voltage generated by the IDG300, 1.23V\"");
    anf.println("\"Column F is the rotation rate sensor X axis\"");
    anf.println("\"Column G is the rotation rate sensor Y axis\"");
    anf.println("\"Column H is the rotation rate sensor Z axis\"");
    anf.println("\"Column I is the ground voltage, nominally 0V\"");
    anf.println("\"Each cell is the sum of 1600 consecutive measurements between 0(=0V) and 1023(=Vcc)\"");

    abgf.println("\"Rollercoasterometer Digital Summary\"");
    abgf.println("\"Column B is the timestamp of the immediately preceding analog measurement in seconds\"");
    abgf.println("\"Column C is the Accelerometer temperature\"");
    abgf.println("\"Column D is the Accelerometer X axis\"");
    abgf.println("\"Column E is the Accelerometer Y axis\"");
    abgf.println("\"Column F is the Accelerometer Z axis\"");
    abgf.println("\"Column G is the B field X axis\"");
    abgf.println("\"Column H is the B field Y axis\"");
    abgf.println("\"Column I is the B field Z axis\"");
    abgf.println("\"Column J is the rotation rate sensor temperature\"");
    abgf.println("\"Column K is the rotation rate sensor X axis\"");
    abgf.println("\"Column L is the rotation rate sensor Y axis\"");
    abgf.println("\"Column M is the rotation rate sensor Z axis\"");
    abgf.println("\"Column N is new Acc data\"");
    abgf.println("\"Column O is new BField data\"");
    abgf.println("\"Column P is new Gyro data\"");
    abgf.println("\"Column Q is GPS latitude in degrees, positive is north\"");
    abgf.println("\"Column R is GPS longitude in degrees, positive is east\"");
    abgf.println("\"Column S is GPS MSL elevation in meters\"");
    abgf.println("\"Column T is new GPS data\"");
    abgf.println(",tsec,at,ax,ay,az,bx,by,bz,gt,gx,gy,gz,an,bn,gn,lat,lon,alt,date,time,gpn");

    PrintWriter nmf = new PrintWriter(new FileWriter(nmfn));
    PrintWriter gpf = new PrintWriter(new FileWriter(gpfn));
    gpf.println(csvState.getHeader());
    gpxf.println(gpxState.getHeader());

    int atime=0,asec=0;
    int[] acc=new int[4];
    int[] bfl=new int[3];
    int[] gyr=new int[4];
    double[] gps=new double[5];
    int anew=0,bnew=0,gnew=0,gpnew=0;
    while (S != null) {
      checkNMEA(S);
      if (!S.contains("PKWNE") && checkNMEA(S)) {
        S = S.substring(0, S.length() - 3); //Don't need checksum any more
        ouf.println(S);

        String[] part = S.split(",");
        if (part[0].equals("$PKWNA")) {
          lcf.println(S);
          for (int i = 0; i < pwr.length; i++) {
            pwr[i] += Integer.parseInt(part[i + 2]);
          }
          anum++;
          if (anum == binlimit) {
            pwf.printf("%d,%d,%d\n", pwr[0], pwr[4], pwr[8]);
            anf.printf("%d,%d,%d,%d,%d,%d,%d,%d,%d\n", pwr[0], pwr[1], pwr[2], pwr[3], pwr[4], pwr[5], pwr[6], pwr[7], pwr[8]);
            for (int i = 0; i < pwr.length; i++) {
              pwr[i] = 0;
            }
            anum = 0;
          }
          int newatime=Integer.parseInt(part[1]);
          if(newatime<atime)asec++;
          atime=newatime;
        }
        if (part[0].equals("$PKWNI")) {
          lcf.println(S);
          if(part[1].equals("40")) {
            acf.println(S);
            acc[0]=Integer.parseInt(part[8]);  //Temperature
            acc[1]=Integer.parseInt(part[5])>>2;  //X
            acc[2]=Integer.parseInt(part[6])>>2;  //Y
            acc[3]=Integer.parseInt(part[7])>>2;  //Z
//   abgf.println("\"Column B is the timestamp of the immediately preceding analog measurement in seconds\"");
//    abgf.println("\"Column C is the Accelerometer temperature\"");
//    abgf.println("\"Column D is the Accelerometer X axis\"");
//    abgf.println("\"Column E is the Accelerometer Y axis\"");
//    abgf.println("\"Column F is the Accelerometer Z axis\"");
//    abgf.println("\"Column G is the B field X axis\"");
//    abgf.println("\"Column H is the B field Y axis\"");
//    abgf.println("\"Column I is the B field Z axis\"");
//    abgf.println("\"Column J is the rotation rate sensor temperature\"");
//    abgf.println("\"Column K is the rotation rate sensor X axis\"");
//    abgf.println("\"Column L is the rotation rate sensor Y axis\"");
//    abgf.println("\"Column M is the rotation rate sensor Z axis\"");
//    abgf.println("\"Column N is new Acc data\"");
//    abgf.println("\"Column O is new BField data\"");
//    abgf.println("\"Column P is new Gyro data\"");
//    abgf.println("\"Column Q is GPS latitude in degrees, positive is north\"");
//    abgf.println("\"Column R is GPS longitude in degrees, positive is east\"");
//    abgf.println("\"Column S is GPS MSL elevation in meters\"");
//    abgf.println("\"Column T is UTC Date\"");
//    abgf.println("\"Column U is UTC Time\"");
//    abgf.println("\"Column V is new GPS data\"");
            anew=1;      //     b  c  d  e  f 
            abgf.printf(",%d.%03d,%d,%d,%d,%d",asec,atime,acc[0],acc[1],acc[2],acc[3]);
                         //g  h  i
            abgf.printf(",%d,%d,%d",bfl[0],bfl[1],bfl[2]);
                         //j  k  l  m
            abgf.printf(",%d,%d,%d,%d",gyr[0],gyr[1],gyr[2],gyr[3]);
                         //n  o  p
            abgf.printf(",%d,%d,%d",anew,bnew,gnew);
                         //    q      r     s  
            abgf.printf(",%10.6f,%11.6f,%6.1f",gps[0],gps[1],gps[2]);
                         //    t       u  v
            abgf.printf(",%06.0f,%010.3f,%d\n",gps[3],gps[4],gpnew);
            anew=0;
            bnew=0;
            gnew=0;
            gpnew=0;
          } else if(part[1].equals("69")) {
//            try {
              if(part.length<7) {
                System.out.println(S);
              } else {
                gyr[0]=Integer.parseInt(part[3]);  //Temperature
                gyr[1]=Integer.parseInt(part[4]);  //X
                gyr[2]=Integer.parseInt(part[5]);  //Y
                gyr[3]=Integer.parseInt(part[6]);  //Z
                gnew=1;
              }
//            } catch (Throwable e) {}
            gyf.println(S);
          } else if(part[1].equals("1E")) {
//            try {
              bfl[0]=Integer.parseInt(part[3]);  //X
              bfl[1]=Integer.parseInt(part[4]);  //Y
              bfl[2]=Integer.parseInt(part[5]);  //Z
              bnew=1;
//            } catch (Throwable e) {}
            bff.println(S);
          }
        }
        if (part[0].equals("$PKWNP")) {
          clf.println(S);
          nmf.println(S);
        }
        if (part[0].startsWith("$GP")) {
          nmf.println(S);
          gpsLines++;
          if (gpsLines >= 50000) {
            gpf.println(csvState.getFooter());
            gpf.close();
            gpsChunk++;
            gpsLines = 0;
            gpfn = String.format(gpfnFormat, gpsChunk);
            gpf = new PrintWriter(new FileWriter(gpfn));
            gpf.println(csvState.getHeader());
          }
          csvState.handleGPS(gpf, part);
          gpxState.handleGPS(gpxf, part);
          if(part[0].startsWith("$GPGGA")) {
            gpnew=1;
            gps[0]=gpxState.latDeg;
            gps[1]=gpxState.lonDeg;
            gps[2]=gpxState.mslAlt;
            gps[3]=gpxState.utcDate;
            gps[4]=gpxState.utcTime;
          }
        }
        if (part[0].startsWith("$GPZDA")) {
          clf.println(S);
        }
        if (part[0].equals("$PKWNL")) {
          clf.println(S);
          linenum++;
          if (linenum == 300) {
            ouf.close();
            lcf.close();
            linenum = 0;
            oufnum++;
            min0 = oufnum * 5;
            hr0 = min0 / 60;
            min0 %= 60;
            min1 = (oufnum + 1) * 5;
            hr1 = min1 / 60;
            min1 %= 60;
            oufn = String.format(oufnFormat, hr0, min0, hr1, min1);
            lcfn = String.format(lcfnFormat, hr0, min0, hr1, min1);
            System.out.println(oufn);
            ouf = new PrintWriter(new FileWriter(oufn));
            lcf = new PrintWriter(new FileWriter(lcfn));
          }
        }
      }
      S = inf.readLine();
    }
    ouf.close();
    inf.close();
    clf.close();
    lcf.close();
    pwf.close();
    gpf.println(csvState.getFooter());
    gpf.close();
    acf.close();
    bff.close();
    gyf.close();
    abgf.close();
    gpxf.println(gpxState.getFooter());
    gpxf.close();
    anf.close();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface VariableDesc {
    String title();
    String units() default "";
    String format();
  }
  private abstract static class GPSState {
    @VariableDesc(title = "UTC Date", units = "ddmmyy", format = "%06d")    int utcDate;
    @VariableDesc(title = "UTC Time", units = "hhmmss.sss", format = "%010.3f")    double utcTime;
    @VariableDesc(title = "Latitude", units = "deg", format = "%012.6f")    double latDeg;
    @VariableDesc(title = "Longitude", units = "deg", format = "%013.6f")    double lonDeg;
    @VariableDesc(title = "Status", units = "", format = "%1d")    byte status;
    @VariableDesc(title = "fixType", units = "", format = "%1d")    byte fixType;
    @VariableDesc(title = "Sats Used", units = "", format = "%2d")    byte satsUsed;
    @VariableDesc(title = "Horizontal DOP", units = "", format = "%4.1f")    double hdop;
    @VariableDesc(title = "Altitude above MSL", units = "m", format = "%6.1f")    double mslAlt;
    @VariableDesc(title = "Geoid Alt", units = "m", format = "%5.1f")    double geoidAlt;
    @VariableDesc(title = "Age of Dif Corr", units = "s", format = "%4.1f")    double DifCor;
    @VariableDesc(title = "DifID", units = "", format = "%s")    String DifID;
    @VariableDesc(title = "Speed", units = "kt", format = "%6.2f")    double speed;
    @VariableDesc(title = "Course", units = "deg", format = "%6.2f")    double course;
    @VariableDesc(title = "Magnetic Variation", units = "deg", format = "%6.2f")    double magVar;
    @VariableDesc(title = "Sats Visible", units = "", format = "%2d")    int satsVisible;
    int[] satAz;
    int[] satEl;
    int[] satPow;

    public GPSState() {
      satAz = new int[51];
      satEl = new int[51];
      satPow = new int[51];
      for (int i = 0; i < satPow.length; i++) {
        satPow[i] = -1;
      }
    }
    public abstract String getHeader();
    public abstract String getFooter();
    @Override
    public abstract String toString();
    public void handleGPS(PrintWriter gpf, String[] part) {
      if (part[0].contains("GGA")) {
        if (part[1].length() > 0) {
          utcTime = Double.parseDouble(part[1]);
        }
        if (part[2].length() > 0) {
          latDeg = Double.parseDouble(part[2]);
          double deg = Math.floor(latDeg / 100);
          double min = latDeg - (deg * 100);
          latDeg = deg + (min /= 60);
          if (part[3].charAt(0) == 'S') {
            latDeg *= -1;
          }
        }
        if (part[4].length() > 0) {
          lonDeg = Double.parseDouble(part[4]);
          double deg = Math.floor(lonDeg / 100);
          double min = lonDeg - (deg * 100);
          lonDeg = deg + (min /= 60);
          if (part[5].charAt(0) == 'W') {
            lonDeg *= -1;
          }
        }
        if (part[6].length() > 0) {
          fixType = Byte.parseByte(part[6]);
        } else {
          fixType = 0;
        }
        if (part[7].length() > 0) {
          satsUsed = Byte.parseByte(part[7]);
        } else {
          satsUsed = 0;
        }
        if (part[8].length() > 0) {
          hdop = Double.parseDouble(part[8]);
        } else {
          hdop = -1;
        }
        if (part[9].length() > 0) {
          mslAlt = Double.parseDouble(part[9]);
        }
        if (part[11].length() > 0) {
          geoidAlt = Double.parseDouble(part[11]);
        }
        if (part[13].length() > 0) {
          DifCor = Double.parseDouble(part[13]);
        } else {
          DifCor = -1;
        }
        DifID = part[14];
      } else if (part[0].contains("GSV")) {
        if (Integer.parseInt(part[2]) == 1) {
          for (int i = 0; i < 32; i++) {
            satPow[i] = -1;
          }
        }
        satsVisible = Integer.parseInt(part[3]);
        for (int i = 0; i < 4; i++) {
          if (i * 4 + 4 < part.length) {
            int satNum = Integer.parseInt(part[i * 4 + 4]) - 1;
            if (satNum >= satPow.length) {
              System.out.println("Stuff!");
            }
            satEl[satNum] = Integer.parseInt(part[i * 4 + 5]);
            satAz[satNum] = Integer.parseInt(part[i * 4 + 6]);
            if (i * 4 + 7 < part.length && part[i * 4 + 7].length() > 0) {
              satPow[satNum] = Integer.parseInt(part[i * 4 + 7]);
            } else {
              satPow[satNum] = 0;
            }
          }
        }
      } else if (part[0].contains("RMC")) {
        //Only take what's not in GGA
        status = (byte) (part[2].charAt(0) == 'A' ? 1 : 0);
        if (part[7].length() > 0) {
          speed = Double.parseDouble(part[7]);
        }
        if (part[8].length() > 0) {
          course = Double.parseDouble(part[8]);
        }
        if (part[9].length() > 0) {
          utcDate = Integer.parseInt(part[9]);
        }
//      if(part[10].length()>0) gpsState.magVar=Double.parseDouble(part[10]);
        //This message is at the end of the cycle, so print out here
        gpf.println(this);
      }
    }
  }
  private static class GPXState extends GPSState {
    @Override
    public String getHeader() {
      return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n"+
             "<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"Rollercoasterometer\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">"+
             " <trk>\n"+
             "  <trkseg>";
    }
    @Override
    public String toString() {
      if(latDeg==0) return "";
      return String.format("   <trkpt lat=\"%12.7f\" lon=\"%12.7f\">\n",latDeg,lonDeg)+
             String.format("    <ele>%8.2f</ele>\n",mslAlt)+
             String.format("    <time>%04d-%02d-%02dT%02d:%02d:%06.3f</time>\n",utcDate%100,utcDate%10000/100,utcDate/10000+2000,(int)(utcTime/10000),(int)(utcTime%10000/100),utcTime%100)+
                           "   </trkpt>";
    }
    @Override
    public String getFooter() {
      return "  </trkseg>\n"+
             " </trk>\n"+
             "</gpx>";
    }
  }
  private static class CSVState extends GPSState {

    public String getHeader() {
      Class cc = getClass();
      StringBuilder result = new StringBuilder();
      for (Field f : cc.getDeclaredFields()) {
        if (f.isAnnotationPresent(VariableDesc.class)) {
          VariableDesc a = f.getAnnotation(VariableDesc.class);
          result.append(",");
          result.append(a.title());
          if (a.units().length() > 0) {
            result.append(" (").append(a.units()).append(")");
          }
        }
      }

      for (int i = 0; i < satPow.length; i++) {
        result.append(String.format(",Sat Az %02d,Sat El %02d,Sat Pow %02d", i, i, i));
      }
      result.append(",Sats Heard");
      result.deleteCharAt(0); //Get rid of leading comma
      return result.toString();
    }

    public String getFooter() {
      return "";
    }

    public String toString() {
      Class cc = getClass();
      StringBuffer result = new StringBuffer();
      for (Field f : cc.getDeclaredFields()) {
        if (f.isAnnotationPresent(VariableDesc.class)) {
          VariableDesc a = f.getAnnotation(VariableDesc.class);
          try {
            result.append(",");
            result.append(String.format(a.format(), f.get(this)));
          } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
          }
        }
      }
      int satsHeard = 0;
      for (int i = 0; i < satPow.length; i++) {
        if (satPow[i] < 0) {
          result.append(",,,");
        } else {
          result.append(String.format(",%3d,%2d,%2d", satAz[i], satEl[i], satPow[i]));
          if (satPow[i] > 0) {
            satsHeard++;
          }
        }
      }
      result.append(String.format(",%2d", satsHeard));
      result.deleteCharAt(0); //Get rid of leading comma
      return result.toString();
    }
  }
  private static GPSState csvState = new CSVState();
  private static GPSState gpxState = new GPXState();
}
