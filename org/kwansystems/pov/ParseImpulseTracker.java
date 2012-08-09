package org.kwansystems.pov;

import java.io.*;

public class ParseImpulseTracker {
  class Header {
    String sig;
    String SongName;
    int PHighlightMinor;
    int PHighlightMajor;
    int OrdNum;
    int InsNum;
    int SmpNum;
    int PatNum;
    int TrackerVersion;
    int TrackerCompat;
    //Flags field
    boolean IsStereo;
    boolean Vol0MixOptimizations;
    boolean UseInstruments;
    boolean UseLinearSlides;
    boolean OldeEffects;
    boolean LinkGtoEF;
    boolean UseMidiPitch;
    boolean RequestMidiConf;
    //Special field
    boolean HasSongMessage;
    boolean HasMidiConf;
    int GlobalVolume;
    int MixVolume;
    int InitialSpeed;
    int InitialTempo;
    int Separation;
    int PitchWheelDepth;
    int[] ChannelVolume;
    int[] ChannelPan;
    public Header() {}; 
    public Header(RandomAccessFile Inf) throws IOException {
      sig=readNullTerminatedString(Inf,4);
      SongName=readNullTerminatedString(Inf,26);
      PHighlightMinor=Inf.read();
      PHighlightMajor=Inf.read();
      OrdNum=switchEndian2(Inf.readShort(),false);
      InsNum=switchEndian2(Inf.readShort(),false);
      SmpNum=switchEndian2(Inf.readShort(),false);
      PatNum=switchEndian2(Inf.readShort(),false);
      TrackerVersion=switchEndian2(Inf.readShort(),false);
      TrackerCompat=switchEndian2(Inf.readShort(),false);
      int Flags=switchEndian2(Inf.readShort(),false);
      IsStereo            =(Flags & (1<<0))!=0;
      Vol0MixOptimizations=(Flags & (1<<1))!=0;
      UseInstruments      =(Flags & (1<<2))!=0;
      UseLinearSlides     =(Flags & (1<<3))!=0;
      OldeEffects         =(Flags & (1<<4))!=0;
      LinkGtoEF           =(Flags & (1<<5))!=0;
      UseMidiPitch        =(Flags & (1<<6))!=0;
      RequestMidiConf     =(Flags & (1<<7))!=0;
      int Special=switchEndian2(Inf.readShort(),false);
      HasSongMessage      =(Special & (1<<0))>0;
      HasMidiConf         =(Special & (1<<3))>0;
    }
  }
  private static final String[] noteNames={"C-","C#","D-","D#","E-","F-","F#","G-","G#","A-","A#","B-"};
  public static String noteName(int note) {
    String result;
    if(note==254) {
      result="^^^"; //Note cut
    } else if(note==255) {
      result="==="; //Note off
    } else {
      int Octave=note/12;
      int Tone=note%12;
      result=noteNames[Tone]+Octave;
    }
    return result;
  }
  class PatternCell {
    int mask;
    int Note;
    int Inst;
    int VEP;
    int Cmd;
    int Val;
    public String toString() {
      return noteName(Note);//String.format("%03d",Note);
    }
  }
  class PatternRow {
    public PatternCell[] cell;
    public PatternRow(int len) {
      cell=new PatternCell[len];
    }
    public void stretch(int newLen) {
      PatternCell[] oldCells=cell;
      cell=new PatternCell[newLen];
      System.arraycopy(oldCells,0,cell,0,Math.min(oldCells.length,newLen));
    }
    public String toString() {
      String result="";
      for(int i=0;i<cell.length;i++) {
        if(cell[i]==null) {
          result+="---";
        } else {
          result+=cell[i].toString();
        }
        result+=" ";
      }
      return result;
    }
  }
  int[] stretch(int[] in, int newLen) {
    int[] out=new int[newLen];
    System.arraycopy(in,0,out,0,Math.min(newLen,in.length));
    return out;
  }
  class Pattern {
    PatternRow[] row;
    public Pattern() {}; 
    public Pattern(RandomAccessFile Inf) throws IOException {
      int MinNote=1000,MaxNote=-1000;
      int[] Mask=new int[0];
      int[] Note=new int[0];
      int[] Inst=new int[0];
      int[] VEP=new int[0];
      int[] Cmd=new int[0];
      int[] Val=new int[0];
      int Channel,ChannelVariable;
      int Length=switchEndian2(Inf.readUnsignedShort(),false);
      int Rows=switchEndian2(Inf.readUnsignedShort(),false);
      int bytesRead=0;
      row=new PatternRow[Rows];
      Inf.skipBytes(4);
      for(int rowPos=0;rowPos<Rows;rowPos++) {
        row[rowPos]=new PatternRow(Mask.length);
        ChannelVariable=Inf.read(); bytesRead++;
        while(ChannelVariable!=0) {
          Channel=(ChannelVariable-1) & 0x3F;
          if((Channel+1)>Mask.length) {
            row[rowPos].stretch(Channel+1);
            Mask=stretch(Mask,Channel+1);
            Note=stretch(Note,Channel+1);
            Inst=stretch(Inst,Channel+1);
            VEP= stretch(VEP, Channel+1);
            Cmd= stretch(Cmd, Channel+1);
            Val= stretch(Val, Channel+1);
          }
          boolean HasMask=(ChannelVariable & 0x80)!=0;
          if(HasMask) {
            Mask[Channel]=Inf.readByte();bytesRead++;
          }
          if((Mask[Channel] & (1 << 0))!=0) {
            Note[Channel]=Inf.read();bytesRead++;
            if(Note[Channel]<200) {
              MinNote=Math.min(MinNote,Note[Channel]);
              MaxNote=Math.max(MaxNote,Note[Channel]);
            }
          }
          if((Mask[Channel] & (1 << 1))!=0) {Inst[Channel]=Inf.read();bytesRead++;}
          if((Mask[Channel] & (1 << 2))!=0) {VEP[Channel] =Inf.read();bytesRead++;}
          if((Mask[Channel] & (1 << 3))!=0) {
            Cmd[Channel] =Inf.readByte();bytesRead++;
            Val[Channel] =Inf.readByte();bytesRead++;
          }
          row[rowPos].cell[Channel]=new PatternCell();
          if(((Mask[Channel] & (1 << 0)) | (Mask[Channel] & (1 << 4)))!=0) row[rowPos].cell[Channel].Note=Note[Channel];
          if(((Mask[Channel] & (1 << 1)) | (Mask[Channel] & (1 << 5)))!=0) row[rowPos].cell[Channel].Inst=Inst[Channel];
          if(((Mask[Channel] & (1 << 2)) | (Mask[Channel] & (1 << 6)))!=0) row[rowPos].cell[Channel].VEP =VEP[Channel];
          if(((Mask[Channel] & (1 << 3)) | (Mask[Channel] & (1 << 7)))!=0) {
            row[rowPos].cell[Channel].Cmd=Cmd[Channel];
            row[rowPos].cell[Channel].Val=Val[Channel];
          }
          ChannelVariable=Inf.read();bytesRead++;
          if(bytesRead>Length) throw new RuntimeException("Sorceror's Apprentice! Read "+bytesRead+" of "+Length+" bytes");
        }
      }
      System.out.println(noteName(MinNote)+" "+noteName(MaxNote));
    }
    public String toString() {
      String result="";
      for(int i=0;i<row.length;i++) {
        result+=row[i].toString();
        if(i<row.length-1) result+="\n";
      }
      return result;
    }
  }
  Header header;
  int[] order;
  Pattern[] pattern;
  public ParseImpulseTracker() {};
  int switchEndian2(int in, boolean isSigned) {
    int n1=in & 0x00FF;
    int n2=in & 0xFF00;
    int out=((in & 0x00FF) << 8) | ((in & 0xFF00) >>> 8);
    if(isSigned && out>0x7FFF) out=out-0x10000;
    return out;
  }
  int switchEndian4(int in) {
    int out=((in & 0x000000FF) << 24) | ((in & 0x0000FF00) << 8) | ((in & 0x00FF0000) >>> 8) | ((in & 0xFF000000) >>> 24) ;
    return out;
  }
  String readNullTerminatedString(RandomAccessFile Inf, int nSize) throws IOException {
    byte[] B=new byte[nSize];
    Inf.read(B);
    String result=new String(B);
    if(result.indexOf(0)>=0) result=result.substring(0,result.indexOf(0));
    return result;
  }
  public ParseImpulseTracker(RandomAccessFile Inf) throws IOException {
    header=new Header(Inf);
    Inf.seek(0x00C0);
    order=new int[header.OrdNum];
    for(int i=0;i<header.OrdNum;i++) {
      order[i]=Inf.read();
    }
    Inf.seek(0x00C0+header.OrdNum+header.InsNum*4+header.SmpNum*4);
    pattern=new Pattern[header.PatNum];
    int[] patternOfs=new int[header.PatNum];
    for(int i=0;i<header.PatNum;i++) {
      patternOfs[i]=switchEndian4(Inf.readInt());
    }
    for(int i=0;i<header.PatNum;i++) {
      Inf.seek(patternOfs[i]);
      pattern[i]=new Pattern(Inf);
    }
  }
  public static void main(String args[] ) throws IOException {
    RandomAccessFile Inf=new RandomAccessFile("Data/ImpulseTracker/Carol of the Bells.it","r");
    ParseImpulseTracker Imp=new ParseImpulseTracker(Inf);
    for(int i=0;i<Imp.order.length;i++) {
      if(Imp.order[i]<200) {
        System.out.println("Order "+i+" Pattern "+Imp.order[i]);
        System.out.println(Imp.pattern[Imp.order[i]].toString());
      } else {
        System.out.println("End of song");
      }
    }
  }
}
