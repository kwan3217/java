/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.wwv;

import javax.sound.sampled.*;
import org.kwansystems.tools.chart.*;

/**
 *
 * @author chrisj
 */
public class KWWV {
  public static final int sampleRate=48000;
  public static final int maxAmplitude=Short.MAX_VALUE;
  static byte[][] second=new byte[60][sampleRate*2];
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
  public static void main(String[] args) {
    for(int i=0;i<second.length;i++) {
      silence(i,0,1);
      tone(i,0,1,500,0.5);
      double Subcarrier=i%2==0?0.2:0.5;
      tone(i,0,Subcarrier,100,0.5);
      tone(i,Subcarrier,1,100,0.05);
      silence(i,0,0.03);
      silence(i,0.99,1.0);
      tone(i,0,0.005,1200,1.0);
    }
    ChartRecorder C=new ArrayListChartRecorder();
    for(int i=0;i<48000;i++) {
      C.Record((double)(i)/(double)sampleRate, "Sample", (double)getSample(1,i));
    }
    C.PrintTable(new DisplayPrinter());
    
    AudioFormat format=new AudioFormat(sampleRate, 16,1,true,true);
    DataLine.Info info=new DataLine.Info(SourceDataLine.class, format);
    Mixer MM=null;
    SourceDataLine line;
    try {
      line=(SourceDataLine)AudioSystem.getLine(info);
      line.open();
      line.start();
    } catch (LineUnavailableException ex) {
      throw new RuntimeException(ex);
    }
    for(int i=0;i<second.length;i++) {
      System.out.println(i);
      line.write(second[i], 0, second[i].length);
    }
    
  }
}
