/* I attempted to optimize this code for clarity and readability,
 * not size or speed. It should be easier to understand and translate
 * to whatever is your language of choice this way. In languages with
 * array operations, like IDL, this code can be vastly improved by
 * replacing the for-loops with array operations.
 */

import static java.lang.Math.*;

public class bbox_lat_lon {
  /**Desired kilometers per pixel in map grid. Each pixel
     will have an area of almost exactly kmperpix*kmperpix km^2.
     In the typical case of kmperpix=5, pixels near the pole
     are 5km x 5km, while pixels towards the edge are closer to 4x6, but
     still exactly the same area

     All publically available data products currently use kmperpix=5. There
     is a field in the data products from 1B up which says what kmperpix is.
   */
  public static final double kmperpix=5.0;
  /** Radius of generating globe, km. This projection formula presumes a perfect
   *  sphere, and for CIPS we use the equatorial radius of the WGS-84 spheroid */
  public static final double R=6378.137;
  /** Latitude of edge of map in radians. The map grid will be a square
   * which circumscribes the projection of this circle of latitude */
  public static final double lat_min=toRadians(30);
   /** A map projection is used to represent the surface of a sphere on a plane.
    *  For CIPS, the sphere is the cloud deck, an imaginary surface 83km above
    *  the surface of the Earth, and the plane is an image represented by a 2D
    *  array. This 2D array is called the map grid, and each element of it is a
    *  map pixel.
    *
    *  The projection is used to figure out which pixel on the image represents
    *  which point on the sphere, and vice versa.
    *
    *  The projection formula takes the geographic (latitude and longitude)
    *  coordinates of a point and from them calculates the X and Y coordinates
    *  of the mapping of that point on the flat map grid. If you know the
    *  latitude and longitude of a city or ground station, the formula allows
    *  you to plot the station on the map. If you know the coordinates of many
    *  points on a line or curve, you project them all using these formulas,
    *  then draw a line or curve through the points on the map grid. If you know
    *  the color of each point in an area, project all the points in the area,
    *  then paint each point on the map the corresponding color.
    *
    *  The Lambert projection was invented by Johann Lambert, and my reference
    *  is USGS Special Publication 1395, available online at
    *
    *     http://pubs.er.usgs.gov/djvu/PP/PP_1395.pdf
    *
    *  The Lambert projection is documented on pp182-190. We use the spherical
    *  form of the polar aspect.
    *
    *  The polar form of the projection formula is: */
  public static double rhoFwd(double lat, boolean south) {
    //Units of latitude are radians
    //return units are pixels
    if(south) lat=-lat;
    double rhokm=2*R*sin(PI/4.0-lat/2);
    double rhopix=rhokm/maxrho*mapSize/2;
    return rhopix;
  }
  public static double thetaFwd(double lon, double lon0, boolean south) {
    //Units of longitude are radians
    if(south) {
      lon=-lon;
      lon0=-lon0;
    }
    return lon-lon0-PI/2;
  }
  public static double xFwd(double lat, double lon, double lon0, boolean south) {
    //Input uints are radians
    return rhoFwd(lat,south)*cos(thetaFwd(lon,lon0,south))+x0;
  }
  public static double yFwd(double lat, double lon, double lon0, boolean south) {
    //Input uints are radians
    return rhoFwd(lat,south)*sin(thetaFwd(lon,lon0,south))+y0;
  }
    /** where

    lat is the latitude of a point to project
    lon is the longitude of that point
    rho is the polar radial coordinate in the map grid
    theta is the polar angular coordinate in the map grid
    x is the rectangular horizontal coordinate in the map grid
    y is the rectangular vertical coordinate in the map grid
    x0 is the "false easting" which is added to make the x coordinate positive
              for all points (See below for how to calculate it)
    y0 is the "false northing" used for the same reason for the y coordinate.
              On a square map, which is the normal CIPS case, x0 = y0
    lon0 is the "center longitude" used to rotate the map around the pole. We
              use this on CIPS to rotate the single-orbit maps such that the
              orbit swath is roughly horizontal, and the bounding box area is
              reduced. The center longitude is included in all single-orbit
              data products which are map projected (1B, 1C, 2A, 4)
    south is the southern hemisphere flag. Each data product from 1B above has
              a hemisphere flag, a single character either 'N' for north or
              'S' for south. If the hemisphere flag is 'S', pass in true,
              otherwise pass in false. In java, you can say
              south=(hemisphere_flag=='S')

     The inverse projection is used if you know the location on a map grid
     and want to find the geographic coordinates. It is given as follows:
   */
  public static double rhoInv(double x, double y) {
    double rhopix=sqrt((x-x0)*(x-x0)+(y-y0)*(y-y0));
    double rhokm=rhopix*maxrho/mapSize*2;
    return rhokm;
  }
  public static double thetaInv(double x, double y) {
    return atan2(y-y0,x-x0);
  }
  public static double latInv(double x, double y, double lon0, boolean south) {
    double lat=PI/2-2*asin(rhoInv(x,y)/(2*R));
    if(south) lat=-lat;
    return lat;
  }
  public static double lonInv(double x, double y, double lon0, boolean south) {
    if(south) lon0=-lon0;
    double lon=lon0+thetaInv(x,y)+PI/2;
    if(south) lon=-lon;
    if(lon<PI) lon=lon+2*PI;
    if(lon>PI) lon=lon-2*PI;
    return lon;
  }
  /** The Lambert projection has the special property that its areal scale
   *  is the same as the areal scale of the generating globe. If the entire
   *  Earth is the generating globe, the scale of the map is 1:1. This means
   *  that any figure drawn on the map has the same area as the same figure
   *  drawn on the real globe.
   *
   *  It is desired that the CIPS map pixels to be about 5km by 5km. The
   *  scientists want the map pixels to match the worst, farthest away
   *  pixels, not the nearest.
   *
   *  Putting these two facts together, it is possible to determine the
   *  size of the entire map (in pixels) needed to give the desired size
   *  to one pixel. The total area enclosed by a line of latitude is
   *  calculable. The total area enclosed by 1 stack pixel is 25km^2.
   *  Therefore all we need to know is which line of latitude to go to.
   *
   *  The area poleward of a circle of latitude is the same as the area
   *  inside the corresponding circle on the map, since they both have the same
   *  areal scale. Therefore:
   *
   *  A_circ=pi*rho^2
   *        =4*pi*R^2*sin^2(pi/4-lat_min/2)
   *
   *  where rho is the radius of minimum latitude circle projection, calculated
   *  from the Lambert projection formula, and R is the radius of the Earth
   *
   *  The area of the circumscribing square is
   *
   *  A_sq=4*rho^2
   *      =16*R^2*sin^2(pi/4-lat_min/2)
   *
   *  The ratio of the area of a pixel to the area of the whole square is
   *
   *      A_sq
   *  F_A=-----
   *      A_pix
   *
   *  where A_pix is just kmperpix*kmperpix
   *
   *      16*R^2*sin^2(pi/4-lat_min/2)
   *  F_A=----------------------------
   *                 A_pix
   *
   *  So, the ratio of the length of the side is
   *  F_l=sqrt(F_A)
   *
   *      sqrt(16*R^2*sin^2(pi/4-lat_min/2))
   *     =----------------------------------
   *               sqrt(A_pix)
   *
   *            4*R*sin(pi/4-lat_min/2)
   *     =----------------------------------
   *                 kmperpix
   *
   * Since one map pixel is naturally one pixel big, the entire map must be F_l
   * pixels big 
   *
   * The exact size of the CIPS grid is adjusted such that there are an even
   * integer number of pixels on the grid. As a consequence, the pixels are very
   * slightly different in size from what is requested with kmperpix
   */
   public static final double maxrho=2*R*sin(PI/4-lat_min/2);
   public static final int mapSize=(int)(floor(0.5+maxrho/kmperpix)*2);
   /** The false easting and northing are calculated from the map size. If the
    * easting and northing were zero, then points on the minimum latitude circle
    * would vary in x from -mapSize/2 to mapSize/2, and likewise for y. So, we
    * add mapSize/2 to both coordinates, so they each run from 0 to mapSize. */
   public static final int x0=mapSize/2;
   public static final int y0=mapSize/2;
   
