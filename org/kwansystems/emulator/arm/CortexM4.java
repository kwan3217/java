package org.kwansystems.emulator.arm;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CortexM4 extends Datapath {
  public Thumb2Decode decode = new Thumb2Decode();
  private Map<Integer,String> disasmAddrLines = new HashMap<Integer,String>();
  private Pattern P = Pattern.compile("^\\s*([0-9A-Fa-f]{1,8}):.*$");
  public boolean cycleBreakpointEnabled=true;
  public int cycleBreakpoint=3288;
  public boolean addressBreakpointEnabled=false;
  public int addressBreakpoint=0;
  public boolean singleStep=false;
  public void loadDisasm(String path, String filename) throws IOException {
    List<String> disasmLines=Files.readAllLines(Paths.get(path,filename),Charset.forName("UTF-8"));
    for(String line:disasmLines) {
      Matcher m=P.matcher(line);
      if(m.matches()) {
        disasmAddrLines.put(Integer.parseInt(m.group(1),16), line);
      }
    }
  }
  public void cycle() {
    System.out.printf("== Cycle %d ==\n", cycles);
    //Update peripherals
    for(MemoryMappedDevice i:devices) {
      i.tick(cycles);
    }
    // execute
    if(ins!=null) {
      System.out.printf("=== Execute pc=%08x %s ===\n",ins.pc,ins.op.toString());
      if(disasmAddrLines.containsKey(ins.pc)) {
        System.out.println(" "+disasmAddrLines.get(ins.pc));
      }
      if(cycleBreakpointEnabled && cycles>=cycleBreakpoint) {
        cycleBreakpointEnabled=false;
        singleStep=true;
        System.out.printf("Cycle breakpoint at cycle %d\n",cycles);
      } 
      if(addressBreakpointEnabled && ins!=null && ins.pc==addressBreakpoint) {
        addressBreakpointEnabled=false;
        singleStep=true;
        System.out.printf("Address breakpoint at %08x\n",addressBreakpoint);
      }
      ins.execute(this);
      if(singleStep) {
        System.out.println("Single step");
      }
      if(ins!=null && ins.op!=Operation.IT) shiftIT();
    }
    if(flush) {
      decode.flushPipeline();
    }
    flush=false;
    // decode
    if(insDataValid) {
      System.out.println("=== Decode ===");
      ins=decode.decode(insData,r[15]);
    }
    // fetch
    System.out.println("=== Fetch ===");
    insData=readMem2(r[15]);
    insDataValid=true;
    r[15]+=2;
    cycles++;
  }
  public void reset() {
    System.out.println("==Reset==");
    int VTOR=readMem4(0xE000ED08);
    r[13]=readMem4(VTOR);
    BranchWritePC(readMem4(VTOR+4));
  }
}