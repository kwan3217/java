package org.kwansystems.emulator.cips;

import java.io.*;


public class SUROM extends CIPSInstructionMemory {
  public SUROM(PrintStream Louf, long startAddr, long length, InputStream Inf) throws IOException {
    super(Louf);
    loadFile(startAddr,length,Inf);
  }
  public SUROM(PrintStream Louf, long startAddr, InputStream Inf) throws IOException {
    this(Louf, startAddr,-1,Inf);
  }
  public SUROM(PrintStream Louf, InputStream Inf) throws IOException {
    this(Louf,0,Inf);
  }
  public SUROM(PrintStream Louf, long startAddr, long length, String Infn) throws IOException {
    super(Louf);
    loadFile(startAddr,length,Infn);
  }
  public SUROM(PrintStream Louf, long startAddr, String Infn) throws IOException {
    this(Louf, startAddr,-1,Infn);
  }
  public SUROM(PrintStream Louf, String Infn) throws IOException {
    this(Louf,0,Infn);
  }
  public SUROM(PrintStream Louf, long startAddr, long[] data) {
    super(Louf,startAddr,data);
  }
  public static void main(String[] args) throws IOException {
    SUROM S=new SUROM(System.out,1,"jumptest.code.bin");
    System.out.println(Disassembler.disasm(S.page,0,0x13));
  }
}