   /** Given a CIPS bounding box, calculate the latitude and longitude of every 
    * pixel in it.
    *
    * A bounding box defines a rectangular region of interest, and is composed
    * of 4 integers: map grid column of left edge of the area of interest, map
    * grid row of the bottom edge, column of the right edge, and row of the top
    * edge, in that order.
    *
    * The return value is a 3D array, where the first index corresponds to either
    * latitude or longitude, the second is the grid row coordinate,
    * and the third is the grid column coordinate. In effect, it is two
    * images stacked on top of one another
    *
    *         /--------------------------/
    *        /      Latitude result     /
    *       /                          /
    *      /                          /--/
    *     /--------------------------/  /
    *       /                          /
    *      /     Longitude result     /
    *     /--------------------------/
    *
    * @param bbox Bounding box from CIPS data product
    * @param lon0 Center longitude, center_lon from CIPS data product, but
    *        converted to radians
    * @param south true if this is the southern hemisphere, false otherwise.
    *        Set it to (cips data product).Hemisphere=='S'
    */
  public static double[][][] bbox_lat_lon(int[] bbox, double lon0, boolean south) {
    //First, figure out how big the result is. 
    int xsize=bbox[2]-bbox[0]+1;
    int ysize=bbox[3]-bbox[1]+1;

    //Create the result array
    double[][][] result=new double[2][ysize][xsize];
     
    //Now for each point on the grid, calculate the latitude and longitude
    //of the point
    for(int i=0;i<xsize;i++) {
      for(int j=0;j<ysize;j++) {
        int x=i+bbox[0]; //Calculate the map grid coordinates for
        int y=j+bbox[1]; //this point in the bounding box

        double lat=latInv(x,y,lon0,south); //Run the inverse
        double lon=lonInv(x,y,lon0,south); //lambert formula

        result[0][j][i]=lat;  //Store the result for this pixel
        result[1][j][i]=lon;  //in the result array
      }
    }

    return result;
  }

