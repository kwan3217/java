package org.kwansystems.emulator.arm;

import java.io.*;
import java.util.*;

import org.kwansystems.emulator.arm.peripherals.*;
import org.kwansystems.emulator.arm.peripherals.Timer; //Since there is a java.util.Timer also

import java.nio.file.*;
import java.nio.charset.*;

public class Emulator {
  public static void main(String args[]) throws IOException {
    Thumb2Decode decode=new Thumb2Decode();
    Datapath datapath=new Datapath();
    int[][] GPIOinputData=new int[][] {
      //Cycle 0 1 2 3 4 5 
      {     0,0,0,(1<<10),0,0,0}
    };
    ReadOnlyMemory MainFlash=new ReadOnlyMemory(0x00000000,0x80000);
    MainFlash.poke(0x2FC,0x43218765); //Temporarily force CRP3
    datapath.addDevice(MainFlash);  //Main Flash
    datapath.addDevice(new ReadOnlyMemory(0x1fff0000,0x10000,"/mnt/big/home/chrisj/workspace/code/Loginator/SerialTest/bootstrap.ofs0.bin")); //Boot ROM 
    datapath.addDevice(new RandomAccessMemory(0x10000000,0x10000)); //Main SRAM
    datapath.addDevice(new RandomAccessMemory(0x20000000, 0x8000)); //Peripheral SRAM
    datapath.addDevice(new BootBlock());
    datapath.addDevice(new LockBlock());
    datapath.addDevice(new SystemControlBlock());
    datapath.addDevice(new Peripheral("ADC",0x40034000));
    datapath.addDevice(new Watchdog());
    datapath.addDevice(new GPIO(datapath,GPIOinputData));
    datapath.addDevice(new UART(0,0x4000C000,datapath));
    datapath.addDevice(new UART(1,0x40010000,datapath));
    datapath.addDevice(new UART(2,0x40098000,datapath));
    datapath.addDevice(new UART(3,0x4009C000,datapath));
    datapath.addDevice(new UART(4,0x400A4000,datapath));
    datapath.addDevice(new Timer(0,0x40004000,datapath));
    datapath.addDevice(new Timer(1,0x40008000,datapath));
    datapath.addDevice(new Timer(2,0x40090000,datapath));
    datapath.addDevice(new Timer(3,0x40094000,datapath));
    datapath.addDevice(new Peripheral("EMC",0x2009C000));
    datapath.addDevice(new PinConnect());
    List<String> disasmLines=Files.readAllLines(Paths.get("/mnt/big/home/chrisj/workspace/code/Loginator/SerialTest","bootstrap.disasm"),Charset.forName("UTF-8"));
    Map<Integer,String> disasmAddrLines=new HashMap<Integer,String>();
    for(String line:disasmLines) {
      if(line.length()>=9 && line.substring(0,9).matches("[0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f]:")) {
        disasmAddrLines.put(Integer.parseInt(line.substring(0,8),16), line);
      }
    }
    System.out.println("==Reset==");
    datapath.r[13]=datapath.readMem4(0x1FFF0000);
    datapath.r[15]=datapath.readMem4(0x1FFF0004) & ~(0x01);
    for(;;) {
      System.out.printf("== Cycle %d ==\n", datapath.cycles);
      // execute
      if(datapath.ins!=null) {
        System.out.printf("=== Execute pc=%08x %s ===\n",datapath.ins.pc,datapath.ins.op.toString());
        if(disasmAddrLines.containsKey(datapath.ins.pc)) {
          System.out.println(" "+disasmAddrLines.get(datapath.ins.pc));
        }
        if(datapath.ins!=null && datapath.ins.pc==0x1fff003c) {
          System.out.println("Breakpoint");
        }
        datapath.ins.execute(datapath);
        if(datapath.ins!=null && datapath.ins.op!=Operation.IT) datapath.shiftIT();
      }
      if(datapath.flush) {
        decode.flushPipeline();
      }
      datapath.flush=false;
      // decode
      if(datapath.insDataValid) {
        System.out.println("=== Decode ===");
        datapath.ins=decode.decode(datapath.insData,datapath.r[15]);
      }
      // fetch
      System.out.println("=== Fetch ===");
      datapath.insData=datapath.readMem2(datapath.r[15]);
      datapath.insDataValid=true;
      datapath.r[15]+=2;
      datapath.cycles++;
    }
  }
}
