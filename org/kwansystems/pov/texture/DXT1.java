package org.kwansystems.pov.texture;

import java.io.*;
import static org.kwansystems.pov.texture.DirectDrawSurface.*;

public class DXT1 extends DXT {
  int[][] c;
  int[][] pix;
  int c0;
  int c1;
  int[] p;
  boolean transparent;
  public int extend(int orig, int nOrig) {
	  return (orig>>>nOrig) | (orig << (8-nOrig)) & 0xFF; 
  }
  public int[] unpack565(int c) {
	  int[] result=new int[3];
	  int r=(c & ((1<<5)-1)<<11) >>> 11;
	  int g=(c & ((1<<6)-1)<< 5) >>>  5;
	  int b=(c & ((1<<5)-1)<< 0) >>>  0;
	  result[0]=extend(r,5);
	  result[1]=extend(g,6);
	  result[2]=extend(b,5);
	  return result;
  }
  public DXT1(InputStream Inf,int Lformat) throws IOException {
    super(Inf,Lformat);
  }
  public void readAlpha(InputStream Inf) throws IOException {
    
  }
  public void readColor(InputStream Inf) throws IOException {
    c=new int[4][];
    c0=readShort(Inf);if(c0<0)c0+=0x10000;
    c1=readShort(Inf);if(c1<0)c1+=0x10000;
    transparent=(c0<=c1) & Format==1;
    c[0]=unpack565(c0);
    c[1]=unpack565(c1);
    if(transparent) {
      c[2]=new int[3];
      for(int i=0;i<3;i++)c[2][i]=(c[0][i]+c[1][i])/2;
    } else {
      c[2]=new int[3];
      c[3]=new int[3];
      for(int i=0;i<3;i++) {
        c[2][i]=(2*c[0][i]+1*c[1][i])/3;  
        c[3][i]=(1*c[0][i]+2*c[1][i])/3;  
      }
    }
    pix=new int[4][4];
    p=new int[4];
    for(int i=0;i<4;i++) {
      p[i]=Inf.read();
      for(int j=0;j<4;j++) {
        pix[i][j]=(p[i] >>> j*2) & 0x03;
      }
    }
  }
  public byte[] readRow(int row) {
	  byte[] result=new byte[16];
	  for(int col=0;col<4;col++) {
	    int idx=pix[row][col];
	    if(transparent & idx==3) {
		    result[col*4+3]=0;
	    } else {
	      for(int cha=0;cha<3;cha++)result[col*4+cha]=(byte)c[idx][cha];
	      result[col*4+3]=-1;
	    }
	  }
	  return result;
  }
  public String toString() {
	  StringBuffer result=new StringBuffer();
	  result.append(String.format("c0: %04X\n",c0));
	  result.append(String.format("c1: %04X\n",c1));
	  for(int i=0;i<4;i++) result.append(String.format("p[%d]: %02X\n",i,p[i]));
	  for(int i=0;i<3;i++) result.append(String.format("c%d: R%02X,G%02X,B%02X\n",i,c[i][0],c[i][1],c[i][2]));
	  if(c[3]==null) { 
	    result.append("c3: transparent\n"); 
	  } else {
	    result.append(String.format("c3: R%02X,G%02X,B%02X\n",c[3][0],c[3][1],c[3][2]));
	  }
	  for(int i=0;i<4;i++) {
	    result.append(String.format("pix[%d] ",i));
	    for(int j=0;j<4;j++) {
		    result.append(String.format("%d ",pix[i][j]));
	    }
	    result.append("\n");
	  }
	  return result.toString();
  }
}
