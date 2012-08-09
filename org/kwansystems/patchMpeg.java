package org.kwansystems;

import java.io.*;

public class patchMpeg {
  public static void main(String[] args) throws IOException {
    String fn="C:\\Documents and Settings\\chrisj\\Desktop\\PRG003\\MOV033.MPG";
    RandomAccessFile inf=new RandomAccessFile(fn,"rw");

    long ptr=0;
    int bufSize=32768;
    byte[] search=new byte[bufSize*2];
    byte[] in=new byte[bufSize];
    int bytesRead=read(in);
    System.arraycopy(in, 0, search, 0, bytesRead);
    while(bytesRead>=0) {

    }
  }
}
