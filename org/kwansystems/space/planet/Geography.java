package org.kwansystems.space.planet;

import static java.lang.Math.*;
import static org.kwansystems.space.planet.Spheroid.*;
import static org.kwansystems.tools.Scalar.*;

import java.io.*;

public class Geography {
  /** Reproduce the authalic part of the table in Snyder, p18 */
  public static void testAuthalic() {
    for(int i=90;i>=0;i-=5) {
      double diff=60.0*(toDegrees(Clarke1866.authalic(toRadians(i)))-i);
      int diffM=(int)ceil(diff);
      double diffS=(abs(diff)-abs(diffM))*60;
      System.out.printf("%6.2f   %2d'%04.1f\"\n" , (double)i, diffM, diffS);
    }
  }
  public static double calcWaterAreaFraction() throws IOException {
    byte[] row=new byte[43200];
    double lat_tile_size=PI/21600.0;
    double total_area=4*PI*WGS84.Rea*WGS84.Rea;
    InputStream inf=new FileInputStream("/mnt/big/EarthData/Cover/EarthCover5.raw");
    double acc_area=0;
    double acc_water_area=0;
    for(int i_row=0;i_row<21600;i_row++) {
      double geodetic_center_lat=linterp(-0.5,PI/2,21599.5,-PI/2,i_row);
      double geodetic_north_lat=min( PI/2,geodetic_center_lat+lat_tile_size/2);
      double geodetic_south_lat=max(-PI/2,geodetic_center_lat-lat_tile_size/2);
      double authalic_north_lat=WGS84.authalic(geodetic_north_lat);
      double authalic_south_lat=WGS84.authalic(geodetic_south_lat);
      double area=2*PI*WGS84.Rea*WGS84.Rea*(sin(authalic_north_lat)-sin(authalic_south_lat));
      
      inf.read(row);
      int n_water=0;
      for(int i_col=0;i_col<row.length;i_col++) {
        if(row[i_col]==0) n_water++;
      }
      double water_area=area*n_water/row.length;
      acc_area+=area;
      acc_water_area+=water_area;
      System.out.printf("%d %10.6f %10.6f %10.6f %10.6f\n",i_row,area,water_area,water_area/area*100,acc_water_area/acc_area*100.0);
    }
    System.out.printf("%30.10f\n",total_area/1e6);
    System.out.printf("%30.10f\n",acc_water_area/1e6);
    System.out.printf("%30.10f\n",abs(total_area-acc_area));
    System.out.printf("%30.10f\n",acc_water_area/total_area*100.0);
    return acc_water_area/total_area*100.0;
  }
  public static void main(String[] args) throws IOException {
    System.out.println(WGS84.toString());
    System.out.println(WGS72.toString());
    System.out.printf("%30.20f\n",WGS84.Cbar20*1e10);
    System.out.printf("%30.20f\n",WGS72.Cbar20*1e10);
    System.out.printf("%30.20f\n",Clarke1866.Cbar20*1e10);
  }
}
