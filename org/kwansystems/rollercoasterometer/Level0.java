package org.kwansystems.rollercoasterometer;

import java.io.*;
import org.kwansystems.tools.time.*;

public class Level0 {
  public static byte id(long samples) {
    if(samples<18) {
      return -1;
    }
    if(samples<22) {
      return 0;
    }
    if(samples<48) {
      return -2;
    }
    if(samples<52) {
      return 1;
    }
    if(samples<78) {
      return -3;
    }
    if(samples<85) {
      return 2;
    }
    return -4;
  }
  public static void getMinute(byte[] slots) {
    System.out.print("Have minute ");
  }
  public static void main(String[] args) throws IOException {
    getData("03");
    getData("04");
    getData("05");
    getData("06");
    getData("07");
    getData("10");
  }
  public static void getData(String suffix) throws IOException {
    String path="Data/Rollercoasterometry/";
    RandomAccessFile inf=new RandomAccessFile(path+"raw/LOG"+suffix+".txt","r");
    PrintWriter ouf=new PrintWriter(new FileWriter(path+"Log"+suffix+"_l0a.csv"));
    short V33,AX,AY,AZ,RX,RY,RZ,TC,junk;
    ouf.println("  Sample,Nano timestamp,Best Estimate PDT");
    int tcState=0;
    byte lastBit=0;
    long lastSwitch=0;
    boolean inSync=false;
    Minute thisMinute=null;
    Minute validMinute=null;
    boolean first=true;
    try {
      long i=0;
      long bit=0;
      while(true) {
        V33=inf.readShort();
        AX=inf.readShort();
        AY=inf.readShort();
        AZ=inf.readShort();
        RZ=inf.readShort();
        TC=inf.readShort();
        RX=inf.readShort();
        RY=inf.readShort();
        junk=inf.readShort();
        if(tcState==0) {
          if(TC>500) {
            tcState=1;
            byte thisBit=id(i-lastSwitch);
            ouf.printf("%8d,",lastSwitch,(i-lastSwitch)*10,bit,thisBit);
            if(validMinute!=null) {
              validMinute.tick();
              if(first) {
                System.out.println("First timestamp: "+suffix+" "+validMinute.toTime());
                first=false;
              }
              ouf.println(validMinute.toString()+","+validMinute.toTime());
            } else {
              ouf.println();
            }
            if(thisMinute!=null) {
              if(thisMinute.setBit(thisBit)) {
                validMinute=thisMinute;
                thisMinute=new Minute();
              }
            } else if(thisBit==2 && lastBit==2) {
              thisMinute=new Minute();
              thisMinute.setBit(thisBit);
            }
            lastBit=thisBit;
            bit++;
            lastSwitch=i;
          }
        } else {
          if(TC<50) {
            tcState=0;
            lastSwitch=i;
          }
        }
        i++;
      }
    } catch (EOFException e) {
      inf.close();
      ouf.close();
    }
  }
}
class Minute {
  byte[] slots=new byte[61];
  long slotStart;
  byte currentSlot=0;
  boolean complete=false;
  byte year;
  short doy;
  byte hour;
  byte minute;
  byte second;
  final static Time EpochNano=new Time(2099,1,1,0,0,0,TimeUnits.Seconds,TimeScale.PDT,TimeEpoch.J2000);
  final static Time EpochPDT=new Time(2009,4,18,8,17,58,TimeUnits.Seconds,TimeScale.PDT,TimeEpoch.J2000);
  final double diff=Time.difference(EpochNano, EpochPDT, TimeUnits.Seconds);
  private void interpret() {
    long    nt,nw;
    long    ht,hw;
    long dh,dt,dw;
    long    yt,yw;
    nt=               (slots[ 1]<<2)+(slots[ 2]<<1)+(slots[ 3]<<0);
    nw=(slots[ 5]<<3)+(slots[ 6]<<2)+(slots[ 7]<<1)+(slots[ 8]<<0);
    ht=                              (slots[12]<<1)+(slots[13]<<0);
    hw=(slots[15]<<3)+(slots[16]<<2)+(slots[17]<<1)+(slots[18]<<0);
    dh=                              (slots[22]<<1)+(slots[23]<<0);
    dt=(slots[25]<<3)+(slots[26]<<2)+(slots[27]<<1)+(slots[28]<<0);
    dw=(slots[30]<<3)+(slots[31]<<2)+(slots[32]<<1)+(slots[33]<<0);
    yt=(slots[45]<<3)+(slots[46]<<2)+(slots[47]<<1)+(slots[48]<<0);
    yw=(slots[50]<<3)+(slots[51]<<2)+(slots[52]<<1)+(slots[53]<<0);
    minute=(byte)(nt*10+nw);
    hour=(byte)(ht*10+hw);
    doy=(byte)(dh*100+dt*10+dw);
    year=(byte)(yt*10+yw);
    second=59;
  }
  public String toString() {
    carry();
    return String.format("%02d/%03dT%02d:%02d:%02d",year,doy,hour,minute,second);
  }
  public Time toTime() {
    carry();
    Time result=new Time(2000+year,1,1,hour,minute,second,TimeUnits.Seconds,TimeScale.PDT,TimeEpoch.J2000);
    result.add(doy-1,TimeUnits.Days);
    result.add(diff,TimeUnits.Seconds);
    return result;
  }
  public boolean setBit(byte bit) {
    slots[currentSlot]=bit;
    currentSlot++;
    if(currentSlot==60) {
      complete=true;
      interpret();
    }
    return complete;
  }
  private void carryHour() {
    while(hour>=24) {
      doy++;
      hour-=24;
    }
  }
  private void carryMinute() {
    while(minute>=60) {
      hour++;
      carryHour();
      minute-=60;
    }
  }
  private void carry() {
    while(second>86400) {
      doy++;
      second-=86400;
    }
    while(second>3600) {
      hour++;
      carryHour();
      second-=3600;
    }
    while(second>=60) {
      minute++;
      carryMinute();
      second-=60;
    }
  }
  public String tick() {
    second++;
    return toString();
  }
}