  public static final int[] test1bbox=new int[] {1000,1000,1005,1005};
  public static final double test1lon0=0;
  public static final boolean test1south=false;
  //test results are in degrees, for easier human reading
  public static final double[][][] test1Result=new double[][][] {
    {
      { 72.404403,       72.436503,       72.468542,       72.500521,       72.532439,       72.564297},
      { 72.436503,       72.468659,       72.500755,       72.532791,       72.564767,       72.596682},
      { 72.468542,       72.500755,       72.532908,       72.565002,       72.597035,       72.629007},
      { 72.500521,       72.532791,       72.565002,       72.597152,       72.629243,       72.661273},
      { 72.532439,       72.564767,       72.597035,       72.629243,       72.661391,       72.693479},
      { 72.564297,       72.596682,       72.629007,       72.661273,       72.693479,       72.725624}
    },{
      {-45.000001,      -44.896016,      -44.791654,      -44.686912,      -44.581791,      -44.476288},
      {-45.103986,      -45.000001,      -44.895637,      -44.790893,      -44.685768,      -44.580259},
      {-45.208348,      -45.104365,      -45.000001,      -44.895256,      -44.790127,      -44.684615},
      {-45.313090,      -45.209109,      -45.104746,      -45.000001,      -44.894871,      -44.789356},
      {-45.418211,      -45.314234,      -45.209875,      -45.105131,      -45.000001,      -44.894484},
      {-45.523714,      -45.419743,      -45.315387,      -45.210646,      -45.105518,      -45.000001}
    }
  };
  //Test non-square arrays
  public static final int[] test2bbox=new int[] {1000,1000,1004,1006};
  public static final double test2lon0=0;
  public static final boolean test2south=false;
  public static final double[][][] test2Result=new double[][][] {
    {
      { 72.404403,       72.436503,       72.468542,       72.500521,       72.532439},
      { 72.436503,       72.468659,       72.500755,       72.532791,       72.564767},
      { 72.468542,       72.500755,       72.532908,       72.565002,       72.597035},
      { 72.500521,       72.532791,       72.565002,       72.597152,       72.629243},
      { 72.532439,       72.564767,       72.597035,       72.629243,       72.661391},
      { 72.564297,       72.596682,       72.629007,       72.661273,       72.693479},
      { 72.596093,       72.628536,       72.660919,       72.693242,       72.725505}
    },{
      {-45.000001,      -44.896016,      -44.791654,      -44.686912,      -44.581791},
      {-45.103986,      -45.000001,      -44.895637,      -44.790893,      -44.685768},
      {-45.208348,      -45.104365,      -45.000001,      -44.895256,      -44.790127},
      {-45.313090,      -45.209109,      -45.104746,      -45.000001,      -44.894871},
      {-45.418211,      -45.314234,      -45.209875,      -45.105131,      -45.000001},
      {-45.523714,      -45.419743,      -45.315387,      -45.210646,      -45.105518},
      {-45.629600,      -45.525636,      -45.421286,      -45.316549,      -45.211424}
    }
  };
  //test southern hemisphere 
  public static final int[] test3bbox=new int[] {1500,1500,1504,1506};
  public static final double test3lon0=0;
  public static final boolean test3south=true;
  public static final double[][][] test3Result=new double[][][] {
    {
      {-75.738781,      -75.706746,      -75.674640,      -75.642461,      -75.610212},
      {-75.706746,      -75.674782,      -75.642745,      -75.610637,      -75.578457},
      {-75.674640,      -75.642745,      -75.610778,      -75.578740,      -75.546629},
      {-75.642461,      -75.610637,      -75.578740,      -75.546770,      -75.514729},
      {-75.610212,      -75.578457,      -75.546629,      -75.514729,      -75.482758},
      {-75.577892,      -75.546206,      -75.514448,      -75.482617,      -75.450714},
      {-75.545501,      -75.513885,      -75.482196,      -75.450434,      -75.418601}
    },{
      {-135.00000,      -134.87239,      -134.74535,      -134.61888,      -134.49297},
      {-135.12761,      -135.00000,      -134.87296,      -134.74648,      -134.62056},
      {-135.25465,      -135.12704,      -135.00000,      -134.87352,      -134.74760},
      {-135.38112,      -135.25352,      -135.12648,      -135.00000,      -134.87408},
      {-135.50703,      -135.37944,      -135.25240,      -135.12592,      -135.00000},
      {-135.63238,      -135.50480,      -135.37777,      -135.25130,      -135.12537},
      {-135.75717,      -135.62960,      -135.50258,      -135.37612,      -135.25020}
    }
  };
  //test northern nonzero center longitude
  public static final int[] test4bbox=new int[] {1500,1500,1504,1506};
  public static final double test4lon0=toRadians(60);
  public static final boolean test4south=false;
  public static final double[][][] test4Result=new double[][][] {
    {
      {75.738781,       75.706746,       75.674640,       75.642461,       75.610212},
      {75.706746,       75.674782,       75.642745,       75.610637,       75.578457},
      {75.674640,       75.642745,       75.610778,       75.578740,       75.546629},
      {75.642461,       75.610637,       75.578740,       75.546770,       75.514729},
      {75.610212,       75.578457,       75.546629,       75.514729,       75.482758},
      {75.577892,       75.546206,       75.514448,       75.482617,       75.450714},
      {75.545501,       75.513885,       75.482196,       75.450434,       75.418601}
    },{
      {-165.00000,      -165.12761,      -165.25465,      -165.38112,      -165.50703},
      {-164.87239,      -165.00000,      -165.12704,      -165.25352,      -165.37944},
      {-164.74535,      -164.87296,      -165.00000,      -165.12648,      -165.25240},
      {-164.61888,      -164.74648,      -164.87352,      -165.00000,      -165.12592},
      {-164.49297,      -164.62056,      -164.74760,      -164.87408,      -165.00000},
      {-164.36762,      -164.49520,      -164.62223,      -164.74870,      -164.87463},
      {-164.24283,      -164.37040,      -164.49742,      -164.62388,      -164.74980}
    }
  };
  //test north pole
  public static final int[] test5bbox=new int[] {1274,1274,1278,1278};
  public static final double test5lon0=toRadians(0);
  public static final boolean test5south=false;
  public static final double[][][] test5Result=new double[][][] {
    {
      {89.872996,       89.899595,       89.910195,       89.899595,       89.872996},
      {89.899595,       89.936498,       89.955097,       89.936498,       89.899595},
      {89.910195,       89.955097,       90.000000,       89.955097,       89.910195},
      {89.899595,       89.936498,       89.955097,       89.936498,       89.899595},
      {89.872996,       89.899595,       89.910195,       89.899595,       89.872996}
    },{
      {-45.000001,      -26.565052, -6.9717301e-007,       26.565051,       45.000000},
      {-63.434950,      -45.000001, -6.9717301e-007,       45.000000,       63.434949},
      {-89.999999,      -89.999999,       90.000000,       90.000000,       90.000000},
      {-116.56505,      -135.00000,      -180.00000,       135.00000,       116.56505},
      {-135.00000,      -153.43495,      -180.00000,       153.43495,       135.00000}
    }
  };

