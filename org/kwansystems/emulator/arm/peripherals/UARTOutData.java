package org.kwansystems.emulator.arm.peripherals;

public class UARTOutData {
  public final int cycles;
  private final String data;
  public int ptr;

  public UARTOutData(int Lcycles, String Ldata) {cycles=Lcycles;data=Ldata;ptr=0;}
  public char get() {char result=data.charAt(ptr);ptr++;return result;}
  public boolean done() {return ptr>=data.length();}

}