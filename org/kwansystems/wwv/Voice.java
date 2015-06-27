/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kwansystems.wwv;

import org.kwansystems.tools.*;
import java.io.*;

/**
 *
 * @author chrisj
 */
public class Voice {
  public char  ChunkID[];
  public int   ChunkSize;
  public char  Format[];
  public char  Subchunk1ID[];
  public int   Subchunk1Size;
  public short AudioFormat;
  public short NumChannels;
  public int   SampleRate;
  public int   ByteRate;
  public short BlockAlign;
  public short BitsPerSample;
  public char  Subchunk2ID[];
  public int   Subchunk2Size;
  public short data[];
  public int   replayPtr;
  public boolean done() {
    return replayPtr>=(Subchunk2Size/2);
  }
  public Voice(String infn) throws IOException {
    DataInputStream inf=new DataInputStream(new FileInputStream(infn));
    ChunkID=new char[4];
    for(int i=0;i<4;i++) {
      ChunkID[i]=(Character.toChars(inf.read()))[0];
    }
    ChunkSize=Endian.swapEndian(inf.readInt());
    Format=new char[4];
    for(int i=0;i<4;i++) {
      Format[i]=(Character.toChars(inf.read()))[0];
    }
    Subchunk1ID=new char[4];
    for(int i=0;i<4;i++) {
      Subchunk1ID[i]=(Character.toChars(inf.read()))[0];
    }
    Subchunk1Size=Endian.swapEndian(inf.readInt());
    AudioFormat=Endian.swapEndian(inf.readShort());
    NumChannels=Endian.swapEndian(inf.readShort());
    SampleRate=Endian.swapEndian(inf.readInt());
    ByteRate=Endian.swapEndian(inf.readInt());
    BlockAlign=Endian.swapEndian(inf.readShort());
    BitsPerSample=Endian.swapEndian(inf.readShort());
    Subchunk2ID=new char[4];
    for(int i=0;i<4;i++) {
      Subchunk2ID[i]=(Character.toChars(inf.read()))[0];
    }
    Subchunk2Size=Endian.swapEndian(inf.readInt());
    data=new short[Subchunk2Size/2];
    for(int i=0;i<Subchunk2Size/2;i++) {
      data[i]=Endian.swapEndian(inf.readShort());
    }
    inf.close();
    replayPtr=0;
  }
  public static String genText(String text, String tmp, String wav, String voice, int sampleRate) throws IOException {
    PrintWriter ouf=new PrintWriter(new FileWriter(tmp));
    ouf.println(text);
    ouf.close();
    String cmd=String.format("text2wave -F %d -o %s %s -eval (voice_%s)",sampleRate,wav,tmp,voice);
//    System.out.println(cmd);
    try {
      Runtime.getRuntime().exec(cmd).waitFor();
    } catch (InterruptedException E) {
      E.printStackTrace();
    }
    return wav;
  }
  public Voice(String text, String tmp, String wav, String voice, int sampleRate) throws IOException {
    this(genText(text,tmp,wav,voice,sampleRate));
  }
  public Voice(String text, String tmp, String wav, int sampleRate) throws IOException {
    this(text,tmp,wav,"us2_mbrola",sampleRate);
  }
  public int samplesLeft() {
    return Subchunk2Size/2-replayPtr;
  }
  public short getSample() {
    short result=data[replayPtr];
    replayPtr++;
    return result;
  }
  public static void main(String[] args) throws IOException {
    Voice RW=new Voice("/tmp/atthetone.wav");
  }

    void mix(byte[] tone, int i) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
