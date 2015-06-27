package org.kwansystems.pov;

import java.io.*;
import static org.kwansystems.pov.BlueMarbleTile.*;

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
public class BlueMarbleBin {
  static class Binner {
    Binner oub;
    OutputStream ouf;
    int rowdata[][],bindata[];
    int width,row,col,cha,factor;
    public Binner(OutputStream Louf, int Lfactor, int Lwidth) {
      this((Binner)null,Louf,Lfactor,Lwidth);
    }
    public Binner(Binner Lout, int Lwidth) {
      this(Lout,null,0,Lwidth);
    }
    public Binner(Binner Loub, OutputStream Louf, int Lfactor, int Lwidth) {
      factor=Lfactor;
      ouf=Louf;
      oub=Loub; 
      width=Lwidth;
      row=0;col=0;
      rowdata=new int[2][width*nBytes];
      bindata=new int[width*nBytes/2];
    }
    public void write(byte[] data) throws IOException {
      int[] b=new int[data.length];
      for(int i=0;i<data.length;i++) {
        int newdata=data[i];
        if(data[i]<0) {
          newdata+=256;
        }
        b[i]=newdata;
      }
      write(b);
    }
    public void write(int[] data) throws IOException {
      rowdata[row%2]=(int[])data.clone();
      row++;
      if(row%2==0) {
        for(int col=0;col<width/2;col++) {
          for(int cha=0;cha<nBytes;cha++) {
            bindata[col*nBytes+cha]=rowdata[0][(col*2+0)*nBytes+cha]+
                                    rowdata[1][(col*2+0)*nBytes+cha]+
                                    rowdata[0][(col*2+1)*nBytes+cha]+
                                    rowdata[1][(col*2+1)*nBytes+cha];
          }
        }
        if(oub!=null)oub.write(bindata);
        if(ouf!=null){
          byte[] b=new byte[bindata.length];
          for(int i=0;i<bindata.length;i++)b[i]=(byte)(bindata[i]>>>factor);
          ouf.write(b);
          if(width==2700) {
            System.out.print(".");
            if((row%50)==0) System.out.println(String.format("%4d",row)); 
          }
        }
      }
    }
    public void close() throws IOException {
      if(ouf!=null)ouf.close();
    }
  }
  public static void main(String args[]) throws IOException {
    int nBytes=1;
    byte[] Row=new byte[NPixelsX(NLevels-1)*nBytes]; 
    InputStream inf=new FileInputStream("/mnt/big/umd/EarthCover5.raw");
    Binner Bin1to0=new Binner(new FileOutputStream("/mnt/big/umd/EarthCover0.raw"),2+2+2+2+2,1350*2);
    Binner Bin2to1=new Binner(Bin1to0,new FileOutputStream("/mnt/big/umd/EarthCover1.raw"),2+2+2+2,1350* 4);
    Binner Bin3to2=new Binner(Bin2to1,new FileOutputStream("/mnt/big/umd/EarthCover2.raw"),2+2+2,1350* 8);
    Binner Bin4to3=new Binner(Bin3to2,new FileOutputStream("/mnt/big/umd/EarthCover3.raw"),2+2,1350*16);
    Binner Bin5to4=new Binner(Bin4to3,new FileOutputStream("/mnt/big/umd/EarthCover4.raw"),2,1350*32);
    for(int rows=0;rows<NPixelsY(NLevels-1);rows++) {
      inf.read(Row);
      Bin5to4.write(Row);
    }
    Bin1to0.close();
  }
  
}
