package org.kwansystems.tools.chart;

import java.io.*;

public abstract class FileChartPrinter implements ChartPrinter {
  PrintWriter out;
  boolean OpenedOut;
  public FileChartPrinter(PrintWriter Lout) {out=Lout;OpenedOut=false;}
  public FileChartPrinter(String fn) {
    try {
      out=new PrintWriter(new FileWriter(fn));
      OpenedOut=true;
    } catch(IOException E) {throw new IllegalArgumentException(E.toString());}
  }
  //Chart printer guts
  public abstract void StartOfChart(int TFirstIdx, int NRows, ChartRecorder CR,String[] ColumnNames); 
  public abstract void BetweenHead();
  public abstract void EndOfHead();
  public abstract void Head(String S);
  public abstract void StartOfRow();
  public abstract void BetweenData();
  public abstract void EndOfRow();
  public abstract void Data(Object S);
  public abstract void NoData();
  public void EndOfChart() {
    if(OpenedOut)out.close();
  }
}


