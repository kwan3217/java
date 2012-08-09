/* Spheroid tile height field generator */

package org.kwansystems.pov;

import java.io.*;

import org.kwansystems.space.planet.*;
import org.kwansystems.tools.Scalar;
import org.kwansystems.tools.vector.*;

import static org.kwansystems.space.planet.Spheroid.*;
import static java.lang.Math.*;

public class SphHeightField {
  public static void main(String args[]) throws IOException {
//	  System.out.print("Combining");
//	  combine(args[0],args[1],args[2]);
    int layer=5;
    int x=Integer.parseInt(args[0]);
    int y=Integer.parseInt(args[1]);
		
    System.out.print("Segmenting");
    segment("/usr/codebase/pov/BlueMarble/HiResDEM/W140N90",layer,x,y);
    System.out.print("Meshing");
    makeMesh(layer,x,y,WGS84);
  }
  public static double getLat(int layer, int y) {
    double TileSize=180/(5*pow(2,layer-1));
    return   90-TileSize*y;
  }
  public static double getLon(int layer, int x) {
    double TileSize=180/(5*pow(2,layer-1));
    return -180+TileSize*x;
  }
  public static void combine(String left, String right, String both) throws IOException {
    byte bufLeft[] =new byte[ 8100];
    byte bufRight[]=new byte[ 8100];
    byte bufBoth[] =new byte[16200];

    FileInputStream  infLeft= new FileInputStream (left);	
    FileInputStream  infRight=new FileInputStream (right);	
    FileOutputStream oufBoth =new FileOutputStream(both);	

    for(int i=0;i<8100;i++) {
      infLeft. read(bufLeft );
      infRight.read(bufRight);
      System.arraycopy(bufLeft, 0,bufBoth,   0,8100);
      System.arraycopy(bufRight,0,bufBoth,8100,8100);
      oufBoth.write(bufBoth);
      System.out.print(".");
    }
    System.out.print("\n");
  }
  public static void segment(String infn, int layer, int x, int y) throws IOException {
    FileInputStream  infDem = new FileInputStream (infn+".DEM");	
    String oufn=Integer.toString(layer)+"-";
    if(x<100) oufn=oufn+"0";
    if(x<10) oufn=oufn+"0";
    oufn=oufn+Integer.toString(x)+"x";
    if(y<100) oufn=oufn+"0";
    if(y<10) oufn=oufn+"0";
    oufn=oufn+Integer.toString(y)+".mbil";
    FileOutputStream ouf    = new FileOutputStream(oufn);	
				
    int nRows = 12000;
    int nCols =  4800;
    double Wmap = -140;
    double Nmap =   90;
    double Emap = -100;
    double Smap =  -10;

    double Wseg = getLon(layer,x);
    double Nseg = getLat(layer,y);
    double Eseg = getLon(layer,x+1);
    double Sseg = getLat(layer,y+1);
		
    int firstRow = ((int)Scalar.linterp(Nmap,0,Smap,nRows,Nseg));
    int lastRow  = ((int)Scalar.linterp(Nmap,0,Smap,nRows,Sseg));
    int firstCol = ((int)Scalar.linterp(Wmap,0,Emap,nCols,Wseg));
    int lastCol  = ((int)Scalar.linterp(Wmap,0,Emap,nCols,Eseg));
    System.out.println("firstRow: "+firstRow);
    System.out.println("lastRow:  "+lastRow);
    System.out.println("firstCol: "+firstCol);
    System.out.println("lastCol:  "+lastCol);

    infDem.skip((firstRow-1)*nCols*2);
    byte buf[] =new byte[(lastCol-firstCol)*2];

    for(int i=firstRow;i<lastRow+1;i++) {
      infDem.skip((firstCol-0)*2);
      infDem. read(buf);
      ouf.write(buf);
      infDem.skip((nCols-lastCol)*2);
      System.out.print(".");
    }
    System.out.print("\n");
  }
  public static void makeMesh (int layer, int x, int y, Spheroid Sph) throws IOException {
    String oufn=Integer.toString(layer)+"-";
    if(x<100) oufn=oufn+"0";
    if(x<10) oufn=oufn+"0";
    oufn=oufn+Integer.toString(x)+"x";
    if(y<100) oufn=oufn+"0";
    if(y<10) oufn=oufn+"0";
    oufn=oufn+Integer.toString(y);
    FileInputStream  inf = new FileInputStream(oufn+".mbil");	
    PrintWriter      ouf = new PrintWriter    (oufn+".mesh2");	
    double N = toRadians(getLat(layer,y));
    double S = toRadians(getLat(layer,y+1));
    double W = toRadians(getLon(layer,x));
    double E = toRadians(getLon(layer,x+1));
		
    int ImgSize=270;
    /* If a pixel is a picture element, 
     * a topel must be a topography element */
    int TopelSize=270;
    int TopelPerPix=TopelSize/ImgSize;
    int Decimate=1;
    int Keep=TopelPerPix/Decimate;
    byte[] inBuf=new byte[TopelSize*2];
    short[][] buffer=new short[Decimate][];
    double[]  outHeight=new double[Keep*ImgSize];

    /* Write the mesh header */
    System.out.print(" Vertices");
    ouf.println("mesh2 {");
    ouf.println("  vertex_vectors {");
    ouf.println("    "+((ImgSize*Keep+1)*(ImgSize*Keep+1)));

    /* For each line in the (decimated) data...*/
    for(int i=0;i<ImgSize*Keep;i++) {
      /* Read the source lines */
      for(int j=0;j<Decimate;j++) {
        inf.read(inBuf);
        buffer[j]=byteToShort(inBuf,'M');
      }
      /* For each pixel in the compressed line */
      for(int j=0;j<Keep*ImgSize;j++) {
        outHeight[j]=0;
	/* Read all the topels in this pixel */
        for(int k=0;k<Decimate;k++) {
	   for(int m=0;m<Decimate;m++) {
	     outHeight[j]+=buffer[k][j*Decimate+m];
	   }
	 }
	 /* Average it all out */
	 outHeight[j]/=(Decimate*Decimate);
	 /* Calculate the vertex coordinate */
	 double lat=Scalar.linterp(0,N,ImgSize*Keep,S,i);
	 double lon=Scalar.linterp(0,W,ImgSize*Keep,E,j);
	 MathVector vertex=Sph.lla2xyz(lat,lon,outHeight[j]);
	 ouf.print(
	   "    <"+
          (floor(vertex.X())/1000.0)+", "+
 	   (floor(vertex.Y())/1000.0)+", "+
          (floor(vertex.Z())/1000.0)+">, "+
	   //"//lat: "+(toDegrees(lat))+" lon: "+(toDegrees(lon))+" alt: "+outHeight[j]+
	   "\n"
        );
      }
      //Simulate another texel on the right end of the line, duplicating the previous one
      double lat=Scalar.linterp(0,N,ImgSize*Keep,S,i);
      double lon=E;
      MathVector vertex=Sph.lla2xyz(lat,lon,outHeight[ImgSize*Keep-1]);
      ouf.print(
	 "    <"+
        (floor(vertex.X())/1000.0)+", "+
        (floor(vertex.Y())/1000.0)+", "+
	 (floor(vertex.Z())/1000.0)+
        ">, "+
	 //"//lat: "+(toDegrees(lat))+" lon: "+(toDegrees(lon))+" alt: "+outHeight[j]+
	 "\n"
      );
      System.out.print(".");
    }
    /* Simulate another row of texels at the bottom edge, duplicating the previous one */
    for(int j=0;j<Keep*ImgSize;j++) {
      /* Calculate the vertex coordinate */
      double lon=Scalar.linterp(0,W,ImgSize*Keep,E,j);
      MathVector vertex=Sph.lla2xyz(S,lon,outHeight[j]);
      ouf.print(
	 "    <"+(floor(vertex.X())/1000.0)+", "+
 	 (floor(vertex.Y())/1000.0)+", "+
	 (floor(vertex.Z())/1000.0)+">, "+
	 //"//lat: "+(toDegrees(lat))+" lon: "+(toDegrees(lon))+" alt: "+outHeight[j]+
	 "\n"
      );
    }
    //Simulate another texel on the right end of the line, duplicating the previous one
    MathVector vertex=Sph.lla2xyz(S,E,outHeight[ImgSize*Keep-1]);
    ouf.print(
      "    <"+
      (floor(vertex.X())/1000.0)+", "+
      (floor(vertex.Y())/1000.0)+", "+
      (floor(vertex.Z())/1000.0)+
      ">, "+
      //"//lat: "+(toDegrees(lat))+" lon: "+(toDegrees(lon))+" alt: "+outHeight[j]+
      "\n"
    );
    ouf.println("  }");
    System.out.print("\nFaces");
    ouf.println("  face_indices {");
    ouf.println("    "+((ImgSize*Keep)*(ImgSize*Keep)*2));
    /* Interestingly, the face indices do not depend on where the vertices are...*/
    /* For each line in the (decimated) data...*/
    for(int i=0;i<(ImgSize*Keep);i++) {
      /* For each pixel in the compressed line... */
      for(int j=0;j<(ImgSize*Keep);j++) {
	 /* There are two triangles: one with two points in this row, and one with two points in the next */
	 ouf.println(
	   "    <"+
	   ((i+0)*ImgSize*Keep+(j+0))+", "+
	   ((i+0)*ImgSize*Keep+(j+1))+", "+
	   ((i+1)*ImgSize*Keep+(j+0))+">, "
	 );
	 ouf.println(
	   "    <"+
	   ((i+0)*ImgSize*Keep+(j+1))+", "+
	   ((i+1)*ImgSize*Keep+(j+1))+", "+
	   ((i+1)*ImgSize*Keep+(j+0))+">, "
	 );
      }
      System.out.print(".");
    }
    ouf.println("  }");
    ouf.println("}");
    inf.close();
    ouf.close();
  }
  public static short[] byteToShort(byte[] buf, char byteOrder) {
    int scratch1,scratch2;
    short result[]=new short[buf.length/2];

    for(int i=0;i<result.length;i++) {
      scratch1=buf[i*2+0];
      scratch1+=(scratch1<0)?256:0;
      scratch2=buf[i*2+1];
      scratch2+=(scratch2<0)?256:0;

      result[i]=byteOrder=='I'?
	      ((short)(scratch1+scratch2*256)):
	      ((short)(scratch2+scratch1*256));
    }
    return result;
  }
}
		
		
