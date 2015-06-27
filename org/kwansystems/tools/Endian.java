package org.kwansystems.tools;

import java.io.*;

public class Endian {
  public static int swapEndian(int in) {
	  return (int)(((in >>> 24) & 0xFF) | ((in >>> 8) & 0xFF00) | ((in << 8) & 0xFF0000) | ((in << 24) & 0xFF000000));
  }
  public static long swapEndian(long in) {
//    System.out.printf("in %016X\n",in);
    int lo=(int)(in & 0xFFFFFFFF);
//    System.out.printf("lo %08X\n",lo);
    lo=swapEndian(lo);
//    System.out.printf("lo %08X\n",lo);
    int hi=(int)((in >>> 32) & 0xFFFFFFFF);
//    System.out.printf("hi %08X\n",hi);
    hi=swapEndian(hi);
//    System.out.printf("hi %08X\n",hi);
    long result_hi=((long)lo) << 32;
//    System.out.printf("result_hi %016X\n",result_hi);
    long result_lo=hi;
    result_lo=result_lo << 32 >>> 32;
//    System.out.printf("result_lo %016X\n",result_lo);
    long result=result_hi | result_lo;
//    System.out.printf("result    %016X\n",result);
    return result;
  }
  public static short swapEndian(short in) {
    return (short)(((in >>> 8) & 0xFF) | ((in << 8) & 0xFF00));
  }
  public static float swapEndian(float in) {
    return Float.intBitsToFloat(swapEndian(Float.floatToRawIntBits(in)));
  }
  public static double swapEndian(double in) {
    return Double.longBitsToDouble(swapEndian(Double.doubleToRawLongBits(in)));
  }
  public static long readLong(InputStream inf) throws IOException {
    byte[] b=new byte[8];
    inf.read(b);
    return readLong(b,0);
  }
  public static int readInt(InputStream inf) throws IOException {
    byte[] b=new byte[4];
    inf.read(b);
    return readInt(b,0);
  }
  public static short readShort(InputStream inf) throws IOException {
    byte[] b=new byte[2];
    inf.read(b);
    return readShort(b,0);
  }
  public static long readLong(byte[] payloadBytes, int pos) {
    return (long)(
      ((((long)payloadBytes[pos+0])&0xff)<<56) |
      ((((long)payloadBytes[pos+1])&0xff)<<48) |
      ((((long)payloadBytes[pos+2])&0xff)<<40) |
      ((((long)payloadBytes[pos+3])&0xff)<<32) |
      ((((long)payloadBytes[pos+4])&0xff)<<24) |
      ((((long)payloadBytes[pos+5])&0xff)<<16) |
      ((((long)payloadBytes[pos+6])&0xff)<< 8) |
      ((((long)payloadBytes[pos+7])&0xff)<< 0)
    );
  }
  public static int readInt(byte[] payloadBytes, int pos) {
    return (int)(
      ((((long)payloadBytes[pos+0])&0xff)<<24) |
      ((((long)payloadBytes[pos+1])&0xff)<<16) |
      ((((long)payloadBytes[pos+2])&0xff)<< 8) |
      ((((long)payloadBytes[pos+3])&0xff)<< 0)
    );
  }
  public static short readShort(byte[] payloadBytes, int pos) {
    return (short)(
      ((((long)payloadBytes[pos+0])&0xff)<< 8) |
      ((((long)payloadBytes[pos+1])&0xff)<< 0)
    );
  }
  public static short readUByte(byte[] payloadBytes, int pos) {
    short result=payloadBytes[pos];
    return ((short)(((int)result) & 0xFF));
  }
  public static int readLEUShort(byte[] payloadBytes, int pos) {
    short result=swapEndian(readShort(payloadBytes,pos));
    return ((int)result) & 0xFFFF;
  }
  public static long readLEUInt(byte[] payloadBytes, int pos) {
    int result=swapEndian(readInt(payloadBytes,pos));
    return ((long)result) & 0xFFFFFFFF;
  }
  public static byte[] readBytes(byte[] payloadBytes, int pos, int len) {
    byte[] result=new byte[len];
    System.arraycopy(payloadBytes,pos,result,0,len);
    return result;
  }
  public static double readDouble(byte[] payloadBytes, int pos) {
    return Double.longBitsToDouble(readLong(payloadBytes,pos));
  }
  public static float readFloat(byte[] payloadBytes, int pos) {
    return Float.intBitsToFloat(readInt(payloadBytes,pos));
  }
}
