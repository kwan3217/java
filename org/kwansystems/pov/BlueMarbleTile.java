package org.kwansystems.pov;

import java.io.*;

/**Breaks up a BMNG world_big file into a heirarchy of tiles.
 * <p>
 * <table>
 * <tr><th>Level</th><th>Tiles in X direction</th><th>Tiles in Y direction</th><th>Total Tiles</th><th>Total pixels X</th><th>Total pixels Y</th></tr>
 * <tr><td>0</td><td>1</td><td>1</td><td>1</td><td>1350</td><td>675</td></tr>
 * <tr><td>1</td><td>10</td><td>5</td><td>50</td><td>2700</td><td>1350</td></tr>
 * <tr><td>2</td><td>20</td><td>10</td><td>200</td><td>5400</td><td>2700</td></tr>
 * <tr><td>3</td><td>40</td><td>20</td><td>800</td><td>10800</td><td>5400</td></tr>
 * <tr><td>4</td><td>80</td><td>40</td><td>3200</td><td>21600</td><td>10800</td></tr>
 * <tr><td>5</td><td>160</td><td>80</td><td>12800</td><td>43200</td><td>21600</td></tr>
 * <tr><td>6</td><td>320</td><td>160</td><td>51200</td><td>86400</td><td>43200</td></tr>
 * </table>
 * 
 * @version 0.01, 1 November 2005
 * @author Chris Jeppesen
 *
 */
public class BlueMarbleTile {
  static final int FullSizeWidth=43200;
  static final int NLevels=6;
  static final int NTileRows1=6;
  static final int FullSizeHeight=FullSizeWidth/2;
  static final int TileSize=NPixelsY(1)/NTileRows1;
  static final int nBytes=1;
  static int NPixelsX(int level) {
    return FullSizeWidth>>((NLevels-1)-level);
  }
  static int NPixelsY(int level) {
    return NPixelsX(level)>>1;
  }
  static int NTilesX(int level) {
    if(level==0) return 1;
    return NPixelsX(level)/TileSize;
  }
  static int NTilesY(int level) {
    if(level==0) return 1;
    return NPixelsY(level)/TileSize;
  }
  public static void main(String args[]) throws IOException {
    for(int Level=1;Level<NLevels;Level++) {
      System.out.print("Level "+Level+": "+NTilesY(Level)+" tile rows");
      byte[] Row=new byte[NPixelsX(Level)*nBytes];
      FileOutputStream[] Tiles=new FileOutputStream[NTilesX(Level)];
      InputStream inf=new FileInputStream("/mnt/big/umd/EarthCover"+Level+".raw");
      for(int rowtile=0;rowtile<NTilesY(Level);rowtile++) {
        if(rowtile % 10 ==0 ) {
          if(rowtile % 50 == 0) System.out.println();
          System.out.print(String.format("%3d",rowtile));
        } else {
          System.out.print(".");
          
        }
        for(int coltile=0;coltile<NTilesX(Level);coltile++)Tiles[coltile]=new FileOutputStream(String.format("/mnt/big/umd/%d/%03dx%03d.raw",Level,rowtile,coltile));
        for(int rows=0;rows<TileSize;rows++) {
          inf.read(Row);
          for(int coltile=0;coltile<NTilesX(Level);coltile++) {
            Tiles[coltile].write(Row,coltile*TileSize*nBytes,TileSize*nBytes);
          }
        }
        for(int coltile=0;coltile<NTilesX(Level);coltile++)Tiles[coltile].close();
        System.gc();
      }
      inf.close();
      System.out.println();
    }
  }
  
}
