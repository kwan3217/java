/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.fits;

import java.io.*;
/**
 *
 * @author jeppesen
 */
public class FitsDump {
  public static void main(String args[]) throws IOException {
    for(int hour=0;hour<24;hour++) {
      FileInputStream inf =new FileInputStream (String.format("c:\\users\\jeppesen\\Desktop\\Class X Flare\\EVS_L2_2011046_%02d_001_01.fit",hour));
      FileOutputStream ouf=new FileOutputStream(String.format("c:\\users\\jeppesen\\Desktop\\Class X Flare\\EVS_L2_2011046_%02d_001_01.raw",hour));
      String h="";
      byte[] record=new byte[2880];
      inf.read(record);
      byte[] header=new byte[80];
      int ptr=0;
      while(!h.startsWith("END")) {
        if (ptr>=2880) {
          inf.read(record);
          ptr=0;
      }
      System.arraycopy(record, ptr, header, 0, 80);
      ptr+=80;
      h=new String(header);
      System.out.println(h);
    }
    inf.read(record);
    ptr=0;
    h="";
    while(!h.startsWith("END")) {
      if (ptr>=2880) {
        inf.read(record);
        ptr=0;
      }
      System.arraycopy(record, ptr, header, 0, 80);
      ptr+=80;
      h=new String(header);
      System.out.println(h);
    }
    int dsize=8*5200;
    int blocks=dsize/2880;
    if(dsize%2880>0) blocks++;
    inf.skip(blocks*2880);

      inf.read(record);
      ptr=0;
      h="";
      while(!h.startsWith("END")) {
        if (ptr>=2880) {
          inf.read(record);
          ptr=0;
        }
        System.arraycopy(record, ptr, header, 0, 80);
        ptr+=80;
        h=new String(header);
        System.out.println(h);
      }
      //Now we're at the data
      int naxis1=46832;
      int naxis2=360;
      byte row[]=new byte[naxis1];
      byte row2[]=new byte[5200*4];
      blocks=dsize/2880;
      if(dsize%2880>0) blocks++;
      for(int i=0;i<naxis2;i++) {
        inf.read(row);
        System.arraycopy(row,32,row2,0,5200*4);
        ouf.write(row2);
      }
      ouf.close();
      inf.close();
    }
  }
}
