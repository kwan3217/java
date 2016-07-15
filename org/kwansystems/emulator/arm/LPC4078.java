package org.kwansystems.emulator.arm;

import java.io.IOException;

import org.kwansystems.emulator.arm.peripherals.BootBlock;
import org.kwansystems.emulator.arm.peripherals.GPIO;
import org.kwansystems.emulator.arm.peripherals.LockBlock;
import org.kwansystems.emulator.arm.peripherals.PinConnect;
import org.kwansystems.emulator.arm.peripherals.SystemControlBlock;
import org.kwansystems.emulator.arm.peripherals.SystemControlSpace;
import org.kwansystems.emulator.arm.peripherals.Timer;
import org.kwansystems.emulator.arm.peripherals.UART;
import org.kwansystems.emulator.arm.peripherals.Watchdog;

public class LPC4078 extends CortexM {
  public ReadOnlyMemory MainFlash=new ReadOnlyMemory("Main Flash",0x00000000,0x80000);
  public ReadOnlyMemory BootRom=new ReadOnlyMemory("Boot ROM",0x1fff0000,0x10000);
  public RandomAccessMemory MainSram=new RandomAccessMemory("Main SRAM",0x10000000,0x10000);
  public RandomAccessMemory PeripheralSram=new RandomAccessMemory("Peripheral SRAM",0x20000000,0x8000);
  public BootBlock bootBlock=new BootBlock(this);
  public LockBlock lockBlock=new LockBlock(this);
  public Peripheral adc=new Peripheral(this,"ADC",0x40034000,0x100);
  public Watchdog watchdog=new Watchdog(this);
  public GPIO gpio;
  public UART[] uart=new UART[] {
    new UART(this,0,0x4000C000),
    new UART(this,1,0x40010000),
    new UART(this,2,0x40098000),
    new UART(this,3,0x4009C000),
    new UART(this,4,0x400A4000)
  };
  public Timer[] timer=new Timer[] {
    new Timer(this,0,0x40004000),
    new Timer(this,1,0x40008000),
    new Timer(this,2,0x40090000),
    new Timer(this,3,0x40094000)
  };
  public Peripheral emc=new Peripheral(this,"EMC",0x2009C000,0x100);
  public PinConnect pinConnect=new PinConnect(this);
  public SystemControlSpace systemControlSpace=new SystemControlSpace(this);
  public Peripheral[] resetArray0=new Peripheral[] {
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
    null, //placeholder for gpio
    
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
  public SystemControlBlock systemControlBlock=new SystemControlBlock(this,resetArray0);
  public LPC4078(int[][] gpioData) throws IOException {
    gpio=new GPIO(this,gpioData);
    resetArray0[15]=gpio;
    addDevice(MainFlash);
    addDevice(BootRom); 
    addDevice(MainSram);
    addDevice(PeripheralSram);
    addDevice(bootBlock);
    addDevice(lockBlock);
    addDevice(systemControlBlock);
    addDevice(adc);
    addDevice(watchdog);
    addDevice(gpio);
    addDevice(uart[0]);
    addDevice(uart[1]);
    addDevice(uart[2]);
    addDevice(uart[3]);
    addDevice(uart[4]);
    addDevice(timer[0]);
    addDevice(timer[1]);
    addDevice(timer[2]);
    addDevice(timer[3]);
    addDevice(emc);
    addDevice(pinConnect);
    addDevice(systemControlSpace);
  }
}
