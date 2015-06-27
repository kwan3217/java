package org.kwansystems.fat16;

import java.io.*;
import static org.kwansystems.tools.Endian.*;

public class Fat16Boot {
  String OEMName;
  int bytesPerSector;
  int sectorsPerCluster;
  int reservedSectors;
  int numFATs;
  int maxRootDirEntries;
  int totalSectors;
  int mediaDescriptor;
  int sectorsPerFAT;
  int sectorsPerTrack;
  int numHeads;
  int numHiddenSectors;
  int physicalDriveNum;
  int reserved1;
  int extBootSig;
  int serial;
  String fatType;
  public Fat16Boot(RandomAccessFile inf) throws IOException {
    byte[] buf=new byte[512];
    inf.read(buf);
    OEMName=new String(readBytes(buf,0x03,8));
    bytesPerSector=(int)readShort(buf,0x0b) & 0xFFFF;
    sectorsPerCluster=(int)buf[0x0d] & 0xFF;
    reservedSectors=(int)readShort(buf,0x0b) & 0xFFFF;

    inf.seek(0x0b);
  }

}
