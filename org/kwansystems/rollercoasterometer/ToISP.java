package org.kwansystems.rollercoasterometer;

import java.io.*;

public class ToISP {
  public static String uuencodeLine(byte[] data, int idx, int len) {
    StringBuffer result=new StringBuffer();
    result.append((char)(len+32));
    if(len<45) {
//      System.out.println(len);
    }
    for(int i=0;i<((len+2)/3);i++) {
      long acc=            ((long)((data[idx+i*3+0])) & 0xFF)<<16;
      if(i*3+1<len) acc |= ((long)((data[idx+i*3+1])) & 0xFF)<< 8;
      if(i*3+2<len) acc |= ((long)((data[idx+i*3+2])) & 0xFF)<< 0;
      char b1=(char)(((acc >>> 18) & 0x3F)+32);if(b1==' ')b1='`';result.append(b1);
      char b2=(char)(((acc >>> 12) & 0x3F)+32);if(b2==' ')b2='`';result.append(b2);
      char b3=(char)(((acc >>>  6) & 0x3F)+32);if(b3==' ')b3='`';result.append(b3);
      char b4=(char)(((acc >>>  0) & 0x3F)+32);if(b4==' ')b4='`';result.append(b4);
 //     System.out.printf("%2d %2d %s\n",i,result.toString().length(),result.toString());
    }
    return result.toString();
  }
  public static int[] uudecodeLine(String data) {
    int len=data.charAt(0)-32;
    int[] result=new int[len];
    for(int i=0;i<(data.length()-1)/4;i++) {
      int b1=(int)((data.charAt(i*5+1)) & 0xFF);if(b1==96)b1=32;b1-=32;
      int b2=(int)((data.charAt(i*5+2)) & 0xFF);if(b2==96)b2=32;b2-=32;
      int b3=(int)((data.charAt(i*5+3)) & 0xFF);if(b3==96)b3=32;b3-=32;
      int b4=(int)((data.charAt(i*5+4)) & 0xFF);if(b4==96)b4=32;b4-=32;
      long acc=(b1 << 18) | (b2 << 12) | (b3 << 6) | (b4 << 0);
//      if(i*3+1<len) acc |= ((long)((data[idx+i*3+1])) & 0xFF)<< 8;
//      if(i*3+2<len) acc |= ((long)((data[idx+i*3+2])) & 0xFF)<< 0;
//      char b1=(char)(((acc >>> 18) & 0x3F)+32);if(b1==' ')b1='`';result.append(b1);
//      char b2=(char)(((acc >>> 12) & 0x3F)+32);if(b2==' ')b2='`';result.append(b2);
//      char b3=(char)(((acc >>>  6) & 0x3F)+32);if(b3==' ')b3='`';result.append(b3);
//      char b4=(char)(((acc >>>  0) & 0x3F)+32);if(b4==' ')b4='`';result.append(b4);
    }
    return result;
  }
  public static String uuencode(byte[] data, int len) {
    int idx=0;
    StringBuffer result=new StringBuffer();
    int lines=0;
    while(len-idx>45) {
      result.append("\n"+uuencodeLine(data,idx,45));
      idx+=45;
      lines++;
    }
    if(len-idx>0) {
      result.append("\n"+uuencodeLine(data,idx,len-idx));
      lines++;
    }
//    while(lines<20) {
//      result.append("\n"+"`");
//      lines++;
//    }
    result.delete(0,1);
    return result.toString();
  }
  public static int flash_sector(int x) {
    if(x < 0x8000) {
      return (x) >> 12;
    } else if (x < 0x78000) {
      return ((x-0x8000) >> 15) + 8;
    } else if(x < 0x7D000) {
      return ((x-0x78000) >> 12) + 22;
    } else {
      //In the boot block 0x7D000-0x7FFFF or not in flash at all
      return -1;
    }
  }

  public static void main(String[] args) throws IOException {
//    String infn="C:\\Documents and Settings\\chrisj\\Desktop\\Rollercoasterometer\\Main\\fw.sfe";
    String path="C:\\Users\\chrisj\\Documents\\NetBeansProjects\\JavaProject1\\Logomatic\\Bootloader";
    String infn=path+"\\main.bin";
    String oufn=path+"\\main.isp";
    FileInputStream inf=new FileInputStream(infn);
    PrintWriter ouf=new PrintWriter(new FileWriter(oufn));
    byte[] buffer = new byte[512];
    int bytesRead;
    long cksum=0;
    int pos=0;
    ouf.println("U 23130");
    ouf.println("P 0 8");
    ouf.println("E 0 8");
    while ((bytesRead = inf.read(buffer)) > 0) {
      cksum=0;
      ouf.printf("W %d %d\n",0x40002000,buffer.length);
      for (int i=0;i<buffer.length;i++) {
        long thisbyte=((long)(buffer[i]) & 0xFF);
        cksum+=thisbyte;
      }
      ouf.println(uuencode(buffer,buffer.length));
      for(int i=0;i<buffer.length;i++) {
        buffer[i]=0;
      }
      ouf.println(cksum);
      ouf.printf("P %d %d\n",flash_sector(pos),flash_sector(pos));
      ouf.printf("C %d %d %d\n",pos,0x40002000,buffer.length);
      pos+=buffer.length;
    }
    ouf.close();
  }
}
