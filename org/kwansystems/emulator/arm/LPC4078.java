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

public class LPC4078 extends CortexM4 {
  public ReadOnlyMemory MainFlash=new ReadOnlyMemory(0x00000000,0x80000);
  public ReadOnlyMemory BootRom=new ReadOnlyMemory(0x1fff0000,0x10000);
  public RandomAccessMemory MainSram=new RandomAccessMemory(0x10000000,0x10000);
  public RandomAccessMemory PeripheralSram=new RandomAccessMemory(0x20000000,0x8000);
  public BootBlock bootBlock=new BootBlock();
  public LockBlock lockBlock=new LockBlock();
  public Peripheral adc=new Peripheral("ADC",0x40034000);
  public Watchdog watchdog=new Watchdog();
  public GPIO gpio;
  public UART[] uart=new UART[] {
    new UART(0,0x4000C000,"Synchronized\r\n12000\r\nJ\r\n"),
    new UART(1,0x40010000,""),
    new UART(2,0x40098000,""),
    new UART(3,0x4009C000,""),
    new UART(4,0x400A4000,"")
  };
  public Timer[] timer=new Timer[] {
    new Timer(0,0x40004000),
    new Timer(1,0x40008000),
    new Timer(2,0x40090000),
    new Timer(3,0x40094000)
  };
  public Peripheral emc=new Peripheral("EMC",0x2009C000);
  public PinConnect pinConnect=new PinConnect();
  public SystemControlSpace systemControlSpace=new SystemControlSpace();
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
  public SystemControlBlock systemControlBlock=new SystemControlBlock(resetArray0);
  public LPC4078(int[][] gpioData) throws IOException {
    gpio=new GPIO(gpioData);
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
