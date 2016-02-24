package org.kwansystems.emulator.arm;

import java.io.*;

public class Emulator {
  public static void main(String args[]) throws IOException {
    Thumb2Decode decode=new Thumb2Decode();
    Datapath datapath=new Datapath();
    datapath.loadBin("/home/chrisj/workspace/code/Loginator/SerialTest/bootstrap.ofs0.bin", 0x1FFF0000);
    datapath.r[13]=datapath.readMem4(0x1FFF0000);
    datapath.r[15]=datapath.readMem4(0x1FFF0004) & ~(0x01);
    for(;;) {
      // execute
      if(datapath.ins!=null) datapath.ins.execute(datapath);
      if(datapath.flush) {
        decode.flushPipeline();
      }
      datapath.flush=false;
      // decode
      if(datapath.insDataValid) datapath.ins=decode.decode(datapath.insData,datapath.r[15]-2);
      // fetch
      datapath.insData=datapath.readMem2(datapath.r[15]);
      datapath.insDataValid=true;
      datapath.r[15]+=2;
    }
  }
}
