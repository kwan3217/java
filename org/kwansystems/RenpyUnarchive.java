/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 *
 * @author chrisj
 */
public class RenpyUnarchive {

  final static byte MARK            = '(';
  final static byte STOP            = '.';
  final static byte POP             = '0';
  final static byte POP_MARK        = '1';
  final static byte DUP             = '2';
  final static byte FLOAT           = 'F';
  final static byte INT             = 'I';
  final static byte BININT          = 'J';
  final static byte BININT1         = 'K';
  final static byte LONG            = 'L';
  final static byte BININT2         = 'M';
  final static byte NONE            = 'N';
  final static byte PERSID          = 'P';
  final static byte BINPERSID       = 'Q';
  final static byte REDUCE          = 'R';
  final static byte STRING          = 'S';
  final static byte BINSTRING       = 'T';
  final static byte SHORT_BINSTRING = 'U';
  final static byte UNICODE         = 'V';
  final static byte BINUNICODE      = 'X';
  final static byte APPEND          = 'a';
  final static byte BUILD           = 'b';
  final static byte GLOBAL          = 'c';
  final static byte DICT            = 'd';
  final static byte EMPTY_DICT      = '}';
  final static byte APPENDS         = 'e';
  final static byte GET             = 'g';
  final static byte BINGET          = 'h';
  final static byte INST            = 'i';
  final static byte LONG_BINGET     = 'j';
  final static byte LIST            = 'l';
  final static byte EMPTY_LIST      = ']';
  final static byte OBJ             = 'o';
  final static byte PUT             = 'p';
  final static byte BINPUT          = 'q';
  final static byte LONG_BINPUT     = 'r';
  final static byte SETITEM         = 's';
  final static byte TUPLE           = 't';
  final static byte EMPTY_TUPLE     = ')';
  final static byte SETITEMS        = 'u';
  final static byte BINFLOAT        = 'G';
  final static byte TUPLE2          = (byte)0x86;
  final static String mark="spam";
  public static void main(String[] args) throws IOException, DataFormatException {
    RandomAccessFile inf=new RandomAccessFile("data.rpa","r");
    String s=inf.readLine();
    System.out.println(s);
    String ofss=s.substring(8,24);
    System.out.println(ofss);
    long ofs=Long.parseLong(ofss,16);
    System.out.println(ofs);
    String keys=s.substring(25,33);
    System.out.println(keys);
    int key=Integer.parseInt(keys,16);
    System.out.println(key);
    inf.seek(ofs);
    byte[] index=new byte[((int)(inf.length()-ofs))];
    inf.read(index);
    Inflater i=new Inflater();
    i.setInput(index,0,index.length);
    byte[] indexunc=new byte[index.length*10];
    int indexLen=i.inflate(indexunc);
    System.out.println(indexLen);
    String hexdigit="0123456789ABCDEF";
    for(int j=0;j<=indexLen/16;j++) {
      for(int k=0;k<4;k++) {
        for(int m=0;m<4;m++) {
          if(j*16+k*4+m<indexLen) {
            System.out.print(hexdigit.charAt((indexunc[j*16+k*4+m] & 0xFF)/16));
            System.out.print(hexdigit.charAt((indexunc[j*16+k*4+m] & 0xFF)%16));
          } else {
            System.out.print("  ");
          }
        }
        System.out.print(" ");
      }
      for(int k=0;k<4;k++) {
        for(int m=0;m<4;m++) {
          if(j*16+k*4+m<indexLen) {
            if(indexunc[j*16+k*4+m]>=32 & indexunc[j*16+k*4+m]<127) {
              System.out.print((char)indexunc[j*16+k*4+m]);
            } else {
              System.out.print(".");
            }
          } else {
            System.out.print(" ");
          }
        }
        System.out.print("|");
      }
      System.out.println();
    }
    //Skip the first two bytes
    int j=2;
    boolean done=false;
    Stack st=new Stack();
    Map memo=new TreeMap();
    while(!done) {
      byte tag=indexunc[j];
      String tags;
      if(tag>32 & tag<=127) {
        tags=new String("Tag '"+(char)tag+"'");
      } else {
        tags=String.format("Tag 0x%02X",tag);
      }
//      System.out.println("---");
//      System.out.println(memo);
//      System.out.println(st);
//      System.out.println(tags);
      j++;
      switch(tag) {
        case EMPTY_DICT:
//          System.out.println("Push empty dictionary");
          st.push(new HashMap());
          break;
        case BINPUT:
          int bin=indexunc[j]&0xff;
          Object top=st.peek();
//          System.out.println("Binput "+top+" at "+bin);
          memo.put(bin,top);
          j++;
          break;
        case MARK:
//          System.out.println("Push mark");
          st.push(mark);
          break;
        case SHORT_BINSTRING:
          int len=indexunc[j]&0xff;
          j++;
          byte[] stuff=new byte[len];
          System.arraycopy(indexunc,j,stuff,0,len);
          j+=len;
          String bins=new String(stuff);
          st.push(bins);
//          System.out.println("Push string '"+bins+"', length " +len);
          break;
        case EMPTY_LIST:
//          System.out.println("Push empty list");
          st.push(new ArrayList());
          break;
        case BININT:
          int bi=((indexunc[j+0] & 0xFF)<< 0) |
                 ((indexunc[j+1] & 0xFF)<< 8) |
                 ((indexunc[j+2] & 0xFF)<<16) |
                 ((indexunc[j+3] & 0xFF)<<24);
          j+=4;
          st.push(bi);
//          System.out.printf("Push binint %d (0x%08X)\n",bi,bi);
//          System.out.printf("Decoded %d (0x%08X)\n",bi ^ key,bi ^ key);
          break;

        case APPEND:
          Object a=st.pop();
//          System.out.print("Append "+a);
          List L=(List)(st.peek());
//          System.out.println(" to list "+L);
          L.add(a);
          break;
        case TUPLE2:
//          System.out.print("Make tuple2 ");
          Object[] oa=new Object[2];
          oa[1]=st.pop();
          oa[0]=st.pop();
//          System.out.println(oa);
          st.push(oa);
          break;
        case LONG_BINPUT:
          bin=((indexunc[j+0] & 0xFF)<< 0) |
              ((indexunc[j+1] & 0xFF)<< 8) |
              ((indexunc[j+2] & 0xFF)<<16) |
              ((indexunc[j+3] & 0xFF)<<24);
          top=st.peek();
//          System.out.println("Long Binput "+top+" at "+bin);
          memo.put(bin,top);
          j+=4;
          break;
        case SETITEMS:
//          System.out.println("Set Items ");
          //Find marker
          int markerPos=-1;
          for(int sp=st.size()-1;sp>0;sp--) {
            if(mark==st.get(sp)) markerPos=sp;
          }
          if(markerPos<0) throw new RuntimeException("Waah!");
          Map dict=(Map)st.get(markerPos-1);
          for(int sp=markerPos+1;sp<st.size();sp+=2) {
            dict.put(st.get(sp), st.get(sp+1));
          }
          while(st.size()>markerPos) st.pop();
          break;
        case STOP:
//          System.out.println("Stop unpickling");
          done=true;
          break;
        default:
          System.out.println(tags+" not understood");
      }
    }
    Map Index=(Map)st.pop();
    for(Object K:Index.keySet()) {
      List L=(List)Index.get(K);
      Object[] V=(Object[])L.get(0);
      ofs=((Integer)(V[0])).intValue()^key;
      int len=((Integer)(V[1])).intValue()^key;
      System.out.printf("File: %s\n  Ofs: %d\n  Len: %d\n",K,ofs,len);
      inf.seek(ofs);
      byte[] file=new byte[len];
      inf.read(file);
      FileOutputStream ouf=new FileOutputStream("Renai/"+K);
      ouf.write(file);
      ouf.close();
    }
  }
}
