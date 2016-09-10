package org.kwansystems.image.dxt;

import java.io.*;

public abstract class DXT {
  int Format;
  public byte[][] readOut() {
  	byte[][] result=new byte[4][];
	  for(int i=0;i<4;i++) result[i]=readRow(i);
	  return result;
  }
  public abstract void readColor(InputStream Inf) throws IOException;
  public abstract void readAlpha(InputStream Inf) throws IOException;
  public abstract byte[] readRow(int row);
  public DXT(InputStream Inf, int Lformat) throws IOException {
    Format=Lformat;
    readAlpha(Inf);
    readColor(Inf);
  }
  public static DXT read(InputStream Inf,int Format) throws IOException {
	  switch(Format) {
	    case 1:
		    return new DXT1(Inf,Format);
      case 2:
      case 3:
        return new DXT23(Inf,Format);
      case 4:
      case 5:
        return new DXT45(Inf,Format);
  	  default:
	  	  throw new IllegalArgumentException(String.format("Format %d not supported yet!",Format));
	  } 
  }
}
