/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.wwv;

import javax.sound.sampled.*;
import org.kwansystems.tools.time.*;
import static java.lang.Math.*;

/**
 *
 * @author chrisj
 */
public class KWWVB {
  public static final int sampleRate=48000;
  public static final int maxAmplitude=Short.MAX_VALUE;
  static byte[][] second=new byte[3][sampleRate*4];
  public static void silence(int Lsecond, double start, double stop) {
    for(int i=(int)(start*sampleRate);i<(int)(stop*sampleRate);i++) {
      second[Lsecond][i*2]=0;
      second[Lsecond][i*2+1]=0;
    }
  };
  public static short getSample(int Lsecond, int i) {
    int b0=(second[Lsecond][i*2] & 0xff);
    int b1=(second[Lsecond][i*2+1] & 0xff);
    int result=b0 << 8 | b1;
    return (short)result;
  }
  public static void putSample(int Lsecond, int i, short sample) {
    second[Lsecond][i*2+1]=(byte)(sample & 0xFF);
    second[Lsecond][i*2]=(byte)(sample >>> 8);
  }
  //All tones are such that their phase is 0deg at the start of the second. How convenient!
  public static void tone(int Lsecond, double start, double stop, double Hz, double Mod) {
    for(int i=(int)(start*sampleRate);i<(int)(stop*sampleRate);i++) {
      short sample=(short)(Math.sin(2*Math.PI*i*Hz/sampleRate)*Mod*maxAmplitude);
      short oldSample=getSample(Lsecond, i);
      sample+=oldSample;
      putSample(Lsecond,i,sample);
    }
  }
  public static int[] breakdownTime() {
    long t=System.currentTimeMillis();
    Time D=new Time(t,TimeUnits.Milliseconds,TimeScale.UTC,TimeEpoch.Java);
    int m=(int)(t%1000);
    t=t/1000;
    int s=(int)(t%60);
    t=t/60;
    int n=(int)(t%60);
    t=t/60;
    int h=(int)(t%24);
    int[] ymd=D.getymdhnsu();
    int d=ymd[2];
    int mm=ymd[1];
    int y=ymd[0]%100;
    int leapYear=(y%4==0)?1:0; //Don't bother with gregorian rules, doesn't matter until 2100
    switch(mm) {
      case 12:
        d+=30;
      case 11:
        d+=31;
      case 10:
        d+=30;
      case  9:
        d+=31;
      case  8:
        d+=31;
      case  7:
        d+=31;
      case  6:
        d+=31;
      case  5:
        d+=31;
      case  4:
        d+=31;
      case  3:
        d+=28+leapYear;
      case  2:
        d+=31;
      case  1:
    }
    //now d is doy
    if(m>500) {
      m=m-1000;
      s++;
      if(s>=60) {
        s-=60;
        m++;
        if(m>=60) {
          m-=60;
          h++;
          if(h>=24) {
            h-=24;
            d++;
            while(d>365+leapYear) {
              d-=365+leapYear;
              y++;
              leapYear=(y%4==0)?1:0;
            }
          }
        }
      }
    }
    int dut1=3;
    int ls=0;
    int dst=3;
    return new int[] {y,d,h,n,s,m,dut1,leapYear,ls,dst};
  }
  static String[] toneMeaning=new String[] {
/*00*/    "Frame Marker",
/*01*/    "Minutes 40",
/*02*/    "Minutes 20",
/*03*/    "Minutes 10",
/*04*/    "Zero",
/*05*/    "Minutes  8",
/*06*/    "Minutes  4",
/*07*/    "Minutes  2",
/*08*/    "Minutes  1",
/*09*/    "Position Marker 1",
/*10*/    "Zero",
/*11*/    "Zero",
/*12*/    "Hours   20",
/*13*/    "Hours   10",
/*14*/    "Zero",
/*15*/    "Hours    8",
/*16*/    "Hours    4",
/*17*/    "Hours    2",
/*18*/    "Hours    1",
/*19*/    "Position Marker 2",
/*30*/    "Zero",
/*21*/    "Zero",
/*22*/    "Days   200",
/*23*/    "Days   100",
/*24*/    "Zero",
/*25*/    "Days    80",
/*26*/    "Days    40",
/*27*/    "Days    20",
/*28*/    "Days    10",
/*29*/    "Position Marker 3",
/*30*/    "Days     8",
/*31*/    "Days     4",
/*32*/    "Days     2",
/*33*/    "Days     1",
/*34*/    "Zero",
/*35*/    "Zero",
/*36*/    "DUT1     +",
/*37*/    "DUT1     -",
/*38*/    "DUT1     +",
/*39*/    "Position Marker 4",
/*40*/    "DUT1   0.8",
/*41*/    "DUT1   0.4",
/*42*/    "DUT1   0.2",
/*43*/    "DUT1   0.1",
/*44*/    "Zero",
/*45*/    "Year    80",
/*46*/    "Year    40",
/*47*/    "Year    20",
/*48*/    "Year    10",
/*49*/    "Position Marker 5",
/*50*/    "Year     8",
/*51*/    "Year     4",
/*52*/    "Year     2",
/*53*/    "Year     1",
/*54*/    "Zero",
/*55*/    "LYI      ",
/*56*/    "LSW      ",
/*57*/    "DST      2",
/*58*/    "DST      1",
/*59*/    "Position Marker 0",

  };

