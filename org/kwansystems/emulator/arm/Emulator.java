package org.kwansystems.emulator.arm;

import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.nio.charset.*;

public class Emulator {
  public static void main(String args[]) throws IOException {
    Thumb2Decode decode=new Thumb2Decode();
    Datapath datapath=new Datapath();
    datapath.addDevice(new ReadOnlyMemory(0x00000000,0x80000));  //Main Flash
    datapath.addDevice(new ReadOnlyMemory(0x1fff0000,0x10000,"/mnt/big/home/chrisj/workspace/code/Loginator/SerialTest/bootstrap.ofs0.bin")); //Boot ROM 
    datapath.addDevice(new RandomAccessMemory(0x10000000,0x10000)); //Main SRAM
    datapath.addDevice(new RandomAccessMemory(0x20000000, 0x8000)); //Peripheral SRAM
    datapath.addDevice(new BootBlock());
    datapath.addDevice(new LockBlock());
    datapath.addDevice(new SystemControlBlock());
    List<String> disasmLines=Files.readAllLines(Paths.get("/mnt/big/home/chrisj/workspace/code/Loginator/SerialTest","bootstrap.disasm"),Charset.forName("UTF-8"));
    Map<Integer,String> disasmAddrLines=new HashMap<Integer,String>();
    for(String line:disasmLines) {
      if(line.length()>=9 && line.substring(0,9).matches("[0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f]:")) {
        disasmAddrLines.put(Integer.parseInt(line.substring(0,8),16), line);
      }
    }
    System.out.println("Reset");
    datapath.r[13]=datapath.readMem4(0x1FFF0000);
    datapath.r[15]=datapath.readMem4(0x1FFF0004) & ~(0x01);
    int cycles=0;
    for(;;) {
      System.out.printf("=== Cycle %d ===\n", cycles);
      // execute
      if(datapath.ins!=null) {
        System.out.printf("Execute pc=%08x %s\n",datapath.ins.pc,datapath.ins.op.toString());
        if(disasmAddrLines.containsKey(datapath.ins.pc)) {
          System.out.println(disasmAddrLines.get(datapath.ins.pc));
        }
        datapath.ins.execute(datapath);
        if(datapath.ins!=null && datapath.ins.op!=Operation.IT) datapath.shiftIT();
      }
      if(datapath.flush) {
        decode.flushPipeline();
      }
      datapath.flush=false;
      // decode
      if(cycles==200) {
        System.out.println("stop");
      }
      if(datapath.insDataValid) {
        System.out.println("Decode");
        datapath.ins=decode.decode(datapath.insData,datapath.r[15]);
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
