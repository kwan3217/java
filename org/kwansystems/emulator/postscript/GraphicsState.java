package org.kwansystems.emulator.postscript;

import org.kwansystems.emulator.postscript.path.*;

public class GraphicsState {
  public PsMatrix CTM;
  public Path currentPath;
  public Path currentClipPath;
  public ColorSpace currentColorSpace;
  public double[] colorData;
  public String[] colorStringData;
  public double linewidth;
  public int linecap;
  public int linejoin;
  public double miterlimit;
  public double[] dasharray;
  public double dashoffset0;
  public double flat;
  public GraphicsState() {
    CTM=new PsMatrix();
    currentPath=new Path();
    currentClipPath=new Path();
    currentColorSpace=ColorSpace.DeviceGray;
    colorData=new double[] {0};
    colorStringData=new String[] {};
    linewidth=1;
    linecap=0;
    linejoin=0;
    miterlimit=10.0;
    dasharray=new double[0];
    dashoffset0=0;
    flat=1.0;
  }
  public GraphicsState(GraphicsState Lsource) {
    CTM=new PsMatrix(Lsource.CTM);
    currentPath=new Path(Lsource.currentPath);
    currentClipPath=new Path(Lsource.currentClipPath);
    currentColorSpace=Lsource.currentColorSpace;
    colorData=new double[Lsource.colorData.length];
    System.arraycopy(Lsource.colorData, 0, colorData, 0, Lsource.colorData.length);
    colorStringData=new String[Lsource.colorStringData.length];
    for(int i=0;i<colorStringData.length;i++) colorStringData[i]=new String(Lsource.colorStringData[i]);
    linewidth=Lsource.linewidth;
    linecap=Lsource.linecap;
    linejoin=Lsource.linejoin;
    miterlimit=Lsource.miterlimit;
    dasharray=new double[Lsource.dasharray.length];
    System.arraycopy(Lsource.dasharray, 0, dasharray, 0, Lsource.dasharray.length);
    dashoffset0=Lsource.dashoffset0;
    flat=Lsource.flat;
  }
  public void setlinecap(int cap) {
    linecap=cap;
  }

  public void setlinejoin(int join) {
    linejoin=join;
  }

  public void setdash(double[] LDA, double Loffset) {
    dasharray=LDA;
    dashoffset0=Loffset;
  }

  public void setflat(double Lflat) {
    flat=Lflat;
  }

  public void setmiterlimit(double limit) {
    miterlimit=limit;
  }

  void setlinewidth(double width) {
    linewidth=width;
  }
  public void moveto(double deviceX, double deviceY) {
    currentPath.add(new MoveTo(deviceX,deviceY));
  }
  public void lineto(double deviceX, double deviceY) {
    currentPath.add(new LineSegment(deviceX,deviceY));
  }
  public void curveto(double x1, double y1, double x2, double y2, double x3, double y3) {
    currentPath.add(new CurveSegment(x1,y1,x2,y2,x3,y3));
  }
  public void closepath() {
    double[] start=currentPath.getCurrentSubpathStart();
    if(start!=null) currentPath.add(new ClosePath(start[0],start[1]));
  }
  public void newpath() {
    currentPath=new Path();
  }
  public void setrgbcolor(double r, double g, double b) {
    currentColorSpace=ColorSpace.DeviceRGB;
    colorData=new double[] {r,g,b};
  }
  public void setgray(double w) {
    currentColorSpace=ColorSpace.DeviceGray;
    colorData=new double[] {w};
  }
}
