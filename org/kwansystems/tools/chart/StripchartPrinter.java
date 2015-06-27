package org.kwansystems.tools.chart;

import java.io.*;

public class StripchartPrinter extends FileChartPrinter {
  boolean FirstData,FirstRow;
  double Xmin,Xmax,Ymin,Ymax;
  double Xtick,Ytick;
  double XWidth,YHeight;
  double TimeData;
  int LegendCount;
  String Column;
  ChartRecorder CR;
  public StripchartPrinter(PrintWriter Lout) {
    super(Lout);
  }
  public StripchartPrinter(String fn) {
    super(fn);
  }
  private void Prolog() {
    out.println("/rshow {dup stringwidth pop neg 0 rmoveto show} bind def");
    out.println("/cshow {dup stringwidth pop 2 div neg 0 rmoveto show} bind def");
    out.println("/axis {dup stringwidth pop 2 div neg 0 rmoveto show} bind def");
   /* 
    /yaxis {
      /ymax exch def
      /ymin exch def
      /x exch def
      /xticksize exch def
      /ysize 7.5 inch def
      
    }
    */
    out.println("/NaN 0 def");
    NewPage();
  }
  public void setSize(double LXmin, double LXmax, double LYmin, double LYmax, int LXWidth, int LYHeight) {
    Xmin=LXmin;
    Xmax=LXmax;
    Ymin=LYmin;
    Ymax=LYmax;
    Xtick=0;
    Ytick=0;
    XWidth=LXWidth;
    YHeight=LYHeight;
  }
  public void setAutoSize() {
    Xmin=0;
    Xmax=CR.getTMax();
    double[] Auto=AutoRange.calcAutoRange(Xmin,Xmax,6);
    Xmin=Auto[0];
    Xmax=Auto[1];
    Xtick=Auto[2];
    Ymin=CR.columnMin(Column);
    Ymax=CR.columnMax(Column);
    Auto=AutoRange.calcAutoRange(Ymin,Ymax,6);
    Ymin=Auto[0];
    Ymax=Auto[1];
    Ytick=Auto[2];
    XWidth=700;
    YHeight=500;
  }
  public void StartOfChart(int TFirstIdx, int NRows, ChartRecorder LCR, String[] ColumnNames) {
    if(ColumnNames.length>1) throw new IllegalArgumentException("Only one column allowed");
    CR=LCR;
    FirstRow=true;
    Column=ColumnNames[0];
    setAutoSize();
    Prolog();
  }
  public void BetweenHead() {}
  public void EndOfHead() {}
  public void Head(String S) {}
  public void StartOfRow() {FirstData=true;}
  public void BetweenData() {}
  public void EndOfRow() {
  }
  public void Data(Object S) {
    double ScaleData=((Double)S).doubleValue();
    if(FirstData) {
      ScaleData-=Xmin;
      ScaleData*=(XWidth/(Xmax-Xmin));
      TimeData=ScaleData;
      FirstData=false;
    } else {
      ScaleData-=Ymin;
      ScaleData*=(YHeight/(Ymax-Ymin));
      out.print(TimeData+" "+ScaleData+" ");
      if(FirstRow) {
        out.println("moveto");
        FirstRow=false;
      } else {
        out.println("lineto");
      }
    }
  }
  public void NoData() {
    //Data(new Double(0));
  }
  public void EndOfChart() {
    out.println("stroke");
    if(OpenedOut)EndPage();
    super.EndOfChart();
  }
  public void EndPage() {
    XAxis("Time");
    YAxis("Data");
    PrintTitles("Stuff");
    out.println("showpage");
  }
  public void NewPage() {
    LegendCount=0;
    out.println("8 0.75 translate 90 rotate 72 scale");
    out.println("0 0 moveto 10 0 lineto 10 7.5 lineto 0 7.5 lineto closepath stroke");
  }
  public void PrintTitles(String Title) {
    out.println("/Helvetica findfont 16 72 div scalefont setfont");
    out.println("5 7.5 mul 0.05 add moveto ("+Title+") cshow");
    out.println("/Helvetica findfont 8 72 div scalefont setfont");
    out.println("0 7.5 0.10 add moveto (ASEN5050) show");
    out.println("0 7.5 0.05 add moveto (Homework 8) show");
    out.println("10 7.5 72 mul 0.10 add moveto (Chris Jeppesen) rshow");
    out.println("10 7.5 72 mul 0.05 add moveto (7 November 2002) rshow");
  }
  public void Legend(double LineWidth, double[] Dash, String ColumnName) {
    out.println(LineWidth+" setlinewidth");
    out.print("[");
    for(int i=0;i<Dash.length;i++) {
      out.print(" "+Dash[i]);
    }
    out.println(" ] 0 setdash");
    out.println("9 72 mul 7 72 mul "+LegendCount+" 12 mul sub moveto 36 0 rlineto gsave stroke grestore ("+ColumnName+") show");
    LegendCount++;
  }

  public void XAxis(String AxisLabel) {
    int ticks=(int)((Xmax-Xmin)/Xtick);
    out.println("0 1 "+ticks+"{dup "+ticks+" div 10 mul dup 0 moveto 0 -0.125 rlineto stroke 0.01 add -0.125 moveto ");
    out.println((Xmax-Xmin)+" mul "+ticks+" div "+Xmin+" add 10 string cvs show} for");
    out.println("5 -0.25 moveto ("+AxisLabel+") cshow");
  }
  public void YAxis(String AxisLabel) {
    int ticks=(int)((Ymax-Ymin)/Ytick);
    out.println("0 1 "+ticks+"{dup "+ticks+" div 7.5 mul 72 mul dup 0 exch moveto -8 0 rlineto stroke 3 sub -9 exch moveto ");
    out.println((Ymax-Ymin)+" mul "+ticks+" div "+Ymin+" add 10 string cvs rshow} for");
    out.println("gsave 90 rotate 7.5 72 mul 2 div 30 moveto ("+AxisLabel+") cshow grestore");
  }
  public void PrintColumns(String[] ColumnNames, ChartRecorder C) {
    double[] Widths={1,1,0.5,0.5};
    double[][] Dashes={{},{1},{},{2,2}};
    for(int i=0;i<ColumnNames.length;i++) {
      Legend(Widths[i],Dashes[i],ColumnNames[i]);
      C.PrintSubTable(new String[] {ColumnNames[i]},this);
    }
  }
}
