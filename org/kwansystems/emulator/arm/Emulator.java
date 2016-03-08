package org.kwansystems.emulator.arm;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kwansystems.emulator.arm.peripherals.*;
import org.kwansystems.emulator.arm.peripherals.Timer; //Since there is a java.util.Timer also

import java.nio.file.*;
import java.nio.charset.*;

public class Emulator {
  public static Thumb2Decode decode=new Thumb2Decode();
  public static Datapath datapath=new Datapath();
  public static ReadOnlyMemory MainFlash;
  public static ReadOnlyMemory BootRom;
  public static RandomAccessMemory MainSram=new RandomAccessMemory(0x10000000,0x10000);
  public static RandomAccessMemory PeripheralSram=new RandomAccessMemory(0x20000000,0x8000);
  public static BootBlock bootBlock=new BootBlock();
  public static LockBlock lockBlock=new LockBlock();
  public static SystemControlBlock systemControlBlock=new SystemControlBlock();
  public static Peripheral adc=new Peripheral("ADC",0x40034000);
  public static Watchdog watchdog=new Watchdog();
  public static final int baud=38400;
  public static final int fosc=12000000;
  public static final int bitcycles=fosc/baud;
  public static GPIO gpio=new GPIO(datapath,new int[][] {
    //Cycle             0      1 2       3 4 5 
    {     0,            (1<<1),0,(0<<10),0,0,0}, //Start state, BSL asserted, UART0 RX idle
    {   410,            0     ,0,(0<<10),0,0,0}, //leading edge of start bit (down)
    {   410+bitcycles*1,(1<<1),0,(0<<10),0,0,0}, //leading edge LSB high bits (up)
    {   410+bitcycles*7,0     ,0,(0<<10),0,0,0}, //trailing edge data high bits (down)
    {   410+bitcycles*9,(1<<1),0,(0<<10),0,0,0}, //trailing edge data low bits (up)
  });
  public static UART[] uart=new UART[] {
    new UART(0,0x4000C000,datapath),
    new UART(1,0x40010000,datapath),
    new UART(2,0x40098000,datapath),
    new UART(3,0x4009C000,datapath),
    new UART(4,0x400A4000,datapath)
  };
  public static Timer[] timer=new Timer[] {
    new Timer(0,0x40004000,datapath),
    new Timer(1,0x40008000,datapath),
    new Timer(2,0x40090000,datapath),
    new Timer(3,0x40094000,datapath)
  };
  public static Peripheral emc=new Peripheral("EMC",0x2009C000);
  public static PinConnect pinConnect=new PinConnect();
  public static SystemControlSpace systemControlSpace=new SystemControlSpace();
  public static Peripheral[] resetArray0=new Peripheral[] {
    null, //lcd
    timer[0],
    timer[1],
    uart[0],
    
    uart[1],
    null, //pwm0
    null, //pwm1
    null, //i2c0
    
    uart[4],
    null, //rtc
    null, //ssp1
    emc,
    
    adc,
    null, //can1
    null, //can2
    gpio,
    
    null, //spifi
    null, //mcpwm
    null, //qei
    null, //i2c1
    
    null, //ssp2
    null, //ssp0
    timer[2],
    timer[3],
    
    uart[2],
    uart[3],
    null, //i2c2
    null, //i2s
    
    null, //sdc
    null, //gpdma
    null, //enet
    null, //usb
  };
  static {
    try {
      MainFlash=new ReadOnlyMemory(0x00000000,0x80000,"/mnt/big/home/chrisj/workspace/code/Loginator/SerialTest/FW.SFE");
      BootRom=new ReadOnlyMemory(0x1fff0000,0x10000,"/mnt/big/home/chrisj/workspace/code/Loginator/SerialTest/bootstrap.ofs0.bin");
    } catch (IOException e) {
      throw new RuntimeException(e); // Rethrow it as a RuntimeException
    }
    datapath.addDevice(MainFlash);
    datapath.addDevice(BootRom); 
    datapath.addDevice(MainSram);
    datapath.addDevice(PeripheralSram);
    datapath.addDevice(bootBlock);
    datapath.addDevice(lockBlock);
    datapath.addDevice(systemControlBlock);
    datapath.addDevice(adc);
    datapath.addDevice(watchdog);
    datapath.addDevice(gpio);
    datapath.addDevice(uart[0]);
    datapath.addDevice(uart[1]);
    datapath.addDevice(uart[2]);
    datapath.addDevice(uart[3]);
    datapath.addDevice(uart[4]);
    datapath.addDevice(timer[0]);
    datapath.addDevice(timer[1]);
    datapath.addDevice(timer[2]);
    datapath.addDevice(timer[3]);
    datapath.addDevice(emc);
    datapath.addDevice(pinConnect);
    datapath.addDevice(systemControlSpace);
  }
  public static void main(String args[]) throws IOException {
//    MainFlash.poke(0x2FC,0x43218765); //Temporarily force CRP3
    List<String> disasmLines=Files.readAllLines(Paths.get("/mnt/big/home/chrisj/workspace/code/Loginator/SerialTest","bootstrap.disasm"),Charset.forName("UTF-8"));
    Map<Integer,String> disasmAddrLines=new HashMap<Integer,String>();
    Pattern P=Pattern.compile("^\\s*([0-9A-Fa-f]{1,8}):.*$");
    for(String line:disasmLines) {
      Matcher m=P.matcher(line);
      if(m.matches()) {
        disasmAddrLines.put(Integer.parseInt(m.group(1),16), line);
      }
    }
    disasmLines=Files.readAllLines(Paths.get("/mnt/big/home/chrisj/workspace/code/Loginator/SerialTest","SerialTest.lss"),Charset.forName("UTF-8"));
    for(String line:disasmLines) {
      Matcher m=P.matcher(line);
      if(m.matches()) {
        disasmAddrLines.put(Integer.parseInt(m.group(1),16), line);
      }
    }
    datapath.reset();
    for(;;) {
      System.out.printf("== Cycle %d ==\n", datapath.cycles);
      // execute
      if(datapath.ins!=null) {
        System.out.printf("=== Execute pc=%08x %s ===\n",datapath.ins.pc,datapath.ins.op.toString());
        if(disasmAddrLines.containsKey(datapath.ins.pc)) {
          System.out.println(" "+disasmAddrLines.get(datapath.ins.pc));
        }
        if(datapath.ins!=null && datapath.ins.pc==0x1fff01d6) {
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
