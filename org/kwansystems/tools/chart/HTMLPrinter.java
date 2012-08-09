package org.kwansystems.tools.chart;

import org.kwansystems.tools.vector.*;

import java.io.*;

public class HTMLPrinter extends FileChartPrinter {
  public HTMLPrinter(PrintWriter Lout) {super(Lout);}
  public HTMLPrinter(String fn) {super(fn);}
  public void StartOfChart(int TFirstIdx, int NRows, ChartRecorder CR, String[] ColumnNames) {
    out.println("<html><body>");
    out.println("<table border=1>");
    out.println("<tr><th>");
  }
  public void BetweenHead() {out.print("</th><th>");}
  public void EndOfHead() {out.println("</th></tr>");}
  public void Head(String S) {out.print(S);}  
  public void StartOfRow() {out.print("<tr><td>");}
  public void BetweenData() {out.print("</td><td>");}
  public void EndOfRow() {out.println("</td></tr>");}
  public void Data(Object S) {
    if(S.getClass().getName().equals("org.kwansystems.vector.MathVector")) {
      MathVector V=(MathVector)S;
      out.print("&lt;"+V.get(0));
      for(int i=1;i<V.dimension();i++) {
        out.print(","+V.get(i));
      }
      out.print("&gt;");
    } else if(S.getClass().getName().equals("java.lang.Double")) {
      out.print(S);
    } else {
      String SS=S.toString();
      SS=SS.replaceAll("\n","<br>");
      out.print(SS);
    }
  }
  public void NoData() {Data("-");}
  public void EndOfChart() {
    out.println("</table></body></html>");
    super.EndOfChart();
  }
}
