/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.wwv;

import java.io.*;
import org.kwansystems.wwv.WWVScheduler.BCDBits;
import static org.kwansystems.wwv.WWVScheduler.BCDBits.*;
import java.util.*;
import javax.sound.sampled.*;
import org.kwansystems.tools.time.*;
import static java.lang.Math.*;

/**
 *
 * @author chrisj
 */
public class KWWVH {
  public static final int sampleRate=48000;
  public static final int maxAmplitude=Short.MAX_VALUE/2;
  public static Voice AtTheTone,nextAtTheTone;
  public static Voice thisAnnouncement, nextAnnouncement;
  static Map<WWVScheduler,byte[]> second=new HashMap<WWVScheduler,byte[]>();
  public static byte[] buildSecond(WWVScheduler WWVS) {
    byte[] result=new byte[sampleRate*4];
    //Lay down background tone if present
    if(WWVS.bgToneFreq>0) {
      tone(result,0,1,WWVS.bgToneFreq,/*0.5*/1.00);
    }
    //Lay down BCD code if present
    if(WWVS.bcd!=Blank) {
      tone(result,0,WWVS.bcd.length,100,/*0.18*/1.00);
 //     tone(result,WWVS.bcd.length,1,100,0.03);
    }
    //Lay down tick
    if(WWVS.tickLen>0) {
      silence(result,0,WWVS.tickLen+0.025);
      tone(result,0,WWVS.tickLen,WWVS.tickFreq,1.0);
    }
    //Lay down double tick if present
    if(WWVS.doubleTick) {
//      silence(result,0.1,0.105);
      tone(result,0.1,0.105,WWVS.tickFreq,1.0);
    }
    //Lay down end guard
    if(WWVS.endGuard) {
      silence(result,0.99,1.0);
    }
    return result;
  }
  public static void silence(byte[] second, double start, double stop) {
    for(int i=(int)(start*sampleRate);i<(int)(stop*sampleRate);i++) {
      second[i*2]=0;
      second[i*2+1]=0;
    }
  };
  public static short getSample(byte[] second, int i) {
    int b0=(second[i*2] & 0xff);
    int b1=(second[i*2+1] & 0xff);
    int result=b0 << 8 | b1;
    return (short)result;
  }
  public static void putSample(byte[] second, int i, short sample) {
    second[i*2+1]=(byte)(sample & 0xFF);
    second[i*2]=(byte)(sample >>> 8);
  }
  //All tones are such that their phase is 0deg at the start of the second. How convenient!
  public static void tone(byte[] second, double start, double stop, double Hz, double Mod) {
    for(int i=(int)(start*sampleRate);i<(int)(stop*sampleRate);i++) {
      short sample=(short)(Math.sin(2*Math.PI*i*Hz/sampleRate)*Mod*maxAmplitude);
      short oldSample=getSample(second, i);
      sample+=oldSample;
      putSample(second,i,sample);
    }
  }
  public static void mix(byte[] second, double start,Voice voice, WWVScheduler WWVS) {
    int secondSample=(int)(start*sampleRate);
    int samplesLeft=sampleRate-secondSample;
    if(voice.samplesLeft()<samplesLeft) samplesLeft=voice.samplesLeft();
    for(int i=0;i<samplesLeft;i++) {
      short sample=getSample(second, i+secondSample);
      sample+=voice.getSample();
      putSample(second,i+secondSample,sample);
    }
    //Reposition tick and silence as needed
    if(WWVS.tickLen>0) {
      silence(second,0,WWVS.tickLen+0.025);
      tone(second,0,WWVS.tickLen,WWVS.tickFreq,1.0);
    }
    //Lay down end guard
    if(WWVS.endGuard) {
      silence(second,0.99,1.0);
    }
  }
  public static byte[] selectTone(WWVScheduler tt) {
    if(second.containsKey(tt)) return second.get(tt);
    byte[] newSecond=buildSecond(tt);
    second.put(tt, newSecond);
    String time;
      int m=tt.tt[5];
      if(m<0) {
        time=String.format("Time is %02d/%03dT%02d:%02d:%02d-.%03d",tt.tt[0],tt.tt[1],tt.tt[2],tt.tt[3],tt.tt[4],-tt.tt[5]);
      } else {
        time=String.format("Time is %02d/%03dT%02d:%02d:%02d+.%03d",tt.tt[0],tt.tt[1],tt.tt[2],tt.tt[3],tt.tt[4], tt.tt[5]);
      }
      System.out.println(time);
    System.out.println("Number of things in the bucket: "+second.size());
    for(WWVScheduler WWVS:second.keySet()) {
      System.out.println(WWVS.toString());
    }
    return newSecond;
  }
  private static Runnable FestivalThread=new Runnable() {
    Calendar C=Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    public void run() {
      int oldmin=-1;
      for(;;) {
        Date D=new Date();
        C.setTime(D);
        C.add(Calendar.MINUTE, 1);
        int nh=C.get(Calendar.HOUR_OF_DAY);
        int nm=C.get(Calendar.MINUTE);
        if(nm!=oldmin) {
          oldmin=nm;
          try {
            nextAtTheTone=new Voice(
              String.format("At the tone, %d hour%s, %d minute%s, coordinated universal time.",nh, nh==1?" ":"s",nm,nm==1?" ":"s" ),
              "/tmp/atthetone.txt","/tmp/atthetone.wav",sampleRate);
          } catch (Throwable E) {
            System.out.println("Couldn't generate voice");
            E.printStackTrace();
          }
          WWVScheduler WWVSNext=new WWVScheduler(C.getTime(),false);
          try {
            switch(WWVSNext.Special) {
              case StationID:
                nextAnnouncement=new Voice("Kwan Systems Standard Time. "+
                                           "This is signal generator K W W V, Longmont Colorado, "+
                                           "broadcasting at a standard sample rate of 48 kilohertz, "+
                                           "providing time of day, standard interval, and other related cat annoyances. "+
                                           "Inquiries relating to these services may be sent to Kwan Systems, "+
                                           "nineteen oh 1 Red Cloud Road, Longmont Colorado, 8 0 5 0 4.",
                                           "/tmp/stationid.txt","/tmp/stationid.wav",sampleRate);
   
            }
          } catch (Throwable E) {
            System.out.println("Couldn't generate voice");
            E.printStackTrace();
          }
        }
        try {
	  Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
      }
    }
  };
  public static void main(String[] args) {
    AudioFormat format=new AudioFormat(sampleRate, 16,1,true,true);
    DataLine.Info info=new DataLine.Info(SourceDataLine.class, format);
    Mixer MM=null;
    new Thread(FestivalThread).start();
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
    int[] tt=new int[6];
    byte[] tone;
    int offset=5;
    Calendar C=Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    Voice activeVoice=null;
    for(;;) {
      Date D=new Date();
      C.setTime(D);
      WWVScheduler WWVS=new WWVScheduler(D,false);
      m=WWVS.tt[5];
//      System.out.println(WWVS.toString());
      if(activeVoice!=null) {
        tone=new byte[sampleRate*4];
        System.arraycopy(selectTone(WWVS), 0, tone, 0,tone.length);
        mix(tone,0,activeVoice,WWVS);
        if(activeVoice.done()) activeVoice=null;
      } else if(WWVS.tt[4]==52) {
        tone=new byte[sampleRate*4];
        System.arraycopy(selectTone(WWVS), 0, tone, 0,tone.length);
        activeVoice=nextAtTheTone;
        nextAtTheTone=null;
        mix(tone,0.5,activeVoice,WWVS);
        if(activeVoice.done()) activeVoice=null;
      } else if(WWVS.tt[4]==1 & nextAnnouncement!=null) {
        activeVoice=nextAnnouncement;
        nextAnnouncement=null;
        tone=new byte[sampleRate*4];
        System.arraycopy(selectTone(WWVS), 0, tone, 0,tone.length);
        mix(tone,0.0,activeVoice,WWVS);
        if(activeVoice.done()) activeVoice=null;
      } else {
        tone=selectTone(WWVS);
      }
      if(m<0) {
        time=String.format("Time is %02d/%03dT%02d:%02d:%02d-.%03d",WWVS.tt[0],WWVS.tt[1],WWVS.tt[2],WWVS.tt[3],WWVS.tt[4],-WWVS.tt[5]);
      } else {
        time=String.format("Time is %02d/%03dT%02d:%02d:%02d+.%03d",WWVS.tt[0],WWVS.tt[1],WWVS.tt[2],WWVS.tt[3],WWVS.tt[4], WWVS.tt[5]);
      }
//      System.out.println(time);
      length=(int)(2*((1000+m-offset)*48));
      line.write(tone, 0, length);

      t=System.currentTimeMillis()%60000;
      m=t%1000;
      s=t/1000;
      System.out.println(1000-m);
      offset=(int)(20-((1000-m)/20));
//      System.out.println(offset);
      try {
        Thread.currentThread();
	Thread.sleep(1000 - m);
      } catch (InterruptedException ex) {}
    }
  }
}
