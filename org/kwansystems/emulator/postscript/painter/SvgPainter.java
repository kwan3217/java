package org.kwansystems.emulator.postscript.painter;

import java.io.*;
import org.kwansystems.emulator.postscript.path.*;
import org.kwansystems.emulator.postscript.*;

public class SvgPainter implements Painter {
  private PrintWriter ouf;
  private boolean selfOpen;
  private double xsize,ysize;
  int clipId=-1;
  public SvgPainter(PrintWriter Louf, double Lxsize, double Lysize) {
    ouf=Louf;
    selfOpen=false;
    xsize=Lxsize;
    ysize=Lysize;
    WriteHeader();
  }
  
  public SvgPainter(String Oufn, double Lxsize, double Lysize) throws IOException {
    this(new PrintWriter(new FileWriter(Oufn)),Lxsize,Lysize);
    selfOpen=true;
  }
  private void WriteHeader() {
    ouf.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
    ouf.println("<svg");
    ouf.println("   xmlns:svg=\"http://www.w3.org/2000/svg\"");
    ouf.println("   xmlns=\"http://www.w3.org/2000/svg\"");
    ouf.println("   width=\""+xsize+"\"");
    ouf.println("   height=\""+ysize+"\">");

  }
  private void WriteFooter() {
    ouf.println("</svg>");
  }
  public void stroke(GraphicsState G) {
//    outGraphicsState(G);
//    outPath(G);
//    ouf.println("stroke");
//    ouf.flush();
  }
  private String getColor(GraphicsState G) {
    switch(G.currentColorSpace) {
      case DeviceRGB:
        return String.format("#%02x%02x%02x",
          ((int)(G.colorData[0]*255)),
          ((int)(G.colorData[1]*255)),
          ((int)(G.colorData[2]*255))
        );
      case DeviceGray:
        return String.format("#%02x%02x%02x",
          ((int)(G.colorData[0]*255)),
          ((int)(G.colorData[0]*255)),
          ((int)(G.colorData[0]*255))
        );
      default:
        throw new RuntimeException("This color mode is not yet implemented");
    }
  }
  private String outPath(Path P) {
    StringBuffer result=new StringBuffer("");
    for(PathElement pe:P) {
      if(pe instanceof MoveTo) {
        double[] data=pe.getData();
        result.append(String.format("M%.2f,%.2f\n", data[0],data[1]));
      } else if(pe instanceof ClosePath) {
        result.append(String.format("z\n"));
      } else if(pe instanceof LineSegment) {
        double[] data=pe.getData();
        result.append(String.format("L%.2f,%.2f\n", data[0],data[1]));
      } else if(pe instanceof CurveSegment) {
        double[] data=pe.getData();
        result.append(String.format("C%.2f,%.2f %.2f,%.2f %.2f,%.2f\n", data[0],data[1], data[2], data[3], data[4], data[5]));
      }
    }
    return result.toString();
  }
  public void fill(GraphicsState G) {
    ouf.printf("    <path clip-path=\"url(#clip%04d)\"\n",clipId);
    ouf.println("       style=\"opacity:1;fill:"+getColor(G)+";fill-opacity:1;stroke:none;\"");
    ouf.println("       d=\""+outPath(G.currentPath)+"\"");
    ouf.println("    />");
    ouf.flush();
  }
  public void WriteClip(GraphicsState G) {
    clipId++;
    ouf.printf("    <clipPath id=\"clip%04d\">\n",clipId);
    ouf.println("    <path");
    ouf.println("       d=\""+outPath(G.currentClipPath)+"\"");
    ouf.println("    />");
    ouf.printf("    </clipPath>");
    ouf.flush();
  }

  public void showpage(GraphicsState G) {
    ouf.flush();
  }

  public void done(GraphicsState G) {
    WriteFooter();
    ouf.flush();
    if(selfOpen) ouf.close();
  }

  public void initclip(GraphicsState G) {
    G.currentClipPath.add(new MoveTo(0,0));
    G.currentClipPath.add(new LineSegment(xsize,0));
    G.currentClipPath.add(new LineSegment(xsize,ysize));
    G.currentClipPath.add(new LineSegment(0,ysize));
    G.currentClipPath.add(new ClosePath(0,0));
    WriteClip(G);
  }

  public void initctm(GraphicsState G) {
    G.CTM=new PsMatrix(new double[] {1,0,0,-1,0,ysize});
  }
}

