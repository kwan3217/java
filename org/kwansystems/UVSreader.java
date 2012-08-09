/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems;

import java.io.*;
import java.lang.reflect.*;

/**
 *
 * @author chrisj
 */
public class UVSreader {
  public float fds=0; //FDS count
  public short spno=0		; //Spectrum Number
  public short scid=0		; //Space Craft ID
  public short data_mode=0	; //Data mode
  public short scet_yr=0	; //Space Craft Event Time (YEAR)
  public short scet_day=0	; //Space Craft Event Time (DAY)
  public short scet_hr=0	; //Space Craft Event Time (HOUR)
  public short scet_min=0	; //Space Craft Event Time (MIN)
  public short scet_sec=0	; //Space Craft Event Time (SEC)
  public short scet_ms=0	; //Space Craft Event Time (MS)

		//; History
  public short fpn=0			; //FPN spectrum number
  public short cal=0			; //CAL spectrum number
  public short scat=0		; //SCAT matrix number

		// counting mode and HV level
  public short counting_mode=0	; //Counting mode
  public short hv=0			; //High Voltage Level

		// integration time and scale factor
  public float dt=0; //Integration time
  public float scale=0		; //Scale Factor

		// UVS original pointing
  public float ag_az=0		; //Air-Glow Azimuth
  public float ag_el=0		; //Air-Glow Elevation
  public float ag_ra=0		; //Air-Glow Right-Ascension
  public float ag_dec=0		; //Air-Glow Declination
  public float occ_ra=0		; //Occultation Right-Ascension
  public float occ_dec=0		; //Occultation Declination
  public float[] dl=new float[5]	; //Delta L [5 values]
  public float[] dw=new float[5]	; //Delta W [5 values]
  public float[] rot=new float[4]	; //Rotation matrix [A,B,C,D]
  public float sun_ra=0		; //Sun Right-Ascension
  public float sun_dec=0		; //Sun Declination
  public float pba=0		; //Pitch Bias Angle
  public float yba=0		; //Yaw Bias Angle

		// footprint
  public short pb_id=0		; //Picture Body ID
  public short cb_id=0		; //Central Body ID
  public float[] sc_cb=new float[3]	; //S/C, Central Body Centered, EME50
  public float[] pb_sc=new float[3]	; //Picture Body, S/C Centered, EME50
  public float[] sun_sc=new float[3]	; //Sun (unit vector), S/C Centered, EME50
  public float sc_cb_range=0	; //S/C - Central Body Range
  public float sc_pb_range=0	; //S/C - Picture Body Range
  public float sc_sun_range=0	; //S/C - Sun Range
  public float pb_subsl_lat=0	; //Picture Body Subsolar Latitude
  public float pb_subsl_lon=0	; //Picture Body Subsolar Longitude
  public float pb_subsc_lat=0	; //Picture Body Sub-S/C Latitude
  public float pb_subsc_lon=0	; //Picture Body Sub-S/C Longitude
  public float cb_subsc_lon=0	; //Central Body Sub-S/C Longitude
  public float sc_phase_ang=0	; //S/C Phase Angle
  public float pb_semi_diam=0	; //Picture Body Angular Semi-diameter

  public float scan_az=0		; //Azimuth of Scan Platform
  public float scan_el=0		; //Elevation of Scan Platform
  public float scan_twist=0	; //Twist Angle of Scan Platform

  public float p5_lat=0		; //P5 Planetodetic Latitude
  public float p5_lon=0		; //P5 Longitude
  public float p5_incidence=0	; //P5 Solar Incidence Angle
  public float p5_emission=0	; //P5 S/C Emission Angle
  public float p5_phase=0		; //P5 S/C Phase Angle
  public float p5_pca_altitude=0	; //P5 PCA Altitude
  public float p5_pca_hour_angle=0	; //P5 PCA Hour Angle
  public float p5_slant_range=0	; //P5 Slant Range

  public float p2_lat=0		; //P2 Planetodetic Latitude
  public float p2_lon=0		; //P2 Longitude
  public float p2_incidence=0	; //P2 Solar Incidence Angle
  public float p2_emission=0	; //P2 S/C Emission Angle
  public float p2_phase=0		; //P2 S/C Phase Angle
  public float p2_pca_altitude=0	; //P2 PCA Altitude
  public float p2_pca_hour_angle=0	; //P2 PCA Hour Angle
  public float p2_slant_range=0	; //P2 Slant Range

  public float p8_lat=0		; //P8 Planetodetic Latitude
  public float p8_lon=0		; //P8 Longitude
  public float p8_incidence=0	; //P8 Solar Incidence Angle
  public float p8_emission=0	; //P8 S/C Emission Angle
  public float p8_phase=0		; //P8 S/C Phase Angle
  public float p8_pca_altitude=0	; //P8 PCA Altitude
  public float p8_pca_hour_angle=0	; //P8 PCA Hour Angle
  public float p8_slant_range=0	; //P8 Slant Range

  public float p5_X=0		; //P5 X Position in System
  public float p5_Y=0		; //P5 Y Position in System
  public float p5_XM=0		; //P5 X Position in System
  public float p5_YM=0		; //P5 Y Position in System
  public float slit_tilt=0		; //Slit Tilt wrt System

		// status words
  public short pointing_bad=0	; //original pointing flagged bad OR uncorrectable glitch
  public short azel_corrected=0	; //AZ/EL pointing glitch was corrected
  public short footprint_bad=0	; //footprint quantities could not be calculated
  public short spectrum_bad=0	; //spectra data quality is bad

		// Julian date
  public double jd=0		; //Julian Date

		// UVS data vector with 6 shift channels to either side
  public float[] lshift=new float[6]	; //6 Shift channels (shortward)
  public float[] data=new float[126]	; //126 Data channels
  public float[] rshift=new float[6];
  public UVSreader(RandomAccessFile Inf) throws IllegalAccessException, IOException {
    Class c=this.getClass();
    Field[] ff=c.getFields();
    for(Field f:ff) {
      Class t=f.getType();
      if(t.isArray()) {
        float[] thisArray=(float[])f.get(this);
        for(int i=0;i<thisArray.length;i++)thisArray[i]=Inf.readFloat();
      } else if(t.isPrimitive()) {
        if(t.getCanonicalName().equals("float")) {
          f.setFloat(this, Inf.readFloat());
        } else if(t.getCanonicalName().equals("double")) {
          f.setDouble(this, Inf.readDouble());
        } else if(t.getCanonicalName().equals("short")) {
          f.setShort(this, Inf.readShort());
        }
      }
    }
  }
  @Override
public String toString() {
    return String.format("\"%04d/%03d %02d:%02d:%02d.%03d\",%f,%f,%f",scet_yr,scet_day,scet_hr,scet_min,scet_sec,scet_ms,
             ((scet_hr*60+scet_min)*60+scet_sec)+scet_ms/1000.0,scan_az,scan_el);
  }
  public static void main(String args[]) throws IOException, IllegalAccessException {
    RandomAccessFile Inf=new RandomAccessFile("/mnt/big/codebase/pov/SpiceSolar/t890825.dat","r");
    PrintWriter ouf=new PrintWriter(new FileWriter("/mnt/big/codebase/pov/SpiceSolar/t890825.csv"));
    while(Inf.getFilePointer()<Inf.length()) {
      UVSreader u=new UVSreader(Inf);
      if(u.scan_az!=0) ouf.println(u);
    }
    ouf.close();
  }
}