  public static int selectTone(int[] tt) {
    int y=tt[0];
    int d=tt[1];
    int h=tt[2];
    int n=tt[3];
    int s=tt[4];
    int m=tt[5];
    int dut1=tt[6];
    int leapYear=tt[7];
    int ls=tt[8];
    int dst=tt[9];
    int t=(tt[4]/10);
    int o=(tt[4]%10);
    if (o==4) return 0;
    if (o==9) return 2;
    switch (t) {
      case 0:
        switch(o) {
          case 0:
            return 2;
          case 1:
            return (((n/10)>>2) & 1);
          case 2:
            return (((n/10)>>1) & 1);
          case 3:
            return (((n/10)>>0) & 1);
          case 5:
            return (((n%10)>>3) & 1);
          case 6:
            return (((n%10)>>2) & 1);
          case 7:
            return (((n%10)>>1) & 1);
          case 8:
            return (((n%10)>>0) & 1);
        }
      case 1:
        switch(o) {
          case 0:
          case 1:
            return 0;
          case 2:
            return (((h/10)>>1) & 1);
          case 3:
            return (((h/10)>>0) & 1);
          case 5:
            return (((h%10)>>3) & 1);
          case 6:
            return (((h%10)>>2) & 1);
          case 7:
            return (((h%10)>>1) & 1);
          case 8:
            return (((h%10)>>0) & 1);
        }
        break;
      case 2:
        switch(o) {
          case 0:
          case 1:
            return 0;
          case 2:
            return (((d/100)>>1) & 1);
          case 3:
            return (((d/100)>>0) & 1);
          case 5:
            return (((d%100)/10>>3) & 1);
          case 6:
            return (((d%100)/10>>2) & 1);
          case 7:
            return (((d%100)/10>>1) & 1);
          case 8:
            return (((d%100)/10>>0) & 1);
        }
        break;
      case 3:
        switch(o) {
          case 0:
            return (((d%10)>>3) & 1);
          case 1:
            return (((d%10)>>2) & 1);
          case 2:
            return (((d%10)>>1) & 1);
          case 3:
            return (((d%10)>>0) & 1);
          case 5:
            return 0;
          case 6:
            return (dut1>=0)?1:0;
          case 7:
            return (dut1>=0)?0:1;
          case 8:
            return (dut1>=0)?1:0;
        }
        break;
      case 4:
        switch(o) {
          case 0:
            return ((abs(dut1)>>3) & 1);
          case 1:
            return ((abs(dut1)>>2) & 1);
          case 2:
            return ((abs(dut1)>>1) & 1);
          case 3:
            return ((abs(dut1)>>0) & 1);
          case 5:
            return (((y/10)>>3) & 1);
          case 6:
            return (((y/10)>>2) & 1);
          case 7:
            return (((y/10)>>1) & 1);
          case 8:
            return (((y/10)>>0) & 1);
        }
        break;
      case 5:
        switch(o) {
          case 0:
            return (((y%10)>>3) & 1);
          case 1:
            return (((y%10)>>2) & 1);
          case 2:
            return (((y%10)>>1) & 1);
          case 3:
            return (((y%10)>>0) & 1);
          case 5:
            return leapYear;
          case 6:
            return ls;
          case 7:
            return ((dst>>1) & 1);
          case 8:
            return ((dst>>0) & 1);
        }
        break;
    }
    return 0;
  }
  public static void main(String[] args) {
    for(int i=0;i<second.length;i++) {
      silence(i,0,2);
      tone(i,0,        0.2+i*0.3,400,0.25);
      tone(i,0.2+i*0.3,2,        500,0.5);
    }

    AudioFormat format=new AudioFormat(sampleRate, 16,1,true,true);
    DataLine.Info info=new DataLine.Info(SourceDataLine.class, format);
    Mixer MM=null;
    SourceDataLine line;
    try {
      line=(SourceDataLine)AudioSystem.getLine(info);
      line.open();
      line.start();
      System.out.println(line.available());
    } catch (LineUnavailableException ex) {
      throw new RuntimeException(ex);
    }
    long t=System.currentTimeMillis();
    t=t%60000;
    long h,n,m=t%1000;
    long s=t/1000;
    try {
      Thread.currentThread();
	Thread.sleep(1000 - m);
    } catch (InterruptedException ex) {
      
    }

    int length;
    String time;
    int[] tt;
    int tone;
    int offset=5;
    for(;;) {
      tt=breakdownTime();
      m=tt[5];
      tone=selectTone(tt);
      if(m<0) {
        time=String.format("Time is %02d/%03dT%02d:%02d:%02d-0.%03d dut1 %02d bit %d %s",tt[0],tt[1],tt[2],tt[3],tt[4],-tt[5],tt[6],tone,toneMeaning[tt[4]]);
      } else {
        time=String.format("Time is %02d/%03dT%02d:%02d:%02d+0.%03d dut1 %02d bit %d %s",tt[0],tt[1],tt[2],tt[3],tt[4],tt[5],tt[6],tone,toneMeaning[tt[4]]);
      }
      System.out.println(time);
      length=(int)(2*((1000+m-offset)*48));
      line.write(second[tone], 0, length);

      t=System.currentTimeMillis()%60000;
      m=t%1000;
      s=t/1000;
      System.out.println(1000-m);
      offset=(int)(20-((1000-m)/20));
      System.out.println(offset);
      try {
        Thread.currentThread();
		Thread.sleep(1000 - m);
      } catch (InterruptedException ex) {}
    }
  }
}
