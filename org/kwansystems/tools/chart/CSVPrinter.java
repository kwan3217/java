package org.kwansystems.tools.chart;

import java.io.*;

public class CSVPrinter extends FileChartPrinter {
  public CSVPrinter(PrintWriter Lout) {
    super(Lout);
  }

  public CSVPrinter(String fn) {
    super(fn);
  }

  public void StartOfChart(int TFirstIdx, int NRows, ChartRecorder CR, String[] ColumnNames) {}

  public void BetweenHead() {
    out.print(",");
  }

  public void EndOfHead() {
    out.println("");
  }

  public void Head(String S) {
    out.print("\""+S+"\"");
  }

  public void StartOfRow() {}

  public void BetweenData() {
    out.print(",");
  }

  public void EndOfRow() {
    out.println("");
  }

  public void Data(Object S) {
    // System.out.println(S.getClass().getName());
    if(S.getClass().getName().equals("org.kwansystems.vector.MathVector")) {
      out.print(S);
    } else if(S.getClass().getName().equals("java.lang.Double")) {
      out.print(S);
    } else {
      out.print("\""+S+"\"");
    }
  }

  public void NoData() {
    Data("-");
  }

  public void EndOfChart() {
    out.close();
  }
}
