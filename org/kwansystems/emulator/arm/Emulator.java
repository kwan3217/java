package org.kwansystems.emulator.arm;

import java.io.*;

public class Emulator {
  public static void main(String args[]) throws IOException {
    Thumb2Decode decode=new Thumb2Decode();
    Datapath datapath=new Datapath();
    datapath.loadBin("/mnt/big/home/chrisj/workspace/code/Loginator/SerialTest/bootstrap.ofs0.bin", 0x1FFF0000);
    System.out.println("Reset");
    datapath.r[13]=datapath.readMem4(0x1FFF0000);
    datapath.r[15]=datapath.readMem4(0x1FFF0004) & ~(0x01);
    int cycles=0;
    for(;;) {
      System.out.printf("=== Cycle %d ===\n", cycles);
      // execute
      if(datapath.ins!=null) {
        System.out.println("Execute "+datapath.ins.op.toString());
        datapath.ins.execute(datapath);
        if(datapath.ins!=null && datapath.ins.op!=Operation.IT) datapath.shiftIT();
      }
      if(datapath.flush) {
        decode.flushPipeline();
      }
      datapath.flush=false;
      // decode
      if(datapath.insDataValid) {
        System.out.println("Decode");
        datapath.ins=decode.decode(datapath.insData,datapath.r[15]-2);
      }
      // fetch
      System.out.println("Fetch");
      datapath.insData=datapath.readMem2(datapath.r[15]);
      datapath.insDataValid=true;
      datapath.r[15]+=2;
      cycles++;
    }
  }
}
