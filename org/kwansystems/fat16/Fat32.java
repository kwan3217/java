/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.fat16;

import java.io.*;
import static org.kwansystems.tools.Endian.*;

/**
 *
 * @author chrisj
 */
public class Fat32 {
  String OEMName;
  int bytesPerSector;
  int sectorsPerCluster;
  int reservedSectors;
  int numFATs;
  int maxRootDirEntries;
  long totalSectors;
  int mediaDescriptor;
  long sectorsPerFAT;
  int sectorsPerTrack;
  int numHeads;
  long numHiddenSectors;
  int physicalDriveNum;
  int reserved1;
  int extBootSig;
  int serial;
  String fatType;
  int fat1[];
  int fat2[];
  RandomAccessFile inf;
  public String toString() {
    String result="";
    result+="OEMName:           "+OEMName;
    result+="\nbytesPerSector:    "+bytesPerSector;
    result+="\nsectorsPerCluster: "+sectorsPerCluster;
    result+="\nreservedSectors:   "+reservedSectors;
    result+="\nnumFATs:           "+numFATs;
    result+="\nmaxRootDirEntries: "+maxRootDirEntries;
    result+="\ntotalSectors:      "+totalSectors;
    result+="\nmediaDescriptor:   "+mediaDescriptor;
    result+="\nsectorsPerFAT:     "+sectorsPerFAT;
    result+="\nsectorsPerTrack:   "+sectorsPerTrack;
    result+="\nnumHeads:          "+numHeads;
    result+="\nnumHiddenSectors:  "+numHiddenSectors;
    return result;
  }
  public Fat32(RandomAccessFile Linf) throws IOException {
    inf=Linf;
    byte[] buf=new byte[512];
    inf.read(buf);
    OEMName=new String(readBytes(buf,0x03,8));
    bytesPerSector=readLEUShort(buf,0x0b);
    sectorsPerCluster=readUByte(buf,0x0d);
    reservedSectors=readLEUShort(buf,0x0e);
    numFATs=readUByte(buf,0x10);
    maxRootDirEntries=readLEUShort(buf,0x11);
    totalSectors=readLEUShort(buf,0x13);
    if(totalSectors==0) totalSectors=readLEUInt(buf,0x20);
    mediaDescriptor=readUByte(buf,0x15);
    sectorsPerFAT=readLEUShort(buf,0x16);
    if(sectorsPerFAT==0) sectorsPerFAT=readLEUInt(buf,0x24);
    sectorsPerTrack=readLEUShort(buf,0x18);
    numHeads=readLEUShort(buf,0x1a);
    numHiddenSectors=readLEUInt(buf,0x1C);
    inf.seek(bytesPerSector*reservedSectors);
    fat1=new int[(int)sectorsPerFAT*bytesPerSector/4];
    fat2=new int[(int)sectorsPerFAT*bytesPerSector/4];
    for(int i=0;i<fat1.length;i++) {
      fat1[i]=swapEndian(inf.readInt());
    }
    for(int i=0;i<fat2.length;i++) {
      fat2[i]=swapEndian(inf.readInt());
    }
  }
  public void dumpChain(String path, int chainStart, int[] fat, boolean[] clusterUsed) throws IOException {
    RandomAccessFile ouf=new RandomAccessFile(String.format("%s/file%08d",path,chainStart),"rw");
    int nextCluster=chainStart;
    int dataStart=((int)(bytesPerSector*(reservedSectors+sectorsPerFAT*numFATs)));
    do {
      clusterUsed[nextCluster]=true;
      long seekTo=dataStart+(long)bytesPerSector*(long)sectorsPerCluster*((long)nextCluster-2);
      inf.seek(seekTo);
      byte[] buf=new byte[bytesPerSector];
      for(int i=0;i<sectorsPerCluster;i++) {
        inf.read(buf);
        ouf.write(buf);
      }
      nextCluster=fat[nextCluster];
    } while (nextCluster>0 && nextCluster<=0xFFFFFEF);
    ouf.close();
  }
  public void dumpToTheEnd(String path, int chainStart) throws IOException {
    RandomAccessFile ouf=new RandomAccessFile(String.format("%s/file%08d",path,chainStart),"rw");
    int nextCluster=chainStart;
    int dataStart=((int)(bytesPerSector*(reservedSectors+sectorsPerFAT*numFATs)));
    for(;;) {
      long seekTo=dataStart+(long)bytesPerSector*(long)sectorsPerCluster*((long)nextCluster-2);
      if(seekTo>inf.length()) {
        ouf.close();
        return;
      }
      inf.seek(seekTo);
      byte[] buf=new byte[bytesPerSector];
      for(int i=0;i<sectorsPerCluster;i++) {
        inf.read(buf);
        ouf.write(buf);
      }
      nextCluster++;
    } 
  }
  public void dumpChains(String path, int[] fat) throws IOException {
    boolean[] clusterUsed=new boolean[fat.length];
    clusterUsed[0]=true;
    clusterUsed[1]=true;
    for(int i=0;i<clusterUsed.length;i++) {
      if(!clusterUsed[i]) {
        if(fat[i]<2) {
          clusterUsed[i]=true;
        } else if(fat[i]>0xffffff0 & fat[i]<=0xffffff7) {
          clusterUsed[i]=true;
        } else {
          dumpChain(path,i,fat,clusterUsed);
        }
      }
    }
  }
  public static void main(String[] args) throws IOException {
    RandomAccessFile inf=new RandomAccessFile("/home/chrisj/Videos/Fwd1.dump","r");
    Fat32 F=new Fat32(inf);
    System.out.println(F);
//    F.dumpChains("/home/chrisj/Videos/Fwd1",F.fat1);
//    F.dumpChains("/home/chrisj/Videos/Fwd2",F.fat2);
//    F.dumpToTheEnd("/home/chrisj/Videos/Fwd1",13547);
    inf.close();

    inf=new RandomAccessFile("/home/chrisj/Videos/Aft1.dump","r");
    F=new Fat32(inf);
    System.out.println(F);
//    F.dumpChains("/home/chrisj/Videos/Aft1",F.fat1);
//    F.dumpChains("/home/chrisj/Videos/Aft2",F.fat2);
//    F.dumpToTheEnd("/home/chrisj/Videos/Aft1",13575);
    inf.close();
  }
}
