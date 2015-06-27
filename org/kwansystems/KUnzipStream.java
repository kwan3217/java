/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems;

import java.io.*;
import org.kwansystems.tools.time.*;

/**
 *
 * @author jeppesen
 */
public class KUnzipStream extends FilterInputStream {
  /* Read big-endian 32-bit signed integer from input stream */
  public static int readInt(InputStream inf) throws IOException {
    byte[] result=new byte[4];
    if(inf.read(result)!=4) throw new IOException("Waah!");
    int b0=result[3] & 0xFF;
    int b1=(result[2]<<8) & 0xFF00;
    int b2=(result[1]<<16) & 0xFF0000;
    int b3=(result[0]<<24) & 0xFF000000;
    return (b0 | b1 | b2 | b3);
  }
  /* Read big-endian 16-bit signed integer from input stream */
  public static short readShort(InputStream inf) throws IOException {
    byte[] result=new byte[2];
    if(inf.read(result)!=2) throw new IOException("Waah!");
    int b0=result[1] & 0xFF;
    int b1=(result[0]<<8) & 0xFF00;
    return (short)(b0 | b1);
  }
  /* Read big-endian 16-bit unsigned integer from input stream */
  public static int readUShort(InputStream inf) throws IOException {
    byte[] result=new byte[2];
    if(inf.read(result)!=2) throw new IOException("Waah!");
    int b0=result[1] & 0xFF;
    int b1=(result[0]<<8) & 0xFF00;
    return (b0 | b1);
  }
  public static String readSZ(InputStream Lin) throws IOException {
    StringBuffer s=new StringBuffer();
    int c=Lin.read();
    while(c>0) {
      s.append((char)c);
      c=Lin.read();
    }
    return s.toString();
  }
  public static String[] osNames=new String[]{
    "FAT filesystem (MS-DOS, OS/2, NT/Win32)",
    "Amiga",
    "VMS (or OpenVMS)",
    "Unix",
    "VM/CMS",
    "Atari TOS",
    "HPFS filesystem (OS/2, NT)",
    "Macintosh",
    "Z-System",
    "CP/M",
    "TOPS-20",
    "NTFS filesystem (NT)",
    "QDOS",
    "Acorn RISCOS",
    "unknown"
  };
  public class ZLIBBlockHeader {
    int CMF,FLG;
    int FCHECK;
    boolean FDICT;
    int FLEVEL;
    int DICTID;
    public ZLIBBlockHeader(InputStream Lin) throws IOException {
      CMF=Lin.read();
      if(CMF!= 8) throw new IllegalArgumentException("Not a zlib block, unknown CMF (is "+CMF+", should be 8)");
      FLG=Lin.read();
      FCHECK=FLG & 0x1F;
      int checkVal=(CMF<<8+FLG)%31;
      if(checkVal!=0) throw new IllegalArgumentException("Check bits in block header failed (CMF="+String.format("%2X",CMF)+", FLG="+String.format("%2X",FLG)+", %31="+checkVal+", should be 0)");
      FDICT   =(((FLG>>5)&1)>0);
      FLEVEL  =FLG>>6;
      if(FDICT) {
        DICTID=readInt(Lin);
      }
    }
    public class DeflateBlockHeader {

    }
    @Override
	public String toString() {
      StringWriter result=new StringWriter();
      PrintWriter ouf=new PrintWriter(result,true);
      ouf.printf("CMF:   %02X (%d)\n",CMF,CMF);
      ouf.printf("FLG:   %02X (%d)\n",FLG,FLG);
      ouf.printf(" FCHECK:   %02X (%d)\n",FCHECK,FCHECK);
      ouf.printf(" FDICT:    %b\n",FDICT);
      ouf.printf(" FLEVEL:   %02X (%d)\n",FCHECK,FCHECK);
      if(FDICT) {
        ouf.printf("DICTID:    %08X (%d)\n",DICTID,DICTID);
      }
      ouf.close();
      return result.toString();
    }
  }
  public class GZIPFileHeader {
    int ID1,ID2,CM,FLG;
    boolean FTEXT,FHCRC,FEXTRA,FNAME,FCOMMENT;
    int MTIME;
    int XFL,OS;
    int XLEN;
    byte[] extraField;
    String name;
    String comment;
    int CRC16;
    public GZIPFileHeader(InputStream Lin) throws IOException {
      ID1=Lin.read();
      if(ID1!= 31) throw new IllegalArgumentException("Not a gzip file, bad ID1 (is "+ID1+", should be 31)");
      ID2=Lin.read();
      if(ID2!=139) throw new IllegalArgumentException("Not a gzip file, bad ID2 (is "+ID2+", should be 139)");
      CM=Lin.read();
      if(CM!=8) throw new IllegalArgumentException("Unrecognized compression method (is "+CM+", should be 8)");
      FLG=Lin.read();
      FTEXT   =(((FLG>>0)&1)>0);
      FHCRC   =(((FLG>>1)&1)>0);
      FEXTRA  =(((FLG>>2)&1)>0);
      FNAME   =(((FLG>>3)&1)>0);
      FCOMMENT=(((FLG>>4)&1)>0);
      if(((FLG>>5)&1)>0) throw new IllegalArgumentException("FLG5!=0");
      if(((FLG>>6)&1)>0) throw new IllegalArgumentException("FLG6!=0");
      if(((FLG>>7)&1)>0) throw new IllegalArgumentException("FLG7!=0");
      MTIME=readInt(Lin);
      XFL=Lin.read();
      OS=Lin.read();
      if(FEXTRA) {
        XLEN=readUShort(Lin);
        extraField=new byte[XLEN];
        Lin.read(extraField);
      }
      if(FNAME)    name   =readSZ(Lin);
      if(FCOMMENT) comment=readSZ(Lin);
      if(FHCRC)    CRC16  =readUShort(Lin);
    }
    @Override
	public String toString() {
      StringWriter result=new StringWriter();
      PrintWriter ouf=new PrintWriter(result,true);
      ouf.printf("ID1:   %02X (%d)\n",ID1,ID1);
      ouf.printf("ID2:   %02X (%d)\n",ID2,ID2);
      ouf.printf("CM:    %02X (%d)\n",CM, CM);
      ouf.printf("FLG:   %02X (%d)\n",FLG,FLG);
      ouf.printf(" FTEXT:    %b\n",FTEXT);
      ouf.printf(" FHCRC:    %b\n",FHCRC);
      ouf.printf(" FEXTRA:   %b\n",FEXTRA);
      ouf.printf(" FNAME:    %b\n",FNAME);
      ouf.printf(" FCOMMENT: %b\n",FCOMMENT);
      ouf.printf("MTIME: %08X (%d) - %s\n",MTIME,MTIME,new Time(MTIME,TimeUnits.Seconds,TimeScale.UTC,TimeEpoch.Java));
//      ouf.printf("MTIME: %08X (%d) - %s\n",MTIME,MTIME,new Date(MTIME*1000L));
      ouf.printf("XFL:   %02X (%d)\n",XFL,XFL);
      ouf.printf("OS:    %02X (%d) - %s\n",OS,OS,OS<osNames.length?osNames[OS]:osNames[osNames.length-1]);
      if(FNAME) {
        ouf.printf("Name:    '%s'\n",name);
      }
      if(FCOMMENT) {
        ouf.printf("Comment: '%s'\n",comment);
      }
      if(FHCRC) {
        ouf.printf("CRC16:   %04X (%d)\n",CRC16,CRC16);
      }
      ouf.close();
      return result.toString();
    }
  }
  public GZIPFileHeader head;
  public KUnzipStream(InputStream Lin) throws IOException {
    super(Lin);
    head=new GZIPFileHeader(in);
//    System.out.println(new ZLIBBlockHeader(in).toString());
  }
  public static void main(String[] args) throws IOException {
    String infn="/data/home/horsehead/jeppesen/5887/cips_sci_1c_orbit_05887_2008-145_v03.20.nc.gz";
    String oufn;
    if(args.length>1) {
      oufn=args[1];
    } else {
      if(infn.contains(".gz")) {
        oufn=infn.substring(0, infn.length()-3);
      } else {
        throw new IllegalArgumentException("Can't figure out unzip name for "+infn);
      }
    }
    FileOutputStream ouf=new FileOutputStream(oufn);
    KUnzipStream inf=new KUnzipStream(new FileInputStream(infn));
    System.out.println(inf.head.toString());
    byte[] buf=new byte[1024];
    int len=inf.read(buf);
    int i=0;
    while(len>0) {
      i++;
      if(i%100==0) System.out.print(".");
      if(i%8000==0) System.out.println();
      ouf.write(buf,0,len);
      len=inf.read(buf);
    }
    inf.close();
    ouf.close();
  }
}
