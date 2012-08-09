package org.kwansystems.pov.btg;

import java.io.*;
import java.util.*;
import org.kwansystems.tools.*;
import static org.kwansystems.tools.Endian.*;
import static org.kwansystems.pov.btg.BTG2mesh2.*;

public class BTG2mesh2 implements Povable {
  static long readUInt(InputStream Inf) throws IOException {
    return ((long)(readInt(Inf))) & 0xFFFFFFFFL;
  }
  static int readInt(InputStream Inf) throws IOException {
    return swapEndian(Endian.readInt(Inf));
  }
  static int readUShort(InputStream Inf) throws IOException {
    return ((int)(readShort(Inf))) & 0xFFFF;
  }
  static short readShort(InputStream Inf) throws IOException {
    return swapEndian(Endian.readShort(Inf));
  }
  static short readUByte(InputStream Inf) throws IOException {
    int in=Inf.read();
    in=in & 0xFF;
    short out=(short)in;
    return out;
  }
  static byte readByte(RandomAccessFile Inf) throws IOException {
    return (byte)swapEndian(Inf.read());
  }
  int Version;
  int Magic;
  Date CreationTime;
  BTGObject[] Objects;

  public BTG2mesh2(InputStream Inf) throws IOException {
    Version=readUShort(Inf);
    Magic=readUShort(Inf);
    CreationTime=new Date(readUInt(Inf)*1000);
    System.out.println(CreationTime.toString());
    int nObjects=readUShort(Inf);
    Objects=new BTGObject[nObjects];
    for(int i=0;i<nObjects;i++) {
      Objects[i]=BTGObject.read(Inf);
      System.out.println(Objects[i]);
    }
  }
  public String toString() {
    String result="";
    for(BTGObject B:Objects) result=result+B.toString()+"\n";
    return result;
  }
  public static void main(String args[] ) throws IOException {
    File d=new File("C:\\Users\\jeppesen\\Desktop\\w106n32\\");
    String[] f=d.list(new FilenameFilter() {public boolean accept(File dir, String name) {
      return name.endsWith(".btg.gz");
    }});
    //String s="C:\\Users\\jeppesen\\Desktop\\w106n32\\"+f[0];
    String s="C:\\Users\\jeppesen\\Desktop\\1220224.btg";
    System.out.println(s);
    InputStream Inf;
    if (s.endsWith(".gz")) {
      Inf=new java.util.zip.GZIPInputStream(new FileInputStream(s));
    } else {
      Inf=new FileInputStream(s);
    }
    BTG2mesh2 Imp=new BTG2mesh2(Inf);
    System.out.print(Imp.toPov());
  }
  private void vertex_vectors(PrintWriter ouf) {
    int acc=0;
    for(BTGObject B:Objects) if(B.ObjectType==1) {
      acc+=B.n;
    }
    if(acc!=0) {
      ouf.printf("  vertex_vectors {\n    %d", acc);
      for(BTGObject B:Objects) if(B.ObjectType==1) {
        ouf.println(B.toPov());
      }
      ouf.println("  }");
    }
  }
  private void normal_vectors(PrintWriter ouf) {
    int acc=0;
    for(BTGObject B:Objects) if(B.ObjectType==2) {
      acc+=B.n;
    }
    if(acc!=0) {
      ouf.printf("  normal_vectors {\n    %d", acc);
      for(BTGObject B:Objects) if(B.ObjectType==2) {
        ouf.println(B.toPov());
      }
      ouf.println("  }");
    }
  }
  private void translate(PrintWriter ouf) {
    for(BTGObject B:Objects) if(B.ObjectType==0) {
      ouf.println(B.toPov());
    }
  }
  public String toPov() {
    StringWriter S=new StringWriter();
    PrintWriter ouf=new PrintWriter(S);
    ouf.println("mesh2 {");
    vertex_vectors(ouf);
    normal_vectors(ouf);
    translate(ouf);
    ouf.println("}");
    return S.toString();
  }
}
