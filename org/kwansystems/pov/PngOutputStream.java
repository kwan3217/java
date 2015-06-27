package org.kwansystems.pov;

import java.util.zip.*;
import java.io.*;

/**
 * PngEncoder takes a Java Image object and creates a byte string which can be
 * saved as a PNG file. The Image is presumed to use the DirectColorModel.
 * <p>
 * Thanks to Jay Denny at KeyPoint Software http://www.keypoint.com/ who let me
 * develop this code on company time.
 * <p>
 * You may contact me with (probably very-much-needed) improvements, comments,
 * and bug fixes at:
 * <p>
 * david@catcode.com
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA A copy of the GNU LGPL
 * may be found at http://www.gnu.org/copyleft/lesser.html,
 * <p>
 * Slightly modified by Chris Jeppesen (Put in my tree, reformatted, progress
 * stuff removed
 *
 * @author J. David Eisenberg
 * @version 1.4, 31 March 2000
 * @version 1.5, 1 November 2005
 * 
 */
public class PngOutputStream extends FilterOutputStream {
  /** Constants for filters */
  public static final int FILTER_NONE=0;
  public static final int FILTER_SUB=1;
  public static final int FILTER_UP=2;
  public static final int FILTER_LAST=2;

  protected byte[] scanLine;

  protected int width, height;
  protected int scanLinePos, row;
  protected boolean Alpha;
  protected int bytesPerPixel;
  protected ChunkOutputStream compressedBytes;
  protected DeflaterOutputStream defout;

  public PngOutputStream(String Loutn, int Lwidth, int Lheight, boolean LAlpha) throws IOException {
	this(new FileOutputStream(Loutn),Lwidth,Lheight,LAlpha);
  }
  public PngOutputStream(String Loutn, int Lwidth, int Lheight) throws IOException {
    this(new FileOutputStream(Loutn),Lwidth,Lheight,false);
  }
  public PngOutputStream(OutputStream Lout, int Lwidth, int Lheight) throws IOException {
    this(Lout,Lwidth,Lheight,false);
  }
  public PngOutputStream(OutputStream Lout, int Lwidth, int Lheight, boolean LAlpha) throws IOException {
    super(Lout);
    width=Lwidth;
    height=Lheight;
    Alpha=LAlpha;
    bytesPerPixel=Alpha?4:3;
    out.write(new byte[] {-119,80,78,71,13,10,26,10});
    writeHeader();
    init();
  }

  protected static byte[] resizeByteArray(byte[] array, int newLength) {
    byte[] newArray=new byte[newLength];
    int oldLength=array.length;

    System.arraycopy(array, 0, newArray, 0, Math.min(oldLength, newLength));
    return newArray;
  }

  public static byte[] writeBytes(byte[] data, byte[] target, int offset) {
    if (data.length+offset>target.length) {
      target=resizeByteArray(target, data.length+offset);
    }
    System.arraycopy(data, 0, target, offset, data.length);
    return target;
  }

  public static byte[] writeBytes(byte[] data, byte[] target, int start, int nBytes, int offset) {
    if (nBytes+offset>target.length) {
      target=resizeByteArray(target, offset+nBytes);
    }
    System.arraycopy(data, start, target, offset, nBytes);
    return target;
  }

  public static byte[] writeInt2(int n, byte[] target, int offset) {
    byte[] temp= {(byte)((n>>8)&0xff), (byte)(n&0xff)};
    return writeBytes(temp, target, offset);
  }

  public static byte[] writeInt4(int n, byte[] target, int offset) {
    byte[] temp= {(byte)((n>>24)&0xff), (byte)((n>>16)&0xff), (byte)((n>>8)&0xff), (byte)(n&0xff)};
    return writeBytes(temp, target, offset);
  }

  public static byte[] writeByte(int b, byte[] target, int offset) {
    byte[] temp= {(byte)b};
    return writeBytes(temp, target, offset);
  }

  public static byte[] writeString(String s, byte[] target, int offset) {
    return writeBytes(s.getBytes(), target, offset);
  }
  
  protected static byte[] makeChunk(String ChunkName, byte[] data, int len) {
    return makeChunk(ChunkName,data,0,len);
  }

  protected static byte[] makeChunk(String ChunkName, byte[] data, int start, int len) {
    CRC32 crc=new CRC32();
    byte[] chunk=new byte[len+12];
    chunk=writeInt4(len,chunk,0);
    chunk=writeString(ChunkName,chunk,4);
    chunk=writeBytes(data,chunk,start,len,8);
    crc.reset();
    crc.update(chunk, 4, len+4);
    chunk=writeInt4((int)crc.getValue(), chunk, len+8);
    return chunk;
  }
  
  protected static byte[] makeChunk(String ChunkName, byte[] data) {
    return makeChunk(ChunkName,data,data.length);
  }
  
  public void writeChunk(String ChunkName, byte[] data) throws IOException{
    byte[] chunk=makeChunk(ChunkName,data);
    out.write(chunk);
  }

  private class ChunkOutputStream extends FilterOutputStream {
    ByteArrayOutputStream buffer;
    int len;
    public ChunkOutputStream(OutputStream arg0) {
      super(arg0);
      buffer=new ByteArrayOutputStream();
    }
    public void write(int arg0) throws IOException {
      buffer.write(arg0);
    }
    public void write(byte[] data) throws IOException {
      buffer.write(data);
    }
    public void write(byte[] data,int start, int datalen) throws IOException {
      buffer.write(data,start,datalen);
    }
    public void close() throws IOException {
      writeChunk("IDAT",buffer.toByteArray());
    }
  }
  private void init() {
    Deflater def=new Deflater(9);
    compressedBytes=new ChunkOutputStream(this.out);
    defout=new DeflaterOutputStream(compressedBytes,def);
    scanLine=new byte[bytesPerPixel*width+1];
    row=0;
    scanLinePos=0;
  }
  protected void writeHeader() throws IOException {
    byte[] chunk=new byte[] {};
    chunk=writeInt4(width, chunk, 0);
    chunk=writeInt4(height, chunk, 4);
    chunk=writeByte(8, chunk, 8); // bit depth per channel
    chunk=writeByte(Alpha?6:2, chunk, 9); // Color type (6=RGBA quad, 2=RGB triple)
    chunk=writeByte(0, chunk, 10); // compression method (deflate)
    chunk=writeByte(0, chunk, 11); // filter method 0 (PNG adaptive)
    chunk=writeByte(0, chunk, 12); // no interlace
    writeChunk("IHDR",chunk);
  }
  public void close() throws IOException {
    defout.close();
    writeChunk("IEND",new byte[] {});
    super.close();
  }
  public void write(int arg0) throws IOException {
    defout.write(new byte[]{(byte)arg0});
  }
  public void write(byte[] data) throws IOException {
    write(data,0,data.length);
  }
  public void write(byte[] data, int start, int len) throws IOException {
    int bytestoend=(width*bytesPerPixel)-(scanLinePos);
    int pos=start;
    while(len>0) {
      int bytestocopy=Math.min(bytestoend,len);
      System.arraycopy(data,pos,scanLine,1+scanLinePos,bytestocopy);
      scanLinePos+=bytestocopy;
      pos+=bytestocopy;
      len-=bytestocopy;
      if(scanLinePos>width*bytesPerPixel) throw new RuntimeException("This can't happen!");
      if(scanLinePos==width*bytesPerPixel) {
        scanLinePos=0;
        row++;
        writeScanLine();
      }
    }
  }
  
  //This is where we would apply a filter and such
  private void writeScanLine() throws IOException {
    defout.write(scanLine);
  }
}
