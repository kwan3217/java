package org.kwansystems.space.ephemeris.earth.sgp4Vallado;

import static java.lang.Math.*;
import java.io.*;

import static org.kwansystems.space.planet.Spheroid.*;

import org.kwansystems.space.kepler.*;
import org.kwansystems.tools.time.Time;
import org.kwansystems.tools.time.TimeEpoch;
import org.kwansystems.tools.time.TimeScale;
import org.kwansystems.tools.time.TimeUnits;
import org.kwansystems.tools.vector.*;

public class TestJava {
  /*
   * ---------------------------------------------------------------------
   * 
   * testcpp.cpp
   * 
   * this program tests the sgp4 propagator.
   * 
   * companion code for fundamentals of astrodynamics and applications 2004 by
   * david vallado
   * 
   * (w) 719-573-2600, email dvallado@agi.com
   * ***************************************************************** current :
   * 14 aug 06 david vallado update mfe for verification time steps, constants
   * changes : 20 jul 05 david vallado fixes for paper, corrections from paul
   * crawford 7 jul 04 david vallado fix record file and get working 14 may 01
   * david vallado 2nd edition baseline 97 nasa internet version 80 norad
   * original baseline
   * ----------------------------------------------------------------
   */

  public static void main(String args[]) throws IOException {
    String infilename;
    double[] rvo = new double[6];
    char typerun;
    SGP4Core.gravconsttype whichconst;
    int whichcon;
    LineNumberReader infile;
    PrintWriter outfile;

    // ---------------------------- locals -------------------------------
    double jd, rad, tsince, startmfe, stopmfe, deltamin;
    String longstr1, longstr2;

    SGP4Core satrec;

    rad = 180.0 / PI;
    // ------------------------ implementation --------------------------

    // typerun = 'c' compare 1 year of full satcat data
    // typerun = 'v' verification run, requires modified elm file with
    // start stop and delta times
    // printf("input type of run c, v \n");
    // scanf( "%c",&typerun );
    typerun = 'v';

    // printf("input which constants 72 84 \n");
    // scanf( "%i",&whichcon );
    whichcon = 72;
    if (whichcon == 721)
      whichconst = SGP4Core.gravconsttype.wgs72old;
    else if (whichcon == 72)
      whichconst = SGP4Core.gravconsttype.wgs72;
    else whichconst = SGP4Core.gravconsttype.wgs84;

    // ---------------- setup files for operation ------------------
    // input 2-line element set file
    // printf("input elset filename: \n");
    // scanf( "%s",&infilename );
    infilename = "Data/EarthSatSGP4Vallado/CPP/sgp4-ver.tle";
    infile = new LineNumberReader(new FileReader(infilename));
    // infile = fopen(infilename, "r");

    if (typerun == 'c')
      outfile = new PrintWriter(new FileWriter("tcppall.out"));
    else {
      if (typerun == 'v')
        outfile = new PrintWriter(new FileWriter("tcppver.out"));
      else
        outfile = new PrintWriter(new FileWriter("tcpp.out"));
    }

    // dbgfile = fopen("sgp4test.dbg", "w");
    // fprintf(dbgfile,"this is the debug output\n\n" );

    // ----------------- test simple propagation -------------------
    String S = infile.readLine();
    while (S != null) {
      do {
        longstr1 = S;
        S = infile.readLine();
      } while ((longstr1.charAt(0) == '#') && (S != null));

      if (S != null) {
        longstr2 = S;
        S = infile.readLine();
        // convert the char string to sgp4 elements
        // includes initialization of sgp4
        TwoLineElement TLE=new TwoLineElement(longstr1, longstr2, whichconst);
        satrec = new SGP4Core(TLE);
        startmfe = TLE.startmfe;
        stopmfe = TLE.stopmfe;
        deltamin = TLE.deltamin;
        outfile.printf("%d xx\n", TLE.satnum);
        System.out.printf(" %d\n", TLE.satnum);
        try {
          // call the propagator to get the initial state vector value
          rvo = satrec.sgp4(0.0);
        } catch (SGP4Exception ex) {
          throw new RuntimeException(ex);
        }

        jd = TLE.jdsatepoch;

        outfile.printf(" %16.8f %16.8f %16.8f %16.8f %12.9f %12.9f %12.9f\n",
            0.0, rvo[0], rvo[1], rvo[2], rvo[3], rvo[4], rvo[5]);

        tsince = startmfe;
        // check so the first value isn't written twice
        if (abs(tsince) > 1.0e-8)
          tsince = tsince - deltamin;

        // loop to perform the propagation
        int error=0;
        while ((tsince < stopmfe) && (error == 0)) {
          tsince = tsince + deltamin;

          if (tsince > stopmfe)
            tsince = stopmfe;
          try {
            rvo = satrec.sgp4(tsince);
          } catch (SGP4Exception ex) {
            System.out.printf("# *** error: t:= %f *** code = %3d\n", ex.tsince,
                ex.code);
            error=ex.code;
          }

          if (error == 0) {
            jd = TLE.jdsatepoch + tsince / 1440.0;
            Time T=new Time(jd,TimeUnits.Days,TimeScale.UTC,TimeEpoch.JD);
            
            if ((typerun != 'v') && (typerun != 'c')) {
              
              

              outfile.println(T.toString());
            } else {
              outfile.printf(
                  " %16.8f %16.8f %16.8f %16.8f %12.9f %12.9f %12.9f", tsince,
                  rvo[0], rvo[1], rvo[2], rvo[3], rvo[4], rvo[5]);
              T.Units=TimeUnits.Seconds;
              Elements E = new Elements(new MathStateTime(rvo,new Time(jd,TimeUnits.Days,TimeScale.UTC,TimeEpoch.JD)), WGS84km.GM, "km");
              T.Units=TimeUnits.Days;
              outfile.printf(
                      " %14.6f %8.6f %10.5f %10.5f %10.5f %10.5f %10.5f %s\n",
                      E.A, E.E, E.I * rad, E.LAN * rad,
                      E.AP * rad, E.TA * rad, E.M * rad, T.toString());
            }
          } // if satrec.error == 0

        } // while propagating the orbit

      } // if not eof

    } // while through the input file
    outfile.close();
    infile.close();
  } // end testcpp

}
