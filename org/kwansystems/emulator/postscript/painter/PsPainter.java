package org.kwansystems.emulator.postscript.painter;

import java.io.*;
import org.kwansystems.emulator.postscript.path.*;
import org.kwansystems.emulator.postscript.*;

public class PsPainter implements Painter {
  private PrintWriter ouf;
  private boolean selfOpen;
  public PsPainter(PrintWriter Louf) {
    ouf=Louf;
    selfOpen=false;
  }
  
  public PsPainter(String Oufn) throws IOException {
    ouf=new PrintWriter(new FileWriter(Oufn));
    selfOpen=true;
  }

  public void stroke(GraphicsState G) {
    outGraphicsState(G);
    outPath(G);
    ouf.println("stroke");
    ouf.flush();
  }
  
  public void fill(GraphicsState G) {
    outGraphicsState(G);
    outPath(G);
    ouf.println("fill");
    ouf.flush();
  }

  public void showpage(GraphicsState G) {
    ouf.println("showpage");
    ouf.flush();
  }

  public void done(GraphicsState G) {
    ouf.flush();
    if(selfOpen) ouf.close();
  }

  private void outGraphicsState(GraphicsState G) {
    switch(G.currentColorSpace) {
      case DeviceRGB:
        ouf.printf("%f %f %f setrgbcolor\n",G.colorData[0],G.colorData[1],G.colorData[2]);
        break;
      case DeviceGray:
        ouf.printf("%f setgray\n",G.colorData[0]);
        break;
    }
  }

  private void outPath(GraphicsState G) {
    for(PathElement pe:G.currentPath) {
      if(pe instanceof MoveTo) {
        double[] data=pe.getData();
        ouf.printf("%f %f moveto\n", data[0],data[1]);
      } else if(pe instanceof ClosePath) {
        ouf.printf("closepath\n");
      } else if(pe instanceof LineSegment) {
        double[] data=pe.getData();
        ouf.printf("%f %f lineto\n", data[0],data[1]);
      } else if(pe instanceof CurveSegment) {
        double[] data=pe.getData();
        ouf.printf("%f %f %f %f %f %f curveto\n", data[0],data[1], data[2], data[3], data[4], data[5]);
      }
    }
  }

  public void initclip(GraphicsState G) {
  //  throw new UnsupportedOperationException("Not supported yet.");
  }

  public void initctm(GraphicsState G) {
    G.CTM=new PsMatrix(new double[] {1,0,0,1,0,0});
  }
}

