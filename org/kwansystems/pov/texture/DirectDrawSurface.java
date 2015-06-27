package org.kwansystems.pov.texture;

import java.io.*;

import org.kwansystems.pov.*;
import static org.kwansystems.tools.Endian.*;

/**
 * Direct Draw Surface texture decompressor. 
 *
 */
public class DirectDrawSurface {
  public static int readInt(InputStream inf) throws IOException {
    byte[] result=new byte[4];
    if(inf.read(result)!=4) throw new IOException("Waah!");
    int b0=result[0] & 0xFF;
    int b1=(result[1]<<8) & 0xFF00;
    int b2=(result[2]<<16) & 0xFF0000;
    int b3=(result[3]<<24) & 0xFF000000;
    return (int)(b0 | b1 | b2 | b3);
  }
  public static short readShort(InputStream inf) throws IOException {
    byte[] result=new byte[2];
    if(inf.read(result)!=2) throw new IOException("Waah!");
    int[] result3=new int[2];
    for(int i=0;i<2;i++) result3[i]=((int)result[i]) & 0xFF;
    int result2=result3[1] *256;
    result2=result2 | result3[0];
    return (short)result2;
  }
  public static String readString(InputStream inf, int len) throws IOException {
	  byte[] result=new byte[len];
    if(inf.read(result)!=len) throw new IOException("Waah!");
	  return new String(result);
  }
  class DDPIXELFORMAT {
	  public int dwSize;
	  public int dwFlags;
	  public String dwFourCC;
	  public int dwRGBBitCount;
	  public int dwRBitMask;
	  public int dwGBitMask;
	  public int dwBBitMask;
	  public int dwABitMask;
	  String[] ddpfFlags=new String[] {"DDPF_ALPHAPIXELS", "", "DDPF_FOURCC", "", "","","DDPF_RGB"};
	  public String toString() {
	    StringBuffer result=new StringBuffer("");
      result.append(String.format("dwSize: %3d\n",dwSize)); 
      result.append(String.format("dwFlags: %08X\n",dwFlags));
      for(int i=0;i<ddpfFlags.length;i++) if((dwFlags & (1<<i))>0) result.append(ddpfFlags[i]+"\n");
      result.append(String.format("dwFourCC: \"%s\"\n",dwFourCC));
      result.append(String.format("dwRGBBitCount: %d\n",dwRGBBitCount));
      result.append(String.format("dwRBitMask: %08X\n",dwRBitMask));
      result.append(String.format("dwGBitMask: %08X\n",dwGBitMask));
      result.append(String.format("dwBBitMask: %08X\n",dwBBitMask));
      result.append(String.format("dwABitMask: %08X\n",dwABitMask));
      return result.toString();
	  }
	  public DDPIXELFORMAT(InputStream Inf) throws IOException {
      dwSize=readInt(Inf);
      dwFlags=readInt(Inf);
      dwFourCC=readString(Inf,4);
      dwRGBBitCount=readInt(Inf);
      dwRBitMask=readInt(Inf);
      dwGBitMask=readInt(Inf);
      dwBBitMask=readInt(Inf);
      dwABitMask=readInt(Inf);
	  }
  }
  class DDCAPS2 {
	  public int dwCaps1;
	  public int dwCaps2;
	  public int[] dwReserved;
	  String[] ddscFlags=new String[] {"DDPF_ALPHAPIXELS", "", "DDPF_FOURCC", "","","","DDPF_RGB"};
	  String[] ddsc2Flags=new String[] {"DDPF_ALPHAPIXELS", "", "DDPF_FOURCC", "", "","","DDPF_RGB"};
	  public String toString() {
	    StringBuffer result=new StringBuffer("");
      result.append(String.format("dwCaps1: %08X\n",dwCaps1));
      for(int i=0;i<ddscFlags.length;i++) if((dwCaps1 & (1<<i))>0) result.append(ddscFlags[i]+"\n");
      result.append(String.format("dwCaps2: %08X\n",dwCaps2));
      for(int i=0;i<ddsc2Flags.length;i++) if((dwCaps2 & (1<<i))>0) result.append(ddsc2Flags[i]+"\n");
      result.append("dwReserved:\n");
      for(int i=0;i<dwReserved.length;i++) result.append(String.format("[%02d] %08X\n",i,dwReserved[i]));
      return result.toString();
	  }
	  public DDCAPS2(InputStream Inf) throws IOException {
      dwCaps1=readInt(Inf);
      dwCaps2=readInt(Inf);
      dwReserved=new int[2];
      for(int i=0;i<dwReserved.length;i++) dwReserved[i]=readInt(Inf);
	  }
  }
  class DDSURFACEDESC2 {
	  public int dwSize;
	  public int dwFlags;
	  public int dwHeight;
	  public int dwWidth;
	  public int dwPitchOrLinearSize;
	  public int dwDepth;
	  public int dwMipMapCount;
	  public int[] dwReserved1;
	  public DDPIXELFORMAT ddpfPixelFormat;
	  public DDCAPS2 ddsCaps;
	  public int dwReserved2;
	  String[] ddsdFlags=new String[] {"DDSD_CAPS","DDSD_HEIGHT","DDSD_WIDTH","DDSD_PITCH","","","","","","","","",	  "DDSD_PIXELFORMAT","","","",	  "","DDSD_MIPMAPCOUNT","","DDSD_LINEARSIZE",	  "","","","DDSD_DEPTH"};
  	public String toString() {
	    StringBuffer result=new StringBuffer("");
      result.append(String.format("dwSize: %3d\n",dwSize)); 
      result.append(String.format("dwFlags: %08X\n",dwFlags));
      for(int i=0;i<ddsdFlags.length;i++) if((dwFlags & (1<<i))>0) result.append(ddsdFlags[i]+"\n");
      result.append(String.format("dwHeight: %d\n",dwHeight));
      result.append(String.format("dwWidth: %d\n",dwWidth));
      result.append(String.format("dwPitchOrLinearSize: %d\n",dwPitchOrLinearSize));
      result.append(String.format("dwDepth; %d\n", dwDepth));
      result.append(String.format("dwMipMapCount: %d\n",dwMipMapCount));
      result.append("dwReserved1:\n");
      for(int i=0;i<dwReserved1.length;i++) result.append(String.format("[%02d] %08X\n",i,dwReserved1[i]));
      result.append(String.format("ddpfPixelFormat: \n%s",ddpfPixelFormat.toString()));
      result.append(String.format("ddsCaps: \n%s",ddsCaps.toString()));
      result.append(String.format("dwReserved2: %08X\n",dwReserved2));
      return result.toString();
	  }
	  public DDSURFACEDESC2(InputStream Inf) throws IOException {
      dwSize=readInt(Inf);
      dwFlags=readInt(Inf);
      dwHeight=readInt(Inf);
      dwWidth=readInt(Inf);
      dwPitchOrLinearSize=readInt(Inf);
      dwDepth=readInt(Inf);
      dwMipMapCount=readInt(Inf);
      dwReserved1=new int[11];
      for(int i=0;i<dwReserved1.length;i++) dwReserved1[i]=readInt(Inf);
      ddpfPixelFormat=new DDPIXELFORMAT(Inf);
      ddsCaps=new DDCAPS2(Inf);
      dwReserved2=readInt(Inf);
	  }
  }
  public String FourCC;
  public DDSURFACEDESC2 ddsd;
  public InputStream Inf;
  public int compFormat;
  public DirectDrawSurface(InputStream LInf) throws IOException {
	  Inf=LInf;
	  FourCC=readString(Inf,4);
	  ddsd=new DDSURFACEDESC2(Inf);
	  compFormat=ddsd.ddpfPixelFormat.dwFourCC.codePointAt(3)-48;
  }
  public String toString() {
  	StringBuffer result=new StringBuffer();
	  result.append("FourCC: \""+FourCC+"\"\n");
  	result.append("ddsd: \n"+ddsd.toString());
	  return result.toString();
  }
  public void toPng(String oufn) throws IOException {
	  PngOutputStream png=new PngOutputStream(oufn,ddsd.dwWidth,ddsd.dwHeight,true);
	  writePng(png);
  }
  public void toPng(OutputStream ouf) throws IOException {
	  PngOutputStream png=new PngOutputStream(ouf,ddsd.dwWidth,ddsd.dwHeight,true);
	  writePng(png);
  }
  public void writePng(PngOutputStream png) throws IOException {
    DXT[] bData1;
	  bData1=new DXT[ddsd.dwWidth/4];
	  for(int blockrow=0;blockrow<(ddsd.dwHeight/4);blockrow++) {
      for(int blockcol=0;blockcol<bData1.length;blockcol++) {
		    bData1[blockcol]=DXT.read(Inf,compFormat);
	    }
	    for(int pixrow=0;pixrow<4;pixrow++) {
	      for(int blockcol=0;blockcol<bData1.length;blockcol++) {
	        png.write(bData1[blockcol].readRow(pixrow)); 	
  	    }
	    }
  	} 
	  png.close();
  }
  public static void main(String[] args) throws FileNotFoundException, IOException {
    File Folder=new File("e:\\Orbiter10\\Textures\\");
    File[] files=Folder.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
    	return name.matches("Earth.*\\.dds");
      }
    });
    for(File f:files) {
      System.out.println(f);
      try {
	      DirectDrawSurface DDS=new DirectDrawSurface(new FileInputStream(f));
        System.out.println(DDS.toString());
        System.out.println(Folder.getPath()+"\\"+f.getName()+".png");
        DDS.toPng(Folder.getPath()+"\\DDS Preview\\"+f.getName()+".png");
      } catch (IllegalArgumentException e) {e.printStackTrace();}
    }
  }
}
