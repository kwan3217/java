package org.kwansystems.tools.chart;

import org.kwansystems.tools.vector.*;
import org.kwansystems.tools.rotation.*;

import java.io.*;

public class POVColumnPrinter extends FileChartPrinter {
  String ColumnName;
  String VarName;
  Double Time;
  Object Data;
  boolean FirstData;
  public POVColumnPrinter(PrintWriter Lout) {
    this(Lout,null);
  }
  public POVColumnPrinter(PrintWriter Lout,String LVarName) {
    super(Lout);
    VarName=LVarName;
  }
  public POVColumnPrinter(String fn) {
    super(fn);
  }
  public POVColumnPrinter(String fn,String LVarName) {
    this(fn);
    VarName=LVarName;
  }
  public void StartOfChart(int TFirstIdx, int NRows, ChartRecorder CR,String[] ColumnNames) {
    if(TFirstIdx+NRows>CR.NumRows()) NRows=CR.NumRows()-TFirstIdx;
    int POVCols=1;
    for(String ColumnName:ColumnNames) {
      Data=CR.Playback(0, ColumnName);
      if(Data!=null) {
        if(Data.getClass().getName().contains(".MathVector")) {
          POVCols+=((MathVector)Data).dimension();
        } else if(Data.getClass().getName().contains(".SixDOFState")) {
          POVCols+=((MathVector)Data).dimension();
        } else if(Data.getClass().getName().contains(".Quaternion")) {
          POVCols+=4;
        } else {
          POVCols++;
        }
      } else {
        POVCols++;
      }
    }
    out.print("#declare "+VarName+"=array["+NRows+"]["+POVCols+"] {");
  }
  public void BetweenHead() {}
  public void EndOfHead() {}
  public void Head(String S) {}
  public void StartOfRow() {
    FirstData=true;
    out.print("{");
  }
  public void BetweenData() {}
  public void EndOfRow() {
    out.println("},");
  }
  public void Data(Object S) {
    if(FirstData) {
      Time=(Double)S;
      FirstData=false;
      out.print(Time+",");
    } else {
      Data=S;
      if(Data!=null) {
        if(Data.getClass().getName().contains(".MathVector")) {
          MathVector V=(MathVector)Data;
          out.print(V.get(0));
          for(int i=1;i<V.dimension();i++) {
            out.print(","+V.get(i));
          }
        } else if(Data.getClass().getName().contains(".SixDOFState")) {
          SixDOFState SDS=(SixDOFState)Data;
          out.print(SDS.get(0));
          for(int i=1;i<SDS.dimension();i++) {
            out.print(","+SDS.get(i));
          }
        } else if(Data.getClass().getName().contains(".Quaternion")) {
          Quaternion Q=(Quaternion)Data;
          out.print(Q.X()+","+Q.Y()+","+Q.Z()+","+Q.W());
        } else if(Data.getClass().getName().equals("java.lang.Double")) {
          out.print(Data);
        } else {
          out.print(Data);
        }
      }
      out.print(",");
    }
  }
  public void NoData() {Data("0");}
  public void EndOfChart() {
    out.println("}");
    super.EndOfChart();
  }
}
