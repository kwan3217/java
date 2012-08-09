package org.kwansystems.rocketometer;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ReadGPS {
  private GPSListener listener;
  SerialPort serialPort;
  public ReadGPS() {
    super();
  }

  void connect(String portName, int baud) throws Exception {
    CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
    if (portIdentifier.isCurrentlyOwned()) {
      System.out.println("Error: Port is currently in use");
    } else {
      CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

      if (commPort instanceof SerialPort) {
        serialPort = (SerialPort) commPort;
        serialPort.setSerialPortParams(baud, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

        InputStream in = serialPort.getInputStream();
        OutputStream out = serialPort.getOutputStream();

        (new Thread(new SerialReader(in,listener))).start();
        (new Thread(new SerialWriter(out))).start();

      } else {
        System.out.println("Error: Only serial ports are handled by this example.");
      }
    }
  }

  void setListener(GPSListener Llistener) {
    listener =Llistener;
  }

  void disconnect() {
    serialPort.close();
  }

  /** */
  public static class SerialReader implements Runnable {
    InputStream in;
    GPSListener listener;

    public SerialReader(InputStream in, GPSListener Llistener) {
      this.in = in;
      listener=Llistener;
    }

    public void run() {
      byte[] buffer = new byte[1024];
      int len = -1;
      try {
        while ((len = this.in.read(buffer)) > -1) {
          String S=new String(buffer, 0, len);
          if(listener!=null) listener.listen(S);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /** */
  public static class SerialWriter implements Runnable {
    OutputStream out;

    public SerialWriter(OutputStream out) {
      this.out = out;
    }

    public void run() {
      try {
        int c = 0;
        while ((c = System.in.read()) > -1) {
          this.out.write(c);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    try {
      new ReadGPS().connect("COM8",115200);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