  public static double[][][] test(int[] bbox, double lon0, boolean south) {
    double[][][] result=bbox_lat_lon(bbox,lon0,south);
    System.out.printf("delete_var,lat & delete_var,lon & bbox_lat_lon,[%d,%d,%d,%d],south=%d,center_lon=%f,/no_co,lat=lat,lon=lon & print,lat & print,lon\n",
                      bbox[0],bbox[1],bbox[2],bbox[3],south?1:0,lon0);
    System.out.println("Calculated latitude");
    for(int i=0;i<result[0].length;i++) {
      for(int j=0;j<result[0][i].length;j++) {
        System.out.printf("%15.6f",toDegrees(result[0][i][j]));
      }
      System.out.println();
    }
    System.out.println("Calculated longitude");
    for(int i=0;i<result[0].length;i++) {
      for(int j=0;j<result[0][i].length;j++) {
        System.out.printf("%15.6f",toDegrees(result[1][i][j]));
      }
      System.out.println();
    }
    return result;
  }
  public static void test(int[] bbox, double lon0, boolean south, double[][][] testResult) {
    double[][][] result=test(bbox,lon0,south);
    System.out.println("Latitude difference");
    for(int i=0;i<result[0].length;i++) {
      for(int j=0;j<result[0][i].length;j++) {
        System.out.printf("%15.6f",toDegrees(result[0][i][j])-testResult[0][i][j]);
      }
      System.out.println();
    }
    System.out.println("Longitude difference");
    for(int i=0;i<result[0].length;i++) {
      for(int j=0;j<result[0][i].length;j++) {
        System.out.printf("%15.6f",toDegrees(result[1][i][j])-testResult[1][i][j]);
      }
      System.out.println();
    }
  }
  public static void main(String args[]) {
    System.out.println(x0);
    test(test1bbox,test1lon0,test1south,test1Result);
    test(test2bbox,test2lon0,test2south,test2Result);
    test(test3bbox,test3lon0,test3south,test3Result);
    test(test4bbox,test4lon0,test4south,test4Result);
    test(test5bbox,test5lon0,test5south,test5Result);
  }
}
