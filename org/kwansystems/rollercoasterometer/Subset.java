package org.kwansystems.rollercoasterometer;

import java.io.*;

public class Subset {
  public static final String[] columnName=new String[] {
    "3.3V reference",
    "-X acc (Positive DN is accelerate back)",
    "-Y acc (Positive DN is accelerate left)",
    "-Z acc (Positive DN is accelerate up)",
    "-Z rot (positive DN is yaw left)",
    "Time Code",
    "-x rot (Positive DN is roll left)",
    "+y rot (Positive DN is pitch up)"
  };
  public static void read(RandomAccessFile inf, PrintWriter ouf, int start, int stop) throws IOException {
    inf.seek(start*9*2);
    ouf.printf("Sample");
    for(int k=0;k<8;k++) {
      ouf.printf(",%s",columnName[k]);
    }
    ouf.println();
    for(int i=start;i<stop;i++) {
      long[] mean=new long[8];
      for(int k=0;k<8;k++) {
        mean[k]+=inf.readShort();
      }
      short junk=inf.readShort();
      ouf.printf("%8d",i);
      for(int k=0;k<8;k++) {
        ouf.printf(",%4d",mean[k]);
      }
      ouf.println();
    }
    ouf.close();
  }
  public static void main(String[] args) throws IOException {
    //Log19 was taken at home at +-4.0g setting
    read(new RandomAccessFile("Data/Rollercoasterometry/raw/LOG19.TXT","r"),
         new PrintWriter("Data/Rollercoasterometry/BatteryCalPZA.csv"),
         700,13300);
    read(new RandomAccessFile("Data/Rollercoasterometry/raw/LOG19.TXT","r"),
         new PrintWriter("Data/Rollercoasterometry/BatteryCalMZA.csv"),
         14000,24900);
    read(new RandomAccessFile("Data/Rollercoasterometry/raw/LOG19.TXT","r"),
         new PrintWriter("Data/Rollercoasterometry/BatteryCalPYA.csv"),
         26500,45100);
    read(new RandomAccessFile("Data/Rollercoasterometry/raw/LOG19.TXT","r"),
         new PrintWriter("Data/Rollercoasterometry/BatteryCalMYA.csv"),
         46000,57500);
    read(new RandomAccessFile("Data/Rollercoasterometry/raw/LOG19.TXT","r"),
         new PrintWriter("Data/Rollercoasterometry/BatteryCalPXA.csv"),
         58500,75400);
    read(new RandomAccessFile("Data/Rollercoasterometry/raw/LOG19.TXT","r"),
         new PrintWriter("Data/Rollercoasterometry/BatteryCalMXA.csv"),
         76300,95600);
  }
}
