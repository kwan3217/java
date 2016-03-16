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
  public static void main(String args[]) throws IOException {
    final int baud=38400;
    final int fosc=12000000;
    final int bitcycles=fosc/baud;
    LPC4078 lpc4078=new LPC4078(new int[][] {
    //Cycle             0      1 2       3 4 5 
    {     0,            (1<<1),0,(0<<10),0,0,0}, //Start state, BSL asserted, UART0 RX idle
    {   410,            0     ,0,(0<<10),0,0,0}, //leading edge of start bit (down)
    {   410+bitcycles*1,(1<<1),0,(0<<10),0,0,0}, //leading edge LSB high bits (up)
    {   410+bitcycles*7,0     ,0,(0<<10),0,0,0}, //trailing edge data high bits (down)
    {   410+bitcycles*9,(1<<1),0,(0<<10),0,0,0}, //trailing edge data low bits (up)
    }
        );
    ((UART)(lpc4078.uart[0])).outData=new UARTOutData[] {
      new UARTOutData(3568,"Synchronized\r\n"),
      new UARTOutData(6753,"12000\r\n"),
      new UARTOutData(9579,"J\r\n"),
    };
    ((UART)(lpc4078.uart[0])).ouf=new PrintWriter(new FileWriter("/mnt/big/home/chrisj/workspace/code/Loginator/SerialTest/bootstrap.out"));
    lpc4078.MainFlash.loadBin("/mnt/big/home/chrisj/workspace/code/Loginator/SerialTest/FW.SFE");
    lpc4078.BootRom.loadBin("/mnt/big/home/chrisj/workspace/code/Loginator/SerialTest/bootstrap.ofs0.bin");
    lpc4078.loadDisasm("/mnt/big/home/chrisj/workspace/code/Loginator/SerialTest","bootstrap.disasm");
    lpc4078.loadDisasm("/mnt/big/home/chrisj/workspace/code/Loginator/SerialTest","SerialTest.lss");
    lpc4078.reset();
    lpc4078.addressBreakpoint=0x1fff0116;
    lpc4078.addressBreakpointEnabled=true;
    for(;;) {
      lpc4078.cycle();
    }
  }
}
